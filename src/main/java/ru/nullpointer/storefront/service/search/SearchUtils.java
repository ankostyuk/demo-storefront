package ru.nullpointer.storefront.service.search;

import java.math.BigDecimal;
import java.util.Set;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanFilter;
import org.apache.lucene.search.FieldCacheRangeFilter;
import org.apache.lucene.search.FieldCacheTermsFilter;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.FilterClause;
import org.apache.lucene.search.QueryWrapperFilter;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TermQuery;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import ru.nullpointer.storefront.domain.Brand;
import ru.nullpointer.storefront.domain.CatalogItem;
import ru.nullpointer.storefront.domain.Model;
import ru.nullpointer.storefront.domain.Offer;
import ru.nullpointer.storefront.domain.Region;
import ru.nullpointer.storefront.domain.support.CompanyRegion;
import ru.nullpointer.storefront.service.OfferService;

/**
 * @author ankostyuk
 */
// TODO Assert?
@Component
public class SearchUtils {

    /*
     * Поле идентификации
     */
    public static final String FIELD_IDENTITY = "identity";
    
    /*
     * Поле идентификации "родительского" элемента
     */
    public static final String FIELD_PARENT_IDENTITY = "parent";

    /*
     * Поле имени
     */
    public static final String FIELD_NAME = "name";
    
    /*
     * Поле порядка типа
     */
    public static final String FIELD_TYPE_ORDER = "type_order";

    /*
     * Поле элементов каталога
     */
    public static final String FIELD_CATALOG_ITEMS = "catalog_items";

    /*
     * Поле региона товарного предложения
     */
    public static final String FIELD_REGION = "region";

    /*
     * Поле минимальной цены
     */
    public static final String FIELD_PRICE_MIN = "price_min"; // TODO !

    /*
     * Поле максимальной цены
     */
    public static final String FIELD_PRICE_MAX = "price_max"; // TODO !

    /*
     * Поле идентификатора компании-поставщика
     */
    public static final String FIELD_COMPANY_ID = "company_id";

    /*
     * Основное поле документа для поиска
     */
    public static final String FIELD_CONTENTS = "contents"; // TODO ?

    public static final float FUZZY_QUERY_MIN_SIMILARITY_DEFAULT = 0.0F;
    public static final int FUZZY_QUERY_PREFIX_LENGTH_DEFAULT = 0;

    public static final Sort SORT_RELEVANCE = Sort.RELEVANCE; // TODO is works?
    public static final Sort SORT_INDEX_ORDER = Sort.INDEXORDER;

    public static final SortField SORT_FIELD_NAME = new SortField(FIELD_NAME, SortField.STRING);
    public static final Sort SORT_NAME = new Sort(SORT_FIELD_NAME);

    public static final SortField SORT_FIELD_TYPE_ORDER = new SortField(FIELD_TYPE_ORDER, SortField.INT);
    public static final Sort SORT_TYPE_ORDER = new Sort(SORT_FIELD_TYPE_ORDER);
    
    public static final SortField SORT_FIELD_PRICE_ASC = new SortField(FIELD_PRICE_MIN, SortField.DOUBLE, false);
    public static final Sort SORT_PRICE_ASC = new Sort(SORT_FIELD_PRICE_ASC);
    public static final SortField SORT_FIELD_PRICE_DESC = new SortField(FIELD_PRICE_MIN, SortField.DOUBLE, true); // TODO FIELD_PRICE_MAX ?
    public static final Sort SORT_PRICE_DESC = new Sort(SORT_FIELD_PRICE_DESC);

    public IdentityFieldValue buildSectionIdentityFieldValue(Integer sectionId) {
        return new IdentityFieldValue(Type.SECTION, sectionId);
    }

    public IdentityFieldValue buildCategoryIdentityFieldValue(Integer categoryId) {
        return new IdentityFieldValue(Type.CATEGORY, categoryId);
    }

    public Term buildSectionIdentityFieldValueTerm(Integer sectionId) {
        IdentityFieldValue identity = buildSectionIdentityFieldValue(sectionId);
        return new Term(FIELD_IDENTITY, identity.getValue());
    }

    public Term buildCategoryIdentityFieldValueTerm(Integer categoryId) {
        IdentityFieldValue identity = buildCategoryIdentityFieldValue(categoryId);
        return new Term(FIELD_IDENTITY, identity.getValue());
    }

