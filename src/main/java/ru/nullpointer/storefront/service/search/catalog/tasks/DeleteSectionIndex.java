package ru.nullpointer.storefront.service.search.catalog.tasks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import ru.nullpointer.storefront.service.search.IndexException;

import ru.nullpointer.storefront.service.search.catalog.CatalogIndexer;

/**
 * @author ankostyuk
 */
public class DeleteSectionIndex implements Runnable {

    private Logger logger = LoggerFactory.getLogger(DeleteSectionIndex.class);
    //

    private CatalogIndexer catalogIndexer;
    private Integer sectionId;

    public DeleteSectionIndex(CatalogIndexer catalogIndexer, Integer sectionId) {
        Assert.notNull(catalogIndexer);
        Assert.notNull(sectionId);
        this.catalogIndexer = catalogIndexer;
        this.sectionId = sectionId;
    }

    @Override
    public void run() {
        logger.debug("start");
        try {
            catalogIndexer.deleteSectionIndex(sectionId);
        } catch (Exception e) {
            throw new IndexException("Исключение при удалении раздела каталога из поискового индекса каталога: ", e);
        }
        logger.debug("finish");
    }
}
