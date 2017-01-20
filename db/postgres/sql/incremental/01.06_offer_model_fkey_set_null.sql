ALTER TABLE offer DROP CONSTRAINT offer_off_model_id_fkey;
ALTER TABLE offer ADD  CONSTRAINT offer_off_model_id_fkey FOREIGN KEY(off_model_id) REFERENCES model(mod_id) ON DELETE SET NULL;

INSERT INTO schema_version VALUES ('01.06');