<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="fn2" uri="http://www.nullpointer.ru/fn2/tags" %>

<div class="col1">
    <c:if test="${empty category}">
        <p>
            Выберите категорию для добавления модели. Модель можно добавить только в категорию с параметрами.
        </p>
    </c:if>
    <c:if test="${not empty category}">
        <div class="model-cat-path">
            <c:forEach var="item" items="${path}" varStatus="st">
                <span>
                    <c:if test="${item.type == 'SECTION'}">
                        <c:out value="${item.name}" />
                    </c:if>
                    <c:if test="${item.type == 'CATEGORY'}">
                        <a href="<spring:url value="/secured/manager/model/{id}">
                               <spring:param name="id" value="${item.id}" />
                           </spring:url>"><c:out value="${item.name}" /></a>
                    </c:if>
                </span>
                <c:if test="${not st.last}">&nbsp;/&nbsp;</c:if>
            </c:forEach>

            <c:if test="${queryResult.total > 0}">
                <a class="composite new" href="<spring:url value='/secured/manager/model/add/{id}'>
                       <spring:param name='id' value='${category.id}' />
                   </spring:url>"><span class="link">Добавить модель</span></a>
            </c:if>
        </div>

        <c:if test="${queryResult.total == 0}">
            <p>
                В этой категории еще нет моделей. Вы можете <a href="<spring:url value='/secured/manager/model/add/{id}'>
                                                                   <spring:param name='id' value='${category.id}' />
                                                               </spring:url>">добавить их прямо сейчас</a>.
            </p>
        </c:if>
        <c:if test="${queryResult.total > 0}">
            <p>
                Всего моделей — ${queryResult.total}, показаны с ${queryResult.firstNumber} по ${queryResult.lastNumber}
            </p>
            <div class="model-list">
                <c:forEach var="model" items="${queryResult.list}">
                    <div class="model-item">
                        <p class="toolbox">
                            <span>
                                <a href="<spring:url value="/secured/manager/model/copy/{id}">
                                       <spring:param name="id" value="${model.id}" />
                                   </spring:url>">Копировать</a>
                            </span>
                            <span>
                                <a href="<spring:url value="/secured/manager/model/delete/{id}">
                                       <spring:param name="id" value="${model.id}" />
                                   </spring:url>">Удалить</a>
                            </span>
                        </p>
                        <h2><a href="<spring:url value="/secured/manager/model/edit/{id}"><spring:param name="id" value="${model.id}" /></spring:url>"><c:out value="${model.name}" /></a></h2>
                        <c:if test="${not empty model.description}">
                            <p class="description"><c:out value="${model.description}" /></p>
                        </c:if>
                        <c:if test="${not empty model.paramDescription}">
                            <p class="param-description">Характеристики: ${fn2:htmlformula(model.paramDescription)}</p>
                        </c:if>
                        <p class="vendor-code">
                            Код производителя: <c:out value="${model.vendorCode}" />
                        </p>
                        <c:if test="${not empty model.keywords}">
                            <p class="keywords">
                                Ключевые слова: <c:out value="${model.keywords}" />
                            </p>
                        </c:if>
                        <c:if test="${not empty model.site}">
                            <p class="site">
                                Адрес на сайте производителя: <a href="<c:out value="${model.site}" />"><c:out value="${model.site}" /></a>
                            </p>
                        </c:if>
                    </div>
                </c:forEach>
            </div>
            <c:if test="${queryResult.total > queryResult.pageSize}">
                <tiles:insertTemplate template="/WEB-INF/jsp/template/common-pager.jsp">
                    <tiles:putAttribute name="queryResult" value="${queryResult}" />
                    <tiles:putAttribute name="pagerUrl">
                        <spring:url value="/secured/manager/model/${category.id}" />
                    </tiles:putAttribute>
                    <tiles:putAttribute name="pagerUrlHasParams" value="false" />
                </tiles:insertTemplate>
            </c:if>
        </c:if>
    </c:if>
</div>
<div class="col2">
    <tiles:insertDefinition name="common-tree">
        <tiles:putAttribute name="nodeList" value="${catalogTree}" />
        <tiles:putAttribute name="treeClass" value="manager-catalog-tree" />
    </tiles:insertDefinition>
</div>