<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn2" uri="http://www.nullpointer.ru/fn2/tags" %>
<%@ taglib prefix="cdn" uri="http://www.nullpointer.ru/cdn/tags" %>

<tiles:useAttribute name="_catalogItem" />
<tiles:useAttribute name="paramModel" />
<tiles:useAttribute name="paramInput" />
<tiles:useAttribute name="paramCountGroupMap" />

<div class="match-param">
    <ul class="match-param-table-filter" style="display: none;">
        <li><a id="param-filter-option-base" class="pseudo act">Основные характеристики</a></li>
        <li class="current"><a id="param-filter-option-all" class="pseudo act">Все характеристики</a></li>
    </ul>
    <table class="match-param-table">
        <tbody>
            <c:set var="prevGroupId" value="${null}" />
            <c:forEach var="p" items="${paramModel.paramList}">
                <c:if test="${p.paramGroupId != prevGroupId && paramCountGroupMap[p.paramGroupId] > 0}">
                    <c:set var="prevGroupId" value="${p.paramGroupId}" />
                    <c:set var="cssFilter" value="${'filter-all'}" />
                    <c:forEach var="pBase" items="${paramModel.paramList}">
                        <c:if test="${not empty paramInput.p[pBase.id] && pBase.paramGroupId == prevGroupId && pBase.base}"><c:set var="cssFilter" value="${'filter-all filter-base'}" /></c:if>
                    </c:forEach>
                    <tr class="filterable group ${cssFilter}"><td class="group-name" colspan="2"><h4><c:out value="${paramModel.groupMap[p.paramGroupId].name}" /></h4></td></tr>
                </c:if>
                <c:set var="paramValue" value="${paramInput.p[p.id]}" />
                <c:if test="${not empty paramValue}">
                    <tr class="filterable param filter-all<c:if test="${p.base}">${' filter-base'}</c:if>">
                        <td class="name"><c:out value="${p.name}" /><c:if test="${not empty p.description}">&nbsp;<a id="param-tooltip-p${p.id}" class="param-description" href="<spring:url value='/category/{id}/term'><spring:param name='id' value='${_catalogItem.id}' /></spring:url>#${fn2:xmlid(p.id)}">&nbsp;</a></c:if></td>
                        <td class="value">
                            <c:choose>
                                <c:when test="${p.type == 'BOOLEAN'}">
                                    <c:if test="${!empty paramValue && paramValue == true}"><img src="<cdn:url container="css" key="${'/i/param-value-true.png'}" />" alt="<c:out value="${p.trueName}" />" /></c:if>
                                    <c:if test="${!empty paramValue && paramValue == false}"><img src="<cdn:url container="css" key="${'/i/param-value-false.png'}" />" alt="<c:out value="${p.falseName}" />" /></c:if>
                                </c:when>
                                <c:when test="${p.type == 'NUMBER'}">
                                    <c:set var="paramUnit" value="${paramModel.unitMap[p.unitId]}" />
                                    <fmt:formatNumber value="${paramValue}" minFractionDigits="${p.precision}" maxFractionDigits="${p.precision}" />&nbsp;<abbr title="<c:out value="${paramUnit.name}" />">${fn2:htmlformula(paramUnit.abbreviation)}</abbr>
                                </c:when>
                                <c:when test="${p.type == 'SELECT'}">
                                    <c:forEach var="option" items="${paramModel.selectOptionMap[p.id]}">
                                        <c:if test="${paramValue == option.id}">
                                            <c:out value="${option.name}" />
                                        </c:if>
                                    </c:forEach>
                                </c:when>
                            </c:choose>
                        </td>
                    </tr>
                </c:if>
            </c:forEach>
            <tr>
                <td colspan="2"><p class="info">Перед покупкой уточняйте технические характеристики и комплектацию у продавца</p></td>
            </tr>
            <%-- TODO ? Сообщить об ошибке в описании --%>
        </tbody>
    </table>
</div>

<script type="text/javascript">
    // <![CDATA[
    jsHelper.document.ready(function(){
        <%-- Фильтр (вкладки) параметров --%>
        var paramFilter = $("ul.match-param-table-filter");
        var paramTable = $("table.match-param-table");

        // Возможность фильтрации только если множество основных параметров > 0 и множества основных и всех параметров не совпадают
        var allCount = paramTable.find("tr.filterable.param.filter-all").length;
        var baseCount = paramTable.find("tr.filterable.param.filter-base").length;
        if (baseCount > 0 && baseCount !== allCount) {
            paramFilter.find("> li > a").click(function(){
                var link = $(this);
                if (!link.parent().hasClass("current")) {
                    paramFilter.find("> li.current").removeClass("current");
                    link.parent().addClass("current");
                    var filter = "filter-" + link.attr("id").substr("param-filter-option-".length);
                    paramTable.find("tr.filterable").each(function(){
                        var filterable = $(this);
                        if (filterable.hasClass(filter)) {
                            filterable.removeClass("hide");
                        } else {
                            filterable.addClass("hide");
                        }
                    });

                    // Убрать наименование группы, если после фильтрации остается только одна группа параметров
                    var visibleGroupCount = 0;
                    var group = paramTable.find("tr.filterable.group").removeClass("single").each(function(){
                        if (!$(this).hasClass("hide")) {
                            visibleGroupCount++;
                        }
                    });
                    if (visibleGroupCount === 1) {
                        group.addClass("single");
                    }
                }
            });

            $("#param-filter-option-base").click();

            paramFilter.show();
        }
        
        <%-- Подсказка по параметрам --%>
        jsHelper.buildParamTooltip({
            selector: "table.match-param-table td.name .param-description",
            paramIdPrefix: "param-tooltip-p",
            categoryName: "<c:out value="${_catalogItem.name}" />",
            categoryTermUrl: "<spring:url value='/category/{id}/term'><spring:param name='id' value='${_catalogItem.id}' /></spring:url>",
            right: true
        });
    });
    // ]]>
</script>

