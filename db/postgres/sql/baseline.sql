--
-- Аккаунт
--
CREATE TYPE t_account_type AS ENUM('ADMIN', 'COMPANY', 'MANAGER');
CREATE SEQUENCE account_acc_id_seq START WITH 1000;
CREATE TABLE account (
    acc_id INTEGER NOT NULL DEFAULT NEXTVAL('account_acc_id_seq') PRIMARY KEY,

    -- Адрес электронной почты (логин)
    acc_email VARCHAR(255) UNIQUE NOT NULL,

    -- Хеш пароля.
    -- Хранится в виде HEX(SHA-256(password || '{' || acc_id || '}')), где
    -- || - конкатенация строк, password - пароль, acc_id - ID аккаунта.
    -- Шестнадцатиричный результат хранится в нижнем регистре.
    acc_password VARCHAR(255) NOT NULL,

    -- When using 'mail me a new password', a random
    -- password is generated and the hash stored here.
    -- The previous password is left in place until
    -- someone actually logs in with the new password,
    -- at which point the hash is moved to acc_password
    -- and the old password is invalidated.
    acc_newpassword VARCHAR(255),

    -- Timestamp of the last time when a new password was
    -- sent, for throttling purposes
    acc_newpass_date TIMESTAMP,

    -- Initially NULL; when a accounts's e-mail address has been
    -- validated by returning with a mailed token, this is
    -- set to the current timestamp.
    acc_email_authenticated_date TIMESTAMP,

    -- Randomly generated token created when the e-mail address
    -- is set and a confirmation test mail sent.
    acc_email_token VARCHAR(63),

    -- Expiration date for the acc_email_token
    acc_email_token_expires_date TIMESTAMP,

    -- Дата регистрации
    acc_registration_date TIMESTAMP,

    -- Дата последнего входа
    acc_last_login_date TIMESTAMP,

    -- Тип аккаунта
    acc_type t_account_type NOT NULL,

    CONSTRAINT const_account_valid CHECK (
        acc_email <> '' AND
        acc_password <> ''
    )
);
ALTER SEQUENCE account_acc_id_seq OWNED BY account.acc_id;

--
-- Роль
--
CREATE TYPE t_role_type AS ENUM(
    'ROLE_MANAGER_CATALOG',
    'ROLE_MANAGER_MODEL',
    'ROLE_MANAGER_BRAND',
    'ROLE_MANAGER_DICTIONARY',
    --
    'ROLE_MANAGER_MODERATOR_OFFER'
);
CREATE TABLE role (
    role_account_id INTEGER NOT NULL REFERENCES account ON DELETE CASCADE,

    -- Тип роли
    role_type t_role_type NOT NULL,

    PRIMARY KEY(role_account_id, role_type)
);

--
-- Регион
--
CREATE TABLE region (
    reg_id serial PRIMARY KEY,

    -- Наименование региона
    reg_name VARCHAR(63) NOT NULL,

    -- индексы (modified preorder traversal)
    reg_left INTEGER NOT NULL,
    reg_right INTEGER NOT NULL,

    CONSTRAINT const_region_valid CHECK (
        TRIM(reg_name) <> '' AND
        reg_left > 0 AND reg_right > 0 AND
        reg_right > reg_left
    )
);

---
--- Страна
--- ISO 3166
--- http://www.iso.org/iso/country_codes
--- TODO DELETE?, UPDATE?
---
CREATE TABLE country (
    -- ISO alpha-2 code
    cnt_alpha2 VARCHAR(2) PRIMARY KEY,
    -- ISO alpha-3 code
    cnt_alpha3 VARCHAR(3) UNIQUE,
    -- ISO numeric code
    cnt_numeric INTEGER UNIQUE,
    -- ISO eng name
    cnt_eng_name VARCHAR(63) UNIQUE,

    -- для отображения пользователям
    cnt_name VARCHAR(63) NOT NULL UNIQUE,

    cnt_keywords VARCHAR(255),

    CONSTRAINT const_model_valid CHECK (
        LENGTH(TRIM(cnt_alpha2)) = 2 AND
        TRIM(cnt_name) <> ''
    )
);

