package ru.nullpointer.storefront.web.support;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import ru.nullpointer.storefront.domain.Brand;
import ru.nullpointer.storefront.domain.param.Param;
import ru.nullpointer.storefront.domain.support.NumberInterval;
import ru.nullpointer.storefront.domain.support.MatchFilter;
import ru.nullpointer.storefront.domain.support.ParamModel;
import ru.nullpointer.storefront.web.ui.DecimalEditor;

/**
 *
 * @author Alexander Yastrebov
 */
public class MatchFilterParser {

    private Logger logger = LoggerFactory.getLogger(MatchFilterParser.class);
    //
    private ParamModel paramModel;
    private PriceParser priceParser;
    private BrandParser brandParser;
    private Map<Param.Type, ParamParser> paramParserMap;

    public MatchFilterParser(List<Brand> brandList, ParamModel paramModel) {
        this.paramModel = paramModel;

        priceParser = new PriceParser(new DecimalEditor(BigDecimal.class, true, 2));
        brandParser = new BrandParser(brandList);

        paramParserMap = new EnumMap<Param.Type, ParamParser>(Param.Type.class);
        paramParserMap.put(Param.Type.BOOLEAN, new BooleanParamParser(new CustomBooleanEditor("1", "0", true)));
        paramParserMap.put(Param.Type.NUMBER, new NumberParamParser(new DecimalEditor(BigDecimal.class, true, 4)));
        paramParserMap.put(Param.Type.SELECT, new SelectParamParser(new CustomNumberEditor(Integer.class, true)));
    }

    public MatchFilter parse(HttpServletRequest request) {
        NumberInterval price = priceParser.parse(request);
        Set<Integer> brandIdSet = brandParser.parse(request);

        Map<Integer, Object> paramValueMap = null;
        if (paramModel != null) {
            paramValueMap = parseParamValues(request, paramModel);
        }

        return new MatchFilter(price, brandIdSet, paramValueMap);
    }

    public String serialize(MatchFilter matchFilter) {
        StringBuilder sb = new StringBuilder();

        boolean first = true;
        NumberInterval price = matchFilter.getPrice();
        if (price != null) {
            int len = sb.length();

            priceParser.serialize(price, sb);

            first = insertDelimeter(len, first, sb);
        }

        Set<Integer> brandIdSet = matchFilter.getBrandIdSet();
        if (brandIdSet != null) {
            int len = sb.length();

            brandParser.serialize(brandIdSet, sb);

            first = insertDelimeter(len, first, sb);
        }

        Map<Integer, Object> paramValueMap = matchFilter.getParamValueMap();
        if (paramValueMap != null) {
            int len = sb.length();

            serializeParamValues(paramValueMap, paramModel, sb);

            first = insertDelimeter(len, first, sb);
        }
        return sb.toString();
    }

    private Map<Integer, Object> parseParamValues(HttpServletRequest request, ParamModel paramModel) {
        Map<Integer, Object> paramValueMap = new HashMap<Integer, Object>();
        for (Param p : paramModel.getParamList()) {
            ParamParser parser = paramParserMap.get(p.getType());
            if (parser != null) {
                Object value = parser.parse(p, paramModel, request);
                if (value != null) {
                    paramValueMap.put(p.getId(), value);
                }
            } else {
                throw new RuntimeException("Неподдерживаемый тип параметра " + p.getType());
            }
        }
        return paramValueMap;
    }

    private void serializeParamValues(Map<Integer, Object> paramValueMap, ParamModel paramModel, StringBuilder sb) {
        boolean first = true;
        for (Param p : paramModel.getParamList()) {
            int len = sb.length();

            Object value = paramValueMap.get(p.getId());
            if (value != null) {
                ParamParser parser = paramParserMap.get(p.getType());
                if (parser != null) {
                    parser.serialize(value, p, paramModel, sb);
                } else {
                    throw new RuntimeException("Неподдерживаемый тип параметра " + p.getType());
                }
            }
            first = insertDelimeter(len, first, sb);
        }
    }

    private boolean insertDelimeter(int oldLen, boolean first, StringBuilder sb) {
        // вставить разделитель
        if (sb.length() > oldLen) {
            if (first) {
                return false;
            } else {
                sb.insert(oldLen, ParserSupport.PARAM_DELIMETER);
            }
        }
        return first;
    }
}
