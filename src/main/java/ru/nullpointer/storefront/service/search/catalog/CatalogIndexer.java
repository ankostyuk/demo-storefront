package ru.nullpointer.storefront.service.search.catalog;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.Term;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import ru.nullpointer.storefront.domain.Brand;
import ru.nullpointer.storefront.domain.CatalogItem;
import ru.nullpointer.storefront.domain.Category;
import ru.nullpointer.storefront.domain.Company;
import ru.nullpointer.storefront.domain.Model;
import ru.nullpointer.storefront.domain.Offer;
import ru.nullpointer.storefront.domain.support.CompanyRegion;
import ru.nullpointer.storefront.domain.support.PageConfig;
import ru.nullpointer.storefront.domain.support.PaginatedQueryResult;
import ru.nullpointer.storefront.service.search.SearchUtils;
import ru.nullpointer.storefront.service.search.DataAccess;
import ru.nullpointer.storefront.service.search.DataAccessHelper;
import ru.nullpointer.storefront.service.search.IdentityFieldValue;
import ru.nullpointer.storefront.service.search.Indexer;

/**
 * @author ankostyuk
 */
// TODO обновление индекса при трансформации товарного предложения в модель (и обратно:) )
@Component
public class CatalogIndexer extends Indexer {

    private Logger logger = LoggerFactory.getLogger(CatalogIndexer.class);
    //
    private static final Integer CATEGORY_MODEL_PAGE_SIZE = 100;
    private static final Integer COMPANY_PAGE_SIZE = 100;
    private static final Integer COMPANY_OFFER_PAGE_SIZE = 100;
    private static final Integer CATEGORY_OFFER_PAGE_SIZE = 100;
    @Resource
    private SearchUtils searchUtils;
    @Resource
    private DataAccess dataAccess;
    @Resource
    private DataAccessHelper dataAccessHelper;

    @PostConstruct
    public void initIndex() throws CorruptIndexException, IOException {
        init(getLuceneConfig().getLuceneIndexCatalog());
    }

    /**
     * Создает индекс каталога. Индексирует: 
     * разделы и категории каталога,
     * товарные предложения,
     * модели,
     * бренды.
     * 
     * @throws IOException
     * @throws CorruptIndexException
     */
    public synchronized void createIndex() throws CorruptIndexException, IOException {
        getIndexWriter().deleteAll();

        indexCatalog();
        indexOffer(true);
        indexModel();
        indexBrand();

        commit();
    }

    /**
     * Добавляет раздел каталога в индекс. Если раздел каталога c
     * <code>sectionItem.getId()</code> существует в индексе, то обновляет индекс.
     * 
     * @param sectionItem
     *            раздел каталога
     * @throws IllegalArgumentException
     * @throws IOException
     * @throws CorruptIndexException
     */
    public synchronized void addSectionIndex(CatalogItem sectionItem) throws CorruptIndexException, IOException {
        Assert.notNull(sectionItem);

        if (!searchUtils.isSectionCanIndex(sectionItem)) {
            // TODO delete?
            return;
        }

        Term idTerm = searchUtils.buildSectionIdentityFieldValueTerm(sectionItem.getId());
        getIndexWriter().updateDocument(idTerm, createSectionDoc(sectionItem,
                dataAccessHelper.getCatalogItemPathIdSet(sectionItem.getId(), false)));

        commit();
    }

    /**
     * Добавляет категорию каталога в индекс. Если категория каталога c
     * <code>categoryItem.getId()</code> существует в индексе, то обновляет индекс.
     *
     * @param categoryItem
     *            категория каталога
     * @throws IllegalArgumentException
     * @throws IOException
     * @throws CorruptIndexException
     */
    public synchronized void addCategoryIndex(CatalogItem categoryItem) throws CorruptIndexException, IOException {
        Assert.notNull(categoryItem);

        if (!searchUtils.isCategoryCanIndex(categoryItem)) {
            // TODO delete?
            return;
        }

        Term idTerm = searchUtils.buildCategoryIdentityFieldValueTerm(categoryItem.getId());
        getIndexWriter().updateDocument(idTerm, createCategoryDoc(categoryItem,
                dataAccessHelper.getCatalogItemPathIdSet(categoryItem.getId(), false)));

        commit();
    }

