package ru.nullpointer.storefront.web.secured.manager.catalog.structure;

import javax.annotation.Resource;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import ru.nullpointer.storefront.domain.CatalogItem;
import ru.nullpointer.storefront.domain.Category;
import ru.nullpointer.storefront.web.ui.catalog.CategoryProperties;
import ru.nullpointer.storefront.web.ui.catalog.ItemProperties;
import ru.nullpointer.storefront.web.ui.catalog.SectionProperties;
import ru.nullpointer.storefront.service.CatalogService;

/**
 * @author ankostyuk
 */
@Component
public class CatalogStructureHelper {

    @Resource
    private MessageSource messageSource;
    @Resource
    private CatalogService catalogService;

    private void setItemProperties(ItemProperties itemProperties, CatalogItem item, boolean all) {
        itemProperties.setItem(item);
        if (!all) {
            return;
        }

        CatalogItem parentItem = catalogService.getParentItem(item.getId());
        itemProperties.setParentItemId(parentItem != null ? parentItem.getId() : null);

        CatalogItem afterItem = catalogService.getAfterItem(item);
        itemProperties.setAfterItemId(afterItem != null ? afterItem.getId() : null);

        itemProperties.setCanChoose(false);

        itemProperties.setOfferCount(null); // TODO Количество товарных
        // предложений в категории (включая
        // подкатегории)
        // itemProperties.setOfferCount(offerService.getCategoryOfferCount(item.getId()));

        itemProperties.setLevel(catalogService.getItemLevel(item));
        itemProperties.setCanDelete(catalogService.canDeleteItem(item));
        itemProperties.setCanActive(catalogService.canActivateItem(item));
    }

    public ItemProperties buildItemProperties(CatalogItem item, boolean all) {
        Assert.notNull(item);

        // ItemProperties itemProperties = new ItemProperties();
        // setItemProperties(itemProperties, item, all);

        ItemProperties itemProperties = null;

        if (catalogService.isSectionItem(item)) {
            itemProperties = buildSectionProperties(item, all);
        } else if (catalogService.isCategoryItem(item)) {
            itemProperties = buildCategoryProperties(item, all);
        }

        return itemProperties;
    }

    public SectionProperties buildSectionProperties(CatalogItem sectionItem, boolean all) {
        Assert.notNull(sectionItem);
        Assert.isTrue(catalogService.isSectionItem(sectionItem));

        SectionProperties sectionProperties = new SectionProperties();
        setItemProperties(sectionProperties, sectionItem, all);

        return sectionProperties;
    }

    public SectionProperties buildNewSectionProperties() {
        CatalogItem sectionItem = new CatalogItem();
        sectionItem.setType(CatalogItem.Type.SECTION);
        sectionItem.setActive(false); // from parent?

        SectionProperties sectionProperties = new SectionProperties();
        setItemProperties(sectionProperties, sectionItem, false);

        sectionProperties.setCanActive(false);

        return sectionProperties;
    }

    public CategoryProperties buildCategoryProperties(CatalogItem categoryItem, boolean all) {
        Assert.notNull(categoryItem);
        Assert.isTrue(catalogService.isCategoryItem(categoryItem));

        CategoryProperties categoryProperties = new CategoryProperties();
        setItemProperties(categoryProperties, categoryItem, all);

        Category category = catalogService.getCategoryById(categoryItem.getId());
        categoryProperties.setCategory(category);

        categoryProperties.setParamCategory(category.getParameterSetDescriptorId() != null);

        return categoryProperties;
    }

    public CategoryProperties buildNewCategoryProperties() {
        CatalogItem sectionItem = new CatalogItem();
        sectionItem.setType(CatalogItem.Type.CATEGORY);
        sectionItem.setActive(false); // from parent?

        CategoryProperties categoryProperties = new CategoryProperties();
        setItemProperties(categoryProperties, sectionItem, false);

        categoryProperties.setCanActive(false);

        Category category = new Category();
        categoryProperties.setCategory(category);

        categoryProperties.setParamCategory(false);

        return categoryProperties;
    }

    public ItemProperties buildCatalogRootItemProperties() {
        ItemProperties itemProperties = new ItemProperties();

        CatalogItem item = new CatalogItem();
        item.setId(null);
        item.setName(messageSource.getMessage("ui.catalog.structure.catalog.root.displayName", null, null));
        item.setType(null);
        item.setActive(true);

        itemProperties.setItem(item);
        itemProperties.setLevel(1);
        
        return itemProperties;
    }

    public ItemProperties buildChildrenLastItemProperties() {
        ItemProperties itemProperties = new ItemProperties();

        CatalogItem item = new CatalogItem();
        item.setId(null);
        item.setName(messageSource.getMessage("ui.catalog.structure.item.children.last.displayName", null, null));
        item.setType(null);
        item.setActive(true);

        itemProperties.setItem(item);

        return itemProperties;
    }
}

// TODO fucking IE: tree (<ul><li><float div>), <select><option disabled="..." >
