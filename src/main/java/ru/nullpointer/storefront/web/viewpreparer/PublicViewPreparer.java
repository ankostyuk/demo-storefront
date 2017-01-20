package ru.nullpointer.storefront.web.viewpreparer;

import javax.annotation.Resource;
import org.apache.tiles.Attribute;
import org.apache.tiles.AttributeContext;
import org.apache.tiles.context.TilesRequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.nullpointer.storefront.domain.Settings;
import ru.nullpointer.storefront.service.SettingsService;

/**
 * @author Александр
 * @author ankostyuk
 */
@Component
public class PublicViewPreparer extends BasicViewPreparer {

    private Logger logger = LoggerFactory.getLogger(PublicViewPreparer.class);
    //
    @Resource
    private SettingsService settingsService;

    @Override
    protected void doExecute(TilesRequestContext tilesContext, AttributeContext attributeContext) {
        Settings settings = settingsService.getSettings();
        if (settings.getRegion() != null) {
            attributeContext.putAttribute("ui.region.name", new Attribute(settings.getRegion().getName()), true);
        }
        attributeContext.putAttribute("ui.region.aware", new Attribute(settings.isRegionAware()), true);
    }
}
