function SurfaceWebGL(element){
	var that = this;
	this.element = element;

	if (!Detector.webgl) {
	    var warning = Detector.getWebGLErrorMessage();
	    element.appendChild(warning);
	}
	else{
		this.xRot = 0;
		this.yRot = 0;
		this.zRot = 0;
		this.zoom = 1;
		this.focalX = 0;
		this.focalY = 0;
		this.centerX = 0;
		this.centerY = 0;
		this.fov = 70;
		this.z = 200;
		
	//	this.renderer = new THREE.WebGLRenderer();
		this.renderer = new THREE.WebGLRenderer({antialias:true});
		this.canvas = this.renderer.domElement;
		element.appendChild(this.canvas);
		
		this.start();
	}
}

SurfaceWebGL.prototype.close=function(){
	this.data = null;
	this.freeTexts();
	this.freeLines();
	this.freePolys();
	if(this.scene)
		this.scene.remove(this.world);
	if(this.canvas)
		this.element.removeChild(this.canvas);
	if(this.renderer){
		this.renderer.dispose()
		this.renderer = null;
	}
	this.canvas = null;
}

SurfaceWebGL.prototype.start=function(){
	this.scene = new THREE.Scene();
	this.scene.background = new THREE.Color(0xffffff);
	this.camera = new THREE.PerspectiveCamera( this.fov, this.canvas.width / this.canvas.height, 0.1, 10000 );
	this.raycaster = new THREE.Raycaster();
	this.mouse = new THREE.Vector2();
//	this.camera.filmGauge=12;
//	this.camera.position.z=this.z;
	this.euler = new THREE.Euler();
	this.world = new THREE.Group();
	this.world.rotation.order="XZY";
	
//	this.ambientLight = new THREE.AmbientLight(0x000000);
//	this.scene.add(this.ambientLight);
	this.lights = [];
	this.lights[0] = new THREE.PointLight(0xffffff, 1, 0);
	this.lights[1] = new THREE.PointLight(0xffffff, 1, 0);
	this.lights[2] = new THREE.PointLight(0xffffff, 1, 0);
	this.lights[0].position.set(0, 200, 0); 
	this.lights[1].position.set(100, 200, 100); 
	this.lights[2].position.set(-100, -200, -100); 
	this.scene.add(this.lights[0]);
	this.scene.add(this.lights[1]);
	this.scene.add(this.lights[2]);
	
	//Init materials
	//Text Material
	this.font = "Arial"
	this.fontSize = 32;
	this.lettersPerSide = 16;
	this.fontTexture = this.loadFont(this.font, this.fontSize, this.lettersPerSide);
	var uniforms = {
		    map : { type: "t", value: this.fontTexture }
		  };
	var vshader =
		"precision highp float; uniform mat4 modelMatrix; uniform mat4 viewMatrix; uniform mat4 projectionMatrix; attribute vec3 position; attribute vec3 offset; attribute vec3 center; attribute vec2 uvoffset; attribute vec4 color; attribute vec2 uv; varying vec4 vColor; varying vec2 vUv; mat4 getT(vec3 v){ mat4 m = mat4(1.0); m[3][0] = v[0]; m[3][1] = v[1]; m[3][2] = v[2]; return m; } mat4 getInvR(mat4 m){ float sx = 1.0/ length(vec3(m[0][0], m[0][1], m[0][2])); float sy = 1.0/ length(vec3(m[1][0], m[1][1], m[1][2])); float sz = 1.0/ length(vec3(m[2][0], m[2][1], m[2][2])); mat4 m2 = mat4(1.0); m2[0][0] = m[0][0]* sx; m2[0][1] = m[1][0]* sx; m2[0][2] = m[2][0]* sx; m2[1][0] = m[0][1]* sy; m2[1][1] = m[1][1]* sy; m2[1][2] = m[2][1]* sy; m2[2][0] = m[0][2]* sz; m2[2][1] = m[1][2]* sz; m2[2][2] = m[2][2]* sz; return m2; } void main(){ vec4 pos = vec4(position+offset-center, 1.0); pos = getInvR(modelMatrix) * pos; pos = getT(center) * pos; pos = modelMatrix* pos; gl_Position = projectionMatrix * viewMatrix * pos; vColor = color; vUv = uv+uvoffset; }";	
	var fshader = 
		"precision highp float; uniform sampler2D map; varying vec4 vColor; varying vec2 vUv; void main() { vec4 diffuse = texture2D(map, vUv); gl_FragColor =vec4(vColor.rgb, diffuse.a) ; }";
	
	this.textMaterial = new THREE.RawShaderMaterial({uniforms : uniforms, vertexShader :vshader, fragmentShader : fshader, 	/* side: THREE.DoubleSide, wireframe: true,*/ transparent: true });	
	this.linesMaterial = new THREE.LineBasicMaterial( { linewidth: 40, vertexColors: THREE.VertexColors, shading: THREE.FlatShading } );
	this.material3 = new THREE.MeshLambertMaterial( { vertexColors:THREE.VertexColors, side:THREE.DoubleSide, shading:THREE.FlatShading} );
	
//	this.material = new THREE.MeshBasicMaterial( { color: 0x00ff00} );
//	this.material4 = new THREE.MeshStandardMaterial( { color: 0x00ff00, side:THREE.DoubleSide, shading:THREE.FlatShading} );
//	this.material5 = new THREE.MeshStandardMaterial( { vertexColors:THREE.VertexColors, side:THREE.DoubleSide, shading:THREE.FlatShading} );
//	this.material3 = new THREE.MeshLambertMaterial( { wireframe:true, vertexColors:THREE.VertexColors, side:THREE.DoubleSide, shading:THREE.FlatShading} );
//	this.material2 = new THREE.LineBasicMaterial({color:0x000000});
//	this.cube = new THREE.Mesh( this.boxGeometry, this.material );
	
	this.scene.add(this.world);
	
	
	
}


SurfaceWebGL.prototype.updateScale=function() {
	if(this.data != null  && this.data.polys != null){
		var polys = this.data.polys;
		var minX = minY = minZ = Infinity;
		var maxX = maxY = maxZ = -Infinity;
		
		for(var i = 0; i < polys.length; i++){
			minX = Math.min(minX, polys[i][0], polys[i][4], polys[i][8]);
			maxX = Math.max(maxX, polys[i][0], polys[i][4], polys[i][8]);
			minY = Math.min(minY, polys[i][1], polys[i][5], polys[i][9]);
			maxY = Math.max(maxY, polys[i][1], polys[i][5], polys[i][9]);
			minZ = Math.min(minZ, polys[i][2], polys[i][6], polys[i][10]);
			maxZ = Math.max(maxZ, polys[i][2], polys[i][6], polys[i][10]);
			
		}
		this.sfX = maxX - minX;
		this.sfY = maxY - minY;
		this.sfZ = maxZ - minZ;
		this.sfX = this.sfY = this.sfZ = Math.min(this.sfX, this.sfY, this.sfZ);
	}
	else{
		this.sfX = 50;
		this.sfY = 50;
		this.sfZ = 50;
	}
//	err([this.sfX, this.sfY, this.sfZ]);
}
SurfaceWebGL.prototype.updateData=function(data) {
	this.data = data;
	this.updateScale();
	this.prepareLines();
	this.preparePolys();
	this.prepareTexts();
}
SurfaceWebGL.prototype.getTriangleId=function(x,y){
	var triId = null;
	if(!this.polys)
		return triId;
	this.mouse.x = (x/this.canvas.width) * 2 -1;
	this.mouse.y = -(y/this.canvas.height) * 2 +1;
	this.raycaster.setFromCamera(this.mouse, this.camera);
	
	var out = this.raycaster.intersectObject(this.polys);
	if(out.length){
		var id = out[0].faceIndex/3;
		if(id > 0)
			triId = id;
	}
	return triId;
}
SurfaceWebGL.prototype.getPolygonId=function(x,y){
	var polyId = null;
	if(!this.polys)
		return polyId;
	this.mouse.x = (x/this.canvas.width) * 2 -1;
	this.mouse.y = -(y/this.canvas.height) * 2 +1;
	this.raycaster.setFromCamera(this.mouse, this.camera);
	
	var out = this.raycaster.intersectObject(this.polys);
	if(out.length){
		var index =out[0].faceIndex/3;
		var id = this.data.polys[index][12];
		if(id > 0)
			polyId = id;
	}
	return polyId;
}

