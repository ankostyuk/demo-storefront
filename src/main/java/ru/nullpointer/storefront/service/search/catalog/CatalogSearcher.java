package ru.nullpointer.storefront.service.search.catalog;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import ru.nullpointer.storefront.service.search.Searcher;

/**
 * @author ankostyuk
 */
@Component
public class CatalogSearcher extends Searcher {

    @Resource
    private CatalogIndexer catalogIndexer;

    @PostConstruct
    public void initSearcher() throws IOException {
        init(catalogIndexer);
    }
}
