<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<tiles:useAttribute name="_itemList" />
<tiles:useAttribute name="_subItemMap" />
<tiles:useAttribute name="_maxColumnCount" />

<c:set var="itemListSize" value="${fn:length(_itemList)}"/>

<c:if test="${itemListSize > 0}">
    <c:if test="${itemListSize < _maxColumnCount}">
        <c:set var="_maxColumnCount" value="${itemListSize}"></c:set>
    </c:if>
    <c:set var="divColumnCount" value="${100/_maxColumnCount}"></c:set>
    <table class="catalog-item-list<c:if test="${itemListSize > _maxColumnCount*3}"> compact</c:if>">
        <tbody>
            <tr>
                <c:forEach var="item" items="${_itemList}" varStatus="status">
                    <c:set var="itemUrl">
                        <c:if test="${item.type == 'SECTION'}">
                            <spring:url value='/section/{id}'><spring:param name='id' value='${item.id}' /></spring:url>
                        </c:if>
                        <c:if test="${item.type == 'CATEGORY'}">
                            <spring:url value='/category/{id}'><spring:param name='id' value='${item.id}' /></spring:url>
                        </c:if>
                    </c:set>
                    <c:set var="rest" value="${status.count % _maxColumnCount}" />
                    <td <c:if test="${rest == 0}">class="last"</c:if> <c:if test="${status.count <= _maxColumnCount && itemListSize > 1}">style="width: ${divColumnCount}%;"</c:if>>
                        <span class="catalog-item-top"><a href="${itemUrl}"><c:out value="${item.name}" /></a></span>
                        <p class="catalog-item-sub">
                            <c:forEach var="subItem" items="${_subItemMap[item.id]}" varStatus="st">
                                <c:set var="subItemUrl">
                                    <c:if test="${subItem.type == 'SECTION'}">
                                        <spring:url value='/section/{id}'><spring:param name='id' value='${subItem.id}' /></spring:url>
                                    </c:if>
                                    <c:if test="${subItem.type == 'CATEGORY'}">
                                        <spring:url value='/category/{id}'><spring:param name='id' value='${subItem.id}' /></spring:url>
                                    </c:if>
                                </c:set>
                                <%--Пробел между </a> и </span> обязателен - IE bug --%>
                                <span <c:if test="${st.last}">class="last"</c:if>>
                                    <a href="${subItemUrl}" ><c:out value="${subItem.name}" /></a><c:if test="${st.last}"><span>&nbsp;</span><a href="${itemUrl}">...</a></c:if>
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
