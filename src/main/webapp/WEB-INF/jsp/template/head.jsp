<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cdn" uri="http://www.nullpointer.ru/cdn/tags" %>
<head>
    <meta http-equiv="Content-Type" content="application/xhtml+xml; charset=utf-8" />
    <title><c:if test='${not empty metadata.title}'><c:out value='${metadata.title}' /></c:if><c:if test='${empty metadata.title}'><tiles:insertAttribute name='metadata.title' ignore='true' /></c:if></title>
    <meta name="description" content="<c:if test='${not empty metadata.description}'><c:out value='${metadata.description}' /></c:if><c:if test='${empty metadata.description}'><tiles:insertAttribute name='metadata.description' ignore='true' /></c:if>" />
    <meta name="keywords" content="<c:if test='${not empty metadata.keywords}'><c:out value='${metadata.keywords}' /></c:if><c:if test='${empty metadata.keywords}'><tiles:insertAttribute name='metadata.keywords' ignore='true' /></c:if>" />
    <meta name="robots" content="index, follow" />
    <link rel="shortcut icon" href="<cdn:url container="img" key="${'favicon-public.ico?20101026'}" />" type="image/x-icon" />

    <%-- Стили --%>
    <tiles:useAttribute name="cssList" classname="java.util.List" ignore="true" />
    <c:forEach var="css" items="${cssList}">
        <link rel="stylesheet" type="text/css" media="screen, projection" href="<cdn:url container="css" key="${css}" />" />
    </c:forEach>
    <%-- Стиль шапки --%>
    <c:if test="${empty headerTheme}"><tiles:useAttribute name="headerTheme" ignore="true" /></c:if>
    <c:if test="${not empty headerTheme}">
        <link rel="stylesheet" type="text/css" media="screen, projection" href="<cdn:url container="css" key="header/${headerTheme}.css?20110115" />" />
    </c:if>
    <%-- Стили для IE --%>
    <tiles:useAttribute name="cssIE" ignore="true" />
    <c:if test="${not empty cssIE}">
        <!--[if IE]><link rel="stylesheet" href="<cdn:url container="css" key="${cssIE}" />" type="text/css" media="screen, projection" /><![endif]-->
    </c:if>
    <tiles:useAttribute name="cssIE7" ignore="true" />
    <c:if test="${not empty cssIE7}">
        <!--[if IE 7]><link rel="stylesheet" href="<cdn:url container="css" key="${cssIE7}" />" media="screen, projection" /><![endif]-->
    </c:if>

    <%-- Скрипты --%>
    <tiles:useAttribute name="scriptList" classname="java.util.List" ignore="true" />
    <c:forEach var="script" items="${scriptList}">
        <script type="text/javascript" src="<cdn:url container="js" key="${script}" />"></script>
    </c:forEach>

    <%-- Google Analytics --%>
    <script type="text/javascript">
        // <![CDATA[
            var _gaq = _gaq || [];
            _gaq.push(['_setAccount', 'UA-19127535-2']);
            _gaq.push(['_trackPageview']);

            (function() {
            var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
            ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
            var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
            })();
        // ]]>
    </script>
</head>
