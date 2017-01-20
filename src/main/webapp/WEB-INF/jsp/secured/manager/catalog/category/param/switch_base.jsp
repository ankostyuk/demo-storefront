<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div class="col1">
    <h2>Изменение параметра категории «<c:out value="${catalogItem.name}" />» группы «<c:out value="${paramGroup.name}" />»</h2>
    <p>
        Основные параметры категории отображаются первыми при поиске и просмотре товарных предложений.
    </p>
    <p>
        <c:if test="${p.base}">
            Сделать параметр «<c:out value="${p.name}" />» неосновным?
        </c:if>
        <c:if test="${not p.base}">
            Сделать параметр «<c:out value="${p.name}" />» основным?
        </c:if>
    </p>
    <form class="basic" action="<spring:url value="/secured/manager/catalog/category/param/switch/base/{id}">
              <spring:param name="id" value="${p.id}" />
          </spring:url>" method="post">
        <div class="field">
            <c:if test="${p.base}">
                <input type="submit" value="Сделать неосновным" />
            </c:if>
            <c:if test="${not p.base}">
                <input type="submit" value="Сделать основным" />
            </c:if>
        </div>
    </form>
</div>
<div class="col2">

</div>