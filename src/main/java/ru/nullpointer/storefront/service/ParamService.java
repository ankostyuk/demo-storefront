package ru.nullpointer.storefront.service;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Resource;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import ru.nullpointer.storefront.dao.CatalogItemDAO;
import ru.nullpointer.storefront.dao.CategoryDAO;
import ru.nullpointer.storefront.dao.OfferDAO;
import ru.nullpointer.storefront.dao.ParamDAO;
import ru.nullpointer.storefront.dao.ParamGroupDAO;
import ru.nullpointer.storefront.dao.ParamSelectOptionDAO;
import ru.nullpointer.storefront.dao.ParamSetDAO;
import ru.nullpointer.storefront.domain.CatalogItem;
import ru.nullpointer.storefront.domain.Category;
import ru.nullpointer.storefront.domain.param.Param;
import ru.nullpointer.storefront.domain.param.ParamGroup;
import ru.nullpointer.storefront.domain.param.ParamSelectOption;
import ru.nullpointer.storefront.domain.param.ParamSetDescriptor;
import ru.nullpointer.storefront.domain.support.ParamModel;
import ru.nullpointer.storefront.domain.support.ParamNameComparator;

/**
 *
 * @author ankostyuk
 * @author Alexander Yastrebov
 */
@Service
public class ParamService {

    private static final String PSD_CATEGORY_NAME_FORMAT = "psd_category_{0}";
    private static final String PSD_CATEGORY_TABLE_NAME_FORMAT = "param_set_category_{0}";
    private static final String PARAM_CATEGORY_COLUMN_NAME_FORMAT = "psx_category_{0}_param_";
    //
    private Logger logger = LoggerFactory.getLogger(ParamService.class);
    //
    @Resource
    private CatalogItemDAO catalogItemDAO;
    @Resource
    private CategoryDAO categoryDAO;
    @Resource
    private ParamSetDAO paramSetDAO;
    @Resource
    private ParamGroupDAO paramGroupDAO;
    @Resource
    private ParamDAO paramDAO;
    @Resource
    private ParamSelectOptionDAO paramSelectOptionDAO;
    @Resource
    private OfferDAO offerDAO;
    @Resource
    private ParamModelHelper paramModelHelper;

    /**
     * Формирует связь категории с параметрами. Устанавливает категории <code>parameterSetDescriptorId</code>.
     * @param category категория
     * @throws IllegalArgumentException если <code>category</code> равен <code>null</code> или обязательные атрибуты равны <code>null</code>
     */
    private void buildParamCategory(Category category) { // TODO TEST
        Assert.notNull(category);

        ParamSetDescriptor psd = new ParamSetDescriptor();
        psd.setName(MessageFormat.format(PSD_CATEGORY_NAME_FORMAT, category.getId()));
        psd.setTableName(MessageFormat.format(PSD_CATEGORY_TABLE_NAME_FORMAT, category.getId()));

        paramSetDAO.addParamSetDescriptor(psd);

        category.setParameterSetDescriptorId(psd.getId());

        categoryDAO.updateCategoryParameterSetDescriptor(category);
    }

    /**
     * Возвращает элемент соответствующий категории каталога по идентификатору группы параметров категории
     * @param paramGroupId идентификатор группы параметров категории
     * @return элемент или <code>null</code> если элемента с группой параметров с таким <code>paramGroupId</code> не существует
     * @throws IllegalArgumentException если <code>paramGroupId</code> равен <code>null</code>
     */
    @Transactional(readOnly = true)
    public CatalogItem getCategoryItemByParamGroupId(Integer paramGroupId) {
        ParamGroup paramGroup = paramGroupDAO.getParamGroupById(paramGroupId);
        Assert.notNull(paramGroup);
        Category category = categoryDAO.getCategoryByParamSetDescriptorId(paramGroup.getParameterSetDescriptorId());
        Assert.notNull(category);
        return catalogItemDAO.getCategoryItemById(category.getId());
    }

    /**
     * Возвращает группу параметров по идентификатору.
     * @return Группу параметров или <code>null</code> если группы параметров с таким идентификатором не существует
     * @throws IllegalArgumentException если <code>id</code> равен <code>null</code>
     */
    @Transactional(readOnly = true)
    public ParamGroup getParamGroupById(Integer id) {
        return paramGroupDAO.getParamGroupById(id);
    }

