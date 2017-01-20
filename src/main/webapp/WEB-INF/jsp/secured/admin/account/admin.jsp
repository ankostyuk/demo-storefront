<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<sec:authentication property="principal.accountId" var="authenticatedId" />
<div class="col1">
    <c:if test="${empty accountList}">
        <p>
            Ни одного администратора еще не создано.
            Вы можете добавить аккаунт администратора <a href="<spring:url value="/secured/admin/account/admin/add" />">прямо сейчас</a>.
        </p>
    </c:if>
    <c:if test="${not empty accountList}">
        <c:set var="redirectUrlValue">
            <spring:url value="secured/admin/account/admin">
                <c:if test="${not empty param.sort}"><spring:param name="sort" value="${param.sort}" /></c:if>
            </spring:url>
        </c:set>
        <c:set var="redirectUrlValue" value="/${redirectUrlValue}" />

        <table class="accounts" cellspacing="0">
            <thead>
                <tr>
                    <th>
                        <c:choose>
                            <c:when test="${sorting == 'EMAIL_ASCENDING'}">
                                <a title="Показать в обратном порядке" href="<spring:url value="/secured/admin/account/admin">
                                       <spring:param name="sort" value="email-desc" />
                                   </spring:url>"><span class="nowrap">Электронная почта</span></a>&nbsp;&#x25B4;
                            </c:when>
                            <c:when test="${sorting == 'EMAIL_DESCENDING'}">
                                <a title="Показать в алфавитном порядке" href="<spring:url value="/secured/admin/account/admin">
                                       <spring:param name="sort" value="email-asc" />
                                   </spring:url>"><span class="nowrap">Электронная почта</span></a>&nbsp;&#x25BE;
                            </c:when>
                            <c:otherwise>
                                <a title="Сортировать по адресу электронной почты" href="<spring:url value="/secured/admin/account/admin">
                                       <spring:param name="sort" value="email-asc" />
                                   </spring:url>"><span class="nowrap">Электронная почта</span></a>
                            </c:otherwise>
                        </c:choose>
                    </th>
                    <th>
                        <c:choose>
                            <c:when test="${sorting == 'DATE_LAST_LOGIN_ASCENDING'}">
                                <a title="Показать более поздние" href="<spring:url value="/secured/admin/account/admin">
                                       <spring:param name="sort" value="date-last-login-desc" />
                                   </spring:url>"><span class="nowrap">Последний вход</span></a>&nbsp;&#x25B4;
                            </c:when>
                            <c:when test="${sorting == 'DATE_LAST_LOGIN_DESCENDING'}">
                                <a title="Показать более ранние" href="<spring:url value="/secured/admin/account/admin">
                                       <spring:param name="sort" value="date-last-login-asc" />
                                   </spring:url>"><span class="nowrap">Последний вход</span></a>&nbsp;&#x25BE;
                            </c:when>
                            <c:otherwise>
                                <a title="Сортировать по дате последнего входа" href="<spring:url value="/secured/admin/account/admin">
                                       <spring:param name="sort" value="date-last-login-asc" />
                                   </spring:url>"><span class="nowrap">Последний вход</span></a>
                            </c:otherwise>
                        </c:choose>
                    </th>
                    <th>
                        <c:choose>
                            <c:when test="${sorting == 'DATE_REGISTERED_ASCENDING'}">
                                <a title="Показать более поздние" href="<spring:url value="/secured/admin/account/admin">
                                       <spring:param name="sort" value="date-registered-desc" />
                                   </spring:url>"><span class="nowrap">Дата регистрации</span></a>&nbsp;&#x25B4;
                            </c:when>
                            <c:when test="${sorting == 'DATE_REGISTERED_DESCENDING'}">
                                <a title="Показать более ранние" href="<spring:url value="/secured/admin/account/admin">
                                       <spring:param name="sort" value="date-registered-asc" />
                                   </spring:url>"><span class="nowrap">Дата регистрации</span></a>&nbsp;&#x25BE;
                            </c:when>
                            <c:otherwise>
                                <a title="Сортировать по дате регистрации" href="<spring:url value="/secured/admin/account/admin">
                                       <spring:param name="sort" value="date-registered-asc" />
                                   </spring:url>"><span class="nowrap">Дата регистрации</span></a>
                            </c:otherwise>
                        </c:choose>
                    </th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="account" items="${accountList}" varStatus="st">
                    <tr <c:if test="${st.index % 2 != 0}">class="even"</c:if><c:if test="${st.index % 2 == 0}">class="odd"</c:if>>
                        <td><a title="Редактировать" href="<spring:url value="/secured/admin/account/admin/edit/{id}">
                                   <spring:param name="id" value="${account.id}" />
                                   <spring:param name="redirect" value="${redirectUrlValue}" />
                               </spring:url>"><c:out value="${account.email}" /></a>
                            <c:if test="${authenticatedId == account.id}">
                                <span class="me">(это Вы)</span>
                            </c:if>
                        </td>
                        <td><fmt:formatDate pattern="dd.MM.yyyy HH:mm" value="${account.lastLoginDate}"/></td>
                        <td><fmt:formatDate pattern="dd.MM.yyyy HH:mm" value="${account.registrationDate}"/></td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </c:if>
</div>
<div class="col2">
    <ul>
        <li><a href="<spring:url value="/secured/admin/account/admin/add">
                   <spring:param name="redirect" value="${redirectUrlValue}" />
               </spring:url>">Добавить аккаунт администратора</a></li>
    </ul>
</div>