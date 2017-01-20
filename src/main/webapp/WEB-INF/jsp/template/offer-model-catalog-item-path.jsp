<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<tiles:useAttribute name="_categoryPath" />
<tiles:useAttribute name="_offerModel" />

<div class="catalog-item-path">
    <%--Пробел между </a> и </span> обязателен - IE bug --%>
    <span>
        <a href="<c:url value='/' />">Каталог</a>
    </span>
    <c:forEach var="item" items="${_categoryPath}">
        <c:set var="itemUrl">
            <c:if test="${item.type == 'SECTION'}">
                <spring:url value='/section/{id}'><spring:param name='id' value='${item.id}' /></spring:url>
            </c:if>
            <c:if test="${item.type == 'CATEGORY'}">
                <spring:url value='/category/{id}'><spring:param name='id' value='${item.id}' /></spring:url>
            </c:if>
        </c:set>
        <span>
            <a href="${itemUrl}"><c:out value="${item.name}" /></a>
        </span>
    </c:forEach>
    <span class="last">
        <a href="<spring:url value='/model/{id}'><spring:param name='id' value='${_offerModel.id}' /></spring:url>" title="Посмотреть информацию о модели"><c:out value="${_offerModel.name}" /></a>
    </span>
</div>