    /**
     * Добавляет группу параметров категории. Устанавливает группе параметров полученный <code>id</code>. Устанавливает группе параметров <code>parameterSetDescriptorId</code>. Устанавливает порядок (<code>ordinal</code> - максимальный). Если параметры категории не сформированны - формирует параметры категории.
     * @param category категория
     * @param paramGroup добавляемая группа параметров
     * @throws IllegalArgumentException если один из аргументов равен <code>null</code> или обязательные атрибуты не "валидны" 
     */
    @Secured("ROLE_MANAGER_CATALOG")
    @Transactional(readOnly = false)
    public void addParamGroup(Category category, ParamGroup paramGroup) {
        Assert.notNull(category);
        Assert.notNull(paramGroup);

        Integer psdId = category.getParameterSetDescriptorId();
        if (psdId == null) {
            buildParamCategory(category);
            // сохранить полученный id
            psdId = category.getParameterSetDescriptorId();
        }

        paramGroup.setParameterSetDescriptorId(psdId);

        paramGroupDAO.insert(paramGroup);
    }

    /**
     * Обновляет информацию о группе параметров. Не обновляет идентификатор, порядок, связь с дескриптором набора параметров.
     * @param paramGroup обновляемая группа параметров
     * @throws IllegalArgumentException если <code>paramGroup</code> равен <code>null</code> или обязательные атрибуты не "валидны" 
     */
    @Secured("ROLE_MANAGER_CATALOG")
    @Transactional(readOnly = false)
    public void updateParamGroupInfo(ParamGroup paramGroup) {
        paramGroupDAO.updateInfo(paramGroup);
    }

    /**
     * Устанавливает порядок группы параметров.
     * Переупорядочивает остальные группы параметров в категории.
     * @param paramGroup группа параметров
     * @throws IllegalArgumentException если <code>paramGroup</code> равен <code>null</code> или обязательные атрибуты не "валидны"
     */
    @Secured("ROLE_MANAGER_CATALOG")
    @Transactional(readOnly = false)
    public void updateParamGroupOrder(ParamGroup paramGroup) {
        paramGroupDAO.updateOrder(paramGroup);
    }

    /**
     * Возвращает список групп параметров по идентификационному номеру дескриптора набора параметров
     * @param paramSetDescriptorId идентификационный номер дескриптора набора параметров
     * @return
     */
    @Transactional(readOnly = true)
    public List<ParamGroup> getParamGroupList(Integer paramSetDescriptorId) {
        Assert.notNull(paramSetDescriptorId);
        return paramGroupDAO.getParamGroupsByParamSetDescriptorId(paramSetDescriptorId);
    }

    /**
     * Проверяет возможность удаления группы параметров категории.
     * @param paramGroup группа параметров категории
     * @return Возможность удаления - <code>true</code>, иначе - <code>false</code>
     * @throws IllegalArgumentException если <code>paramGroup</code> равен <code>null</code>
     */
    @Transactional(readOnly = true)
    public boolean canDeleteParamGroup(ParamGroup paramGroup) {
        Assert.notNull(paramGroup);
        return (paramDAO.getParamsByParamGroupId(paramGroup.getId()).isEmpty());
    }

    /**
     * Удаляет группу параметров.
     * @param paramGroup группа параметров
     * @throws IllegalArgumentException если <code>paramGroup</code> равен <code>null</code> или если группа параметров не может быть удалена
     */
    @Secured("ROLE_MANAGER_CATALOG")
    @Transactional(readOnly = false)
    public void deleteParamGroup(ParamGroup paramGroup) {
        Assert.notNull(paramGroup);
        Assert.isTrue(canDeleteParamGroup(paramGroup));

        paramGroupDAO.delete(paramGroup.getId());

        Integer psdId = paramGroup.getParameterSetDescriptorId();

        Category category = categoryDAO.getCategoryByParamSetDescriptorId(psdId);
        Assert.notNull(category);

        List<ParamGroup> categoryParamGroups = paramGroupDAO.getParamGroupsByParamSetDescriptorId(psdId);

        if (categoryParamGroups.isEmpty()) {
            category.setParameterSetDescriptorId(null);
            categoryDAO.updateCategoryParameterSetDescriptor(category);
            paramSetDAO.deleteParamSetDescriptor(psdId);
            offerDAO.removeParamSetRefs(category.getId());
        }
    }

    /**
     * Возвращает параметр по идентификатору.
     * @return Параметр или <code>null</code> если параметра с таким идентификатором не существует
     * @throws IllegalArgumentException если <code>id</code> равен <code>null</code>
     */
    @Transactional(readOnly = true)
    public Param getParamById(Integer id) {
        return paramDAO.getParamById(id);
    }

