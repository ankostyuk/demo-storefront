<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cdn" uri="http://www.nullpointer.ru/cdn/tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<div id="content-no-sidebar">
    <h1><c:out value="${brand.name}" /></h1>
    <c:if test="${not empty brand.logo}">
        <div class="brand-logo">
            <a <c:if test="${not empty brand.site}">href="<c:out value='${brand.site}' />"</c:if>><img src="<cdn:url container="brand" key="${brand.logo}" />" alt="<c:out value="${brand.name}" />" /></a>
        </div>
    </c:if>
    <c:if test="${not empty brand.site}">
        <a href="<c:out value='${brand.site}' />"><c:out value="${fn:substringAfter(brand.site, '://')}" /></a>
    </c:if>
    <c:if test="${not empty rootSectionList}">
        <tiles:insertDefinition name="catalog-category-item-list">
            <tiles:putAttribute name="_sectionList" value="${rootSectionList}" />
            <tiles:putAttribute name="_sectionCategoryMap" value="${rootSectionCategoryMap}" />
            <tiles:putAttribute name="_maxColumnCount" value="${3}" />
            <tiles:putAttribute name="_urlParams">brand=${brand.id}</tiles:putAttribute>
        </tiles:insertDefinition>
    </c:if>
    <p class="brand-search-text">
        Найти <a href="<c:url value="/search"><c:param name='text' value='${brand.name}'/></c:url>"><c:out value="${brand.name}" /></a> в предложениях
    </p>
</div><!-- #content-->