SurfaceWebGL.prototype.getPolygonIds=function(x1,y1,x2,y2){
    var sel={};
    var selList=[];
    
	if(!this.polys)
		return selList;
	
    if(x2<x1){var t=x1;x1=x2;x2=t;}
    if(y2<y1){var t=y1;y1=y2;y2=t;}
    
    var w = this.canvas.width;
    var h = this.canvas.height;
    
    var m = new THREE.Vector3(2*(x1/w) - 1.0, -2*(y2/h) + 1.0, 0);
    var m2 = new THREE.Vector3(2*(x2/w) - 1.0, -2*(y1/h) + 1.0, 0);
    
    var position = this.polys.geometry.attributes.position;
    var i, l;
    var vA, vB, vC;
    vA = new THREE.Vector3();
    vB = new THREE.Vector3();
    vC = new THREE.Vector3();
    
    
    for(i = 0, l = position.count; i < l; i+=3){
   		var id = this.data.polys[Math.floor(i/3)][12];
   		if(id > 0){
	   		if(sel[id] == true)
	   			continue;
	    	
	    	//check for intersection
	    	var intersect = false;
	    	
	    	vA.fromBufferAttribute(position,i);
	    	vB.fromBufferAttribute(position,i+1);
	    	vC.fromBufferAttribute(position,i+2);
	    	vA.applyEuler(this.euler);
	    	vB.applyEuler(this.euler);
	    	vC.applyEuler(this.euler);
	    	vA.project(this.camera);
	    	vB.project(this.camera);
	    	vC.project(this.camera);
	    	var mnX = Math.min(vA.x, vB.x, vC.x);
	    	var mxX = Math.max(vA.x, vB.x, vC.x);
	    	var mnY = Math.min(vA.y, vB.y, vC.y);
	    	var mxY = Math.max(vA.y, vB.y, vC.y);
	    	
	    	if(m.x < mxX && mnX < m2.x &&  m.y < mxY && mnY < m2.y){
	    		intersect = true;
	    	}
	    	if(intersect){
				sel[id]=true;
				selList[selList.length]=id;
	    	}
   		}
    }
    return selList;
}

SurfaceWebGL.prototype.drawScene=function() {
	var xr = (180-this.xRot)*Math.PI/180;
	var yr = this.yRot*Math.PI/180;
	var zr = this.zRot*Math.PI/180;
	this.euler.set(xr,yr,zr, "XZY");
	this.world.setRotationFromEuler(this.euler);
	
	
	var fl = this.camera.getFocalLength();
	this.camera.position.x=this.focalX/fl;
	this.camera.position.y=this.focalY/fl;
	this.camera.position.z = fl* this.z/this.fov;
//	this.camera.lookAt(new THREE.Vector3(0,0,-100));
//	this.camera.lookAt(this.scene.position);
	
	this.camera.updateMatrixWorld();
	this.camera.updateProjectionMatrix();
	
	this.renderer.render( this.scene, this.camera );
}

SurfaceWebGL.prototype.loadFont=function(font, fontSize, lettersPerSide) {
	  var yfontSize = fontSize+4;
	  var c = document.createElement('canvas');
	  c.width = fontSize*lettersPerSide;
	  c.height = yfontSize*lettersPerSide;
	  var ctx = c.getContext('2d');
	  ctx.font = fontSize+'px ' + font;
	  var i=0;

	  for (var y=0; y<lettersPerSide; y++) {
	  	// ctx.beginPath();
	  	// ctx.moveTo(0, y*yfontSize);
	  	// ctx.lineTo(c.width, y*yfontSize);
	  	// ctx.strokeStyle= '#00ff00';
	  	// ctx.stroke();

	  	// ctx.beginPath();
	  	// ctx.moveTo(0, (y+1)*yfontSize-1);
	  	// ctx.lineTo(c.width, (y+1)*yfontSize-1);
	  	// ctx.strokeStyle= '#ff0000';
	  	// ctx.stroke();
	    for (var x=0; x<lettersPerSide; x++,i++) {
	      var ch = String.fromCharCode(i);
	      var cx = +(5/32)*fontSize+x*fontSize;
	      var cy =  -(10/32)*yfontSize+(y+1)*yfontSize;
	      ctx.fillText(ch, cx, cy);
	    }
	  }

	  var tex = new THREE.Texture(c);
	  tex.flipY = false;
	  tex.needsUpdate = true;
	  return tex;	
}

SurfaceWebGL.prototype.freeTexts=function(){
	if(this.textsGeometry != null){
		this.textsGeometry.dispose();
		this.textsGeometry.attributes = null;
		this.textsGeometry = null;
	}
	if(this.textsMaterial != null){
		this.textsMaterial.dispose();
	}
	
	if(this.texts)
		this.world.remove(this.texts);
}
SurfaceWebGL.prototype.prepareTexts=function(){
	if(!this.data.texts)
		return;
	
	this.freeTexts();
	
	// Create new InstancedBufferGeometry (Square)
	this.textsGeometry = new THREE.InstancedBufferGeometry();
	// 2 triangles, 3 points, 3 dims
	var sz = (1.2/this.sfY)/2;
	var vertices = new THREE.BufferAttribute(new Float32Array([
	         -sz, -sz, 0.0,
	         +sz, -sz, 0.0,
	         -sz, +sz, 0.0,
	         
	         +sz, +sz, 0.0,
	         -sz, +sz, 0.0,
	         +sz, -sz, 0.0,
			]), 3);
	this.textsGeometry.addAttribute('position', vertices);
	
	var usz = (this.fontSize -1) /(this.fontSize * this.lettersPerSide); 
	var vertices = new THREE.BufferAttribute(new Float32Array([
	         0.0, usz, 
	         usz, usz,
	         0.0, 0.0,
	         
	         usz, 0.0,
	         0.0, 0.0,
	         usz, usz,
			]), 2);
	this.textsGeometry.addAttribute('uv', vertices);
	
	// Count number instances
	var instances = 0;
	
	var texs = this.data.texts;
	for(var il = 0; il < texs.length; il++){
		var str = texs[il][3];
		instances += str.length;
	}
	
	// Find the offsets
	
	var centers = new THREE.InstancedBufferAttribute( new Float32Array( instances * 3 ), 3, 1 );
	var offsets = new THREE.InstancedBufferAttribute( new Float32Array( instances * 3 ), 3, 1 );
	var uvoffsets = new THREE.InstancedBufferAttribute( new Float32Array( instances * 2 ), 2, 1 );
	var colors = new THREE.InstancedBufferAttribute( new Float32Array( instances * 4 ), 4, 1 );

	var oi = 0;
	var ui = 0;
	for(var i1 = 0; i1 < texs.length; i1++){
		var text = texs[i1];
		var str = text[3];
		var oox = text[0]/this.sfX;
		var ooy = text[1]/this.sfY;
		var ooz = text[2]/this.sfZ;
		var color = text[5];
		var r= (color >> 16 & 255) /255.0;
		var g= (color >> 8 & 255) /255.0;
		var b= (color & 255) /255.0;
		var j=0, ln=0;
		for (i=0; i<str.length; i++) {
			var code = str.charCodeAt(i);
			var cx = code % this.lettersPerSide;
			var cy = Math.floor(code / this.lettersPerSide);
			
			var ox=cx/this.lettersPerSide ;
			var oy=cy/this.lettersPerSide ;
			
			centers.setXYZ(oi, oox, ooy, ooz);
			offsets.setXYZ(oi, j*2.0*sz+oox, ln*2.0*sz+ooy, ooz);
			colors.setXYZ(oi, r,g,b, Math.random());
			uvoffsets.setXYZ(oi, ox, oy);
			
			oi++;
			if (code == 10) {
				ln--;
				j=0;
			} else {
				j++;
			}
		}
	}
	this.textsGeometry.addAttribute( 'center', centers );
	this.textsGeometry.addAttribute( 'offset', offsets );
	this.textsGeometry.addAttribute( 'color', colors );
	this.textsGeometry.addAttribute( 'uvoffset', uvoffsets);
	 
	this.texts = new THREE.Mesh(this.textsGeometry, this.textMaterial);
	this.world.add(this.texts);
}

