package ru.nullpointer.storefront.web.support;

import java.util.List;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;
import org.springframework.util.Assert;
import ru.nullpointer.storefront.domain.CatalogItem;
import ru.nullpointer.storefront.service.search.catalog.CatalogSearchBuilder;
import ru.nullpointer.storefront.web.Metadata;
import ru.nullpointer.storefront.web.SearchSettings;

/**
 * @author ankostyuk
 * @author Alexander Yastrebov
 */
@Component
public class UIHelper {

    private Logger logger = LoggerFactory.getLogger(UIHelper.class);
    //
    private static final String PAGE_TITLE_SEPARATOR = " – ";
    //
    @Resource
    private CatalogSearchBuilder сatalogSearch;
    @Resource
    private MessageSource messageSource;

    public TitleBuilder titleBuilder() {
        return new TitleBuilder(messageSource);
    }

    public void initSearchSettings(HttpServletRequest request, Integer catalogItemId, ModelMap model) {
        SearchSettings searchSettings = new SearchSettings();

        searchSettings.setText(normalizeSearchText(request.getParameter("text")));

        searchSettings.setCatalogItemId(catalogItemId);

        if (catalogItemId == null) { // TODO example from catalogItemId
            searchSettings.setExample(сatalogSearch.getActualSearchExample());
        }

        model.addAttribute("searchSettings", searchSettings);
    }

    public void setCatalogTheme(List<CatalogItem> path, ModelMap model) {
        Assert.notNull(path);
        for (int i = path.size() - 1; i >= 0; i--) {
            String theme = path.get(i).getTheme();
            if (theme != null) {
                model.addAttribute("headerTheme", theme);
                break;
            }
        }
    }

    public static String normalizeSearchText(String text) {
        return text == null ? null : text.replaceAll("^\\s+|\\s+$", "");
    }

    public static class TitleBuilder {

        private StringBuilder sb;
        private MessageSource messageSource;

        private TitleBuilder(MessageSource messageSource) {
            this.messageSource = messageSource;
            sb = new StringBuilder();
        }

        public TitleBuilder append(String s) {
            if (sb.length() > 0) {
                sb.append(PAGE_TITLE_SEPARATOR);
            }
            sb.append(s);
            return this;
        }

        public TitleBuilder appendMessage(String messageCode) {
            String message = messageSource.getMessage(messageCode, null, null);
            return append(message);
        }

        public void build(Metadata metadata) {
            metadata.setTitle(sb.toString());
        }
    }
}
