package ru.nullpointer.storefront.service.search.company.tasks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import ru.nullpointer.storefront.domain.Offer;
import ru.nullpointer.storefront.service.search.IndexException;
import ru.nullpointer.storefront.service.search.company.CompanyIndexer;

/**
 * @author ankostyuk
 */
public class UpdateCompanyOfferIndex implements Runnable {

    private Logger logger = LoggerFactory.getLogger(UpdateCompanyOfferIndex.class);
    //

    private CompanyIndexer companyIndexer;
    private Offer offer;

    public UpdateCompanyOfferIndex(CompanyIndexer companyIndexer, Offer offer) {
        Assert.notNull(companyIndexer);
        Assert.notNull(offer);
        this.companyIndexer = companyIndexer;
        this.offer = offer;
    }

    @Override
    public void run() {
        logger.debug("start");
        try {
            companyIndexer.updateOfferIndex(offer);
        } catch (Exception e) {
            throw new IndexException("Исключение при обновлении поискового индекса компаний: ", e);
        }
        logger.debug("finish");
    }
}
