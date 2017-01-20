<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>

<tiles:useAttribute id="applicationBuildMode" name="ui.application.build.mode" />

<div id="content-no-sidebar">
    <h1>Вход</h1>

    <c:if test="${applicationBuildMode == 'DEMO'}">
        <form action="<c:url value='/login/check' />" method="post">
            <p>
                Вы находитесь в демо-версии. 
                Вы можете войти в <input type="submit" value="личный кабинет" /> или <a href="http://bildika.ru/registration/company">начать регистрацию</a>.
            </p>
            <div>
                <input type="hidden" name="j_username" value="demo@bildika.ru" />
                <input type="hidden" name="j_password" value="demodemo" />
            </div>
        </form>
    </c:if>

    <c:set var="reminderUrl">
        <c:choose>
            <c:when test="${not empty paramValues['email']}">
                <c:url value='/login/reminder'><c:param name="email" value="${param['email']}"/></c:url>
            </c:when>
            <c:otherwise>
                <c:url value='/login/reminder'/>
            </c:otherwise>
        </c:choose>
    </c:set>
    <form action="<c:url value='/login/check' />" method="post" class="login">
        <c:if test="${not empty paramValues['error']}">
            <p class="message"><span class="form-error">Неверный адрес электронной почты или пароль</span></p>
        </c:if>
        <c:choose>
            <c:when test="${not empty paramValues['registered']}">
                <p class="message"><span class="form-error">Вы уже регистрировались на сайте</span></p>
                <ul class="message-list">
                    <li>Проверьте, не получали ли вы письмо с кодом активации.</li>
                    <li>Если вы не можете найти письмо, мы поможем <a href="${reminderUrl}">восстановить пароль</a>.</li>
                </ul>
            </c:when>
            <c:when test="${not empty paramValues['reminder']}">
                <p class="message"><span class="form-done">Новый пароль выслан на указанный вами адрес электронной почты</span></p>
            </c:when>
        </c:choose>
        <ul class="field-group">
            <li>
                <label class="text" for="email">Электронная почта</label>
                <input class="text" type="text" id="email" name="j_username" value="<c:if test='${not empty paramValues["email"]}'>${param['email']}</c:if>" tabindex="110" />
                <c:if test="${applicationBuildMode != 'DEMO'}">
                    <span class="action"><a class="act" href="<c:url value="/registration/company/partner" />">Регистрация компании</a></span>
                </c:if>
            </li>
            <li>
                <label class="text" for="password">Пароль</label>
                <input class="text" type="password" id="password" name="j_password" tabindex="120" />
                <c:if test="${applicationBuildMode != 'DEMO'}">
                    <span class="action"><a class="" href="${reminderUrl}">Восстановить пароль</a></span>
                </c:if>
            </li>
        </ul>
        <div class="submit">
            <input type="submit" value="Войти" tabindex="130" />
        </div>
    </form>
    <script type="text/javascript">
        // <![CDATA[
        jsHelper.document.ready(function(){
        <%-- Установить фокус на вводе электронной почты или на вводе пароля --%>
        <c:if test='${empty paramValues["email"]}'>
                $("#email").focus();
        </c:if>
        <c:if test='${not empty paramValues["email"]}'>
                $("#password").focus();
        </c:if>
            });
            // ]]>
    </script>
</div><!-- #content-->


