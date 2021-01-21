DELETE FROM SBI_WS_EVENT WHERE ORGANIZATION = ?
DELETE FROM SBI_TIMESPAN WHERE ORGANIZATION = ?

DELETE FROM SBI_TRIGGER_PAUSED WHERE ORGANIZATION = ?

DELETE FROM SBI_CROSS_NAVIGATION_PAR WHERE ORGANIZATION = ?
DELETE FROM SBI_CROSS_NAVIGATION WHERE ORGANIZATION = ?

DELETE FROM SBI_GL_TABLE_WLIST WHERE ORGANIZATION = ?
DELETE FROM SBI_GL_BNESS_CLS_WLIST WHERE ORGANIZATION = ?
DELETE FROM SBI_GL_ATTRIBUTES WHERE ORGANIZATION = ?
DELETE FROM SBI_GL_WLIST WHERE ORGANIZATION = ?
-- As long as DELETE ORDER BY instruction is not safe for null values we set all parent keys to null
-- in order to allow deletion without ordering issues
UPDATE SBI_GL_CONTENTS SET PARENT_ID = NULL WHERE ORGANIZATION = ?
DELETE FROM SBI_GL_CONTENTS WHERE ORGANIZATION = ?
DELETE FROM SBI_GL_WORD_ATTR WHERE ORGANIZATION = ?
DELETE FROM SBI_GL_REFERENCES WHERE ORGANIZATION = ?
DELETE FROM SBI_GL_DOCWLIST WHERE ORGANIZATION = ?
DELETE FROM SBI_GL_DATASETWLIST WHERE ORGANIZATION = ?
DELETE FROM SBI_GL_WORD WHERE ORGANIZATION = ?
DELETE FROM SBI_GL_GLOSSARY WHERE ORGANIZATION = ?

DELETE FROM SBI_META_DATASET_REL WHERE ORGANIZATION =?
DELETE FROM SBI_META_DOCUMENT_REL WHERE ORGANIZATION = ?
DELETE FROM SBI_META_JOB_SOURCE WHERE ORGANIZATION = ?
DELETE FROM SBI_META_JOB_TABLE WHERE ORGANIZATION = ?
DELETE FROM SBI_META_DS_BC WHERE ORGANIZATION = ?
DELETE FROM SBI_META_TABLE_BC WHERE ORGANIZATION = ?
DELETE FROM SBI_META_BC_ATTRIBUTE WHERE ORGANIZATION = ?
DELETE FROM SBI_META_BC WHERE ORGANIZATION = ?
DELETE FROM SBI_META_TABLE_COLUMN WHERE ORGANIZATION = ?
DELETE FROM SBI_META_TABLE WHERE ORGANIZATION = ?
DELETE FROM SBI_META_JOB WHERE ORGANIZATION =?
DELETE FROM SBI_META_SOURCE WHERE ORGANIZATION =?

DELETE FROM SBI_METAMODEL_PARUSE WHERE ORGANIZATION = ?
DELETE FROM SBI_METAMODEL_PARVIEW WHERE ORGANIZATION = ?
DELETE FROM SBI_METAMODEL_PAR WHERE ORGANIZATION = ?

DELETE FROM SBI_OBJ_FUNC_ORGANIZER WHERE ORGANIZATION = ?
-- As long as DELETE ORDER BY instruction is not safe for null values we set all parent keys to null
-- in order to allow deletion without ordering issues
UPDATE SBI_FUNCTIONS_ORGANIZER SET PARENT_FUNCT_ID = NULL WHERE ORGANIZATION = ?
DELETE FROM SBI_FUNCTIONS_ORGANIZER WHERE ORGANIZATION = ?

-- The following tables do not contain tenant info: see KNOWAGE-2245
-- As a workaround, we cacht references from SBI_CATALOG_FUNCTION
DELETE FROM SBI_FUNCTION_INPUT_VARIABLE WHERE FUNCTION_ID IN (SELECT c.FUNCTION_ID FROM SBI_CATALOG_FUNCTION c WHERE c.ORGANIZATION = ?)
DELETE FROM SBI_FUNCTION_INPUT_COLUMN 	WHERE FUNCTION_ID IN (SELECT c.FUNCTION_ID FROM SBI_CATALOG_FUNCTION c WHERE c.ORGANIZATION = ?)
DELETE FROM SBI_FUNCTION_OUTPUT_COLUMN 	WHERE FUNCTION_ID IN (SELECT c.FUNCTION_ID FROM SBI_CATALOG_FUNCTION c WHERE c.ORGANIZATION = ?)
DELETE FROM SBI_OBJ_FUNCTION		 	WHERE FUNCTION_ID IN (SELECT c.FUNCTION_ID FROM SBI_CATALOG_FUNCTION c WHERE c.ORGANIZATION = ?)
DELETE FROM SBI_CATALOG_FUNCTION WHERE ORGANIZATION = ?

