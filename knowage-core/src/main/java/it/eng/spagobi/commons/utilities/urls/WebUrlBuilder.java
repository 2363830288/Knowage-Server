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
package it.eng.spagobi.commons.utilities.urls;

import java.io.File;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import it.eng.LightNavigationConstants;
import it.eng.knowage.commons.security.KnowageSystemConfiguration;
import it.eng.knowage.wapp.Environment;
import it.eng.knowage.wapp.Version;
import it.eng.spago.configuration.ConfigSingleton;

/**
 * The implementation of IUrlBuilder used when SpagoBI is used as a STANDALONE WEB APPLICATION
 */
public class WebUrlBuilder implements IUrlBuilder {

	private static final Logger LOGGER = Logger.getLogger(WebUrlBuilder.class);
	private static final String KNOWAGE_VERSION = Version.getCompleteVersion();
	private static final Environment ENVIRONMENT = Version.getEnvironment();
	private static final String[] REG_EXP_RESOURCES = { "/js/(src)", "/themes/commons/(css)/" };

	private String baseURL = "";
	private String baseResourceURL = "";

	/**
	 * Inits the.
	 *
	 * @param aHttpServletRequest the a http servlet request
	 */
	public void init(HttpServletRequest aHttpServletRequest) {
		LOGGER.debug("IN");
		baseResourceURL = KnowageSystemConfiguration.getKnowageContext() + "/";
		LOGGER.debug("baseResourceURL" + baseResourceURL);
		baseURL = baseResourceURL + "servlet/AdapterHTTP";
		LOGGER.debug("OUT.baseURL=" + baseURL);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see it.eng.spagobi.commons.utilities.urls.IUrlBuilder#getUrl(javax.servlet.http.HttpServletRequest, java.util.Map)
	 */
	@Override
	public String getUrl(HttpServletRequest aHttpServletRequest, Map parameters) {
		LOGGER.debug("IN");
		init(aHttpServletRequest);
		// ConfigSingleton.getInstance().getAttribute(dal master fin qua SPAGO_ADAPTERHTTP_URL)
		StringBuilder sb = new StringBuilder();
		sb.append(baseURL);
		if (parameters != null) {
			Iterator keysIt = parameters.keySet().iterator();
			boolean isFirst = true;
			String paramName = null;
			Object paramValue = null;
			while (keysIt.hasNext()) {
				paramName = (String) keysIt.next();
				paramValue = parameters.get(paramName);
				if (paramValue == null) {
					LOGGER.warn("Parameter with name " + paramName + " has null value. This parameter will be not considered.");
					continue;
				}
				if (isFirst) {
					sb.append("?");
					isFirst = false;
				} else {
					sb.append("&");
				}
				sb.append(paramName + "=" + paramValue.toString());
			}
		}
		// propagating light navigator id
		String lightNavigatorId = aHttpServletRequest.getParameter(LightNavigationConstants.LIGHT_NAVIGATOR_ID);
		if (lightNavigatorId != null && !lightNavigatorId.trim().equals("")) {
			if (sb.indexOf("?") != -1) {
				sb.append("&" + LightNavigationConstants.LIGHT_NAVIGATOR_ID + "=" + lightNavigatorId);
			} else {
				sb.append("?" + LightNavigationConstants.LIGHT_NAVIGATOR_ID + "=" + lightNavigatorId);
			}
		}
		String url = sb.toString();

		LOGGER.debug("OUT.url=" + url);
		return url;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see it.eng.spagobi.commons.utilities.urls.IUrlBuilder#getResourceLink(javax.servlet.http.HttpServletRequest, java.lang.String)
	 */
	@Override
	public String getResourceLink(HttpServletRequest aHttpServletRequest, String originalUrl) {
		LOGGER.debug("IN.originalUrl=" + originalUrl);
		init(aHttpServletRequest);
		originalUrl = originalUrl.trim();
		if (originalUrl.startsWith("/")) {
			originalUrl = originalUrl.substring(1);
		}
		originalUrl = baseResourceURL + originalUrl;
		if (ENVIRONMENT == Environment.PRODUCTION)
			originalUrl = concatSrcWithKnowageVersion(originalUrl);
		LOGGER.debug("OUT.originalUrl=" + originalUrl);
		return originalUrl;
	}

	@Override
	public String getResourceLinkByTheme(HttpServletRequest aHttpServletRequest, String originalUrl, String theme) {
		LOGGER.debug("IN");
		String rootPath = ConfigSingleton.getRootPath();
		String urlByTheme = originalUrl.trim();
		if (originalUrl.startsWith("/"))
			originalUrl = originalUrl.substring(1);

		if (theme != null) {
			urlByTheme = "/themes/" + theme + "/" + originalUrl;
		}

		String urlComplete = rootPath + urlByTheme;
		// check if object exists
		File check = new File(urlComplete);
		// if file
		if (!check.exists()) {
			urlByTheme = "/themes/sbi_default/" + originalUrl;

			// check if the default object exist
			urlComplete = rootPath + urlByTheme;
			File checkDef = new File(urlComplete);
			// if file
			if (!checkDef.exists()) {
				urlByTheme = originalUrl;
			}
		}

		LOGGER.debug("OUT");
		return getResourceLink(aHttpServletRequest, urlByTheme);
	}

	private String concatSrcWithKnowageVersion(String url) {
		LOGGER.debug("IN");
		for (int i = 0; i < REG_EXP_RESOURCES.length; i++) {
			String pattern = REG_EXP_RESOURCES[i];
			Pattern srcPattern = Pattern.compile(pattern);
			Matcher srcMatcher = srcPattern.matcher(url);

			if (srcMatcher.find()) {
				String src = srcMatcher.group(1);
				url = url.replaceFirst(src, src + "-" + KNOWAGE_VERSION);
			}
		}

		LOGGER.debug("OUT");
		return url;
	}

}