--
-- Поставщик
--
CREATE TABLE company (
    -- ИД поставщика совпадает с ИД соответствующего аккаунта.
    -- Если в таблице account есть запись с типом 'COMPANY', то
    -- в таблице company должен ОБЯЗАТЕЛЬНО быть соотвтетсвующий элемент.
    com_id INTEGER PRIMARY KEY REFERENCES account,
    
    -- Наименование компании
    com_name VARCHAR(63) NOT NULL,

    -- Регион поставщика
    com_region_id INTEGER REFERENCES region NOT NULL,

    com_address TEXT NOT NULL,
    com_contact_phone VARCHAR(63) NOT NULL,
    com_contact_person VARCHAR(255),

    com_site VARCHAR(255),

    -- Режим работы
    com_schedule VARCHAR(255),

    -- Описание области деятельности
    com_scope TEXT,

    -- Условия доставки
    com_delivery_conditions TEXT,

    -- Условия оплаты
    com_payment_conditions TEXT,

    -- Оплата наличными
    com_payment_cash BOOLEAN,
    -- Оплата по безналичному расчету
    com_payment_cashless BOOLEAN,

    -- Логотип
    com_logo VARCHAR(63),

    CONSTRAINT const_company_valid CHECK (
        com_contact_phone <> '' AND
        com_address <> ''
    )
);

--
-- Список регионов доставки компании
--
CREATE TABLE delivery (
    del_company_id INTEGER REFERENCES company,
    del_region_id INTEGER REFERENCES region,

    PRIMARY KEY(del_company_id, del_region_id)
);

--
-- Дополнительная контактная информация
--
CREATE TYPE t_contact_type AS ENUM('PHONE', 'EMAIL', 'ICQ', 'SKYPE');
CREATE TABLE contact (
    con_id serial PRIMARY KEY,
    con_account_id INTEGER REFERENCES account NOT NULL,
    con_type t_contact_type NOT NULL,
    con_value VARCHAR(63) NOT NULL,
    con_label VARCHAR(255),
    
    CONSTRAINT const_contact_valid CHECK (
        TRIM(con_value) <> ''
    )
);

CREATE TABLE extra_currency (
    ec_id serial PRIMARY KEY,
    ec_company_id INTEGER REFERENCES company NOT NULL,
    ec_currency CHAR(3) NOT NULL,
    ec_percent DECIMAL(4,2),
    ec_fixed_rate DECIMAL(9,4),

    UNIQUE(ec_company_id, ec_currency),
    CONSTRAINT const_extra_currency_valid CHECK (
        (ec_fixed_rate IS NULL AND ec_percent IS NOT NULL AND ec_percent >= 0) OR
        (ec_fixed_rate IS NOT NULL AND ec_fixed_rate > 0 AND ec_percent IS NULL)
    )
);

--
-- Курсы валют
--
CREATE TABLE currency_rate (

    -- Исходная валюта
    cr_from_currency CHAR(3) NOT NULL,

    -- Количество исходной валюты
    cr_from_rate DECIMAL(9,4) NOT NULL,

    --Котируемая валюта
    cr_to_currency CHAR(3) NOT NULL,

    -- Количество котируемой валюты
    cr_to_rate DECIMAL(9,4) NOT NULL,

    PRIMARY KEY (cr_from_currency, cr_to_currency),
    CONSTRAINT const_currency_rate_valid CHECK (
        cr_from_rate > 0 AND cr_to_rate > 0
    )
);

/* 
 * Каталог
 */
--
-- Единица измерения
--
CREATE TABLE unit (
    unit_id serial PRIMARY KEY,
    unit_name VARCHAR(63) NOT NULL,
    unit_abbr VARCHAR(15),
    CONSTRAINT const_unit_valid CHECK (
        TRIM(unit_name) <> ''
    )
);

CREATE TABLE param_set_descriptor (
    psd_id serial PRIMARY KEY,
    psd_name VARCHAR(63) NOT NULL,
    psd_table_name VARCHAR(63) NOT NULL UNIQUE,
    CONSTRAINT const_param_set_descriptor_valid CHECK (
        psd_name <> '' AND
        psd_table_name <> ''
    )
);

