<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>

<div id="content-no-sidebar">
    <h1>Регистрация</h1>
    <p>
        Пожалуйста, заполните все поля ниже.
    </p>
    <form:form modelAttribute="company">
        <spring:bind path="company.*" >
            <c:if test="${status.error || invalidCaptcha}">
                <p class="message"><span class="form-error">Некорректно заполнены некоторые поля ниже</span></p>
            </c:if>
        </spring:bind>
        <ul class="field-group">
            <li>
                <%-- TODO ? Без пробела между label и input --%>
                <form:label cssClass="text" path="name">Название</form:label>
                <form:input cssClass="text" path="name" />
                <form:errors cssClass="error" path="name" />
                <p class="hint">Например: ООО Ромашка</p>
            </li>
            <li>
                <tiles:insertDefinition name="region-init">
                    <tiles:putAttribute name="_propertyPath" value="${'regionId'}" />
                    <tiles:putAttribute name="_modelAttribute" value="${'company'}" />
                    <tiles:putAttribute name="_labelText" value="${'Свой регион'}" />
                    <tiles:putAttribute name="_hintText" value="${'Необходимо указать один город, например: Москва'}" />
                    <tiles:putAttribute name="_regionListByText" value="${initRegionListByText}" />
                    <tiles:putAttribute name="_regionText" value="${initRegionText}" />
                    <tiles:putAttribute name="_baseRegion" value="${initRegion}" />
                </tiles:insertDefinition>
            </li>
            <li>
                <form:label cssClass="text" path="account.email">Электронная почта</form:label>
                <form:input cssClass="text" path="account.email" />
                <form:errors cssClass="error" path="account.email" />
                <p class="hint">Этот адрес будет использоваться для входа</p>
            </li>
            <li>
                <form:label cssClass="text" path="account.password">Пароль</form:label>
                <form:password cssClass="text" path="account.password" />
                <form:errors cssClass="error" path="account.password" />
                <p class="hint">
                    <spring:bind path="account.password">
                        <%-- Пароль валидный, проверить другие ошибки и капчу --%>
                        <spring:bind path="company.*" >
                            <c:choose>
                                <%-- Если есть другие ошибки или капча введена неверно, напомнить повторно ввести пароль --%>
                                <c:when test="${status.error || invalidCaptcha}">
                                    <span class="repeat">Введите пароль заново. Минимальная длина — 6 символов.</span>
                                </c:when>
                                <%-- Иначе просто показать хинт --%>
                                <c:otherwise>Минимальная длина — 6 символов</c:otherwise>
                            </c:choose>
                        </spring:bind>
                    </spring:bind>
                </p>
            </li>
            <li>
                <label class="text" for="passwordRepeat">Повторите пароль</label>
                <input class="text" type="password" id="passwordRepeat" name="passwordRepeat" />
            </li>
        </ul>

        <h3>Защита от злоумышленников</h3>
        <ul class="field-group">
            <li>
                <label class="text" for="captcha">Введите символы</label>
                <img class="captcha" src="<c:url value='/captcha'/>" alt="" /><%--
                --%><input id="captcha" class="text captcha" name="captcha" type="text" autocomplete="off" /><%--
                --%><c:if test="${status.error || invalidCaptcha}">
                    <span class="error">Вы ввели неверные символы</span>
                </c:if>
                <p class="hint">
                    <spring:bind path="company.*" >
                        <%-- Если есть другие ошибки или капча введена неверно, напомнить повторно ввести код --%>
                        <c:if test="${status.error || invalidCaptcha}">
                            <span class="repeat">Введите символы, показанные на картинке заново</span>
                        </c:if>
                    </spring:bind>
                </p>
            </li>
        </ul>
        <p>
            Нажимая кнопку, вы принимаете <a class="composite external" target="_blank" href="<c:url value="/agreement" />"><span class="link">условия пользовательского соглашения</span></a>.
        </p>
        <div class="submit">
            <input type="submit" value="Зарегистрировать компанию" />
        </div>
    </form:form>
</div><!-- #content-->

<script type="text/javascript">
    // <![CDATA[
    jsHelper.document.ready(function(){
    <%-- Установить фокус на первом поле регистрации --%>
            $("#name").focus();
        });
        // ]]>
</script>
