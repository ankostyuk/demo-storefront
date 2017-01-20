package ru.nullpointer.storefront.service.search;

import java.io.IOException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.FieldCache;
import org.apache.lucene.search.FieldComparator;
import org.apache.lucene.search.FieldComparatorSource;
import org.springframework.util.Assert;
import ru.nullpointer.storefront.domain.Region;

/**
 * @author ankostyuk
 *
 * Внимание: используется экспериментальное API Lucene 3.0.2 
 * {@link FieldComparatorSource} - может быть удалено в следующих версиях Lucene.
 */
public class RegionMatchFieldComparatorSource extends FieldComparatorSource {

    private Region region;

    RegionMatchFieldComparatorSource(Region region) {
        Assert.notNull(region);
        this.region = region;
    }

    @Override
    public FieldComparator newComparator(String fieldName, int numHits, int sortPos, boolean reversed) throws IOException {
        return new RegionMatchComparator(region, fieldName, numHits);
    }

    /**
     * Сортировка по наличию соответствия региона
     */
    public static final class RegionMatchComparator extends FieldComparator {

        private Region region;
        private final String fieldName;
        private final String[] values;
        private String[] currentReaderValues;
        private String bottom;

        RegionMatchComparator(Region region, String fieldName, int numHits) {
            Assert.notNull(region);
            Assert.hasText(fieldName);

            this.region = region;
            this.fieldName = fieldName;
            values = new String[numHits];
        }

        @Override
        public int compare(int slot1, int slot2) {
            return compareValues(values[slot1], values[slot2]);
        }

        @Override
        public int compareBottom(int doc) throws IOException {
            return compareValues(bottom, currentReaderValues[doc]);
        }

        @Override
        public void copy(int slot, int doc) throws IOException {
            values[slot] = currentReaderValues[doc];
        }

        @Override
        public void setNextReader(IndexReader reader, int docBase) throws IOException {
            currentReaderValues = FieldCache.DEFAULT.getStrings(reader, fieldName);
        }

        @Override
        public void setBottom(final int bottom) {
            this.bottom = values[bottom];
        }

        @Override
        public Comparable value(int slot) {
            return getComparable(values[slot]); // TODO !
        }

        private int compareValues(String val1, String val2) {
            Boolean match1 = (val1 == null ? false : RegionFilter.isMatch(val1, region));
            Boolean match2 = (val2 == null ? false : RegionFilter.isMatch(val2, region));
            return match1.compareTo(match2);
        }

        private Comparable getComparable(String val) {
            return Boolean.valueOf(val == null ? false : RegionFilter.isMatch(val, region));
        }
    }
}
