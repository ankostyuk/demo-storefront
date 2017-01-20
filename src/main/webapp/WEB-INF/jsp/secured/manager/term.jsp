<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fn2" uri="http://www.nullpointer.ru/fn2/tags" %>

<div class="col1">

    <c:if test="${empty termToc}">
        <p>
            В словаре нет терминов. <a href="<spring:url value="/secured/manager/term/add" />">Добавить термин</a>.
        </p>
    </c:if>

    <c:if test="${not empty termToc}">

        <tiles:insertDefinition name="common-alpha-toc-horizontal">
            <tiles:putAttribute name="toc" value="${termToc}" />
        </tiles:insertDefinition>

        <c:forEach var="termGroup" items="${termGroupList}">
            <h3 class="term-group">
                <c:out value="${termGroup.name == '0' ? '0 - 9' : termGroup.name}" />&nbsp;<a name="${fn2:xmlid(termGroup.name)}" title="В начало" href="#">^</a>
            </h3>
            <ul class="term-list">
                <c:forEach var="term" items="${termGroup.list}">
                    <li><a href="<spring:url value="/secured/manager/term/edit/{id}">
                           <spring:param name="id" value="${term.id}" />
                           </spring:url>"><c:out value="${term.name}" /></a></li>
                </c:forEach>
            </ul>
        </c:forEach>
    </c:if>
</div>
<div class="col2">
    <c:if test="${not empty termToc}">
        <ul class="result-toolbox">
            <li>
                <a href="<spring:url value="/secured/manager/term/add" />">Добавить термин</a>
            </li>
        </ul>
    </c:if>
</div>