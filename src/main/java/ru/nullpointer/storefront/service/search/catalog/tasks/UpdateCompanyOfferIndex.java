package ru.nullpointer.storefront.service.search.catalog.tasks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import ru.nullpointer.storefront.domain.Company;
import ru.nullpointer.storefront.service.search.IndexException;
import ru.nullpointer.storefront.service.search.catalog.CatalogIndexer;

/**
 * @author ankostyuk
 */
public class UpdateCompanyOfferIndex implements Runnable {

    private Logger logger = LoggerFactory.getLogger(UpdateCompanyOfferIndex.class);
    //

    private CatalogIndexer catalogIndexer;
    private Company company;

    public UpdateCompanyOfferIndex(CatalogIndexer catalogIndexer, Company company) {
        Assert.notNull(catalogIndexer);
        Assert.notNull(company);
        this.catalogIndexer = catalogIndexer;
        this.company = company;
    }

    @Override
    public void run() {
        logger.debug("start");
        try {
            catalogIndexer.updateCompanyOfferIndex(company);
        } catch (Exception e) {
            throw new IndexException("Исключение при обновлении поискового индекса каталога: ", e);
        }
        logger.debug("finish");
    }
}
