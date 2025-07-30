package com.f1._jsps;

import com.f1.http.*;
import com.f1.utils.*;
import com.f1.http.handler.AbstractHttpHandler;
import com.f1.http.HttpUtils;
import static com.f1.http.HttpUtils.*;

public class portal_rsc_javascript_3d_js_1 extends AbstractHttpHandler{

	public portal_rsc_javascript_3d_js_1() {
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
            "function SurfaceWebGL(element){\r\n"+
            "	var that = this;\r\n"+
            "	this.element = element;\r\n"+
            "\r\n"+
            "	if (!Detector.webgl) {\r\n"+
            "	    var warning = Detector.getWebGLErrorMessage();\r\n"+
            "	    element.appendChild(warning);\r\n"+
            "	}\r\n"+
            "	else{\r\n"+
            "		this.xRot = 0;\r\n"+
            "		this.yRot = 0;\r\n"+
            "		this.zRot = 0;\r\n"+
            "		this.zoom = 1;\r\n"+
            "		this.focalX = 0;\r\n"+
            "		this.focalY = 0;\r\n"+
            "		this.centerX = 0;\r\n"+
            "		this.centerY = 0;\r\n"+
            "		this.fov = 70;\r\n"+
            "		this.z = 200;\r\n"+
            "		\r\n"+
            "	//	this.renderer = new THREE.WebGLRenderer();\r\n"+
            "		this.renderer = new THREE.WebGLRenderer({antialias:true});\r\n"+
            "		this.canvas = this.renderer.domElement;\r\n"+
            "		element.appendChild(this.canvas);\r\n"+
            "		\r\n"+
            "		this.start();\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "SurfaceWebGL.prototype.close=function(){\r\n"+
            "	this.data = null;\r\n"+
            "	this.freeTexts();\r\n"+
            "	this.freeLines();\r\n"+
            "	this.freePolys();\r\n"+
            "	if(this.scene)\r\n"+
            "		this.scene.remove(this.world);\r\n"+
            "	if(this.canvas)\r\n"+
            "		this.element.removeChild(this.canvas);\r\n"+
            "	if(this.renderer){\r\n"+
            "		this.renderer.dispose()\r\n"+
            "		this.renderer = null;\r\n"+
            "	}\r\n"+
            "	this.canvas = null;\r\n"+
            "}\r\n"+
            "\r\n"+
            "SurfaceWebGL.prototype.start=function(){\r\n"+
            "	this.scene = new THREE.Scene();\r\n"+
            "	this.scene.background = new THREE.Color(0xffffff);\r\n"+
            "	this.camera = new THREE.PerspectiveCamera( this.fov, this.canvas.width / this.canvas.height, 0.1, 10000 );\r\n"+
            "	this.raycaster = new THREE.Raycaster();\r\n"+
            "	this.mouse = new THREE.Vector2();\r\n"+
            "//	this.camera.filmGauge=12;\r\n"+
            "//	this.camera.position.z=this.z;\r\n"+
            "	this.euler = new THREE.Euler();\r\n"+
            "	this.world = new THREE.Group();\r\n"+
            "	this.world.rotation.order=\"XZY\";\r\n"+
            "	\r\n"+
            "//	this.ambientLight = new THREE.AmbientLight(0x000000);\r\n"+
            "//	this.scene.add(this.ambientLight);\r\n"+
            "	this.lights = [];\r\n"+
            "	this.lights[0] = new THREE.PointLight(0xffffff, 1, 0);\r\n"+
            "	this.lights[1] = new THREE.PointLight(0xffffff, 1, 0);\r\n"+
            "	this.lights[2] = new THREE.PointLight(0xffffff, 1, 0);\r\n"+
            "	this.lights[0].position.set(0, 200, 0); \r\n"+
            "	this.lights[1].position.set(100, 200, 100); \r\n"+
            "	this.lights[2].position.set(-100, -200, -100); \r\n"+
            "	this.scene.add(this.lights[0]);\r\n"+
            "	this.scene.add(this.lights[1]);\r\n"+
            "	this.scene.add(this.lights[2]);\r\n"+
            "	\r\n"+
            "	//Init materials\r\n"+
            "	//Text Material\r\n"+
            "	this.font = \"Arial\"\r\n"+
            "	this.fontSize = 32;\r\n"+
            "	this.lettersPerSide = 16;\r\n"+
            "	this.fontTexture = this.loadFont(this.font, this.fontSize, this.lettersPerSide);\r\n"+
            "	var uniforms = {\r\n"+
            "		    map : { type: \"t\", value: this.fontTexture }\r\n"+
            "		  };\r\n"+
            "	var vshader =\r\n"+
            "		\"precision highp float; uniform mat4 modelMatrix; uniform mat4 viewMatrix; uniform mat4 projectionMatrix; attribute vec3 position; attribute vec3 offset; attribute vec3 center; attribute vec2 uvoffset; attribute vec4 color; attribute vec2 uv; varying vec4 vColor; varying vec2 vUv; mat4 getT(vec3 v){ mat4 m = mat4(1.0); m[3][0] = v[0]; m[3][1] = v[1]; m[3][2] = v[2]; return m; } mat4 getInvR(mat4 m){ float sx = 1.0/ length(vec3(m[0][0], m[0][1], m[0][2])); float sy = 1.0/ length(vec3(m[1][0], m[1][1], m[1][2])); float sz = 1.0/ length(vec3(m[2][0], m[2][1], m[2][2])); mat4 m2 = mat4(1.0); m2[0][0] = m[0][0]* sx; m2[0][1] = m[1][0]* sx; m2[0][2] = m[2][0]* sx; m2[1][0] = m[0][1]* sy; m2[1][1] = m[1][1]* sy; m2[1][2] = m[2][1]* sy; m2[2][0] = m[0][2]* sz; m2[2][1] = m[1][2]* sz; m2[2][2] = m[2][2]* sz; return m2; } void main(){ vec4 pos = vec4(position+offset-center, 1.0); pos = getInvR(modelMatrix) * pos; pos = getT(center) * pos; pos = modelMatrix* pos; gl_Position = projectionMatrix * viewMatrix * pos; vColor = color; vUv = uv+uvoffset; }\";	\r\n"+
            "	var fshader = \r\n"+
            "		\"precision highp float; uniform sampler2D map; varying vec4 vColor; varying vec2 vUv; void main() { vec4 diffuse = texture2D(map, vUv); gl_FragColor =vec4(vColor.rgb, diffuse.a) ; }\";\r\n"+
            "	\r\n"+
            "	this.textMaterial = new THREE.RawShaderMaterial({uniforms : uniforms, vertexShader :vshader, fragmentShader : fshader, 	/* side: THREE.DoubleSide, wireframe: true,*/ transparent: true });	\r\n"+
            "	this.linesMaterial = new THREE.LineBasicMaterial( { linewidth: 40, vertexColors: THREE.VertexColors, shading: THREE.FlatShading } );\r\n"+
            "	this.material3 = new THREE.MeshLambertMaterial( { vertexColors:THREE.VertexColors, side:THREE.DoubleSide, shading:THREE.FlatShading} );\r\n"+
            "	\r\n"+
            "//	this.material = new THREE.MeshBasicMaterial( { color: 0x00ff00} );\r\n"+
            "//	this.material4 = new THREE.MeshStandardMaterial( { color: 0x00ff00, side:THREE.DoubleSide, shading:THREE.FlatShading} );\r\n"+
            "//	this.material5 = new THREE.MeshStandardMaterial( { vertexColors:THREE.VertexColors, side:THREE.DoubleSide, shading:THREE.FlatShading} );\r\n"+
            "//	this.material3 = new THREE.MeshLambertMaterial( { wireframe:true, vertexColors:THREE.VertexColors, side:THREE.DoubleSide, shading:THREE.FlatShading} );\r\n"+
            "//	this.material2 = new THREE.LineBasicMaterial({color:0x000000});\r\n"+
            "//	this.cube = new THREE.Mesh( this.boxGeometry, this.material );\r\n"+
            "	\r\n"+
            "	this.scene.add(this.world);\r\n"+
            "	\r\n"+
            "	\r\n"+
            "	\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "SurfaceWebGL.prototype.updateScale=function() {\r\n"+
            "	if(this.data != null  && this.data.polys != null){\r\n"+
            "		var polys = this.data.polys;\r\n"+
            "		var minX = minY = minZ = Infinity;\r\n"+
            "		var maxX = maxY = maxZ = -Infinity;\r\n"+
            "		\r\n"+
            "		for(var i = 0; i < polys.length; i++){\r\n"+
            "			minX = Math.min(minX, polys[i][0], polys[i][4], polys[i][8]);\r\n"+
            "			maxX = Math.max(maxX, polys[i][0], polys[i][4], polys[i][8]);\r\n"+
            "			minY = Math.min(minY, polys[i][1], polys[i][5], polys[i][9]);\r\n"+
            "			maxY = Math.max(maxY, polys[i][1], polys[i][5], polys[i][9]);\r\n"+
            "			minZ = Math.min(minZ, polys[i][2], polys[i][6], polys[i][10]);\r\n"+
            "			maxZ = Math.max(maxZ, polys[i][2], polys[i][6], polys[i][10]);\r\n"+
            "			\r\n"+
            "		}\r\n"+
            "		this.sfX = maxX - minX;\r\n"+
            "		this.sfY = maxY - minY;\r\n"+
            "		this.sfZ = maxZ - minZ;\r\n"+
            "		this.sfX = this.sfY = this.sfZ = Math.min(this.sfX, this.sfY, this.sfZ);\r\n"+
            "	}\r\n"+
            "	else{\r\n"+
            "		this.sfX = 50;\r\n"+
            "		this.sfY = 50;\r\n"+
            "		this.sfZ = 50;\r\n"+
            "	}\r\n"+
            "//	err([this.sfX, this.sfY, this.sfZ]);\r\n"+
            "}\r\n"+
            "SurfaceWebGL.prototype.updateData=function(data) {\r\n"+
            "	this.data = data;\r\n"+
            "	this.updateScale();\r\n"+
            "	this.prepareLines();\r\n"+
            "	this.preparePolys();\r\n"+
            "	this.prepareTexts();\r\n"+
            "}\r\n"+
            "SurfaceWebGL.prototype.getTriangleId=function(x,y){\r\n"+
            "	var triId = null;\r\n"+
            "	if(!this.polys)\r\n"+
            "		return triId;\r\n"+
            "	this.mouse.x = (x/this.canvas.width) * 2 -1;\r\n"+
            "	this.mouse.y = -(y/this.canvas.height) * 2 +1;\r\n"+
            "	this.raycaster.setFromCamera(this.mouse, this.camera);\r\n"+
            "	\r\n"+
            "	var out = this.raycaster.intersectObject(this.polys);\r\n"+
            "	if(out.length){\r\n"+
            "		var id = out[0].faceIndex/3;\r\n"+
            "		if(id > 0)\r\n"+
            "			triId = id;\r\n"+
            "	}\r\n"+
            "	return triId;\r\n"+
            "}\r\n"+
            "SurfaceWebGL.prototype.getPolygonId=function(x,y){\r\n"+
            "	var polyId = null;\r\n"+
            "	if(!this.polys)\r\n"+
            "		return polyId;\r\n"+
            "	this.mouse.x = (x/this.canvas.width) * 2 -1;\r\n"+
            "	this.mouse.y = -(y/this.canvas.height) * 2 +1;\r\n"+
            "	this.raycaster.setFromCamera(this.mouse, this.camera);\r\n"+
            "	\r\n"+
            "	var out = this.raycaster.intersectObject(this.polys);\r\n"+
            "	if(out.length){\r\n"+
            "		var index =out[0].faceIndex/3;\r\n"+
            "		var id = this.data.polys[index][12];\r\n"+
            "		if(id > 0)\r\n"+
            "			polyId = id;\r\n"+
            "	}\r\n"+
            "	return polyId;\r\n"+
            "}\r\n"+
            "\r\n"+
            "SurfaceWebGL.prototype.getPolygonIds=function(x1,y1,x2,y2){\r\n"+
            "    var sel={};\r\n"+
            "    var selList=[];\r\n"+
            "    \r\n"+
            "	if(!this.polys)\r\n"+
            "		return selList;\r\n"+
            "	\r\n"+
            "    if(x2<x1){var t=x1;x1=x2;x2=t;}\r\n"+
            "    if(y2<y1){var t=y1;y1=y2;y2=t;}\r\n"+
            "    \r\n"+
            "    var w = this.canvas.width;\r\n"+
            "    var h = this.canvas.height;\r\n"+
            "    \r\n"+
            "    var m = new THREE.Vector3(2*(x1/w) - 1.0, -2*(y2/h) + 1.0, 0);\r\n"+
            "    var m2 = new THREE.Vector3(2*(x2/w) - 1.0, -2*(y1/h) + 1.0, 0);\r\n"+
            "    \r\n"+
            "    var position = this.polys.geometry.attributes.position;\r\n"+
            "    var i, l;\r\n"+
            "    var vA, vB, vC;\r\n"+
            "    vA = new THREE.Vector3();\r\n"+
            "    vB = new THREE.Vector3();\r\n"+
            "    vC = new THREE.Vector3();\r\n"+
            "    \r\n"+
            "    \r\n"+
            "    for(i = 0, l = position.count; i < l; i+=3){\r\n"+
            "   		var id = this.data.polys[Math.floor(i/3)][12];\r\n"+
            "   		if(id > 0){\r\n"+
            "	   		if(sel[id] == true)\r\n"+
            "	   			continue;\r\n"+
            "	    	\r\n"+
            "	    	//check for intersection\r\n"+
            "	    	var intersect = false;\r\n"+
            "	    	\r\n"+
            "	    	vA.fromBufferAttribute(position,i);\r\n"+
            "	    	vB.fromBufferAttribute(position,i+1);\r\n"+
            "	    	vC.fromBufferAttribute(position,i+2);\r\n"+
            "	    	vA.applyEuler(this.euler);\r\n"+
            "	    	vB.applyEuler(this.euler);\r\n"+
            "	    	vC.applyEuler(this.euler);\r\n"+
            "	    	vA.project(this.camera);\r\n"+
            "	    	vB.project(this.camera);\r\n"+
            "	    	vC.project(this.camera);\r\n"+
            "	    	var mnX = Math.min(vA.x, vB.x, vC.x);\r\n"+
            "	    	var mxX = Math.max(vA.x, vB.x, vC.x);\r\n"+
            "	    	var mnY = Math.min(vA.y, vB.y, vC.y);\r\n"+
            "	    	var mxY = Math.max(vA.y, vB.y, vC.y);\r\n"+
            "	    	\r\n"+
            "	    	if(m.x < mxX && mnX < m2.x &&  m.y < mxY && mnY < m2.y){\r\n"+
            "	    		intersect = true;\r\n"+
            "	    	}\r\n"+
            "	    	if(intersect){\r\n"+
            "				sel[id]=true;\r\n"+
            "				selList[selList.length]=id;\r\n"+
            "	    	}\r\n"+
            "   		}\r\n"+
            "    }\r\n"+
            "    return selList;\r\n"+
            "}\r\n"+
            "\r\n"+
            "SurfaceWebGL.prototype.drawScene=function() {\r\n"+
            "	var xr = (180-this.xRot)*Math.PI/180;\r\n"+
            "	var yr = this.yRot*Math.PI/180;\r\n"+
            "	var zr = this.zRot*Math.PI/180;\r\n"+
            "	this.euler.set(xr,yr,zr, \"XZY\");\r\n"+
            "	this.world.setRotationFromEuler(this.euler);\r\n"+
            "	\r\n"+
            "	\r\n"+
            "	var fl = this.camera.getFocalLength();\r\n"+
            "	this.camera.position.x=this.focalX/fl;\r\n"+
            "	this.camera.position.y=this.focalY/fl;\r\n"+
            "	this.camera.position.z = fl* this.z/this.fov;\r\n"+
            "//	this.camera.lookAt(new THREE.Vector3(0,0,-100));\r\n"+
            "//	this.camera.lookAt(this.scene.position);\r\n"+
            "	\r\n"+
            "	this.camera.updateMatrixWorld();\r\n"+
            "	this.camera.updateProjectionMatrix();\r\n"+
            "	\r\n"+
            "	this.renderer.render( this.scene, this.camera );\r\n"+
            "}\r\n"+
            "\r\n"+
            "SurfaceWebGL.prototype.loadFont=function(font, fontSize, lettersPerSide) {\r\n"+
            "	  var yfontSize = fontSize+4;\r\n"+
            "	  var c = document.createElement('canvas');\r\n"+
            "	  c.width = fontSize*lettersPerSide;\r\n"+
            "	  c.height = yfontSize*lettersPerSide;\r\n"+
            "	  var ctx = c.getContext('2d');\r\n"+
            "	  ctx.font = fontSize+'px ' + font;\r\n"+
            "	  var i=0;\r\n"+
            "\r\n"+
            "	  for (var y=0; y<lettersPerSide; y++) {\r\n"+
            "	  	// ctx.beginPath();\r\n"+
            "	  	// ctx.moveTo(0, y*yfontSize);\r\n"+
            "	  	// ctx.lineTo(c.width, y*yfontSize);\r\n"+
            "	  	// ctx.strokeStyle= '#00ff00';\r\n"+
            "	  	// ctx.stroke();\r\n"+
            "\r\n"+
            "	  	// ctx.beginPath();\r\n"+
            "	  	// ctx.moveTo(0, (y+1)*yfontSize-1);\r\n"+
            "	  	// ctx.lineTo(c.width, (y+1)*yfontSize-1);\r\n"+
            "	  	// ctx.strokeStyle= '#ff0000';\r\n"+
            "	  	// ctx.stroke();\r\n"+
            "	    for (var x=0; x<lettersPerSide; x++,i++) {\r\n"+
            "	      var ch = String.fromCharCode(i);\r\n"+
            "	      var cx = +(5/32)*fontSize+x*fontSize;\r\n"+
            "	      var cy =  -(10/32)*yfontSize+(y+1)*yfontSize;\r\n"+
            "	      ctx.fillText(ch, cx, cy);\r\n"+
            "	    }\r\n"+
            "	  }\r\n"+
            "\r\n"+
            "	  var tex = new THREE.Texture(c);\r\n"+
            "	  tex.flipY = false;\r\n"+
            "	  tex.needsUpdate = true;\r\n"+
            "	  return tex;	\r\n"+
            "}\r\n"+
            "\r\n"+
            "Surfa");
          out.print(
            "ceWebGL.prototype.freeTexts=function(){\r\n"+
            "	if(this.textsGeometry != null){\r\n"+
            "		this.textsGeometry.dispose();\r\n"+
            "		this.textsGeometry.attributes = null;\r\n"+
            "		this.textsGeometry = null;\r\n"+
            "	}\r\n"+
            "	if(this.textsMaterial != null){\r\n"+
            "		this.textsMaterial.dispose();\r\n"+
            "	}\r\n"+
            "	\r\n"+
            "	if(this.texts)\r\n"+
            "		this.world.remove(this.texts);\r\n"+
            "}\r\n"+
            "SurfaceWebGL.prototype.prepareTexts=function(){\r\n"+
            "	if(!this.data.texts)\r\n"+
            "		return;\r\n"+
            "	\r\n"+
            "	this.freeTexts();\r\n"+
            "	\r\n"+
            "	// Create new InstancedBufferGeometry (Square)\r\n"+
            "	this.textsGeometry = new THREE.InstancedBufferGeometry();\r\n"+
            "	// 2 triangles, 3 points, 3 dims\r\n"+
            "	var sz = (1.2/this.sfY)/2;\r\n"+
            "	var vertices = new THREE.BufferAttribute(new Float32Array([\r\n"+
            "	         -sz, -sz, 0.0,\r\n"+
            "	         +sz, -sz, 0.0,\r\n"+
            "	         -sz, +sz, 0.0,\r\n"+
            "	         \r\n"+
            "	         +sz, +sz, 0.0,\r\n"+
            "	         -sz, +sz, 0.0,\r\n"+
            "	         +sz, -sz, 0.0,\r\n"+
            "			]), 3);\r\n"+
            "	this.textsGeometry.addAttribute('position', vertices);\r\n"+
            "	\r\n"+
            "	var usz = (this.fontSize -1) /(this.fontSize * this.lettersPerSide); \r\n"+
            "	var vertices = new THREE.BufferAttribute(new Float32Array([\r\n"+
            "	         0.0, usz, \r\n"+
            "	         usz, usz,\r\n"+
            "	         0.0, 0.0,\r\n"+
            "	         \r\n"+
            "	         usz, 0.0,\r\n"+
            "	         0.0, 0.0,\r\n"+
            "	         usz, usz,\r\n"+
            "			]), 2);\r\n"+
            "	this.textsGeometry.addAttribute('uv', vertices);\r\n"+
            "	\r\n"+
            "	// Count number instances\r\n"+
            "	var instances = 0;\r\n"+
            "	\r\n"+
            "	var texs = this.data.texts;\r\n"+
            "	for(var il = 0; il < texs.length; il++){\r\n"+
            "		var str = texs[il][3];\r\n"+
            "		instances += str.length;\r\n"+
            "	}\r\n"+
            "	\r\n"+
            "	// Find the offsets\r\n"+
            "	\r\n"+
            "	var centers = new THREE.InstancedBufferAttribute( new Float32Array( instances * 3 ), 3, 1 );\r\n"+
            "	var offsets = new THREE.InstancedBufferAttribute( new Float32Array( instances * 3 ), 3, 1 );\r\n"+
            "	var uvoffsets = new THREE.InstancedBufferAttribute( new Float32Array( instances * 2 ), 2, 1 );\r\n"+
            "	var colors = new THREE.InstancedBufferAttribute( new Float32Array( instances * 4 ), 4, 1 );\r\n"+
            "\r\n"+
            "	var oi = 0;\r\n"+
            "	var ui = 0;\r\n"+
            "	for(var i1 = 0; i1 < texs.length; i1++){\r\n"+
            "		var text = texs[i1];\r\n"+
            "		var str = text[3];\r\n"+
            "		var oox = text[0]/this.sfX;\r\n"+
            "		var ooy = text[1]/this.sfY;\r\n"+
            "		var ooz = text[2]/this.sfZ;\r\n"+
            "		var color = text[5];\r\n"+
            "		var r= (color >> 16 & 255) /255.0;\r\n"+
            "		var g= (color >> 8 & 255) /255.0;\r\n"+
            "		var b= (color & 255) /255.0;\r\n"+
            "		var j=0, ln=0;\r\n"+
            "		for (i=0; i<str.length; i++) {\r\n"+
            "			var code = str.charCodeAt(i);\r\n"+
            "			var cx = code % this.lettersPerSide;\r\n"+
            "			var cy = Math.floor(code / this.lettersPerSide);\r\n"+
            "			\r\n"+
            "			var ox=cx/this.lettersPerSide ;\r\n"+
            "			var oy=cy/this.lettersPerSide ;\r\n"+
            "			\r\n"+
            "			centers.setXYZ(oi, oox, ooy, ooz);\r\n"+
            "			offsets.setXYZ(oi, j*2.0*sz+oox, ln*2.0*sz+ooy, ooz);\r\n"+
            "			colors.setXYZ(oi, r,g,b, Math.random());\r\n"+
            "			uvoffsets.setXYZ(oi, ox, oy);\r\n"+
            "			\r\n"+
            "			oi++;\r\n"+
            "			if (code == 10) {\r\n"+
            "				ln--;\r\n"+
            "				j=0;\r\n"+
            "			} else {\r\n"+
            "				j++;\r\n"+
            "			}\r\n"+
            "		}\r\n"+
            "	}\r\n"+
            "	this.textsGeometry.addAttribute( 'center', centers );\r\n"+
            "	this.textsGeometry.addAttribute( 'offset', offsets );\r\n"+
            "	this.textsGeometry.addAttribute( 'color', colors );\r\n"+
            "	this.textsGeometry.addAttribute( 'uvoffset', uvoffsets);\r\n"+
            "	 \r\n"+
            "	this.texts = new THREE.Mesh(this.textsGeometry, this.textMaterial);\r\n"+
            "	this.world.add(this.texts);\r\n"+
            "}\r\n"+
            "\r\n"+
            "SurfaceWebGL.prototype.freeLines=function(){\r\n"+
            "	if(this.linesGeometry != null){\r\n"+
            "		this.linesGeometry.clearGroups();\r\n"+
            "		this.linesGeometry.dispose();\r\n"+
            "		this.linesGeometry.attributes = null;\r\n"+
            "		this.linesGeometry = null;\r\n"+
            "	}\r\n"+
            "	if(this.linesMaterial != null){\r\n"+
            "		this.linesMaterial.dispose();\r\n"+
            "	}\r\n"+
            "	\r\n"+
            "	if(this.lines)\r\n"+
            "		this.world.remove(this.lines);\r\n"+
            "}\r\n"+
            "SurfaceWebGL.prototype.prepareLines=function(){\r\n"+
            "	if(!this.data.lines)\r\n"+
            "		return;\r\n"+
            "	\r\n"+
            "	this.freeLines();\r\n"+
            "//	if(this.axis)\r\n"+
            "//		return;\r\n"+
            "	\r\n"+
            "	var lines = this.data.lines;\r\n"+
            "	\r\n"+
            "	var vertices = [];\r\n"+
            "	var colorsRGB = [];\r\n"+
            "	for(var i = 0; i < lines.length; i++){\r\n"+
            "		vertices.push(lines[i][0]/ this.sfX);\r\n"+
            "		vertices.push(lines[i][1]/ this.sfY);\r\n"+
            "		vertices.push(lines[i][2]/ this.sfZ);\r\n"+
            "			\r\n"+
            "		vertices.push(lines[i][3]/ this.sfX);\r\n"+
            "		vertices.push(lines[i][4]/ this.sfY);\r\n"+
            "		vertices.push(lines[i][5]/ this.sfZ);\r\n"+
            "		colorsRGB.push((lines[i][6] >> 16 & 255) / 255.0);\r\n"+
            "		colorsRGB.push((lines[i][6] >> 8 & 255) / 255.0);\r\n"+
            "		colorsRGB.push((lines[i][6] & 255) / 255.0);\r\n"+
            "		colorsRGB.push((lines[i][6] >> 16 & 255) / 255.0);\r\n"+
            "		colorsRGB.push((lines[i][6] >> 8 & 255) / 255.0);\r\n"+
            "		colorsRGB.push((lines[i][6] & 255) / 255.0);\r\n"+
            "	}\r\n"+
            "	\r\n"+
            "	this.linesGeometry = new THREE.BufferGeometry();\r\n"+
            "	\r\n"+
            "	var verticesArray = new Float32Array(vertices);\r\n"+
            "	this.linesGeometry.addAttribute('position', new THREE.BufferAttribute(verticesArray, 3));\r\n"+
            "	\r\n"+
            "	var colorsArray = new Float32Array(colorsRGB);\r\n"+
            "	this.linesGeometry.addAttribute('color', new THREE.BufferAttribute(colorsArray, 3));\r\n"+
            "	\r\n"+
            "	this.lines = new THREE.LineSegments(this.linesGeometry, this.linesMaterial);\r\n"+
            "	this.world.add(this.lines);\r\n"+
            "	\r\n"+
            "	return vertices;\r\n"+
            "	\r\n"+
            "}\r\n"+
            "\r\n"+
            "SurfaceWebGL.prototype.freePolys=function(){\r\n"+
            "	if(this.polysGeometry != null){\r\n"+
            "		this.polysGeometry.clearGroups();\r\n"+
            "		this.polysGeometry.dispose();\r\n"+
            "		this.polysGeometry.attributes = null;\r\n"+
            "		this.polysGeometry = null;\r\n"+
            "	}\r\n"+
            "	if(this.polysMaterial != null){\r\n"+
            "		this.polysMaterial.dispose();\r\n"+
            "	}\r\n"+
            "	\r\n"+
            "	if(this.polys)\r\n"+
            "		this.world.remove(this.polys);\r\n"+
            "}\r\n"+
            "\r\n"+
            "SurfaceWebGL.prototype.preparePolys=function(){\r\n"+
            "	if(!this.data.polys)\r\n"+
            "		return;\r\n"+
            "	this.freePolys();\r\n"+
            "	\r\n"+
            "	var vertices = [];\r\n"+
            "	\r\n"+
            "	var polys = this.data.polys;\r\n"+
            "	\r\n"+
            "	var ax;\r\n"+
            "	var bx;\r\n"+
            "	var cx;\r\n"+
            "	for(var i = 0; i < polys.length; i++){\r\n"+
            "		vertices.push(polys[i][0]/this.sfX);\r\n"+
            "		vertices.push(polys[i][1]/this.sfY);\r\n"+
            "		vertices.push(polys[i][2]/this.sfZ);\r\n"+
            "			\r\n"+
            "		vertices.push(polys[i][4]/this.sfX);\r\n"+
            "		vertices.push(polys[i][5]/this.sfY);\r\n"+
            "		vertices.push(polys[i][6]/this.sfZ);\r\n"+
            "		\r\n"+
            "		vertices.push(polys[i][8]/this.sfX);\r\n"+
            "		vertices.push(polys[i][9]/this.sfY);\r\n"+
            "		vertices.push(polys[i][10]/this.sfZ);\r\n"+
            "	}\r\n"+
            "	\r\n"+
            "	var colorsHex = [];\r\n"+
            "	for(var i = 0; i < polys.length; i++){\r\n"+
            "		colorsHex.push(polys[i][3]);\r\n"+
            "		colorsHex.push(polys[i][7]);\r\n"+
            "		colorsHex.push(polys[i][11]);\r\n"+
            "	}\r\n"+
            "	var colorsRGB = [];\r\n"+
            "	var r;\r\n"+
            "	var g;\r\n"+
            "	var b;\r\n"+
            "	for(var i = 0; i < colorsHex.length; i++){\r\n"+
            "		r = (colorsHex[i] >> 16 & 255) / 255.0\r\n"+
            "		g = (colorsHex[i] >> 8 & 255) / 255.0\r\n"+
            "		b = (colorsHex[i] & 255) / 255.0\r\n"+
            "		colorsRGB.push(r);\r\n"+
            "		colorsRGB.push(g);\r\n"+
            "		colorsRGB.push(b);\r\n"+
            "	}\r\n"+
            "	\r\n"+
            "	this.polysGeometry = new THREE.BufferGeometry();\r\n"+
            "	var verticesArray = new Float32Array(vertices);\r\n"+
            "	var colorsArray = new Float32Array(colorsRGB);\r\n"+
            "	\r\n"+
            "	this.polysGeometry.addAttribute('position', new THREE.BufferAttribute(verticesArray, 3));\r\n"+
            "	this.polysGeometry.addAttribute('color', new THREE.BufferAttribute(colorsArray, 3));\r\n"+
            "	this.polysGeometry.computeFaceNormals();\r\n"+
            "	this.polysGeometry.computeVertexNormals();\r\n"+
            "	this.polys = new THREE.Mesh(this.polysGeometry, this.material3);\r\n"+
            "	this.world.add(this.polys);\r\n"+
            "	return vertices;\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "\r\n"+
            "\r\n"+
            "SurfaceWebGL.prototype.setSize=function(width,height) {\r\n"+
            "//	this.canvas.width = width;\r\n"+
            "//	this.canvas.height = height;\r\n"+
            "//	this.gl.viewport(0,0,width,height);\r\n"+
            "	this.camera.aspect = width/height;\r\n"+
            "	this.camera.updateProjectionMatrix();\r\n"+
            "	this.renderer.setSize( width, height);\r\n"+
            "}\r\n"+
            "SurfaceWebGL.prototype.setZoom=function(zoom){\r\n"+
            "	this.zoom = zoom;\r\n"+
            "	this.camera.zoom=this.zoom;\r\n"+
            "}\r\n"+
            "SurfaceWebGL.prototype.setFov=function(fov){\r\n"+
            "	this.fov = fov;\r\n"+
            "	this.camera.fov = this.fov;\r\n"+
            "	this.camera.updateProjectionMatrix();\r\n"+
            "}\r\n"+
            "SurfaceWebGL.prototype.setBackground=function(color){\r\n"+
            "	this.scene.background = new THREE.Color(color);\r\n"+
            "}\r\n"+
            "SurfaceWebGL.prototype.setFocalX=function(x){\r\n"+
            "	this.focalX = x;\r\n"+
            "}\r\n"+
            "SurfaceWebGL.prototype.setFocalY=function(y){\r\n"+
            "	this.focalY = y;\r\n"+
            "}\r\n"+
            "SurfaceWebGL.prototype.repaint=function(data){\r\n"+
            "	this.data = data;\r\n"+
            "	this.drawScene();\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "function Surface(element){ \r\n"+
            "  var that=this; \r\n"+
            "  this.surfaceGL = new SurfaceWebGL(element);\r\n"+
            "  this.textbuf=nw('canvas');\r\n"+
            "  this.textbuf.width=500;\r\n"+
            "  this.textbuf.height=40;\r\n"+
            "\r\n"+
            "  this.textbufContext=this.textbuf.getContext('2d');\r\n"+
            "  this.textbufContext.font=\"20px Arial\";\r\n"+
            "  this.textbufContext.fillStyle=\"black\";\r\n"+
            "  this.textbufContext.textAlign='left';\r\n"+
            "  this.textbufContext.textBaseline='top';\r\n"+
            "  this.rotScale=Math.PI/180;\r\n"+
            "  this.element=element;\r\n"+
            "  this.element.style.background='white';\r\n"+
            "  this.element.style.width='100%';\r\n"+
            "  this.element.style.height='100%';\r\n"+
            "  this.canvas = this.surfaceGL.canvas;\r\n"+
            "//  this.canvas=nw('canvas');\r\n"+
            "  this.context = this.canvas.getContext('2d');\r\n"+
            "  this.centerYOffset=0;\r\n"+
            "  this.centerXOffset=0;\r\n"+
            "  this.selCanvas=nw('canvas');\r\n"+
            "  this.setSize(10,10);\r\n"+
            "  this.selCanvas.tabIndex=1;\r\n"+
            "  makeCanvasKeyEventable(this.selCanvas,this);\r\n"+
            "  this.selContext = this.selCanvas.getContext('2d');\r\n"+
            "  \r\n"+
            "  makeDraggable(this.selCanvas);\r\n"+
            "  this.selCanvas.ondraggingStart=function(target,e){\r\n"+
            "    var button=getMouseButton(e);\r\n"+
            "    that.dragSelect=button!=1 || (!e.altKey && (e.shiftKey || e.ctrlKey));\r\n"+
            "    that.dragSelectToggle=e.ctrlKey;\r\n"+
            "    that.dragSelectAdd=e.shiftKey;\r\n"+
            "    that.dragDeltaX=0;\r\n"+
            "    that.dragDeltaY=0;\r\n"+
            "  };\r\n"+
            "  this.selCanvas.onMouseWheel=function(e,delta){\r\n"+
            "    if(e.altKey){\r\n"+
            "    	//log(delta);\r\n"+
            "      var t=that.fov;\r\n"+
            "      t+=that.fov*that.fov*(delta/1500);\r\n"+
            "      //t=Math.sqrt(t);\r\n"+
            "      that.setFov(t);\r\n"+
            "    	//log(that.getFov());\r\n"+
            "    }else{\r\n"+
            "      that.setZoom(delta/2+that.getZoom(),true);\r\n"+
            "    }\r\n"+
            "    that.repaintIfNeeded();\r\n"+
            "    that.fireOnUserChangedPerspective();\r\n"+
            "  };\r\n"+
            "  this.selCanvas.ondblclick=function(){that.dumpPosition();/*that.selCanvas.focus()*/};\r\n"+
            "  this.selCanvas.ondragging=function(target,deltax,deltay,e){ that.onDragging(target,deltax,deltay,e); };\r\n"+
            "  this.selCanvas.ondraggingEnd=function(target,deltax,deltay,e){ that.onDraggingEnd(target,deltax,deltay,e); };\r\n"+
            "  this.selCanvas.onmouseout=function(e){that.onMouseOut(e);};\r\n"+
            "  this.selCanvas.onmousemove=function(e){that.onMouseMove(e);};\r\n"+
            "  this.canvas.style.position='absolute';\r\n"+
            "  this.selCanvas.style.position='absolute';\r\n"+
            "  this.element.appendChild(this.canvas);\r\n"+
            "  this.element.appendChild(this.selCanvas);\r\n"+
            "  this.setRotationX(0);\r\n"+
            "  this.setRotationY(0);\r\n"+
            "  this.setRotationZ(0);\r\n"+
            "  this.focalX=0;\r\n"+
            "  this.focalY=0;\r\n"+
            "  this.zoom=1;\r\n"+
            "  this.fov=400;\r\n"+
            "  this.setOptions({});\r\n"+
            "  this.needsRepaint();\r\n"+
            "  this.zoomMin=1;\r\n"+
            "  this.zoomMax=40;\r\n"+
            "  t");
          out.print(
            "his.fovMin=70;\r\n"+
            "  this.fovMax=1000;\r\n"+
            "} \r\n"+
            "\r\n"+
            "Surface.prototype.close=function(){\r\n"+
            "	this.texts = null;\r\n"+
            "	this.polys = null;\r\n"+
            "	this.lines = null\r\n"+
            "	this.surfaceGL.close();\r\n"+
            "	this.surfaceGL = null;\r\n"+
            "	\r\n"+
            "}\r\n"+
            "Surface.prototype.onMouseMove=function(e){\r\n"+
            "    var point=getMouseLayerPoint(e);\r\n"+
            "    if(this.mmlastX==point.x && this.mmlastY==point.y)\r\n"+
            "    	return;\r\n"+
            "    this.mmlastX=point.x;\r\n"+
            "    this.mmlastY=point.y;\r\n"+
            "	if(this.hoverTimer!=null){\r\n"+
            "		clearTimeout(this.hoverTimer);\r\n"+
            "		this.hoverTimer=null;\r\n"+
            "	}\r\n"+
            "	if(this.onHover!=null){\r\n"+
            "	  var that=this;\r\n"+
            "      this.hoverTimer=setTimeout(function(){that.onMouseStill(e);}, 100);\r\n"+
            "    }\r\n"+
            "}\r\n"+
            "Surface.prototype.onMouseOut=function(e){\r\n"+
            "	if(this.hoverTimer!=null){\r\n"+
            "		clearTimeout(this.hoverTimer);\r\n"+
            "		this.hoverTimer=null;\r\n"+
            "	}\r\n"+
            "	this.clearHover();\r\n"+
            "}\r\n"+
            "\r\n"+
            "Surface.prototype.onMouseStill=function(e){\r\n"+
            "	if(this.hoverTimer!=null){\r\n"+
            "		clearTimeout(this.hoverTimer);\r\n"+
            "		this.hoverTimer=null;\r\n"+
            "	}\r\n"+
            "    var point=getMouseLayerPoint(e);\r\n"+
            "    var x1=point.x;\r\n"+
            "    var y1=point.y;\r\n"+
            "    var pid=this.surfaceGL.getTriangleId(x1,y1);\r\n"+
            "    if(pid!=this.hoverPid){\r\n"+
            "	  this.clearHover();\r\n"+
            "      this.hoverPid=pid;\r\n"+
            "      this.hoverRequest=pid;\r\n"+
            "      this.onHover(pid,x1,y1);\r\n"+
            "    }else if(pid==null)\r\n"+
            "	  this.clearHover();\r\n"+
            "}\r\n"+
            "\r\n"+
            "Surface.prototype.setHover=function(x,y,sel,name,xAlign,yAlign){\r\n"+
            "	  if(this.hoverRequest==sel){\r\n"+
            "		if(this.tooltipDiv!=null)\r\n"+
            "		  this.element.removeChild(this.tooltipDiv);\r\n"+
            "	    this.tooltipDiv=nw(\"div\",\"ami_chart_tooltip\");\r\n"+
            "		var div=this.tooltipDiv;\r\n"+
            "		this.hoverX=MOUSE_POSITION_X;\r\n"+
            "		this.hoverY=MOUSE_POSITION_Y;\r\n"+
            "		div.innerHTML=name;\r\n"+
            "		if(div.firstChild!=null && div.firstChild.tagName=='DIV'){\r\n"+
            "			this.tooltipDiv=div.firstChild;\r\n"+
            "		    div=this.tooltipDiv;\r\n"+
            "		}\r\n"+
            "		this.element.appendChild(div);\r\n"+
            "		var rect=new Rect().readFromElement(div);\r\n"+
            "		var h=rect.height;\r\n"+
            "		var w=rect.width;\r\n"+
            "		switch(xAlign){\r\n"+
            "		  case ALIGN_LEFT: div.style.left=toPx(x); break;\r\n"+
            "		  case ALIGN_RIGHT: div.style.left=toPx(x-w); break;\r\n"+
            "		  default: div.style.left=toPx(x-w/2); break;\r\n"+
            "		}\r\n"+
            "		switch(yAlign){\r\n"+
            "		  case ALIGN_TOP: div.style.top=toPx(y); break;\r\n"+
            "		  case ALIGN_BOTTOM: div.style.top=toPx(y-h); break;\r\n"+
            "		  default: div.style.top=toPx(y-h/2); break;\r\n"+
            "		}\r\n"+
            "		ensureInDiv(div,this.element);\r\n"+
            "	  }\r\n"+
            "	}\r\n"+
            "\r\n"+
            "Surface.prototype.clearHover=function(){\r\n"+
            "  if(this.tooltipDiv!=null){\r\n"+
            "    this.element.removeChild(this.tooltipDiv);\r\n"+
            "    this.tooltipDiv=null;\r\n"+
            "  }\r\n"+
            "  this.hoverRequest=null;\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "\r\n"+
            "Surface.prototype.getPolygonIds=function(x1,y1,x2,y2){\r\n"+
            "    if(x2<x1){var t=x1;x1=x2;x2=t;}\r\n"+
            "    if(y2<y1){var t=y1;y1=y2;y2=t;}\r\n"+
            "    var sel={};\r\n"+
            "    var selList=[];\r\n"+
            "    for(var x=x1;x<=x2;x++){\r\n"+
            "      for(var y=y1;y<=y2;y++){\r\n"+
            "        var id=this.getPolygonId(x,y);\r\n"+
            "        if(id!=null){\r\n"+
            "          poly=this.currentPolys[id];\r\n"+
            "          if(poly!=null){\r\n"+
            "            var polyId=poly[12];\r\n"+
            "            if(polyId>0){\r\n"+
            "              if(!sel[polyId]){\r\n"+
            "                sel[polyId]=true;\r\n"+
            "                selList[selList.length]=polyId;\r\n"+
            "              }\r\n"+
            "            }\r\n"+
            "          }\r\n"+
            "        }\r\n"+
            "      }\r\n"+
            "    }\r\n"+
            "    return selList;\r\n"+
            "}\r\n"+
            "\r\n"+
            "Surface.prototype.onDraggingEnd=function(target,deltax,deltay,e){\r\n"+
            "  if(this.dragSelect || (deltax==0 && deltay==0)){\r\n"+
            "    var point=getMouseLayerPoint(e);\r\n"+
            "    var x1=point.x;\r\n"+
            "    var y1=point.y;\r\n"+
            "    var x2=x1-deltax;\r\n"+
            "    var y2=y1-deltay;\r\n"+
            "	var selList=this.surfaceGL.getPolygonIds(x1,y1,x2,y2);\r\n"+
            "\r\n"+
            "    \r\n"+
            "    var clearSelect=selList.length==0 && deltax<2 && deltay<2 && deltax>-2 && deltay>-2;\r\n"+
            "    if(this.onSelectionChanged)\r\n"+
            "      this.onSelectionChanged(selList,this.dragSelectToggle,this.dragSelectAdd,clearSelect);\r\n"+
            "    this.selContext.clearRect(0,0,this.width,this.height);\r\n"+
            "        if(getMouseButton(e)==2){\r\n"+
            "      if(this.onShowContextMenu)\r\n"+
            "        this.onShowContextMenu();\r\n"+
            "    }\r\n"+
            "    return;\r\n"+
            "  }\r\n"+
            "}\r\n"+
            "Surface.prototype.onDragging=function(target,deltax,deltay,e){\r\n"+
            "  if(this.dragSelect){\r\n"+
            "    var point=getMouseLayerPoint(e);\r\n"+
            "    this.selContext.clearRect(0,0,this.width,this.height);\r\n"+
            "    this.selContext.lineWidth='2';\r\n"+
            "    this.selContext.strokeStyle='rgba(64,64,64,.5)';\r\n"+
            "    this.selContext.strokeRect(point.x,point.y,-deltax,-deltay);\r\n"+
            "    this.selContext.stroke();\r\n"+
            "    return;\r\n"+
            "  }\r\n"+
            "      var dx=deltax-this.dragDeltaX;\r\n"+
            "      var dy=deltay-this.dragDeltaY;\r\n"+
            "      this.dragDeltaX=deltax;\r\n"+
            "      this.dragDeltaY=deltay;\r\n"+
            "      if(e.altKey){\r\n"+
            "        if(e.shiftKey){\r\n"+
            "          this.focalX+=dx;\r\n"+
            "          this.focalY+=dy;\r\n"+
            "        }else{\r\n"+
            "          this.centerXOffset+=dx;\r\n"+
            "          this.centerYOffset+=dy;\r\n"+
            "        }\r\n"+
            "        this.needsRepaint();\r\n"+
            "        this.repaintIfNeeded();\r\n"+
            "        //this.fireOnUserChangedPerspective();\r\n"+
            "        return;\r\n"+
            "      }\r\n"+
            "	  var q=new Quaternion();\r\n"+
            "	  if(dx==0 && dy==0)\r\n"+
            "	    return;\r\n"+
            "	  if(Math.abs(dx)>Math.abs(dy))dy=0;\r\n"+
            "	  else if(Math.abs(dy)>Math.abs(dx))dx=0;\r\n"+
            "	  var x=this.getRotationX();\r\n"+
            "	  var y=this.getRotationY();\r\n"+
            "	  var z=this.getRotationZ();\r\n"+
            "	  q.setFromEuler({x:x,y:y,z:z});\r\n"+
            "	  var qy=new Quaternion();\r\n"+
            "	  qy.setFromAxisAngle({x:0,y:1,z:0},-dx/90);\r\n"+
            "	  var qx=new Quaternion();\r\n"+
            "	  qx.setFromAxisAngle({x:1,y:0,z:0},-dy/90);\r\n"+
            "	  q.multiplySelf(qx);\r\n"+
            "	  q.multiplySelf(qy);\r\n"+
            "	  q.normalize();\r\n"+
            "	  var o={};\r\n"+
            "	  q.setToEuler(o);\r\n"+
            "	  this.setRotationX(o.x);\r\n"+
            "	  this.setRotationY(o.y);\r\n"+
            "	  this.setRotationZ(o.z);\r\n"+
            "      this.repaintIfNeeded();\r\n"+
            "	  this.fireOnUserChangedPerspective();\r\n"+
            "}\r\n"+
            "\r\n"+
            "function makeCanvasKeyEventable(o,target){\r\n"+
            "	if(target==null)\r\n"+
            "		target=o;\r\n"+
            "	o.tabIndex=1;\r\n"+
            "	o.style.outline=0;\r\n"+
            "    o.addEventListener('keydown',function(e){if(target.onkeydown) target.onkeydown(e);},false);\r\n"+
            "//    o.addEventListener('mouseover',function(e){o.focus();},false);\r\n"+
            "//    o.addEventListener('mouseout',function(e){o.blur();},false);\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "Surface.prototype.dumpPosition=function(){\r\n"+
            "	//log({rotx:this.getRotationX(),roty:this.getRotationY(),rotz:this.getRotationZ(),zoom:this.getZoom()})\r\n"+
            "}\r\n"+
            "\r\n"+
            "Surface.prototype.onkeydown=function(e){\r\n"+
            "	if(e.keyCode==38){\r\n"+
            "		this.setRotationX(this.getRotationX()+10);\r\n"+
            "	}else if(e.keyCode==40){\r\n"+
            "		this.setRotationX(this.getRotationX()-10);\r\n"+
            "	} else if(e.keyCode==37){\r\n"+
            "		this.setRotationY(this.getRotationY()+10);\r\n"+
            "	}else if(e.keyCode==39){\r\n"+
            "		this.setRotationY(this.getRotationY()-10);\r\n"+
            "	}\r\n"+
            "    this.repaintIfNeeded();\r\n"+
            "    this.fireOnUserChangedPerspective();\r\n"+
            "}\r\n"+
            "Surface.prototype.fireOnUserChangedPerspective=function(){\r\n"+
            "  if(this.onUserChangedPerspective)\r\n"+
            "    this.onUserChangedPerspective(this.getRotationX(),this.getRotationY(),this.getRotationZ(),this.zoom,this.fov);\r\n"+
            "}\r\n"+
            "\r\n"+
            "Surface.prototype.needsRepaint=function(){\r\n"+
            "  this.needsRepaintFlag=true;\r\n"+
            "}\r\n"+
            "\r\n"+
            "Surface.prototype.repaintIfNeeded=function(){\r\n"+
            "  if(!this.needsRepaintFlag)\r\n"+
            "    return;\r\n"+
            "  this.surfaceGL.repaint({texts:this.texts, polys:this.polys, lines:this.lines});\r\n"+
            "//  this.repaint();\r\n"+
            "  this.needsRepaintFlag=false;\r\n"+
            "}\r\n"+
            "Surface.prototype.repaint=function(){\r\n"+
            "  this.centerX=(this.canvas.width/2)+this.centerXOffset-this.focalX;\r\n"+
            "  this.centerY=(this.canvas.height/2)+this.centerYOffset-this.focalY;\r\n"+
            "  var width=this.canvas.width;\r\n"+
            "  var height=this.canvas.height;\r\n"+
            "  this.width=width;\r\n"+
            "  this.height=height;\r\n"+
            "  var zbuffer=[];\r\n"+
            "  var idbuffer=[];\r\n"+
            "  var imgbuffer=[];\r\n"+
            "  for(var i=0;i<height;i++){\r\n"+
            "	  zbuffer[i]=[];\r\n"+
            "	  idbuffer[i]=[];\r\n"+
            "	  imgbuffer[i]=[];\r\n"+
            "  }\r\n"+
            "  this.zbuffer=zbuffer;\r\n"+
            "  this.idbuffer=idbuffer;\r\n"+
            "  this.imgbuffer=imgbuffer;\r\n"+
            "	  \r\n"+
            "  \r\n"+
            "  this.context.clearRect(0,0,width,height);\r\n"+
            "  this.pixelBuf=this.context.createImageData(this.canvas.width,this.canvas.height);\r\n"+
            "  this.pixelBufData=this.pixelBuf.data;\r\n"+
            "  \r\n"+
            "    \r\n"+
            "  this.doPolys();\r\n"+
            "  this.doTexts();\r\n"+
            "  this.doLines();\r\n"+
            "  \r\n"+
            "  \r\n"+
            "  this.context.putImageData(this.pixelBuf,0,0);\r\n"+
            "}\r\n"+
            "               \r\n"+
            "Surface.prototype.doPolys=function(){\r\n"+
            "  if(this.polys){\r\n"+
            "    var polys=[];\r\n"+
            "    var j=0;\r\n"+
            "	var d1=Date.now();\r\n"+
            "	var rd=0,pd=0;\r\n"+
            "    for(var i=0;i<this.polys.length;i++){\r\n"+
            "      var p=this.polys[i].slice();\r\n"+
            "      //var p=new Float32Array(this.polys[i]);\r\n"+
            "      //rd-=Date.now();\r\n"+
            "      this.rotate(p,0);\r\n"+
            "      this.rotate(p,4);\r\n"+
            "      this.rotate(p,8);\r\n"+
            "     // rd+=Date.now();\r\n"+
            "      \r\n"+
            "      //pd-=Date.now();\r\n"+
            "      this.toPerspective(p,0);\r\n"+
            "      this.toPerspective(p,4);\r\n"+
            "      this.toPerspective(p,8);\r\n"+
            "      //pd+=Date.now();\r\n"+
            "      \r\n"+
            "      //if(diff(p[0],p[4]) >= .5  || diff(p[0],p[8]) >=.5  || diff(p[4],p[8]) >=.5  || diff(p[1],p[5]) >=.5  || diff(p[1],p[9]) >=.5  || diff(p[5],p[9]) >=.5 ){\r\n"+
            "    	  polys[j++]=p;\r\n"+
            "      //}\r\n"+
            "}\r\n"+
            "	var d2=Date.now();\r\n"+
            "    this.sortPolys(polys);	var d3=Date.now();\r\n"+
            "\r\n"+
            "    for(var i=0;i<polys.length;i++)\r\n"+
            "      prepTriangle(polys[i]);\r\n"+
            "	var d4=Date.now();\r\n"+
            "    for(var i=0;i<polys.length;i++){\r\n"+
            "      var p=polys[i];\r\n"+
            "      //if(diff(p[0],p[4]) >= 1  || diff(p[0],p[8]) >= 1  || diff(p[4],p[8]) >= 1  || diff(p[1],p[5]) >= 1  || diff(p[1],p[9]) >= 1  || diff(p[5],p[9]) >= 1 ){\r\n"+
            "        this.fillTriangle(p,i);\r\n"+
            "      //}else \r\n"+
            "    	  //this.setPixel(fl(p[0]),fl(p[1]),255,0,255,255);\r\n"+
            "    }\r\n"+
            "	var d5=Date.now();\r\n"+
            "	//log([d1,d2-d1,d3-d2,d4-d3,d5-d4,rd,pd]);\r\n"+
            "    this.currentPolys=polys;\r\n"+
            "  }\r\n"+
            "}\r\n"+
            "Surface.prototype.doTexts=function(){\r\n"+
            "  if(this.texts){\r\n"+
            "      var texts=this.texts.slice();//clone\r\n"+
            "      for(var i in texts){\r\n"+
            "    	  var p=texts[i].slice();\r\n"+
            "		  this.rotate(p,0);\r\n"+
            "    	  this.toPerspective(p,0);\r\n"+
            "    	  texts[i]=p;\r\n"+
            "      }\r\n"+
            "	  for(var i in texts)\r\n"+
            "        this.drawText(texts[i],10000+i);\r\n"+
            "      this.currentTexts=texts;\r\n"+
            "  }\r\n"+
            "}\r\n"+
            "Surface.prototype.doLines=function(){\r\n"+
            "	var d1=Date.now();\r\n"+
            "  if(this.lines){      var lines=this.lines.slice();      for(var i in lines){    	  var p=lines[i].slice();		  this.rotate(p,0);		  this.rotate(p,3);    	  this.toPerspective(p,0);    	  this.toPerspective(p,3);    	  lines[i]=p;      }	  for(var i in lines)        this.drawLine(lines[i],10000+i);      this.currentLines=lines;  }	var d2=Date.now();\r\n"+
            "//log(d2-d1);\r\n"+
            "}\r\n"+
            "Surface.prototype.setData=function(data){\r\n"+
            "  this.polys=data.polys ? data.polys : [];\r\n"+
            "  this.texts=data.texts ? data.texts : [];\r\n"+
            "  this.lines=data.lines ? data.lines : [];\r\n"+
            "  this.surfaceGL.u");
          out.print(
            "pdateData(data)\r\n"+
            "  this.needsRepaint();\r\n"+
            "}\r\n"+
            "Surface.prototype.setOptions=function(o){\r\n"+
            "  if(o.minXRot!=null) this.minXRot=o.minXRot*1;// else this.minXRot=null;\r\n"+
            "  if(o.minYRot!=null) this.minYRot=o.minYRot*1;// else this.minYRot=null;\r\n"+
            "  if(o.minZRot!=null) this.minZRot=o.minZRot*1;// else this.minZRot=null;\r\n"+
            "  if(o.maxXRot!=null) this.maxXRot=o.maxXRot*1;// else this.maxXRot=null;\r\n"+
            "  if(o.maxYRot!=null) this.maxYRot=o.maxYRot*1;// else this.maxYRot=null;\r\n"+
            "  if(o.maxZRot!=null) this.maxZRot=o.maxZRot*1;// else this.maxZRot=null;\r\n"+
            "  if(o.rotX!=null) this.setRotationX(o.rotX*1);\r\n"+
            "  if(o.rotY!=null) this.setRotationY(o.rotY*1);\r\n"+
            "  if(o.rotZ!=null) this.setRotationZ(o.rotZ*1);\r\n"+
            "  if(o.zoom!=null) this.setZoom(o.zoom*1);\r\n"+
            "  if(o.background!=null) {this.element.style.background=o.background;};\r\n"+
            "  if(o.backgroundClass!=null) {this.element.className=o.backgroundClass;};\r\n"+
            "  if(o.centerY!=null) this.centerYOffset=o.centerY*1;// else this.centerYOffset=0;\r\n"+
            "  if(o.centerX!=null) this.centerXOffset=o.centerX*1;// else this.centerXOffset=0;\r\n"+
            "  if(o.focalY!=null) this.focalY=o.focalY*1;// else this.centerYOffset=0;\r\n"+
            "  if(o.focalX!=null) this.focalX=o.focalX*1;// else this.centerXOffset=0;\r\n"+
            "  if(o.fov!=null) this.fov=o.fov*1;\r\n"+
            "  \r\n"+
            "  //SurfaceGL setOptions\r\n"+
            "  if(o.background !=null)this.surfaceGL.setBackground(o.background);\r\n"+
            "  if(o.fov != null) this.surfaceGL.setFov(o.fov);\r\n"+
            "  if(o.focalX != null) this.surfaceGL.setFocalX(o.focalX);\r\n"+
            "  if(o.focalY != null) this.surfaceGL.setFocalY(o.focalY);\r\n"+
            "  if(o.centerX != null) this.surfaceGL.centerX = o.centerX;\r\n"+
            "  if(o.centerY != null) this.surfaceGL.centerY = o.centerY;\r\n"+
            "  \r\n"+
            "  var start=(new Date().getTime());\r\n"+
            "  this.repaintIfNeeded();\r\n"+
            "  var end=(new Date().getTime());\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "\r\n"+
            "Surface.prototype.toPerspective=function(polys,offset){\r\n"+
            "  var t=this.zoom*this.fov/(this.fov+polys[offset+2]);\r\n"+
            "  polys[offset]=(polys[offset]+this.focalX)*t+this.centerX;\r\n"+
            "  polys[offset+1]=(polys[offset+1]+this.focalY)*t+this.centerY;\r\n"+
            "}\r\n"+
            "\r\n"+
            "Surface.prototype.setSize=function(width,height){\r\n"+
            "	this.width = width;\r\n"+
            "	this.height = height;\r\n"+
            "	this.surfaceGL.setSize(width,height);\r\n"+
            "	this.canvas.width=width;\r\n"+
            "	this.canvas.height=height;\r\n"+
            "	this.selCanvas.width=width;\r\n"+
            "	this.selCanvas.height=height;\r\n"+
            "	this.needsRepaint();\r\n"+
            "	this.repaintIfNeeded();\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "Surface.prototype.drawText=function(p,pid){	var x=fl(p[0]);	var y=fl(p[1]);	var z=fl(p[2]);	var text=p[3];	var align=p[4];	var color=p[5];	var w=Math.min(this.textbufContext.measureText(text).width,this.textbuf.width);\r\n"+
            "	if(w<1)\r\n"+
            "	  return;	var h=this.textbuf.height;\r\n"+
            "	if(this.textbufContext.lastColor!=color){	  this.textbufContext.lastColor=color;	  this.textbufContext.fillStyle='rgb('+getRed(color)+','+getGrn(color)+','+getBlu(color)+')';\r\n"+
            "	}\r\n"+
            "    var t=this.zoom*this.fov/(this.fov+z);\r\n"+
            "    if(t<3)\r\n"+
            "    	return;\r\n"+
            "    this.textbufContext.font=min(fl(t*2),15)+\"px Arial\";\r\n"+
            "	this.textbufContext.clearRect(0,0,w,h);	this.textbufContext.fillText(text, 0, 20);	var data=this.textbufContext.getImageData(0,0,w,h).data;\r\n"+
            "	y-=30;	if(align==1)		x-=w;\r\n"+
            "    else if(align==0)		x-=rd(w/2);\r\n"+
            "\r\n"+
            "	for(var y2=0;y2<h;y2++){\r\n"+
            "	  var zbuf=this.zbuffer[y+y2];\r\n"+
            "	  if(zbuf==null)\r\n"+
            "		  continue;	  for(var x2=0;x2<w;x2++){		  var bufoffset=(x2+y2*w)*4;		  var bufloc=(x+x2);		  if(data[bufoffset+3]<32)			  continue;\r\n"+
            "		  if(zbuf[bufloc]==null || z<zbuf[bufloc]){		    this.setPixel(x+x2,y+y2,data[bufoffset+0],data[bufoffset+1],data[bufoffset+2],data[bufoffset+3]);		     zbuf[bufloc]=z;		  }	  }	}}\r\n"+
            "\r\n"+
            "Surface.prototype.drawLine=function(p,pid){\r\n"+
            "	var x1=fl(p[0]);\r\n"+
            "	var y1=fl(p[1]);\r\n"+
            "	var z1=fl(p[2]);\r\n"+
            "	var x2=fl(p[3]);\r\n"+
            "	var y2=fl(p[4]);\r\n"+
            "	var z2=fl(p[5]);\r\n"+
            "	var color=p[6] || 0;\r\n"+
            "	var width=p[7] || 1;\r\n"+
            "	var r=getRed(color);\r\n"+
            "	var g=getGrn(color);\r\n"+
            "	var b=getBlu(color);\r\n"+
            "	var xd=x2-x1;\r\n"+
            "	var yd=y2-y1;\r\n"+
            "	var zd=z2-z1;\r\n"+
            "	var this_zbuffer=this.zbuffer;\r\n"+
            "	var this_idbuffer=this.idbuffer;\r\n"+
            "	var this_height=this.height;\r\n"+
            "	var this_width=this.width;\r\n"+
            "	if(xd==0 && yd==0){\r\n"+
            "		   if(width>1){\r\n"+
            "			  var lf=x1-fl(width/2);\r\n"+
            "			  var rt=lf+width;\r\n"+
            "			  var tp=y1-fl(width/2);\r\n"+
            "			  var bt=tp+width;\r\n"+
            "			  for(var x=lf;x<rt;x++){\r\n"+
            "			    for(var y=tp;y<bt;y++)\r\n"+
            "		           if(z1<this.getZ(x,y)){\r\n"+
            "		        	 var zbuf=this_zbuffer[y];\r\n"+
            "		        	 if(zbuf==null)\r\n"+
            "		        		 continue;\r\n"+
            "			         zbuf[x]=z1;\r\n"+
            "			         this.setPixel(x,y,r,g,b,255);\r\n"+
            "		           }\r\n"+
            "			  }\r\n"+
            "			}else{\r\n"+
            "		      if(z1<this.getZ(x1,y1)){\r\n"+
            "		    	var zbuf=this_zbuffer[y1];\r\n"+
            "		    	if(zbuf){\r\n"+
            "			      zbuf[x1]=z1;\r\n"+
            "			      this.setPixel(x1,y1,r,g,b,255);\r\n"+
            "		    	}\r\n"+
            "		      }\r\n"+
            "			}\r\n"+
            "		return;\r\n"+
            "	}\r\n"+
            "	if(z1>this.getZ(x1,y1) && z2>this.getZ(x2,y2) && this.getPolygonId(x1,y1)==this.getPolygonId(x2,y2))\r\n"+
            "		return;\r\n"+
            "	if(Math.abs(xd)>Math.abs(yd)){\r\n"+
            "		var min,max;\r\n"+
            "		if(x1<x2){\r\n"+
            "			min=x1;\r\n"+
            "			max=x2;\r\n"+
            "		}else{\r\n"+
            "			min=x2;\r\n"+
            "			max=x1;\r\n"+
            "		}\r\n"+
            "		var slope = yd/ xd;\r\n"+
            "		var zslope = zd/ xd;\r\n"+
            "		if(min<0)min=0;\r\n"+
            "		if(max>=this_width)max=this_width-1;\r\n"+
            "		if(width>1){\r\n"+
            "		    var woff=fl(width/2);\r\n"+
            "		    var w2=Math.sqrt(width*width/(1+slope*slope));\r\n"+
            "		    for(var i=0;i<=w2;i++){\r\n"+
            "			  var w=i-woff;\r\n"+
            "		      for(var x=min;x<=max;x++){\r\n"+
            "			    var y=fl(y1+(x-x1)*slope);\r\n"+
            "			    var z=z1+(x-x1)*zslope;\r\n"+
            "			    var x2=fl(x-w*slope);\r\n"+
            "			    var y2=y+w;\r\n"+
            "			    if(y2>=this_height || y2<0) continue;\r\n"+
            "			    if(this_zbuffer[y2][x2]==null || z<this_zbuffer[y2][x2]){\r\n"+
            "				  this_zbuffer[y2][x2]=z;\r\n"+
            "				  this_idbuffer[y2][x2]=pid;\r\n"+
            "			      this.setPixel(x2,y2,r,g,b,255);\r\n"+
            "			    }\r\n"+
            "			    y2++;\r\n"+
            "			    if(y2>=this_height) continue;\r\n"+
            "			    if(this_zbuffer[y2][x2]==null || z<this_zbuffer[y2][x2]){\r\n"+
            "				  this_zbuffer[y2][x2]=z;\r\n"+
            "				  this_idbuffer[y2][x2]=pid;\r\n"+
            "			      this.setPixel(x2,y2,r,g,b,255);\r\n"+
            "			    }\r\n"+
            "		      }\r\n"+
            "		    }\r\n"+
            "		}else{\r\n"+
            "		  for(var x=min;x<=max;x++){\r\n"+
            "			var y=fl(y1+(x-x1)*slope);\r\n"+
            "			var z=z1+(x-x1)*zslope;\r\n"+
            "			  if(y<0 || y>=this_height) continue;\r\n"+
            "			if(this_zbuffer[y][x]==null || z<this_zbuffer[y][x]){\r\n"+
            "				this_zbuffer[y][x]=z;\r\n"+
            "				this_idbuffer[y][x]=pid;\r\n"+
            "			    this.setPixel(x,y,r,g,b,255);\r\n"+
            "			}\r\n"+
            "		  }\r\n"+
            "		}\r\n"+
            "	}else{\r\n"+
            "		var min,max;\r\n"+
            "		if(y1<y2){\r\n"+
            "			min=y1;\r\n"+
            "			max=y2;\r\n"+
            "		}else{\r\n"+
            "			min=y2;\r\n"+
            "			max=y1;\r\n"+
            "		}\r\n"+
            "		if(min<0)min=0;\r\n"+
            "		if(max>=this_height)max=this_height-1;\r\n"+
            "		var slope = xd/ yd;\r\n"+
            "		var zslope = zd/ yd;\r\n"+
            "		if(width>1){\r\n"+
            "		  var woff=fl(width/2);\r\n"+
            "		  var w2=Math.sqrt(width*width/(1+slope*slope));\r\n"+
            "		  for(var i=0;i<=w2;i++){\r\n"+
            "			var w=i-woff;\r\n"+
            "		    for(var y=min;y<=max;y++){  \r\n"+
            "			  var x=fl(x1+(y-y1)*slope);\r\n"+
            "			  var z=z1+(y-y1)*zslope;\r\n"+
            "			  var x2=x+w;\r\n"+
            "			  var y2=fl(y-w*slope);\r\n"+
            "			  if(x2>=this_width || x2<0) continue;\r\n"+
            "			  if(y2>=this_height || y2<0) continue;\r\n"+
            "			  if(this_zbuffer[y2][x2]==null || z<this_zbuffer[y2][x2]){\r\n"+
            "				this_zbuffer[y2][x2]=z;\r\n"+
            "				this_idbuffer[y2][x2]=pid;\r\n"+
            "			    this.setPixel(x2,y2,r,g,b,255);\r\n"+
            "			  }\r\n"+
            "			  x2++;\r\n"+
            "			  if(x2>=this_width) continue;\r\n"+
            "			  if(this_zbuffer[y2][x2]==null || z<this_zbuffer[y2][x2]){\r\n"+
            "				this_zbuffer[y2][x2]=z;\r\n"+
            "				this_idbuffer[y2][x2]=pid;\r\n"+
            "			    this.setPixel(x2,y2,r,g,b,255);\r\n"+
            "			  }\r\n"+
            "		    }\r\n"+
            "		  }\r\n"+
            "		} else{\r\n"+
            "		  for(var y=min;y<=max;y++){\r\n"+
            "			var x=fl(x1+(y-y1)*slope);\r\n"+
            "			var z=z1+(y-y1)*zslope;\r\n"+
            "			if(this_zbuffer[y][x]==null || z<this_zbuffer[y][x]){\r\n"+
            "				this_zbuffer[y][x]=z;\r\n"+
            "				this_idbuffer[y][x]=pid;\r\n"+
            "			    this.setPixel(x,y,r,g,b,255);\r\n"+
            "			}\r\n"+
            "		  }\r\n"+
            "		}\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "               \r\n"+
            "Surface.prototype.sortPolys=function(polys){\r\n"+
            "	//polys.sort(function(a,b){return Math.max(a[2],a[6],a[10]) - Math.max(b[2],b[6],b[10]); });	polys.sort(function(a,b){return max3(a[2],a[6],a[10],b[2],b[6],b[10]); });\r\n"+
            "}\r\n"+
            "\r\n"+
            "function max3(a,b,c,d,e,f){\r\n"+
            "	var m1=a>b ? (a > c ? a : c) : (b > c ? b : c);\r\n"+
            "	if(m1<d || m1<e || m1<f)\r\n"+
            "		return 1;\r\n"+
            "	else if(m1==d && m1==e && m1==f) \r\n"+
            "		return 0;\r\n"+
            "	else\r\n"+
            "		return -1;\r\n"+
            "		\r\n"+
            "		\r\n"+
            "}\r\n"+
            "Surface.prototype.fillTrianglePart=function(\r\n"+
            "		pid,top,bottom,\r\n"+
            "		topLeft,topRight,bottomLeft,bottomRight,\r\n"+
            "		topLeftZ,topRightZ,bottomLeftZ,bottomRightZ,\r\n"+
            "		topLeftCR,topRightCR,bottomLeftCR,bottomRightCR,\r\n"+
            "		topLeftCG,topRightCG,bottomLeftCG,bottomRightCG,\r\n"+
            "		topLeftCB,topRightCB,bottomLeftCB,bottomRightCB\r\n"+
            "		){\r\n"+
            "	if(bottomLeft>bottomRight || topLeft>topRight){\r\n"+
            "		var t=bottomLeft;\r\n"+
            "		bottomLeft=bottomRight;\r\n"+
            "		bottomRight=t;\r\n"+
            "		t=topLeft;\r\n"+
            "		topLeft=topRight;\r\n"+
            "		topRight=t;\r\n"+
            "		\r\n"+
            "		t=bottomLeftZ;\r\n"+
            "		bottomLeftZ=bottomRightZ;\r\n"+
            "		bottomRightZ=t;\r\n"+
            "		t=topLeftZ;\r\n"+
            "		topLeftZ=topRightZ;\r\n"+
            "		topRightZ=t;\r\n"+
            "		\r\n"+
            "		t=bottomLeftCR;\r\n"+
            "		bottomLeftCR=bottomRightCR;\r\n"+
            "		bottomRightCR=t;\r\n"+
            "		t=topLeftCR;\r\n"+
            "		topLeftCR=topRightCR;\r\n"+
            "		topRightCR=t;\r\n"+
            "		\r\n"+
            "		t=bottomLeftCG;\r\n"+
            "		bottomLeftCG=bottomRightCG;\r\n"+
            "		bottomRightCG=t;\r\n"+
            "		t=topLeftCG;\r\n"+
            "		topLeftCG=topRightCG;\r\n"+
            "		topRightCG=t;\r\n"+
            "		\r\n"+
            "		t=bottomLeftCB;\r\n"+
            "		bottomLeftCB=bottomRightCB;\r\n"+
            "		bottomRightCB=t;\r\n"+
            "		t=topLeftCB;\r\n"+
            "		topLeftCB=topRightCB;\r\n"+
            "		topRightCB=t;\r\n"+
            "	}\r\n"+
            "	\r\n"+
            "	//bottom+=1;\r\n"+
            "	//top-=1;\r\n"+
            "	//bottomLeft-=1;\r\n"+
            "	//bottomRight+=1;\r\n"+
            "	//topLeft-=1;\r\n"+
            "	//topRight+=1;\r\n"+
            "	var this_zbuffer=this.zbuffer;\r\n"+
            "	var this_idbuffer=this.idbuffer;\r\n"+
            "	var this_imgbuffer=this.imgbuffer;\r\n"+
            "	var this_width=this.width;\r\n"+
            "	var this_height=this.height;\r\n"+
            "    var this_pixelBufData=this.pixelBufData;\r\n"+
            "	\r\n"+
            "	var vdiff=bottom-top;\r\n"+
            "	var slope1=(topLeft-bottomLeft)/vdiff;\r\n"+
            "	var slope2=(topRight-bottomRight)/vdiff;\r\n"+
            "	var slopeZ1=(topLeftZ-bottomLeftZ)/vdiff;\r\n"+
            "	var slopeZ2=(topRightZ-bottomRightZ)/vdiff;\r\n"+
            "	var slopeCR1=((topLeftCR)-(bottomLeftCR))/vdiff;\r\n"+
            "	var slopeCR2=((topRightCR)-(bottomRightCR))/vdiff;\r\n"+
            "	var slopeCG1=((topLeftCG)-(bottomLeftCG))/vdiff;\r\n"+
            "	var slopeCG2=((topRightCG)-(bottomRightCG))/vdiff;\r\n"+
            "	var slopeCB1=((topLeftCB)-(bottomLeftCB))/vdiff;\r\n"+
            "	var slopeCB2=((topRightCB)-(bottomRightCB))/vdiff;\r\n"+
            "	if(bottom>=this_height)\r\n"+
            "		bottom=this_height-1;\r\n"+
            "	var topi=Math.max(top,0);\r\n"+
            "	\r\n"+
            "	for(var i=topi;i<=bottom;i++){\r\n"+
            "		var voffset=top-i;\r\n"+
            "		var y=fl(i);\r\n"+
            "		var x1=voffset*slope1+topLeft;\r\n"+
            "		var x2=voffset*slope2+topRight;\r\n"+
            "		var xdiff=x2-x1;\r\n"+
            "		var left=fl");
          out.print(
            "(x1);\r\n"+
            "		var right=fl(x2);\r\n"+
            "		var z1=voffset*slopeZ1+topLeftZ;\r\n"+
            "		var z2=voffset*slopeZ2+topRightZ;\r\n"+
            "		var zbuf=this_zbuffer[y];\r\n"+
            "		var idbuf=this_idbuffer[y];\r\n"+
            "		if(zbuf[left]<z1 && zbuf[right]<z2 && idbuf[left] == idbuf[right])\r\n"+
            "			continue;\r\n"+
            "		var imgbuf=this_imgbuffer[y];\r\n"+
            "			\r\n"+
            "		var zslope=x1==x2 ? 0 : (z2-z1)/(xdiff);\r\n"+
            "		\r\n"+
            "		var cr1=voffset*slopeCR1+(topLeftCR);\r\n"+
            "		var cr2=voffset*slopeCR2+(topRightCR);\r\n"+
            "		var crslope=x1==x2 ? 0 : (cr2-cr1)/(xdiff);\r\n"+
            "		\r\n"+
            "		var cg1=voffset*slopeCG1+(topLeftCG);\r\n"+
            "		var cg2=voffset*slopeCG2+(topRightCG);\r\n"+
            "		var cgslope=x1==x2 ? 0 : (cg2-cg1)/(xdiff);\r\n"+
            "		\r\n"+
            "		var cb1=voffset*slopeCB1+(topLeftCB);\r\n"+
            "		var cb2=voffset*slopeCB2+(topRightCB);\r\n"+
            "		var cbslope=x1==x2 ? 0 : (cb2-cb1)/(xdiff);\r\n"+
            "		\r\n"+
            "	    var bufOffset=y*this_width;\r\n"+
            "		var leftLine=x1+Math.abs(slope1);\r\n"+
            "		var rightLine=x2-Math.abs(slope2);\r\n"+
            "		\r\n"+
            "		var leftClipped=left<0 ? 0 : left;\r\n"+
            "		var rightClipped=right>=this_width ? this_width-1 : right;\r\n"+
            "	    for(x=leftClipped;x<=rightClipped;x++){\r\n"+
            "	    	var existingZ=zbuf[x];\r\n"+
            "	    	var xoffset=x-left;\r\n"+
            "	    	var z=z1+zslope*(xoffset);\r\n"+
            "	    	if(existingZ==null || existingZ>z){\r\n"+
            "	    	  zbuf[x]=z;\r\n"+
            "	    	  idbuf[x]=pid;\r\n"+
            "	    	  var r=cr1+crslope*xoffset;\r\n"+
            "	    	  var g=cg1+cgslope*xoffset;\r\n"+
            "	    	  var b=cb1+cbslope*xoffset;\r\n"+
            "	    	  var border=x<=leftLine || x>=rightLine;\r\n"+
            "	    	  if(border){ r-=10; g-=10; b-=10; }\r\n"+
            "              var idx = (x + bufOffset) * 4;\r\n"+
            "              r= (r < 1 ? 0 : r > 254 ? 255 : fl(r));\r\n"+
            "              g= (g < 1 ? 0 : g > 254 ? 255 : fl(g));\r\n"+
            "              b= (b < 1 ? 0 : b > 254 ? 255 : fl(b));\r\n"+
            "              //imgbuf[x]=(r)|(g<<8)|(b<<16);\r\n"+
            "              this_pixelBufData[idx] = (r < 1 ? 0 : r > 254 ? 255 : fl(r));\r\n"+
            "              this_pixelBufData[idx+1] = (g < 1 ? 0 : g > 254 ? 255 : fl(g));\r\n"+
            "              this_pixelBufData[idx+2] = (b < 1 ? 0 : b > 254 ? 255 : fl(b));\r\n"+
            "              this_pixelBufData[idx+3] = 255;\r\n"+
            "	    	}\r\n"+
            "	    }\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "Surface.prototype.getPolygonId=function(x, y) {\r\n"+
            "	if(x<0 || y<0 || x>this.width || y>this.height)\r\n"+
            "		return null;\r\n"+
            "	return this.idbuffer[fl(y)][fl(x)];\r\n"+
            "}\r\n"+
            "Surface.prototype.getZ=function(x, y) {\r\n"+
            "	if(x<0 || y<0 || x>=this.width || y>=this.height)\r\n"+
            "		return 1000000;\r\n"+
            "	return this.zbuffer[fl(y)][fl(x)] || 1000000;\r\n"+
            "}\r\n"+
            "\r\n"+
            "Surface.prototype.setPixel=function(x, y, r, g, b, a) {\r\n"+
            "    if(x<0 || x>=this.width || y<0 || y>=this.height)\r\n"+
            "      return;\r\n"+
            "    var idx = (x + y * this.width) * 4;\r\n"+
            "    var buf=this.pixelBufData;\r\n"+
            "    buf[idx+0] = (r < 1 ? 0 : r > 254 ? 255 : fl(r));\r\n"+
            "    buf[idx+1] = (g < 1 ? 0 : g > 254 ? 255 : fl(g));\r\n"+
            "    buf[idx+2] = (b < 1 ? 0 : b > 254 ? 255 : fl(b));\r\n"+
            "    buf[idx+3] = (a);\r\n"+
            "}\r\n"+
            "               \r\n"+
            "\r\n"+
            "\r\n"+
            "function swap(p,i,j){\r\n"+
            "	var t=p[i]; \r\n"+
            "	p[i]=p[j]; \r\n"+
            "	p[j]=t;\r\n"+
            "	\r\n"+
            "	t=p[i+1]; \r\n"+
            "	p[i+1]=p[j+1]; \r\n"+
            "	p[j+1]=t;\r\n"+
            "	\r\n"+
            "	t=p[i+2]; \r\n"+
            "	p[i+2]=p[j+2]; \r\n"+
            "	p[j+2]=t;\r\n"+
            "	\r\n"+
            "	t=p[i+3]; \r\n"+
            "	p[i+3]=p[j+3]; \r\n"+
            "	p[j+3]=t;\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "\r\n"+
            "// 0=x, 1=y, 2=z, 3=c,     4=x, 5=y, 6=z, 7=c,    8=x, 9=y, 10=z, 11=c\r\n"+
            "function prepTriangle(p){\r\n"+
            "	var a = p[0];\r\n"+
            "	var b = p[1];\r\n"+
            "	var c = p[2];\r\n"+
            "	var d = p[3];\r\n"+
            "	\r\n"+
            "	var e = p[4];\r\n"+
            "	var f = p[5];\r\n"+
            "	var g = p[6];\r\n"+
            "	var h = p[7];\r\n"+
            "	\r\n"+
            "	var i = p[8];\r\n"+
            "	var j = p[9];\r\n"+
            "	var k = p[10];\r\n"+
            "	var l = p[11];\r\n"+
            "	\r\n"+
            "	if(b>f){\r\n"+
            "		if(b>j){\r\n"+
            "			if(f>j){//3,2,1:  1=3,3=1\r\n"+
            "//				this.swap(p,0,8);\r\n"+
            "				p[0]=i;p[1]=j;p[2]=k;p[3]=l;\r\n"+
            "				p[8]=a;p[9]=b;p[10]=c;p[11]=d;\r\n"+
            "			}else{//2,3,1: 1=2,2=3,3=1\r\n"+
            "//				this.swap(p,0,4);\r\n"+
            "//				this.swap(p,8,4);\r\n"+
            "				p[0]=e;p[1]=f;p[2]=g;p[3]=h;\r\n"+
            "				p[4]=i;p[5]=j;p[6]=k;p[7]=l;\r\n"+
            "				p[8]=a;p[9]=b;p[10]=c;p[11]=d;\r\n"+
            "			}\r\n"+
            "		}else{//2,1,3: 1=2,2=1\r\n"+
            "//			this.swap(p,0,4);\r\n"+
            "			p[0]=e;p[1]=f;p[2]=g;p[3]=h;\r\n"+
            "			p[4]=a;p[5]=b;p[6]=c;p[7]=d;\r\n"+
            "		}\r\n"+
            "	}else if(f > j){\r\n"+
            "		if(b>j){//2,3,1: 1=3,3=2,2=1\r\n"+
            "//			swap(p,0,8);\r\n"+
            "//			swap(p,8,4);\r\n"+
            "			p[0]=i;p[1]=j;p[2]=k;p[3]=l;\r\n"+
            "			p[4]=a;p[5]=b;p[6]=c;p[7]=d;\r\n"+
            "			p[8]=e;p[9]=f;p[10]=g;p[11]=h;\r\n"+
            "		}else{//1,3,2: 2=3,3=2\r\n"+
            "//			this.swap(p,4,8);\r\n"+
            "			p[4]=i;p[5]=j;p[6]=k;p[7]=l;\r\n"+
            "			p[8]=e;p[9]=f;p[10]=g;p[11]=h;\r\n"+
            "		}\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "Surface.prototype.fillTriangle=function(p,pid){\r\n"+
            "	 var id=this.getPolygonId(p[0],p[1]);	 if(id!=null && id==this.getPolygonId(p[4],p[5]) && id==this.getPolygonId(p[8],p[9])){//	   var maxz=Math.max(p[2],p[6],p[10]);\r\n"+
            "	   var maxz = p[2] > p[6] ? (p[2] > p[10] ? p[2] : p[10]) : (p[6] > p[10] ? p[6] : p[10]);	   if(maxz>this.getZ(p[0],p[1]) && maxz>this.getZ(p[4],p[5]) && maxz>this.getZ(p[8],p[9])){\r\n"+
            "	  	 return;	   }	 }	var yd1=p[5]-p[1];\r\n"+
            "	var yd2=p[9]-p[1];\r\n"+
            "	var d=yd1/yd2;\r\n"+
            "	var x2=p[0]+(p[8]-p[0])*d;\r\n"+
            "	var z2=p[2]+(p[10]-p[2])*d;\r\n"+
            "	var cr2=getRed(p[3])+(getRed(p[11])-getRed(p[3]))*d;\r\n"+
            "	var cg2=getGrn(p[3])+(getGrn(p[11])-getGrn(p[3]))*d;\r\n"+
            "	var cb2=getBlu(p[3])+(getBlu(p[11])-getBlu(p[3]))*d;\r\n"+
            "	\r\n"+
            "	this.fillTrianglePart(pid,  p[1],p[5] ,p[0],p[0],p[4],x2  ,p[2],p[2],p[6],z2    ,getRed(p[3]),getRed(p[3]),getRed(p[7]),cr2,   getGrn(p[3]),getGrn(p[3]),getGrn(p[7]),cg2,   getBlu(p[3]),getBlu(p[3]),getBlu(p[7]),cb2   );\r\n"+
            "	this.fillTrianglePart(pid, p[5],p[9] ,p[4],x2,p[8],p[8]  ,p[6],z2,p[10],p[10]  ,getRed(p[7]),cr2,getRed(p[11]),getRed(p[11]), getGrn(p[7]),cg2,getGrn(p[11]),getGrn(p[11]), getBlu(p[7]),cb2,getBlu(p[11]),getBlu(p[11]) );\r\n"+
            "}\r\n"+
            "\r\n"+
            "function getRed(c1){return (c1 & 0xff0000)>>16;}\r\n"+
            "function getGrn(c1){return (c1 & 0x00ff00)>>8;}\r\n"+
            "function getBlu(c1){return (c1 & 0x0000ff);}\r\n"+
            "\r\n"+
            "\r\n"+
            "Surface.prototype.rotate=function(data,offset){\r\n"+
            "  var x=data[offset];\r\n"+
            "  var y=data[offset+1];\r\n"+
            "  var z=data[offset+2];\r\n"+
            "  var t;\r\n"+
            "  if(this.yRot){\r\n"+
            "    t=z;\r\n"+
            "    z=z*(this.yRotCos)-x*(this.yRotSin);\r\n"+
            "    x=t*(this.yRotSin)+x*(this.yRotCos);\r\n"+
            "  }\r\n"+
            "  if(this.zRot){\r\n"+
            "    t=x;\r\n"+
            "    x=x*(this.zRotCos)-y*(this.zRotSin);\r\n"+
            "    y=t*(this.zRotSin)+y*(this.zRotCos);\r\n"+
            "  }\r\n"+
            "  if(this.xRot){\r\n"+
            "    t=z;\r\n"+
            "    z=z*(this.xRotCos)-y*(this.xRotSin);\r\n"+
            "    y=t*(this.xRotSin)+y*(this.xRotCos);\r\n"+
            "    }\r\n"+
            "  data[offset]=x;\r\n"+
            "  data[offset+1]=y;\r\n"+
            "  data[offset+2]=z;\r\n"+
            "}\r\n"+
            "\r\n"+
            "function rotate(data,offset,xRot,yRot,zRot){\r\n"+
            "  var rotScale=Math.PI/180;\r\n"+
            "  xRot=xRot%360;\r\n"+
            "  xRot=xRot*rotScale;\r\n"+
            "  var xRotCos=Math.cos(xRot);\r\n"+
            "  var xRotSin=Math.sin(xRot);\r\n"+
            "  yRot=yRot%360;\r\n"+
            "  yRot=yRot*rotScale;\r\n"+
            "  var yRotCos=Math.cos(yRot);\r\n"+
            "  var yRotSin=Math.sin(yRot);\r\n"+
            "  zRot=zRot%360;\r\n"+
            "  zRot=zRot*rotScale;\r\n"+
            "  var zRotCos=Math.cos(zRot);\r\n"+
            "  var zRotSin=Math.sin(zRot);\r\n"+
            "  \r\n"+
            "  var x=data[offset];\r\n"+
            "  var y=data[offset+1];\r\n"+
            "  var z=data[offset+2];\r\n"+
            "  var t;\r\n"+
            "  if(yRot){\r\n"+
            "    t=z;\r\n"+
            "    z=z*(yRotCos)-x*(yRotSin);\r\n"+
            "    x=t*(yRotSin)+x*(yRotCos);\r\n"+
            "  }\r\n"+
            "  if(zRot){\r\n"+
            "    t=x;\r\n"+
            "    x=x*(zRotCos)-y*(zRotSin);\r\n"+
            "    y=t*(zRotSin)+y*(zRotCos);\r\n"+
            "  }\r\n"+
            "  if(xRot){\r\n"+
            "    t=z;\r\n"+
            "    z=z*(xRotCos)-y*(xRotSin);\r\n"+
            "    y=t*(xRotSin)+y*(xRotCos);\r\n"+
            "  }\r\n"+
            "  data[offset]=x;\r\n"+
            "  data[offset+1]=y;\r\n"+
            "  data[offset+2]=z;\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "Surface.prototype.getRotationX=function(){\r\n"+
            "  return this.xRot / this.rotScale;\r\n"+
            "}\r\n"+
            "Surface.prototype.getRotationY=function(){\r\n"+
            "  return this.yRot / this.rotScale;\r\n"+
            "}\r\n"+
            "Surface.prototype.getRotationZ=function(){\r\n"+
            "  return this.zRot / this.rotScale;\r\n"+
            "}\r\n"+
            "\r\n"+
            "Surface.prototype.setRotationZ=function(zRot){\r\n"+
            "  zRot=between(zRot%360,this.minZRot,this.maxZRot);\r\n"+
            "  this.surfaceGL.zRot = zRot;\r\n"+
            "  this.zRot=zRot * this.rotScale;\r\n"+
            "  \r\n"+
            "  this.zRotCos=Math.cos(this.zRot);\r\n"+
            "  this.zRotSin=Math.sin(this.zRot);\r\n"+
            "  this.needsRepaint();\r\n"+
            "}\r\n"+
            "Surface.prototype.setRotationY=function(yRot){\r\n"+
            "  yRot=between(yRot%360,this.minYRot,this.maxYRot);\r\n"+
            "  this.surfaceGL.yRot = yRot;\r\n"+
            "  this.yRot=yRot * this.rotScale;\r\n"+
            "  this.yRotCos=Math.cos(this.yRot);\r\n"+
            "  this.yRotSin=Math.sin(this.yRot);\r\n"+
            "  this.needsRepaint();\r\n"+
            "}\r\n"+
            "\r\n"+
            "function between(i,min,max){\r\n"+
            "	if(min!=null) i=Math.max(i,min);\r\n"+
            "	if(max!=null) i=Math.min(i,max);\r\n"+
            "	return i;\r\n"+
            "}\r\n"+
            "Surface.prototype.setRotationX=function(xRot){\r\n"+
            "  xRot=between(xRot%360,this.minXRot,this.maxXRot);\r\n"+
            "  this.surfaceGL.xRot = xRot;\r\n"+
            "  this.xRot=xRot * this.rotScale;\r\n"+
            "  this.xRotCos=Math.cos(this.xRot);\r\n"+
            "  this.xRotSin=Math.sin(this.xRot);\r\n"+
            "  this.needsRepaint();\r\n"+
            "}\r\n"+
            "Surface.prototype.setFov=function(fov){\r\n"+
            "  if(fov>this.fovMax)\r\n"+
            "	  fov=this.fovMax;\r\n"+
            "  if(fov<this.fovMin)\r\n"+
            "	  fov=this.fovMin;\r\n"+
            "  this.fov=fov;\r\n"+
            "  this.needsRepaint();\r\n"+
            "}\r\n"+
            "Surface.prototype.setZoom=function(zoom){\r\n"+
            "  if(zoom>this.zoomMax)\r\n"+
            "	  zoom=this.zoomMax;\r\n"+
            "  if(zoom<this.zoomMin)\r\n"+
            "	  zoom=this.zoomMin;\r\n"+
            "  this.zoom=zoom;\r\n"+
            "  this.surfaceGL.setZoom(this.zoom);\r\n"+
            "  this.needsRepaint();\r\n"+
            "}\r\n"+
            "\r\n"+
            "Surface.prototype.getZoom=function(){\r\n"+
            "  return this.zoom;\r\n"+
            "}\r\n"+
            "Surface.prototype.getFov=function(){\r\n"+
            "  return this.fov;\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "\r\n"+
            "");

	}
	
}