<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div id="content-no-sidebar">
    <h1>Восстановление пароля</h1>
    <form action="<c:url value='/login/reminder' />" method="post" class="login">
        <c:if test="${error}">
            <p class="message"><span class="form-error">Что-то пошло не так</span></p>
            <ul class="message-list">
                <li>Проверьте введенный адрес электронной почты.</li>
                <li>Попробуйте восстановить пароль через несколько минут.</li>
            </ul>
        </c:if>
        <ul class="field-group">
            <li>
                <label class="text" for="email">Электронная почта</label>
                <c:choose>
                    <c:when test='${not empty paramValues["email"]}'>
                        <input class="text" type="text" id="email" name="email" value="${param['email']}" />
                    </c:when>
                    <c:otherwise>
                        <input class="text" type="text" id="email" name="email" value="" />
                    </c:otherwise>
                </c:choose>
                <p class="hint">Введите адрес указанный при регистрации</p>
            </li>
        </ul>
        <div class="submit">
            <input type="submit" value="Прислать пароль" />
        </div>
    </form>
</div><!-- #content-->
