<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>

<div id="content-no-sidebar">
    <tiles:insertDefinition name="secured-company-settings-menu" />
    <h1>Условия доставки</h1>
    <c:url var="action" value="/secured/company/settings/delivery" />
    <form:form cssClass="plain" modelAttribute="company" action="${action}">
        <c:if test="${not empty paramValues['updated']}">
            <p class="message"><span class="form-updated">Настройки успешно сохранены</span></p>
        </c:if>
        <spring:bind path="company.*" >
            <c:if test="${status.error || (not empty regionError && regionError)}">
                <p class="message"><span class="form-error">Некорректно заполнены некоторые поля ниже</span></p>
            </c:if>
        </spring:bind>
        <h3>Регионы доставки</h3>
        <div class="delivery-region-items-box">
            <ul class="delivery-region-items">
                <li class="template" style="display: none;">
                    <div class="item">
                        <div class="properties">
                            <p class="name">&nbsp;</p>
                            <p class="path">&nbsp;</p>
                        </div>
                        <div class="tools">
                            <a class="delivery-region-delete" title="Удалить регион доставки" href="<spring:url value="/secured/company/settings/delivery/region/delete/" />">&nbsp;</a>
                        </div>
                    </div>
                    <input type="hidden" />
                </li>
                <c:forEach items="${deliveryRegionList}" var="deliveryRegion">
                    <li id="delivery-region-${deliveryRegion.id}">
                        <div class="item">
                            <div class="properties">
                                <p class="name"><c:out value='${deliveryRegion.name}' /></p>
                                <c:if test="${not empty deliveryRegion.path}">
                                    <p class="path">
                                        <c:set var="pathLast" value ="${fn:length(deliveryRegion.path) - 1}" />
                                        <c:forEach var="i" begin="0" end="${pathLast}" step="1"><!-- TODO ? not supported size, step -1 -->
                                            <c:out value='${deliveryRegion.path[pathLast - i].name}' />
                                            <c:if test="${i < pathLast}">,</c:if>
                                        </c:forEach>
                                    </p>
                                </c:if>
                            </div>
                            <div class="tools">
                                <a class="delivery-region-delete" title="Удалить регион доставки" href="<spring:url value="/secured/company/settings/delivery/region/delete/{id}">
                                       <spring:param name="id" value="${deliveryRegion.id}" />
                                   </spring:url>">&nbsp;</a>
                            </div>
                        </div>
                        <input type="hidden" name="deliveryRegionId" value="${deliveryRegion.id}" />
                    </li>
                </c:forEach>
            </ul>
        </div>
        <div class="add-region-box">
            <ul class="field-group">
                <li>
                    <tiles:insertDefinition name="region-init">
                        <tiles:putAttribute name="_propertyPath" value="${'newDeliveryRegionId'}" />
                        <tiles:putAttribute name="_labelText" value="${'Добавить'}" />
                        <tiles:putAttribute name="_hintText" value="${'Действует на все предложения с доставкой'}" />
                        <tiles:putAttribute name="_regionListByText" value="${initRegionListByText}" />
                        <tiles:putAttribute name="_regionText" value="${initRegionText}" />
                        <tiles:putAttribute name="_noSuggest" value="true" />
                        <tiles:putAttribute name="_errorMessage" value="${not empty initRegionError ? 'Может уточните регион?' : null}" />
                    </tiles:insertDefinition>
                </li>
            </ul>
        </div>

        <h3>Условия осуществления доставки</h3>
        <ul class="field-group">
            <li>
                <form:textarea rows="4" cols="20" cssClass="text" path="deliveryConditions" />
                <form:errors cssClass="error" path="deliveryConditions" />
                <p class="hint">Например: Москва от 180 руб. в пределах МКАД, от 365 руб. за МКАД</p>
            </li>
        </ul>
        <div class="submit">
            <input type="submit" value="Сохранить"/>
        </div>
    </form:form>

</div><!-- #content-->

<script type="text/javascript">
    // <![CDATA[
    jsHelper.document.ready(function(){
        <%-- Редактирование регионов доставки --%>
        var regionItemList = $("ul.delivery-region-items");

        var inputRegionName = $("#_initRegionText");
        var inputRegionId = $("#newDeliveryRegionId").attr({id: "_initRegionId", name: "_initRegionId"});;

        jsHelper.buildRegionSuggest({
            url: "<c:url value='/region/suggest' />",
            nameInputSelector: "#_initRegionText",
            onRegionSelect: function(region) {
                inputRegionId.val(region.id);

                var existRegionItem = $("#delivery-region-" + region.id);
                if (existRegionItem.length > 0) {
                    inputRegionName.effect("transfer", {to: existRegionItem, className: "delivery-region-exist-effect-transfer"}, jsHelper.transferEffectSpeed);
                    return;
                }
                
                var regionItem = regionItemList.find("> li.template").clone(true)
                                    .removeClass("template")
                                    .attr("id", "delivery-region-" + region.id);

                regionItem.find("p.name").text(region.name);

                var path = "";
                for (var i = region.path.length - 1; i >= 0; i--) {
                    path += region.path[i].name + (i == 0 ? "" : ", ");
                }
                regionItem.find("p.path").text(path);

                var deleteLink = regionItem.find("a.delivery-region-delete");
                deleteLink.attr("href", deleteLink.attr("href") + region.id);

                regionItem.find("input[type='hidden']").attr({
                    name: "deliveryRegionId",
                    value: region.id
                });

                regionItem.appendTo(regionItemList).slideDown(jsHelper.effectSpeed);

                inputRegionName.val("");
                inputRegionId.val("");
            }
        });

        regionItemList.find("a.delivery-region-delete").click(function(e){
            e.preventDefault();
            jsHelper.removeContent($(this).parents("ul.delivery-region-items > li"));
        });
    });
    // ]]>
</script>
