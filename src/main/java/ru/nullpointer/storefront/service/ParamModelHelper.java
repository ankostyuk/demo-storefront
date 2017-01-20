package ru.nullpointer.storefront.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import ru.nullpointer.storefront.dao.ParamDAO;
import ru.nullpointer.storefront.dao.ParamGroupDAO;
import ru.nullpointer.storefront.dao.ParamSelectOptionDAO;
import ru.nullpointer.storefront.dao.UnitDAO;
import ru.nullpointer.storefront.domain.Unit;
import ru.nullpointer.storefront.domain.param.BooleanParam;
import ru.nullpointer.storefront.domain.param.NumberParam;
import ru.nullpointer.storefront.domain.param.Param;
import ru.nullpointer.storefront.domain.param.ParamGroup;
import ru.nullpointer.storefront.domain.param.ParamSelectOption;
import ru.nullpointer.storefront.domain.support.Constants;
import ru.nullpointer.storefront.domain.support.ParamModel;
import ru.nullpointer.storefront.web.ui.DecimalEditor;

/**
 *
 * @author Alexander Yastrebov
 */
@Component
class ParamModelHelper {

    private Logger logger = LoggerFactory.getLogger(ParamModelHelper.class);
    //
    @Resource
    private ParamGroupDAO paramGroupDAO;
    @Resource
    private ParamDAO paramDAO;
    @Resource
    private UnitDAO unitDAO;
    @Resource
    private ParamSelectOptionDAO paramSelectOptionDAO;

    /**
     * Возвращает модель параметров
     * @param paramSetDescriptorId
     * @return
     */
    ParamModel getParamModel(Integer paramSetDescriptorId) {
        Assert.notNull(paramSetDescriptorId);

        List<ParamGroup> groupList = paramGroupDAO.getParamGroupsByParamSetDescriptorId(paramSetDescriptorId);
        Assert.notNull(groupList);

        final Map<Integer, ParamGroup> groupMap = new HashMap<Integer, ParamGroup>();
        for (ParamGroup group : groupList) {
            groupMap.put(group.getId(), group);
        }

        Set<Integer> selectParamIdSet = new HashSet<Integer>();
        Set<Integer> unitIdSet = new HashSet<Integer>();
        List<Param> paramList = new ArrayList<Param>();

        for (Param p : paramDAO.getParamListByDescriptorId(paramSetDescriptorId)) {
            paramList.add(p);
            Integer paramId = p.getId();
            switch (p.getType()) {
                case BOOLEAN:
                    // нечего делать
                    break;
                case NUMBER:
                    unitIdSet.add(((NumberParam) p).getUnitId());
                    break;
                case SELECT:
                    selectParamIdSet.add(paramId);
                    break;
                default:
                    throw new RuntimeException("Неподдерживаемый тип параметра: " + p.getType());
            }
        }

        Collections.sort(paramList, new Comparator<Param>() {

            @Override
            public int compare(Param p1, Param p2) {
                ParamGroup g1 = groupMap.get(p1.getParamGroupId());
                ParamGroup g2 = groupMap.get(p2.getParamGroupId());
                if (g1 != g2) {
                    return g1.getOrdinal().compareTo(g2.getOrdinal());
                } else {
                    return p1.getOrdinal().compareTo(p2.getOrdinal());
                }
            }
        });

        Map<Integer, List<ParamSelectOption>> selectOptionMap = paramSelectOptionDAO.getParamSelectOptionMap(selectParamIdSet);
        Map<Integer, Unit> unitMap = unitDAO.getUnitMap(unitIdSet);

        return new ParamModel(paramList, groupMap, selectOptionMap, unitMap);
    }

    /**
     * Создает описание характеристик предложения или модели
     * @param paramSetDescriptorId
     * @param paramValueMap
     * @return
     */
    String buildParamDescription(Integer paramSetDescriptorId, Map<Integer, Object> paramValueMap) {
        StringBuilder result = new StringBuilder();

        ParamModel paramModel = getParamModel(paramSetDescriptorId);
        for (Param p : paramModel.getParamList()) {
            Object value = paramValueMap.get(p.getId());
            if (value == null) {
                continue;
            }

            StringBuilder paramDescription = new StringBuilder();
            if (result.length() > 0) {
                paramDescription.append(", ");
            }

            switch (p.getType()) {
                case BOOLEAN:
                    BooleanParam bp = (BooleanParam) p;
                    boolean b = ((Boolean) value).booleanValue();

                    paramDescription.append(p.getName().toLowerCase())//
                            .append(": ")//
                            .append(b ? bp.getTrueName().toLowerCase() : bp.getFalseName().toLowerCase());
                    break;
                case NUMBER:
                    NumberParam np = (NumberParam) p;

                    DecimalEditor ed = new DecimalEditor(BigDecimal.class, false, np.getPrecision());
                    ed.setValue(value);

                    paramDescription.append(p.getName().toLowerCase())//
                            .append(" ")//
                            .append(ed.getAsText())//
                            .append(" ")//
                            .append(paramModel.getUnitMap().get(np.getUnitId()).getAbbreviation());
                    break;
                case SELECT:
                    List<ParamSelectOption> psoList = paramModel.getSelectOptionMap().get(p.getId());

                    for (ParamSelectOption pso : psoList) {
                        if (pso.getId().equals(value)) {
                            String optionName = pso.getName();
                            paramDescription.append(p.getName().toLowerCase())//
                                    .append(": ")//
                                    .append(needsLowering(optionName) ? optionName.toLowerCase() : optionName);
                            break;
                        }
                    }
                    break;
                default:
                    throw new RuntimeException("Неподдерживаемый тип параметра: " + p.getType());
            }

            if (result.length() + paramDescription.length() <= Constants.PRODUCT_MAX_PARAM_DESCRIPTION_LENGTH) {
                result.append(paramDescription.toString());
            } else {
                break;
            }
        }

        return result.length() > 0 ? result.toString() : null;
    }

    /**
     * Определяет необходимо ли перевести строку в нижний регистр
     * @param value
     * @return
     */
    private boolean needsLowering(String value) {
        Assert.notNull(value);
        Assert.isTrue(value.length() > 0);

        // Не приводим короткие строки
        if (value.length() < 3) {
            return false;
        }

        // Если строка содержит подряд две заглавные буквы
        // или заглавную букву и не букву, то не приводить в нижний регистр
        char prev = value.charAt(0);
        for (int i = 1; i < value.length(); i++) {
            char next = value.charAt(i);
            if (Character.isUpperCase(prev) || !Character.isLetter(prev)) {
                if (Character.isUpperCase(next) || !Character.isLetter(next)) {
                    return false;
                }
            }
            prev = next;
        }
        return true;
    }
}
