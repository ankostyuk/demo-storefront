<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fn2" uri="http://www.nullpointer.ru/fn2/tags" %>

<c:set var="searchText" value="${not empty correctedText ? correctedText : text}" />

<c:set var="baseUrlValue">/search?text=${fn2:urlencode(searchText)}<c:if test="${not empty catalogItemId}">&item=${catalogItemId}</c:if></c:set>
<c:set var="pageUrlParam"><c:if test="${offerResult.pageNumber > 1}">&page=${offerResult.pageNumber}</c:if></c:set>
<c:set var="redirectUrlValue">${baseUrlValue}${pageUrlParam}</c:set>

<div id="content-no-sidebar">
    <c:if test="${not empty catalogItemPath}">
        <tiles:insertDefinition name="catalog-item-path">
            <tiles:putAttribute name="catalogItemPath" value="${catalogItemPath}" />
        </tiles:insertDefinition>
    </c:if>
    <c:if test="${not empty correctedText}">
        <p>Возможно, вы искали «<c:out value="${correctedText}" />». По запросу «<em><c:out value="${text}" /></em>» ничего не найдено.</p>
    </c:if>
    <c:if test="${empty offerResult}">
        <p>
            «<c:out value="${searchText}" />» в предложениях не найдено.
        </p>
        <c:if test="${regionAware}">
            <p class="info">
                Попробуйте включить отображение предложений <a id="region-info-set-link" class="act" rel="nofollow" href="<c:url value='/settings/region'/>">других регионов</a>.
            </p>
        </c:if>
    </c:if>
    <c:if test="${not empty offerResult && offerResult.total > 0}">
        <p class="info">
            По запросу «<c:out value="${searchText}" />» нашлось
            <c:if test="${not offerTotalMore}">
                <c:out value="${offerResult.total} ${fn2:ruplural(offerResult.total, 'предложение/предложения/предложений')}" />
            </c:if>
            <c:if test="${offerTotalMore}">
                более <c:out value="${offerResult.total - 1} ${fn2:ruplural(offerResult.total - 1, 'предложения/предложений/предложений')}" />
            </c:if>
            <c:if test="${offerResult.total != 1 && offerResult.total <= offerResult.pageSize}">, показаны все.</c:if>
            <c:if test="${offerResult.total > offerResult.pageSize}">, показаны с ${offerResult.firstNumber} по ${offerResult.lastNumber}.</c:if>
            <c:if test="${offerResult.total == 1}">.</c:if>
            <c:if test="${empty userSession.settings.region}">
                Кстати, вы можете указать <a id="region-info-set-link" class="act" rel="nofollow" href="<c:url value='/settings/region'/>">свой регион</a>.
            </c:if>
        </p>
        <div class="match-list">
            <c:forEach var="match" items="${offerResult.list}">
                <c:if test="${match.type == 'OFFER'}">
                    <tiles:insertDefinition name="match-item-offer">
                        <tiles:putAttribute name="match" value="${match}" />
                        <tiles:putAttribute name="categoryPath" value="${categoryPathMap[match.offer.categoryId]}" />
                        <tiles:putAttribute name="redirect" value="${redirectUrlValue}"/>
                        <tiles:putAttribute name="_unit" value="${categoryUnitMap[match.offer.categoryId]}" />
                    </tiles:insertDefinition>
                </c:if>
                <c:if test="${match.type == 'MODEL'}">
                    <tiles:insertDefinition name="match-item-model">
                        <tiles:putAttribute name="match" value="${match}" />
                        <tiles:putAttribute name="categoryPath" value="${categoryPathMap[match.model.categoryId]}" />
                        <tiles:putAttribute name="redirect" value="${redirectUrlValue}"/>
                        <tiles:putAttribute name="_unit" value="${categoryUnitMap[match.model.categoryId]}" />
                    </tiles:insertDefinition>
                </c:if>
            </c:forEach>
            <tiles:insertDefinition name="match-tools">
                <tiles:putAttribute name="_redirect" value="${redirectUrlValue}" />
            </tiles:insertDefinition>
        </div>
        <c:if test="${offerResult.total > offerResult.pageSize}">
            <tiles:insertTemplate template="/WEB-INF/jsp/template/common-pager.jsp">
                <tiles:putAttribute name="queryResult" value="${offerResult}" />
                <tiles:putAttribute name="pagerUrl">
                    <spring:url value="${baseUrlValue}" htmlEscape="true" />
                </tiles:putAttribute>
                <tiles:putAttribute name="pagerUrlHasParams" value="true" />
                <tiles:putAttribute name="displayedPageSideCount" value="${offerDisplayedPageSideCount}" />
            </tiles:insertTemplate>
        </c:if>
    </c:if>
    <c:if test="${not empty catalogItemResult}">
        <div class="search-result-catalog-item">
            <h3>Категории</h3>
            <ul class="v-list">
                <c:forEach var="path" items="${catalogItemResult}">
                    <li class="">
                        <tiles:insertDefinition name="catalog-item-path-plain">
                            <tiles:putAttribute name="_path" value="${path}" />
                        </tiles:insertDefinition>
                    </li>
                </c:forEach>
            </ul>
        </div>
    </c:if>
    <c:if test="${not empty brandResult}">
        <div class="search-result-brand">
            <h3>Бренды</h3>
            <p class="brand-list">
                <c:forEach var="brand" items="${brandResult}" varStatus="st">
                    <%--Пробел между </a> и </span> обязателен - IE bug --%>
                    <span <c:if test="${st.last}">class="last"</c:if>>
                        <a href="<spring:url value='/brand/{id}'>
                               <spring:param name='id' value='${brand.id}' />
                           </spring:url>" ><c:out value="${brand.name}" /></a>
                    </span>
                </c:forEach>
            </p>
        </div>
    </c:if>
</div><!-- #content-->
