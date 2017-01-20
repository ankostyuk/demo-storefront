<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fn2" uri="http://www.nullpointer.ru/fn2/tags" %>

<tiles:importAttribute name="toc" />
<tiles:importAttribute name="baseURL" ignore="true" />
<tiles:importAttribute name="groupName" ignore="true" />

<table class="alpha-toc-h">
    <tbody>
        <tr>
            <td>
                <c:set var="alphabet" value="А Б В Г Д Е Ё Ж З И К Л М Н О П Р С Т У Ф Х Ц Ч Ш Щ Э Ю Я" />
                <c:forEach var="alphabetItem" items="${fn:split(alphabet, ' ')}">
                    <c:choose>
                        <c:when test="${fn2:contains(toc, alphabetItem)}">
                            <c:set var="href">
                                <c:if test="${empty baseURL}">
                                    #${fn2:xmlid(alphabetItem)}
                                </c:if>
                                <c:if test="${not empty baseURL}">
                                    <c:url value="${baseURL}/${alphabetItem}" />
                                </c:if>
                            </c:set>
                            <c:if test="${alphabetItem == groupName}">
                                <span class="current"><c:out value="${alphabetItem}" /></span>
                            </c:if>
                            <c:if test="${alphabetItem != groupName}">
                                <span> <a href="${href}"><c:out value="${alphabetItem}" /></a></span><%-- пробел между </a> и </span> - иначе ИЕ продлевает подчеркивание --%>
                            </c:if>
                        </c:when>
                        <c:otherwise>
                            <span><c:out value="${alphabetItem}" /></span>
                        </c:otherwise>
                    </c:choose>
                </c:forEach>
            </td>
        </tr>
        <tr>
            <td>
                <c:set var="alphabet" value="A B C D E F G H I J K L M N O P Q R S T U V W X Y Z" />
                <c:forEach var="alphabetItem" items="${fn:split(alphabet, ' ')}">
                    <c:choose>
                        <c:when test="${fn2:contains(toc, alphabetItem)}">
                            <c:set var="href">
                                <c:if test="${empty baseURL}">
                                    #${fn2:xmlid(alphabetItem)}
                                </c:if>
                                <c:if test="${not empty baseURL}">
                                    <c:url value="${baseURL}/${alphabetItem}" />
                                </c:if>
                            </c:set>
                            <c:if test="${alphabetItem == groupName}">
                                <span class="current"><c:out value="${alphabetItem}" /></span>
                            </c:if>
                            <c:if test="${alphabetItem != groupName}">
                                <span> <a href="${href}"><c:out value="${alphabetItem}" /></a></span><%-- пробел между </a> и </span> - иначе ИЕ продлевает подчеркивание --%>
                            </c:if>
                        </c:when>
                        <c:otherwise>
                            <span><c:out value="${alphabetItem}" /></span>
                        </c:otherwise>
                    </c:choose>
                </c:forEach>
                <c:if test="${fn2:contains(toc, '0')}">
                    <c:set var="href">
                        <c:if test="${empty baseURL}">
                            #${fn2:xmlid('0')}
                        </c:if>
                        <c:if test="${not empty baseURL}">
                            <c:url value="${baseURL}/0" />
                        </c:if>
                    </c:set>
                    <c:if test="${'0' == groupName}">
                        <span class="digits current">0&nbsp;-&nbsp;9</span>
                    </c:if>
                    <c:if test="${'0' != groupName}">
                        <span class="digits"> <a href="${href}">0&nbsp;-&nbsp;9</a></span><%-- пробел между </a> и </span> - иначе ИЕ продлевает подчеркивание --%>
                    </c:if>
                </c:if>
            </td>
        </tr>
    </tbody>
</table>
