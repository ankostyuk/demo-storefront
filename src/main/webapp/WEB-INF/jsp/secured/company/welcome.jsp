<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<div id="content-no-sidebar">
    <h1>Добро пожаловать</h1>
    <p>
        Ваша учетная запись успешно активирована.
    </p>
    <p>
        Теперь вы можете <a href="<spring:url value="/secured/company/settings" />">указать подробную информацию</a> о своей компании
        и добавить товарные предложения.
    </p>
</div><!-- #content-->
