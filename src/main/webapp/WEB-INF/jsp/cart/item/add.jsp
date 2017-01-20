<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fn2" uri="http://www.nullpointer.ru/fn2/tags" %>

<div id="content-no-sidebar">
    <div class="plain-view">
        <h1>Добавление в список покупок</h1>
        <c:if test="${empty _actionInfo}">
            <c:if test="${empty matchesToAdd}">
                <p>
                    Вы ничего не выбрали для добавления.
                </p>
            </c:if>
            <c:if test="${not empty matchesToAdd}">
                <form action="<spring:url value="/cart/item/add" />" method="post">
                    <p>
                        Добавить в
                        <c:if test="${fn:length(cartList) <= 1}">
                            «<c:out value="${cart.name}" />»
                        </c:if>
                        <c:if test="${fn:length(cartList) > 1}">
                            <select name="cartId">
                                <c:forEach var="toCart" items="${cartList}">
                                    <option value="${toCart.id}" <c:if test="${toCart.id == cart.id}">selected="selected"</c:if>><c:out value="${toCart.name}" /></option>
                                </c:forEach>
                            </select>
                        </c:if>
                    </p>
                    <ul>
                        <c:forEach var="match" items="${matchesToAdd}">
                            <li>
                                <c:choose>
                                    <c:when test="${match.type == 'OFFER'}">
                                        Предложение <strong><c:out value="${match.offer.name}" /></strong>
                                        <input type="hidden" name="id" value="o${match.offer.id}" />
                                    </c:when>
                                    <c:when test="${match.type == 'MODEL'}">
                                        Модель <strong><c:out value="${match.model.name}" /></strong>
                                        <input type="hidden" name="id" value="m${match.model.id}" />
                                    </c:when>
                                </c:choose>
                            </li>
                        </c:forEach>
                    </ul>
                    <div class="field">
                        <input type="submit" value="Добавить" />
                        <c:if test="${not empty param.redirect}">
                            <input type="hidden" name="redirect" value="<c:out value="${param.redirect}" />" />
                            или <a href="<spring:url value="${param.redirect}" htmlEscape="true" />">вернуться назад</a>
                        </c:if>
                    </div>
                </form>
            </c:if>
        </c:if>
        <c:if test="${not empty _actionInfo && not _actionInfo.success}">
            <p>
                Невозможно добавить в список покупок, список покупок переполнен.
            </p>
            <p>
                <a rel="nofollow" href="<spring:url value="/cart/add" htmlEscape="true">
                       <spring:param name="redirect">/cart/item/add?${fn2:buildUrlParams('id', _actionInfo.idValues)}<c:if test="${not empty _actionInfo.redirect}">&redirect=${fn2:urlencode(_actionInfo.redirect)}</c:if></spring:param>
                   </spring:url>">Создать новый список</a>
            </p>
        </c:if>
    </div>
</div><!-- #content-->
