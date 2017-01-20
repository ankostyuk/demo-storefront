/**
 * nullpointer jquery.ui.customslider 0.1
 *
 * Расширяет jQuery UI Slider 1.8.2, иправляет некоторые ошибки и вводит дополнительные настройки.
 *
 * jQuery UI 1.8.2
 *
 * Copyright 2010, nullpointer
 * Dual licensed under the MIT or GPL Version 2 licenses.
 * http://jquery.org/license
 * 
 * required:
 * jquery-1.4.2.js
 * jquery-ui-1.8.2.js
 * 
 * @author ankostyuk
 * 
 */

;(function($) {
$.widget( "ui.customSlider", $.ui.slider, {
    options: $.extend(true, $.ui.slider.options, {
        // Базовый CSS-класс
        baseClass: "ui-customslider",
        // По умолчанию, "скидывает" theme-CSS-классы jQuery, оставляет только CSS-классы внутренних элементов $.ui.slider
        removeClass: "ui-slider ui-slider-horizontal ui-widget ui-widget-content ui-corner-all"
    }),
	_create: function() {
        $.ui.slider.prototype._create.apply(this, arguments);

        var self = this, o = this.options;

        self.element.addClass(o.baseClass).removeClass(o.removeClass).find("*").removeClass(o.removeClass);
        
		this.handles.each(function(i) {
			$(this).addClass(i % 2 ? "right" : "left");
		});
    },
    // Переопределено, изменения помечены @customSlider
	_mouseCapture: function( event ) {
		var o = this.options,
			position,
			normValue,
			//distance,  // @customSlider
			closestHandle,
			//self, // @customSlider
			index,
			allowed,
			offset,
			mouseOverHandle;

		if ( o.disabled ) {
			return false;
		}

		this.elementSize = {
			width: this.element.outerWidth(),
			height: this.element.outerHeight()
		};
		this.elementOffset = this.element.offset();

		position = {x: event.pageX, y: event.pageY};

// @customSlider
        index = 0;
		normValue = this._normValueFromMouse(position);
        var val0 = this.values(0);
        var val1 = this.values(1);
        if (normValue <= val0) {
            index = 0;
        } else
        if (normValue >= val1) {
            index = 1;
        } else
        if (normValue > val0 && normValue < val1) {
            index = (normValue - val0) < (val1 - normValue) ? 0 : 1;
        }
// @customSlider
//		normValue = this._normValueFromMouse( position );
//		distance = this._valueMax() - this._valueMin() + 1;
//		self = this;
//		this.handles.each(function( i ) {
//			var thisDistance = Math.abs( normValue - self.values(i) );
//			if ( distance > thisDistance ) {
//				distance = thisDistance;
//				closestHandle = $( this );
//				index = i;
//			}
//		});

// @customSlider
//		// workaround for bug #3736 (if both handles of a range are at 0,
//		// the first is always used as the one with least distance,
//		// and moving it is obviously prevented by preventing negative ranges)
//		if( o.range === true && this.values(1) === o.min) {
//			index += 1;
//			closestHandle = $( this.handles[index] ); // @customSlider
//		}

        closestHandle = $(this.handles[index]); // @customSlider

		allowed = this._start( event, index );
		if ( allowed === false ) {
			return false;
		}
		this._mouseSliding = true;

		this._handleIndex = index; // @customSlider

		closestHandle
			.addClass( "ui-state-active" )
			.focus();

		offset = closestHandle.offset();
		mouseOverHandle = !$(event.target).parents().andSelf().is(".ui-slider-handle");
		this._clickOffset = mouseOverHandle ? {left: 0, top: 0} : {
			left: event.pageX - offset.left - (index === 0 ? closestHandle.width() : 0),// ( closestHandle.width() / 2 ), //@customSlider
			top: event.pageY - offset.top -
				( closestHandle.height() / 2 ) -
				( parseInt( closestHandle.css("borderTopWidth"), 10 ) || 0 ) -
				( parseInt( closestHandle.css("borderBottomWidth"), 10 ) || 0) +
				( parseInt( closestHandle.css("marginTop"), 10 ) || 0)
		};

		normValue = this._normValueFromMouse( position );
		this._slide( event, index, normValue );
		this._animateOff = true;
		return true;
	}
});
//
/**
 * Возвращает свойства "ползунка" исходя из предпочтительных начальных уловий.
 * @param min минимальное значение
 * @param max максимальное значение
 * @param stepCount количество шагов по всему ходу "ползунка"
 * @param stepRadix основание шага
 * @return {
 *      min: Number,
 *      max: Number,
 *      step: Number,
 * }
 */
$.fn.customSlider.getRangeProperties = function(min, max, stepCount, stepRadix) {
    if (max - min <= 0) {
        if (min > 0) {
            max = 2*min;
        } else
        if (min < 0) {
            max = min;
            min = 2*min;
        } else {
            max = 1;
        }
    }

    var s = Math.abs(max - min)/stepCount;
    var n = Math.ceil((Math.log(s))/(Math.log(stepRadix)));
    var p = Math.pow(stepRadix, n);
    var step = s/p < 0.5 ? p/stepRadix : p;
    var smin = min < 0 ? Math.floor((min)/step)*step : Math.floor(min/step)*step;
    var smax = max < 0 ? Math.ceil(max/step)*step : Math.ceil((max)/step)*step;

    return { // parseFloat(Number.toFixed(5)) - исправление ошибки JavaScript при действиях с Float
        min: parseFloat(smin.toFixed(5)),
        max: parseFloat(smax.toFixed(5)),
        step: parseFloat(step.toFixed(5))
    };
};
//
}(jQuery));