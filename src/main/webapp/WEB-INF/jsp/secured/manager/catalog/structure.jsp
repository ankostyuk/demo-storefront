<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<div class="col1">
    <h2>Структура каталога товаров</h2>
    <c:if test="${not (fn:length(itemList) > 0)}">
        <p>
            Каталог пуст
        </p>
    </c:if>
    <c:if test="${(fn:length(itemList) > 0)}">
        <!-- TODO fucking IE -->
        <div class="catalog-tree">
            <c:set var="currentLevel" value="0" />
            <c:forEach var="itemProperties" items="${itemList}">
                <c:set var="canShiftUp" value="true" />
                <c:set var="canShiftDown" value="true" />
                <c:if test="${itemProperties.afterItemId == null}">
                    <c:set var="canShiftDown" value="false" />
                </c:if>
                <c:choose>
                    <c:when test="${itemProperties.level > currentLevel}">
                        <c:set var="canShiftUp" value="false" />
                        <ul><li>
                    </c:when>
                    <c:when test="${itemProperties.level < currentLevel}">
                        <c:forEach begin="${itemProperties.level}" end="${currentLevel - 1}">
                            </li></ul>
                        </c:forEach>
                        </li><li>
                    </c:when>
                    <c:otherwise>
                        </li><li>
                    </c:otherwise>
                </c:choose>
                <c:choose>
                    <c:when test="${itemProperties.item.type == 'SECTION'}">
                        <c:set var="cssItem" value="section" />
                    </c:when>
                    <c:when test="${itemProperties.item.type == 'CATEGORY'}">
                        <c:set var="cssItem" value="category" />
                    </c:when>
                    <c:otherwise>
                        <c:set var="cssItem" value="" />
                    </c:otherwise>
                </c:choose>
                <c:choose>
                    <c:when test="${itemProperties.item.active}">
                        <c:set var="cssActive" value="active" />
                        <c:set var="switchActiveTitle" value="Деактивировать" />
                    </c:when>
                    <c:otherwise>
                        <c:set var="cssActive" value="deactive" />
                        <c:set var="switchActiveTitle" value="Активировать" />
                    </c:otherwise>
                </c:choose>
                <div class="item ${cssItem}">
                    <div class="properties ${cssActive}">
                        <!-- TODO При наличии в категории товарных подкатегорий с параметрами, категория должна визуально выделяться -->   
                        <c:choose>
                            <c:when test="${itemProperties.item.type == 'SECTION'}">
                                <span class="info"><span>&#8226;&nbsp;</span></span>
                                <span class="name"><a class="${cssActive}" title="Редактировать раздел" href="<spring:url value='/secured/manager/catalog/structure/section/edit/{id}'><spring:param name='id' value='${itemProperties.item.id}' /></spring:url>"><c:out value="${itemProperties.item.name}" /></a></span>
                            </c:when>
                            <c:when test="${itemProperties.item.type == 'CATEGORY'}">
                                <c:if test="${itemProperties.paramCategory}">
                                    <span class="info"><span>&#167;</span></span>
                                </c:if>
                                <c:if test="${not itemProperties.paramCategory}">
                                    <span class="info"><span>&#164;</span></span>
                                </c:if>
                                <span class="name"><a class="${cssActive}" title="Редактировать категорию" href="<spring:url value='/secured/manager/catalog/structure/category/edit/{id}'><spring:param name='id' value='${itemProperties.item.id}' /></spring:url>"><c:out value="${itemProperties.item.name}" /></a></span>
                            </c:when>
                            <c:otherwise>
                                <span class="info"><span>&nbsp;&nbsp;</span></span>
                                <span class="name"><span class="${cssActive}"><c:out value="${itemProperties.item.name}" /></span></span>
                            </c:otherwise>
                        </c:choose>
                        <span class="info"><span>(<c:out value="${itemProperties.offerCount}" />)</span></span>
                    </div>
                    <div class="tools">
                        <c:choose>
                            <c:when test="${itemProperties.item.type == 'SECTION'}">
                                <span class="tool"><a class="enabled" title="Добавить подраздел" href="<spring:url value='/secured/manager/catalog/structure/section/add'><spring:param name='parentId' value='${itemProperties.item.id}' /></spring:url>">&nbsp;Р&nbsp;</a></span>
                                <span class="tool"><a class="enabled" title="Добавить категорию" href="<spring:url value='/secured/manager/catalog/structure/category/add'><spring:param name='parentId' value='${itemProperties.item.id}' /></spring:url>">&nbsp;К&nbsp;</a></span>
                                <span class="tool"><a class="enabled" title="Просмотреть раздел в каталоге" href="<spring:url value='/section/{id}'><spring:param name='id' value='${itemProperties.item.id}' /></spring:url>">&nbsp;&#8663;&nbsp;</a></span>
                            </c:when>
                            <c:when test="${itemProperties.item.type == 'CATEGORY'}">
                                <span class="tool"><a class="enabled" title="Просмотреть категорию в каталоге" href="<spring:url value='/category/{id}'><spring:param name='id' value='${itemProperties.item.id}' /></spring:url>">&nbsp;&#8663;&nbsp;</a></span>
                            </c:when>
                            <c:otherwise>
                                <span class="tool"><span class="disabled">&nbsp;&#8663;&nbsp;</span></span>
                            </c:otherwise>
                        </c:choose>
                        <c:choose>
                            <c:when test="${itemProperties.canActive && itemProperties.item.type == 'SECTION'}">
                                <span class="tool"><a class="enabled" title="${switchActiveTitle}" href="<spring:url value='/secured/manager/catalog/structure/section/switch/active/{id}'><spring:param name='id' value='${itemProperties.item.id}' /></spring:url>">&nbsp;*&nbsp;</a></span>
                            </c:when>
                            <c:when test="${itemProperties.canActive && itemProperties.item.type == 'CATEGORY'}">
                                <span class="tool"><a class="enabled" title="${switchActiveTitle}" href="<spring:url value='/secured/manager/catalog/structure/category/switch/active/{id}'><spring:param name='id' value='${itemProperties.item.id}' /></spring:url>">&nbsp;*&nbsp;</a></span>
                            </c:when>
                            <c:otherwise>
                                <span class="tool"><span class="disabled">&nbsp;*&nbsp;</span></span>
                            </c:otherwise>
                        </c:choose>
                        <c:choose>
                            <c:when test="${canShiftUp}">
                                <span class="tool"><a class="enabled" title="Вверх" href="<spring:url value='/secured/manager/catalog/structure/item/shift/up/{id}'><spring:param name='id' value='${itemProperties.item.id}' /></spring:url>">&nbsp;&#8593;&nbsp;</a></span>
                            </c:when>
                            <c:otherwise>
                                <span class="tool"><span class="disabled">&nbsp;&#8593;&nbsp;</span></span>
                            </c:otherwise>
                        </c:choose>
                        <c:choose>
                            <c:when test="${canShiftDown}">
                                <span class="tool"><a class="enabled" title="Вниз" href="<spring:url value='/secured/manager/catalog/structure/item/shift/down/{id}'><spring:param name='id' value='${itemProperties.item.id}' /></spring:url>">&nbsp;&#8595;&nbsp;</a></span>
                            </c:when>
                            <c:otherwise>
                                <span class="tool"><span class="disabled">&nbsp;&#8595;&nbsp;</span></span>
                            </c:otherwise>
                        </c:choose>
                        <c:choose>
                            <c:when test="${itemProperties.canDelete}">
                                <c:if test="${itemProperties.item.type == 'SECTION'}">
                                    <span class="tool"><a class="enabled" title="Удалить раздел" href="<spring:url value="/secured/manager/catalog/structure/section/delete/{id}"><spring:param name='id' value='${itemProperties.item.id}' /></spring:url>">&nbsp;&#8855;&nbsp;</a></span>
                                </c:if>
                                <c:if test="${itemProperties.item.type == 'CATEGORY'}">
                                    <span class="tool"><a class="enabled" title="Удалить категорию" href="<spring:url value="/secured/manager/catalog/structure/category/delete/{id}"><spring:param name='id' value='${itemProperties.item.id}' /></spring:url>">&nbsp;&#8855;&nbsp;</a></span>
                                </c:if>
                            </c:when>
                            <c:otherwise>
                                <span class="tool"><span class="disabled">&nbsp;&#8855;&nbsp;</span></span>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
                <c:set var="currentLevel" value ="${itemProperties.level}" />
            </c:forEach>
            <c:if test="${currentLevel > 0}">
                  <c:forEach begin="1" end="${currentLevel}">
                      </li></ul>
                  </c:forEach>
            </c:if>
            
            <%--<br>
            <br>
            <br>
            <ul>
                <li>Инструменты
                    <ul>
                        <li>Пилы</li>
                        <li>Электроинструменты
                            <ul>
                                <li>Дрели</li>
                                <li>Шлифовальные машины</li>
                            </ul>
                        </li>
                    </ul>
                </li>
                <li>Сантехника
                    <ul>
                        <li>Ванны</li>
                        <li>Смесители</li>
                        <li>Трубы
                            <ul>
                                <li>Водопроводные</li>
                                <li>Канализационные</li>
                            </ul>
                        </li>
                    </ul>
                </li>
                <li>Вентиляция</li>
                <li>Двери
                    <ul>
                        <li>Замки</li>
                        <li>Ручки</li>
                        <li>Межкомнатные двери</li>
                    </ul>
                </li>
                <li>Электрика
                    <ul>
                        <li>Провода, кабели</li>
                        <li>Измерительный инструмент
                            <ul>
                                <li>Высоковольтный
                                    <ul>
                                        <li>Амперметры</li>
                                    </ul>
                                </li>
                            </ul>
                        </li>
                    </ul>
                </li>
                <li>TEST</li>
            </ul>--%>
        </div>
    </c:if>
</div>
<div class="col2">
    <ul>
        <li>
            <a class="action enabled" href="<spring:url value="/secured/manager/catalog/structure/section/add" />">Добавить раздел</a>
        </li>
        <li>
            <a class="action <c:if test="${fn:length(itemList) > 0}">enabled</c:if><c:if test="${fn:length(itemList) < 1}">disabled</c:if>" href="<spring:url value="/secured/manager/catalog/structure/category/add" />">Добавить категорию</a>
        </li>
    </ul>
</div>