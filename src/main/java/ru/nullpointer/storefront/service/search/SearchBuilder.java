package ru.nullpointer.storefront.service.search;

import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;
import org.apache.commons.lang.StringUtils;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.Sort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import ru.nullpointer.storefront.domain.support.PageConfig;
import ru.nullpointer.storefront.domain.support.PaginatedQueryResult;

/**
 * @author ankostyuk
 */
public class SearchBuilder {

    private Logger logger = LoggerFactory.getLogger(SearchBuilder.class);
    //

    private static final int CORRECT_SEARCH_TEXT_RESULT_COUNT = 10;

    @Resource
    private SearchHighlighter searchHighlighter;

    private Searcher searcher;

    public void init(Searcher searcher) {
        this.searcher = searcher;
    }

    /**
     * Возвращает "актуальный" пример для поиска (или
     * <code>null</code> при пустом индексе или при исключении)
     */
    public String getActualSearchExample() {
        // TODO popular? other?
        try {
            Document doc = searcher.getRandomDoc();
            if (doc != null) {
                return doc.get(SearchUtils.FIELD_NAME);
            }
        } catch (Exception e) {
            logger.error("{}", e);
        }
        return null;
    }

    /**
     * Предлагает результаты поиска по текстовой части.
     * 
     * @param text
     *            текстовая часть запроса на поиск
     * @param resultCount
     *            максимальное количество предлагаемых результатов
     * @param sort
     *            сортировка
     * @param filter
     *            фильтр, может равняться <code>null</code>
     * @return список результатов или пустой список,
     *         отсутствует предложение или при исключении.
     * @throws IllegalArgumentException
     *             <code>resultCount</code> меньше <code>1</code>,
     *             <code>sort</code> равен <code>null</code>
     */
    // TODO добавить результаты исправления опечаток и ошибок?
    public List<Document> suggestSearchByText(String text, int resultCount, Sort sort, Filter filter) {
        Assert.isTrue(resultCount > 0);
        Assert.notNull(sort);

        if (StringUtils.isBlank(text)) {
            return Collections.emptyList();
        }

        try {
            SearchConfig searchConfig = new SearchConfig(resultCount, null, sort, filter);
            SearchResult<Document> result = searcher.deepSearchContents(text, searchConfig);
            return result.getList();
        } catch (Exception e) {
            logger.error("{}", e);
        }

        return Collections.emptyList();
    }

    /**
     * Постранично возвращает результаты поиска по текстовой части.
     * Навигация по страницам осуществляется среди количества результатов, ограниченных <code>resultCount</code>.
     *
     * @param text
     *            текстовая часть запроса на поиск
     * @param pageConfig
     *            настройки постраничного вывода
     * @param resultCount
     *            максимальное количество результатов
     * @param sort
     *            сортировка
     * @param filter
     *            фильтр, может равняться <code>null</code>
     * @return список результатов или пустой список,
     *         если отсутствуют результаты или при исключении.
     * @throws IllegalArgumentException
     *             <code>pageConfig</code> равен <code>null</code>,
     *             <code>resultCount</code> меньше <code>1</code>,
     *             <code>sort</code> равен <code>null</code>
     */
    public PaginatedQueryResult<Document> searchPaginatedByText(
            String text, PageConfig pageConfig, int resultCount, Sort sort, Filter filter) {
        Assert.notNull(pageConfig);
        Assert.isTrue(resultCount > 0);
        Assert.notNull(sort);

        if (StringUtils.isBlank(text)) {
            return new PaginatedQueryResult<Document>(pageConfig, Collections.<Document>emptyList(), 0);
        }

        List<Document> docs = null;
        int total = 0;

        try {
            SearchConfig searchConfig = new SearchConfig(resultCount, pageConfig, sort, filter);
            SearchResult<Document> result = searcher.deepSearchContents(text, searchConfig);
            docs = result.getList();
            total = result.getTotal();
        } catch (Exception e) {
            logger.error("{}", e);
            docs = Collections.emptyList();
            total = 0;
        }

        return new PaginatedQueryResult<Document>(pageConfig, docs, total);
    }

    /**
     * Предлагает скорректированный текст для поиска
     *
     * @param text
     *            скорректированная текстовая часть запроса на поиск,
     *            или <code>null</code> если не нашлось вариантов для корректировки.
     * @param filter
     *            фильтр, может равняться <code>null</code>
     * @return текстовая часть запроса на поиск
     */
    public String suggestCorrectSearchText(String text, Filter filter) {
        if (StringUtils.isBlank(text)) {
            return null;
        }

        try {
            SearchConfig searchConfig = new SearchConfig(
                    CORRECT_SEARCH_TEXT_RESULT_COUNT, null, SearchUtils.SORT_RELEVANCE, filter);

            SearchResult<Document> result = searcher.fuzzySearchContents(text, searchConfig);

            List<Document> docs = result.getList();

            if (!docs.isEmpty()) {
                StringBuilder contents = new StringBuilder(docs.get(0).get(SearchUtils.FIELD_CONTENTS));
                for (int i = 1; i < docs.size(); i++) {
                    contents.append(" ").append(docs.get(i).get(SearchUtils.FIELD_CONTENTS));
                }

                List<Fragment> fragments = searchHighlighter.getBestFragments(
                        text,
                        SearchUtils.FIELD_CONTENTS, contents.toString(),
                        result.getQuery(), result.getAnalyzer());

                if ( !fragments.isEmpty() ) {
                    Collections.sort(fragments, new FragmentScoreComparator());
                    return fragments.get(fragments.size() - 1).getText();
                }
            }
        } catch (Exception e) {
            logger.error("{}", e);
        }

        return null;
    }
}
// TODO поиск по нескольким аттрибутам (полям). Объединение нескольких
// аттрибутов в "FIELD_CONTENTS_NAME"?
// TODO хранение запросов на поиск в БД, индексе?
