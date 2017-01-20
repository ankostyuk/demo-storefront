ALTER TABLE role ALTER COLUMN role_type TYPE TEXT;
DROP TYPE t_role_type;

INSERT INTO schema_version VALUES ('01.10');