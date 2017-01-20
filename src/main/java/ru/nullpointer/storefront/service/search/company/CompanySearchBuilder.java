package ru.nullpointer.storefront.service.search.company;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.stereotype.Component;
import ru.nullpointer.storefront.service.search.SearchBuilder;

/**
 * @author ankostyuk
 */
@Component
public class CompanySearchBuilder extends SearchBuilder {

    @Resource
    private CompanySearcher companySearcher;

    @PostConstruct
    public void initBuilder() {
        init(companySearcher);
    }
}
