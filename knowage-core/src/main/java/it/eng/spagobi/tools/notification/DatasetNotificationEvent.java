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
package it.eng.spagobi.tools.notification;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.eng.spagobi.analiticalmodel.document.bo.BIObject;
import it.eng.spagobi.analiticalmodel.document.bo.ObjTemplate;
import it.eng.spagobi.analiticalmodel.document.dao.IBIObjectDAO;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.services.security.bo.SpagoBIUserProfile;
import it.eng.spagobi.services.security.service.ISecurityServiceSupplier;
import it.eng.spagobi.services.security.service.SecurityServiceSupplierFactory;
import it.eng.spagobi.tools.dataset.bo.IDataSet;
import it.eng.spagobi.tools.dataset.measurecatalogue.MeasureCatalogue;
import it.eng.spagobi.tools.dataset.measurecatalogue.MeasureCatalogueMeasure;
import it.eng.spagobi.tools.dataset.measurecatalogue.MeasureCatalogueSingleton;
import it.eng.spagobi.utilities.json.JSONUtils;

/**
 * @author Marco Cortella (marco.cortella@eng.it)
 *
 */
public class DatasetNotificationEvent extends AbstractEvent {

	public static final Logger LOGGER = Logger.getLogger(DatasetNotificationEvent.class);

	Set<String> emailAdressesOfMapAuthors;

	public DatasetNotificationEvent(String eventName, String eventDescription, IDataSet dataset) {
		this.eventName = eventName;
		this.eventDescritpion = eventDescription;
		this.argument = dataset;
	}

	/*
	 * Get all email addresses of authors of Map based on the dataset or using a Measure of the dataset
	 */
	public Set<String> retrieveEmailAddressesOfMapAuthors() throws Exception {
		if (emailAdressesOfMapAuthors == null) {
			// Set of email addresses
			Set<String> emailsAddressOfAuthors = new HashSet<>();
			if (argument != null) {
				if (argument instanceof IDataSet) {
					IDataSet dataset = (IDataSet) argument;

					// We have to get all the maps documents based on this dataset
					int datasetId = dataset.getId();

					IBIObjectDAO biObjectDAO = DAOFactory.getBIObjectDAO();

					// get all the maps documents
					List mapsDocuments = biObjectDAO.loadBIObjects("MAP", null, null);

					// Retrieve Measure Catalogue
					MeasureCatalogue catalogue = null;
					try {
						catalogue = MeasureCatalogueSingleton.getMeasureCatologue();
					} catch (Exception e) {
						LOGGER.debug("Measure Catalog Cannot be instantiated");
					}
					List<MeasureCatalogueMeasure> measuresOfDataset = null;
					if (catalogue != null) {
						measuresOfDataset = catalogue.getMeasureCatalogueMeasure(dataset);
					}

					// Search email addresses to notify

					Iterator iterator = mapsDocuments.iterator();
					while (iterator.hasNext()) {
						Object document = iterator.next();
						if (document instanceof BIObject) {
							BIObject sbiDocument = (BIObject) document;
							// check if the document is using this dataset
							if ((sbiDocument.getDataSetId() != null) && (sbiDocument.getDataSetId() == datasetId)) {
								String documentCreationUser = sbiDocument.getCreationUser();

								ISecurityServiceSupplier supplier = SecurityServiceSupplierFactory
										.createISecurityServiceSupplier();
								SpagoBIUserProfile userProfile = supplier.createUserProfile(documentCreationUser);
								Map userAttributes = userProfile.getAttributes();

								if (userAttributes.get("email") != null) {
									String emailAddressDocumentCreationUser = (String) userAttributes.get("email");
									if (!emailAddressDocumentCreationUser.isEmpty()) {
										// ADD EMAIL ADDRESS TO SET OF ADDRESS
										emailsAddressOfAuthors.add(emailAddressDocumentCreationUser);
									}
								}

							} else {
								// check if the document is using the dataset indirectly inside a Measure
								if ((measuresOfDataset != null) && (!measuresOfDataset.isEmpty())) {
									Set<String> emailMapMeasures = checkMapMeasures(measuresOfDataset, sbiDocument);
									emailsAddressOfAuthors.addAll(emailMapMeasures);
								}

							}

						}
					}

				}
			}

			this.setEmailAdressesOfMapAuthors(emailsAddressOfAuthors);
			return emailsAddressOfAuthors;
		} else {
			return this.getEmailAdressesOfMapAuthors();
		}

	}

