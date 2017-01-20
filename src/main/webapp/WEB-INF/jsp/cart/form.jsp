<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div id="content-no-sidebar">
    <div class="plain-view">
        <c:if test="${empty _actionInfo}">
            <c:set var="newCart" value="${empty cart.id}" />

            <c:if test="${newCart}">
                <h1>Добавление списка покупок</h1>
            </c:if>
            <c:if test="${not newCart}">
                <h1>Редактирование списка покупок</h1>
            </c:if>
            <form:form modelAttribute="cart">
                <ul class="field-group">
                    <li>
                        <form:label cssClass="text" path="name">Наименование</form:label>
                        <form:input cssClass="text" path="name" />
                        <form:errors cssClass="error" path="name" />
                    </li>
                    <li>
                        <form:label cssClass="text" path="description">Комментарий</form:label>
                        <form:textarea rows="4" cols="20" cssClass="text" path="description" />
                        <form:errors cssClass="error" path="description" />
                    </li>
                </ul>
                <div class="submit">
                    <c:if test="${newCart}">
                        <input type="submit" value="Добавить список покупок" />
                    </c:if>
                    <c:if test="${not newCart}">
                        <input type="submit" value="Сохранить список покупок" />
                    </c:if>
                </div>
            </form:form>
        </c:if>
        <c:if test="${not empty _actionInfo && not _actionInfo.success}">
            <c:if test="${_actionInfo.type == 'CART_ADD'}">
                <p>
                    Невозможно создать список покупок, превышено максимальное количество списка покупок.
                </p>
            </c:if>
            <c:if test="${not empty _actionInfo.redirect}">
                <p>
                    <a href="<spring:url value="${_actionInfo.redirect}" htmlEscape="true" />">Вернуться назад</a>
                </p>
           </c:if>
        </c:if>
    </div>
</div><!-- #content-->
