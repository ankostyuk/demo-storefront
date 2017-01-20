<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn2" uri="http://www.nullpointer.ru/fn2/tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>

<div id="content-no-sidebar">
    <div class="catalog-item-path">
        <tiles:insertDefinition name="secured-company-catalog-item-path">
            <tiles:putAttribute name="_path" value="${path}" />
            <tiles:putAttribute name="_lastLink" value="${true}" />
        </tiles:insertDefinition>
    </div>

    <c:set var="newOffer" value="${empty offer.id}" />
    <div class="form-header-with-help">
        <div class="help-box h1">
            <a class="composite warn" target="_blank" href="<c:url value="/help/offer" />"><span class="link">Как правильно размещать товарные предложения</span></a>
            <div id="help-offer" class="inline-help" style="display: none;">
                <tiles:insertDefinition name="help-offer" />
            </div>
        </div>
        <h1>
            <c:if test="${newOffer}">Добавление предложения</c:if>
            <c:if test="${not newOffer}">Редактирование предложения</c:if>
        </h1>
    </div>

    <c:if test="${offer.status == 'REJECTED'}">
        <div class="offer-moderation-info">
            <p>Предложение отклонено <fmt:formatDate pattern="dd.MM.yyyy" value="${offer.moderationEndDate}" /> по следующим причинам:</p>
            <ul>
                <c:forEach var="reason" items="${offer.rejectionList}">
                    <li>
                        <c:if test="${reason == 'CATEGORY'}">
                            <a title="Переместить предложение в другую категорию" href="<spring:url value="/secured/company/offer/move/{id}" htmlEscape="true">
                                   <spring:param name="id" value="${offer.id}" />
                               </spring:url>">
                            </c:if>
                            <spring:message code="ui.offer.rejection.${reason}" />
                            <c:if test="${reason == 'CATEGORY'}"></a></c:if>
                    </li>
                </c:forEach>
            </ul>
        </div>
    </c:if>

    <p class="info">
        Поля отмеченные <span class="mandatory">*</span> обязательны для заполнения.
    </p>

    <form:form cssClass="offer" modelAttribute="offer" enctype="multipart/form-data">
        <spring:bind path="offer.*" >
            <c:if test="${status.error}">
                <p class="message"><span class="form-error">Некорректно заполнены некоторые поля ниже</span></p>
            </c:if>
        </spring:bind>
        <ul class="field-group">
            <li>
                <form:label path="name" cssClass="text">Наименование<span class="mandatory">*</span></form:label>
                <form:input cssClass="text" path="name" />
                <form:errors cssClass="error" path="name" />
                <p class="hint">Длина наименования не должна превышать 60 символов</p>
            </li>
            <li>
                <form:label path="price" cssClass="text">Цена<span class="mandatory">*</span></form:label>
                <form:input cssClass="text price" path="price" />
                <form:select cssClass="text currency" path="currency">
                    <c:forEach var="currency" items="${currencyList}">
                        <spring:message var="currencyName" code="currency.${currency.name}.short" />
                        <form:option value="${currency.value}" label="${currencyName}" />
                    </c:forEach>
                </form:select>
                <div class="ratio-box">
                    <form:label path="ratio">за</form:label>
                    <form:input cssClass="text ratio" path="ratio" />
                    <abbr title="<c:out value="${unit.name}" />">${fn2:htmlformula(unit.abbreviation)}</abbr>
                </div>
                <form:errors cssClass="error" path="price" />
                <form:errors cssClass="error" path="currency" />
                <form:errors cssClass="error" path="ratio" />
            </li>
            <li>
                <div class="actual-box">
                    <label class="checkbox actual">
                        <form:checkbox path="active" cssClass="checkbox"/>
                        Актуально до
                    </label>
                    <form:input cssClass="text actual-date datepicker" path="actualDate" />
                </div>
                <form:errors cssClass="error" path="actualDate" />
                <p class="hint">
                    Только актуальные предложения публикуются в каталоге
                </p>
            </li>
            <li>
                <label class="checkbox">
                    <form:checkbox path="available" cssClass="checkbox" />
                    Наличие
                </label>
                <p class="hint">
                    Укажите, есть ли товар в наличии
                </p>
            </li>
            <li>
                <label class="checkbox">
                    <form:checkbox path="delivery" cssClass="checkbox" />
                    Доставка
                </label>
                <p class="hint">
                    Укажите, осуществляется ли доставка
                </p>
            </li>
            <li>
                <form:label path="description" cssClass="text">Описание</form:label>
                <form:textarea rows="5" cols="20" cssClass="text" path="description" />
                <form:errors cssClass="error" path="description" />
                <p class="hint">Длина описания не должна превышать 240 символов</p>
            </li>
            <li>
                <tiles:insertDefinition name="secured-common-image-select">
                    <tiles:putAttribute name="paramName" value="offerImage" />
                    <tiles:putAttribute name="label" value="Изображение" />
                    <tiles:putAttribute name="reselect" value="${reselectOfferImage}" />
                    <tiles:putAttribute name="key" value="${offer.image}" />
                    <tiles:putAttribute name="container" value="offer_mini" />
                    <tiles:putAttribute name="overflow" value="true" />
                </tiles:insertDefinition>
            </li>

            <%-- <h3>Дополнительные параметры</h3> --%>
            <li>
                <form:label path="originCountry" cssClass="text">Страна производства</form:label>
                <form:select cssClass="text" path="originCountry" >
                    <form:option value="${null}" label="<неизвестно>" />
                    <form:options items="${countryList}" itemLabel="value" itemValue="name" />
                </form:select>
            </li>
            <li>
                <form:label path="brandName" cssClass="text">Бренд (производитель)</form:label>
                <form:input cssClass="text" path="brandName" />
                <form:hidden path="brandId" />
                <form:errors cssClass="error" path="brandName" />
            </li>
            <li>
                <form:label path="modelName" cssClass="text">Модель (код&nbsp;производителя)</form:label>
                <form:input cssClass="text" path="modelName" />
                <form:errors cssClass="error" path="modelName" />
            </li>
        </ul>

        <c:if test="${category.parametrized}">
            <div><form:hidden path="modelId" /></div>
            <div id="paramInput" class="param-box">
                <tiles:insertDefinition name="secured-common-param-input">
                    <tiles:putAttribute name="_catalogItem" value="${catalogItem}" />
                </tiles:insertDefinition>
            </div>
        </c:if>

        <div class="submit">
            <c:if test="${newOffer}">
                <input type="submit" value="Добавить предложение" />
            </c:if>
            <c:if test="${not newOffer}">
                <input type="submit" value="Сохранить предложение" />
            </c:if>
        </div>
    </form:form>
