<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div id="content-no-sidebar">
    <div class="plain-view">
        <c:if test="${empty _actionInfo}">
            <h1>Удаление из «<c:out value="${cart.name}" />»</h1>
            <c:if test="${empty matchesToDelete}">
                <p>
                    Вы ничего не выбрали для удаления.
                </p>
            </c:if>
            <c:if test="${not empty matchesToDelete}">
                <form action="<spring:url value="/cart/{cartId}/item/delete">
                          <spring:param  name="cartId" value="${cart.id}" />
                      </spring:url>" method="post">
                    <ul>
                        <c:forEach var="match" items="${matchesToDelete}">
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
                        <input type="submit" value="Удалить" />
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
                Невозможно удалить из списка покупок. Пожалуйста, попробуйте позже.
            </p>
            <p>
                <c:if test="${not empty _actionInfo.redirect}">
                    <a href="<spring:url value="${_actionInfo.redirect}" htmlEscape="true" />">Вернуться назад</a>
                </c:if>
            </p>
        </c:if>
    </div>
</div><!-- #content-->
