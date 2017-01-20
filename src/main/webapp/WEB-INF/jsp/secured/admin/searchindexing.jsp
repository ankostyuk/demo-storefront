<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="col1">
    <p><strong>Запущено задач по индексации: ${runningTaskCount}</strong></p>
    <form action="<c:url value='/secured/admin/searchindexing/catalog/recreate' />" method="post">
        <p>
            <input type="submit" value="Пересоздать поисковый индекс каталога" />
        </p>
    </form>
    <form action="<c:url value='/secured/admin/searchindexing/company/recreate' />" method="post">
        <p>
            <input type="submit" value="Пересоздать поисковый индекс компаний" />
        </p>
    </form>
</div>
<div class="col2">
</div>