</div>

<script type="text/javascript">
    // <![CDATA[
    jsHelper.document.ready(function() {
        <%-- TODO оптимизировать (инициализировать объекты заранее, ...) --%>
                
        <%-- Подсказчик бренда --%>
        jsHelper.buildSuggest({
            selector: "input[type='text'][name='brandName']",
            suggestOptions: {
                request: {
                    url: "<c:url value="/brand/suggest" />"
                },
                data: {
                    searchField: "name"
                },
                onSelected: function(itemData) {
                    $("input[name='brandId']").val(itemData.id);

                    var modelSuggest = $("input[type='text'][name='modelName']").suggest();

                    if (modelSuggest) {
                        modelSuggest.setRequestParams({
                            categoryId: ${category.id},
                            brandId: itemData.id
                        });
                    }
                },
                onBlurWithChanged: function() {
                    $("input[name='brandId']").val("");

                    var modelSuggest = $("input[type='text'][name='modelName']").suggest();

                    if (modelSuggest) {
                        modelSuggest.setRequestParams({
                            categoryId: ${category.id}
                        });
                    }
                }
            }
        });

    <c:if test="${category.parametrized}">
        <%-- Подсказчик модели --%>
        jsHelper.buildSuggest({
            selector: "input[type='text'][name='modelName']",
            suggestOptions: {
                request: {
                    url: "<c:url value="/model/suggest" />",
                    params: {
                        categoryId: ${category.id}
                    }
                },
                data: {
                    searchField: "name"
                },
                onSelected: function(itemData) {
                    $("input[name='modelId']").val(itemData.id);
                    showParamInputByModel(itemData.id);
                },
                onBlurWithChanged: function() {
                    $("input[name='modelId']").val("");
                }
            }
        });

        <%-- Подгрузка параметров модели --%>
        initParamInput();

        function showParamInputByModel(modelId) {
            $("#paramInput").load("<c:url value="/param/model" />/" + modelId, function(response, status, xhr){
                if (status == "success") {
                    initParamInput();
                }
            });
        }

        function initParamInput() {
            $("#paramInput").find("input, select").change(function(){
                if ($("input[name='modelId']").val() !== "") {
                    $("input[name='modelId']").val("");
                    $("input[type='text'][name='modelName']").val("");
                }
            })
        }
    </c:if>

        <%-- Подсказчик страны производства --%>
        $("label[for='originCountry']").attr("for", "originCountryName");

        var originCountry = $("select[id='originCountry'] option:selected").val();
        var originCountryName = (originCountry === "" ? "" : $("select[id='originCountry'] option:selected").text());

        $("select[id='originCountry']").replaceWith(
            "<input id='originCountryName' name='originCountryName' value='" + originCountryName + "' class='text jquery-suggest-edit' type='text'>" +
            "<input id='originCountry' name='originCountry' value='" + originCountry + "' type='hidden'>"
        );

        jsHelper.buildSuggest({
            selector: "input[type='text'][name='originCountryName']",
            suggestOptions: {
                request: {
                    url: "<c:url value="/country/suggest" />"
                },
                data: {
                    searchField: "name"
                },
                onSelected: function(itemData) {
                    $("input[name='originCountry']").val(itemData.alpha2);
                    this.data("countryName", itemData.name);
                },
                onBlurWithChanged: function() {
                    if (this.val() === "") {
                        $("input[name='originCountry']").val("");
                    } else {
                        this.val(this.data("countryName"));
                    }
                }
            }
        });
        $("input[type='text'][name='originCountryName']").data("countryName", originCountryName);

        <%-- Всплывающий календарь --%>
        jsHelper.buildDatepicker({
            inputSelector: "input.datepicker"
        });

        <%-- Контроль доступности ввода даты --%>
        var actualDateInput = $("div.actual-box input[name='actualDate']");
        var activeInput = $("div.actual-box input[name='active']").change(function(){
            checkActualDateInput();
        });

        checkActualDateInput();

        function checkActualDateInput() {
            if (activeInput.attr("checked")) {
                actualDateInput.removeAttr("disabled");
                actualDateInput.removeClass("disabled");
            } else {
                actualDateInput.attr("disabled", "disabled");
                actualDateInput.addClass("disabled");
            }
        }
        
        <%-- Справка --%>
        $("div.inline-help a").addClass("composite external").attr("target", "_blank").wrapInner('<span class="link"/>');
        jsHelper.buildPseudoLink({
            pseudoLinkSelector: "div.help-box a.warn",
            pseudoLinkClass: "pseudo",
            copyLink: true,
            dropdownContentSelector: "#help-offer",
            dropdownClass: "help"
        });
    });
    // ]]>
</script>