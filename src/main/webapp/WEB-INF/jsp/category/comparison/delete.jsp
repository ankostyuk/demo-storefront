<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="usf" uri="http://www.nullpointer.ru/usf/tags" %>

<div id="content-no-sidebar">
    <div class="plain-view">
        <c:if test="${empty _actionInfo}">
            <h1>Удаление из сравнения</h1>
            <c:if test="${not empty comparisonMatchList}">
                <p>
                    В списке сравнения:
                </p>
                <ul>
                    <c:forEach var="match" items="${comparisonMatchList}">
                        <li>
                            <c:choose>
                                <c:when test="${match.type == 'OFFER'}">
                                    Предложение <strong><c:out value="${match.offer.name}" /></strong>
                                </c:when>
                                <c:when test="${match.type == 'MODEL'}">
                                    Модель <strong><c:out value="${match.model.name}" /></strong>
                                </c:when>
                            </c:choose>
                        </li>
                    </c:forEach>
                </ul>
            </c:if>
            <c:if test="${empty matchesToDelete}">
                <p>
                    Вы ничего не выбрали для удаления.
                </p>
            </c:if>
            <c:if test="${not empty matchesToDelete}">
                <form action="<spring:url value="/category/{categoryId}/comparison/delete">
                          <spring:param  name="categoryId" value="${category.id}" />
                      </spring:url>" method="post">
                    <p>
                        Удалить из списка:
                    </p>
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
                        <c:if test="${not empty param.success}">
                            <input type="hidden" name="success" value="<c:out value="${param.success}" />" />
                        </c:if>
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
                Невозможно удалить из <a rel="nofollow" href="<spring:url value="/category/{categoryId}/comparison">
                       <spring:param name="categoryId" value="${_categoryId}" />
                       <spring:param name="ids" value="${usf:getComparisonUrlParam(_userSession, _categoryId)}" />
                   </spring:url>">списка сравнения</a>. Пожалуйста, попробуйте позже.
            </p>
            <c:if test="${not empty _actionInfo.redirect}">
                <p>
                    <a href="<spring:url value="${_actionInfo.redirect}" htmlEscape="true" />">Вернуться назад</a>
                </p>
            </c:if>
        </c:if>
    </div>
</div><!-- #content-->
