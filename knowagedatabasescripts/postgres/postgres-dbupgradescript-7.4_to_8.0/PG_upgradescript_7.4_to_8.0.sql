-- 06/04/2021
UPDATE SBI_CONFIG SC SET VALUE_CHECK = 'it-IT,en-US,fr-FR,es-ES,pt-BR,en-GB,zh-Hans-CN,de-DE' WHERE LABEL = 'SPAGOBI.LANGUAGE_SUPPORTED.LANGUAGES';
UPDATE SBI_CONFIG SC SET VALUE_CHECK = 'en-US' WHERE LABEL = 'SPAGOBI.LANGUAGE_SUPPORTED.LANGUAGE.default';

UPDATE SBI_DOMAINS SET VALUE_CD = 'it-IT', DOMAIN_NM='Language tag code', VALUE_NM = 'Italian (IT)', VALUE_DS=VALUE_NM WHERE DOMAIN_CD = 'LANG' AND VALUE_NM = 'Italian' AND DOMAIN_NM = 'Language ISO code';
UPDATE SBI_DOMAINS SET VALUE_CD = 'en-US', DOMAIN_NM='Language tag code', VALUE_NM = 'English (US)', VALUE_DS=VALUE_NM WHERE DOMAIN_CD = 'LANG' AND VALUE_NM = 'English' AND DOMAIN_NM = 'Language ISO code';
UPDATE SBI_DOMAINS SET VALUE_CD = 'es-ES', DOMAIN_NM='Language tag code', VALUE_NM = 'Spanish (ES)', VALUE_DS=VALUE_NM WHERE DOMAIN_CD = 'LANG' AND VALUE_NM = 'Spanish' AND DOMAIN_NM = 'Language ISO code';
UPDATE SBI_DOMAINS SET VALUE_CD = 'fr-FR', DOMAIN_NM='Language tag code', VALUE_NM = 'French (FR)', VALUE_DS=VALUE_NM WHERE DOMAIN_CD = 'LANG' AND VALUE_NM = 'French' AND DOMAIN_NM = 'Language ISO code';
UPDATE SBI_DOMAINS SET VALUE_CD = 'pt-BR', DOMAIN_NM='Language tag code', VALUE_NM = 'Portoguese (BR)', VALUE_DS=VALUE_NM WHERE DOMAIN_CD = 'LANG' AND VALUE_NM = 'Portoguese' AND DOMAIN_NM = 'Language ISO code';

ALTER TABLE SBI_MENU ALTER COLUMN CUST_ICON TYPE TEXT USING CUST_ICON::TEXT;

CREATE TABLE SBI_WIDGET_GALLERY (
  UUID CHAR(36) NOT NULL,
  NAME VARCHAR(200) NOT NULL,
  DESCRIPTION VARCHAR(500) NULL,
  TYPE VARCHAR(45) NULL,
  PREVIEW_IMAGE BYTEA NULL,
  TEMPLATE BYTEA NULL,
  AUTHOR VARCHAR(100) NULL,
  USAGE_COUNTER INT NULL,
  OUTPUT_TYPE VARCHAR(100),
  USER_IN    VARCHAR(100) NOT NULL,
  USER_UP    VARCHAR(100),
  USER_DE    VARCHAR(100),
  TIME_IN    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  TIME_UP    TIMESTAMP NULL DEFAULT NULL,
  TIME_DE    TIMESTAMP NULL DEFAULT NULL,
  SBI_VERSION_IN  VARCHAR(10),
  SBI_VERSION_UP  VARCHAR(10),
  SBI_VERSION_DE  VARCHAR(10),
  ORGANIZATION    VARCHAR(20),
  UNIQUE (NAME, ORGANIZATION),
  PRIMARY KEY (UUID, ORGANIZATION)
);

CREATE TABLE SBI_WIDGET_GALLERY_TAGS (
  WIDGET_ID CHAR(36) NOT NULL, -- widget id reference
  TAG VARCHAR(50) NOT NULL, -- tag name
  USER_IN    VARCHAR(100) NOT NULL,
  USER_UP    VARCHAR(100),
  USER_DE    VARCHAR(100),
  TIME_IN    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  TIME_UP    TIMESTAMP NULL DEFAULT NULL,    
  TIME_DE    TIMESTAMP NULL DEFAULT NULL,
  SBI_VERSION_IN  VARCHAR(10),
  SBI_VERSION_UP  VARCHAR(10),
  SBI_VERSION_DE  VARCHAR(10),
  ORGANIZATION    VARCHAR(20),
  PRIMARY KEY (WIDGET_ID, TAG, ORGANIZATION),
  CONSTRAINT FK_SBI_WIDGET_GALLERY_TAGS_1 FOREIGN KEY (WIDGET_ID, ORGANIZATION) REFERENCES SBI_WIDGET_GALLERY (UUID, ORGANIZATION) ON DELETE CASCADE
);