DELETE FROM SBI_ARTIFACTS_VERSIONS WHERE ORGANIZATION = ?
DELETE FROM SBI_ARTIFACTS WHERE ORGANIZATION = ?
DELETE FROM SBI_META_MODELS_VERSIONS WHERE ORGANIZATION = ?
DELETE FROM SBI_META_MODELS WHERE ORGANIZATION = ?
DELETE FROM SBI_EXT_ROLES_CATEGORY WHERE EXT_ROLE_ID IN (SELECT t.EXT_ROLE_ID FROM SBI_EXT_ROLES t WHERE t.ORGANIZATION = ?)
DELETE FROM SBI_META_OBJ_DS WHERE ORGANIZATION = ?
DELETE FROM SBI_OBJ_DATA_SET WHERE ORGANIZATION = ?

-- This removes the references to federations, otherwise the foreign key will throw an error
UPDATE SBI_DATA_SET SET FEDERATION_ID = NULL WHERE ORGANIZATION = ? 
-- The following statement will erase tenant also into SBI_DATA_SET_FEDERATION table, because foreign key is ON DELETE CASCADE
DELETE FROM SBI_FEDERATION_DEFINITION WHERE ORGANIZATION = ?
DELETE FROM SBI_DATA_SET WHERE ORGANIZATION = ?

-- We can now proceed deleting other kinds of datasets than SbiFederatedDataSet (query, file, ....)
DELETE FROM SBI_DATA_SET WHERE ORGANIZATION = ?
DELETE FROM SBI_OBJECTS_RATING WHERE ORGANIZATION = ?
DELETE FROM SBI_REMEMBER_ME WHERE ORGANIZATION = ?
DELETE FROM SBI_AUDIT WHERE ORGANIZATION = ?
DELETE FROM SBI_EVENTS_ROLES WHERE ROLE_ID IN (SELECT t.EXT_ROLE_ID FROM SBI_EXT_ROLES t WHERE t.ORGANIZATION = ?)
DELETE FROM SBI_EVENTS_LOG WHERE ORGANIZATION = ?
DELETE FROM SBI_EVENTS WHERE ORGANIZATION = ?
DELETE FROM SBI_SUBREPORTS WHERE ORGANIZATION = ?
DELETE FROM SBI_OBJ_PARUSE WHERE ORGANIZATION = ?
DELETE FROM SBI_OBJ_PARVIEW WHERE ORGANIZATION = ?
DELETE FROM SBI_PARUSE_CK WHERE ORGANIZATION = ?
DELETE FROM SBI_PARUSE_DET WHERE ORGANIZATION = ?
DELETE FROM SBI_PARUSE WHERE ORGANIZATION = ?
DELETE FROM SBI_FUNC_ROLE WHERE ORGANIZATION = ?
DELETE FROM SBI_OBJ_FUNC WHERE ORGANIZATION = ?
-- As long as DELETE ORDER BY instruction is not safe for null values we set all parent keys to null
-- in order to allow deletion without ordering issues
UPDATE SBI_FUNCTIONS SET PARENT_FUNCT_ID = NULL WHERE ORGANIZATION = ?
DELETE FROM SBI_FUNCTIONS WHERE ORGANIZATION = ?
DELETE FROM SBI_CHECKS WHERE ORGANIZATION = ?
DELETE FROM SBI_OBJ_PAR WHERE ORGANIZATION = ?
DELETE FROM SBI_PARAMETERS WHERE ORGANIZATION = ?
DELETE FROM SBI_OBJECT_NOTES WHERE ORGANIZATION = ?
DELETE FROM SBI_OBJECT_TEMPLATES WHERE ORGANIZATION = ?
DELETE FROM SBI_OBJ_METACONTENTS WHERE ORGANIZATION = ?
DELETE FROM SBI_SUBOBJECTS WHERE ORGANIZATION = ?
DELETE FROM SBI_SNAPSHOTS WHERE ORGANIZATION = ?
DELETE FROM SBI_OBJ_STATE WHERE ORGANIZATION = ?

DELETE FROM SBI_EXT_USER_ROLES WHERE EXT_ROLE_ID IN (SELECT t.EXT_ROLE_ID FROM SBI_EXT_ROLES t WHERE t.ORGANIZATION = ?)
DELETE FROM SBI_LOV  WHERE ORGANIZATION = ?
DELETE FROM SBI_GEO_MAP_FEATURES WHERE ORGANIZATION = ?
DELETE FROM SBI_GEO_FEATURES WHERE ORGANIZATION = ?
DELETE FROM SBI_GEO_MAPS WHERE ORGANIZATION = ?
DELETE FROM SBI_ROLES_LAYERS WHERE ORGANIZATION = ?
DELETE FROM SBI_GEO_LAYERS  WHERE ORGANIZATION = ?
DELETE FROM SBI_VIEWPOINTS WHERE ORGANIZATION = ?
DELETE FROM SBI_MENU_ROLE WHERE ORGANIZATION = ?
DELETE FROM SBI_MENU WHERE ORGANIZATION = ?
DELETE FROM SBI_DIST_LIST_OBJECTS WHERE ORGANIZATION = ?
DELETE FROM SBI_DIST_LIST_USER WHERE ORGANIZATION = ?
DELETE FROM SBI_DIST_LIST WHERE ORGANIZATION = ?

