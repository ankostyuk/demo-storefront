<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn2" uri="http://www.nullpointer.ru/fn2/tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>

<div class="col1">
    <h1>Добавление аккаунта менеджера</h1>
    <p>Символом <span class="mandatory">*</span> отмечены поля, обязательные для заполнения.</p>
    <form:form cssClass="basic" modelAttribute="login">
        <div class="field">
            <form:label path="email">Электронная почта<span class="mandatory">*</span></form:label>
            <form:input cssClass="text medium" path="email" />
            <p class="hint">
                <spring:bind path="email">
                    <c:if test="${!status.error}">Этот адрес будет использоваться для входа</c:if>
                </spring:bind>
                <form:errors cssClass="error" path="email" />
            </p>
        </div>
        <div class="field">
            <form:label path="password">Пароль<span class="mandatory">*</span></form:label>
            <form:password cssClass="text medium" path="password" />
            <p class="hint">
                <spring:bind path="password">
                    <c:if test="${!status.error}">
                        <%-- Пароль валидный, проверить другие ошибки --%>
                        <spring:bind path="*">
                            <c:choose>
                                <%-- Если есть другие ошибки, напомнить повторно ввести пароль --%>
                                <c:when test="${status.error}">
                                    <span class="error">Введите пароль заново. Минимальная длина — 6 символов</span>
                                </c:when>
                                <%-- Иначе просто показать хинт --%>
                                <c:otherwise>Минимальная длина — 6 символов</c:otherwise>
                            </c:choose>
                        </spring:bind>
                    </c:if>
                </spring:bind>
                <form:errors cssClass="error" path="password" />
            </p>
        </div>
        <div class="field">
            <label for="passwordRepeat">Повторите пароль<span class="mandatory">*</span></label>
            <input type="password" id="passwordRepeat" name="passwordRepeat" class="text medium" />
        </div>
        <fieldset>
            <legend>Роли<span class="mandatory">*</span></legend>
            <spring:bind path="login" ignoreNestedPath="true">
                <c:if test="${status.error}">
                    <p class="hint">
                        <span class="error"><c:out value="${status.errorMessage}" /></span>
                    </p>
                </c:if>
            </spring:bind>
            <c:forEach var="role" items="${availableRoles}">
                <div class="field">
                    <label>
                        <input type="checkbox" name="role" value="${role}" <c:if test="${fn2:contains(accountRoleList, role)}">checked="checked"</c:if> />
                        <spring:message code="ui.account.role.${role}" />
                    </label>
                </div>
            </c:forEach>
        </fieldset>
        <div class="field">
            <input type="submit" value="Добавить аккаунт менеджера" />
        </div>
    </form:form>
</div>
<div class="col2">
</div>