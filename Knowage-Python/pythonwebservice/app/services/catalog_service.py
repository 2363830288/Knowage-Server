#!/usr/bin/env python3

# Knowage, Open Source Business Intelligence suite
# Copyright (C) 2016 Engineering Ingegneria Informatica S.p.A.
#
# Knowage is free software: you can redistribute it and/or modify
# it under the terms of the GNU Affero General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
#  (at your option) any later version.
#
# Knowage is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU Affero General Public License for more details.
#
# You should have received a copy of the GNU Affero General Public License
# along with this program.  If not, see <http://www.gnu.org/licenses/>.

from flask import Blueprint, request
from app.utilities import security, utils

catalog = Blueprint('catalog', __name__)
#url: knowage_addr:port/catalog

@catalog.route('execute', methods = ['POST'])
def python_function_execute():
    # retrieve input parameters
    try:
        data = request.get_json()
        datastore = data['datastore']
        col_names = datastore["metaData"]["fields"]
        rows = datastore["rows"]
        datastoreDataframe = utils.convertKnowageDatasetToDataframe(col_names, rows)
        token = data['script']
        isAuthenticated, script = security.jwtToken2pythonScript(token)
        input_values = data['input']
    except Exception as e:
        return str(e), 400

    if not isAuthenticated:
        return "Unauthorized", 401

    # resolve references to datastore
    script = script.replace("${df}", "df_")
    #build inputs dictionary
    inputs = buildInputVariables(input_values)
    # resolve input values references
    for input in inputs:
        if input_values[input].type == 'column':
            script = script.replace("df_." + input, "df_." + inputs['input'])
        if input_values[input].type == 'variable':
            script = script.replace(input, "inputs_.get(\'" + input + "\')")
    # execute script
    try:
        namespace = {"df_": "", "inputs_": inputs}
        exec (script, namespace)
    except Exception as e:
        return str(e), 400
    # collect script result
    df = namespace["df_"]

    # convert dataframe to knowage json format
    knowage_json = utils.convertDataframeToKnowageDataset(df)

    return str(knowage_json).replace('\'', "\""), 200

def buildInputVariables(input_values):
    return []