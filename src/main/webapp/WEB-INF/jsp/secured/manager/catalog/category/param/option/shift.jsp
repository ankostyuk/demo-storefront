<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div class="col1">
    <h2>Перемещение варианта выбора</h2>
    <p>
        Переместить <c:if test="${shiftUp}">выше</c:if><c:if test="${not shiftUp}">ниже</c:if> вариант выбора «<c:out value="${paramSelectOption.name}" />»?
    </p>
    <form class="basic" action="<spring:url value="/secured/manager/catalog/category/param/select/option/shift/{dir}/{id}">
              <spring:param name="dir" value="${shiftUp ? 'up' : 'down'}"/>
              <spring:param name="id" value="${paramSelectOption.id}" />
          </spring:url>" method="post">
        <div class="field">
            <input type="submit" value="Переместить" />
        </div>
    </form>
</div>
<div class="col2">

</div>