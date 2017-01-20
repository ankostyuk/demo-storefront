<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<div class="col1">
    <h2>Зарегистрировано поставщиков</h2>
    <table class="reg-count">
        <tbody>
            <tr>
                <td class="label">сегодня</td>
                <td class="count">${companiesThisDay}</td>
            </tr>
            <tr>
                <td class="label">за эту неделю</td>
                <td class="count">${companiesThisWeek}</td>
            </tr>
            <tr>
                <td class="label">за этот месяц</td>
                <td class="count">${companiesThisMonth}</td>
            </tr>
            <tr>
                <td class="label">всего</td>
                <td class="count"><strong>${companiesTotal}</strong></td>
            </tr>
        </tbody>
    </table>
</div>
<div class="col2">

</div>