CREATE TABLE param_group (
    pg_id serial PRIMARY KEY,
    pg_psd_id INTEGER NOT NULL REFERENCES param_set_descriptor ON DELETE CASCADE,
    pg_name VARCHAR(63) NOT NULL,
    pg_ordinal INTEGER,
    CONSTRAINT const_param_group_valid CHECK (
        pg_name <> ''
    )
);

CREATE TYPE t_param_type AS ENUM('BOOLEAN', 'NUMBER', 'SELECT');
CREATE TABLE param (
    par_id serial PRIMARY KEY,
    par_psd_id INTEGER NOT NULL REFERENCES param_set_descriptor ON DELETE CASCADE,
    par_pg_id INTEGER NOT NULL REFERENCES param_group,
    par_name VARCHAR(63) NOT NULL,
    par_description TEXT NOT NULL,
    par_column_name VARCHAR(63) NOT NULL UNIQUE,
    par_type t_param_type NOT NULL,
    par_ordinal INTEGER,
    par_base BOOLEAN NOT NULL,
    CONSTRAINT const_param_valid CHECK (
        par_name <> '' AND
        par_column_name <> ''
    )
);

CREATE TABLE param_boolean (
    pb_id INTEGER PRIMARY KEY REFERENCES param ON DELETE CASCADE,
    pb_true_name VARCHAR(63) NOT NULL,
    pb_false_name VARCHAR(63) NOT NULL,
    CONSTRAINT const_param_boolean_valid CHECK (
        pb_true_name <> '' AND
        pb_false_name <> ''
    )
);

CREATE TABLE param_number (
    pn_id INTEGER PRIMARY KEY REFERENCES param ON DELETE CASCADE,
    pn_min_value DECIMAL(12,4) NOT NULL,
    pn_max_value DECIMAL(12,4) NOT NULL,
    pn_unit_id INTEGER REFERENCES unit NOT NULL,
    pn_precision INTEGER NOT NULL,
    CONSTRAINT const_param_number_valid CHECK (
        pn_max_value > pn_min_value AND
        pn_precision >= 0
    )
);

CREATE TABLE param_select_option (
    pso_id serial PRIMARY KEY,
    pso_param_id INTEGER NOT NULL REFERENCES param ON DELETE CASCADE,
    pso_name VARCHAR(63) NOT NULL,
    pso_ordinal INTEGER,
    CONSTRAINT const_param_select_option_valid CHECK (
        pso_name <> ''
    )
);

--
-- Элемент каталога
-- Хранится по алгоритму Materialized Path
--
CREATE TYPE t_catalog_item_type AS ENUM('SECTION', 'CATEGORY');
CREATE TABLE catalog_item (
    ci_id serial PRIMARY KEY,
    ci_name VARCHAR(63) NOT NULL,
    ci_path VARCHAR(64) NOT NULL UNIQUE,
    ci_type t_catalog_item_type NOT NULL,
    ci_active BOOLEAN NOT NULL,

    CONSTRAINT const_catalog_item_valid CHECK (
        ci_name <> ''
    )
);

-- 
-- Товарная категория
--
CREATE TABLE category (
    -- ИД Категории совпадает с ИД соответствующего элемента в catalog_item.
    -- Если в таблице catalog_item есть запись с типом 'CATEGORY', то
    -- в таблице category должен ОБЯЗАТЕЛЬНО быть соотвтетсвующий элемент.
    cat_id INTEGER PRIMARY KEY REFERENCES catalog_item,

    -- ссылка на единицу измерения категории
    cat_unit_id INTEGER NOT NULL REFERENCES unit,

    -- Дескриптор набора параметров
    cat_psd_id INTEGER REFERENCES param_set_descriptor UNIQUE
);

---
--- Торговая марка
---
CREATE TABLE brand (
    br_id serial PRIMARY KEY,
    br_name VARCHAR(63) NOT NULL,
    br_keywords VARCHAR(255),
    br_site VARCHAR(255),
    br_logo VARCHAR(63),
    CONSTRAINT const_brand_valid CHECK (
        TRIM(br_name) <> ''
    )
);

