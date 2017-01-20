ALTER TABLE company ALTER com_address DROP NOT NULL;
ALTER TABLE company ALTER com_contact_phone DROP NOT NULL;
ALTER TABLE company DROP CONSTRAINT const_company_valid;

INSERT INTO schema_version VALUES ('01.11');