    /**
     * Удаляет раздел каталога из индекса.
     * 
     * @param sectionId
     *            идентификатор раздела каталога
     * @throws IllegalArgumentException
     *             если <code>sectionId</code> равен <code>null</code>
     * @throws IOException
     * @throws CorruptIndexException
     */
    public synchronized void deleteSectionIndex(Integer sectionId) throws CorruptIndexException, IOException {
        Assert.notNull(sectionId);

        Term idTerm = searchUtils.buildSectionIdentityFieldValueTerm(sectionId);
        getIndexWriter().deleteDocuments(idTerm);

        commit();
    }

    /**
     * Удаляет категорию каталога из индекса.
     *
     * @param categoryId
     *            идентификатор категории каталога
     * @throws IllegalArgumentException
     *             если <code>categoryId</code> равен <code>null</code>
     * @throws IOException
     * @throws CorruptIndexException
     */
    public synchronized void deleteCategoryIndex(Integer categoryId) throws CorruptIndexException, IOException {
        Assert.notNull(categoryId);

        Term idTerm = searchUtils.buildCategoryIdentityFieldValueTerm(categoryId);
        getIndexWriter().deleteDocuments(idTerm);

        commit();
    }

    /**
     * Обновляет раздел каталога в индексе.
     * 
     * @param item
     *            раздел каталога
     * @throws IllegalArgumentException
     * @throws IOException
     * @throws CorruptIndexException
     */
    public synchronized void updateSectionIndex(CatalogItem sectionItem) throws CorruptIndexException, IOException {
        Assert.notNull(sectionItem);

        updateSection(sectionItem);

        commit();
    }

    /**
     * Обновляет категорию каталога в индексе.
     *
     * @param categoryItem
     *            категория каталога
     * @throws IllegalArgumentException
     * @throws IOException
     * @throws CorruptIndexException
     */
    public synchronized void updateCategoryIndex(CatalogItem categoryItem) throws CorruptIndexException, IOException {
        Assert.notNull(categoryItem);

        updateCategory(categoryItem);

        commit();
    }

    /**
     * Добавляет товарное предложение в индекс. Если товарное предложение c
     * <code>offer.getId()</code> существует в индексе, то обновляет индекс.
     *
     * @param offer
     *            товарное предложение
     * @throws IllegalArgumentException
     * @throws IOException
     * @throws CorruptIndexException
     */
    public synchronized void addOfferIndex(Offer offer) throws CorruptIndexException, IOException {
        Assert.notNull(offer);

        if (!searchUtils.isOfferCanIndex(offer)) {
            // TODO delete?
            return;
        }

        createOfferIndex(offer);

        commit();
    }

    /**
     * Обновляет товарное предложение в индексе.
     *
     * @param offer
     *            товарное предложение
     * @throws IllegalArgumentException
     * @throws IOException
     * @throws CorruptIndexException
     */
    public synchronized void updateOfferIndex(Offer offer) throws CorruptIndexException, IOException {
        Assert.notNull(offer);

        if (searchUtils.isOfferCanIndex(offer)) {
            createOfferIndex(offer);
        } else {
            removeOfferIndex(offer);
        }

        commit();
    }

    /**
     * Обновляет товарные предложения компании в индексе.
     *
     * @param company
     *            компания
     * @throws IllegalArgumentException
     * @throws IOException
     * @throws CorruptIndexException
     */
    public synchronized void updateCompanyOfferIndex(Company company) throws CorruptIndexException, IOException {
        // TODO только при изменении необходимых полей (регион, ...)
        Assert.notNull(company);

        Map<Integer, List<CatalogItem>> categoryPathListMap = dataAccess.getCategoryPathMap();
        indexCompanyOffer(company, categoryPathListMap, false);

        commit();
    }

    /**
     * Удаляет товарное предложение из индекса.
     *
     * @param offer
     *            товарное предложение
     * @throws IOException
     * @throws CorruptIndexException
     */
    public synchronized void deleteOfferIndex(Offer offer) throws CorruptIndexException, IOException {
        Assert.notNull(offer);

        removeOfferIndex(offer);

        commit();
    }

    /**
     * Добавляет модель в индекс. Если модель c
     * <code>model.getId()</code> существует в индексе, то обновляет индекс.
     *
     * @param model
     *            модель
     * @throws IllegalArgumentException
     * @throws IOException
     * @throws CorruptIndexException
     */
    public synchronized void addModelIndex(Model model) throws CorruptIndexException, IOException {
        Assert.notNull(model);

        if (!searchUtils.isModelCanIndex(model)) {
            // TODO delete?
            return;
        }

        Term idTerm = searchUtils.buildModelIdentityFieldValueTerm(model.getId());
        createModelIndex(model, idTerm);

        commit();
    }

