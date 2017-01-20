<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<tiles:useAttribute name="cartItem" />
<tiles:useAttribute name="_redirect" ignore="true"/>

<c:if test="${cartItem.match.type == 'OFFER'}">
    <c:set var="matchId" value="o${cartItem.match.id}" />
</c:if>
<c:if test="${cartItem.match.type == 'MODEL'}">
    <c:set var="matchId" value="m${cartItem.match.id}" />
</c:if>

<div class="match-cart-info">
    Добавлено <fmt:formatDate pattern="dd.MM.yyyy HH:mm" value="${cartItem.dateAdded}" />
    <ul class="h-list actions">
        <c:if test="${fn:length(userSession.cartList) > 1}">
            <li><a id="cart-item-${matchId}" class="act cart-item-move" rel="nofollow" href="<spring:url value="/cart/{cartId}/item/move" htmlEscape="true">
                       <spring:param name="id" value="${matchId}" />
                       <spring:param name="cartId" value="${cart.id}" />
                       <c:if test="${not empty _redirect}">
                           <spring:param name="redirect" value="${_redirect}" />
                       </c:if>
                   </spring:url>">Переместить</a></li>
        </c:if>
        <li><a class="dce cart-item-delete" rel="nofollow" href="<spring:url value="/cart/{cartId}/item/delete" htmlEscape="true">
                   <spring:param name="id" value="${matchId}" />
                   <spring:param name="cartId" value="${cart.id}" />
                   <c:if test="${not empty _redirect}">
                       <spring:param name="redirect" value="${_redirect}" />
                   </c:if>
               </spring:url>">Удалить</a></li>
    </ul>
</div>