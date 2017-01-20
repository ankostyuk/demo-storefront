package ru.nullpointer.storefront.web.ui.catalog;

import java.util.List;

import javax.validation.Valid;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import ru.nullpointer.storefront.domain.param.ParamGroup;

/**
 * @author ankostyuk
 */
public class ParamGroupProperties {
    @Valid
    private ParamGroup paramGroup;

    private List<ParamProperties> paramList;

    private Boolean canDelete;

    // for grouping operations
    private Boolean canChoose;

    public ParamGroup getParamGroup() {
        return paramGroup;
    }

    public void setParamGroup(ParamGroup paramGroup) {
        this.paramGroup = paramGroup;
    }

    public List<ParamProperties> getParamList() {
        return paramList;
    }

    public void setParamList(List<ParamProperties> paramList) {
        this.paramList = paramList;
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
                .append("paramGroup", paramGroup)//
                .append("canDelete", canDelete)//
                .append("canChoose", canChoose)//
                .toString();
    }
}
