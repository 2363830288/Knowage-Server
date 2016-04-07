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
package it.eng.spagobi.engines.datamining.compute;

import it.eng.spago.security.IEngUserProfile;
import it.eng.spagobi.engines.datamining.DataMiningEngineInstance;
import it.eng.spagobi.engines.datamining.common.utils.DataMiningConstants;
import it.eng.spagobi.engines.datamining.model.DataMiningCommand;
import it.eng.spagobi.engines.datamining.model.DataMiningScript;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.log4j.Logger;
import org.jpy.PyLib;

public class PythonScriptExecutor {

	static private Logger logger = Logger.getLogger(PythonScriptExecutor.class);

	DataMiningEngineInstance dataminingInstance;
	IEngUserProfile profile;

	public PythonScriptExecutor(DataMiningEngineInstance dataminingInstance, IEngUserProfile profile) {
		this.dataminingInstance = dataminingInstance;
		this.profile = profile;
	}

	protected void evalScript(DataMiningCommand command, Boolean rerun) throws Exception {
		logger.debug("IN");

		if (!PyLib.isPythonRunning()) {
			logger.debug("No Python instance found");
			return;
		}
		// checks whether executed before
		if (rerun || command.getExecuted() == null || !command.getExecuted()) {
			logger.debug("rerun or first execution");
			// load libraries from local dir (if needed)
			DataMiningScript script = getScript(command);
			loadLibrariesFromPythonLocal(script.getLibraries());
			logger.debug("loaded specified libraries");
			// command-->script name --> execute script without output
			String scriptToExecute = getScriptCodeToEval(command);
			logger.debug("loaded script to execute");
			String codeToExecute = DataMiningUtils.replaceVariables(command.getVariables(), script.getCode());
			PyLib.execScript(codeToExecute);

			logger.debug("detects action to execute from command --> used to call functions");
			// detects action to execute from command --> used to call functions
			String action = command.getAction();
			if (action != null) {
				PyLib.execScript(action);
				logger.debug("evaluated action");
			}

			command.setExecuted(true);
			// logger.debug("delete temporary scripts");
			// deleteTemporarySourceScript(ret);
			// logger.debug("deleted temporary scripts");
		} else {
			// everithing in user workspace
			logger.debug("Command " + command.getName() + " script " + command.getScriptName() + " already executed");
		}
		logger.debug("OUT");
	}

	protected void evalExternalScript(String fileName, Map params) throws Exception { // called by DataminingPythonExecutor.externalExecution(), but
																						// DataminingPythonExecutor.externalExecution() is never used
		/*
		 * logger.debug("IN"); String path = DataMiningUtils.UPLOADED_FILE_PATH + DataMiningConstants.DATA_MINING_EXTERNAL_CODE_PATH; String codeResource = path
		 * + fileName; logger.debug(codeResource); if (params != null && !params.isEmpty()) { // get libraries param to load libraries before execution
		 * logger.debug("get libraries param  to load libraries before execution"); if (params.keySet().contains(DataMiningConstants.PARAM_LIBRARIES)) { String
		 * libs = (String) params.get(DataMiningConstants.PARAM_LIBRARIES); if (libs != null && !libs.equals("")) { loadLibrariesFromPythonLocal(libs);
		 * logger.debug("Loaded libraries " + libs); } }
		 * 
		 * String codeResourceTemp = path + "temp_" + fileName; logger.debug("Needs params for temp script " + codeResourceTemp); File codeResourceFile = new
		 * File(codeResource); if (codeResourceFile.exists()) { BufferedReader br = null; String code = null; FileWriter fw = null; BufferedWriter bw = null;
		 * try {
		 * 
		 * String sCurrentLine; StringBuffer content = new StringBuffer(); br = new BufferedReader(new FileReader(codeResourceFile));
		 * 
		 * while ((sCurrentLine = br.readLine()) != null) { content.append(sCurrentLine + "\n"); } code = content.toString();
		 * logger.debug("code read from input file"); if (code != null && !code.equals("")) { code = StringUtilities.substituteParametersInString(code, params,
		 * null, false); logger.debug("parameters replaced"); }
		 * 
		 * } catch (IOException e) { logger.error("Unable to read file " + codeResource); throw e; } finally { try { if (br != null) br.close(); } catch
		 * (IOException ex) { logger.error("Unable to close file " + codeResource); throw ex; } } try { File codeResourceFileTemp = new File(codeResourceTemp);
		 * fw = new FileWriter(codeResourceFileTemp); bw = new BufferedWriter(fw);
		 * 
		 * bw.write(code); bw.close(); fw.close();
		 * 
		 * } finally { logger.debug("temp file created"); try { if (bw != null) bw.close(); if (fw != null) fw.close();
		 * 
		 * } catch (IOException ex) { logger.error("Unable to close file writer " + codeResource); throw ex; } }
		 * logger.debug("Ready to execute external script with params"); // re.parseAndEval("source(\"" + codeResourceTemp + "\")"); **
		 * logger.debug("External script executed with params"); deleteTemporarySourceScript(codeResourceTemp); logger.debug("Deleted temp source file"); } else
		 * { logger.debug("Ready to execute external script without params"); // re.parseAndEval("source(\"" + codeResource + "\")"); **
		 * logger.debug("External script executed without params"); }
		 * 
		 * } logger.debug("OUT");
		 */
	}

	protected void deleteTemporarySourceScript(String path) {
		logger.debug("IN");
		boolean success = (new File(path)).delete();
		logger.debug("OUT");
	}

	private String getScriptCodeToEval(DataMiningCommand command) throws Exception {
		logger.debug("IN");
		String code = "";

		DataMiningScript script = getScript(command);
		if (script != null) {
			code = DataMiningUtils.replaceVariables(command.getVariables(), script.getCode());
		}
		logger.debug("OUT");
		return code;
	}

	private void loadLibrariesFromPythonLocal(String libraryNames) throws Exception {
		logger.debug("IN");

		if (libraryNames != null) {
			String[] libs = libraryNames.split(",");
			for (int i = 0; i < libs.length; i++) {
				String lib = libs[i].trim();
				PyLib.execScript("import " + lib + "\n");
				// System.out.println("import " + lib); // Add
			}
		}

		logger.debug("OUT");
	}

	private DataMiningScript getScript(DataMiningCommand command) {
		logger.debug("IN");
		String scriptName = command.getScriptName();
		if (dataminingInstance.getScripts() != null && !dataminingInstance.getScripts().isEmpty()) {
			for (Iterator it = dataminingInstance.getScripts().iterator(); it.hasNext();) {
				DataMiningScript script = (DataMiningScript) it.next();
				if (script.getName().equals(scriptName)) {
					return script;
				}
			}
		}
		logger.debug("OUT");
		return null;
	}

	private String createTemporarySourceScript(String code) throws IOException {
		logger.debug("IN");
		String name = RandomStringUtils.randomAlphabetic(10);
		File temporarySource = new File(DataMiningUtils.getUserResourcesPath(profile) + DataMiningConstants.DATA_MINING_TEMP_PATH_SUFFIX + name + ".py");
		if (!temporarySource.getParentFile().exists()) {
			temporarySource.getParentFile().mkdirs();
		}
		FileWriter fw = null;
		String ret = "";
		try {
			fw = new FileWriter(temporarySource);
			fw.write(code);
			fw.close();
			ret = temporarySource.getPath();
			ret = ret.replaceAll("\\\\", "/");
		} finally {
			if (fw != null) {
				try {
					fw.close();
				} catch (IOException e) {
					logger.error(e);
				}
			}
		}
		logger.debug("OUT");
		return ret;

	}

}
