package ru.nullpointer.storefront.service.search.catalog.tasks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import ru.nullpointer.storefront.domain.CatalogItem;
import ru.nullpointer.storefront.service.search.IndexException;
import ru.nullpointer.storefront.service.search.catalog.CatalogIndexer;

/**
 * @author ankostyuk
 */
public class UpdateCategoryIndex implements Runnable {

    private Logger logger = LoggerFactory.getLogger(UpdateCategoryIndex.class);
    //

    private CatalogIndexer catalogIndexer;
    private CatalogItem categoryItem;

    public UpdateCategoryIndex(CatalogIndexer catalogIndexer, CatalogItem categoryItem) {
        Assert.notNull(catalogIndexer);
        Assert.notNull(categoryItem);
        this.catalogIndexer = catalogIndexer;
        this.categoryItem = categoryItem;
    }

    @Override
    public void run() {
        logger.debug("start");
        try {
            catalogIndexer.updateCategoryIndex(categoryItem);
        } catch (Exception e) {
            throw new IndexException("Исключение при обновлении поискового индекса каталога: ", e);
        }
        logger.debug("finish");
    }
}
