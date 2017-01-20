package ru.nullpointer.storefront.domain.support;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.springframework.util.Assert;
import ru.nullpointer.storefront.domain.Unit;
import ru.nullpointer.storefront.domain.param.Param;
import ru.nullpointer.storefront.domain.param.ParamGroup;
import ru.nullpointer.storefront.domain.param.ParamSelectOption;

/**
 *
 * @author alexander
 */
public class ParamModel {

    private List<Param> paramList;
    private Map<Integer, ParamGroup> groupMap;
    private Map<Integer, List<ParamSelectOption>> selectOptionMap;
    private Map<Integer, Unit> unitMap;

    public ParamModel(
            List<Param> paramList,
            Map<Integer, ParamGroup> groupMap,
            Map<Integer, List<ParamSelectOption>> selectOptionMap,
            Map<Integer, Unit> unitMap) {
        Assert.notNull(paramList);
        Assert.notNull(groupMap);
        Assert.notNull(selectOptionMap);
        Assert.notNull(unitMap);

        this.paramList = Collections.unmodifiableList(paramList);
        this.groupMap = Collections.unmodifiableMap(groupMap);
        this.selectOptionMap = Collections.unmodifiableMap(selectOptionMap);
        this.unitMap = Collections.unmodifiableMap(unitMap);
    }

    public List<Param> getParamList() {
        return paramList;
    }

    public Map<Integer, ParamGroup> getGroupMap() {
        return groupMap;
    }

    public Map<Integer, List<ParamSelectOption>> getSelectOptionMap() {
        return selectOptionMap;
    }

    public Map<Integer, Unit> getUnitMap() {
        return unitMap;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)//
                .append("paramList", paramList)//
                .append("groupMap", groupMap)//
                .toString();
    }
}