SurfaceWebGL.prototype.freeLines=function(){
	if(this.linesGeometry != null){
		this.linesGeometry.clearGroups();
		this.linesGeometry.dispose();
		this.linesGeometry.attributes = null;
		this.linesGeometry = null;
	}
	if(this.linesMaterial != null){
		this.linesMaterial.dispose();
	}
	
	if(this.lines)
		this.world.remove(this.lines);
}
SurfaceWebGL.prototype.prepareLines=function(){
	if(!this.data.lines)
		return;
	
	this.freeLines();
//	if(this.axis)
//		return;
	
	var lines = this.data.lines;
	
	var vertices = [];
	var colorsRGB = [];
	for(var i = 0; i < lines.length; i++){
		vertices.push(lines[i][0]/ this.sfX);
		vertices.push(lines[i][1]/ this.sfY);
		vertices.push(lines[i][2]/ this.sfZ);
			
		vertices.push(lines[i][3]/ this.sfX);
		vertices.push(lines[i][4]/ this.sfY);
		vertices.push(lines[i][5]/ this.sfZ);
		colorsRGB.push((lines[i][6] >> 16 & 255) / 255.0);
		colorsRGB.push((lines[i][6] >> 8 & 255) / 255.0);
		colorsRGB.push((lines[i][6] & 255) / 255.0);
		colorsRGB.push((lines[i][6] >> 16 & 255) / 255.0);
		colorsRGB.push((lines[i][6] >> 8 & 255) / 255.0);
		colorsRGB.push((lines[i][6] & 255) / 255.0);
	}
	
	this.linesGeometry = new THREE.BufferGeometry();
	
	var verticesArray = new Float32Array(vertices);
	this.linesGeometry.addAttribute('position', new THREE.BufferAttribute(verticesArray, 3));
	
	var colorsArray = new Float32Array(colorsRGB);
	this.linesGeometry.addAttribute('color', new THREE.BufferAttribute(colorsArray, 3));
	
	this.lines = new THREE.LineSegments(this.linesGeometry, this.linesMaterial);
	this.world.add(this.lines);
	
	return vertices;
	
}

SurfaceWebGL.prototype.freePolys=function(){
	if(this.polysGeometry != null){
		this.polysGeometry.clearGroups();
		this.polysGeometry.dispose();
		this.polysGeometry.attributes = null;
		this.polysGeometry = null;
	}
	if(this.polysMaterial != null){
		this.polysMaterial.dispose();
	}
	
	if(this.polys)
		this.world.remove(this.polys);
}

SurfaceWebGL.prototype.preparePolys=function(){
	if(!this.data.polys)
		return;
	this.freePolys();
	
	var vertices = [];
	
	var polys = this.data.polys;
	
	var ax;
	var bx;
	var cx;
	for(var i = 0; i < polys.length; i++){
		vertices.push(polys[i][0]/this.sfX);
		vertices.push(polys[i][1]/this.sfY);
		vertices.push(polys[i][2]/this.sfZ);
			
		vertices.push(polys[i][4]/this.sfX);
		vertices.push(polys[i][5]/this.sfY);
		vertices.push(polys[i][6]/this.sfZ);
		
		vertices.push(polys[i][8]/this.sfX);
		vertices.push(polys[i][9]/this.sfY);
		vertices.push(polys[i][10]/this.sfZ);
	}
	
	var colorsHex = [];
	for(var i = 0; i < polys.length; i++){
		colorsHex.push(polys[i][3]);
		colorsHex.push(polys[i][7]);
		colorsHex.push(polys[i][11]);
	}
	var colorsRGB = [];
	var r;
	var g;
	var b;
	for(var i = 0; i < colorsHex.length; i++){
		r = (colorsHex[i] >> 16 & 255) / 255.0
		g = (colorsHex[i] >> 8 & 255) / 255.0
		b = (colorsHex[i] & 255) / 255.0
		colorsRGB.push(r);
		colorsRGB.push(g);
		colorsRGB.push(b);
	}
	
	this.polysGeometry = new THREE.BufferGeometry();
	var verticesArray = new Float32Array(vertices);
	var colorsArray = new Float32Array(colorsRGB);
	
	this.polysGeometry.addAttribute('position', new THREE.BufferAttribute(verticesArray, 3));
	this.polysGeometry.addAttribute('color', new THREE.BufferAttribute(colorsArray, 3));
	this.polysGeometry.computeFaceNormals();
	this.polysGeometry.computeVertexNormals();
	this.polys = new THREE.Mesh(this.polysGeometry, this.material3);
	this.world.add(this.polys);
	return vertices;
}




SurfaceWebGL.prototype.setSize=function(width,height) {
//	this.canvas.width = width;
//	this.canvas.height = height;
//	this.gl.viewport(0,0,width,height);
	this.camera.aspect = width/height;
	this.camera.updateProjectionMatrix();
	this.renderer.setSize( width, height);
}
SurfaceWebGL.prototype.setZoom=function(zoom){
	this.zoom = zoom;
	this.camera.zoom=this.zoom;
}
SurfaceWebGL.prototype.setFov=function(fov){
	this.fov = fov;
	this.camera.fov = this.fov;
	this.camera.updateProjectionMatrix();
}
SurfaceWebGL.prototype.setBackground=function(color){
	this.scene.background = new THREE.Color(color);
}
SurfaceWebGL.prototype.setFocalX=function(x){
	this.focalX = x;
}
SurfaceWebGL.prototype.setFocalY=function(y){
	this.focalY = y;
}
SurfaceWebGL.prototype.repaint=function(data){
	this.data = data;
	this.drawScene();
}


function Surface(element){ 
  var that=this; 
  this.surfaceGL = new SurfaceWebGL(element);
  this.textbuf=nw('canvas');
  this.textbuf.width=500;
  this.textbuf.height=40;

  this.textbufContext=this.textbuf.getContext('2d');
  this.textbufContext.font="20px Arial";
  this.textbufContext.fillStyle="black";
  this.textbufContext.textAlign='left';
  this.textbufContext.textBaseline='top';
  this.rotScale=Math.PI/180;
  this.element=element;
  this.element.style.background='white';
  this.element.style.width='100%';
  this.element.style.height='100%';
  this.canvas = this.surfaceGL.canvas;
//  this.canvas=nw('canvas');
  this.context = this.canvas.getContext('2d');
  this.centerYOffset=0;
  this.centerXOffset=0;
  this.selCanvas=nw('canvas');
  this.setSize(10,10);
  this.selCanvas.tabIndex=1;
  makeCanvasKeyEventable(this.selCanvas,this);
  this.selContext = this.selCanvas.getContext('2d');
  
  makeDraggable(this.selCanvas);
  this.selCanvas.ondraggingStart=function(target,e){
    var button=getMouseButton(e);
    that.dragSelect=button!=1 || (!e.altKey && (e.shiftKey || e.ctrlKey));
    that.dragSelectToggle=e.ctrlKey;
    that.dragSelectAdd=e.shiftKey;
    that.dragDeltaX=0;
    that.dragDeltaY=0;
  };
  this.selCanvas.onMouseWheel=function(e,delta){
    if(e.altKey){
    	//log(delta);
      var t=that.fov;
      t+=that.fov*that.fov*(delta/1500);
      //t=Math.sqrt(t);
      that.setFov(t);
    	//log(that.getFov());
    }else{
      that.setZoom(delta/2+that.getZoom(),true);
    }
    that.repaintIfNeeded();
    that.fireOnUserChangedPerspective();
  };
  this.selCanvas.ondblclick=function(){that.dumpPosition();/*that.selCanvas.focus()*/};
  this.selCanvas.ondragging=function(target,deltax,deltay,e){ that.onDragging(target,deltax,deltay,e); };
  this.selCanvas.ondraggingEnd=function(target,deltax,deltay,e){ that.onDraggingEnd(target,deltax,deltay,e); };
  this.selCanvas.onmouseout=function(e){that.onMouseOut(e);};
  this.selCanvas.onmousemove=function(e){that.onMouseMove(e);};
  this.canvas.style.position='absolute';
  this.selCanvas.style.position='absolute';
  this.element.appendChild(this.canvas);
  this.element.appendChild(this.selCanvas);
  this.setRotationX(0);
  this.setRotationY(0);
  this.setRotationZ(0);
  this.focalX=0;
  this.focalY=0;
  this.zoom=1;
  this.fov=400;
  this.setOptions({});
  this.needsRepaint();
  this.zoomMin=1;
  this.zoomMax=40;
  this.fovMin=70;
  this.fovMax=1000;
} 

