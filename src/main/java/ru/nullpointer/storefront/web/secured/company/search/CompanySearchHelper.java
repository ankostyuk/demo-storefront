package ru.nullpointer.storefront.web.secured.company.search;

import org.apache.lucene.document.Document;
import org.springframework.stereotype.Component;

import ru.nullpointer.storefront.service.search.SearchUtils;
import ru.nullpointer.storefront.service.search.IdentityFieldValue;
import ru.nullpointer.storefront.service.search.Type;
import ru.nullpointer.storefront.web.ui.Target;

/**
 * @author ankostyuk
 */
@Component
public class CompanySearchHelper {

    public Target buildTarget(Document doc) {
        Target target = new Target();

        target.setName(doc.get(SearchUtils.FIELD_NAME));

        IdentityFieldValue identity = new IdentityFieldValue(doc.get(SearchUtils.FIELD_IDENTITY));

        Integer id = identity.getId();
        
        Type type = identity.getType();

        switch (type) {
            case OFFER:
                target.setUrl("/secured/company/offer/edit/" + id.toString());
                break;
            default:
                throw new IllegalArgumentException("Неизвестный тип результата поиска: " + type);
        }

        return target;
    }

    public Integer getId(Document doc) {
        IdentityFieldValue identity = new IdentityFieldValue(doc.get(SearchUtils.FIELD_IDENTITY));
        return identity.getId();
    }
}
