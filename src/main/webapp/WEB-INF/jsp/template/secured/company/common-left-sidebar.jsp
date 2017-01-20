<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="ru-RU">
    <tiles:insertAttribute name="head" />
    <body>
        <div id="wrapper">
            <div id="header">
                <tiles:insertAttribute name="header" />
            </div><!-- #header-->
            <div id="middle">
                <tiles:insertAttribute name="content" />
                <%--
                    <div id="container">
                        <div id="content-left-sidebar">

                        </div><!-- #content-->
                    </div><!-- #container-->
                    <div class="sidebar" id="sideLeft">

                    </div><!-- .sidebar#sideLeft -->
                --%>
            </div><!-- #middle-->
        </div><!-- #wrapper -->
        <div id="footer">
            <div class="footer-content">
                <tiles:insertAttribute name="footer" />
            </div>
        </div><!-- #footer -->
        <tiles:insertAttribute name="foot" />
    </body>
</html>
