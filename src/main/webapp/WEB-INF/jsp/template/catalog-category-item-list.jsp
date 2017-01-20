<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<tiles:useAttribute name="_sectionList" />
<tiles:useAttribute name="_sectionCategoryMap" />
<tiles:useAttribute name="_maxColumnCount" />
<tiles:useAttribute name="_urlParams" ignore="true" />

<c:set var="sectionListSize" value="${fn:length(_sectionList)}"/>

<c:if test="${sectionListSize > 0}">
    <c:if test="${sectionListSize < _maxColumnCount}">
        <c:set var="_maxColumnCount" value="${sectionListSize}"></c:set>
    </c:if>
    <c:set var="divColumnCount" value="${100/_maxColumnCount}"></c:set>
    <table class="catalog-item-list">
        <tbody>
            <tr>
                <c:forEach var="item" items="${_sectionList}" varStatus="status">
                    <c:set var="rest" value="${status.count % _maxColumnCount}" />
                    <td <c:if test="${rest == 0}">class="last"</c:if> <c:if test="${status.count <= _maxColumnCount && sectionListSize > 1}">style="width: ${divColumnCount}%;"</c:if>>
                        <span class="catalog-item-top"><c:out value="${item.name}" /></span>
                        <p class="catalog-item-sub">
                            <c:forEach var="subItem" items="${_sectionCategoryMap[item.id]}" varStatus="st">
                                <%--Пробел между </a> и </span> обязателен - IE bug --%>
                                <span <c:if test="${st.last}">class="last"</c:if>>
                                    <a href="<spring:url value='/category/{id}'>
                                           <spring:param name='id' value='${subItem.id}' />
                                       </spring:url><c:if test="${not empty _urlParams}">?${_urlParams}</c:if>" ><c:out value="${subItem.name}" /></a>
                                </span>
                            </c:forEach>
                        </p>
                    </td>
                    <c:if test="${!status.last && rest == 0}"></tr><tr></c:if>
                    <c:if test="${status.last && rest != 0}"><c:forEach begin="1" end="${_maxColumnCount - rest}"><td></td></c:forEach></c:if>
                </c:forEach>
            </tr>
        </tbody>
    </table>
</c:if>
