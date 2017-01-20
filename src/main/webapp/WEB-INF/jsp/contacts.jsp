<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cdn" uri="http://www.nullpointer.ru/cdn/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<div id="content-no-sidebar" class="contacts-page">
    <h1>Контакты</h1>
    <h3>Телефон</h3>
    <ul>
        <li>+7 926 780-22-76</li>
    </ul>
    <h3>Электронная почта</h3>
    <ul>
        <li><a href="mailto:feedback@bildika.ru">feedback@bildika.ru</a></li>
    </ul>
    <h3>ICQ</h3>
    <ul>
        <li>
            <a class="img"><img class="status" src="http://status.icq.com/online.gif?icq=631641370&amp;img=27" style="border: none;" alt="" /></a>
            631641370
        </li>
    </ul>
    <h3>Skype</h3>
    <ul>
        <li>
            <%-- TODO На кой? <script type="text/javascript" src="http://download.skype.com/share/skypebuttons/js/skypeCheck.js"></script>--%>
            <a class="img" href="skype:bildika?call"><img class="status" src="http://mystatus.skype.com/smallicon/bildika" style="border: none;" width="16" height="16" alt="" /></a>
            bildika
        </li>
    </ul>
    <h3>Отзывы и пожелания</h3>
    <ul>
        <li><a href="http://bildika.reformal.ru/">bildika.reformal.ru</a></li>
    </ul>
    <h3 class="links-header">Владельцам сайтов</h3>
    <p class="links-info">Вы можете установить на своем сайте ссылку на Бильдику.</p>
    <p class="links-info">Выберите вариант ссылки, подходящий вам по виду и размеру, и разместите код на страницах вашего сайта или блога.</p>
    <ul class="links">
        <c:set var="domainUrl">http://<%= request.getHeader("Host") %></c:set>
        <c:set var="contextUrl">${domainUrl}<%= request.getContextPath() %></c:set>
        <li>
            <c:set var="linkCode"><a style="border: 0 none;" href="${contextUrl}"><img style="border: 0 none;" title="Бильдика — поиск и выбор товаров для строительства и ремонта" alt="" src="${domainUrl}<cdn:url container="img" key="${'link-logo-135x35.png'}" />" /></a></c:set>
            <div class="link-box">
                ${linkCode}
            </div>
            <div class="code-box">
                <textarea cols="100" rows="3" readonly="readonly"><c:out value='${linkCode}' /></textarea>
                <span class="info">Логотип 135×35</span>
            </div>
        </li>
        <li>
            <c:set var="linkCode"><a style="border: 0 none;" href="${contextUrl}"><img style="border: 0 none;" title="Бильдика — поиск и выбор товаров для строительства и ремонта" alt="" src="${domainUrl}<cdn:url container="img" key="${'link-logo-90x24.png'}" />" /></a></c:set>
            <div class="link-box">
                ${linkCode}
            </div>
            <div class="code-box">
                <textarea cols="100" rows="3" readonly="readonly"><c:out value='${linkCode}' /></textarea>
                <span class="info">Логотип 90×24</span>
            </div>
        </li>
        <li>
            <c:set var="linkCode"><a style="border-bottom: 1px solid; text-decoration: none; color: #f47b20; font-family: Arial; font-size: 16px; font-weight: bold;" title="Бильдика — поиск и выбор товаров для строительства и ремонта" href="${contextUrl}">Бильдика</a></c:set>
            <div class="link-box">
                ${linkCode}
            </div>
            <div class="code-box">
                <textarea cols="100" rows="3" readonly="readonly"><c:out value='${linkCode}' /></textarea>
                <span class="info">Текст 1</span>
            </div>
        </li>
        <li>
            <c:set var="linkCode"><a style="border-bottom: 1px solid; text-decoration: none; color: #221f1f; font-family: Arial; font-size: 16px; font-weight: bold;" title="Бильдика — поиск и выбор товаров для строительства и ремонта" href="${contextUrl}">Бильдика</a></c:set>
            <div class="link-box">
                ${linkCode}
            </div>
            <div class="code-box">
                <textarea cols="100" rows="3" readonly="readonly"><c:out value='${linkCode}' /></textarea>
                <span class="info">Текст 2</span>
            </div>
        </li>
    </ul>
</div><!-- #content-->

<script type="text/javascript">
    // <![CDATA[
    jsHelper.window.load(function(){
        var p = new Date().getTime();
        $("img.status").each(function(){
            var img = $(this);
            var src = img.attr("src");
            img.attr("src", src + (src.indexOf("?") > 0 ? "&_=" : "?_=") + p);
        });
    });
    // ]]>
</script>
