package ru.nullpointer.storefront.service.search.company;

import java.io.IOException;
import javax.annotation.PostConstruct;

import javax.annotation.Resource;
import org.apache.lucene.document.Document;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.Term;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import ru.nullpointer.storefront.domain.Company;
import ru.nullpointer.storefront.domain.Offer;
import ru.nullpointer.storefront.domain.support.PageConfig;
import ru.nullpointer.storefront.domain.support.PaginatedQueryResult;

import ru.nullpointer.storefront.service.search.SearchUtils;
import ru.nullpointer.storefront.service.search.DataAccess;
import ru.nullpointer.storefront.service.search.DataAccessHelper;
import ru.nullpointer.storefront.service.search.IdentityFieldValue;
import ru.nullpointer.storefront.service.search.Indexer;

/**
 * @author ankostyuk
 */
@Component
public class CompanyIndexer extends Indexer {

    private Logger logger = LoggerFactory.getLogger(CompanyIndexer.class);
    //

    private static final Integer COMPANY_PAGE_SIZE = 100; // TODO в настройку?
    private static final Integer COMPANY_OFFER_PAGE_SIZE = 100; // TODO в настройку?

    @Resource
    private SearchUtils searchUtils;

    @Resource
    private DataAccess dataAccess;
    @Resource
    private DataAccessHelper dataAccessHelper;

    @PostConstruct
    public void initIndex() throws CorruptIndexException, IOException {
        init(getLuceneConfig().getLuceneIndexCompany());
    }

    /**
     * Создает индекс компаний. Индексирует:
     * товарные предложения.
     * 
     * @throws IOException
     * @throws CorruptIndexException
     */
    public synchronized void createIndex() throws CorruptIndexException, IOException {
        getIndexWriter().deleteAll();

        indexOffer();

        commit();
    }

    /**
     * Добавляет товарное предложение в индекс. Если товарное предложение c
     * <code>offer.getId()</code> существует в индексе, то обновляет индекс.
     *
     * @param offer
     *            товарное предложение
     * @throws IllegalArgumentException
     * @throws IOException
     * @throws CorruptIndexException
     */
    public synchronized void addOfferIndex(Offer offer) throws CorruptIndexException, IOException {
        Assert.notNull(offer);

        Term idTerm = searchUtils.buildOfferIdentityFieldValueTerm(offer.getId());
        getIndexWriter().updateDocument(idTerm, createOfferDoc(offer));

        commit();
    }

    /**
     * Обновляет товарное предложение в индексе.
     *
     * @param offer
     *            товарное предложение
     * @throws IllegalArgumentException
     * @throws IOException
     * @throws CorruptIndexException
     */
    public synchronized void updateOfferIndex(Offer offer) throws CorruptIndexException, IOException {
        Assert.notNull(offer);

        Term idTerm = searchUtils.buildOfferIdentityFieldValueTerm(offer.getId());

        getIndexWriter().updateDocument(idTerm, createOfferDoc(offer));

        commit();
    }

    /**
     * Удаляет товарное предложение из индекса.
     *
     * @param offer
     *            товарное предложение
     * @throws IOException
     * @throws CorruptIndexException
     */
    public synchronized void deleteOfferIndex(Offer offer) throws CorruptIndexException, IOException {
        Assert.notNull(offer);

        Term idTerm = searchUtils.buildOfferIdentityFieldValueTerm(offer.getId());
        getIndexWriter().deleteDocuments(idTerm);

        commit();
    }

    private void indexOffer() throws CorruptIndexException, IOException {
        PaginatedQueryResult<Company> companyResult = null;
        PageConfig companyPageConfig = new PageConfig(1, COMPANY_PAGE_SIZE);

        do {
            companyResult = dataAccess.getCompanyPaginatedList(companyPageConfig);

            for (Company company : companyResult.getList()) {
                indexCompanyOffer(company, true);
            }

            companyPageConfig = new PageConfig(companyResult.getPageNumber() + 1, companyResult.getPageSize());
        } while (companyPageConfig.getPageNumber() <= companyResult.getPageCount());
    }
    
    private void indexCompanyOffer(Company company, boolean create) throws CorruptIndexException, IOException {
        PaginatedQueryResult<Offer> offerResult = null;
        PageConfig offerPageConfig = new PageConfig(1, COMPANY_OFFER_PAGE_SIZE);

        do {
            offerResult = dataAccess.getCompanyOfferPaginatedList(company.getId(), offerPageConfig);

            for (Offer offer : offerResult.getList()) {
                if (create) {
                    getIndexWriter().addDocument(createOfferDoc(offer));
                } else {
                    Term idTerm = searchUtils.buildOfferIdentityFieldValueTerm(offer.getId());
                    getIndexWriter().updateDocument(idTerm, createOfferDoc(offer));
                }
            }

            offerPageConfig = new PageConfig(offerResult.getPageNumber() + 1, offerResult.getPageSize());
        } while (offerPageConfig.getPageNumber() <= offerResult.getPageCount());
    }

    private Document createOfferDoc(Offer offer) {
        Document doc = new Document();

        IdentityFieldValue identity = searchUtils.buildOfferIdentityFieldValue(offer.getId());

        doc.add(searchUtils.buildIdentityField(identity));
        doc.add(searchUtils.buildCompanyIdField(offer.getCompanyId()));
        doc.add(searchUtils.buildNameField(offer.getName()));

        doc.add(searchUtils.buildContentsField(offer.getName()));

        return doc;
    }
}
