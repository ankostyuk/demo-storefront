<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>

<div class="col0">
    <form class="search-box" action="<spring:url value="/secured/manager/company" />" method="get">
        <div>
            <input type="text" name="text" value="${param.text}"/>
            <input type="submit" value="Найти поставщика" />
            <c:if test="${not empty param.text}"><a href="<spring:url value="/secured/manager/company" />">Показать всех</a></c:if>
        </div>
    </form>
    <c:if test="${empty queryResult.list}">
        <p>Не найдено ни одного поставщика</p>
    </c:if>
    <c:if test="${not empty queryResult.list}">
        <p>
            Всего поставщиков — ${queryResult.total}, показаны — с ${queryResult.firstNumber} по ${queryResult.lastNumber}
        </p>
        <table class="manager-company-list" cellspacing="0">
            <thead>
                <tr>
                    <th>Название</th>
                    <th>Электронная почта</th>
                    <th>Телефон</th>
                    <th>Сайт</th>
                    <th>Последний вход</th>
                    <th>Почта подтверждена</th>
                    <th>Адрес</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="company" items="${queryResult.list}" varStatus="st">
                    <c:set var="account" value="${accountMap[company.id]}" />
                    <tr <c:if test="${st.index % 2 != 0}">class="even"</c:if><c:if test="${st.index % 2 == 0}">class="odd"</c:if>>
                        <td>
                            <a title="Посмотреть карточку" href="<spring:url value="/company/{id}">
                                   <spring:param name="id" value="${company.id}" />
                               </spring:url>"><c:out value="${company.name}" /></a>
                        </td>
                        <td>
                            <c:if test="${not empty account.emailAuthenticatedDate}">
                                <a title="Войти от имени поставщика" href="<spring:url value="/secured/manager/company/login">
                                       <spring:param name="email" value="${account.email}" />
                                   </spring:url>"><c:out value="${account.email}" /></a>
                            </c:if>
                            <c:if test="${empty account.emailAuthenticatedDate}">
                                <span class="disabled" title="Адрес не подтвержден"><c:out value="${account.email}" /></span>
                            </c:if>
                        </td>
                        <td>
                            <c:if test="${not empty company.contactPhone}">
                                <c:if test="${not empty company.contactPerson}">
                                    <abbr title="<c:out value="${company.contactPerson}" />">
                                        <c:out value="${company.contactPhone}" />
                                    </abbr>
                                </c:if>
                                <c:if test="${empty company.contactPerson}">
                                    <c:out value="${company.contactPhone}" />
                                </c:if>
                            </c:if>
                        </td>
                        <td>
                            <c:if test="${not empty company.site}">
                                <a href="<c:url value="${company.site}" />"><c:out value="${fn:substringAfter(company.site, '://')}" /></a>
                            </c:if>
                        </td>
                        <td><fmt:formatDate pattern="dd.MM.yyyy HH:mm" value="${account.lastLoginDate}"/></td>
                        <td><fmt:formatDate pattern="dd.MM.yyyy" value="${account.emailAuthenticatedDate}"/></td>
                        <td>
                            <c:if test="${not empty company.address}">
                                <c:out value="${company.address}" />
                            </c:if>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
        <c:if test="${queryResult.total > queryResult.pageSize}">
            <tiles:insertTemplate template="/WEB-INF/jsp/template/common-pager.jsp">
                <tiles:putAttribute name="queryResult" value="${queryResult}" />
                <tiles:putAttribute name="pagerUrl">
                    <spring:url value="/secured/manager/company">
                        <c:if test="${not empty param.text}"><spring:param name="text" value="${param.text}" /></c:if>
                    </spring:url>
                </tiles:putAttribute>
                <tiles:putAttribute name="pagerUrlHasParams" value="${not empty param.text}" />
            </tiles:insertTemplate>
        </c:if>
    </c:if>
</div>

