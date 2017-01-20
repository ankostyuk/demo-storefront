package ru.nullpointer.storefront.web.support;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import ru.nullpointer.storefront.domain.Brand;

/**
 *
 * @author Alexander Yastrebov
 */
class BrandParser extends ParserSupport {

    private static final String BRAND_PARAM = "brand";
    //
    private Set<Integer> brandIdSet;

    BrandParser(List<Brand> brandList) {
        super(new CustomNumberEditor(Integer.class, true));

        brandIdSet = new HashSet<Integer>();
        for (Brand brand : brandList) {
            brandIdSet.add(brand.getId());
        }
    }

    public Set<Integer> parse(HttpServletRequest request) {
        String[] values = request.getParameterValues(BRAND_PARAM);
        if (values != null) {
            Set<Integer> result = new HashSet<Integer>();
            for (String v : values) {
                Integer brandId = (Integer) parseValue(v);
                if (brandId != null && brandIdSet.contains(brandId)) {
                    result.add(brandId);
                }
            }
            return result;
        }
        return null;
    }

    public StringBuilder serialize(Set<Integer> brandIdSet, StringBuilder sb) {
        boolean first = true;
        for (Integer brandId : brandIdSet) {
            if (first) {
                first = false;
            } else {
                sb.append(PARAM_DELIMETER);
            }
            serializeValue(BRAND_PARAM, brandId, sb);
        }
        return sb;
    }
}
