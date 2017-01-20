<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="fn2" uri="http://www.nullpointer.ru/fn2/tags" %>

<div id="container">
    <div id="content-right-sidebar" class="term-page">
        <h1>Словарь строительных терминов</h1>
        <c:if test="${empty termToc}">
            <p>
                Пока в нашем словаре нет терминов.
            </p>
        </c:if>
        <c:if test="${not empty termToc}">
            <tiles:insertDefinition name="common-alpha-toc-horizontal">
                <tiles:putAttribute name="toc" value="${termToc}" />
                <tiles:putAttribute name="baseURL" value="${'/term'}" />
                <tiles:putAttribute name="groupName" value="${groupName}" />
            </tiles:insertDefinition>
        </c:if>
        <c:if test="${empty groupName}">
            <p class="info">
                Собрание из <c:out value="${termTotal} ${fn2:ruplural(termTotal, 'термина/терминов/терминов')}" />, используемых в области строительства и ремонта.
            </p>
        </c:if>
        <c:if test="${not empty groupName}">
            <% pageContext.setAttribute("newline", "\r\n");%>
            <c:forEach var="term" items="${termList}">
                <div class="term">
                    <h3><a name="${fn2:xmlid(term.id)}"></a><c:out value="${term.name}" /></h3>
                    <c:forTokens items="${term.description}" delims="${newline}" var="paragraph">
                        <p><c:out value="${paragraph}" /></p>
                    </c:forTokens>
                    <c:if test="${not empty term.source}">
                        <p class="source">${term.source}</p>
                    </c:if>
                </div>
            </c:forEach>
        </c:if>
    </div><!-- #content-right-sidebar-->
</div><!-- #container-->

<div class="sidebar" id="sideRight">
    <c:if test="${not empty termToc && not empty groupName}">
        <ul class="v-list inside term-toc">
            <c:forEach var="term" items="${termList}">
                <li><a class="act" href="#${fn2:xmlid(term.id)}"><c:out value="${term.name}" /></a></li>
            </c:forEach>
        </ul>
    </c:if>
</div><!-- .sidebar#sideRight -->