---
--- Модель товарного предложения
---
CREATE TABLE model (
    mod_id serial PRIMARY KEY,
    mod_category_id INTEGER REFERENCES category NOT NULL,
    mod_param_set_id INTEGER NOT NULL,
    mod_brand_id INTEGER REFERENCES brand NOT NULL,

    mod_name VARCHAR(63) NOT NULL,
    mod_vendor_code VARCHAR(63) NOT NULL,
    mod_description TEXT,
    mod_image VARCHAR(63),

    UNIQUE(mod_category_id, mod_param_set_id),
    CONSTRAINT const_model_valid CHECK (
        TRIM(mod_name) <> '' AND
        TRIM(mod_vendor_code) <> ''
    )
);

--
-- Товарное предложение
--
CREATE TYPE t_offer_status AS ENUM(
    'APPROVED',
    'PENDING',
    'REJECTED'
);
CREATE TABLE offer (
    off_id serial PRIMARY KEY,
    off_category_id INTEGER REFERENCES category NOT NULL,
    off_company_id INTEGER REFERENCES company NOT NULL,
    off_name VARCHAR(255) NOT NULL,
    off_description TEXT NOT NULL,

    -- Торговая марка товарного предложения
    -- Вводится поставщиком в свободной форме
    off_brand_name VARCHAR(63),

    -- Модель (код производителя)
    -- Вводится поставщиком в свободной форме
    off_model_name VARCHAR(63),

    -- Страна производства
    off_origin_country CHAR(2) REFERENCES country,

    -- Цена за единицу товара
    -- [0.01, 9 999 999]
    off_price DECIMAL(9,2) NOT NULL,

    -- Валюта цены
    -- ISO 4217
    -- http://www.iso.org/iso/support/faqs/faqs_widely_used_standards/widely_used_standards_other/currency_codes/currency_codes_list-1.htm
    off_currency CHAR(3) NOT NULL,

    -- Норма упаковки.
    -- Определяет сколько единиц измерения категории содержит единица товара.
    -- [0.0001, 99 999]
    off_ratio DECIMAL(9,4) NOT NULL,

    -- Приведенная цена за единицу измерения в рублях.
    -- TODO: Подумать: Для увеличения производительности можно изменить тип на FLOAT
    -- Определяется как отношение off_price/off_ratio пересчитанное в рубли по курсу Поставщика.
    -- [0.01/99 999 ~ 0.0001, 9 999 999 / 0.0001 = 99 999 990 000]
    off_unit_price DECIMAL(15,4) NOT NULL DEFAULT 0,

    off_actual_date TIMESTAMP NOT NULL,
    off_active BOOLEAN NOT NULL,
    off_available BOOLEAN NOT NULL,
    off_delivery BOOLEAN NOT NULL,

    -- Дата добавления
    off_create_date TIMESTAMP NOT NULL,

    -- Дата последнего редактирования
    off_edit_date TIMESTAMP NOT NULL,

    off_image VARCHAR(63),

    -- Ссылка на справочник торговых марок
    -- Для предложений с моделью off_brand_id == mod_brand_id.
    -- Для простых предложений и предложений с параметрами, торговой маркой
    -- является запись из справочника с ИД off_brand_id или текст off_brand_name.
    off_brand_id INTEGER REFERENCES brand,

    -- Набор параметров товарного предложения
    off_param_set_id INTEGER,

    -- Модель товарного предложения
    off_model_id INTEGER REFERENCES model,

    -- Модерация
    -- Статус предложения
    off_status t_offer_status NOT NULL DEFAULT 'APPROVED',

    -- Модератор, NULL если модератор не назначен
    off_moderator_id INTEGER REFERENCES account,

    -- Дата начала модерации (точность 1 мс)
    off_moderation_start_date TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Дата завершения модерации (точность 1 мс)
    off_moderation_end_date TIMESTAMP(3),

    -- Причины отказа
    off_rejection_mask INTEGER,

    UNIQUE(off_category_id, off_param_set_id), -- NULL is not unique value, check if will be porting to other RDBMS
    CONSTRAINT const_offer_valid CHECK (
        TRIM(off_name) <> '' AND
        TRIM(off_description) <> '' AND
        off_price > 0
    ),
    CONSTRAINT const_offer_valid_model_param CHECK (
        -- Если указана модель, дополнительные параметры должны равняться NULL
        off_model_id IS NULL OR off_param_set_id IS NULL
    ),
    CONSTRAINT const_offer_valid_rejection_status CHECK (
        -- Если статус REJECTED, должна быть установлена маска причин отказа
        off_status <> 'REJECTED' OR
        (off_rejection_mask IS NOT NULL AND off_rejection_mask <> 0)
    ),
    CONSTRAINT const_offer_valid_moderation_date CHECK (
        off_moderation_end_date IS NULL OR (off_moderation_start_date <= off_moderation_end_date)
    )
);

