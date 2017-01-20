UPDATE account SET acc_registration_date = CURRENT_TIMESTAMP WHERE acc_registration_date IS NULL;
ALTER TABLE account ALTER COLUMN acc_registration_date SET NOT NULL;

INSERT INTO schema_version VALUES ('01.05')