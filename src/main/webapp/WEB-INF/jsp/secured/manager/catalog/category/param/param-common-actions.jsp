<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<div class="action-set">
    <p>
        <a class="action enabled" href="<spring:url value='/secured/manager/catalog/category/param/edit/order/{id}'>
               <spring:param name='id' value='${paramId}' />
           </spring:url>">Изменить порядок параметра в группе</a>
    </p>
</div>
<div class="action-set">
    <p>
        <a class="action enabled" href="<spring:url value="/secured/manager/catalog/category/param/delete/{id}">
               <spring:param name='id' value='${paramId}' />
           </spring:url>">Удалить параметр</a>
    </p>
</div>
