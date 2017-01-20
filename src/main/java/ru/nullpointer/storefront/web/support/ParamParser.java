package ru.nullpointer.storefront.web.support;

import javax.servlet.http.HttpServletRequest;
import ru.nullpointer.storefront.domain.param.Param;
import ru.nullpointer.storefront.domain.support.ParamModel;

/**
 *
 * @author Alexander Yastrebov
 */
interface ParamParser {

    Object parse(Param param, ParamModel paramModel, HttpServletRequest request);

    StringBuilder serialize(Object value, Param param, ParamModel paramModel, StringBuilder sb);
}
