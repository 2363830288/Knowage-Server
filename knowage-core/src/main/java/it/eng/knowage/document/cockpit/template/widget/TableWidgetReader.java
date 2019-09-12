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
package it.eng.knowage.document.cockpit.template.widget;

import org.json.JSONArray;
import org.json.JSONObject;

import it.eng.spagobi.utilities.assertion.Assert;

/**
 * @author Dragan Pirkovic
 *
 */
public class TableWidgetReader extends AbstactWidgetReader {

	/**
	 * @param jsonWidget
	 */
	public TableWidgetReader(JSONObject jsonWidget) {
		this.jsonWidget = jsonWidget;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see it.eng.knowage.document.cockpit.template.widget.ICockpitWidget#getName()
	 */
	@Override
	public String getName() {
		return getContentName();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see it.eng.knowage.document.cockpit.template.widget.ICockpitWidget#getFilters()
	 */
	@Override
	public JSONArray getFilters() {
		Assert.assertNotNull(jsonWidget, "jsonWidget cannot be null");
		return jsonWidget.optJSONArray("filters");

	}

}
