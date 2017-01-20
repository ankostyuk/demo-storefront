<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>

<div id="content-no-sidebar">
    <tiles:insertDefinition name="secured-company-settings-menu" />

    <h1>Общие настройки</h1>
    <c:url var="action" value="/secured/company/settings" />
    <form:form modelAttribute="company" enctype="multipart/form-data" action="${action}">
        <c:if test="${not empty paramValues['updated']}">
            <p class="message"><span class="form-updated">Настройки успешно сохранены</span></p>
        </c:if>
        <spring:bind path="company.*" >
            <c:if test="${status.error}">
                <p class="message"><span class="form-error">Некорректно заполнены некоторые поля ниже</span></p>
            </c:if>
        </spring:bind>
        <ul class="field-group">
            <li>
                <form:label cssClass="text" path="name">Название</form:label>
                <form:input cssClass="text" path="name" />
                <form:errors cssClass="error" path="name" />
            </li>
            <li>
                <tiles:insertDefinition name="region-init">
                    <tiles:putAttribute name="_modelAttribute" value="${'company'}" />
                    <tiles:putAttribute name="_propertyPath" value="${'regionId'}" />
                    <tiles:putAttribute name="_labelText" value="${'Свой регион'}" />
                    <tiles:putAttribute name="_hintText" value="${'Необходимо указать один город, например: Москва'}" />
                    <tiles:putAttribute name="_regionListByText" value="${initRegionListByText}" />
                    <tiles:putAttribute name="_regionText" value="${companyRegion != null ? companyRegion.name : initRegionText}" />
                    <tiles:putAttribute name="_baseRegion" value="${companyRegion != null ? companyRegion : initRegion}" />
                </tiles:insertDefinition>
            </li>
            <li>
                <form:label cssClass="text" path="address">Адрес</form:label>
                <form:input cssClass="text" path="address" />
                <form:errors cssClass="error" path="address" />
                <p class="hint">Например: Москва, улица Строителей, 4</p>
            </li>
            <li>
                <form:label cssClass="text" path="contactPhone">Контактный телефон</form:label>
                <form:input cssClass="text" path="contactPhone" />
                <form:errors cssClass="error" path="contactPhone" />
                <p class="hint">Например: +7 495 123-45-67, +7 499 765-43-21 доб. 123</p>
            </li>
            <li>
                <form:label cssClass="text" path="contactPerson">Ф.И.О. контактного лица</form:label>
                <form:input cssClass="text" path="contactPerson" />
                <form:errors cssClass="error" path="contactPerson" />
                <%--<p class="hint">Например: Иванов Иван Иванович</p>--%>
            </li>
            <li>
                <form:label cssClass="text" path="site">Адрес веб-сайта</form:label>
                <form:input cssClass="text" path="site" />
                <form:errors cssClass="error" path="site" />
                <p class="hint">Например: http://www.mycompany.ru</p>
            </li>
            <li>
                <tiles:insertDefinition name="secured-common-image-select">
                    <tiles:putAttribute name="paramName" value="logo" />
                    <tiles:putAttribute name="label" value="Логотип" />
                    <tiles:putAttribute name="reselect" value="${reselectLogo}" />
                    <tiles:putAttribute name="key" value="${company.logo}" />
                    <tiles:putAttribute name="container" value="company" />
                </tiles:insertDefinition>
            </li>     
            <li>
                <form:label cssClass="text" path="scope">Описание области деятельности</form:label>
                <form:textarea rows="4" cols="20" cssClass="text" path="scope" />
                <form:errors cssClass="error" path="scope" />
                <p class="hint">Не более 1&nbsp;000 символов</p>
            </li>
            <li>
                <form:label cssClass="text" path="schedule">Режим работы</form:label>
                <form:textarea rows="4" cols="20" cssClass="text" path="schedule" />
                <form:errors cssClass="error" path="schedule" />
                <p class="hint">Например: Ежедневно с 10:00 до 23:00</p>
            </li>
        </ul>
        <h3 class="field-group-name">Условия осуществления оплаты</h3>
        <ul class="field-group">
            <li>
                <form:label cssClass="text" path="paymentConditions.text">Описание</form:label>
                <form:textarea rows="4" cols="20" cssClass="text" path="paymentConditions.text" />
                <form:errors cssClass="error" path="paymentConditions.text" />
                <p class="hint">Например: При доставке по Москве оплата заказа производится наличными курьеру</p>
            </li>
            <li>
                <label class="checkbox">
                    <form:checkbox cssClass="checkbox" path="paymentConditions.cash" />
                    Оплата наличными
                </label>
            </li>
            <li>
                <label class="checkbox">
                    <form:checkbox class="checkbox" path="paymentConditions.cashless" />
                    Оплата по безналичному расчету
                </label>
            </li>
        </ul>
        <div class="submit">
            <input type="submit" value="Сохранить настройки"/>
        </div>
    </form:form>

</div><!-- #content-->
