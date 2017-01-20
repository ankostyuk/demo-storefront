<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn2" uri="http://www.nullpointer.ru/fn2/tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>

<c:set var="baseUrlValue"><spring:url value='/category/{id}'><spring:param name='id' value='${catalogItem.id}' /></spring:url></c:set>

<c:if test="${queryResult.total > 1}">
    <div class="sort-box">
        <span class="title">Сортировать по</span>
        <ul class="h-list">
            <c:if test="${sorting == 'PRICE_ASCENDING'}">
                <li class="current">
                    <a class="composite sort-asc" title="Показать более дорогие" href="${baseUrlValue}?sort=price-desc&amp;page=${queryResult.pageNumber}<c:if test="${not empty filterUrlParams}"><c:out value="&${filterUrlParams}" /></c:if>"><span class="link">цене за ${fn2:htmlformula(unit.abbreviation)}</span></a>
                </li>
            </c:if>
            <c:if test="${sorting == 'PRICE_DESCENDING'}">
                <li class="current">
                    <a class="composite sort-desc" title="Показать более дешёвые" href="${baseUrlValue}?sort=price-asc&amp;page=${queryResult.pageNumber}<c:if test="${not empty filterUrlParams}"><c:out value="&${filterUrlParams}" /></c:if>"><span class="link">цене за ${fn2:htmlformula(unit.abbreviation)}</span></a>
                </li>
            </c:if>
        </ul>
    </div>
</c:if>
<div class="match-list">
    <c:forEach var="match" items="${queryResult.list}">
        <c:if test="${match.type == 'OFFER'}">
            <tiles:insertDefinition name="match-item-offer">
                <tiles:putAttribute name="match" value="${match}" />
                <tiles:putAttribute name="redirect">/category/${catalogItem.id}?sort=${sorting.alias}&page=${queryResult.pageNumber}<c:if test="${not empty filterUrlParams}">&${filterUrlParams}</c:if></tiles:putAttribute>
            </tiles:insertDefinition>
        </c:if>
        <c:if test="${match.type == 'MODEL'}">
            <tiles:insertDefinition name="match-item-model">
                <tiles:putAttribute name="match" value="${match}" />
                <tiles:putAttribute name="redirect">/category/${catalogItem.id}?sort=${sorting.alias}&page=${queryResult.pageNumber}<c:if test="${not empty filterUrlParams}">&${filterUrlParams}</c:if></tiles:putAttribute>
            </tiles:insertDefinition>
        </c:if>
    </c:forEach>
</div>
<c:if test="${queryResult.total > queryResult.pageSize}">
    <tiles:insertTemplate template="/WEB-INF/jsp/template/common-pager.jsp">
        <tiles:putAttribute name="queryResult" value="${queryResult}" />
        <tiles:putAttribute name="pagerUrl">${baseUrlValue}?sort=${sorting.alias}<c:if test="${not empty filterUrlParams}"><c:out value="&${filterUrlParams}" /></c:if></tiles:putAttribute>
        <tiles:putAttribute name="pagerUrlHasParams" value="true" />
    </tiles:insertTemplate>
</c:if>
