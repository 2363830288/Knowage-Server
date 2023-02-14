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
package it.eng.spagobi.kpi.metadata;

import java.util.HashSet;
import java.util.Set;

import it.eng.spagobi.commons.dao.dto.SbiCategory;

// Generated 19-feb-2016 16.57.14 by Hibernate Tools 3.6.0

import it.eng.spagobi.commons.metadata.SbiDomains;
import it.eng.spagobi.commons.metadata.SbiHibernateModel;

/**
 * SbiKpiRuleOutput generated by hbm2java
 */
public class SbiKpiRuleOutput extends SbiHibernateModel implements java.io.Serializable {

	private Integer id;
	private int typeId;
	private Integer hierarchyId;
	private SbiKpiRule sbiKpiRule;
	private SbiKpiAlias sbiKpiAlias;
	private SbiDomains type;
	private SbiCategory category;
	private SbiDomains hierarchy;

	private Set<SbiKpiKpi> sbiKpiKpis = new HashSet<>();

	public SbiKpiRuleOutput() {
	}

	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @return the typeId
	 */
	public int getTypeId() {
		return typeId;
	}

	/**
	 * @param typeId the typeId to set
	 */
	public void setTypeId(int typeId) {
		this.typeId = typeId;
	}

	/**
	 * @return the sbiKpiAlias
	 */
	public SbiKpiAlias getSbiKpiAlias() {
		return sbiKpiAlias;
	}

	/**
	 * @param sbiKpiAlias the sbiKpiAlias to set
	 */
	public void setSbiKpiAlias(SbiKpiAlias sbiKpiAlias) {
		this.sbiKpiAlias = sbiKpiAlias;
	}

	/**
	 * @return the type
	 */
	public SbiDomains getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(SbiDomains type) {
		this.type = type;
	}

	/**
	 * @return the category
	 */
	public SbiCategory getCategory() {
		return category;
	}

	/**
	 * @param category the category to set
	 */
	public void setCategory(SbiCategory category) {
		this.category = category;
	}

	/**
	 * @return the sbiKpiRule
	 */
	public SbiKpiRule getSbiKpiRule() {
		return sbiKpiRule;
	}

	/**
	 * @param sbiKpiRule the sbiKpiRule to set
	 */
	public void setSbiKpiRule(SbiKpiRule sbiKpiRule) {
		this.sbiKpiRule = sbiKpiRule;
	}

	/**
	 * @return the hierarchy
	 */
	public SbiDomains getHierarchy() {
		return hierarchy;
	}

	/**
	 * @param hierarchy the hierarchy to set
	 */
	public void setHierarchy(SbiDomains hierarchy) {
		this.hierarchy = hierarchy;
	}

	/**
	 * @return the hierarchyId
	 */
	public Integer getHierarchyId() {
		return hierarchyId;
	}

	/**
	 * @param hierarchyId the hierarchyId to set
	 */
	public void setHierarchyId(Integer hierarchyId) {
		this.hierarchyId = hierarchyId;
	}

	/**
	 * @return the sbiKpiKpis
	 */
	public Set<SbiKpiKpi> getSbiKpiKpis() {
		return sbiKpiKpis;
	}

	/**
	 * @param sbiKpiKpis the sbiKpiKpis to set
	 */
	public void setSbiKpiKpis(Set<SbiKpiKpi> sbiKpiKpis) {
		this.sbiKpiKpis = sbiKpiKpis;
	}

}