    /**
     * Возвращает список параметров группы, которой принадлежит параметр, отсортированный по порядку.
     * @param paramGroupId идентификационный номер группы параметров
     * @return Список параметров
     * @throws IllegalArgumentException если <code>param</code> равен <code>null</code>
     */
    @Transactional(readOnly = true)
    public List<Param> getParamList(Integer paramGroupId) {
        Assert.notNull(paramGroupId);
        return (paramDAO.getParamsByParamGroupId(paramGroupId));
    }

    /**
     * Проверяет возможность удаления параметра категории.
     * @param param параметр категории
     * @return Возможность удаления - <code>true</code>, иначе - <code>false</code>
     * @throws IllegalArgumentException если <code>param</code> равен <code>null</code>
     */
    @Transactional(readOnly = true)
    public boolean canDeleteParam(Param param) {
        Assert.notNull(param);
        return true; // TODO impl
    }

    /**
     * Возвращает элемент соответствующий категории каталога по идентификатору параметра категории
     * @param paramId идентификатор параметра категории
     * @return элемент или <code>null</code> если элемента с группой параметров с таким <code>paramId</code> не существует
     * @throws IllegalArgumentException если <code>paramId</code> равен <code>null</code>
     */
    @Transactional(readOnly = true)
    public CatalogItem getCategoryItemByParamId(Integer paramId) {
        Param param = paramDAO.getParamById(paramId);
        Assert.notNull(param);
        Category category = categoryDAO.getCategoryByParamSetDescriptorId(param.getParameterSetDescriptorId());
        Assert.notNull(category);
        return catalogItemDAO.getCategoryItemById(category.getId());
    }

    /**
     * Добавляет параметр категории в группу. Устанавливает для <code>param</code>.
     * Устанавливает для <code>param</code> полученный <code>id</code>.
     * Устанавливает для <code>param</code> <code>parameterSetDescriptorId</code>.
     * Устанавливает для <code>param</code> <code>paramGroupId</code>.
     * Устанавливает для <code>param</code> порядок (<code>ordinal</code> - максимальный).
     * @param paramGroup группа параметров
     * @param param добавляемый параметр
     * @throws IllegalArgumentException если один из аргументов равен <code>null</code> или обязательные атрибуты не "валидны"
     */
    @Secured("ROLE_MANAGER_CATALOG")
    @Transactional(readOnly = false)
    public void addParam(ParamGroup paramGroup, Param param) {
        Assert.notNull(paramGroup);
        Assert.notNull(paramGroup.getParameterSetDescriptorId());
        Assert.notNull(param);

        CatalogItem categoryItem = getCategoryItemByParamGroupId(paramGroup.getId());
        Assert.notNull(categoryItem);

        param.setParameterSetDescriptorId(paramGroup.getParameterSetDescriptorId());
        param.setParamGroupId(paramGroup.getId());
        param.setColumnName(MessageFormat.format(PARAM_CATEGORY_COLUMN_NAME_FORMAT, categoryItem.getId()));

        paramDAO.insert(param);
    }

    /**
     * Обновляет информацию о параметре.
     * Не обновляет идентификатор, порядок, связь с дескриптором набора параметров, связь с группой параметров, наименование столбца, тип.
     * @param param обновляемый параметр
     * @throws IllegalArgumentException если <code>param</code> равен <code>null</code>
     */
    @Secured("ROLE_MANAGER_CATALOG")
    @Transactional(readOnly = false)
    public void updateParamInfo(Param param) {
        paramDAO.updateInfo(param);
    }

    /**
     * Устанавливает порядок параметра в группе.
     * Переупорядочивает остальные параметры в группе.
     * @param param параметр
     * @throws IllegalArgumentException если <code>param</code> равен <code>null</code> или обязательные атрибуты не "валидны"
     */
    @Secured("ROLE_MANAGER_CATALOG")
    @Transactional(readOnly = false)
    public void updateParamOrder(Param param) {
        paramDAO.updateOrder(param);
    }

    /**
     * Удаляет параметр.
     * @param param параметр
     * @throws IllegalArgumentException если <code>param</code> равен <code>null</code> или если параметр не может быть удален
     */
    @Secured("ROLE_MANAGER_CATALOG")
    @Transactional(readOnly = false)
    public void deleteParam(Param param) {
        Assert.notNull(param);
        Assert.isTrue(canDeleteParam(param));

        paramDAO.delete(param.getId());
    }

