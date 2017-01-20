package ru.nullpointer.storefront.service.search.catalog;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.stereotype.Component;
import ru.nullpointer.storefront.service.search.SearchBuilder;

/**
 * @author ankostyuk
 */
@Component
public class CatalogSearchBuilder extends SearchBuilder {

    @Resource
    private CatalogSearcher catalogSearcher;

    @PostConstruct
    public void initBuilder() {
        init(catalogSearcher);
    }
}
