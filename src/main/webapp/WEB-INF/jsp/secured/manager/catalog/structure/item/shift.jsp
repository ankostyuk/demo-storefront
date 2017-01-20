<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<div class="col1">
    <h2>Изменение порядка элемента каталога</h2>
    <h3><c:out value="${itemProperties.item.name}" /></h3>
    <form class="basic" method="POST">
        <div class="field">
            <label>
             Переместить <c:if test="${shiftUp}">вверх</c:if><c:if test="${not shiftUp}">вниз</c:if> на один порядок?
            </label>
            <input type="submit" value="Переместить" />
        </div>
    </form>
</div>
<div class="col2">
    <!-- Column 2 start -->
    <p>column2</p>
    <!-- Column 2 end -->
</div>