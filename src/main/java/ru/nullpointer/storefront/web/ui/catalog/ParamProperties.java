package ru.nullpointer.storefront.web.ui.catalog;

import javax.validation.Valid;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import ru.nullpointer.storefront.domain.param.Param;

/**
 * @author ankostyuk
 */
public class ParamProperties {
    @Valid
    private Param param;

    private Boolean canDelete;

    // for grouping operations
    private Boolean canChoose;

    public Param getParam() {
        return param;
    }

    public void setParam(Param param) {
        this.param = param;
    }

    public Boolean getCanDelete() {
        return canDelete;
    }

    public void setCanDelete(Boolean canDelete) {
        this.canDelete = canDelete;
    }

    public Boolean getCanChoose() {
        return canChoose;
    }

    public void setCanChoose(Boolean canChoose) {
        this.canChoose = canChoose;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)//
                .append("param", param)//
                .append("canDelete", canDelete)//
                .append("canChoose", canChoose)//
                .toString();
    }
}