Surface.prototype.close=function(){
	this.texts = null;
	this.polys = null;
	this.lines = null
	this.surfaceGL.close();
	this.surfaceGL = null;
	
}
Surface.prototype.onMouseMove=function(e){
    var point=getMouseLayerPoint(e);
    if(this.mmlastX==point.x && this.mmlastY==point.y)
    	return;
    this.mmlastX=point.x;
    this.mmlastY=point.y;
	if(this.hoverTimer!=null){
		clearTimeout(this.hoverTimer);
		this.hoverTimer=null;
	}
	if(this.onHover!=null){
	  var that=this;
      this.hoverTimer=setTimeout(function(){that.onMouseStill(e);}, 100);
    }
}
Surface.prototype.onMouseOut=function(e){
	if(this.hoverTimer!=null){
		clearTimeout(this.hoverTimer);
		this.hoverTimer=null;
	}
	this.clearHover();
}

Surface.prototype.onMouseStill=function(e){
	if(this.hoverTimer!=null){
		clearTimeout(this.hoverTimer);
		this.hoverTimer=null;
	}
    var point=getMouseLayerPoint(e);
    var x1=point.x;
    var y1=point.y;
    var pid=this.surfaceGL.getTriangleId(x1,y1);
    if(pid!=this.hoverPid){
	  this.clearHover();
      this.hoverPid=pid;
      this.hoverRequest=pid;
      this.onHover(pid,x1,y1);
    }else if(pid==null)
	  this.clearHover();
}

Surface.prototype.setHover=function(x,y,sel,name,xAlign,yAlign){
	  if(this.hoverRequest==sel){
		if(this.tooltipDiv!=null)
		  this.element.removeChild(this.tooltipDiv);
	    this.tooltipDiv=nw("div","ami_chart_tooltip");
		var div=this.tooltipDiv;
		this.hoverX=MOUSE_POSITION_X;
		this.hoverY=MOUSE_POSITION_Y;
		div.innerHTML=name;
		if(div.firstChild!=null && div.firstChild.tagName=='DIV'){
			this.tooltipDiv=div.firstChild;
		    div=this.tooltipDiv;
		}
		this.element.appendChild(div);
		var rect=new Rect().readFromElement(div);
		var h=rect.height;
		var w=rect.width;
		switch(xAlign){
		  case ALIGN_LEFT: div.style.left=toPx(x); break;
		  case ALIGN_RIGHT: div.style.left=toPx(x-w); break;
		  default: div.style.left=toPx(x-w/2); break;
		}
		switch(yAlign){
		  case ALIGN_TOP: div.style.top=toPx(y); break;
		  case ALIGN_BOTTOM: div.style.top=toPx(y-h); break;
		  default: div.style.top=toPx(y-h/2); break;
		}
		ensureInDiv(div,this.element);
	  }
	}

Surface.prototype.clearHover=function(){
  if(this.tooltipDiv!=null){
    this.element.removeChild(this.tooltipDiv);
    this.tooltipDiv=null;
  }
  this.hoverRequest=null;
}



Surface.prototype.getPolygonIds=function(x1,y1,x2,y2){
    if(x2<x1){var t=x1;x1=x2;x2=t;}
    if(y2<y1){var t=y1;y1=y2;y2=t;}
    var sel={};
    var selList=[];
    for(var x=x1;x<=x2;x++){
      for(var y=y1;y<=y2;y++){
        var id=this.getPolygonId(x,y);
        if(id!=null){
          poly=this.currentPolys[id];
          if(poly!=null){
            var polyId=poly[12];
            if(polyId>0){
              if(!sel[polyId]){
                sel[polyId]=true;
                selList[selList.length]=polyId;
              }
            }
          }
        }
      }
    }
    return selList;
}

Surface.prototype.onDraggingEnd=function(target,deltax,deltay,e){
  if(this.dragSelect || (deltax==0 && deltay==0)){
    var point=getMouseLayerPoint(e);
    var x1=point.x;
    var y1=point.y;
    var x2=x1-deltax;
    var y2=y1-deltay;
	var selList=this.surfaceGL.getPolygonIds(x1,y1,x2,y2);

    
    var clearSelect=selList.length==0 && deltax<2 && deltay<2 && deltax>-2 && deltay>-2;
    if(this.onSelectionChanged)
      this.onSelectionChanged(selList,this.dragSelectToggle,this.dragSelectAdd,clearSelect);
    this.selContext.clearRect(0,0,this.width,this.height);
        if(getMouseButton(e)==2){
      if(this.onShowContextMenu)
        this.onShowContextMenu();
    }
    return;
  }
}
Surface.prototype.onDragging=function(target,deltax,deltay,e){
  if(this.dragSelect){
    var point=getMouseLayerPoint(e);
    this.selContext.clearRect(0,0,this.width,this.height);
    this.selContext.lineWidth='2';
    this.selContext.strokeStyle='rgba(64,64,64,.5)';
    this.selContext.strokeRect(point.x,point.y,-deltax,-deltay);
    this.selContext.stroke();
    return;
  }
      var dx=deltax-this.dragDeltaX;
      var dy=deltay-this.dragDeltaY;
      this.dragDeltaX=deltax;
      this.dragDeltaY=deltay;
      if(e.altKey){
        if(e.shiftKey){
          this.focalX+=dx;
          this.focalY+=dy;
        }else{
          this.centerXOffset+=dx;
          this.centerYOffset+=dy;
        }
        this.needsRepaint();
        this.repaintIfNeeded();
        //this.fireOnUserChangedPerspective();
        return;
      }
	  var q=new Quaternion();
	  if(dx==0 && dy==0)
	    return;
	  if(Math.abs(dx)>Math.abs(dy))dy=0;
	  else if(Math.abs(dy)>Math.abs(dx))dx=0;
	  var x=this.getRotationX();
	  var y=this.getRotationY();
	  var z=this.getRotationZ();
	  q.setFromEuler({x:x,y:y,z:z});
	  var qy=new Quaternion();
	  qy.setFromAxisAngle({x:0,y:1,z:0},-dx/90);
	  var qx=new Quaternion();
	  qx.setFromAxisAngle({x:1,y:0,z:0},-dy/90);
	  q.multiplySelf(qx);
	  q.multiplySelf(qy);
	  q.normalize();
	  var o={};
	  q.setToEuler(o);
	  this.setRotationX(o.x);
	  this.setRotationY(o.y);
	  this.setRotationZ(o.z);
      this.repaintIfNeeded();
	  this.fireOnUserChangedPerspective();
}

