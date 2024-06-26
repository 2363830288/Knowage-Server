/*
 * Knowage, Open Source Business Intelligence suite
 * Copyright (C) 2021 Engineering Ingegneria Informatica S.p.A.
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
package it.eng.knowage.knowageapi.dao;

import java.util.List;

import it.eng.knowage.knowageapi.dao.dto.SbiWidgetGallery;
import it.eng.knowage.knowageapi.resource.dto.WidgetGalleryDTO;

public interface SbiWidgetGalleryDao {

	String create(SbiWidgetGallery sbiWidgetGallery);

	String update(SbiWidgetGallery sbiWidgetGallery);

//	WidgetGalleryDTO updateCounter(SbiWidgetGallery sbiWidgetGallery);

	WidgetGalleryDTO findById(String id);

	List<WidgetGalleryDTO> findAll();

	WidgetGalleryDTO findByIdTenant(String id, String tenant);

	SbiWidgetGallery findByIdTenantSbiWidgetGallery(String id, String tenant);

	List<WidgetGalleryDTO> findAllByTenant(String tenant);

	List<WidgetGalleryDTO> findAllByTenantAndType(String tenant, String type);

	int deleteByIdTenant(String id, String tenant);

}