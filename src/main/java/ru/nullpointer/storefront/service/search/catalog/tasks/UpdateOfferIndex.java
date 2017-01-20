package ru.nullpointer.storefront.service.search.catalog.tasks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import ru.nullpointer.storefront.domain.Offer;
import ru.nullpointer.storefront.service.search.IndexException;
import ru.nullpointer.storefront.service.search.catalog.CatalogIndexer;

/**
 * @author ankostyuk
 */
public class UpdateOfferIndex implements Runnable {

    private Logger logger = LoggerFactory.getLogger(UpdateOfferIndex.class);
    //

    private CatalogIndexer catalogIndexer;
    private Offer offer;

    public UpdateOfferIndex(CatalogIndexer catalogIndexer, Offer offer) {
        Assert.notNull(catalogIndexer);
        Assert.notNull(offer);
        this.catalogIndexer = catalogIndexer;
        this.offer = offer;
    }

    @Override
    public void run() {
        logger.debug("start");
        try {
            catalogIndexer.updateOfferIndex(offer);
        } catch (Exception e) {
            throw new IndexException("Исключение при обновлении поискового индекса каталога: ", e);
        }
        logger.debug("finish");
    }
}
