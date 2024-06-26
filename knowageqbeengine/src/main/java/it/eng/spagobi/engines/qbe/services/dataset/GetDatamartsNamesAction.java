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
package it.eng.spagobi.engines.qbe.services.dataset;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import it.eng.spago.base.SourceBean;
import it.eng.spagobi.engines.qbe.QbeEngineConfig;
import it.eng.spagobi.engines.qbe.services.core.AbstractQbeEngineAction;
import it.eng.spagobi.utilities.engines.SpagoBIEngineServiceException;
import it.eng.spagobi.utilities.engines.SpagoBIEngineServiceExceptionHandler;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;
import it.eng.spagobi.utilities.service.JSONSuccess;


/**
 * The Class GetDatamartsNamesAction.
 *
 * @author Davide Zerbetto (davide.zerbetto@eng.it)
 */
public class GetDatamartsNamesAction extends AbstractQbeEngineAction {

	/** Logger component. */
	private static final Logger LOGGER = Logger.getLogger(GetDatamartsNamesAction.class);

	public static final String ENGINE_NAME = "SpagoBIQbeEngine";

	// INPUT PARAMETERS
	public static final String CALLBACK = "callback";

	// OUTPUT PARAMETERS

	// SESSION PARAMETRES

	// AVAILABLE PUBLISHERS

    @Override
	public void service(SourceBean request, SourceBean response) {

    	LOGGER.debug("IN");

    	try {
			super.service(request, response);

			List<String> datamartsName = getMetamodelNames();

			JSONArray array = new JSONArray();
			Iterator<String> it = datamartsName.iterator();
			while (it.hasNext()) {
				String aDatamartName = it.next();
				JSONObject temp = new JSONObject();
				temp.put("datamart", aDatamartName);
				array.put(temp);
			}

			String callback = getAttributeAsString( CALLBACK );

			try {
				if(callback == null) {
					writeBackToClient( new JSONSuccess( array ));
				} else {
					writeBackToClient( new JSONSuccess( array, callback ));
				}
			} catch (IOException e) {
				String message = "Impossible to write back the responce to the client";
				throw new SpagoBIEngineServiceException(getActionName(), message, e);
			}

		} catch (Throwable t) {
			throw SpagoBIEngineServiceExceptionHandler.getInstance().getWrappedException(getActionName(), getEngineInstance(), t);
		} finally {
			LOGGER.debug("OUT");
		}

	}

    /**
     * @return the list of existing metamodel names
     */
	private List<String> getMetamodelNames() {

		List<String> metamodelNames;

		LOGGER.trace("IN");

		metamodelNames = null;
		try {
			metamodelNames = new ArrayList<>();
			File metamodelFolder = QbeEngineConfig.getInstance().getQbeDataMartDir();
			File[] folders = metamodelFolder.listFiles(File::isDirectory);

			if (folders == null || folders.length == 0) {
				throw new SpagoBIRuntimeException("No metamodels found!! Check configuration for metamodels repository");
			}

			for (int i = 0; i < folders.length; i++) {
				metamodelNames.add(folders[i].getName());
			}
			LOGGER.debug("OUT");
		} catch(Throwable t) {

		} finally {
			LOGGER.trace("OUT");
		}
		return metamodelNames;
	}

}
