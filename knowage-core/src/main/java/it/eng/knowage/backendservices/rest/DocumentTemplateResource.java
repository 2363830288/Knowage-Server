/*
 * Knowage, Open Source Business Intelligence suite
 * Copyright (C) 2019 Engineering Ingegneria Informatica S.p.A.
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

package it.eng.knowage.backendservices.rest;

import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;

import it.eng.spagobi.commons.bo.UserProfile;
import it.eng.spagobi.services.content.bo.Content;
import it.eng.spagobi.services.content.service.ContentServiceImplSupplier;
import it.eng.spagobi.user.UserProfileManager;

@Path("/2.0/backendservices/documenttemplate")
public class DocumentTemplateResource {

	protected static Logger logger = Logger.getLogger(DocumentTemplateResource.class);

	@POST
	@Path("/{document_id}")
	@Consumes(MediaType.APPLICATION_JSON)
	public String readTemplate(@PathParam("document_id") Integer documentId, Map<String, String> params) {
		logger.debug("IN");
		UserProfile userProfile = UserProfileManager.getProfile();
		String userId = (String) userProfile.getUserUniqueIdentifier();
		ContentServiceImplSupplier supplier = new ContentServiceImplSupplier();
		Content content;
		try {
			content = supplier.readTemplate(userId, Integer.toString(documentId), params);
		} catch (Exception e) {
			logger.error("error while retrieving template for userId [" + userId + "] and documentId [" + documentId + "]");
			return null;
		} finally {
			logger.debug("OUT");
		}
		return content.getContent();
	}

}
