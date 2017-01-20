<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<sec:authentication property="principal.accountId" var="authenticatedId" />
<div class="col1">
    <h1>Удаление аккаунта администратора</h1>
    <c:if test="${authenticatedId == account.id}">
        Вы не можете удалить свой аккаунт. Обратитесь к <a href="<c:url value="/secured/admin/account/admin" />">другому администратору</a>.
    </c:if>
    <c:if test="${authenticatedId != account.id}">
        <p>
            Вы уверены что хотите аккаунт «<strong><c:out value="${account.email}" /></strong>»?
        </p>
        <form:form modelAttribute="account">
            <div class="field">
                <input type="submit" value="Удалить аккаунт" />
            </div>
        </form:form>
    </c:if>
</div>
<div class="col2">
</div>