	// Return the email addresses of the Map Authors using Measure of Dataset
	public Set<String> checkMapMeasures(List<MeasureCatalogueMeasure> measuresOfDataset, BIObject sbiDocument)
			throws JSONException, IOException {

		Set<String> emailsAddressOfAuthors = new HashSet<>();
		ObjTemplate templateMap = sbiDocument.getActiveTemplate();
		byte[] templateContentBytes = templateMap.getContent();
		if (templateContentBytes != null) {
			String templateContent = new String(templateContentBytes);
			// to check if is a XML or JSON (other map templates are XML)
			if (!templateContent.trim().startsWith("<")) {
				if (isValidJSON(templateContent)) {
					JSONObject mapTemplateJSONObject = JSONUtils.toJSONObject(templateContent);
					if (mapTemplateJSONObject.has("storeType")) {
						String storeType = mapTemplateJSONObject.getString("storeType");
						if (storeType.equalsIgnoreCase("virtualStore")) {
							if (mapTemplateJSONObject.has("storeConfig")) {
								JSONObject storeConfigJSON = mapTemplateJSONObject.getJSONObject("storeConfig");
								if (storeConfigJSON.has("params")) {
									JSONObject paramsJSON = storeConfigJSON.getJSONObject("params");
									JSONArray measureLabelsArray = paramsJSON.getJSONArray("labels");
									for (int i = 0; i < measureLabelsArray.length(); i++) {
										String measureLabel = measureLabelsArray.getString(i);

										for (MeasureCatalogueMeasure measureDataset : measuresOfDataset) {
											if (measureDataset.getLabel().equals(measureLabel)) {
												String documentCreationUser = sbiDocument.getCreationUser();
												ISecurityServiceSupplier supplier = SecurityServiceSupplierFactory
														.createISecurityServiceSupplier();
												SpagoBIUserProfile userProfile = supplier
														.createUserProfile(documentCreationUser);
												Map userAttributes = userProfile.getAttributes();

												if (userAttributes.get("email") != null) {
													String emailAddressDocumentCreationUser = (String) userAttributes
															.get("email");
													if (!emailAddressDocumentCreationUser.isEmpty()) {
														// ADD EMAIL ADDRESS TO SET OF ADDRESS
														emailsAddressOfAuthors.add(emailAddressDocumentCreationUser);
													}
												}
											}
										}

									}
								}
							}
						}
					}
				}
			}

		}
		return emailsAddressOfAuthors;
	}

	public boolean isValidJSON(final String json) {
		boolean valid = false;
		try (final JsonParser parser = new ObjectMapper().getJsonFactory().createJsonParser(json)) {
			while (parser.nextToken() != null) {
			}
			valid = true;
		} catch (JsonParseException jpe) {
			LOGGER.debug("Non-fatal error. Parsed String is not a valid JSON: " + json, jpe);
		} catch (IOException ioe) {
			LOGGER.debug("Non-fatal error. Parsed String is not a valid JSON: " + json, ioe);
		}

		return valid;
	}

	/**
	 * @return the emailAdressesOfMapAuthors
	 */
	public Set<String> getEmailAdressesOfMapAuthors() {
		return emailAdressesOfMapAuthors;
	}

	/**
	 * @param emailAdressesOfMapAuthors the emailAdressesOfMapAuthors to set
	 */
	public void setEmailAdressesOfMapAuthors(Set<String> emailAdressesOfMapAuthors) {
		this.emailAdressesOfMapAuthors = emailAdressesOfMapAuthors;
	}

}
