<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<tiles:useAttribute name="_propertyPath" />
<tiles:useAttribute name="_modelAttribute" ignore="true" />
<tiles:useAttribute name="_labelText" ignore="true" />
<tiles:useAttribute name="_hintText" ignore="true" />
<tiles:useAttribute name="_regionListByText" ignore="true" />
<tiles:useAttribute name="_regionText" ignore="true" />
<tiles:useAttribute name="_baseRegion" ignore="true" />
<tiles:useAttribute name="_noSuggest" ignore="true" />
<tiles:useAttribute name="_errorMessage" ignore="true" />

<c:if test="${not empty _labelText}"><label for="_initRegionText" class="text"><c:out value="${_labelText}"/></label></c:if>
<c:if test="${empty _regionListByText}">
    <input id="_initRegionText" type="text" name="_initRegionText" class="text" value="<c:out value='${_regionText}' />" /><%--
    --%><c:if test="${not empty _modelAttribute}"><form:errors cssClass="error" path="${_propertyPath}" />
        <form:hidden path="${_propertyPath}" />
    </c:if><%--
    --%><c:if test="${not empty _errorMessage}"><span class="error"><c:out value="${_errorMessage}"/></span></c:if>
    <c:if test="${empty _modelAttribute}">
        <input id="${_propertyPath}" name="${_propertyPath}" value="" type="hidden" />
    </c:if>
    <p id="_regionPath" class="hint">
        <c:if test="${not empty _baseRegion}">
            <tiles:insertDefinition name="region-path">
                <tiles:putAttribute name="_region" value="${fn:length(_baseRegion.path) > 0 ? _baseRegion : null}" />
                <tiles:putAttribute name="_regionPath" value="${_baseRegion.path}" />
            </tiles:insertDefinition>
        </c:if>
    </p>
    <c:if test="${not empty _hintText}">
        <p class="hint"><c:out value="${_hintText}"/></p>
    </c:if>
    <c:if test="${empty _noSuggest || not _noSuggest}">
        <script type="text/javascript">
            // <![CDATA[
            jsHelper.document.ready(function(){
                $("#${_propertyPath}").attr({id: "_initRegionId", name: "_initRegionId"});
                jsHelper.buildRegionSuggest({
                    url: "<c:url value='/region/suggest' />",
                    nameInputSelector: "#_initRegionText",
                    idInputSelector: "#_initRegionId",
                    regionPathSelector: "#_regionPath"
                });
            });
            // ]]>
        </script>
    </c:if>
</c:if>
<c:if test="${not empty _regionListByText}">
    <input id="_initRegionText" type="text" name="_initRegionText" class="text" value="<c:out value='${_regionText}' />" /><%--
    --%><span class="error">Уточните регион</span>
    <c:if test="${not empty _hintText}">
        <p class="hint"><c:out value="${_hintText}"/></p>
    </c:if>
    <div class="region-suggest-box">
        <ul class="field-group">
            <c:forEach var="region" items="${_regionListByText}">
                <li>
                    <label><input type="radio" class="radio" name="_initRegionId" value="${region.id}" />
                        <tiles:insertDefinition name="region-path">
                            <tiles:putAttribute name="_region" value="${region}" />
                            <tiles:putAttribute name="_regionPath" value="${region.path}" />
                        </tiles:insertDefinition>
                    </label>
                </li>
            </c:forEach>
        </ul>
    </div>
</c:if>