    /**
     * Обновляет модель в индексе.
     *
     * @param model
     *            модель
     * @throws IllegalArgumentException
     * @throws IOException
     * @throws CorruptIndexException
     */
    public synchronized void updateModelIndex(Model model) throws CorruptIndexException, IOException {
        Assert.notNull(model);

        Term idTerm = searchUtils.buildModelIdentityFieldValueTerm(model.getId());

        if (searchUtils.isModelCanIndex(model)) {
            createModelIndex(model, idTerm);
        } else {
            getIndexWriter().deleteDocuments(idTerm);
        }

        commit();
    }

    /**
     * Удаляет модель из индекса.
     *
     * @param model
     *            модель
     * @throws IOException
     * @throws CorruptIndexException
     */
    public synchronized void deleteModelIndex(Model model) throws CorruptIndexException, IOException {
        Assert.notNull(model);

        Term idTerm = searchUtils.buildModelIdentityFieldValueTerm(model.getId());
        getIndexWriter().deleteDocuments(idTerm);

        commit();
    }

    /**
     * Добавляет бренд в индекс. Если бренд c
     * <code>brand.getId()</code> существует в индексе, то обновляет индекс.
     *
     * @param brand
     *            бренд
     * @throws IllegalArgumentException
     * @throws IOException
     * @throws CorruptIndexException
     */
    public synchronized void addBrandIndex(Brand brand) throws CorruptIndexException, IOException {
        Assert.notNull(brand);

        if (!searchUtils.isBrandCanIndex(brand)) {
            // TODO delete?
            return;
        }

        Term idTerm = searchUtils.buildBrandIdentityFieldValueTerm(brand.getId());
        getIndexWriter().updateDocument(idTerm, createBrandDoc(brand));

        commit();
    }

    /**
     * Обновляет бренд в индексе.
     *
     * @param brand
     *            бренд
     * @throws IllegalArgumentException
     * @throws IOException
     * @throws CorruptIndexException
     */
    public synchronized void updateBrandIndex(Brand brand) throws CorruptIndexException, IOException {
        Assert.notNull(brand);

        Term idTerm = searchUtils.buildBrandIdentityFieldValueTerm(brand.getId());

        if (searchUtils.isBrandCanIndex(brand)) {
            getIndexWriter().updateDocument(idTerm, createBrandDoc(brand));
        } else {
            getIndexWriter().deleteDocuments(idTerm);
        }

        commit();
    }

    /**
     * Удаляет бренд из индекса.
     *
     * @param brand
     *            бренд
     * @throws IOException
     * @throws CorruptIndexException
     */
    public synchronized void deleteBrandIndex(Brand brand) throws CorruptIndexException, IOException {
        Assert.notNull(brand);

        Term idTerm = searchUtils.buildBrandIdentityFieldValueTerm(brand.getId());
        getIndexWriter().deleteDocuments(idTerm);

        commit();
    }

    // TODO clear
    private void indexCatalog() throws CorruptIndexException, IOException {
        List<CatalogItem> items = dataAccess.getCatalogItemTree(null);

        for (CatalogItem item : items) {
            Set<Integer> pathIdSetShort = dataAccessHelper.getCatalogItemPathIdSet(item.getId(), false);

            if (searchUtils.isSection(item) && searchUtils.isSectionCanIndex(item)) {
                getIndexWriter().addDocument(createSectionDoc(item, pathIdSetShort));
            } else if (searchUtils.isCategory(item) && searchUtils.isCategoryCanIndex(item)) {
                getIndexWriter().addDocument(createCategoryDoc(item, pathIdSetShort));
            }
        }
    }

    private void indexOffer(boolean create) throws CorruptIndexException, IOException {
        Map<Integer, List<CatalogItem>> categoryPathMap = dataAccess.getCategoryPathMap();

        PaginatedQueryResult<Company> companyResult = null;
        PageConfig companyPageConfig = new PageConfig(1, COMPANY_PAGE_SIZE);

        do {
            companyResult = dataAccess.getCompanyPaginatedList(companyPageConfig);

            for (Company company : companyResult) {
                indexCompanyOffer(company, categoryPathMap, create);
            }

            companyPageConfig = new PageConfig(companyResult.getPageNumber() + 1, companyResult.getPageSize());
        } while (companyPageConfig.getPageNumber() <= companyResult.getPageCount());
    }

