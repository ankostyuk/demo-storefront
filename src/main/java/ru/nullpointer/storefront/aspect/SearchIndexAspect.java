package ru.nullpointer.storefront.aspect;

import java.util.List;
import java.util.Map;
import javax.annotation.Resource;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import ru.nullpointer.storefront.dao.OfferDAO;
import ru.nullpointer.storefront.domain.Brand;

import ru.nullpointer.storefront.domain.CatalogItem;
import ru.nullpointer.storefront.domain.Category;
import ru.nullpointer.storefront.domain.Company;
import ru.nullpointer.storefront.domain.Model;
import ru.nullpointer.storefront.domain.Offer;
import ru.nullpointer.storefront.domain.Region;
import ru.nullpointer.storefront.service.SecurityService;
import ru.nullpointer.storefront.service.search.SearchLifecycle;

/**
 * @author ankostyuk
 */
@Aspect
@Order(Integer.MIN_VALUE + 1)
public class SearchIndexAspect {

    private Logger logger = LoggerFactory.getLogger(SearchIndexAspect.class);
    //

    @Resource
    private SearchLifecycle searchLifecycle;
    @Resource
    private OfferDAO offerDAO;
    @Resource
    private SecurityService securityService;
    //

    @AfterReturning("execution(* ru.nullpointer.storefront.service.CatalogService.addSection(..)) && args(sectionItem, sectionId) && @annotation(ru.nullpointer.storefront.service.search.SearchIndexing)")
    public void after_addSection(CatalogItem sectionItem, Integer sectionId) {
        searchLifecycle.addCatalogSectionIndex(sectionItem);
    }

    @AfterReturning("execution(* ru.nullpointer.storefront.service.CatalogService.addCategory(..)) && args(categoryItem, category, sectionId) && @annotation(ru.nullpointer.storefront.service.search.SearchIndexing)")
    public void after_addCategory(CatalogItem categoryItem, Category category, Integer sectionId) {
        searchLifecycle.addCatalogCategoryIndex(categoryItem);
    }

    @AfterReturning("execution(* ru.nullpointer.storefront.service.CatalogService.deleteSection(..)) && args(sectionId) && @annotation(ru.nullpointer.storefront.service.search.SearchIndexing)")
    public void after_deleteSection(Integer sectionId) {
        searchLifecycle.deleteCatalogSectionIndex(sectionId);
    }

    @AfterReturning("execution(* ru.nullpointer.storefront.service.CatalogService.deleteCategory(..)) && args(categoryItem) && @annotation(ru.nullpointer.storefront.service.search.SearchIndexing)")
    public void after_deleteCategory(CatalogItem categoryItem) {
        searchLifecycle.deleteCatalogCategoryIndex(categoryItem.getId());
    }

    @AfterReturning("execution(* ru.nullpointer.storefront.service.CatalogService.updateSectionInfo(..)) && args(sectionItem) && @annotation(ru.nullpointer.storefront.service.search.SearchIndexing)")
    public void after_updateSectionInfo(CatalogItem sectionItem) {
        searchLifecycle.updateCatalogSectionIndex(sectionItem);
    }

    @AfterReturning("execution(* ru.nullpointer.storefront.service.CatalogService.updateCategoryInfo(..)) && args(categoryItem, category) && @annotation(ru.nullpointer.storefront.service.search.SearchIndexing)")
    public void after_updateCategoryInfo(CatalogItem categoryItem, Category category) {
        searchLifecycle.updateCatalogCategoryIndex(categoryItem);
    }

    @AfterReturning("execution(* ru.nullpointer.storefront.service.CatalogService.updateSectionTreeActive(..)) && args(sectionItem) && @annotation(ru.nullpointer.storefront.service.search.SearchIndexing)")
    public void after_updateSectionTreeActive(CatalogItem sectionItem) {
        searchLifecycle.updateCatalogSectionIndex(sectionItem);
    }

    @AfterReturning("execution(* ru.nullpointer.storefront.service.CatalogService.updateCategoryActive(..)) && args(categoryItem) && @annotation(ru.nullpointer.storefront.service.search.SearchIndexing)")
    public void after_updateCategoryActive(CatalogItem categoryItem) {
        searchLifecycle.updateCatalogCategoryIndex(categoryItem);
    }

    @AfterReturning("execution(* ru.nullpointer.storefront.service.CatalogService.changeParentItem(..)) && args(item, sectionId) && @annotation(ru.nullpointer.storefront.service.search.SearchIndexing)")
    public void after_changeParentItem(CatalogItem item, Integer sectionId) {
        if (CatalogItem.Type.SECTION.equals(item.getType())) {
            searchLifecycle.updateCatalogSectionIndex(item);
        } else if (CatalogItem.Type.CATEGORY.equals(item.getType())) {
            searchLifecycle.updateCatalogCategoryIndex(item);
        }
    }

    @AfterReturning("execution(* ru.nullpointer.storefront.service.OfferService.storeOffer(..)) && args(offer, paramValueMap) && @annotation(ru.nullpointer.storefront.service.search.SearchIndexing)")
    public void after_storeOffer(Offer offer, Map<Integer, Object> paramValueMap) {
        searchLifecycle.addCatalogOfferIndex(offer);
        searchLifecycle.addCompanyOfferIndex(offer);
    }

