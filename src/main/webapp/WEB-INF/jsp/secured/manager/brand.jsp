<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fn2" uri="http://www.nullpointer.ru/fn2/tags" %>

<div class="col1">

    <c:if test="${empty brandToc}">
        <p>
            В каталоге еще нет брендов. Вы можете <a href="<spring:url value="/secured/manager/brand/add" />">добавить их прямо сейчас</a>.
        </p>
    </c:if>

    <c:if test="${not empty brandToc}">

        <tiles:insertDefinition name="common-alpha-toc-horizontal">
            <tiles:putAttribute name="toc" value="${brandToc}" />
        </tiles:insertDefinition>

        <c:forEach var="brandGroup" items="${brandGroupList}">
            <h3 class="brand-group">
                <c:out value="${brandGroup.name == '0' ? '0 - 9' : brandGroup.name}" /><a name="${fn2:xmlid(brandGroup.name)}" title="В начало" href="#">^</a>
            </h3>
            <ul class="brand-list">
                <c:forEach var="brand" items="${brandGroup.list}">
                    <li><a href="<spring:url value="/secured/manager/brand/edit/{id}">
                               <spring:param name="id" value="${brand.id}" />
                           </spring:url>"><c:out value="${brand.name}" /></a></li>
                    </c:forEach>
            </ul>
        </c:forEach>
    </c:if>
</div>
<div class="col2">
    <c:if test="${not empty brandToc}">
        <ul class="result-toolbox">
            <li>
                <a href="<spring:url value="/secured/manager/brand/add" />">Добавить бренд</a>
            </li>
        </ul>
    </c:if>

    <c:if test="${not empty unlinkedBrandNames}">
        <h4>Наименования несвязанных брендов</h4>
        <ul class="unlinked-brands">
            <c:forEach var="unlinkedName" items="${unlinkedBrandNames}">
                <li>
                    <a title="Связать предложения с брендом" href="<spring:url value="/secured/manager/brand/link">
                           <spring:param name="name" value="${unlinkedName.value}" />
                       </spring:url>"><c:out value="${unlinkedName.value}" /></a>
                    <span class="count">${unlinkedName.count}</span>
                </li>
            </c:forEach>
        </ul>
    </c:if>
</div>