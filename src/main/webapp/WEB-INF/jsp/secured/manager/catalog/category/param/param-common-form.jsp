<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<p>Символом <span class="mandatory">*</span> отмечены поля, обязательные для заполнения.</p>
<div class="field">
    <form:label path="name">Наименование<span class="mandatory">*</span></form:label>
    <form:input cssClass="text long" path="name" />
    <p class="hint">
        <form:errors cssClass="error" path="name" />
    </p>
</div>
<div class="field">
    <form:label path="description">Описание</form:label>
    <form:textarea cssClass="long" path="description" cols="20" rows="16" />
    <p class="hint">
        <form:errors cssClass="error" path="description" />
    </p>
</div>
<div class="field">
    <label title="Основные параметры категории отображаются первыми при поиске и просмотре товарных предложений">
        <form:checkbox path="base" />
        Основной параметр
    </label>
</div>