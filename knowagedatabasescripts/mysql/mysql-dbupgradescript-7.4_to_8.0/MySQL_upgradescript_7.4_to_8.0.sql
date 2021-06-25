-- 06/04/2021
UPDATE SBI_CONFIG SC SET VALUE_CHECK = 'it-IT,en-US,fr-FR,es-ES,pt-BR,en-GB,zh-Hans-CN,de-DE' WHERE LABEL = 'SPAGOBI.LANGUAGE_SUPPORTED.LANGUAGES';
UPDATE SBI_CONFIG SC SET VALUE_CHECK = 'en-US' WHERE LABEL = 'SPAGOBI.LANGUAGE_SUPPORTED.LANGUAGE.default';

UPDATE SBI_DOMAINS SET VALUE_CD = 'it-IT', DOMAIN_NM='Language tag code', VALUE_NM = 'Italian (IT)', VALUE_DS=VALUE_NM WHERE DOMAIN_CD = 'LANG' AND VALUE_NM = 'Italian' AND DOMAIN_NM = 'Language ISO code';
UPDATE SBI_DOMAINS SET VALUE_CD = 'en-US', DOMAIN_NM='Language tag code', VALUE_NM = 'English (US)', VALUE_DS=VALUE_NM WHERE DOMAIN_CD = 'LANG' AND VALUE_NM = 'English' AND DOMAIN_NM = 'Language ISO code';
UPDATE SBI_DOMAINS SET VALUE_CD = 'es-ES', DOMAIN_NM='Language tag code', VALUE_NM = 'Spanish (ES)', VALUE_DS=VALUE_NM WHERE DOMAIN_CD = 'LANG' AND VALUE_NM = 'Spanish' AND DOMAIN_NM = 'Language ISO code';
UPDATE SBI_DOMAINS SET VALUE_CD = 'fr-FR', DOMAIN_NM='Language tag code', VALUE_NM = 'French (FR)', VALUE_DS=VALUE_NM WHERE DOMAIN_CD = 'LANG' AND VALUE_NM = 'French' AND DOMAIN_NM = 'Language ISO code';
UPDATE SBI_DOMAINS SET VALUE_CD = 'pt-BR', DOMAIN_NM='Language tag code', VALUE_NM = 'Portoguese (BR)', VALUE_DS=VALUE_NM WHERE DOMAIN_CD = 'LANG' AND VALUE_NM = 'Portoguese' AND DOMAIN_NM = 'Language ISO code';

ALTER TABLE sbi_menu MODIFY COLUMN CUST_ICON TEXT NULL;

CREATE TABLE SBI_WIDGET_GALLERY (
  UUID CHAR(36) NOT NULL, -- primary key
  NAME VARCHAR(200) NOT NULL,
  DESCRIPTION VARCHAR(500) NULL,
  TYPE VARCHAR(45) NULL, -- HTML/CUSTOM CHART/PYTHON/R
  PREVIEW_IMAGE LONGBLOB NULL, -- binary of preview file
  TEMPLATE LONGTEXT NULL, -- text with template as a JSON
  AUTHOR VARCHAR(100) NULL,
  USAGE_COUNTER INT NULL, -- counter to see how many times the widgets was used
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
  OUTPUT_TYPE    VARCHAR(100),
  UNIQUE XAK1SBI_WIDGET_GALLERY (NAME, ORGANIZATION),
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
  KEY FK_SBI_WIDGET_GALLERY_TAGS_1_IDX (WIDGET_ID, TAG, ORGANIZATION),
  CONSTRAINT FK_SBI_WIDGET_GALLERY_TAGS_1 FOREIGN KEY (WIDGET_ID, ORGANIZATION) REFERENCES SBI_WIDGET_GALLERY (UUID, ORGANIZATION) ON DELETE CASCADE
) ENGINE=INNODB;
