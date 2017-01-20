package ru.nullpointer.storefront.service.search.catalog.tasks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import ru.nullpointer.storefront.domain.Model;
import ru.nullpointer.storefront.service.search.IndexException;

import ru.nullpointer.storefront.service.search.catalog.CatalogIndexer;

/**
 * @author ankostyuk
 */
public class AddModelIndex implements Runnable {

    private Logger logger = LoggerFactory.getLogger(AddModelIndex.class);
    //

    private CatalogIndexer catalogIndexer;
    private Model model;

    public AddModelIndex(CatalogIndexer catalogIndexer, Model model) {
        Assert.notNull(catalogIndexer);
        Assert.notNull(model);
        this.catalogIndexer = catalogIndexer;
        this.model = model;
    }

    @Override
    public void run() {
        logger.debug("start", model);
        try {
            catalogIndexer.addModelIndex(model);
        } catch (Exception e) {
            throw new IndexException("Исключение при добавлении модели в поисковый индекс каталога: ", e);
        }
        logger.debug("finish");
    }
}
