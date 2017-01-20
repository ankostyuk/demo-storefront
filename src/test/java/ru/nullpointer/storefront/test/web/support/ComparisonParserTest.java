package ru.nullpointer.storefront.test.web.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.Test;
import ru.nullpointer.storefront.domain.Match;
import ru.nullpointer.storefront.web.support.MatchParser;
import static org.junit.Assert.*;

/**
 *
 * @author Alexander Yastrebov
 */
public class ComparisonParserTest {

    @Test
    public void test_parse() {
        MatchParser parser = new MatchParser();
        List<Match> matchList = null;

        int maxCount = 10;
        assertEquals(0, parser.parse("", maxCount).size());
        assertEquals(0, parser.parse("  ", maxCount).size());
        assertEquals(0, parser.parse((String) null, maxCount).size());

        matchList = parser.parse("m1", maxCount);
        assertNotNull(matchList);
        assertEquals(1, matchList.size());
        assertEquals(Match.Type.MODEL, matchList.get(0).getType());
        assertEquals(Integer.valueOf(1), matchList.get(0).getId());

        matchList = parser.parse("m2,o3", maxCount);
        assertNotNull(matchList);
        assertEquals(2, matchList.size());
        assertEquals(Match.Type.MODEL, matchList.get(0).getType());
        assertEquals(Integer.valueOf(2), matchList.get(0).getId());
        assertEquals(Match.Type.OFFER, matchList.get(1).getType());
        assertEquals(Integer.valueOf(3), matchList.get(1).getId());

        matchList = parser.parse("m5,o5,m6", maxCount);
        assertNotNull(matchList);
        assertEquals(3, matchList.size());
        assertEquals(Match.Type.MODEL, matchList.get(0).getType());
        assertEquals(Integer.valueOf(5), matchList.get(0).getId());
        assertEquals(Match.Type.OFFER, matchList.get(1).getType());
        assertEquals(Integer.valueOf(5), matchList.get(1).getId());
        assertEquals(Match.Type.MODEL, matchList.get(2).getType());
        assertEquals(Integer.valueOf(6), matchList.get(2).getId());
    }

    @Test
    public void test_serialize() {
        MatchParser parser = new MatchParser();

        assertEquals("", parser.serialize(Collections.<Match>emptyList()));

        Match m = null;
        List<Match> matchList = new ArrayList<Match>();
        m = new Match();
        m.setType(Match.Type.MODEL);
        m.setId(123);
        matchList.add(m);

        assertEquals("m123", parser.serialize(matchList));

        m = new Match();
        m.setType(Match.Type.OFFER);
        m.setId(0);
        matchList.add(m);

        assertEquals("m123,o0", parser.serialize(matchList));
    }

    @Test
    public void test_badValues() {
        MatchParser parser = new MatchParser();

        int maxCount = 10;
        assertEquals("m1,m2,m3,o3,o234,m34534", parser.serialize(parser.parse("m1,m2,m3,o3,o234,m34534", maxCount)));
        assertEquals("m1,m2,m3,o3,o234,m34534", parser.serialize(parser.parse("m1,,m2,,m3,o3,o234,,m34534", maxCount)));
        assertEquals("m1,m2,m3,o3,o234,m34534", parser.serialize(parser.parse("m1,,m2,   ,m3,o3  ,  o234,  ,m34534", maxCount)));

        // max count
        assertEquals("m1,m2,m3,m4,m5,o1,o2,o3,o4,o5", parser.serialize(parser.parse("m1,  m2,  m3, m4,m5,o1,o2,o3,o4,o5, o6, m6", maxCount)));
    }

    @Test
    public void test_dublicates() {
        MatchParser parser = new MatchParser();
        assertEquals("m1,m2,m3,o1,o2,o3", parser.serialize(parser.parse("m1,m1,m2,m2,m3,m3,o1,o1,o2,o2,o3,o3", 10)));
    }
}
