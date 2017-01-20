<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fn2" uri="http://www.nullpointer.ru/fn2/tags" %>
<div class="col1">
    <h2>Единицы измерения</h2>
    <c:if test="${empty unitList}">
        <p>
            Единицы измерения отсутствуют
        </p>
    </c:if>
    <c:if test="${not empty unitList}">
        <ul class="unit-list">
            <c:forEach var="unit" items="${unitList}">
                <li><a title="Редактировать единицу измерения" href="<spring:url value='/secured/manager/catalog/unit/edit/{id}'>
                           <spring:param name='id' value='${unit.id}' />
                       </spring:url>">
                        <c:out value="${unit.name}"/> (${fn2:htmlformula(unit.abbreviation)})
                    </a>
                </li>
            </c:forEach>
        </ul>
    </c:if>
    <div class="action-set">
        <p>
            <a class="action enabled" href="<spring:url value='/secured/manager/catalog/unit/add'></spring:url>">Добавить единицу измерения</a>
        </p>
    </div>
</div>
<div class="col2">

</div>