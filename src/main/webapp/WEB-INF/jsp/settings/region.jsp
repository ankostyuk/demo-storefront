<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>

<div id="content-no-sidebar">
    <h1>Настройка региона</h1>
    <%-- необходимо чтобы параметр "updated" отсутствовал в URL формы, поэтому задаем action явно --%>
    <c:url var="action" value="/settings/region" />
    <form:form cssClass="plain settings" modelAttribute="settings" action="${action}">
        <c:if test="${not empty paramValues['updated']}">
            <p class="message"><span class="form-updated">Настройка региона успешно сохранена</span></p>
        </c:if>
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
        <div class="submit">
            <input type="submit" value="Сохранить"/>
        </div>
    </form:form>
</div><!-- #content-->