function makeCanvasKeyEventable(o,target){
	if(target==null)
		target=o;
	o.tabIndex=1;
	o.style.outline=0;
    o.addEventListener('keydown',function(e){if(target.onkeydown) target.onkeydown(e);},false);
//    o.addEventListener('mouseover',function(e){o.focus();},false);
//    o.addEventListener('mouseout',function(e){o.blur();},false);
}


Surface.prototype.dumpPosition=function(){
	//log({rotx:this.getRotationX(),roty:this.getRotationY(),rotz:this.getRotationZ(),zoom:this.getZoom()})
}

Surface.prototype.onkeydown=function(e){
	if(e.keyCode==38){
		this.setRotationX(this.getRotationX()+10);
	}else if(e.keyCode==40){
		this.setRotationX(this.getRotationX()-10);
	} else if(e.keyCode==37){
		this.setRotationY(this.getRotationY()+10);
	}else if(e.keyCode==39){
		this.setRotationY(this.getRotationY()-10);
	}
    this.repaintIfNeeded();
    this.fireOnUserChangedPerspective();
}
Surface.prototype.fireOnUserChangedPerspective=function(){
  if(this.onUserChangedPerspective)
    this.onUserChangedPerspective(this.getRotationX(),this.getRotationY(),this.getRotationZ(),this.zoom,this.fov);
}

Surface.prototype.needsRepaint=function(){
  this.needsRepaintFlag=true;
}

Surface.prototype.repaintIfNeeded=function(){
  if(!this.needsRepaintFlag)
    return;
  this.surfaceGL.repaint({texts:this.texts, polys:this.polys, lines:this.lines});
//  this.repaint();
  this.needsRepaintFlag=false;
}
Surface.prototype.repaint=function(){
  this.centerX=(this.canvas.width/2)+this.centerXOffset-this.focalX;
  this.centerY=(this.canvas.height/2)+this.centerYOffset-this.focalY;
  var width=this.canvas.width;
  var height=this.canvas.height;
  this.width=width;
  this.height=height;
  var zbuffer=[];
  var idbuffer=[];
  var imgbuffer=[];
  for(var i=0;i<height;i++){
	  zbuffer[i]=[];
	  idbuffer[i]=[];
	  imgbuffer[i]=[];
  }
  this.zbuffer=zbuffer;
  this.idbuffer=idbuffer;
  this.imgbuffer=imgbuffer;
	  
  
  this.context.clearRect(0,0,width,height);
  this.pixelBuf=this.context.createImageData(this.canvas.width,this.canvas.height);
  this.pixelBufData=this.pixelBuf.data;
  
    
  this.doPolys();
  this.doTexts();
  this.doLines();
  
  
  this.context.putImageData(this.pixelBuf,0,0);
}
               
Surface.prototype.doPolys=function(){
  if(this.polys){
    var polys=[];
    var j=0;
	var d1=Date.now();
	var rd=0,pd=0;
    for(var i=0;i<this.polys.length;i++){
      var p=this.polys[i].slice();
      //var p=new Float32Array(this.polys[i]);
      //rd-=Date.now();
      this.rotate(p,0);
      this.rotate(p,4);
      this.rotate(p,8);
     // rd+=Date.now();
      
      //pd-=Date.now();
      this.toPerspective(p,0);
      this.toPerspective(p,4);
      this.toPerspective(p,8);
      //pd+=Date.now();
      
      //if(diff(p[0],p[4]) >= .5  || diff(p[0],p[8]) >=.5  || diff(p[4],p[8]) >=.5  || diff(p[1],p[5]) >=.5  || diff(p[1],p[9]) >=.5  || diff(p[5],p[9]) >=.5 ){
    	  polys[j++]=p;
      //}
}
	var d2=Date.now();
    this.sortPolys(polys);	var d3=Date.now();

    for(var i=0;i<polys.length;i++)
      prepTriangle(polys[i]);
	var d4=Date.now();
    for(var i=0;i<polys.length;i++){
      var p=polys[i];
      //if(diff(p[0],p[4]) >= 1  || diff(p[0],p[8]) >= 1  || diff(p[4],p[8]) >= 1  || diff(p[1],p[5]) >= 1  || diff(p[1],p[9]) >= 1  || diff(p[5],p[9]) >= 1 ){
        this.fillTriangle(p,i);
      //}else 
    	  //this.setPixel(fl(p[0]),fl(p[1]),255,0,255,255);
    }
	var d5=Date.now();
	//log([d1,d2-d1,d3-d2,d4-d3,d5-d4,rd,pd]);
    this.currentPolys=polys;
  }
}
Surface.prototype.doTexts=function(){
  if(this.texts){
      var texts=this.texts.slice();//clone
      for(var i in texts){
    	  var p=texts[i].slice();
		  this.rotate(p,0);
    	  this.toPerspective(p,0);
    	  texts[i]=p;
      }
	  for(var i in texts)
        this.drawText(texts[i],10000+i);
      this.currentTexts=texts;
  }
}
Surface.prototype.doLines=function(){
	var d1=Date.now();
  if(this.lines){      var lines=this.lines.slice();      for(var i in lines){    	  var p=lines[i].slice();		  this.rotate(p,0);		  this.rotate(p,3);    	  this.toPerspective(p,0);    	  this.toPerspective(p,3);    	  lines[i]=p;      }	  for(var i in lines)        this.drawLine(lines[i],10000+i);      this.currentLines=lines;  }	var d2=Date.now();
//log(d2-d1);
}
Surface.prototype.setData=function(data){
  this.polys=data.polys ? data.polys : [];
  this.texts=data.texts ? data.texts : [];
  this.lines=data.lines ? data.lines : [];
  this.surfaceGL.updateData(data)
  this.needsRepaint();
}
Surface.prototype.setOptions=function(o){
  if(o.minXRot!=null) this.minXRot=o.minXRot*1;// else this.minXRot=null;
  if(o.minYRot!=null) this.minYRot=o.minYRot*1;// else this.minYRot=null;
  if(o.minZRot!=null) this.minZRot=o.minZRot*1;// else this.minZRot=null;
  if(o.maxXRot!=null) this.maxXRot=o.maxXRot*1;// else this.maxXRot=null;
  if(o.maxYRot!=null) this.maxYRot=o.maxYRot*1;// else this.maxYRot=null;
  if(o.maxZRot!=null) this.maxZRot=o.maxZRot*1;// else this.maxZRot=null;
  if(o.rotX!=null) this.setRotationX(o.rotX*1);
  if(o.rotY!=null) this.setRotationY(o.rotY*1);
  if(o.rotZ!=null) this.setRotationZ(o.rotZ*1);
  if(o.zoom!=null) this.setZoom(o.zoom*1);
  if(o.background!=null) {this.element.style.background=o.background;};
  if(o.backgroundClass!=null) {this.element.className=o.backgroundClass;};
  if(o.centerY!=null) this.centerYOffset=o.centerY*1;// else this.centerYOffset=0;
  if(o.centerX!=null) this.centerXOffset=o.centerX*1;// else this.centerXOffset=0;
  if(o.focalY!=null) this.focalY=o.focalY*1;// else this.centerYOffset=0;
  if(o.focalX!=null) this.focalX=o.focalX*1;// else this.centerXOffset=0;
  if(o.fov!=null) this.fov=o.fov*1;
  
  //SurfaceGL setOptions
  if(o.background !=null)this.surfaceGL.setBackground(o.background);
  if(o.fov != null) this.surfaceGL.setFov(o.fov);
  if(o.focalX != null) this.surfaceGL.setFocalX(o.focalX);
  if(o.focalY != null) this.surfaceGL.setFocalY(o.focalY);
  if(o.centerX != null) this.surfaceGL.centerX = o.centerX;
  if(o.centerY != null) this.surfaceGL.centerY = o.centerY;
  
  var start=(new Date().getTime());
  this.repaintIfNeeded();
  var end=(new Date().getTime());
}



