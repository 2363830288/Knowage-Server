/*
 * Knowage, Open Source Business Intelligence suite
 * Copyright (C) 2020 Engineering Ingegneria Informatica S.p.A.

 * Knowage is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * Knowage is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.

 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package it.eng.spagobi.api.v3;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.curator.shaded.com.google.common.collect.Lists;

import it.eng.spagobi.tools.dataset.metadata.SbiDataSet;

class DataSetMainDTO extends AbstractDataSetDTO {

	private final List<String> tags = Lists.newArrayList();

	public DataSetMainDTO(SbiDataSet dataset) {
		super(dataset);

		List<String> tags = dataset.getTags().stream().map(e -> e.getName()).collect(Collectors.toList());

		this.tags.addAll(tags);

	}

	public List<String> getTags() {
		return tags;
	}

}