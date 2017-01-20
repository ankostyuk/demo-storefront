package ru.nullpointer.storefront.config;

import java.io.File;

import org.apache.lucene.util.Version;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * @author ankostyuk
 */
@Component
public class LuceneConfig implements InitializingBean {

    private Version luceneVersion = Version.LUCENE_30;

    @Value("${lucene.index.catalog.path}")
    private String luceneIndexCatalogPath;
    @Value("${lucene.index.company.path}")
    private String luceneIndexCompanyPath;

    public Version getLuceneVersion() {
        return luceneVersion;
    }

    public File getLuceneIndexCatalog() {
        return new File(luceneIndexCatalogPath);
    }

    public File getLuceneIndexCompany() {
        return new File(luceneIndexCompanyPath);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.hasText(luceneIndexCatalogPath, "Свойство 'luceneIndexCatalogPath' должно быть установлено");
        checkCatalog(luceneIndexCatalogPath);
        Assert.hasText(luceneIndexCompanyPath, "Свойство 'luceneIndexCompanyPath' должно быть установлено");
        checkCatalog(luceneIndexCompanyPath);
    }

    private void checkCatalog(String catalogPath) {
        File f = new File(catalogPath);
        String path = f.getAbsolutePath();
        Assert.isTrue(f.exists(), "Каталог с именем '" + path + "' не существует");
        Assert.isTrue(f.isDirectory(), "Файл '" + path + "' не является каталогом");
    }
}
