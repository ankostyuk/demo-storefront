<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cdn" uri="http://www.nullpointer.ru/cdn/tags" %>
<%@ taglib prefix="fn2" uri="http://www.nullpointer.ru/fn2/tags" %>

<div class="col1">
    <c:if test="${action == 'approve'}">
        <h1>Одобрить предложение</h1>
    </c:if>
    <c:if test="${action != 'approve'}">
        <h1>Отклонить предложение</h1>
    </c:if>

    <h2><c:out value="${offer.name}" /></h2>
    <c:if test="${not empty offer.description}">
        <p><c:out value="${offer.description}" /></p>
    </c:if>

    <form class="basic" action="<spring:url value="/secured/manager/moderator/offer/decision/{id}" >
              <spring:param name="id" value="${offer.id}" />
          </spring:url>" method="post">
        <c:if test="${not empty param.redirect}">
            <div>
                <input type="hidden" name="redirect" value="${param.redirect}" />
            </div>
        </c:if>

        <c:if test="${action == 'approve'}">
            <div class="field">
                <input type="hidden" name="action" value="approve" />
                <input type="submit" value="Одобрить" />
            </div>
        </c:if>
        <c:if test="${action != 'approve'}">
            <c:if test="${selectRejection}">
                <p class="hint">
                    <span class="error">
                        Необходимо указать хотя-бы одну причину отклонения
                    </span>
                </p>
            </c:if>
            <fieldset>
                <legend>Причины отклонения</legend>
                <c:forEach var="val" items="${rejectionValues}">
                    <div class="field">
                        <label>
                            <input type="checkbox" name="rejectionValue" value="${val}" <c:if test="${fn2:contains(offer.rejectionList, val)}">checked="checked"</c:if>/>
                            <spring:message code="ui.offer.rejection.${val}" />
                        </label>
                    </div>
                </c:forEach>
            </fieldset>
            <div class="field">
                <input type="hidden" name="action" value="reject" />
                <input type="submit" value="Отклонить" />
            </div>
        </c:if>
    </form>
</div>
<div class="col2">

</div>
