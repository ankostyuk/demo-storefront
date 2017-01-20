package ru.nullpointer.storefront.service.search.company;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import ru.nullpointer.storefront.domain.Offer;
import ru.nullpointer.storefront.service.search.IndexException;

/**
 * @author ankostyuk
 */
public class DeleteCompanyOfferIndex implements Runnable {

    private Logger logger = LoggerFactory.getLogger(DeleteCompanyOfferIndex.class);
    //

    private CompanyIndexer companyIndexer;
    private Offer offer;

    public DeleteCompanyOfferIndex(CompanyIndexer companyIndexer, Offer offer) {
        Assert.notNull(companyIndexer);
        Assert.notNull(offer);
        this.companyIndexer = companyIndexer;
        this.offer = offer;
    }

    @Override
    public void run() {
        logger.debug("start");
        try {
            companyIndexer.deleteOfferIndex(offer);
        } catch (Exception e) {
            throw new IndexException("Исключение при удалении товарного предложения из поискового индекса каталога: ", e);
        }
        logger.debug("finish");
    }
}
