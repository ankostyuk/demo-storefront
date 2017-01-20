<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>

<div id="content-no-sidebar">
    <tiles:insertDefinition name="secured-company-settings-menu" />

    <h1>Изменить пароль</h1>
    <p>
        Чтобы изменить пароль, укажите свой текущий пароль.
    </p>
    <%-- необходимо чтобы параметр "updated" отсутствовал в URL формы, поэтому задаем action явно --%>
    <c:url var="action" value="/secured/company/settings/password" />
    <form:form modelAttribute="passwordUpdate" action="${action}">
        <c:if test="${not empty paramValues['updated']}">
            <p class="message"><span class="form-updated">Пароль успешно изменен</span></p>
        </c:if>
        <ul class="field-group">
            <li>
                <form:label path="oldPassword" cssClass="text">Текущий пароль</form:label>
                <form:password cssClass="text" path="oldPassword" />
                <form:errors path="oldPassword" cssClass="error" />
            </li>
            <li>
                <form:label path="newPassword" cssClass="text">Новый пароль</form:label>
                <form:password cssClass="text" path="newPassword" />
                <form:errors path="newPassword" cssClass="error" />
            </li>
            <li>
                <form:label path="newPasswordRepeat" cssClass="text">Повторите новый пароль</form:label>
                <form:password cssClass="text" path="newPasswordRepeat" />
                <form:errors path="newPasswordRepeat" cssClass="error" />
            </li>
        </ul>
        <div class="submit">
            <input type="submit" value="Изменить пароль"/>
        </div>
    </form:form>

</div><!-- #content-->