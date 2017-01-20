package ru.nullpointer.storefront.web.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.Assert;
import ru.nullpointer.storefront.domain.Match;

/**
 *
 * @author Alexander Yastrebov
 */
public class MatchParser {

    private static final char SEPARATOR = ',';
    //
    private Map<String, Match.Type> prefixMap;

    public MatchParser() {
        Map<String, Match.Type> map = new HashMap<String, Match.Type>(Match.Type.values().length);
        map.put("m", Match.Type.MODEL);
        map.put("o", Match.Type.OFFER);

        Assert.isTrue(map.size() == Match.Type.values().length);
        prefixMap = Collections.unmodifiableMap(map);
    }

    public List<Match> parse(String idString, int maxSize) {
        return parse(StringUtils.split(idString, SEPARATOR), maxSize);
    }

    public List<Match> parse(String[] idValues, int maxSize) {
        if (idValues == null || idValues.length == 0) {
            return Collections.emptyList();
        }
        Assert.noNullElements(idValues);
        Assert.isTrue(maxSize > 0);

        List<Match> result = new ArrayList<Match>();
        for (String val : idValues) {
            Match m = parseMatch(val);
            if (m != null) {
                if (!result.contains(m)) {
                    result.add(m);
                    if (result.size() == maxSize) {
                        break;
                    }
                }
            }
        }
        return result;
    }

    public String serialize(List<Match> matchList) {
        Assert.notNull(matchList);
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Match m : matchList) {
            if (!first) {
                sb.append(SEPARATOR);
            }
            serializeMatch(m, sb);
            first = false;
        }
        return sb.toString();
    }

    private void serializeMatch(Match m, StringBuilder sb) {
        for (String prefix : prefixMap.keySet()) {
            Match.Type type = prefixMap.get(prefix);
            if (type == m.getType()) {
                sb.append(prefix);
                sb.append(m.getId().toString());
                break;
            }
        }
    }

    private Match parseMatch(String val) {
        val = val.trim();
        for (String prefix : prefixMap.keySet()) {
            if (val.startsWith(prefix)) {
                String str = val.substring(prefix.length());
                if (!str.isEmpty()) {
                    Integer id = parseInteger(str);
                    if (id != null) {
                        return new Match(prefixMap.get(prefix), id);
                    }
                }
                break;
            }
        }
        return null;
    }

    private Integer parseInteger(String str) {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}
