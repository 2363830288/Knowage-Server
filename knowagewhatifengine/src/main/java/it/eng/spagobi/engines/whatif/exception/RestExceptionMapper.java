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

package it.eng.spagobi.engines.whatif.exception;

import static java.nio.charset.StandardCharsets.UTF_8;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import it.eng.spagobi.engines.whatif.common.AbstractWhatIfEngineService;
import it.eng.spagobi.utilities.engines.SpagoBIEngineServiceException;

/**
 * @author Alberto Ghedin (alberto.ghedin@eng.it)
 *
 */
@Provider
public class RestExceptionMapper extends AbstractWhatIfEngineService implements ExceptionMapper<Throwable> {

	private static Logger logger = Logger.getLogger(RestExceptionMapper.class);
	private static final String LOCALIZED_MESSAGE = "localizedMessage";
	private static final String ERROR_MESSAGE = "message";
	private static final String ERROR_SERVICE = "errorService";
	private static final String ERROR_MESSAGES = "errors";

	@Override
	public Response toResponse(Throwable e) {
		logger.debug("RestExceptionMapper:toResponse IN");
		String localizedMessage = e.getLocalizedMessage();
		String errorMessage = "";
		String errorService = "";

		if(e.getCause()!=null){
			errorMessage = e.getCause().toString();
		}else{
			errorMessage = e.toString();
		}

		// logs the error
		logger.error("Catched error", e);

		if (e instanceof SpagoBIEngineServiceException) {
			SpagoBIEngineServiceException exception = (SpagoBIEngineServiceException) e;
			errorService = exception.getServiceName();
		}

		JSONObject error = new JSONObject();
		JSONObject serializedMessages = new JSONObject();
		JSONArray errors = new JSONArray();

		try {
			error.put(LOCALIZED_MESSAGE, localizedMessage);
			error.put(ERROR_MESSAGE, errorMessage);
			error.put(ERROR_SERVICE, errorService);

			errors.put(error);

			serializedMessages.put(ERROR_MESSAGES, errors);
		} catch (JSONException e1) {
			logger.debug("Error serializing the exception ", e1);
		}

		byte[] bytesResponse = null;

		bytesResponse = serializedMessages.toString().getBytes(UTF_8);

		Response response = Response.status(200).entity(bytesResponse).header(HttpHeaders.CONTENT_ENCODING, "UTF8").build();

		logger.debug("RestExceptionMapper:toResponse OUT");

		return response;
	}

}