<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fn2" uri="http://www.nullpointer.ru/fn2/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="cdn" uri="http://www.nullpointer.ru/cdn/tags" %>

<c:set var="searchText" value="${not empty correctedText ? correctedText : text}" />
<c:set var="baseUrlValue" value="/secured/company/search?text=${fn2:urlencode(searchText)}" />

<div id="content-no-sidebar">

    <c:if test="${not empty correctedText}">
        <p>Возможно, вы искали «<c:out value="${correctedText}" />». По запросу «<em><c:out value="${text}" /></em>» ничего не найдено.</p>
    </c:if>
    <p class="info"><%-- TODO код объединить с кодом выдачи результатов поиска /search/result --%>
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
    </p>

    <c:set var="redirectUrlValue" value="${baseUrlValue}&page=${offerResult.pageNumber}" />
    <div class="offer-list">
        <c:forEach var="offer" items="${offerResult.list}">
            <tiles:insertDefinition name="secured-company-offer-list-item">
                <tiles:putAttribute name="_offer" value="${offer}" />
                <c:if test="${pathMap != null}">
                    <tiles:putAttribute name="_categoryPath" value="${pathMap[offer.categoryId]}" />
                </c:if>
                <tiles:putAttribute name="_defaultCurrency" value="${defaultCurrency}" />
                <tiles:putAttribute name="_unit" value="${unitMap[offer.categoryId]}" />
                <tiles:putAttribute name="_redirect" value="${redirectUrlValue}" />
            </tiles:insertDefinition>
        </c:forEach>
    </div>

    <c:if test="${offerResult.total > offerResult.pageSize}">
        <tiles:insertTemplate template="/WEB-INF/jsp/template/common-pager.jsp">
            <tiles:putAttribute name="queryResult" value="${offerResult}" />
            <tiles:putAttribute name="pagerUrl">
                <spring:url value="${baseUrlValue}" htmlEscape="true">
                    <%-- TODO "объединие" <spring:param name="show" value="${showing.alias}" />
                    <spring:param name="sort" value="${sorting.alias}" />--%>
                </spring:url>
            </tiles:putAttribute>
            <tiles:putAttribute name="pagerUrlHasParams" value="true" />
            <tiles:putAttribute name="displayedPageSideCount" value="${offerDisplayedPageSideCount}" />
        </tiles:insertTemplate>
    </c:if>

</div><!-- #content-->
