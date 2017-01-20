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
public class AddCompanyOfferIndex implements Runnable {

    private Logger logger = LoggerFactory.getLogger(AddCompanyOfferIndex.class);
    //

    private CompanyIndexer companyIndexer;
    private Offer offer;

    public AddCompanyOfferIndex(CompanyIndexer companyIndexer, Offer offer) {
        Assert.notNull(companyIndexer);
        Assert.notNull(offer);
        this.companyIndexer = companyIndexer;
        this.offer = offer;
    }

    @Override
    public void run() {
        logger.debug("start", offer);
        try {
            companyIndexer.addOfferIndex(offer);
        } catch (Exception e) {
            throw new IndexException("Исключение при добавлении товарного предложения в поисковый индекс компаний: ", e);
        }
        logger.debug("finish");
    }
}