Surface.prototype.toPerspective=function(polys,offset){
  var t=this.zoom*this.fov/(this.fov+polys[offset+2]);
  polys[offset]=(polys[offset]+this.focalX)*t+this.centerX;
  polys[offset+1]=(polys[offset+1]+this.focalY)*t+this.centerY;
}

Surface.prototype.setSize=function(width,height){
	this.width = width;
	this.height = height;
	this.surfaceGL.setSize(width,height);
	this.canvas.width=width;
	this.canvas.height=height;
	this.selCanvas.width=width;
	this.selCanvas.height=height;
	this.needsRepaint();
	this.repaintIfNeeded();
}


Surface.prototype.drawText=function(p,pid){	var x=fl(p[0]);	var y=fl(p[1]);	var z=fl(p[2]);	var text=p[3];	var align=p[4];	var color=p[5];	var w=Math.min(this.textbufContext.measureText(text).width,this.textbuf.width);
	if(w<1)
	  return;	var h=this.textbuf.height;
	if(this.textbufContext.lastColor!=color){	  this.textbufContext.lastColor=color;	  this.textbufContext.fillStyle='rgb('+getRed(color)+','+getGrn(color)+','+getBlu(color)+')';
	}
    var t=this.zoom*this.fov/(this.fov+z);
    if(t<3)
    	return;
    this.textbufContext.font=min(fl(t*2),15)+"px Arial";
	this.textbufContext.clearRect(0,0,w,h);	this.textbufContext.fillText(text, 0, 20);	var data=this.textbufContext.getImageData(0,0,w,h).data;
	y-=30;	if(align==1)		x-=w;
    else if(align==0)		x-=rd(w/2);

	for(var y2=0;y2<h;y2++){
	  var zbuf=this.zbuffer[y+y2];
	  if(zbuf==null)
		  continue;	  for(var x2=0;x2<w;x2++){		  var bufoffset=(x2+y2*w)*4;		  var bufloc=(x+x2);		  if(data[bufoffset+3]<32)			  continue;
		  if(zbuf[bufloc]==null || z<zbuf[bufloc]){		    this.setPixel(x+x2,y+y2,data[bufoffset+0],data[bufoffset+1],data[bufoffset+2],data[bufoffset+3]);		     zbuf[bufloc]=z;		  }	  }	}}

Surface.prototype.drawLine=function(p,pid){
	var x1=fl(p[0]);
	var y1=fl(p[1]);
	var z1=fl(p[2]);
	var x2=fl(p[3]);
	var y2=fl(p[4]);
	var z2=fl(p[5]);
	var color=p[6] || 0;
	var width=p[7] || 1;
	var r=getRed(color);
	var g=getGrn(color);
	var b=getBlu(color);
	var xd=x2-x1;
	var yd=y2-y1;
	var zd=z2-z1;
	var this_zbuffer=this.zbuffer;
	var this_idbuffer=this.idbuffer;
	var this_height=this.height;
	var this_width=this.width;
	if(xd==0 && yd==0){
		   if(width>1){
			  var lf=x1-fl(width/2);
			  var rt=lf+width;
			  var tp=y1-fl(width/2);
			  var bt=tp+width;
			  for(var x=lf;x<rt;x++){
			    for(var y=tp;y<bt;y++)
		           if(z1<this.getZ(x,y)){
		        	 var zbuf=this_zbuffer[y];
		        	 if(zbuf==null)
		        		 continue;
			         zbuf[x]=z1;
			         this.setPixel(x,y,r,g,b,255);
		           }
			  }
			}else{
		      if(z1<this.getZ(x1,y1)){
		    	var zbuf=this_zbuffer[y1];
		    	if(zbuf){
			      zbuf[x1]=z1;
			      this.setPixel(x1,y1,r,g,b,255);
		    	}
		      }
			}
		return;
	}
	if(z1>this.getZ(x1,y1) && z2>this.getZ(x2,y2) && this.getPolygonId(x1,y1)==this.getPolygonId(x2,y2))
		return;
	if(Math.abs(xd)>Math.abs(yd)){
		var min,max;
		if(x1<x2){
			min=x1;
			max=x2;
		}else{
			min=x2;
			max=x1;
		}
		var slope = yd/ xd;
		var zslope = zd/ xd;
		if(min<0)min=0;
		if(max>=this_width)max=this_width-1;
		if(width>1){
		    var woff=fl(width/2);
		    var w2=Math.sqrt(width*width/(1+slope*slope));
		    for(var i=0;i<=w2;i++){
			  var w=i-woff;
		      for(var x=min;x<=max;x++){
			    var y=fl(y1+(x-x1)*slope);
			    var z=z1+(x-x1)*zslope;
			    var x2=fl(x-w*slope);
			    var y2=y+w;
			    if(y2>=this_height || y2<0) continue;
			    if(this_zbuffer[y2][x2]==null || z<this_zbuffer[y2][x2]){
				  this_zbuffer[y2][x2]=z;
				  this_idbuffer[y2][x2]=pid;
			      this.setPixel(x2,y2,r,g,b,255);
			    }
			    y2++;
			    if(y2>=this_height) continue;
			    if(this_zbuffer[y2][x2]==null || z<this_zbuffer[y2][x2]){
				  this_zbuffer[y2][x2]=z;
				  this_idbuffer[y2][x2]=pid;
			      this.setPixel(x2,y2,r,g,b,255);
			    }
		      }
		    }
		}else{
		  for(var x=min;x<=max;x++){
			var y=fl(y1+(x-x1)*slope);
			var z=z1+(x-x1)*zslope;
			  if(y<0 || y>=this_height) continue;
			if(this_zbuffer[y][x]==null || z<this_zbuffer[y][x]){
				this_zbuffer[y][x]=z;
				this_idbuffer[y][x]=pid;
			    this.setPixel(x,y,r,g,b,255);
			}
		  }
		}
	}else{
		var min,max;
		if(y1<y2){
			min=y1;
			max=y2;
		}else{
			min=y2;
			max=y1;
		}
		if(min<0)min=0;
		if(max>=this_height)max=this_height-1;
		var slope = xd/ yd;
		var zslope = zd/ yd;
		if(width>1){
		  var woff=fl(width/2);
		  var w2=Math.sqrt(width*width/(1+slope*slope));
		  for(var i=0;i<=w2;i++){
			var w=i-woff;
		    for(var y=min;y<=max;y++){  
			  var x=fl(x1+(y-y1)*slope);
			  var z=z1+(y-y1)*zslope;
			  var x2=x+w;
			  var y2=fl(y-w*slope);
			  if(x2>=this_width || x2<0) continue;
			  if(y2>=this_height || y2<0) continue;
			  if(this_zbuffer[y2][x2]==null || z<this_zbuffer[y2][x2]){
				this_zbuffer[y2][x2]=z;
				this_idbuffer[y2][x2]=pid;
			    this.setPixel(x2,y2,r,g,b,255);
			  }
			  x2++;
			  if(x2>=this_width) continue;
			  if(this_zbuffer[y2][x2]==null || z<this_zbuffer[y2][x2]){
				this_zbuffer[y2][x2]=z;
				this_idbuffer[y2][x2]=pid;
			    this.setPixel(x2,y2,r,g,b,255);
			  }
		    }
		  }
		} else{
		  for(var y=min;y<=max;y++){
			var x=fl(x1+(y-y1)*slope);
			var z=z1+(y-y1)*zslope;
			if(this_zbuffer[y][x]==null || z<this_zbuffer[y][x]){
				this_zbuffer[y][x]=z;
				this_idbuffer[y][x]=pid;
			    this.setPixel(x,y,r,g,b,255);
			}
		  }
		}
	}
}

               
Surface.prototype.sortPolys=function(polys){
	//polys.sort(function(a,b){return Math.max(a[2],a[6],a[10]) - Math.max(b[2],b[6],b[10]); });	polys.sort(function(a,b){return max3(a[2],a[6],a[10],b[2],b[6],b[10]); });
}

