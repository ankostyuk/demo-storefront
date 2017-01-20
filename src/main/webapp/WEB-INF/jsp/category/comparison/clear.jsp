<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<div id="content-no-sidebar">
    <div class="plain-view">
        <c:if test="${empty _actionInfo}">
            <h1>Очистка списка сравнения</h1>
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
            <form action="<spring:url value="/category/{categoryId}/comparison/clear">
                      <spring:param  name="categoryId" value="${category.id}" />
                  </spring:url>" method="post">
                <%--<p>
                    Очистить список?
                </p>--%>
                <div class="field">
                    <input type="submit" value="Очистить список" />
                    <c:if test="${not empty param.redirect}">
                        <input type="hidden" name="redirect" value="<c:out value="${param.redirect}" />" />
                        или <a href="<spring:url value="${param.redirect}" htmlEscape="true" />">вернуться назад</a>
                    </c:if>
                </div>
            </form>
        </c:if>
        <c:if test="${not empty _actionInfo && not _actionInfo.success}">
            <p>
                Невозможно очистить список сравнения. Пожалуйста, попробуйте позже.
            </p>
            <p>
                <c:if test="${not empty _actionInfo.redirect}">
                    <a href="<spring:url value="${_actionInfo.redirect}" htmlEscape="true" />">Вернуться назад</a>
                </c:if>
            </p>
        </c:if>
    </div>
</div><!-- #content-->
