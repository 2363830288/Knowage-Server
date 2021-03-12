package it.eng.knowage.knowageapi.dao.dto;

import java.io.Serializable;
import java.util.UUID;

import javax.persistence.Embeddable;

@Embeddable
public class SbiWidgetGalleryTagId implements Serializable {

	private String tag;
	private UUID widgetId;

	public SbiWidgetGalleryTagId(String tag, UUID widgetId) {
		super();
		this.tag = tag;
		this.widgetId = widgetId;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public UUID getWidgetId() {
		return widgetId;
	}

	public void setWidgetId(UUID widgetId) {
		this.widgetId = widgetId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((tag == null) ? 0 : tag.hashCode());
		result = prime * result + widgetId.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SbiWidgetGalleryTagId other = (SbiWidgetGalleryTagId) obj;
		if (tag == null) {
			if (other.tag != null)
				return false;
		} else if (!tag.equals(other.tag))
			return false;
		if (widgetId != other.widgetId)
			return false;
		return true;
	}

}
