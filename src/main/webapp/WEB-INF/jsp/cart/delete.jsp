<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<div id="content-no-sidebar">
    <div class="plain-view">
        <c:if test="${empty _actionInfo}">
            <h1>Удаление списка покупок</h1>
            <p>Вы уверены, что хотите удалить «<strong><c:out value="${cart.name}" /></strong>»?</p>
            <form:form modelAttribute="cart">
                <div class="field">
                    <input type="submit" value="Удалить список" />
                    <c:if test="${not empty param.redirect}">
                        или <a href="<spring:url value="${param.redirect}" htmlEscape="true" />">вернуться назад</a>
                    </c:if>
                </div>
            </form:form>
        </c:if>
        <c:if test="${not empty _actionInfo && not _actionInfo.success}">
            <p>
                Невозможно удалить список покупок. Пожалуйста, попробуйте позже.
            </p>
            <p>
                <c:if test="${not empty _actionInfo.redirect}">
                    <a href="<spring:url value="${_actionInfo.redirect}" htmlEscape="true" />">Вернуться назад</a>
                </c:if>
            </p>
        </c:if>
    </div>
</div><!-- #content-->
