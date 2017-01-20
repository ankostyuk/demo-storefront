<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div id="content-no-sidebar">
    <c:if test="${success}">
        <h1>Ваша учетная запись успешно активирована</h1>
        <p>
            Теперь вы можете войти в свой <a href="<c:url value='/login'><c:param name='email' value='${email}' /></c:url>">личный кабинет</a>.
        </p>
    </c:if>
    <c:if test="${!success}">
        <h1>Что-то пошло не так</h1>
        <p>
            Нам не удалось активировать вашу учетную запись.
        </p>
        <ul>
            <li>Возможно ваша учетная запись уже активирована. Попробуйте войти в свой <a href="<c:url value='/login'><c:param name='email' value='${email}' /></c:url>">личный кабинет</a>.</li>
            <%--<li>Также вы можете обратиться в службу <a href="#">технической поддержки</a></li>--%> <%-- TODO mail, feedback, ... --%>
        </ul>
    </c:if>
</div><!-- #content-->
