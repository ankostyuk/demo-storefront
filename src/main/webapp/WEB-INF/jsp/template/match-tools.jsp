<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>

<tiles:useAttribute name="_redirect" ignore="true" />

<c:set var="multiCart" value="${fn:length(userSession.cartList) > 1}"/>

<c:if test="${multiCart}">
    <ul id="dropdown-cart-item-list" class="v-list inside">
        <c:forEach var="cart" items="${userSession.cartList}">
            <li><a id="to-cart-${cart.id}" class="act pseudo to-cart" rel="nofollow" href="<spring:url value="/cart/item/add" htmlEscape="true">
                       <spring:param name="cartId" value="${cart.id}" />
                       <c:if test="${not empty _redirect}">
                           <spring:param name="redirect" value="${_redirect}" />
                       </c:if>
                   </spring:url>"><c:out value="${cart.name}" /></a>
            </li>
        </c:forEach>
    </ul>
</c:if>
<script type="text/javascript">
    // <![CDATA[
    jsHelper.document.ready(function(){

        <%-- Подсветка ссылок при наводе на изображение --%>
        $("a[class*='hover-src-']").each(function(){
            var src = $(this);
            var dst = "a[class*='hover-dst-" + src.attr("class").match(/hover-src-([^\s]+)/)[1] + "']";
            src.hover(
                function(){
                    $(dst).addClass("hover");
                },
                function(){
                    $(dst).removeClass("hover");
                }
            );
        });

        <%-- Подробности доставки --%>
        $("span.delivery-conditions-all").hide();
        $("span.delivery-conditions-short").show();
        $("span.delivery-conditions-act").addClass("js").click(function(){
            var act = $(this);
            act.toggleClass("show-all");
            if (act.hasClass("show-all")) {
                act.next().hide().next().show();
            } else {
                act.next().next().hide().prev().show();
            }
        });

    <c:if test="${not multiCart}">

        jsHelper.buildPseudoLink({
            pseudoLinkSelector: "a.cart-item-add",
            pseudoLinkClass: "pseudo",
            requestData: {inline: true},
            success: jsHelper.buildPseudoLinkSuccessCallback()
        });

    </c:if>

    <c:if test="${multiCart}">

        var pseudoLink = jsHelper.buildPseudoLink({
            pseudoLinkSelector: "a.cart-item-add",
            pseudoLinkClass: "pseudo",
            dropdownContentSelector: "#dropdown-cart-item-list"
        });
    
        $("#dropdown-cart-item-list a.to-cart").click(function(e){
            e.preventDefault();

            pseudoLink.hideDropdown();

            var cartLink = $(this);
            var link = pseudoLink.getLink();

            $.ajax({
                context: link[0],
                url: cartLink.attr("href"),
                data: {
                    inline: true,
                    id: link.attr("id").substr("cart-item-".length)
                },
                type: "POST",
                success: jsHelper.buildPseudoLinkSuccessCallback()
            });
        });

    </c:if>

        jsHelper.buildPseudoLink({
            pseudoLinkSelector: "a.comparison-item-add",
            pseudoLinkClass: "pseudo",
            requestData: {inline: true},
            success: jsHelper.buildPseudoLinkSuccessCallback({
                beforeNoError: function(html){
                    var href = html.attr("href");
                    var url = href.substr(0, href.indexOf("?") + 1);
                    $("a[href^='" + url + "']").attr("href", href);

                <c:if test="${not empty category}">
                    $("#comparison-list-box").load("<c:url value="/category/${category.id}/comparison/list" />", function(response, status, xhr){
                        if (status == "success") {
                            buildComparisonTools();
                        }
                    });
                </c:if>
                }
            })
        });

    <c:if test="${not empty category}">

        buildComparisonTools();

        function buildComparisonTools(){
            <%--
            // Сворачивание/разворачивание списка сравнения
            // TODO Реализовать сохранение состояния при работе фильтра, перезагрузке, редиректах, ...
            // Возможно, и само сворачивание/разворачивание надо включить только после реализации сохранения.
            --%>
            jsHelper.buildCollapsedContent({
                container: "#comparison-list-box div.comparison-list",
                collapsedClass: "collapsed",
                addClass: "js",
                click: "h3 span",
                collapsable: "div.content-box"
            });
            
            jsHelper.buildPseudoLink({
                pseudoLinkSelector: "#comparison-list-box a.comparison-clear",
                pseudoLinkClass: "pseudo",
                requestData: {inline: true},
                withConfirm: true,
                success: jsHelper.buildPseudoLinkSuccessCallback({
                    noError: function(){
                        window.location.reload(true); // TODO более красивое решение, без перезагрузки.
                    }
                })
            });
        }
        
    </c:if>

    });
    // ]]>
</script>
