/*
 * Knowage, Open Source Business Intelligence suite
 * Copyright (C) 2016 Engineering Ingegneria Informatica S.p.A.
 *
 * Knowage is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Knowage is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package it.eng.spagobi.engines.commonj.services;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import commonj.work.Work;
import commonj.work.WorkEvent;
import commonj.work.WorkItem;
import de.myfoo.commonj.work.FooRemoteWorkItem;
import it.eng.spago.base.SourceBean;
import it.eng.spago.base.SourceBeanAttribute;
import it.eng.spago.base.SourceBeanException;
import it.eng.spagobi.commons.bo.UserProfile;
import it.eng.spagobi.engines.commonj.CommonjEngine;
import it.eng.spagobi.engines.commonj.runtime.CommonjWork;
import it.eng.spagobi.engines.commonj.runtime.CommonjWorkContainer;
import it.eng.spagobi.engines.commonj.runtime.CommonjWorkListener;
import it.eng.spagobi.engines.commonj.runtime.WorkConfiguration;
import it.eng.spagobi.engines.commonj.runtime.WorksRepository;
import it.eng.spagobi.engines.commonj.utils.GeneralUtils;
import it.eng.spagobi.engines.commonj.utils.ProcessesStatusContainer;
import it.eng.spagobi.services.content.bo.Content;
import it.eng.spagobi.services.proxy.ContentServiceProxy;
import it.eng.spagobi.tenant.Tenant;
import it.eng.spagobi.tenant.TenantManager;
import it.eng.spagobi.utilities.ParametersDecoder;
import it.eng.spagobi.utilities.engines.AbstractEngineAction;
import it.eng.spagobi.utilities.engines.SpagoBIEngineException;
import it.eng.spagobi.utilities.engines.SpagoBIEngineRuntimeException;
import it.eng.spagobi.utilities.engines.SpagoBIEngineServiceException;
import it.eng.spagobi.utilities.engines.SpagoBIEngineStartupException;
import it.eng.spagobi.utilities.service.JSONFailure;
import it.eng.spagobi.utilities.service.JSONSuccess;
import it.eng.spagobi.utilities.threadmanager.WorkManager;

public class StartWorkAction extends AbstractEngineAction {

	private static final Logger LOGGER = Logger.getLogger(StartWorkAction.class);

	private static final Base64.Decoder DECODER = Base64.getDecoder();

	private Content template;
	private String documentId;
	private String documentLabel;
	private String userId = null;
	private transient ContentServiceProxy contentProxy;
	private transient HttpSession session = null;
	private transient HttpServletRequest httpRequest = null;

	/**
	 * Reads document Id and user Id, get the template, configure the work, create process Id, start work
	 */

	@Override
	public void service(SourceBean request, SourceBean response) {
		LOGGER.debug("IN");
		super.service(request, response);
		HttpSession currSession = getHttpSession();
		HttpServletRequest currHttpRequest = getHttpRequest();

		UserProfile profile = (UserProfile) currSession.getAttribute("ENG_USER_PROFILE");
		if (profile != null) {
			String tenantId = profile.getOrganization();
			LOGGER.debug("Retrieved tenantId from user profile object : [" + tenantId + "]");
			// putting tenant id on thread local
			if (tenantId != null) {
				Tenant tenant = new Tenant(tenantId);
				TenantManager.setTenant(tenant);
			}
		}

		// USER_ID
		Object userIdO = request.getAttribute("USER_ID");
		if (userIdO != null)
			userId = userIdO.toString();
		else {
			// userId
			userIdO = request.getAttribute("userId");
			if (userIdO != null) {
				userId = userIdO.toString();
			} else {
				// userId from session
				userIdO = currSession.getAttribute("userId");

				if (userIdO != null) {
					userId = userIdO.toString();
				} else {

					LOGGER.error("could not retrieve user id");
					return;
				}
			}
		}

		// get DOcument ID
		Object documentIdO = null;
		documentIdO = request.getAttribute("DOCUMENT_ID");
		documentId = null;
		documentLabel = null;
		if (documentIdO != null) {
			documentId = documentIdO.toString();
		} else {
			LOGGER.warn("could not retrieve document id, check for label");

			Object documentLabelO = request.getAttribute("DOCUMENT_LABEL");
			if (documentLabelO != null) {
				documentLabel = documentLabelO.toString();
			} else {
				LOGGER.error("could not retrieve neither document id nor document label, exception!");
				return;
			}

		}

		// get Parameters
		Map parameters = new HashMap();

		List attributes = request.getContainedAttributes();
		for (Iterator iterator = attributes.iterator(); iterator.hasNext();) {
			SourceBeanAttribute object = (SourceBeanAttribute) iterator.next();
			String key = object.getKey();
			Object value = object.getValue();
			parameters.put(key, value);
		}

		serviceStart(userId, documentId, parameters, currSession, currHttpRequest, true);

		LOGGER.debug("OUT");

	}

	public void serviceStart(String userId, String documentId, Map parameters, HttpSession inSession, HttpServletRequest inHttpRequest, boolean actionMode) {
		LOGGER.debug("IN");

		this.session = inSession;
		this.httpRequest = inHttpRequest;
		this.documentId = documentId;
		this.userId = userId;

		try {
			JSONObject info = null;
			// work to build
			WorksRepository worksRepository = null;
			CommonjWork work = null;

			// can take the document id or the document label

			boolean isLabel = false;
			String documentUnique = null;
			if (documentLabel != null) {
				isLabel = true;
				documentUnique = documentLabel;
			} else if (documentId != null) {
				documentUnique = documentId;
			}

			// Build work from template
			try {
				work = new CommonjWork(getTemplateAsSourceBean());
			} catch (SpagoBIEngineException e) {
				LOGGER.error("Error in reading work template", e);
				return;
			}

			// calculate process Id
			String pId = work.calculatePId();
			LOGGER.debug("process Id is " + pId);
			work.setSbiParametersMap(parameters);

			try {
				worksRepository = CommonjEngine.getWorksRepository();
			} catch (SpagoBIEngineException e) {
				LOGGER.error("Error in reatriving works repository", e);
				return;

			}

			// call Work configurqations's configure method
			try {
				WorkConfiguration workConfiguration = new WorkConfiguration(worksRepository);
				workConfiguration.configure(session, work, parameters, documentUnique, isLabel);
			} catch (Exception e) {
				LOGGER.error("Error in configuring work", e);
				return;
			}

			// Get the container object from session: it MUST be present if start button is enabled
			ProcessesStatusContainer processesStatusContainer = ProcessesStatusContainer.getInstance();
			Object o = processesStatusContainer.getPidContainerMap().get(pId);
			CommonjWorkContainer container = (CommonjWorkContainer) o;

			WorkManager wm = container.getWm();
			Work workToDo = container.getWork();
			CommonjWorkListener listener = container.getListener();
			FooRemoteWorkItem fooRemoteWorkItem = wm.buildFooRemoteWorkItem(workToDo, listener);

			int statusWI;

			// Check if work was accepted
			if (fooRemoteWorkItem.getStatus() == WorkEvent.WORK_ACCEPTED) {
				container.setFooRemoteWorkItem(fooRemoteWorkItem);
				// run work!
				WorkItem workItem = wm.runWithReturnWI(workToDo, listener);
				container.setWorkItem(workItem);
				statusWI = workItem.getStatus();
				// put new Object in singleton!!!

				processesStatusContainer.getPidContainerMap().put(pId, container);

				// if not in action mode don't send the response
				if (actionMode) {
					try {
						info = GeneralUtils.buildJSONObject(pId, statusWI);
						writeBackToClient(new JSONSuccess(info));

					} catch (IOException e) {
						String message = "Impossible to write back the responce to the client";
						throw new SpagoBIEngineServiceException(getActionName(), message, e);
					}
				}
			} else { // WORK is rejected
				if (actionMode) {
					try {
						statusWI = fooRemoteWorkItem.getStatus();
						info = GeneralUtils.buildJSONObject(pId, statusWI);
						writeBackToClient(new JSONSuccess(info));
					} catch (IOException e) {
						String message = "Impossible to write back the responce to the client";
						throw new SpagoBIEngineServiceException(getActionName(), message, e);
					}
				}
			}
		} catch (Exception e) {

			LOGGER.error("Error in starting the work", e);
			if (actionMode) {
				try {
					writeBackToClient(new JSONFailure(e));
				} catch (IOException e1) {
					LOGGER.error("Error in starting the work and in writing back to client", e);
					throw new SpagoBIEngineServiceException(getActionName(), "Error in starting the work and in writing back to client", e1);
				} catch (JSONException e1) {
					LOGGER.error("Error in starting the work and in writing back to client", e);
					throw new SpagoBIEngineServiceException(getActionName(), "Error in starting the work and in writing back to client", e1);
				}
			}
		}

		LOGGER.debug("OUT");
	}

	/*
	 * FUNCTIONS FROM ACTION ENGINE
	 */

	public SourceBean getTemplateAsSourceBean() {
		SourceBean templateSB = null;
		try {
			templateSB = SourceBean.fromXMLString(getTemplateAsString());
		} catch (SourceBeanException e) {
			SpagoBIEngineStartupException engineException = new SpagoBIEngineStartupException("CommonJ", "Impossible to parse template's content", e);
			engineException.setDescription("Impossible to parse template's content:  " + e.getMessage());
			engineException.addHint("Check if the document's template is a well formed xml file");
			throw engineException;
		}

		return templateSB;
	}

	public String getTemplateAsString() {
		return new String(getTemplate());
	}

	private byte[] getTemplate() {
		byte[] templateContent = null;
		HashMap requestParameters;

		if (template == null) {
			contentProxy = getContentServiceProxy();
			if (contentProxy == null) {
				throw new SpagoBIEngineStartupException("SpagoBIQbeEngine", "Impossible to instatiate proxy class [" + ContentServiceProxy.class.getName()
						+ "] " + "in order to retrive the template of document [" + documentId + "]");
			}

			requestParameters = ParametersDecoder.getDecodedRequestParameters(httpRequest);
			if (documentId != null) {
				template = contentProxy.readTemplate(documentId, requestParameters);
			} else if (documentLabel != null) {
				template = contentProxy.readTemplateByLabel(documentLabel, requestParameters);
			}
			try {
				if (template == null)
					throw new SpagoBIEngineRuntimeException("There are no template associated to document [" + documentId + "]");
				templateContent = DECODER.decode(template.getContent());
			} catch (Throwable e) {
				SpagoBIEngineStartupException engineException = new SpagoBIEngineStartupException("COmmonj", "Impossible to get template's content", e);
				engineException.setDescription("Impossible to get template's content:  " + e.getMessage());
				engineException.addHint("Check the document's template");
				throw engineException;
			}
		}
		return templateContent;
	}

	private ContentServiceProxy getContentServiceProxy() {
		if (contentProxy == null) {
			contentProxy = new ContentServiceProxy(userId, session);
		}

		return contentProxy;
	}

	public ContentServiceProxy getContentProxy() {
		return contentProxy;
	}

	public void setContentProxy(ContentServiceProxy contentProxy) {
		this.contentProxy = contentProxy;
	}

	public String getDocumentId() {
		return documentId;
	}

	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}

}
