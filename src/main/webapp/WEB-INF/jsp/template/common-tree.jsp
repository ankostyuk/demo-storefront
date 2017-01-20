<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<%-- Упорядоченный список элементов дерева --%>
<tiles:importAttribute name="nodeList" />
<%-- HTML класс дерева --%>
<tiles:importAttribute name="treeClass" ignore="true" />
<%-- HTML класс текущей ноды --%>
<tiles:importAttribute name="currentClass" ignore="true" />
<%-- HTML класс неактивной ноды --%>
<tiles:importAttribute name="disabledClass" ignore="true" />
<%-- HTML класс наименования ноды --%>
<tiles:importAttribute name="nameClass" ignore="true" />
<%-- HTML класс информационной части ноды --%>
<tiles:importAttribute name="infoClass" ignore="true" />
<%--
    Префикс значения идентификаторов.
    Если префикс пуст - идентификаторы не устанавливаются.
    Значение идентичикаторов берутся из node.data['id']
--%>
<tiles:importAttribute name="idPrefix" ignore="true" />

<c:if test="${empty currentClass}">
    <c:set var="currentClass" value="current" />
</c:if>
<c:if test="${empty disabledClass}">
    <c:set var="disabledClass" value="disabled" />
</c:if>
<c:if test="${empty nameClass}">
    <c:set var="nameClass" value="name" />
</c:if>
<c:if test="${empty infoClass}">
    <c:set var="infoClass" value="info" />
</c:if>

<ul <c:if test="${not empty treeClass}">class="<c:out value="${treeClass}" />"</c:if>>
    <c:set var="oldLevel" value="1" />
    <c:forEach var="node" items="${nodeList}" varStatus="st">
        <c:choose>
            <c:when test="${node.level > oldLevel}">
                <ul>
                <c:set var="oldLevel" value="${node.level}" />
            </c:when>
            <c:when test="${node.level < oldLevel}">
                </li>
                <c:forEach begin="${node.level + 1}" end="${oldLevel}">
                    </ul>
                </li>
                </c:forEach>
                <c:set var="oldLevel" value="${node.level}" />
            </c:when>
            <c:when test="${node.level == oldLevel && not st.first}">
                </li>
            </c:when>
        </c:choose>


        <c:if test="${node.current}">
            <c:set var="class" value="${currentClass}" />
        </c:if>
        <c:if test="${node.disabled}">
            <c:set var="class" value="${class} ${disabledClass}" />
        </c:if>

        <li <c:if test="${not empty idPrefix}">id="<c:out value="${idPrefix}${node.data['id']}" />"</c:if>>
            <c:if test="${not empty node.link && not node.current}">
                <c:set var="nodeUrl">
                    <spring:url value="${node.link.url}" htmlEscape="true">
                        <c:if test="${not empty node.link.paramMap}">
                            <c:forEach var="paramItem" items="${node.link.paramMap}">
                                <spring:param name="${paramItem.key}" value="${paramItem.value}" />
                            </c:forEach>
                        </c:if>
                    </spring:url>
                </c:set>
                <span <c:if test="${not empty class}"> class="${class}"</c:if>><a <c:if test="${not empty node.description}"> title="${node.description}"</c:if> href="${nodeUrl}"><span class="${nameClass}"><c:out value="${node.name}" /></span></a><c:if test="${not empty node.data['info']}"><span class="${infoClass} <c:if test="${node.disabled}"><c:out value=" ${disabledClass}" /></c:if>">&nbsp;<c:out value="${node.data['info']}" /></span></c:if></span>
            </c:if>
            <c:if test="${empty node.link || node.current}">
                <span <c:if test="${not empty class}"> class="${class}"</c:if> <c:if test="${not empty node.description}"> title="${node.description}"</c:if>><span class="${nameClass}"><c:out value="${node.name}" /></span><c:if test="${not empty node.data['info']}"><span class="${infoClass} <c:if test="${node.disabled}"><c:out value=" ${disabledClass}" /></c:if>">&nbsp;<c:out value="${node.data['info']}" /></span></c:if></span>
            </c:if>
                
        <c:set var="class" />

        <c:if test="${st.last}">
            </li>
            <c:forEach begin="1" end="${node.level - 1}">
                </ul>
            </li>
            </c:forEach>
        </c:if>
    </c:forEach>
</ul>
