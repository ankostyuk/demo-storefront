package ru.nullpointer.storefront.domain.support;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.springframework.util.Assert;
import ru.nullpointer.storefront.domain.Match;
import ru.nullpointer.storefront.domain.Model;
import ru.nullpointer.storefront.domain.ModelInfo;

/**
 *
 * @author Alexander Yastrebov
 */
public class ModelMatch extends AbstractMatch {

    private Model model;
    private ModelInfo modelInfo;

    public ModelMatch(Model model, ModelInfo modelInfo) {
        Assert.notNull(model);
        //Assert.notNull(modelInfo);
        //
        this.model = model;
        this.modelInfo = modelInfo;
    }

    public Model getModel() {
        return model;
    }

    public ModelInfo getModelInfo() {
        return modelInfo;
    }

    @Override
    public Match.Type getType() {
        return Match.Type.MODEL;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE) //
                .append("model", model)//
                .append("modelInfo", modelInfo)//
                .toString();
    }
}