-- DROP OLD FOREIGN KEYS
CREATE OR REPLACE PROCEDURE DROP_FOREIGN_KEY(tableName VARCHAR)
LANGUAGE plpgsql
AS $$
DECLARE my_query VARCHAR(4000);
DECLARE temprow record;
begin
	FOR temprow in (
	select *
	from information_schema.table_constraints
	where (table_name = LOWER(tableName) or table_name = UPPER(tableName))
	      and constraint_type = 'FOREIGN KEY') LOOP

	     if (temprow is not NULL) then 
	     	EXECUTE CONCAT('ALTER TABLE ', tableName, ' DISABLE TRIGGER ALL;');
	    	EXECUTE CONCAT('ALTER TABLE ', tableName, ' DROP CONSTRAINT IF EXISTS ', temprow.constraint_name ,';');
		   	EXECUTE CONCAT('ALTER TABLE ', tableName, ' ENABLE TRIGGER ALL;');
		    commit;
		 end if;
	end loop;
end;
$$;

CALL DROP_FOREIGN_KEY('SBI_FUNCTION_INPUT_VARIABLE');
CALL DROP_FOREIGN_KEY('SBI_FUNCTION_INPUT_COLUMN');
CALL DROP_FOREIGN_KEY('SBI_FUNCTION_OUTPUT_COLUMN');
CALL DROP_FOREIGN_KEY('SBI_OBJ_FUNCTION');

DROP PROCEDURE DROP_FOREIGN_KEY;

-- DROP OLD PRIMARY KEYS
CREATE OR REPLACE PROCEDURE DROP_PRIMARY_KEY(tableName VARCHAR)
LANGUAGE plpgsql
AS $$
DECLARE my_query VARCHAR(4000);
begin
	select CONCAT('ALTER TABLE ', tableName, ' DROP CONSTRAINT IF EXISTS ', constraint_name, ';')  into my_query
	from information_schema.table_constraints
	where table_schema = 'public'
	      and (table_name = LOWER(tableName) or table_name = UPPER(tableName))
	      and constraint_type = 'PRIMARY KEY';

	     if (my_query is not NULL) then
	     	EXECUTE CONCAT('ALTER TABLE ', tableName, ' DISABLE TRIGGER ALL;');
		    EXECUTE my_query;
		   	EXECUTE CONCAT('ALTER TABLE ', tableName, ' ENABLE TRIGGER ALL;');
		   commit;
		 end if;
   end;
$$;

CALL DROP_PRIMARY_KEY('SBI_OBJ_FUNCTION');
CALL DROP_PRIMARY_KEY('SBI_FUNCTION_OUTPUT_COLUMN');
CALL DROP_PRIMARY_KEY('SBI_FUNCTION_INPUT_VARIABLE');
CALL DROP_PRIMARY_KEY('SBI_FUNCTION_INPUT_COLUMN');
CALL DROP_PRIMARY_KEY('SBI_CATALOG_FUNCTION');

DROP PROCEDURE DROP_PRIMARY_KEY;

-- DROP OLD UNIQUE KEYS
CREATE OR REPLACE PROCEDURE DROP_UNIQUE_KEY(tableName VARCHAR)
LANGUAGE plpgsql
AS $$
DECLARE my_query VARCHAR(4000);
DECLARE temprow record;
begin
	FOR temprow in (
	select *
	from information_schema.table_constraints
	where (table_name = LOWER(tableName) or table_name = UPPER(tableName))
	      and constraint_type = 'UNIQUE') LOOP

	     if (temprow is not NULL) then 
	     	EXECUTE CONCAT('ALTER TABLE ', tableName, ' DISABLE TRIGGER ALL;');
	    	EXECUTE CONCAT('ALTER TABLE ', tableName, ' DROP CONSTRAINT IF EXISTS ', temprow.constraint_name ,';');
		   	EXECUTE CONCAT('ALTER TABLE ', tableName, ' ENABLE TRIGGER ALL;');
		    commit;
		 end if;
	end loop;
end;
$$;

CALL DROP_UNIQUE_KEY('SBI_CATALOG_FUNCTION');

DROP PROCEDURE DROP_UNIQUE_KEY;

-- ADD NEW UUID COLUMN
CREATE EXTENSION IF NOT exists "uuid-ossp";
ALTER TABLE SBI_CATALOG_FUNCTION ADD COLUMN FUNCTION_UUID VARCHAR(36) DEFAULT (uuid_generate_v4());
ALTER TABLE SBI_FUNCTION_INPUT_COLUMN ADD COLUMN FUNCTION_UUID VARCHAR(36) ;
ALTER TABLE SBI_FUNCTION_INPUT_VARIABLE ADD COLUMN FUNCTION_UUID VARCHAR(36) ;
ALTER TABLE SBI_FUNCTION_OUTPUT_COLUMN ADD COLUMN FUNCTION_UUID VARCHAR(36) ;
ALTER TABLE SBI_OBJ_FUNCTION ADD COLUMN FUNCTION_UUID VARCHAR(36) ;

