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
package it.eng.spagobi.commons.metadata;

import java.util.Objects;

// Generated 20-nov-2013 15.29.05 by Hibernate Tools 3.4.0.CR1

/**
 * SbiOrganizationDatasourceId generated by hbm2java
 */
public class SbiOrganizationThemeId implements java.io.Serializable {

	private String themeName;
	private int organizationId;

	public SbiOrganizationThemeId() {
	}

	public SbiOrganizationThemeId(String themeName, int organizationId) {
		this.themeName = themeName;
		this.organizationId = organizationId;
	}

	public String getThemeName() {
		return this.themeName;
	}

	public void setThemeName(String themeName) {
		this.themeName = themeName;
	}

	public int getOrganizationId() {
		return this.organizationId;
	}

	public void setOrganizationId(int organizationId) {
		this.organizationId = organizationId;
	}

	@Override
	public int hashCode() {
		return Objects.hash(organizationId, themeName);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SbiOrganizationThemeId other = (SbiOrganizationThemeId) obj;
		return organizationId == other.organizationId && Objects.equals(themeName, other.themeName);
	}

}
