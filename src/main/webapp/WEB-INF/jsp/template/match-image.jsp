<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>

<tiles:useAttribute name="_matchTitle" />
<tiles:useAttribute name="_imageThumbUrl" />
<tiles:useAttribute name="_imageUrl" />

<div class="match-page-image">
    <div class="image">
        <a class="hover-src-x" href="${_imageUrl}"><img class="zoom" src="${_imageThumbUrl}" alt="<c:out value="${_matchTitle}" />" /></a>
    </div>
    <div class="actions">
        <a class="act zoom hover-dst-x" href="${_imageUrl}">Увеличить</a>
    </div>
</div>

<script type="text/javascript">
    // <![CDATA[
    jsHelper.document.ready(function(){
        <%-- Окно увеличения изображения --%>
        jsHelper.buildImagePopup({
            title: "<c:out value="${_matchTitle}" />",
            imageUrl: "${_imageUrl}",
            pseudoLinkSelector: "div.match-page-image .zoom",
            pseudoLinkClass: "pseudo"
        });
    });
    // ]]>
</script>