---
--- Термин
---
CREATE TABLE term (
    term_id serial PRIMARY KEY,
    term_name VARCHAR(127) NOT NULL,
    term_description TEXT NOT NULL,
    term_source VARCHAR(255),
    CONSTRAINT const_term_valid CHECK (
        TRIM(term_name) <> '' AND
        TRIM(term_description) <> ''
    )
);

---
--- Дескриптор сессии пользователя
---
CREATE TABLE session_descriptor (
    sd_id serial PRIMARY KEY,
    sd_session_id VARCHAR(32) UNIQUE,
    -- Дата последнего обращения
    sd_touch_date TIMESTAMP
);

---
--- Настройки пользователя
---
CREATE TYPE t_price_type AS ENUM('DEFAULT', 'EXTRA_CURRENCY');
CREATE TABLE settings (
    set_id INTEGER PRIMARY KEY REFERENCES session_descriptor ON DELETE CASCADE,
    set_region_id INTEGER REFERENCES region,
    set_region_aware BOOLEAN NOT NULL,
    set_price_type t_price_type NOT NULL,
    set_extra_currency CHAR(3) NOT NULL,
    set_page_size INTEGER NOT NULL,
    CONSTRAINT const_settings_valid CHECK (
        (set_region_id IS NOT NULL OR set_region_aware = FALSE) AND
        TRIM(set_extra_currency) <> '' AND
        set_page_size > 0
    )
);

---
--- Элемент списка сравнения
---
CREATE TYPE t_match_type AS ENUM('OFFER', 'MODEL');
CREATE TABLE comparison_list_item (
    cli_id serial PRIMARY KEY,
    cli_session_id INTEGER REFERENCES session_descriptor ON DELETE CASCADE NOT NULL,
    cli_category_id INTEGER REFERENCES category NOT NULL,
    cli_match_type t_match_type NOT NULL,
    cli_match_id INTEGER NOT NULL,
    UNIQUE(cli_session_id, cli_category_id, cli_match_type, cli_match_id)
);

---
--- Список покупок
---
CREATE TABLE cart (
    cart_id serial PRIMARY KEY,
    cart_session_id INTEGER REFERENCES session_descriptor ON DELETE CASCADE NOT NULL,
    cart_name VARCHAR(63) NOT NULL,
    cart_description VARCHAR(255),
    CONSTRAINT const_cart_valid CHECK (
        TRIM(cart_name) <> ''
    )
);

---
--- Элемент списка покупок
---
CREATE TABLE cart_item (
    cit_cart_id INTEGER REFERENCES cart ON DELETE CASCADE NOT NULL,
    cit_date_added TIMESTAMP NOT NULL,
    cit_match_type t_match_type NOT NULL,
    cit_match_id INTEGER NOT NULL,
    PRIMARY KEY(cit_cart_id, cit_match_type, cit_match_id)
);

---
--- Версия схемы
---
CREATE TABLE schema_version (
    sv_version VARCHAR(16) PRIMARY KEY,
    sv_applied TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

/*
 *
 * Функции
 *
 */
CREATE FUNCTION param_set_descriptor_insert() RETURNS trigger AS $$
    BEGIN
        RAISE NOTICE 'Creating table % ', NEW.psd_table_name;
        EXECUTE 'CREATE TABLE ' || NEW.psd_table_name ||
        '(
                psx_id serial PRIMARY KEY
        );';
        RETURN NEW;
    END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER param_set_descriptor_insert AFTER INSERT ON param_set_descriptor
    FOR EACH ROW EXECUTE PROCEDURE param_set_descriptor_insert();
    
    