    /**
     * Перемещает параметр в новую группу последним по порядку.
     * @param param параметр
     * @throws IllegalArgumentException если <code>param</code> равен <code>null</code> или обязательные атрибуты не "валидны" 
     */
    @Secured("ROLE_MANAGER_CATALOG")
    @Transactional(readOnly = false)
    public void moveParamToGroup(Param param) {
        Assert.notNull(param);
        Assert.notNull(param.getParamGroupId());

        Param paramInfo = paramDAO.getParamById(param.getId());
        if (paramInfo.getParamGroupId().equals(param.getParamGroupId())) {
            // nothing move
            return;
        }

        paramDAO.updateParamGroup(param);
    }

    /**
     * Переключает свойство параметра основной/неосновной.
     * @param param параметр
     * @throws IllegalArgumentException если <code>param</code> равен <code>null</code> или обязательные атрибуты не "валидны" 
     */
    @Secured("ROLE_MANAGER_CATALOG")
    @Transactional(readOnly = false)
    public void switchParamBase(Param param) {
        Assert.notNull(param);
        Assert.notNull(param.getBase());

        param.setBase(!param.getBase());
        paramDAO.updateBase(param);
    }

    /**
     * Возвращает вариант выбора выборочного параметра по идентификатору.
     * @return Вариант выбора выборочного параметра или <code>null</code> если варианта выбора выборочного параметра с таким идентификатором не существует
     * @throws IllegalArgumentException если <code>id</code> равен <code>null</code>
     */
    @Transactional(readOnly = true)
    public ParamSelectOption getParamSelectOptionById(Integer id) {
        return paramSelectOptionDAO.getParamSelectOptionById(id);
    }

    /**
     * Возвращает список всех вариантов выбора выборочного параметра, связанных с конкретным параметром, отсортированный по <code>ordinal</code>.
     * @param param параметр
     * @return Список вариантов выбора выборочного параметра или пустой список если варианты выбора выборочного параметра, связанные с конкретным параметром, отсутствуют
     * @throws IllegalArgumentException если <code>param</code> равен <code>null</code>
     */
    @Transactional(readOnly = true)
    public List<ParamSelectOption> getParamSelectOptions(Param param) {
        Assert.notNull(param);
        return paramSelectOptionDAO.getParamSelectOptionsByParamId(param.getId());
    }

    /**
     * Добавляет варианты выбора к выборочному параметру.
     * @param paramId
     * @param selectOptions добавляемые вырианты выбора выборочного параметра, отсортированные в порядке добавления
     * @throws IllegalArgumentException если один из аргументов равен <code>null</code> или обязательные атрибуты не "валидны"
     */
    @Secured("ROLE_MANAGER_CATALOG")
    @Transactional(readOnly = false)
    public void addParamSelectOptions(Integer paramId, List<ParamSelectOption> selectOptions) {
        Assert.notNull(paramId);
        Assert.notNull(selectOptions);

        Param p = paramDAO.getParamById(paramId);

        Assert.notNull(p);
        Assert.isTrue(p.getType() == Param.Type.SELECT);

        int oldSize = paramSelectOptionDAO.getParamSelectOptionsByParamId(paramId).size();
        for (int i = 0; i < selectOptions.size(); i++) {
            // TODO batch insert
            ParamSelectOption option = selectOptions.get(i);
            option.setParamId(paramId);
            option.setOrdinal(oldSize + i);
            paramSelectOptionDAO.insert(option);
        }
    }

    /**
     * Обновляет варианты выбора параметра.
     * @param paramId
     * @param selectOptions вырианты выбора выборочного параметра
     * @throws IllegalArgumentException если один из аргументов равен <code>null</code> или обязательные атрибуты не "валидны"
     */
    @Secured("ROLE_MANAGER_CATALOG")
    @Transactional(readOnly = false)
    public void updateParamSelectOptions(Integer paramId, List<ParamSelectOption> selectOptions) {
        Assert.notNull(paramId);
        Assert.notNull(selectOptions);

        Param p = paramDAO.getParamById(paramId);

        Assert.notNull(p);
        Assert.isTrue(p.getType() == Param.Type.SELECT);

        for (ParamSelectOption option : selectOptions) {
            option.setParamId(paramId);
            paramSelectOptionDAO.updateInfo(option);
        }
    }

