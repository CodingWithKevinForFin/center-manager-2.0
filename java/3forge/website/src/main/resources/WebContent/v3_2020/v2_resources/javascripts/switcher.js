/*
 jQuery Cookie Plugin
 https://github.com/carhartl/jquery-cookie

 Copyright 2011, Klaus Hartl
 Dual licensed under the MIT or GPL Version 2 licenses.
 http://www.opensource.org/licenses/mit-license.php
 http://www.opensource.org/licenses/GPL-2.0
*/
(function(c){function e(d,a,b){c(d).removeClass(function(b,c){return(c.match(new RegExp("\\b"+a+"\\S+","g"))||[]).join(" ")});c(d).addClass(b);c.cookie(a+"smartbox",b,{path:"/",expires:7})}c.cookie=function(d,a,b){if(1<arguments.length&&(!/Object/.test(Object.prototype.toString.call(a))||null===a||void 0===a)){b=c.extend({},b);if(null===a||void 0===a)b.expires=-1;if("number"===typeof b.expires){var e=b.expires,f=b.expires=new Date;f.setDate(f.getDate()+e)}a=String(a);return document.cookie=[encodeURIComponent(d),
"=",b.raw?a:encodeURIComponent(a),b.expires?"; expires="+b.expires.toUTCString():"",b.path?"; path="+b.path:"",b.domain?"; domain="+b.domain:"",b.secure?"; secure":""].join("")}b=a||{};e=b.raw?function(a){return a}:decodeURIComponent;f=document.cookie.split("; ");for(var g=0,h;h=f[g]&&f[g].split("=");g++)if(e(h[0])===d)return e(h[1]||"");return null};c(document).ready(function(){var d=c("#style-switcher");null!==c.cookie("theme-pattern-smartbox")&&e("body","theme-pattern-",c.cookie("theme-pattern-smartbox"));
null!==c.cookie("theme-color-smartbox")&&e("body","theme-color-",c.cookie("theme-color-smartbox"));null!==c.cookie("theme-smartbox")&&e("footer","theme-",c.cookie("theme-smartbox"));d.find(".pattern-switch").click(function(){var a=c(this),b=a.data("pattern");a=a.data("theme");e("body","theme-pattern-",b);e("footer","theme-",a)});d.find(".colour-switch").click(function(){var a=c(this).data("color");e("body","theme-color-",a)});d.find(".handle").on("click",function(a){d.toggleClass("open");d.hasClass("open")?
d.animate({left:0},400):d.animate({left:-240},400);a.preventDefault()})})})(jQuery);