    private void indexCompanyOffer(Company company, Map<Integer, List<CatalogItem>> categoryPathListMap,
            boolean create) throws CorruptIndexException, IOException {
        CompanyRegion companyRegion = dataAccess.getCompanyRegion(company);

        PaginatedQueryResult<Offer> offerResult = null;
        PageConfig offerPageConfig = new PageConfig(1, COMPANY_OFFER_PAGE_SIZE);

        do {
            offerResult = dataAccess.getCompanyOfferPaginatedList(company.getId(), offerPageConfig);

            for (Offer offer : offerResult) {
                boolean canIndex = searchUtils.isOfferCanIndex(offer);
                if (create) {
                    if (canIndex) {
                        getIndexWriter().addDocument(createOfferDoc(offer, categoryPathListMap.get(offer.getCategoryId()), companyRegion));
                    }
                } else {
                    Term idTerm = searchUtils.buildOfferIdentityFieldValueTerm(offer.getId());
                    if (canIndex) {
                        getIndexWriter().updateDocument(idTerm, createOfferDoc(offer, categoryPathListMap.get(offer.getCategoryId()), companyRegion));
                    } else {
                        getIndexWriter().deleteDocuments(idTerm);
                    }
                    updateOfferModelIndex(offer);
                }
            }

            offerPageConfig = new PageConfig(offerResult.getPageNumber() + 1, offerResult.getPageSize());
        } while (offerPageConfig.getPageNumber() <= offerResult.getPageCount());
    }

    private void updateSection(CatalogItem sectionItem) throws CorruptIndexException, IOException {
        List<CatalogItem> items = dataAccess.getCatalogItemTree(sectionItem.getId());
        items.add(sectionItem);
        for (CatalogItem item : items) {
            if (searchUtils.isSection(item)) {
                Term idTerm = searchUtils.buildSectionIdentityFieldValueTerm(item.getId());
                if (searchUtils.isSectionCanIndex(item)) {
                    getIndexWriter().updateDocument(idTerm, createSectionDoc(item,
                            dataAccessHelper.getCatalogItemPathIdSet(item.getId(), false)));
                } else {
                    getIndexWriter().deleteDocuments(idTerm);
                }
            } else if (searchUtils.isCategory(item)) {
                updateCategory(item);
            }
        }
    }

    private void updateCategory(CatalogItem categoryItem) throws CorruptIndexException, IOException {
        Term idTerm = searchUtils.buildCategoryIdentityFieldValueTerm(categoryItem.getId());

        if (searchUtils.isCategoryCanIndex(categoryItem)) {
            getIndexWriter().updateDocument(idTerm, createCategoryDoc(categoryItem,
                    dataAccessHelper.getCatalogItemPathIdSet(categoryItem.getId(), false)));
        } else {
            getIndexWriter().deleteDocuments(idTerm);
        }

        updateCategoryOfferIndex(categoryItem);
        updateCategoryModelIndex(categoryItem);
    }

    private void updateCategoryOfferIndex(CatalogItem categoryItem) throws CorruptIndexException, IOException {
        List<CatalogItem> categoryPathList = dataAccessHelper.getCatalogItemPath(categoryItem.getId(), true);

        PaginatedQueryResult<Offer> offerResult = null;
        PageConfig offerPageConfig = new PageConfig(1, CATEGORY_OFFER_PAGE_SIZE);

        do {
            offerResult = dataAccess.getCategoryOfferPaginatedList(categoryItem.getId(), offerPageConfig);

            Set<Integer> companyIdSet = new HashSet<Integer>();
            for (Offer o : offerResult) {
                companyIdSet.add(o.getCompanyId());
            }
            Map<Integer, CompanyRegion> companyRegionMap = dataAccess.getCompanyRegionMap(companyIdSet);

            for (Offer offer : offerResult) {
                Term idTerm = searchUtils.buildOfferIdentityFieldValueTerm(offer.getId());
                if (searchUtils.isOfferCanIndex(offer)) {
                    getIndexWriter().updateDocument(idTerm, createOfferDoc(offer, categoryPathList, companyRegionMap.get(offer.getCompanyId())));
                } else {
                    getIndexWriter().deleteDocuments(idTerm);
                }
            }

            offerPageConfig = new PageConfig(offerResult.getPageNumber() + 1, offerResult.getPageSize());
        } while (offerPageConfig.getPageNumber() <= offerResult.getPageCount());
    }

