<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fn2" uri="http://www.nullpointer.ru/fn2/tags" %>

<c:set var="redirect">/category/${category.id}?sort=${sorting.alias}&page=${queryResult.pageNumber}<c:if test="${not empty filterUrlParams}">&${filterUrlParams}</c:if></c:set>

<div id="container">
    <div id="content-right-sidebar">
        <tiles:insertDefinition name="catalog-item-path">
            <tiles:putAttribute name="catalogItemPath" value="${path}" />
        </tiles:insertDefinition>
        <c:if test="${queryResult.total > 0}">
            <p class="info">
                Всего <c:out value="${queryResult.total} ${fn2:ruplural(queryResult.total, 'предложение/предложения/предложений')}" /><c:if test="${queryResult.total > 1}">, показаны
                    <c:if test="${queryResult.total <= queryResult.pageSize}">все.</c:if>
                    <c:if test="${queryResult.total > queryResult.pageSize}">с ${queryResult.firstNumber} по ${queryResult.lastNumber}.</c:if>
                </c:if>
                <c:if test="${queryResult.total <= 1}">.</c:if>
                <c:if test="${empty userSession.settings.region}">
                    Кстати, вы можете указать <a id="region-info-set-link" class="act" rel="nofollow" href="<c:url value='/settings/region'/>">свой регион</a>.
                </c:if>
            </p>
            <tiles:insertDefinition name="matches" />
        </c:if>
        <c:if test="${queryResult.total == 0}">
            <c:if test="${empty filterUrlParams}">
                <c:if test="${not regionAware}">
                    <p class="info">
                        К сожалению, в этой категории пока еще нет предложений.
                    </p>
                </c:if>
                <c:if test="${regionAware}">
                    <p class="info">
                        Нет предложений в вашем регионе.
                    </p>
                    <p class="info">
                        Попробуйте включить отображение предложений <a id="region-info-set-link" class="act" rel="nofollow" href="<c:url value='/settings/region'/>">других регионов</a>.
                    </p>
                </c:if>
            </c:if>
            <c:if test="${not empty filterUrlParams}">
                <p class="info">
                    Нет предложений соответствующих заданным характеристикам<c:if test="${regionAware}">&nbsp;в вашем регионе</c:if>.
                </p>
                <c:if test="${regionAware}">
                    <p class="info">
                        Попробуйте включить отображение предложений <a id="region-info-set-link" class="act" rel="nofollow" href="<c:url value='/settings/region'/>">других регионов</a>.
                    </p>
                </c:if>
            </c:if>
        </c:if>
        <tiles:insertDefinition name="match-tools">
            <tiles:putAttribute name="_redirect" value="${redirect}" />
        </tiles:insertDefinition>
    </div><!-- #content-right-sidebar-->
</div><!-- #container-->

<div class="sidebar" id="sideRight">
    <div id="comparison-list-box">
        <c:if test="${fn:length(comparisonList) > 0}">
            <tiles:insertDefinition name="category-comparison-list">
                <tiles:putAttribute name="_categoryId" value="${category.id}" />
                <tiles:putAttribute name="_comparisonList" value="${comparisonList}" />
                <tiles:putAttribute name="_userSession" value="${userSession}" />
                <tiles:putAttribute name="_redirect" value="${redirect}" />
            </tiles:insertDefinition>
        </c:if>
    </div>
    <c:if test="${queryResult.total > 1 || not empty filterUrlParams}">
        <tiles:insertDefinition name="match-filter">
            <tiles:putAttribute name="_catalogItem" value="${catalogItem}" />
            <tiles:putAttribute name="actionUrl">/category/${catalogItem.id}</tiles:putAttribute>
            <tiles:putAttribute name="settings" value="${userSession.settings}" />
            <tiles:putAttribute name="sorting" value="${sorting}" />
            <tiles:putAttribute name="regionAware" value="${regionAware}" />
            <tiles:putAttribute name="matchFilter" value="${matchFilter}" />
            <tiles:putAttribute name="priceInterval" value="${priceInterval}" />
            <tiles:putAttribute name="unit" value="${unit}" />
            <tiles:putAttribute name="filterUrlParams" value="${filterUrlParams}" />
            <tiles:putAttribute name="brandList" value="${brandList}" />
            <tiles:putAttribute name="paramModel" value="${paramModel}" />
        </tiles:insertDefinition>
    </c:if>
</div><!-- .sidebar#sideRight -->
