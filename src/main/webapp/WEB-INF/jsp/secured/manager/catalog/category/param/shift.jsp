<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div class="col1">
    <h2>Перемещение параметра категории «<c:out value="${catalogItem.name}" />» группы «<c:out value="${paramGroup.name}" />»</h2>
    <p>
        Переместить <c:if test="${shiftUp}">выше</c:if><c:if test="${not shiftUp}">ниже</c:if> параметр «<c:out value="${p.name}" />»?
    </p>
    <form class="basic" action="<spring:url value="/secured/manager/catalog/category/param/shift/{dir}/{id}">
              <spring:param name="dir" value="${shiftUp ? 'up' : 'down'}"/>
              <spring:param name="id" value="${p.id}" />
          </spring:url>" method="post">
        <div class="field">
            <input type="submit" value="Переместить" />
        </div>
    </form>
</div>
<div class="col2">

</div>