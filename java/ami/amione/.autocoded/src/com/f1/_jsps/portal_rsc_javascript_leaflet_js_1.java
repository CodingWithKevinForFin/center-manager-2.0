package com.f1._jsps;

import com.f1.http.*;
import com.f1.utils.*;
import com.f1.http.handler.AbstractHttpHandler;
import com.f1.http.HttpUtils;
import static com.f1.http.HttpUtils.*;

public class portal_rsc_javascript_leaflet_js_1 extends AbstractHttpHandler{

	public portal_rsc_javascript_leaflet_js_1() {
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
            " Leaflet, a JavaScript library for mobile-friendly interactive maps. http://leafletjs.com\r\n"+
            " (c) 2010-2013, Vladimir Agafonkin\r\n"+
            " (c) 2010-2011, CloudMade\r\n"+
            "*/\r\n"+
            "\r\n"+
            "var MAPBOX_ICON;\r\n"+
            "\r\n"+
            "function MapBoxPortlet(portletId, parentId){\r\n"+
            "	this.id = portletId;\r\n"+
            "	this.portlet = new Portlet(this, portletId, null);\r\n"+
            "	this.mapboxDiv = nw(\"div\");\r\n"+
            "	this.mapboxDiv.style.width = \"100%\";\r\n"+
            "	this.mapboxDiv.style.height = \"100%\";\r\n"+
            "	this.mapboxDiv.id = \"mapbox_\"+portletId;\r\n"+
            "	this.needsAccessTokenDiv = nw(\"div\",\"mapBox_needsToken\");\r\n"+
            "	this.needsAccessTokenDiv.innerHTML = \"<B>YOU MUST SETUP A MAPBOX ACCESS TOKEN</B>. <P>(1) Visit <A target='_blank' href='https://mapbox.com/'>mapbox.com</A> to create a token. <BR>(2) Open your local.properties file <BR>(3) Copy/Paste the token into the property:<P>&nbsp;&nbsp;  ami.mapbox.token=<i>your_token_id</i> <P>(4) Restart AMI\";\r\n"+
            "	\r\n"+
            "	this.divElement.appendChild(this.mapboxDiv);\r\n"+
            "	this.mapboxDiv.appendChild(this.needsAccessTokenDiv);\r\n"+
            "}\r\n"+
            "\r\n"+
            "MapBoxPortlet.prototype.showNeedsAccessToken=function(show){\r\n"+
            "	if(show == false)\r\n"+
            "		this.needsAccessTokenDiv.style.display = \"none\";\r\n"+
            "	else{\r\n"+
            "		this.needsAccessTokenDiv.style.display = \"block\";\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "MapBoxPortlet.prototype.initMapBox = function(){\r\n"+
            "	var window_ = getWindow(this.mapboxDiv);\r\n"+
            "	this.map = L.map('mapbox_'+this.id, null, window_.document);\r\n"+
            "	this.map.fitWorld();\r\n"+
            "	//this.map.setView([51.505, -0.09], 15);\r\n"+
            "	this.map.amiMapId=this.id;\r\n"+
            "	this.layerGroup=L.featureGroup();\r\n"+
            "	this.layersByLayerIdPointId=[];\r\n"+
            "	this.markers=[];\r\n"+
            "}\r\n"+
            "\r\n"+
            "MapBoxPortlet.prototype.tileLayer=function(token){\r\n"+
            "	L.tileLayer('https://api.tiles.mapbox.com/v4/{id}/{z}/{x}/{y}.png?access_token='+token, { maxZoom: 18,id: 'mapbox.satellite'}).addTo(this.map);\r\n"+
            "}\r\n"+
            "\r\n"+
            "MapBoxPortlet.prototype.resizeMapBox = function(){\r\n"+
            "	if(this.map != null)\r\n"+
            "		this.map.invalidateSize();\r\n"+
            "}\r\n"+
            "\r\n"+
            "MapBoxPortlet.prototype.clearPointsMapBox = function(){\r\n"+
            "	if(this.map != null){\r\n"+
            "	    var group=this.layerGroup;\r\n"+
            "		var map=this.map;\r\n"+
            "		var toRemove=group.getLayers();\r\n"+
            "	    for(var i=0,l=toRemove.length;i<l;i++){\r\n"+
            "	    	map.removeLayer(toRemove[i]);\r\n"+
            "	    	group.removeLayer(toRemove[i]);\r\n"+
            "	    }\r\n"+
            "	}\r\n"+
            "	this.layersByLayerIdPointId=[];\r\n"+
            "}\r\n"+
            "\r\n"+
            "MapBoxPortlet.prototype.updatePointsMapBox = function(options){\r\n"+
            "	if(this.map != null){\r\n"+
            "	    var group=this.layerGroup;\r\n"+
            "		var map=this.map;\r\n"+
            "	    var borderColors=options.borderColors;\r\n"+
            "	    var colors=options.colors;\r\n"+
            "	    var amiLayerIds=options.amiLayerIds;\r\n"+
            "	    var amiPointIds=options.amiPointIds;\r\n"+
            "	    var cnt=amiPointIds.length;\r\n"+
            "	    for(var i=0;i<cnt;i++){\r\n"+
            "	    	layer=this.layersByLayerIdPointId[group,amiLayerIds[i]][amiPointIds[i]];\r\n"+
            "	    	if(layer!=null)\r\n"+
            "	    	  layer.setStyle({color: borderColors[i], fillColor: colors[i]});\r\n"+
            "	    }\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "MapBoxPortlet.prototype.clearExistingLabels=function() {\r\n"+
            "	for (var i = 0; i < this.markers.length; i++)\r\n"+
            "		this.map.removeLayer(this.markers[i]);\r\n"+
            "	this.markers=[];\r\n"+
            "}\r\n"+
            "MapBoxPortlet.prototype.addPointsMapBox = function(options){\r\n"+
            "	if(this.map != null){\r\n"+
            "	    var group=this.layerGroup;\r\n"+
            "		var map=this.map;\r\n"+
            "	    var lats=options.lats;\r\n"+
            "	    var lons=options.lons;\r\n"+
            "		var layerId=options.layerId;\r\n"+
            "	    var borderColors=options.borderColors;\r\n"+
            "	    var colors=options.colors;\r\n"+
            "	    var opacities=options.opacities;\r\n"+
            "	    var sizes=options.sizes;\r\n"+
            "	    var labels=options.labels;\r\n"+
            "	    var labelLimit=options.labelLimit;\r\n"+
            "	    var labelFontFamilies=options.labelFontFamilies;\r\n"+
            "	    var labelFontSizes=options.labelFontSizes;\r\n"+
            "	    var labelFontColors=options.labelFontColors;\r\n"+
            "	    var labelPositions=options.labelPositions;\r\n"+
            "	    //var titles=options.titles;\r\n"+
            "	    var t=this.layersByLayerIdPointId[layerId];\r\n"+
            "	    if(t==null)\r\n"+
            "	      t=this.layersByLayerIdPointId[layerId]=[];\r\n"+
            "	    var cnt=lats.length;\r\n"+
            "	    // labels\r\n"+
            "	    this.clearExistingLabels();\r\n"+
            "		for(var i=0;i<cnt;i++){\r\n"+
            "		  var layer=L.circle([lats[i], lons[i]], sizes[i], {color: borderColors[i], fillColor: colors[i], fillOpacity: opacities[i],weight:2});\r\n"+
            "		  if (i < labelLimit && labels[i] != null) {\r\n"+
            "		  	var labelStyles = {};\r\n"+
            "		  	labelStyles.labelFontFam=labelFontFamilies[i];\r\n"+
            "		  	labelStyles.labelFontSz=labelFontSizes[i] ;\r\n"+
            "		  	labelStyles.labelFontCl=labelFontColors[i];\r\n"+
            "		  	labelStyles.labelPos=labelPositions[i];\r\n"+
            "		  	this.addLabel(lats[i], lons[i], labels[i], labelStyles);\r\n"+
            "		  }\r\n"+
            "		  layer.addTo(map);\r\n"+
            "		  layer.amiPointId=i;\r\n"+
            "		  layer.amiLayerId=layerId;\r\n"+
            "		  t[i]=layer;\r\n"+
            "		  layer.on('mouseover',onMapBoxPointMouseover);\r\n"+
            "		  layer.on('mouseout',onMapBoxPointMouseout);\r\n"+
            "		  layer.on('click',onMapBoxPointClick);\r\n"+
            "		  group.addLayer(layer);\r\n"+
            "//		  log(\"added layer, \"+group.getLayers().length);\r\n"+
            "		}\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "MapBoxPortlet.prototype.addLabel=function(lat, lon, labelContent, labelStyles){\r\n"+
            "	var position = labelStyles.labelPos;\r\n"+
            "	var posCssStyle = \"margin-top:7px !important\" // default position\r\n"+
            "	if (position == \"center\")\r\n"+
            "		posCssStyle = \"margin:0px !important\";\r\n"+
            "	else if (position == \"top\")\r\n"+
            "		posCssStyle = \"margin-top:-20px !important\";\r\n"+
            "	 \r\n"+
            "	var style =\"font-family:\" + labelStyles.labelFontFam + \"; font-size:\" + labelStyles.labelFontSz + \"px; color:\" + labelStyles.labelFontCl + \"; \" + posCssStyle;\r\n"+
            "	var marker = L.marker([lat,lon], {\r\n"+
            "	  icon: L.divIcon({\r\n"+
            "	      className: 'map-label', \r\n"+
            "	      html: '<div style=\"'+ style + '\">' + labelContent + '</div>'\r\n"+
            "	  }),\r\n"+
            "	  zIndexOffset: 1000 \r\n"+
            "	});\r\n"+
            "	marker.addTo(this.map);\r\n"+
            "	this.markers.push(marker);\r\n"+
            "	\r\n"+
            "    /* another method to add label (need to extend addLayer)\r\n"+
            "    var labelLocation = new L.LatLng(lat, lon);\r\n"+
            "    var label = new L.LabelOverlay(labelLocation, labelContent);\r\n"+
            "    this.map.addLayer(label);\r\n"+
            "    */\r\n"+
            "}\r\n"+
            "function onMapBoxPointMouseover(e){\r\n"+
            "	var portletId=e.target._map.amiMapId;\r\n"+
            "	var pointId=e.target.amiPointId;\r\n"+
            "	var portlet=getPortletManager().getPortletNoThrow(portletId);\r\n"+
            "    portlet.hoverTimer=setTimeout(function(){onMapBoxPointHover(e);}, 150);\r\n"+
            "}\r\n"+
            "function onMapBoxPointMouseout(e){\r\n"+
            "	var portletId=e.target._map.amiMapId;\r\n"+
            "	var pointId=e.target.amiPointId;\r\n"+
            "	var portlet=getPortletManager().getPortletNoThrow(portletId);\r\n"+
            "	if(portlet.hoverTimer!=null){\r\n"+
            "		clearTimeout(portlet.hoverTimer);\r\n"+
            "		portlet.hoverTimer=null;\r\n"+
            "	    portlet.hoverRequestLayerId=null;\r\n"+
            "	    portlet.hoverRequestPointId=null;\r\n"+
            "	}\r\n"+
            "	    if(portlet.tooltipDiv!=null){\r\n"+
            "	       portlet.divElement.removeChild(portlet.tooltipDiv);\r\n"+
            "	       portlet.tooltipDiv=null;\r\n"+
            "	    }\r\n"+
            "}\r\n"+
            "function onMapBoxPointHover(e){\r\n"+
            "	if(e.target._map==null)\r\n"+
            "		return;\r\n"+
            "	var portletId=e.target._map.amiMapId;\r\n"+
            "	var pointId=e.target.amiPointId;\r\n"+
            "	var layerId=e.target.amiLayerId;\r\n"+
            "	var portlet=getPortletManager().getPortletNoThrow(portletId);\r\n"+
            "	var point=getMousePointRelativeTo(e.originalEvent,portlet.divElement);\r\n"+
            "	portlet.hoverRequestLayerId=layerId;\r\n"+
            "	portlet.hoverRequestPointId=pointId;\r\n"+
            "	portlet.hoverTimer=null;\r\n"+
            "    portlet.callBack('pointHover',{pointId:pointId,layerId:layerId,x:point.x,y:point.y});\r\n"+
            "}\r\n"+
            "function onMapBoxPointClick(e,e2){\r\n"+
            "    var shiftKey=e.originalEvent.shiftKey;\r\n"+
            "    var ctrlKey=e.originalEvent.ctrlKey;\r\n"+
            "	var layer=e.target;\r\n"+
            "	var portletId=layer._map.amiMapId;\r\n"+
            "	var portlet=getPortletManager().getPortletNoThrow(portletId);\r\n"+
            "    var group=portlet.layerGroup;\r\n"+
            "	var pointId=layer.amiPointId;\r\n"+
            "	var layerId=layer.amiLayerId;\r\n"+
            "//	layer.setStyle({fillColor:\"#AABBCC\"});\r\n"+
            "//		  log(\"1added layer, \"+group.getLayers().length);\r\n"+
            "	//layer._map.removeLayer(e.target);\r\n"+
            "	\r\n"+
            "    //group=portlet.layerGroup;\r\n"+
            "	//group.removeLayer(e.target);\r\n"+
            "	//group.addLayer(e.target);\r\n"+
            "//		  log(\"2added layer, \"+group.getLayers().length);\r\n"+
            "	//group.addLayer(e.target);\r\n"+
            "//		  log(\"3added layer, \"+group.getLayers().length);\r\n"+
            "	portlet.map.invalidateSize();\r\n"+
            "    portlet.callBack('pointClicked',{pointId:pointId,layerId:layerId,shiftKey:shiftKey,ctrlKey:ctrlKey});\r\n"+
            "}\r\n"+
            "\r\n"+
            "MapBoxPortlet.prototype.fitPointsMapBox=function(){\r\n"+
            "	if(this.map != null){\r\n"+
            "	    var group=this.layerGroup;\r\n"+
            "		var map=this.map;\r\n"+
            "	    if(group.getLayers().length>0)\r\n"+
            "		    map.fitBounds(group.getBounds());\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "MapBoxPortlet.prototype.setHoverMapBox=function(x,y,layerId,pointId,name){\r\n"+
            "	if(this.hoverRequestLayerId==layerId && this.hoverRequestPointId==pointId){\r\n"+
            "	    this.hoverRequestLayerId=null;\r\n"+
            "	    this.hoverRequestPointId=null;\r\n"+
            "	    if(this.tooltipDiv!=null)\r\n"+
            "	       this.divElement.removeChild(this.tooltipDiv);\r\n"+
            "         this.tooltipDiv=nw(\"div\",\"ami_chart_tooltip\");\r\n"+
            "	     var div=this.tooltipDiv;\r\n"+
            "	    div.innerHTML=name;\r\n"+
            "	    if(div.firstChild!=null && div.firstChild.tagName=='DIV'){\r\n"+
            "		    this.tooltipDiv=div.firstChild;\r\n"+
            "	        div=this.tooltipDiv;\r\n"+
            "	     }\r\n"+
            "	     this.divElement.appendChild(div);\r\n"+
            "	     div.style.left=toPx(x);\r\n"+
            "	     div.style.top=toPx(y-new Rect().readFromElement(div).height);\r\n"+
            "	     ensureInDiv(div,this.divElement);\r\n"+
            "  }\r\n"+
            "}\r\n"+
            "\r\n"+
            "!function loadLeaflet(t, e, i) {\r\n"+
            "    var n = t.L,\r\n"+
            "        o = {};\r\n"+
            "    o.version = \"0.7.7\", \"object\" == typeof module && \"object\" == typeof module.exports ? module.exports = o : \"function\" == typeof define && define.amd && define(o), o.noConflict = function() {\r\n"+
            "            return t.L = n, this\r\n"+
            "        }, t.L = o, o.Util = {\r\n"+
            "            extend: function(t) {\r\n"+
            "                var e, i, n, o, s = Array.prototype.slice.call(arguments, 1);\r\n"+
            "                for (i = 0, n = s.length; n > i; i++) {\r\n"+
            "                    o = s[i] || {};\r\n"+
            "                    for (e in o) o.hasOwnProperty(e) && (t[e] = o[e])\r\n"+
            "                }\r\n"+
            "                return t\r\n"+
            "            },\r\n"+
            "            bind: function(t, e) {\r\n"+
            "                var i = arguments.length > 2 ? Array.prototype.slice.call(arguments, 2) : null;\r\n"+
            "                return function() {\r\n"+
            "                    return t.apply(e, i || arguments)\r\n"+
            "                }\r\n"+
            "            },\r\n"+
            "            stamp: function() {\r\n"+
            "                var t = 0,\r\n"+
            "                    e = \"_leaflet_id\";\r\n"+
            "                return function(i) {\r\n"+
            "                    return i[e] = i[e] || ++t, i[e]\r\n"+
            "                }\r\n"+
            "            }(),\r\n"+
            "            invokeEach: function(t, e, i) {\r\n"+
            "                var n, o;\r\n"+
            "                if (\"object\" == typeof t");
          out.print(
            ") {\r\n"+
            "                    o = Array.prototype.slice.call(arguments, 3);\r\n"+
            "                    for (n in t) e.apply(i, [n, t[n]].concat(o));\r\n"+
            "                    return !0\r\n"+
            "                }\r\n"+
            "                return !1\r\n"+
            "            },\r\n"+
            "            limitExecByInterval: function(t, e, i) {\r\n"+
            "                var n, o;\r\n"+
            "                return function s() {\r\n"+
            "                    var a = arguments;\r\n"+
            "                    return n ? void(o = !0) : (n = !0, setTimeout(function() {\r\n"+
            "                        n = !1, o && (s.apply(i, a), o = !1)\r\n"+
            "                    }, e), void t.apply(i, a))\r\n"+
            "                }\r\n"+
            "            },\r\n"+
            "            falseFn: function() {\r\n"+
            "                return !1\r\n"+
            "            },\r\n"+
            "            formatNum: function(t, e) {\r\n"+
            "                var i = Math.pow(10, e || 5);\r\n"+
            "                return Math.round(t * i) / i\r\n"+
            "            },\r\n"+
            "            trim: function(t) {\r\n"+
            "                return t.trim ? t.trim() : t.replace(/^\\s+|\\s+$/g, \"\")\r\n"+
            "            },\r\n"+
            "            splitWords: function(t) {\r\n"+
            "                return o.Util.trim(t).split(/\\s+/)\r\n"+
            "            },\r\n"+
            "            setOptions: function(t, e) {\r\n"+
            "                return t.options = o.extend({}, t.options, e), t.options\r\n"+
            "            },\r\n"+
            "            getParamString: function(t, e, i) {\r\n"+
            "                var n = [];\r\n"+
            "                for (var o in t) n.push(encodeURIComponent(i ? o.toUpperCase() : o) + \"=\" + encodeURIComponent(t[o]));\r\n"+
            "                return (e && -1 !== e.indexOf(\"?\") ? \"&\" : \"?\") + n.join(\"&\")\r\n"+
            "            },\r\n"+
            "            template: function(t, e) {\r\n"+
            "                return t.replace(/\\{ *([\\w_]+) *\\}/g, function(t, n) {\r\n"+
            "                    var o = e[n];\r\n"+
            "                    if (o === i) throw new Error(\"No value provided for variable \" + t);\r\n"+
            "                    return \"function\" == typeof o && (o = o(e)), o\r\n"+
            "                })\r\n"+
            "            },\r\n"+
            "            isArray: Array.isArray || function(t) {\r\n"+
            "                return \"[object Array]\" === Object.prototype.toString.call(t)\r\n"+
            "            },\r\n"+
            "            emptyImageUrl: \"data:image/gif;base64,R0lGODlhAQABAAD/ACwAAAAAAQABAAACADs=\"\r\n"+
            "        },\r\n"+
            "        function() {\r\n"+
            "            function e(e) {\r\n"+
            "                var i, n, o = [\"webkit\", \"moz\", \"o\", \"ms\"];\r\n"+
            "                for (i = 0; i < o.length && !n; i++) n = t[o[i] + e];\r\n"+
            "                return n\r\n"+
            "            }\r\n"+
            "\r\n"+
            "            function i(e) {\r\n"+
            "                var i = +new Date,\r\n"+
            "                    o = Math.max(0, 16 - (i - n));\r\n"+
            "                return n = i + o, t.setTimeout(e, o)\r\n"+
            "            }\r\n"+
            "            var n = 0,\r\n"+
            "                s = t.requestAnimationFrame || e(\"RequestAnimationFrame\") || i,\r\n"+
            "                a = t.cancelAnimationFrame || e(\"CancelAnimationFrame\") || e(\"CancelRequestAnimationFrame\") || function(e) {\r\n"+
            "                    t.clearTimeout(e)\r\n"+
            "                };\r\n"+
            "            o.Util.requestAnimFrame = function(e, n, a, r) {\r\n"+
            "                return e = o.bind(e, n), a && s === i ? void e() : s.call(t, e, r)\r\n"+
            "            }, o.Util.cancelAnimFrame = function(e) {\r\n"+
            "                e && a.call(t, e)\r\n"+
            "            }\r\n"+
            "        }(), o.extend = o.Util.extend, o.bind = o.Util.bind, o.stamp = o.Util.stamp, o.setOptions = o.Util.setOptions, o.Class = function() {}, o.Class.extend = function(t) {\r\n"+
            "            var e = function() {\r\n"+
            "                    this.initialize && this.initialize.apply(this, arguments), this._initHooks && this.callInitHooks()\r\n"+
            "                },\r\n"+
            "                i = function() {};\r\n"+
            "            i.prototype = this.prototype;\r\n"+
            "            var n = new i;\r\n"+
            "            n.constructor = e, e.prototype = n;\r\n"+
            "            for (var s in this) this.hasOwnProperty(s) && \"prototype\" !== s && (e[s] = this[s]);\r\n"+
            "            t.statics && (o.extend(e, t.statics), delete t.statics), t.includes && (o.Util.extend.apply(null, [n].concat(t.includes)), delete t.includes), t.options && n.options && (t.options = o.extend({}, n.options, t.options)), o.extend(n, t), n._initHooks = [];\r\n"+
            "            var a = this;\r\n"+
            "            return e.__super__ = a.prototype, n.callInitHooks = function() {\r\n"+
            "                if (!this._initHooksCalled) {\r\n"+
            "                    a.prototype.callInitHooks && a.prototype.callInitHooks.call(this), this._initHooksCalled = !0;\r\n"+
            "                    for (var t = 0, e = n._initHooks.length; e > t; t++) n._initHooks[t].call(this)\r\n"+
            "                }\r\n"+
            "            }, e\r\n"+
            "        }, o.Class.include = function(t) {\r\n"+
            "            o.extend(this.prototype, t)\r\n"+
            "        }, o.Class.mergeOptions = function(t) {\r\n"+
            "            o.extend(this.prototype.options, t)\r\n"+
            "        }, o.Class.addInitHook = function(t) {\r\n"+
            "            var e = Array.prototype.slice.call(arguments, 1),\r\n"+
            "                i = \"function\" == typeof t ? t : function() {\r\n"+
            "                    this[t].apply(this, e)\r\n"+
            "                };\r\n"+
            "            this.prototype._initHooks = this.prototype._initHooks || [], this.prototype._initHooks.push(i)\r\n"+
            "        };\r\n"+
            "    var s = \"_leaflet_events\";\r\n"+
            "    o.Mixin = {}, o.Mixin.Events = {\r\n"+
            "            addEventListener: function(t, e, i) {\r\n"+
            "                if (o.Util.invokeEach(t, this.addEventListener, this, e, i)) return this;\r\n"+
            "                var n, a, r, h, l, u, c, d = this[s] = this[s] || {},\r\n"+
            "                    p = i && i !== this && o.stamp(i);\r\n"+
            "                for (t = o.Util.splitWords(t), n = 0, a = t.length; a > n; n++) r = {\r\n"+
            "                    action: e,\r\n"+
            "                    context: i || this\r\n"+
            "                }, h = t[n], p ? (l = h + \"_idx\", u = l + \"_len\", c = d[l] = d[l] || {}, c[p] || (c[p] = [], d[u] = (d[u] || 0) + 1), c[p].push(r)) : (d[h] = d[h] || [], d[h].push(r));\r\n"+
            "                return this\r\n"+
            "            },\r\n"+
            "            hasEventListeners: function(t) {\r\n"+
            "                var e = this[s];\r\n"+
            "                return !!e && (t in e && e[t].length > 0 || t + \"_idx\" in e && e[t + \"_idx_len\"] > 0)\r\n"+
            "            },\r\n"+
            "            removeEventListener: function(t, e, i) {\r\n"+
            "                if (!this[s]) return this;\r\n"+
            "                if (!t) return this.clearAllEventListeners();\r\n"+
            "                if (o.Util.invokeEach(t, this.removeEventListener, this, e, i)) return this;\r\n"+
            "                var n, a, r, h, l, u, c, d, p, _ = this[s],\r\n"+
            "                    m = i && i !== this && o.stamp(i);\r\n"+
            "                for (t = o.Util.splitWords(t), n = 0, a = t.length; a > n; n++)\r\n"+
            "                    if (r = t[n], u = r + \"_idx\", c = u + \"_len\", d = _[u], e) {\r\n"+
            "                        if (h = m && d ? d[m] : _[r]) {\r\n"+
            "                            for (l = h.length - 1; l >= 0; l--) h[l].action !== e || i && h[l].context !== i || (p = h.splice(l, 1), p[0].action = o.Util.falseFn);\r\n"+
            "                            i && d && 0 === h.length && (delete d[m], _[c]--)\r\n"+
            "                        }\r\n"+
            "                    } else delete _[r], delete _[u], delete _[c];\r\n"+
            "                return this\r\n"+
            "            },\r\n"+
            "            clearAllEventListeners: function() {\r\n"+
            "                return delete this[s], this\r\n"+
            "            },\r\n"+
            "            fireEvent: function(t, e) {\r\n"+
            "                if (!this.hasEventListeners(t)) return this;\r\n"+
            "                var i, n, a, r, h, l = o.Util.extend({}, e, {\r\n"+
            "                        type: t,\r\n"+
            "                        target: this\r\n"+
            "                    }),\r\n"+
            "                    u = this[s];\r\n"+
            "                if (u[t])\r\n"+
            "                    for (i = u[t].slice(), n = 0, a = i.length; a > n; n++) i[n].action.call(i[n].context, l);\r\n"+
            "                r = u[t + \"_idx\"];\r\n"+
            "                for (h in r)\r\n"+
            "                    if (i = r[h].slice())\r\n"+
            "                        for (n = 0, a = i.length; a > n; n++) i[n].action.call(i[n].context, l);\r\n"+
            "                return this\r\n"+
            "            },\r\n"+
            "            addOneTimeEventListener: function(t, e, i) {\r\n"+
            "                if (o.Util.invokeEach(t, this.addOneTimeEventListener, this, e, i)) return this;\r\n"+
            "                var n = o.bind(function() {\r\n"+
            "                    this.removeEventListener(t, e, i).removeEventListener(t, n, i)\r\n"+
            "                }, this);\r\n"+
            "                return this.addEventListener(t, e, i).addEventListener(t, n, i)\r\n"+
            "            }\r\n"+
            "        }, o.Mixin.Events.on = o.Mixin.Events.addEventListener, o.Mixin.Events.off = o.Mixin.Events.removeEventListener, o.Mixin.Events.once = o.Mixin.Events.addOneTimeEventListener, o.Mixin.Events.fire = o.Mixin.Events.fireEvent,\r\n"+
            "        function() {\r\n"+
            "            var n = \"ActiveXObject\" in t,\r\n"+
            "                s = n && !e.addEventListener,\r\n"+
            "                a = navigator.userAgent.toLowerCase(),\r\n"+
            "                r = -1 !== a.indexOf(\"webkit\"),\r\n"+
            "                h = -1 !== a.indexOf(\"chrome\"),\r\n"+
            "                l = -1 !== a.indexOf(\"phantom\"),\r\n"+
            "                u = -1 !== a.indexOf(\"android\"),\r\n"+
            "                c = -1 !== a.search(\"android [23]\"),\r\n"+
            "                d = -1 !== a.indexOf(\"gecko\"),\r\n"+
            "                p = typeof orientation != i + \"\",\r\n"+
            "                _ = !t.PointerEvent && t.MSPointerEvent,\r\n"+
            "                m = t.PointerEvent && t.navigator.pointerEnabled || _,\r\n"+
            "                f = \"devicePixelRatio\" in t && t.devicePixelRatio > 1 || \"matchMedia\" in t && t.matchMedia(\"(min-resolution:144dpi)\") && t.matchMedia(\"(min-resolution:144dpi)\").matches,\r\n"+
            "                g = e.documentElement,\r\n"+
            "                v = n && \"transition\" in g.style,\r\n"+
            "                y = \"WebKitCSSMatrix\" in t && \"m11\" in new t.WebKitCSSMatrix && !c,\r\n"+
            "                P = \"MozPerspective\" in g.style,\r\n"+
            "                L = \"OTransition\" in g.style,\r\n"+
            "                x = !t.L_DISABLE_3D && (v || y || P || L) && !l,\r\n"+
            "                w = !t.L_NO_TOUCH && !l && (m || \"ontouchstart\" in t || t.DocumentTouch && e instanceof t.DocumentTouch);\r\n"+
            "            o.Browser = {\r\n"+
            "                ie: n,\r\n"+
            "                ielt9: s,\r\n"+
            "                webkit: r,\r\n"+
            "                gecko: d && !r && !t.opera && !n,\r\n"+
            "                android: u,\r\n"+
            "                android23: c,\r\n"+
            "                chrome: h,\r\n"+
            "                ie3d: v,\r\n"+
            "                webkit3d: y,\r\n"+
            "                gecko3d: P,\r\n"+
            "                opera3d: L,\r\n"+
            "                any3d: x,\r\n"+
            "                mobile: p,\r\n"+
            "                mobileWebkit: p && r,\r\n"+
            "                mobileWebkit3d: p && y,\r\n"+
            "       ");
          out.print(
            "         mobileOpera: p && t.opera,\r\n"+
            "                touch: w,\r\n"+
            "                msPointer: _,\r\n"+
            "                pointer: m,\r\n"+
            "                retina: f\r\n"+
            "            }\r\n"+
            "        }(), o.Point = function(t, e, i) {\r\n"+
            "            this.x = i ? Math.round(t) : t, this.y = i ? Math.round(e) : e\r\n"+
            "        }, o.Point.prototype = {\r\n"+
            "            clone: function() {\r\n"+
            "                return new o.Point(this.x, this.y)\r\n"+
            "            },\r\n"+
            "            add: function(t) {\r\n"+
            "                return this.clone()._add(o.point(t))\r\n"+
            "            },\r\n"+
            "            _add: function(t) {\r\n"+
            "                return this.x += t.x, this.y += t.y, this\r\n"+
            "            },\r\n"+
            "            subtract: function(t) {\r\n"+
            "                return this.clone()._subtract(o.point(t))\r\n"+
            "            },\r\n"+
            "            _subtract: function(t) {\r\n"+
            "                return this.x -= t.x, this.y -= t.y, this\r\n"+
            "            },\r\n"+
            "            divideBy: function(t) {\r\n"+
            "                return this.clone()._divideBy(t)\r\n"+
            "            },\r\n"+
            "            _divideBy: function(t) {\r\n"+
            "                return this.x /= t, this.y /= t, this\r\n"+
            "            },\r\n"+
            "            multiplyBy: function(t) {\r\n"+
            "                return this.clone()._multiplyBy(t)\r\n"+
            "            },\r\n"+
            "            _multiplyBy: function(t) {\r\n"+
            "                return this.x *= t, this.y *= t, this\r\n"+
            "            },\r\n"+
            "            round: function() {\r\n"+
            "                return this.clone()._round()\r\n"+
            "            },\r\n"+
            "            _round: function() {\r\n"+
            "                return this.x = Math.round(this.x), this.y = Math.round(this.y), this\r\n"+
            "            },\r\n"+
            "            floor: function() {\r\n"+
            "                return this.clone()._floor()\r\n"+
            "            },\r\n"+
            "            _floor: function() {\r\n"+
            "                return this.x = Math.floor(this.x), this.y = Math.floor(this.y), this\r\n"+
            "            },\r\n"+
            "            distanceTo: function(t) {\r\n"+
            "                t = o.point(t);\r\n"+
            "                var e = t.x - this.x,\r\n"+
            "                    i = t.y - this.y;\r\n"+
            "                return Math.sqrt(e * e + i * i)\r\n"+
            "            },\r\n"+
            "            equals: function(t) {\r\n"+
            "                return t = o.point(t), t.x === this.x && t.y === this.y\r\n"+
            "            },\r\n"+
            "            contains: function(t) {\r\n"+
            "                return t = o.point(t), Math.abs(t.x) <= Math.abs(this.x) && Math.abs(t.y) <= Math.abs(this.y)\r\n"+
            "            },\r\n"+
            "            toString: function() {\r\n"+
            "                return \"Point(\" + o.Util.formatNum(this.x) + \", \" + o.Util.formatNum(this.y) + \")\"\r\n"+
            "            }\r\n"+
            "        }, o.point = function(t, e, n) {\r\n"+
            "            return t instanceof o.Point ? t : o.Util.isArray(t) ? new o.Point(t[0], t[1]) : t === i || null === t ? t : new o.Point(t, e, n)\r\n"+
            "        }, o.Bounds = function(t, e) {\r\n"+
            "            if (t)\r\n"+
            "                for (var i = e ? [t, e] : t, n = 0, o = i.length; o > n; n++) this.extend(i[n])\r\n"+
            "        }, o.Bounds.prototype = {\r\n"+
            "            extend: function(t) {\r\n"+
            "                return t = o.point(t), this.min || this.max ? (this.min.x = Math.min(t.x, this.min.x), this.max.x = Math.max(t.x, this.max.x), this.min.y = Math.min(t.y, this.min.y), this.max.y = Math.max(t.y, this.max.y)) : (this.min = t.clone(), this.max = t.clone()), this\r\n"+
            "            },\r\n"+
            "            getCenter: function(t) {\r\n"+
            "                return new o.Point((this.min.x + this.max.x) / 2, (this.min.y + this.max.y) / 2, t)\r\n"+
            "            },\r\n"+
            "            getBottomLeft: function() {\r\n"+
            "                return new o.Point(this.min.x, this.max.y)\r\n"+
            "            },\r\n"+
            "            getTopRight: function() {\r\n"+
            "                return new o.Point(this.max.x, this.min.y)\r\n"+
            "            },\r\n"+
            "            getSize: function() {\r\n"+
            "                return this.max.subtract(this.min)\r\n"+
            "            },\r\n"+
            "            contains: function(t) {\r\n"+
            "                var e, i;\r\n"+
            "                return t = \"number\" == typeof t[0] || t instanceof o.Point ? o.point(t) : o.bounds(t), t instanceof o.Bounds ? (e = t.min, i = t.max) : e = i = t, e.x >= this.min.x && i.x <= this.max.x && e.y >= this.min.y && i.y <= this.max.y\r\n"+
            "            },\r\n"+
            "            intersects: function(t) {\r\n"+
            "                t = o.bounds(t);\r\n"+
            "                var e = this.min,\r\n"+
            "                    i = this.max,\r\n"+
            "                    n = t.min,\r\n"+
            "                    s = t.max,\r\n"+
            "                    a = s.x >= e.x && n.x <= i.x,\r\n"+
            "                    r = s.y >= e.y && n.y <= i.y;\r\n"+
            "                return a && r\r\n"+
            "            },\r\n"+
            "            isValid: function() {\r\n"+
            "                return !(!this.min || !this.max)\r\n"+
            "            }\r\n"+
            "        }, o.bounds = function(t, e) {\r\n"+
            "            return !t || t instanceof o.Bounds ? t : new o.Bounds(t, e)\r\n"+
            "        }, o.Transformation = function(t, e, i, n) {\r\n"+
            "            this._a = t, this._b = e, this._c = i, this._d = n\r\n"+
            "        }, o.Transformation.prototype = {\r\n"+
            "            transform: function(t, e) {\r\n"+
            "                return this._transform(t.clone(), e)\r\n"+
            "            },\r\n"+
            "            _transform: function(t, e) {\r\n"+
            "                return e = e || 1, t.x = e * (this._a * t.x + this._b), t.y = e * (this._c * t.y + this._d), t\r\n"+
            "            },\r\n"+
            "            untransform: function(t, e) {\r\n"+
            "                return e = e || 1, new o.Point((t.x / e - this._b) / this._a, (t.y / e - this._d) / this._c)\r\n"+
            "            }\r\n"+
            "        }, o.DomUtil = {\r\n"+
            "            get: function(t,doc) {\r\n"+
            "            	if(doc != null)\r\n"+
            "            		return \"string\" == typeof t ? doc.getElementById(t) : t\r\n"+
            "            	else\r\n"+
            "            		return \"string\" == typeof t ? e.getElementById(t) : t\r\n"+
            "            },\r\n"+
            "            getStyle: function(t, i) {\r\n"+
            "                var n = t.style[i];\r\n"+
            "                if (!n && t.currentStyle && (n = t.currentStyle[i]), (!n || \"auto\" === n) && e.defaultView) {\r\n"+
            "                    var o = e.defaultView.getComputedStyle(t, null);\r\n"+
            "                    n = o ? o[i] : null\r\n"+
            "                }\r\n"+
            "                return \"auto\" === n ? null : n\r\n"+
            "            },\r\n"+
            "            getViewportOffset: function(t) {\r\n"+
            "                var i, n = 0,\r\n"+
            "                    s = 0,\r\n"+
            "                    a = t,\r\n"+
            "                    r = e.body,\r\n"+
            "                    h = e.documentElement;\r\n"+
            "                do {\r\n"+
            "                    if (n += a.offsetTop || 0, s += a.offsetLeft || 0, n += parseInt(o.DomUtil.getStyle(a, \"borderTopWidth\"), 10) || 0, s += parseInt(o.DomUtil.getStyle(a, \"borderLeftWidth\"), 10) || 0, i = o.DomUtil.getStyle(a, \"position\"), a.offsetParent === r && \"absolute\" === i) break;\r\n"+
            "                    if (\"fixed\" === i) {\r\n"+
            "                        n += r.scrollTop || h.scrollTop || 0, s += r.scrollLeft || h.scrollLeft || 0;\r\n"+
            "                        break\r\n"+
            "                    }\r\n"+
            "                    if (\"relative\" === i && !a.offsetLeft) {\r\n"+
            "                        var l = o.DomUtil.getStyle(a, \"width\"),\r\n"+
            "                            u = o.DomUtil.getStyle(a, \"max-width\"),\r\n"+
            "                            c = a.getBoundingClientRect();\r\n"+
            "                        (\"none\" !== l || \"none\" !== u) && (s += c.left + a.clientLeft), n += c.top + (r.scrollTop || h.scrollTop || 0);\r\n"+
            "                        break\r\n"+
            "                    }\r\n"+
            "                    a = a.offsetParent\r\n"+
            "                } while (a);\r\n"+
            "                a = t;\r\n"+
            "                do {\r\n"+
            "                    if (a === r) break;\r\n"+
            "                    n -= a.scrollTop || 0, s -= a.scrollLeft || 0, a = a.parentNode\r\n"+
            "                } while (a);\r\n"+
            "                return new o.Point(s, n)\r\n"+
            "            },\r\n"+
            "            documentIsLtr: function() {\r\n"+
            "                return o.DomUtil._docIsLtrCached || (o.DomUtil._docIsLtrCached = !0, o.DomUtil._docIsLtr = \"ltr\" === o.DomUtil.getStyle(e.body, \"direction\")), o.DomUtil._docIsLtr\r\n"+
            "            },\r\n"+
            "            create: function(t, i, n) {\r\n"+
            "                var o = e.createElement(t);\r\n"+
            "                return o.className = i, n && n.appendChild(o), o\r\n"+
            "            },\r\n"+
            "            hasClass: function(t, e) {\r\n"+
            "                if (t.classList !== i) return t.classList.contains(e);\r\n"+
            "                var n = o.DomUtil._getClass(t);\r\n"+
            "                return n.length > 0 && new RegExp(\"(^|\\\\s)\" + e + \"(\\\\s|$)\").test(n)\r\n"+
            "            },\r\n"+
            "            addClass: function(t, e) {\r\n"+
            "                if (t.classList !== i)\r\n"+
            "                    for (var n = o.Util.splitWords(e), s = 0, a = n.length; a > s; s++) t.classList.add(n[s]);\r\n"+
            "                else if (!o.DomUtil.hasClass(t, e)) {\r\n"+
            "                    var r = o.DomUtil._getClass(t);\r\n"+
            "                    o.DomUtil._setClass(t, (r ? r + \" \" : \"\") + e)\r\n"+
            "                }\r\n"+
            "            },\r\n"+
            "            removeClass: function(t, e) {\r\n"+
            "                t.classList !== i ? t.classList.remove(e) : o.DomUtil._setClass(t, o.Util.trim((\" \" + o.DomUtil._getClass(t) + \" \").replace(\" \" + e + \" \", \" \")))\r\n"+
            "            },\r\n"+
            "            _setClass: function(t, e) {\r\n"+
            "                t.className.baseVal === i ? t.className = e : t.className.baseVal = e\r\n"+
            "            },\r\n"+
            "            _getClass: function(t) {\r\n"+
            "                return t.className.baseVal === i ? t.className : t.className.baseVal\r\n"+
            "            },\r\n"+
            "            setOpacity: function(t, e) {\r\n"+
            "                if (\"opacity\" in t.style) t.style.opacity = e;\r\n"+
            "                else if (\"filter\" in t.style) {\r\n"+
            "                    var i = !1,\r\n"+
            "                        n = \"DXImageTransform.Microsoft.Alpha\";\r\n"+
            "                    try {\r\n"+
            "                        i = t.filters.item(n)\r\n"+
            "                    } catch (o) {\r\n"+
            "                        if (1 === e) return\r\n"+
            "                    }\r\n"+
            "                    e = Math.round(100 * e), i ? (i.Enabled = 100 !== e, i.Opacity = e) : t.style.filter += \" progid:\" + n + \"(opacity=\" + e + \")\"\r\n"+
            "                }\r\n"+
            "            },\r\n"+
            "            testProp: function(t) {\r\n"+
            "                for (var i = e.documentElement.style, n = 0; n < t.length; n++)\r\n"+
            "                    if (t[n] in i) return t[n];\r\n"+
            "                return !1\r\n"+
            "            },\r\n"+
            "            getTranslateString: function(t) {\r\n"+
            "                var e = o.Browser.webkit3d,\r\n"+
            "                    i = \"translate\" + (e ? \"3d\" : \"\") + \"(\",\r\n"+
            "                    n = (e ? \",0\" : \"\") + \")\";\r\n"+
            "                return i + t.x + \"px,\" + t.y + \"px\" + n\r\n"+
            "            },\r\n"+
            "            getScaleString: function(t, e) {\r\n"+
            "                var i = o.Dom");
          out.print(
            "Util.getTranslateString(e.add(e.multiplyBy(-1 * t))),\r\n"+
            "                    n = \" scale(\" + t + \") \";\r\n"+
            "                return i + n\r\n"+
            "            },\r\n"+
            "            setPosition: function(t, e, i) {\r\n"+
            "                t._leaflet_pos = e, !i && o.Browser.any3d ? t.style[o.DomUtil.TRANSFORM] = o.DomUtil.getTranslateString(e) : (t.style.left = e.x + \"px\", t.style.top = e.y + \"px\")\r\n"+
            "            },\r\n"+
            "            getPosition: function(t) {\r\n"+
            "                return t._leaflet_pos\r\n"+
            "            }\r\n"+
            "        }, o.DomUtil.TRANSFORM = o.DomUtil.testProp([\"transform\", \"WebkitTransform\", \"OTransform\", \"MozTransform\", \"msTransform\"]), o.DomUtil.TRANSITION = o.DomUtil.testProp([\"webkitTransition\", \"transition\", \"OTransition\", \"MozTransition\", \"msTransition\"]), o.DomUtil.TRANSITION_END = \"webkitTransition\" === o.DomUtil.TRANSITION || \"OTransition\" === o.DomUtil.TRANSITION ? o.DomUtil.TRANSITION + \"End\" : \"transitionend\",\r\n"+
            "        function() {\r\n"+
            "            if (\"onselectstart\" in e) o.extend(o.DomUtil, {\r\n"+
            "                disableTextSelection: function() {\r\n"+
            "                    o.DomEvent.on(t, \"selectstart\", o.DomEvent.preventDefault)\r\n"+
            "                },\r\n"+
            "                enableTextSelection: function() {\r\n"+
            "                    o.DomEvent.off(t, \"selectstart\", o.DomEvent.preventDefault)\r\n"+
            "                }\r\n"+
            "            });\r\n"+
            "            else {\r\n"+
            "                var i = o.DomUtil.testProp([\"userSelect\", \"WebkitUserSelect\", \"OUserSelect\", \"MozUserSelect\", \"msUserSelect\"]);\r\n"+
            "                o.extend(o.DomUtil, {\r\n"+
            "                    disableTextSelection: function() {\r\n"+
            "                        if (i) {\r\n"+
            "                            var t = e.documentElement.style;\r\n"+
            "                            this._userSelect = t[i], t[i] = \"none\"\r\n"+
            "                        }\r\n"+
            "                    },\r\n"+
            "                    enableTextSelection: function() {\r\n"+
            "                        i && (e.documentElement.style[i] = this._userSelect, delete this._userSelect)\r\n"+
            "                    }\r\n"+
            "                })\r\n"+
            "            }\r\n"+
            "            o.extend(o.DomUtil, {\r\n"+
            "                disableImageDrag: function() {\r\n"+
            "                    o.DomEvent.on(t, \"dragstart\", o.DomEvent.preventDefault)\r\n"+
            "                },\r\n"+
            "                enableImageDrag: function() {\r\n"+
            "                    o.DomEvent.off(t, \"dragstart\", o.DomEvent.preventDefault)\r\n"+
            "                }\r\n"+
            "            })\r\n"+
            "        }(), o.LatLng = function(t, e, n) {\r\n"+
            "            if (t = parseFloat(t), e = parseFloat(e), isNaN(t) || isNaN(e)) throw new Error(\"Invalid LatLng object: (\" + t + \", \" + e + \")\");\r\n"+
            "            this.lat = t, this.lng = e, n !== i && (this.alt = parseFloat(n))\r\n"+
            "        }, o.extend(o.LatLng, {\r\n"+
            "            DEG_TO_RAD: Math.PI / 180,\r\n"+
            "            RAD_TO_DEG: 180 / Math.PI,\r\n"+
            "            MAX_MARGIN: 1e-9\r\n"+
            "        }), o.LatLng.prototype = {\r\n"+
            "            equals: function(t) {\r\n"+
            "                if (!t) return !1;\r\n"+
            "                t = o.latLng(t);\r\n"+
            "                var e = Math.max(Math.abs(this.lat - t.lat), Math.abs(this.lng - t.lng));\r\n"+
            "                return e <= o.LatLng.MAX_MARGIN\r\n"+
            "            },\r\n"+
            "            toString: function(t) {\r\n"+
            "                return \"LatLng(\" + o.Util.formatNum(this.lat, t) + \", \" + o.Util.formatNum(this.lng, t) + \")\"\r\n"+
            "            },\r\n"+
            "            distanceTo: function(t) {\r\n"+
            "                t = o.latLng(t);\r\n"+
            "                var e = 6378137,\r\n"+
            "                    i = o.LatLng.DEG_TO_RAD,\r\n"+
            "                    n = (t.lat - this.lat) * i,\r\n"+
            "                    s = (t.lng - this.lng) * i,\r\n"+
            "                    a = this.lat * i,\r\n"+
            "                    r = t.lat * i,\r\n"+
            "                    h = Math.sin(n / 2),\r\n"+
            "                    l = Math.sin(s / 2),\r\n"+
            "                    u = h * h + l * l * Math.cos(a) * Math.cos(r);\r\n"+
            "                return 2 * e * Math.atan2(Math.sqrt(u), Math.sqrt(1 - u))\r\n"+
            "            },\r\n"+
            "            wrap: function(t, e) {\r\n"+
            "                var i = this.lng;\r\n"+
            "                return t = t || -180, e = e || 180, i = (i + e) % (e - t) + (t > i || i === e ? e : t), new o.LatLng(this.lat, i)\r\n"+
            "            }\r\n"+
            "        }, o.latLng = function(t, e) {\r\n"+
            "            return t instanceof o.LatLng ? t : o.Util.isArray(t) ? \"number\" == typeof t[0] || \"string\" == typeof t[0] ? new o.LatLng(t[0], t[1], t[2]) : null : t === i || null === t ? t : \"object\" == typeof t && \"lat\" in t ? new o.LatLng(t.lat, \"lng\" in t ? t.lng : t.lon) : e === i ? null : new o.LatLng(t, e)\r\n"+
            "        }, o.LatLngBounds = function(t, e) {\r\n"+
            "            if (t)\r\n"+
            "                for (var i = e ? [t, e] : t, n = 0, o = i.length; o > n; n++) this.extend(i[n])\r\n"+
            "        }, o.LatLngBounds.prototype = {\r\n"+
            "            extend: function(t) {\r\n"+
            "                if (!t) return this;\r\n"+
            "                var e = o.latLng(t);\r\n"+
            "                return t = null !== e ? e : o.latLngBounds(t), t instanceof o.LatLng ? this._southWest || this._northEast ? (this._southWest.lat = Math.min(t.lat, this._southWest.lat), this._southWest.lng = Math.min(t.lng, this._southWest.lng), this._northEast.lat = Math.max(t.lat, this._northEast.lat), this._northEast.lng = Math.max(t.lng, this._northEast.lng)) : (this._southWest = new o.LatLng(t.lat, t.lng), this._northEast = new o.LatLng(t.lat, t.lng)) : t instanceof o.LatLngBounds && (this.extend(t._southWest), this.extend(t._northEast)), this\r\n"+
            "            },\r\n"+
            "            pad: function(t) {\r\n"+
            "                var e = this._southWest,\r\n"+
            "                    i = this._northEast,\r\n"+
            "                    n = Math.abs(e.lat - i.lat) * t,\r\n"+
            "                    s = Math.abs(e.lng - i.lng) * t;\r\n"+
            "                return new o.LatLngBounds(new o.LatLng(e.lat - n, e.lng - s), new o.LatLng(i.lat + n, i.lng + s))\r\n"+
            "            },\r\n"+
            "            getCenter: function() {\r\n"+
            "                return new o.LatLng((this._southWest.lat + this._northEast.lat) / 2, (this._southWest.lng + this._northEast.lng) / 2)\r\n"+
            "            },\r\n"+
            "            getSouthWest: function() {\r\n"+
            "                return this._southWest\r\n"+
            "            },\r\n"+
            "            getNorthEast: function() {\r\n"+
            "                return this._northEast\r\n"+
            "            },\r\n"+
            "            getNorthWest: function() {\r\n"+
            "                return new o.LatLng(this.getNorth(), this.getWest())\r\n"+
            "            },\r\n"+
            "            getSouthEast: function() {\r\n"+
            "                return new o.LatLng(this.getSouth(), this.getEast())\r\n"+
            "            },\r\n"+
            "            getWest: function() {\r\n"+
            "                return this._southWest.lng\r\n"+
            "            },\r\n"+
            "            getSouth: function() {\r\n"+
            "                return this._southWest.lat\r\n"+
            "            },\r\n"+
            "            getEast: function() {\r\n"+
            "                return this._northEast.lng\r\n"+
            "            },\r\n"+
            "            getNorth: function() {\r\n"+
            "                return this._northEast.lat\r\n"+
            "            },\r\n"+
            "            contains: function(t) {\r\n"+
            "                t = \"number\" == typeof t[0] || t instanceof o.LatLng ? o.latLng(t) : o.latLngBounds(t);\r\n"+
            "                var e, i, n = this._southWest,\r\n"+
            "                    s = this._northEast;\r\n"+
            "                return t instanceof o.LatLngBounds ? (e = t.getSouthWest(), i = t.getNorthEast()) : e = i = t, e.lat >= n.lat && i.lat <= s.lat && e.lng >= n.lng && i.lng <= s.lng\r\n"+
            "            },\r\n"+
            "            intersects: function(t) {\r\n"+
            "                t = o.latLngBounds(t);\r\n"+
            "                var e = this._southWest,\r\n"+
            "                    i = this._northEast,\r\n"+
            "                    n = t.getSouthWest(),\r\n"+
            "                    s = t.getNorthEast(),\r\n"+
            "                    a = s.lat >= e.lat && n.lat <= i.lat,\r\n"+
            "                    r = s.lng >= e.lng && n.lng <= i.lng;\r\n"+
            "                return a && r\r\n"+
            "            },\r\n"+
            "            toBBoxString: function() {\r\n"+
            "                return [this.getWest(), this.getSouth(), this.getEast(), this.getNorth()].join(\",\")\r\n"+
            "            },\r\n"+
            "            equals: function(t) {\r\n"+
            "                return t ? (t = o.latLngBounds(t), this._southWest.equals(t.getSouthWest()) && this._northEast.equals(t.getNorthEast())) : !1\r\n"+
            "            },\r\n"+
            "            isValid: function() {\r\n"+
            "                return !(!this._southWest || !this._northEast)\r\n"+
            "            }\r\n"+
            "        }, o.latLngBounds = function(t, e) {\r\n"+
            "            return !t || t instanceof o.LatLngBounds ? t : new o.LatLngBounds(t, e)\r\n"+
            "        }, o.Projection = {}, o.Projection.SphericalMercator = {\r\n"+
            "            MAX_LATITUDE: 85.0511287798,\r\n"+
            "            project: function(t) {\r\n"+
            "                var e = o.LatLng.DEG_TO_RAD,\r\n"+
            "                    i = this.MAX_LATITUDE,\r\n"+
            "                    n = Math.max(Math.min(i, t.lat), -i),\r\n"+
            "                    s = t.lng * e,\r\n"+
            "                    a = n * e;\r\n"+
            "                return a = Math.log(Math.tan(Math.PI / 4 + a / 2)), new o.Point(s, a)\r\n"+
            "            },\r\n"+
            "            unproject: function(t) {\r\n"+
            "                var e = o.LatLng.RAD_TO_DEG,\r\n"+
            "                    i = t.x * e,\r\n"+
            "                    n = (2 * Math.atan(Math.exp(t.y)) - Math.PI / 2) * e;\r\n"+
            "                return new o.LatLng(n, i)\r\n"+
            "            }\r\n"+
            "        }, o.Projection.LonLat = {\r\n"+
            "            project: function(t) {\r\n"+
            "                return new o.Point(t.lng, t.lat)\r\n"+
            "            },\r\n"+
            "            unproject: function(t) {\r\n"+
            "                return new o.LatLng(t.y, t.x)\r\n"+
            "            }\r\n"+
            "        }, o.CRS = {\r\n"+
            "            latLngToPoint: function(t, e) {\r\n"+
            "                var i = this.projection.project(t),\r\n"+
            "                    n = this.scale(e);\r\n"+
            "                return this.transformation._transform(i, n)\r\n"+
            "            },\r\n"+
            "            pointToLatLng: function(t, e) {\r\n"+
            "                var i = this.scale(e),\r\n"+
            "                    n = this.transformation.untransform(t, i);\r\n"+
            "                return this.projection.unproject(n)\r\n"+
            "            },\r\n"+
            "            project: function(t) {\r\n"+
            "                return this.projection.project(t)\r\n"+
            "            },\r\n"+
            "            scale: function(t) {\r\n"+
            "                return 256 * Math.pow(2, t)\r\n"+
            "            },\r\n"+
            "            getSize: function(t) {\r\n"+
            "                var e = this.scale(t);\r\n"+
            "                return o.point(e, e)\r\n"+
            "            }\r\n"+
            "        }, o.CRS.Simple = o.extend({}, o.CRS, {\r\n"+
            "            projection: o.Projection.LonLat,\r\n"+
            "            transformation: new o.Transformation(1, 0, -1, 0),\r\n"+
            "            scale: function(t) {\r\n"+
            "                return Math.pow(2, t)\r\n"+
            "   ");
          out.print(
            "         }\r\n"+
            "        }), o.CRS.EPSG3857 = o.extend({}, o.CRS, {\r\n"+
            "            code: \"EPSG:3857\",\r\n"+
            "            projection: o.Projection.SphericalMercator,\r\n"+
            "            transformation: new o.Transformation(.5 / Math.PI, .5, -.5 / Math.PI, .5),\r\n"+
            "            project: function(t) {\r\n"+
            "                var e = this.projection.project(t),\r\n"+
            "                    i = 6378137;\r\n"+
            "                return e.multiplyBy(i)\r\n"+
            "            }\r\n"+
            "        }), o.CRS.EPSG900913 = o.extend({}, o.CRS.EPSG3857, {\r\n"+
            "            code: \"EPSG:900913\"\r\n"+
            "        }), o.CRS.EPSG4326 = o.extend({}, o.CRS, {\r\n"+
            "            code: \"EPSG:4326\",\r\n"+
            "            projection: o.Projection.LonLat,\r\n"+
            "            transformation: new o.Transformation(1 / 360, .5, -1 / 360, .5)\r\n"+
            "        }), o.Map = o.Class.extend({\r\n"+
            "            includes: o.Mixin.Events,\r\n"+
            "            options: {\r\n"+
            "                crs: o.CRS.EPSG3857,\r\n"+
            "                fadeAnimation: o.DomUtil.TRANSITION && !o.Browser.android23,\r\n"+
            "                trackResize: !0,\r\n"+
            "                markerZoomAnimation: o.DomUtil.TRANSITION && o.Browser.any3d\r\n"+
            "            },\r\n"+
            "            initialize: function(t, e, doc) {\r\n"+
            "                e = o.setOptions(this, e), this._initContainer(t, doc), this._initLayout(), this._onResize = o.bind(this._onResize, this), this._initEvents(), e.maxBounds && this.setMaxBounds(e.maxBounds), e.center && e.zoom !== i && this.setView(o.latLng(e.center), e.zoom, {\r\n"+
            "                    reset: !0\r\n"+
            "                }), this._handlers = [], this._layers = {}, this._zoomBoundLayers = {}, this._tileLayersNum = 0, this.callInitHooks(), this._addLayers(e.layers)\r\n"+
            "            },\r\n"+
            "            setView: function(t, e) {\r\n"+
            "                return e = e === i ? this.getZoom() : e, this._resetView(o.latLng(t), this._limitZoom(e)), this\r\n"+
            "            },\r\n"+
            "            setZoom: function(t, e) {\r\n"+
            "                return this._loaded ? this.setView(this.getCenter(), t, {\r\n"+
            "                    zoom: e\r\n"+
            "                }) : (this._zoom = this._limitZoom(t), this)\r\n"+
            "            },\r\n"+
            "            zoomIn: function(t, e) {\r\n"+
            "                return this.setZoom(this._zoom + (t || 1), e)\r\n"+
            "            },\r\n"+
            "            zoomOut: function(t, e) {\r\n"+
            "                return this.setZoom(this._zoom - (t || 1), e)\r\n"+
            "            },\r\n"+
            "            setZoomAround: function(t, e, i) {\r\n"+
            "                var n = this.getZoomScale(e),\r\n"+
            "                    s = this.getSize().divideBy(2),\r\n"+
            "                    a = t instanceof o.Point ? t : this.latLngToContainerPoint(t),\r\n"+
            "                    r = a.subtract(s).multiplyBy(1 - 1 / n),\r\n"+
            "                    h = this.containerPointToLatLng(s.add(r));\r\n"+
            "                return this.setView(h, e, {\r\n"+
            "                    zoom: i\r\n"+
            "                })\r\n"+
            "            },\r\n"+
            "            fitBounds: function(t, e) {\r\n"+
            "                e = e || {}, t = t.getBounds ? t.getBounds() : o.latLngBounds(t);\r\n"+
            "                var i = o.point(e.paddingTopLeft || e.padding || [0, 0]),\r\n"+
            "                    n = o.point(e.paddingBottomRight || e.padding || [0, 0]),\r\n"+
            "                    s = this.getBoundsZoom(t, !1, i.add(n));\r\n"+
            "                s = e.maxZoom ? Math.min(e.maxZoom, s) : s;\r\n"+
            "                var a = n.subtract(i).divideBy(2),\r\n"+
            "                    r = this.project(t.getSouthWest(), s),\r\n"+
            "                    h = this.project(t.getNorthEast(), s),\r\n"+
            "                    l = this.unproject(r.add(h).divideBy(2).add(a), s);\r\n"+
            "                return this.setView(l, s, e)\r\n"+
            "            },\r\n"+
            "            fitWorld: function(t) {\r\n"+
            "                return this.fitBounds([\r\n"+
            "                    [-90, -180],\r\n"+
            "                    [90, 180]\r\n"+
            "                ], t)\r\n"+
            "            },\r\n"+
            "            panTo: function(t, e) {\r\n"+
            "                return this.setView(t, this._zoom, {\r\n"+
            "                    pan: e\r\n"+
            "                })\r\n"+
            "            },\r\n"+
            "            panBy: function(t) {\r\n"+
            "                return this.fire(\"movestart\"), this._rawPanBy(o.point(t)), this.fire(\"move\"), this.fire(\"moveend\")\r\n"+
            "            },\r\n"+
            "            setMaxBounds: function(t) {\r\n"+
            "                return t = o.latLngBounds(t), this.options.maxBounds = t, t ? (this._loaded && this._panInsideMaxBounds(), this.on(\"moveend\", this._panInsideMaxBounds, this)) : this.off(\"moveend\", this._panInsideMaxBounds, this)\r\n"+
            "            },\r\n"+
            "            panInsideBounds: function(t, e) {\r\n"+
            "                var i = this.getCenter(),\r\n"+
            "                    n = this._limitCenter(i, this._zoom, t);\r\n"+
            "                return i.equals(n) ? this : this.panTo(n, e)\r\n"+
            "            },\r\n"+
            "            addLayer: function(t) {\r\n"+
            "                var e = o.stamp(t);\r\n"+
            "                return this._layers[e] ? this : (this._layers[e] = t, !t.options || isNaN(t.options.maxZoom) && isNaN(t.options.minZoom) || (this._zoomBoundLayers[e] = t, this._updateZoomLevels()), this.options.zoomAnimation && o.TileLayer && t instanceof o.TileLayer && (this._tileLayersNum++, this._tileLayersToLoad++, t.on(\"load\", this._onTileLayerLoad, this)), this._loaded && this._layerAdd(t), this)\r\n"+
            "            },\r\n"+
            "            removeLayer: function(t) {\r\n"+
            "                var e = o.stamp(t);\r\n"+
            "                return this._layers[e] ? (this._loaded && t.onRemove(this), delete this._layers[e], this._loaded && this.fire(\"layerremove\", {\r\n"+
            "                    layer: t\r\n"+
            "                }), this._zoomBoundLayers[e] && (delete this._zoomBoundLayers[e], this._updateZoomLevels()), this.options.zoomAnimation && o.TileLayer && t instanceof o.TileLayer && (this._tileLayersNum--, this._tileLayersToLoad--, t.off(\"load\", this._onTileLayerLoad, this)), this) : this\r\n"+
            "            },\r\n"+
            "            hasLayer: function(t) {\r\n"+
            "                return t ? o.stamp(t) in this._layers : !1\r\n"+
            "            },\r\n"+
            "            eachLayer: function(t, e) {\r\n"+
            "                for (var i in this._layers) t.call(e, this._layers[i]);\r\n"+
            "                return this\r\n"+
            "            },\r\n"+
            "            invalidateSize: function(t) {\r\n"+
            "                if (!this._loaded) return this;\r\n"+
            "                t = o.extend({\r\n"+
            "                    animate: !1,\r\n"+
            "                    pan: !0\r\n"+
            "                }, t === !0 ? {\r\n"+
            "                    animate: !0\r\n"+
            "                } : t);\r\n"+
            "                var e = this.getSize();\r\n"+
            "                this._sizeChanged = !0, this._initialCenter = null;\r\n"+
            "                var i = this.getSize(),\r\n"+
            "                    n = e.divideBy(2).round(),\r\n"+
            "                    s = i.divideBy(2).round(),\r\n"+
            "                    a = n.subtract(s);\r\n"+
            "                return a.x || a.y ? (t.animate && t.pan ? this.panBy(a) : (t.pan && this._rawPanBy(a), this.fire(\"move\"), t.debounceMoveend ? (clearTimeout(this._sizeTimer), this._sizeTimer = setTimeout(o.bind(this.fire, this, \"moveend\"), 200)) : this.fire(\"moveend\")), this.fire(\"resize\", {\r\n"+
            "                    oldSize: e,\r\n"+
            "                    newSize: i\r\n"+
            "                })) : this\r\n"+
            "            },\r\n"+
            "            addHandler: function(t, e) {\r\n"+
            "                if (!e) return this;\r\n"+
            "                var i = this[t] = new e(this);\r\n"+
            "                return this._handlers.push(i), this.options[t] && i.enable(), this\r\n"+
            "            },\r\n"+
            "            remove: function() {\r\n"+
            "                this._loaded && this.fire(\"unload\"), this._initEvents(\"off\");\r\n"+
            "                try {\r\n"+
            "                    delete this._container._leaflet\r\n"+
            "                } catch (t) {\r\n"+
            "                    this._container._leaflet = i\r\n"+
            "                }\r\n"+
            "                return this._clearPanes(), this._clearControlPos && this._clearControlPos(), this._clearHandlers(), this\r\n"+
            "            },\r\n"+
            "            getCenter: function() {\r\n"+
            "                return this._checkIfLoaded(), this._initialCenter && !this._moved() ? this._initialCenter : this.layerPointToLatLng(this._getCenterLayerPoint())\r\n"+
            "            },\r\n"+
            "            getZoom: function() {\r\n"+
            "                return this._zoom\r\n"+
            "            },\r\n"+
            "            getBounds: function() {\r\n"+
            "                var t = this.getPixelBounds(),\r\n"+
            "                    e = this.unproject(t.getBottomLeft()),\r\n"+
            "                    i = this.unproject(t.getTopRight());\r\n"+
            "                return new o.LatLngBounds(e, i)\r\n"+
            "            },\r\n"+
            "            getMinZoom: function() {\r\n"+
            "                return this.options.minZoom === i ? this._layersMinZoom === i ? 0 : this._layersMinZoom : this.options.minZoom\r\n"+
            "            },\r\n"+
            "            getMaxZoom: function() {\r\n"+
            "                return this.options.maxZoom === i ? this._layersMaxZoom === i ? 1 / 0 : this._layersMaxZoom : this.options.maxZoom\r\n"+
            "            },\r\n"+
            "            getBoundsZoom: function(t, e, i) {\r\n"+
            "                t = o.latLngBounds(t);\r\n"+
            "                var n, s = this.getMinZoom() - (e ? 1 : 0),\r\n"+
            "                    a = this.getMaxZoom(),\r\n"+
            "                    r = this.getSize(),\r\n"+
            "                    h = t.getNorthWest(),\r\n"+
            "                    l = t.getSouthEast(),\r\n"+
            "                    u = !0;\r\n"+
            "                i = o.point(i || [0, 0]);\r\n"+
            "                do s++, n = this.project(l, s).subtract(this.project(h, s)).add(i), u = e ? n.x < r.x || n.y < r.y : r.contains(n); while (u && a >= s);\r\n"+
            "                return u && e ? null : e ? s : s - 1\r\n"+
            "            },\r\n"+
            "            getSize: function() {\r\n"+
            "                return (!this._size || this._sizeChanged) && (this._size = new o.Point(this._container.clientWidth, this._container.clientHeight), this._sizeChanged = !1), this._size.clone()\r\n"+
            "            },\r\n"+
            "            getPixelBounds: function() {\r\n"+
            "                var t = this._getTopLeftPoint();\r\n"+
            "                return new o.Bounds(t, t.add(this.getSize()))\r\n"+
            "            },\r\n"+
            "            getPixelOrigin: function() {\r\n"+
            "                return this._checkIfLoaded(), this._initialTopLeftPoint\r\n"+
            "            },\r\n"+
            "            getPanes: function() {\r\n"+
            "                return this._panes\r\n"+
            "            },\r\n"+
            "            getContainer: function() {\r\n"+
            "                return this._container\r\n"+
            "            },\r\n"+
            "            getZoomScale: function(t) {\r\n"+
            "                var e = this.options.crs;\r\n"+
            "                return e.scale(t) / e.scale(this._zoom)\r\n"+
            "            },\r\n"+
            "            getScaleZoom: function(t) {\r\n"+
            "                return this._zoom + Math.log(t) / Math.LN2\r\n"+
            "            },\r\n"+
            "            project: function(t, e) {\r\n"+
            "                return e = e === i ");
          out.print(
            "? this._zoom : e, this.options.crs.latLngToPoint(o.latLng(t), e)\r\n"+
            "            },\r\n"+
            "            unproject: function(t, e) {\r\n"+
            "                return e = e === i ? this._zoom : e, this.options.crs.pointToLatLng(o.point(t), e)\r\n"+
            "            },\r\n"+
            "            layerPointToLatLng: function(t) {\r\n"+
            "                var e = o.point(t).add(this.getPixelOrigin());\r\n"+
            "                return this.unproject(e)\r\n"+
            "            },\r\n"+
            "            latLngToLayerPoint: function(t) {\r\n"+
            "                var e = this.project(o.latLng(t))._round();\r\n"+
            "                return e._subtract(this.getPixelOrigin())\r\n"+
            "            },\r\n"+
            "            containerPointToLayerPoint: function(t) {\r\n"+
            "                return o.point(t).subtract(this._getMapPanePos())\r\n"+
            "            },\r\n"+
            "            layerPointToContainerPoint: function(t) {\r\n"+
            "                return o.point(t).add(this._getMapPanePos())\r\n"+
            "            },\r\n"+
            "            containerPointToLatLng: function(t) {\r\n"+
            "                var e = this.containerPointToLayerPoint(o.point(t));\r\n"+
            "                return this.layerPointToLatLng(e)\r\n"+
            "            },\r\n"+
            "            latLngToContainerPoint: function(t) {\r\n"+
            "                return this.layerPointToContainerPoint(this.latLngToLayerPoint(o.latLng(t)))\r\n"+
            "            },\r\n"+
            "            mouseEventToContainerPoint: function(t) {\r\n"+
            "                return o.DomEvent.getMousePosition(t, this._container)\r\n"+
            "            },\r\n"+
            "            mouseEventToLayerPoint: function(t) {\r\n"+
            "                return this.containerPointToLayerPoint(this.mouseEventToContainerPoint(t))\r\n"+
            "            },\r\n"+
            "            mouseEventToLatLng: function(t) {\r\n"+
            "                return this.layerPointToLatLng(this.mouseEventToLayerPoint(t))\r\n"+
            "            },\r\n"+
            "            _initContainer: function(t, doc) {\r\n"+
            "                var e = this._container = o.DomUtil.get(t, doc);\r\n"+
            "                if (!e) throw new Error(\"Map container not found.\");\r\n"+
            "                if (e._leaflet) throw new Error(\"Map container is already initialized.\");\r\n"+
            "                e._leaflet = !0\r\n"+
            "            },\r\n"+
            "            _initLayout: function() {\r\n"+
            "                var t = this._container;\r\n"+
            "                o.DomUtil.addClass(t, \"leaflet-container\" + (o.Browser.touch ? \" leaflet-touch\" : \"\") + (o.Browser.retina ? \" leaflet-retina\" : \"\") + (o.Browser.ielt9 ? \" leaflet-oldie\" : \"\") + (this.options.fadeAnimation ? \" leaflet-fade-anim\" : \"\"));\r\n"+
            "                var e = o.DomUtil.getStyle(t, \"position\");\r\n"+
            "                \"absolute\" !== e && \"relative\" !== e && \"fixed\" !== e && (t.style.position = \"relative\"), this._initPanes(), this._initControlPos && this._initControlPos()\r\n"+
            "            },\r\n"+
            "            _initPanes: function() {\r\n"+
            "                var t = this._panes = {};\r\n"+
            "                this._mapPane = t.mapPane = this._createPane(\"leaflet-map-pane\", this._container), this._tilePane = t.tilePane = this._createPane(\"leaflet-tile-pane\", this._mapPane), t.objectsPane = this._createPane(\"leaflet-objects-pane\", this._mapPane), t.shadowPane = this._createPane(\"leaflet-shadow-pane\"), t.overlayPane = this._createPane(\"leaflet-overlay-pane\"), t.markerPane = this._createPane(\"leaflet-marker-pane\"), t.popupPane = this._createPane(\"leaflet-popup-pane\");\r\n"+
            "                var e = \" leaflet-zoom-hide\";\r\n"+
            "                this.options.markerZoomAnimation || (o.DomUtil.addClass(t.markerPane, e), o.DomUtil.addClass(t.shadowPane, e), o.DomUtil.addClass(t.popupPane, e))\r\n"+
            "            },\r\n"+
            "            _createPane: function(t, e) {\r\n"+
            "                return o.DomUtil.create(\"div\", t, e || this._panes.objectsPane)\r\n"+
            "            },\r\n"+
            "            _clearPanes: function() {\r\n"+
            "                this._container.removeChild(this._mapPane)\r\n"+
            "            },\r\n"+
            "            _addLayers: function(t) {\r\n"+
            "                t = t ? o.Util.isArray(t) ? t : [t] : [];\r\n"+
            "                for (var e = 0, i = t.length; i > e; e++) this.addLayer(t[e])\r\n"+
            "            },\r\n"+
            "            _resetView: function(t, e, i, n) {\r\n"+
            "                var s = this._zoom !== e;\r\n"+
            "                n || (this.fire(\"movestart\"), s && this.fire(\"zoomstart\")), this._zoom = e, this._initialCenter = t, this._initialTopLeftPoint = this._getNewTopLeftPoint(t), i ? this._initialTopLeftPoint._add(this._getMapPanePos()) : o.DomUtil.setPosition(this._mapPane, new o.Point(0, 0)), this._tileLayersToLoad = this._tileLayersNum;\r\n"+
            "                var a = !this._loaded;\r\n"+
            "                this._loaded = !0, this.fire(\"viewreset\", {\r\n"+
            "                    hard: !i\r\n"+
            "                }), a && (this.fire(\"load\"), this.eachLayer(this._layerAdd, this)), this.fire(\"move\"), (s || n) && this.fire(\"zoomend\"), this.fire(\"moveend\", {\r\n"+
            "                    hard: !i\r\n"+
            "                })\r\n"+
            "            },\r\n"+
            "            _rawPanBy: function(t) {\r\n"+
            "                o.DomUtil.setPosition(this._mapPane, this._getMapPanePos().subtract(t))\r\n"+
            "            },\r\n"+
            "            _getZoomSpan: function() {\r\n"+
            "                return this.getMaxZoom() - this.getMinZoom()\r\n"+
            "            },\r\n"+
            "            _updateZoomLevels: function() {\r\n"+
            "                var t, e = 1 / 0,\r\n"+
            "                    n = -(1 / 0),\r\n"+
            "                    o = this._getZoomSpan();\r\n"+
            "                for (t in this._zoomBoundLayers) {\r\n"+
            "                    var s = this._zoomBoundLayers[t];\r\n"+
            "                    isNaN(s.options.minZoom) || (e = Math.min(e, s.options.minZoom)), isNaN(s.options.maxZoom) || (n = Math.max(n, s.options.maxZoom))\r\n"+
            "                }\r\n"+
            "                t === i ? this._layersMaxZoom = this._layersMinZoom = i : (this._layersMaxZoom = n, this._layersMinZoom = e), o !== this._getZoomSpan() && this.fire(\"zoomlevelschange\")\r\n"+
            "            },\r\n"+
            "            _panInsideMaxBounds: function() {\r\n"+
            "                this.panInsideBounds(this.options.maxBounds)\r\n"+
            "            },\r\n"+
            "            _checkIfLoaded: function() {\r\n"+
            "                if (!this._loaded) throw new Error(\"Set map center and zoom first.\")\r\n"+
            "            },\r\n"+
            "            _initEvents: function(e) {\r\n"+
            "                if (o.DomEvent) {\r\n"+
            "                    e = e || \"on\", o.DomEvent[e](this._container, \"click\", this._onMouseClick, this);\r\n"+
            "                    var i, n, s = [\"dblclick\", \"mousedown\", \"mouseup\", \"mouseenter\", \"mouseleave\", \"mousemove\", \"contextmenu\"];\r\n"+
            "                    for (i = 0, n = s.length; n > i; i++) o.DomEvent[e](this._container, s[i], this._fireMouseEvent, this);\r\n"+
            "                    this.options.trackResize && o.DomEvent[e](t, \"resize\", this._onResize, this)\r\n"+
            "                }\r\n"+
            "            },\r\n"+
            "            _onResize: function() {\r\n"+
            "                o.Util.cancelAnimFrame(this._resizeRequest), this._resizeRequest = o.Util.requestAnimFrame(function() {\r\n"+
            "                    this.invalidateSize({\r\n"+
            "                        debounceMoveend: !0\r\n"+
            "                    })\r\n"+
            "                }, this, !1, this._container)\r\n"+
            "            },\r\n"+
            "            _onMouseClick: function(t) {\r\n"+
            "                !this._loaded || !t._simulated && (this.dragging && this.dragging.moved() || this.boxZoom && this.boxZoom.moved()) || o.DomEvent._skipped(t) || (this.fire(\"preclick\"), this._fireMouseEvent(t))\r\n"+
            "            },\r\n"+
            "            _fireMouseEvent: function(t) {\r\n"+
            "                if (this._loaded && !o.DomEvent._skipped(t)) {\r\n"+
            "                    var e = t.type;\r\n"+
            "                    if (e = \"mouseenter\" === e ? \"mouseover\" : \"mouseleave\" === e ? \"mouseout\" : e, this.hasEventListeners(e)) {\r\n"+
            "                        \"contextmenu\" === e && o.DomEvent.preventDefault(t);\r\n"+
            "                        var i = this.mouseEventToContainerPoint(t),\r\n"+
            "                            n = this.containerPointToLayerPoint(i),\r\n"+
            "                            s = this.layerPointToLatLng(n);\r\n"+
            "                        this.fire(e, {\r\n"+
            "                            latlng: s,\r\n"+
            "                            layerPoint: n,\r\n"+
            "                            containerPoint: i,\r\n"+
            "                            originalEvent: t\r\n"+
            "                        })\r\n"+
            "                    }\r\n"+
            "                }\r\n"+
            "            },\r\n"+
            "            _onTileLayerLoad: function() {\r\n"+
            "                this._tileLayersToLoad--, this._tileLayersNum && !this._tileLayersToLoad && this.fire(\"tilelayersload\")\r\n"+
            "            },\r\n"+
            "            _clearHandlers: function() {\r\n"+
            "                for (var t = 0, e = this._handlers.length; e > t; t++) this._handlers[t].disable()\r\n"+
            "            },\r\n"+
            "            whenReady: function(t, e) {\r\n"+
            "                return this._loaded ? t.call(e || this, this) : this.on(\"load\", t, e), this\r\n"+
            "            },\r\n"+
            "            _layerAdd: function(t) {\r\n"+
            "                t.onAdd(this), this.fire(\"layeradd\", {\r\n"+
            "                    layer: t\r\n"+
            "                })\r\n"+
            "            },\r\n"+
            "            _getMapPanePos: function() {\r\n"+
            "                return o.DomUtil.getPosition(this._mapPane)\r\n"+
            "            },\r\n"+
            "            _moved: function() {\r\n"+
            "                var t = this._getMapPanePos();\r\n"+
            "                return t && !t.equals([0, 0])\r\n"+
            "            },\r\n"+
            "            _getTopLeftPoint: function() {\r\n"+
            "                return this.getPixelOrigin().subtract(this._getMapPanePos())\r\n"+
            "            },\r\n"+
            "            _getNewTopLeftPoint: function(t, e) {\r\n"+
            "                var i = this.getSize()._divideBy(2);\r\n"+
            "                return this.project(t, e)._subtract(i)._round()\r\n"+
            "            },\r\n"+
            "            _latLngToNewLayerPoint: function(t, e, i) {\r\n"+
            "                var n = this._getNewTopLeftPoint(i, e).add(this._getMapPanePos());\r\n"+
            "                return this.project(t, e)._subtract(n)\r\n"+
            "            },\r\n"+
            "            _getCenterLayerPoint: function() {\r\n"+
            "                return this.containerPointToLayerPoint(this.getSize()._divideBy(2))\r\n"+
            "            },\r\n"+
            "            _getCenterOffset: function(t) {\r\n"+
            "                return this.latLngToLayerPoint(t).subtract(this._getCenterLayerPoint())\r\n"+
            "            },\r\n"+
            "            _limitCenter: function(t, e, i) {\r\n"+
            "                if (!i) return t;\r\n"+
            "                var n = this.project(t, e),\r\n"+
            "                    s = this.getSize().divideBy(2),\r\n"+
            "                    a = new o.Bounds(n.subtract(s), n.add(s)),\r\n"+
            "                    r = this._getBoundsOffset(a, i, e);\r\n"+
            "                return this.unproject(n.add(r), e)\r\n"+
            "            },\r\n"+
            "            _limitOffset: function(t, e) {\r\n"+
            "                if (!e) return t;\r\n"+
            "                var i ");
          out.print(
            "= this.getPixelBounds(),\r\n"+
            "                    n = new o.Bounds(i.min.add(t), i.max.add(t));\r\n"+
            "                return t.add(this._getBoundsOffset(n, e))\r\n"+
            "            },\r\n"+
            "            _getBoundsOffset: function(t, e, i) {\r\n"+
            "                var n = this.project(e.getNorthWest(), i).subtract(t.min),\r\n"+
            "                    s = this.project(e.getSouthEast(), i).subtract(t.max),\r\n"+
            "                    a = this._rebound(n.x, -s.x),\r\n"+
            "                    r = this._rebound(n.y, -s.y);\r\n"+
            "                return new o.Point(a, r)\r\n"+
            "            },\r\n"+
            "            _rebound: function(t, e) {\r\n"+
            "                return t + e > 0 ? Math.round(t - e) / 2 : Math.max(0, Math.ceil(t)) - Math.max(0, Math.floor(e))\r\n"+
            "            },\r\n"+
            "            _limitZoom: function(t) {\r\n"+
            "                var e = this.getMinZoom(),\r\n"+
            "                    i = this.getMaxZoom();\r\n"+
            "                return Math.max(e, Math.min(i, t))\r\n"+
            "            }\r\n"+
            "        }), o.map = function(t, e, doc) {\r\n"+
            "            return new o.Map(t, e, doc)\r\n"+
            "        }, o.Projection.Mercator = {\r\n"+
            "            MAX_LATITUDE: 85.0840591556,\r\n"+
            "            R_MINOR: 6356752.314245179,\r\n"+
            "            R_MAJOR: 6378137,\r\n"+
            "            project: function(t) {\r\n"+
            "                var e = o.LatLng.DEG_TO_RAD,\r\n"+
            "                    i = this.MAX_LATITUDE,\r\n"+
            "                    n = Math.max(Math.min(i, t.lat), -i),\r\n"+
            "                    s = this.R_MAJOR,\r\n"+
            "                    a = this.R_MINOR,\r\n"+
            "                    r = t.lng * e * s,\r\n"+
            "                    h = n * e,\r\n"+
            "                    l = a / s,\r\n"+
            "                    u = Math.sqrt(1 - l * l),\r\n"+
            "                    c = u * Math.sin(h);\r\n"+
            "                c = Math.pow((1 - c) / (1 + c), .5 * u);\r\n"+
            "                var d = Math.tan(.5 * (.5 * Math.PI - h)) / c;\r\n"+
            "                return h = -s * Math.log(d), new o.Point(r, h)\r\n"+
            "            },\r\n"+
            "            unproject: function(t) {\r\n"+
            "                for (var e, i = o.LatLng.RAD_TO_DEG, n = this.R_MAJOR, s = this.R_MINOR, a = t.x * i / n, r = s / n, h = Math.sqrt(1 - r * r), l = Math.exp(-t.y / n), u = Math.PI / 2 - 2 * Math.atan(l), c = 15, d = 1e-7, p = c, _ = .1; Math.abs(_) > d && --p > 0;) e = h * Math.sin(u), _ = Math.PI / 2 - 2 * Math.atan(l * Math.pow((1 - e) / (1 + e), .5 * h)) - u, u += _;\r\n"+
            "                return new o.LatLng(u * i, a)\r\n"+
            "            }\r\n"+
            "        }, o.CRS.EPSG3395 = o.extend({}, o.CRS, {\r\n"+
            "            code: \"EPSG:3395\",\r\n"+
            "            projection: o.Projection.Mercator,\r\n"+
            "            transformation: function() {\r\n"+
            "                var t = o.Projection.Mercator,\r\n"+
            "                    e = t.R_MAJOR,\r\n"+
            "                    i = .5 / (Math.PI * e);\r\n"+
            "                return new o.Transformation(i, .5, -i, .5)\r\n"+
            "            }()\r\n"+
            "        }), o.TileLayer = o.Class.extend({\r\n"+
            "            includes: o.Mixin.Events,\r\n"+
            "            options: {\r\n"+
            "                minZoom: 0,\r\n"+
            "                maxZoom: 18,\r\n"+
            "                tileSize: 256,\r\n"+
            "                subdomains: \"abc\",\r\n"+
            "                errorTileUrl: \"\",\r\n"+
            "                attribution: \"\",\r\n"+
            "                zoomOffset: 0,\r\n"+
            "                opacity: 1,\r\n"+
            "                unloadInvisibleTiles: o.Browser.mobile,\r\n"+
            "                updateWhenIdle: o.Browser.mobile\r\n"+
            "            },\r\n"+
            "            initialize: function(t, e) {\r\n"+
            "                e = o.setOptions(this, e), e.detectRetina && o.Browser.retina && e.maxZoom > 0 && (e.tileSize = Math.floor(e.tileSize / 2), e.zoomOffset++, e.minZoom > 0 && e.minZoom--, this.options.maxZoom--), e.bounds && (e.bounds = o.latLngBounds(e.bounds)), this._url = t;\r\n"+
            "                var i = this.options.subdomains;\r\n"+
            "                \"string\" == typeof i && (this.options.subdomains = i.split(\"\"))\r\n"+
            "            },\r\n"+
            "            onAdd: function(t) {\r\n"+
            "                this._map = t, this._animated = t._zoomAnimated, this._initContainer(), t.on({\r\n"+
            "                    viewreset: this._reset,\r\n"+
            "                    moveend: this._update\r\n"+
            "                }, this), this._animated && t.on({\r\n"+
            "                    zoomanim: this._animateZoom,\r\n"+
            "                    zoomend: this._endZoomAnim\r\n"+
            "                }, this), this.options.updateWhenIdle || (this._limitedUpdate = o.Util.limitExecByInterval(this._update, 150, this), t.on(\"move\", this._limitedUpdate, this)), this._reset(), this._update()\r\n"+
            "            },\r\n"+
            "            addTo: function(t) {\r\n"+
            "                return t.addLayer(this), this\r\n"+
            "            },\r\n"+
            "            onRemove: function(t) {\r\n"+
            "                this._container.parentNode.removeChild(this._container), t.off({\r\n"+
            "                    viewreset: this._reset,\r\n"+
            "                    moveend: this._update\r\n"+
            "                }, this), this._animated && t.off({\r\n"+
            "                    zoomanim: this._animateZoom,\r\n"+
            "                    zoomend: this._endZoomAnim\r\n"+
            "                }, this), this.options.updateWhenIdle || t.off(\"move\", this._limitedUpdate, this), this._container = null, this._map = null\r\n"+
            "            },\r\n"+
            "            bringToFront: function() {\r\n"+
            "                var t = this._map._panes.tilePane;\r\n"+
            "                return this._container && (t.appendChild(this._container), this._setAutoZIndex(t, Math.max)), this\r\n"+
            "            },\r\n"+
            "            bringToBack: function() {\r\n"+
            "                var t = this._map._panes.tilePane;\r\n"+
            "                return this._container && (t.insertBefore(this._container, t.firstChild), this._setAutoZIndex(t, Math.min)), this\r\n"+
            "            },\r\n"+
            "            getAttribution: function() {\r\n"+
            "                return this.options.attribution\r\n"+
            "            },\r\n"+
            "            getContainer: function() {\r\n"+
            "                return this._container\r\n"+
            "            },\r\n"+
            "            setOpacity: function(t) {\r\n"+
            "                return this.options.opacity = t, this._map && this._updateOpacity(), this\r\n"+
            "            },\r\n"+
            "            setZIndex: function(t) {\r\n"+
            "                return this.options.zIndex = t, this._updateZIndex(), this\r\n"+
            "            },\r\n"+
            "            setUrl: function(t, e) {\r\n"+
            "                return this._url = t, e || this.redraw(), this\r\n"+
            "            },\r\n"+
            "            redraw: function() {\r\n"+
            "                return this._map && (this._reset({\r\n"+
            "                    hard: !0\r\n"+
            "                }), this._update()), this\r\n"+
            "            },\r\n"+
            "            _updateZIndex: function() {\r\n"+
            "                this._container && this.options.zIndex !== i && (this._container.style.zIndex = this.options.zIndex)\r\n"+
            "            },\r\n"+
            "            _setAutoZIndex: function(t, e) {\r\n"+
            "                var i, n, o, s = t.children,\r\n"+
            "                    a = -e(1 / 0, -(1 / 0));\r\n"+
            "                for (n = 0, o = s.length; o > n; n++) s[n] !== this._container && (i = parseInt(s[n].style.zIndex, 10), isNaN(i) || (a = e(a, i)));\r\n"+
            "                this.options.zIndex = this._container.style.zIndex = (isFinite(a) ? a : 0) + e(1, -1)\r\n"+
            "            },\r\n"+
            "            _updateOpacity: function() {\r\n"+
            "                var t, e = this._tiles;\r\n"+
            "                if (o.Browser.ielt9)\r\n"+
            "                    for (t in e) o.DomUtil.setOpacity(e[t], this.options.opacity);\r\n"+
            "                else o.DomUtil.setOpacity(this._container, this.options.opacity)\r\n"+
            "            },\r\n"+
            "            _initContainer: function() {\r\n"+
            "                var t = this._map._panes.tilePane;\r\n"+
            "                if (!this._container) {\r\n"+
            "                    if (this._container = o.DomUtil.create(\"div\", \"leaflet-layer\"), this._updateZIndex(), this._animated) {\r\n"+
            "                        var e = \"leaflet-tile-container\";\r\n"+
            "                        this._bgBuffer = o.DomUtil.create(\"div\", e, this._container), this._tileContainer = o.DomUtil.create(\"div\", e, this._container)\r\n"+
            "                    } else this._tileContainer = this._container;\r\n"+
            "                    t.appendChild(this._container), this.options.opacity < 1 && this._updateOpacity()\r\n"+
            "                }\r\n"+
            "            },\r\n"+
            "            _reset: function(t) {\r\n"+
            "                for (var e in this._tiles) this.fire(\"tileunload\", {\r\n"+
            "                    tile: this._tiles[e]\r\n"+
            "                });\r\n"+
            "                this._tiles = {}, this._tilesToLoad = 0, this.options.reuseTiles && (this._unusedTiles = []), this._tileContainer.innerHTML = \"\", this._animated && t && t.hard && this._clearBgBuffer(), this._initContainer()\r\n"+
            "            },\r\n"+
            "            _getTileSize: function() {\r\n"+
            "                var t = this._map,\r\n"+
            "                    e = t.getZoom() + this.options.zoomOffset,\r\n"+
            "                    i = this.options.maxNativeZoom,\r\n"+
            "                    n = this.options.tileSize;\r\n"+
            "                return i && e > i && (n = Math.round(t.getZoomScale(e) / t.getZoomScale(i) * n)), n\r\n"+
            "            },\r\n"+
            "            _update: function() {\r\n"+
            "                if (this._map) {\r\n"+
            "                    var t = this._map,\r\n"+
            "                        e = t.getPixelBounds(),\r\n"+
            "                        i = t.getZoom(),\r\n"+
            "                        n = this._getTileSize();\r\n"+
            "                    if (!(i > this.options.maxZoom || i < this.options.minZoom)) {\r\n"+
            "                        var s = o.bounds(e.min.divideBy(n)._floor(), e.max.divideBy(n)._floor());\r\n"+
            "                        this._addTilesFromCenterOut(s), (this.options.unloadInvisibleTiles || this.options.reuseTiles) && this._removeOtherTiles(s)\r\n"+
            "                    }\r\n"+
            "                }\r\n"+
            "            },\r\n"+
            "            _addTilesFromCenterOut: function(t) {\r\n"+
            "                var i, n, s, a = [],\r\n"+
            "                    r = t.getCenter();\r\n"+
            "                for (i = t.min.y; i <= t.max.y; i++)\r\n"+
            "                    for (n = t.min.x; n <= t.max.x; n++) s = new o.Point(n, i), this._tileShouldBeLoaded(s) && a.push(s);\r\n"+
            "                var h = a.length;\r\n"+
            "                if (0 !== h) {\r\n"+
            "                    a.sort(function(t, e) {\r\n"+
            "                        return t.distanceTo(r) - e.distanceTo(r)\r\n"+
            "                    });\r\n"+
            "                    var l = e.createDocumentFragment();\r\n"+
            "                    for (this._tilesToLoad || this.fire(\"loading\"), this._tilesToLoad += h, n = 0; h > n; n++) this._addTile(a[n], l);\r\n"+
            "                    this._tileContainer.appendChild(l)\r\n"+
            "                }\r\n"+
            "            },\r\n"+
            "            _tileShouldBeLoaded: function(t) {\r\n"+
            "                if (t.x + \":\" + t.y in this._tiles) return !1;\r\n"+
            "                var e = this.options;\r\n"+
            "                if (!e.continuousWorld) {\r\n"+
            "             ");
          out.print(
            "       var i = this._getWrapTileNum();\r\n"+
            "                    if (e.noWrap && (t.x < 0 || t.x >= i.x) || t.y < 0 || t.y >= i.y) return !1\r\n"+
            "                }\r\n"+
            "                if (e.bounds) {\r\n"+
            "                    var n = this._getTileSize(),\r\n"+
            "                        o = t.multiplyBy(n),\r\n"+
            "                        s = o.add([n, n]),\r\n"+
            "                        a = this._map.unproject(o),\r\n"+
            "                        r = this._map.unproject(s);\r\n"+
            "                    if (e.continuousWorld || e.noWrap || (a = a.wrap(), r = r.wrap()), !e.bounds.intersects([a, r])) return !1\r\n"+
            "                }\r\n"+
            "                return !0\r\n"+
            "            },\r\n"+
            "            _removeOtherTiles: function(t) {\r\n"+
            "                var e, i, n, o;\r\n"+
            "                for (o in this._tiles) e = o.split(\":\"), i = parseInt(e[0], 10), n = parseInt(e[1], 10), (i < t.min.x || i > t.max.x || n < t.min.y || n > t.max.y) && this._removeTile(o)\r\n"+
            "            },\r\n"+
            "            _removeTile: function(t) {\r\n"+
            "                var e = this._tiles[t];\r\n"+
            "                this.fire(\"tileunload\", {\r\n"+
            "                    tile: e,\r\n"+
            "                    url: e.src\r\n"+
            "                }), this.options.reuseTiles ? (o.DomUtil.removeClass(e, \"leaflet-tile-loaded\"), this._unusedTiles.push(e)) : e.parentNode === this._tileContainer && this._tileContainer.removeChild(e), o.Browser.android || (e.onload = null, e.src = o.Util.emptyImageUrl), delete this._tiles[t]\r\n"+
            "            },\r\n"+
            "            _addTile: function(t, e) {\r\n"+
            "                var i = this._getTilePos(t),\r\n"+
            "                    n = this._getTile();\r\n"+
            "                o.DomUtil.setPosition(n, i, o.Browser.chrome), this._tiles[t.x + \":\" + t.y] = n, this._loadTile(n, t), n.parentNode !== this._tileContainer && e.appendChild(n)\r\n"+
            "            },\r\n"+
            "            _getZoomForUrl: function() {\r\n"+
            "                var t = this.options,\r\n"+
            "                    e = this._map.getZoom();\r\n"+
            "                return t.zoomReverse && (e = t.maxZoom - e), e += t.zoomOffset, t.maxNativeZoom ? Math.min(e, t.maxNativeZoom) : e\r\n"+
            "            },\r\n"+
            "            _getTilePos: function(t) {\r\n"+
            "                var e = this._map.getPixelOrigin(),\r\n"+
            "                    i = this._getTileSize();\r\n"+
            "                return t.multiplyBy(i).subtract(e)\r\n"+
            "            },\r\n"+
            "            getTileUrl: function(t) {\r\n"+
            "                return o.Util.template(this._url, o.extend({\r\n"+
            "                    s: this._getSubdomain(t),\r\n"+
            "                    z: t.z,\r\n"+
            "                    x: t.x,\r\n"+
            "                    y: t.y\r\n"+
            "                }, this.options))\r\n"+
            "            },\r\n"+
            "            _getWrapTileNum: function() {\r\n"+
            "                var t = this._map.options.crs,\r\n"+
            "                    e = t.getSize(this._map.getZoom());\r\n"+
            "                return e.divideBy(this._getTileSize())._floor()\r\n"+
            "            },\r\n"+
            "            _adjustTilePoint: function(t) {\r\n"+
            "                var e = this._getWrapTileNum();\r\n"+
            "                this.options.continuousWorld || this.options.noWrap || (t.x = (t.x % e.x + e.x) % e.x), this.options.tms && (t.y = e.y - t.y - 1), t.z = this._getZoomForUrl()\r\n"+
            "            },\r\n"+
            "            _getSubdomain: function(t) {\r\n"+
            "                var e = Math.abs(t.x + t.y) % this.options.subdomains.length;\r\n"+
            "                return this.options.subdomains[e]\r\n"+
            "            },\r\n"+
            "            _getTile: function() {\r\n"+
            "                if (this.options.reuseTiles && this._unusedTiles.length > 0) {\r\n"+
            "                    var t = this._unusedTiles.pop();\r\n"+
            "                    return this._resetTile(t), t\r\n"+
            "                }\r\n"+
            "                return this._createTile()\r\n"+
            "            },\r\n"+
            "            _resetTile: function() {},\r\n"+
            "            _createTile: function() {\r\n"+
            "                var t = o.DomUtil.create(\"img\", \"leaflet-tile\");\r\n"+
            "                return t.style.width = t.style.height = this._getTileSize() + \"px\", t.galleryimg = \"no\", t.onselectstart = t.onmousemove = o.Util.falseFn, o.Browser.ielt9 && this.options.opacity !== i && o.DomUtil.setOpacity(t, this.options.opacity), o.Browser.mobileWebkit3d && (t.style.WebkitBackfaceVisibility = \"hidden\"), t\r\n"+
            "            },\r\n"+
            "            _loadTile: function(t, e) {\r\n"+
            "                t._layer = this, t.onload = this._tileOnLoad, t.onerror = this._tileOnError, this._adjustTilePoint(e), t.src = this.getTileUrl(e), this.fire(\"tileloadstart\", {\r\n"+
            "                    tile: t,\r\n"+
            "                    url: t.src\r\n"+
            "                })\r\n"+
            "            },\r\n"+
            "            _tileLoaded: function() {\r\n"+
            "                this._tilesToLoad--, this._animated && o.DomUtil.addClass(this._tileContainer, \"leaflet-zoom-animated\"), this._tilesToLoad || (this.fire(\"load\"), this._animated && (clearTimeout(this._clearBgBufferTimer), this._clearBgBufferTimer = setTimeout(o.bind(this._clearBgBuffer, this), 500)))\r\n"+
            "            },\r\n"+
            "            _tileOnLoad: function() {\r\n"+
            "                var t = this._layer;\r\n"+
            "                this.src !== o.Util.emptyImageUrl && (o.DomUtil.addClass(this, \"leaflet-tile-loaded\"), t.fire(\"tileload\", {\r\n"+
            "                    tile: this,\r\n"+
            "                    url: this.src\r\n"+
            "                })), t._tileLoaded()\r\n"+
            "            },\r\n"+
            "            _tileOnError: function() {\r\n"+
            "                var t = this._layer;\r\n"+
            "                t.fire(\"tileerror\", {\r\n"+
            "                    tile: this,\r\n"+
            "                    url: this.src\r\n"+
            "                });\r\n"+
            "                var e = t.options.errorTileUrl;\r\n"+
            "                e && (this.src = e), t._tileLoaded()\r\n"+
            "            }\r\n"+
            "        }), o.tileLayer = function(t, e) {\r\n"+
            "            return new o.TileLayer(t, e)\r\n"+
            "        }, o.TileLayer.WMS = o.TileLayer.extend({\r\n"+
            "            defaultWmsParams: {\r\n"+
            "                service: \"WMS\",\r\n"+
            "                request: \"GetMap\",\r\n"+
            "                version: \"1.1.1\",\r\n"+
            "                layers: \"\",\r\n"+
            "                styles: \"\",\r\n"+
            "                format: \"image/jpeg\",\r\n"+
            "                transparent: !1\r\n"+
            "            },\r\n"+
            "            initialize: function(t, e) {\r\n"+
            "                this._url = t;\r\n"+
            "                var i = o.extend({}, this.defaultWmsParams),\r\n"+
            "                    n = e.tileSize || this.options.tileSize;\r\n"+
            "                e.detectRetina && o.Browser.retina ? i.width = i.height = 2 * n : i.width = i.height = n;\r\n"+
            "                for (var s in e) this.options.hasOwnProperty(s) || \"crs\" === s || (i[s] = e[s]);\r\n"+
            "                this.wmsParams = i, o.setOptions(this, e)\r\n"+
            "            },\r\n"+
            "            onAdd: function(t) {\r\n"+
            "                this._crs = this.options.crs || t.options.crs, this._wmsVersion = parseFloat(this.wmsParams.version);\r\n"+
            "                var e = this._wmsVersion >= 1.3 ? \"crs\" : \"srs\";\r\n"+
            "                this.wmsParams[e] = this._crs.code, o.TileLayer.prototype.onAdd.call(this, t)\r\n"+
            "            },\r\n"+
            "            getTileUrl: function(t) {\r\n"+
            "                var e = this._map,\r\n"+
            "                    i = this.options.tileSize,\r\n"+
            "                    n = t.multiplyBy(i),\r\n"+
            "                    s = n.add([i, i]),\r\n"+
            "                    a = this._crs.project(e.unproject(n, t.z)),\r\n"+
            "                    r = this._crs.project(e.unproject(s, t.z)),\r\n"+
            "                    h = this._wmsVersion >= 1.3 && this._crs === o.CRS.EPSG4326 ? [r.y, a.x, a.y, r.x].join(\",\") : [a.x, r.y, r.x, a.y].join(\",\"),\r\n"+
            "                    l = o.Util.template(this._url, {\r\n"+
            "                        s: this._getSubdomain(t)\r\n"+
            "                    });\r\n"+
            "                return l + o.Util.getParamString(this.wmsParams, l, !0) + \"&BBOX=\" + h\r\n"+
            "            },\r\n"+
            "            setParams: function(t, e) {\r\n"+
            "                return o.extend(this.wmsParams, t), e || this.redraw(), this\r\n"+
            "            }\r\n"+
            "        }), o.tileLayer.wms = function(t, e) {\r\n"+
            "            return new o.TileLayer.WMS(t, e)\r\n"+
            "        }, o.TileLayer.Canvas = o.TileLayer.extend({\r\n"+
            "            options: {\r\n"+
            "                async: !1\r\n"+
            "            },\r\n"+
            "            initialize: function(t) {\r\n"+
            "                o.setOptions(this, t)\r\n"+
            "            },\r\n"+
            "            redraw: function() {\r\n"+
            "                this._map && (this._reset({\r\n"+
            "                    hard: !0\r\n"+
            "                }), this._update());\r\n"+
            "                for (var t in this._tiles) this._redrawTile(this._tiles[t]);\r\n"+
            "                return this\r\n"+
            "            },\r\n"+
            "            _redrawTile: function(t) {\r\n"+
            "                this.drawTile(t, t._tilePoint, this._map._zoom)\r\n"+
            "            },\r\n"+
            "            _createTile: function() {\r\n"+
            "                var t = o.DomUtil.create(\"canvas\", \"leaflet-tile\");\r\n"+
            "                return t.width = t.height = this.options.tileSize, t.onselectstart = t.onmousemove = o.Util.falseFn, t\r\n"+
            "            },\r\n"+
            "            _loadTile: function(t, e) {\r\n"+
            "                t._layer = this, t._tilePoint = e, this._redrawTile(t), this.options.async || this.tileDrawn(t)\r\n"+
            "            },\r\n"+
            "            drawTile: function() {},\r\n"+
            "            tileDrawn: function(t) {\r\n"+
            "                this._tileOnLoad.call(t)\r\n"+
            "            }\r\n"+
            "        }), o.tileLayer.canvas = function(t) {\r\n"+
            "            return new o.TileLayer.Canvas(t)\r\n"+
            "        }, o.ImageOverlay = o.Class.extend({\r\n"+
            "            includes: o.Mixin.Events,\r\n"+
            "            options: {\r\n"+
            "                opacity: 1\r\n"+
            "            },\r\n"+
            "            initialize: function(t, e, i) {\r\n"+
            "                this._url = t, this._bounds = o.latLngBounds(e), o.setOptions(this, i)\r\n"+
            "            },\r\n"+
            "            onAdd: function(t) {\r\n"+
            "                this._map = t, this._image || this._initImage(), t._panes.overlayPane.appendChild(this._image), t.on(\"viewreset\", this._reset, this), t.options.zoomAnimation && o.Browser.any3d && t.on(\"zoomanim\", this._animateZoom, this), this._reset()\r\n"+
            "            },\r\n"+
            "            onRemove: function(t) {\r\n"+
            "                t.getPanes().overlayPane.removeChild(this._image), t.off(\"viewreset\", this._reset, this), t.options.zoomAnimation && t.off(\"zoomanim\", this._animateZoom, this)\r\n"+
            "            },\r\n"+
            "            addTo: function(t) {\r\n"+
            "                return t.addLayer(this), this\r\n"+
            "            },\r\n"+
            "            setOpacity: function(t) {\r\n"+
            "                return this.options.opacity = t, this._updateOpacity(), this\r\n"+
            "            },\r\n"+
            "            bringToFront: function() {\r\n"+
            "                return this._image && this._map._panes.overlayPane.appendChild(this._image), this\r\n"+
            "            },\r\n"+
            "            bringToBack: function() {\r\n"+
            "                v");
          out.print(
            "ar t = this._map._panes.overlayPane;\r\n"+
            "                return this._image && t.insertBefore(this._image, t.firstChild), this\r\n"+
            "            },\r\n"+
            "            setUrl: function(t) {\r\n"+
            "                this._url = t, this._image.src = this._url\r\n"+
            "            },\r\n"+
            "            getAttribution: function() {\r\n"+
            "                return this.options.attribution\r\n"+
            "            },\r\n"+
            "            _initImage: function() {\r\n"+
            "                this._image = o.DomUtil.create(\"img\", \"leaflet-image-layer\"), this._map.options.zoomAnimation && o.Browser.any3d ? o.DomUtil.addClass(this._image, \"leaflet-zoom-animated\") : o.DomUtil.addClass(this._image, \"leaflet-zoom-hide\"), this._updateOpacity(), o.extend(this._image, {\r\n"+
            "                    galleryimg: \"no\",\r\n"+
            "                    onselectstart: o.Util.falseFn,\r\n"+
            "                    onmousemove: o.Util.falseFn,\r\n"+
            "                    onload: o.bind(this._onImageLoad, this),\r\n"+
            "                    src: this._url\r\n"+
            "                })\r\n"+
            "            },\r\n"+
            "            _animateZoom: function(t) {\r\n"+
            "                var e = this._map,\r\n"+
            "                    i = this._image,\r\n"+
            "                    n = e.getZoomScale(t.zoom),\r\n"+
            "                    s = this._bounds.getNorthWest(),\r\n"+
            "                    a = this._bounds.getSouthEast(),\r\n"+
            "                    r = e._latLngToNewLayerPoint(s, t.zoom, t.center),\r\n"+
            "                    h = e._latLngToNewLayerPoint(a, t.zoom, t.center)._subtract(r),\r\n"+
            "                    l = r._add(h._multiplyBy(.5 * (1 - 1 / n)));\r\n"+
            "                i.style[o.DomUtil.TRANSFORM] = o.DomUtil.getTranslateString(l) + \" scale(\" + n + \") \"\r\n"+
            "            },\r\n"+
            "            _reset: function() {\r\n"+
            "                var t = this._image,\r\n"+
            "                    e = this._map.latLngToLayerPoint(this._bounds.getNorthWest()),\r\n"+
            "                    i = this._map.latLngToLayerPoint(this._bounds.getSouthEast())._subtract(e);\r\n"+
            "                o.DomUtil.setPosition(t, e), t.style.width = i.x + \"px\", t.style.height = i.y + \"px\"\r\n"+
            "            },\r\n"+
            "            _onImageLoad: function() {\r\n"+
            "                this.fire(\"load\")\r\n"+
            "            },\r\n"+
            "            _updateOpacity: function() {\r\n"+
            "                o.DomUtil.setOpacity(this._image, this.options.opacity)\r\n"+
            "            }\r\n"+
            "        }), o.imageOverlay = function(t, e, i) {\r\n"+
            "            return new o.ImageOverlay(t, e, i)\r\n"+
            "        }, o.Icon = o.Class.extend({\r\n"+
            "            options: {\r\n"+
            "                className: \"\"\r\n"+
            "            },\r\n"+
            "            initialize: function(t) {\r\n"+
            "                o.setOptions(this, t)\r\n"+
            "            },\r\n"+
            "            createIcon: function(t) {\r\n"+
            "                return this._createIcon(\"icon\", t)\r\n"+
            "            },\r\n"+
            "            createShadow: function(t) {\r\n"+
            "                return this._createIcon(\"shadow\", t)\r\n"+
            "            },\r\n"+
            "            _createIcon: function(t, e) {\r\n"+
            "                var i = this._getIconUrl(t);\r\n"+
            "                if (!i) {\r\n"+
            "                    if (\"icon\" === t) throw new Error(\"iconUrl not set in Icon options (see the docs).\");\r\n"+
            "                    return null\r\n"+
            "                }\r\n"+
            "                var n;\r\n"+
            "                return n = e && \"IMG\" === e.tagName ? this._createImg(i, e) : this._createImg(i), this._setIconStyles(n, t), n\r\n"+
            "            },\r\n"+
            "            _setIconStyles: function(t, e) {\r\n"+
            "                var i, n = this.options,\r\n"+
            "                    s = o.point(n[e + \"Size\"]);\r\n"+
            "                i = \"shadow\" === e ? o.point(n.shadowAnchor || n.iconAnchor) : o.point(n.iconAnchor), !i && s && (i = s.divideBy(2, !0)), t.className = \"leaflet-marker-\" + e + \" \" + n.className, i && (t.style.marginLeft = -i.x + \"px\", t.style.marginTop = -i.y + \"px\"), s && (t.style.width = s.x + \"px\", t.style.height = s.y + \"px\")\r\n"+
            "            },\r\n"+
            "            _createImg: function(t, i) {\r\n"+
            "                return i = i || e.createElement(\"img\"), i.src = t, i\r\n"+
            "            },\r\n"+
            "            _getIconUrl: function(t) {\r\n"+
            "                return o.Browser.retina && this.options[t + \"RetinaUrl\"] ? this.options[t + \"RetinaUrl\"] : this.options[t + \"Url\"]\r\n"+
            "            }\r\n"+
            "        }), o.icon = function(t) {\r\n"+
            "            return new o.Icon(t)\r\n"+
            "        }, o.Icon.Default = o.Icon.extend({\r\n"+
            "            options: {\r\n"+
            "                iconSize: [25, 41],\r\n"+
            "                iconAnchor: [12, 41],\r\n"+
            "                popupAnchor: [1, -34],\r\n"+
            "                shadowSize: [41, 41]\r\n"+
            "            },\r\n"+
            "            _getIconUrl: function(t) {\r\n"+
            "                var e = t + \"Url\";\r\n"+
            "                if (this.options[e]) return this.options[e];\r\n"+
            "                o.Browser.retina && \"icon\" === t && (t += \"-2x\");\r\n"+
            "                var i = o.Icon.Default.imagePath;\r\n"+
            "                if (!i) throw new Error(\"Couldn't autodetect L.Icon.Default.imagePath, set it manually.\");\r\n"+
            "                return i + \"/marker-\" + t + \".png\"\r\n"+
            "            }\r\n"+
            "        }), o.Icon.Default.imagePath = function() {\r\n"+
            "            var t, i, n, o, s, a = e.getElementsByTagName(\"script\"),\r\n"+
            "                r = /[\\/^]leaflet[\\-\\._]?([\\w\\-\\._]*)\\.js\\??/;\r\n"+
            "            for (t = 0, i = a.length; i > t; t++)\r\n"+
            "                if (n = a[t].src, o = n.match(r)) return s = n.split(r)[0], (s ? s + \"/\" : \"\") + \"images\"\r\n"+
            "        }(), o.Marker = o.Class.extend({\r\n"+
            "            includes: o.Mixin.Events,\r\n"+
            "            options: {\r\n"+
            "                icon: new o.Icon.Default,\r\n"+
            "                title: \"\",\r\n"+
            "                alt: \"\",\r\n"+
            "                clickable: !0,\r\n"+
            "                draggable: !1,\r\n"+
            "                keyboard: !0,\r\n"+
            "                zIndexOffset: 0,\r\n"+
            "                opacity: 1,\r\n"+
            "                riseOnHover: !1,\r\n"+
            "                riseOffset: 250\r\n"+
            "            },\r\n"+
            "            initialize: function(t, e) {\r\n"+
            "                o.setOptions(this, e), this._latlng = o.latLng(t)\r\n"+
            "            },\r\n"+
            "            onAdd: function(t) {\r\n"+
            "                this._map = t, t.on(\"viewreset\", this.update, this), this._initIcon(), this.update(), this.fire(\"add\"), t.options.zoomAnimation && t.options.markerZoomAnimation && t.on(\"zoomanim\", this._animateZoom, this)\r\n"+
            "            },\r\n"+
            "            addTo: function(t) {\r\n"+
            "                return t.addLayer(this), this\r\n"+
            "            },\r\n"+
            "            onRemove: function(t) {\r\n"+
            "                this.dragging && this.dragging.disable(), this._removeIcon(), this._removeShadow(), this.fire(\"remove\"), t.off({\r\n"+
            "                    viewreset: this.update,\r\n"+
            "                    zoomanim: this._animateZoom\r\n"+
            "                }, this), this._map = null\r\n"+
            "            },\r\n"+
            "            getLatLng: function() {\r\n"+
            "                return this._latlng\r\n"+
            "            },\r\n"+
            "            setLatLng: function(t) {\r\n"+
            "                return this._latlng = o.latLng(t), this.update(), this.fire(\"move\", {\r\n"+
            "                    latlng: this._latlng\r\n"+
            "                })\r\n"+
            "            },\r\n"+
            "            setZIndexOffset: function(t) {\r\n"+
            "                return this.options.zIndexOffset = t, this.update(), this\r\n"+
            "            },\r\n"+
            "            setIcon: function(t) {\r\n"+
            "                return this.options.icon = t, this._map && (this._initIcon(), this.update()), this._popup && this.bindPopup(this._popup), this\r\n"+
            "            },\r\n"+
            "            update: function() {\r\n"+
            "                return this._icon && this._setPos(this._map.latLngToLayerPoint(this._latlng).round()), this\r\n"+
            "            },\r\n"+
            "            _initIcon: function() {\r\n"+
            "                var t = this.options,\r\n"+
            "                    e = this._map,\r\n"+
            "                    i = e.options.zoomAnimation && e.options.markerZoomAnimation,\r\n"+
            "                    n = i ? \"leaflet-zoom-animated\" : \"leaflet-zoom-hide\",\r\n"+
            "                    s = t.icon.createIcon(this._icon),\r\n"+
            "                    a = !1;\r\n"+
            "                s !== this._icon && (this._icon && this._removeIcon(), a = !0, t.title && (s.title = t.title), t.alt && (s.alt = t.alt)), o.DomUtil.addClass(s, n), t.keyboard && (s.tabIndex = \"0\"), this._icon = s, this._initInteraction(), t.riseOnHover && o.DomEvent.on(s, \"mouseover\", this._bringToFront, this).on(s, \"mouseout\", this._resetZIndex, this);\r\n"+
            "                var r = t.icon.createShadow(this._shadow),\r\n"+
            "                    h = !1;\r\n"+
            "                r !== this._shadow && (this._removeShadow(), h = !0), r && o.DomUtil.addClass(r, n), this._shadow = r, t.opacity < 1 && this._updateOpacity();\r\n"+
            "                var l = this._map._panes;\r\n"+
            "                a && l.markerPane.appendChild(this._icon), r && h && l.shadowPane.appendChild(this._shadow)\r\n"+
            "            },\r\n"+
            "            _removeIcon: function() {\r\n"+
            "                this.options.riseOnHover && o.DomEvent.off(this._icon, \"mouseover\", this._bringToFront).off(this._icon, \"mouseout\", this._resetZIndex), this._map._panes.markerPane.removeChild(this._icon), this._icon = null\r\n"+
            "            },\r\n"+
            "            _removeShadow: function() {\r\n"+
            "                this._shadow && this._map._panes.shadowPane.removeChild(this._shadow), this._shadow = null\r\n"+
            "            },\r\n"+
            "            _setPos: function(t) {\r\n"+
            "                o.DomUtil.setPosition(this._icon, t), this._shadow && o.DomUtil.setPosition(this._shadow, t), this._zIndex = t.y + this.options.zIndexOffset, this._resetZIndex()\r\n"+
            "            },\r\n"+
            "            _updateZIndex: function(t) {\r\n"+
            "                this._icon.style.zIndex = this._zIndex + t\r\n"+
            "            },\r\n"+
            "            _animateZoom: function(t) {\r\n"+
            "                var e = this._map._latLngToNewLayerPoint(this._latlng, t.zoom, t.center).round();\r\n"+
            "                this._setPos(e)\r\n"+
            "            },\r\n"+
            "            _initInteraction: function() {\r\n"+
            "                if (this.options.clickable) {\r\n"+
            "                    var t = this._icon,\r\n"+
            "                        e = [\"dblclick\", \"mousedown\", \"mouseover\", \"mouseout\", \"contextmenu\"];\r\n"+
            "                    o.DomUtil.addClass(t, \"leaflet-clickable\"), o.DomEvent.on(t, \"click\", this._onMouseClick, this), o.DomEvent.on(t, \"keypress\", this._onKeyPress, this);\r\n"+
            "                    for (var i = 0; i < e.length; i++) o.DomEvent.on(t, e[i], this._fireMouseEvent, this);\r\n"+
            "                    o.Handler.MarkerDrag && (this.dragging = new o.Handler.MarkerDrag(this), this.options.draggable && this.dragging.enable())\r\n"+
            "                }\r\n"+
            "            },\r\n"+
            "            _onMouseClick: function(t) {\r\n"+
            "                var e = this.dragging && this.dragging.moved();\r\n"+
            "                (this.hasEventList");
          out.print(
            "eners(t.type) || e) && o.DomEvent.stopPropagation(t), e || (this.dragging && this.dragging._enabled || !this._map.dragging || !this._map.dragging.moved()) && this.fire(t.type, {\r\n"+
            "                    originalEvent: t,\r\n"+
            "                    latlng: this._latlng\r\n"+
            "                })\r\n"+
            "            },\r\n"+
            "            _onKeyPress: function(t) {\r\n"+
            "                13 === t.keyCode && this.fire(\"click\", {\r\n"+
            "                    originalEvent: t,\r\n"+
            "                    latlng: this._latlng\r\n"+
            "                })\r\n"+
            "            },\r\n"+
            "            _fireMouseEvent: function(t) {\r\n"+
            "                this.fire(t.type, {\r\n"+
            "                    originalEvent: t,\r\n"+
            "                    latlng: this._latlng\r\n"+
            "                }), \"contextmenu\" === t.type && this.hasEventListeners(t.type) && o.DomEvent.preventDefault(t), \"mousedown\" !== t.type ? o.DomEvent.stopPropagation(t) : o.DomEvent.preventDefault(t)\r\n"+
            "            },\r\n"+
            "            setOpacity: function(t) {\r\n"+
            "                return this.options.opacity = t, this._map && this._updateOpacity(), this\r\n"+
            "            },\r\n"+
            "            _updateOpacity: function() {\r\n"+
            "                o.DomUtil.setOpacity(this._icon, this.options.opacity), this._shadow && o.DomUtil.setOpacity(this._shadow, this.options.opacity)\r\n"+
            "            },\r\n"+
            "            _bringToFront: function() {\r\n"+
            "                this._updateZIndex(this.options.riseOffset)\r\n"+
            "            },\r\n"+
            "            _resetZIndex: function() {\r\n"+
            "                this._updateZIndex(0)\r\n"+
            "            }\r\n"+
            "        }), o.marker = function(t, e) {\r\n"+
            "            return new o.Marker(t, e)\r\n"+
            "        }, o.DivIcon = o.Icon.extend({\r\n"+
            "            options: {\r\n"+
            "                iconSize: [12, 12],\r\n"+
            "                className: \"leaflet-div-icon\",\r\n"+
            "                html: !1\r\n"+
            "            },\r\n"+
            "            createIcon: function(t) {\r\n"+
            "                var i = t && \"DIV\" === t.tagName ? t : e.createElement(\"div\"),\r\n"+
            "                    n = this.options;\r\n"+
            "                return n.html !== !1 ? i.innerHTML = n.html : i.innerHTML = \"\", n.bgPos && (i.style.backgroundPosition = -n.bgPos.x + \"px \" + -n.bgPos.y + \"px\"), this._setIconStyles(i, \"icon\"), i\r\n"+
            "            },\r\n"+
            "            createShadow: function() {\r\n"+
            "                return null\r\n"+
            "            }\r\n"+
            "        }), o.divIcon = function(t) {\r\n"+
            "            return new o.DivIcon(t)\r\n"+
            "        }, o.Map.mergeOptions({\r\n"+
            "            closePopupOnClick: !0\r\n"+
            "        }), o.Popup = o.Class.extend({\r\n"+
            "            includes: o.Mixin.Events,\r\n"+
            "            options: {\r\n"+
            "                minWidth: 50,\r\n"+
            "                maxWidth: 300,\r\n"+
            "                autoPan: !0,\r\n"+
            "                closeButton: !0,\r\n"+
            "                offset: [0, 7],\r\n"+
            "                autoPanPadding: [5, 5],\r\n"+
            "                keepInView: !1,\r\n"+
            "                className: \"\",\r\n"+
            "                zoomAnimation: !0\r\n"+
            "            },\r\n"+
            "            initialize: function(t, e) {\r\n"+
            "                o.setOptions(this, t), this._source = e, this._animated = o.Browser.any3d && this.options.zoomAnimation, this._isOpen = !1\r\n"+
            "            },\r\n"+
            "            onAdd: function(t) {\r\n"+
            "                this._map = t, this._container || this._initLayout();\r\n"+
            "                var e = t.options.fadeAnimation;\r\n"+
            "                e && o.DomUtil.setOpacity(this._container, 0), t._panes.popupPane.appendChild(this._container), t.on(this._getEvents(), this), this.update(), e && o.DomUtil.setOpacity(this._container, 1), this.fire(\"open\"), t.fire(\"popupopen\", {\r\n"+
            "                    popup: this\r\n"+
            "                }), this._source && this._source.fire(\"popupopen\", {\r\n"+
            "                    popup: this\r\n"+
            "                })\r\n"+
            "            },\r\n"+
            "            addTo: function(t) {\r\n"+
            "                return t.addLayer(this), this\r\n"+
            "            },\r\n"+
            "            openOn: function(t) {\r\n"+
            "                return t.openPopup(this), this\r\n"+
            "            },\r\n"+
            "            onRemove: function(t) {\r\n"+
            "                t._panes.popupPane.removeChild(this._container), o.Util.falseFn(this._container.offsetWidth), t.off(this._getEvents(), this), t.options.fadeAnimation && o.DomUtil.setOpacity(this._container, 0), this._map = null, this.fire(\"close\"), t.fire(\"popupclose\", {\r\n"+
            "                    popup: this\r\n"+
            "                }), this._source && this._source.fire(\"popupclose\", {\r\n"+
            "                    popup: this\r\n"+
            "                })\r\n"+
            "            },\r\n"+
            "            getLatLng: function() {\r\n"+
            "                return this._latlng\r\n"+
            "            },\r\n"+
            "            setLatLng: function(t) {\r\n"+
            "                return this._latlng = o.latLng(t), this._map && (this._updatePosition(), this._adjustPan()), this\r\n"+
            "            },\r\n"+
            "            getContent: function() {\r\n"+
            "                return this._content\r\n"+
            "            },\r\n"+
            "            setContent: function(t) {\r\n"+
            "                return this._content = t, this.update(), this\r\n"+
            "            },\r\n"+
            "            update: function() {\r\n"+
            "                this._map && (this._container.style.visibility = \"hidden\", this._updateContent(), this._updateLayout(), this._updatePosition(), this._container.style.visibility = \"\", this._adjustPan())\r\n"+
            "            },\r\n"+
            "            _getEvents: function() {\r\n"+
            "                var t = {\r\n"+
            "                    viewreset: this._updatePosition\r\n"+
            "                };\r\n"+
            "                return this._animated && (t.zoomanim = this._zoomAnimation), (\"closeOnClick\" in this.options ? this.options.closeOnClick : this._map.options.closePopupOnClick) && (t.preclick = this._close), this.options.keepInView && (t.moveend = this._adjustPan), t\r\n"+
            "            },\r\n"+
            "            _close: function() {\r\n"+
            "                this._map && this._map.closePopup(this)\r\n"+
            "            },\r\n"+
            "            _initLayout: function() {\r\n"+
            "                var t, e = \"leaflet-popup\",\r\n"+
            "                    i = e + \" \" + this.options.className + \" leaflet-zoom-\" + (this._animated ? \"animated\" : \"hide\"),\r\n"+
            "                    n = this._container = o.DomUtil.create(\"div\", i);\r\n"+
            "                this.options.closeButton && (t = this._closeButton = o.DomUtil.create(\"a\", e + \"-close-button\", n), t.href = \"#close\", t.innerHTML = \"&#215;\", o.DomEvent.disableClickPropagation(t), o.DomEvent.on(t, \"click\", this._onCloseButtonClick, this));\r\n"+
            "                var s = this._wrapper = o.DomUtil.create(\"div\", e + \"-content-wrapper\", n);\r\n"+
            "                o.DomEvent.disableClickPropagation(s), this._contentNode = o.DomUtil.create(\"div\", e + \"-content\", s), o.DomEvent.disableScrollPropagation(this._contentNode), o.DomEvent.on(s, \"contextmenu\", o.DomEvent.stopPropagation), this._tipContainer = o.DomUtil.create(\"div\", e + \"-tip-container\", n), this._tip = o.DomUtil.create(\"div\", e + \"-tip\", this._tipContainer)\r\n"+
            "            },\r\n"+
            "            _updateContent: function() {\r\n"+
            "                if (this._content) {\r\n"+
            "                    if (\"string\" == typeof this._content) this._contentNode.innerHTML = this._content;\r\n"+
            "                    else {\r\n"+
            "                        for (; this._contentNode.hasChildNodes();) this._contentNode.removeChild(this._contentNode.firstChild);\r\n"+
            "                        this._contentNode.appendChild(this._content)\r\n"+
            "                    }\r\n"+
            "                    this.fire(\"contentupdate\")\r\n"+
            "                }\r\n"+
            "            },\r\n"+
            "            _updateLayout: function() {\r\n"+
            "                var t = this._contentNode,\r\n"+
            "                    e = t.style;\r\n"+
            "                e.width = \"\", e.whiteSpace = \"nowrap\";\r\n"+
            "                var i = t.offsetWidth;\r\n"+
            "                i = Math.min(i, this.options.maxWidth), i = Math.max(i, this.options.minWidth), e.width = i + 1 + \"px\", e.whiteSpace = \"\", e.height = \"\";\r\n"+
            "                var n = t.offsetHeight,\r\n"+
            "                    s = this.options.maxHeight,\r\n"+
            "                    a = \"leaflet-popup-scrolled\";\r\n"+
            "                s && n > s ? (e.height = s + \"px\", o.DomUtil.addClass(t, a)) : o.DomUtil.removeClass(t, a), this._containerWidth = this._container.offsetWidth\r\n"+
            "            },\r\n"+
            "            _updatePosition: function() {\r\n"+
            "                if (this._map) {\r\n"+
            "                    var t = this._map.latLngToLayerPoint(this._latlng),\r\n"+
            "                        e = this._animated,\r\n"+
            "                        i = o.point(this.options.offset);\r\n"+
            "                    e && o.DomUtil.setPosition(this._container, t), this._containerBottom = -i.y - (e ? 0 : t.y), this._containerLeft = -Math.round(this._containerWidth / 2) + i.x + (e ? 0 : t.x), this._container.style.bottom = this._containerBottom + \"px\", this._container.style.left = this._containerLeft + \"px\"\r\n"+
            "                }\r\n"+
            "            },\r\n"+
            "            _zoomAnimation: function(t) {\r\n"+
            "                var e = this._map._latLngToNewLayerPoint(this._latlng, t.zoom, t.center);\r\n"+
            "                o.DomUtil.setPosition(this._container, e)\r\n"+
            "            },\r\n"+
            "            _adjustPan: function() {\r\n"+
            "                if (this.options.autoPan) {\r\n"+
            "                    var t = this._map,\r\n"+
            "                        e = this._container.offsetHeight,\r\n"+
            "                        i = this._containerWidth,\r\n"+
            "                        n = new o.Point(this._containerLeft, -e - this._containerBottom);\r\n"+
            "                    this._animated && n._add(o.DomUtil.getPosition(this._container));\r\n"+
            "                    var s = t.layerPointToContainerPoint(n),\r\n"+
            "                        a = o.point(this.options.autoPanPadding),\r\n"+
            "                        r = o.point(this.options.autoPanPaddingTopLeft || a),\r\n"+
            "                        h = o.point(this.options.autoPanPaddingBottomRight || a),\r\n"+
            "                        l = t.getSize(),\r\n"+
            "                        u = 0,\r\n"+
            "                        c = 0;\r\n"+
            "                    s.x + i + h.x > l.x && (u = s.x + i - l.x + h.x), s.x - u - r.x < 0 && (u = s.x - r.x), s.y + e + h.y > l.y && (c = s.y + e - l.y + h.y), s.y - c - r.y < 0 && (c = s.y - r.y), (u || c) && t.fire(\"autopanstart\").panBy([u, c])\r\n"+
            "                }\r\n"+
            "            },\r\n"+
            "            _onCloseButtonClick: function(t) {\r\n"+
            "                this._close(), o.DomEvent.stop(t)\r\n"+
            "            }\r\n"+
            "        }), o.popup = function(t, e) {\r\n"+
            "            return new o.Popup(t, e)\r\n"+
            "        }, o.Map.include({\r\n"+
            "            openPopup: function(t, e, i) {\r\n"+
            "                if (this.closePopup(), !(t instanceof o.Popup)) {\r\n"+
            "                    var n = t;\r\n"+
            "                    t = new o.Popup(i).setLatLng(e).setContent(n)\r\n"+
            "    ");
          out.print(
            "            }\r\n"+
            "                return t._isOpen = !0, this._popup = t, this.addLayer(t)\r\n"+
            "            },\r\n"+
            "            closePopup: function(t) {\r\n"+
            "                return t && t !== this._popup || (t = this._popup, this._popup = null), t && (this.removeLayer(t), t._isOpen = !1), this\r\n"+
            "            }\r\n"+
            "        }), o.Marker.include({\r\n"+
            "            openPopup: function() {\r\n"+
            "                return this._popup && this._map && !this._map.hasLayer(this._popup) && (this._popup.setLatLng(this._latlng), this._map.openPopup(this._popup)), this\r\n"+
            "            },\r\n"+
            "            closePopup: function() {\r\n"+
            "                return this._popup && this._popup._close(), this\r\n"+
            "            },\r\n"+
            "            togglePopup: function() {\r\n"+
            "                return this._popup && (this._popup._isOpen ? this.closePopup() : this.openPopup()), this\r\n"+
            "            },\r\n"+
            "            bindPopup: function(t, e) {\r\n"+
            "                var i = o.point(this.options.icon.options.popupAnchor || [0, 0]);\r\n"+
            "                return i = i.add(o.Popup.prototype.options.offset), e && e.offset && (i = i.add(e.offset)), e = o.extend({\r\n"+
            "                    offset: i\r\n"+
            "                }, e), this._popupHandlersAdded || (this.on(\"click\", this.togglePopup, this).on(\"remove\", this.closePopup, this).on(\"move\", this._movePopup, this), this._popupHandlersAdded = !0), t instanceof o.Popup ? (o.setOptions(t, e), this._popup = t, t._source = this) : this._popup = new o.Popup(e, this).setContent(t), this\r\n"+
            "            },\r\n"+
            "            setPopupContent: function(t) {\r\n"+
            "                return this._popup && this._popup.setContent(t), this\r\n"+
            "            },\r\n"+
            "            unbindPopup: function() {\r\n"+
            "                return this._popup && (this._popup = null, this.off(\"click\", this.togglePopup, this).off(\"remove\", this.closePopup, this).off(\"move\", this._movePopup, this), this._popupHandlersAdded = !1), this\r\n"+
            "            },\r\n"+
            "            getPopup: function() {\r\n"+
            "                return this._popup\r\n"+
            "            },\r\n"+
            "            _movePopup: function(t) {\r\n"+
            "                this._popup.setLatLng(t.latlng)\r\n"+
            "            }\r\n"+
            "        }), o.LayerGroup = o.Class.extend({\r\n"+
            "            initialize: function(t) {\r\n"+
            "                this._layers = {};\r\n"+
            "                var e, i;\r\n"+
            "                if (t)\r\n"+
            "                    for (e = 0, i = t.length; i > e; e++) this.addLayer(t[e])\r\n"+
            "            },\r\n"+
            "            addLayer: function(t) {\r\n"+
            "                var e = this.getLayerId(t);\r\n"+
            "                return this._layers[e] = t, this._map && this._map.addLayer(t), this\r\n"+
            "            },\r\n"+
            "            removeLayer: function(t) {\r\n"+
            "                var e = t in this._layers ? t : this.getLayerId(t);\r\n"+
            "                return this._map && this._layers[e] && this._map.removeLayer(this._layers[e]), delete this._layers[e], this\r\n"+
            "            },\r\n"+
            "            hasLayer: function(t) {\r\n"+
            "                return t ? t in this._layers || this.getLayerId(t) in this._layers : !1\r\n"+
            "            },\r\n"+
            "            clearLayers: function() {\r\n"+
            "                return this.eachLayer(this.removeLayer, this), this\r\n"+
            "            },\r\n"+
            "            invoke: function(t) {\r\n"+
            "                var e, i, n = Array.prototype.slice.call(arguments, 1);\r\n"+
            "                for (e in this._layers) i = this._layers[e], i[t] && i[t].apply(i, n);\r\n"+
            "                return this\r\n"+
            "            },\r\n"+
            "            onAdd: function(t) {\r\n"+
            "                this._map = t, this.eachLayer(t.addLayer, t)\r\n"+
            "            },\r\n"+
            "            onRemove: function(t) {\r\n"+
            "                this.eachLayer(t.removeLayer, t), this._map = null\r\n"+
            "            },\r\n"+
            "            addTo: function(t) {\r\n"+
            "                return t.addLayer(this), this\r\n"+
            "            },\r\n"+
            "            eachLayer: function(t, e) {\r\n"+
            "                for (var i in this._layers) t.call(e, this._layers[i]);\r\n"+
            "                return this\r\n"+
            "            },\r\n"+
            "            getLayer: function(t) {\r\n"+
            "                return this._layers[t]\r\n"+
            "            },\r\n"+
            "            getLayers: function() {\r\n"+
            "                var t = [];\r\n"+
            "                for (var e in this._layers) t.push(this._layers[e]);\r\n"+
            "                return t\r\n"+
            "            },\r\n"+
            "            setZIndex: function(t) {\r\n"+
            "                return this.invoke(\"setZIndex\", t)\r\n"+
            "            },\r\n"+
            "            getLayerId: function(t) {\r\n"+
            "                return o.stamp(t)\r\n"+
            "            }\r\n"+
            "        }), o.layerGroup = function(t) {\r\n"+
            "            return new o.LayerGroup(t)\r\n"+
            "        }, o.FeatureGroup = o.LayerGroup.extend({\r\n"+
            "            includes: o.Mixin.Events,\r\n"+
            "            statics: {\r\n"+
            "                EVENTS: \"click dblclick mouseover mouseout mousemove contextmenu popupopen popupclose\"\r\n"+
            "            },\r\n"+
            "            addLayer: function(t) {\r\n"+
            "                return this.hasLayer(t) ? this : (\"on\" in t && t.on(o.FeatureGroup.EVENTS, this._propagateEvent, this), o.LayerGroup.prototype.addLayer.call(this, t), this._popupContent && t.bindPopup && t.bindPopup(this._popupContent, this._popupOptions), this.fire(\"layeradd\", {\r\n"+
            "                    layer: t\r\n"+
            "                }))\r\n"+
            "            },\r\n"+
            "            removeLayer: function(t) {\r\n"+
            "                return this.hasLayer(t) ? (t in this._layers && (t = this._layers[t]), \"off\" in t && t.off(o.FeatureGroup.EVENTS, this._propagateEvent, this), o.LayerGroup.prototype.removeLayer.call(this, t), this._popupContent && this.invoke(\"unbindPopup\"), this.fire(\"layerremove\", {\r\n"+
            "                    layer: t\r\n"+
            "                })) : this\r\n"+
            "            },\r\n"+
            "            bindPopup: function(t, e) {\r\n"+
            "                return this._popupContent = t, this._popupOptions = e, this.invoke(\"bindPopup\", t, e)\r\n"+
            "            },\r\n"+
            "            openPopup: function(t) {\r\n"+
            "                for (var e in this._layers) {\r\n"+
            "                    this._layers[e].openPopup(t);\r\n"+
            "                    break\r\n"+
            "                }\r\n"+
            "                return this\r\n"+
            "            },\r\n"+
            "            setStyle: function(t) {\r\n"+
            "                return this.invoke(\"setStyle\", t)\r\n"+
            "            },\r\n"+
            "            bringToFront: function() {\r\n"+
            "                return this.invoke(\"bringToFront\")\r\n"+
            "            },\r\n"+
            "            bringToBack: function() {\r\n"+
            "                return this.invoke(\"bringToBack\")\r\n"+
            "            },\r\n"+
            "            getBounds: function() {\r\n"+
            "                var t = new o.LatLngBounds;\r\n"+
            "                return this.eachLayer(function(e) {\r\n"+
            "                    t.extend(e instanceof o.Marker ? e.getLatLng() : e.getBounds())\r\n"+
            "                }), t\r\n"+
            "            },\r\n"+
            "            _propagateEvent: function(t) {\r\n"+
            "                t = o.extend({\r\n"+
            "                    layer: t.target,\r\n"+
            "                    target: this\r\n"+
            "                }, t), this.fire(t.type, t)\r\n"+
            "            }\r\n"+
            "        }), o.featureGroup = function(t) {\r\n"+
            "            return new o.FeatureGroup(t)\r\n"+
            "        }, o.Path = o.Class.extend({\r\n"+
            "            includes: [o.Mixin.Events],\r\n"+
            "            statics: {\r\n"+
            "                CLIP_PADDING: function() {\r\n"+
            "                    var e = o.Browser.mobile ? 1280 : 2e3,\r\n"+
            "                        i = (e / Math.max(t.outerWidth, t.outerHeight) - 1) / 2;\r\n"+
            "                    return Math.max(0, Math.min(.5, i))\r\n"+
            "                }()\r\n"+
            "            },\r\n"+
            "            options: {\r\n"+
            "                stroke: !0,\r\n"+
            "                color: \"#0033ff\",\r\n"+
            "                dashArray: null,\r\n"+
            "                lineCap: null,\r\n"+
            "                lineJoin: null,\r\n"+
            "                weight: 5,\r\n"+
            "                opacity: .5,\r\n"+
            "                fill: !1,\r\n"+
            "                fillColor: null,\r\n"+
            "                fillOpacity: .2,\r\n"+
            "                clickable: !0\r\n"+
            "            },\r\n"+
            "            initialize: function(t) {\r\n"+
            "                o.setOptions(this, t)\r\n"+
            "            },\r\n"+
            "            onAdd: function(t) {\r\n"+
            "                this._map = t, this._container || (this._initElements(), this._initEvents()), this.projectLatlngs(), this._updatePath(), this._container && this._map._pathRoot.appendChild(this._container), this.fire(\"add\"), t.on({\r\n"+
            "                    viewreset: this.projectLatlngs,\r\n"+
            "                    moveend: this._updatePath\r\n"+
            "                }, this)\r\n"+
            "            },\r\n"+
            "            addTo: function(t) {\r\n"+
            "                return t.addLayer(this), this\r\n"+
            "            },\r\n"+
            "            onRemove: function(t) {\r\n"+
            "                t._pathRoot.removeChild(this._container), this.fire(\"remove\"), this._map = null, o.Browser.vml && (this._container = null, this._stroke = null, this._fill = null), t.off({\r\n"+
            "                    viewreset: this.projectLatlngs,\r\n"+
            "                    moveend: this._updatePath\r\n"+
            "                }, this)\r\n"+
            "            },\r\n"+
            "            projectLatlngs: function() {},\r\n"+
            "            setStyle: function(t) {\r\n"+
            "                return o.setOptions(this, t), this._container && this._updateStyle(), this\r\n"+
            "            },\r\n"+
            "            redraw: function() {\r\n"+
            "                return this._map && (this.projectLatlngs(), this._updatePath()), this\r\n"+
            "            }\r\n"+
            "        }), o.Map.include({\r\n"+
            "            _updatePathViewport: function() {\r\n"+
            "                var t = o.Path.CLIP_PADDING,\r\n"+
            "                    e = this.getSize(),\r\n"+
            "                    i = o.DomUtil.getPosition(this._mapPane),\r\n"+
            "                    n = i.multiplyBy(-1)._subtract(e.multiplyBy(t)._round()),\r\n"+
            "                    s = n.add(e.multiplyBy(1 + 2 * t)._round());\r\n"+
            "                this._pathViewport = new o.Bounds(n, s)\r\n"+
            "            }\r\n"+
            "        }), o.Path.SVG_NS = \"http://www.w3.org/2000/svg\", o.Browser.svg = !(!e.createElementNS || !e.createElementNS(o.Path.SVG_NS, \"svg\").createSVGRect), o.Path = o.Path.extend({\r\n"+
            "            statics: {\r\n"+
            "                SVG: o.Browser.svg\r\n"+
            "            },\r\n"+
            "            bringToFront: function() {\r\n"+
            "                var t = this._map._pathRoot,\r\n"+
            "                    e = this._container;\r\n"+
            "                return e && t.lastChild !== e && t.appendChild(e), this\r\n"+
            "            },\r\n"+
            "            bringToBack: function() {\r\n"+
            "                var t = this._map._pathRoot,\r\n"+
            "                    e = this._container,\r\n"+
            "                    i = t.firstChild;\r\n"+
            "                return e && i !== e && t.insertBefore(e, i), this\r\n"+
            "            },\r\n"+
            "            getPathString: function() {},\r\n"+
            "            _createElement: function(t) {\r\n"+
            "                return e.createElementNS(o.Path.SVG_NS, t)\r\n"+
            "            },\r\n"+
            "            _initElements: function() {\r\n"+
            "");
          out.print(
            "                this._map._initPathRoot(), this._initPath(), this._initStyle()\r\n"+
            "            },\r\n"+
            "            _initPath: function() {\r\n"+
            "                this._container = this._createElement(\"g\"), this._path = this._createElement(\"path\"), this.options.className && o.DomUtil.addClass(this._path, this.options.className), this._container.appendChild(this._path)\r\n"+
            "            },\r\n"+
            "            _initStyle: function() {\r\n"+
            "                this.options.stroke && (this._path.setAttribute(\"stroke-linejoin\", \"round\"), this._path.setAttribute(\"stroke-linecap\", \"round\")), this.options.fill && this._path.setAttribute(\"fill-rule\", \"evenodd\"), this.options.pointerEvents && this._path.setAttribute(\"pointer-events\", this.options.pointerEvents), this.options.clickable || this.options.pointerEvents || this._path.setAttribute(\"pointer-events\", \"none\"), this._updateStyle()\r\n"+
            "            },\r\n"+
            "            _updateStyle: function() {\r\n"+
            "                this.options.stroke ? (this._path.setAttribute(\"stroke\", this.options.color), this._path.setAttribute(\"stroke-opacity\", this.options.opacity), this._path.setAttribute(\"stroke-width\", this.options.weight), this.options.dashArray ? this._path.setAttribute(\"stroke-dasharray\", this.options.dashArray) : this._path.removeAttribute(\"stroke-dasharray\"), this.options.lineCap && this._path.setAttribute(\"stroke-linecap\", this.options.lineCap), this.options.lineJoin && this._path.setAttribute(\"stroke-linejoin\", this.options.lineJoin)) : this._path.setAttribute(\"stroke\", \"none\"), this.options.fill ? (this._path.setAttribute(\"fill\", this.options.fillColor || this.options.color), this._path.setAttribute(\"fill-opacity\", this.options.fillOpacity)) : this._path.setAttribute(\"fill\", \"none\")\r\n"+
            "            },\r\n"+
            "            _updatePath: function() {\r\n"+
            "                var t = this.getPathString();\r\n"+
            "                t || (t = \"M0 0\"), this._path.setAttribute(\"d\", t)\r\n"+
            "            },\r\n"+
            "            _initEvents: function() {\r\n"+
            "                if (this.options.clickable) {\r\n"+
            "                    (o.Browser.svg || !o.Browser.vml) && o.DomUtil.addClass(this._path, \"leaflet-clickable\"), o.DomEvent.on(this._container, \"click\", this._onMouseClick, this);\r\n"+
            "                    for (var t = [\"dblclick\", \"mousedown\", \"mouseover\", \"mouseout\", \"mousemove\", \"contextmenu\"], e = 0; e < t.length; e++) o.DomEvent.on(this._container, t[e], this._fireMouseEvent, this)\r\n"+
            "                }\r\n"+
            "            },\r\n"+
            "            _onMouseClick: function(t) {\r\n"+
            "                this._map.dragging && this._map.dragging.moved() || this._fireMouseEvent(t)\r\n"+
            "            },\r\n"+
            "            _fireMouseEvent: function(t) {\r\n"+
            "                if (this._map && this.hasEventListeners(t.type)) {\r\n"+
            "                    var e = this._map,\r\n"+
            "                        i = e.mouseEventToContainerPoint(t),\r\n"+
            "                        n = e.containerPointToLayerPoint(i),\r\n"+
            "                        s = e.layerPointToLatLng(n);\r\n"+
            "                    this.fire(t.type, {\r\n"+
            "                        latlng: s,\r\n"+
            "                        layerPoint: n,\r\n"+
            "                        containerPoint: i,\r\n"+
            "                        originalEvent: t\r\n"+
            "                    }), \"contextmenu\" === t.type && o.DomEvent.preventDefault(t), \"mousemove\" !== t.type && o.DomEvent.stopPropagation(t)\r\n"+
            "                }\r\n"+
            "            }\r\n"+
            "        }), o.Map.include({\r\n"+
            "            _initPathRoot: function() {\r\n"+
            "                this._pathRoot || (this._pathRoot = o.Path.prototype._createElement(\"svg\"), this._panes.overlayPane.appendChild(this._pathRoot), this.options.zoomAnimation && o.Browser.any3d ? (o.DomUtil.addClass(this._pathRoot, \"leaflet-zoom-animated\"),\r\n"+
            "                    this.on({\r\n"+
            "                        zoomanim: this._animatePathZoom,\r\n"+
            "                        zoomend: this._endPathZoom\r\n"+
            "                    })) : o.DomUtil.addClass(this._pathRoot, \"leaflet-zoom-hide\"), this.on(\"moveend\", this._updateSvgViewport), this._updateSvgViewport())\r\n"+
            "            },\r\n"+
            "            _animatePathZoom: function(t) {\r\n"+
            "                var e = this.getZoomScale(t.zoom),\r\n"+
            "                    i = this._getCenterOffset(t.center)._multiplyBy(-e)._add(this._pathViewport.min);\r\n"+
            "                this._pathRoot.style[o.DomUtil.TRANSFORM] = o.DomUtil.getTranslateString(i) + \" scale(\" + e + \") \", this._pathZooming = !0\r\n"+
            "            },\r\n"+
            "            _endPathZoom: function() {\r\n"+
            "                this._pathZooming = !1\r\n"+
            "            },\r\n"+
            "            _updateSvgViewport: function() {\r\n"+
            "                if (!this._pathZooming) {\r\n"+
            "                    this._updatePathViewport();\r\n"+
            "                    var t = this._pathViewport,\r\n"+
            "                        e = t.min,\r\n"+
            "                        i = t.max,\r\n"+
            "                        n = i.x - e.x,\r\n"+
            "                        s = i.y - e.y,\r\n"+
            "                        a = this._pathRoot,\r\n"+
            "                        r = this._panes.overlayPane;\r\n"+
            "                    o.Browser.mobileWebkit && r.removeChild(a), o.DomUtil.setPosition(a, e), a.setAttribute(\"width\", n), a.setAttribute(\"height\", s), a.setAttribute(\"viewBox\", [e.x, e.y, n, s].join(\" \")), o.Browser.mobileWebkit && r.appendChild(a)\r\n"+
            "                }\r\n"+
            "            }\r\n"+
            "        }), o.Path.include({\r\n"+
            "            bindPopup: function(t, e) {\r\n"+
            "                return t instanceof o.Popup ? this._popup = t : ((!this._popup || e) && (this._popup = new o.Popup(e, this)), this._popup.setContent(t)), this._popupHandlersAdded || (this.on(\"click\", this._openPopup, this).on(\"remove\", this.closePopup, this), this._popupHandlersAdded = !0), this\r\n"+
            "            },\r\n"+
            "            unbindPopup: function() {\r\n"+
            "                return this._popup && (this._popup = null, this.off(\"click\", this._openPopup).off(\"remove\", this.closePopup), this._popupHandlersAdded = !1), this\r\n"+
            "            },\r\n"+
            "            openPopup: function(t) {\r\n"+
            "                return this._popup && (t = t || this._latlng || this._latlngs[Math.floor(this._latlngs.length / 2)], this._openPopup({\r\n"+
            "                    latlng: t\r\n"+
            "                })), this\r\n"+
            "            },\r\n"+
            "            closePopup: function() {\r\n"+
            "                return this._popup && this._popup._close(), this\r\n"+
            "            },\r\n"+
            "            _openPopup: function(t) {\r\n"+
            "                this._popup.setLatLng(t.latlng), this._map.openPopup(this._popup)\r\n"+
            "            }\r\n"+
            "        }), o.Browser.vml = !o.Browser.svg && function() {\r\n"+
            "            try {\r\n"+
            "                var t = e.createElement(\"div\");\r\n"+
            "                t.innerHTML = '<v:shape adj=\"1\"/>';\r\n"+
            "                var i = t.firstChild;\r\n"+
            "                return i.style.behavior = \"url(#default#VML)\", i && \"object\" == typeof i.adj\r\n"+
            "            } catch (n) {\r\n"+
            "                return !1\r\n"+
            "            }\r\n"+
            "        }(), o.Path = o.Browser.svg || !o.Browser.vml ? o.Path : o.Path.extend({\r\n"+
            "            statics: {\r\n"+
            "                VML: !0,\r\n"+
            "                CLIP_PADDING: .02\r\n"+
            "            },\r\n"+
            "            _createElement: function() {\r\n"+
            "                try {\r\n"+
            "                    return e.namespaces.add(\"lvml\", \"urn:schemas-microsoft-com:vml\"),\r\n"+
            "                        function(t) {\r\n"+
            "                            return e.createElement(\"<lvml:\" + t + ' class=\"lvml\">')\r\n"+
            "                        }\r\n"+
            "                } catch (t) {\r\n"+
            "                    return function(t) {\r\n"+
            "                        return e.createElement(\"<\" + t + ' xmlns=\"urn:schemas-microsoft.com:vml\" class=\"lvml\">')\r\n"+
            "                    }\r\n"+
            "                }\r\n"+
            "            }(),\r\n"+
            "            _initPath: function() {\r\n"+
            "                var t = this._container = this._createElement(\"shape\");\r\n"+
            "                o.DomUtil.addClass(t, \"leaflet-vml-shape\" + (this.options.className ? \" \" + this.options.className : \"\")), this.options.clickable && o.DomUtil.addClass(t, \"leaflet-clickable\"), t.coordsize = \"1 1\", this._path = this._createElement(\"path\"), t.appendChild(this._path), this._map._pathRoot.appendChild(t)\r\n"+
            "            },\r\n"+
            "            _initStyle: function() {\r\n"+
            "                this._updateStyle()\r\n"+
            "            },\r\n"+
            "            _updateStyle: function() {\r\n"+
            "                var t = this._stroke,\r\n"+
            "                    e = this._fill,\r\n"+
            "                    i = this.options,\r\n"+
            "                    n = this._container;\r\n"+
            "                n.stroked = i.stroke, n.filled = i.fill, i.stroke ? (t || (t = this._stroke = this._createElement(\"stroke\"), t.endcap = \"round\", n.appendChild(t)), t.weight = i.weight + \"px\", t.color = i.color, t.opacity = i.opacity, i.dashArray ? t.dashStyle = o.Util.isArray(i.dashArray) ? i.dashArray.join(\" \") : i.dashArray.replace(/( *, *)/g, \" \") : t.dashStyle = \"\", i.lineCap && (t.endcap = i.lineCap.replace(\"butt\", \"flat\")), i.lineJoin && (t.joinstyle = i.lineJoin)) : t && (n.removeChild(t), this._stroke = null), i.fill ? (e || (e = this._fill = this._createElement(\"fill\"), n.appendChild(e)), e.color = i.fillColor || i.color, e.opacity = i.fillOpacity) : e && (n.removeChild(e), this._fill = null)\r\n"+
            "            },\r\n"+
            "            _updatePath: function() {\r\n"+
            "                var t = this._container.style;\r\n"+
            "                t.display = \"none\", this._path.v = this.getPathString() + \" \", t.display = \"\"\r\n"+
            "            }\r\n"+
            "        }), o.Map.include(o.Browser.svg || !o.Browser.vml ? {} : {\r\n"+
            "            _initPathRoot: function() {\r\n"+
            "                if (!this._pathRoot) {\r\n"+
            "                    var t = this._pathRoot = e.createElement(\"div\");\r\n"+
            "                    t.className = \"leaflet-vml-container\", this._panes.overlayPane.appendChild(t), this.on(\"moveend\", this._updatePathViewport), this._updatePathViewport()\r\n"+
            "                }\r\n"+
            "            }\r\n"+
            "        }), o.Browser.canvas = function() {\r\n"+
            "            return !!e.createElement(\"canvas\").getContext\r\n"+
            "        }(), o.Path = o.Path.SVG && !t.L_PREFER_CANVAS || !o.Browser.canvas ? o.Path : o.Path.extend({\r\n"+
            "            statics: {\r\n"+
            "                CANVAS: !0,\r\n"+
            "                SVG: !1\r\n"+
            "            },\r\n"+
            "            redraw: function() {\r\n"+
            "                return this._map && (this.projectLatlngs(), this._requestUpdate()), this\r\n"+
            "            },\r\n"+
            "            setStyle: function(t) {\r\n"+
            "                return o.setOptions(this, t), this._map && (this._updateStyle(), this._requestUpdate()), this\r\n"+
            "            },\r\n"+
            "            o");
          out.print(
            "nRemove: function(t) {\r\n"+
            "                t.off(\"viewreset\", this.projectLatlngs, this).off(\"moveend\", this._updatePath, this), this.options.clickable && (this._map.off(\"click\", this._onClick, this), this._map.off(\"mousemove\", this._onMouseMove, this)), this._requestUpdate(), this.fire(\"remove\"), this._map = null\r\n"+
            "            },\r\n"+
            "            _requestUpdate: function() {\r\n"+
            "                this._map && !o.Path._updateRequest && (o.Path._updateRequest = o.Util.requestAnimFrame(this._fireMapMoveEnd, this._map))\r\n"+
            "            },\r\n"+
            "            _fireMapMoveEnd: function() {\r\n"+
            "                o.Path._updateRequest = null, this.fire(\"moveend\")\r\n"+
            "            },\r\n"+
            "            _initElements: function() {\r\n"+
            "                this._map._initPathRoot(), this._ctx = this._map._canvasCtx\r\n"+
            "            },\r\n"+
            "            _updateStyle: function() {\r\n"+
            "                var t = this.options;\r\n"+
            "                t.stroke && (this._ctx.lineWidth = t.weight, this._ctx.strokeStyle = t.color), t.fill && (this._ctx.fillStyle = t.fillColor || t.color), t.lineCap && (this._ctx.lineCap = t.lineCap), t.lineJoin && (this._ctx.lineJoin = t.lineJoin)\r\n"+
            "            },\r\n"+
            "            _drawPath: function() {\r\n"+
            "                var t, e, i, n, s, a;\r\n"+
            "                for (this._ctx.beginPath(), t = 0, i = this._parts.length; i > t; t++) {\r\n"+
            "                    for (e = 0, n = this._parts[t].length; n > e; e++) s = this._parts[t][e], a = (0 === e ? \"move\" : \"line\") + \"To\", this._ctx[a](s.x, s.y);\r\n"+
            "                    this instanceof o.Polygon && this._ctx.closePath()\r\n"+
            "                }\r\n"+
            "            },\r\n"+
            "            _checkIfEmpty: function() {\r\n"+
            "                return !this._parts.length\r\n"+
            "            },\r\n"+
            "            _updatePath: function() {\r\n"+
            "                if (!this._checkIfEmpty()) {\r\n"+
            "                    var t = this._ctx,\r\n"+
            "                        e = this.options;\r\n"+
            "                    this._drawPath(), t.save(), this._updateStyle(), e.fill && (t.globalAlpha = e.fillOpacity, t.fill(e.fillRule || \"evenodd\")), e.stroke && (t.globalAlpha = e.opacity, t.stroke()), t.restore()\r\n"+
            "                }\r\n"+
            "            },\r\n"+
            "            _initEvents: function() {\r\n"+
            "                this.options.clickable && (this._map.on(\"mousemove\", this._onMouseMove, this), this._map.on(\"click dblclick contextmenu\", this._fireMouseEvent, this))\r\n"+
            "            },\r\n"+
            "            _fireMouseEvent: function(t) {\r\n"+
            "                this._containsPoint(t.layerPoint) && this.fire(t.type, t)\r\n"+
            "            },\r\n"+
            "            _onMouseMove: function(t) {\r\n"+
            "                this._map && !this._map._animatingZoom && (this._containsPoint(t.layerPoint) ? (this._ctx.canvas.style.cursor = \"pointer\", this._mouseInside = !0, this.fire(\"mouseover\", t)) : this._mouseInside && (this._ctx.canvas.style.cursor = \"\", this._mouseInside = !1, this.fire(\"mouseout\", t)))\r\n"+
            "            }\r\n"+
            "        }), o.Map.include(o.Path.SVG && !t.L_PREFER_CANVAS || !o.Browser.canvas ? {} : {\r\n"+
            "            _initPathRoot: function() {\r\n"+
            "                var t, i = this._pathRoot;\r\n"+
            "                i || (i = this._pathRoot = e.createElement(\"canvas\"), i.style.position = \"absolute\", t = this._canvasCtx = i.getContext(\"2d\"), t.lineCap = \"round\", t.lineJoin = \"round\", this._panes.overlayPane.appendChild(i), this.options.zoomAnimation && (this._pathRoot.className = \"leaflet-zoom-animated\", this.on(\"zoomanim\", this._animatePathZoom), this.on(\"zoomend\", this._endPathZoom)), this.on(\"moveend\", this._updateCanvasViewport), this._updateCanvasViewport())\r\n"+
            "            },\r\n"+
            "            _updateCanvasViewport: function() {\r\n"+
            "                if (!this._pathZooming) {\r\n"+
            "                    this._updatePathViewport();\r\n"+
            "                    var t = this._pathViewport,\r\n"+
            "                        e = t.min,\r\n"+
            "                        i = t.max.subtract(e),\r\n"+
            "                        n = this._pathRoot;\r\n"+
            "                    o.DomUtil.setPosition(n, e), n.width = i.x, n.height = i.y, n.getContext(\"2d\").translate(-e.x, -e.y)\r\n"+
            "                }\r\n"+
            "            }\r\n"+
            "        }), o.LineUtil = {\r\n"+
            "            simplify: function(t, e) {\r\n"+
            "                if (!e || !t.length) return t.slice();\r\n"+
            "                var i = e * e;\r\n"+
            "                return t = this._reducePoints(t, i), t = this._simplifyDP(t, i)\r\n"+
            "            },\r\n"+
            "            pointToSegmentDistance: function(t, e, i) {\r\n"+
            "                return Math.sqrt(this._sqClosestPointOnSegment(t, e, i, !0))\r\n"+
            "            },\r\n"+
            "            closestPointOnSegment: function(t, e, i) {\r\n"+
            "                return this._sqClosestPointOnSegment(t, e, i)\r\n"+
            "            },\r\n"+
            "            _simplifyDP: function(t, e) {\r\n"+
            "                var n = t.length,\r\n"+
            "                    o = typeof Uint8Array != i + \"\" ? Uint8Array : Array,\r\n"+
            "                    s = new o(n);\r\n"+
            "                s[0] = s[n - 1] = 1, this._simplifyDPStep(t, s, e, 0, n - 1);\r\n"+
            "                var a, r = [];\r\n"+
            "                for (a = 0; n > a; a++) s[a] && r.push(t[a]);\r\n"+
            "                return r\r\n"+
            "            },\r\n"+
            "            _simplifyDPStep: function(t, e, i, n, o) {\r\n"+
            "                var s, a, r, h = 0;\r\n"+
            "                for (a = n + 1; o - 1 >= a; a++) r = this._sqClosestPointOnSegment(t[a], t[n], t[o], !0), r > h && (s = a, h = r);\r\n"+
            "                h > i && (e[s] = 1, this._simplifyDPStep(t, e, i, n, s), this._simplifyDPStep(t, e, i, s, o))\r\n"+
            "            },\r\n"+
            "            _reducePoints: function(t, e) {\r\n"+
            "                for (var i = [t[0]], n = 1, o = 0, s = t.length; s > n; n++) this._sqDist(t[n], t[o]) > e && (i.push(t[n]), o = n);\r\n"+
            "                return s - 1 > o && i.push(t[s - 1]), i\r\n"+
            "            },\r\n"+
            "            clipSegment: function(t, e, i, n) {\r\n"+
            "                var o, s, a, r = n ? this._lastCode : this._getBitCode(t, i),\r\n"+
            "                    h = this._getBitCode(e, i);\r\n"+
            "                for (this._lastCode = h;;) {\r\n"+
            "                    if (!(r | h)) return [t, e];\r\n"+
            "                    if (r & h) return !1;\r\n"+
            "                    o = r || h, s = this._getEdgeIntersection(t, e, o, i), a = this._getBitCode(s, i), o === r ? (t = s, r = a) : (e = s, h = a)\r\n"+
            "                }\r\n"+
            "            },\r\n"+
            "            _getEdgeIntersection: function(t, e, i, n) {\r\n"+
            "                var s = e.x - t.x,\r\n"+
            "                    a = e.y - t.y,\r\n"+
            "                    r = n.min,\r\n"+
            "                    h = n.max;\r\n"+
            "                return 8 & i ? new o.Point(t.x + s * (h.y - t.y) / a, h.y) : 4 & i ? new o.Point(t.x + s * (r.y - t.y) / a, r.y) : 2 & i ? new o.Point(h.x, t.y + a * (h.x - t.x) / s) : 1 & i ? new o.Point(r.x, t.y + a * (r.x - t.x) / s) : void 0\r\n"+
            "            },\r\n"+
            "            _getBitCode: function(t, e) {\r\n"+
            "                var i = 0;\r\n"+
            "                return t.x < e.min.x ? i |= 1 : t.x > e.max.x && (i |= 2), t.y < e.min.y ? i |= 4 : t.y > e.max.y && (i |= 8), i\r\n"+
            "            },\r\n"+
            "            _sqDist: function(t, e) {\r\n"+
            "                var i = e.x - t.x,\r\n"+
            "                    n = e.y - t.y;\r\n"+
            "                return i * i + n * n\r\n"+
            "            },\r\n"+
            "            _sqClosestPointOnSegment: function(t, e, i, n) {\r\n"+
            "                var s, a = e.x,\r\n"+
            "                    r = e.y,\r\n"+
            "                    h = i.x - a,\r\n"+
            "                    l = i.y - r,\r\n"+
            "                    u = h * h + l * l;\r\n"+
            "                return u > 0 && (s = ((t.x - a) * h + (t.y - r) * l) / u, s > 1 ? (a = i.x, r = i.y) : s > 0 && (a += h * s, r += l * s)), h = t.x - a, l = t.y - r, n ? h * h + l * l : new o.Point(a, r)\r\n"+
            "            }\r\n"+
            "        }, o.Polyline = o.Path.extend({\r\n"+
            "            initialize: function(t, e) {\r\n"+
            "                o.Path.prototype.initialize.call(this, e), this._latlngs = this._convertLatLngs(t)\r\n"+
            "            },\r\n"+
            "            options: {\r\n"+
            "                smoothFactor: 1,\r\n"+
            "                noClip: !1\r\n"+
            "            },\r\n"+
            "            projectLatlngs: function() {\r\n"+
            "                this._originalPoints = [];\r\n"+
            "                for (var t = 0, e = this._latlngs.length; e > t; t++) this._originalPoints[t] = this._map.latLngToLayerPoint(this._latlngs[t])\r\n"+
            "            },\r\n"+
            "            getPathString: function() {\r\n"+
            "                for (var t = 0, e = this._parts.length, i = \"\"; e > t; t++) i += this._getPathPartStr(this._parts[t]);\r\n"+
            "                return i\r\n"+
            "            },\r\n"+
            "            getLatLngs: function() {\r\n"+
            "                return this._latlngs\r\n"+
            "            },\r\n"+
            "            setLatLngs: function(t) {\r\n"+
            "                return this._latlngs = this._convertLatLngs(t), this.redraw()\r\n"+
            "            },\r\n"+
            "            addLatLng: function(t) {\r\n"+
            "                return this._latlngs.push(o.latLng(t)), this.redraw()\r\n"+
            "            },\r\n"+
            "            spliceLatLngs: function() {\r\n"+
            "                var t = [].splice.apply(this._latlngs, arguments);\r\n"+
            "                return this._convertLatLngs(this._latlngs, !0), this.redraw(), t\r\n"+
            "            },\r\n"+
            "            closestLayerPoint: function(t) {\r\n"+
            "                for (var e, i, n = 1 / 0, s = this._parts, a = null, r = 0, h = s.length; h > r; r++)\r\n"+
            "                    for (var l = s[r], u = 1, c = l.length; c > u; u++) {\r\n"+
            "                        e = l[u - 1], i = l[u];\r\n"+
            "                        var d = o.LineUtil._sqClosestPointOnSegment(t, e, i, !0);\r\n"+
            "                        n > d && (n = d, a = o.LineUtil._sqClosestPointOnSegment(t, e, i))\r\n"+
            "                    }\r\n"+
            "                return a && (a.distance = Math.sqrt(n)), a\r\n"+
            "            },\r\n"+
            "            getBounds: function() {\r\n"+
            "                return new o.LatLngBounds(this.getLatLngs())\r\n"+
            "            },\r\n"+
            "            _convertLatLngs: function(t, e) {\r\n"+
            "                var i, n, s = e ? t : [];\r\n"+
            "                for (i = 0, n = t.length; n > i; i++) {\r\n"+
            "                    if (o.Util.isArray(t[i]) && \"number\" != typeof t[i][0]) return;\r\n"+
            "                    s[i] = o.latLng(t[i])\r\n"+
            "                }\r\n"+
            "                return s\r\n"+
            "            },\r\n"+
            "            _initEvents: function() {\r\n"+
            "                o.Path.prototype._initEvents.call(this)\r\n"+
            "            },\r\n"+
            "            _getPathPartStr: function(t) {\r\n"+
            "                for (var e, i = o.Path.VML, n = 0, s = t.length, a = \"\"; s > n; n++) e = t[n], i && e._round(), a += (n ? \"L\" : \"M\") + e.x + \" \" + e.y;\r\n"+
            "                return a\r\n"+
            "            },\r\n"+
            "            _clipPoints: function() {\r\n"+
            "                var t, e, i, n = this.");
          out.print(
            "_originalPoints,\r\n"+
            "                    s = n.length;\r\n"+
            "                if (this.options.noClip) return void(this._parts = [n]);\r\n"+
            "                this._parts = [];\r\n"+
            "                var a = this._parts,\r\n"+
            "                    r = this._map._pathViewport,\r\n"+
            "                    h = o.LineUtil;\r\n"+
            "                for (t = 0, e = 0; s - 1 > t; t++) i = h.clipSegment(n[t], n[t + 1], r, t), i && (a[e] = a[e] || [], a[e].push(i[0]), (i[1] !== n[t + 1] || t === s - 2) && (a[e].push(i[1]), e++))\r\n"+
            "            },\r\n"+
            "            _simplifyPoints: function() {\r\n"+
            "                for (var t = this._parts, e = o.LineUtil, i = 0, n = t.length; n > i; i++) t[i] = e.simplify(t[i], this.options.smoothFactor)\r\n"+
            "            },\r\n"+
            "            _updatePath: function() {\r\n"+
            "                this._map && (this._clipPoints(), this._simplifyPoints(), o.Path.prototype._updatePath.call(this))\r\n"+
            "            }\r\n"+
            "        }), o.polyline = function(t, e) {\r\n"+
            "            return new o.Polyline(t, e)\r\n"+
            "        }, o.PolyUtil = {}, o.PolyUtil.clipPolygon = function(t, e) {\r\n"+
            "            var i, n, s, a, r, h, l, u, c, d = [1, 4, 2, 8],\r\n"+
            "                p = o.LineUtil;\r\n"+
            "            for (n = 0, l = t.length; l > n; n++) t[n]._code = p._getBitCode(t[n], e);\r\n"+
            "            for (a = 0; 4 > a; a++) {\r\n"+
            "                for (u = d[a], i = [], n = 0, l = t.length, s = l - 1; l > n; s = n++) r = t[n], h = t[s], r._code & u ? h._code & u || (c = p._getEdgeIntersection(h, r, u, e), c._code = p._getBitCode(c, e), i.push(c)) : (h._code & u && (c = p._getEdgeIntersection(h, r, u, e), c._code = p._getBitCode(c, e), i.push(c)), i.push(r));\r\n"+
            "                t = i\r\n"+
            "            }\r\n"+
            "            return t\r\n"+
            "        }, o.Polygon = o.Polyline.extend({\r\n"+
            "            options: {\r\n"+
            "                fill: !0\r\n"+
            "            },\r\n"+
            "            initialize: function(t, e) {\r\n"+
            "                o.Polyline.prototype.initialize.call(this, t, e), this._initWithHoles(t)\r\n"+
            "            },\r\n"+
            "            _initWithHoles: function(t) {\r\n"+
            "                var e, i, n;\r\n"+
            "                if (t && o.Util.isArray(t[0]) && \"number\" != typeof t[0][0])\r\n"+
            "                    for (this._latlngs = this._convertLatLngs(t[0]), this._holes = t.slice(1), e = 0, i = this._holes.length; i > e; e++) n = this._holes[e] = this._convertLatLngs(this._holes[e]), n[0].equals(n[n.length - 1]) && n.pop();\r\n"+
            "                t = this._latlngs, t.length >= 2 && t[0].equals(t[t.length - 1]) && t.pop()\r\n"+
            "            },\r\n"+
            "            projectLatlngs: function() {\r\n"+
            "                if (o.Polyline.prototype.projectLatlngs.call(this), this._holePoints = [], this._holes) {\r\n"+
            "                    var t, e, i, n;\r\n"+
            "                    for (t = 0, i = this._holes.length; i > t; t++)\r\n"+
            "                        for (this._holePoints[t] = [], e = 0, n = this._holes[t].length; n > e; e++) this._holePoints[t][e] = this._map.latLngToLayerPoint(this._holes[t][e])\r\n"+
            "                }\r\n"+
            "            },\r\n"+
            "            setLatLngs: function(t) {\r\n"+
            "                return t && o.Util.isArray(t[0]) && \"number\" != typeof t[0][0] ? (this._initWithHoles(t), this.redraw()) : o.Polyline.prototype.setLatLngs.call(this, t)\r\n"+
            "            },\r\n"+
            "            _clipPoints: function() {\r\n"+
            "                var t = this._originalPoints,\r\n"+
            "                    e = [];\r\n"+
            "                if (this._parts = [t].concat(this._holePoints), !this.options.noClip) {\r\n"+
            "                    for (var i = 0, n = this._parts.length; n > i; i++) {\r\n"+
            "                        var s = o.PolyUtil.clipPolygon(this._parts[i], this._map._pathViewport);\r\n"+
            "                        s.length && e.push(s)\r\n"+
            "                    }\r\n"+
            "                    this._parts = e\r\n"+
            "                }\r\n"+
            "            },\r\n"+
            "            _getPathPartStr: function(t) {\r\n"+
            "                var e = o.Polyline.prototype._getPathPartStr.call(this, t);\r\n"+
            "                return e + (o.Browser.svg ? \"z\" : \"x\")\r\n"+
            "            }\r\n"+
            "        }), o.polygon = function(t, e) {\r\n"+
            "            return new o.Polygon(t, e)\r\n"+
            "        },\r\n"+
            "        function() {\r\n"+
            "            function t(t) {\r\n"+
            "                return o.FeatureGroup.extend({\r\n"+
            "                    initialize: function(t, e) {\r\n"+
            "                        this._layers = {}, this._options = e, this.setLatLngs(t)\r\n"+
            "                    },\r\n"+
            "                    setLatLngs: function(e) {\r\n"+
            "                        var i = 0,\r\n"+
            "                            n = e.length;\r\n"+
            "                        for (this.eachLayer(function(t) {\r\n"+
            "                                n > i ? t.setLatLngs(e[i++]) : this.removeLayer(t)\r\n"+
            "                            }, this); n > i;) this.addLayer(new t(e[i++], this._options));\r\n"+
            "                        return this\r\n"+
            "                    },\r\n"+
            "                    getLatLngs: function() {\r\n"+
            "                        var t = [];\r\n"+
            "                        return this.eachLayer(function(e) {\r\n"+
            "                            t.push(e.getLatLngs())\r\n"+
            "                        }), t\r\n"+
            "                    }\r\n"+
            "                })\r\n"+
            "            }\r\n"+
            "            o.MultiPolyline = t(o.Polyline), o.MultiPolygon = t(o.Polygon), o.multiPolyline = function(t, e) {\r\n"+
            "                return new o.MultiPolyline(t, e)\r\n"+
            "            }, o.multiPolygon = function(t, e) {\r\n"+
            "                return new o.MultiPolygon(t, e)\r\n"+
            "            }\r\n"+
            "        }(), o.Rectangle = o.Polygon.extend({\r\n"+
            "            initialize: function(t, e) {\r\n"+
            "                o.Polygon.prototype.initialize.call(this, this._boundsToLatLngs(t), e)\r\n"+
            "            },\r\n"+
            "            setBounds: function(t) {\r\n"+
            "                this.setLatLngs(this._boundsToLatLngs(t))\r\n"+
            "            },\r\n"+
            "            _boundsToLatLngs: function(t) {\r\n"+
            "                return t = o.latLngBounds(t), [t.getSouthWest(), t.getNorthWest(), t.getNorthEast(), t.getSouthEast()]\r\n"+
            "            }\r\n"+
            "        }), o.rectangle = function(t, e) {\r\n"+
            "            return new o.Rectangle(t, e)\r\n"+
            "        }, o.Circle = o.Path.extend({\r\n"+
            "            initialize: function(t, e, i) {\r\n"+
            "                o.Path.prototype.initialize.call(this, i), this._latlng = o.latLng(t), this._mRadius = e\r\n"+
            "            },\r\n"+
            "            options: {\r\n"+
            "                fill: !0\r\n"+
            "            },\r\n"+
            "            setLatLng: function(t) {\r\n"+
            "                return this._latlng = o.latLng(t), this.redraw()\r\n"+
            "            },\r\n"+
            "            setRadius: function(t) {\r\n"+
            "                return this._mRadius = t, this.redraw()\r\n"+
            "            },\r\n"+
            "            projectLatlngs: function() {\r\n"+
            "                var t = this._getLngRadius(),\r\n"+
            "                    e = this._latlng,\r\n"+
            "                    i = this._map.latLngToLayerPoint([e.lat, e.lng - t]);\r\n"+
            "                this._point = this._map.latLngToLayerPoint(e), this._radius = Math.max(this._point.x - i.x, 1)\r\n"+
            "            },\r\n"+
            "            getBounds: function() {\r\n"+
            "                var t = this._getLngRadius(),\r\n"+
            "                    e = this._mRadius / 40075017 * 360,\r\n"+
            "                    i = this._latlng;\r\n"+
            "                return new o.LatLngBounds([i.lat - e, i.lng - t], [i.lat + e, i.lng + t])\r\n"+
            "            },\r\n"+
            "            getLatLng: function() {\r\n"+
            "                return this._latlng\r\n"+
            "            },\r\n"+
            "            getPathString: function() {\r\n"+
            "                var t = this._point,\r\n"+
            "                    e = this._radius;\r\n"+
            "                return this._checkIfEmpty() ? \"\" : o.Browser.svg ? \"M\" + t.x + \",\" + (t.y - e) + \"A\" + e + \",\" + e + \",0,1,1,\" + (t.x - .1) + \",\" + (t.y - e) + \" z\" : (t._round(), e = Math.round(e), \"AL \" + t.x + \",\" + t.y + \" \" + e + \",\" + e + \" 0,23592600\")\r\n"+
            "            },\r\n"+
            "            getRadius: function() {\r\n"+
            "                return this._mRadius\r\n"+
            "            },\r\n"+
            "            _getLatRadius: function() {\r\n"+
            "                return this._mRadius / 40075017 * 360\r\n"+
            "            },\r\n"+
            "            _getLngRadius: function() {\r\n"+
            "                return this._getLatRadius() / Math.cos(o.LatLng.DEG_TO_RAD * this._latlng.lat)\r\n"+
            "            },\r\n"+
            "            _checkIfEmpty: function() {\r\n"+
            "                if (!this._map) return !1;\r\n"+
            "                var t = this._map._pathViewport,\r\n"+
            "                    e = this._radius,\r\n"+
            "                    i = this._point;\r\n"+
            "                return i.x - e > t.max.x || i.y - e > t.max.y || i.x + e < t.min.x || i.y + e < t.min.y\r\n"+
            "            }\r\n"+
            "        }), o.circle = function(t, e, i) {\r\n"+
            "            return new o.Circle(t, e, i)\r\n"+
            "        }, o.CircleMarker = o.Circle.extend({\r\n"+
            "            options: {\r\n"+
            "                radius: 10,\r\n"+
            "                weight: 2\r\n"+
            "            },\r\n"+
            "            initialize: function(t, e) {\r\n"+
            "                o.Circle.prototype.initialize.call(this, t, null, e), this._radius = this.options.radius\r\n"+
            "            },\r\n"+
            "            projectLatlngs: function() {\r\n"+
            "                this._point = this._map.latLngToLayerPoint(this._latlng)\r\n"+
            "            },\r\n"+
            "            _updateStyle: function() {\r\n"+
            "                o.Circle.prototype._updateStyle.call(this), this.setRadius(this.options.radius)\r\n"+
            "            },\r\n"+
            "            setLatLng: function(t) {\r\n"+
            "                return o.Circle.prototype.setLatLng.call(this, t), this._popup && this._popup._isOpen && this._popup.setLatLng(t), this\r\n"+
            "            },\r\n"+
            "            setRadius: function(t) {\r\n"+
            "                return this.options.radius = this._radius = t, this.redraw()\r\n"+
            "            },\r\n"+
            "            getRadius: function() {\r\n"+
            "                return this._radius\r\n"+
            "            }\r\n"+
            "        }), o.circleMarker = function(t, e) {\r\n"+
            "            return new o.CircleMarker(t, e)\r\n"+
            "        }, o.Polyline.include(o.Path.CANVAS ? {\r\n"+
            "            _containsPoint: function(t, e) {\r\n"+
            "                var i, n, s, a, r, h, l, u = this.options.weight / 2;\r\n"+
            "                for (o.Browser.touch && (u += 10), i = 0, a = this._parts.length; a > i; i++)\r\n"+
            "                    for (l = this._parts[i], n = 0, r = l.length, s = r - 1; r > n; s = n++)\r\n"+
            "                        if ((e || 0 !== n) && (h = o.LineUtil.pointToSegmentDistance(t, l[s], l[n]), u >= h)) return !0;\r\n"+
            "                return !1\r\n"+
            "            }\r\n"+
            "        } : {}), o.Polygon.include(o.Path.CANVAS ? {\r\n"+
            "            _containsPoint: function(t) {\r\n"+
            "                var e, i, n, s, a, r, h, l, u = !1;\r\n"+
            "                if (o.Polyline.prototype._containsPoint.call(this, t, !0)) return !0;\r\n"+
            "                for (s = 0, h = this._parts.length; h > s; s++)");
          out.print(
            "\r\n"+
            "                    for (e = this._parts[s], a = 0, l = e.length, r = l - 1; l > a; r = a++) i = e[a], n = e[r], i.y > t.y != n.y > t.y && t.x < (n.x - i.x) * (t.y - i.y) / (n.y - i.y) + i.x && (u = !u);\r\n"+
            "                return u\r\n"+
            "            }\r\n"+
            "        } : {}), o.Circle.include(o.Path.CANVAS ? {\r\n"+
            "            _drawPath: function() {\r\n"+
            "                var t = this._point;\r\n"+
            "                this._ctx.beginPath(), this._ctx.arc(t.x, t.y, this._radius, 0, 2 * Math.PI, !1)\r\n"+
            "            },\r\n"+
            "            _containsPoint: function(t) {\r\n"+
            "                var e = this._point,\r\n"+
            "                    i = this.options.stroke ? this.options.weight / 2 : 0;\r\n"+
            "                return t.distanceTo(e) <= this._radius + i\r\n"+
            "            }\r\n"+
            "        } : {}), o.CircleMarker.include(o.Path.CANVAS ? {\r\n"+
            "            _updateStyle: function() {\r\n"+
            "                o.Path.prototype._updateStyle.call(this)\r\n"+
            "            }\r\n"+
            "        } : {}), o.GeoJSON = o.FeatureGroup.extend({\r\n"+
            "            initialize: function(t, e) {\r\n"+
            "                o.setOptions(this, e), this._layers = {}, t && this.addData(t)\r\n"+
            "            },\r\n"+
            "            addData: function(t) {\r\n"+
            "                var e, i, n, s = o.Util.isArray(t) ? t : t.features;\r\n"+
            "                if (s) {\r\n"+
            "                    for (e = 0, i = s.length; i > e; e++) n = s[e], (n.geometries || n.geometry || n.features || n.coordinates) && this.addData(s[e]);\r\n"+
            "                    return this\r\n"+
            "                }\r\n"+
            "                var a = this.options;\r\n"+
            "                if (!a.filter || a.filter(t)) {\r\n"+
            "                    var r = o.GeoJSON.geometryToLayer(t, a.pointToLayer, a.coordsToLatLng, a);\r\n"+
            "                    return r.feature = o.GeoJSON.asFeature(t), r.defaultOptions = r.options, this.resetStyle(r), a.onEachFeature && a.onEachFeature(t, r), this.addLayer(r)\r\n"+
            "                }\r\n"+
            "            },\r\n"+
            "            resetStyle: function(t) {\r\n"+
            "                var e = this.options.style;\r\n"+
            "                e && (o.Util.extend(t.options, t.defaultOptions), this._setLayerStyle(t, e))\r\n"+
            "            },\r\n"+
            "            setStyle: function(t) {\r\n"+
            "                this.eachLayer(function(e) {\r\n"+
            "                    this._setLayerStyle(e, t)\r\n"+
            "                }, this)\r\n"+
            "            },\r\n"+
            "            _setLayerStyle: function(t, e) {\r\n"+
            "                \"function\" == typeof e && (e = e(t.feature)), t.setStyle && t.setStyle(e)\r\n"+
            "            }\r\n"+
            "        }), o.extend(o.GeoJSON, {\r\n"+
            "            geometryToLayer: function(t, e, i, n) {\r\n"+
            "                var s, a, r, h, l = \"Feature\" === t.type ? t.geometry : t,\r\n"+
            "                    u = l.coordinates,\r\n"+
            "                    c = [];\r\n"+
            "                switch (i = i || this.coordsToLatLng, l.type) {\r\n"+
            "                    case \"Point\":\r\n"+
            "                        return s = i(u), e ? e(t, s) : new o.Marker(s);\r\n"+
            "                    case \"MultiPoint\":\r\n"+
            "                        for (r = 0, h = u.length; h > r; r++) s = i(u[r]), c.push(e ? e(t, s) : new o.Marker(s));\r\n"+
            "                        return new o.FeatureGroup(c);\r\n"+
            "                    case \"LineString\":\r\n"+
            "                        return a = this.coordsToLatLngs(u, 0, i), new o.Polyline(a, n);\r\n"+
            "                    case \"Polygon\":\r\n"+
            "                        if (2 === u.length && !u[1].length) throw new Error(\"Invalid GeoJSON object.\");\r\n"+
            "                        return a = this.coordsToLatLngs(u, 1, i), new o.Polygon(a, n);\r\n"+
            "                    case \"MultiLineString\":\r\n"+
            "                        return a = this.coordsToLatLngs(u, 1, i), new o.MultiPolyline(a, n);\r\n"+
            "                    case \"MultiPolygon\":\r\n"+
            "                        return a = this.coordsToLatLngs(u, 2, i), new o.MultiPolygon(a, n);\r\n"+
            "                    case \"GeometryCollection\":\r\n"+
            "                        for (r = 0, h = l.geometries.length; h > r; r++) c.push(this.geometryToLayer({\r\n"+
            "                            geometry: l.geometries[r],\r\n"+
            "                            type: \"Feature\",\r\n"+
            "                            properties: t.properties\r\n"+
            "                        }, e, i, n));\r\n"+
            "                        return new o.FeatureGroup(c);\r\n"+
            "                    default:\r\n"+
            "                        throw new Error(\"Invalid GeoJSON object.\")\r\n"+
            "                }\r\n"+
            "            },\r\n"+
            "            coordsToLatLng: function(t) {\r\n"+
            "                return new o.LatLng(t[1], t[0], t[2])\r\n"+
            "            },\r\n"+
            "            coordsToLatLngs: function(t, e, i) {\r\n"+
            "                var n, o, s, a = [];\r\n"+
            "                for (o = 0, s = t.length; s > o; o++) n = e ? this.coordsToLatLngs(t[o], e - 1, i) : (i || this.coordsToLatLng)(t[o]), a.push(n);\r\n"+
            "                return a\r\n"+
            "            },\r\n"+
            "            latLngToCoords: function(t) {\r\n"+
            "                var e = [t.lng, t.lat];\r\n"+
            "                return t.alt !== i && e.push(t.alt), e\r\n"+
            "            },\r\n"+
            "            latLngsToCoords: function(t) {\r\n"+
            "                for (var e = [], i = 0, n = t.length; n > i; i++) e.push(o.GeoJSON.latLngToCoords(t[i]));\r\n"+
            "                return e\r\n"+
            "            },\r\n"+
            "            getFeature: function(t, e) {\r\n"+
            "                return t.feature ? o.extend({}, t.feature, {\r\n"+
            "                    geometry: e\r\n"+
            "                }) : o.GeoJSON.asFeature(e)\r\n"+
            "            },\r\n"+
            "            asFeature: function(t) {\r\n"+
            "                return \"Feature\" === t.type ? t : {\r\n"+
            "                    type: \"Feature\",\r\n"+
            "                    properties: {},\r\n"+
            "                    geometry: t\r\n"+
            "                }\r\n"+
            "            }\r\n"+
            "        });\r\n"+
            "    var a = {\r\n"+
            "        toGeoJSON: function() {\r\n"+
            "            return o.GeoJSON.getFeature(this, {\r\n"+
            "                type: \"Point\",\r\n"+
            "                coordinates: o.GeoJSON.latLngToCoords(this.getLatLng())\r\n"+
            "            })\r\n"+
            "        }\r\n"+
            "    };\r\n"+
            "    o.Marker.include(a), o.Circle.include(a), o.CircleMarker.include(a), o.Polyline.include({\r\n"+
            "            toGeoJSON: function() {\r\n"+
            "                return o.GeoJSON.getFeature(this, {\r\n"+
            "                    type: \"LineString\",\r\n"+
            "                    coordinates: o.GeoJSON.latLngsToCoords(this.getLatLngs())\r\n"+
            "                })\r\n"+
            "            }\r\n"+
            "        }), o.Polygon.include({\r\n"+
            "            toGeoJSON: function() {\r\n"+
            "                var t, e, i, n = [o.GeoJSON.latLngsToCoords(this.getLatLngs())];\r\n"+
            "                if (n[0].push(n[0][0]), this._holes)\r\n"+
            "                    for (t = 0, e = this._holes.length; e > t; t++) i = o.GeoJSON.latLngsToCoords(this._holes[t]), i.push(i[0]), n.push(i);\r\n"+
            "                return o.GeoJSON.getFeature(this, {\r\n"+
            "                    type: \"Polygon\",\r\n"+
            "                    coordinates: n\r\n"+
            "                })\r\n"+
            "            }\r\n"+
            "        }),\r\n"+
            "        function() {\r\n"+
            "            function t(t) {\r\n"+
            "                return function() {\r\n"+
            "                    var e = [];\r\n"+
            "                    return this.eachLayer(function(t) {\r\n"+
            "                        e.push(t.toGeoJSON().geometry.coordinates)\r\n"+
            "                    }), o.GeoJSON.getFeature(this, {\r\n"+
            "                        type: t,\r\n"+
            "                        coordinates: e\r\n"+
            "                    })\r\n"+
            "                }\r\n"+
            "            }\r\n"+
            "            o.MultiPolyline.include({\r\n"+
            "                toGeoJSON: t(\"MultiLineString\")\r\n"+
            "            }), o.MultiPolygon.include({\r\n"+
            "                toGeoJSON: t(\"MultiPolygon\")\r\n"+
            "            }), o.LayerGroup.include({\r\n"+
            "                toGeoJSON: function() {\r\n"+
            "                    var e, i = this.feature && this.feature.geometry,\r\n"+
            "                        n = [];\r\n"+
            "                    if (i && \"MultiPoint\" === i.type) return t(\"MultiPoint\").call(this);\r\n"+
            "                    var s = i && \"GeometryCollection\" === i.type;\r\n"+
            "                    return this.eachLayer(function(t) {\r\n"+
            "                        t.toGeoJSON && (e = t.toGeoJSON(), n.push(s ? e.geometry : o.GeoJSON.asFeature(e)))\r\n"+
            "                    }), s ? o.GeoJSON.getFeature(this, {\r\n"+
            "                        geometries: n,\r\n"+
            "                        type: \"GeometryCollection\"\r\n"+
            "                    }) : {\r\n"+
            "                        type: \"FeatureCollection\",\r\n"+
            "                        features: n\r\n"+
            "                    }\r\n"+
            "                }\r\n"+
            "            })\r\n"+
            "        }(), o.geoJson = function(t, e) {\r\n"+
            "            return new o.GeoJSON(t, e)\r\n"+
            "        }, o.DomEvent = {\r\n"+
            "            addListener: function(t, e, i, n) {\r\n"+
            "                var s, a, r, h = o.stamp(i),\r\n"+
            "                    l = \"_leaflet_\" + e + h;\r\n"+
            "                return t[l] ? this : (s = function(e) {\r\n"+
            "                    return i.call(n || t, e || o.DomEvent._getEvent())\r\n"+
            "                }, o.Browser.pointer && 0 === e.indexOf(\"touch\") ? this.addPointerListener(t, e, s, h) : (o.Browser.touch && \"dblclick\" === e && this.addDoubleTapListener && this.addDoubleTapListener(t, s, h), \"addEventListener\" in t ? \"mousewheel\" === e ? (t.addEventListener(\"DOMMouseScroll\", s, !1), t.addEventListener(e, s, !1)) : \"mouseenter\" === e || \"mouseleave\" === e ? (a = s, r = \"mouseenter\" === e ? \"mouseover\" : \"mouseout\", s = function(e) {\r\n"+
            "                    return o.DomEvent._checkMouse(t, e) ? a(e) : void 0\r\n"+
            "                }, t.addEventListener(r, s, !1)) : \"click\" === e && o.Browser.android ? (a = s, s = function(t) {\r\n"+
            "                    return o.DomEvent._filterClick(t, a)\r\n"+
            "                }, t.addEventListener(e, s, !1)) : t.addEventListener(e, s, !1) : \"attachEvent\" in t && t.attachEvent(\"on\" + e, s), t[l] = s, this))\r\n"+
            "            },\r\n"+
            "            removeListener: function(t, e, i) {\r\n"+
            "                var n = o.stamp(i),\r\n"+
            "                    s = \"_leaflet_\" + e + n,\r\n"+
            "                    a = t[s];\r\n"+
            "                return a ? (o.Browser.pointer && 0 === e.indexOf(\"touch\") ? this.removePointerListener(t, e, n) : o.Browser.touch && \"dblclick\" === e && this.removeDoubleTapListener ? this.removeDoubleTapListener(t, n) : \"removeEventListener\" in t ? \"mousewheel\" === e ? (t.removeEventListener(\"DOMMouseScroll\", a, !1), t.removeEventListener(e, a, !1)) : \"mouseenter\" === e || \"mouseleave\" === e ? t.removeEventListener(\"mouseenter\" === e ? \"mouseover\" : \"mouseout\", a, !1) : t.removeEventListener(e, a, !1) : \"detachEvent\" in t && t.detachEvent(\"on\" + e, a), t[s] = null, this) : this\r\n"+
            "            },\r\n"+
            "            stopPropagation: function(t) {\r\n"+
            "                return t.stopPropagation ? t.stopPropagation() : t.cancelBubble = !0, o.DomEvent._skip");
          out.print(
            "ped(t), this\r\n"+
            "            },\r\n"+
            "            disableScrollPropagation: function(t) {\r\n"+
            "                var e = o.DomEvent.stopPropagation;\r\n"+
            "                return o.DomEvent.on(t, \"mousewheel\", e).on(t, \"MozMousePixelScroll\", e)\r\n"+
            "            },\r\n"+
            "            disableClickPropagation: function(t) {\r\n"+
            "                for (var e = o.DomEvent.stopPropagation, i = o.Draggable.START.length - 1; i >= 0; i--) o.DomEvent.on(t, o.Draggable.START[i], e);\r\n"+
            "                return o.DomEvent.on(t, \"click\", o.DomEvent._fakeStop).on(t, \"dblclick\", e)\r\n"+
            "            },\r\n"+
            "            preventDefault: function(t) {\r\n"+
            "                return t.preventDefault ? t.preventDefault() : t.returnValue = !1, this\r\n"+
            "            },\r\n"+
            "            stop: function(t) {\r\n"+
            "                return o.DomEvent.preventDefault(t).stopPropagation(t)\r\n"+
            "            },\r\n"+
            "            getMousePosition: function(t, e) {\r\n"+
            "                if (!e) return new o.Point(t.clientX, t.clientY);\r\n"+
            "                var i = e.getBoundingClientRect();\r\n"+
            "                return new o.Point(t.clientX - i.left - e.clientLeft, t.clientY - i.top - e.clientTop)\r\n"+
            "            },\r\n"+
            "            getWheelDelta: function(t) {\r\n"+
            "                var e = 0;\r\n"+
            "                return t.wheelDelta && (e = t.wheelDelta / 120), t.detail && (e = -t.detail / 3), e\r\n"+
            "            },\r\n"+
            "            _skipEvents: {},\r\n"+
            "            _fakeStop: function(t) {\r\n"+
            "                o.DomEvent._skipEvents[t.type] = !0\r\n"+
            "            },\r\n"+
            "            _skipped: function(t) {\r\n"+
            "                var e = this._skipEvents[t.type];\r\n"+
            "                return this._skipEvents[t.type] = !1, e\r\n"+
            "            },\r\n"+
            "            _checkMouse: function(t, e) {\r\n"+
            "                var i = e.relatedTarget;\r\n"+
            "                if (!i) return !0;\r\n"+
            "                try {\r\n"+
            "                    for (; i && i !== t;) i = i.parentNode\r\n"+
            "                } catch (n) {\r\n"+
            "                    return !1\r\n"+
            "                }\r\n"+
            "                return i !== t\r\n"+
            "            },\r\n"+
            "            _getEvent: function() {\r\n"+
            "                var e = t.event;\r\n"+
            "                if (!e)\r\n"+
            "                    for (var i = arguments.callee.caller; i && (e = i.arguments[0], !e || t.Event !== e.constructor);) i = i.caller;\r\n"+
            "                return e\r\n"+
            "            },\r\n"+
            "            _filterClick: function(t, e) {\r\n"+
            "                var i = t.timeStamp || t.originalEvent.timeStamp,\r\n"+
            "                    n = o.DomEvent._lastClick && i - o.DomEvent._lastClick;\r\n"+
            "                return n && n > 100 && 500 > n || t.target._simulatedClick && !t._simulated ? void o.DomEvent.stop(t) : (o.DomEvent._lastClick = i, e(t))\r\n"+
            "            }\r\n"+
            "        }, o.DomEvent.on = o.DomEvent.addListener, o.DomEvent.off = o.DomEvent.removeListener, o.Draggable = o.Class.extend({\r\n"+
            "            includes: o.Mixin.Events,\r\n"+
            "            statics: {\r\n"+
            "                START: o.Browser.touch ? [\"touchstart\", \"mousedown\"] : [\"mousedown\"],\r\n"+
            "                END: {\r\n"+
            "                    mousedown: \"mouseup\",\r\n"+
            "                    touchstart: \"touchend\",\r\n"+
            "                    pointerdown: \"touchend\",\r\n"+
            "                    MSPointerDown: \"touchend\"\r\n"+
            "                },\r\n"+
            "                MOVE: {\r\n"+
            "                    mousedown: \"mousemove\",\r\n"+
            "                    touchstart: \"touchmove\",\r\n"+
            "                    pointerdown: \"touchmove\",\r\n"+
            "                    MSPointerDown: \"touchmove\"\r\n"+
            "                }\r\n"+
            "            },\r\n"+
            "            initialize: function(t, e) {\r\n"+
            "                this._element = t, this._dragStartTarget = e || t\r\n"+
            "            },\r\n"+
            "            enable: function() {\r\n"+
            "                if (!this._enabled) {\r\n"+
            "                    for (var t = o.Draggable.START.length - 1; t >= 0; t--) o.DomEvent.on(this._dragStartTarget, o.Draggable.START[t], this._onDown, this);\r\n"+
            "                    this._enabled = !0\r\n"+
            "                }\r\n"+
            "            },\r\n"+
            "            disable: function() {\r\n"+
            "                if (this._enabled) {\r\n"+
            "                    for (var t = o.Draggable.START.length - 1; t >= 0; t--) o.DomEvent.off(this._dragStartTarget, o.Draggable.START[t], this._onDown, this);\r\n"+
            "                    this._enabled = !1, this._moved = !1\r\n"+
            "                }\r\n"+
            "            },\r\n"+
            "            _onDown: function(t) {\r\n"+
            "                if (this._moved = !1, !t.shiftKey && (1 === t.which || 1 === t.button || t.touches) && (o.DomEvent.stopPropagation(t), !o.Draggable._disabled && (o.DomUtil.disableImageDrag(), o.DomUtil.disableTextSelection(), !this._moving))) {\r\n"+
            "                	var doc = getDocument(this._element);\r\n"+
            "                    var i = t.touches ? t.touches[0] : t;\r\n"+
            "                    this._startPoint = new o.Point(i.clientX, i.clientY), this._startPos = this._newPos = o.DomUtil.getPosition(this._element), o.DomEvent.on(doc, o.Draggable.MOVE[t.type], this._onMove, this).on(doc, o.Draggable.END[t.type], this._onUp, this)\r\n"+
            "                }\r\n"+
            "            },\r\n"+
            "            _onMove: function(t) {\r\n"+
            "                if (t.touches && t.touches.length > 1) return void(this._moved = !0);\r\n"+
            "                var i = t.touches && 1 === t.touches.length ? t.touches[0] : t,\r\n"+
            "                    n = new o.Point(i.clientX, i.clientY),\r\n"+
            "                    s = n.subtract(this._startPoint);\r\n"+
            "                (s.x || s.y) && (o.Browser.touch && Math.abs(s.x) + Math.abs(s.y) < 3 || (o.DomEvent.preventDefault(t), this._moved || (this.fire(\"dragstart\"), this._moved = !0, this._startPos = o.DomUtil.getPosition(this._element).subtract(s), o.DomUtil.addClass(e.body, \"leaflet-dragging\"), this._lastTarget = t.target || t.srcElement, o.DomUtil.addClass(this._lastTarget, \"leaflet-drag-target\")), this._newPos = this._startPos.add(s), this._moving = !0, o.Util.cancelAnimFrame(this._animRequest), this._animRequest = o.Util.requestAnimFrame(this._updatePosition, this, !0, this._dragStartTarget)))\r\n"+
            "            },\r\n"+
            "            _updatePosition: function() {\r\n"+
            "                this.fire(\"predrag\"), o.DomUtil.setPosition(this._element, this._newPos), this.fire(\"drag\")\r\n"+
            "            },\r\n"+
            "            _onUp: function() {\r\n"+
            "               	var doc = getDocument(this._element);\r\n"+
            "                o.DomUtil.removeClass(e.body, \"leaflet-dragging\"), this._lastTarget && (o.DomUtil.removeClass(this._lastTarget, \"leaflet-drag-target\"), this._lastTarget = null);\r\n"+
            "                for (var t in o.Draggable.MOVE) o.DomEvent.off(doc, o.Draggable.MOVE[t], this._onMove).off(doc, o.Draggable.END[t], this._onUp);\r\n"+
            "                o.DomUtil.enableImageDrag(), o.DomUtil.enableTextSelection(), this._moved && this._moving && (o.Util.cancelAnimFrame(this._animRequest), this.fire(\"dragend\", {\r\n"+
            "                    distance: this._newPos.distanceTo(this._startPos)\r\n"+
            "                })), this._moving = !1\r\n"+
            "            }\r\n"+
            "        }), o.Handler = o.Class.extend({\r\n"+
            "            initialize: function(t) {\r\n"+
            "                this._map = t\r\n"+
            "            },\r\n"+
            "            enable: function() {\r\n"+
            "                this._enabled || (this._enabled = !0, this.addHooks())\r\n"+
            "            },\r\n"+
            "            disable: function() {\r\n"+
            "                this._enabled && (this._enabled = !1, this.removeHooks())\r\n"+
            "            },\r\n"+
            "            enabled: function() {\r\n"+
            "                return !!this._enabled\r\n"+
            "            }\r\n"+
            "        }), o.Map.mergeOptions({\r\n"+
            "            dragging: !0,\r\n"+
            "            inertia: !o.Browser.android23,\r\n"+
            "            inertiaDeceleration: 3400,\r\n"+
            "            inertiaMaxSpeed: 1 / 0,\r\n"+
            "            inertiaThreshold: o.Browser.touch ? 32 : 18,\r\n"+
            "            easeLinearity: .25,\r\n"+
            "            worldCopyJump: !1\r\n"+
            "        }), o.Map.Drag = o.Handler.extend({\r\n"+
            "            addHooks: function() {\r\n"+
            "                if (!this._draggable) {\r\n"+
            "                    var t = this._map;\r\n"+
            "                    this._draggable = new o.Draggable(t._mapPane, t._container), this._draggable.on({\r\n"+
            "                        dragstart: this._onDragStart,\r\n"+
            "                        drag: this._onDrag,\r\n"+
            "                        dragend: this._onDragEnd\r\n"+
            "                    }, this), t.options.worldCopyJump && (this._draggable.on(\"predrag\", this._onPreDrag, this), t.on(\"viewreset\", this._onViewReset, this), t.whenReady(this._onViewReset, this))\r\n"+
            "                }\r\n"+
            "                this._draggable.enable()\r\n"+
            "            },\r\n"+
            "            removeHooks: function() {\r\n"+
            "                this._draggable.disable()\r\n"+
            "            },\r\n"+
            "            moved: function() {\r\n"+
            "                return this._draggable && this._draggable._moved\r\n"+
            "            },\r\n"+
            "            _onDragStart: function() {\r\n"+
            "                var t = this._map;\r\n"+
            "                t._panAnim && t._panAnim.stop(), t.fire(\"movestart\").fire(\"dragstart\"), t.options.inertia && (this._positions = [], this._times = [])\r\n"+
            "            },\r\n"+
            "            _onDrag: function() {\r\n"+
            "                if (this._map.options.inertia) {\r\n"+
            "                    var t = this._lastTime = +new Date,\r\n"+
            "                        e = this._lastPos = this._draggable._newPos;\r\n"+
            "                    this._positions.push(e), this._times.push(t), t - this._times[0] > 200 && (this._positions.shift(), this._times.shift())\r\n"+
            "                }\r\n"+
            "                this._map.fire(\"move\").fire(\"drag\")\r\n"+
            "            },\r\n"+
            "            _onViewReset: function() {\r\n"+
            "                var t = this._map.getSize()._divideBy(2),\r\n"+
            "                    e = this._map.latLngToLayerPoint([0, 0]);\r\n"+
            "                this._initialWorldOffset = e.subtract(t).x, this._worldWidth = this._map.project([0, 180]).x\r\n"+
            "            },\r\n"+
            "            _onPreDrag: function() {\r\n"+
            "                var t = this._worldWidth,\r\n"+
            "                    e = Math.round(t / 2),\r\n"+
            "                    i = this._initialWorldOffset,\r\n"+
            "                    n = this._draggable._newPos.x,\r\n"+
            "                    o = (n - e + i) % t + e - i,\r\n"+
            "                    s = (n + e + i) % t - e - i,\r\n"+
            "                    a = Math.abs(o + i) < Math.abs(s + i) ? o : s;\r\n"+
            "                this._draggable._newPos.x = a\r\n"+
            "            },\r\n"+
            "            _onDragEnd: function(t) {\r\n"+
            "                var e = this._map,\r\n"+
            "                    i = e.options,\r\n"+
            "                    n = +new Date - this._lastTime,\r\n"+
            "                    s = !i.inertia || n > i.inertiaThreshold || !this._positions[0];\r\n"+
            "                if (e.fire(\"dragend\", t), s) e.fire(\"moveend\");\r\n"+
            "            ");
          out.print(
            "    else {\r\n"+
            "                    var a = this._lastPos.subtract(this._positions[0]),\r\n"+
            "                        r = (this._lastTime + n - this._times[0]) / 1e3,\r\n"+
            "                        h = i.easeLinearity,\r\n"+
            "                        l = a.multiplyBy(h / r),\r\n"+
            "                        u = l.distanceTo([0, 0]),\r\n"+
            "                        c = Math.min(i.inertiaMaxSpeed, u),\r\n"+
            "                        d = l.multiplyBy(c / u),\r\n"+
            "                        p = c / (i.inertiaDeceleration * h),\r\n"+
            "                        _ = d.multiplyBy(-p / 2).round();\r\n"+
            "                    _.x && _.y ? (_ = e._limitOffset(_, e.options.maxBounds), o.Util.requestAnimFrame(function() {\r\n"+
            "                        e.panBy(_, {\r\n"+
            "                            duration: p,\r\n"+
            "                            easeLinearity: h,\r\n"+
            "                            noMoveStart: !0\r\n"+
            "                        })\r\n"+
            "                    })) : e.fire(\"moveend\")\r\n"+
            "                }\r\n"+
            "            }\r\n"+
            "        }), o.Map.addInitHook(\"addHandler\", \"dragging\", o.Map.Drag), o.Map.mergeOptions({\r\n"+
            "            doubleClickZoom: !0\r\n"+
            "        }), o.Map.DoubleClickZoom = o.Handler.extend({\r\n"+
            "            addHooks: function() {\r\n"+
            "                this._map.on(\"dblclick\", this._onDoubleClick, this)\r\n"+
            "            },\r\n"+
            "            removeHooks: function() {\r\n"+
            "                this._map.off(\"dblclick\", this._onDoubleClick, this)\r\n"+
            "            },\r\n"+
            "            _onDoubleClick: function(t) {\r\n"+
            "                var e = this._map,\r\n"+
            "                    i = e.getZoom() + (t.originalEvent.shiftKey ? -1 : 1);\r\n"+
            "                \"center\" === e.options.doubleClickZoom ? e.setZoom(i) : e.setZoomAround(t.containerPoint, i)\r\n"+
            "            }\r\n"+
            "        }), o.Map.addInitHook(\"addHandler\", \"doubleClickZoom\", o.Map.DoubleClickZoom), o.Map.mergeOptions({\r\n"+
            "            scrollWheelZoom: !0\r\n"+
            "        }), o.Map.ScrollWheelZoom = o.Handler.extend({\r\n"+
            "            addHooks: function() {\r\n"+
            "                o.DomEvent.on(this._map._container, \"mousewheel\", this._onWheelScroll, this), o.DomEvent.on(this._map._container, \"MozMousePixelScroll\", o.DomEvent.preventDefault), this._delta = 0\r\n"+
            "            },\r\n"+
            "            removeHooks: function() {\r\n"+
            "                o.DomEvent.off(this._map._container, \"mousewheel\", this._onWheelScroll), o.DomEvent.off(this._map._container, \"MozMousePixelScroll\", o.DomEvent.preventDefault)\r\n"+
            "            },\r\n"+
            "            _onWheelScroll: function(t) {\r\n"+
            "                var e = o.DomEvent.getWheelDelta(t);\r\n"+
            "                this._delta += e, this._lastMousePos = this._map.mouseEventToContainerPoint(t), this._startTime || (this._startTime = +new Date);\r\n"+
            "                var i = Math.max(40 - (+new Date - this._startTime), 0);\r\n"+
            "                clearTimeout(this._timer), this._timer = setTimeout(o.bind(this._performZoom, this), i), o.DomEvent.preventDefault(t), o.DomEvent.stopPropagation(t)\r\n"+
            "            },\r\n"+
            "            _performZoom: function() {\r\n"+
            "                var t = this._map,\r\n"+
            "                    e = this._delta,\r\n"+
            "                    i = t.getZoom();\r\n"+
            "                e = e > 0 ? Math.ceil(e) : Math.floor(e), e = Math.max(Math.min(e, 4), -4), e = t._limitZoom(i + e) - i, this._delta = 0, this._startTime = null, e && (\"center\" === t.options.scrollWheelZoom ? t.setZoom(i + e) : t.setZoomAround(this._lastMousePos, i + e))\r\n"+
            "            }\r\n"+
            "        }), o.Map.addInitHook(\"addHandler\", \"scrollWheelZoom\", o.Map.ScrollWheelZoom), o.extend(o.DomEvent, {\r\n"+
            "            _touchstart: o.Browser.msPointer ? \"MSPointerDown\" : o.Browser.pointer ? \"pointerdown\" : \"touchstart\",\r\n"+
            "            _touchend: o.Browser.msPointer ? \"MSPointerUp\" : o.Browser.pointer ? \"pointerup\" : \"touchend\",\r\n"+
            "            addDoubleTapListener: function(t, i, n) {\r\n"+
            "                function s(t) {\r\n"+
            "                    var e;\r\n"+
            "                    if (o.Browser.pointer ? (_.push(t.pointerId), e = _.length) : e = t.touches.length, !(e > 1)) {\r\n"+
            "                        var i = Date.now(),\r\n"+
            "                            n = i - (r || i);\r\n"+
            "                        h = t.touches ? t.touches[0] : t, l = n > 0 && u >= n, r = i\r\n"+
            "                    }\r\n"+
            "                }\r\n"+
            "\r\n"+
            "                function a(t) {\r\n"+
            "                    if (o.Browser.pointer) {\r\n"+
            "                        var e = _.indexOf(t.pointerId);\r\n"+
            "                        if (-1 === e) return;\r\n"+
            "                        _.splice(e, 1)\r\n"+
            "                    }\r\n"+
            "                    if (l) {\r\n"+
            "                        if (o.Browser.pointer) {\r\n"+
            "                            var n, s = {};\r\n"+
            "                            for (var a in h) n = h[a], \"function\" == typeof n ? s[a] = n.bind(h) : s[a] = n;\r\n"+
            "                            h = s\r\n"+
            "                        }\r\n"+
            "                        h.type = \"dblclick\", i(h), r = null\r\n"+
            "                    }\r\n"+
            "                }\r\n"+
            "                var r, h, l = !1,\r\n"+
            "                    u = 250,\r\n"+
            "                    c = \"_leaflet_\",\r\n"+
            "                    d = this._touchstart,\r\n"+
            "                    p = this._touchend,\r\n"+
            "                    _ = [];\r\n"+
            "                t[c + d + n] = s, t[c + p + n] = a;\r\n"+
            "                var m = o.Browser.pointer ? e.documentElement : t;\r\n"+
            "                return t.addEventListener(d, s, !1), m.addEventListener(p, a, !1), o.Browser.pointer && m.addEventListener(o.DomEvent.POINTER_CANCEL, a, !1), this\r\n"+
            "            },\r\n"+
            "            removeDoubleTapListener: function(t, i) {\r\n"+
            "                var n = \"_leaflet_\";\r\n"+
            "                return t.removeEventListener(this._touchstart, t[n + this._touchstart + i], !1), (o.Browser.pointer ? e.documentElement : t).removeEventListener(this._touchend, t[n + this._touchend + i], !1), o.Browser.pointer && e.documentElement.removeEventListener(o.DomEvent.POINTER_CANCEL, t[n + this._touchend + i], !1), this\r\n"+
            "            }\r\n"+
            "        }), o.extend(o.DomEvent, {\r\n"+
            "            POINTER_DOWN: o.Browser.msPointer ? \"MSPointerDown\" : \"pointerdown\",\r\n"+
            "            POINTER_MOVE: o.Browser.msPointer ? \"MSPointerMove\" : \"pointermove\",\r\n"+
            "            POINTER_UP: o.Browser.msPointer ? \"MSPointerUp\" : \"pointerup\",\r\n"+
            "            POINTER_CANCEL: o.Browser.msPointer ? \"MSPointerCancel\" : \"pointercancel\",\r\n"+
            "            _pointers: [],\r\n"+
            "            _pointerDocumentListener: !1,\r\n"+
            "            addPointerListener: function(t, e, i, n) {\r\n"+
            "                switch (e) {\r\n"+
            "                    case \"touchstart\":\r\n"+
            "                        return this.addPointerListenerStart(t, e, i, n);\r\n"+
            "                    case \"touchend\":\r\n"+
            "                        return this.addPointerListenerEnd(t, e, i, n);\r\n"+
            "                    case \"touchmove\":\r\n"+
            "                        return this.addPointerListenerMove(t, e, i, n);\r\n"+
            "                    default:\r\n"+
            "                        throw \"Unknown touch event type\"\r\n"+
            "                }\r\n"+
            "            },\r\n"+
            "            addPointerListenerStart: function(t, i, n, s) {\r\n"+
            "                var a = \"_leaflet_\",\r\n"+
            "                    r = this._pointers,\r\n"+
            "                    h = function(t) {\r\n"+
            "                        \"mouse\" !== t.pointerType && t.pointerType !== t.MSPOINTER_TYPE_MOUSE && o.DomEvent.preventDefault(t);\r\n"+
            "                        for (var e = !1, i = 0; i < r.length; i++)\r\n"+
            "                            if (r[i].pointerId === t.pointerId) {\r\n"+
            "                                e = !0;\r\n"+
            "                                break\r\n"+
            "                            }\r\n"+
            "                        e || r.push(t), t.touches = r.slice(), t.changedTouches = [t], n(t)\r\n"+
            "                    };\r\n"+
            "                if (t[a + \"touchstart\" + s] = h, t.addEventListener(this.POINTER_DOWN, h, !1), !this._pointerDocumentListener) {\r\n"+
            "                    var l = function(t) {\r\n"+
            "                        for (var e = 0; e < r.length; e++)\r\n"+
            "                            if (r[e].pointerId === t.pointerId) {\r\n"+
            "                                r.splice(e, 1);\r\n"+
            "                                break\r\n"+
            "                            }\r\n"+
            "                    };\r\n"+
            "                    e.documentElement.addEventListener(this.POINTER_UP, l, !1), e.documentElement.addEventListener(this.POINTER_CANCEL, l, !1), this._pointerDocumentListener = !0\r\n"+
            "                }\r\n"+
            "                return this\r\n"+
            "            },\r\n"+
            "            addPointerListenerMove: function(t, e, i, n) {\r\n"+
            "                function o(t) {\r\n"+
            "                    if (t.pointerType !== t.MSPOINTER_TYPE_MOUSE && \"mouse\" !== t.pointerType || 0 !== t.buttons) {\r\n"+
            "                        for (var e = 0; e < a.length; e++)\r\n"+
            "                            if (a[e].pointerId === t.pointerId) {\r\n"+
            "                                a[e] = t;\r\n"+
            "                                break\r\n"+
            "                            }\r\n"+
            "                        t.touches = a.slice(), t.changedTouches = [t], i(t)\r\n"+
            "                    }\r\n"+
            "                }\r\n"+
            "                var s = \"_leaflet_\",\r\n"+
            "                    a = this._pointers;\r\n"+
            "                return t[s + \"touchmove\" + n] = o, t.addEventListener(this.POINTER_MOVE, o, !1), this\r\n"+
            "            },\r\n"+
            "            addPointerListenerEnd: function(t, e, i, n) {\r\n"+
            "                var o = \"_leaflet_\",\r\n"+
            "                    s = this._pointers,\r\n"+
            "                    a = function(t) {\r\n"+
            "                        for (var e = 0; e < s.length; e++)\r\n"+
            "                            if (s[e].pointerId === t.pointerId) {\r\n"+
            "                                s.splice(e, 1);\r\n"+
            "                                break\r\n"+
            "                            }\r\n"+
            "                        t.touches = s.slice(), t.changedTouches = [t], i(t)\r\n"+
            "                    };\r\n"+
            "                return t[o + \"touchend\" + n] = a, t.addEventListener(this.POINTER_UP, a, !1), t.addEventListener(this.POINTER_CANCEL, a, !1), this\r\n"+
            "            },\r\n"+
            "            removePointerListener: function(t, e, i) {\r\n"+
            "                var n = \"_leaflet_\",\r\n"+
            "                    o = t[n + e + i];\r\n"+
            "                switch (e) {\r\n"+
            "                    case \"touchstart\":\r\n"+
            "                        t.removeEventListener(this.POINTER_DOWN, o, !1);\r\n"+
            "                        break;\r\n"+
            "                    case \"touchmove\":\r\n"+
            "                        t.removeEventListener(this.POINTER_MOVE, o, !1);\r\n"+
            "                        break;\r\n"+
            "                    case \"touchend\":\r\n"+
            "                        t.removeEventListener(this.POINTER_UP, o");
          out.print(
            ", !1), t.removeEventListener(this.POINTER_CANCEL, o, !1)\r\n"+
            "                }\r\n"+
            "                return this\r\n"+
            "            }\r\n"+
            "        }), o.Map.mergeOptions({\r\n"+
            "            touchZoom: o.Browser.touch && !o.Browser.android23,\r\n"+
            "            bounceAtZoomLimits: !0\r\n"+
            "        }), o.Map.TouchZoom = o.Handler.extend({\r\n"+
            "            addHooks: function() {\r\n"+
            "                o.DomEvent.on(this._map._container, \"touchstart\", this._onTouchStart, this)\r\n"+
            "            },\r\n"+
            "            removeHooks: function() {\r\n"+
            "                o.DomEvent.off(this._map._container, \"touchstart\", this._onTouchStart, this)\r\n"+
            "            },\r\n"+
            "            _onTouchStart: function(t) {\r\n"+
            "                var i = this._map;\r\n"+
            "                if (t.touches && 2 === t.touches.length && !i._animatingZoom && !this._zooming) {\r\n"+
            "                    var n = i.mouseEventToLayerPoint(t.touches[0]),\r\n"+
            "                        s = i.mouseEventToLayerPoint(t.touches[1]),\r\n"+
            "                        a = i._getCenterLayerPoint();\r\n"+
            "                    this._startCenter = n.add(s)._divideBy(2), this._startDist = n.distanceTo(s), this._moved = !1, this._zooming = !0, this._centerOffset = a.subtract(this._startCenter), i._panAnim && i._panAnim.stop(), o.DomEvent.on(e, \"touchmove\", this._onTouchMove, this).on(e, \"touchend\", this._onTouchEnd, this), o.DomEvent.preventDefault(t)\r\n"+
            "                }\r\n"+
            "            },\r\n"+
            "            _onTouchMove: function(t) {\r\n"+
            "                var e = this._map;\r\n"+
            "                if (t.touches && 2 === t.touches.length && this._zooming) {\r\n"+
            "                    var i = e.mouseEventToLayerPoint(t.touches[0]),\r\n"+
            "                        n = e.mouseEventToLayerPoint(t.touches[1]);\r\n"+
            "                    this._scale = i.distanceTo(n) / this._startDist, this._delta = i._add(n)._divideBy(2)._subtract(this._startCenter), 1 !== this._scale && (e.options.bounceAtZoomLimits || !(e.getZoom() === e.getMinZoom() && this._scale < 1 || e.getZoom() === e.getMaxZoom() && this._scale > 1)) && (this._moved || (o.DomUtil.addClass(e._mapPane, \"leaflet-touching\"), e.fire(\"movestart\").fire(\"zoomstart\"), this._moved = !0), o.Util.cancelAnimFrame(this._animRequest), this._animRequest = o.Util.requestAnimFrame(this._updateOnMove, this, !0, this._map._container), o.DomEvent.preventDefault(t))\r\n"+
            "                }\r\n"+
            "            },\r\n"+
            "            _updateOnMove: function() {\r\n"+
            "                var t = this._map,\r\n"+
            "                    e = this._getScaleOrigin(),\r\n"+
            "                    i = t.layerPointToLatLng(e),\r\n"+
            "                    n = t.getScaleZoom(this._scale);\r\n"+
            "                t._animateZoom(i, n, this._startCenter, this._scale, this._delta, !1, !0)\r\n"+
            "            },\r\n"+
            "            _onTouchEnd: function() {\r\n"+
            "                if (!this._moved || !this._zooming) return void(this._zooming = !1);\r\n"+
            "                var t = this._map;\r\n"+
            "                this._zooming = !1, o.DomUtil.removeClass(t._mapPane, \"leaflet-touching\"), o.Util.cancelAnimFrame(this._animRequest), o.DomEvent.off(e, \"touchmove\", this._onTouchMove).off(e, \"touchend\", this._onTouchEnd);\r\n"+
            "                var i = this._getScaleOrigin(),\r\n"+
            "                    n = t.layerPointToLatLng(i),\r\n"+
            "                    s = t.getZoom(),\r\n"+
            "                    a = t.getScaleZoom(this._scale) - s,\r\n"+
            "                    r = a > 0 ? Math.ceil(a) : Math.floor(a),\r\n"+
            "                    h = t._limitZoom(s + r),\r\n"+
            "                    l = t.getZoomScale(h) / this._scale;\r\n"+
            "                t._animateZoom(n, h, i, l)\r\n"+
            "            },\r\n"+
            "            _getScaleOrigin: function() {\r\n"+
            "                var t = this._centerOffset.subtract(this._delta).divideBy(this._scale);\r\n"+
            "                return this._startCenter.add(t)\r\n"+
            "            }\r\n"+
            "        }), o.Map.addInitHook(\"addHandler\", \"touchZoom\", o.Map.TouchZoom), o.Map.mergeOptions({\r\n"+
            "            tap: !0,\r\n"+
            "            tapTolerance: 15\r\n"+
            "        }), o.Map.Tap = o.Handler.extend({\r\n"+
            "            addHooks: function() {\r\n"+
            "                o.DomEvent.on(this._map._container, \"touchstart\", this._onDown, this)\r\n"+
            "            },\r\n"+
            "            removeHooks: function() {\r\n"+
            "                o.DomEvent.off(this._map._container, \"touchstart\", this._onDown, this)\r\n"+
            "            },\r\n"+
            "            _onDown: function(t) {\r\n"+
            "                if (t.touches) {\r\n"+
            "                    if (o.DomEvent.preventDefault(t), this._fireClick = !0, t.touches.length > 1) return this._fireClick = !1, void clearTimeout(this._holdTimeout);\r\n"+
            "                    var i = t.touches[0],\r\n"+
            "                        n = i.target;\r\n"+
            "                    this._startPos = this._newPos = new o.Point(i.clientX, i.clientY), n.tagName && \"a\" === n.tagName.toLowerCase() && o.DomUtil.addClass(n, \"leaflet-active\"), this._holdTimeout = setTimeout(o.bind(function() {\r\n"+
            "                        this._isTapValid() && (this._fireClick = !1, this._onUp(), this._simulateEvent(\"contextmenu\", i))\r\n"+
            "                    }, this), 1e3), o.DomEvent.on(e, \"touchmove\", this._onMove, this).on(e, \"touchend\", this._onUp, this)\r\n"+
            "                }\r\n"+
            "            },\r\n"+
            "            _onUp: function(t) {\r\n"+
            "                if (clearTimeout(this._holdTimeout), o.DomEvent.off(e, \"touchmove\", this._onMove, this).off(e, \"touchend\", this._onUp, this), this._fireClick && t && t.changedTouches) {\r\n"+
            "                    var i = t.changedTouches[0],\r\n"+
            "                        n = i.target;\r\n"+
            "                    n && n.tagName && \"a\" === n.tagName.toLowerCase() && o.DomUtil.removeClass(n, \"leaflet-active\"), this._isTapValid() && this._simulateEvent(\"click\", i)\r\n"+
            "                }\r\n"+
            "            },\r\n"+
            "            _isTapValid: function() {\r\n"+
            "                return this._newPos.distanceTo(this._startPos) <= this._map.options.tapTolerance\r\n"+
            "            },\r\n"+
            "            _onMove: function(t) {\r\n"+
            "                var e = t.touches[0];\r\n"+
            "                this._newPos = new o.Point(e.clientX, e.clientY)\r\n"+
            "            },\r\n"+
            "            _simulateEvent: function(i, n) {\r\n"+
            "                var o = e.createEvent(\"MouseEvents\");\r\n"+
            "                o._simulated = !0, n.target._simulatedClick = !0, o.initMouseEvent(i, !0, !0, t, 1, n.screenX, n.screenY, n.clientX, n.clientY, !1, !1, !1, !1, 0, null), n.target.dispatchEvent(o)\r\n"+
            "            }\r\n"+
            "        }), o.Browser.touch && !o.Browser.pointer && o.Map.addInitHook(\"addHandler\", \"tap\", o.Map.Tap), o.Map.mergeOptions({\r\n"+
            "            boxZoom: !0\r\n"+
            "        }), o.Map.BoxZoom = o.Handler.extend({\r\n"+
            "            initialize: function(t) {\r\n"+
            "                this._map = t, this._container = t._container, this._pane = t._panes.overlayPane, this._moved = !1\r\n"+
            "            },\r\n"+
            "            addHooks: function() {\r\n"+
            "                o.DomEvent.on(this._container, \"mousedown\", this._onMouseDown, this)\r\n"+
            "            },\r\n"+
            "            removeHooks: function() {\r\n"+
            "                o.DomEvent.off(this._container, \"mousedown\", this._onMouseDown), this._moved = !1\r\n"+
            "            },\r\n"+
            "            moved: function() {\r\n"+
            "                return this._moved\r\n"+
            "            },\r\n"+
            "            _onMouseDown: function(t) {\r\n"+
            "                return this._moved = !1, !t.shiftKey || 1 !== t.which && 1 !== t.button ? !1 : (o.DomUtil.disableTextSelection(), o.DomUtil.disableImageDrag(), this._startLayerPoint = this._map.mouseEventToLayerPoint(t), void o.DomEvent.on(e, \"mousemove\", this._onMouseMove, this).on(e, \"mouseup\", this._onMouseUp, this).on(e, \"keydown\", this._onKeyDown, this))\r\n"+
            "            },\r\n"+
            "            _onMouseMove: function(t) {\r\n"+
            "                this._moved || (this._box = o.DomUtil.create(\"div\", \"leaflet-zoom-box\", this._pane), o.DomUtil.setPosition(this._box, this._startLayerPoint), this._container.style.cursor = \"crosshair\", this._map.fire(\"boxzoomstart\"));\r\n"+
            "                var e = this._startLayerPoint,\r\n"+
            "                    i = this._box,\r\n"+
            "                    n = this._map.mouseEventToLayerPoint(t),\r\n"+
            "                    s = n.subtract(e),\r\n"+
            "                    a = new o.Point(Math.min(n.x, e.x), Math.min(n.y, e.y));\r\n"+
            "                o.DomUtil.setPosition(i, a), this._moved = !0, i.style.width = Math.max(0, Math.abs(s.x) - 4) + \"px\", i.style.height = Math.max(0, Math.abs(s.y) - 4) + \"px\"\r\n"+
            "            },\r\n"+
            "            _finish: function() {\r\n"+
            "                this._moved && (this._pane.removeChild(this._box), this._container.style.cursor = \"\"), o.DomUtil.enableTextSelection(), o.DomUtil.enableImageDrag(), o.DomEvent.off(e, \"mousemove\", this._onMouseMove).off(e, \"mouseup\", this._onMouseUp).off(e, \"keydown\", this._onKeyDown)\r\n"+
            "            },\r\n"+
            "            _onMouseUp: function(t) {\r\n"+
            "                this._finish();\r\n"+
            "                var e = this._map,\r\n"+
            "                    i = e.mouseEventToLayerPoint(t);\r\n"+
            "                if (!this._startLayerPoint.equals(i)) {\r\n"+
            "                    var n = new o.LatLngBounds(e.layerPointToLatLng(this._startLayerPoint), e.layerPointToLatLng(i));\r\n"+
            "                    e.fitBounds(n), e.fire(\"boxzoomend\", {\r\n"+
            "                        boxZoomBounds: n\r\n"+
            "                    })\r\n"+
            "                }\r\n"+
            "            },\r\n"+
            "            _onKeyDown: function(t) {\r\n"+
            "                27 === t.keyCode && this._finish()\r\n"+
            "            }\r\n"+
            "        }), o.Map.addInitHook(\"addHandler\", \"boxZoom\", o.Map.BoxZoom), o.Map.mergeOptions({\r\n"+
            "            keyboard: !0,\r\n"+
            "            keyboardPanOffset: 80,\r\n"+
            "            keyboardZoomOffset: 1\r\n"+
            "        }), o.Map.Keyboard = o.Handler.extend({\r\n"+
            "            keyCodes: {\r\n"+
            "                left: [37],\r\n"+
            "                right: [39],\r\n"+
            "                down: [40],\r\n"+
            "                up: [38],\r\n"+
            "                zoomIn: [187, 107, 61, 171],\r\n"+
            "                zoomOut: [189, 109, 173]\r\n"+
            "            },\r\n"+
            "            initialize: function(t) {\r\n"+
            "                this._map = t, this._setPanOffset(t.options.keyboardPanOffset), this._setZoomOffset(t.options.keyboardZoomOffset)\r\n"+
            "            },\r\n"+
            "            addHooks: function() {\r\n"+
            "                var t = this._map._container; - 1 === t.tabIndex && (t.tabIndex = \"0\"), o.DomEvent.on(t, \"focus\", this._onFocus, this).on(t, \"blur\", this._onBlur, this).on(t, \"mousedown\", this._onMouseDown, this), this._map.on(\"focus\", this._addHooks, this).on(\"blur\", this._removeHooks, this)\r\n"+
            "            },\r\n"+
            "            removeHooks: function() {\r\n"+
            "                this._removeHooks();\r\n"+
            "      ");
          out.print(
            "          var t = this._map._container;\r\n"+
            "                o.DomEvent.off(t, \"focus\", this._onFocus, this).off(t, \"blur\", this._onBlur, this).off(t, \"mousedown\", this._onMouseDown, this), this._map.off(\"focus\", this._addHooks, this).off(\"blur\", this._removeHooks, this)\r\n"+
            "            },\r\n"+
            "            _onMouseDown: function() {\r\n"+
            "                if (!this._focused) {\r\n"+
            "                    var i = e.body,\r\n"+
            "                        n = e.documentElement,\r\n"+
            "                        o = i.scrollTop || n.scrollTop,\r\n"+
            "                        s = i.scrollLeft || n.scrollLeft;\r\n"+
            "                    this._map._container.focus(), t.scrollTo(s, o)\r\n"+
            "                }\r\n"+
            "            },\r\n"+
            "            _onFocus: function() {\r\n"+
            "                this._focused = !0, this._map.fire(\"focus\")\r\n"+
            "            },\r\n"+
            "            _onBlur: function() {\r\n"+
            "                this._focused = !1, this._map.fire(\"blur\")\r\n"+
            "            },\r\n"+
            "            _setPanOffset: function(t) {\r\n"+
            "                var e, i, n = this._panKeys = {},\r\n"+
            "                    o = this.keyCodes;\r\n"+
            "                for (e = 0, i = o.left.length; i > e; e++) n[o.left[e]] = [-1 * t, 0];\r\n"+
            "                for (e = 0, i = o.right.length; i > e; e++) n[o.right[e]] = [t, 0];\r\n"+
            "                for (e = 0, i = o.down.length; i > e; e++) n[o.down[e]] = [0, t];\r\n"+
            "                for (e = 0, i = o.up.length; i > e; e++) n[o.up[e]] = [0, -1 * t]\r\n"+
            "            },\r\n"+
            "            _setZoomOffset: function(t) {\r\n"+
            "                var e, i, n = this._zoomKeys = {},\r\n"+
            "                    o = this.keyCodes;\r\n"+
            "                for (e = 0, i = o.zoomIn.length; i > e; e++) n[o.zoomIn[e]] = t;\r\n"+
            "                for (e = 0, i = o.zoomOut.length; i > e; e++) n[o.zoomOut[e]] = -t\r\n"+
            "            },\r\n"+
            "            _addHooks: function() {\r\n"+
            "                o.DomEvent.on(e, \"keydown\", this._onKeyDown, this)\r\n"+
            "            },\r\n"+
            "            _removeHooks: function() {\r\n"+
            "                o.DomEvent.off(e, \"keydown\", this._onKeyDown, this)\r\n"+
            "            },\r\n"+
            "            _onKeyDown: function(t) {\r\n"+
            "                var e = t.keyCode,\r\n"+
            "                    i = this._map;\r\n"+
            "                if (e in this._panKeys) {\r\n"+
            "                    if (i._panAnim && i._panAnim._inProgress) return;\r\n"+
            "                    i.panBy(this._panKeys[e]), i.options.maxBounds && i.panInsideBounds(i.options.maxBounds)\r\n"+
            "                } else {\r\n"+
            "                    if (!(e in this._zoomKeys)) return;\r\n"+
            "                    i.setZoom(i.getZoom() + this._zoomKeys[e])\r\n"+
            "                }\r\n"+
            "                o.DomEvent.stop(t)\r\n"+
            "            }\r\n"+
            "        }), o.Map.addInitHook(\"addHandler\", \"keyboard\", o.Map.Keyboard), o.Handler.MarkerDrag = o.Handler.extend({\r\n"+
            "            initialize: function(t) {\r\n"+
            "                this._marker = t\r\n"+
            "            },\r\n"+
            "            addHooks: function() {\r\n"+
            "                var t = this._marker._icon;\r\n"+
            "                this._draggable || (this._draggable = new o.Draggable(t, t)), this._draggable.on(\"dragstart\", this._onDragStart, this).on(\"drag\", this._onDrag, this).on(\"dragend\", this._onDragEnd, this), this._draggable.enable(), o.DomUtil.addClass(this._marker._icon, \"leaflet-marker-draggable\")\r\n"+
            "            },\r\n"+
            "            removeHooks: function() {\r\n"+
            "                this._draggable.off(\"dragstart\", this._onDragStart, this).off(\"drag\", this._onDrag, this).off(\"dragend\", this._onDragEnd, this), this._draggable.disable(), o.DomUtil.removeClass(this._marker._icon, \"leaflet-marker-draggable\")\r\n"+
            "            },\r\n"+
            "            moved: function() {\r\n"+
            "                return this._draggable && this._draggable._moved\r\n"+
            "            },\r\n"+
            "            _onDragStart: function() {\r\n"+
            "                this._marker.closePopup().fire(\"movestart\").fire(\"dragstart\")\r\n"+
            "            },\r\n"+
            "            _onDrag: function() {\r\n"+
            "                var t = this._marker,\r\n"+
            "                    e = t._shadow,\r\n"+
            "                    i = o.DomUtil.getPosition(t._icon),\r\n"+
            "                    n = t._map.layerPointToLatLng(i);\r\n"+
            "                e && o.DomUtil.setPosition(e, i), t._latlng = n, t.fire(\"move\", {\r\n"+
            "                    latlng: n\r\n"+
            "                }).fire(\"drag\")\r\n"+
            "            },\r\n"+
            "            _onDragEnd: function(t) {\r\n"+
            "                this._marker.fire(\"moveend\").fire(\"dragend\", t)\r\n"+
            "            }\r\n"+
            "        }), o.Control = o.Class.extend({\r\n"+
            "            options: {\r\n"+
            "                position: \"topright\"\r\n"+
            "            },\r\n"+
            "            initialize: function(t) {\r\n"+
            "                o.setOptions(this, t)\r\n"+
            "            },\r\n"+
            "            getPosition: function() {\r\n"+
            "                return this.options.position\r\n"+
            "            },\r\n"+
            "            setPosition: function(t) {\r\n"+
            "                var e = this._map;\r\n"+
            "                return e && e.removeControl(this), this.options.position = t, e && e.addControl(this), this\r\n"+
            "            },\r\n"+
            "            getContainer: function() {\r\n"+
            "                return this._container\r\n"+
            "            },\r\n"+
            "            addTo: function(t) {\r\n"+
            "                this._map = t;\r\n"+
            "                var e = this._container = this.onAdd(t),\r\n"+
            "                    i = this.getPosition(),\r\n"+
            "                    n = t._controlCorners[i];\r\n"+
            "                return o.DomUtil.addClass(e, \"leaflet-control\"), -1 !== i.indexOf(\"bottom\") ? n.insertBefore(e, n.firstChild) : n.appendChild(e), this\r\n"+
            "            },\r\n"+
            "            removeFrom: function(t) {\r\n"+
            "                var e = this.getPosition(),\r\n"+
            "                    i = t._controlCorners[e];\r\n"+
            "                return i.removeChild(this._container), this._map = null, this.onRemove && this.onRemove(t), this\r\n"+
            "            },\r\n"+
            "            _refocusOnMap: function() {\r\n"+
            "                this._map && this._map.getContainer().focus()\r\n"+
            "            }\r\n"+
            "        }), o.control = function(t) {\r\n"+
            "            return new o.Control(t)\r\n"+
            "        }, o.Map.include({\r\n"+
            "            addControl: function(t) {\r\n"+
            "                return t.addTo(this), this\r\n"+
            "            },\r\n"+
            "            removeControl: function(t) {\r\n"+
            "                return t.removeFrom(this), this\r\n"+
            "            },\r\n"+
            "            _initControlPos: function() {\r\n"+
            "                function t(t, s) {\r\n"+
            "                    var a = i + t + \" \" + i + s;\r\n"+
            "                    e[t + s] = o.DomUtil.create(\"div\", a, n)\r\n"+
            "                }\r\n"+
            "                var e = this._controlCorners = {},\r\n"+
            "                    i = \"leaflet-\",\r\n"+
            "                    n = this._controlContainer = o.DomUtil.create(\"div\", i + \"control-container\", this._container);\r\n"+
            "                t(\"top\", \"left\"), t(\"top\", \"right\"), t(\"bottom\", \"left\"), t(\"bottom\", \"right\")\r\n"+
            "            },\r\n"+
            "            _clearControlPos: function() {\r\n"+
            "                this._container.removeChild(this._controlContainer)\r\n"+
            "            }\r\n"+
            "        }), o.Control.Zoom = o.Control.extend({\r\n"+
            "            options: {\r\n"+
            "                position: \"topleft\",\r\n"+
            "                zoomInText: \"+\",\r\n"+
            "                zoomInTitle: \"Zoom in\",\r\n"+
            "                zoomOutText: \"-\",\r\n"+
            "                zoomOutTitle: \"Zoom out\"\r\n"+
            "            },\r\n"+
            "            onAdd: function(t) {\r\n"+
            "                var e = \"leaflet-control-zoom\",\r\n"+
            "                    i = o.DomUtil.create(\"div\", e + \" leaflet-bar\");\r\n"+
            "                return this._map = t, this._zoomInButton = this._createButton(this.options.zoomInText, this.options.zoomInTitle, e + \"-in\", i, this._zoomIn, this), this._zoomOutButton = this._createButton(this.options.zoomOutText, this.options.zoomOutTitle, e + \"-out\", i, this._zoomOut, this), this._updateDisabled(), t.on(\"zoomend zoomlevelschange\", this._updateDisabled, this), i\r\n"+
            "            },\r\n"+
            "            onRemove: function(t) {\r\n"+
            "                t.off(\"zoomend zoomlevelschange\", this._updateDisabled, this)\r\n"+
            "            },\r\n"+
            "            _zoomIn: function(t) {\r\n"+
            "                this._map.zoomIn(t.shiftKey ? 3 : 1)\r\n"+
            "            },\r\n"+
            "            _zoomOut: function(t) {\r\n"+
            "                this._map.zoomOut(t.shiftKey ? 3 : 1)\r\n"+
            "            },\r\n"+
            "            _createButton: function(t, e, i, n, s, a) {\r\n"+
            "                var r = o.DomUtil.create(\"a\", i, n);\r\n"+
            "                r.innerHTML = t, r.href = \"#\", r.title = e;\r\n"+
            "                var h = o.DomEvent.stopPropagation;\r\n"+
            "                return o.DomEvent.on(r, \"click\", h).on(r, \"mousedown\", h).on(r, \"dblclick\", h).on(r, \"click\", o.DomEvent.preventDefault).on(r, \"click\", s, a).on(r, \"click\", this._refocusOnMap, a), r\r\n"+
            "            },\r\n"+
            "            _updateDisabled: function() {\r\n"+
            "                var t = this._map,\r\n"+
            "                    e = \"leaflet-disabled\";\r\n"+
            "                o.DomUtil.removeClass(this._zoomInButton, e), o.DomUtil.removeClass(this._zoomOutButton, e), t._zoom === t.getMinZoom() && o.DomUtil.addClass(this._zoomOutButton, e), t._zoom === t.getMaxZoom() && o.DomUtil.addClass(this._zoomInButton, e)\r\n"+
            "            }\r\n"+
            "        }), o.Map.mergeOptions({\r\n"+
            "            zoomControl: !0\r\n"+
            "        }), o.Map.addInitHook(function() {\r\n"+
            "            this.options.zoomControl && (this.zoomControl = new o.Control.Zoom, this.addControl(this.zoomControl))\r\n"+
            "        }), o.control.zoom = function(t) {\r\n"+
            "            return new o.Control.Zoom(t)\r\n"+
            "        }, o.Control.Attribution = o.Control.extend({\r\n"+
            "            options: {\r\n"+
            "                position: \"bottomright\",\r\n"+
            "                prefix: '<a href=\"http://leafletjs.com\" title=\"A JS library for interactive maps\">Leaflet</a>'\r\n"+
            "            },\r\n"+
            "            initialize: function(t) {\r\n"+
            "                o.setOptions(this, t), this._attributions = {}\r\n"+
            "            },\r\n"+
            "            onAdd: function(t) {\r\n"+
            "                this._container = o.DomUtil.create(\"div\", \"leaflet-control-attribution\"), o.DomEvent.disableClickPropagation(this._container);\r\n"+
            "                for (var e in t._layers) t._layers[e].getAttribution && this.addAttribution(t._layers[e].getAttribution());\r\n"+
            "                return t.on(\"layeradd\", this._onLayerAdd, this).on(\"layerremove\", this._onLayerRemove, this), this._update(), this._container\r\n"+
            "            },\r\n"+
            "            onRemove: function(t) {\r\n"+
            "                t.off(\"layeradd\", this._onLayerAdd).off(\"layerremove\", this._onLayerRemove)\r\n"+
            "            },\r\n"+
            "            setPrefix: function(t) {\r\n"+
            "                return this.options.prefix = t, this._update(), this\r\n"+
            "            },\r\n"+
            "            addAttribution: function(t) {\r\n"+
            "                return t ? (this._attribution");
          out.print(
            "s[t] || (this._attributions[t] = 0), this._attributions[t]++, this._update(), this) : void 0\r\n"+
            "            },\r\n"+
            "            removeAttribution: function(t) {\r\n"+
            "                return t ? (this._attributions[t] && (this._attributions[t]--, this._update()), this) : void 0\r\n"+
            "            },\r\n"+
            "            _update: function() {\r\n"+
            "                if (this._map) {\r\n"+
            "                    var t = [];\r\n"+
            "                    for (var e in this._attributions) this._attributions[e] && t.push(e);\r\n"+
            "                    var i = [];\r\n"+
            "                    this.options.prefix && i.push(this.options.prefix), t.length && i.push(t.join(\", \")), this._container.innerHTML = i.join(\" | \")\r\n"+
            "                }\r\n"+
            "            },\r\n"+
            "            _onLayerAdd: function(t) {\r\n"+
            "                t.layer.getAttribution && this.addAttribution(t.layer.getAttribution())\r\n"+
            "            },\r\n"+
            "            _onLayerRemove: function(t) {\r\n"+
            "                t.layer.getAttribution && this.removeAttribution(t.layer.getAttribution())\r\n"+
            "            }\r\n"+
            "        }), o.Map.mergeOptions({\r\n"+
            "            attributionControl: !0\r\n"+
            "        }), o.Map.addInitHook(function() {\r\n"+
            "            this.options.attributionControl && (this.attributionControl = (new o.Control.Attribution).addTo(this))\r\n"+
            "        }), o.control.attribution = function(t) {\r\n"+
            "            return new o.Control.Attribution(t)\r\n"+
            "        }, o.Control.Scale = o.Control.extend({\r\n"+
            "            options: {\r\n"+
            "                position: \"bottomleft\",\r\n"+
            "                maxWidth: 100,\r\n"+
            "                metric: !0,\r\n"+
            "                imperial: !0,\r\n"+
            "                updateWhenIdle: !1\r\n"+
            "            },\r\n"+
            "            onAdd: function(t) {\r\n"+
            "                this._map = t;\r\n"+
            "                var e = \"leaflet-control-scale\",\r\n"+
            "                    i = o.DomUtil.create(\"div\", e),\r\n"+
            "                    n = this.options;\r\n"+
            "                return this._addScales(n, e, i), t.on(n.updateWhenIdle ? \"moveend\" : \"move\", this._update, this), t.whenReady(this._update, this), i\r\n"+
            "            },\r\n"+
            "            onRemove: function(t) {\r\n"+
            "                t.off(this.options.updateWhenIdle ? \"moveend\" : \"move\", this._update, this)\r\n"+
            "            },\r\n"+
            "            _addScales: function(t, e, i) {\r\n"+
            "                t.metric && (this._mScale = o.DomUtil.create(\"div\", e + \"-line\", i)), t.imperial && (this._iScale = o.DomUtil.create(\"div\", e + \"-line\", i))\r\n"+
            "            },\r\n"+
            "            _update: function() {\r\n"+
            "                var t = this._map.getBounds(),\r\n"+
            "                    e = t.getCenter().lat,\r\n"+
            "                    i = 6378137 * Math.PI * Math.cos(e * Math.PI / 180),\r\n"+
            "                    n = i * (t.getNorthEast().lng - t.getSouthWest().lng) / 180,\r\n"+
            "                    o = this._map.getSize(),\r\n"+
            "                    s = this.options,\r\n"+
            "                    a = 0;\r\n"+
            "                o.x > 0 && (a = n * (s.maxWidth / o.x)), this._updateScales(s, a)\r\n"+
            "            },\r\n"+
            "            _updateScales: function(t, e) {\r\n"+
            "                t.metric && e && this._updateMetric(e), t.imperial && e && this._updateImperial(e)\r\n"+
            "            },\r\n"+
            "            _updateMetric: function(t) {\r\n"+
            "                var e = this._getRoundNum(t);\r\n"+
            "                this._mScale.style.width = this._getScaleWidth(e / t) + \"px\", this._mScale.innerHTML = 1e3 > e ? e + \" m\" : e / 1e3 + \" km\"\r\n"+
            "            },\r\n"+
            "            _updateImperial: function(t) {\r\n"+
            "                var e, i, n, o = 3.2808399 * t,\r\n"+
            "                    s = this._iScale;\r\n"+
            "                o > 5280 ? (e = o / 5280, i = this._getRoundNum(e), s.style.width = this._getScaleWidth(i / e) + \"px\", s.innerHTML = i + \" mi\") : (n = this._getRoundNum(o), s.style.width = this._getScaleWidth(n / o) + \"px\", s.innerHTML = n + \" ft\")\r\n"+
            "            },\r\n"+
            "            _getScaleWidth: function(t) {\r\n"+
            "                return Math.round(this.options.maxWidth * t) - 10\r\n"+
            "            },\r\n"+
            "            _getRoundNum: function(t) {\r\n"+
            "                var e = Math.pow(10, (Math.floor(t) + \"\").length - 1),\r\n"+
            "                    i = t / e;\r\n"+
            "                return i = i >= 10 ? 10 : i >= 5 ? 5 : i >= 3 ? 3 : i >= 2 ? 2 : 1, e * i\r\n"+
            "            }\r\n"+
            "        }), o.control.scale = function(t) {\r\n"+
            "            return new o.Control.Scale(t)\r\n"+
            "        }, o.Control.Layers = o.Control.extend({\r\n"+
            "            options: {\r\n"+
            "                collapsed: !0,\r\n"+
            "                position: \"topright\",\r\n"+
            "                autoZIndex: !0\r\n"+
            "            },\r\n"+
            "            initialize: function(t, e, i) {\r\n"+
            "                o.setOptions(this, i), this._layers = {}, this._lastZIndex = 0, this._handlingClick = !1;\r\n"+
            "                for (var n in t) this._addLayer(t[n], n);\r\n"+
            "                for (n in e) this._addLayer(e[n], n, !0)\r\n"+
            "            },\r\n"+
            "            onAdd: function(t) {\r\n"+
            "                return this._initLayout(), this._update(), t.on(\"layeradd\", this._onLayerChange, this).on(\"layerremove\", this._onLayerChange, this), this._container\r\n"+
            "            },\r\n"+
            "            onRemove: function(t) {\r\n"+
            "                t.off(\"layeradd\", this._onLayerChange, this).off(\"layerremove\", this._onLayerChange, this)\r\n"+
            "            },\r\n"+
            "            addBaseLayer: function(t, e) {\r\n"+
            "                return this._addLayer(t, e), this._update(), this\r\n"+
            "            },\r\n"+
            "            addOverlay: function(t, e) {\r\n"+
            "                return this._addLayer(t, e, !0), this._update(), this\r\n"+
            "            },\r\n"+
            "            removeLayer: function(t) {\r\n"+
            "                var e = o.stamp(t);\r\n"+
            "                return delete this._layers[e], this._update(), this\r\n"+
            "            },\r\n"+
            "            _initLayout: function() {\r\n"+
            "                var t = \"leaflet-control-layers\",\r\n"+
            "                    e = this._container = o.DomUtil.create(\"div\", t);\r\n"+
            "                e.setAttribute(\"aria-haspopup\", !0), o.Browser.touch ? o.DomEvent.on(e, \"click\", o.DomEvent.stopPropagation) : o.DomEvent.disableClickPropagation(e).disableScrollPropagation(e);\r\n"+
            "                var i = this._form = o.DomUtil.create(\"form\", t + \"-list\");\r\n"+
            "                if (this.options.collapsed) {\r\n"+
            "                    o.Browser.android || o.DomEvent.on(e, \"mouseover\", this._expand, this).on(e, \"mouseout\", this._collapse, this);\r\n"+
            "                    var n = this._layersLink = o.DomUtil.create(\"a\", t + \"-toggle\", e);\r\n"+
            "                    n.href = \"#\", n.title = \"Layers\", o.Browser.touch ? o.DomEvent.on(n, \"click\", o.DomEvent.stop).on(n, \"click\", this._expand, this) : o.DomEvent.on(n, \"focus\", this._expand, this), o.DomEvent.on(i, \"click\", function() {\r\n"+
            "                        setTimeout(o.bind(this._onInputClick, this), 0)\r\n"+
            "                    }, this), this._map.on(\"click\", this._collapse, this)\r\n"+
            "                } else this._expand();\r\n"+
            "                this._baseLayersList = o.DomUtil.create(\"div\", t + \"-base\", i), this._separator = o.DomUtil.create(\"div\", t + \"-separator\", i), this._overlaysList = o.DomUtil.create(\"div\", t + \"-overlays\", i), e.appendChild(i)\r\n"+
            "            },\r\n"+
            "            _addLayer: function(t, e, i) {\r\n"+
            "                var n = o.stamp(t);\r\n"+
            "                this._layers[n] = {\r\n"+
            "                    layer: t,\r\n"+
            "                    name: e,\r\n"+
            "                    overlay: i\r\n"+
            "                }, this.options.autoZIndex && t.setZIndex && (this._lastZIndex++, t.setZIndex(this._lastZIndex))\r\n"+
            "            },\r\n"+
            "            _update: function() {\r\n"+
            "                if (this._container) {\r\n"+
            "                    this._baseLayersList.innerHTML = \"\", this._overlaysList.innerHTML = \"\";\r\n"+
            "                    var t, e, i = !1,\r\n"+
            "                        n = !1;\r\n"+
            "                    for (t in this._layers) e = this._layers[t], this._addItem(e), n = n || e.overlay, i = i || !e.overlay;\r\n"+
            "                    this._separator.style.display = n && i ? \"\" : \"none\"\r\n"+
            "                }\r\n"+
            "            },\r\n"+
            "            _onLayerChange: function(t) {\r\n"+
            "                var e = this._layers[o.stamp(t.layer)];\r\n"+
            "                if (e) {\r\n"+
            "                    this._handlingClick || this._update();\r\n"+
            "                    var i = e.overlay ? \"layeradd\" === t.type ? \"overlayadd\" : \"overlayremove\" : \"layeradd\" === t.type ? \"baselayerchange\" : null;\r\n"+
            "                    i && this._map.fire(i, e)\r\n"+
            "                }\r\n"+
            "            },\r\n"+
            "            _createRadioElement: function(t, i) {\r\n"+
            "                var n = '<input type=\"radio\" class=\"leaflet-control-layers-selector\" name=\"' + t + '\"';\r\n"+
            "                i && (n += ' checked=\"checked\"'), n += \"/>\";\r\n"+
            "                var o = e.createElement(\"div\");\r\n"+
            "                return o.innerHTML = n, o.firstChild\r\n"+
            "            },\r\n"+
            "            _addItem: function(t) {\r\n"+
            "                var i, n = e.createElement(\"label\"),\r\n"+
            "                    s = this._map.hasLayer(t.layer);\r\n"+
            "                t.overlay ? (i = e.createElement(\"input\"), i.type = \"checkbox\", i.className = \"leaflet-control-layers-selector\", i.defaultChecked = s) : i = this._createRadioElement(\"leaflet-base-layers\", s), i.layerId = o.stamp(t.layer), o.DomEvent.on(i, \"click\", this._onInputClick, this);\r\n"+
            "                var a = e.createElement(\"span\");\r\n"+
            "                a.innerHTML = \" \" + t.name, n.appendChild(i), n.appendChild(a);\r\n"+
            "                var r = t.overlay ? this._overlaysList : this._baseLayersList;\r\n"+
            "                return r.appendChild(n), n\r\n"+
            "            },\r\n"+
            "            _onInputClick: function() {\r\n"+
            "                var t, e, i, n = this._form.getElementsByTagName(\"input\"),\r\n"+
            "                    o = n.length;\r\n"+
            "                for (this._handlingClick = !0, t = 0; o > t; t++) e = n[t], i = this._layers[e.layerId], e.checked && !this._map.hasLayer(i.layer) ? this._map.addLayer(i.layer) : !e.checked && this._map.hasLayer(i.layer) && this._map.removeLayer(i.layer);\r\n"+
            "                this._handlingClick = !1, this._refocusOnMap()\r\n"+
            "            },\r\n"+
            "            _expand: function() {\r\n"+
            "                o.DomUtil.addClass(this._container, \"leaflet-control-layers-expanded\")\r\n"+
            "            },\r\n"+
            "            _collapse: function() {\r\n"+
            "                this._container.className = this._container.className.replace(\" leaflet-control-layers-expanded\", \"\")\r\n"+
            "            }\r\n"+
            "        }), o.control.layers = function(t, e, i) {\r\n"+
            "            return new o.Control.Layers(t, e, i)\r\n"+
            "        }, o.PosAnimation = o.Class.extend({\r\n"+
            "            includes: o.Mixin.Events,\r\n"+
            "          ");
          out.print(
            "  run: function(t, e, i, n) {\r\n"+
            "                this.stop(), this._el = t, this._inProgress = !0, this._newPos = e, this.fire(\"start\"), t.style[o.DomUtil.TRANSITION] = \"all \" + (i || .25) + \"s cubic-bezier(0,0,\" + (n || .5) + \",1)\", o.DomEvent.on(t, o.DomUtil.TRANSITION_END, this._onTransitionEnd, this), o.DomUtil.setPosition(t, e), o.Util.falseFn(t.offsetWidth), this._stepTimer = setInterval(o.bind(this._onStep, this), 50)\r\n"+
            "            },\r\n"+
            "            stop: function() {\r\n"+
            "                this._inProgress && (o.DomUtil.setPosition(this._el, this._getPos()), this._onTransitionEnd(), o.Util.falseFn(this._el.offsetWidth))\r\n"+
            "            },\r\n"+
            "            _onStep: function() {\r\n"+
            "                var t = this._getPos();\r\n"+
            "                return t ? (this._el._leaflet_pos = t, void this.fire(\"step\")) : void this._onTransitionEnd()\r\n"+
            "            },\r\n"+
            "            _transformRe: /([-+]?(?:\\d*\\.)?\\d+)\\D*, ([-+]?(?:\\d*\\.)?\\d+)\\D*\\)/,\r\n"+
            "            _getPos: function() {\r\n"+
            "                var e, i, n, s = this._el,\r\n"+
            "                    a = t.getComputedStyle(s);\r\n"+
            "                if (o.Browser.any3d) {\r\n"+
            "                    if (n = a[o.DomUtil.TRANSFORM].match(this._transformRe), !n) return;\r\n"+
            "                    e = parseFloat(n[1]), i = parseFloat(n[2])\r\n"+
            "                } else e = parseFloat(a.left), i = parseFloat(a.top);\r\n"+
            "                return new o.Point(e, i, !0)\r\n"+
            "            },\r\n"+
            "            _onTransitionEnd: function() {\r\n"+
            "                o.DomEvent.off(this._el, o.DomUtil.TRANSITION_END, this._onTransitionEnd, this), this._inProgress && (this._inProgress = !1, this._el.style[o.DomUtil.TRANSITION] = \"\", this._el._leaflet_pos = this._newPos, clearInterval(this._stepTimer), this.fire(\"step\").fire(\"end\"))\r\n"+
            "            }\r\n"+
            "        }), o.Map.include({\r\n"+
            "            setView: function(t, e, n) {\r\n"+
            "                if (e = e === i ? this._zoom : this._limitZoom(e), t = this._limitCenter(o.latLng(t), e, this.options.maxBounds), n = n || {}, this._panAnim && this._panAnim.stop(), this._loaded && !n.reset && n !== !0) {\r\n"+
            "                    n.animate !== i && (n.zoom = o.extend({\r\n"+
            "                        animate: n.animate\r\n"+
            "                    }, n.zoom), n.pan = o.extend({\r\n"+
            "                        animate: n.animate\r\n"+
            "                    }, n.pan));\r\n"+
            "                    var s = this._zoom !== e ? this._tryAnimatedZoom && this._tryAnimatedZoom(t, e, n.zoom) : this._tryAnimatedPan(t, n.pan);\r\n"+
            "                    if (s) return clearTimeout(this._sizeTimer), this\r\n"+
            "                }\r\n"+
            "                return this._resetView(t, e), this\r\n"+
            "            },\r\n"+
            "            panBy: function(t, e) {\r\n"+
            "                if (t = o.point(t).round(), e = e || {}, !t.x && !t.y) return this;\r\n"+
            "                if (this._panAnim || (this._panAnim = new o.PosAnimation, this._panAnim.on({\r\n"+
            "                        step: this._onPanTransitionStep,\r\n"+
            "                        end: this._onPanTransitionEnd\r\n"+
            "                    }, this)), e.noMoveStart || this.fire(\"movestart\"), e.animate !== !1) {\r\n"+
            "                    o.DomUtil.addClass(this._mapPane, \"leaflet-pan-anim\");\r\n"+
            "                    var i = this._getMapPanePos().subtract(t);\r\n"+
            "                    this._panAnim.run(this._mapPane, i, e.duration || .25, e.easeLinearity)\r\n"+
            "                } else this._rawPanBy(t), this.fire(\"move\").fire(\"moveend\");\r\n"+
            "                return this\r\n"+
            "            },\r\n"+
            "            _onPanTransitionStep: function() {\r\n"+
            "                this.fire(\"move\")\r\n"+
            "            },\r\n"+
            "            _onPanTransitionEnd: function() {\r\n"+
            "                o.DomUtil.removeClass(this._mapPane, \"leaflet-pan-anim\"), this.fire(\"moveend\")\r\n"+
            "            },\r\n"+
            "            _tryAnimatedPan: function(t, e) {\r\n"+
            "                var i = this._getCenterOffset(t)._floor();\r\n"+
            "                return (e && e.animate) === !0 || this.getSize().contains(i) ? (this.panBy(i, e), !0) : !1\r\n"+
            "            }\r\n"+
            "        }), o.PosAnimation = o.DomUtil.TRANSITION ? o.PosAnimation : o.PosAnimation.extend({\r\n"+
            "            run: function(t, e, i, n) {\r\n"+
            "                this.stop(), this._el = t, this._inProgress = !0, this._duration = i || .25, this._easeOutPower = 1 / Math.max(n || .5, .2), this._startPos = o.DomUtil.getPosition(t), this._offset = e.subtract(this._startPos), this._startTime = +new Date, this.fire(\"start\"), this._animate()\r\n"+
            "            },\r\n"+
            "            stop: function() {\r\n"+
            "                this._inProgress && (this._step(), this._complete())\r\n"+
            "            },\r\n"+
            "            _animate: function() {\r\n"+
            "                this._animId = o.Util.requestAnimFrame(this._animate, this), this._step()\r\n"+
            "            },\r\n"+
            "            _step: function() {\r\n"+
            "                var t = +new Date - this._startTime,\r\n"+
            "                    e = 1e3 * this._duration;\r\n"+
            "                e > t ? this._runFrame(this._easeOut(t / e)) : (this._runFrame(1), this._complete())\r\n"+
            "            },\r\n"+
            "            _runFrame: function(t) {\r\n"+
            "                var e = this._startPos.add(this._offset.multiplyBy(t));\r\n"+
            "                o.DomUtil.setPosition(this._el, e), this.fire(\"step\")\r\n"+
            "            },\r\n"+
            "            _complete: function() {\r\n"+
            "                o.Util.cancelAnimFrame(this._animId), this._inProgress = !1, this.fire(\"end\")\r\n"+
            "            },\r\n"+
            "            _easeOut: function(t) {\r\n"+
            "                return 1 - Math.pow(1 - t, this._easeOutPower)\r\n"+
            "            }\r\n"+
            "        }), o.Map.mergeOptions({\r\n"+
            "            zoomAnimation: !0,\r\n"+
            "            zoomAnimationThreshold: 4\r\n"+
            "        }), o.DomUtil.TRANSITION && o.Map.addInitHook(function() {\r\n"+
            "            this._zoomAnimated = this.options.zoomAnimation && o.DomUtil.TRANSITION && o.Browser.any3d && !o.Browser.android23 && !o.Browser.mobileOpera, this._zoomAnimated && o.DomEvent.on(this._mapPane, o.DomUtil.TRANSITION_END, this._catchTransitionEnd, this)\r\n"+
            "        }), o.Map.include(o.DomUtil.TRANSITION ? {\r\n"+
            "            _catchTransitionEnd: function(t) {\r\n"+
            "                this._animatingZoom && t.propertyName.indexOf(\"transform\") >= 0 && this._onZoomTransitionEnd()\r\n"+
            "            },\r\n"+
            "            _nothingToAnimate: function() {\r\n"+
            "                return !this._container.getElementsByClassName(\"leaflet-zoom-animated\").length\r\n"+
            "            },\r\n"+
            "            _tryAnimatedZoom: function(t, e, i) {\r\n"+
            "                if (this._animatingZoom) return !0;\r\n"+
            "                if (i = i || {}, !this._zoomAnimated || i.animate === !1 || this._nothingToAnimate() || Math.abs(e - this._zoom) > this.options.zoomAnimationThreshold) return !1;\r\n"+
            "                var n = this.getZoomScale(e),\r\n"+
            "                    o = this._getCenterOffset(t)._divideBy(1 - 1 / n),\r\n"+
            "                    s = this._getCenterLayerPoint()._add(o);\r\n"+
            "                return i.animate === !0 || this.getSize().contains(o) ? (this.fire(\"movestart\").fire(\"zoomstart\"), this._animateZoom(t, e, s, n, null, !0), !0) : !1\r\n"+
            "            },\r\n"+
            "            _animateZoom: function(t, e, i, n, s, a, r) {\r\n"+
            "                r || (this._animatingZoom = !0), o.DomUtil.addClass(this._mapPane, \"leaflet-zoom-anim\"), this._animateToCenter = t, this._animateToZoom = e, o.Draggable && (o.Draggable._disabled = !0), o.Util.requestAnimFrame(function() {\r\n"+
            "                    this.fire(\"zoomanim\", {\r\n"+
            "                        center: t,\r\n"+
            "                        zoom: e,\r\n"+
            "                        origin: i,\r\n"+
            "                        scale: n,\r\n"+
            "                        delta: s,\r\n"+
            "                        backwards: a\r\n"+
            "                    }), setTimeout(o.bind(this._onZoomTransitionEnd, this), 250)\r\n"+
            "                }, this)\r\n"+
            "            },\r\n"+
            "            _onZoomTransitionEnd: function() {\r\n"+
            "                this._animatingZoom && (this._animatingZoom = !1, o.DomUtil.removeClass(this._mapPane, \"leaflet-zoom-anim\"), o.Util.requestAnimFrame(function() {\r\n"+
            "                    this._resetView(this._animateToCenter, this._animateToZoom, !0, !0), o.Draggable && (o.Draggable._disabled = !1)\r\n"+
            "                }, this))\r\n"+
            "            }\r\n"+
            "        } : {}), o.TileLayer.include({\r\n"+
            "            _animateZoom: function(t) {\r\n"+
            "                this._animating || (this._animating = !0, this._prepareBgBuffer());\r\n"+
            "                var e = this._bgBuffer,\r\n"+
            "                    i = o.DomUtil.TRANSFORM,\r\n"+
            "                    n = t.delta ? o.DomUtil.getTranslateString(t.delta) : e.style[i],\r\n"+
            "                    s = o.DomUtil.getScaleString(t.scale, t.origin);\r\n"+
            "                e.style[i] = t.backwards ? s + \" \" + n : n + \" \" + s\r\n"+
            "            },\r\n"+
            "            _endZoomAnim: function() {\r\n"+
            "                var t = this._tileContainer,\r\n"+
            "                    e = this._bgBuffer;\r\n"+
            "                t.style.visibility = \"\", t.parentNode.appendChild(t), o.Util.falseFn(e.offsetWidth);\r\n"+
            "                var i = this._map.getZoom();\r\n"+
            "                (i > this.options.maxZoom || i < this.options.minZoom) && this._clearBgBuffer(), this._animating = !1\r\n"+
            "            },\r\n"+
            "            _clearBgBuffer: function() {\r\n"+
            "                var t = this._map;\r\n"+
            "                !t || t._animatingZoom || t.touchZoom._zooming || (this._bgBuffer.innerHTML = \"\", this._bgBuffer.style[o.DomUtil.TRANSFORM] = \"\")\r\n"+
            "            },\r\n"+
            "            _prepareBgBuffer: function() {\r\n"+
            "                var t = this._tileContainer,\r\n"+
            "                    e = this._bgBuffer,\r\n"+
            "                    i = this._getLoadedTilesPercentage(e),\r\n"+
            "                    n = this._getLoadedTilesPercentage(t);\r\n"+
            "                return e && i > .5 && .5 > n ? (t.style.visibility = \"hidden\", void this._stopLoadingImages(t)) : (e.style.visibility = \"hidden\", e.style[o.DomUtil.TRANSFORM] = \"\", this._tileContainer = e, e = this._bgBuffer = t, this._stopLoadingImages(e), void clearTimeout(this._clearBgBufferTimer))\r\n"+
            "            },\r\n"+
            "            _getLoadedTilesPercentage: function(t) {\r\n"+
            "                var e, i, n = t.getElementsByTagName(\"img\"),\r\n"+
            "                    o = 0;\r\n"+
            "                for (e = 0, i = n.length; i > e; e++) n[e].complete && o++;\r\n"+
            "                return o / i\r\n"+
            "            },\r\n"+
            "            _stopLoadingImages: function(t) {\r\n"+
            "                var e, i, n, s = Array.prototype.slice.call(t.getElementsByTagName(\"img\"));\r\n"+
            "                for (e = 0, i = s.length; i > e; e++) n = s[e], n.complete || (n.onload = o.Util.f");
          out.print(
            "alseFn, n.onerror = o.Util.falseFn, n.src = o.Util.emptyImageUrl, n.parentNode.removeChild(n))\r\n"+
            "            }\r\n"+
            "        }), o.Map.include({\r\n"+
            "            _defaultLocateOptions: {\r\n"+
            "                watch: !1,\r\n"+
            "                setView: !1,\r\n"+
            "                maxZoom: 1 / 0,\r\n"+
            "                timeout: 1e4,\r\n"+
            "                maximumAge: 0,\r\n"+
            "                enableHighAccuracy: !1\r\n"+
            "            },\r\n"+
            "            locate: function(t) {\r\n"+
            "                if (t = this._locateOptions = o.extend(this._defaultLocateOptions, t), !navigator.geolocation) return this._handleGeolocationError({\r\n"+
            "                    code: 0,\r\n"+
            "                    message: \"Geolocation not supported.\"\r\n"+
            "                }), this;\r\n"+
            "                var e = o.bind(this._handleGeolocationResponse, this),\r\n"+
            "                    i = o.bind(this._handleGeolocationError, this);\r\n"+
            "                return t.watch ? this._locationWatchId = navigator.geolocation.watchPosition(e, i, t) : navigator.geolocation.getCurrentPosition(e, i, t), this\r\n"+
            "            },\r\n"+
            "            stopLocate: function() {\r\n"+
            "                return navigator.geolocation && navigator.geolocation.clearWatch(this._locationWatchId), this._locateOptions && (this._locateOptions.setView = !1), this\r\n"+
            "            },\r\n"+
            "            _handleGeolocationError: function(t) {\r\n"+
            "                var e = t.code,\r\n"+
            "                    i = t.message || (1 === e ? \"permission denied\" : 2 === e ? \"position unavailable\" : \"timeout\");\r\n"+
            "                this._locateOptions.setView && !this._loaded && this.fitWorld(), this.fire(\"locationerror\", {\r\n"+
            "                    code: e,\r\n"+
            "                    message: \"Geolocation error: \" + i + \".\"\r\n"+
            "                })\r\n"+
            "            },\r\n"+
            "            _handleGeolocationResponse: function(t) {\r\n"+
            "                var e = t.coords.latitude,\r\n"+
            "                    i = t.coords.longitude,\r\n"+
            "                    n = new o.LatLng(e, i),\r\n"+
            "                    s = 180 * t.coords.accuracy / 40075017,\r\n"+
            "                    a = s / Math.cos(o.LatLng.DEG_TO_RAD * e),\r\n"+
            "                    r = o.latLngBounds([e - s, i - a], [e + s, i + a]),\r\n"+
            "                    h = this._locateOptions;\r\n"+
            "                if (h.setView) {\r\n"+
            "                    var l = Math.min(this.getBoundsZoom(r), h.maxZoom);\r\n"+
            "                    this.setView(n, l)\r\n"+
            "                }\r\n"+
            "                var u = {\r\n"+
            "                    latlng: n,\r\n"+
            "                    bounds: r,\r\n"+
            "                    timestamp: t.timestamp\r\n"+
            "                };\r\n"+
            "                for (var c in t.coords) \"number\" == typeof t.coords[c] && (u[c] = t.coords[c]);\r\n"+
            "                this.fire(\"locationfound\", u)\r\n"+
            "            }\r\n"+
            "        })\r\n"+
            "}(window, document);\r\n"+
            "MAPBOX_ICON=L.icon({iconUrl:'rsc/marker.png'});\r\n"+
            "");

	}
	
}