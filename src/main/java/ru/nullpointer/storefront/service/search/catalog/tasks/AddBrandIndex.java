package ru.nullpointer.storefront.service.search.catalog.tasks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import ru.nullpointer.storefront.domain.Brand;
import ru.nullpointer.storefront.service.search.IndexException;

import ru.nullpointer.storefront.service.search.catalog.CatalogIndexer;

/**
 * @author ankostyuk
 */
public class AddBrandIndex implements Runnable {

    private Logger logger = LoggerFactory.getLogger(AddBrandIndex.class);
    //

    private CatalogIndexer catalogIndexer;
    private Brand brand;

    public AddBrandIndex(CatalogIndexer catalogIndexer, Brand brand) {
        Assert.notNull(catalogIndexer);
        Assert.notNull(brand);
        this.catalogIndexer = catalogIndexer;
        this.brand = brand;
    }

    @Override
    public void run() {
        logger.debug("start", brand);
        try {
            catalogIndexer.addBrandIndex(brand);
        } catch (Exception e) {
            throw new IndexException("Исключение при добавлении бренда в поисковый индекс каталога: ", e);
        }
        logger.debug("finish");
    }
}
