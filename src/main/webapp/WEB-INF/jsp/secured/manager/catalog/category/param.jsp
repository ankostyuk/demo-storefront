<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<div class="col1">
    <h2>Параметры категории «<c:out value="${categoryProperties.item.name}" />»</h2>
    <c:if test="${categoryProperties.paramCategory}">
        <div class="action-set no-border">
            <p class="right">
                <span>Показать </span>
                <c:if test="${baseParams}"><a class="action enabled" href="<spring:url value='/secured/manager/catalog/category/param/{id}'><spring:param name="id" value='${categoryProperties.item.id}' /></spring:url>">все параметры</a></c:if>
                <c:if test="${not baseParams}"><a class="action enabled" href="<spring:url value='/secured/manager/catalog/category/param/{id}'><spring:param name="id" value='${categoryProperties.item.id}' /><spring:param name="base" value='true' /></spring:url>">основные параметры</a></c:if>
            </p>
        </div>
        <div class="param-group-list">
            <ul>
                <c:forEach var="paramGroupProperties" items="${paramGroupList}" varStatus="listStatus">
                    <li>
                        <div class="param-group">
                            <div class="properties">
                                <span class="details"><a title="TODO: Возможность скрытия-показа списка параметров в группе">&nbsp;&#177;</a></span><span class="name"><a title="Редактировать группу параметров" href="<spring:url value='/secured/manager/catalog/category/param/group/edit/{id}'><spring:param name='id' value='${paramGroupProperties.paramGroup.id}' /></spring:url>"><c:out value="${paramGroupProperties.paramGroup.name}"/></a></span>
                            </div>
                            <div class="tools">
                                <span class="tool"><a class="enabled" title="Добавить параметр в группу" href="<spring:url value='/secured/manager/catalog/category/param/add/{id}'><spring:param name="id" value='${paramGroupProperties.paramGroup.id}' /></spring:url>">&nbsp;&#8853;&nbsp;</a></span>
                                <c:choose>
                                    <c:when test="${not listStatus.first}">
                                        <span class="tool"><a class="enabled" title="Сдвинуть группу вверх" href="<spring:url value='/secured/manager/catalog/category/param/group/shift/up/{id}'><spring:param name='id' value='${paramGroupProperties.paramGroup.id}' /></spring:url>">&nbsp;&#8593;&nbsp;</a></span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="tool"><span class="disabled">&nbsp;&#8593;&nbsp;</span></span>
                                    </c:otherwise>
                                </c:choose>
                                <c:choose>
                                    <c:when test="${not listStatus.last}">
                                        <span class="tool"><a class="enabled" title="Сдвинуть группу вниз" href="<spring:url value='/secured/manager/catalog/category/param/group/shift/down/{id}'><spring:param name='id' value='${paramGroupProperties.paramGroup.id}' /></spring:url>">&nbsp;&#8595;&nbsp;</a></span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="tool"><span class="disabled">&nbsp;&#8595;&nbsp;</span></span>
                                    </c:otherwise>
                                </c:choose>
                                <c:choose>
                                    <c:when test="${paramGroupProperties.canDelete}">
                                        <span class="tool"><a class="enabled" title="Удалить группу параметров" href="<spring:url value='/secured/manager/catalog/category/param/group/delete/{id}'><spring:param name='id' value='${paramGroupProperties.paramGroup.id}' /></spring:url>">&nbsp;&#8855;&nbsp;</a></span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="tool"><span class="disabled">&nbsp;&#8855;&nbsp;</span></span>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                            <c:if test="${(fn:length(paramGroupProperties.paramList) > 0)}">
                                <div class="param-list">
                                    <ul>
                                        <c:forEach var="paramProperties" items="${paramGroupProperties.paramList}" varStatus="listSt">
                                            <li>
                                                <div class="param">
                                                    <div class="properties">
                                                        <%-- TODO ? title="Редактировать параметр" --%>
                                                        <span class="name"><a title="<c:out value="${paramProperties.param.description}" />" href="<spring:url value='/secured/manager/catalog/category/param/edit/{id}'><spring:param name="id" value='${paramProperties.param.id}' /></spring:url>"><c:out value="${paramProperties.param.name}"/></a></span>
                                                    </div>
                                                    <div class="tools">
                                                        <c:choose>
                                                            <c:when test="${(fn:length(paramGroupList) > 1)}">
                                                                <span class="tool"><a class="enabled" title="Переместить параметр в другую группу" href="<spring:url value='/secured/manager/catalog/category/param/edit/group/{id}'><spring:param name='id' value='${paramProperties.param.id}' /></spring:url>">&nbsp;&#8629;&nbsp;</a></span>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <span class="tool"><span class="disabled">&nbsp;&#8629;&nbsp;</span></span>
                                                            </c:otherwise>
                                                        </c:choose>
                                                        <c:choose>
                                                            <c:when test="${paramProperties.param.base}">
                                                                <span class="tool"><a class="enabled" title="Сделать параметр неосновным" href="<spring:url value='/secured/manager/catalog/category/param/switch/base/{id}'><spring:param name="id" value='${paramProperties.param.id}' /></spring:url>">&nbsp;&#174;&nbsp;</a></span>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <span class="tool"><a class="enabled" title="Сделать параметр основным" href="<spring:url value='/secured/manager/catalog/category/param/switch/base/{id}'><spring:param name="id" value='${paramProperties.param.id}' /></spring:url>">&nbsp;&#8709;&nbsp;</a></span>
                                                            </c:otherwise>
                                                        </c:choose>
                                                        <c:choose>
                                                            <c:when test="${not listSt.first}">
                                                                <span class="tool"><a class="enabled" title="Сдвинуть параметр вверх" href="<spring:url value='/secured/manager/catalog/category/param/shift/up/{id}'><spring:param name='id' value='${paramProperties.param.id}' /></spring:url>">&nbsp;&#8593;&nbsp;</a></span>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <span class="tool"><span class="disabled">&nbsp;&#8593;&nbsp;</span></span>
                                                            </c:otherwise>
                                                        </c:choose>
                                                        <c:choose>
                                                            <c:when test="${not listSt.last}">
                                                                <span class="tool"><a class="enabled" title="Сдвинуть параметр вниз" href="<spring:url value='/secured/manager/catalog/category/param/shift/down/{id}'><spring:param name='id' value='${paramProperties.param.id}' /></spring:url>">&nbsp;&#8595;&nbsp;</a></span>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <span class="tool"><span class="disabled">&nbsp;&#8595;&nbsp;</span></span>
                                                            </c:otherwise>
                                                        </c:choose>
                                                        <c:choose>
                                                            <c:when test="${paramProperties.canDelete}">
                                                                <span class="tool"><a class="enabled" title="Удалить параметр" href="<spring:url value='/secured/manager/catalog/category/param/delete/{id}'><spring:param name="id" value='${paramProperties.param.id}' /></spring:url>">&nbsp;&#8855;&nbsp;</a></span>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <span class="tool"><span class="disabled">&nbsp;&#8855;&nbsp;</span></span>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </div>
                                                </div>
                                            </li>
                                        </c:forEach>
                                    </ul>
                                </div>
                            </c:if>
                        </div>
                        <c:if test="${(fn:length(paramGroupProperties.paramList) > 0)}">
                            <div class="param-list-sep"></div>
                        </c:if>
                    </li>
                </c:forEach>
            </ul>
        </div>
        <div class="action-set">
            <p>
                <a class="action enabled" href="<spring:url value='/secured/manager/catalog/category/param/group/add/{id}'><spring:param name="id" value='${categoryProperties.item.id}' /></spring:url>">Добавить группу параметров</a>
            </p>
        </div>
    </c:if>
    <c:if test="${not categoryProperties.paramCategory}">
        <p>
               Параметры категории отсутствуют.
            Вы можете <a class="action enabled" href="<spring:url value='/secured/manager/catalog/category/param/group/add/{id}'>
                             <spring:param name="id" value='${categoryProperties.item.id}' />
                         </spring:url>">создать группу параметров</a> категории.
        </p>
        <ul>
            <li><a class="action enabled" href="<spring:url value='/secured/manager/catalog/structure' />">Структура каталога товаров</a></li>
        </ul>
    </c:if>
</div>
<div class="col2">
    <!-- Column 2 start -->
    <p>column2</p>
    <!-- Column 2 end -->
</div>