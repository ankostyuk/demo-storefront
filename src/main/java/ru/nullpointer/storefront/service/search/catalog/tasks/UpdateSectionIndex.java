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
public class UpdateSectionIndex implements Runnable {

    private Logger logger = LoggerFactory.getLogger(UpdateSectionIndex.class);
    //

    private CatalogIndexer catalogIndexer;
    private CatalogItem sectionItem;

    public UpdateSectionIndex(CatalogIndexer catalogIndexer, CatalogItem sectionItem) {
        Assert.notNull(catalogIndexer);
        Assert.notNull(sectionItem);
        this.catalogIndexer = catalogIndexer;
        this.sectionItem = sectionItem;
    }

    @Override
    public void run() {
        logger.debug("start");
        try {
            catalogIndexer.updateSectionIndex(sectionItem);
        } catch (Exception e) {
            throw new IndexException("Исключение при обновлении поискового индекса каталога: ", e);
        }
        logger.debug("finish");
    }
}
