/**
 * nullpointer jquery.listenerchange 0.1
 *
 * "Слушает" изменения значений элементов (пока элементов форм text, checkbox, radio) в группе
 *
 * jQuery 1.4.2
 *
 * Copyright 2010, nullpointer
 * Dual licensed under the MIT or GPL Version 2 licenses.
 * http://jquery.org/license
 * 
 * required:
 * jquery-1.4.2.js
 * 
 * @author ankostyuk
 * 
 */

// TODO расширить для других элементов
;(function($) {
    //
    $.listenerchange = function(listened, options) {
        //
        var opts = $.extend(true, {}, $.fn.listenerchange.defaults, options || {});

        var listenedList = [];

        listened.each(function() {
            var el = $(this);
            listenedList.push({
                el: el,
                value: getValue(el)
            });
            if (opts.focusChangeableElement) {
                if (el.is(":checkbox") || el.is(":radio")) {
                    el.change(function(){
                        $(this).focus();
                    });
                }
            }
        });

        var timerControlId = null;
        //
        startControl();
        //

        function startControl() {
            timerControlId = window.setInterval(function() {
                var changed = [];

                for (var i = 0; i < listenedList.length; i++) {
                    var obj = listenedList[i];
                    var val = getValue(obj.el);
                    if (!equalValue(obj.el, obj.value, val)) {
                        obj.value = val;
                        changed.push(obj.el);
                    }
                }

                if (changed.length > 0) {
                    if ($.isFunction(opts.onChange)) {
                        opts.onChange.call(this, changed);
                    }
                }
            }, opts.sensitivity);
        }

        function getValue(el) {
            if ($.isFunction(opts.getValue)) {
                var ret = opts.getValue.call(this, el);
                if (ret !== undefined) {
                    return ret;
                }
            }
            
            if (el.is(":text")) {
                return el.val();
            }
            if (el.is(":checkbox") || el.is(":radio")) {
                return el.attr("checked");
            }

            return null;
        }

        function equalValue(el, prev, current) {
            if ($.isFunction(opts.equalValue)) {
                var ret = opts.equalValue.call(this, el, prev, current);
                if (ret !== undefined) {
                    return ret;
                }
            }

            return prev === current;
        }

        // Object
        return {
            opts: opts
        }
    };
    //
    $.fn.listenerchange = function(options) {
        if ( options === undefined ) {
            return this.data($.fn.listenerchange.accessName);
        } else {
            var listenerchange = new $.listenerchange(this, options);
            return this.each(function() {
                $(this).data($.fn.listenerchange.accessName, listenerchange);
            });
        }
    };
    //
    $.fn.listenerchange.accessName = "listenerchange";
    //
    $.fn.listenerchange.defaults = {
        focusChangeableElement: true,
        getValue: function(el) {
        },
        equalValue: function(el, prev, current) {
        },
        onChange: function(changed) {
        },
        sensitivity: 500
    };
    //
})(jQuery);