<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>

<div id="content-no-sidebar">
    <tiles:insertDefinition name="catalog-item-path">
        <tiles:putAttribute name="catalogItemPath" value="${path}" />
        <tiles:putAttribute name="_noLastLink" value="${true}" />
    </tiles:insertDefinition>
    <c:if test="${empty itemList}">
        <p class="info">
            К сожалению, в этом разделе пока еще нет предложений.
        </p>
    </c:if>
    <c:if test="${not empty itemList}">
        <tiles:insertDefinition name="catalog-item-list">
            <tiles:putAttribute name="_itemList" value="${itemList}" />
            <tiles:putAttribute name="_subItemMap" value="${popularMap}" />
            <tiles:putAttribute name="_maxColumnCount" value="${3}" />
        </tiles:insertDefinition>
    </c:if>
</div><!-- #content-->