    @AfterReturning("execution(* ru.nullpointer.storefront.service.OfferService.updateOfferInfo(..)) && args(offer, paramValueMap) && @annotation(ru.nullpointer.storefront.service.search.SearchIndexing)")
    public void after_updateOfferInfo(Offer offer, Map<Integer, Object> paramValueMap) {
        searchLifecycle.updateCatalogOfferIndex(offer);
        searchLifecycle.updateCompanyOfferIndex(offer);
    }

    @AfterReturning("execution(* ru.nullpointer.storefront.service.OfferService.approveOffer(..)) && args(offerId) && @annotation(ru.nullpointer.storefront.service.search.SearchIndexing)")
    public void after_approveOffer(Integer offerId) {
        Offer offer = offerDAO.getOfferById(offerId);
        if (offer == null) {
            return;
        }
        searchLifecycle.updateCatalogOfferIndex(offer);
    }

    @AfterReturning("execution(* ru.nullpointer.storefront.service.OfferService.rejectOffer(..)) && args(offerId, rejectionList) && @annotation(ru.nullpointer.storefront.service.search.SearchIndexing)")
    public void after_rejectOffer(Integer offerId, List<Offer.Rejection> rejectionList) {
        Offer offer = offerDAO.getOfferById(offerId);
        if (offer == null) {
            return;
        }
        searchLifecycle.updateCatalogOfferIndex(offer);
    }

        @AfterReturning("execution(* ru.nullpointer.storefront.service.CompanyService.updateCompanyInfo(..)) && args(company) && @annotation(ru.nullpointer.storefront.service.search.SearchIndexing)")
    public void after_updateCompanyInfo(Company company) {
        searchLifecycle.updateCatalogCompanyOfferIndex(company);
    }

    @AfterReturning("execution(* ru.nullpointer.storefront.service.CompanyService.setCompanyDeliveryRegionList(..)) && args(regionIdList) && @annotation(ru.nullpointer.storefront.service.search.SearchIndexing)")
    public void after_setCompanyDeliveryRegionList(List<Integer> regionIdList) {
        Company company = securityService.getAuthenticatedCompany();
        searchLifecycle.updateCatalogCompanyOfferIndex(company);
    }

    @AfterReturning("execution(* ru.nullpointer.storefront.service.CompanyService.deleteCompanyDeliveryRegion(..)) && args(region) && @annotation(ru.nullpointer.storefront.service.search.SearchIndexing)")
    public void after_deleteCompanyDeliveryRegion(Region region) {
        Company company = securityService.getAuthenticatedCompany();
        searchLifecycle.updateCatalogCompanyOfferIndex(company);
    }

    @AfterReturning("execution(* ru.nullpointer.storefront.service.OfferService.deleteOffer(..)) && args(offer) && @annotation(ru.nullpointer.storefront.service.search.SearchIndexing)")
    public void after_deleteOffer(Offer offer) {
        searchLifecycle.deleteCatalogOfferIndex(offer);
        searchLifecycle.deleteCompanyOfferIndex(offer);
    }

    @AfterReturning("execution(* ru.nullpointer.storefront.service.ModelService.storeModel(..)) && args(model, paramValueMap) && @annotation(ru.nullpointer.storefront.service.search.SearchIndexing)")
    public void after_storeModel(Model model, Map<Integer, Object> paramValueMap) {
        searchLifecycle.addCatalogModelIndex(model);
    }

    @AfterReturning("execution(* ru.nullpointer.storefront.service.ModelService.updateModelInfo(..)) && args(model, paramValueMap) && @annotation(ru.nullpointer.storefront.service.search.SearchIndexing)")
    public void after_updateModelInfo(Model model, Map<Integer, Object> paramValueMap) {
        searchLifecycle.updateCatalogModelIndex(model);
    }

    @AfterReturning("execution(* ru.nullpointer.storefront.service.ModelService.deleteModel(..)) && args(model) && @annotation(ru.nullpointer.storefront.service.search.SearchIndexing)")
    public void after_deleteModel(Model model) {
        searchLifecycle.deleteCatalogModelIndex(model);
    }

    @AfterReturning("execution(* ru.nullpointer.storefront.service.BrandService.storeBrand(..)) && args(brand) && @annotation(ru.nullpointer.storefront.service.search.SearchIndexing)")
    public void after_storeBrand(Brand brand) {
        searchLifecycle.addCatalogBrandIndex(brand);
    }

    @AfterReturning("execution(* ru.nullpointer.storefront.service.BrandService.updateBrandInfo(..)) && args(brand) && @annotation(ru.nullpointer.storefront.service.search.SearchIndexing)")
    public void after_updateBrandInfo(Brand brand) {
        searchLifecycle.updateCatalogBrandIndex(brand);
    }

    @AfterReturning("execution(* ru.nullpointer.storefront.service.BrandService.deleteBrand(..)) && args(brand) && @annotation(ru.nullpointer.storefront.service.search.SearchIndexing)")
    public void after_deleteBrand(Brand brand) {
        searchLifecycle.deleteCatalogBrandIndex(brand);
    }
}
