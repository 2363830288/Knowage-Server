/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package it.eng.knowage.api.dossier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import commonj.work.Work;
import it.eng.knowage.engines.dossier.template.placeholder.PlaceHolder;
import it.eng.spago.error.EMFUserError;
import it.eng.spago.security.IEngUserProfile;
import it.eng.spagobi.analiticalmodel.document.bo.BIObject;
import it.eng.spagobi.analiticalmodel.document.bo.DocumentMetadataProperty;
import it.eng.spagobi.commons.bo.UserProfile;
import it.eng.spagobi.commons.constants.SpagoBIConstants;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.commons.exceptions.SpagoBIEmptyFileExeception;
import it.eng.spagobi.commons.exceptions.SpagoBIResponseHasErrorsExeception;
import it.eng.spagobi.tenant.TenantManager;
import it.eng.spagobi.tools.massiveExport.dao.IProgressThreadDAO;
import it.eng.spagobi.tools.objmetadata.bo.ObjMetacontent;
import it.eng.spagobi.tools.objmetadata.bo.ObjMetadata;
import it.eng.spagobi.tools.objmetadata.dao.IObjMetacontentDAO;
import it.eng.spagobi.tools.objmetadata.dao.IObjMetadataDAO;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;
import it.eng.spagobi.utilities.exceptions.SpagoBIServiceException;

public class DocumentExecutionWork extends AbstractDocumentExecutionWork implements Work {

	private static final Logger LOGGER = Logger.getLogger(DocumentExecutionWork.class);

	public static final String OUTPUT_PDF = "application/pdf";

	static byte[] buf = new byte[1024];

	private boolean completeWithoutError = false;

	public DocumentExecutionWork(List<BIObjectPlaceholdersPair> documents, IEngUserProfile userProfile, Integer progressThreadId, String randomKey) {
		this.documents = documents;
		this.userProfile = userProfile;
		this.progressThreadId = progressThreadId;
		this.randomKey = randomKey;
	}

	@Override
	public void run() {
		try {
			this.setTenant();
			this.runInternal();
		} finally {
			TenantManager.unset();
		}
	}

	private void runInternal() {
		LOGGER.debug("IN");

		ProgressThreadManager progressThreadManager = null;
		IObjMetadataDAO metaDAO = null;
		IObjMetacontentDAO contentDAO = null;

		Thread thread = Thread.currentThread();
		Long threadId = thread.getId();

		LOGGER.debug("Started thread Id " + threadId + " from user id: " + ((UserProfile) userProfile).getUserId());

		Integer totalDocs = documents.size();
		LOGGER.debug("# of documents: " + totalDocs);

		progressThreadManager = new ProgressThreadManager();
		progressThreadManager.setStatusStarted(progressThreadId);
		try {

			metaDAO = DAOFactory.getObjMetadataDAO();
			contentDAO = DAOFactory.getObjMetacontentDAO();

		} catch (Exception e) {
			LOGGER.error("Error setting DAO");
			progressThreadManager.deleteThread(progressThreadId);
			throw new SpagoBIServiceException("Error setting DAO", e);
		}

		String fileExtension = ".pdf";

		LOGGER.debug("Export File extension: " + fileExtension);

		for (BIObjectPlaceholdersPair document : documents) {

			BIObject biObject = document.getBiObject();
			List<PlaceHolder> placeHolders = document.getPlaceholders();
			byte[] returnByteArray = null;
			ExecutionProxyWrapper executionProxyWrapper = new ExecutionProxyWrapper(biObject, OUTPUT_PDF);

			// get Obj Metadata
			List<DocumentMetadataProperty> listObjMetaContent = null;
			try {
				listObjMetaContent = getMetaDataAndContent(metaDAO, contentDAO, biObject);
			} catch (Exception e1) {
				String message = "Error while retrieving metadata and content of the biObject with id " + biObject.getId();
				LOGGER.error(message);
				throw new SpagoBIRuntimeException(message, e1);
			}
			biObject.setObjMetaDataAndContents(listObjMetaContent);

			try {
				returnByteArray = executionProxyWrapper.exec(userProfile, SpagoBIConstants.MASSIVE_EXPORT_MODALITY, "pdf");
				executionProxyWrapper.handleByteResponse(returnByteArray, randomKey, placeHolders);

				// update progress table
				progressThreadManager.incrementPartial(progressThreadId);
				LOGGER.debug("progress Id incremented");
			} catch (SpagoBIEmptyFileExeception e) {
				LOGGER.error("Response byte array is empty", e);
				progressThreadManager.setStatusError(progressThreadId);
				throw new SpagoBIServiceException("Exception in  writeing export file for BiObject with label " + biObject.getLabel() + " delete DB row", e);
			} catch (SpagoBIResponseHasErrorsExeception e) {
				LOGGER.error("Response has errors", e);
				progressThreadManager.setStatusError(progressThreadId);
				throw new SpagoBIServiceException("Response has errors ", e);
			}

		} // close For

		progressThreadManager.setStatusDownload(progressThreadId);
		LOGGER.debug("Thread row in database set as download state");

		LOGGER.debug("OUT");
	}

	private List<DocumentMetadataProperty> getMetaDataAndContent(IObjMetadataDAO metaDao, IObjMetacontentDAO metaContentDAO, BIObject obj) throws Exception {
		LOGGER.debug("IN");
		List<DocumentMetadataProperty> toReturn = null;

		try {
			DocumentMetadataProperty objMetaDataAndContent = null;
			List<ObjMetadata> allMetas = metaDao.loadAllObjMetadata();
			Map<Integer, ObjMetacontent> values = new HashMap<>();

			List<ObjMetacontent> list = metaContentDAO.loadObjOrSubObjMetacontents(obj.getId(), null);
			for (Iterator<ObjMetacontent> iterator = list.iterator(); iterator.hasNext();) {
				ObjMetacontent content = iterator.next();
				Integer metaid = content.getObjmetaId();
				values.put(metaid, content);
			}

			for (Iterator<ObjMetadata> iterator = allMetas.iterator(); iterator.hasNext();) {
				ObjMetadata meta = iterator.next();
				objMetaDataAndContent = new DocumentMetadataProperty();
				objMetaDataAndContent.setMetadataPropertyDefinition(meta);
				objMetaDataAndContent.setMetadataPropertyValue(values.get(meta.getObjMetaId()));
				if (toReturn == null)
					toReturn = new ArrayList<>();
				toReturn.add(objMetaDataAndContent);
			}

		} catch (Exception e) {
			LOGGER.error("error in retrieving metadata and metacontent for biobj id " + obj.getId(), e);
			throw e;
		}
		LOGGER.debug("OUT");
		return toReturn;
	}

	@Override
	public boolean isDaemon() {
		return false;
	}

	@Override
	public List getBiObjects() {
		return documents;
	}

	@Override
	public void setBiObjects(List biObjects) {
		this.documents = biObjects;
	}

	/**
	 * Checks if is complete without error.
	 *
	 * @return true, if is complete without error
	 */
	@Override
	public boolean isCompleteWithoutError() {
		return completeWithoutError;
	}

	@Override
	public void setProfile(IEngUserProfile profile) {
		this.userProfile = profile;
	}

	@Override
	public void deleteDBRowInCaseOfError(IProgressThreadDAO threadDAO, Integer progressThreadId) {
		LOGGER.debug("IN");
		try {
			threadDAO.deleteProgressThread(progressThreadId);
		} catch (EMFUserError e1) {
			LOGGER.error("Error in deleting the row with the progress id " + progressThreadId);
		}
		LOGGER.debug("OUT");

	}

}