    private void updateCategoryModelIndex(CatalogItem categoryItem) throws CorruptIndexException, IOException {
        indexCategoryModel(categoryItem.getId(), false);
    }

    private Document createSectionDoc(CatalogItem sectionItem, Set<Integer> catalogItemIdSet) {
        Document doc = new Document();

        IdentityFieldValue identity = searchUtils.buildSectionIdentityFieldValue(sectionItem.getId());

        doc.add(searchUtils.buildIdentityField(identity));
        doc.add(searchUtils.buildNameField(sectionItem.getName()));
        doc.add(searchUtils.buildSectionOrderField(sectionItem));

        if (!catalogItemIdSet.isEmpty()) {
            doc.add(searchUtils.buildCatalogItemsField(catalogItemIdSet));
        }

        doc.add(searchUtils.buildContentsField(sectionItem.getName()));

        return doc;
    }

    private Document createCategoryDoc(CatalogItem categoryItem, Set<Integer> catalogItemIdSet) {
        Document doc = new Document();

        IdentityFieldValue identity = searchUtils.buildCategoryIdentityFieldValue(categoryItem.getId());

        doc.add(searchUtils.buildIdentityField(identity));
        doc.add(searchUtils.buildNameField(categoryItem.getName()));
        doc.add(searchUtils.buildCategoryOrderField(categoryItem));

        if (!catalogItemIdSet.isEmpty()) {
            doc.add(searchUtils.buildCatalogItemsField(catalogItemIdSet));
        }

        doc.add(searchUtils.buildContentsField(categoryItem.getName()));

        return doc;
    }

    private void createOfferIndex(Offer offer) throws CorruptIndexException, IOException {
        Company company = dataAccess.getCompanyById(offer.getCompanyId());
        CompanyRegion companyRegion = dataAccess.getCompanyRegion(company);
        Term idTerm = searchUtils.buildOfferIdentityFieldValueTerm(offer.getId());
        getIndexWriter().updateDocument(idTerm, createOfferDoc(offer,
                dataAccessHelper.getCatalogItemPath(offer.getCategoryId(), true), companyRegion));

        updateOfferModelIndex(offer);
    }

    private void removeOfferIndex(Offer offer) throws CorruptIndexException, IOException {
        Term idTerm = searchUtils.buildOfferIdentityFieldValueTerm(offer.getId());
        getIndexWriter().deleteDocuments(idTerm);

        updateOfferModelIndex(offer);
    }

    private void updateOfferModelIndex(Offer offer) throws CorruptIndexException, IOException {
        if (offer.isModelLinked()) {
            Model model = dataAccess.getModelById(offer.getModelId());
            if (searchUtils.isModelCanIndex(model)) {
                Term modelIdTerm = searchUtils.buildModelIdentityFieldValueTerm(model.getId());
                createModelIndex(model, modelIdTerm);
            }
        }
    }

    private void createModelIndex(Model model, Term idTerm) throws CorruptIndexException, IOException {
        getIndexWriter().updateDocument(idTerm, createModelDoc(model,
                dataAccessHelper.getCatalogItemPath(model.getCategoryId(), true)));
    }

    private Document createOfferDoc(Offer offer, List<CatalogItem> categoryPathList, CompanyRegion companyRegion) {
        Document doc = new Document();

        IdentityFieldValue identity = searchUtils.buildOfferIdentityFieldValue(offer.getId());
        IdentityFieldValue parent = searchUtils.buildCompanyIdentityFieldValue(offer.getCompanyId()); // TODO использовать?

        doc.add(searchUtils.buildIdentityField(identity));
        doc.add(searchUtils.buildParentIdentityField(parent));
        doc.add(searchUtils.buildNameField(offer.getName()));
        doc.add(searchUtils.buildOfferOrderField(offer));

        if (!categoryPathList.isEmpty()) {
            doc.add(searchUtils.buildCatalogItemsField(dataAccessHelper.catalogItemListToIdSet(categoryPathList)));
        }

        doc.add(searchUtils.buildOfferRegionField(offer, companyRegion));

        //
        StringBuilder contents = new StringBuilder(offer.getName());

        for (CatalogItem catalogItem : categoryPathList) {
            contents.append(" ")//
                    .append(catalogItem.getName());
        }

        if (StringUtils.isNotBlank(offer.getBrandName())) {
            contents.append(" ")//
                    .append(offer.getBrandName());
        }
        if (offer.getBrandId() != null) {
            Brand brand = dataAccess.getBrandById(offer.getBrandId());
            if (brand != null) {
                contents.append(" ")//
                        .append(brand.getName());
                if (StringUtils.isNotBlank(brand.getKeywords())) {
                    contents.append(" ")//
                            .append(brand.getKeywords());
                }
            }
        }

        doc.add(searchUtils.buildContentsField(contents.toString()));
        //

        return doc;
    }

