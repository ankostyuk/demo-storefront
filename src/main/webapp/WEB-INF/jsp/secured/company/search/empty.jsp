<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div id="content-no-sidebar"><%-- TODO код объединить с кодом выдачи результатов поиска /search/empty --%>
    <c:if test="${not empty emptyText}">
        <p>Вы задали пустой поисковый запрос.</p>
    </c:if>
    <c:if test="${not empty emptyResult}">
        <p>По вашему запросу «<c:out value="${text}" />» ничего не найдено.</p>
    </c:if>
</div><!-- #content-->
