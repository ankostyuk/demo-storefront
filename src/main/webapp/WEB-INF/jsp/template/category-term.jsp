<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="fn2" uri="http://www.nullpointer.ru/fn2/tags" %>

<tiles:useAttribute name="_param" ignore="true" />
<% pageContext.setAttribute("newline", "\r\n");%>

<c:if test="${not empty _param}">
    <div class="term">
        <h3><a name="${fn2:xmlid(_param.id)}"></a><c:out value="${_param.name}" /></h3>
        <c:forTokens items="${_param.description}" delims="${newline}" var="paragraph">
            <p><c:out value="${paragraph}" /></p>
        </c:forTokens>
    </div>
</c:if>
