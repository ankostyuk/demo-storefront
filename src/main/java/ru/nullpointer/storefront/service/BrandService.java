package ru.nullpointer.storefront.service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Resource;
import org.apache.commons.lang.StringUtils;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import ru.nullpointer.storefront.dao.BrandDAO;
import ru.nullpointer.storefront.domain.Brand;
import ru.nullpointer.storefront.domain.support.ResultGroup;
import ru.nullpointer.storefront.service.search.SearchIndexing;
import ru.nullpointer.storefront.service.support.AlphabetResultBuilder;
import ru.nullpointer.storefront.service.support.FieldUtils;

/**
 * @author Alexander Yastrebov
 * @author ankostyuk
 */
@Service
public class BrandService {

    @Resource
    private BrandDAO brandDAO;

    /**
     * Возвращает количество брендов
     */
    @Transactional(readOnly = true)
    public int getBrandCount() {
        return brandDAO.getBrandCount();
    }

    /**
     * Возвращает бренд по идентификационному номеру
     * @param brandId
     * @return
     */
    @Transactional(readOnly = true)
    public Brand getBrandById(Integer brandId) {
        return brandDAO.getBrandById(brandId);
    }

    /**
     * Возвращает карту производителей.
     * @param brandIdSet набор <code>id</code> производителей
     * @return Карту производителей.
     * Ключем является <code>id</code> производителя.
     * @throws IllegalArgumentException если <code>brandIdSet</code> равен <code>null</code>
     */
    @Transactional(readOnly = true)
    public Map<Integer, Brand> getBrandMap(Set<Integer> brandIdSet) {
        return brandDAO.getBrandMap(brandIdSet);
    }

    /**
     * Возвращает список брендов по набору <code>id</code> брендов.
     * Сортировка в алфавитном порядке наименования бренда.
     * @param brandIdSet набор <code>id</code> брендов
     * @return 
     * @throws IllegalArgumentException если <code>brandIdSet</code> равен <code>null</code>
     */
    @Transactional(readOnly = true)
    public List<Brand> getBrandListByIdSet(Set<Integer> brandIdSet) {
        return brandDAO.getBrandListByIdSet(brandIdSet);
    }

    /**
     * Возвращает список брендов сгруппированных по первой букве наименования
     * @return
     */
    @Transactional(readOnly = true)
    public List<ResultGroup<Brand>> getBrandGroupList() {
        List<Brand> brandList = brandDAO.getBrandList();

        return new BrandResultBuilder().buildResult(brandList);
    }

    /**
     * Возвращает список первых букв имен доступных брендов
     * @return
     */
    @Transactional(readOnly = true)
    public List<String> getBrandToc() {
        List<String> prefixList = brandDAO.getBrandNamePrefixList();

        return new BrandResultBuilder().buildToc(prefixList);
    }

    @Transactional(readOnly = true)
    public List<Brand> findBrandListByText(String text, int limit) {
        if (StringUtils.isBlank(text)) {
            return Collections.emptyList();
        }

        return brandDAO.findBrandListByText(text, limit);
    }

    /**
     * Возвращает список брендов, для которых существуют предложения в категории.
     * Список отсортирован в алфавитном порядке наименования бренда.
     * @param categoryId
     * @return
     * @throws IllegalArgumentException если <code>categoryId</code> равен <code>null</code>
     */
    @Transactional(readOnly = true)
    public List<Brand> getCategoryBrandList(Integer categoryId) {
        return brandDAO.getCategoryBrandList(categoryId);
    }

    /**
     * Возвращает список брендов из набора брендов, для которых существуют предложения в наборе категорий.
     * Сортировка в алфавитном порядке наименования бренда.
     * @param brandIdSet набор <code>id</code> брендов
     * @param categoryIdSet набор <code>id</code> категорий
     * @return
     * @throws IllegalArgumentException если <code>brandIdSet</code> равен <code>null</code> или
     * <code>categoryIdSet</code> равен <code>null</code>
     */
    @Transactional(readOnly = true)
    public List<Brand> getIntersectionList(Set<Integer> brandIdSet, Set<Integer> categoryIdSet) {
        return brandDAO.getIntersectionList(brandIdSet, categoryIdSet);
    }

    /**
     * Сохраняет бренд
     * @param brand
     */
    @Secured("ROLE_MANAGER_BRAND")
    @Transactional
    @SearchIndexing
    public void storeBrand(Brand brand) {
        checkBrand(brand);

        brandDAO.insert(brand);
    }

    /**
     * Обновляет информацию о бренде
     * @param brand
     */
    @Secured("ROLE_MANAGER_BRAND")
    @Transactional
    @SearchIndexing
    public void updateBrandInfo(Brand brand) {
        checkBrand(brand);

        brandDAO.updateInfo(brand);
    }

    /**
     * Удаляет производителя.
     */
    @Secured("ROLE_MANAGER_BRAND")
    @Transactional
    @SearchIndexing
    @Deprecated
    public void deleteBrand(Brand brand) {
        Assert.notNull(brand);

        // TODO вычистить ссылки

        brandDAO.delete(brand.getId());
    }

    private void checkBrand(Brand brand) {
        Assert.notNull(brand);

        FieldUtils.nullifyIfEmpty(brand, "keywords");
        FieldUtils.nullifyIfEmpty(brand, "site");
        FieldUtils.nullifyIfEmpty(brand, "logo");
    }
    //

    private static class BrandResultBuilder extends AlphabetResultBuilder<Brand> {

        @Override
        public String getGroupValue(Brand brand) {
            return brand.getName();
        }
    }
}