CREATE FUNCTION param_set_descriptor_delete() RETURNS trigger AS $$
    BEGIN
        RAISE NOTICE 'Deleteing params for table % ', OLD.psd_table_name;
        EXECUTE 'DELETE FROM param WHERE par_psd_id = ' || OLD.psd_id;
        RAISE NOTICE 'Dropping table % ', OLD.psd_table_name;
        EXECUTE 'DROP TABLE ' || OLD.psd_table_name || ';';
        RETURN OLD;
    END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER param_set_descriptor_delete BEFORE DELETE ON param_set_descriptor
    FOR EACH ROW EXECUTE PROCEDURE param_set_descriptor_delete();
    
    
CREATE FUNCTION param_insert() RETURNS trigger AS $$
    DECLARE
        table_name VARCHAR(63);
        column_data_type VARCHAR(63);
    BEGIN
        table_name := (SELECT psd_table_name FROM param_set_descriptor WHERE psd_id = NEW.par_psd_id);
        IF (NEW.par_type = 'BOOLEAN') THEN -- BOOLEAN
                column_data_type := 'BOOLEAN';
        ELSIF (NEW.par_type = 'NUMBER') THEN -- DECIMAL(12,4)
                column_data_type := 'DECIMAL(12,4)';
        ELSIF (NEW.par_type = 'SELECT') THEN -- INTEGER (select option id)
                column_data_type := 'INTEGER';
        ELSE
                RAISE EXCEPTION 'Unknown parameter type % ', NEW.par_type;
        END IF;

        RAISE NOTICE 'Adding column % to table % ', NEW.par_column_name, table_name;
        EXECUTE 'ALTER TABLE ' || table_name || ' ADD COLUMN ' ||
        NEW.par_column_name || ' ' || column_data_type || ';';
        RETURN NEW;
    END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER param_insert AFTER INSERT ON param
    FOR EACH ROW EXECUTE PROCEDURE param_insert();


CREATE FUNCTION param_delete() RETURNS trigger AS $$
    DECLARE
        table_name VARCHAR(63);
    BEGIN
        table_name := (SELECT psd_table_name FROM param_set_descriptor WHERE psd_id = OLD.par_psd_id);
        RAISE NOTICE 'Removing column % from table % ', OLD.par_column_name, table_name;
        EXECUTE 'ALTER TABLE ' || table_name || ' DROP COLUMN ' ||
        OLD.par_column_name || ';';
        RETURN OLD;
    END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER param_delete AFTER DELETE ON param
    FOR EACH ROW EXECUTE PROCEDURE param_delete();
    

CREATE FUNCTION param_select_option_delete() RETURNS trigger AS $$
    DECLARE
        param_row param%ROWTYPE;
        table_name VARCHAR(63);
    BEGIN
        SELECT * INTO param_row FROM param WHERE par_id = OLD.pso_param_id;
        table_name := (SELECT psd_table_name FROM param_set_descriptor WHERE psd_id = param_row.par_psd_id);
        IF table_name IS NOT NULL THEN
            RAISE NOTICE 'Nullifying column % from table % with values %', param_row.par_column_name, table_name, OLD.pso_id;
            EXECUTE 'UPDATE ' || table_name || ' SET ' ||
            param_row.par_column_name || ' = NULL ' ||
            'WHERE ' || param_row.par_column_name || ' = ' || OLD.pso_id;
        ELSE
            RAISE NOTICE 'Table name is null, probably because of param cascade deletion';
        END IF;
        RETURN OLD;
    END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER param_select_option_delete AFTER DELETE ON param_select_option
    FOR EACH ROW EXECUTE PROCEDURE param_select_option_delete();

/*
 *
 * Данные
 *
 */
/* 
    Аккаунт администратора
    Пароль: 'admin'
*/
INSERT INTO account(acc_id, acc_email, acc_password, acc_type)
VALUES (0, 'admin@localhost', '3906d7b83b9465acf661e58f9d25a3528814d9fff097826c9000813d00a87a31', 'ADMIN');

/* Курсы валют (по состоянию на 29.09.2009) */
INSERT INTO currency_rate (cr_from_currency, cr_from_rate, cr_to_currency, cr_to_rate) VALUES
('RUB', 30.0922, 'USD', 1),
('RUB', 44.0068, 'EUR', 1),
('USD', 1, 'RUB', 30.0922),
('EUR', 1, 'RUB', 44.0068)
;

/* Версия схемы */
INSERT INTO schema_version VALUES('01.00');