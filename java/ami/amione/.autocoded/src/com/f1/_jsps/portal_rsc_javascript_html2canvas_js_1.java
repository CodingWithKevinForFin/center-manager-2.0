package com.f1._jsps;

import com.f1.http.*;
import com.f1.utils.*;
import com.f1.http.handler.AbstractHttpHandler;
import com.f1.http.HttpUtils;
import static com.f1.http.HttpUtils.*;

public class portal_rsc_javascript_html2canvas_js_1 extends AbstractHttpHandler{

	public portal_rsc_javascript_html2canvas_js_1() {
	}
  
	public boolean canHandle(HttpRequestResponse request){
	  return true;
	}

	public void handle(HttpRequestResponse request) throws java.io.IOException{
	  super.handle(request);
	  com.f1.utils.FastPrintStream out = request.getOutputStream();
	  HttpSession session = request.getSession(false);
	  HttpServer server = request.getHttpServer();
	  LocaleFormatter formatter = session == null ? server.getHttpSessionManager().getDefaultFormatter() : session.getFormatter();
          out.print(
            "/*\r\n"+
            "  html2canvas 0.4.1 <http://html2canvas.hertzen.com>\r\n"+
            "  Copyright (c) 2013 Niklas von Hertzen\r\n"+
            "\r\n"+
            "  Released under MIT License\r\n"+
            "*/\r\n"+
            "\r\n"+
            "(function(window, document, undefined){\r\n"+
            "\r\n"+
            "\"use strict\";\r\n"+
            "\r\n"+
            "var _html2canvas = {},\r\n"+
            "previousElement,\r\n"+
            "computedCSS,\r\n"+
            "html2canvas;\r\n"+
            "\r\n"+
            "_html2canvas.Util = {};\r\n"+
            "\r\n"+
            "_html2canvas.Util.log = function(a) {\r\n"+
            "  if (_html2canvas.logging && window.console && window.console.log) {\r\n"+
            "    window.console.log(a);\r\n"+
            "  }\r\n"+
            "};\r\n"+
            "\r\n"+
            "_html2canvas.Util.trimText = (function(isNative){\r\n"+
            "  return function(input) {\r\n"+
            "    return isNative ? isNative.apply(input) : ((input || '') + '').replace( /^\\s+|\\s+$/g , '' );\r\n"+
            "  };\r\n"+
            "})(String.prototype.trim);\r\n"+
            "\r\n"+
            "_html2canvas.Util.asFloat = function(v) {\r\n"+
            "  return parseFloat(v);\r\n"+
            "};\r\n"+
            "\r\n"+
            "(function() {\r\n"+
            "  // TODO: support all possible length values\r\n"+
            "  var TEXT_SHADOW_PROPERTY = /((rgba|rgb)\\([^\\)]+\\)(\\s-?\\d+px){0,})/g;\r\n"+
            "  var TEXT_SHADOW_VALUES = /(-?\\d+px)|(#.+)|(rgb\\(.+\\))|(rgba\\(.+\\))/g;\r\n"+
            "  _html2canvas.Util.parseTextShadows = function (value) {\r\n"+
            "    if (!value || value === 'none') {\r\n"+
            "      return [];\r\n"+
            "    }\r\n"+
            "\r\n"+
            "    // find multiple shadow declarations\r\n"+
            "    var shadows = value.match(TEXT_SHADOW_PROPERTY),\r\n"+
            "      results = [];\r\n"+
            "    for (var i = 0; shadows && (i < shadows.length); i++) {\r\n"+
            "      var s = shadows[i].match(TEXT_SHADOW_VALUES);\r\n"+
            "      results.push({\r\n"+
            "        color: s[0],\r\n"+
            "        offsetX: s[1] ? s[1].replace('px', '') : 0,\r\n"+
            "        offsetY: s[2] ? s[2].replace('px', '') : 0,\r\n"+
            "        blur: s[3] ? s[3].replace('px', '') : 0\r\n"+
            "      });\r\n"+
            "    }\r\n"+
            "    return results;\r\n"+
            "  };\r\n"+
            "})();\r\n"+
            "\r\n"+
            "\r\n"+
            "_html2canvas.Util.parseBackgroundImage = function (value) {\r\n"+
            "    var whitespace = ' \\r\\n\\t',\r\n"+
            "        method, definition, prefix, prefix_i, block, results = [],\r\n"+
            "        c, mode = 0, numParen = 0, quote, args;\r\n"+
            "\r\n"+
            "    var appendResult = function(){\r\n"+
            "        if(method) {\r\n"+
            "            if(definition.substr( 0, 1 ) === '\"') {\r\n"+
            "                definition = definition.substr( 1, definition.length - 2 );\r\n"+
            "            }\r\n"+
            "            if(definition) {\r\n"+
            "                args.push(definition);\r\n"+
            "            }\r\n"+
            "            if(method.substr( 0, 1 ) === '-' &&\r\n"+
            "                    (prefix_i = method.indexOf( '-', 1 ) + 1) > 0) {\r\n"+
            "                prefix = method.substr( 0, prefix_i);\r\n"+
            "                method = method.substr( prefix_i );\r\n"+
            "            }\r\n"+
            "            results.push({\r\n"+
            "                prefix: prefix,\r\n"+
            "                method: method.toLowerCase(),\r\n"+
            "                value: block,\r\n"+
            "                args: args\r\n"+
            "            });\r\n"+
            "        }\r\n"+
            "        args = []; //for some odd reason, setting .length = 0 didn't work in safari\r\n"+
            "        method =\r\n"+
            "            prefix =\r\n"+
            "            definition =\r\n"+
            "            block = '';\r\n"+
            "    };\r\n"+
            "\r\n"+
            "    appendResult();\r\n"+
            "    for(var i = 0, ii = value.length; i<ii; i++) {\r\n"+
            "        c = value[i];\r\n"+
            "        if(mode === 0 && whitespace.indexOf( c ) > -1){\r\n"+
            "            continue;\r\n"+
            "        }\r\n"+
            "        switch(c) {\r\n"+
            "            case '\"':\r\n"+
            "                if(!quote) {\r\n"+
            "                    quote = c;\r\n"+
            "                }\r\n"+
            "                else if(quote === c) {\r\n"+
            "                    quote = null;\r\n"+
            "                }\r\n"+
            "                break;\r\n"+
            "\r\n"+
            "            case '(':\r\n"+
            "                if(quote) { break; }\r\n"+
            "                else if(mode === 0) {\r\n"+
            "                    mode = 1;\r\n"+
            "                    block += c;\r\n"+
            "                    continue;\r\n"+
            "                } else {\r\n"+
            "                    numParen++;\r\n"+
            "                }\r\n"+
            "                break;\r\n"+
            "\r\n"+
            "            case ')':\r\n"+
            "                if(quote) { break; }\r\n"+
            "                else if(mode === 1) {\r\n"+
            "                    if(numParen === 0) {\r\n"+
            "                        mode = 0;\r\n"+
            "                        block += c;\r\n"+
            "                        appendResult();\r\n"+
            "                        continue;\r\n"+
            "                    } else {\r\n"+
            "                        numParen--;\r\n"+
            "                    }\r\n"+
            "                }\r\n"+
            "                break;\r\n"+
            "\r\n"+
            "            case ',':\r\n"+
            "                if(quote) { break; }\r\n"+
            "                else if(mode === 0) {\r\n"+
            "                    appendResult();\r\n"+
            "                    continue;\r\n"+
            "                }\r\n"+
            "                else if (mode === 1) {\r\n"+
            "                    if(numParen === 0 && !method.match(/^url$/i)) {\r\n"+
            "                        args.push(definition);\r\n"+
            "                        definition = '';\r\n"+
            "                        block += c;\r\n"+
            "                        continue;\r\n"+
            "                    }\r\n"+
            "                }\r\n"+
            "                break;\r\n"+
            "        }\r\n"+
            "\r\n"+
            "        block += c;\r\n"+
            "        if(mode === 0) { method += c; }\r\n"+
            "        else { definition += c; }\r\n"+
            "    }\r\n"+
            "    appendResult();\r\n"+
            "\r\n"+
            "    return results;\r\n"+
            "};\r\n"+
            "\r\n"+
            "_html2canvas.Util.Bounds = function (element) {\r\n"+
            "  var clientRect, bounds = {};\r\n"+
            "\r\n"+
            "  if (element.getBoundingClientRect){\r\n"+
            "    clientRect = element.getBoundingClientRect();\r\n"+
            "\r\n"+
            "    // TODO add scroll position to bounds, so no scrolling of window necessary\r\n"+
            "    bounds.top = clientRect.top;\r\n"+
            "    bounds.bottom = clientRect.bottom || (clientRect.top + clientRect.height);\r\n"+
            "    bounds.left = clientRect.left;\r\n"+
            "\r\n"+
            "    bounds.width = element.offsetWidth;\r\n"+
            "    bounds.height = element.offsetHeight;\r\n"+
            "  }\r\n"+
            "\r\n"+
            "  return bounds;\r\n"+
            "};\r\n"+
            "\r\n"+
            "// TODO ideally, we'd want everything to go through this function instead of Util.Bounds,\r\n"+
            "// but would require further work to calculate the correct positions for elements with offsetParents\r\n"+
            "_html2canvas.Util.OffsetBounds = function (element) {\r\n"+
            "  var parent = element.offsetParent ? _html2canvas.Util.OffsetBounds(element.offsetParent) : {top: 0, left: 0};\r\n"+
            "\r\n"+
            "  return {\r\n"+
            "    top: element.offsetTop + parent.top,\r\n"+
            "    bottom: element.offsetTop + element.offsetHeight + parent.top,\r\n"+
            "    left: element.offsetLeft + parent.left,\r\n"+
            "    width: element.offsetWidth,\r\n"+
            "    height: element.offsetHeight\r\n"+
            "  };\r\n"+
            "};\r\n"+
            "\r\n"+
            "function toPX(element, attribute, value ) {\r\n"+
            "    var rsLeft = element.runtimeStyle && element.runtimeStyle[attribute],\r\n"+
            "        left,\r\n"+
            "        style = element.style;\r\n"+
            "\r\n"+
            "    // Check if we are not dealing with pixels, (Opera has issues with this)\r\n"+
            "    // Ported from jQuery css.js\r\n"+
            "    // From the awesome hack by Dean Edwards\r\n"+
            "    // http://erik.eae.net/archives/2007/07/27/18.54.15/#comment-102291\r\n"+
            "\r\n"+
            "    // If we're not dealing with a regular pixel number\r\n"+
            "    // but a number that has a weird ending, we need to convert it to pixels\r\n"+
            "\r\n"+
            "    if ( !/^-?[0-9]+\\.?[0-9]*(?:px)?$/i.test( value ) && /^-?\\d/.test(value) ) {\r\n"+
            "        // Remember the original values\r\n"+
            "        left = style.left;\r\n"+
            "\r\n"+
            "        // Put in the new values to get a computed value out\r\n"+
            "        if (rsLeft) {\r\n"+
            "            element.runtimeStyle.left = element.currentStyle.left;\r\n"+
            "        }\r\n"+
            "        style.left = attribute === \"fontSize\" ? \"1em\" : (value || 0);\r\n"+
            "        value = style.pixelLeft + \"px\";\r\n"+
            "\r\n"+
            "        // Revert the changed values\r\n"+
            "        style.left = left;\r\n"+
            "        if (rsLeft) {\r\n"+
            "            element.runtimeStyle.left = rsLeft;\r\n"+
            "        }\r\n"+
            "    }\r\n"+
            "\r\n"+
            "    if (!/^(thin|medium|thick)$/i.test(value)) {\r\n"+
            "        return Math.round(parseFloat(value)) + \"px\";\r\n"+
            "    }\r\n"+
            "\r\n"+
            "    return value;\r\n"+
            "}\r\n"+
            "\r\n"+
            "function asInt(val) {\r\n"+
            "    return parseInt(val, 10);\r\n"+
            "}\r\n"+
            "\r\n"+
            "function parseBackgroundSizePosition(value, element, attribute, index) {\r\n"+
            "    value = (value || '').split(',');\r\n"+
            "    value = value[index || 0] || value[0] || 'auto';\r\n"+
            "    value = _html2canvas.Util.trimText(value).split(' ');\r\n"+
            "\r\n"+
            "    if(attribute === 'backgroundSize' && (!value[0] || value[0].match(/cover|contain|auto/))) {\r\n"+
            "        //these values will be handled in the parent function\r\n"+
            "    } else {\r\n"+
            "        value[0] = (value[0].indexOf( \"%\" ) === -1) ? toPX(element, attribute + \"X\", value[0]) : value[0];\r\n"+
            "        if(value[1] === undefined) {\r\n"+
            "            if(attribute === 'backgroundSize') {\r\n"+
            "                value[1] = 'auto';\r\n"+
            "                return value;\r\n"+
            "            } else {\r\n"+
            "                // IE 9 doesn't return double digit always\r\n"+
            "                value[1] = value[0];\r\n"+
            "            }\r\n"+
            "        }\r\n"+
            "        value[1] = (value[1].indexOf(\"%\") === -1) ? toPX(element, attribute + \"Y\", value[1]) : value[1];\r\n"+
            "    }\r\n"+
            "    return value;\r\n"+
            "}\r\n"+
            "\r\n"+
            "_html2canvas.Util.getCSS = function (element, attribute, index) {\r\n"+
            "    if (previousElement !== element) {\r\n"+
            "      computedCSS = document.defaultView.getComputedStyle(element, null);\r\n"+
            "    }\r\n"+
            "\r\n"+
            "    var value = computedCSS[attribute];\r\n"+
            "\r\n"+
            "    if (/^background(Size|Position)$/.test(attribute)) {\r\n"+
            "        return parseBackgroundSizePosition(value, element, attribute, index);\r\n"+
            "    } else if (/border(Top|Bottom)(Left|Right)Radius/.test(attribute)) {\r\n"+
            "      var arr = value.split(\" \");\r\n"+
            "      if (arr.length <= 1) {\r\n"+
            "          arr[1] = arr[0];\r\n"+
            "      }\r\n"+
            "      return arr.map(asInt);\r\n"+
            "    }\r\n"+
            "\r\n"+
            "  return value;\r\n"+
            "};\r\n"+
            "\r\n"+
            "_html2canvas.Util.resizeBounds = function( current_width, current_height, target_width, target_height, stretch_mode ){\r\n"+
            "  var target_ratio = target_width / target_height,\r\n"+
            "    current_ratio = current_width / current_height,\r\n"+
            "    output_width, output_height;\r\n"+
            "\r\n"+
            "  if(!stretch_mode || stretch_mode === 'auto') {\r\n"+
            "    output_width = target_width;\r\n"+
            "    output_height = target_height;\r\n"+
            "  } else if(target_ratio < current_ratio ^ stretch_mode === 'contain') {\r\n"+
            "    output_height = target_height;\r\n"+
            "    output_width = target_height * current_ratio;\r\n"+
            "  } else {\r\n"+
            "    output_width = target_width;\r\n"+
            "    output_height = target_width / current_ratio;\r\n"+
            "  }\r\n"+
            "\r\n"+
            "  return {\r\n"+
            "    width: output_width,\r\n"+
            "    height: output_height\r\n"+
            "  };\r\n"+
            "};\r\n"+
            "\r\n"+
            "function backgroundBoundsFactory( prop, el, bounds, image, imageIndex, backgroundSize ) {\r\n"+
            "    var bgposition =  _html2canvas.Util.getCSS( el, prop, imageIndex ) ,\r\n"+
            "    topPos,\r\n"+
            "    left,\r\n"+
            "    percentage,\r\n"+
            "    val;\r\n"+
            "\r\n"+
            "    if (bgposition.length === 1){\r\n"+
            "      val = bgposition[0];\r\n"+
            "\r\n"+
            "      bgposition = [];\r\n"+
            "\r\n"+
            "      bgposition[0] = val;\r\n"+
            "      bgposition[1] = val;\r\n"+
            "    }\r\n"+
            "\r\n"+
            "    if (bgposition[0].toString().indexOf(\"%\") !== -1){");
          out.print(
            "\r\n"+
            "      percentage = (parseFloat(bgposition[0])/100);\r\n"+
            "      left = bounds.width * percentage;\r\n"+
            "      if(prop !== 'backgroundSize') {\r\n"+
            "        left -= (backgroundSize || image).width*percentage;\r\n"+
            "      }\r\n"+
            "    } else {\r\n"+
            "      if(prop === 'backgroundSize') {\r\n"+
            "        if(bgposition[0] === 'auto') {\r\n"+
            "          left = image.width;\r\n"+
            "        } else {\r\n"+
            "          if (/contain|cover/.test(bgposition[0])) {\r\n"+
            "            var resized = _html2canvas.Util.resizeBounds(image.width, image.height, bounds.width, bounds.height, bgposition[0]);\r\n"+
            "            left = resized.width;\r\n"+
            "            topPos = resized.height;\r\n"+
            "          } else {\r\n"+
            "            left = parseInt(bgposition[0], 10);\r\n"+
            "          }\r\n"+
            "        }\r\n"+
            "      } else {\r\n"+
            "        left = parseInt( bgposition[0], 10);\r\n"+
            "      }\r\n"+
            "    }\r\n"+
            "\r\n"+
            "\r\n"+
            "    if(bgposition[1] === 'auto') {\r\n"+
            "      topPos = left / image.width * image.height;\r\n"+
            "    } else if (bgposition[1].toString().indexOf(\"%\") !== -1){\r\n"+
            "      percentage = (parseFloat(bgposition[1])/100);\r\n"+
            "      topPos =  bounds.height * percentage;\r\n"+
            "      if(prop !== 'backgroundSize') {\r\n"+
            "        topPos -= (backgroundSize || image).height * percentage;\r\n"+
            "      }\r\n"+
            "\r\n"+
            "    } else {\r\n"+
            "      topPos = parseInt(bgposition[1],10);\r\n"+
            "    }\r\n"+
            "\r\n"+
            "    return [left, topPos];\r\n"+
            "}\r\n"+
            "\r\n"+
            "_html2canvas.Util.BackgroundPosition = function( el, bounds, image, imageIndex, backgroundSize ) {\r\n"+
            "    var result = backgroundBoundsFactory( 'backgroundPosition', el, bounds, image, imageIndex, backgroundSize );\r\n"+
            "    return { left: result[0], top: result[1] };\r\n"+
            "};\r\n"+
            "\r\n"+
            "_html2canvas.Util.BackgroundSize = function( el, bounds, image, imageIndex ) {\r\n"+
            "    var result = backgroundBoundsFactory( 'backgroundSize', el, bounds, image, imageIndex );\r\n"+
            "    return { width: result[0], height: result[1] };\r\n"+
            "};\r\n"+
            "\r\n"+
            "_html2canvas.Util.Extend = function (options, defaults) {\r\n"+
            "  for (var key in options) {\r\n"+
            "    if (options.hasOwnProperty(key)) {\r\n"+
            "      defaults[key] = options[key];\r\n"+
            "    }\r\n"+
            "  }\r\n"+
            "  return defaults;\r\n"+
            "};\r\n"+
            "\r\n"+
            "\r\n"+
            "/*\r\n"+
            " * Derived from jQuery.contents()\r\n"+
            " * Copyright 2010, John Resig\r\n"+
            " * Dual licensed under the MIT or GPL Version 2 licenses.\r\n"+
            " * http://jquery.org/license\r\n"+
            " */\r\n"+
            "_html2canvas.Util.Children = function( elem ) {\r\n"+
            "  var children;\r\n"+
            "  try {\r\n"+
            "    children = (elem.nodeName && elem.nodeName.toUpperCase() === \"IFRAME\") ? elem.contentDocument || elem.contentWindow.document : (function(array) {\r\n"+
            "      var ret = [];\r\n"+
            "      if (array !== null) {\r\n"+
            "        (function(first, second ) {\r\n"+
            "          var i = first.length,\r\n"+
            "          j = 0;\r\n"+
            "\r\n"+
            "          if (typeof second.length === \"number\") {\r\n"+
            "            for (var l = second.length; j < l; j++) {\r\n"+
            "              first[i++] = second[j];\r\n"+
            "            }\r\n"+
            "          } else {\r\n"+
            "            while (second[j] !== undefined) {\r\n"+
            "              first[i++] = second[j++];\r\n"+
            "            }\r\n"+
            "          }\r\n"+
            "\r\n"+
            "          first.length = i;\r\n"+
            "\r\n"+
            "          return first;\r\n"+
            "        })(ret, array);\r\n"+
            "      }\r\n"+
            "      return ret;\r\n"+
            "    })(elem.childNodes);\r\n"+
            "\r\n"+
            "  } catch (ex) {\r\n"+
            "    _html2canvas.Util.log(\"html2canvas.Util.Children failed with exception: \" + ex.message);\r\n"+
            "    children = [];\r\n"+
            "  }\r\n"+
            "  return children;\r\n"+
            "};\r\n"+
            "\r\n"+
            "_html2canvas.Util.isTransparent = function(backgroundColor) {\r\n"+
            "  return (backgroundColor === \"transparent\" || backgroundColor === \"rgba(0, 0, 0, 0)\");\r\n"+
            "};\r\n"+
            "_html2canvas.Util.Font = (function () {\r\n"+
            "\r\n"+
            "  var fontData = {};\r\n"+
            "\r\n"+
            "  return function(font, fontSize, doc) {\r\n"+
            "    if (fontData[font + \"-\" + fontSize] !== undefined) {\r\n"+
            "      return fontData[font + \"-\" + fontSize];\r\n"+
            "    }\r\n"+
            "\r\n"+
            "    var container = doc.createElement('div'),\r\n"+
            "    img = doc.createElement('img'),\r\n"+
            "    span = doc.createElement('span'),\r\n"+
            "    sampleText = 'Hidden Text',\r\n"+
            "    baseline,\r\n"+
            "    middle,\r\n"+
            "    metricsObj;\r\n"+
            "\r\n"+
            "    container.style.visibility = \"hidden\";\r\n"+
            "    container.style.fontFamily = font;\r\n"+
            "    container.style.fontSize = fontSize;\r\n"+
            "    container.style.margin = 0;\r\n"+
            "    container.style.padding = 0;\r\n"+
            "\r\n"+
            "    doc.body.appendChild(container);\r\n"+
            "\r\n"+
            "    // http://probablyprogramming.com/2009/03/15/the-tiniest-gif-ever (handtinywhite.gif)\r\n"+
            "    img.src = \"data:image/gif;base64,R0lGODlhAQABAIABAP///wAAACwAAAAAAQABAAACAkQBADs=\";\r\n"+
            "    img.width = 1;\r\n"+
            "    img.height = 1;\r\n"+
            "\r\n"+
            "    img.style.margin = 0;\r\n"+
            "    img.style.padding = 0;\r\n"+
            "    img.style.verticalAlign = \"baseline\";\r\n"+
            "\r\n"+
            "    span.style.fontFamily = font;\r\n"+
            "    span.style.fontSize = fontSize;\r\n"+
            "    span.style.margin = 0;\r\n"+
            "    span.style.padding = 0;\r\n"+
            "\r\n"+
            "    span.appendChild(doc.createTextNode(sampleText));\r\n"+
            "    container.appendChild(span);\r\n"+
            "    container.appendChild(img);\r\n"+
            "    baseline = (img.offsetTop - span.offsetTop) + 1;\r\n"+
            "\r\n"+
            "    container.removeChild(span);\r\n"+
            "    container.appendChild(doc.createTextNode(sampleText));\r\n"+
            "\r\n"+
            "    container.style.lineHeight = \"normal\";\r\n"+
            "    img.style.verticalAlign = \"super\";\r\n"+
            "\r\n"+
            "    middle = (img.offsetTop-container.offsetTop) + 1;\r\n"+
            "    metricsObj = {\r\n"+
            "      baseline: baseline,\r\n"+
            "      lineWidth: 1,\r\n"+
            "      middle: middle\r\n"+
            "    };\r\n"+
            "\r\n"+
            "    fontData[font + \"-\" + fontSize] = metricsObj;\r\n"+
            "\r\n"+
            "    doc.body.removeChild(container);\r\n"+
            "\r\n"+
            "    return metricsObj;\r\n"+
            "  };\r\n"+
            "})();\r\n"+
            "\r\n"+
            "(function(){\r\n"+
            "  var Util = _html2canvas.Util,\r\n"+
            "    Generate = {};\r\n"+
            "\r\n"+
            "  _html2canvas.Generate = Generate;\r\n"+
            "\r\n"+
            "  var reGradients = [\r\n"+
            "  /^(-webkit-linear-gradient)\\(([a-z\\s]+)([\\w\\d\\.\\s,%\\(\\)]+)\\)$/,\r\n"+
            "  /^(-o-linear-gradient)\\(([a-z\\s]+)([\\w\\d\\.\\s,%\\(\\)]+)\\)$/,\r\n"+
            "  /^(-webkit-gradient)\\((linear|radial),\\s((?:\\d{1,3}%?)\\s(?:\\d{1,3}%?),\\s(?:\\d{1,3}%?)\\s(?:\\d{1,3}%?))([\\w\\d\\.\\s,%\\(\\)\\-]+)\\)$/,\r\n"+
            "  /^(-moz-linear-gradient)\\(((?:\\d{1,3}%?)\\s(?:\\d{1,3}%?))([\\w\\d\\.\\s,%\\(\\)]+)\\)$/,\r\n"+
            "  /^(-webkit-radial-gradient)\\(((?:\\d{1,3}%?)\\s(?:\\d{1,3}%?)),\\s(\\w+)\\s([a-z\\-]+)([\\w\\d\\.\\s,%\\(\\)]+)\\)$/,\r\n"+
            "  /^(-moz-radial-gradient)\\(((?:\\d{1,3}%?)\\s(?:\\d{1,3}%?)),\\s(\\w+)\\s?([a-z\\-]*)([\\w\\d\\.\\s,%\\(\\)]+)\\)$/,\r\n"+
            "  /^(-o-radial-gradient)\\(((?:\\d{1,3}%?)\\s(?:\\d{1,3}%?)),\\s(\\w+)\\s([a-z\\-]+)([\\w\\d\\.\\s,%\\(\\)]+)\\)$/\r\n"+
            "  ];\r\n"+
            "\r\n"+
            "  /*\r\n"+
            " * TODO: Add IE10 vendor prefix (-ms) support\r\n"+
            " * TODO: Add W3C gradient (linear-gradient) support\r\n"+
            " * TODO: Add old Webkit -webkit-gradient(radial, ...) support\r\n"+
            " * TODO: Maybe some RegExp optimizations are possible ;o)\r\n"+
            " */\r\n"+
            "  Generate.parseGradient = function(css, bounds) {\r\n"+
            "    var gradient, i, len = reGradients.length, m1, stop, m2, m2Len, step, m3, tl,tr,br,bl;\r\n"+
            "\r\n"+
            "    for(i = 0; i < len; i+=1){\r\n"+
            "      m1 = css.match(reGradients[i]);\r\n"+
            "      if(m1) {\r\n"+
            "        break;\r\n"+
            "      }\r\n"+
            "    }\r\n"+
            "\r\n"+
            "    if(m1) {\r\n"+
            "      switch(m1[1]) {\r\n"+
            "        case '-webkit-linear-gradient':\r\n"+
            "        case '-o-linear-gradient':\r\n"+
            "\r\n"+
            "          gradient = {\r\n"+
            "            type: 'linear',\r\n"+
            "            x0: null,\r\n"+
            "            y0: null,\r\n"+
            "            x1: null,\r\n"+
            "            y1: null,\r\n"+
            "            colorStops: []\r\n"+
            "          };\r\n"+
            "\r\n"+
            "          // get coordinates\r\n"+
            "          m2 = m1[2].match(/\\w+/g);\r\n"+
            "          if(m2){\r\n"+
            "            m2Len = m2.length;\r\n"+
            "            for(i = 0; i < m2Len; i+=1){\r\n"+
            "              switch(m2[i]) {\r\n"+
            "                case 'top':\r\n"+
            "                  gradient.y0 = 0;\r\n"+
            "                  gradient.y1 = bounds.height;\r\n"+
            "                  break;\r\n"+
            "\r\n"+
            "                case 'right':\r\n"+
            "                  gradient.x0 = bounds.width;\r\n"+
            "                  gradient.x1 = 0;\r\n"+
            "                  break;\r\n"+
            "\r\n"+
            "                case 'bottom':\r\n"+
            "                  gradient.y0 = bounds.height;\r\n"+
            "                  gradient.y1 = 0;\r\n"+
            "                  break;\r\n"+
            "\r\n"+
            "                case 'left':\r\n"+
            "                  gradient.x0 = 0;\r\n"+
            "                  gradient.x1 = bounds.width;\r\n"+
            "                  break;\r\n"+
            "              }\r\n"+
            "            }\r\n"+
            "          }\r\n"+
            "          if(gradient.x0 === null && gradient.x1 === null){ // center\r\n"+
            "            gradient.x0 = gradient.x1 = bounds.width / 2;\r\n"+
            "          }\r\n"+
            "          if(gradient.y0 === null && gradient.y1 === null){ // center\r\n"+
            "            gradient.y0 = gradient.y1 = bounds.height / 2;\r\n"+
            "          }\r\n"+
            "\r\n"+
            "          // get colors and stops\r\n"+
            "          m2 = m1[3].match(/((?:rgb|rgba)\\(\\d{1,3},\\s\\d{1,3},\\s\\d{1,3}(?:,\\s[0-9\\.]+)?\\)(?:\\s\\d{1,3}(?:%|px))?)+/g);\r\n"+
            "          if(m2){\r\n"+
            "            m2Len = m2.length;\r\n"+
            "            step = 1 / Math.max(m2Len - 1, 1);\r\n"+
            "            for(i = 0; i < m2Len; i+=1){\r\n"+
            "              m3 = m2[i].match(/((?:rgb|rgba)\\(\\d{1,3},\\s\\d{1,3},\\s\\d{1,3}(?:,\\s[0-9\\.]+)?\\))\\s*(\\d{1,3})?(%|px)?/);\r\n"+
            "              if(m3[2]){\r\n"+
            "                stop = parseFloat(m3[2]);\r\n"+
            "                if(m3[3] === '%'){\r\n"+
            "                  stop /= 100;\r\n"+
            "                } else { // px - stupid opera\r\n"+
            "                  stop /= bounds.width;\r\n"+
            "                }\r\n"+
            "              } else {\r\n"+
            "                stop = i * step;\r\n"+
            "              }\r\n"+
            "              gradient.colorStops.push({\r\n"+
            "                color: m3[1],\r\n"+
            "                stop: stop\r\n"+
            "              });\r\n"+
            "            }\r\n"+
            "          }\r\n"+
            "          break;\r\n"+
            "\r\n"+
            "        case '-webkit-gradient':\r\n"+
            "\r\n"+
            "          gradient = {\r\n"+
            "            type: m1[2] === 'radial' ? 'circle' : m1[2], // TODO: Add radial gradient support for older mozilla definitions\r\n"+
            "            x0: 0,\r\n"+
            "            y0: 0,\r\n"+
            "            x1: 0,\r\n"+
            "            y1: 0,\r\n"+
            "            colorStops: []\r\n"+
            "          };\r\n"+
            "\r\n"+
            "          // get coordinates\r\n"+
            "          m2 = m1[3].match(/(\\d{1,3})%?\\s(\\d{1,3})%?,\\s(\\d{1,3})%?\\s(\\d{1,3})%?/);\r\n"+
            "          if(m2){\r\n"+
            "            gradient.x0 = (m2[1] * bounds.width) / 100;\r\n"+
            "            gradient.y0 = (m2[2] * bounds.height) / 100;\r\n"+
            "            gradient.x1 = (m2[3] * bounds.width) / 100;\r\n"+
            "            gradient.y1 = (m2[4] * bounds.height) / 100;\r\n"+
            "          }\r\n"+
            "\r\n"+
            "          // get colors and stops\r\n"+
            "          m2 = m1[4].match(/((?:from|to|color-stop)\\((?:[0-9\\.]+,\\s)?(?:rgb|rgba)\\(\\d{1,3},\\s\\d{1,3},\\s\\d{1,3}(?:,\\s[0-9\\.]+)?\\)\\))+/g);\r\n"+
            "          if(m2){\r\n"+
            "            m2Len = m2.length;\r\n"+
            "            for(i = 0; i < m2Len; i+=1){\r\n"+
            "              m3 = m2[i].match(/(from|to|color-stop)\\(([0-9\\.]+)?(?:,\\s)?((?:rgb|rgba)\\(\\d{1,3},\\s\\d{1,3},\\s\\d{1,3}(?:,\\s[0-9\\.]+)?\\))\\)/);\r\n"+
            "              sto");
          out.print(
            "p = parseFloat(m3[2]);\r\n"+
            "              if(m3[1] === 'from') {\r\n"+
            "                stop = 0.0;\r\n"+
            "              }\r\n"+
            "              if(m3[1] === 'to') {\r\n"+
            "                stop = 1.0;\r\n"+
            "              }\r\n"+
            "              gradient.colorStops.push({\r\n"+
            "                color: m3[3],\r\n"+
            "                stop: stop\r\n"+
            "              });\r\n"+
            "            }\r\n"+
            "          }\r\n"+
            "          break;\r\n"+
            "\r\n"+
            "        case '-moz-linear-gradient':\r\n"+
            "\r\n"+
            "          gradient = {\r\n"+
            "            type: 'linear',\r\n"+
            "            x0: 0,\r\n"+
            "            y0: 0,\r\n"+
            "            x1: 0,\r\n"+
            "            y1: 0,\r\n"+
            "            colorStops: []\r\n"+
            "          };\r\n"+
            "\r\n"+
            "          // get coordinates\r\n"+
            "          m2 = m1[2].match(/(\\d{1,3})%?\\s(\\d{1,3})%?/);\r\n"+
            "\r\n"+
            "          // m2[1] == 0%   -> left\r\n"+
            "          // m2[1] == 50%  -> center\r\n"+
            "          // m2[1] == 100% -> right\r\n"+
            "\r\n"+
            "          // m2[2] == 0%   -> top\r\n"+
            "          // m2[2] == 50%  -> center\r\n"+
            "          // m2[2] == 100% -> bottom\r\n"+
            "\r\n"+
            "          if(m2){\r\n"+
            "            gradient.x0 = (m2[1] * bounds.width) / 100;\r\n"+
            "            gradient.y0 = (m2[2] * bounds.height) / 100;\r\n"+
            "            gradient.x1 = bounds.width - gradient.x0;\r\n"+
            "            gradient.y1 = bounds.height - gradient.y0;\r\n"+
            "          }\r\n"+
            "\r\n"+
            "          // get colors and stops\r\n"+
            "          m2 = m1[3].match(/((?:rgb|rgba)\\(\\d{1,3},\\s\\d{1,3},\\s\\d{1,3}(?:,\\s[0-9\\.]+)?\\)(?:\\s\\d{1,3}%)?)+/g);\r\n"+
            "          if(m2){\r\n"+
            "            m2Len = m2.length;\r\n"+
            "            step = 1 / Math.max(m2Len - 1, 1);\r\n"+
            "            for(i = 0; i < m2Len; i+=1){\r\n"+
            "              m3 = m2[i].match(/((?:rgb|rgba)\\(\\d{1,3},\\s\\d{1,3},\\s\\d{1,3}(?:,\\s[0-9\\.]+)?\\))\\s*(\\d{1,3})?(%)?/);\r\n"+
            "              if(m3[2]){\r\n"+
            "                stop = parseFloat(m3[2]);\r\n"+
            "                if(m3[3]){ // percentage\r\n"+
            "                  stop /= 100;\r\n"+
            "                }\r\n"+
            "              } else {\r\n"+
            "                stop = i * step;\r\n"+
            "              }\r\n"+
            "              gradient.colorStops.push({\r\n"+
            "                color: m3[1],\r\n"+
            "                stop: stop\r\n"+
            "              });\r\n"+
            "            }\r\n"+
            "          }\r\n"+
            "          break;\r\n"+
            "\r\n"+
            "        case '-webkit-radial-gradient':\r\n"+
            "        case '-moz-radial-gradient':\r\n"+
            "        case '-o-radial-gradient':\r\n"+
            "\r\n"+
            "          gradient = {\r\n"+
            "            type: 'circle',\r\n"+
            "            x0: 0,\r\n"+
            "            y0: 0,\r\n"+
            "            x1: bounds.width,\r\n"+
            "            y1: bounds.height,\r\n"+
            "            cx: 0,\r\n"+
            "            cy: 0,\r\n"+
            "            rx: 0,\r\n"+
            "            ry: 0,\r\n"+
            "            colorStops: []\r\n"+
            "          };\r\n"+
            "\r\n"+
            "          // center\r\n"+
            "          m2 = m1[2].match(/(\\d{1,3})%?\\s(\\d{1,3})%?/);\r\n"+
            "          if(m2){\r\n"+
            "            gradient.cx = (m2[1] * bounds.width) / 100;\r\n"+
            "            gradient.cy = (m2[2] * bounds.height) / 100;\r\n"+
            "          }\r\n"+
            "\r\n"+
            "          // size\r\n"+
            "          m2 = m1[3].match(/\\w+/);\r\n"+
            "          m3 = m1[4].match(/[a-z\\-]*/);\r\n"+
            "          if(m2 && m3){\r\n"+
            "            switch(m3[0]){\r\n"+
            "              case 'farthest-corner':\r\n"+
            "              case 'cover': // is equivalent to farthest-corner\r\n"+
            "              case '': // mozilla removes \"cover\" from definition :(\r\n"+
            "                tl = Math.sqrt(Math.pow(gradient.cx, 2) + Math.pow(gradient.cy, 2));\r\n"+
            "                tr = Math.sqrt(Math.pow(gradient.cx, 2) + Math.pow(gradient.y1 - gradient.cy, 2));\r\n"+
            "                br = Math.sqrt(Math.pow(gradient.x1 - gradient.cx, 2) + Math.pow(gradient.y1 - gradient.cy, 2));\r\n"+
            "                bl = Math.sqrt(Math.pow(gradient.x1 - gradient.cx, 2) + Math.pow(gradient.cy, 2));\r\n"+
            "                gradient.rx = gradient.ry = Math.max(tl, tr, br, bl);\r\n"+
            "                break;\r\n"+
            "              case 'closest-corner':\r\n"+
            "                tl = Math.sqrt(Math.pow(gradient.cx, 2) + Math.pow(gradient.cy, 2));\r\n"+
            "                tr = Math.sqrt(Math.pow(gradient.cx, 2) + Math.pow(gradient.y1 - gradient.cy, 2));\r\n"+
            "                br = Math.sqrt(Math.pow(gradient.x1 - gradient.cx, 2) + Math.pow(gradient.y1 - gradient.cy, 2));\r\n"+
            "                bl = Math.sqrt(Math.pow(gradient.x1 - gradient.cx, 2) + Math.pow(gradient.cy, 2));\r\n"+
            "                gradient.rx = gradient.ry = Math.min(tl, tr, br, bl);\r\n"+
            "                break;\r\n"+
            "              case 'farthest-side':\r\n"+
            "                if(m2[0] === 'circle'){\r\n"+
            "                  gradient.rx = gradient.ry = Math.max(\r\n"+
            "                    gradient.cx,\r\n"+
            "                    gradient.cy,\r\n"+
            "                    gradient.x1 - gradient.cx,\r\n"+
            "                    gradient.y1 - gradient.cy\r\n"+
            "                    );\r\n"+
            "                } else { // ellipse\r\n"+
            "\r\n"+
            "                  gradient.type = m2[0];\r\n"+
            "\r\n"+
            "                  gradient.rx = Math.max(\r\n"+
            "                    gradient.cx,\r\n"+
            "                    gradient.x1 - gradient.cx\r\n"+
            "                    );\r\n"+
            "                  gradient.ry = Math.max(\r\n"+
            "                    gradient.cy,\r\n"+
            "                    gradient.y1 - gradient.cy\r\n"+
            "                    );\r\n"+
            "                }\r\n"+
            "                break;\r\n"+
            "              case 'closest-side':\r\n"+
            "              case 'contain': // is equivalent to closest-side\r\n"+
            "                if(m2[0] === 'circle'){\r\n"+
            "                  gradient.rx = gradient.ry = Math.min(\r\n"+
            "                    gradient.cx,\r\n"+
            "                    gradient.cy,\r\n"+
            "                    gradient.x1 - gradient.cx,\r\n"+
            "                    gradient.y1 - gradient.cy\r\n"+
            "                    );\r\n"+
            "                } else { // ellipse\r\n"+
            "\r\n"+
            "                  gradient.type = m2[0];\r\n"+
            "\r\n"+
            "                  gradient.rx = Math.min(\r\n"+
            "                    gradient.cx,\r\n"+
            "                    gradient.x1 - gradient.cx\r\n"+
            "                    );\r\n"+
            "                  gradient.ry = Math.min(\r\n"+
            "                    gradient.cy,\r\n"+
            "                    gradient.y1 - gradient.cy\r\n"+
            "                    );\r\n"+
            "                }\r\n"+
            "                break;\r\n"+
            "\r\n"+
            "            // TODO: add support for \"30px 40px\" sizes (webkit only)\r\n"+
            "            }\r\n"+
            "          }\r\n"+
            "\r\n"+
            "          // color stops\r\n"+
            "          m2 = m1[5].match(/((?:rgb|rgba)\\(\\d{1,3},\\s\\d{1,3},\\s\\d{1,3}(?:,\\s[0-9\\.]+)?\\)(?:\\s\\d{1,3}(?:%|px))?)+/g);\r\n"+
            "          if(m2){\r\n"+
            "            m2Len = m2.length;\r\n"+
            "            step = 1 / Math.max(m2Len - 1, 1);\r\n"+
            "            for(i = 0; i < m2Len; i+=1){\r\n"+
            "              m3 = m2[i].match(/((?:rgb|rgba)\\(\\d{1,3},\\s\\d{1,3},\\s\\d{1,3}(?:,\\s[0-9\\.]+)?\\))\\s*(\\d{1,3})?(%|px)?/);\r\n"+
            "              if(m3[2]){\r\n"+
            "                stop = parseFloat(m3[2]);\r\n"+
            "                if(m3[3] === '%'){\r\n"+
            "                  stop /= 100;\r\n"+
            "                } else { // px - stupid opera\r\n"+
            "                  stop /= bounds.width;\r\n"+
            "                }\r\n"+
            "              } else {\r\n"+
            "                stop = i * step;\r\n"+
            "              }\r\n"+
            "              gradient.colorStops.push({\r\n"+
            "                color: m3[1],\r\n"+
            "                stop: stop\r\n"+
            "              });\r\n"+
            "            }\r\n"+
            "          }\r\n"+
            "          break;\r\n"+
            "      }\r\n"+
            "    }\r\n"+
            "\r\n"+
            "    return gradient;\r\n"+
            "  };\r\n"+
            "\r\n"+
            "  function addScrollStops(grad) {\r\n"+
            "    return function(colorStop) {\r\n"+
            "      try {\r\n"+
            "        grad.addColorStop(colorStop.stop, colorStop.color);\r\n"+
            "      }\r\n"+
            "      catch(e) {\r\n"+
            "        Util.log(['failed to add color stop: ', e, '; tried to add: ', colorStop]);\r\n"+
            "      }\r\n"+
            "    };\r\n"+
            "  }\r\n"+
            "\r\n"+
            "  Generate.Gradient = function(src, bounds) {\r\n"+
            "    if(bounds.width === 0 || bounds.height === 0) {\r\n"+
            "      return;\r\n"+
            "    }\r\n"+
            "\r\n"+
            "    var canvas = document.createElement('canvas'),\r\n"+
            "    ctx = canvas.getContext('2d'),\r\n"+
            "    gradient, grad;\r\n"+
            "\r\n"+
            "    canvas.width = bounds.width;\r\n"+
            "    canvas.height = bounds.height;\r\n"+
            "\r\n"+
            "    // TODO: add support for multi defined background gradients\r\n"+
            "    gradient = _html2canvas.Generate.parseGradient(src, bounds);\r\n"+
            "\r\n"+
            "    if(gradient) {\r\n"+
            "      switch(gradient.type) {\r\n"+
            "        case 'linear':\r\n"+
            "          grad = ctx.createLinearGradient(gradient.x0, gradient.y0, gradient.x1, gradient.y1);\r\n"+
            "          gradient.colorStops.forEach(addScrollStops(grad));\r\n"+
            "          ctx.fillStyle = grad;\r\n"+
            "          ctx.fillRect(0, 0, bounds.width, bounds.height);\r\n"+
            "          break;\r\n"+
            "\r\n"+
            "        case 'circle':\r\n"+
            "          grad = ctx.createRadialGradient(gradient.cx, gradient.cy, 0, gradient.cx, gradient.cy, gradient.rx);\r\n"+
            "          gradient.colorStops.forEach(addScrollStops(grad));\r\n"+
            "          ctx.fillStyle = grad;\r\n"+
            "          ctx.fillRect(0, 0, bounds.width, bounds.height);\r\n"+
            "          break;\r\n"+
            "\r\n"+
            "        case 'ellipse':\r\n"+
            "          var canvasRadial = document.createElement('canvas'),\r\n"+
            "            ctxRadial = canvasRadial.getContext('2d'),\r\n"+
            "            ri = Math.max(gradient.rx, gradient.ry),\r\n"+
            "            di = ri * 2;\r\n"+
            "\r\n"+
            "          canvasRadial.width = canvasRadial.height = di;\r\n"+
            "\r\n"+
            "          grad = ctxRadial.createRadialGradient(gradient.rx, gradient.ry, 0, gradient.rx, gradient.ry, ri);\r\n"+
            "          gradient.colorStops.forEach(addScrollStops(grad));\r\n"+
            "\r\n"+
            "          ctxRadial.fillStyle = grad;\r\n"+
            "          ctxRadial.fillRect(0, 0, di, di);\r\n"+
            "\r\n"+
            "          ctx.fillStyle = gradient.colorStops[gradient.colorStops.length - 1].color;\r\n"+
            "          ctx.fillRect(0, 0, canvas.width, canvas.height);\r\n"+
            "          ctx.drawImage(canvasRadial, gradient.cx - gradient.rx, gradient.cy - gradient.ry, 2 * gradient.rx, 2 * gradient.ry);\r\n"+
            "          break;\r\n"+
            "      }\r\n"+
            "    }\r\n"+
            "\r\n"+
            "    return canvas;\r\n"+
            "  };\r\n"+
            "\r\n"+
            "  Generate.ListAlpha = function(number) {\r\n"+
            "    var tmp = \"\",\r\n"+
            "    modulus;\r\n"+
            "\r\n"+
            "    do {\r\n"+
            "      modulus = number % 26;\r\n"+
            "      tmp = String.fromCharCode((modulus) + 64) + tmp;\r\n"+
            "      number = number / 26;\r\n"+
            "    }while((number*26) > 26);\r\n"+
            "\r\n"+
            "    return tmp;\r\n"+
            "  };\r\n"+
            "\r\n"+
            "  Generate.ListRoman = function(number) {\r\n"+
            "    var romanArray = [\"M\", \"CM\", \"D\", \"CD\", \"C\", \"XC\", \"L\", \"XL\", \"X\", \"IX\", \"V\", \"IV\", \"I\"],\r\n"+
            "    decimal = [1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1],\r\n"+
            "    roman = \"\",\r\n"+
            "    v,\r\n"+
            "    len = romanArray.length;\r\n"+
            "\r\n"+
            "    if (number <= 0 || number >= 4000) {\r\n"+
            "      return number;\r\n"+
            "    }\r\n"+
            "\r\n"+
            "    for (v=0; v < len; v+=1) {\r\n"+
            "      while (number >= decimal[v]) {\r\n"+
            "        number -= decimal[v];\r\n"+
            "        roman += romanArray[v];\r\n"+
            "      }\r\n"+
            "    }\r\n"+
            "\r\n"+
            "    return roman;\r\n"+
            "  };\r\n"+
            "})();\r\n"+
            "function h2cRenderContext(width, height) {\r\n"+
            "  var storage = [];\r\n"+
            "  return {\r\n"+
            "    storage: storage,\r\n"+
            "    width: width,\r\n"+
            "    height: height,");
          out.print(
            "\r\n"+
            "    clip: function() {\r\n"+
            "      storage.push({\r\n"+
            "        type: \"function\",\r\n"+
            "        name: \"clip\",\r\n"+
            "        'arguments': arguments\r\n"+
            "      });\r\n"+
            "    },\r\n"+
            "    translate: function() {\r\n"+
            "      storage.push({\r\n"+
            "        type: \"function\",\r\n"+
            "        name: \"translate\",\r\n"+
            "        'arguments': arguments\r\n"+
            "      });\r\n"+
            "    },\r\n"+
            "    fill: function() {\r\n"+
            "      storage.push({\r\n"+
            "        type: \"function\",\r\n"+
            "        name: \"fill\",\r\n"+
            "        'arguments': arguments\r\n"+
            "      });\r\n"+
            "    },\r\n"+
            "    save: function() {\r\n"+
            "      storage.push({\r\n"+
            "        type: \"function\",\r\n"+
            "        name: \"save\",\r\n"+
            "        'arguments': arguments\r\n"+
            "      });\r\n"+
            "    },\r\n"+
            "    restore: function() {\r\n"+
            "      storage.push({\r\n"+
            "        type: \"function\",\r\n"+
            "        name: \"restore\",\r\n"+
            "        'arguments': arguments\r\n"+
            "      });\r\n"+
            "    },\r\n"+
            "    fillRect: function () {\r\n"+
            "      storage.push({\r\n"+
            "        type: \"function\",\r\n"+
            "        name: \"fillRect\",\r\n"+
            "        'arguments': arguments\r\n"+
            "      });\r\n"+
            "    },\r\n"+
            "    createPattern: function() {\r\n"+
            "      storage.push({\r\n"+
            "        type: \"function\",\r\n"+
            "        name: \"createPattern\",\r\n"+
            "        'arguments': arguments\r\n"+
            "      });\r\n"+
            "    },\r\n"+
            "    drawShape: function() {\r\n"+
            "\r\n"+
            "      var shape = [];\r\n"+
            "\r\n"+
            "      storage.push({\r\n"+
            "        type: \"function\",\r\n"+
            "        name: \"drawShape\",\r\n"+
            "        'arguments': shape\r\n"+
            "      });\r\n"+
            "\r\n"+
            "      return {\r\n"+
            "        moveTo: function() {\r\n"+
            "          shape.push({\r\n"+
            "            name: \"moveTo\",\r\n"+
            "            'arguments': arguments\r\n"+
            "          });\r\n"+
            "        },\r\n"+
            "        lineTo: function() {\r\n"+
            "          shape.push({\r\n"+
            "            name: \"lineTo\",\r\n"+
            "            'arguments': arguments\r\n"+
            "          });\r\n"+
            "        },\r\n"+
            "        arcTo: function() {\r\n"+
            "          shape.push({\r\n"+
            "            name: \"arcTo\",\r\n"+
            "            'arguments': arguments\r\n"+
            "          });\r\n"+
            "        },\r\n"+
            "        bezierCurveTo: function() {\r\n"+
            "          shape.push({\r\n"+
            "            name: \"bezierCurveTo\",\r\n"+
            "            'arguments': arguments\r\n"+
            "          });\r\n"+
            "        },\r\n"+
            "        quadraticCurveTo: function() {\r\n"+
            "          shape.push({\r\n"+
            "            name: \"quadraticCurveTo\",\r\n"+
            "            'arguments': arguments\r\n"+
            "          });\r\n"+
            "        }\r\n"+
            "      };\r\n"+
            "\r\n"+
            "    },\r\n"+
            "    drawImage: function () {\r\n"+
            "      storage.push({\r\n"+
            "        type: \"function\",\r\n"+
            "        name: \"drawImage\",\r\n"+
            "        'arguments': arguments\r\n"+
            "      });\r\n"+
            "    },\r\n"+
            "    fillText: function () {\r\n"+
            "      storage.push({\r\n"+
            "        type: \"function\",\r\n"+
            "        name: \"fillText\",\r\n"+
            "        'arguments': arguments\r\n"+
            "      });\r\n"+
            "    },\r\n"+
            "    setVariable: function (variable, value) {\r\n"+
            "      storage.push({\r\n"+
            "        type: \"variable\",\r\n"+
            "        name: variable,\r\n"+
            "        'arguments': value\r\n"+
            "      });\r\n"+
            "      return value;\r\n"+
            "    }\r\n"+
            "  };\r\n"+
            "}\r\n"+
            "_html2canvas.Parse = function (images, options) {\r\n"+
            "  window.scroll(0,0);\r\n"+
            "\r\n"+
            "  var element = (( options.elements === undefined ) ? document.body : options.elements[0]), // select body by default\r\n"+
            "  numDraws = 0,\r\n"+
            "  doc = element.ownerDocument,\r\n"+
            "  Util = _html2canvas.Util,\r\n"+
            "  support = Util.Support(options, doc),\r\n"+
            "  ignoreElementsRegExp = new RegExp(\"(\" + options.ignoreElements + \")\"),\r\n"+
            "  body = doc.body,\r\n"+
            "  getCSS = Util.getCSS,\r\n"+
            "  pseudoHide = \"___html2canvas___pseudoelement\",\r\n"+
            "  hidePseudoElements = doc.createElement('style');\r\n"+
            "\r\n"+
            "  hidePseudoElements.innerHTML = '.' + pseudoHide + '-before:before { content: \"\" !important; display: none !important; }' +\r\n"+
            "  '.' + pseudoHide + '-after:after { content: \"\" !important; display: none !important; }';\r\n"+
            "\r\n"+
            "  body.appendChild(hidePseudoElements);\r\n"+
            "\r\n"+
            "  images = images || {};\r\n"+
            "\r\n"+
            "  function documentWidth () {\r\n"+
            "    return Math.max(\r\n"+
            "      Math.max(doc.body.scrollWidth, doc.documentElement.scrollWidth),\r\n"+
            "      Math.max(doc.body.offsetWidth, doc.documentElement.offsetWidth),\r\n"+
            "      Math.max(doc.body.clientWidth, doc.documentElement.clientWidth)\r\n"+
            "      );\r\n"+
            "  }\r\n"+
            "\r\n"+
            "  function documentHeight () {\r\n"+
            "    return Math.max(\r\n"+
            "      Math.max(doc.body.scrollHeight, doc.documentElement.scrollHeight),\r\n"+
            "      Math.max(doc.body.offsetHeight, doc.documentElement.offsetHeight),\r\n"+
            "      Math.max(doc.body.clientHeight, doc.documentElement.clientHeight)\r\n"+
            "      );\r\n"+
            "  }\r\n"+
            "\r\n"+
            "  function getCSSInt(element, attribute) {\r\n"+
            "    var val = parseInt(getCSS(element, attribute), 10);\r\n"+
            "    return (isNaN(val)) ? 0 : val; // borders in old IE are throwing 'medium' for demo.html\r\n"+
            "  }\r\n"+
            "\r\n"+
            "  function renderRect (ctx, x, y, w, h, bgcolor) {\r\n"+
            "    if (bgcolor !== \"transparent\"){\r\n"+
            "      ctx.setVariable(\"fillStyle\", bgcolor);\r\n"+
            "      ctx.fillRect(x, y, w, h);\r\n"+
            "      numDraws+=1;\r\n"+
            "    }\r\n"+
            "  }\r\n"+
            "\r\n"+
            "  function capitalize(m, p1, p2) {\r\n"+
            "    if (m.length > 0) {\r\n"+
            "      return p1 + p2.toUpperCase();\r\n"+
            "    }\r\n"+
            "  }\r\n"+
            "\r\n"+
            "  function textTransform (text, transform) {\r\n"+
            "    switch(transform){\r\n"+
            "      case \"lowercase\":\r\n"+
            "        return text.toLowerCase();\r\n"+
            "      case \"capitalize\":\r\n"+
            "        return text.replace( /(^|\\s|:|-|\\(|\\))([a-z])/g, capitalize);\r\n"+
            "      case \"uppercase\":\r\n"+
            "        return text.toUpperCase();\r\n"+
            "      default:\r\n"+
            "        return text;\r\n"+
            "    }\r\n"+
            "  }\r\n"+
            "\r\n"+
            "  function noLetterSpacing(letter_spacing) {\r\n"+
            "    return (/^(normal|none|0px)$/.test(letter_spacing));\r\n"+
            "  }\r\n"+
            "\r\n"+
            "  function drawText(currentText, x, y, ctx){\r\n"+
            "    if (currentText !== null && Util.trimText(currentText).length > 0) {\r\n"+
            "      ctx.fillText(currentText, x, y);\r\n"+
            "      numDraws+=1;\r\n"+
            "    }\r\n"+
            "  }\r\n"+
            "\r\n"+
            "  function setTextVariables(ctx, el, text_decoration, color) {\r\n"+
            "    var align = false,\r\n"+
            "    bold = getCSS(el, \"fontWeight\"),\r\n"+
            "    family = getCSS(el, \"fontFamily\"),\r\n"+
            "    size = getCSS(el, \"fontSize\"),\r\n"+
            "    shadows = Util.parseTextShadows(getCSS(el, \"textShadow\"));\r\n"+
            "\r\n"+
            "    switch(parseInt(bold, 10)){\r\n"+
            "      case 401:\r\n"+
            "        bold = \"bold\";\r\n"+
            "        break;\r\n"+
            "      case 400:\r\n"+
            "        bold = \"normal\";\r\n"+
            "        break;\r\n"+
            "    }\r\n"+
            "\r\n"+
            "    ctx.setVariable(\"fillStyle\", color);\r\n"+
            "    ctx.setVariable(\"font\", [getCSS(el, \"fontStyle\"), getCSS(el, \"fontVariant\"), bold, size, family].join(\" \"));\r\n"+
            "    ctx.setVariable(\"textAlign\", (align) ? \"right\" : \"left\");\r\n"+
            "\r\n"+
            "    if (shadows.length) {\r\n"+
            "      // TODO: support multiple text shadows\r\n"+
            "      // apply the first text shadow\r\n"+
            "      ctx.setVariable(\"shadowColor\", shadows[0].color);\r\n"+
            "      ctx.setVariable(\"shadowOffsetX\", shadows[0].offsetX);\r\n"+
            "      ctx.setVariable(\"shadowOffsetY\", shadows[0].offsetY);\r\n"+
            "      ctx.setVariable(\"shadowBlur\", shadows[0].blur);\r\n"+
            "    }\r\n"+
            "\r\n"+
            "    if (text_decoration !== \"none\"){\r\n"+
            "      return Util.Font(family, size, doc);\r\n"+
            "    }\r\n"+
            "  }\r\n"+
            "\r\n"+
            "  function renderTextDecoration(ctx, text_decoration, bounds, metrics, color) {\r\n"+
            "    switch(text_decoration) {\r\n"+
            "      case \"underline\":\r\n"+
            "        // Draws a line at the baseline of the font\r\n"+
            "        // TODO As some browsers display the line as more than 1px if the font-size is big, need to take that into account both in position and size\r\n"+
            "        renderRect(ctx, bounds.left, Math.round(bounds.top + metrics.baseline + metrics.lineWidth), bounds.width, 1, color);\r\n"+
            "        break;\r\n"+
            "      case \"overline\":\r\n"+
            "        renderRect(ctx, bounds.left, Math.round(bounds.top), bounds.width, 1, color);\r\n"+
            "        break;\r\n"+
            "      case \"line-through\":\r\n"+
            "        // TODO try and find exact position for line-through\r\n"+
            "        renderRect(ctx, bounds.left, Math.ceil(bounds.top + metrics.middle + metrics.lineWidth), bounds.width, 1, color);\r\n"+
            "        break;\r\n"+
            "    }\r\n"+
            "  }\r\n"+
            "\r\n"+
            "  function getTextBounds(state, text, textDecoration, isLast, transform) {\r\n"+
            "    var bounds;\r\n"+
            "    if (support.rangeBounds && !transform) {\r\n"+
            "      if (textDecoration !== \"none\" || Util.trimText(text).length !== 0) {\r\n"+
            "        bounds = textRangeBounds(text, state.node, state.textOffset);\r\n"+
            "      }\r\n"+
            "      state.textOffset += text.length;\r\n"+
            "    } else if (state.node && typeof state.node.nodeValue === \"string\" ){\r\n"+
            "      var newTextNode = (isLast) ? state.node.splitText(text.length) : null;\r\n"+
            "      bounds = textWrapperBounds(state.node, transform);\r\n"+
            "      state.node = newTextNode;\r\n"+
            "    }\r\n"+
            "    return bounds;\r\n"+
            "  }\r\n"+
            "\r\n"+
            "  function textRangeBounds(text, textNode, textOffset) {\r\n"+
            "    var range = doc.createRange();\r\n"+
            "    range.setStart(textNode, textOffset);\r\n"+
            "    range.setEnd(textNode, textOffset + text.length);\r\n"+
            "    return range.getBoundingClientRect();\r\n"+
            "  }\r\n"+
            "\r\n"+
            "  function textWrapperBounds(oldTextNode, transform) {\r\n"+
            "    var parent = oldTextNode.parentNode,\r\n"+
            "    wrapElement = doc.createElement('wrapper'),\r\n"+
            "    backupText = oldTextNode.cloneNode(true);\r\n"+
            "\r\n"+
            "    wrapElement.appendChild(oldTextNode.cloneNode(true));\r\n"+
            "    parent.replaceChild(wrapElement, oldTextNode);\r\n"+
            "\r\n"+
            "    var bounds = transform ? Util.OffsetBounds(wrapElement) : Util.Bounds(wrapElement);\r\n"+
            "    parent.replaceChild(backupText, wrapElement);\r\n"+
            "    return bounds;\r\n"+
            "  }\r\n"+
            "\r\n"+
            "  function renderText(el, textNode, stack) {\r\n"+
            "    var ctx = stack.ctx,\r\n"+
            "    color = getCSS(el, \"color\"),\r\n"+
            "    textDecoration = getCSS(el, \"textDecoration\"),\r\n"+
            "    textAlign = getCSS(el, \"textAlign\"),\r\n"+
            "    metrics,\r\n"+
            "    textList,\r\n"+
            "    state = {\r\n"+
            "      node: textNode,\r\n"+
            "      textOffset: 0\r\n"+
            "    };\r\n"+
            "\r\n"+
            "    if (Util.trimText(textNode.nodeValue).length > 0) {\r\n"+
            "      textNode.nodeValue = textTransform(textNode.nodeValue, getCSS(el, \"textTransform\"));\r\n"+
            "      textAlign = textAlign.replace([\"-webkit-auto\"],[\"auto\"]);\r\n"+
            "\r\n"+
            "      textList = (!options.letterRendering && /^(left|right|justify|auto)$/.test(textAlign) && noLetterSpacing(getCSS(el, \"letterSpacing\"))) ?\r\n"+
            "      textNode.nodeValue.split(/(\\b| )/)\r\n"+
            "      : textNode.nodeValue.split(\"\");\r\n"+
            "\r\n"+
            "      metrics = setTextVariables(ctx, el, textDecoration, color);\r\n"+
            "\r\n"+
            "      if (options.chinese) {\r\n"+
            "        textList.forEach(function(word, index) {\r\n"+
            "          if (/.*[\\u4E00-\\u9FA5].*$/.test(word)) {\r\n"+
            "            word = word.split(\"\");\r\n"+
            "            word.unshift(index, 1);\r\n"+
            "            textList.splice.apply(textList, word);\r\n"+
            "          }\r\n"+
            "        });\r\n"+
            "      }\r\n"+
            "\r\n"+
            "      textList.forEach(function(text, index) {\r\n"+
            "        var bounds = getTextBounds(state, text, textDecoration, (index < textList.length - 1), stack.transform.matrix);\r\n"+
            "        ");
          out.print(
            "if (bounds) {\r\n"+
            "          drawText(text, bounds.left, bounds.bottom, ctx);\r\n"+
            "          renderTextDecoration(ctx, textDecoration, bounds, metrics, color);\r\n"+
            "        }\r\n"+
            "      });\r\n"+
            "    }\r\n"+
            "  }\r\n"+
            "\r\n"+
            "  function listPosition (element, val) {\r\n"+
            "    var boundElement = doc.createElement( \"boundelement\" ),\r\n"+
            "    originalType,\r\n"+
            "    bounds;\r\n"+
            "\r\n"+
            "    boundElement.style.display = \"inline\";\r\n"+
            "\r\n"+
            "    originalType = element.style.listStyleType;\r\n"+
            "    element.style.listStyleType = \"none\";\r\n"+
            "\r\n"+
            "    boundElement.appendChild(doc.createTextNode(val));\r\n"+
            "\r\n"+
            "    element.insertBefore(boundElement, element.firstChild);\r\n"+
            "\r\n"+
            "    bounds = Util.Bounds(boundElement);\r\n"+
            "    element.removeChild(boundElement);\r\n"+
            "    element.style.listStyleType = originalType;\r\n"+
            "    return bounds;\r\n"+
            "  }\r\n"+
            "\r\n"+
            "  function elementIndex(el) {\r\n"+
            "    var i = -1,\r\n"+
            "    count = 1,\r\n"+
            "    childs = el.parentNode.childNodes;\r\n"+
            "\r\n"+
            "    if (el.parentNode) {\r\n"+
            "      while(childs[++i] !== el) {\r\n"+
            "        if (childs[i].nodeType === 1) {\r\n"+
            "          count++;\r\n"+
            "        }\r\n"+
            "      }\r\n"+
            "      return count;\r\n"+
            "    } else {\r\n"+
            "      return -1;\r\n"+
            "    }\r\n"+
            "  }\r\n"+
            "\r\n"+
            "  function listItemText(element, type) {\r\n"+
            "    var currentIndex = elementIndex(element), text;\r\n"+
            "    switch(type){\r\n"+
            "      case \"decimal\":\r\n"+
            "        text = currentIndex;\r\n"+
            "        break;\r\n"+
            "      case \"decimal-leading-zero\":\r\n"+
            "        text = (currentIndex.toString().length === 1) ? currentIndex = \"0\" + currentIndex.toString() : currentIndex.toString();\r\n"+
            "        break;\r\n"+
            "      case \"upper-roman\":\r\n"+
            "        text = _html2canvas.Generate.ListRoman( currentIndex );\r\n"+
            "        break;\r\n"+
            "      case \"lower-roman\":\r\n"+
            "        text = _html2canvas.Generate.ListRoman( currentIndex ).toLowerCase();\r\n"+
            "        break;\r\n"+
            "      case \"lower-alpha\":\r\n"+
            "        text = _html2canvas.Generate.ListAlpha( currentIndex ).toLowerCase();\r\n"+
            "        break;\r\n"+
            "      case \"upper-alpha\":\r\n"+
            "        text = _html2canvas.Generate.ListAlpha( currentIndex );\r\n"+
            "        break;\r\n"+
            "    }\r\n"+
            "\r\n"+
            "    return text + \". \";\r\n"+
            "  }\r\n"+
            "\r\n"+
            "  function renderListItem(element, stack, elBounds) {\r\n"+
            "    var x,\r\n"+
            "    text,\r\n"+
            "    ctx = stack.ctx,\r\n"+
            "    type = getCSS(element, \"listStyleType\"),\r\n"+
            "    listBounds;\r\n"+
            "\r\n"+
            "    if (/^(decimal|decimal-leading-zero|upper-alpha|upper-latin|upper-roman|lower-alpha|lower-greek|lower-latin|lower-roman)$/i.test(type)) {\r\n"+
            "      text = listItemText(element, type);\r\n"+
            "      listBounds = listPosition(element, text);\r\n"+
            "      setTextVariables(ctx, element, \"none\", getCSS(element, \"color\"));\r\n"+
            "\r\n"+
            "      if (getCSS(element, \"listStylePosition\") === \"inside\") {\r\n"+
            "        ctx.setVariable(\"textAlign\", \"left\");\r\n"+
            "        x = elBounds.left;\r\n"+
            "      } else {\r\n"+
            "        return;\r\n"+
            "      }\r\n"+
            "\r\n"+
            "      drawText(text, x, listBounds.bottom, ctx);\r\n"+
            "    }\r\n"+
            "  }\r\n"+
            "\r\n"+
            "  function loadImage (src){\r\n"+
            "    var img = images[src];\r\n"+
            "    return (img && img.succeeded === true) ? img.img : false;\r\n"+
            "  }\r\n"+
            "\r\n"+
            "  function clipBounds(src, dst){\r\n"+
            "    var x = Math.max(src.left, dst.left),\r\n"+
            "    y = Math.max(src.top, dst.top),\r\n"+
            "    x2 = Math.min((src.left + src.width), (dst.left + dst.width)),\r\n"+
            "    y2 = Math.min((src.top + src.height), (dst.top + dst.height));\r\n"+
            "\r\n"+
            "    return {\r\n"+
            "      left:x,\r\n"+
            "      top:y,\r\n"+
            "      width:x2-x,\r\n"+
            "      height:y2-y\r\n"+
            "    };\r\n"+
            "  }\r\n"+
            "\r\n"+
            "  function setZ(element, stack, parentStack){\r\n"+
            "    var newContext,\r\n"+
            "    isPositioned = stack.cssPosition !== 'static',\r\n"+
            "    zIndex = isPositioned ? getCSS(element, 'zIndex') : 'auto',\r\n"+
            "    opacity = getCSS(element, 'opacity'),\r\n"+
            "    isFloated = getCSS(element, 'cssFloat') !== 'none';\r\n"+
            "\r\n"+
            "    // https://developer.mozilla.org/en-US/docs/Web/Guide/CSS/Understanding_z_index/The_stacking_context\r\n"+
            "    // When a new stacking context should be created:\r\n"+
            "    // the root element (HTML),\r\n"+
            "    // positioned (absolutely or relatively) with a z-index value other than \"auto\",\r\n"+
            "    // elements with an opacity value less than 1. (See the specification for opacity),\r\n"+
            "    // on mobile WebKit and Chrome 22+, position: fixed always creates a new stacking context, even when z-index is \"auto\" (See this post)\r\n"+
            "\r\n"+
            "    stack.zIndex = newContext = h2czContext(zIndex);\r\n"+
            "    newContext.isPositioned = isPositioned;\r\n"+
            "    newContext.isFloated = isFloated;\r\n"+
            "    newContext.opacity = opacity;\r\n"+
            "    newContext.ownStacking = (zIndex !== 'auto' || opacity < 1);\r\n"+
            "\r\n"+
            "    if (parentStack) {\r\n"+
            "      parentStack.zIndex.children.push(stack);\r\n"+
            "    }\r\n"+
            "  }\r\n"+
            "\r\n"+
            "  function renderImage(ctx, element, image, bounds, borders) {\r\n"+
            "\r\n"+
            "    var paddingLeft = getCSSInt(element, 'paddingLeft'),\r\n"+
            "    paddingTop = getCSSInt(element, 'paddingTop'),\r\n"+
            "    paddingRight = getCSSInt(element, 'paddingRight'),\r\n"+
            "    paddingBottom = getCSSInt(element, 'paddingBottom');\r\n"+
            "\r\n"+
            "    drawImage(\r\n"+
            "      ctx,\r\n"+
            "      image,\r\n"+
            "      0, //sx\r\n"+
            "      0, //sy\r\n"+
            "      image.width, //sw\r\n"+
            "      image.height, //sh\r\n"+
            "      bounds.left + paddingLeft + borders[3].width, //dx\r\n"+
            "      bounds.top + paddingTop + borders[0].width, // dy\r\n"+
            "      bounds.width - (borders[1].width + borders[3].width + paddingLeft + paddingRight), //dw\r\n"+
            "      bounds.height - (borders[0].width + borders[2].width + paddingTop + paddingBottom) //dh\r\n"+
            "      );\r\n"+
            "  }\r\n"+
            "\r\n"+
            "  function getBorderData(element) {\r\n"+
            "    return [\"Top\", \"Right\", \"Bottom\", \"Left\"].map(function(side) {\r\n"+
            "      return {\r\n"+
            "        width: getCSSInt(element, 'border' + side + 'Width'),\r\n"+
            "        color: getCSS(element, 'border' + side + 'Color')\r\n"+
            "      };\r\n"+
            "    });\r\n"+
            "  }\r\n"+
            "\r\n"+
            "  function getBorderRadiusData(element) {\r\n"+
            "    return [\"TopLeft\", \"TopRight\", \"BottomRight\", \"BottomLeft\"].map(function(side) {\r\n"+
            "      return getCSS(element, 'border' + side + 'Radius');\r\n"+
            "    });\r\n"+
            "  }\r\n"+
            "\r\n"+
            "  var getCurvePoints = (function(kappa) {\r\n"+
            "\r\n"+
            "    return function(x, y, r1, r2) {\r\n"+
            "      var ox = (r1) * kappa, // control point offset horizontal\r\n"+
            "      oy = (r2) * kappa, // control point offset vertical\r\n"+
            "      xm = x + r1, // x-middle\r\n"+
            "      ym = y + r2; // y-middle\r\n"+
            "      return {\r\n"+
            "        topLeft: bezierCurve({\r\n"+
            "          x:x,\r\n"+
            "          y:ym\r\n"+
            "        }, {\r\n"+
            "          x:x,\r\n"+
            "          y:ym - oy\r\n"+
            "        }, {\r\n"+
            "          x:xm - ox,\r\n"+
            "          y:y\r\n"+
            "        }, {\r\n"+
            "          x:xm,\r\n"+
            "          y:y\r\n"+
            "        }),\r\n"+
            "        topRight: bezierCurve({\r\n"+
            "          x:x,\r\n"+
            "          y:y\r\n"+
            "        }, {\r\n"+
            "          x:x + ox,\r\n"+
            "          y:y\r\n"+
            "        }, {\r\n"+
            "          x:xm,\r\n"+
            "          y:ym - oy\r\n"+
            "        }, {\r\n"+
            "          x:xm,\r\n"+
            "          y:ym\r\n"+
            "        }),\r\n"+
            "        bottomRight: bezierCurve({\r\n"+
            "          x:xm,\r\n"+
            "          y:y\r\n"+
            "        }, {\r\n"+
            "          x:xm,\r\n"+
            "          y:y + oy\r\n"+
            "        }, {\r\n"+
            "          x:x + ox,\r\n"+
            "          y:ym\r\n"+
            "        }, {\r\n"+
            "          x:x,\r\n"+
            "          y:ym\r\n"+
            "        }),\r\n"+
            "        bottomLeft: bezierCurve({\r\n"+
            "          x:xm,\r\n"+
            "          y:ym\r\n"+
            "        }, {\r\n"+
            "          x:xm - ox,\r\n"+
            "          y:ym\r\n"+
            "        }, {\r\n"+
            "          x:x,\r\n"+
            "          y:y + oy\r\n"+
            "        }, {\r\n"+
            "          x:x,\r\n"+
            "          y:y\r\n"+
            "        })\r\n"+
            "      };\r\n"+
            "    };\r\n"+
            "  })(4 * ((Math.sqrt(2) - 1) / 3));\r\n"+
            "\r\n"+
            "  function bezierCurve(start, startControl, endControl, end) {\r\n"+
            "\r\n"+
            "    var lerp = function (a, b, t) {\r\n"+
            "      return {\r\n"+
            "        x:a.x + (b.x - a.x) * t,\r\n"+
            "        y:a.y + (b.y - a.y) * t\r\n"+
            "      };\r\n"+
            "    };\r\n"+
            "\r\n"+
            "    return {\r\n"+
            "      start: start,\r\n"+
            "      startControl: startControl,\r\n"+
            "      endControl: endControl,\r\n"+
            "      end: end,\r\n"+
            "      subdivide: function(t) {\r\n"+
            "        var ab = lerp(start, startControl, t),\r\n"+
            "        bc = lerp(startControl, endControl, t),\r\n"+
            "        cd = lerp(endControl, end, t),\r\n"+
            "        abbc = lerp(ab, bc, t),\r\n"+
            "        bccd = lerp(bc, cd, t),\r\n"+
            "        dest = lerp(abbc, bccd, t);\r\n"+
            "        return [bezierCurve(start, ab, abbc, dest), bezierCurve(dest, bccd, cd, end)];\r\n"+
            "      },\r\n"+
            "      curveTo: function(borderArgs) {\r\n"+
            "        borderArgs.push([\"bezierCurve\", startControl.x, startControl.y, endControl.x, endControl.y, end.x, end.y]);\r\n"+
            "      },\r\n"+
            "      curveToReversed: function(borderArgs) {\r\n"+
            "        borderArgs.push([\"bezierCurve\", endControl.x, endControl.y, startControl.x, startControl.y, start.x, start.y]);\r\n"+
            "      }\r\n"+
            "    };\r\n"+
            "  }\r\n"+
            "\r\n"+
            "  function parseCorner(borderArgs, radius1, radius2, corner1, corner2, x, y) {\r\n"+
            "    if (radius1[0] > 0 || radius1[1] > 0) {\r\n"+
            "      borderArgs.push([\"line\", corner1[0].start.x, corner1[0].start.y]);\r\n"+
            "      corner1[0].curveTo(borderArgs);\r\n"+
            "      corner1[1].curveTo(borderArgs);\r\n"+
            "    } else {\r\n"+
            "      borderArgs.push([\"line\", x, y]);\r\n"+
            "    }\r\n"+
            "\r\n"+
            "    if (radius2[0] > 0 || radius2[1] > 0) {\r\n"+
            "      borderArgs.push([\"line\", corner2[0].start.x, corner2[0].start.y]);\r\n"+
            "    }\r\n"+
            "  }\r\n"+
            "\r\n"+
            "  function drawSide(borderData, radius1, radius2, outer1, inner1, outer2, inner2) {\r\n"+
            "    var borderArgs = [];\r\n"+
            "\r\n"+
            "    if (radius1[0] > 0 || radius1[1] > 0) {\r\n"+
            "      borderArgs.push([\"line\", outer1[1].start.x, outer1[1].start.y]);\r\n"+
            "      outer1[1].curveTo(borderArgs);\r\n"+
            "    } else {\r\n"+
            "      borderArgs.push([ \"line\", borderData.c1[0], borderData.c1[1]]);\r\n"+
            "    }\r\n"+
            "\r\n"+
            "    if (radius2[0] > 0 || radius2[1] > 0) {\r\n"+
            "      borderArgs.push([\"line\", outer2[0].start.x, outer2[0].start.y]);\r\n"+
            "      outer2[0].curveTo(borderArgs);\r\n"+
            "      borderArgs.push([\"line\", inner2[0].end.x, inner2[0].end.y]);\r\n"+
            "      inner2[0].curveToReversed(borderArgs);\r\n"+
            "    } else {\r\n"+
            "      borderArgs.push([ \"line\", borderData.c2[0], borderData.c2[1]]);\r\n"+
            "      borderArgs.push([ \"line\", borderData.c3[0], borderData.c3[1]]);\r\n"+
            "    }\r\n"+
            "\r\n"+
            "    if (radius1[0] > 0 || radius1[1] > 0) {\r\n"+
            "      borderArgs.push([\"line\", inner1[1].end.x, inner1[1].end.y]);\r\n"+
            "      inner1[1].curveToReversed(borderArgs);\r\n"+
            "    } else {\r\n"+
            "      borderArgs.push([ \"line\", borderData.c4[0], borderData.c4[1]]);\r\n"+
            "    }\r\n"+
            "\r\n"+
            "    return borderArgs;\r\n"+
            "  }\r\n"+
            "\r\n"+
            "  function calculateCurvePoints(bounds, borderRadius, borders) {\r\n"+
            "\r\n"+
            "    var x = bounds.left,\r\n"+
            "    y = bounds.top,\r\n"+
            "    width = bounds.width,\r\n"+
            "    height = bounds.height,\r\n"+
            "\r\n"+
            "    tlh = borderRadius[0][0],\r\n"+
            "    tlv = borderRadius[0][1],\r\n"+
            "    trh = borderRadius[1][0],\r\n"+
            "    trv = borderRadius[1][1],\r\n"+
            "    brh = borderRadius[2][0],\r\n"+
            "    brv = borderRadius[2][1],\r\n"+
            "   ");
          out.print(
            " blh = borderRadius[3][0],\r\n"+
            "    blv = borderRadius[3][1],\r\n"+
            "\r\n"+
            "    topWidth = width - trh,\r\n"+
            "    rightHeight = height - brv,\r\n"+
            "    bottomWidth = width - brh,\r\n"+
            "    leftHeight = height - blv;\r\n"+
            "\r\n"+
            "    return {\r\n"+
            "      topLeftOuter: getCurvePoints(\r\n"+
            "        x,\r\n"+
            "        y,\r\n"+
            "        tlh,\r\n"+
            "        tlv\r\n"+
            "        ).topLeft.subdivide(0.5),\r\n"+
            "\r\n"+
            "      topLeftInner: getCurvePoints(\r\n"+
            "        x + borders[3].width,\r\n"+
            "        y + borders[0].width,\r\n"+
            "        Math.max(0, tlh - borders[3].width),\r\n"+
            "        Math.max(0, tlv - borders[0].width)\r\n"+
            "        ).topLeft.subdivide(0.5),\r\n"+
            "\r\n"+
            "      topRightOuter: getCurvePoints(\r\n"+
            "        x + topWidth,\r\n"+
            "        y,\r\n"+
            "        trh,\r\n"+
            "        trv\r\n"+
            "        ).topRight.subdivide(0.5),\r\n"+
            "\r\n"+
            "      topRightInner: getCurvePoints(\r\n"+
            "        x + Math.min(topWidth, width + borders[3].width),\r\n"+
            "        y + borders[0].width,\r\n"+
            "        (topWidth > width + borders[3].width) ? 0 :trh - borders[3].width,\r\n"+
            "        trv - borders[0].width\r\n"+
            "        ).topRight.subdivide(0.5),\r\n"+
            "\r\n"+
            "      bottomRightOuter: getCurvePoints(\r\n"+
            "        x + bottomWidth,\r\n"+
            "        y + rightHeight,\r\n"+
            "        brh,\r\n"+
            "        brv\r\n"+
            "        ).bottomRight.subdivide(0.5),\r\n"+
            "\r\n"+
            "      bottomRightInner: getCurvePoints(\r\n"+
            "        x + Math.min(bottomWidth, width + borders[3].width),\r\n"+
            "        y + Math.min(rightHeight, height + borders[0].width),\r\n"+
            "        Math.max(0, brh - borders[1].width),\r\n"+
            "        Math.max(0, brv - borders[2].width)\r\n"+
            "        ).bottomRight.subdivide(0.5),\r\n"+
            "\r\n"+
            "      bottomLeftOuter: getCurvePoints(\r\n"+
            "        x,\r\n"+
            "        y + leftHeight,\r\n"+
            "        blh,\r\n"+
            "        blv\r\n"+
            "        ).bottomLeft.subdivide(0.5),\r\n"+
            "\r\n"+
            "      bottomLeftInner: getCurvePoints(\r\n"+
            "        x + borders[3].width,\r\n"+
            "        y + leftHeight,\r\n"+
            "        Math.max(0, blh - borders[3].width),\r\n"+
            "        Math.max(0, blv - borders[2].width)\r\n"+
            "        ).bottomLeft.subdivide(0.5)\r\n"+
            "    };\r\n"+
            "  }\r\n"+
            "\r\n"+
            "  function getBorderClip(element, borderPoints, borders, radius, bounds) {\r\n"+
            "    var backgroundClip = getCSS(element, 'backgroundClip'),\r\n"+
            "    borderArgs = [];\r\n"+
            "\r\n"+
            "    switch(backgroundClip) {\r\n"+
            "      case \"content-box\":\r\n"+
            "      case \"padding-box\":\r\n"+
            "        parseCorner(borderArgs, radius[0], radius[1], borderPoints.topLeftInner, borderPoints.topRightInner, bounds.left + borders[3].width, bounds.top + borders[0].width);\r\n"+
            "        parseCorner(borderArgs, radius[1], radius[2], borderPoints.topRightInner, borderPoints.bottomRightInner, bounds.left + bounds.width - borders[1].width, bounds.top + borders[0].width);\r\n"+
            "        parseCorner(borderArgs, radius[2], radius[3], borderPoints.bottomRightInner, borderPoints.bottomLeftInner, bounds.left + bounds.width - borders[1].width, bounds.top + bounds.height - borders[2].width);\r\n"+
            "        parseCorner(borderArgs, radius[3], radius[0], borderPoints.bottomLeftInner, borderPoints.topLeftInner, bounds.left + borders[3].width, bounds.top + bounds.height - borders[2].width);\r\n"+
            "        break;\r\n"+
            "\r\n"+
            "      default:\r\n"+
            "        parseCorner(borderArgs, radius[0], radius[1], borderPoints.topLeftOuter, borderPoints.topRightOuter, bounds.left, bounds.top);\r\n"+
            "        parseCorner(borderArgs, radius[1], radius[2], borderPoints.topRightOuter, borderPoints.bottomRightOuter, bounds.left + bounds.width, bounds.top);\r\n"+
            "        parseCorner(borderArgs, radius[2], radius[3], borderPoints.bottomRightOuter, borderPoints.bottomLeftOuter, bounds.left + bounds.width, bounds.top + bounds.height);\r\n"+
            "        parseCorner(borderArgs, radius[3], radius[0], borderPoints.bottomLeftOuter, borderPoints.topLeftOuter, bounds.left, bounds.top + bounds.height);\r\n"+
            "        break;\r\n"+
            "    }\r\n"+
            "\r\n"+
            "    return borderArgs;\r\n"+
            "  }\r\n"+
            "\r\n"+
            "  function parseBorders(element, bounds, borders){\r\n"+
            "    var x = bounds.left,\r\n"+
            "    y = bounds.top,\r\n"+
            "    width = bounds.width,\r\n"+
            "    height = bounds.height,\r\n"+
            "    borderSide,\r\n"+
            "    bx,\r\n"+
            "    by,\r\n"+
            "    bw,\r\n"+
            "    bh,\r\n"+
            "    borderArgs,\r\n"+
            "    // http://www.w3.org/TR/css3-background/#the-border-radius\r\n"+
            "    borderRadius = getBorderRadiusData(element),\r\n"+
            "    borderPoints = calculateCurvePoints(bounds, borderRadius, borders),\r\n"+
            "    borderData = {\r\n"+
            "      clip: getBorderClip(element, borderPoints, borders, borderRadius, bounds),\r\n"+
            "      borders: []\r\n"+
            "    };\r\n"+
            "\r\n"+
            "    for (borderSide = 0; borderSide < 4; borderSide++) {\r\n"+
            "\r\n"+
            "      if (borders[borderSide].width > 0) {\r\n"+
            "        bx = x;\r\n"+
            "        by = y;\r\n"+
            "        bw = width;\r\n"+
            "        bh = height - (borders[2].width);\r\n"+
            "\r\n"+
            "        switch(borderSide) {\r\n"+
            "          case 0:\r\n"+
            "            // top border\r\n"+
            "            bh = borders[0].width;\r\n"+
            "\r\n"+
            "            borderArgs = drawSide({\r\n"+
            "              c1: [bx, by],\r\n"+
            "              c2: [bx + bw, by],\r\n"+
            "              c3: [bx + bw - borders[1].width, by + bh],\r\n"+
            "              c4: [bx + borders[3].width, by + bh]\r\n"+
            "            }, borderRadius[0], borderRadius[1],\r\n"+
            "            borderPoints.topLeftOuter, borderPoints.topLeftInner, borderPoints.topRightOuter, borderPoints.topRightInner);\r\n"+
            "            break;\r\n"+
            "          case 1:\r\n"+
            "            // right border\r\n"+
            "            bx = x + width - (borders[1].width);\r\n"+
            "            bw = borders[1].width;\r\n"+
            "\r\n"+
            "            borderArgs = drawSide({\r\n"+
            "              c1: [bx + bw, by],\r\n"+
            "              c2: [bx + bw, by + bh + borders[2].width],\r\n"+
            "              c3: [bx, by + bh],\r\n"+
            "              c4: [bx, by + borders[0].width]\r\n"+
            "            }, borderRadius[1], borderRadius[2],\r\n"+
            "            borderPoints.topRightOuter, borderPoints.topRightInner, borderPoints.bottomRightOuter, borderPoints.bottomRightInner);\r\n"+
            "            break;\r\n"+
            "          case 2:\r\n"+
            "            // bottom border\r\n"+
            "            by = (by + height) - (borders[2].width);\r\n"+
            "            bh = borders[2].width;\r\n"+
            "\r\n"+
            "            borderArgs = drawSide({\r\n"+
            "              c1: [bx + bw, by + bh],\r\n"+
            "              c2: [bx, by + bh],\r\n"+
            "              c3: [bx + borders[3].width, by],\r\n"+
            "              c4: [bx + bw - borders[3].width, by]\r\n"+
            "            }, borderRadius[2], borderRadius[3],\r\n"+
            "            borderPoints.bottomRightOuter, borderPoints.bottomRightInner, borderPoints.bottomLeftOuter, borderPoints.bottomLeftInner);\r\n"+
            "            break;\r\n"+
            "          case 3:\r\n"+
            "            // left border\r\n"+
            "            bw = borders[3].width;\r\n"+
            "\r\n"+
            "            borderArgs = drawSide({\r\n"+
            "              c1: [bx, by + bh + borders[2].width],\r\n"+
            "              c2: [bx, by],\r\n"+
            "              c3: [bx + bw, by + borders[0].width],\r\n"+
            "              c4: [bx + bw, by + bh]\r\n"+
            "            }, borderRadius[3], borderRadius[0],\r\n"+
            "            borderPoints.bottomLeftOuter, borderPoints.bottomLeftInner, borderPoints.topLeftOuter, borderPoints.topLeftInner);\r\n"+
            "            break;\r\n"+
            "        }\r\n"+
            "\r\n"+
            "        borderData.borders.push({\r\n"+
            "          args: borderArgs,\r\n"+
            "          color: borders[borderSide].color\r\n"+
            "        });\r\n"+
            "\r\n"+
            "      }\r\n"+
            "    }\r\n"+
            "\r\n"+
            "    return borderData;\r\n"+
            "  }\r\n"+
            "\r\n"+
            "  function createShape(ctx, args) {\r\n"+
            "    var shape = ctx.drawShape();\r\n"+
            "    args.forEach(function(border, index) {\r\n"+
            "      shape[(index === 0) ? \"moveTo\" : border[0] + \"To\" ].apply(null, border.slice(1));\r\n"+
            "    });\r\n"+
            "    return shape;\r\n"+
            "  }\r\n"+
            "\r\n"+
            "  function renderBorders(ctx, borderArgs, color) {\r\n"+
            "    if (color !== \"transparent\") {\r\n"+
            "      ctx.setVariable( \"fillStyle\", color);\r\n"+
            "      createShape(ctx, borderArgs);\r\n"+
            "      ctx.fill();\r\n"+
            "      numDraws+=1;\r\n"+
            "    }\r\n"+
            "  }\r\n"+
            "\r\n"+
            "  function renderFormValue (el, bounds, stack){\r\n"+
            "\r\n"+
            "    var valueWrap = doc.createElement('valuewrap'),\r\n"+
            "    cssPropertyArray = ['lineHeight','textAlign','fontFamily','color','fontSize','paddingLeft','paddingTop','width','height','border','borderLeftWidth','borderTopWidth'],\r\n"+
            "    textValue,\r\n"+
            "    textNode;\r\n"+
            "\r\n"+
            "    cssPropertyArray.forEach(function(property) {\r\n"+
            "      try {\r\n"+
            "        valueWrap.style[property] = getCSS(el, property);\r\n"+
            "      } catch(e) {\r\n"+
            "        // Older IE has issues with \"border\"\r\n"+
            "        Util.log(\"html2canvas: Parse: Exception caught in renderFormValue: \" + e.message);\r\n"+
            "      }\r\n"+
            "    });\r\n"+
            "\r\n"+
            "    valueWrap.style.borderColor = \"black\";\r\n"+
            "    valueWrap.style.borderStyle = \"solid\";\r\n"+
            "    valueWrap.style.display = \"block\";\r\n"+
            "    valueWrap.style.position = \"absolute\";\r\n"+
            "\r\n"+
            "    if (/^(submit|reset|button|text|password)$/.test(el.type) || el.nodeName === \"SELECT\"){\r\n"+
            "      valueWrap.style.lineHeight = getCSS(el, \"height\");\r\n"+
            "    }\r\n"+
            "\r\n"+
            "    valueWrap.style.top = bounds.top + \"px\";\r\n"+
            "    valueWrap.style.left = bounds.left + \"px\";\r\n"+
            "\r\n"+
            "    textValue = (el.nodeName === \"SELECT\") ? (el.options[el.selectedIndex] || 0).text : el.value;\r\n"+
            "    if(!textValue) {\r\n"+
            "      textValue = el.placeholder;\r\n"+
            "    }\r\n"+
            "\r\n"+
            "    textNode = doc.createTextNode(textValue);\r\n"+
            "\r\n"+
            "    valueWrap.appendChild(textNode);\r\n"+
            "    body.appendChild(valueWrap);\r\n"+
            "\r\n"+
            "    renderText(el, textNode, stack);\r\n"+
            "    body.removeChild(valueWrap);\r\n"+
            "  }\r\n"+
            "\r\n"+
            "  function drawImage (ctx) {\r\n"+
            "    ctx.drawImage.apply(ctx, Array.prototype.slice.call(arguments, 1));\r\n"+
            "    numDraws+=1;\r\n"+
            "  }\r\n"+
            "\r\n"+
            "  function getPseudoElement(el, which) {\r\n"+
            "    var elStyle = window.getComputedStyle(el, which);\r\n"+
            "    if(!elStyle || !elStyle.content || elStyle.content === \"none\" || elStyle.content === \"-moz-alt-content\" || elStyle.display === \"none\") {\r\n"+
            "      return;\r\n"+
            "    }\r\n"+
            "    var content = elStyle.content + '',\r\n"+
            "    first = content.substr( 0, 1 );\r\n"+
            "    //strips quotes\r\n"+
            "    if(first === content.substr( content.length - 1 ) && first.match(/'|\"/)) {\r\n"+
            "      content = content.substr( 1, content.length - 2 );\r\n"+
            "    }\r\n"+
            "\r\n"+
            "    var isImage = content.substr( 0, 3 ) === 'url',\r\n"+
            "    elps = document.createElement( isImage ? 'img' : 'span' );\r\n"+
            "\r\n"+
            "    elps.className = pseudoHide + \"-before \" + pseudoHide + \"-after\";\r\n"+
            "\r\n"+
            "    Object.keys(elStyle).filter(indexedProperty).forEach(function(prop) {\r\n"+
            "      // Prevent assigning of read only CSS Rules, ex. length, parentRule\r\n"+
            "      try {\r\n"+
            "        elps.style[prop] = elStyle[prop];\r\n"+
            "      } catch (e) {\r\n"+
            "        Util.log(['Tried to assign readonly property ', prop, 'Error:', e]);\r\n"+
            "      }\r\n"+
            "    });\r\n"+
            "\r\n"+
            "    if(isImage) {\r\n"+
            "      elps.src = Util.parseBackgroundImage(content)[0].args[0];\r\n"+
            "    } else {\r\n"+
            "      elps.innerHTML = con");
          out.print(
            "tent;\r\n"+
            "    }\r\n"+
            "    return elps;\r\n"+
            "  }\r\n"+
            "\r\n"+
            "  function indexedProperty(property) {\r\n"+
            "    return (isNaN(window.parseInt(property, 10)));\r\n"+
            "  }\r\n"+
            "\r\n"+
            "  function injectPseudoElements(el, stack) {\r\n"+
            "    var before = getPseudoElement(el, ':before'),\r\n"+
            "    after = getPseudoElement(el, ':after');\r\n"+
            "    if(!before && !after) {\r\n"+
            "      return;\r\n"+
            "    }\r\n"+
            "\r\n"+
            "    if(before) {\r\n"+
            "      el.className += \" \" + pseudoHide + \"-before\";\r\n"+
            "      el.parentNode.insertBefore(before, el);\r\n"+
            "      parseElement(before, stack, true);\r\n"+
            "      el.parentNode.removeChild(before);\r\n"+
            "      el.className = el.className.replace(pseudoHide + \"-before\", \"\").trim();\r\n"+
            "    }\r\n"+
            "\r\n"+
            "    if (after) {\r\n"+
            "      el.className += \" \" + pseudoHide + \"-after\";\r\n"+
            "      el.appendChild(after);\r\n"+
            "      parseElement(after, stack, true);\r\n"+
            "      el.removeChild(after);\r\n"+
            "      el.className = el.className.replace(pseudoHide + \"-after\", \"\").trim();\r\n"+
            "    }\r\n"+
            "\r\n"+
            "  }\r\n"+
            "\r\n"+
            "  function renderBackgroundRepeat(ctx, image, backgroundPosition, bounds) {\r\n"+
            "    var offsetX = Math.round(bounds.left + backgroundPosition.left),\r\n"+
            "    offsetY = Math.round(bounds.top + backgroundPosition.top);\r\n"+
            "\r\n"+
            "    ctx.createPattern(image);\r\n"+
            "    ctx.translate(offsetX, offsetY);\r\n"+
            "    ctx.fill();\r\n"+
            "    ctx.translate(-offsetX, -offsetY);\r\n"+
            "  }\r\n"+
            "\r\n"+
            "  function backgroundRepeatShape(ctx, image, backgroundPosition, bounds, left, top, width, height) {\r\n"+
            "    var args = [];\r\n"+
            "    args.push([\"line\", Math.round(left), Math.round(top)]);\r\n"+
            "    args.push([\"line\", Math.round(left + width), Math.round(top)]);\r\n"+
            "    args.push([\"line\", Math.round(left + width), Math.round(height + top)]);\r\n"+
            "    args.push([\"line\", Math.round(left), Math.round(height + top)]);\r\n"+
            "    createShape(ctx, args);\r\n"+
            "    ctx.save();\r\n"+
            "    ctx.clip();\r\n"+
            "    renderBackgroundRepeat(ctx, image, backgroundPosition, bounds);\r\n"+
            "    ctx.restore();\r\n"+
            "  }\r\n"+
            "\r\n"+
            "  function renderBackgroundColor(ctx, backgroundBounds, bgcolor) {\r\n"+
            "    renderRect(\r\n"+
            "      ctx,\r\n"+
            "      backgroundBounds.left,\r\n"+
            "      backgroundBounds.top,\r\n"+
            "      backgroundBounds.width,\r\n"+
            "      backgroundBounds.height,\r\n"+
            "      bgcolor\r\n"+
            "      );\r\n"+
            "  }\r\n"+
            "\r\n"+
            "  function renderBackgroundRepeating(el, bounds, ctx, image, imageIndex) {\r\n"+
            "    var backgroundSize = Util.BackgroundSize(el, bounds, image, imageIndex),\r\n"+
            "    backgroundPosition = Util.BackgroundPosition(el, bounds, image, imageIndex, backgroundSize),\r\n"+
            "    backgroundRepeat = getCSS(el, \"backgroundRepeat\").split(\",\").map(Util.trimText);\r\n"+
            "\r\n"+
            "    image = resizeImage(image, backgroundSize);\r\n"+
            "\r\n"+
            "    backgroundRepeat = backgroundRepeat[imageIndex] || backgroundRepeat[0];\r\n"+
            "\r\n"+
            "    switch (backgroundRepeat) {\r\n"+
            "      case \"repeat-x\":\r\n"+
            "        backgroundRepeatShape(ctx, image, backgroundPosition, bounds,\r\n"+
            "          bounds.left, bounds.top + backgroundPosition.top, 99999, image.height);\r\n"+
            "        break;\r\n"+
            "\r\n"+
            "      case \"repeat-y\":\r\n"+
            "        backgroundRepeatShape(ctx, image, backgroundPosition, bounds,\r\n"+
            "          bounds.left + backgroundPosition.left, bounds.top, image.width, 99999);\r\n"+
            "        break;\r\n"+
            "\r\n"+
            "      case \"no-repeat\":\r\n"+
            "        backgroundRepeatShape(ctx, image, backgroundPosition, bounds,\r\n"+
            "          bounds.left + backgroundPosition.left, bounds.top + backgroundPosition.top, image.width, image.height);\r\n"+
            "        break;\r\n"+
            "\r\n"+
            "      default:\r\n"+
            "        renderBackgroundRepeat(ctx, image, backgroundPosition, {\r\n"+
            "          top: bounds.top,\r\n"+
            "          left: bounds.left,\r\n"+
            "          width: image.width,\r\n"+
            "          height: image.height\r\n"+
            "        });\r\n"+
            "        break;\r\n"+
            "    }\r\n"+
            "  }\r\n"+
            "\r\n"+
            "  function renderBackgroundImage(element, bounds, ctx) {\r\n"+
            "    var backgroundImage = getCSS(element, \"backgroundImage\"),\r\n"+
            "    backgroundImages = Util.parseBackgroundImage(backgroundImage),\r\n"+
            "    image,\r\n"+
            "    imageIndex = backgroundImages.length;\r\n"+
            "\r\n"+
            "    while(imageIndex--) {\r\n"+
            "      backgroundImage = backgroundImages[imageIndex];\r\n"+
            "\r\n"+
            "      if (!backgroundImage.args || backgroundImage.args.length === 0) {\r\n"+
            "        continue;\r\n"+
            "      }\r\n"+
            "\r\n"+
            "      var key = backgroundImage.method === 'url' ?\r\n"+
            "      backgroundImage.args[0] :\r\n"+
            "      backgroundImage.value;\r\n"+
            "\r\n"+
            "      image = loadImage(key);\r\n"+
            "\r\n"+
            "      // TODO add support for background-origin\r\n"+
            "      if (image) {\r\n"+
            "        renderBackgroundRepeating(element, bounds, ctx, image, imageIndex);\r\n"+
            "      } else {\r\n"+
            "        Util.log(\"html2canvas: Error loading background:\", backgroundImage);\r\n"+
            "      }\r\n"+
            "    }\r\n"+
            "  }\r\n"+
            "\r\n"+
            "  function resizeImage(image, bounds) {\r\n"+
            "    if(image.width === bounds.width && image.height === bounds.height) {\r\n"+
            "      return image;\r\n"+
            "    }\r\n"+
            "\r\n"+
            "    var ctx, canvas = doc.createElement('canvas');\r\n"+
            "    canvas.width = bounds.width;\r\n"+
            "    canvas.height = bounds.height;\r\n"+
            "    ctx = canvas.getContext(\"2d\");\r\n"+
            "    drawImage(ctx, image, 0, 0, image.width, image.height, 0, 0, bounds.width, bounds.height );\r\n"+
            "    return canvas;\r\n"+
            "  }\r\n"+
            "\r\n"+
            "  function setOpacity(ctx, element, parentStack) {\r\n"+
            "    return ctx.setVariable(\"globalAlpha\", getCSS(element, \"opacity\") * ((parentStack) ? parentStack.opacity : 1));\r\n"+
            "  }\r\n"+
            "\r\n"+
            "  function removePx(str) {\r\n"+
            "    return str.replace(\"px\", \"\");\r\n"+
            "  }\r\n"+
            "\r\n"+
            "  var transformRegExp = /(matrix)\\((.+)\\)/;\r\n"+
            "\r\n"+
            "  function getTransform(element, parentStack) {\r\n"+
            "    var transform = getCSS(element, \"transform\") || getCSS(element, \"-webkit-transform\") || getCSS(element, \"-moz-transform\") || getCSS(element, \"-ms-transform\") || getCSS(element, \"-o-transform\");\r\n"+
            "    var transformOrigin = getCSS(element, \"transform-origin\") || getCSS(element, \"-webkit-transform-origin\") || getCSS(element, \"-moz-transform-origin\") || getCSS(element, \"-ms-transform-origin\") || getCSS(element, \"-o-transform-origin\") || \"0px 0px\";\r\n"+
            "\r\n"+
            "    transformOrigin = transformOrigin.split(\" \").map(removePx).map(Util.asFloat);\r\n"+
            "\r\n"+
            "    var matrix;\r\n"+
            "    if (transform && transform !== \"none\") {\r\n"+
            "      var match = transform.match(transformRegExp);\r\n"+
            "      if (match) {\r\n"+
            "        switch(match[1]) {\r\n"+
            "          case \"matrix\":\r\n"+
            "            matrix = match[2].split(\",\").map(Util.trimText).map(Util.asFloat);\r\n"+
            "            break;\r\n"+
            "        }\r\n"+
            "      }\r\n"+
            "    }\r\n"+
            "\r\n"+
            "    return {\r\n"+
            "      origin: transformOrigin,\r\n"+
            "      matrix: matrix\r\n"+
            "    };\r\n"+
            "  }\r\n"+
            "\r\n"+
            "  function createStack(element, parentStack, bounds, transform) {\r\n"+
            "    var ctx = h2cRenderContext((!parentStack) ? documentWidth() : bounds.width , (!parentStack) ? documentHeight() : bounds.height),\r\n"+
            "    stack = {\r\n"+
            "      ctx: ctx,\r\n"+
            "      opacity: setOpacity(ctx, element, parentStack),\r\n"+
            "      cssPosition: getCSS(element, \"position\"),\r\n"+
            "      borders: getBorderData(element),\r\n"+
            "      transform: transform,\r\n"+
            "      clip: (parentStack && parentStack.clip) ? Util.Extend( {}, parentStack.clip ) : null\r\n"+
            "    };\r\n"+
            "\r\n"+
            "    setZ(element, stack, parentStack);\r\n"+
            "\r\n"+
            "    // TODO correct overflow for absolute content residing under a static position\r\n"+
            "    if (options.useOverflow === true && /(hidden|scroll|auto)/.test(getCSS(element, \"overflow\")) === true && /(BODY)/i.test(element.nodeName) === false){\r\n"+
            "      stack.clip = (stack.clip) ? clipBounds(stack.clip, bounds) : bounds;\r\n"+
            "    }\r\n"+
            "\r\n"+
            "    return stack;\r\n"+
            "  }\r\n"+
            "\r\n"+
            "  function getBackgroundBounds(borders, bounds, clip) {\r\n"+
            "    var backgroundBounds = {\r\n"+
            "      left: bounds.left + borders[3].width,\r\n"+
            "      top: bounds.top + borders[0].width,\r\n"+
            "      width: bounds.width - (borders[1].width + borders[3].width),\r\n"+
            "      height: bounds.height - (borders[0].width + borders[2].width)\r\n"+
            "    };\r\n"+
            "\r\n"+
            "    if (clip) {\r\n"+
            "      backgroundBounds = clipBounds(backgroundBounds, clip);\r\n"+
            "    }\r\n"+
            "\r\n"+
            "    return backgroundBounds;\r\n"+
            "  }\r\n"+
            "\r\n"+
            "  function getBounds(element, transform) {\r\n"+
            "    var bounds = (transform.matrix) ? Util.OffsetBounds(element) : Util.Bounds(element);\r\n"+
            "    transform.origin[0] += bounds.left;\r\n"+
            "    transform.origin[1] += bounds.top;\r\n"+
            "    return bounds;\r\n"+
            "  }\r\n"+
            "\r\n"+
            "  function renderElement(element, parentStack, pseudoElement, ignoreBackground) {\r\n"+
            "    var transform = getTransform(element, parentStack),\r\n"+
            "    bounds = getBounds(element, transform),\r\n"+
            "    image,\r\n"+
            "    stack = createStack(element, parentStack, bounds, transform),\r\n"+
            "    borders = stack.borders,\r\n"+
            "    ctx = stack.ctx,\r\n"+
            "    backgroundBounds = getBackgroundBounds(borders, bounds, stack.clip),\r\n"+
            "    borderData = parseBorders(element, bounds, borders),\r\n"+
            "    backgroundColor = (ignoreElementsRegExp.test(element.nodeName)) ? \"#efefef\" : getCSS(element, \"backgroundColor\");\r\n"+
            "\r\n"+
            "\r\n"+
            "    createShape(ctx, borderData.clip);\r\n"+
            "\r\n"+
            "    ctx.save();\r\n"+
            "    ctx.clip();\r\n"+
            "\r\n"+
            "    if (backgroundBounds.height > 0 && backgroundBounds.width > 0 && !ignoreBackground) {\r\n"+
            "      renderBackgroundColor(ctx, bounds, backgroundColor);\r\n"+
            "      renderBackgroundImage(element, backgroundBounds, ctx);\r\n"+
            "    } else if (ignoreBackground) {\r\n"+
            "      stack.backgroundColor =  backgroundColor;\r\n"+
            "    }\r\n"+
            "\r\n"+
            "    ctx.restore();\r\n"+
            "\r\n"+
            "    borderData.borders.forEach(function(border) {\r\n"+
            "      renderBorders(ctx, border.args, border.color);\r\n"+
            "    });\r\n"+
            "\r\n"+
            "    if (!pseudoElement) {\r\n"+
            "      injectPseudoElements(element, stack);\r\n"+
            "    }\r\n"+
            "\r\n"+
            "    switch(element.nodeName){\r\n"+
            "      case \"IMG\":\r\n"+
            "        if ((image = loadImage(element.getAttribute('src')))) {\r\n"+
            "          renderImage(ctx, element, image, bounds, borders);\r\n"+
            "        } else {\r\n"+
            "          Util.log(\"html2canvas: Error loading <img>:\" + element.getAttribute('src'));\r\n"+
            "        }\r\n"+
            "        break;\r\n"+
            "      case \"INPUT\":\r\n"+
            "        // TODO add all relevant type's, i.e. HTML5 new stuff\r\n"+
            "        // todo add support for placeholder attribute for browsers which support it\r\n"+
            "        if (/^(text|url|email|submit|button|reset)$/.test(element.type) && (element.value || element.placeholder || \"\").length > 0){\r\n"+
            "          renderFormValue(element, bounds, stack);\r\n"+
            "        }\r\n"+
            "        break;\r\n"+
            "      case \"TEXTAREA\":\r\n"+
            "        if ((element.value || element.placeholder || \"\").length > 0){\r\n"+
            "          renderFormValue(element, bounds, stack);\r\n"+
            "        }\r\n"+
            "        break;\r\n"+
            "      case \"SELECT\":\r\n"+
            "        if ((element.options||element.placeholder || \"\").length > 0){\r\n"+
            "          renderFormValue(elem");
          out.print(
            "ent, bounds, stack);\r\n"+
            "        }\r\n"+
            "        break;\r\n"+
            "      case \"LI\":\r\n"+
            "        renderListItem(element, stack, backgroundBounds);\r\n"+
            "        break;\r\n"+
            "      case \"CANVAS\":\r\n"+
            "        renderImage(ctx, element, element, bounds, borders);\r\n"+
            "        break;\r\n"+
            "    }\r\n"+
            "\r\n"+
            "    return stack;\r\n"+
            "  }\r\n"+
            "\r\n"+
            "  function isElementVisible(element) {\r\n"+
            "    return (getCSS(element, 'display') !== \"none\" && getCSS(element, 'visibility') !== \"hidden\" && !element.hasAttribute(\"data-html2canvas-ignore\"));\r\n"+
            "  }\r\n"+
            "\r\n"+
            "  function parseElement (element, stack, pseudoElement) {\r\n"+
            "    if (isElementVisible(element)) {\r\n"+
            "      stack = renderElement(element, stack, pseudoElement, false) || stack;\r\n"+
            "      if (!ignoreElementsRegExp.test(element.nodeName)) {\r\n"+
            "        parseChildren(element, stack, pseudoElement);\r\n"+
            "      }\r\n"+
            "    }\r\n"+
            "  }\r\n"+
            "\r\n"+
            "  function parseChildren(element, stack, pseudoElement) {\r\n"+
            "    Util.Children(element).forEach(function(node) {\r\n"+
            "      if (node.nodeType === node.ELEMENT_NODE) {\r\n"+
            "        parseElement(node, stack, pseudoElement);\r\n"+
            "      } else if (node.nodeType === node.TEXT_NODE) {\r\n"+
            "        renderText(element, node, stack);\r\n"+
            "      }\r\n"+
            "    });\r\n"+
            "  }\r\n"+
            "\r\n"+
            "  function init() {\r\n"+
            "    var background = getCSS(document.documentElement, \"backgroundColor\"),\r\n"+
            "      transparentBackground = (Util.isTransparent(background) && element === document.body),\r\n"+
            "      stack = renderElement(element, null, false, transparentBackground);\r\n"+
            "    parseChildren(element, stack);\r\n"+
            "\r\n"+
            "    if (transparentBackground) {\r\n"+
            "      background = stack.backgroundColor;\r\n"+
            "    }\r\n"+
            "\r\n"+
            "    body.removeChild(hidePseudoElements);\r\n"+
            "    return {\r\n"+
            "      backgroundColor: background,\r\n"+
            "      stack: stack\r\n"+
            "    };\r\n"+
            "  }\r\n"+
            "\r\n"+
            "  return init();\r\n"+
            "};\r\n"+
            "\r\n"+
            "function h2czContext(zindex) {\r\n"+
            "  return {\r\n"+
            "    zindex: zindex,\r\n"+
            "    children: []\r\n"+
            "  };\r\n"+
            "}\r\n"+
            "\r\n"+
            "_html2canvas.Preload = function( options ) {\r\n"+
            "\r\n"+
            "  var images = {\r\n"+
            "    numLoaded: 0,   // also failed are counted here\r\n"+
            "    numFailed: 0,\r\n"+
            "    numTotal: 0,\r\n"+
            "    cleanupDone: false\r\n"+
            "  },\r\n"+
            "  pageOrigin,\r\n"+
            "  Util = _html2canvas.Util,\r\n"+
            "  methods,\r\n"+
            "  i,\r\n"+
            "  count = 0,\r\n"+
            "  element = options.elements[0] || document.body,\r\n"+
            "  doc = element.ownerDocument,\r\n"+
            "  domImages = element.getElementsByTagName('img'), // Fetch images of the present element only\r\n"+
            "  imgLen = domImages.length,\r\n"+
            "  link = doc.createElement(\"a\"),\r\n"+
            "  supportCORS = (function( img ){\r\n"+
            "    return (img.crossOrigin !== undefined);\r\n"+
            "  })(new Image()),\r\n"+
            "  timeoutTimer;\r\n"+
            "\r\n"+
            "  link.href = window.location.href;\r\n"+
            "  pageOrigin  = link.protocol + link.host;\r\n"+
            "\r\n"+
            "  function isSameOrigin(url){\r\n"+
            "    link.href = url;\r\n"+
            "    link.href = link.href; // YES, BELIEVE IT OR NOT, that is required for IE9 - http://jsfiddle.net/niklasvh/2e48b/\r\n"+
            "    var origin = link.protocol + link.host;\r\n"+
            "    return (origin === pageOrigin);\r\n"+
            "  }\r\n"+
            "\r\n"+
            "  function start(){\r\n"+
            "    Util.log(\"html2canvas: start: images: \" + images.numLoaded + \" / \" + images.numTotal + \" (failed: \" + images.numFailed + \")\");\r\n"+
            "    if (!images.firstRun && images.numLoaded >= images.numTotal){\r\n"+
            "      Util.log(\"Finished loading images: # \" + images.numTotal + \" (failed: \" + images.numFailed + \")\");\r\n"+
            "\r\n"+
            "      if (typeof options.complete === \"function\"){\r\n"+
            "        options.complete(images);\r\n"+
            "      }\r\n"+
            "\r\n"+
            "    }\r\n"+
            "  }\r\n"+
            "\r\n"+
            "  // TODO modify proxy to serve images with CORS enabled, where available\r\n"+
            "  function proxyGetImage(url, img, imageObj){\r\n"+
            "    var callback_name,\r\n"+
            "    scriptUrl = options.proxy,\r\n"+
            "    script;\r\n"+
            "\r\n"+
            "    link.href = url;\r\n"+
            "    url = link.href; // work around for pages with base href=\"\" set - WARNING: this may change the url\r\n"+
            "\r\n"+
            "    callback_name = 'html2canvas_' + (count++);\r\n"+
            "    imageObj.callbackname = callback_name;\r\n"+
            "\r\n"+
            "    if (scriptUrl.indexOf(\"?\") > -1) {\r\n"+
            "      scriptUrl += \"&\";\r\n"+
            "    } else {\r\n"+
            "      scriptUrl += \"?\";\r\n"+
            "    }\r\n"+
            "    scriptUrl += 'url=' + encodeURIComponent(url) + '&callback=' + callback_name;\r\n"+
            "    script = doc.createElement(\"script\");\r\n"+
            "\r\n"+
            "    window[callback_name] = function(a){\r\n"+
            "      if (a.substring(0,6) === \"error:\"){\r\n"+
            "        imageObj.succeeded = false;\r\n"+
            "        images.numLoaded++;\r\n"+
            "        images.numFailed++;\r\n"+
            "        start();\r\n"+
            "      } else {\r\n"+
            "        setImageLoadHandlers(img, imageObj);\r\n"+
            "        img.src = a;\r\n"+
            "      }\r\n"+
            "      window[callback_name] = undefined; // to work with IE<9  // NOTE: that the undefined callback property-name still exists on the window object (for IE<9)\r\n"+
            "      try {\r\n"+
            "        delete window[callback_name];  // for all browser that support this\r\n"+
            "      } catch(ex) {}\r\n"+
            "      script.parentNode.removeChild(script);\r\n"+
            "      script = null;\r\n"+
            "      delete imageObj.script;\r\n"+
            "      delete imageObj.callbackname;\r\n"+
            "    };\r\n"+
            "\r\n"+
            "    script.setAttribute(\"type\", \"text/javascript\");\r\n"+
            "    script.setAttribute(\"src\", scriptUrl);\r\n"+
            "    imageObj.script = script;\r\n"+
            "    window.document.body.appendChild(script);\r\n"+
            "\r\n"+
            "  }\r\n"+
            "\r\n"+
            "  function loadPseudoElement(element, type) {\r\n"+
            "    var style = window.getComputedStyle(element, type),\r\n"+
            "    content = style.content;\r\n"+
            "    if (content.substr(0, 3) === 'url') {\r\n"+
            "      methods.loadImage(_html2canvas.Util.parseBackgroundImage(content)[0].args[0]);\r\n"+
            "    }\r\n"+
            "    loadBackgroundImages(style.backgroundImage, element);\r\n"+
            "  }\r\n"+
            "\r\n"+
            "  function loadPseudoElementImages(element) {\r\n"+
            "    loadPseudoElement(element, \":before\");\r\n"+
            "    loadPseudoElement(element, \":after\");\r\n"+
            "  }\r\n"+
            "\r\n"+
            "  function loadGradientImage(backgroundImage, bounds) {\r\n"+
            "    var img = _html2canvas.Generate.Gradient(backgroundImage, bounds);\r\n"+
            "\r\n"+
            "    if (img !== undefined){\r\n"+
            "      images[backgroundImage] = {\r\n"+
            "        img: img,\r\n"+
            "        succeeded: true\r\n"+
            "      };\r\n"+
            "      images.numTotal++;\r\n"+
            "      images.numLoaded++;\r\n"+
            "      start();\r\n"+
            "    }\r\n"+
            "  }\r\n"+
            "\r\n"+
            "  function invalidBackgrounds(background_image) {\r\n"+
            "    return (background_image && background_image.method && background_image.args && background_image.args.length > 0 );\r\n"+
            "  }\r\n"+
            "\r\n"+
            "  function loadBackgroundImages(background_image, el) {\r\n"+
            "    var bounds;\r\n"+
            "\r\n"+
            "    _html2canvas.Util.parseBackgroundImage(background_image).filter(invalidBackgrounds).forEach(function(background_image) {\r\n"+
            "      if (background_image.method === 'url') {\r\n"+
            "        methods.loadImage(background_image.args[0]);\r\n"+
            "      } else if(background_image.method.match(/\\-?gradient$/)) {\r\n"+
            "        if(bounds === undefined) {\r\n"+
            "          bounds = _html2canvas.Util.Bounds(el);\r\n"+
            "        }\r\n"+
            "        loadGradientImage(background_image.value, bounds);\r\n"+
            "      }\r\n"+
            "    });\r\n"+
            "  }\r\n"+
            "\r\n"+
            "  function getImages (el) {\r\n"+
            "    var elNodeType = false;\r\n"+
            "\r\n"+
            "    // Firefox fails with permission denied on pages with iframes\r\n"+
            "    try {\r\n"+
            "    	var child = Util.Children(el);\r\n"+
            "    	if(child.constructor ===Array)\r\n"+
            "    		child.forEach(getImages);\r\n"+
            "//      Util.Children(el).forEach(getImages);\r\n"+
            "    }\r\n"+
            "    catch( e ) {}\r\n"+
            "\r\n"+
            "    try {\r\n"+
            "      elNodeType = el.nodeType;\r\n"+
            "    } catch (ex) {\r\n"+
            "      elNodeType = false;\r\n"+
            "      Util.log(\"html2canvas: failed to access some element's nodeType - Exception: \" + ex.message);\r\n"+
            "    }\r\n"+
            "\r\n"+
            "    if (elNodeType === 1 || elNodeType === undefined) {\r\n"+
            "      loadPseudoElementImages(el);\r\n"+
            "      try {\r\n"+
            "        loadBackgroundImages(Util.getCSS(el, 'backgroundImage'), el);\r\n"+
            "      } catch(e) {\r\n"+
            "        Util.log(\"html2canvas: failed to get background-image - Exception: \" + e.message);\r\n"+
            "      }\r\n"+
            "      loadBackgroundImages(el);\r\n"+
            "    }\r\n"+
            "  }\r\n"+
            "\r\n"+
            "  function setImageLoadHandlers(img, imageObj) {\r\n"+
            "    img.onload = function() {\r\n"+
            "      if ( imageObj.timer !== undefined ) {\r\n"+
            "        // CORS succeeded\r\n"+
            "        window.clearTimeout( imageObj.timer );\r\n"+
            "      }\r\n"+
            "\r\n"+
            "      images.numLoaded++;\r\n"+
            "      imageObj.succeeded = true;\r\n"+
            "      img.onerror = img.onload = null;\r\n"+
            "      start();\r\n"+
            "    };\r\n"+
            "    img.onerror = function() {\r\n"+
            "      if (img.crossOrigin === \"anonymous\") {\r\n"+
            "        // CORS failed\r\n"+
            "        window.clearTimeout( imageObj.timer );\r\n"+
            "\r\n"+
            "        // let's try with proxy instead\r\n"+
            "        if ( options.proxy ) {\r\n"+
            "          var src = img.src;\r\n"+
            "          img = new Image();\r\n"+
            "          imageObj.img = img;\r\n"+
            "          img.src = src;\r\n"+
            "\r\n"+
            "          proxyGetImage( img.src, img, imageObj );\r\n"+
            "          return;\r\n"+
            "        }\r\n"+
            "      }\r\n"+
            "\r\n"+
            "      images.numLoaded++;\r\n"+
            "      images.numFailed++;\r\n"+
            "      imageObj.succeeded = false;\r\n"+
            "      img.onerror = img.onload = null;\r\n"+
            "      start();\r\n"+
            "    };\r\n"+
            "  }\r\n"+
            "\r\n"+
            "  methods = {\r\n"+
            "    loadImage: function( src ) {\r\n"+
            "      var img, imageObj;\r\n"+
            "      if ( src && images[src] === undefined ) {\r\n"+
            "        img = new Image();\r\n"+
            "        if ( src.match(/data:image\\/.*;base64,/i) ) {\r\n"+
            "          img.src = src.replace(/url\\(['\"]{0,}|['\"]{0,}\\)$/ig, '');\r\n"+
            "          imageObj = images[src] = {\r\n"+
            "            img: img\r\n"+
            "          };\r\n"+
            "          images.numTotal++;\r\n"+
            "          setImageLoadHandlers(img, imageObj);\r\n"+
            "        } else if ( isSameOrigin( src ) || options.allowTaint ===  true ) {\r\n"+
            "          imageObj = images[src] = {\r\n"+
            "            img: img\r\n"+
            "          };\r\n"+
            "          images.numTotal++;\r\n"+
            "          setImageLoadHandlers(img, imageObj);\r\n"+
            "          img.src = src;\r\n"+
            "        } else if ( supportCORS && !options.allowTaint && options.useCORS ) {\r\n"+
            "          // attempt to load with CORS\r\n"+
            "\r\n"+
            "          img.crossOrigin = \"anonymous\";\r\n"+
            "          imageObj = images[src] = {\r\n"+
            "            img: img\r\n"+
            "          };\r\n"+
            "          images.numTotal++;\r\n"+
            "          setImageLoadHandlers(img, imageObj);\r\n"+
            "          img.src = src;\r\n"+
            "        } else if ( options.proxy ) {\r\n"+
            "          imageObj = images[src] = {\r\n"+
            "            img: img\r\n"+
            "          };\r\n"+
            "          images.numTotal++;\r\n"+
            "          proxyGetImage( src, img, imageObj );\r\n"+
            "        }\r\n"+
            "      }\r\n"+
            "\r\n"+
            "    },\r\n"+
            "    cleanupDOM: function(cause) {\r\n"+
            "      var img, src;\r\n"+
            "      if (!images.cleanupDone) {\r\n"+
            "        if (cause && typeof cause === \"string\") {\r\n"+
            "          Util.log(\"html2canvas: Cleanup because: \" + cause);\r\n"+
            "        } else {\r\n"+
            "          Util.log(\"html2canvas: Cleanup after timeout: \" + options.timeout + \" ms.\");\r\n"+
            "        }\r\n"+
            "\r\n"+
            "        for (src in images) {\r\n"+
            "          if (images.hasOwnProp");
          out.print(
            "erty(src)) {\r\n"+
            "            img = images[src];\r\n"+
            "            if (typeof img === \"object\" && img.callbackname && img.succeeded === undefined) {\r\n"+
            "              // cancel proxy image request\r\n"+
            "              window[img.callbackname] = undefined; // to work with IE<9  // NOTE: that the undefined callback property-name still exists on the window object (for IE<9)\r\n"+
            "              try {\r\n"+
            "                delete window[img.callbackname];  // for all browser that support this\r\n"+
            "              } catch(ex) {}\r\n"+
            "              if (img.script && img.script.parentNode) {\r\n"+
            "                img.script.setAttribute(\"src\", \"about:blank\");  // try to cancel running request\r\n"+
            "                img.script.parentNode.removeChild(img.script);\r\n"+
            "              }\r\n"+
            "              images.numLoaded++;\r\n"+
            "              images.numFailed++;\r\n"+
            "              Util.log(\"html2canvas: Cleaned up failed img: '\" + src + \"' Steps: \" + images.numLoaded + \" / \" + images.numTotal);\r\n"+
            "            }\r\n"+
            "          }\r\n"+
            "        }\r\n"+
            "\r\n"+
            "        // cancel any pending requests\r\n"+
            "        if(window.stop !== undefined) {\r\n"+
            "          window.stop();\r\n"+
            "        } else if(document.execCommand !== undefined) {\r\n"+
            "          document.execCommand(\"Stop\", false);\r\n"+
            "        }\r\n"+
            "        if (document.close !== undefined) {\r\n"+
            "          document.close();\r\n"+
            "        }\r\n"+
            "        images.cleanupDone = true;\r\n"+
            "        if (!(cause && typeof cause === \"string\")) {\r\n"+
            "          start();\r\n"+
            "        }\r\n"+
            "      }\r\n"+
            "    },\r\n"+
            "\r\n"+
            "    renderingDone: function() {\r\n"+
            "      if (timeoutTimer) {\r\n"+
            "        window.clearTimeout(timeoutTimer);\r\n"+
            "      }\r\n"+
            "    }\r\n"+
            "  };\r\n"+
            "\r\n"+
            "  if (options.timeout > 0) {\r\n"+
            "    timeoutTimer = window.setTimeout(methods.cleanupDOM, options.timeout);\r\n"+
            "  }\r\n"+
            "\r\n"+
            "  Util.log('html2canvas: Preload starts: finding background-images');\r\n"+
            "  images.firstRun = true;\r\n"+
            "\r\n"+
            "  getImages(element);\r\n"+
            "\r\n"+
            "  Util.log('html2canvas: Preload: Finding images');\r\n"+
            "  // load <img> images\r\n"+
            "  for (i = 0; i < imgLen; i+=1){\r\n"+
            "    methods.loadImage( domImages[i].getAttribute( \"src\" ) );\r\n"+
            "  }\r\n"+
            "\r\n"+
            "  images.firstRun = false;\r\n"+
            "  Util.log('html2canvas: Preload: Done.');\r\n"+
            "  if (images.numTotal === images.numLoaded) {\r\n"+
            "    start();\r\n"+
            "  }\r\n"+
            "\r\n"+
            "  return methods;\r\n"+
            "};\r\n"+
            "\r\n"+
            "_html2canvas.Renderer = function(parseQueue, options){\r\n"+
            "\r\n"+
            "  // http://www.w3.org/TR/CSS21/zindex.html\r\n"+
            "  function createRenderQueue(parseQueue) {\r\n"+
            "    var queue = [],\r\n"+
            "    rootContext;\r\n"+
            "\r\n"+
            "    rootContext = (function buildStackingContext(rootNode) {\r\n"+
            "      var rootContext = {};\r\n"+
            "      function insert(context, node, specialParent) {\r\n"+
            "        var zi = (node.zIndex.zindex === 'auto') ? 0 : Number(node.zIndex.zindex),\r\n"+
            "        contextForChildren = context, // the stacking context for children\r\n"+
            "        isPositioned = node.zIndex.isPositioned,\r\n"+
            "        isFloated = node.zIndex.isFloated,\r\n"+
            "        stub = {node: node},\r\n"+
            "        childrenDest = specialParent; // where children without z-index should be pushed into\r\n"+
            "\r\n"+
            "        if (node.zIndex.ownStacking) {\r\n"+
            "          // '!' comes before numbers in sorted array\r\n"+
            "          contextForChildren = stub.context = { '!': [{node:node, children: []}]};\r\n"+
            "          childrenDest = undefined;\r\n"+
            "        } else if (isPositioned || isFloated) {\r\n"+
            "          childrenDest = stub.children = [];\r\n"+
            "        }\r\n"+
            "\r\n"+
            "        if (zi === 0 && specialParent) {\r\n"+
            "          specialParent.push(stub);\r\n"+
            "        } else {\r\n"+
            "          if (!context[zi]) { context[zi] = []; }\r\n"+
            "          context[zi].push(stub);\r\n"+
            "        }\r\n"+
            "\r\n"+
            "        node.zIndex.children.forEach(function(childNode) {\r\n"+
            "          insert(contextForChildren, childNode, childrenDest);\r\n"+
            "        });\r\n"+
            "      }\r\n"+
            "      insert(rootContext, rootNode);\r\n"+
            "      return rootContext;\r\n"+
            "    })(parseQueue);\r\n"+
            "\r\n"+
            "    function sortZ(context) {\r\n"+
            "      Object.keys(context).sort().forEach(function(zi) {\r\n"+
            "        var nonPositioned = [],\r\n"+
            "        floated = [],\r\n"+
            "        positioned = [],\r\n"+
            "        list = [];\r\n"+
            "\r\n"+
            "        // positioned after static\r\n"+
            "        context[zi].forEach(function(v) {\r\n"+
            "          if (v.node.zIndex.isPositioned || v.node.zIndex.opacity < 1) {\r\n"+
            "            // http://www.w3.org/TR/css3-color/#transparency\r\n"+
            "            // non-positioned element with opactiy < 1 should be stacked as if it were a positioned element with ‘z-index: 0’ and ‘opacity: 1’.\r\n"+
            "            positioned.push(v);\r\n"+
            "          } else if (v.node.zIndex.isFloated) {\r\n"+
            "            floated.push(v);\r\n"+
            "          } else {\r\n"+
            "            nonPositioned.push(v);\r\n"+
            "          }\r\n"+
            "        });\r\n"+
            "\r\n"+
            "        (function walk(arr) {\r\n"+
            "          arr.forEach(function(v) {\r\n"+
            "            list.push(v);\r\n"+
            "            if (v.children) { walk(v.children); }\r\n"+
            "          });\r\n"+
            "        })(nonPositioned.concat(floated, positioned));\r\n"+
            "\r\n"+
            "        list.forEach(function(v) {\r\n"+
            "          if (v.context) {\r\n"+
            "            sortZ(v.context);\r\n"+
            "          } else {\r\n"+
            "            queue.push(v.node);\r\n"+
            "          }\r\n"+
            "        });\r\n"+
            "      });\r\n"+
            "    }\r\n"+
            "\r\n"+
            "    sortZ(rootContext);\r\n"+
            "\r\n"+
            "    return queue;\r\n"+
            "  }\r\n"+
            "\r\n"+
            "  function getRenderer(rendererName) {\r\n"+
            "    var renderer;\r\n"+
            "\r\n"+
            "    if (typeof options.renderer === \"string\" && _html2canvas.Renderer[rendererName] !== undefined) {\r\n"+
            "      renderer = _html2canvas.Renderer[rendererName](options);\r\n"+
            "    } else if (typeof rendererName === \"function\") {\r\n"+
            "      renderer = rendererName(options);\r\n"+
            "    } else {\r\n"+
            "      throw new Error(\"Unknown renderer\");\r\n"+
            "    }\r\n"+
            "\r\n"+
            "    if ( typeof renderer !== \"function\" ) {\r\n"+
            "      throw new Error(\"Invalid renderer defined\");\r\n"+
            "    }\r\n"+
            "    return renderer;\r\n"+
            "  }\r\n"+
            "\r\n"+
            "  return getRenderer(options.renderer)(parseQueue, options, document, createRenderQueue(parseQueue.stack), _html2canvas);\r\n"+
            "};\r\n"+
            "\r\n"+
            "_html2canvas.Util.Support = function (options, doc) {\r\n"+
            "\r\n"+
            "  function supportSVGRendering() {\r\n"+
            "    var img = new Image(),\r\n"+
            "    canvas = doc.createElement(\"canvas\"),\r\n"+
            "    ctx = (canvas.getContext === undefined) ? false : canvas.getContext(\"2d\");\r\n"+
            "    if (ctx === false) {\r\n"+
            "      return false;\r\n"+
            "    }\r\n"+
            "    canvas.width = canvas.height = 10;\r\n"+
            "    img.src = [\r\n"+
            "    \"data:image/svg+xml,\",\r\n"+
            "    \"<svg xmlns='http://www.w3.org/2000/svg' width='10' height='10'>\",\r\n"+
            "    \"<foreignObject width='10' height='10'>\",\r\n"+
            "    \"<div xmlns='http://www.w3.org/1999/xhtml' style='width:10;height:10;'>\",\r\n"+
            "    \"sup\",\r\n"+
            "    \"</div>\",\r\n"+
            "    \"</foreignObject>\",\r\n"+
            "    \"</svg>\"\r\n"+
            "    ].join(\"\");\r\n"+
            "    try {\r\n"+
            "      ctx.drawImage(img, 0, 0);\r\n"+
            "      canvas.toDataURL();\r\n"+
            "    } catch(e) {\r\n"+
            "      return false;\r\n"+
            "    }\r\n"+
            "    _html2canvas.Util.log('html2canvas: Parse: SVG powered rendering available');\r\n"+
            "    return true;\r\n"+
            "  }\r\n"+
            "\r\n"+
            "  // Test whether we can use ranges to measure bounding boxes\r\n"+
            "  // Opera doesn't provide valid bounds.height/bottom even though it supports the method.\r\n"+
            "\r\n"+
            "  function supportRangeBounds() {\r\n"+
            "    var r, testElement, rangeBounds, rangeHeight, support = false;\r\n"+
            "\r\n"+
            "    if (doc.createRange) {\r\n"+
            "      r = doc.createRange();\r\n"+
            "      if (r.getBoundingClientRect) {\r\n"+
            "        testElement = doc.createElement('boundtest');\r\n"+
            "        testElement.style.height = \"123px\";\r\n"+
            "        testElement.style.display = \"block\";\r\n"+
            "        doc.body.appendChild(testElement);\r\n"+
            "\r\n"+
            "        r.selectNode(testElement);\r\n"+
            "        rangeBounds = r.getBoundingClientRect();\r\n"+
            "        rangeHeight = rangeBounds.height;\r\n"+
            "\r\n"+
            "        if (rangeHeight === 123) {\r\n"+
            "          support = true;\r\n"+
            "        }\r\n"+
            "        doc.body.removeChild(testElement);\r\n"+
            "      }\r\n"+
            "    }\r\n"+
            "\r\n"+
            "    return support;\r\n"+
            "  }\r\n"+
            "\r\n"+
            "  return {\r\n"+
            "    rangeBounds: supportRangeBounds(),\r\n"+
            "    svgRendering: options.svgRendering && supportSVGRendering()\r\n"+
            "  };\r\n"+
            "};\r\n"+
            "window.html2canvas = function(elements, opts) {\r\n"+
            "  elements = (elements.length) ? elements : [elements];\r\n"+
            "  var queue,\r\n"+
            "  canvas,\r\n"+
            "  options = {\r\n"+
            "    // general\r\n"+
            "    logging: false,\r\n"+
            "    elements: elements,\r\n"+
            "    background: \"#fff\",\r\n"+
            "\r\n"+
            "    // preload options\r\n"+
            "    proxy: null,\r\n"+
            "    timeout: 0,    // no timeout\r\n"+
            "    useCORS: false, // try to load images as CORS (where available), before falling back to proxy\r\n"+
            "    allowTaint: false, // whether to allow images to taint the canvas, won't need proxy if set to true\r\n"+
            "\r\n"+
            "    // parse options\r\n"+
            "    svgRendering: false, // use svg powered rendering where available (FF11+)\r\n"+
            "    ignoreElements: \"IFRAME|OBJECT|PARAM\",\r\n"+
            "    useOverflow: true,\r\n"+
            "    letterRendering: false,\r\n"+
            "    chinese: false,\r\n"+
            "\r\n"+
            "    // render options\r\n"+
            "\r\n"+
            "    width: null,\r\n"+
            "    height: null,\r\n"+
            "    taintTest: true, // do a taint test with all images before applying to canvas\r\n"+
            "    renderer: \"Canvas\"\r\n"+
            "  };\r\n"+
            "\r\n"+
            "  options = _html2canvas.Util.Extend(opts, options);\r\n"+
            "\r\n"+
            "  _html2canvas.logging = options.logging;\r\n"+
            "  options.complete = function( images ) {\r\n"+
            "\r\n"+
            "    if (typeof options.onpreloaded === \"function\") {\r\n"+
            "      if ( options.onpreloaded( images ) === false ) {\r\n"+
            "        return;\r\n"+
            "      }\r\n"+
            "    }\r\n"+
            "    queue = _html2canvas.Parse( images, options );\r\n"+
            "\r\n"+
            "    if (typeof options.onparsed === \"function\") {\r\n"+
            "      if ( options.onparsed( queue ) === false ) {\r\n"+
            "        return;\r\n"+
            "      }\r\n"+
            "    }\r\n"+
            "\r\n"+
            "    canvas = _html2canvas.Renderer( queue, options );\r\n"+
            "\r\n"+
            "    if (typeof options.onrendered === \"function\") {\r\n"+
            "      options.onrendered( canvas );\r\n"+
            "    }\r\n"+
            "\r\n"+
            "\r\n"+
            "  };\r\n"+
            "\r\n"+
            "  // for pages without images, we still want this to be async, i.e. return methods before executing\r\n"+
            "  window.setTimeout( function(){\r\n"+
            "    _html2canvas.Preload( options );\r\n"+
            "  }, 0 );\r\n"+
            "\r\n"+
            "  return {\r\n"+
            "    render: function( queue, opts ) {\r\n"+
            "      return _html2canvas.Renderer( queue, _html2canvas.Util.Extend(opts, options) );\r\n"+
            "    },\r\n"+
            "    parse: function( images, opts ) {\r\n"+
            "      return _html2canvas.Parse( images, _html2canvas.Util.Extend(opts, options) );\r\n"+
            "    },\r\n"+
            "    preload: function( opts ) {\r\n"+
            "      return _html2canvas.Preload( _html2canvas.Util.Extend(opts, options) );\r\n"+
            "    },\r\n"+
            "    log: _html2canvas.Util.log\r\n"+
            "  };\r\n"+
            "};\r\n"+
            "\r\n"+
            "window.html2canvas.log = _html2canvas.Util.log; // for renderers\r\n"+
            "window.html2canvas.Renderer = {\r\n"+
            "  Canvas: undefined // We are assuming this will be used\r\n"+
            "};\r\n"+
            "_html2canva");
          out.print(
            "s.Renderer.Canvas = function(options) {\r\n"+
            "  options = options || {};\r\n"+
            "\r\n"+
            "  var doc = document,\r\n"+
            "  safeImages = [],\r\n"+
            "  testCanvas = document.createElement(\"canvas\"),\r\n"+
            "  testctx = testCanvas.getContext(\"2d\"),\r\n"+
            "  Util = _html2canvas.Util,\r\n"+
            "  canvas = options.canvas || doc.createElement('canvas');\r\n"+
            "\r\n"+
            "  function createShape(ctx, args) {\r\n"+
            "    ctx.beginPath();\r\n"+
            "    args.forEach(function(arg) {\r\n"+
            "      ctx[arg.name].apply(ctx, arg['arguments']);\r\n"+
            "    });\r\n"+
            "    ctx.closePath();\r\n"+
            "  }\r\n"+
            "\r\n"+
            "  function safeImage(item) {\r\n"+
            "    if (safeImages.indexOf(item['arguments'][0].src ) === -1) {\r\n"+
            "      testctx.drawImage(item['arguments'][0], 0, 0);\r\n"+
            "      try {\r\n"+
            "        testctx.getImageData(0, 0, 1, 1);\r\n"+
            "      } catch(e) {\r\n"+
            "        testCanvas = doc.createElement(\"canvas\");\r\n"+
            "        testctx = testCanvas.getContext(\"2d\");\r\n"+
            "        return false;\r\n"+
            "      }\r\n"+
            "      safeImages.push(item['arguments'][0].src);\r\n"+
            "    }\r\n"+
            "    return true;\r\n"+
            "  }\r\n"+
            "\r\n"+
            "  function renderItem(ctx, item) {\r\n"+
            "    switch(item.type){\r\n"+
            "      case \"variable\":\r\n"+
            "        ctx[item.name] = item['arguments'];\r\n"+
            "        break;\r\n"+
            "      case \"function\":\r\n"+
            "        switch(item.name) {\r\n"+
            "          case \"createPattern\":\r\n"+
            "            if (item['arguments'][0].width > 0 && item['arguments'][0].height > 0) {\r\n"+
            "              try {\r\n"+
            "                ctx.fillStyle = ctx.createPattern(item['arguments'][0], \"repeat\");\r\n"+
            "              }\r\n"+
            "              catch(e) {\r\n"+
            "                Util.log(\"html2canvas: Renderer: Error creating pattern\", e.message);\r\n"+
            "              }\r\n"+
            "            }\r\n"+
            "            break;\r\n"+
            "          case \"drawShape\":\r\n"+
            "            createShape(ctx, item['arguments']);\r\n"+
            "            break;\r\n"+
            "          case \"drawImage\":\r\n"+
            "            if (item['arguments'][8] > 0 && item['arguments'][7] > 0) {\r\n"+
            "              if (!options.taintTest || (options.taintTest && safeImage(item))) {\r\n"+
            "                ctx.drawImage.apply( ctx, item['arguments'] );\r\n"+
            "              }\r\n"+
            "            }\r\n"+
            "            break;\r\n"+
            "          default:\r\n"+
            "            ctx[item.name].apply(ctx, item['arguments']);\r\n"+
            "        }\r\n"+
            "        break;\r\n"+
            "    }\r\n"+
            "  }\r\n"+
            "\r\n"+
            "  return function(parsedData, options, document, queue, _html2canvas) {\r\n"+
            "    var ctx = canvas.getContext(\"2d\"),\r\n"+
            "    newCanvas,\r\n"+
            "    bounds,\r\n"+
            "    fstyle,\r\n"+
            "    zStack = parsedData.stack;\r\n"+
            "\r\n"+
            "    canvas.width = canvas.style.width =  options.width || zStack.ctx.width;\r\n"+
            "    canvas.height = canvas.style.height = options.height || zStack.ctx.height;\r\n"+
            "\r\n"+
            "    fstyle = ctx.fillStyle;\r\n"+
            "    ctx.fillStyle = (Util.isTransparent(zStack.backgroundColor) && options.background !== undefined) ? options.background : parsedData.backgroundColor;\r\n"+
            "    ctx.fillRect(0, 0, canvas.width, canvas.height);\r\n"+
            "    ctx.fillStyle = fstyle;\r\n"+
            "\r\n"+
            "    queue.forEach(function(storageContext) {\r\n"+
            "      // set common settings for canvas\r\n"+
            "      ctx.textBaseline = \"bottom\";\r\n"+
            "      ctx.save();\r\n"+
            "\r\n"+
            "      if (storageContext.transform.matrix) {\r\n"+
            "        ctx.translate(storageContext.transform.origin[0], storageContext.transform.origin[1]);\r\n"+
            "        ctx.transform.apply(ctx, storageContext.transform.matrix);\r\n"+
            "        ctx.translate(-storageContext.transform.origin[0], -storageContext.transform.origin[1]);\r\n"+
            "      }\r\n"+
            "\r\n"+
            "      if (storageContext.clip){\r\n"+
            "        ctx.beginPath();\r\n"+
            "        ctx.rect(storageContext.clip.left, storageContext.clip.top, storageContext.clip.width, storageContext.clip.height);\r\n"+
            "        ctx.clip();\r\n"+
            "      }\r\n"+
            "\r\n"+
            "      if (storageContext.ctx.storage) {\r\n"+
            "        storageContext.ctx.storage.forEach(function(item) {\r\n"+
            "          renderItem(ctx, item);\r\n"+
            "        });\r\n"+
            "      }\r\n"+
            "\r\n"+
            "      ctx.restore();\r\n"+
            "    });\r\n"+
            "\r\n"+
            "    Util.log(\"html2canvas: Renderer: Canvas renderer done - returning canvas obj\");\r\n"+
            "\r\n"+
            "    if (options.elements.length === 1) {\r\n"+
            "      if (typeof options.elements[0] === \"object\" && options.elements[0].nodeName !== \"BODY\") {\r\n"+
            "        // crop image to the bounds of selected (single) element\r\n"+
            "        bounds = _html2canvas.Util.Bounds(options.elements[0]);\r\n"+
            "        newCanvas = document.createElement('canvas');\r\n"+
            "        newCanvas.width = Math.ceil(bounds.width);\r\n"+
            "        newCanvas.height = Math.ceil(bounds.height);\r\n"+
            "        ctx = newCanvas.getContext(\"2d\");\r\n"+
            "\r\n"+
            "        ctx.drawImage(canvas, bounds.left, bounds.top, bounds.width, bounds.height, 0, 0, bounds.width, bounds.height);\r\n"+
            "        canvas = null;\r\n"+
            "        return newCanvas;\r\n"+
            "      }\r\n"+
            "    }\r\n"+
            "\r\n"+
            "    return canvas;\r\n"+
            "  };\r\n"+
            "};\r\n"+
            "})(window,document);");

	}
	
}