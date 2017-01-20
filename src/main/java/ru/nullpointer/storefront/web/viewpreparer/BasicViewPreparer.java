package ru.nullpointer.storefront.web.viewpreparer;

import org.apache.tiles.Attribute;
import org.apache.tiles.AttributeContext;
import org.apache.tiles.context.TilesRequestContext;
import org.apache.tiles.preparer.ViewPreparer;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;

/**
 *
 * @author Alexander Yastreboc
 */
abstract class BasicViewPreparer implements ViewPreparer, InitializingBean {

    @Value("${application.build.mode}")
    private String applicationBuildMode;

    @Override
    public void execute(TilesRequestContext tilesContext, AttributeContext attributeContext) {
        attributeContext.putAttribute("ui.application.build.mode", new Attribute(applicationBuildMode), true);

        doExecute(tilesContext, attributeContext);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.hasText(applicationBuildMode, "Свойство applicationBuildMode не установлено");
    }

    protected void doExecute(TilesRequestContext tilesContext, AttributeContext attributeContext) {
    }
}
