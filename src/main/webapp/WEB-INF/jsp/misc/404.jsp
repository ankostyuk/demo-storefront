<%-- set HTTP status code to 404 --%>
<% response.setStatus(HttpServletResponse.SC_NOT_FOUND); %>
<% response.setHeader("Content-Type", "text/html; charset=UTF-8"); %>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cdn" uri="http://www.nullpointer.ru/cdn/tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
    <head>
        <meta http-equiv="Content-Type" content="application/xhtml+xml; charset=utf-8" />
        <title>Ошибка - 404</title>
        <link rel="shortcut icon" href="<cdn:url container="img" key="${'favicon-error.ico?20101026'}" />" type="image/x-icon" />
        <link rel="stylesheet" href="<cdn:url container="css" key="${'style.css?20110115'}" />" type="text/css" media="screen, projection" />
        <link rel="stylesheet" href="<cdn:url container="css" key="${'header/error.css?20110115'}" />" type="text/css" media="screen, projection" />
    </head>
    <body class="error-404">
        <div id="wrapper">
            <div id="header">
                <div id="logo">
                    <a href="<c:url value="/" />"><img alt="Бильдика" src="<cdn:url container="css" key="${'/i/logo-grey.png'}" />" /></a>
                </div>
            </div><!-- #header-->
            <div id="content-no-sidebar">
                <h1><span class="semi-white">Страница не найдена</span></h1>
                <p>
                    <span class="semi-white">Неправильно набран адрес или такой страницы больше не существует.</span>
                </p>
                <p>
                    <span class="semi-white">Перейти на <a href="<c:url value="/" />">главную страницу</a></span>
                </p>
            </div><!-- #content-->
        </div><!-- #wrapper -->
        <div id="footer">
            <p class="copyright">
                ©&nbsp;Бильдика, 2010—2011
            </p>
        </div><!-- #footer -->
    </body>
</html>
