<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>

<tiles:useAttribute id="applicationBuildMode" name="ui.application.build.mode" />

<div id="content-no-sidebar">
    <h1>Для компаний</h1>
    <ul>
        <li>Вы хотите, чтобы о вас узнали больше?</li>
        <li>Здесь покупатели сами ищут, что им нужно для строительства и ремонта!</li>
    </ul>
    <p>
        Зарегистрируйтесь и разместите свои товары. Это абсолютно бесплатно и не займет много времени.
    </p>
    <c:if test="${applicationBuildMode == 'PRODUCTION'}">
        <p>
            <a href="<c:url value="/registration/company" />">Начать регистрацию</a>
        </p>
        <%-- Вход в демо-версию --%>
        <form action="http://demo.bildika.ru/login/check" method="post">
            <p>
                Вы также можете попробовать <input type="submit" value="демо-версию" />
            </p>
            <div>
                <input type="hidden" name="j_username" value="demo@bildika.ru" />
                <input type="hidden" name="j_password" value="demodemo" />
            </div>
        </form>
        
        <p class="info">
            Если вы уже регистрировались, то можете войти в свой <a href="<c:url value="/login" />">личный кабинет</a>.
        </p>
        <p class="info">
            <a href="<c:url value="/contacts" />">Свяжитесь с нами</a>, если у вас есть вопросы или пожелания.
        </p>
    </c:if>

    <c:if test="${applicationBuildMode == 'DEMO'}">
        <p>
            <a href="http://bildika.ru/registration/company">Начать регистрацию</a>
        </p>
        <form action="<c:url value='/login/check' />" method="post">
            <p>
                Вы находитесь в демо-версии. Войти в <input type="submit" value="личный кабинет" />?
            </p>
            <div>
                <input type="hidden" name="j_username" value="demo@bildika.ru" />
                <input type="hidden" name="j_password" value="demodemo" />
            </div>
        </form>
    </c:if>
</div><!-- #content-->
