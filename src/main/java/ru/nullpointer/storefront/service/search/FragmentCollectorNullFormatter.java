package ru.nullpointer.storefront.service.search;

import java.util.ArrayList;
import java.util.List;
import org.apache.lucene.search.highlight.Formatter;
import org.apache.lucene.search.highlight.TokenGroup;

/**
 * @author ankostyuk
 */
public class FragmentCollectorNullFormatter implements Formatter {

    private float collectorFactor;
    private List<Fragment> fragments;

    public FragmentCollectorNullFormatter(float collectorFactor) {
        this.collectorFactor = collectorFactor;
        fragments = new ArrayList<Fragment>();
    }

    public float getCollectorFactor() {
        return collectorFactor;
    }

    public void setCollectorFactor(float collectorFactor) {
        this.collectorFactor = collectorFactor;
    }

    public List<Fragment> getFragments() {
        return fragments;
    }
    
    @Override
    public String highlightTerm(String string, TokenGroup tg) {

        if (tg.getTotalScore() >= collectorFactor) {
            fragments.add(new Fragment(string, tg.getTotalScore()));
        }
        
        return string;
    }
}
