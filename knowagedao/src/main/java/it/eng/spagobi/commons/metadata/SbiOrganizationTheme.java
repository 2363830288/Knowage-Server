/*
 * Knowage, Open Source Business Intelligence suite
 * Copyright (C) 2022 Engineering Ingegneria Informatica S.p.A.
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

/**
 *
 */
package it.eng.spagobi.commons.metadata;

import java.util.Date;

/**
 * @author albnale
 *
 */
public class SbiOrganizationTheme extends SbiHibernateModel {

	/**
	 *
	 */
	private static final long serialVersionUID = 1895036383390787978L;

	private SbiOrganizationThemeId id;
	private String config;
	private boolean active;
	private String userIn;
	private String userUp;
	private String userDe;
	private Date timeIn;
	private Date timeUp;
	private Date timeDe;
	private String sbiVersionIn;
	private String sbiVersionUp;
	private String sbiVersionDe;
	private String metaVersion;

	public SbiOrganizationTheme() {

	}

	/**
	 * @param id
	 * @param config
	 * @param active
	 */
	public SbiOrganizationTheme(SbiOrganizationThemeId id, String config, boolean active) {
		super();
		this.id = id;
		this.config = config;
		this.active = active;
	}

	/**
	 * @param id
	 * @param config
	 * @param active
	 * @param userIn
	 * @param userUp
	 * @param userDe
	 * @param timeIn
	 * @param timeUp
	 * @param timeDe
	 * @param sbiVersionIn
	 * @param sbiVersionUp
	 * @param sbiVersionDe
	 * @param metaVersion
	 */
	public SbiOrganizationTheme(SbiOrganizationThemeId id, String config, boolean active, String userIn, String userUp, String userDe, Date timeIn, Date timeUp,
			Date timeDe, String sbiVersionIn, String sbiVersionUp, String sbiVersionDe, String metaVersion) {
		super();
		this.id = id;
		this.config = config;
		this.active = active;
		this.userIn = userIn;
		this.userUp = userUp;
		this.userDe = userDe;
		this.timeIn = timeIn;
		this.timeUp = timeUp;
		this.timeDe = timeDe;
		this.sbiVersionIn = sbiVersionIn;
		this.sbiVersionUp = sbiVersionUp;
		this.sbiVersionDe = sbiVersionDe;
		this.metaVersion = metaVersion;
	}

	/**
	 * @return the id
	 */
	public SbiOrganizationThemeId getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(SbiOrganizationThemeId id) {
		this.id = id;
	}

	/**
	 * @return the config
	 */
	public String getConfig() {
		return config;
	}

	/**
	 * @param config the config to set
	 */
	public void setConfig(String config) {
		this.config = config;
	}

	/**
	 * @return the active
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * @param active the active to set
	 */
	public void setActive(boolean active) {
		this.active = active;
	}

	public String getUserIn() {
		return userIn;
	}

	public void setUserIn(String userIn) {
		this.userIn = userIn;
	}

	public String getUserUp() {
		return userUp;
	}

	public void setUserUp(String userUp) {
		this.userUp = userUp;
	}

	public String getUserDe() {
		return userDe;
	}

	public void setUserDe(String userDe) {
		this.userDe = userDe;
	}

	public Date getTimeIn() {
		return timeIn;
	}

	public void setTimeIn(Date timeIn) {
		this.timeIn = timeIn;
	}

	public Date getTimeUp() {
		return timeUp;
	}

	public void setTimeUp(Date timeUp) {
		this.timeUp = timeUp;
	}

	public Date getTimeDe() {
		return timeDe;
	}

	public void setTimeDe(Date timeDe) {
		this.timeDe = timeDe;
	}

	public String getSbiVersionIn() {
		return sbiVersionIn;
	}

	public void setSbiVersionIn(String sbiVersionIn) {
		this.sbiVersionIn = sbiVersionIn;
	}

	public String getSbiVersionUp() {
		return sbiVersionUp;
	}

	public void setSbiVersionUp(String sbiVersionUp) {
		this.sbiVersionUp = sbiVersionUp;
	}

	public String getSbiVersionDe() {
		return sbiVersionDe;
	}

	public void setSbiVersionDe(String sbiVersionDe) {
		this.sbiVersionDe = sbiVersionDe;
	}

	public String getMetaVersion() {
		return metaVersion;
	}

	public void setMetaVersion(String metaVersion) {
		this.metaVersion = metaVersion;
	}

}
