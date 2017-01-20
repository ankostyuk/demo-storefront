package ru.nullpointer.storefront.service.search;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.nullpointer.storefront.dao.BrandDAO;
import ru.nullpointer.storefront.dao.CatalogItemDAO;
import ru.nullpointer.storefront.dao.CategoryDAO;
import ru.nullpointer.storefront.dao.CompanyDAO;
import ru.nullpointer.storefront.dao.MatchDAO;
import ru.nullpointer.storefront.dao.ModelDAO;
import ru.nullpointer.storefront.dao.OfferDAO;
import ru.nullpointer.storefront.dao.RegionDAO;
import ru.nullpointer.storefront.domain.Brand;
import ru.nullpointer.storefront.domain.CatalogItem;
import ru.nullpointer.storefront.domain.Category;
import ru.nullpointer.storefront.domain.Company;
import ru.nullpointer.storefront.domain.Model;
import ru.nullpointer.storefront.domain.ModelInfo;
import ru.nullpointer.storefront.domain.Offer;
import ru.nullpointer.storefront.domain.Region;
import ru.nullpointer.storefront.domain.support.CompanyOfferShowing;
import ru.nullpointer.storefront.domain.support.CompanyOfferSorting;
import ru.nullpointer.storefront.domain.support.CompanyRegion;
import ru.nullpointer.storefront.domain.support.PageConfig;
import ru.nullpointer.storefront.domain.support.PaginatedQueryResult;

/**
 * @author ankostyuk
 */
@Component
public class DataAccess {

    private Logger logger = LoggerFactory.getLogger(DataAccess.class);
    //
    @Resource
    private CatalogItemDAO catalogItemDAO;
    @Resource
    private OfferDAO offerDAO;
    @Resource
    private ModelDAO modelDAO;
    @Resource
    private CategoryDAO categoryDAO;
    @Resource
    private BrandDAO brandDAO;
    @Resource
    private CompanyDAO companyDAO;
    @Resource
    private RegionDAO regionDAO;
    @Resource
    private MatchDAO matchDAO;

    @Transactional(readOnly = true)
    public List<CatalogItem> getCatalogItemTree(Integer sectionId) {
        return catalogItemDAO.getSubTree(sectionId);
    }

    @Transactional(readOnly = true)
    public List<CatalogItem> getCatalogItemPath(Integer itemId) {
        return catalogItemDAO.getPath(itemId);
    }

    @Transactional(readOnly = true)
    public PaginatedQueryResult<Offer> getCategoryOfferPaginatedList(Integer categoryId, PageConfig pageConfig) {
        List<Offer> list = offerDAO.getCategoryOfferPaginatedList(categoryId, pageConfig);
        int total = offerDAO.getCategoryOfferCount(categoryId);
        return new PaginatedQueryResult<Offer>(pageConfig, list, total);
    }

    @Transactional(readOnly = true)
    public PaginatedQueryResult<Model> getCategoryModelPaginatedList(Integer categoryId, PageConfig pageConfig) {
        List<Model> list = modelDAO.getCategoryModelList(categoryId, pageConfig);
        int total = modelDAO.getCategoryModelCount(categoryId);
        return new PaginatedQueryResult<Model>(pageConfig, list, total);
    }

    @Transactional(readOnly = true)
    public List<Category> getCategoryList() {
        return categoryDAO.getAllCategories();
    }

    @Transactional(readOnly = true)
    public Brand getBrandById(Integer brandId) {
        return brandDAO.getBrandById(brandId);
    }

    @Transactional(readOnly = true)
    public List<Brand> getBrandList() {
        return brandDAO.getBrandList();
    }

    @Transactional(readOnly = true)
    public PaginatedQueryResult<Company> getCompanyPaginatedList(PageConfig pageConfig) {
        List<Company> list = companyDAO.getCompanyList(null, pageConfig);
        int total = companyDAO.getCompanyCount(null);
        return new PaginatedQueryResult<Company>(pageConfig, list, total);
    }

    @Transactional(readOnly = true)
    public Map<Integer, List<CatalogItem>> getCategoryPathMap() {
        List<Category> categoryList = categoryDAO.getAllCategories();
        Map<Integer, List<CatalogItem>> map = new HashMap<Integer, List<CatalogItem>>(categoryList.size());
        for (Category category : categoryList) {
            map.put(category.getId(), catalogItemDAO.getPath(category.getId()));
        }
        return map;
    }

    @Transactional(readOnly = true)
    public PaginatedQueryResult<Offer> getCompanyOfferPaginatedList(Integer companyId, PageConfig pageConfig) {
        CompanyOfferShowing showing = CompanyOfferShowing.ALL;
        CompanyOfferSorting sorting = CompanyOfferSorting.DATE_CREATED_ASCENDING;

        List<Offer> list = offerDAO.getCompanyOfferList(companyId, null, pageConfig, showing, sorting);
        int total = offerDAO.getCompanyOfferCount(companyId, null, showing);

        return new PaginatedQueryResult<Offer>(pageConfig, list, total);
    }

    @Transactional(readOnly = true)
    public Company getCompanyById(Integer id) {
        return companyDAO.getCompanyById(id);
    }

    @Transactional(readOnly = true)
    public CompanyRegion getCompanyRegion(Company company) {
        Region selfRegion = regionDAO.getRegionById(company.getRegionId());
        List<Region> deliveryRegionList = regionDAO.getCompanyDeliveryRegionList(company.getId());
        return new CompanyRegion(company, selfRegion, deliveryRegionList);
    }

    @Transactional(readOnly = true)
    public Map<Integer, CompanyRegion> getCompanyRegionMap(Set<Integer> companyIdSet) {
        Map<Integer, Company> companyMap = companyDAO.getCompanyMap(companyIdSet);
        Map<Integer, CompanyRegion> companyRegionMap = new HashMap<Integer, CompanyRegion>(companyMap.size());

        for (Company company : companyMap.values()) {
            Region selfRegion = regionDAO.getRegionById(company.getRegionId());
            List<Region> deliveryRegionList = regionDAO.getCompanyDeliveryRegionList(company.getId());
            companyRegionMap.put(company.getId(), new CompanyRegion(company, selfRegion, deliveryRegionList));
        }

        return companyRegionMap;
    }

    @Transactional(readOnly = true)
    public Model getModelById(Integer id) {
        return modelDAO.getModelById(id);
    }

    @Transactional(readOnly = true)
    public ModelInfo getModelInfo(Integer modelId) {
        Set<Integer> modelIdSet = new HashSet<Integer>();
        modelIdSet.add(modelId);
        Map<Integer, ModelInfo> modelInfoMap = matchDAO.getModelInfoMap(modelIdSet, null);
        return modelInfoMap.get(modelId);
    }
}
