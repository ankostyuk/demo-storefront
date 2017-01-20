<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>

<div class="col1">
    <h2>Управление каталогом товаров</h2>
    <ul>
        <li><a href="<c:url value='/secured/manager/catalog/structure' />">Структура каталога товаров</a></li>
        <li><a href="<c:url value='/secured/manager/catalog/unit' />">Единицы измерения</a></li>
    </ul>
</div>
<div class="col2">

</div>