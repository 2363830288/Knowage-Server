package it.eng.spagobi.api.v3;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonIgnore;

import it.eng.spagobi.analiticalmodel.execution.service.ExecuteAdHocUtility;
import it.eng.spagobi.tools.dataset.constants.DataSetConstants;
import it.eng.spagobi.tools.dataset.metadata.SbiDataSet;

class DataSetResourceMainFacade {

	static protected Logger logger = Logger.getLogger(DataSetResourceMainFacade.class);

	private static final Map<String, String> TYPE_2_DS_TYPE_CD = DataSetConstants.code2name;

	protected final SbiDataSet dataset;
	private final List<DataSetResourceAction> actions = new ArrayList<>();

	public DataSetResourceMainFacade(SbiDataSet dataset) {
		super();
		this.dataset = dataset;
	}

	public Integer getId() {
		return dataset.getId().getDsId();
	}

	@JsonIgnore
	public boolean isGeoDataSet() {
		boolean ret = false;
		try {
			String meta = getDsMetadata();

			if (meta != null && !meta.equals("")) {
				ret = ExecuteAdHocUtility.hasGeoHierarchy(meta);
			}
		} catch (Exception e) {
			logger.error("Error during check of Geo spatial column", e);
		}

		return ret;
	}

	public String getLabel() {
		return dataset.getLabel();
	}

	public String getName() {
		return dataset.getName();
	}

	public String getOwner() {
		return dataset.getOwner();
	}

	public List<DataSetResourceAction> getActions() {
		return actions;
	}

	@JsonIgnore
	public String getType() {
		return dataset.getType();
	}

	public String getDsTypeCd() {
		String type = dataset.getType();
		String ret = "";

		if (TYPE_2_DS_TYPE_CD.containsKey(type)) {
			ret = TYPE_2_DS_TYPE_CD.get(type);
		}

		return ret;
	}

	@JsonIgnore
	public String getDsMetadata() {
		return dataset.getDsMetadata();
	}

}