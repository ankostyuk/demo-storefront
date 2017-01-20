<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>

<tiles:useAttribute name="_id" ignore="true" />
<tiles:useAttribute name="_cartId" ignore="true" />
<tiles:useAttribute name="_redirect" ignore="true" />
<tiles:useAttribute name="_actionInfo" ignore="true" />

<c:if test="${empty _actionInfo}">
    <c:if test="${empty _cartId}">
        <a id="cart-item-${_id}" class="act cart-item-add" rel="nofollow" href="<spring:url value="/cart/item/add" htmlEscape="true">
               <spring:param name="id" value="${_id}" />
               <c:if test="${not empty _redirect}">
                   <spring:param name="redirect" value="${_redirect}" />
               </c:if>
           </spring:url>">В список покупок</a>
    </c:if>
    <c:if test="${not empty _cartId}">
        <a rel="nofollow" href="<spring:url value="/cart/{cartId}">
               <spring:param name="cartId" value="${_cartId}" />
           </spring:url>">Уже в списке покупок</a>
    </c:if>
</c:if>
<c:if test="${not empty _actionInfo}">
    <c:if test="${_actionInfo.type == 'MATCH_ADD_TO_CART'}">
        <c:if test="${_actionInfo.success}">
            <a rel="nofollow" href="<spring:url value="/cart/{cartId}">
                   <spring:param name="cartId" value="${_cartId}" />
               </spring:url>">Уже в списке покупок</a>
        </c:if>
        <c:if test="${not _actionInfo.success}">
            <div class="action-error-info">
                <p>
                    Невозможно добавить в список покупок, список покупок переполнен.
                </p>
                <p>
                    <a rel="nofollow" href="<spring:url value="/cart/add" htmlEscape="true">
                           <c:if test="${not empty _actionInfo.redirect}">
                               <spring:param name="redirect" value="${_actionInfo.redirect}" />
                           </c:if>
                       </spring:url>">Создать новый список</a>
                </p>
            </div>
        </c:if>
    </c:if>
    <c:if test="${_actionInfo.type == 'MATCH_MOVE_TO_CART'}">
        <c:if test="${not _actionInfo.success}">
            <div class="action-error-info">
                <p>
                    Невозможно переместить в выбранный список покупок, список покупок переполнен.
                </p>
                <p>
                    <a rel="nofollow" href="<spring:url value="/cart/add" htmlEscape="true">
                           <c:if test="${not empty _actionInfo.redirect}">
                               <spring:param name="redirect" value="${_actionInfo.redirect}" />
                           </c:if>
                       </spring:url>">Создать новый список</a>
                </p>
            </div>
        </c:if>
    </c:if>
    <c:if test="${_actionInfo.type == 'MATCH_DELETE_FROM_CART'}">
        <c:if test="${not _actionInfo.success}">
            <div class="action-error-info">
                <p>
                    Невозможно удалить из списка покупок. Пожалуйста, попробуйте позже.
                </p>
            </div>
        </c:if>
    </c:if>
</c:if>