    public IdentityFieldValue buildOfferIdentityFieldValue(Integer offerId) {
        // TODO OFFER_PARAM
        return new IdentityFieldValue(Type.OFFER, offerId);
    }

    public IdentityFieldValue buildModelIdentityFieldValue(Integer modelId) {
        return new IdentityFieldValue(Type.MODEL, modelId);
    }

    public IdentityFieldValue buildBrandIdentityFieldValue(Integer brandId) {
        return new IdentityFieldValue(Type.BRAND, brandId);
    }

    public IdentityFieldValue buildCompanyIdentityFieldValue(Integer companyId) {
        return new IdentityFieldValue(Type.COMPANY, companyId);
    }

    public Term buildOfferIdentityFieldValueTerm(Integer offerId) {
        IdentityFieldValue identity = buildOfferIdentityFieldValue(offerId);
        return new Term(FIELD_IDENTITY, identity.getValue());
    }

    public Term buildModelIdentityFieldValueTerm(Integer modelId) {
        IdentityFieldValue identity = buildModelIdentityFieldValue(modelId);
        return new Term(FIELD_IDENTITY, identity.getValue());
    }

    public Term buildBrandIdentityFieldValueTerm(Integer brandId) {
        IdentityFieldValue identity = buildBrandIdentityFieldValue(brandId);
        return new Term(FIELD_IDENTITY, identity.getValue());
    }

    public Term buildParentCategoryIdentityFieldValueTerm(Integer categoryId) {
        IdentityFieldValue identity = buildCategoryIdentityFieldValue(categoryId);
        return new Term(FIELD_PARENT_IDENTITY, identity.getValue());
    }

    public Field buildIdentityField(IdentityFieldValue identity) {
        return new Field(FIELD_IDENTITY, identity.getValue(),
                Field.Store.YES, Field.Index.NOT_ANALYZED);
    }

    public Field buildParentIdentityField(IdentityFieldValue identity) {
        return new Field(FIELD_PARENT_IDENTITY, identity.getValue(),
                Field.Store.YES, Field.Index.NOT_ANALYZED);
    }

    public Field buildNameField(String name) {
        return new Field(FIELD_NAME, name,
                Field.Store.YES, Field.Index.NOT_ANALYZED);
    }

    public Field buildSectionOrderField(CatalogItem sectionItem) {
        return buildTypeOrderField(Type.SECTION);
    }

    public Field buildCategoryOrderField(CatalogItem categoryItem) {
        return buildTypeOrderField(Type.CATEGORY);
    }

    public Field buildOfferOrderField(Offer offer) {
        // TODO OFFER_PARAM
        return buildTypeOrderField(Type.OFFER);
    }

    public Field buildModelOrderField(Model model) {
        return buildTypeOrderField(Type.MODEL);
    }

    public Field buildBrandOrderField(Brand brand) {
        return buildTypeOrderField(Type.BRAND);
    }

    public Field buildCatalogItemsField(Set<Integer> itemIdSet) {
        StringBuilder sb = new StringBuilder();

        for (Integer id : itemIdSet) {
            sb.append(" ").append(Integer.toString(id));
        }

        String ids = itemIdSet.isEmpty() ? "" : sb.substring(1);

        return new Field(FIELD_CATALOG_ITEMS, ids,
                Field.Store.YES, Field.Index.ANALYZED);
    }

    public Field buildOfferRegionField(Offer offer, CompanyRegion companyRegion) {
        return RegionFilter.buildOfferRegionField(FIELD_REGION, offer, companyRegion);
    }

    public Field buildModelRegionField(Model model) {
        return RegionFilter.buildModelRegionField(FIELD_REGION, model);
    }

    public Field buildPriceMinField(Double price) {
        return new Field(FIELD_PRICE_MIN, price.toString(),
                Field.Store.YES, Field.Index.NOT_ANALYZED);
    }

    public Field buildPriceMaxField(Double price) {
        return new Field(FIELD_PRICE_MAX, price.toString(),
                Field.Store.YES, Field.Index.NOT_ANALYZED);
    }

    public Field buildCompanyIdField(Integer companyId) {
        return new Field(FIELD_COMPANY_ID, companyId.toString(),
                Field.Store.YES, Field.Index.NOT_ANALYZED);
    }

    public Field buildContentsField(String contents) {
        return new Field(FIELD_CONTENTS, contents,
                Field.Store.YES, Field.Index.ANALYZED,
                Field.TermVector.WITH_POSITIONS_OFFSETS);
    }

