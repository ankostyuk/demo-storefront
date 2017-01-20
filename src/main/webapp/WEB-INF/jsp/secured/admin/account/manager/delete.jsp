<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div class="col1">
    <h1>Удаление аккаунта менеджера</h1>
    <p>
        Вы уверены что хотите аккаунт «<strong><c:out value="${account.email}" /></strong>»?
    </p>
    <form:form modelAttribute="account">
        <div class="field">
            <input type="submit" value="Удалить аккаунт" />
        </div>
    </form:form>
</div>
<div class="col2">
</div>