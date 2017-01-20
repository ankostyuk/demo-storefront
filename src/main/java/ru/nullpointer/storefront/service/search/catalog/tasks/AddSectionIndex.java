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
public class AddSectionIndex implements Runnable {

    private Logger logger = LoggerFactory.getLogger(AddSectionIndex.class);
    //

    private CatalogIndexer catalogIndexer;
    private CatalogItem sectionItem;

    public AddSectionIndex(CatalogIndexer catalogIndexer, CatalogItem sectionItem) {
        Assert.notNull(catalogIndexer);
        Assert.notNull(sectionItem);
        this.catalogIndexer = catalogIndexer;
        this.sectionItem = sectionItem;
    }

    @Override
    public void run() {
        logger.debug("start", sectionItem);
        try {
            catalogIndexer.addSectionIndex(sectionItem);
        } catch (Exception e) {
            throw new IndexException("Исключение при добавлении раздела каталога в поисковый индекс каталога: ", e);
        }
        logger.debug("finish");
    }
}
