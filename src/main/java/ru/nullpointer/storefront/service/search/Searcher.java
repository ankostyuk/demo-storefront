package ru.nullpointer.storefront.service.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.WildcardQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.nullpointer.storefront.config.LuceneConfig;
import ru.nullpointer.storefront.domain.support.PageConfig;

/**
 * @author ankostyuk
 */
// TODO contents -> ?
// TODO исправление ошибок раскладки клавиатуры (латиница -> кириллица)
// TODO thread search (lucene in action)?
public class Searcher {

    private Logger logger = LoggerFactory.getLogger(Searcher.class);
    //

    private static final String SPACE_CHARACTERS_REGEXP_PATTERN =  "[^_0-9a-zа-я]";

    @Resource
    private LuceneConfig luceneConfig;

    private Indexer indexer;

    private IndexSearcher indexSearcher;

    public void init(Indexer indexer) throws IOException {
        this.indexer = indexer;
        indexSearcher = new IndexSearcher(indexer.getIndexDir());
    }

    @PreDestroy
    public void clean() throws IOException {
        indexSearcher.close();
    }

    /**
     * Находит документы по содержимому, удовлетворяющие строке lucene-запроса.
     * 
     * @param queryString
     *            строка запроса
     * @param searchConfig
     *            настройки поиска
     * @return Список найденных документов
     * @throws CorruptIndexException
     * @throws IOException
     * @throws ParseException
     */
    public SearchResult<Document> searchContents(String queryString, SearchConfig searchConfig) throws CorruptIndexException, IOException, ParseException {
        IndexSearcher searcher = getActualIndexSearcher();
        Analyzer analyzer = getAnalyzer();
        QueryParser queryParser = getQueryParser(analyzer);
        Query query = queryParser.parse(queryString);
        return searchByQuery(query, searchConfig, searcher, analyzer);
    }

    /**
     * Находит документы по содержимому, удовлетворяющему строке текста.
     * Используется "глубокий" поиск.
     * 
     * @param text
     *            строка текста
     * @param searchConfig
     *            настройки поиска
     * @return Список найденных документов
     * @throws CorruptIndexException
     * @throws IOException
     * @throws ParseException
     */
    // TODO добавить "нечеткий" поиск?
    public SearchResult<Document> deepSearchContents(String text, SearchConfig searchConfig) throws CorruptIndexException, IOException, ParseException {
        text = normalizeSearchText(text);
        if (text.isEmpty()) {
            return new SearchResult<Document>(Collections.<Document>emptyList(), 0, null, null);
        }

        IndexSearcher searcher = getActualIndexSearcher();

        // TODO поиск с префиксом сначала? Или после? В зависимости от
        // применения?
        String queryString = new StringBuilder("+")
                .append(text.replaceAll("\\s+", " +"))
                .append("*")
                .toString();

        Analyzer analyzer = getAnalyzer();
        QueryParser queryParser = getQueryParser(analyzer);

        SearchResult<Document> result = searchByQuery(queryParser.parse(queryString), searchConfig, searcher, analyzer);

        if (result.getList().isEmpty()) {
            queryString = queryString.substring(0, queryString.length() - 1);
            result = searchByQuery(queryParser.parse(queryString), searchConfig, searcher, analyzer);
        }

        return result;
    }

    /**
     * Находит документы по содержимому, удовлетворяющему строке текста.
     * Используется "нечеткий" поиск.
     * 
     * @param text
     *            строка текста
     * @param searchConfig
     *            настройки поиска
     * @return Список найденных документов
     * @throws CorruptIndexException
     * @throws IOException
     * @throws ParseException
     */
    public SearchResult<Document> fuzzySearchContents(String text, SearchConfig searchConfig) throws CorruptIndexException, IOException, ParseException {
        text = normalizeSearchText(text);
        if (text.isEmpty()) {
            return new SearchResult<Document>(Collections.<Document>emptyList(), 0, null, null);
        }

        IndexSearcher searcher = getActualIndexSearcher();

        String similarity = new StringBuilder("~").append(SearchUtils.FUZZY_QUERY_MIN_SIMILARITY_DEFAULT).toString();
        String queryString = new StringBuilder("+")
                .append(text.replaceAll("\\s+", new StringBuilder(similarity).append(" +").toString()))
                .append(similarity)
                .toString();

        Analyzer analyzer = getAnalyzer();
        QueryParser queryParser = getQueryParser(analyzer);

        return searchByQuery(queryParser.parse(queryString), searchConfig, searcher, analyzer);
    }

