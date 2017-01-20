<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>

<tiles:useAttribute name="pageKey" ignore="true" />

<p class="copyright">
    ©&nbsp;Бильдика, 2010—2011
</p>
<ul class="contacts h-list">
    <li>
        <c:if test="${pageKey != 'contacts'}">
            <a href="<c:url value="/contacts" />">Контакты</a>
        </c:if>
        <c:if test="${pageKey == 'contacts'}">
            Контакты
        </c:if>
    </li>
    <sec:authorize ifAnyGranted="ROLE_ANONYMOUS">
        <li><a href="<c:url value="/registration/company/partner" />">Реклама</a></li><%-- TODO --%>
    </sec:authorize>
</ul>
<ul class="info h-list">
    <li>
        <c:if test="${pageKey != 'agreement'}">
            <a href="<c:url value="/agreement" />">Пользовательское соглашение</a>
        </c:if>
        <c:if test="${pageKey == 'agreement'}">
            Пользовательское соглашение
        </c:if>
    </li>
</ul>
<p class="designer">
    <a href="http://bbrb.ru/" ></a>
</p>
