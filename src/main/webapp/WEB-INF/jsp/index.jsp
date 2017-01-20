<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>

<div id="content-no-sidebar">
    <c:if test="${empty itemList}">
        <p>
            В каталоге пока нет предложений.
        </p>
        <p>
            Если вы продаете товары для строительства и ремонта, то можете <a href="<c:url value="/registration/company/partner" />">зарегистрироваться</a> и разместить свои предложения в каталоге.
        </p>
    </c:if>

    <c:if test="${not empty itemList}">
        <tiles:insertDefinition name="catalog-item-list">
            <tiles:putAttribute name="_itemList" value="${itemList}" />
            <tiles:putAttribute name="_subItemMap" value="${popularMap}" />
            <tiles:putAttribute name="_maxColumnCount" value="${3}" />
        </tiles:insertDefinition>
    </c:if>

    <p class="info">
        В нашем каталоге более 300 категорий, просто мы скрыли пустые.
    </p>
    
</div><!-- #content-->
