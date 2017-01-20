package ru.nullpointer.storefront.service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import ru.nullpointer.storefront.dao.CompanyDAO;
import ru.nullpointer.storefront.dao.RegionDAO;
import ru.nullpointer.storefront.domain.Company;
import ru.nullpointer.storefront.domain.Region;
import ru.nullpointer.storefront.domain.support.PageConfig;
import ru.nullpointer.storefront.domain.support.PaginatedQueryResult;
import ru.nullpointer.storefront.service.search.SearchIndexing;
import ru.nullpointer.storefront.service.support.FieldUtils;

/**
 * @author Alexander Yastrebov
 * @author ankostyuk
 */
@Service
public class CompanyService {

    private Logger logger = LoggerFactory.getLogger(CompanyService.class);
    //
    @Resource
    private CompanyDAO companyDAO;
    @Resource
    private RegionDAO regionDAO;
    @Resource
    private SecurityService securityService;

    /**
     * Возвращает компанию по <code>id</code>
     * @param id <code>id</code> компании
     * @return компания или <code>null</code> если компании с таким <code>id</code> не существует
     */
    @Transactional(readOnly = true)
    public Company getCompanyById(int id) {
        return companyDAO.getCompanyById(id);
    }

    /**
     * Возвращает карту поставщиков.
     * @param companyIdSet набор <code>id</code> компаний
     * @return Карту компаний. Ключем является <code>id</code> компании. Карта может быть пустой если список <code>companyIdSet</code> пуст или содержит неверные данные
     * @throws IllegalArgumentException если <code>companyIdSet</code> равен <code>null</code>
     */
    @Transactional(readOnly = true)
    public Map<Integer, Company> getCompanyMap(Set<Integer> companyIdSet) {
        return companyDAO.getCompanyMap(companyIdSet);
    }

    /**
     * Обновляет информацию о компании. Не меняет <code>id</code>, <code>accountId</code>
     * @param company
     * @throws IllegalArgumentException если <code>company</code> равен <code>null</code>
     */
    // TODO Подумать об обновлении <code>name</code>
    @Secured("ROLE_COMPANY")
    @Transactional
    @SearchIndexing
    public void updateCompanyInfo(Company company) {
        Assert.notNull(company);
        Assert.notNull(company.getId());

        Integer companyId = securityService.getAuthenticatedCompanyId();
        Assert.isTrue(company.getId().equals(companyId), "Попытка обновить информацию чужого поставщика");

        // MARKER-SPN (marker string property name)
        FieldUtils.nullifyIfEmpty(company, "contactPerson");
        FieldUtils.nullifyIfEmpty(company, "site");
        FieldUtils.nullifyIfEmpty(company, "schedule");
        FieldUtils.nullifyIfEmpty(company, "scope");
        FieldUtils.nullifyIfEmpty(company, "deliveryConditions");
        FieldUtils.nullifyIfEmpty(company, "paymentConditions.text");

        companyDAO.updateInfo(company);
    }

    /**
     * Возвращает список регионов доставки поставщика
     * @param companyId ИД поставщика
     * @return список регионов или пустой список если поставщик не указал ни одного региона доставки
     * или поставщика с таким ИД не существует
     * @throws IllegalArgumentException если <code>companyId</code> равен <code>null</code>
     */
    @Secured("ROLE_COMPANY")
    @Transactional(readOnly = true)
    public List<Region> getCompanyDeliveryRegionList() {
        Integer companyId = securityService.getAuthenticatedCompanyId();
        return regionDAO.getCompanyDeliveryRegionList(companyId);
    }

    /**
     * Устанавливает список регионов доставки поставщика
     * @param deliveryList
     * @throws IllegalArgumentException если <code>regionIdList</code> равен <code>null</code>
     * TODO: Проверять «вложенность» регионов и совпадение со своим регионом
     */
    @Secured("ROLE_COMPANY")
    @Transactional
    @SearchIndexing
    public void setCompanyDeliveryRegionList(List<Integer> regionIdList) {
        Assert.notNull(regionIdList);

        Integer companyId = securityService.getAuthenticatedCompanyId();

        Set<Integer> deleteList = new HashSet<Integer>();
        // создать копию
        Set<Integer> insertList = new HashSet<Integer>(regionIdList);

        List<Region> oldDeliveryList = regionDAO.getCompanyDeliveryRegionList(companyId);
        for (Region old : oldDeliveryList) {
            Integer oldId = old.getId();
            if (insertList.contains(oldId)) {
                // уже есть такой регион
                insertList.remove(oldId);
            } else {
                // нет такого региона
                deleteList.add(oldId);
            }
        }

        for (Integer id : deleteList) {
            regionDAO.deleteCompanyDeliveryRegion(companyId, id);
        }

        for (Integer id : insertList) {
            regionDAO.insertCompanyDeliveryRegion(companyId, id);
        }
    }

    /**
     * Удаляет регион доставки поставщика
     * @param region
     * @throws IllegalArgumentException если <code>region</code> равен <code>null</code>
     */
    @Secured("ROLE_COMPANY")
    @Transactional
    @SearchIndexing
    public void deleteCompanyDeliveryRegion(Region region) {
        Assert.notNull(region);
        Integer companyId = securityService.getAuthenticatedCompanyId();
        regionDAO.deleteCompanyDeliveryRegion(companyId, region.getId());
    }

    /**
     * Возвращает список поставщиков отсортированный по убыванию даты подтверждения емейла
     * @param text текст в наименовании поставщика или <code>null</code> для всех поставщиков
     * @param pageConfig
     * @return
     */
    @Secured({"ROLE_ADMIN", "ROLE_MANAGER_COMPANY_LOGIN"})
    @Transactional(readOnly = true)
    public PaginatedQueryResult<Company> getCompanyList(String text, PageConfig pageConfig) {
        List<Company> list = companyDAO.getCompanyList(text, pageConfig);

        int total = companyDAO.getCompanyCount(text);

        return new PaginatedQueryResult<Company>(pageConfig, list, total);
    }
}
