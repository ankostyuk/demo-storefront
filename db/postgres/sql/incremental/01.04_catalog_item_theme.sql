ALTER TABLE catalog_item ADD COLUMN ci_theme VARCHAR(15);

INSERT INTO schema_version VALUES ('01.04')