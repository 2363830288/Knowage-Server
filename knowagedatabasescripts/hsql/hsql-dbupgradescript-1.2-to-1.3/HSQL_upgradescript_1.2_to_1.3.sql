ALTER TABLE SBI_META_MODELS_VERSIONS ALTER COLUMN CONTENT LONGVARBINARY DEFAULT NULL;

ALTER TABLE SBI_META_MODELS_VERSIONS ADD FILE_MODEL LONGVARBINARY NOT NULL;

ALTER TABLE SBI_OBJ_METACONTENTS ADD ADDITIONAL_INFO VARCHAR(255) DEFAULT NULL;

ALTER TABLE SBI_CATALOG_FUNCTION  ADD OWNER VARCHAR(50) NOT NULL;
ALTER TABLE SBI_CATALOG_FUNCTION  ADD KEYWORDS VARCHAR(255) DEFAULT NULL;

ALTER TABLE SBI_CATALOG_FUNCTION ADD LABEL VARCHAR(50) NOT NULL;

ALTER TABLE SBI_CATALOG_FUNCTION ADD CONSTRAINT UNIQUE_LABEL_ORG UNIQUE (LABEL,ORGANIZATION);

ALTER TABLE SBI_CATALOG_FUNCTION ADD TYPE VARCHAR(50) DEFAULT NULL;

ALTER TABLE SBI_DATA_SET DROP COLUMN IS_PUBLIC;