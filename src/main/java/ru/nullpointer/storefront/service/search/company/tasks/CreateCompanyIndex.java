package ru.nullpointer.storefront.service.search.company.tasks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import ru.nullpointer.storefront.service.search.IndexException;
import ru.nullpointer.storefront.service.search.company.CompanyIndexer;

/**
 * @author ankostyuk
 */
public class CreateCompanyIndex implements Runnable {

    private Logger logger = LoggerFactory.getLogger(CreateCompanyIndex.class);
    //

    private CompanyIndexer companyIndexer;

    public CreateCompanyIndex(CompanyIndexer companyIndexer) {
        Assert.notNull(companyIndexer);
        this.companyIndexer = companyIndexer;
    }

    @Override
    public void run() {
        logger.debug("start");
        try {
            companyIndexer.createIndex();
        } catch (Exception e) {
            throw new IndexException("Исключение при создании поискового индекса компаний: ", e);
        }
        logger.debug("finish");
    }
}
