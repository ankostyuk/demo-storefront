<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<tiles:useAttribute name="_path" />

<div class="catalog-item-path-plain">
    <%-- Пробел между </a> и </span> обязателен - IE bug --%>
    <c:forEach var="item" items="${_path}" varStatus="st">
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
            <span class="last">
                <a href="${itemUrl}"><c:out value="${item.name}" /></a>
            </span>
        </c:if>
    </c:forEach>
</div>
