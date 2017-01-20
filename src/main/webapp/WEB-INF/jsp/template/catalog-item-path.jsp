<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<tiles:useAttribute name="catalogItemPath" />
<tiles:useAttribute name="_noLastLink" ignore="true" />
<c:if test="${empty _noLastLink}"><c:set var="_noLastLink" value="${false}"/></c:if>

<div class="catalog-item-path">
    <%--Пробел между </a> и </span> обязателен - IE bug --%>
    <span>
        <a href="<c:url value='/' />">Каталог</a>
    </span>
    <c:forEach var="item" items="${catalogItemPath}" varStatus="st">
        <c:set var="itemUrl">
            <c:if test="${item.type == 'SECTION'}">
                <spring:url value='/section/{id}'><spring:param name='id' value='${item.id}' /></spring:url>
            </c:if>
            <c:if test="${item.type == 'CATEGORY'}">
                <spring:url value='/category/{id}'><spring:param name='id' value='${item.id}' /></spring:url>
            </c:if>
        </c:set>
        <c:if test="${not st.last}">
            <span>
                <a href="${itemUrl}"><c:out value="${item.name}" /></a>
            </span>
        </c:if>
        <c:if test="${st.last}">
            <c:if test="${not _noLastLink}">
                <span class="last">
                    <a href="${itemUrl}"><c:out value="${item.name}" /></a>
                </span>
            </c:if>
            <c:if test="${_noLastLink}">
                <span class="last"><c:out value="${item.name}" /></span>
            </c:if>
        </c:if>
    </c:forEach>
</div>