UPDATE SBI_FUNCTION_INPUT_COLUMN A SET FUNCTION_UUID = (SELECT FUNCTION_UUID FROM SBI_CATALOG_FUNCTION SCF WHERE SCF.FUNCTION_ID=A.FUNCTION_ID);
UPDATE SBI_FUNCTION_INPUT_VARIABLE A SET FUNCTION_UUID = (SELECT FUNCTION_UUID FROM SBI_CATALOG_FUNCTION SCF WHERE SCF.FUNCTION_ID=A.FUNCTION_ID);
UPDATE SBI_FUNCTION_OUTPUT_COLUMN A SET FUNCTION_UUID = (SELECT FUNCTION_UUID FROM SBI_CATALOG_FUNCTION SCF WHERE SCF.FUNCTION_ID=A.FUNCTION_ID);
UPDATE SBI_OBJ_FUNCTION A SET FUNCTION_UUID = (SELECT FUNCTION_UUID FROM SBI_CATALOG_FUNCTION SCF WHERE SCF.FUNCTION_ID=A.FUNCTION_ID);

ALTER TABLE SBI_CATALOG_FUNCTION ALTER COLUMN FUNCTION_UUID DROP DEFAULT;

-- CREATE NEW PRIMARY KEYS
ALTER TABLE SBI_CATALOG_FUNCTION ADD CONSTRAINT PK_SBI_CATALOG_FUNCTION PRIMARY KEY(FUNCTION_UUID, ORGANIZATION);
ALTER TABLE SBI_FUNCTION_INPUT_COLUMN ADD CONSTRAINT PK_SBI_FUNCTION_INPUT_COLUMN PRIMARY KEY(FUNCTION_UUID, COL_NAME, ORGANIZATION);
ALTER TABLE SBI_FUNCTION_INPUT_VARIABLE ADD CONSTRAINT PK_SBI_FUNCTION_INPUT_VARIABLE PRIMARY KEY(FUNCTION_UUID, VAR_NAME, ORGANIZATION);
ALTER TABLE SBI_FUNCTION_OUTPUT_COLUMN ADD CONSTRAINT PK_SBI_FUNCTION_OUTPUT_COLUMN PRIMARY KEY(FUNCTION_UUID, COL_NAME, ORGANIZATION);

-- CREATE NEW UNIQUE KEYS
ALTER TABLE SBI_OBJ_FUNCTION ADD CONSTRAINT U_SBI_OBJ_FUNCTION UNIQUE(BIOBJ_ID, FUNCTION_UUID, ORGANIZATION);

-- CREATE NEW FOREIGN KEYS
ALTER TABLE SBI_FUNCTION_INPUT_VARIABLE ADD CONSTRAINT FK_FUNCTION_INPUT_VARIABLES_1 FOREIGN KEY (FUNCTION_UUID,ORGANIZATION) REFERENCES SBI_CATALOG_FUNCTION(FUNCTION_UUID,ORGANIZATION) ON DELETE RESTRICT ON UPDATE RESTRICT;
ALTER TABLE SBI_FUNCTION_INPUT_COLUMN ADD CONSTRAINT FK_FUNCTION_INPUT_COLUMNS_1 FOREIGN KEY (FUNCTION_UUID,ORGANIZATION) REFERENCES SBI_CATALOG_FUNCTION(FUNCTION_UUID,ORGANIZATION) ON DELETE RESTRICT ON UPDATE RESTRICT;
ALTER TABLE SBI_FUNCTION_OUTPUT_COLUMN ADD CONSTRAINT FK_FUNCTION_OUTPUT_COLUMNS_1 FOREIGN KEY (FUNCTION_UUID,ORGANIZATION) REFERENCES SBI_CATALOG_FUNCTION(FUNCTION_UUID,ORGANIZATION) ON DELETE RESTRICT ON UPDATE RESTRICT;
ALTER TABLE SBI_OBJ_FUNCTION ADD CONSTRAINT FK_SBI_OBJ_FUNCTION_1 FOREIGN KEY (BIOBJ_ID) REFERENCES SBI_OBJECTS (BIOBJ_ID) ON DELETE  RESTRICT ON UPDATE RESTRICT;
ALTER TABLE SBI_OBJ_FUNCTION ADD CONSTRAINT FK_SBI_OBJ_FUNCTION_2 FOREIGN KEY (FUNCTION_UUID,ORGANIZATION) REFERENCES SBI_CATALOG_FUNCTION (FUNCTION_UUID,ORGANIZATION) ON DELETE  RESTRICT ON UPDATE RESTRICT;

-- DROP OLD ID COLUMNS
ALTER TABLE SBI_CATALOG_FUNCTION DROP COLUMN FUNCTION_ID;
ALTER TABLE SBI_FUNCTION_INPUT_COLUMN DROP COLUMN FUNCTION_ID;
ALTER TABLE SBI_FUNCTION_INPUT_VARIABLE DROP COLUMN FUNCTION_ID;
ALTER TABLE SBI_FUNCTION_OUTPUT_COLUMN DROP COLUMN FUNCTION_ID;
ALTER TABLE SBI_OBJ_FUNCTION DROP COLUMN FUNCTION_ID;