    private Document createModelDoc(Model model, List<CatalogItem> categoryPathList) {
        Document doc = new Document();

        IdentityFieldValue identity = searchUtils.buildModelIdentityFieldValue(model.getId());
        IdentityFieldValue parent = searchUtils.buildCategoryIdentityFieldValue(model.getCategoryId());

        doc.add(searchUtils.buildIdentityField(identity));
        doc.add(searchUtils.buildParentIdentityField(parent));
        doc.add(searchUtils.buildNameField(model.getName()));
        doc.add(searchUtils.buildModelOrderField(model));

        if (!categoryPathList.isEmpty()) {
            doc.add(searchUtils.buildCatalogItemsField(dataAccessHelper.catalogItemListToIdSet(categoryPathList)));
        }

        doc.add(searchUtils.buildModelRegionField(model));

        //
        StringBuilder contents = new StringBuilder();

        contents.append(model.getName())//
                .append(" ")//
                .append(model.getVendorCode());

        if (StringUtils.isNotBlank(model.getKeywords())) {
            contents.append(" ")//
                    .append(model.getKeywords());
        }
        
        for (CatalogItem catalogItem : categoryPathList) {
            contents.append(" ")//
                    .append(catalogItem.getName());
        }

        Brand brand = dataAccess.getBrandById(model.getBrandId());
        if (brand != null) {
            contents.append(" ")//
                    .append(brand.getName());
            if (StringUtils.isNotBlank(brand.getKeywords())) {
                contents.append(" ")//
                        .append(brand.getKeywords());
            }
        }

        doc.add(searchUtils.buildContentsField(contents.toString()));
        //

        return doc;
    }

    private void indexModel() throws CorruptIndexException, IOException {
        List<Category> categories = dataAccess.getCategoryList();

        for (Category category : categories) {
            indexCategoryModel(category.getId(), true);
        }
    }

    private void indexCategoryModel(Integer categoryId, boolean create) throws CorruptIndexException, IOException {
        List<CatalogItem> catalogItemPath = dataAccessHelper.getCatalogItemPath(categoryId, true);

        PaginatedQueryResult<Model> result = null;
        PageConfig pageConfig = new PageConfig(1, CATEGORY_MODEL_PAGE_SIZE);

        do {
            result = dataAccess.getCategoryModelPaginatedList(categoryId, pageConfig);

            for (Model model : result) {
                boolean canIndex = searchUtils.isModelCanIndex(model);
                if (create) {
                    if (canIndex) {
                        getIndexWriter().addDocument(createModelDoc(model, catalogItemPath));
                    }
                } else {
                    Term idTerm = searchUtils.buildModelIdentityFieldValueTerm(model.getId());
                    if (canIndex) {
                        getIndexWriter().updateDocument(idTerm, createModelDoc(model, catalogItemPath));
                    } else {
                        getIndexWriter().deleteDocuments(idTerm);
                    }
                }
            }

            pageConfig = new PageConfig(result.getPageNumber() + 1, result.getPageSize());
        } while (pageConfig.getPageNumber() <= result.getPageCount());
    }

    private void indexBrand() throws CorruptIndexException, IOException {
        List<Brand> brands = dataAccess.getBrandList();

        for (Brand brand : brands) {
            if (searchUtils.isBrandCanIndex(brand)) {
                getIndexWriter().addDocument(createBrandDoc(brand));
            }
        }
    }

    private Document createBrandDoc(Brand brand) {
        Document doc = new Document();

        IdentityFieldValue identity = searchUtils.buildBrandIdentityFieldValue(brand.getId());

        doc.add(searchUtils.buildIdentityField(identity));
        doc.add(searchUtils.buildNameField(brand.getName()));
        doc.add(searchUtils.buildBrandOrderField(brand));

        StringBuilder contents = new StringBuilder();

        contents.append(brand.getName());
        if (brand.getKeywords() != null) {
            contents.append(" ")//
                    .append(brand.getKeywords());
        }
        doc.add(searchUtils.buildContentsField(contents.toString()));

        return doc;
    }
}
// TODO BOOSTING (разделы, категории, товарные предложения, модели, ...)
// TODO SORT