DELETE FROM SBI_ALERT_LOG WHERE ORGANIZATION = ?
DELETE FROM SBI_ALERT_ACTION WHERE ORGANIZATION = ?
DELETE FROM SBI_ALERT WHERE ORGANIZATION = ?
DELETE FROM SBI_ALERT_LISTENER WHERE ORGANIZATION = ?


DELETE FROM SBI_KPI_SCORECARD_KPI WHERE KPI_ID IN (SELECT k.ID FROM SBI_KPI_KPI k WHERE k.ORGANIZATION = ?)
DELETE FROM SBI_KPI_VALUE WHERE KPI_ID IN (SELECT k.ID FROM SBI_KPI_KPI k WHERE k.ORGANIZATION = ?)
DELETE FROM SBI_KPI_VALUE_EXEC_LOG WHERE SCHEDULER_ID IN (SELECT x.ID FROM SBI_KPI_EXECUTION x WHERE x.ORGANIZATION = ?)

DELETE FROM SBI_KPI_TARGET_VALUE WHERE ORGANIZATION = ?
DELETE FROM SBI_KPI_TARGET WHERE ORGANIZATION = ?

DELETE FROM SBI_KPI_THRESHOLD_VALUE WHERE ORGANIZATION = ?
DELETE FROM SBI_KPI_THRESHOLD WHERE ORGANIZATION = ?
DELETE FROM SBI_KPI_EXECUTION_KPI WHERE KPI_ID IN (SELECT k.ID FROM SBI_KPI_KPI k WHERE k.ORGANIZATION = ?)
DELETE FROM SBI_KPI_EXECUTION_FILTER WHERE ORGANIZATION = ?
DELETE FROM SBI_KPI_EXECUTION WHERE ORGANIZATION = ?
DELETE FROM SBI_KPI_KPI_RULE_OUTPUT WHERE KPI_ID IN (SELECT k.ID FROM SBI_KPI_KPI k WHERE k.ORGANIZATION = ?)
DELETE FROM SBI_KPI_RULE_OUTPUT WHERE ORGANIZATION = ?
DELETE FROM SBI_KPI_RULE_PLACEHOLDER WHERE RULE_ID IN (SELECT k.ID FROM SBI_KPI_RULE k WHERE k.ORGANIZATION = ?)
DELETE FROM SBI_KPI_ALIAS WHERE ORGANIZATION = ?
DELETE FROM SBI_KPI_PLACEHOLDER WHERE ORGANIZATION = ?
DELETE FROM SBI_KPI_RULE WHERE ORGANIZATION = ?
DELETE FROM SBI_KPI_KPI WHERE ORGANIZATION = ?

DELETE FROM SBI_KPI_SCORECARD WHERE ORGANIZATION = ?
DELETE FROM SBI_AUTHORIZATIONS_ROLES WHERE ORGANIZATION = ?
DELETE FROM SBI_EXT_ROLES WHERE ORGANIZATION = ?
DELETE FROM SBI_OBJ_METADATA WHERE ORGANIZATION = ?
DELETE FROM SBI_OUTPUT_PARAMETER WHERE ORGANIZATION = ?
DELETE FROM SBI_OBJECTS WHERE ORGANIZATION = ?
DELETE FROM SBI_BINARY_CONTENTS WHERE ORGANIZATION = ?
DELETE FROM SBI_UDP_VALUE WHERE ORGANIZATION = ?
DELETE FROM SBI_UDP WHERE ORGANIZATION = ?
DELETE FROM SBI_USER_ATTRIBUTES WHERE ATTRIBUTE_ID IN (SELECT t.ATTRIBUTE_ID FROM SBI_ATTRIBUTE t WHERE t.ORGANIZATION = ?)
DELETE FROM SBI_ATTRIBUTE WHERE ORGANIZATION = ?  
DELETE FROM SBI_ACCESSIBILITY_PREFERENCES WHERE ORGANIZATION = ?  
DELETE FROM SBI_USER WHERE ORGANIZATION = ? 
DELETE FROM SBI_I18N_MESSAGES WHERE ORGANIZATION = ?
DELETE FROM SBI_COMMUNITY WHERE ORGANIZATION = ?
DELETE FROM SBI_COMMUNITY_USERS WHERE ORGANIZATION = ?
DELETE FROM SBI_WHATIF_WORKFLOW WHERE ORGANIZATION = ?
DELETE FROM SBI_AUTHORIZATIONS WHERE ORGANIZATION = ?
DELETE FROM SBI_CACHE_ITEM WHERE ORGANIZATION = ?
DELETE FROM SBI_IMAGES WHERE ORGANIZATION = ?
DELETE FROM SBI_ORGANIZATION_DATASOURCE WHERE ORGANIZATION = ?
DELETE FROM SBI_PRODUCT_TYPE_ENGINE WHERE ORGANIZATION = ?
DELETE FROM SBI_ORGANIZATION_PRODUCT_TYPE WHERE ORGANIZATION = ?
DELETE FROM SBI_ORGANIZATIONS WHERE NAME = ?