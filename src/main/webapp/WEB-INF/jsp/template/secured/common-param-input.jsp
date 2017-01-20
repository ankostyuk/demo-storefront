<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn2" uri="http://www.nullpointer.ru/fn2/tags" %>

<tiles:useAttribute name="_catalogItem" ignore="true" />

<c:set var="prevGroupId" value="${null}" />
<c:forEach var="p" items="${paramModel.paramList}">
    <c:if test="${p.paramGroupId != prevGroupId}">
        <c:if test="${not empty prevGroupId}"></ul></c:if>
        <c:set var="prevGroupId" value="${p.paramGroupId}" />
    <h3 class="field-group-name"><c:out value="${paramModel.groupMap[p.paramGroupId].name}" /></h3>
    <ul class="field-group">
    </c:if>
    <li>
        <spring:bind path="paramInput.p[${p.id}]" ignoreNestedPath="true">
            <label for="p${p.id}" class="text"><c:out value="${p.name}" /><c:if test="${not empty p.description}">&nbsp;<a id="param-tooltip-p${p.id}" class="param-description" href="<spring:url value='/category/{id}/term'><spring:param name='id' value='${_catalogItem.id}' /></spring:url>#${fn2:xmlid(p.id)}">&nbsp;</a></c:if></label>
            <c:choose>
                <c:when test="${p.type == 'BOOLEAN'}">
                    <select id="p${p.id}" name="${status.expression}" class="text">
                        <option value="" <c:if test="${empty status.value}">selected="selected"</c:if>>&lt;неизвестно&gt;</option>
                        <option value="1" <c:if test="${!empty status.value && status.value == true}">selected="selected"</c:if>><c:out value="${p.trueName}" /></option>
                        <option value="0" <c:if test="${!empty status.value && status.value == false}">selected="selected"</c:if>><c:out value="${p.falseName}" /></option>
                    </select>
                </c:when>
                <c:when test="${p.type == 'NUMBER'}">
                    <input id="p${p.id}" name="${status.expression}" class="text" value="${status.value}" />
                    <c:if test="${status.error}"><span class="error"><c:out value="${status.errorMessage}" /></span></c:if>
                    <p class="hint">
                        от
                        <fmt:formatNumber
                            value="${p.minValue}"
                            minFractionDigits="${p.precision}"
                            maxFractionDigits="${p.precision}" />
                        до
                        <fmt:formatNumber
                            value="${p.maxValue}"
                            minFractionDigits="${p.precision}"
                            maxFractionDigits="${p.precision}" />&nbsp;<c:set var="paramUnit" value="${paramModel.unitMap[p.unitId]}" />
                        <abbr title="<c:out value="${paramUnit.name}" />">${fn2:htmlformula(paramUnit.abbreviation)}</abbr>
                    </p>
                </c:when>
                <c:when test="${p.type == 'SELECT'}">
                    <select id="p${p.id}" name="${status.expression}" class="text">
                        <option value="" <c:if test="${status.value == null}">selected="selected"</c:if>>&lt;неизвестно&gt;</option>
                        <c:forEach var="selectOption" items="${paramModel.selectOptionMap[p.id]}">
                            <option value="${selectOption.id}" <c:if test="${status.value == selectOption.id}">selected="selected"</c:if>><c:out value="${selectOption.name}" /></option>
                        </c:forEach>
                    </select>
                    <c:if test="${status.error}"><span class="error"><c:out value="${status.errorMessage}" /></span></c:if>
                </c:when>
            </c:choose>
        </spring:bind>
    </li>
</c:forEach>
</ul>

<script type="text/javascript">
    // <![CDATA[
    jsHelper.document.ready(function(){
        <%-- Подсказка по параметрам --%>
        jsHelper.buildParamTooltip({
            selector: "a.param-description",
            paramIdPrefix: "param-tooltip-p",
            categoryName: "<c:out value="${_catalogItem.name}" />",
            categoryTermUrl: "<spring:url value='/category/{id}/term'><spring:param name='id' value='${_catalogItem.id}' /></spring:url>",
            right: true
        });
    });
    // ]]>
</script>
