<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cdn" uri="http://www.nullpointer.ru/cdn/tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="ru-RU">
    <head>
        <title><c:if test='${not empty metadata.title}'><c:out value='${metadata.title}' /></c:if><c:if test='${empty metadata.title}'><tiles:insertAttribute name='metadata.title' /></c:if></title>
        <meta http-equiv="Content-Type" content="application/xhtml+xml; charset=utf-8" />
        <meta name="description" content="<c:if test='${not empty metadata.description}'><c:out value='${metadata.description}' /></c:if><c:if test='${empty metadata.description}'><tiles:insertAttribute name='metadata.description' /></c:if>" />
        <meta name="keywords" content="<c:if test='${not empty metadata.keywords}'><c:out value='${metadata.keywords}' /></c:if><c:if test='${empty metadata.keywords}'><tiles:insertAttribute name='metadata.keywords' /></c:if>" />
        <meta name="robots" content="index, follow" />
        <link rel="shortcut icon" href="/favicon.ico" type="image/x-icon" />

        <%-- Стили --%>
        <tiles:useAttribute name="cssList" classname="java.util.List" ignore="true" />
        <c:forEach var="css" items="${cssList}">
            <link rel="stylesheet" type="text/css" media="screen" href="<cdn:url container="css" key="${css}" />" />
        </c:forEach>

        <%-- Скрипты --%>
        <tiles:useAttribute name="scriptList" classname="java.util.List" ignore="true" />
        <c:forEach var="script" items="${scriptList}">
            <script type="text/javascript" src="<cdn:url container="js" key="${script}" />"></script>
        </c:forEach>
    </head>
    <body>
        <div id="wrap">
            <tiles:useAttribute name="key" />
            <tiles:insertAttribute name="header">
                <tiles:putAttribute name="key" value="${key}" />
            </tiles:insertAttribute>
            <div class="colmask">
                <tiles:insertAttribute name="content" >
                    <tiles:putAttribute name="key" value="${key}" />
                </tiles:insertAttribute>
            </div>
        </div>
        <tiles:insertAttribute name="footer" />
    </body>
</html>
