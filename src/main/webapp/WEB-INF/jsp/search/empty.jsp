<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>

<div id="content-no-sidebar">
    <c:if test="${not empty catalogItemPath}">
        <tiles:insertDefinition name="catalog-item-path">
            <tiles:putAttribute name="catalogItemPath" value="${catalogItemPath}" />
        </tiles:insertDefinition>
    </c:if>
    <c:if test="${not empty emptyText}">
        <p>Вы задали пустой поисковый запрос.</p>
    </c:if>
    <c:if test="${not empty emptyResult}">
        <p>По вашему запросу «<c:out value="${text}" />» ничего не найдено.</p>
        <c:if test="${regionAware}">
            <p class="info">
                Попробуйте включить отображение предложений <a id="region-info-set-link" class="act" rel="nofollow" href="<c:url value='/settings/region'/>">других регионов</a>.
            </p>
        </c:if>
        <c:if test="${not empty catalogItemPath}">
            <p>
                <a href="<c:url value="/search"><c:param name='text' value='${text}'/></c:url>">Поискать по всей Бильдике</a>
            </p>
        </c:if>
    </c:if>
</div><!-- #content-->
