package ru.nullpointer.storefront.service.search;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.List;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.highlight.Fragmenter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleSpanFragmenter;
import org.apache.lucene.search.highlight.TokenSources;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * @author ankostyuk
 */
// TODO scope?
@Component
public class SearchHighlighter {

    private Logger logger = LoggerFactory.getLogger(FragmentCollectorNullFormatter.class);
    //

    private static final float BEST_FRAGMENTS_COLLECTOR_FACTOR = 0.1F; // TODO ? 0.5F
    private static final float BEST_FRAGMENTS_PHRASE_SCORE = 1.1F;

    public List<Fragment> getBestFragments(
            String text,
            String fieldName, String fieldContents, 
            Query query, Analyzer analyzer) throws IOException, InvalidTokenOffsetsException {

        Assert.hasText(fieldName);
        Assert.hasText(fieldContents);
        Assert.notNull(query);
        Assert.notNull(analyzer);

        TokenStream stream = TokenSources.getTokenStream(fieldName, fieldContents, analyzer);

        QueryScorer scorer = new QueryScorer(query, fieldName);

        Fragmenter fragmenter = new SimpleSpanFragmenter(scorer); // TODO ? SimpleSpanFragmenter(QueryScorer queryScorer, int fragmentSize)
        
        FragmentCollectorNullFormatter formatter = new FragmentCollectorNullFormatter(BEST_FRAGMENTS_COLLECTOR_FACTOR);
        Highlighter highlighter = new Highlighter(formatter, scorer);

        // TODO ? highlighter.setMaxDocCharsToAnalyze()
        highlighter.setTextFragmenter(fragmenter);


        highlighter.getBestFragment(stream, fieldContents);

        List<Fragment> fragments = formatter.getFragments();

        // получить фрагмент исходной фразы или ее части
        int wordCount = text.replaceAll("^\\s+|\\s+$", "").split("\\s+").length;

        if (wordCount > 1) {
            StringBuilder phraseBuilder = new StringBuilder(fragments.get(0).getText());
            for (int i = 1; i < wordCount; i++) {
                phraseBuilder.append(" ").append(fragments.get(i).getText());
            }

            String phrase = phraseBuilder.toString();
            String contents = fieldContents.replaceAll("\\s+", " ");

            if (contents.indexOf(phrase) >= 0) {
                fragments.add(new Fragment(phrase, BEST_FRAGMENTS_PHRASE_SCORE));
            }
        }

        return fragments;
    }
}
