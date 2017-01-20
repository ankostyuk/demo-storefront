package ru.nullpointer.storefront.service.search.catalog.tasks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import ru.nullpointer.storefront.service.search.IndexException;
import ru.nullpointer.storefront.service.search.catalog.CatalogIndexer;

/**
 * @author ankostyuk
 */
public class CreateCatalogIndex implements Runnable {

    private Logger logger = LoggerFactory.getLogger(CreateCatalogIndex.class);
    //

    private CatalogIndexer catalogIndexer;

    public CreateCatalogIndex(CatalogIndexer catalogIndexer) {
        Assert.notNull(catalogIndexer);
        this.catalogIndexer = catalogIndexer;
    }

    @Override
    public void run() {
        logger.debug("start");
        try {
            catalogIndexer.createIndex();
        } catch (Exception e) {
            throw new IndexException("Исключение при создании поискового индекса каталога: ", e);
        }
        logger.debug("finish");
    }
}