function max3(a,b,c,d,e,f){
	var m1=a>b ? (a > c ? a : c) : (b > c ? b : c);
	if(m1<d || m1<e || m1<f)
		return 1;
	else if(m1==d && m1==e && m1==f) 
		return 0;
	else
		return -1;
		
		
}
Surface.prototype.fillTrianglePart=function(
		pid,top,bottom,
		topLeft,topRight,bottomLeft,bottomRight,
		topLeftZ,topRightZ,bottomLeftZ,bottomRightZ,
		topLeftCR,topRightCR,bottomLeftCR,bottomRightCR,
		topLeftCG,topRightCG,bottomLeftCG,bottomRightCG,
		topLeftCB,topRightCB,bottomLeftCB,bottomRightCB
		){
	if(bottomLeft>bottomRight || topLeft>topRight){
		var t=bottomLeft;
		bottomLeft=bottomRight;
		bottomRight=t;
		t=topLeft;
		topLeft=topRight;
		topRight=t;
		
		t=bottomLeftZ;
		bottomLeftZ=bottomRightZ;
		bottomRightZ=t;
		t=topLeftZ;
		topLeftZ=topRightZ;
		topRightZ=t;
		
		t=bottomLeftCR;
		bottomLeftCR=bottomRightCR;
		bottomRightCR=t;
		t=topLeftCR;
		topLeftCR=topRightCR;
		topRightCR=t;
		
		t=bottomLeftCG;
		bottomLeftCG=bottomRightCG;
		bottomRightCG=t;
		t=topLeftCG;
		topLeftCG=topRightCG;
		topRightCG=t;
		
		t=bottomLeftCB;
		bottomLeftCB=bottomRightCB;
		bottomRightCB=t;
		t=topLeftCB;
		topLeftCB=topRightCB;
		topRightCB=t;
	}
	
	//bottom+=1;
	//top-=1;
	//bottomLeft-=1;
	//bottomRight+=1;
	//topLeft-=1;
	//topRight+=1;
	var this_zbuffer=this.zbuffer;
	var this_idbuffer=this.idbuffer;
	var this_imgbuffer=this.imgbuffer;
	var this_width=this.width;
	var this_height=this.height;
    var this_pixelBufData=this.pixelBufData;
	
	var vdiff=bottom-top;
	var slope1=(topLeft-bottomLeft)/vdiff;
	var slope2=(topRight-bottomRight)/vdiff;
	var slopeZ1=(topLeftZ-bottomLeftZ)/vdiff;
	var slopeZ2=(topRightZ-bottomRightZ)/vdiff;
	var slopeCR1=((topLeftCR)-(bottomLeftCR))/vdiff;
	var slopeCR2=((topRightCR)-(bottomRightCR))/vdiff;
	var slopeCG1=((topLeftCG)-(bottomLeftCG))/vdiff;
	var slopeCG2=((topRightCG)-(bottomRightCG))/vdiff;
	var slopeCB1=((topLeftCB)-(bottomLeftCB))/vdiff;
	var slopeCB2=((topRightCB)-(bottomRightCB))/vdiff;
	if(bottom>=this_height)
		bottom=this_height-1;
	var topi=Math.max(top,0);
	
	for(var i=topi;i<=bottom;i++){
		var voffset=top-i;
		var y=fl(i);
		var x1=voffset*slope1+topLeft;
		var x2=voffset*slope2+topRight;
		var xdiff=x2-x1;
		var left=fl(x1);
		var right=fl(x2);
		var z1=voffset*slopeZ1+topLeftZ;
		var z2=voffset*slopeZ2+topRightZ;
		var zbuf=this_zbuffer[y];
		var idbuf=this_idbuffer[y];
		if(zbuf[left]<z1 && zbuf[right]<z2 && idbuf[left] == idbuf[right])
			continue;
		var imgbuf=this_imgbuffer[y];
			
		var zslope=x1==x2 ? 0 : (z2-z1)/(xdiff);
		
		var cr1=voffset*slopeCR1+(topLeftCR);
		var cr2=voffset*slopeCR2+(topRightCR);
		var crslope=x1==x2 ? 0 : (cr2-cr1)/(xdiff);
		
		var cg1=voffset*slopeCG1+(topLeftCG);
		var cg2=voffset*slopeCG2+(topRightCG);
		var cgslope=x1==x2 ? 0 : (cg2-cg1)/(xdiff);
		
		var cb1=voffset*slopeCB1+(topLeftCB);
		var cb2=voffset*slopeCB2+(topRightCB);
		var cbslope=x1==x2 ? 0 : (cb2-cb1)/(xdiff);
		
	    var bufOffset=y*this_width;
		var leftLine=x1+Math.abs(slope1);
		var rightLine=x2-Math.abs(slope2);
		
		var leftClipped=left<0 ? 0 : left;
		var rightClipped=right>=this_width ? this_width-1 : right;
	    for(x=leftClipped;x<=rightClipped;x++){
	    	var existingZ=zbuf[x];
	    	var xoffset=x-left;
	    	var z=z1+zslope*(xoffset);
	    	if(existingZ==null || existingZ>z){
	    	  zbuf[x]=z;
	    	  idbuf[x]=pid;
	    	  var r=cr1+crslope*xoffset;
	    	  var g=cg1+cgslope*xoffset;
	    	  var b=cb1+cbslope*xoffset;
	    	  var border=x<=leftLine || x>=rightLine;
	    	  if(border){ r-=10; g-=10; b-=10; }
              var idx = (x + bufOffset) * 4;
              r= (r < 1 ? 0 : r > 254 ? 255 : fl(r));
              g= (g < 1 ? 0 : g > 254 ? 255 : fl(g));
              b= (b < 1 ? 0 : b > 254 ? 255 : fl(b));
              //imgbuf[x]=(r)|(g<<8)|(b<<16);
              this_pixelBufData[idx] = (r < 1 ? 0 : r > 254 ? 255 : fl(r));
              this_pixelBufData[idx+1] = (g < 1 ? 0 : g > 254 ? 255 : fl(g));
              this_pixelBufData[idx+2] = (b < 1 ? 0 : b > 254 ? 255 : fl(b));
              this_pixelBufData[idx+3] = 255;
	    	}
	    }
	}
}

Surface.prototype.getPolygonId=function(x, y) {
	if(x<0 || y<0 || x>this.width || y>this.height)
		return null;
	return this.idbuffer[fl(y)][fl(x)];
}
Surface.prototype.getZ=function(x, y) {
	if(x<0 || y<0 || x>=this.width || y>=this.height)
		return 1000000;
	return this.zbuffer[fl(y)][fl(x)] || 1000000;
}

Surface.prototype.setPixel=function(x, y, r, g, b, a) {
    if(x<0 || x>=this.width || y<0 || y>=this.height)
      return;
    var idx = (x + y * this.width) * 4;
    var buf=this.pixelBufData;
    buf[idx+0] = (r < 1 ? 0 : r > 254 ? 255 : fl(r));
    buf[idx+1] = (g < 1 ? 0 : g > 254 ? 255 : fl(g));
    buf[idx+2] = (b < 1 ? 0 : b > 254 ? 255 : fl(b));
    buf[idx+3] = (a);
}
               


function swap(p,i,j){
	var t=p[i]; 
	p[i]=p[j]; 
	p[j]=t;
	
	t=p[i+1]; 
	p[i+1]=p[j+1]; 
	p[j+1]=t;
	
	t=p[i+2]; 
	p[i+2]=p[j+2]; 
	p[j+2]=t;
	
	t=p[i+3]; 
	p[i+3]=p[j+3]; 
	p[j+3]=t;
}



