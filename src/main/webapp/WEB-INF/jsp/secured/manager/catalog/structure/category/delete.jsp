<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<div class="col1">
    <h2>Удаление категории каталога</h2>
    <h3><c:out value="${categoryProperties.item.name}" /></h3>
       <c:if test="${categoryProperties.canDelete}">
           <c:if test="${categoryProperties.paramCategory}">
            <p>
                Внимание! Будут удалены все группы <a class="action enabled" href="<spring:url value='/secured/manager/catalog/category/param/{id}'><spring:param name="id" value='${categoryProperties.item.id}' /></spring:url>">параметров категории</a>.
            </p>
           </c:if>
        <form class="basic" method="POST">
            <div class="field">
                <label>
                    Удалить категорию?
                </label>
                <input type="submit" value="Удалить" />
            </div>
        </form>
       </c:if>
       <c:if test="${not categoryProperties.canDelete}">
        <p>
               Категория не может быть удалена!
        </p>
           <p>
               Возможные причины:
           </p>
        <ul>
            <li>в категории имеются товарные предложения — может быть удалена только пустая категория.</li>
        </ul>
       </c:if>
</div>
<div class="col2">
    <!-- Column 2 start -->
    <p>column2</p>
    <!-- Column 2 end -->
</div>