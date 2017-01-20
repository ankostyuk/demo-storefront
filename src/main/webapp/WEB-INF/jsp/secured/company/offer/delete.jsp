<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>

<div id="content-no-sidebar">

    <div class="catalog-item-path">
        <tiles:insertDefinition name="secured-company-catalog-item-path">
            <tiles:putAttribute name="_path" value="${path}" />
            <tiles:putAttribute name="_lastLink" value="${true}" />
        </tiles:insertDefinition>
    </div>

    <h1>Удаление предложения</h1>
    <p>Вы уверены что хотите удалить предложение «<strong><c:out value="${offer.name}" /></strong>»?</p>
    <form:form modelAttribute="offer" cssClass="plain">
        <div class="submit">
            <input type="submit" value="Удалить предложение" />
            <c:if test="${not empty param.redirect}">
                или <a href="<spring:url value="${param.redirect}" htmlEscape="true" />">вернуться назад</a>
            </c:if>
        </div>
    </form:form>

</div><!-- #content-->
