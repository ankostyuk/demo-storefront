<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn2" uri="http://www.nullpointer.ru/fn2/tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>

<div class="col1">
    <h1>Редактирование аккаунта поставщика</h1>
    <p>Символом <span class="mandatory">*</span> отмечены поля, обязательные для заполнения.</p>
    <form:form cssClass="basic" modelAttribute="company">
        <fieldset>
            <legend>Аккаунт</legend>
            <div class="field">
                <spring:bind path="login.email" ignoreNestedPath="true">
                    <label for="${status.expression}">Электронная почта<span class="mandatory">*</span></label>
                    <input id="${status.expression}" name="${status.expression}" value="${status.value}" type="text" class="text medium" />
                    <p class="hint">
                        <c:if test="${status.error}">
                            <c:if test="${!status.error}">Этот адрес будет использоваться для входа</c:if>
                            <c:if test="${status.error}"><span class="error">${status.errorMessage}</span></c:if>
                        </c:if>
                    </p>
                </spring:bind>
            </div>
            <div class="field">
                <spring:bind path="login.password" ignoreNestedPath="true">
                    <label for="${status.expression}">Новый пароль</label>
                    <input id="${status.expression}" name="${status.expression}" value="${status.value}" type="password" class="text medium" />
                    <p class="hint">
                        <c:if test="${!status.error}">
                            <c:set var="passwordValue" value="${status.value}" />
                            <%-- Пароль валидный, проверить другие ошибки --%>
                            <spring:bind path="company.*" ignoreNestedPath="true">
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
                        <c:if test="${status.error}">
                            <span class="error">${status.errorMessage}</span>
                        </c:if>
                    </p>
                </spring:bind>
            </div>
            <div class="field">
                <label for="passwordRepeat">Повторите новый пароль</label>
                <input type="password" id="passwordRepeat" name="passwordRepeat" class="text medium" />
            </div>
        </fieldset>
        <div class="field">
            <form:label path="name">Название<span class="mandatory">*</span></form:label>
            <form:input cssClass="text medium" path="name" />
            <p class="hint">
                <form:errors cssClass="error" path="name" />
            </p>
        </div>
        <div class="field">
            <input type="submit" value="Сохранить аккаунт поставщика" />
        </div>
    </form:form>
</div>
<div class="col2">

</div>