// 0=x, 1=y, 2=z, 3=c,     4=x, 5=y, 6=z, 7=c,    8=x, 9=y, 10=z, 11=c
function prepTriangle(p){
	var a = p[0];
	var b = p[1];
	var c = p[2];
	var d = p[3];
	
	var e = p[4];
	var f = p[5];
	var g = p[6];
	var h = p[7];
	
	var i = p[8];
	var j = p[9];
	var k = p[10];
	var l = p[11];
	
	if(b>f){
		if(b>j){
			if(f>j){//3,2,1:  1=3,3=1
//				this.swap(p,0,8);
				p[0]=i;p[1]=j;p[2]=k;p[3]=l;
				p[8]=a;p[9]=b;p[10]=c;p[11]=d;
			}else{//2,3,1: 1=2,2=3,3=1
//				this.swap(p,0,4);
//				this.swap(p,8,4);
				p[0]=e;p[1]=f;p[2]=g;p[3]=h;
				p[4]=i;p[5]=j;p[6]=k;p[7]=l;
				p[8]=a;p[9]=b;p[10]=c;p[11]=d;
			}
		}else{//2,1,3: 1=2,2=1
//			this.swap(p,0,4);
			p[0]=e;p[1]=f;p[2]=g;p[3]=h;
			p[4]=a;p[5]=b;p[6]=c;p[7]=d;
		}
	}else if(f > j){
		if(b>j){//2,3,1: 1=3,3=2,2=1
//			swap(p,0,8);
//			swap(p,8,4);
			p[0]=i;p[1]=j;p[2]=k;p[3]=l;
			p[4]=a;p[5]=b;p[6]=c;p[7]=d;
			p[8]=e;p[9]=f;p[10]=g;p[11]=h;
		}else{//1,3,2: 2=3,3=2
//			this.swap(p,4,8);
			p[4]=i;p[5]=j;p[6]=k;p[7]=l;
			p[8]=e;p[9]=f;p[10]=g;p[11]=h;
		}
	}
}
Surface.prototype.fillTriangle=function(p,pid){
	 var id=this.getPolygonId(p[0],p[1]);	 if(id!=null && id==this.getPolygonId(p[4],p[5]) && id==this.getPolygonId(p[8],p[9])){//	   var maxz=Math.max(p[2],p[6],p[10]);
	   var maxz = p[2] > p[6] ? (p[2] > p[10] ? p[2] : p[10]) : (p[6] > p[10] ? p[6] : p[10]);	   if(maxz>this.getZ(p[0],p[1]) && maxz>this.getZ(p[4],p[5]) && maxz>this.getZ(p[8],p[9])){
	  	 return;	   }	 }	var yd1=p[5]-p[1];
	var yd2=p[9]-p[1];
	var d=yd1/yd2;
	var x2=p[0]+(p[8]-p[0])*d;
	var z2=p[2]+(p[10]-p[2])*d;
	var cr2=getRed(p[3])+(getRed(p[11])-getRed(p[3]))*d;
	var cg2=getGrn(p[3])+(getGrn(p[11])-getGrn(p[3]))*d;
	var cb2=getBlu(p[3])+(getBlu(p[11])-getBlu(p[3]))*d;
	
	this.fillTrianglePart(pid,  p[1],p[5] ,p[0],p[0],p[4],x2  ,p[2],p[2],p[6],z2    ,getRed(p[3]),getRed(p[3]),getRed(p[7]),cr2,   getGrn(p[3]),getGrn(p[3]),getGrn(p[7]),cg2,   getBlu(p[3]),getBlu(p[3]),getBlu(p[7]),cb2   );
	this.fillTrianglePart(pid, p[5],p[9] ,p[4],x2,p[8],p[8]  ,p[6],z2,p[10],p[10]  ,getRed(p[7]),cr2,getRed(p[11]),getRed(p[11]), getGrn(p[7]),cg2,getGrn(p[11]),getGrn(p[11]), getBlu(p[7]),cb2,getBlu(p[11]),getBlu(p[11]) );
}

function getRed(c1){return (c1 & 0xff0000)>>16;}
function getGrn(c1){return (c1 & 0x00ff00)>>8;}
function getBlu(c1){return (c1 & 0x0000ff);}


Surface.prototype.rotate=function(data,offset){
  var x=data[offset];
  var y=data[offset+1];
  var z=data[offset+2];
  var t;
  if(this.yRot){
    t=z;
    z=z*(this.yRotCos)-x*(this.yRotSin);
    x=t*(this.yRotSin)+x*(this.yRotCos);
  }
  if(this.zRot){
    t=x;
    x=x*(this.zRotCos)-y*(this.zRotSin);
    y=t*(this.zRotSin)+y*(this.zRotCos);
  }
  if(this.xRot){
    t=z;
    z=z*(this.xRotCos)-y*(this.xRotSin);
    y=t*(this.xRotSin)+y*(this.xRotCos);
    }
  data[offset]=x;
  data[offset+1]=y;
  data[offset+2]=z;
}

function rotate(data,offset,xRot,yRot,zRot){
  var rotScale=Math.PI/180;
  xRot=xRot%360;
  xRot=xRot*rotScale;
  var xRotCos=Math.cos(xRot);
  var xRotSin=Math.sin(xRot);
  yRot=yRot%360;
  yRot=yRot*rotScale;
  var yRotCos=Math.cos(yRot);
  var yRotSin=Math.sin(yRot);
  zRot=zRot%360;
  zRot=zRot*rotScale;
  var zRotCos=Math.cos(zRot);
  var zRotSin=Math.sin(zRot);
  
  var x=data[offset];
  var y=data[offset+1];
  var z=data[offset+2];
  var t;
  if(yRot){
    t=z;
    z=z*(yRotCos)-x*(yRotSin);
    x=t*(yRotSin)+x*(yRotCos);
  }
  if(zRot){
    t=x;
    x=x*(zRotCos)-y*(zRotSin);
    y=t*(zRotSin)+y*(zRotCos);
  }
  if(xRot){
    t=z;
    z=z*(xRotCos)-y*(xRotSin);
    y=t*(xRotSin)+y*(xRotCos);
  }
  data[offset]=x;
  data[offset+1]=y;
  data[offset+2]=z;
}


Surface.prototype.getRotationX=function(){
  return this.xRot / this.rotScale;
}
Surface.prototype.getRotationY=function(){
  return this.yRot / this.rotScale;
}
Surface.prototype.getRotationZ=function(){
  return this.zRot / this.rotScale;
}

Surface.prototype.setRotationZ=function(zRot){
  zRot=between(zRot%360,this.minZRot,this.maxZRot);
  this.surfaceGL.zRot = zRot;
  this.zRot=zRot * this.rotScale;
  
  this.zRotCos=Math.cos(this.zRot);
  this.zRotSin=Math.sin(this.zRot);
  this.needsRepaint();
}
Surface.prototype.setRotationY=function(yRot){
  yRot=between(yRot%360,this.minYRot,this.maxYRot);
  this.surfaceGL.yRot = yRot;
  this.yRot=yRot * this.rotScale;
  this.yRotCos=Math.cos(this.yRot);
  this.yRotSin=Math.sin(this.yRot);
  this.needsRepaint();
}

function between(i,min,max){
	if(min!=null) i=Math.max(i,min);
	if(max!=null) i=Math.min(i,max);
	return i;
}
Surface.prototype.setRotationX=function(xRot){
  xRot=between(xRot%360,this.minXRot,this.maxXRot);
  this.surfaceGL.xRot = xRot;
  this.xRot=xRot * this.rotScale;
  this.xRotCos=Math.cos(this.xRot);
  this.xRotSin=Math.sin(this.xRot);
  this.needsRepaint();
}
Surface.prototype.setFov=function(fov){
  if(fov>this.fovMax)
	  fov=this.fovMax;
  if(fov<this.fovMin)
	  fov=this.fovMin;
  this.fov=fov;
  this.needsRepaint();
}
Surface.prototype.setZoom=function(zoom){
  if(zoom>this.zoomMax)
	  zoom=this.zoomMax;
  if(zoom<this.zoomMin)
	  zoom=this.zoomMin;
  this.zoom=zoom;
  this.surfaceGL.setZoom(this.zoom);
  this.needsRepaint();
}

Surface.prototype.getZoom=function(){
  return this.zoom;
}
Surface.prototype.getFov=function(){
  return this.fov;
}



