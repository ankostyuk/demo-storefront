package ru.nullpointer.storefront.service.search;

import java.io.File;
import java.io.IOException;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.ru.RussianAnalyzer;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.nullpointer.storefront.config.LuceneConfig;

/**
 * @author ankostyuk
 */
// TODO INDEX TRANSACTION?
public class Indexer {

    private Logger logger = LoggerFactory.getLogger(Indexer.class);
    //

    @Resource
    private LuceneConfig luceneConfig;

    private Directory indexDir;
    
    private IndexWriter indexWriter;

    public void init(File catalog) throws CorruptIndexException, IOException {
        // TODO other FSDirectory?
        indexDir = new SimpleFSDirectory(catalog, null);

        boolean create = !IndexReader.indexExists(indexDir);

        indexWriter = new IndexWriter(indexDir, buildIndexAnalyzer(), create, IndexWriter.MaxFieldLength.UNLIMITED);

        if (create) {
            logger.info("Поисковый индекс не существует. Создан пустой поисковый индекс.");
        } else {
            logger.info("Поисковый индекс существует, документов: {}", getIndexDocCount());
        }
    }

    @PreDestroy
    public void clean() throws CorruptIndexException, IOException {
        indexWriter.close();
    }

    /**
     * Возвращает конфигурацию Lucene.
     */
    public LuceneConfig getLuceneConfig() {
        return luceneConfig;
    }

    /**
     * Возвращает директорию индекса.
     */
    public Directory getIndexDir() {
        return indexDir;
    }

    /**
     * Создает анализатор индекса.
     */
    public Analyzer buildIndexAnalyzer() {
        return new RussianAnalyzer(luceneConfig.getLuceneVersion());
    }

    /**
     * Проверяет наличие индекса.
     */
    public boolean indexExists() throws IOException {
        return IndexReader.indexExists(indexDir);
    }

    /**
     * Возвращает количество документов в индексе.
     */
    public int getIndexDocCount() throws IOException {
        return indexWriter.numDocs();
    }

    protected IndexWriter getIndexWriter() {
        return indexWriter;
    }

    protected void commit() throws CorruptIndexException, IOException {
        indexWriter.optimize();
        indexWriter.commit();
    }
}
// TODO org.apache.lucene.index.CheckIndex
// TODO IndexWriter.unlock(indexDir)
