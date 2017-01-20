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
public class DeleteBrandIndex implements Runnable {

    private Logger logger = LoggerFactory.getLogger(DeleteBrandIndex.class);
    //

    private CatalogIndexer catalogIndexer;
    private Brand brand;

    public DeleteBrandIndex(CatalogIndexer catalogIndexer, Brand brand) {
        Assert.notNull(catalogIndexer);
        Assert.notNull(brand);
        this.catalogIndexer = catalogIndexer;
        this.brand = brand;
    }

    @Override
    public void run() {
        logger.debug("start");
        try {
            catalogIndexer.deleteBrandIndex(brand);
        } catch (Exception e) {
            throw new IndexException("Исключение при удалении бренда из поискового индекса каталога: ", e);
        }
        logger.debug("finish");
    }
}
