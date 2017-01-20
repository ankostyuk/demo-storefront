<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cdn" uri="http://www.nullpointer.ru/cdn/tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fn2" uri="http://www.nullpointer.ru/fn2/tags" %>

<div id="container">
    <div id="content-right-sidebar" class="model-page">
        <div class="model-info">
            <tiles:insertDefinition name="catalog-item-path">
                <tiles:putAttribute name="catalogItemPath" value="${modelCategoryPath}" />
                <tiles:putAttribute name="_noLastLink" value="${false}" />
            </tiles:insertDefinition>
            <h1><c:out value="${model.name}" /></h1>
            <c:if test="${empty modelInfo}">
                    <c:if test="${not regionAware}">
                        <p class="info">
                            Нет в продаже.
                        </p>
                    </c:if>
                    <c:if test="${regionAware}">
                        <p class="info">
                            Нет в продаже в вашем регионе.
                        </p>
                        <p class="info">
                            Попробуйте включить отображение предложений <a id="region-info-set-link" class="act" rel="nofollow" href="<c:url value='/settings/region'/>">других регионов</a>.
                        </p>
                    </c:if>
                    <p>
                        <a class="description" href="<spring:url value='/model/{id}'><spring:param name='id' value='${model.id}' /></spring:url>">
                            Описание
                        </a>
                    </p>
            </c:if>
            <c:if test="${not empty modelInfo}">
                <p class="price-info">
                    <tiles:insertDefinition name="model-price">
                        <tiles:putAttribute name="modelInfo" value="${modelInfo}" />
                        <tiles:putAttribute name="settings" value="${userSession.settings}" />
                        <tiles:putAttribute name="_unit" value="${unit}" />
                    </tiles:insertDefinition>
                    <span>
                        <a class="description" href="<spring:url value='/model/{id}'><spring:param name='id' value='${model.id}' /></spring:url>">
                            Описание
                        </a>
                    </span>
                </p>
            </c:if>
        </div>
        <c:if test="${not empty modelInfo}">
            <c:if test="${queryResult.total > 0}">
                <c:set var="redirect">/model/${model.id}/offers?sort=${sorting.alias}&page=${queryResult.pageNumber}<c:if test="${not empty filterUrlParams}">&${filterUrlParams}</c:if></c:set>
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
                <c:set var="baseUrlValue"><spring:url value='/model/{id}/offers'><spring:param name='id' value='${model.id}' /></spring:url></c:set>
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
                        <tiles:insertDefinition name="match-item-offer">
                            <tiles:putAttribute name="match" value="${match}" />
                            <tiles:putAttribute name="redirect" value="${redirect}" />
                        </tiles:insertDefinition>
                    </c:forEach>
                </div>
                <c:if test="${queryResult.total > queryResult.pageSize}">
                    <tiles:insertTemplate template="/WEB-INF/jsp/template/common-pager.jsp">
                        <tiles:putAttribute name="queryResult" value="${queryResult}" />
                        <tiles:putAttribute name="pagerUrl">${baseUrlValue}?sort=${sorting.alias}<c:if test="${not empty filterUrlParams}"><c:out value="&${filterUrlParams}" /></c:if></tiles:putAttribute>
                        <tiles:putAttribute name="pagerUrlHasParams" value="true" />
                    </tiles:insertTemplate>
                </c:if>
                <tiles:insertDefinition name="match-tools">
                    <tiles:putAttribute name="_redirect" value="${redirect}" />
                </tiles:insertDefinition>
            </c:if>
            <c:if test="${queryResult.total == 0}">
                <c:if test="${not empty filterUrlParams}">
                    <p class="info">
                        Нет предложений соответствующих заданной цене<c:if test="${regionAware}">&nbsp;в вашем регионе</c:if>.
                    </p>
                    <c:if test="${regionAware}">
                        <p class="info">
                            Попробуйте включить отображение предложений <a id="region-info-set-link" class="act" rel="nofollow" href="<c:url value='/settings/region'/>">других регионов</a>.
                        </p>
                    </c:if>
                </c:if>
            </c:if>
        </c:if>
    </div><!-- #content-right-sidebar-->
</div><!-- #container-->

<div class="sidebar" id="sideRight">
    <c:if test="${queryResult.total > 1 || not empty filterUrlParams}">
        <tiles:insertDefinition name="match-filter">
            <tiles:putAttribute name="_catalogItem" value="${catalogItem}" />
            <tiles:putAttribute name="actionUrl">/model/${model.id}/offers</tiles:putAttribute>
            <tiles:putAttribute name="settings" value="${userSession.settings}" />
            <tiles:putAttribute name="sorting" value="${sorting}" />
            <tiles:putAttribute name="regionAware" value="${regionAware}" />
            <tiles:putAttribute name="matchFilter" value="${matchFilter}" />
            <tiles:putAttribute name="priceInterval" value="${priceInterval}" />
            <tiles:putAttribute name="unit" value="${unit}" />
            <tiles:putAttribute name="filterUrlParams" value="${filterUrlParams}" />
        </tiles:insertDefinition>
    </c:if>
</div><!-- .sidebar#sideRight -->
