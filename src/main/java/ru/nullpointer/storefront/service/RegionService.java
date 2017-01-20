package ru.nullpointer.storefront.service;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Resource;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import ru.nullpointer.storefront.dao.RegionDAO;
import ru.nullpointer.storefront.domain.Region;
import ru.nullpointer.storefront.domain.support.PageConfig;
import ru.nullpointer.storefront.domain.support.PaginatedQueryResult;

/**
 * @author Alexander Yastrebov
 * @author ankostyuk
 */
// TODO добавить @Transactional если будет динамическое изменение данных
@Service
public class RegionService {

    @Resource
    private RegionDAO regionDAO;

    /**
     * Возвращает регион по идентификационному номеру
     * 
     * @param id
     * @return регион или <code>null</code> если региона с таким номером не
     *         существует
     * @throws IllegalArgumentException
     *             если <code>id</code> равен <code>null</code>
     */
    public Region getRegionById(Integer id) {
        return regionDAO.getRegionById(id);
    }

    /**
     * Находит регионы по вхождению текста в наименовании. Вхождение текста
     * определяется в любом месте наименования и без учета регистра.
     * 
     * @param text
     *            текст в наименовании региона
     * @return список регионов (сортировка: в порядке "крупности" + в алфавитном
     *         порядке), или пустой список, если отсутствует вхождение текста в
     *         наименовании регионов.
     * @throws IllegalArgumentException
     *             если <code>text</code> равен <code>null</code> или пустой
     *             строке
     */
    public List<Region> findRegionsByNameText(String text) {
        Assert.hasText(text);
        // TODO разобраться с алфавитной сортировкой
        return regionDAO.getRegionsByNameText(text);
    }

    /**
     * Предлагает регионы по вхождению текста в наименовании. Вхождение текста
     * определяется в любом месте наименования и без учета регистра. Для каждого
     * региона будет определен путь.
     * 
     * @param text
     *            текст в наименовании региона
     * @return список регионов (сортировка: в порядке "крупности" + в алфавитном
     *         порядке), или пустой список, если отсутствует вхождение текста в
     *         наименовании регионов.
     * @throws IllegalArgumentException
     *             если <code>text</code> равен <code>null</code> или пустой
     *             строке
     */
    public List<Region> suggestRegionsByNameText(String text) {
        Assert.hasText(text);

        // TODO разобраться с алфавитной сортировкой
        List<Region> regions = regionDAO.getRegionsByNameText(text);
        for (Region region : regions) {
            region.setPath(regionDAO.getRegionPath(region));
        }

        return regions;
    }

    /**
     * Постранично предлагает регионы по вхождению текста в наименовании.
     * Вхождение текста определяется в любом месте наименования и без учета
     * регистра. Для каждого региона будет определен путь.
     * 
     * @param text
     *            текст в наименовании региона
     * @param pageConfig
     *            настройки постраничного вывода
     * @return список регионов (сортировка: в порядке "крупности" + в алфавитном
     *         порядке), или пустой список, если отсутствует вхождение текста в
     *         наименовании регионов.
     */
    public PaginatedQueryResult<Region> suggestRegionsPaginatedByNameText(String text, PageConfig pageConfig) {
        if (StringUtils.isBlank(text)) {
            return new PaginatedQueryResult<Region>(pageConfig, Collections.<Region>emptyList(), 0);
        }

        // performance
        int total = regionDAO.getRegionsByNameTextCount(text);

        // TODO разобраться с алфавитной сортировкой
        List<Region> regions = regionDAO.getRegionsPaginatedByNameText(text, pageConfig);
        for (Region region : regions) {
            region.setPath(regionDAO.getRegionPath(region));
        }

        return new PaginatedQueryResult<Region>(pageConfig, regions, total);
    }

    /**
     * Возвращает путь региона как список родительских регионов.
     * 
     * @param region
     *            регион
     * @return список родительских регионов для региона, отсортированный в
     *         порядке вложенности (первый элемент в списке - регион 1-го
     *         уровня), или пустой список, если регион 1-го уровня.
     * @throws IllegalArgumentException
     *             если <code>region</code> равен <code>null</code> или
     *             обязательные атрибуты не "валидны"
     */
    public List<Region> getRegionPath(Region region) {
        return regionDAO.getRegionPath(region);
    }

    /**
     * Возвращает список регионов доставки поставщика. 
     * Для каждого региона будет определен путь.
     *
     * @param companyId идентификатор поставщика
     * @return список регионов или пустой список если поставщик не указал ни одного региона доставки
     * или поставщика с таким идентификатором не существует
     * @throws IllegalArgumentException если <code>companyId</code> равен <code>null</code>
     */
    public List<Region> getCompanyDeliveryRegionList(Integer companyId) {
        // TODO sort
        Assert.notNull(companyId);
        List<Region> regions = regionDAO.getCompanyDeliveryRegionList(companyId);
        for (Region region : regions) {
            region.setPath(regionDAO.getRegionPath(region));
        }
        return regions;
    }

    /**
     * Нормализует строку, для поиска по вхождению текста в наименовании
     * региона.
     * 
     * @param str
     *            строка
     * @return нормализованная строка
     * @throws IllegalArgumentException
     *             если <code>text</code> равен <code>null</code>
     */
    // TODO в utils?
    public String normalizeRegionNameText(String str) {
        Assert.notNull(str);
        return str.replaceAll("^\\s+|\\s+$", "").replaceAll("\\s+", " ");
    }

    /**
     * Возвращает <code>true</code>, если поставщик находится или осуществляет доставку
     * в заданный регион 
     * @param region регион
     * @param deliveryRegionList список регионов доставки поставщика
     * @param companyRegion регион поставщика
     * @return
     */
    public boolean hasUserRegionDelivery(Region region, List<Region> deliveryRegionList, Region companyRegion) {
        Assert.notNull(region);
        Assert.notNull(deliveryRegionList);
        Assert.notNull(companyRegion);
        
        if (region == null) {
            return false;
        }

        boolean delivery = contains(region, companyRegion);

        Iterator<Region> it = deliveryRegionList.iterator();
        while (!delivery && it.hasNext()) {
            delivery = contains(region, it.next());
        }
        return delivery;
    }

    private boolean contains(Region r1, Region r2) {
        return (r1.getLeft() > r2.getLeft() && r1.getRight() < r2.getRight())
                || (r1.getLeft() <= r2.getLeft() && r1.getRight() >= r2.getRight());
    }
}
