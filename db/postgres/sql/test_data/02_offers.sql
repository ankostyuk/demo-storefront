INSERT INTO offer(
off_category_id,
off_company_id,
off_name,
off_description,
off_origin_country,
off_price,
off_currency,
off_ratio,
off_unit_price,
off_actual_date,
off_active,
off_available,
off_delivery,
off_create_date,
off_edit_date
)
VALUES
(
(SELECT ci_id FROM storefront.catalog_item WHERE ci_name = 'Пилы'),
(SELECT com_id FROM storefront.company WHERE com_name = 'company1'),
'Makita 6260 DWPE 9,6V NiCd',
'Особенности:
Лучшая аккумуляторная дрель для сверления отверстий маленького и среднего размера и завинчивания маленьких и средних шурупов. 
Изящный узкий корпус инструмента с Т-образной рукояткой. 
Большой и удобный переключатель скоростей. 
Компактный, но мощный мотор обеспечивает лучший крутящий момент среди инструментов этого класса 
2 аккумулятора
Кейс
Технические характеристики:
Частота холостого хода 
1. скорость 0-350 мин-1
2. скорость 0-1.200 мин-1
Диаметр сверления 
В стали 10 мм
В дереве 21 мм
Размер патрона 
10 мм 
Максимальный крутящий момент 
24 Нм
Емкость аккумулятора
1,3 Ah 
Аккумулятор
9.6 V
Вес
1,5 кг
Комплектация:
Чемодан, Аккумулятор -2шт, Зарядное устройство',
NULL,
3960,
'RUB',
1,
3960,
(SELECT current_timestamp + interval '1 month'),
TRUE,
TRUE,
true,
(SELECT current_timestamp), (SELECT current_timestamp)
)
,
(
(SELECT ci_id FROM storefront.catalog_item WHERE ci_name = 'Пилы'),
(SELECT com_id FROM storefront.company WHERE com_name = 'company3'),
'Калибр ЭПЦ - 1800/35',
'Электрическая пила цепная ЭПЦ - 1800/35.  Потребляемая мощность - 1800 Вт;  Длина шины - 350 мм;  Скорость цепи - 9 м/с;  Количество звеньев цепи - 54;  Масляный бак - 200 мл.      Мощный электродвигатель высокой производительности;    Механизм остановки цепи при отдаче;    Автоматическая смазка цепи;    Защита от случайного включения;    Маслоуказатель;    Эргономичная рукоятка для надежного захвата пилы под любым углом.',
NULL,
3250,
'RUB',
1,
3250,
(SELECT current_timestamp + interval '1 month'),
TRUE,
TRUE,
true,
(SELECT current_timestamp), (SELECT current_timestamp)
)
,
(
(SELECT ci_id FROM storefront.catalog_item WHERE ci_name = 'Пилы'),
(SELECT com_id FROM storefront.company WHERE com_name = 'company3'),
'BOSCH GKE 35 BCЕ Professional',
'Небольшой вес в сочетании с удобным располжением рукояток обеспечивает удобство в работе. Электроника контроля постоянства обеспечивает большую мощность на всем диапазоне нагрузок и постоянную скорость протяжки цепи – 12 м/с. Старт без рывков благодаря функции мягкого пуска. Система Bosch-SDS для удобной смены цепи и ее натяжения.
Технические характеристики:
длина меча (см): 35мощность (Вт): 2100скорость протяжки (м/с): 12вес (кг): 4,6шаг цепи: 3/8”длина кабеля (м): 2,5
Особенности:
защита от отдачи посредством предохранительного тормоза с временем торможения 0,1 сек
автоматическая смазка цепи
регулирование количества масла
отсек для ключей на корпусе
защита от перегрузки благодаря встроенному ограничителю потребляемого тока',
NULL,
8200,
'RUB',
1,
8200,
(SELECT current_timestamp + interval '1 month'),
TRUE,
TRUE,
true,
(SELECT current_timestamp), (SELECT current_timestamp)
)
,
(
(SELECT ci_id FROM storefront.catalog_item WHERE ci_name = 'Пилы'),
(SELECT com_id FROM storefront.company WHERE com_name = 'company3'),
'BOSCH GKE 40 BCЕ Professional',
'Небольшой вес в сочетании с удобным располжением рукояток обеспечивает удобство в работе. Электроника контроля постоянства обеспечивает большую мощность на всем диапазоне нагрузок и постоянную скорость протяжки цепи – 12 м/с. Старт без рывков благодаря функции мягкого пуска. Система Bosch-SDS для удобной смены цепи и ее натяжения.
Технические характеристики:
ддлина меча (см): 40мощность (Вт): 2100скорость протяжки (м/с): 12вес (кг): 4,8шаг цепи: 3/8”длина кабеля (м): 2,5
Особенности:
защита от отдачи посредством предохранительного тормоза с временем торможения 0,1 сек
автоматическая смазка цепи
регулирование количества масла
отсек для ключей на корпусе
защита от перегрузки благодаря встроенному ограничителю потребляемого тока',
NULL,
8400,
'RUB',
1,
8400,
(SELECT current_timestamp + interval '1 month'),
TRUE,
TRUE,
true,
(SELECT current_timestamp), (SELECT current_timestamp)
)
,
(
(SELECT ci_id FROM storefront.catalog_item WHERE ci_name = 'Пилы'),
(SELECT com_id FROM storefront.company WHERE com_name = 'company3'),
'Makita DCS 33-35',
'Особенности
Мощная, компактная бензопила для сельского и лесного хозяйства. 
Идеальна для резки деревьев и ветвей 10-20 см. 
4-х канальный цилиндр. 
Вибропогашение. 
Тормоз цепи. 
Внимание, 2-х тактных двигатель! Специальное масло для двухтактного двигателя смешать с бензином АИ-92, смесь залить в бензобак.
Без кейса
Технические характеристики:
Двигатель
33 cm3/1,4KW
Топливный бак 
0,4 Л 
Резервуар для масла цепи 
0,25 Л 
Длина шины 
35 cm
Вес
3,9 кг',
NULL,
7400,
'RUB',
1,
7400,
(SELECT current_timestamp + interval '1 month'),
TRUE,
TRUE,
true,
(SELECT current_timestamp), (SELECT current_timestamp)
)
,
(
(SELECT ci_id FROM storefront.catalog_item WHERE ci_name = 'Пилы'),
(SELECT com_id FROM storefront.company WHERE com_name = 'company3'),
'Makita DCS 34-35',
'Особенности
Обрезка столбов ограждений, распиливание и обрезка сучев. 
Инерционный тормоз цепи. 
Электронное зажигание. 
Автоматически регулируемая система смазки цепи. 
Конструкция инструмента обеспечивает защиту от вибрации. 
Быстрый старт (пусковой топливный насос) 
Внимание, 2-х тактных двигатель! Специальное масло для двухтактного двигателя смешать с бензином АИ-92, смесь залить в бензобак.
Без кейса
Технические характеристики:
Двигатель
33 cm3 / 1,3 KW
Топливный бак 
0,37 Л 
Резервуар для масла цепи 
0,25 Л 
Длина шины 
35 cm
Вес
4,7 кг',
NULL,
6600,
'RUB',
1,
6600,
(SELECT current_timestamp + interval '1 month'),
TRUE,
TRUE,
true,
(SELECT current_timestamp), (SELECT current_timestamp)
)
,
(
(SELECT ci_id FROM storefront.catalog_item WHERE ci_name = 'Пилы'),
(SELECT com_id FROM storefront.company WHERE com_name = 'company3'),
'Makita DCS 34-40',
'Особенности 
Обрезка столбов ограждений, распиливание и обрезка сучев. 
Инерционный тормоз цепи. 
Электронное зажигание. 
Автоматически регулируемая система смазки цепи. 
Конструкция инструмента обеспечивает защиту от вибрации. 
Быстрый старт (пусковой топливный насос) 
Внимание, 2-х тактных двигатель! Специальное масло для двухтактного двигателя смешать с бензином АИ-92, смесь залить в бензобак.
Без кейса
Технические характеристики:
Двигатель
33 cm3 / 1,3 KW
Топливный бак 
0,37 Л 
Резервуар для масла цепи 
0,25 Л 
Длина шины 
40 cm
Вес
4,7 кг',
NULL,
6990,
'RUB',
1,
6990,
(SELECT current_timestamp + interval '1 month'),
TRUE,
TRUE,
true,
(SELECT current_timestamp), (SELECT current_timestamp)
)
,
(
(SELECT ci_id FROM storefront.catalog_item WHERE ci_name = 'Пилы'),
(SELECT com_id FROM storefront.company WHERE com_name = 'company3'),
'Makita DCS 400-40',
'Особенности
Для небольших и общих операций по лесозаготовке, обрезки сучьев, ухода за деревьями, подъемных и строительных работ. 
Инерционный тормоз цепи. 
Электронное зажигание. 
Автоматически регулируемая система смазки цепи. 
Защита от вибрации. 
Внимание, 2-х тактных двигатель! Специальное масло для двухтактного двигателя смешать с бензином АИ-92, смесь залить в бензобак.
Без кейса
Технические характеристики:
Двигатель
39 cm3 / 1,7 KW
Топливный бак 
0,4 Л 
Резервуар для масла цепи 
0,25 Л 
Длина шины 
40 cm
Вес
4 кг',
NULL,
7500,
'RUB',
1,
7500,
(SELECT current_timestamp + interval '1 month'),
TRUE,
TRUE,
true,
(SELECT current_timestamp), (SELECT current_timestamp)
)
,
(
(SELECT ci_id FROM storefront.catalog_item WHERE ci_name = 'Пилы'),
(SELECT com_id FROM storefront.company WHERE com_name = 'company3'),
'Makita DCS 401-40',
'Особенности:
Для небольших и общих операций по лесозаготовке, обрезки сучьев, уходу за деревьями, подьемных и строительных работ.  
Инерционный тормоз цепи. 
Электронное зажигание. 
Автоматически регулируемая система смазки цепи. 
Защита от вибрации. 
Декомпрессионный клапан для облегчения пуска
Внимание, 2-х тактных двигатель! Специальное масло для двухтактного двигателя смешать с бензином АИ-92, смесь залить в бензобак.
Быстрый запуск. 
Двигатель
39 cm3 / 1,7 KW
Резервуар для масла цепи
0,25 Л
Топливный бак 
0,4 Л 
Длина шины 
40 cm
Вес
4 кг',
NULL,
8200,
'RUB',
1,
8200,
(SELECT current_timestamp + interval '1 month'),
TRUE,
TRUE,
true,
(SELECT current_timestamp), (SELECT current_timestamp)
)
,
(
(SELECT ci_id FROM storefront.catalog_item WHERE ci_name = 'Пилы'),
(SELECT com_id FROM storefront.company WHERE com_name = 'company3'),
'Makita DCS 430-45',
'Особенности:
Компактная и прочная, универсальная пила для лесозаготовок, распилка бревен, обрезки сучев и расчистки кустарника. 
Инерционный тормоз цепи. 
Электронное зажигание. 
Автоматически регулируемая система смазки цепи. 
Конструкция инструмента обеспечивает защиту от вибрации. 
Внимание, 2-х тактных двигатель! Специальное масло для двухтактного двигателя смешать с бензином АИ-92, смесь залить в бензобак.
Без кейса
Технические характеристики:
Двигатель
43 cm3 / 2,0 KW
Топливный бак 
0,56 Л 
Резервуар для масла цепи
0,28 Л
Длина шины 
45 cm
Вес
4,6 кг',
NULL,
10250,
'RUB',
1,
10250,
(SELECT current_timestamp + interval '1 month'),
TRUE,
TRUE,
true,
(SELECT current_timestamp), (SELECT current_timestamp)
)
,
(
(SELECT ci_id FROM storefront.catalog_item WHERE ci_name = 'Пилы'),
(SELECT com_id FROM storefront.company WHERE com_name = 'company3'),
'Makita DCS 4600 S-45',
'Особенности:
Эргономичный, до мелочей продуманный дизайн корпуса 
Двигатель с высокой производительностью 
Легкое и удобное обслуживание, и низкие эксплуатационные расходы 
Предварительная камера для лучшей очистки воздуха 
Чрезвычайно низкая вибрация для продолжительной работы без усталости 
Комбинированный выключатель с автоматическим полу-газом
Внимание, 2-х тактных двигатель! Специальное масло для двухтактного двигателя смешать с бензином АИ-92, смесь залить в бензобак.
Без кейса
Технические характеристики:
Двигатель
45,6 cm3 / 2,6 KW
Топливный бак 
0,5 Л 
Резервуар для масла цепи
0,27 Л
Длина шины 
45 cm
Вес
4 кг',
NULL,
13900,
'RUB',
1,
13900,
(SELECT current_timestamp + interval '1 month'),
TRUE,
TRUE,
true,
(SELECT current_timestamp), (SELECT current_timestamp)
)
,
(
(SELECT ci_id FROM storefront.catalog_item WHERE ci_name = 'Пилы'),
(SELECT com_id FROM storefront.company WHERE com_name = 'company2'),
'Makita DCS 5000-53',
'Особенности:
Эргономичный, до мелочей продуманный дизайн корпуса 
Двигатель с высокой производительностью 
Легкое и удобное обслуживание, и низкие эксплуатационные расходы 
Предварительная камера для лучшей очистки воздуха 
Чрезвычайно низкая вибрация для продолжительной работы без усталости 
Комбинированный выключатель с автоматическим полу-газом
Внимание, 2-х тактных двигатель! Специальное масло для двухтактного двигателя смешать с бензином АИ-92, смесь залить в бензобак.
Без кейса
Технические характеристики:
Двигатель
52 cm3 / 2,8 KW
Топливный бак 
0,5 Л 
Резервуар для масла цепи
0,28 Л
Длина шины 
53 cm
Шаг цепи
3/8"
Ширина паза шины
0,058" 
Вес
4,9 кг',
NULL,
14500,
'RUB',
1,
14500,
(SELECT current_timestamp + interval '1 month'),
TRUE,
TRUE,
true,
(SELECT current_timestamp), (SELECT current_timestamp)
)
,
(
(SELECT ci_id FROM storefront.catalog_item WHERE ci_name = 'Пилы'),
(SELECT com_id FROM storefront.company WHERE com_name = 'company2'),
'Makita DCS 5001-53',
'Особенности:
Эргономичный, до мелочей продуманный дизайн корпуса 
Двигатель с высокой производительностью 
Легкое и удобное обслуживание, и низкие эксплуатационные расходы 
Предварительная камера для лучшей очистки воздуха 
Чрезвычайно низкая вибрация для продолжительной работы без усталости 
Комбинированный выключатель с автоматическим полу-газом
Декомпрессионный клапан для облегчения пуска
Внимание, 2-х тактных двигатель! Специальное масло для двухтактного двигателя смешать с бензином АИ-92, смесь залить в бензобак.
Без кейса
Технические характеристики:
Двигатель
52 cm3 / 2,8 KW
Топливный бак 
0,5 Л 
Резервуар для масла цепи
0,28 Л
Длина шины 
53 cm
Шаг цепи
3/8"
Ширина паза шины
0,058" 
Вес
4,9 кг',
NULL,
14900,
'RUB',
1,
14900,
(SELECT current_timestamp + interval '1 month'),
TRUE,
TRUE,
true,
(SELECT current_timestamp), (SELECT current_timestamp)
)
,
(
(SELECT ci_id FROM storefront.catalog_item WHERE ci_name = 'Пилы'),
(SELECT com_id FROM storefront.company WHERE com_name = 'company2'),
'Makita DCS 520-45',
'Особенности:
Компактная и прочная, универсальная пила для лесозаготовок, распилка бревен, обрезки сучьев и расчистки кустарника. 
Инерционный тормоз цепи. 
Электронное зажигание. 
Автоматически регулируемая система смазки цепи. 
Конструкция инструмента обеспечивает защиту от вибрации.
Внимание, 2-х тактных двигатель! Специальное масло для двухтактного двигателя смешать с бензином АИ-92, смесь залить в бензобак.
Без кейса
Технические характеристики:
Двигатель
52 cm3 / 2,4 KW
Топливный бак 
0,56 Л 
Резервуар для масла цепи
0,28 Л
Длина шины 
45 cm
Вес
4,6 кг',
NULL,
12400,
'RUB',
1,
12400,
(SELECT current_timestamp + interval '1 month'),
TRUE,
TRUE,
true,
(SELECT current_timestamp), (SELECT current_timestamp)
)
,
(
(SELECT ci_id FROM storefront.catalog_item WHERE ci_name = 'Пилы'),
(SELECT com_id FROM storefront.company WHERE com_name = 'company2'),
'Makita DCS 5200i-45',
'Особенности:
Сверхнадежный мотор для максимальной производительности при особо тяжелых режимах работы. 
Литой цилиндр с охлаждением. 
Карбюратор с впрыском. 
Двойная система очистки воздуха. 
Инерционный тормоз цепи. 
Электронное зажигание. 
Внимание, 2-х тактных двигатель! Специальное масло для двухтактного двигателя смешать с бензином АИ-92, смесь залить в бензобак.
Без кейса
Технические характеристики:
Двигатель
52 cm3 / 2,7 KW
Топливный бак 
0,56 Л 
Резервуар для масла цепи
0,28 Л
Длина шины 
45 cm
Вес
4,6 кг',
NULL,
13700,
'RUB',
1,
13700,
(SELECT current_timestamp + interval '1 month'),
TRUE,
TRUE,
true,
(SELECT current_timestamp), (SELECT current_timestamp)
)
,
(
(SELECT ci_id FROM storefront.catalog_item WHERE ci_name = 'Пилы'),
(SELECT com_id FROM storefront.company WHERE com_name = 'company2'),
'Makita DCS 6400-45',
'Особенности:
Профессиональная бензопила с эргономичным дизайном корпуса. 
Улучшеная фильтрация воздуха при помощи системы Airmaster. 
Упрощенный пуск благодаря полуавтоматическому арретиру полугаза. 
Жесткие пружины для вибропагошения. 
Внимание, 2-х тактных двигатель! Специальное масло для двухтактного двигателя смешать с бензином АИ-92, смесь залить в бензобак.
Без кейса
Технические характеристики:
Двигатель
64 cm3 / 3.5 KW
Топливный бак 
0,75 Л 
Резервуар для масла цепи
0,42 Л
Длина шины 
45 cm
Вес
6.3 кг',
NULL,
14990,
'RUB',
1,
14990,
(SELECT current_timestamp + interval '1 month'),
TRUE,
TRUE,
true,
(SELECT current_timestamp), (SELECT current_timestamp)
)
,
(
(SELECT ci_id FROM storefront.catalog_item WHERE ci_name = 'Пилы'),
(SELECT com_id FROM storefront.company WHERE com_name = 'company2'),
'Makita DCS 6401-45',
'Особенности:
Профессиональная бензопила с эргономичным дизайном корпуса. 
Улучшеная фильтрация воздуха при помощи системы Airmaster. 
Упрощенный пуск благодаря полуавтоматическому арретиру полугаза. 
Жесткие пружины для вибропагошения. 
Декомпрессионный клапан для облегчения пуска
Внимание, 2-х тактных двигатель! Специальное масло для двухтактного двигателя смешать с бензином АИ-92, смесь залить в бензобак.
Без кейса
Технические характеристики:
Двигатель
64 cm3 / 3.5 KW
Топливный бак 
0,75 Л 
Резервуар для масла цепи
0,42 Л
Длина шины 
45 cm
Вес
6.3 кг',
NULL,
13900,
'RUB',
1,
13900,
(SELECT current_timestamp + interval '1 month'),
TRUE,
TRUE,
true,
(SELECT current_timestamp), (SELECT current_timestamp)
)
,
(
(SELECT ci_id FROM storefront.catalog_item WHERE ci_name = 'Пилы'),
(SELECT com_id FROM storefront.company WHERE com_name = 'company2'),
'Makita DCS 7300-45',
'Особенности 
Профессиональная бензопила с эргономичным дизайном корпуса. 
Улучшенная фильтрация воздуха при помощи системы Airmaster. 
Упрощенный пуск благодаря полуавтоматическому арретиру полугаза. 
Жесткие пружины для вибропогашения. 
Внимание, 2-х тактных двигатель! Специальное масло для двухтактного двигателя смешать с бензином АИ-92, смесь залить в бензобак.
Без кейса
Технические характеристики
Двигатель
73 cm3 / 4,2 KW
Резервуар для масла цепи
0,42 Л
Топливный бак 
0,75 Л 
Длина шины 
45 cm
Вес
6,3 кг',
NULL,
16700,
'RUB',
1,
16700,
(SELECT current_timestamp + interval '1 month'),
TRUE,
TRUE,
true,
(SELECT current_timestamp), (SELECT current_timestamp)
)
,
(
(SELECT ci_id FROM storefront.catalog_item WHERE ci_name = 'Пилы'),
(SELECT com_id FROM storefront.company WHERE com_name = 'company2'),
'Makita DCS 7300-50',
'Особенности 
Профессиональная бензопила с эргономичным дизайном корпуса. 
Улучшенная фильтрация воздуха при помощи системы Airmaster. 
Упрощенный пуск благодаря полуавтоматическому арретиру полугаза. 
Жесткие пружины для вибропогашения. 
Внимание, 2-х тактных двигатель! Специальное масло для двухтактного двигателя смешать с бензином АИ-92, смесь залить в бензобак.
Без кейса
Технические характеристики
Двигатель
73 cm3 / 4,2 KW
Резервуар для масла цепи
0,42 Л
Топливный бак 
0,75 Л 
Длина шины 
50 cm
Вес
6,3 кг',
NULL,
17800,
'RUB',
1,
17800,
(SELECT current_timestamp + interval '1 month'),
TRUE,
TRUE,
true,
(SELECT current_timestamp), (SELECT current_timestamp)
)
,
(
(SELECT ci_id FROM storefront.catalog_item WHERE ci_name = 'Пилы'),
(SELECT com_id FROM storefront.company WHERE com_name = 'company2'),
'Makita DCS 7301-60',
'Особенности 
Профессиональная бензопила с эргономичным дизайном корпуса. 
Улучшенная фильтрация воздуха при помощи системы Airmaster. 
Упрощенный пуск благодаря полуавтоматическому арретиру полугаза. 
Жесткие пружины для вибропогашения. 
Внимание, 2-х тактных двигатель! Специальное масло для двухтактного двигателя смешать с бензином АИ-92, смесь залить в бензобак.
Без кейса
Технические характеристики
Двигатель
73 cm3 / 4,2 KW
Резервуар для масла цепи
0,42 Л
Топливный бак 
0,75 Л 
Длина шины 
60 cm
Вес
6,3 кг',
NULL,
18500,
'RUB',
1,
18500,
(SELECT current_timestamp + interval '1 month'),
TRUE,
TRUE,
true,
(SELECT current_timestamp), (SELECT current_timestamp)
)
,
(
(SELECT ci_id FROM storefront.catalog_item WHERE ci_name = 'Пилы'),
(SELECT com_id FROM storefront.company WHERE com_name = 'company2'),
'Makita DCS 7900-45',
'Особенности 
Профессиональная бензопила с эргономичным дизайном корпуса. 
Лучшее соотношение мощности и веса (1 л.с. на каждый кг веса) 
Улучшенная фильтрация воздуха при помощи системы Airmaster. 
Упрощенный пуск благодаря полуавтоматическому арретиру полугаза. 
Жесткие пружины для вибропогашения
Внимание, 2-х тактных двигатель! Специальное масло для двухтактного двигателя смешать с бензином АИ-92, смесь залить в бензобак.
Без кейса
Технические характеристики
Двигатель
79 cm3 / 4,6 KW
Резервуар для масла цепи
0,42 Л
Топливный бак 
0,75 Л 
Длина шины 
45 cm
Вес
6,3 кг',
NULL,
16800,
'RUB',
1,
16800,
(SELECT current_timestamp + interval '1 month'),
TRUE,
TRUE,
true,
(SELECT current_timestamp), (SELECT current_timestamp)
)
,
(
(SELECT ci_id FROM storefront.catalog_item WHERE ci_name = 'Пилы'),
(SELECT com_id FROM storefront.company WHERE com_name = 'company1'),
'Makita DCS 7900-70',
'Особенности 
Профессиональная бензопила с эргономичным дизайном корпуса. 
Лучшее соотношение мощности и веса (1 л.с. на каждый кг веса) 
Улучшенная фильтрация воздуха при помощи системы Airmaster. 
Упрощенный пуск благодаря полуавтоматическому арретиру полугаза. 
Жесткие пружины для вибропогашения
Внимание, 2-х тактных двигатель! Специальное масло для двухтактного двигателя смешать с бензином АИ-92, смесь залить в бензобак.
Без кейса
Технические характеристики
Двигатель
79 cm3 / 4,6 KW
Резервуар для масла цепи
0,42 Л
Топливный бак 
0,75 Л 
Длина шины 
70 cm
Вес
6,3 кг',
NULL,
19000,
'RUB',
1,
19000,
(SELECT current_timestamp + interval '1 month'),
FALSE,
TRUE,
true,
(SELECT current_timestamp), (SELECT current_timestamp)
)
,
(
(SELECT ci_id FROM storefront.catalog_item WHERE ci_name = 'Пилы'),
(SELECT com_id FROM storefront.company WHERE com_name = 'company1'),
'Makita DCS 7901-70',
'Особенности 
Профессиональная бензопила с эргономичным дизайном корпуса. 
Лучшее соотношение мощности и веса (1 л.с. на каждый кг веса) 
Улучшенная фильтрация воздуха при помощи системы Airmaster. 
Упрощенный пуск благодаря полуавтоматическому арретиру полугаза. 
Жесткие пружины для вибропогашения
Декомпрессионный клапан для облегчения пуска
Внимание, 2-х тактных двигатель! Специальное масло для двухтактного двигателя смешать с бензином АИ-92, смесь залить в бензобак.
Без кейса
Технические характеристики
Двигатель
79 cm3 / 4,6 KW
Резервуар для масла цепи
0,42 Л
Топливный бак 
0,75 Л 
Длина шины 
70 cm
Вес
6,3 кг',
NULL,
19000,
'RUB',
1,
19000,
(SELECT current_timestamp + interval '1 month'),
TRUE,
TRUE,
true,
(SELECT current_timestamp), (SELECT current_timestamp)
)
,
(
(SELECT ci_id FROM storefront.catalog_item WHERE ci_name = 'Пилы'),
(SELECT com_id FROM storefront.company WHERE com_name = 'company1'),
'Makita DCS 9010-60',
'Особенности:
Профессиональная  пила
Электронное зажигание
Инерционный тормоз цепи
Автоматический масляный насос
Система защиты фильтра от опилок
Улучшенная фильтрация воздуха
Зубчатый упор
Внимание, 2-х тактных двигатель! Специальное масло для двухтактного двигателя смешать с бензином АИ-92, смесь залить в бензобак.
Без кейса
Технические характеристики:
Двигатель
90 cm3 / 6.6 Hp
Длина шины 
60 cm
Вес
13 кг',
NULL,
21490,
'RUB',
1,
21490,
(SELECT current_timestamp + interval '1 month'),
TRUE,
TRUE,
true,
(SELECT current_timestamp), (SELECT current_timestamp)
)
,
(
(SELECT ci_id FROM storefront.catalog_item WHERE ci_name = 'Пилы'),
(SELECT com_id FROM storefront.company WHERE com_name = 'company1'),
'Patriot Garden 2512',
'Технические характеристики:    Мощность кВт/л.с.   1,0/1,3Объём цилиндра 25,4 см3    Шина 30см.Вес 2,9 кг.',
NULL,
4000,
'RUB',
1,
4000,
(SELECT current_timestamp + interval '1 month'),
TRUE,
TRUE,
true,
(SELECT current_timestamp), (SELECT current_timestamp)
)
,
(
(SELECT ci_id FROM storefront.catalog_item WHERE ci_name = 'Пилы'),
(SELECT com_id FROM storefront.company WHERE com_name = 'company1'),
'Patriot Garden 3816',
'Мощность кВт/л.с: 1,5 кВт / 2,0 л.с.Объем двигателя см3: 38 сс.Длина шины: 16" ( 40 см).Шаг цепи: 3/8 дюйма.Количество звеньев: 57Объем бензобака: 0,31 л.Объем масленого бачка: 0,21 л.Вес: 4,85 кг.',
NULL,
3700,
'RUB',
1,
3700,
(SELECT current_timestamp + interval '1 month'),
FALSE,
TRUE,
true,
(SELECT current_timestamp), (SELECT current_timestamp)
)
,
(
(SELECT ci_id FROM storefront.catalog_item WHERE ci_name = 'Пилы'),
(SELECT com_id FROM storefront.company WHERE com_name = 'company1'),
'SPARKY TV 5545',
'Технические данные:
 
 
Рабочий объем двигателя
см3 
55 
Мощность двигателя
кВт/Лс 
2,3/3,2 
Длина шины
см 
45 
Шаг цепи
Дюйм 
3/8 
Ширина ведущего звена
мм 
1,3 
Скорость при холостом ходе
мин-1 
2600-2900 
Скорость при макс. мощности
мин-1 
12500-13500 
Вместимость топливного резервуара
см3 
500 
Вместимость резервуара для масла
см3 
250 
Вес
кг 
7,2',
NULL,
9990,
'RUB',
1,
9990,
(SELECT current_timestamp + interval '1 month'),
TRUE,
TRUE,
true,
(SELECT current_timestamp), (SELECT current_timestamp)
)
,
(
(SELECT ci_id FROM storefront.catalog_item WHERE ci_name = 'Пилы'),
(SELECT com_id FROM storefront.company WHERE com_name = 'company1'),
'SPARKY TV 4240',
'Технические данные:
 
 
Рабочий объем двигателя
см3 
42 
Мощность двигателя
кВт/Лс 
1,7/2,3 
Длина шины
см 
40 
Шаг цепи
Дюйм 
3/8 
Ширина ведущего звена
мм 
1,3 
Скорость при холостом ходе
мин-1 
3100 
Скорость при макс. мощности
мин-1 
12500 
Вместимость топливного резервуара
см3 
400 
Вместимость резервуара для масла
см3 
220 
Вес
кг 
6,3 
Поставляется
с: Цепь с низким отскоком, тип Oregon: 1шт., Ведущая шина, тип Oregon :
1шт., Крышка тормозной цепи: 1шт., Комбинированный гаечный ключ: 1шт.,
Зубчатая опора: 1pc., Масло: 1бут.',
NULL,
6700,
'RUB',
1,
6700,
(SELECT current_timestamp + interval '1 month'),
TRUE,
TRUE,
true,
(SELECT current_timestamp), (SELECT current_timestamp)
)
,
(
(SELECT ci_id FROM storefront.catalog_item WHERE ci_name = 'Пилы'),
(SELECT com_id FROM storefront.company WHERE com_name = 'company1'),
'SPARKY TV 3840',
'<ul> <li class="smalltext">Антивибрационная система
</li> <li class="smalltext">Эргономичный дизайн
</li> <li class="smalltext">Автоматическая  смазка 
цепи
</li> <li class="smalltext">Ведущая шина, тип Oregon
</li> <li class="smalltext">Цепь с низким отскоком, 
тип Oregon
</li> <li class="smalltext">Эргономичная задняя рукоятка
</li> <li class="smalltext">Тормоз цепи</li> 
</ul>',
NULL,
5700,
'RUB',
1,
5700,
(SELECT current_timestamp + interval '1 month'),
TRUE,
TRUE,
true,
(SELECT current_timestamp), (SELECT current_timestamp)
)
,
(
(SELECT ci_id FROM storefront.catalog_item WHERE ci_name = 'Пилы'),
(SELECT com_id FROM storefront.company WHERE com_name = 'company1'),
'PACKARD SPENCE PSGS 350С',
'Бензопила цепная,1600Вт, 38см/куб, шина 35см., карбюратор Weber, автовпрыск',
NULL,
4400,
'RUB',
1,
4400,
(SELECT current_timestamp + interval '1 month'),
TRUE,
TRUE,
true,
(SELECT current_timestamp), (SELECT current_timestamp)
)
,
(
(SELECT ci_id FROM storefront.catalog_item WHERE ci_name = 'Пилы'),
(SELECT com_id FROM storefront.company WHERE com_name = 'company1'),
'PACKARD SPENCE PSGS 400С',
'Бензопила цепная, 1600Вт, 38см/куб, шина 40см., карбюратор Weber, автовпрыск',
NULL,
4500,
'RUB',
1,
4500,
(SELECT current_timestamp + interval '1 month'),
TRUE,
TRUE,
true,
(SELECT current_timestamp), (SELECT current_timestamp)
)
,
(
(SELECT ci_id FROM storefront.catalog_item WHERE ci_name = 'Пилы'),
(SELECT com_id FROM storefront.company WHERE com_name = 'company1'),
'PACKARD SPENCE PSGS 450С',
'Бензопила цепная, 1600Вт, 38см/куб, шина 45см., карбюратор Weber, автовпрыск',
NULL,
4600,
'RUB',
1,
4600,
(SELECT current_timestamp + interval '1 month'),
TRUE,
TRUE,
true,
(SELECT current_timestamp), (SELECT current_timestamp)
)
,
(
(SELECT ci_id FROM storefront.catalog_item WHERE ci_name = 'Пилы'),
(SELECT com_id FROM storefront.company WHERE com_name = 'company1'),
'PACKARD SPENCE PSGS 400D',
'Бензопила цепная, 1800Вт, 42 см/куб, шина 40см., карбюратор Weber, автовпрыск',
NULL,
5500,
'RUB',
1,
5500,
(SELECT current_timestamp + interval '1 month'),
TRUE,
TRUE,
true,
(SELECT current_timestamp), (SELECT current_timestamp)
)
,
(
(SELECT ci_id FROM storefront.catalog_item WHERE ci_name = 'Пилы'),
(SELECT com_id FROM storefront.company WHERE com_name = 'company1'),
'PACKARD SPENCE PSGS 450D',
'Бензопила цепная, 1800Вт, 42 см/куб, шина 45см., карбюратор Weber, автовпрыск',
NULL,
4990,
'RUB',
1,
4990,
(SELECT current_timestamp + interval '1 month'),
TRUE,
TRUE,
true,
(SELECT current_timestamp), (SELECT current_timestamp)
)
,
(
(SELECT ci_id FROM storefront.catalog_item WHERE ci_name = 'Пилы'),
(SELECT com_id FROM storefront.company WHERE com_name = 'company1'),
'PACKARD SPENCE PSGS 400E',
'Бензопила цепная, 2100Вт, 45 см/куб, шина 40см., карбюратор Weber, автовпрыск',
NULL,
5200,
'RUB',
1,
5200,
(SELECT current_timestamp + interval '1 month'),
TRUE,
TRUE,
true,
(SELECT current_timestamp), (SELECT current_timestamp)
)
,
(
(SELECT ci_id FROM storefront.catalog_item WHERE ci_name = 'Пилы'),
(SELECT com_id FROM storefront.company WHERE com_name = 'company1'),
'PACKARD SPENCE PSGS 450E',
'Бензопила цепная, 2100Вт, 45 см/куб, шина 45см., карбюратор Weber, автовпрыск',
NULL,
5250,
'RUB',
1,
5250,
(SELECT current_timestamp + interval '1 month'),
TRUE,
TRUE,
true,
(SELECT current_timestamp), (SELECT current_timestamp)
)
,
(
(SELECT ci_id FROM storefront.catalog_item WHERE ci_name = 'Пилы'),
(SELECT com_id FROM storefront.company WHERE com_name = 'company1'),
'PACKARD SPENCE PSGS 450F',
'Бензопила цепная, 2300Вт, 50,2 см/куб, шина 45см., карбюратор Weber, автовпрыск',
NULL,
5300,
'RUB',
1,
5300,
(SELECT current_timestamp + interval '1 month'),
TRUE,
TRUE,
true,
(SELECT current_timestamp), (SELECT current_timestamp)
)
;
