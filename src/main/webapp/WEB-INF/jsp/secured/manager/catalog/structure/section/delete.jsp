<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<div class="col1">
    <h2>Удаление раздела каталога</h2>
    <h3><c:out value="${sectionProperties.item.name}" /></h3>
       <c:if test="${sectionProperties.canDelete}">
        <form class="basic" method="POST">
            <div class="field">
                <label>
                    Удалить раздел?
                </label>
                <input type="submit" value="Удалить" />
            </div>
        </form>
       </c:if>
       <c:if test="${not sectionProperties.canDelete}">
           <p>
            Раздел не может быть удален!
           </p>
           <p>
               Возможные причины:
           </p>
        <ul>
            <li>в разделе имеются подразделы или категории — может быть удален только пустой раздел.</li>
        </ul>
       </c:if>
</div>
<div class="col2">
    <!-- Column 2 start -->
    <p>column2</p>
    <!-- Column 2 end -->
</div>