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
public class AddCategoryIndex implements Runnable {

    private Logger logger = LoggerFactory.getLogger(AddCategoryIndex.class);
    //

    private CatalogIndexer catalogIndexer;
    private CatalogItem categoryItem;

    public AddCategoryIndex(CatalogIndexer catalogIndexer, CatalogItem categoryItem) {
        Assert.notNull(catalogIndexer);
        Assert.notNull(categoryItem);
        this.catalogIndexer = catalogIndexer;
        this.categoryItem = categoryItem;
    }

    @Override
    public void run() {
        logger.debug("start", categoryItem);
        try {
            catalogIndexer.addCategoryIndex(categoryItem);
        } catch (Exception e) {
            throw new IndexException("Исключение при добавлении категории каталога в поисковый индекс каталога: ", e);
        }
        logger.debug("finish");
    }
}
