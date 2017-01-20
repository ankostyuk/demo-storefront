<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>

<div id="content-no-sidebar">
    <h1>Настройки</h1>
    <%-- необходимо чтобы параметр "updated" отсутствовал в URL формы, поэтому задаем action явно --%>
    <c:url var="action" value="/settings" />
    <form:form cssClass="plain settings" modelAttribute="settings" action="${action}">
        <c:if test="${not empty paramValues['updated']}">
            <p class="message"><span class="form-updated">Настройки успешно сохранены</span></p>
        </c:if>
        <h3>Регион</h3>
        <ul>
            <li>
                <tiles:insertDefinition name="region-init">
                    <tiles:putAttribute name="_modelAttribute" value="${'settings'}" />
                    <tiles:putAttribute name="_propertyPath" value="${'regionId'}" />
                    <tiles:putAttribute name="_regionListByText" value="${initRegionListByText}" />
                    <tiles:putAttribute name="_regionText" value="${userRegion != null ? userRegion.name : initRegionText}" />
                    <tiles:putAttribute name="_baseRegion" value="${userRegion != null ? userRegion : initRegion}" />
                    <tiles:putAttribute name="_errorMessage" value="${not empty initRegionError ? 'Может уточните регион?' : null}" />
                </tiles:insertDefinition>
            </li>
            <li><label class="regionAware"><form:checkbox cssClass="checkbox" path="regionAware" />не показывать предложения других регионов</label></li>
        </ul>
        <h3>Показывать цену</h3>
        <ul>
            <li><label><form:radiobutton class="radio" path="priceType" value="DEFAULT" />пересчитанную в основную валюту</label></li>
            <li><label><form:radiobutton class="radio" path="priceType" value="EXTRA_CURRENCY" />пересчитанную в основную и дополнительную валюту</label></li>
        </ul>
        <p class="info">
            Основная валюта&nbsp;&mdash; <strong><spring:message code="currency.default.name" /></strong>&nbsp;(<spring:message code="currency.default.abbr" />)
        </p>
        <h4>Дополнительная валюта</h4>
        <ul>
            <c:forEach var="extraCurrency" items="${extraCurrencyList}">
                <li>
                    <label>
                        <form:radiobutton class="radio" path="extraCurrency" value="${extraCurrency.value}" />
                        <spring:message code="currency.${extraCurrency.name}.name" />&nbsp;(<spring:message code="currency.${extraCurrency.name}.abbr" />)
                    </label>
                </li>
            </c:forEach>
        </ul>
        <p class="info">
            Пересчет цен по курсу ЦБ РФ
        </p>
        <h3>Количество предложений на странице</h3>
        <ul>
            <li><label><form:radiobutton class="radio" path="pageSize" value="10" />10</label></li>
            <li><label><form:radiobutton class="radio" path="pageSize" value="15" />15</label></li>
            <li><label><form:radiobutton class="radio" path="pageSize" value="20" />20</label></li>
            <li><label><form:radiobutton class="radio" path="pageSize" value="50" />50</label></li>
            <li><label><form:radiobutton class="radio" path="pageSize" value="100" />100</label></li>
        </ul>
        <div class="submit">
            <input type="submit" value="Сохранить настройки"/>
        </div>
    </form:form>
</div><!-- #content-->
