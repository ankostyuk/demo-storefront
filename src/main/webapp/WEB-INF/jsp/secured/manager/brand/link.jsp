<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>

<div class="col1">
    <h1>Связывание предложений</h1>
    <p>
        Связать предложения с «<strong><c:out value="${name}" /></strong>» в качестве имени бренда
    </p>
    <p>
        Если бренд еще не существует, вы можете <a title="Добавить бренд «<c:out value='${name}' />»" href="<spring:url value="/secured/manager/brand/add">
                                                       <spring:param name="name" value="${name}" />
                                                   </spring:url>">добавить его</a>
    </p>
    <form class="basic" action="<spring:url value="/secured/manager/brand/link"/>" method="post">
        <div class="field">
            <input type="hidden" name="name" value="${name}" />
            <input type="hidden" name="brandId" value="" />
            <label for="brandName">Бренд </label>
            <input type="text" name="brandName" id="brandName" value="" />
        </div>
        <div class="field">
            <input type="submit" value="Связать предложения" />
        </div>
    </form>
</div>
<div class="col2">

</div>

<script type="text/javascript">
    // <![CDATA[
    $(document).ready(function(){
        $("input[type='text'][name='brandName']").suggest({
            request: {
                url: "<c:url value="/brand/suggest" />"
            },
            data: {
                searchField: "name"
            },
            onSelected: function(itemData) {
                $("input[name='brandId']").val(itemData.id);
            },
            onBlurWithChanged: function() {
                $("input[name='brandId']").val("");
            }
        });
    });
    // ]]>
</script>