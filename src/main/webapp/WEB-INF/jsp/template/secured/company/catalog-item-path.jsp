<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>

<tiles:useAttribute name="_path" />
<tiles:useAttribute name="_catalog" ignore="true" />
<tiles:useAttribute name="_showing" ignore="true" />
<tiles:useAttribute name="_sorting" ignore="true" />
<tiles:useAttribute name="_lastLink" ignore="true" />


<c:forEach var="_item" items="${_path}" varStatus="_st">
    <c:set var="_linkUrl">
        <spring:url value="/secured/company/offers/{id}" htmlEscape="true">
            <spring:param name="id" value="${_item.id}" />
            <c:if test="${not empty _catalog}"><spring:param name="cat" value="${_catalog.alias}" /></c:if>
            <c:if test="${not empty _showing}"><spring:param name="show" value="${_showing.alias}" /></c:if>
            <c:if test="${not empty _sorting}"><spring:param name="sort" value="${_sorting.alias}" /></c:if>
        </spring:url>
    </c:set>
    <span <c:if test="${_st.last}">class="last"</c:if>>
        <%-- Ссылка в конце только если не первая страница --%>
        <c:if test="${not _st.last or _lastLink}"><a href="${_linkUrl}"></c:if>
            <c:out value="${_item.name}" />
            <c:if test="${not _st.last or _lastLink}">
                <%-- Пробел между </a> и </span> обязателен - IE bug --%>
            </a>
        </c:if>
    </span>
</c:forEach>