    /**
     * Находит документы по шаблону <code>*[text]*</code> без учета регистра.
     * 
     * @param text
     *            строка
     * @param searchConfig
     *            настройки поиска
     * @return Список найденных документов
     * @throws CorruptIndexException
     * @throws IOException
     * @throws ParseException
     */
    public SearchResult<Document> templateSearchContents(String text, SearchConfig searchConfig) throws CorruptIndexException, IOException, ParseException {
        IndexSearcher searcher = getActualIndexSearcher();
        Query query = new WildcardQuery(new Term(SearchUtils.FIELD_CONTENTS, "*" + text.toLowerCase() + "*")); // TODO case?
        Analyzer analyzer = getAnalyzer();
        return searchByQuery(query, searchConfig, searcher, analyzer);
    }

    /**
     * Возвращает "случайный" документ.
     * 
     * @throws CorruptIndexException
     * @throws IOException
     */
    public Document getRandomDoc() throws CorruptIndexException, IOException {
        IndexSearcher searcher = getActualIndexSearcher();
        IndexReader reader = searcher.getIndexReader();
        int numDocs = reader.numDocs();
        if (numDocs <= 0) {
            return null;
        }
        return reader.document((int) (Math.random() * numDocs));
    }

    private SearchResult<Document> searchByQuery(Query query, SearchConfig searchConfig, IndexSearcher searcher, Analyzer analyzer) throws CorruptIndexException, IOException {
        TopDocs topDocs = searcher.search(query, searchConfig.getFilter(), searchConfig.getResultCount(), searchConfig.getSort());

        PageConfig pageConfig = searchConfig.getPageConfig();

        List<Document> docs = new ArrayList<Document>(pageConfig == null ? topDocs.scoreDocs.length : pageConfig.getPageSize());

        if (pageConfig == null) {
            for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
                docs.add(searcher.doc(scoreDoc.doc));
            }
        } else {
            int begin = (pageConfig.getPageNumber() - 1) * pageConfig.getPageSize();
            int end = pageConfig.getPageNumber() * pageConfig.getPageSize();

            end = (end < topDocs.scoreDocs.length ? end : topDocs.scoreDocs.length);

            for (int i = begin; i < end; i++) {
                ScoreDoc scoreDoc = topDocs.scoreDocs[i];
                docs.add(searcher.doc(scoreDoc.doc));
            }
        }

        return new SearchResult<Document>(docs, topDocs.scoreDocs.length, query, analyzer);
    }

    private String normalizeSearchText(String text) {
        return text.toLowerCase()
                .replaceAll(SPACE_CHARACTERS_REGEXP_PATTERN, " ")
                .replaceAll("^\\s+|\\s+$", "");
    }

    private Analyzer getAnalyzer() {
        return indexer.buildIndexAnalyzer();
    }

    private QueryParser getQueryParser(Analyzer analyzer) {
        return new QueryParser(luceneConfig.getLuceneVersion(), SearchUtils.FIELD_CONTENTS, analyzer);
    }

    private IndexSearcher getActualIndexSearcher() throws CorruptIndexException, IOException {
        // TODO more or another performance optimization?
        if (!indexSearcher.getIndexReader().isCurrent()) {
            indexSearcher.close();
            indexSearcher = new IndexSearcher(indexSearcher.getIndexReader().directory());
        }
        return indexSearcher;
    }
}