    /**
     * Проверяет возможность удаления варианта выбора выборочного параметра.
     * @param paramSelectOption вариант выбора выборочного параметра
     * @return Возможность удаления - <code>true</code>, иначе - <code>false</code>
     * @throws IllegalArgumentException если <code>paramSelectOption</code> равен <code>null</code>
     */
    @Transactional(readOnly = true)
    public boolean canDeleteParamSelectOption(ParamSelectOption paramSelectOption) {
        Assert.notNull(paramSelectOption);

        // не удалять последний вариант выбора
        List<ParamSelectOption> paramSelectOptions = paramSelectOptionDAO.getParamSelectOptionsByParamId(paramSelectOption.getParamId());
        if (paramSelectOptions.size() <= 1) {
            return false;
        }

        // TODO next impl или обнуление значений параметра у товарных предложений?
        return true;
    }

    /**
     * Удаляет вариант выбора выборочного параметра.
     * @param paramSelectOption вариант выбора выборочного параметра
     * @throws IllegalArgumentException если <code>paramSelectOption</code> равен <code>null</code> или если вариант выбора не может быть удален
     */
    @Secured("ROLE_MANAGER_CATALOG")
    @Transactional(readOnly = false)
    public void deleteParamSelectOption(ParamSelectOption paramSelectOption) {
        Assert.notNull(paramSelectOption);

        if (!canDeleteParamSelectOption(paramSelectOption)) {
            return;
        }

        paramSelectOptionDAO.delete(paramSelectOption.getId());
    }

    /**
     * Устанавливает порядок варианта выбора выборочного параметра. Переупорядочивает остальные варианты выбора выборочного параметра.
     * @param paramSelectOption вариант выбора выборочного параметра
     * @param shiftUp если <code>true</code>, то сдвиг вверх, иначе - вниз
     * @throws IllegalArgumentException если <code>paramSelectOption</code> равен <code>null</code> или если вариант выбора не может быть удален
     */
    @Secured("ROLE_MANAGER_CATALOG")
    @Transactional(readOnly = false)
    public void updateParamSelectOptionOrder(ParamSelectOption paramSelectOption) {
        paramSelectOptionDAO.updateOrder(paramSelectOption);
    }

    /**
     * Возвращает модель параметров
     * @param paramSetDescriptorId
     * @return
     */
    @Transactional(readOnly = true)
    public ParamModel getParamModel(Integer paramSetDescriptorId) {
        return paramModelHelper.getParamModel(paramSetDescriptorId);
    }

    /**
     * Возвращает набор параметров.
     * @param paramDescriptorId идентификатор дескриптора набора параметров
     * @param paramSetId
     * @return Карту значений параметров.
     * Ключем карты является <code>id</code> параметра.
     * Класс объекта-значения зависит от типа параметра.
     * @throws IllegalArgumentException если :
     * - любой из аргументов равен <code>null</code>
     * - дескриптор набора параметров с таким <code>paramDescriptorId</code> не существует
     * - набор параметров с таким <code>paramSetId</code> не существует
     */
    @Transactional(readOnly = true)
    public Map<Integer, Object> getParamSet(Integer paramSetDescriptorId, Integer paramSetId) {
        return paramSetDAO.getParamSetById(paramSetDescriptorId, paramSetId);
    }

    /**
     * Возвращает карту наборов параметров
     * @param paramSetDescriptorId идентификатор дескриптора набора параметров
     * @param paramSetIdSet набор идентификационных номеров наборов параметров
     * @return Карту наборов
     * Ключем карты является идентификационный номер набора параметров
     * Значением карта, где:
     * ключем является <code>id</code> параметра.
     * класс объекта-значения зависит от типа параметра.
     * @throws IllegalArgumentException если :
     * - любой из аргументов равен <code>null</code>
     * - дескриптор набора параметров с таким <code>paramDescriptorId</code> не существует
     * - набор идентификационных номеров <code>paramSetIdSet</code> пуст
     */
    @Transactional(readOnly = true)
    public Map<Integer, Map<Integer, Object>> getParamSetMap(Integer paramSetDescriptorId, Set<Integer> paramSetIdSet) {
        return paramSetDAO.getParamSetMap(paramSetDescriptorId, paramSetIdSet);
    }

    /**
     * Возвращает список параметров, у которых есть описания.
     * Сортировка в алфавитном порядке.
     * @param paramSetDescriptorId
     * @return
     */
    @Transactional(readOnly = true)
    public List<Param> getParamWithDescriptionList(Integer paramSetDescriptorId) {
        Assert.notNull(paramSetDescriptorId);

        List<Param> paramList = new ArrayList<Param>();

        for (Param p : paramDAO.getParamListByDescriptorId(paramSetDescriptorId)) {
            if (StringUtils.isNotBlank(p.getDescription())) {
                paramList.add(p);
            }
        }

        Collections.sort(paramList, new ParamNameComparator());

        return paramList;
    }
}
