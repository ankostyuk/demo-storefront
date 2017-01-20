<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<tiles:useAttribute name="_baseUrlValue" />
<tiles:useAttribute name="_catalog" />
<tiles:useAttribute name="_showing" ignore="true" />
<tiles:useAttribute name="_sorting" ignore="true" />

<div class="catalog-tree-filter-box">
    <span class="h-list-title">Категории</span>
    <ul class="h-list items compact">
        <c:choose>
            <c:when test="${_catalog == 'MY'}">
                <li><span class="current">мои</span></li>
            </c:when>
            <c:otherwise>
                <li><span><a class="list-act" title="Показать мои категории" href="<spring:url value="${_baseUrlValue}" htmlEscape="true">
                                 <spring:param name="cat" value="my" />
                                 <c:if test="${not empty _showing}"><spring:param name="show" value="${_showing.alias}" /></c:if>
                                 <c:if test="${not empty _sorting}"><spring:param name="sort" value="${_sorting.alias}" /></c:if>
                             </spring:url>">мои</a></span>
                </li>
            </c:otherwise>
        </c:choose>
        <c:choose>
            <c:when test="${_catalog == 'ALL'}">
                <li><span class="current">все</span></li>
            </c:when>
            <c:otherwise>
                <li><span><a class="list-act" title="Показать все категории" href="<spring:url value="${_baseUrlValue}" htmlEscape="true">
                                 <spring:param name="cat" value="all" />
                                 <c:if test="${not empty _showing}"><spring:param name="show" value="${_showing.alias}" /></c:if>
                                 <c:if test="${not empty _sorting}"><spring:param name="sort" value="${_sorting.alias}" /></c:if>
                             </spring:url>">все</a></span>
                </li>
            </c:otherwise>
        </c:choose>
    </ul>
</div>