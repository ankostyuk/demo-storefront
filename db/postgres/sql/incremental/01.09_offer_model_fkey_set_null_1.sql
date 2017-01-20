/* Сделать описание товарного предложения необязательным и убрать проверки */
ALTER TABLE offer ALTER off_description DROP NOT NULL;
ALTER TABLE offer DROP CONSTRAINT const_offer_valid;
ALTER TABLE offer ADD CONSTRAINT const_offer_valid CHECK (
    TRIM(off_name) <> '' AND
    off_price > 0
);
/* Добавить колонку со строковым представлением параметров */
ALTER TABLE offer ADD COLUMN off_param_description TEXT;
ALTER TABLE model ADD COLUMN mod_param_description TEXT;

INSERT INTO schema_version VALUES ('01.09');