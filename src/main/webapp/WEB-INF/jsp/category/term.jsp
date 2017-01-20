<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fn2" uri="http://www.nullpointer.ru/fn2/tags" %>

<div id="container">
    <div id="content-right-sidebar" class="category-term-page">
        <tiles:insertDefinition name="catalog-item-path">
            <tiles:putAttribute name="catalogItemPath" value="${path}" />
            <tiles:putAttribute name="_noLastLink" value="${false}" />
        </tiles:insertDefinition>
        <h1>Словарь категории</h1>
        <c:if test="${empty paramList}">
            <p class="info">
                Словарь пуст
            </p>
        </c:if>
        <c:if test="${not empty paramList}">
            <c:forEach var="p" items="${paramList}">
                <tiles:insertDefinition name="category-term">
                    <tiles:putAttribute name="_param" value="${p}" />
                </tiles:insertDefinition>
            </c:forEach>
        </c:if>
    </div><!-- #content-right-sidebar-->
</div><!-- #container-->

<div class="sidebar" id="sideRight">
    <c:if test="${not empty paramList && fn:length(paramList) > 1}">
        <ul class="v-list inside term-toc">
            <c:forEach var="p" items="${paramList}">
                <li><a class="act" href="#${fn2:xmlid(p.id)}"><c:out value="${p.name}" /></a></li>
            </c:forEach>
        </ul>
    </c:if>
</div><!-- .sidebar#sideRight -->
