package ru.nullpointer.storefront.web.search;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.Sort;
import org.springframework.stereotype.Component;
import ru.nullpointer.storefront.domain.Match;
import ru.nullpointer.storefront.domain.support.MatchSorting;

import ru.nullpointer.storefront.service.search.SearchUtils;
import ru.nullpointer.storefront.service.search.IdentityFieldValue;
import ru.nullpointer.storefront.service.search.Type;
import ru.nullpointer.storefront.web.ui.Target;

/**
 * @author ankostyuk
 */
@Component
public class CatalogSearchHelper {

    public Target buildTarget(Document doc) {
        Target target = new Target();

        target.setName(doc.get(SearchUtils.FIELD_NAME));

        IdentityFieldValue identity = new IdentityFieldValue(doc.get(SearchUtils.FIELD_IDENTITY));

        Integer id = identity.getId();
        
        Type type = identity.getType();

        switch (type) {
            // TODO OFFER_PARAM
            case MODEL:
                target.setUrl("/model/" + id.toString());
                break;
            case OFFER:
                target.setUrl("/offer/" + id.toString());
                break;
            case BRAND:
                target.setUrl("/brand/" + id.toString());
                break;
            case CATEGORY:
                target.setUrl("/category/" + id.toString());
                break;
            case SECTION:
                target.setUrl("/section/" + id.toString());
                break;
            default:
                throw new IllegalArgumentException("Неизвестный тип результата поиска: " + type);
        }

        return target;
    }

    public Match buildMatch(Document doc) {
        Match match = new Match();

        IdentityFieldValue identity = new IdentityFieldValue(doc.get(SearchUtils.FIELD_IDENTITY));

        match.setId(identity.getId());

        Type type = identity.getType();
        switch (type) {
            case MODEL:
                match.setType(Match.Type.MODEL);
                break;
            case OFFER:
                match.setType(Match.Type.OFFER);
                break;
            default:
                throw new IllegalArgumentException("Неизвестный тип результата поиска для <" + Match.class.getName() + ">: " + type);
        }

        return match;
    }

    public Integer getId(Document doc) {
        IdentityFieldValue identity = new IdentityFieldValue(doc.get(SearchUtils.FIELD_IDENTITY));
        return identity.getId();
    }

    public Sort getSearchSortByMatchSorting(MatchSorting sorting) {
        switch (sorting) {
            case PRICE_ASCENDING:
                return SearchUtils.SORT_PRICE_ASC;
            case PRICE_DESCENDING:
                return SearchUtils.SORT_PRICE_DESC;
            default:
                throw new IllegalArgumentException("Неизвестный тип сортировки: " + sorting);
        }
    }
}
