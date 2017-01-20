<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>

<div id="content-no-sidebar">
    <tiles:insertDefinition name="secured-company-settings-menu" />

    <h1>Удаление региона доставки</h1>
    <form class="plain" method="post" action="<spring:url value="/secured/company/settings/delivery/region/delete/{id}">
              <spring:param name="id" value="${deliveryRegion.id}" />
          </spring:url>">
        <p>
            Вы уверены что хотите удалить регион
            <tiles:insertDefinition name="region-path">
                <tiles:putAttribute name="_region" value="${deliveryRegion}" />
                <tiles:putAttribute name="_regionPath" value="${deliveryRegion.path}" />
            </tiles:insertDefinition>
            из регионов доставки?
        </p>
        <div class="submit">
            <input type="submit" value="Удалить регион" />
        </div>
    </form>

</div><!-- #content-->