    public boolean isSectionCanIndex(CatalogItem sectionItem) {
        Assert.notNull(sectionItem);

        if (!sectionItem.getActive()) {
            return false;
        }

        return true;
    }

    public boolean isCategoryCanIndex(CatalogItem categoryItem) {
        Assert.notNull(categoryItem);

        if (!categoryItem.getActive()) {
            return false;
        }

        return true;
    }

    public boolean isOfferCanIndex(Offer offer) {
        Assert.notNull(offer);

        if (offer.isModelLinked()
                || !OfferService.isOfferAccessible(offer)) {
            return false;
        }

        return true;
    }

    public boolean isModelCanIndex(Model model) {
        Assert.notNull(model);
        // stub
        return true;
    }

    public boolean isBrandCanIndex(Brand brand) {
        Assert.notNull(brand);
        // stub
        return true;
    }

    public boolean isSection(CatalogItem item) {
        Assert.notNull(item);
        return CatalogItem.Type.SECTION.equals(item.getType());
    }

    public boolean isCategory(CatalogItem item) {
        Assert.notNull(item);
        return CatalogItem.Type.CATEGORY.equals(item.getType());
    }

    static public Filter buildTypeFilter(Type type) {
        return new FieldCacheTermsFilter(FIELD_TYPE_ORDER, Integer.toString(Type.order(type)));
    }

    static public Filter buildTypeFilter(Type[] types) {
        String[] values = new String[types.length];
        for (int i = 0; i < types.length; i++) {
            values[i] = Integer.toString(Type.order(types[i]));
        }
        return new FieldCacheTermsFilter(FIELD_TYPE_ORDER, values);
    }

    public Filter buildCatalogItemFilter(Integer itemId) {
        Assert.notNull(itemId);
        return new QueryWrapperFilter(new TermQuery(new Term(FIELD_CATALOG_ITEMS, Integer.toString(itemId))));
    }

    public Filter buildRegionFilter(Region region) {
        return new RegionFilter(FIELD_REGION, region);
    }

    public Filter buildPriceFilter(BigDecimal min, BigDecimal max) {
        Assert.isTrue(min != null || max != null);

        if (min == null) {
            return FieldCacheRangeFilter.newDoubleRange(FIELD_PRICE_MIN, null, max.doubleValue(), true, true);
        } else
        if (max == null) {
            return FieldCacheRangeFilter.newDoubleRange(FIELD_PRICE_MAX, min.doubleValue(), null, true, true);
        } else {
            BooleanFilter filter = new BooleanFilter();
            FieldCacheRangeFilter<Double> minFilter = FieldCacheRangeFilter.newDoubleRange(FIELD_PRICE_MAX, min.doubleValue(), null, true, true);
            FieldCacheRangeFilter<Double> maxFilter = FieldCacheRangeFilter.newDoubleRange(FIELD_PRICE_MIN, null, max.doubleValue(), true, true);
            filter.add(new FilterClause(minFilter, Occur.MUST));
            filter.add(new FilterClause(maxFilter, Occur.MUST));

            return filter;
        }

        //return null;
        /*
        //Assert.notNull(minPrice);
        //Assert.notNull(maxPrice);

        BooleanFilter filter = new BooleanFilter();
        FieldCacheRangeFilter<Double> minFilter = FieldCacheRangeFilter.newDoubleRange(FIELD_PRICE_MAX, min.doubleValue(), null, true, true);
        FieldCacheRangeFilter<Double> maxFilter = FieldCacheRangeFilter.newDoubleRange(FIELD_PRICE_MIN, null, max.doubleValue(), true, true);
        filter.add(new FilterClause(minFilter, Occur.MUST));
        filter.add(new FilterClause(maxFilter, Occur.MUST));

        return filter;
        */
    }

    public Filter buildCompanyIdFilter(Integer companyId) {
        Assert.notNull(companyId);
        return new FieldCacheTermsFilter(FIELD_COMPANY_ID, companyId.toString());
    }

    public SortField buildRegionMatchSortField(Region region, boolean matchFirst) {
        return new SortField(FIELD_REGION, new RegionMatchFieldComparatorSource(region), matchFirst);
    }

    private Field buildTypeOrderField(Type type) {
        return new Field(FIELD_TYPE_ORDER, Integer.toString(Type.order(type)),
                Field.Store.YES, Field.Index.NOT_ANALYZED);
    }
}
