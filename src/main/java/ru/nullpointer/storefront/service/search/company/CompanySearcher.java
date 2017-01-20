package ru.nullpointer.storefront.service.search.company;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import ru.nullpointer.storefront.service.search.Searcher;

/**
 * @author ankostyuk
 */
@Component
public class CompanySearcher extends Searcher {

    @Resource
    private CompanyIndexer companyIndexer;

    @PostConstruct
    public void initSearcher() throws IOException {
        init(companyIndexer);
    }
}
