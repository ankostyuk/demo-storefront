package ru.nullpointer.storefront.service.search.catalog.tasks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import ru.nullpointer.storefront.service.search.IndexException;

import ru.nullpointer.storefront.service.search.catalog.CatalogIndexer;

/**
 * @author ankostyuk
 */
public class DeleteCategoryIndex implements Runnable {

    private Logger logger = LoggerFactory.getLogger(DeleteCategoryIndex.class);
    //

    private CatalogIndexer catalogIndexer;
    private Integer categoryId;

    public DeleteCategoryIndex(CatalogIndexer catalogIndexer, Integer categoryId) {
        Assert.notNull(catalogIndexer);
        Assert.notNull(categoryId);
        this.catalogIndexer = catalogIndexer;
        this.categoryId = categoryId;
    }

    @Override
    public void run() {
        logger.debug("start");
        try {
            catalogIndexer.deleteCategoryIndex(categoryId);
        } catch (Exception e) {
            throw new IndexException("Исключение при удалении категории каталога из поискового индекса каталога: ", e);
        }
        logger.debug("finish");
    }
}
