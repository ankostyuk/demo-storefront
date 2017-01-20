<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>

<tiles:useAttribute name="_region" ignore="true" />
<tiles:useAttribute name="_regionPath" />

<c:if test="${not empty _region}"><c:out value="${_region.name}" /></c:if>
<c:set var="len" value ="${fn:length(_regionPath)}" />
<c:if test="${len > 0}">
    <span class="region-path">
        (<c:forEach var="i" begin="1" end="${len}" step="1">
            <c:out value="${_regionPath[len - i].name}" />
            <c:if test="${i < len}">, </c:if>
        </c:forEach>)
    </span>
</c:if>