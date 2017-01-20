<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn2" uri="http://www.nullpointer.ru/fn2/tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<sec:authentication property="principal.accountId" var="authenticatedId" />
<div class="col1">
    <h1>Редактирование аккаунта администратора</h1>
    <c:if test="${authenticatedId == accountId}">
        <p>
            Внимание! Вы изменяете свой аккаунт. Изменения вступят в силу после следующего входа.
        </p>
    </c:if>
    <p>Символом <span class="mandatory">*</span> отмечены поля, обязательные для заполнения.</p>
    <form:form cssClass="basic" modelAttribute="login">
        <div class="field">
            <form:label path="email">Электронная почта</form:label>
            <form:input cssClass="text medium" path="email" />
            <p class="hint">
                <spring:bind path="email">
                    <c:if test="${!status.error}">Этот адрес будет использоваться для входа</c:if>
                </spring:bind>
                <form:errors cssClass="error" path="email" />
            </p>
        </div>
        <div class="field">
            <form:label path="password">Новый пароль</form:label>
            <form:password cssClass="text medium" path="password" />
            <p class="hint">
                <spring:bind path="password">
                    <c:if test="${!status.error}">
                        <c:set var="passwordValue" value="${status.value}" />
                        <%-- Пароль валидный, проверить другие ошибки --%>
                        <spring:bind path="*">
                            <c:choose>
                                <%-- Если есть другие ошибки, напомнить повторно ввести пароль --%>
                                <c:when test="${status.error and not empty passwordValue}">
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
            <label for="passwordRepeat">Повторите новый пароль</label>
            <input type="password" id="passwordRepeat" name="passwordRepeat" class="text medium" />
        </div>
        <div class="field">
            <input type="submit" value="Сохранить аккаунт администратора" />
        </div>
    </form:form>
</div>
<div class="col2">
    <c:if test="${authenticatedId == accountId}">
        <p>
            Вы не можете удалить свой аккаунт. Обратитесь к другому администратору.
        </p>
    </c:if>
    <c:if test="${authenticatedId != accountId}">
        <ul>
            <li><a href="<spring:url value="/secured/admin/account/admin/delete/{id}">
                       <spring:param name="id" value="${accountId}" />
                   </spring:url>">Удалить аккаунт</a></li>
        </ul>
    </c:if>
</div>