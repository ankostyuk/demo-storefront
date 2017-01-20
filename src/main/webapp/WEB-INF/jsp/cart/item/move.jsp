<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn2" uri="http://www.nullpointer.ru/fn2/tags" %>

<div id="content-no-sidebar">
    <div class="plain-view">
        <c:if test="${empty _actionInfo}">
            <h1>Перемещение из «<c:out value="${cart.name}" />»</h1>
            <c:if test="${empty matchesToMove}">
                <p>
                    Вы ничего не выбрали для перемещения.
                </p>
            </c:if>
            <c:if test="${not empty matchesToMove}">
                <form action="<spring:url value="/cart/{cartId}/item/move">
                          <spring:param  name="cartId" value="${cart.id}" />
                      </spring:url>" method="post">
                    <ul>
                        <c:forEach var="match" items="${matchesToMove}">
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
                    <p>
                        в
                        <select name="toCartId">
                            <c:forEach var="toCart" items="${cartList}">
                                <c:if test="${cart.id != toCart.id}">
                                    <option value="${toCart.id}" <c:if test="${toCart.id == toCartId}">selected="selected"</c:if>><c:out value="${toCart.name}" /></option>
                                </c:if>
                            </c:forEach>
                        </select>
                    </p>
                    <div class="field">
                        <input type="submit" value="Переместить" />
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
                Невозможно переместить в выбранный список покупок, список покупок переполнен.
            </p>
            <p>
                <a rel="nofollow" href="<spring:url value="/cart/add" htmlEscape="true">
                       <spring:param name="redirect">/cart/${_cartId}/item/move?${fn2:buildUrlParams('id', _actionInfo.idValues)}<c:if test="${not empty _actionInfo.redirect}">&redirect=${fn2:urlencode(_actionInfo.redirect)}</c:if></spring:param>
                   </spring:url>">Создать новый список</a>
            </p>
        </c:if>
    </div>
</div><!-- #content-->
