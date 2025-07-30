//################
//##### init #####

function wheel(event,passive){
    var delta = 0;
    if (!event) /* For IE. */
            event = window.event;
    if(event.consumed)
    	return;
    event.consumed=true;
    if (event.wheelDelta) { /* IE/Opera. */
            delta = event.wheelDelta/120;
    } else if (event.detail) { /* Mozilla case. */
            delta = -event.detail/3;
    }
    if (delta)
        fireOnMouseWheel(event,delta);
    if (passive!=true && event.preventDefault)
        event.preventDefault();
//    event.returnValue = false;
    return false;
};

var IS_LITTLE_ENDIAN;
{
  var buf=new ArrayBuffer(8);
  var data = new Uint32Array(buf);
  //Determine whether Uint32 is little- or big-endian.
  data[1] = 0x0a0b0c0d;
  var isLittleEndian = true;
  if (buf[4] === 0x0a && buf[5] === 0x0b && buf[6] === 0x0c && buf[7] === 0x0d) {
    IS_LITTLE_ENDIAN=false;
  }else
    IS_LITTLE_ENDIAN=true;
}

var SVG_PREFIX='url(\'data:image/svg+xml;utf8,<svg version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" ';
var SVG_SUFFIX='</svg>\')';
var COLORS = {"aliceblue":"#f0f8ff","antiquewhite":"#faebd7","aqua":"#00ffff","aquamarine":"#7fffd4","azure":"#f0ffff","beige":"#f5f5dc","bisque":"#ffe4c4","black":"#000000","blanchedalmond":"#ffebcd",
              "blue":"#0000ff","blueviolet":"#8a2be2","brown":"#a52a2a","burlywood":"#deb887","cadetblue":"#5f9ea0","chartreuse":"#7fff00","chocolate":"#d2691e","coral":"#ff7f50","cornflowerblue":"#6495ed",
              "cornsilk":"#fff8dc","crimson":"#dc143c","cyan":"#00ffff","darkblue":"#00008b","darkcyan":"#008b8b","darkgoldenrod":"#b8860b","darkgray":"#a9a9a9","darkgreen":"#006400","darkkhaki":"#bdb76b",
              "darkmagenta":"#8b008b","darkolivegreen":"#556b2f","darkorange":"#ff8c00","darkorchid":"#9932cc","darkred":"#8b0000","darksalmon":"#e9967a","darkseagreen":"#8fbc8f","darkslateblue":"#483d8b",
              "darkslategray":"#2f4f4f","darkturquoise":"#00ced1","darkviolet":"#9400d3","deeppink":"#ff1493","deepskyblue":"#00bfff","dimgray":"#696969","dodgerblue":"#1e90ff","firebrick":"#b22222",
              "floralwhite":"#fffaf0","forestgreen":"#228b22","fuchsia":"#ff00ff", "gainsboro":"#dcdcdc","ghostwhite":"#f8f8ff","gold":"#ffd700","goldenrod":"#daa520","gray":"#808080","green":"#008000",
              "greenyellow":"#adff2f","honeydew":"#f0fff0","hotpink":"#ff69b4","indianred ":"#cd5c5c","indigo ":"#4b0082","ivory":"#fffff0","khaki":"#f0e68c","lavender":"#e6e6fa","lavenderblush":"#fff0f5",
              "lawngreen":"#7cfc00","lemonchiffon":"#fffacd","lightblue":"#add8e6","lightcoral":"#f08080","lightcyan":"#e0ffff","lightgoldenrodyellow":"#fafad2","lightgrey":"#d3d3d3","lightgreen":"#90ee90",
              "lightpink":"#ffb6c1","lightsalmon":"#ffa07a","lightseagreen":"#20b2aa","lightskyblue":"#87cefa","lightslategray":"#778899","lightsteelblue":"#b0c4de","lightyellow":"#ffffe0","lime":"#00ff00",
              "limegreen":"#32cd32","linen":"#faf0e6","magenta":"#ff00ff","maroon":"#800000","mediumaquamarine":"#66cdaa","mediumblue":"#0000cd","mediumorchid":"#ba55d3","mediumpurple":"#9370d8",
              "mediumseagreen":"#3cb371","mediumslateblue":"#7b68ee","mediumspringgreen":"#00fa9a","mediumturquoise":"#48d1cc","mediumvioletred":"#c71585","midnightblue":"#191970","mintcream":"#f5fffa",
              "mistyrose":"#ffe4e1","moccasin":"#ffe4b5","navajowhite":"#ffdead","navy":"#000080","oldlace":"#fdf5e6","olive":"#808000","olivedrab":"#6b8e23","orange":"#ffa500","orangered":"#ff4500",
              "orchid":"#da70d6","palegoldenrod":"#eee8aa","palegreen":"#98fb98","paleturquoise":"#afeeee","palevioletred":"#d87093","papayawhip":"#ffefd5","peachpuff":"#ffdab9","peru":"#cd853f",
              "pink":"#ffc0cb","plum":"#dda0dd","powderblue":"#b0e0e6","purple":"#800080","red":"#ff0000","rosybrown":"#bc8f8f","royalblue":"#4169e1","saddlebrown":"#8b4513","salmon":"#fa8072",
              "sandybrown":"#f4a460","seagreen":"#2e8b57","seashell":"#fff5ee","sienna":"#a0522d","silver":"#c0c0c0","skyblue":"#87ceeb","slateblue":"#6a5acd","slategray":"#708090","snow":"#fffafa",
              "springgreen":"#00ff7f","steelblue":"#4682b4","tan":"#d2b48c","teal":"#008080","thistle":"#d8bfd8","tomato":"#ff6347","turquoise":"#40e0d0","violet":"#ee82ee","wheat":"#f5deb3","white":"#ffffff",
              "whitesmoke":"#f5f5f5","yellow":"#ffff00","yellowgreen":"#9acd32"};
var SVG_ERROR_BANNER = '<svg id="Layer_1" data-name="Layer 1" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 400 150"><defs><style>.cls-2{fill:#f06b22;}.cls-3{fill:#1092bc;}.cls-4{fill:#7bcdf3;}.cls-5{fill:none;stroke:#545252;stroke-linecap:round;stroke-miterlimit:10;stroke-width:1.61px;}</style></defs>'
						+'<circle class="cls-1" cx="203.72" cy="74.38" r="71.56" /><g id="Page-1"><g id="Group"><rect id="Rectangle" class="cls-2" x="188.97" y="24.77" width="29.81" height="29.81" rx="6.62" transform="translate(3.3 93.6) rotate(-26.06)" /><rect id="Rectangle-2" class="cls-2" x="224.89" y="84.22" width="29.81" height="29.81" rx="6.62" transform="translate(-17.9 129.43) rotate(-29.17)" /><rect id="Rectangle-3" class="cls-3" x="205.61" y="54.79" width="29.81" height="29.81" rx="6.62" transform="translate(115.88 275.93) rotate(-80.64)" /><rect id="Rectangle-4" class="cls-4" x="165.73" y="51.96" width="29.81" height="29.81" rx="6.62" transform="translate(-10.73 44.95) rotate(-13.78)" /><rect id="Rectangle-5" class="cls-3" x="154.54" y="85.02" width="29.81" height="29.81" rx="6.62" transform="translate(10.33 215.59) rotate(-66.54)" /><rect id="Rectangle-6" class="cls-2" x="189.54" y="87.28" width="29.81" height="29.81" rx="6.62" transform="translate(-1.96 4.02) rotate(-1.12)" /></g></g>'
						+ '<line class="cls-5" x1="237.74" y1="72.5" x2="239.85" y2="66.47" /><line class="cls-5" x1="240.5" y1="74.83" x2="247.68" y2="69.92" /><line class="cls-5" x1="242.22" y1="78.37" x2="247.65" y2="77.51" /><line class="cls-5" x1="178.66" y1="48.87" x2="172.47" y2="47.33" /><line class="cls-5" x1="180.73" y1="45.9" x2="175.18" y2="39.22" /><line class="cls-5" x1="184.09" y1="43.87" x2="182.73" y2="38.54" />'
						+ '</svg>';
var SVG_LOGO_WHITE = '<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 575.35 153.35"><defs><style>.cls-1{fill:#fff;}</style></defs><g id="Layer_2" data-name="Layer 2"><g id="Layer_1-2" data-name="Layer 1"><g id="Page-1"><g id="Group"><g id="_3forge" data-name=" 3forge"><path class="cls-1" d="M222.89,80.74a46.76,46.76,0,0,1-3.08,17A34.63,34.63,0,0,1,206,114.85,40.64,40.64,0,0,1,183.27,121a40.36,40.36,0,0,1-22.79-6.42A36,36,0,0,1,146.54,97a49,49,0,0,1-2.73-13.49A2.58,2.58,0,0,1,146,80.54a2.81,2.81,0,0,1,.71,0h18.44a2.82,2.82,0,0,1,3.07,2.56,1.53,1.53,0,0,1,0,.37A26.76,26.76,0,0,0,169.93,91,13.22,13.22,0,0,0,175,97.87a13.82,13.82,0,0,0,8.28,2.48A13.15,13.15,0,0,0,196.41,92a28.68,28.68,0,0,0,2.37-12,30.23,30.23,0,0,0-2.72-13.14A13.24,13.24,0,0,0,182.92,59a15.13,15.13,0,0,0-7.83,3.24,4,4,0,0,1-1.72.5A3.1,3.1,0,0,1,171,61.59l-9-12.13a3.37,3.37,0,0,1-.71-2.07,3,3,0,0,1,1-2l26.63-23.24a.61.61,0,0,0,.35-.76c0-.31-.4-.46-.85-.46H149A2.56,2.56,0,0,1,146,18.74a2.41,2.41,0,0,1,0-.71V3A2.58,2.58,0,0,1,148.26.14a2.73,2.73,0,0,1,.7,0h69.48a2.59,2.59,0,0,1,2.93,2.18,2.73,2.73,0,0,1,0,.7V19.75a5.07,5.07,0,0,1-1.56,3.59L197.42,43.85c-.66.71-.45,1.17.71,1.37A28.38,28.38,0,0,1,219.45,62.8,45.05,45.05,0,0,1,222.89,80.74Z"/><path class="cls-1" d="M280.24,34.61v14.3a2.58,2.58,0,0,1-2.18,2.92,2.73,2.73,0,0,1-.7,0H261.8a.91.91,0,0,0-1,.79v64.14a2.53,2.53,0,0,1-3.08,2.88H239.61a2.53,2.53,0,0,1-2.88-2.12,2.42,2.42,0,0,1,0-.76V53.05a.91.91,0,0,0-.78-1,.87.87,0,0,0-.28,0h-9a2.58,2.58,0,0,1-2.89-2.23,2.35,2.35,0,0,1,0-.7V34.61a2.52,2.52,0,0,1,2.11-2.88,2.49,2.49,0,0,1,.77,0h9a1,1,0,0,0,1.06-.85.81.81,0,0,0,0-.22V27.08A34.57,34.57,0,0,1,240,10.6a18,18,0,0,1,10.51-8.33A58.21,58.21,0,0,1,270.69.14h5.61a2.58,2.58,0,0,1,2.92,2.18,2.35,2.35,0,0,1,0,.7V15.15A2.59,2.59,0,0,1,277,18a2.35,2.35,0,0,1-.7,0h-4.75a12.36,12.36,0,0,0-8.29,2.48A11.89,11.89,0,0,0,260.94,29v1.66a.91.91,0,0,0,.73,1.06.88.88,0,0,0,.28,0h15.56A2.51,2.51,0,0,1,280.25,34,2.41,2.41,0,0,1,280.24,34.61Z"/><path class="cls-1" d="M292.21,113.48a37.41,37.41,0,0,1-14-20.46,60.16,60.16,0,0,1-2.43-17.43,56.87,56.87,0,0,1,2.58-18.09,36.92,36.92,0,0,1,14.1-19.81,41.51,41.51,0,0,1,24.66-7.18,39.94,39.94,0,0,1,24,7.18A36.83,36.83,0,0,1,355,57.34a56.82,56.82,0,0,1,2.58,17.89,67.64,67.64,0,0,1-2.22,17.28A38.26,38.26,0,0,1,317,121,40.42,40.42,0,0,1,292.21,113.48ZM326.37,97a18.66,18.66,0,0,0,5.61-9.4,43.59,43.59,0,0,0,1.52-12,46.29,46.29,0,0,0-1.37-12.13,17.8,17.8,0,0,0-5.61-9.05,14.7,14.7,0,0,0-9.75-3.23,14.53,14.53,0,0,0-9.55,3.23,18.24,18.24,0,0,0-5.66,9.05,46.71,46.71,0,0,0-1.36,12.13,45.55,45.55,0,0,0,1.36,12,18.87,18.87,0,0,0,5.76,9.4,14.5,14.5,0,0,0,9.81,3.39A13.86,13.86,0,0,0,326.37,97Z"/><path class="cls-1" d="M415,32.74a2.88,2.88,0,0,1,1.57,3.58l-3.24,17.94a2,2,0,0,1-1,1.87,4.92,4.92,0,0,1-2.47,0,21.07,21.07,0,0,0-5.05-.71,19,19,0,0,0-3.95.36,16.94,16.94,0,0,0-10.41,4.55A14,14,0,0,0,386.2,71v45.78a2.57,2.57,0,0,1-2.22,2.88,2.81,2.81,0,0,1-.71,0H365.13a2.59,2.59,0,0,1-2.93-2.18,2.73,2.73,0,0,1,0-.7v-82a2.58,2.58,0,0,1,2.16-2.93,2.49,2.49,0,0,1,.77,0h18.24A2.58,2.58,0,0,1,386.3,34a2.49,2.49,0,0,1,0,.77v5c0,.46,0,.76.35.86s.51,0,.81-.35a21.38,21.38,0,0,1,18.45-10.11A18,18,0,0,1,415,32.74Z"/><path class="cls-1" d="M469.27,31.88h18.29a2.57,2.57,0,0,1,2.89,2.22,2.41,2.41,0,0,1,0,.71v77q0,22.38-12.78,31.88a53.48,53.48,0,0,1-33,9.6,111.5,111.5,0,0,1-12.78-.86c-1.72,0-2.58-1.16-2.58-3.08l.71-15.87a2.79,2.79,0,0,1,.91-2.17,2.46,2.46,0,0,1,2.32-.4,82.54,82.54,0,0,0,10.41.85,26.11,26.11,0,0,0,16.83-4.8,18.59,18.59,0,0,0,5.91-15.15c0-.36,0-.56-.35-.61s-.51,0-.86.45a24.38,24.38,0,0,1-18.8,6.83,40.14,40.14,0,0,1-20.21-5.36A29.8,29.8,0,0,1,413,95.8a67,67,0,0,1-2.58-20.21,63.52,63.52,0,0,1,3.09-21.68,33.92,33.92,0,0,1,12.12-16.73,32.41,32.41,0,0,1,19.81-6.31,26,26,0,0,1,20.21,7.68c.36.35.61.45.86.35a.92.92,0,0,0,.36-.86V34.81A2.57,2.57,0,0,1,469,31.9Zm-2.88,43.2q0-4.1-.35-8.19a30.74,30.74,0,0,0-1.16-5.45A15.15,15.15,0,0,0,459.82,54a14.07,14.07,0,0,0-8.89-2.83A13.72,13.72,0,0,0,442.14,54a16,16,0,0,0-5.06,7.43,32.66,32.66,0,0,0-2.52,13.84,33,33,0,0,0,2,13.7A14.84,14.84,0,0,0,451.08,99a13.9,13.9,0,0,0,13.8-9.7A56.93,56.93,0,0,0,466.39,75.08Z"/><path class="cls-1" d="M556.33,92.82a3.16,3.16,0,0,1,2.23-1.17,2.61,2.61,0,0,1,1.92.86l9.7,9.4a2.8,2.8,0,0,1,1,2,3.26,3.26,0,0,1-.66,2.07,39,39,0,0,1-14.45,11,46.17,46.17,0,0,1-19.2,4,41.05,41.05,0,0,1-26-8,39.51,39.51,0,0,1-13.95-22,59.87,59.87,0,0,1-2-15.87,63.74,63.74,0,0,1,1.87-16.73A36.38,36.38,0,0,1,510.5,38.14,39.72,39.72,0,0,1,535,30.46a35.36,35.36,0,0,1,26.53,10.11,49.15,49.15,0,0,1,12.73,27.79c.46,3.79.81,7.93,1.06,12.48a2.62,2.62,0,0,1-2.28,2.93,2.36,2.36,0,0,1-.65,0H520c-.7,0-1.06.3-1.06,1a26.72,26.72,0,0,0,1.21,5.31,13.88,13.88,0,0,0,6.78,8.08A24.54,24.54,0,0,0,539.1,101,22.28,22.28,0,0,0,556.33,92.82ZM525.51,54a13.37,13.37,0,0,0-5,7.43c-.56,2.17-.91,3.58-1,4.29s0,1,.81,1h29c.56,0,.86,0,.86-.65a12.36,12.36,0,0,0-.71-4,14.4,14.4,0,0,0-14.85-10.76A15.1,15.1,0,0,0,525.51,54Z"/></g><rect id="Rectangle" class="cls-1" y="1.41" width="35.27" height="35.27" rx="7.83"/><rect id="Rectangle-2" class="cls-1" x="43.15" y="1.41" width="35.27" height="35.27" rx="7.83"/><rect id="Rectangle-3" class="cls-1" y="44.56" width="35.27" height="35.27" rx="7.83"/><rect id="Rectangle-4" class="cls-1" y="87.66" width="35.27" height="35.27" rx="7.83"/><rect id="Rectangle-5" class="cls-1" x="43.15" y="44.56" width="35.27" height="35.27" rx="7.83"/><rect id="Rectangle-6" class="cls-1" x="86.25" y="1.41" width="35.27" height="35.27" rx="7.83"/></g></g></g></g></svg>';

var SVG_LOADING_ANIMATION_color1 = '<svg xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" style="margin: auto; background: none; display: block; shape-rendering: auto; transform: scale(1.5);" width="75px" height="75px" viewBox="0 0 100 100" preserveAspectRatio="xMidYMid"><rect x="17.5" y="30" width="15" height="40" fill=';

var SVG_LOADING_ANIMATION_color2 = '> <animate attributeName="y" repeatCount="indefinite" dur="1s" calcMode="spline" keyTimes="0;0.5;1" values="16;30;30" keySplines="0 0.5 0.5 1;0 0.5 0.5 1" begin="-0.2s"></animate> <animate attributeName="height" repeatCount="indefinite" dur="1s" calcMode="spline" keyTimes="0;0.5;1" values="68;40;40" keySplines="0 0.5 0.5 1;0 0.5 0.5 1" begin="-0.2s"></animate> </rect> <rect x="42.5" y="30" width="15" height="40" fill=';
	
var SVG_LOADING_ANIMATION_color3 = '> <animate attributeName="y" repeatCount="indefinite" dur="1s" calcMode="spline" keyTimes="0;0.5;1" values="19.499999999999996;30;30" keySplines="0 0.5 0.5 1;0 0.5 0.5 1" begin="-0.1s"></animate> <animate attributeName="height" repeatCount="indefinite" dur="1s" calcMode="spline" keyTimes="0;0.5;1" values="61.00000000000001;40;40" keySplines="0 0.5 0.5 1;0 0.5 0.5 1" begin="-0.1s"></animate> </rect><rect x="67.5" y="30" width="15" height="40" fill='; 

var SVG_LOADING_ANIMATION_end = '> <animate attributeName="y" repeatCount="indefinite" dur="1s" calcMode="spline" keyTimes="0;0.5;1" values="19.499999999999996;30;30" keySplines="0 0.5 0.5 1;0 0.5 0.5 1"></animate> <animate attributeName="height" repeatCount="indefinite" dur="1s" calcMode="spline" keyTimes="0;0.5;1" values="61.00000000000001;40;40" keySplines="0 0.5 0.5 1;0 0.5 0.5 1"></animate> </rect> </svg>'; 

const weekdayNames = ["Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"];
const dayNames=['Su','Mo','Tu','We','Th','Fr','Sa']; 
const monthNames=['January','February','March','April','May','June','July','August','September','October','November','December']; 
const monthNamesShortened=['Jan','Feb','Mar','Apr','May','Jun','Jul','Aug','Sep','Oct','Nov','Dec']; 

fireOnMouseWheel=function(e,delta){
  var point = getMousePoint(e);
  let doc = document;
  if(e.target){
	  doc = getWindow(e.target).document;
  }
  var element=doc.elementFromPoint(point.x,point.y);
  while(element!=null && element!=document && element!=document.body){
      if(element.onMouseWheel){
    	  element.onMouseWheel(e,delta);
    	  return false;
      }
  	element=element.parentNode;
  }
  return true;
};


function initUtils(){
  window.HIDDEN_IFRAME=nw('iframe');
  window.HIDDEN_IFRAME.style.display='none';
  window.HIDDEN_IFRAME.style.width='0px';
  window.HIDDEN_IFRAME.style.height='0px';
  window.HIDDEN_IFRAME.id='HIDDEN_IFRAME';
  window.HIDDEN_IFRAME.name='HIDDEN_IFRAME';
  document.body.appendChild(window.HIDDEN_IFRAME);
  document.oncontextmenu='return false;';
  document.addEventListener('mousemove', onMouseMove, false);
  document.onkeydown=function(event){
    if(event.keyCode===8){
      var tgt=getMouseTarget(event);
      if(isInput(tgt) || tgt.className=="customTextArea")
    	  return;
      event.preventDefault();
    }else if(event.ctrlKey && event.keyCode==82){
      event.preventDefault();
      event.stopPropagation();
    }
  };


  //if (window.addEventListener){
        /** DOMMouseScroll is for mozilla. */
      //var eventType = (navigator.userAgent.indexOf('Firefox') !=-1) ? "DOMMouseScroll" : "mousewheel";            
      //window.addEventListener(eventType, wheel, true);*
  //}
  /** IE/Opera. */
  //window.onmousewheel = document.onmousewheel = wheel;
  
};

function buildSVGLoadAnimation(color1, color2, color3) {
	SVG_LOADING_ANIMATION = SVG_LOADING_ANIMATION_color1 + (color1?JSON.stringify(color1):"#77cefa") + SVG_LOADING_ANIMATION_color2 + (color2?JSON.stringify(color2):"#f16900") + SVG_LOADING_ANIMATION_color3 + (color3?JSON.stringify(color3):"#0e91bb") + SVG_LOADING_ANIMATION_end;
}

function isInput(tgt){
	  if(tgt==null)
		  return false;
      var tn=tgt.tagName;
      if(tn==='INPUT'){
    	var type=tgt.type;
    	if(type!=null){
    	  type=type.toUpperCase();
    	  if(type==='TEXT' || type==='PASSWORD' || type==='FILE' || type==='EMAIL' || type==='SEARCH' || type==='DATE')
    	    return true;
    	}
      }else if(tn==='TEXTAREA')
        return true;
      else if(tn==='COMBO-BOX')
    	return true;
      return false;
}


var MOUSE_POSITION_X=-1;

var MOUSE_POSITION_Y=-1;
var MOUSE_WINDOW=null;
var USER_DRIVEN_PENDING_TASKS=[];

function onMouseMove(e){
	MOUSE_WINDOW=e.view;
	if(USER_DRIVEN_PENDING_TASKS.length>0 && e.button>0){
		for(var i in USER_DRIVEN_PENDING_TASKS){
			USER_DRIVEN_PENDING_TASKS[i]();
		}
		USER_DRIVEN_PENDING_TASKS=[];
	}
    MOUSE_POSITION_X = e.clientX;
    MOUSE_POSITION_Y = e.clientY;
}

function addUserPendingTask(func){
	USER_DRIVEN_PENDING_TASKS[USER_DRIVEN_PENDING_TASKS.length]=func;
}

function getDocumentHeight(window) {
    var d = window.document;
    var r= Math.max( d.body.scrollHeight, d.documentElement.scrollHeight, d.body.offsetHeight, d.documentElement.offsetHeight, d.body.clientHeight, d.documentElement.clientHeight);
    return r;
}
function getDocumentWidth(window) {
    var body=window.document.getElementsByTagName('body')[0];
    return new Rect().readFromElement(body).width;
}

function isChildOf(parent,child){
	while(child!=null){
		if(child==parent)
			return true;
		child=child.parentNode;
	}
	return false;
	
}

//#######################
//##### inheritance #####


//Function.prototype.method = function (name, func) {
    //this.prototype[name] = func;
    //return this;
//};

Function.prototype.inherits = function (parent) {
    var d = {}, p = (this.prototype = new parent());
    this.prototype.uber=function uber(name) {
        if (!(name in d)) {
            d[name] = 0;
        }        
        var f, r, t = d[name], v = parent.prototype;
        if (t) {
            while (t) {
                v = v.constructor.prototype;
                t -= 1;
            }
            f = v[name];
        } else {
            f = p[name];
            if (f == this[name]) {
                f = v[name];
            }
        }
        d[name] += 1;
        r = f.apply(this, Array.prototype.slice.apply(arguments, [1]));
        d[name] -= 1;
        return r;
    };
    return this;
};

//################
//##### Text #####

function joinMap(delim,eq,map){
  if(map.length==0)
    return '';
  var r='';
  var first=true;
  for(var i in map){
     if(first)
       first=false;
     else
       r+=delim;
     r+=i+eq+map[i];
  }
  return r;
};
function joinAndEncodeMap(delim,eq,map){
  if(map.length==0)
    return '';
  var r='';
  var first=true;
  for(var i in map){
	  var val=map[i];
	  if(val!=null){
       if(first)
         first=false;
       else
         r+=delim;
       r+=encodeURIComponent(i)+eq;
       if(typeof val === 'object'){
         r+=encodeURIComponent(JSON.stringify(val));
       }else{
         r+=encodeURIComponent(val);
	   }
	 }
  }
  return r;
};


//Diffs two strings
//Arguments: two strings 
//	The order of the string matters
//Returns the positions where the characters start to differ from the beginning and from the end of the left string
//		and the substring of the right string bounded by the positions where the right string begins to differ from the left string
//The start (s) is inclusive
//The end (e) is exclusive
//The substring is denoted as (c)
//Example: strDiff("Mount Everst", "Mount Everest")
//returns: {s:10, e:10, c:"e"}
//Example2: strDiff("Mount Everest", "Mount Everst")
//returns: {s:10, e:11, c:""}

function strDiff(lString, rString){
	if(lString == null)
		return {c:rString};
	var rLen = rString.length;
	var lLen = lString.length;
	var len = rLen < lLen ? rLen : lLen;
	var start = 0;
	var endOffset = 0;
	for(; start < len; start++){
		if(lString[start] != rString[start])
			break;
	}
	len = len-start;
	for(;endOffset < len; ++endOffset){
		if(lString[lLen-1-endOffset] != rString[rLen-1-endOffset])
			break;
	}
	return {s:start, e:lLen-endOffset , c:rString.substring(start, rLen-endOffset)};
}

//#################
//##### Touch #####

function getTouchTarget(touchEvent){
var mouseEvent = getMouseEvent(touchEvent);
if(touchEvent.touchTarget!=null)
	  return touchEvent.touchTarget;
return touchEvent.target ? touchEvent.target : touchEvent.srcElement;
};

function getTouchEvent(touchEvent){
if(touchEvent!=null) return touchEvent;
return event;
};

function getTouchLayerPoint(touchEvent){
	// falls back to layerX and layerY in firefox.
	if (touchEvent != null) {
		var offX = touchEvent.offsetX || touchEvent.layerX;
		var offY = touchEvent.offsetY || touchEvent.layerY;
		return new Point(offX, offY);
	}
	var offX = event.offsetX || event.layerX;
	var offY = event.offsetY || event.layerY;
	return new Point(offX,offY);
};

//#################
//##### Mouse #####

function getMouseTarget(mouseEvent){
  var mouseEvent = getMouseEvent(mouseEvent);
  if(mouseEvent.touchTarget!=null)
	  return mouseEvent.touchTarget;
  return mouseEvent.target ? mouseEvent.target : mouseEvent.srcElement;
};
function getMouseRelatedTarget(mouseEvent){
  var mouseEvent = getMouseEvent(mouseEvent);
  return mouseEvent.toElement || mouseEvent.relatedTarget;
};

//left=1,right=2,middle=3
function getMouseButton(mouseEvent){
  var mouseEvent = getMouseEvent(mouseEvent);
  if(mouseEvent.which==2)
	  return 3;
  if(mouseEvent.which==3)
	  return 2;
  return mouseEvent.button ? mouseEvent.button : mouseEvent.which;
};

function getMouseEvent(mouseEvent){
  if(mouseEvent!=null) return mouseEvent;
  return event;
};

var lastTouch;
function getMousePoint(mouseEvent){
  if(mouseEvent){
	if(mouseEvent.targetTouches!=null){
	  if(mouseEvent.targetTouches.length>0)
	      lastTouch=mouseEvent.targetTouches[0];
      return new Point(rd(lastTouch.pageX),rd(lastTouch.pageY));
	}
    return new Point(rd(mouseEvent.pageX),rd(mouseEvent.pageY));
  }
  return new Point(rd(event.clientX),rd(event.clientY));
};

function getMouseLayerPoint(mouseEvent){
	// falls back to layerX and layerY in firefox.
	if (mouseEvent != null) {
		var offX = mouseEvent.offsetX || mouseEvent.layerX;
		var offY = mouseEvent.offsetY || mouseEvent.layerY;
		return new Point(offX, offY);
	}
	var offX = event.offsetX || event.layerX;
	var offY = event.offsetY || event.layerY;
	return new Point(offX,offY);
};

function getMousePointRelativeTo(mouseEvent,div){
  var r=getMousePoint(mouseEvent);
  var t=new Rect().readFromElementRelatedToWindow(div);
  r.x-=t.getLeft();
  r.y-=t.getTop();
  return r;
};

function isMouseInside(mouseEvent,element,padding){
  var point=getMousePoint(mouseEvent);
  var rect=new Rect();
  rect.readFromElement(element);
  if(padding==null)
    return rect.pointInside(point);
  else
    return rect.pointInside(point,padding);
};


//################
//##### Math #####

function max(a,b){
   return a>b ? a : b;
};
function abs(a){
   return a<0 ? -a : a;
};

function min(a,b){
   return a<b ? a : b;
};
var PXP={};
for(var i=-1000;i<5000;i++){
  var str=i+"px";
  PXP[i]=str;
  PXP[""+i]=str;
}

function toPx(px){
  var r=PXP[px];
  if(r!=null)
	  return r;
  if(px==null || Number.isNaN(px)) return null;
  if(typeof px == 'number'){
    var px=Math.floor(px+.5);
    var r=PXP[px];
    if(r!=null)
	    return r;
  }
   return px+"px";
};
function fromPx(px){
   return px.substr(0,px.length-2);
};

//missleading function name, clips
function between(val,min,max){
  if(min<max)
    return val<min ? min : val > max ? max : val;
  else
    return val<max ? max : val > min ? min : val;
};

//inclusive
function isBetween(val,min,max){
	if(min>max)
	  return val>=max && val<=min;
	else
	  return val>=min && val<=max;
};

function isBetweenExclusive(val,min,max) {
	if(min>max)
	  return val>max && val<min;
	else
	  return val>min && val<max;
	
}

//#################
//##### Point #####

function Point(x,y){
  if(x!=null && x.x!=null){
	  this.x=x.x;
	  this.y=x.y;
  }else{
    this.x=x;
    this.y=y;
  }
};

Point.prototype.x;
Point.prototype.y;

Point.prototype.getX = function(){
  return this.x;
};

Point.prototype.getY = function(){
  return this.y;
};

Point.prototype.move = function(x,y){
  this.x+=x;
  this.y+=y;
  return this;
};

Point.prototype.clone=function(){
   return new Point(this.x,this.y);
};

Point.prototype.toString = function(){
  return "{'x':"+this.x+",'y':"+this.y+"}";
};

Point.prototype.equals=function(point){
  return this.x == point.x && this.y == point.y;
};


//###################
//##### Element #####


function getElement(id){
  return document.getElementById(id);
};

function getElementOrThrow(id){
  r= document.getElementById(id);
  if(r)
      return r;
  alert("Element not found:"+id);
};

function nw(type,className){
  var r=document.createElement(type);
  if(className!=null)
	  r.className=className;
  return r;
};

function nw2(type,className,element) {
	var r;
	if (element != null && getWindow(element) != null)
		r=getWindow(element).document.createElement(type);
	else
		r=document.createElement(type);
	if(className!=null)
		r.className=className;
	return r;
};

function nw1(type,className){
	 var frame=getHiddenIFrame();
	  var r=frame.document.createElement(type);
	  if(className!=null)
		  r.className=className;
	  return r;
	};
	
function ensureInDiv(inner,outer){
   var innerRect=new Rect().readFromElement(inner);
   var outerRect=new Rect().readFromElement(outer);
   if(outerRect.rectInside(innerRect))
	   return;
   //log();
   //log(innerRect);
   innerRect.ensureInsideRect(outerRect);
   innerRect.writeToElementRelatedToWindow(inner);
   //log(innerRect);
   //log(outerRect);
}
function ensureInWindow(element){
   var divPos=new Rect();
   var divParent=element.parentNode;
   divPos.readFromElement(element);
   divParent.removeChild(element);
   var bodyPos=new Rect();
   var body=getWindow(element).document.getElementsByTagName('body')[0];
   bodyPos.readFromElement(body);
   bodyPos.height=getDocumentHeight(getWindow(element));
   divParent.appendChild(element);
   if(divPos.left<0){
     divPos.left=0;
     element.style.left=toPx(0);
   }
   if(divPos.top<0){
     divPos.top=0;
     element.style.top=toPx(0);
     element.style.bottom=null;
   }
   var h=bodyPos.getRight()-divPos.getRight()-10 ;
   if(h<0)
     divPos.left+=h;
   var v=bodyPos.getBottom()-divPos.getBottom()-10 ;

   if(v<0)
     divPos.top+=v;
   if(h<0 || v<0){
     element.style.left=toPx(divPos.left);
     element.style.top=toPx(divPos.top);
   }
   if(divPos.height > bodyPos.height){
	   element.style.height=toPx(bodyPos.height - 8);
	   element.style.overflowY="scroll";
	   element.style.top="4px";
   }
};

function containInWindow(element){
   var divPos=new Rect();
   var divParent=element.parentNode;
   divPos.readFromElement(element);
   divParent.removeChild(element);
   var bodyPos=new Rect();
   var body=getWindow(element).document.getElementsByTagName('body')[0];
   bodyPos.readFromElement(body);
   bodyPos.height=getDocumentHeight(getWindow(element));
   divParent.appendChild(element);
   var newHeight = divPos.height;
   var newWidth = divPos.width;
   if(divPos.left<0){
     newWidth + divPos.left;
     divPos.left=0;
   }
   if(divPos.top<0){
	 newHeight + divPos.top;
     divPos.top=0;
   }
   var h=bodyPos.getRight()-divPos.getRight()-10 ;
   if(h<0)
	 newWidth += h;
   var v=bodyPos.getBottom()-divPos.getBottom()-10 ;
   if(v<0)
	 newHeight += v;
   element.style.width = toPx(newWidth);
   element.style.height = toPx(newHeight);
   if(divPos.height > bodyPos.height){
	   element.style.height=toPx(newHeight- 8);
	   element.style.overflowY="scroll";
   }
   if(divPos.height > newHeight){
	   element.style.width=toPx(newWidth + 17);
   }
};

//################
//##### Rect ##### 

function Rect(left,top,width,height){
  this.left=left;
  this.top=top;
  this.width=width;
  this.height=height;
};


Rect.prototype.left;
Rect.prototype.top;
Rect.prototype.width;
Rect.prototype.height;

Rect.prototype.getLeft = function(){
  return this.left;
};

Rect.prototype.getTop = function(){
  return this.top;
};

Rect.prototype.getBottom = function(){
  return this.top+this.height;
};

Rect.prototype.getRight = function(){
  return this.left+this.width;
};

Rect.prototype.setLeft = function(left){
  this.left=left;
};

Rect.prototype.setTop = function(top){
  this.top=top;
};
Rect.prototype.setRight = function(right){
  this.left=right-this.width;
};
Rect.prototype.setBottom = function(bottom){
  this.top=bottom-this.height;
};

Rect.prototype.setTop = function(top){
  this.top=top;
};

Rect.prototype.setWidth = function(width){
  this.width=width;
};

Rect.prototype.setHeight = function(height){
  this.height=height;
  if(height<0)
	  throw "invalid height: "+height;
};

Rect.prototype.clone=function(){
   return new Rect(this.left,this.top,this.width,this.height);
};

Rect.prototype.toString = function(){
  return "{'left':"+this.left+",'top':"+this.top+",'width':"+this.width+",'height':"+this.height+"}";
};

Rect.prototype.equals=function(rect){
  return this.left == rect.left && this.top == rect.top && this.width == rect.width && this.height == rect.height;
};
Rect.prototype.move=function(x,y){
  this.left+=x;
  this.top+=y;
  return this;
};

Rect.prototype.grow=function(width,height){
  this.width+=width;
  this.height+=height;
  return this;
};

Rect.prototype.expand=function(width,height){
  if(height==null)
  height=width;
  this.left-=width;
  this.top-=height;
  this.width+=width*2;
  this.height+=height*2;
  return this;
};


Rect.prototype.clipY=function(y,padding){
  return between(y,this.top-padding,this.getBottom()+padding);
};

Rect.prototype.clipX=function(x,padding){
  return between(x,this.left-padding,this.getRight()+padding);
};

Rect.prototype.inside = function(x,y) {
  return x>=this.left && y>=this.top && x<this.getRight() && y<this.getBottom();
};
Rect.prototype.inside = function(x,y,padding) {
  return x>=this.left-padding && y>=this.top-padding && x<this.getRight()+padding && y<this.getBottom()+padding;
};

Rect.prototype.pointInside = function(p,padding) {
  if(padding==null)
	  padding=0;
  return this.inside(p.x,p.y,padding);
};


Rect.prototype.rectInsidePadded = function(rect,padding) {
  this.expand( padding );
  var r=this.pointInside(rect.getUpperLeft()) && this.pointInside(rect.getLowerRight());
  this.expand( -padding );
  return r;
};

Rect.prototype.rectInside = function(rect) {
  var r=this.inside(rect.getUpperLeft()) && this.inside(rect.getLowerRight());
  return r;
};
Rect.prototype.ensureInsideRect = function(outer) {
  var r=true;
  if(this.getLeft()<outer.getLeft()){
	  this.setLeft(outer.getLeft());
  }
  if(this.getTop()<outer.getTop()){
	  this.setTop(outer.getTop());
  }
  if(this.getRight()>outer.getRight()){
	  this.setRight(outer.getRight());
	  r=r && this.getLeft()<outer.getLeft();
  }
  if(this.getBottom()>outer.getBottom()){
	  this.setBottom(outer.getBottom());
	  r=r && this.getTop()<outer.getTop();
  }
  return r;
};

Rect.prototype.insidePadded = function(x,y,padding) {
  this.expand( padding );
  var r=this.inside( x , y );
  this.expand( -padding );
  return r;
};

Rect.prototype.readFromElementRelatedToParent = function(element) {
  this.left = element.offsetLeft;
  this.top = element.offsetTop;
  this.width = element.offsetWidth;
  this.height = element.offsetHeight;
  return this;
};
Rect.prototype.readFromElementRelatedToWindow = function(element) {
  this.left = 0;
  this.top = 0;
  this.width = element.offsetWidth;
  this.height = element.offsetHeight;
  while (element) {
    this.left += element.offsetLeft;
    this.top += element.offsetTop;
    element = element.offsetParent;
  }
  return this;
};

function getAbsoluteLeft(element){
	var r= 0;
  while (element) {
    r += element.offsetLeft;
    element = element.offsetParent;
  }
  return r;
}
function getAbsoluteTop(element){
  var r= 0;
  while (element) {
      r += element.offsetTop;
      element = element.offsetParent;
  }
  return r;
}

//deprecated
Rect.prototype.readFromElement = function(element) {
	return this.readFromElementRelatedToWindow(element);
};
//deprecated
Rect.prototype.writeToElement=function(elem){
   return this.writeToElementRelatedToParent(elem);
}

Rect.prototype.writeToElementRelatedToWindow=function(elem){
   //elem.style.width=toPx(this.width);
   //elem.style.height=toPx(this.height);
   
  var left = this.left;
  var top = this.top;
  //this.width = elem.offsetWidth;
  //this.height = elem.offsetHeight;
  var element=elem.offsetParent;
  while (element) {
    left -= element.offsetLeft;
    top -= element.offsetTop;
    element = element.offsetParent;
  }
  elem.style.left=toPx(left);
  elem.style.top=toPx(top);
  return this;
}


Rect.prototype.writeToElementRelatedToParent=function(elem){
   elem.style.left=toPx(this.left);
   elem.style.top=toPx(this.top);
   elem.style.width=toPx(this.width);
   elem.style.height=toPx(this.height);
   return this;
};

Rect.prototype.getLowerRight=function(){
  return new Point(this.getRight(),this.getBottom());
};

Rect.prototype.getLowerLeft=function(){
  return new Point(this.getLeft(),this.getBottom());
};

Rect.prototype.getUpperRight=function(){
  return new Point(this.getRight(),this.getTop());
};

Rect.prototype.getUpperLeft=function(){
  return new Point(this.getLeft(),this.getTop());
};

Rect.prototype.setMidpoint=function(p){
  this.left=abs(p.x-this.width/2);
  this.top=abs(p.y-this.height/2);
  return this;
};
Rect.prototype.getMidpoint=function(){
  return new Point(this.left+this.width/2,this.top+this.height/2);
};

Rect.prototype.setLocation = function(left,top,width,height){
  this.left=left;
  this.top=top;
  this.width=width;
  this.height=height;
  return this;
};

function getHiddenIFrame(){
  return window.HIDDEN_IFRAME;
};

function generateTicket()
{
    var text = "JT-";
    var possible = "BCDFGHJKMNPQRSTVWXZ23456789";
    for( var i=0; i < 5; i++ )
        text += possible.charAt(Math.floor(Math.random() * possible.length));
    return text;
}


//################
//##### Ajax #####

function onAjaxError(o){
	var dialog = document.getElementById("alert_dialog");
	if (!dialog)
		alertDialog("It appears the web server is not responding.<BR>Please refresh to try again (press F5)");
}


function ajaxAndEval(url,isPost,isAsync,params){
  var callback= function(resp,status) { 
    if(status==200){
       var code = decompressAndDecode(resp);
       eval(code); 
    }
  }
  ajax(url,isPost,isAsync,params,callback);
}
function ajax(url,isPost,isAsync,params,callback){
  var paramsText=joinAndEncodeMap('&','=',params);
  var r;
  if (window.XMLHttpRequest) 
     r=new XMLHttpRequest(); // code for IE7+, Firefox, Chrome, Opera, Safari
  else 
     r= new ActiveXObject("Microsoft.XMLHTTP"); // code for IE6, IE5
  if(!isPost && paramsText)
     url=url+'?'+paramsText;
  r.open(isPost ? "POST" : "GET",url,isAsync);
  if(callback){
    r.onerror=onAjaxError;
    r.responseType="arraybuffer";
    r.onreadystatechange=function(o){ if (r.readyState==4) callback(r.response,r.status);  };
  }
  r.setRequestHeader("Content-type","text/html"); 
    if(isPost)
      r.send(paramsText);
    else
      r.send();
  return ;
};

//##################
//##### select #####


function Select(element){
  this.element=element;
};

Select.prototype.element;

Select.prototype.ensureSelectedVisible = function(){
	var min,max;
	
    var options=this.element.options;
    for(var i=0;i<options.length;i++){
        var option=options[i];
		if(option.selected){
			if(min==null)
				min=option;
		    max=option;
		}
	}
    if(min==null)
    	return;
    min.scrollIntoView();
    max.scrollIntoView();
	
}
Select.prototype.setDisabled = function(disabled){
	this.element.disabled=disabled;
}


Select.prototype.moveSelectedUp = function(){
  var options=this.element.options;
  var notSelected=false;
  for(var i=0;i<options.length;i++){
    var option=options[i];
    if(!option.selected){
      notSelected=true;
      continue;
    } else if(!notSelected)
      continue;
    this.element.removeChild(option);
    this.element.insertBefore(option, this.element[i - 1]);
  }
};
Select.prototype.moveSelectedDown = function(){
  var options=this.element.options;
  var notSelected=false;
  for(var i=options.length-1;i>=0;i--){
    var option=options[i];
    if(!option.selected){
      notSelected=true;
      continue;
    }else if(!notSelected)
      continue;
    this.element.removeChild(option);
    this.element.insertBefore(option, this.element[i + 1]);
  }
};

Select.prototype.clearSelected = function(){
  this.element.selectedIndex=this.element.multiple ?  -1 : 0;
};

Select.prototype.setSelectedValue = function(value){
  for(var i=0;i<this.element.length;i++)
    if(this.element.options[i].value==value){
      this.element.selectedIndex=i;
      return true;
    }
  this.element.selectedIndex=-1;
  return false;
};
Select.prototype.setSelectedValueDelimited = function(values,delim){
  var valuesMap={};
  var vals=values==null ? [] : values.split(delim);
  for(i in vals)
	  valuesMap[vals[i]]=true;
  for(var i=0;i<this.element.length;i++){
	var o=this.element.options[i];
    if(valuesMap[o.value]!=null){
      o.selected='selected';
    }
  }
};
Select.prototype.addOption=function(value,text,isSelected){
  var option=nw('option');
  option.value=value;
  option.text=text;
  option.selected=isSelected;
  this.element.add(option,null);
  return option;
};
Select.prototype.clear=function(){
  this.element.length=0;
};

Select.prototype.getSelected=function(){
    var options=this.element.options;
    var selected=[];
    for(var i=0;i<options.length;i++){
      if(options[i].selected)
        selected[selected.length]=options[i];
    }
    return selected;
};
Select.prototype.getSelectedValues=function(){
    var options=this.element.options;
    var selected=[];
    for(var i=0;i<options.length;i++){
      if(options[i].selected)
        selected[selected.length]=options[i].value;
    }
    return selected;
};
Select.prototype.getSelectedValuesDelimited=function(delim){
    var options=this.element.options;
    var selected="";
    for(var i=0;i<options.length;i++){
      if(options[i].selected){
    	if(selected!="")
    		selected+=delim;
        selected+=options[i].value;
      }
    }
    return selected;
};
Select.prototype.moveSelectedTo=function(target){
    var options=this.getSelected();
    for(var i=0;i<options.length;i++){
      var option=options[i];
      target.addOption(option.value,option.text,false);
      this.element.removeChild(option);
    }
};
Select.prototype.getSelectedValue=function(){
    return this.element.options[this.element.selectedIndex].value;
};
Select.prototype.getSelectedTitle=function(){
    return this.element.options[this.element.selectedIndex].title;
};
Select.prototype.getValues=function(){
    var options=this.element.options;
    var r=[];
    for(var i=0;i<options.length;i++){
      r[i]=options[i].value;
    }
    return r;
};


// windowsObjMap expecting object map
// dialogSink expecting object map
function alertDialogWindowsGeneric(dialogFunc, args, windowsObjMap, dialogsSink){
	if(dialogsSink == null)
		dialogsSink = {};
	
	if(!Array.isArray(args))
		args = [args]; // if not array, convert to array
		
	for(k in windowsObjMap){
		dialogsSink[k] = dialogFunc.call(window, ...args, windowsObjMap[k]);
	}
	return dialogsSink;
}
// dialogSink expecting object map
function closeDialogWindowsGeneric(dialogsSink){
	if(dialogsSink == null)
		return null;
	
	for(k in dialogsSink){
		if(closeDialogGeneric(dialogsSink[k]))
			dialogsSink[k] = null;
	}
	return dialogsSink;
}

function closeDialogGeneric(dialog){
	if(dialog == null)
		return false;
	if(dialog.visible != false)
		dialog.close();
	return true;
}

function alertWarningDialog(title, text, jsErrorText, _window){
	if(_window == null)
		_window = window;

    var content=nw('div','dialog_alert');
    content.innerHTML=text;
	var refreshFunc = function(e) { location.reload();};
	var dialog=new Dialog(content, _window);
	dialog.setHeaderTitle(PORTAL_DIALOG_HEADER_TITLE);
  	dialog.setTitle(title);
	dialog.setImageHtml(SVG_ERROR_BANNER);
	dialog.addButton("Refresh", null, refreshFunc);
	dialog.setCanResize(false);
	if (jsErrorText) {
		dialog.setJsErrorText(jsErrorText);
		dialog.showMoreButton();
	}
	dialog.show();
	return dialog;
}

//##### Dialog Error #####
//##################
function DialogError(Dialog) {
	var that = this;
	this.owningDialog = Dialog;
	this.divElement = nw("div", "dialog_error");
	
	this.headerElement=nw("div","dialog_header");
	this.headerTitle=nw("div", "dialog_header_title");
	this.headerLogoContainer=nw("div", "dialog_header_logo_container");
	this.headerLogoContainer.innerHTML=SVG_LOGO_WHITE;
	this.headerElement.appendChild(this.headerTitle);
	this.headerElement.appendChild(this.headerLogoContainer);
	
	this.dialogErrorBody=nw("div", "dialog_err_body");
	this.messageElement = nw("div", "dialog_err_msg_ele");
	this.message = nw("div", "dialog_err_cont_msg");
	this.copyErrorContainer = nw("div", "dialog_copy_err_cont");
	this.messageElement.appendChild(this.message);
	this.messageElement.appendChild(this.copyErrorContainer);
	
	this.dialogErrorContainer = nw("div", "dialog_err_container");
	this.dialogErrorTextContainer = nw("textArea", "dialog_err_text_container");
	this.dialogErrorContainer.appendChild(this.dialogErrorTextContainer);
	
	this.dialogErrorCloseBtn = nw("div", "dialog_err_close_btn");
	this.dialogErrorCloseBtn.innerHTML="<span>Close</span>"
	
	this.dialogErrorBody.appendChild(this.messageElement);
	this.dialogErrorBody.appendChild(this.dialogErrorContainer);
	this.dialogErrorBody.appendChild(this.dialogErrorCloseBtn);
	
	this.divElement.appendChild(this.headerElement);
	this.divElement.appendChild(this.dialogErrorBody);
	//event listeners
	this.dialogErrorCloseBtn.onclick=function(){ that.onCloseBtnClicked(); };
	this.location=new Rect(0,0,600,450);
	makeDraggable(this.headerElement,this.divElement);
	this.headerElement.ondraggingEnd=function(e,x,y){that.location.move(x,y);};
	this.setCanResize(true);
	
}
DialogError.prototype.flashHeader=function() {
	this.headerElement.style.backgroundColor="#c15400";
	var that = this;
	// it's safe not to clear the timeout
	setTimeout(function() {
		that.headerElement.style.backgroundColor="#f16900";
	}, 150);
}
DialogError.prototype.setTitle=function(title) {
	this.headerTitle.innerHTML = title;
}
DialogError.prototype.setCanResize=function(canResize){
  var that=this;
  if(canResize){
    this.resizeButtonElement=nw("div","dialog_resizebutton");
    this.divElement.appendChild(this.resizeButtonElement);
    makeDraggable(this.resizeButtonElement,null);
    this.resizeButtonElement.ondragging   =function(e,x,y){var rect=that.location.clone().grow(x,y);rect.writeToElement(that.divElement);if(that.onResize)that.onResize(rect); };
    this.resizeButtonElement.ondraggingEnd=function(e,x,y){that.location.grow(x,y).writeToElement(that.divElement);if(that.onResize)that.onResize(that.location); };
  }else{
    if(this.resizeButtonElement!=null){
      this.divElement.removeChild(this.resizeButtonElement);
      this.resizeButtonElement=null;
    }
  }
};
DialogError.prototype.setErrorText=function(errorText) {
	this.dialogErrorTextContainer.value = errorText;
}
DialogError.prototype.onCloseBtnClicked=function() {
	this.close();
}
DialogError.prototype.close=function() {
	var win = this.owningDialog.getWindow();
	if (win) {
		win.document.body.removeChild(this.divElement);
		this.owningDialog.errorDialog = null;
	}
}
DialogError.prototype.show=function() {
	var win = this.owningDialog.getWindow();
	if (win) {
		var bodyRect=new Rect().readFromElement(win.document.body);
		win.document.body.appendChild(this.divElement);
		this.location.setMidpoint(bodyRect.getMidpoint());
		this.location.writeToElement(this.divElement);
		this.setErrorText(this.owningDialog.getJsErrorText());
	}
}

//##### Dialog #####
//##################

function Dialog (content, _window) {
  this._window = _window == null? window :_window;
  var that=this;
  this.jsErrorText = null;
  this.errorDialog = null;
  this.divElement=nw("div","dialog");
  this.divElement.id="alert_dialog";
  this.headerElement=nw("div","dialog_header");
  this.headerTitle=nw("div", "dialog_header_title");
  this.headerLogoContainer=nw("div", "dialog_header_logo_container");
  this.headerLogoContainer.innerHTML=SVG_LOGO_WHITE;
  this.headerElement.appendChild(this.headerTitle);
  this.headerElement.appendChild(this.headerLogoContainer);
  
  this.dialogBody=nw("div", "dialog_body");
  this.imageElement=nw("div", "dialog_image_container");
  this.mainMessage=nw("div", "dialog_main_message");
  this.subMessage=nw("div", "dialog_sub_message");
  this.jsErrorButton=nw("div", "dialog_js_error_button");
  this.jsErrorButton.innerHTML = "<span class='dialog_js_error_button_text'>More</span>";
  this.closeButtonElement=nw("div", "dialog_close_button");
  this.closeButtonElement.innerHTML="&#x2716;";
  
  this.divElement.appendChild(this.headerElement);
  this.divElement.appendChild(this.closeButtonElement);
  this.divElement.appendChild(this.dialogBody);
  
  this.dialogBody.appendChild(this.imageElement);
  this.dialogBody.appendChild(this.mainMessage);
  this.dialogBody.appendChild(this.subMessage);
  this.dialogBody.appendChild(this.jsErrorButton);
  
  this.backgroundElement=nw("div","disable_glass dialog_glass");

  this.visible=false;
  this.location=new Rect(0,0,400,330);
  makeDraggable(this.headerElement,this.divElement);
  this.closeButtonElement.onclick=function(e){that.close(e,'EXIT');};
  this.headerElement.ondraggingEnd=function(e,x,y){that.location.move(x,y);};
  this.jsErrorButton.onclick=function(e){ that.onJsErrorButtonClicked() };
  this.content=content;
  this.subMessage.appendChild(this.content);
}

Dialog.prototype.setHeaderBgColor=function(newColor) {
	this.headerElement.style.backgroundColor = newColor;
}

Dialog.prototype.setDialogBgColor=function(newColor) {
	this.divElement.style.backgroundColor = newColor;
}

Dialog.prototype.setType=function(type) {
	if (type == "loading") {
		this.dialogBody.removeChild(this.mainMessage);
		this.dialogBody.removeChild(this.subMessage);
		this.dialogBody.removeChild(this.jsErrorButton);
	}
}
Dialog.prototype.showMoreButton=function() {
	this.jsErrorButton.style.display="flex";
}
Dialog.prototype.hideMoreButton=function() {
	this.jsErrorButton.style.display="none";
}
Dialog.prototype.onJsErrorButtonClicked=function() {
	if (this.errorDialog) {
		this.errorDialog.flashHeader();
	} else {
		this.errorDialog = new DialogError(this);
		var l = this.subMessage.innerText.split(':');
		if (l.length == 2)
			this.errorDialog.setTitle(l[1]);
		this.errorDialog.show();
	}
}
Dialog.prototype.setHeaderTitle=function(headerTitle) {
	headerTitle = headerTitle ? headerTitle : "";
	this.headerTitle.innerHTML=headerTitle;
}
Dialog.prototype.setSize=function(width,height){
	this.location.width=width;
	this.location.height=height;
};
Dialog.prototype.setCanResize=function(canResize){
  var that=this;
  if(canResize){
    this.resizeButtonElement=nw("div","dialog_resizebutton");
    this.divElement.appendChild(this.resizeButtonElement);
    makeDraggable(this.resizeButtonElement,null);
    this.resizeButtonElement.ondragging   =function(e,x,y){var rect=that.location.clone().grow(x,y);rect.writeToElement(that.divElement);if(that.onResize)that.onResize(rect); };
    this.resizeButtonElement.ondraggingEnd=function(e,x,y){that.location.grow(x,y).writeToElement(that.divElement);if(that.onResize)that.onResize(that.location); };
  }else{
    if(this.resizeButtonElement!=null){
      this.divElement.removeChild(this.resizeButtonElement);
      this.resizeButtonElement=null;
    }
  }
};
Dialog.prototype.setTitle=function(title){
	this.mainMessage.innerText = title;
}
Dialog.prototype.setImageHtml=function(imageHtml){
	this.imageElement.innerHTML = imageHtml;
}
Dialog.prototype.setJsErrorText=function(jsErrorText) {
	this.jsErrorText = jsErrorText;
}
Dialog.prototype.getJsErrorText=function() {
	return this.jsErrorText;
}
Dialog.prototype.addButton=function(title, reason, func){
   var button=nw('div','dialog_refresh_button'); //This css is generic
   button.innerText=title;
   this.dialogBody.appendChild(button);
   var that=this;
   if(func != null){
	   button.onclick=func;
   }
   else{
	   if(reason==null)
	       reason=title;
	   button.onclick=function(e){that.close(e,reason);};
   }
   return button;
}
Dialog.prototype.setGlassOpacity=function(opacity){
	this.backgroundElement.style.opacity=opacity;
}
Dialog.prototype.setImageSize=function(width, height) {
	var img = this.imageElement.firstChild;
	if (img) {
		img.style.height = toPx(height);
		img.style.width = toPx(width);
	}
}
Dialog.prototype.setSmallerCloseButton=function() {
	this.closeButtonElement.classList.add("dialog_close_button_smaller");
}
Dialog.prototype.show=function(){
  if(this.visible)
    return;
  var bodyRect=new Rect().readFromElement(this._window.document.body);
  this._window.document.body.appendChild(this.backgroundElement);
  this._window.document.body.appendChild(this.divElement);
  this.location.setMidpoint(bodyRect.getMidpoint());
  this.location.writeToElement(this.divElement);
  if(this.onResize!=null)
	  this.onResize(this.location);
  this.visible=true;
};
Dialog.prototype.close=function(e,reason){
  if(!this.visible){
    alert('not visible');
    return;
  }
  if(this.onClose)
    if(this.onClose(e,this,reason)==false)
    	return;
  var b=this._window.document.body;
  if(b!=null){
     b.removeChild(this.backgroundElement);
     b.removeChild(this.divElement);
  }
  if (this.errorDialog)
	  this.errorDialog.close();
  this.visible=false;
};
Dialog.prototype.getWindow=function() {
	return this._window;
}

var dragStart;
var dragStartMouse;
var dragElement;
    	function stopDefault(e2) {
    	    if (e2 && e2.preventDefault) { e2.preventDefault(); }
    	    else { window.event.returnValue = false; }
    	}	
    	
function getWindow(element){
	if(element.ownerDocument)
	  return element.ownerDocument.defaultView;
	else{
		log('no window!');
	  return window;
	}
}
function getDocument(element){
   return getWindow(element).document;
}
    	
//var draggableElements=[];

function getDraggable(element, containerElement){
	if(element == null)
		return null;
	if(element.__draggableContainer == containerElement)
		return element;
	if(element.__isPushDraggableContainer == true)
		return element;
	var e = element.parentNode;
	while(e!=null && e.isDraggable != true && e.__draggableContainer != containerElement && e.__isPushDraggableContainer != true)
		e=e.parentNode;
	if(e != containerElement)
		return e;
	else return null;
}

function resetPushDraggableContainer(containerElement){
	containerElement.__pushDraggableElements=[];
	containerElement.__pushDraggableElementsNew=[];
	containerElement.__pushDraggableTriggerElements=[];
	containerElement.__isDraggedVisible=true;
}


function ContainerWithDraggableElements(element, calcContainerRect, onDragHandler, onDragEndHandler, onMousePositionHandler, additionalTargetsList, 
		getIdx, getMousePosition, animated, moveX, moveY){
	var that = this;
	this.__element = element;
	this.__doc = getDocument(this.__element);
	this.__dragOver = this.dragOver.bind(this);
	this.__doc.body.addEventListener("dragover", this.__dragOver);
	this.__drop = this.drop.bind(this);
//	this.__doc.body.addEventListener("drop", this.__drop);
	this.__sortedDraggableElementsX=[];
	this.__sortedDraggableElementsY=[];
	// This is for actions where you want to drag outside or trigger other custom actions
	// Expecting elements with optionally __onDragOver or __onDrop event
	this.__additionalTargetsList = additionalTargetsList == null ? []: additionalTargetsList;
	this.onDragHandler = onDragHandler;
	this.onDragEndHandler = onDragEndHandler;
	this.onMousePositionHandler = onMousePositionHandler;
	this.getIdx=getIdx;
	this.getMousePosition = getMousePosition == null? this.defaultGetMousePosition: getMousePosition;
	this.animated = animated;
	this.__moveX = moveX;
	this.__moveY = moveY;

	//TODO default indexfuncs;
	var getDefaultLocationX = function(de){
		return de.__indexX;
	};
	var getDefaultLocationY = function(de){
		return de.__indexY;
	};
//	https://stackoverflow.com/questions/37958394/html5-on-drag-change-dragging-image-or-icon

	this.calcContainerRect = calcContainerRect;
	this.initRect(calcContainerRect);

    this.noDragImage = new Image();
    this.noDragImage.src = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAAC0lEQVQI12NgAAIAAAUAAeImBZsAAAAASUVORK5CYII=";
} 
ContainerWithDraggableElements.prototype.__element;
ContainerWithDraggableElements.prototype.__rect;
//ContainerWithDraggableElements.prototype.__pushDraggableElements; // unused;
ContainerWithDraggableElements.prototype.__sortedDraggableElementsX;
ContainerWithDraggableElements.prototype.__sortedDraggableElementsY;

ContainerWithDraggableElements.prototype.__moveX;
ContainerWithDraggableElements.prototype.__moveY;
ContainerWithDraggableElements.prototype.__flipMouseXY;

ContainerWithDraggableElements.prototype.__sourceElement;
ContainerWithDraggableElements.prototype.__changedElements = new Set();
//ContainerWithDraggableElements.prototype.__initialMouseOffsetX;
//ContainerWithDraggableElements.prototype.__initialMouseOffsetY;
ContainerWithDraggableElements.prototype._lastTargetIndex;
ContainerWithDraggableElements.prototype.__isDraggedVisible=true;

ContainerWithDraggableElements.prototype.getIdx;

ContainerWithDraggableElements.prototype.initRect=function(calc){
	if(this.calcContainerRect != null){
		this.__rect = this.calcContainerRect(); // Intent: given the rectangle will apply the position
	}
	else{
		this.__rect = this.__element.getBoundingClientRect();
	}
}
ContainerWithDraggableElements.prototype.getDraggableElementsMidX=function(){
	var midpoints = [];
	if(this.__sortedDraggableElementsX.length == 0)
		return midpoints;
	var rectFirst = this.__sortedDraggableElementsX[0].__rect;
	var first = rectFirst.left;
	midpoints.push(first);
	var len = this.__sortedDraggableElementsX.length;
	for(var i = 0; i < len; i++){
		var rect = this.__sortedDraggableElementsX[i].__rect;
		var mp = rect.left + rect.width/2;
		midpoints.push(mp);
	}
	var rectLast = this.__sortedDraggableElementsX[len-1].__rect;
	var last = rectLast.left + rectLast.width;
	midpoints.push(last);
	return midpoints;
}
// Find index of an ordered list for a number if it were to be inserted
// Returns 0 if before any number in list or list length if after all numbers in list
ContainerWithDraggableElements.prototype.findIndexOfNum=function(orderedList, num){
	var i = 0;
	for(; i < orderedList.length; i++){
		var num0 = orderedList[i];
		if(num0 > num)
			break;
	}
	return i;
}

ContainerWithDraggableElements.prototype.clear=function(){
	this.__doc.body.removeEventListener("dragover", this.__dragOver);
	for(var i = 0; i < this.__sortedDraggableElementsX.length; i++){
		this.__sortedDraggableElementsX[i].__triggerElement.removeEventListener("dragstart", this.__dragStart);
		this.__sortedDraggableElementsX[i].__triggerElement.removeEventListener("dragend", this.__dragEnd);
	}

}
ContainerWithDraggableElements.prototype.sort=function(){
	var sortX =  function(a,b){
		return a.__rect.left < b.__rect.left ? -1 : a.__rect.left > b.__rect.left ? 1 : 0;
	};
	var sortY =  function(a,b){
		return a.__rect.top < b.__rect.top ? -1 : a.__rect.top > b.__rect.top ? 1 : 0;
	};
	this.__sortedDraggableElementsX.sort(sortX);
	this.__sortedDraggableElementsY.sort(sortY);

	// Update index
	for(var i = 0; i < this.__sortedDraggableElementsX.length; i++){
		this.__sortedDraggableElementsX[i].__indexX= i;
	}
	for(var i = 0; i < this.__sortedDraggableElementsY.length; i++){
		this.__sortedDraggableElementsY[i].__indexY= i;
	}
}

ContainerWithDraggableElements.prototype.addDraggableElement=function(draggableElement){
	this.__sortedDraggableElementsX.push(draggableElement);
	this.__sortedDraggableElementsY.push(draggableElement);
	this.__dragStart = this.dragStart.bind(this);
	this.__dragEnd = this.dragEnd.bind(this);
	draggableElement.__triggerElement.addEventListener("dragstart", this.__dragStart);
    draggableElement.__triggerElement.addEventListener("dragend", this.__dragEnd);
}
//Returns the object DraggableElement otherwise returns the html element that is an additional target
ContainerWithDraggableElements.prototype.getDraggable=function(element){
	if(element == null)
		return null;
	if(element.__draggableElement != null && element.__draggableElement.__container == this){
		return element.__draggableElement;
	}
	for(var i = 0 ; i < this.__additionalTargetsList.length; i++){
		var elem = this.__additionalTargetsList[i];
		if(element == elem)
			return element;
	}
	
	var e = element.parentNode;
	while(e != null){
		if(e.__draggableElement != null && e.__draggableElement.__container == this)
			return e.__draggableElement;
		for(var i = 0 ; i < this.__additionalTargetsList.length; i++){
			var elem = this.__additionalTargetsList[i];
			if(element == elem)
				return element;
		}
		
		e=e.parentNode;
	} 
	return null;
}
ContainerWithDraggableElements.prototype.moveElement=function(draggableElement, offsetX, offsetY, indexOffsetX, indexOffsetY){
	draggableElement.__rect.left += offsetX;
	draggableElement.__indexX += indexOffsetX;
	draggableElement.applyRect(draggableElement.__rect);
	
}
ContainerWithDraggableElements.prototype.defaultGetMousePosition=function(event){
	var r = { x : event.x - this.__rect.left , 
			y :	this.__rect.top - event.y  };
	return r;
}
ContainerWithDraggableElements.prototype.dragOver=function(event){
	event.preventDefault();
	event.dataTransfer.dropEffect = "move";

	var mouse = this.getMousePosition(event);
	var relativeMouseX = mouse.x;
	var relativeMouseY = mouse.y;

	if(this.__moveX == true){
		var listMidpointsX = this.getDraggableElementsMidX();
		var midPtIdx = this.findIndexOfNum(listMidpointsX, relativeMouseX) - 1;

		var currentIndex = this.__sourceElement.__indexX;
		var afterSourceIndex = null;
		var targetIndex = currentIndex;

		// Move right * needs to be current idx plus one
		if(midPtIdx <= -1 || midPtIdx > this.__sortedDraggableElementsX.length){
			// do nothing
		}
		else if(midPtIdx > (currentIndex +1)){
			afterSourceIndex = currentIndex + 1;
			targetIndex = midPtIdx - 1;
		}
		// Move left * needs to be just less than currentIdx
		else if (midPtIdx < (currentIndex)){
			afterSourceIndex = currentIndex - 1;
			targetIndex = midPtIdx - 0;
		}

		if(targetIndex != currentIndex){
			var sourceIndexOffset = targetIndex - currentIndex;
			var targetIndexOffset = currentIndex - afterSourceIndex;
			var sourcePxOffset = 0;
	   		var targetPxOffset = 0;
	   		
	   		var srcRect = this.__sourceElement.__rect;
	   		var afterSrcRect = this.__sortedDraggableElementsX[afterSourceIndex].__rect;
	   		

	   		var tgtRect = this.__sortedDraggableElementsX[targetIndex].__rect;

	   		
	   		if(targetIndex > currentIndex){
	   			sourcePxOffset = Math.round(tgtRect.left - srcRect.left) + (Math.round(tgtRect.width - srcRect.width));
	   			targetPxOffset = Math.round(srcRect.left - afterSrcRect.left);
	   		}else{
	   			sourcePxOffset = Math.round(tgtRect.left - srcRect.left) ;
		   		targetPxOffset = Math.round(srcRect.left - afterSrcRect.left) + Math.round(srcRect.width - afterSrcRect.width);
	   		}
	   		
			this.moveElement(this.__sourceElement, sourcePxOffset, null, sourceIndexOffset ,null);

			var startIndex = currentIndex < targetIndex? currentIndex+1:targetIndex;
			var endIndex = currentIndex < targetIndex? targetIndex:currentIndex-1;
			for(var i = startIndex; i <= endIndex; i++){
				var currentTarget= this.__sortedDraggableElementsX[i];
				this.moveElement(currentTarget, targetPxOffset, null, targetIndexOffset ,null);
	 		}
			this.sort();
		}
   		// Handle Mouse Position 
		var mousePositionHandler = this.onMousePositionHandler;
   		if(mousePositionHandler != null){
			var contRect = this.__sourceElement.__rect;
			var moved = false;
   			if(this.__moveX )
   				moved = mousePositionHandler(event, this.__rect, relativeMouseX, this.__sourceElement.__rect.width, this.__moveX , this.__moveY );
//   				moved = mousePositionHandler(event.x, this.__rect.x, this.__rect.width, relativeMouseX, this.__sourceElement.__rect.width, this.__moveX , this.__moveY );
   				//moved = mousePositionHandler(event.x, contRect.x, containerElement.__initialMouseOffsetX, eventSource.__rect.width, moveX, moveY);
   			if(this.__moveY)
   				moved = mousePositionHandler(event.y, contRect.top, relativeMouseY, this.__sourceElement.__rect.width, this.__moveX , this.__moveY );
   			if(moved){
				for(var changedIndex of containerElement.__changedIndexes){
					var draggableElement = containerElement.__pushDraggableElements[changedIndex];
//					saveRect(draggableElement)
//					updateRect(draggableElement);
//					repaintRect(draggableElement);
				}

   			}
   		}

		
		
	}
	// loop through all objs
	// find the first

//	var eventTarget = this.getDraggable(event.target);
//	if(eventTarget != null && eventTarget != this.__sourceElement){
//		eventTarget.__onDragOver.call(this, event, this.__sourceElement, eventTarget);
//		err(eventTarget);
//	}


//	err(["dragging",event, event.pageX, event.offsetX, event.x]);
	
}
ContainerWithDraggableElements.prototype.drop=function(event){
//	err(event);
	event.preventDefault();
//	err("drop");
	
}
ContainerWithDraggableElements.prototype.dragStart=function(event){

    event.dataTransfer.setDragImage(this.noDragImage, 0, 0);
//    event.dataTransfer.dropEffect= "move";



	this.__sourceElement = event.target.__draggableElement; 
	this.__sourceElement.__element.style.opacity=0.2;
	this.__changedElements = new Set();
	this.__initialMouseOffsetX = event.offsetX;
	this.__initialMouseOffsetY = event.offsetY;

	this.sort();

	this.__sourceElement.__origIndexX= this.__sourceElement.__indexX;
	this.__sourceElement.__origIndexY= this.__sourceElement.__indexY;
	this.__oldIdx = this.__sourceElement.getIdx();
}

ContainerWithDraggableElements.prototype.dragEnd=function(event){

   	var dropSuccess = true;
	if(event.dataTransfer.dropEffect== "none"){
		dropSuccess = false;
	}
	
	this.sort();
	
	var oldIndex = this.__oldIdx;
	var newIndex = this.__sourceElement.__indexX;
	
	this.__sourceElement.__element.style.opacity="initial";

    //newLocation..
    this.__oldIdx = null
	this.__changedElements.clear();
	this.__initialMouseOffsetX = null;
	this.__initialMouseOffsetY = null;
	this.__sourceElement.__origIndexX= null;
	this.__sourceElement.__origIndexY= null;




	if(dropSuccess && this.onDragEndHandler!=null)
		this.onDragEndHandler(oldIndex, newIndex);
	this.__sourceElement = null; 
}

/*
 * The element is full element, the triggerElement is what holds the event listener, these may just be the same
 * The rect is used to set the location of the element and is relative to the parent element
 * Idx is the idx location given from the server this may be any object
 * The element is expected to be positioned absolutely
 */
function DraggableElement(draggableContainer, element, triggerElement, calcRect, idx){
	this.__container = draggableContainer;
	this.__element = element;
	this.__element.__draggableElement=this;
	this.__triggerElement = triggerElement;
	this.__triggerElement.__draggableElement=this;
	this.__triggerElement.draggable="true";
	this.__element.isDraggable=true; // ??
	this.calcRect = calcRect;
	this.initRect(calcRect);
	this.applyRect(this.__origRect);
	this.__idx = idx;
}
DraggableElement.prototype.__container;
DraggableElement.prototype.__element;
DraggableElement.prototype.__triggerElement;

DraggableElement.prototype.__onDragOver;
DraggableElement.prototype.__onDrop;

DraggableElement.prototype.__rect;
DraggableElement.prototype.__origRect;

DraggableElement.prototype.__relativeRect;
DraggableElement.prototype.__indexX;
DraggableElement.prototype.__indexY;
DraggableElement.prototype.__origIndexX;
DraggableElement.prototype.__origIndexY;
DraggableElement.prototype.__idx; // Location for server

DraggableElement.prototype.initRect=function(calcRect){
	if(calcRect == null){
		var rect = this.__element.getBoundingClientRect();
		var prect = this.__container.__rect;
		this.__origRect = new Rect(
				rect.x-prect.x, rect.y - prect.y,
				rect.width, rect.height
				);
		this.__rect = this.__origRect;
	}
	else{
		this.__origRect=this.calcRect(); // Intent: given the rectangle will apply the position
		this.__rect = this.__origRect;
	}
}

DraggableElement.prototype.applyRect=function(rect){
	this.__element.style.position="absolute";
	this.__element.style.left=toPx(rect.left);
	this.__element.style.top=toPx(rect.top);
	this.__element.style.width=toPx(rect.width);
	this.__element.style.height=toPx(rect.height);
};

DraggableElement.prototype.getIdx=function(){
	return this.__container.getIdx(this);
}


function makePushDraggableContainer(containerElement, moveX, moveY, onDragHandler, mousePositionHandler){
	if(containerElement == null) 
		return; 
	if(containerElement.__isPushDraggableContainer == true)
		return;
	containerElement.__isPushDraggableContainer = true;
	containerElement.__moveX=moveX;
	containerElement.__moveY=moveY;
	resetPushDraggableContainer(containerElement);
	
	// Moves element to calculated offset x and y to follow mouse plane
	var repaintRect = function(element){
		if(element.__offsetX !=null)
	 		element.style.left = toPx(element.__offsetX);
		if(element.__offsetY !=null)
			element.style.top  = toPx(element.__offsetY);
	};
	var saveRect = function(element){
		element.__rect = element.getBoundingClientRect(); 
	}
	// Calculates Offset
	var updateRect = function(element){
		if(element.style.display=='none')
			element.style.display='initial';
		if(element.__draggableContainer != null){
			if(element.__draggableContainer.__moveX && element.__offsetX == null)
				element.__offsetX = element.__rect.x - element.__draggableContainer.__rect.x;
			if(element.__draggableContainer.__moveY && element.__offsetY == null)
				element.__offsetY = element.__rect.y - element.__draggableContainer.__rect.y;
		}
	};
	
	var moveDraggable = function(draggableContainerElement, draggableElement, indexOffset, pxOffset, isMoveX, isMoveY){
		draggableElement.__draggableNewIndex += indexOffset;
			draggableContainerElement.__pushDraggableElementsNew[draggableElement.__draggableNewIndex] = draggableElement; 
		if(isMoveX){
			draggableElement.__rect.x += pxOffset;
			draggableElement.__rect.left += pxOffset;
			
			draggableElement.__offsetX += pxOffset; 
			draggableElement.style.left = toPx(draggableElement.__offsetX);
		}
		else if(isMoveY){
			draggableElement.__rect.y += pxOffset;
			draggableElement.__rect.top += pxOffset;

			draggableElement.__offsetY += pxOffset;
			draggableElement.style.top = toPx(draggableElement.__offsetY);
		}
	};
	
	
    var dragOver = function(event){
    	var eventSource = containerElement.__initiatorElement;
    	// If the target isn't draggable don't start the drag action which means drag event hasn't started
    	if(eventSource == null) // Source is the object being dragged over
    	    return false;
    	var eventTarget = getDraggable(event.target,containerElement);
    	
//    	if(eventTarget == eventSource) // Target is the dragged item
//    		return false;

	   	if(eventTarget !=null){
	    	var isContainer = eventTarget.__isPushDraggableContainer == true;
	    	var isDraggableElement = eventTarget.__draggableContainer == containerElement;
	    	
	    	if(isContainer){
	    		containerElement.__isDraggedVisible=true;
	    		event.preventDefault();
	    		event.dataTransfer.dropEffect = "move";
	    	}
	    	else if(isDraggableElement){
	    		event.preventDefault();
	    		event.dataTransfer.dropEffect = "move";
	
	
				var srcRect = eventSource.__rect;
				var tgtRect = eventTarget.__rect;
	    		var midPointXsrc = srcRect.left + srcRect.width/2;
	    		var midPointYsrc = srcRect.top + srcRect.height/2;
	    		var midPointX = tgtRect.left + tgtRect.width/2; // Target is itself because you're always dragging it
	    		var midPointY = tgtRect.top + tgtRect.height/2;
	    		var currentIndex = eventSource.__draggableNewIndex;
				var targetIndex = eventTarget.__draggableNewIndex;
	
				if(currentIndex == targetIndex){
					containerElement.__lastTargetIndex = targetIndex;
				}
	    		else if(containerElement.__isDraggedVisible==true || targetIndex != containerElement.__lastTargetIndex){
	   				containerElement.__isDraggedVisible=false;
					containerElement.__lastTargetIndex = targetIndex;
	    		}
	    		else if(containerElement.__isDraggedVisible == false){
					//err("show");
	    			var update = false;
	    			var newTargetIndex = null;
	    			if(containerElement.__moveY == true){
	    				if(currentIndex < targetIndex){
	    					if(event.y > midPointY){
	    					}
	    					else{
	    						newTargetIndex = targetIndex-1;
	    					}
	    				}
	    				else if(currentIndex > targetIndex){
	    					if(event.y < midPointY){
	    					}
	    					else{
	    						newTargetIndex = targetIndex+1;
	    					}
	    				}
	    			}
	    			if(containerElement.__moveX == true){
	    				if(currentIndex < targetIndex){
	    					if(event.x > midPointX){
	    					}
	    					else{
	    						newTargetIndex = targetIndex-1;
	    					}
	    				}
	    				else if(currentIndex > targetIndex){
	    					if(event.x < midPointX){
	    					}
	    					else{
	    						newTargetIndex = targetIndex+1;
	    					}
	    				}
	    			}
	    			
	    			//if(newTargetIndex == null)
	    			//	return false;
	    			if(newTargetIndex !=null && newTargetIndex != targetIndex){
	    				var newTargetElement = containerElement.__pushDraggableElements[newTargetIndex];
	    				tgtRect	= newTargetElement.__rect;
						targetIndex = newTargetIndex;
//						err([newTargetElement, eventTarget, eventSource]);
	    			}
	    			
	    			if(currentIndex != targetIndex)
	    				update=true;
	    				
	    			
	    			if(update == true){
	    				containerElement.__isDraggedVisible=true;
						//move
						var sourcePxOffset = 0;
	   					var targetPxOffset = 0;
	   					var sourceIndexOffset = targetIndex - currentIndex;
	   					var targetIndexOffset = - (sourceIndexOffset);
	   					
	   					if(moveX == true){
	    					if(currentIndex < targetIndex){
	    						var srcElement1Offset = containerElement.__pushDraggableElements[currentIndex + 1];
	    						var src1OffsetRect = srcElement1Offset.__rect;

		   						sourcePxOffset = Math.round(tgtRect.x - srcRect.x) + (Math.round(tgtRect.width - srcRect.width)) -1;
		   						targetPxOffset = Math.round(srcRect.x - src1OffsetRect.x) - 1 ;
	    					}
	    					else if(currentIndex > targetIndex){
	    						var srcElement1Offset = containerElement.__pushDraggableElements[currentIndex - 1];
	    						var src1OffsetRect = srcElement1Offset.__rect;

		   						sourcePxOffset = Math.round(tgtRect.x - srcRect.x) + 1;
		   						targetPxOffset = Math.round(srcRect.x - src1OffsetRect.x) + Math.round(srcRect.width - src1OffsetRect.width) + 1;
	   						}
	
	   					}
	   					if(moveY == true){
	    					if(currentIndex < targetIndex){
	    						var srcElement1Offset = containerElement.__pushDraggableElements[currentIndex - 1];
	    						var src1OffsetRect = srcElement1Offset.__rect;

		   						sourcePxOffset = tgtRect.y - srcRect.y + (tgtRect.height - srcRect.height);
		   						targetPxOffset = (srcRect.y - src1OffsetRect.y);
	    					}
	    					else if(currentIndex > targetIndex){
	    						var srcElement1Offset = containerElement.__pushDraggableElements[currentIndex + 1];
	    						var src1OffsetRect = srcElement1Offset.__rect;

		   						sourcePxOffset = (tgtRect.y - srcRect.y) ;
		   						targetPxOffset = +(src1OffsetRect.y - srcRect.y);
	   						}
	   					}
	   					//Moving and updating indexes on element
						containerElement.__changedIndexes.add(currentIndex);
						moveDraggable(containerElement, eventSource, sourceIndexOffset, sourcePxOffset, moveX, moveY);
	 					
	 					var startIndex = currentIndex < targetIndex? currentIndex+1:targetIndex;
	 					var endIndex = currentIndex < targetIndex? targetIndex:currentIndex-1;
	 					
	 					var countTarget = abs(targetIndexOffset);
	 					targetIndexOffset /= countTarget; 
	 					
	 					for(var i = startIndex; i <= endIndex; i++){
	 						var currentTarget= containerElement.__pushDraggableElements[i];
							containerElement.__changedIndexes.add(currentTarget.__draggableIndex);
		   					moveDraggable(containerElement, currentTarget, targetIndexOffset, targetPxOffset, moveX, moveY); 
	 					}

	 					
	 					//update indexes on container
	 					containerElement.__pushDraggableElements[currentIndex] = containerElement.__pushDraggableElementsNew[currentIndex]; 
	 					for(var i = startIndex; i <= endIndex; i++){
							containerElement.__pushDraggableElements[i] = containerElement.__pushDraggableElementsNew[i];
	 					}
	 					
	
						//update indexes
						containerElement.__lastTargetIndex = currentIndex;
						
						
						//Call callback if provided
						if(onDragHandler!=null){
							onDragHandler(eventSource.__draggableIndex, eventSource.__draggableNewIndex);
						}
						
	    			}
	
	    		}
    		}

			//err("ci " + currentIndex + " ti " + targetIndex);

	    }
    	else{
    		//err("invalid");
    	}

   		// Handle Mouse Position 
   		if(mousePositionHandler != null){
			var contRect = containerElement.__rect;
			var moved = false;
   			if(moveX)
   				moved = mousePositionHandler(event.x, contRect.x, containerElement.__initialMouseOffsetX, eventSource.__rect.width, moveX, moveY);
   			if(moveY)
   				moved = mousePositionHandler(event.y, contRect.y, containerElement.__initialMouseOffsetY, eventSource.__rect.width, moveX, moveY);
   			if(moved){
				for(var changedIndex of containerElement.__changedIndexes){
					var draggableElement = containerElement.__pushDraggableElements[changedIndex];
//					saveRect(draggableElement)
//					updateRect(draggableElement);
//					repaintRect(draggableElement);
				}
				
   			}
   		}

    };
	containerElement.addEventListener("dragover", dragOver); 
    
    /*
    var onDrop = function(event){
    	//err("drop");
    };
	containerElement.addEventListener("drop", onDrop); 
    */
};

function makeChildrenPushDraggable(boundsElement, listDraggableElements, moveX, moveY, onDragHandler, onDragEndHandler, mousePositionHandler, animated){
	var containerElement = boundsElement;

	var children = listDraggableElements;
	for(var i = 0; i < children.length; i++){
		var child = children[i];
		
		makePushDraggable(containerElement, child, child, moveX, moveY, onDragHandler, onDragEndHandler, mousePositionHandler, animated);
	}
}
// Either Row or Column, set moveX or moveY to true but not both
function makePushDraggable(boundsElement, initiatorElement, triggerElement, moveX, moveY, onDragHandler, onDragEndHandler, mousePositionHandler, animated){ 
	if(!moveX && !moveY) 
		return; 
	if(boundsElement == null) 
		return; 
	if(triggerElement == null)
	    triggerElement = initiatorElement;

	var containerElement = boundsElement;
	makePushDraggableContainer(containerElement, moveX, moveY, onDragHandler, mousePositionHandler);

    var element = initiatorElement;

	//Give it an index;
	var nextIndex = containerElement.__pushDraggableElements.length;
	containerElement.__pushDraggableElements[nextIndex] = element;
	containerElement.__pushDraggableElementsNew[nextIndex] = element;
	element.__draggableContainer = containerElement;
	element.__draggableIndex = nextIndex;
	element.__draggableNewIndex = nextIndex;
	element.__rect = element.getBoundingClientRect();
	if(animated == true)
		element.classList.add("ami_draggable_element");

    element.isDraggable=true;
    triggerElement.draggable="true";

    
    //Reference
    /*
	var savRect = function(element){
		element.__rect = element.getBoundingClientRect(); 
	}
	var updRect = function(element){
		if(element.__draggableContainer != null){
			if(element.__draggableContainer.__moveX && element.__offsetX == null)
				element.__offsetX = element.__rect.x - element.__draggableContainer.__rect.x;
			if(element.__draggableContainer.__moveY && element.__offsetY == null)
				element.__offsetY = element.__rect.y - element.__draggableContainer.__rect.y;
		}
		

	};
	*/

    var dragStart = function(event){
    	//err("dragStart");
		containerElement.__initiatorElement = containerElement.__pushDraggableElements[element.__draggableIndex];
		if(containerElement.__initiatorElement == null)
            return false; // allow other listeners to handle event
		containerElement.__lastTargetIndex = element.__draggableIndex;
		containerElement.__changedIndexes = new Set();
		containerElement.__initialMouseOffsetX = event.offsetX;
		containerElement.__initialMouseOffsetY = event.offsetY;


		// SaveRect + UpdateRect onDragStart
		containerElement.__rect = containerElement.getBoundingClientRect();
		for(var elementi of containerElement.__pushDraggableElements){
			elementi.__rect = elementi.getBoundingClientRect(); 
			elementi.__offsetX = elementi.__rect.x - containerElement.__rect.x;
			elementi.__offsetY = elementi.__rect.y - containerElement.__rect.y;
		}
		//element.dragTarget=movingElement;
    	//element.draggedElement=initiatorElement;
    };
    var dragEnd = function(event){
    	//err("dragEnd");
    	if(containerElement.__initiatorElement == null)
            return false; // allow other listeners to handle event
    	var dropSuccess = true;
    	var oldIndex = element.__draggableIndex;
    	var newIndex = element.__draggableNewIndex;
    	if(event.dataTransfer.dropEffect == "none"){
    		dropSuccess = false;
    	}
    	
	   	//containerElement.__initiatorElement.style.opacity="100%";
   		containerElement.__isDraggedVisible=true;

   		// Copy the rect
   		/*
		for(var changedIndex of containerElement.__changedIndexes){
			var draggableElement = containerElement.__pushDraggableElements[changedIndex];
			draggableElement.__oldRect = draggableElement.__rect;
		}
		*/
		for(var changedIndex of containerElement.__changedIndexes){
			var draggableElement = containerElement.__pushDraggableElements[changedIndex];
			
			//var newIndexOldRect = containerElement.__pushDraggableElements[draggableElement.__draggableNewIndex].__oldRect;
			draggableElement.__draggableIndex = draggableElement.__draggableNewIndex;
			//draggableElement.__rect = draggableElement.__oldRect;
		}
		/*
		for(var changedIndex of containerElement.__changedIndexes){
			var draggableElement = containerElement.__pushDraggableElements[changedIndex];
			draggableElement.__oldRect = null;
		}
		*/

    	containerElement.__initiatorElement.__rect = null;
		containerElement.__initiatorElement = null;
		containerElement.__lastTargetIndex = null;
		containerElement.__rect = null;
		containerElement.__changedIndexes = null;
		containerElement.__initialMouseOffsetX = null;
		containerElement.__initialMouseOffsetY = null;
		
		//Call onDragEnd Callback if provided
		if(onDragEndHandler!=null){
			onDragEndHandler(oldIndex, newIndex, dropSuccess);
		}
		

    };


    triggerElement.addEventListener("dragstart", dragStart);
    triggerElement.addEventListener("dragend", dragEnd);

}


function makeDraggable2(boundsElement, movingElement, initiatorElement, moveX, moveY){
    /* boundsElement - where the movingElement can move around in, if not set it should be the document
     * movingElement - the element that is moving, if not set, it is the initiatorElement
     * initiatorElement - the element that initiates it
     * moveX - if it moves in the x direction 
     * moveY - if it moves in the y direction 
     */ 
      if(!moveX && !moveY)
          return;
      if(boundsElement == null)
          boundsElement = getWindow(initiatorElement).document;
      if(movingElement == null)
          movingElement = initiatorElement;
      
    //Save properties for the listeners to use for later
      var element = initiatorElement;
    element.isDraggable=true;
    element.dragTarget=movingElement;
    element.draggedElement=initiatorElement;
    element.noDragX=!moveX;
    element.noDragY=!moveY;
    element.draggable="true";
    
    //If dropEvent needs to be fired dropable must be set to true
    var dragOverFunc = function(event,dragged){
       // prevent default to allow drop
//       if(false && dropable)
//           err("ddrag");
           event.preventDefault(); 
           event.stopPropagation();
        var e2 = event;
        var point=getMousePoint(e2);
        var dragStartMouse = dragged.dragStartMouse;
        var dragStart = dragged.dragStart;
        var dragElement = dragged;
      var diffx=point.x-dragStartMouse.x;
      var diffy=point.y-dragStartMouse.y;
      if(dragStart){
        var rect=dragStart.clone().move(diffx,diffy);
        if(dragElement.clipDragging)
        	dragElement.clipDragging(dragElement,rect);
        if(!dragElement.noDragX)
          dragElement.dragTarget.style.left=toPx(rect.left);
        if(!dragElement.noDragY)
          dragElement.dragTarget.style.top=toPx(rect.top);
      }
      if(dragElement.ondragging)
        dragElement.ondragging(dragElement,diffx,diffy,e2);
//      else if(target && target.ondragging)
//        target.ondragging(dragElement,diffx,diffy,e2);
//      var doc=getWindow(element).document;
//      doc.body.focus(); // do we need to do this?    
    };
    var dragEnterFunc = function(event,dragElement){
    };
    var dragLeaveFunc = function(event,dragElement){
    };
    var dragDropFunc = function(event,dragElement){
      // prevent default action (open as link for some elements)
      event.preventDefault();
    };

    var draggedElement = initiatorElement;
    var dragTarget = movingElement;
    var dragList = "drag";
    element.onmousedown=function(e){
//		  e.preventDefault();
        e.stopPropagation();
    };
    element.ondragstart=function(e){
    	e.dataTransfer.setData('text/plain',null);
        e.stopPropagation();
      if(element.onpredragstart)
    	  element.onpredragstart(e); 
      
//        err("start");
//      var doc=getWindow(element).document;
//      doc.body.focus(); // do we need to do this?
      
      var dragStart;
          //If there is a dragTarget get the start position of the target
      if(draggedElement){
          dragStart=new Rect().readFromElementRelatedToParent(dragTarget);
      }else
          dragStart=null;
      
      dragStartMouse=getMousePoint(e);

      draggedElement.dragStart= dragStart;
      draggedElement.dragStartMouse= dragStartMouse;

      draggedElement.addEventListener(dragList, function(event){dragOverFunc(event,draggedElement);}, false);
      
        
    };
    element.ondragend=function(e){
        draggedElement.removeEventListener(dragList, function(event){dragOverFunc(event,draggedElement);}, false);
        if(element.onpostdragend)
    	  element.onpostdragend(e); 
    };
  }
function removeDraggable(element,target,noDragX,noDragY){
  delete element.isDraggable;
  delete element.dragTarget;
  delete element.noDragX;
  delete element.noDragY;
  delete element.ondragstart;
  delete element.onselectstart;
  delete element.onmousedown;
}
function makeDraggable(element,target,noDragX,noDragY){
 
  //for(var i=0;i<draggableElements.length;i++)
	  //if(!document.contains(draggableElements[i]))
		  //draggableElements.splice(i--,1);
  //draggableElements[draggableElements.length]=element;
  
  /* element - the element in which the event listener sits on
   * target - the element that is moving
   * noDragX - if it moves in the x direction (negated)
   * noDragY - if it moves in the y direction (negated)
   */ 
	
  //Save properties for the listeners to use for later
  element.isDraggable=true;
  element.dragTarget=target;
  element.noDragX=noDragX;
  element.noDragY=noDragY;
  
  //
  element.ondragstart=function(){return false;};
  element.onselectstart=function(){return false;};
  element.onmousedown=function(e){
    var doc=getWindow(element).document;
    stopDefault(e);
    // This was added in for touch screens tablets, commenting it out for divider custom menus
    if(e.stopPropagation!=null)
      e.stopPropagation();
    doc.body.focus(); 
    dragElement=getMouseTarget(e);
    if(dragElement.ondraggingStart)
      if(false==dragElement.ondraggingStart(dragElement,e))
    	  return;
    if(dragElement.dragTarget){
      dragStart=new Rect().readFromElementRelatedToParent(dragElement.dragTarget);
    }else
      dragStart=null;
    dragStartMouse=getMousePoint(e);
    doc.onmousemove=function(e2){
      stopDefault(e2);
    	
    	
      var point=getMousePoint(e2);
      var diffx=point.x-dragStartMouse.x;
      var diffy=point.y-dragStartMouse.y;
      if(dragStart){
        var rect=dragStart.clone().move(diffx,diffy);
        if(dragElement.clipDragging)
        	dragElement.clipDragging(dragElement,rect);
        if(!dragElement.noDragX)
          dragElement.dragTarget.style.left=toPx(rect.left);
        if(!dragElement.noDragY)
          dragElement.dragTarget.style.top=toPx(rect.top);
      }
      if(dragElement.ondragging)
        dragElement.ondragging(dragElement,diffx,diffy,e2);
      else if(target && target.ondragging)
        target.ondragging(dragElement,diffx,diffy,e2);
      doc.body.focus(); 
      return true;
    };
    doc.onmouseup=function(e2){
      var point=getMousePoint(e2);
      var diffx=point.x-dragStartMouse.x;
      var diffy=point.y-dragStartMouse.y;
      if(dragElement.ondraggingEnd)
        dragElement.ondraggingEnd(dragElement,diffx,diffy,e2);
      else if(target && target.ondraggingEnd)
        target.ondraggingEnd(target,diffx,diffy,e2);
      doc.onmousemove=null;
      doc.onmouseup=null;
      doc.ontouchmove=null;
      doc.ontouchend=null;
      doc.body.focus(); 
      return true;
    };
    doc.ontouchmove=doc.onmousemove;
    doc.ontouchend=doc.onmouseup;
    doc.body.focus();
  };
  //element.ontouchstart=element.onmousedown;
};

            	
var SWIPE_START_X;
var SWIPE_START_Y;
var SWIPE_TARGET;
var PADDING_FOR_TOUCH=20;
document.ontouchstart=function(event){
    var point=getMousePoint(event);
    var x=point.x;
    var y=point.y;
    MOUSE_POSITION_X = x;
    MOUSE_POSITION_Y = y;
	var element=document.elementFromPoint(x,y);
    for(var i=element;i!=null;i=i.parentNode){
    	if(i.onSwipe){
    		SWIPE_START_X=x;
    		SWIPE_START_Y=y;
    		SWIPE_TARGET=i;
            document.ontouchmove=function(event2){
              var point=getMousePoint(event2);
              SWIPE_TARGET.onSwipe(point.x-SWIPE_START_X,point.y-SWIPE_START_Y);
            }
            document.ontouchend=function(event2){
                var point=getMousePoint(event2);
                SWIPE_TARGET.onSwipeDone(point.x-SWIPE_START_X,point.y-SWIPE_START_Y);
    		    SWIPE_START_X=null;
    		    SWIPE_START_Y=null;
    		    SWIPE_TARGET=null;
            	document.ontouchmove=null;document.ontouchend=null;
            };
    		//log('onSwipe!!!');
    		return;
    	}
    }
    for(xO=0;xO<PADDING_FOR_TOUCH*2;xO++){
      for(yO=0;yO<PADDING_FOR_TOUCH*2;yO++){
	    var element=document.elementFromPoint(x+xO/(xO%2==0 ? 2 : -2),y+yO/(yO%2==0 ? 2 : -2));
	    if(isInput(element)){
	    	element.focus();
	    	return;
	    }
	    if(element.onmousedown==null && element.onclick==null)
	    	continue;
        stopDefault(event);
	    event={pageX:rd(x),pageY:rd(y),target:element,stopPropagation:function(){},button:1,ctrlKey:event.ctrlKey,shiftKey:event.shiftKey};
	    if(element.onmousedown){
		  element.onmousedown(event);
	    }else if(element.onclick){
		  element.onclick(event);
	    }
		  return;
      }
	}
	
}
document.ontouchstart2=function(event){
    var point=getMousePoint(event);
    var x=point.x;
    var y=point.y;
    var rect=new Rect();
	var all = document.getElementsByTagName("*");
	var candidates=[];
	for (var i=0, max=all.length; i < max; i++) {
		var element=all[i];
		if(element.onmousedown!=null || element.onclick!=null){
           rect.readFromElementRelatedToWindow(element);
           if(rect.insidePadded(x,y,PADDING_FOR_TOUCH)){
        	   candidates[candidates.length]=element;
           }
		}
	}
	if(candidates.length==0)
	  return;
	for(var i=0;i<candidates.length;i++){
	  for(var j=0;j<candidates.length;j++){
		 if(i!=j && isChildOf(candidates[i],candidates[j]) || isInfrontOf(candidates[i],candidates[j]) ){
			 candidates.splice(i--,1);
			 break;
		 }
	  }
	}
	
	var result;
	if(candidates.length==1){
	  result=candidates[0];
	}else{
	  outer:for(var n=0;;n++){
	    for(var i=0;i<candidates.length;i++){
	      var element=candidates[i];
          rect.readFromElementRelatedToWindow(element);
          if(rect.insidePadded(x,y,n)){
            result=element;
            break outer;
          }
        }
	  }
	}
	event.touchTarget=result;
	if(result.onmousedown!=null){
	  result.onmousedown(event);
      stopDefault(event);
    }else{
	  result.onclick(event);
      stopDefault(event);
	}
}

function isInfrontOf(a,b){
	var bz=null;
	for(var bb=b;bb!=null;bb=bb.parentNode){
		var z=bb.style!=null && bb.style.zIndex;
		if(z!=null && (bz==null || z>bz))
			bz=z;
	}
	if(bz==null)
	  return false;
	var az=null;
	for(var aa=a;aa!=null;aa=aa.parentNode){
		var z=aa.style!=null && aa.style.zIndex;
		if(z!=null && (az==null || z>az))
			az=z;
	}
	if(az==null || az<bz){
		return;
	}
	return false;
//    
//	
//	
//	if(az==null)
//		return false;
//	  for(var bb=b;bb!=null;bb=bb.parentNode)
//		  if(aa.parentNode==bb.parentNode)
//			return bb.style.zIndex!=null && (aa.style.zIndex==null || aa.style.zIndex*1 < bb.style.zIndex*1);
//  return false;
}
function isChildOf(parent,child){
	for(var i=child;i!=null;i=i.parentNode)
		if(i==parent)
			return true;
	return false;
}




function Array2d(width,height){
  
  this.data=new Array();
  this.setSize(width,height);
};

Array2d.prototype.data;
Array2d.prototype.width=0;
Array2d.prototype.height=0;

Array2d.prototype.setSize = function(width,height){
  if(width!=null)
    this.setWidth(width);
  if(height!=null)
    this.setHeight(height);
};


Array2d.prototype.setWidth = function(width){
  if(this.width>width){
    this.data.length=width;
    this.width=width;
  } else if(this.width<width){
    this.data.length=width;
    for(var i=this.width;i<width;i++)
      this.data[i]=new Array();
    this.width=width;
  }
};


Array2d.prototype.clear = function(){
    for(var i=0;i<this.width;i++){
      this.data[i]=[];
      this.data[i].length=this.height;
    }
};

Array2d.prototype.setHeight = function(height){
  if(height < 0){
	  height = 0;
  }
  if(height!=this.height){
    for(var i=0;i<this.width;i++)
      this.data[i].length=height;
    this.height=height;
  }
};

Array2d.prototype.remove = function(x,y){
	delete this.data[x][y];
};
Array2d.prototype.getWidth = function(){
  return this.width;
};

Array2d.prototype.getHeight = function(){
  return this.height;
};

Array2d.prototype.set = function(x,y,data){
  var t=this.data[x];
  var r=t[y];
  t[y]=data;
  return r;
};

Array2d.prototype.get = function(x,y){
  return this.data[x][y];
};

Array2d.prototype.ensureSize = function(x,y){
   if(x+1 > this.width)
      this.setWidth(x+1);
   if(y+1 > this.height)
      this.setHeight(y+1);

};

var mouseRepeatCount;
var mouseRepeatTimeout;
var mouseRepeatEvent;
var mouseRepeatElement;

function makeMouseRepeat(element){
  element.onmousedown = function(e){
      mouseRepeatElement=getMouseTarget(e);
      if(mouseRepeatElement.onMouseRepeat==null)
        return;
      mousedownCount=0;
      mouseRepeatEvent=e;
      mouseRepeatCount=0;
      if(mouseRepeatTimeout!=null)
          window.clearInterval(mouseRepeatTimeout);
      fireMouseRepeat(e);
      mouseRepeatTimeout = window.setInterval('fireMouseRepeat()', 100);
  };
  
  element.onmouseup = function(){
      if(mouseRepeatTimeout!=null)
          window.clearInterval(mouseRepeatTimeout);
      mouseRepeatCount=0;
      mouseRepeatTimeout=null;
      mouseRepeatEvent=null;
      mouseRepeatElement=null;
  };
  element.onmouseout=element.onmouseup;
  element.ontouchend=element.onmouseup;
};

function fireMouseRepeat(){
    if(mouseRepeatCount==0)
        mouseRepeatElement.onMouseRepeat(mouseRepeatEvent,mouseRepeatCount);
    else if(mouseRepeatCount > 3)
        mouseRepeatElement.onMouseRepeat(mouseRepeatEvent,mouseRepeatCount-3);
    mouseRepeatCount++;
};

function makeEnterable(element,button){
	element.onkeypress=function(e){if(e.keyCode==13) button.onclick(e);};
};

function removeAllChildren(element){
	while(element.hasChildNodes())
		element.removeChild(element.firstChild);
};
function makeEditable(element,enable){
	if(enable==false)
	  element.ondblclick=null;
	else
	  element.ondblclick=function(e){makeTextInput(getMouseTarget(e));};
};

function htmlToText(html){
	if(html==null)
		return "";
	html=html.replace(/\&nbsp\;/ig," ");
	html=html.replace(/\&lt\;/ig,"<");
	html=html.replace(/\&gt\;/ig,">");
	html=html.replace(/\&amp\;/ig,"&");
	return html;
}

function makeTextInput(e){
	var input=nw("input");
	input.origValue=e.innerHTML;
	input.value=htmlToText(e.innerHTML);
	e.innerHTML="";
	e.appendChild(input);
	input.focus();
	input.select();
	input.onblur=function(e){
	  var inp=getMouseTarget(e);
      inp.onblur=null;
      inp.onkeyup=null;
      var parent=inp.parentNode;
      parent.innerHTML=inp.origValue;
      parent.onEdit(inp.origValue,inp.value);
	};
	input.onkeyup=function(e){
	  var inp=getMouseTarget(e);
      if(e.keyCode==27){
        inp.onblur=null;
        inp.onkeyup=null;
        inp.parentNode.innerHTML=inp.origValue;
      } else if(e.keyCode==13){
        inp.onblur=null;
        inp.onkeyup=null;
        var parent=inp.parentNode;
        parent.innerHTML=inp.origValue;
        parent.onEdit(inp.origValue,inp.value);
      }
    };
};
function makeHelpable(element,help){
  if(help==null)
	  help='No help available at this time';
  if(element.className)
  element.className+=' help_label';
  else
  element.className=' help_label';
  element.help=help;
  element.onclick=showHelp;
};


var helpDiv=nw('div','help_popup');
{
	helpDiv.onmouseout=function(e){document.body.removeChild(getMouseTarget(e));};
};

function showHelp(e){
	var text=getMouseTarget(e).help;
	var p=getMousePoint(e);
	helpDiv.style.left=toPx(p.getX()-10);
	helpDiv.style.top=toPx(p.getY()-50);
	helpDiv.innerHTML=text;
	document.body.appendChild(helpDiv);
};

function makeErrorIcon(errors){
	  var element = nw('div', 'portlet_field_icon_error');
	  if(errors==null)
		  element.errors="No error";
	  else
		  element.errors=errors;
	
//	  element.onclick=showError;
	  element.onmouseover=showError;
	  
	  return element;
};


var errorDiv=nw('div','error_popup');
{
	errorDiv.onmouseout=function(e){document.body.removeChild(getMouseTarget(e));removeAllChildren(errorDiv)};
};

function showError(e){
	var errors=getMouseTarget(e).errors;
	var p=getMousePoint(e);
	errorDiv.style.left=toPx(p.getX()-10);
	errorDiv.style.top=toPx(p.getY()-50);
	
	var ul = nw("ul", "ul_disc");
	errorDiv.appendChild(ul);
	for( i in errors ){
		var l = nw("li", "ul_disc");
		l.innerHTML=errors[i];
		ul.appendChild(l);
	}
	
	document.body.appendChild(errorDiv);
};

function log(){
	var w = getMainWindow();
	if(w!=null & w.console!=null && w.console.log !=null)
		w.console.log(...arguments);
	else if(w!=null)
		w.alert([...arguments]);
	else
		alert([...arguments]);
}


function err(){
	var w = getMainWindow();
	if(w!=null & w.console!=null && w.console.error !=null)
		w.console.error(...arguments);
	else if(w!=null)
		w.alert([...arguments]);
	else
		alert([...arguments]);
}

function nwDiv(className,left,top,width,height,innerHTML){ 
	  var r=nw('div',className); 
	  r.style.left=toPx(left); 
	  r.style.top=toPx(top); 
	  r.style.width=toPx(width); 
	  r.style.height=toPx(height); 
	  if(innerHTML!=null)
		  r.innerHTML=innerHTML;
	  return r; 
} 


function fl(x){
	  return Math.floor(x);
}
function rd(x){
	if(x==null) return null;
	return Math.floor(x+.5);
}

function cl(x){
    return Math.ceil(x);
}

function sq(x){
    return x*x;
}

function ColorGradient(){
	this.clear();
}

ColorGradient.prototype.clear = function(value,color){
	this.colors=[];
	this.colorsSorted=[];
}

ColorGradient.prototype.colorsSorted = function(){
	return this.colorsSorted;
}

ColorGradient.prototype.clone = function(){
	var r=new ColorGradient();
	  for(var i in this.colors)
	    r.addStepRgb(+i,this.colors[i][0],this.colors[i][1],this.colors[i][2],this.colors[i][3]);
	return r;
}


ColorGradient.prototype.getStepForValue = function(value){
	for(var i=0;i<this.colorsSorted.length;i++){
		if(this.colorsSorted[i][0]==value)
			return i;
	}
	return -1;
}
ColorGradient.prototype.removeStepByValue=function(value){
	delete this.colors[+value];
	this.buildSortedList();
}

ColorGradient.prototype.addStepRgb = function(value,r,g,b,a){
	if(a==null || a==undefined)
		a=255;
	this.colors[+value]=[r,g,b,a];
	this.buildSortedList();
}
ColorGradient.prototype.addStep = function(value,color){
	this.colors[+value]=parseColor(color);
	this.buildSortedList();
}
ColorGradient.prototype.buildSortedList = function(){
	this.colorsSorted=[];
	var j=0;
	for(var i in this.colors){
		var rgb=this.colors[i];
		this.colorsSorted[j++]=[+i,rgb[0],rgb[1],rgb[2],rgb[3],toColor(rgb[0],rgb[1],rgb[2],rgb[3])];
	}
	this.colorsSorted.sort(function(a,b){return a[0]-b[0];});
}

ColorGradient.prototype.length = function(){
	return this.colorsSorted.length;
}
ColorGradient.prototype.getColorAtStep = function(i){
	  return this.colorsSorted[+i][5];
}
ColorGradient.prototype.getValueAtStep = function(i){
	return this.colorsSorted[+i][0];
}
ColorGradient.prototype.getColorsSorted = function(){
	return this.colorsSorted;
}
ColorGradient.prototype.getMinValue = function(i){
	return this.colorsSorted[0][0];
}
ColorGradient.prototype.getMaxValue = function(i){
	return this.colorsSorted[this.colorsSorted.length-1][0];
}


ColorGradient.prototype.toString = function(){
    var r="";
	for(var i=0;i<this.colorsSorted.length;i++){
	    if(i>0)
	      r+=",";
		r+=this.colorsSorted[i][0]+":"+this.colorsSorted[i][5];
	}
	return r;
}

ColorGradient.prototype.parseString = function(s){
    this.clear();
    var parts=s.split(',');
    for(var i=0;i<parts.length;i++){
      var parts2=parts[i].split(':');
      var value=parts2[0];
      var color=parts2[1];
	  this.colors[+value]=parseColor(color);
    }
    this.buildSortedList();
}


ColorGradient.prototype.toColor = function(val){
	var len=this.colorsSorted.length;
	var rgb1,rgb2;
	switch(len){
	   case 0:
	  return [0,0,0,255];
	   case 1:
	  var rgb=this.colorsSorted[0];
	  return [rgb[1],rgb[2],rgb[3],rgb[4]];
	   case 2:
	  rgb1=this.colorsSorted[0];
	  rgb2=this.colorsSorted[1];
	  break;
	   default:
	  for(var i=0;;i++){
		  if(i==len){
	          var rgb=this.colorsSorted[len-1];
	          return [rgb[1],rgb[2],rgb[3],rgb[4]];
		  }
		  if(val <= this.colorsSorted[i][0]){
			  rgb1=this.colorsSorted[i==0 ? i : i-1];
			  rgb2=this.colorsSorted[i];
			  break;
		  }
	  }
	 }
	 if(val<=rgb1[0])
	  return [rgb1[1],rgb1[2],rgb1[3],rgb1[4]];
	 else if(val>=rgb2[0])
	  return [rgb2[1],rgb2[2],rgb2[3],rgb2[4]];
	 var pct=(val-rgb1[0]) / (rgb2[0]-rgb1[0]);
	 return [
		   (rgb2[1]-rgb1[1])*pct+rgb1[1],
		   (rgb2[2]-rgb1[2])*pct+rgb1[2],
		   (rgb2[3]-rgb1[3])*pct+rgb1[3],
		   (rgb2[4]-rgb1[4])*pct+rgb1[4]
		   ];
}



function parseColor(color){
	if(color.charAt(0)=='#'){
	  color=color.substring(1);
	  if (color.length == 3) { // Support shorthand hexadecimal form
		  first = color.charAt(0);
		  second = color.charAt(1);
		  third = color.charAt(2);
		  color = first + first + second + second + third + third
	  }
	}else {
		var c=COLORS[color.toLowerCase()];
		if(c)
		  color=c.substring(1);
	}
	if(color.length==8){//includes alpha
	  var val=parseInt(color.substring(0,6),16);
	  var opc=parseInt(color.substring(6,8),16);
      return [ (val & 0xff0000)>>16 , (val &0x00ff00)>>8, val & 0x0000ff, opc & 0x0000ff];
	}else{
	  var val=parseInt(color,16);
	  return [ (val & 0xff0000)>>16 , (val &0x00ff00)>>8, val & 0x0000ff,255];
	}
}
function parseColorAlpha(color){
	if(color.charAt(0)=='#'){
	  color=color.substring(1);
	}else {
		var c=COLORS[color.toLowerCase()];
		if(c)
		  color=c.substring(1)+"FF";
	}
	//ensure color is in RGBA Format
	if(color.length == 6)
		color += "FF";
	var val=parseInt(color,16); 
	var t=(val & 0xff000000)>>24;
	if(t<0)
		t=255+t;
	return [ t , (val &0x00ff0000)>>16, (val & 0x0000ff00)>>8,(val & 0x000000ff) ];
}

function toColor(r,g,b,a){
  var rt='#';
  rt+=toColorPart(r);
  rt+=toColorPart(g);
  rt+=toColorPart(b);
  if(a!=undefined && a!=null && a<255)
    rt+=toColorPart(a);
  return rt;
}
function toColorPart(r){
  if(r<1)return '00'; else if(r>254)return 'ff';else if(r<16) return '0'+fl(r).toString(16); else return ''+fl(r).toString(16);
}


TENS=[1,10,100,1000,10000,100000, 1000000, 10000000, 100000000, 1000000000, 10000000000];
function roundDecimals(x,precision){
	return fl(x*TENS[precision])/TENS[precision];
}


function noNaN(val,dflt){
	return isNaN(val) ? dflt: val;
}


function colourNameToHex(colour)
{
    if (typeof colours[colour.toLowerCase()] != 'undefined')
    	return colours[colour.toLowerCase()];

    return false;
}

function resetAppliedStyles(target){
	var a=target.appliedStyles;
	if(a==null)
		return;
	for(var i in a){
		target.style[i]='';
	}
	target.appliedStyles={};
}

/**
 * val is pipe delimited list of:
 * _h = height
 * _w = width
 * _fg = foreground color
 * _bgi = background image
 * _bg = background color
 * _cna = class name append
 * _cn = class name 
 * _fs = font size
 * _fm = format which is comma delimited list of: strike,italic,bold,hide,center,left,right
 * style.* = apply style
 */
function applyStyle(target,val){
	if(target.appliedStyles==null)
	  target.appliedStyles={};
	var entries=(val==null) ? [] : val.split('\|');
	for(var i=0;i<entries.length;i++){
		var keyValue=entries[i].split(/=(.*)?/);
		var key=keyValue[0];
		var value=keyValue[1];
		if (value === "") 
			continue;
		if(key.charAt(0)=='_'){
			if(key=="_fg")
			  target.style.color=value;
			else if(key=="_h")
			  target.style.height=value;
			else if(key=="_w")
			  target.style.width=value;
			else if(key=="_br")
			  target.style.border=value;
			else if(key=="_bgi")
			  target.style.backgroundImage=value;
			else if(key=="_bg")
			  target.style.backgroundColor=value;
			else if(key=="_cna"){
			  target.classList.add(value);
			  target.cellClassName=value;
			}else if(key=="_cnr"){
			  target.classList.remove(value);
			}else if(key=="_cn"){
			  target.className=value;
			  target.cellClassName=value;
			}else if(key=="_fs"){
			  target.style.fontSize=toPx(value);
			}else if(key=="_fm"){
			   var formats = [];
			   if(value != null)
				   formats=value.split(',');
	           var underline=false,strike=false,italic=false,bold=false,font="",align="",hide=false;
	           var textTransform=null;
	           var blink=false;
	           var alignItems = null;
	           for(var j=0;j<formats.length;j++){
	        	   var fmt=formats[j];
	        	   switch(fmt.length){
	        	      case 4:
	        	        if(fmt=='bold') {bold=true;continue;}
	        	        if(fmt=='hide') {hide=true;continue;}
	        	        if(fmt=='left') {align='left'; alignItems="flex-start";continue;}
	        	        break;
	        	      case 5:
	        	        if(fmt=='blink') {blink=true;continue;}
	        	        if(fmt=='right')  {align='right'; alignItems="flex-end";continue;}
	        	        break;
	        	      case 6:
	        	        if(fmt=='strike') {strike=true;continue;}
	        	        if(fmt=='italic') {italic=true;continue;}
	        	        if(fmt=='center') {align='center'; alignItems="center";continue;}
	        	        break;
	        	      case 9:
	        		    if(fmt=="uppercase"){ textTransform="uppercase";continue;}
	        		    if(fmt=="lowercase"){ textTransform="lowercase";continue;}
	        	        if(fmt=='underline'){ underline=true;continue;}
	        		    break;
	        	      case 10:
	        		    if(fmt=="capitalize"){ textTransform="capitalize";continue;}
	        		    if(fmt=="normalcase") {textTransform="none";continue;}
	        		    break;
	        	   }
	        	   if(fmt.length>4)  font=fmt;/*TODO:validate font*/
	           }
	           target.style.fontFamily=font;
	           if(!underline && !strike) target.style.textDecoration='';
	           else if(underline && strike) target.style.textDecoration='underline line-through';
	           else target.style.textDecoration=strike ? 'line-through' : 'underline';
	           target.style.fontWeight=bold ? 'bold' : 'normal'; 
	           if(hide)
	             target.style.textIndent='-9999px';
	           else
	             target.style.textIndent='inherit'
	           target.style.textAlign=align;
	           if(alignItems) target.style.alignItems=alignItems;
	           target.style.fontStyle=italic ? 'italic' : '';
	           if(textTransform != null)
	             target.style.textTransform=textTransform;
        	   target.classList.toggle("ami_blink", blink);
			}
		}else if(key.indexOf('style.')==0){
			var k=key.slice(6);
			if(target.appliedStyles[k]!=value){  
			  target.appliedStyles[k]=value;
			}
			if(target.style[k]!=value){
				target.style[k]=value;
			}
		}else if(key=='className'){
			target.className+=' '+value;
			target.cellClassName=value;
		} else if (key=='disabled') {
			target.setAttribute('disabled', 'true');
		}
		else{
			target[key]=value;
		}
	}
}

/**
 * get the corresponding value given the key from the pipe delimetered style string: getStyleValue("_fg", "_fs=13|_fg=#e54949|_bg=#ffffff")
 */
function getStyleValue(key,style){
	var entries=(style==null) ? [] : style.split('\|');
	for(var i=0;i<entries.length;i++){
		var keyValue=entries[i].split(/=(.*)?/);
		var k=keyValue[0];
		var value=keyValue[1];
		if (value === "") 
			continue;
		if(k==key)
			return value;
	}
	return null;
}

function setCursorPosition(input, start, end) {
    if (arguments.length < 3) end = start;
    if ("selectionStart" in input) {
        setTimeout(function() {
            input.selectionStart = start;
            input.selectionEnd = end;
        }, 1);
    }
    else if (input.createTextRange) {
        var rng = input.createTextRange();
        rng.moveStart("character", start);
        rng.collapse();
        rng.moveEnd("character", end - start);
        rng.select();
    }
}


function getCursorPosition(input) {
    if ("selectionStart" in input) {
    	return input.selectionStart;
        //return {
            //start: input.selectionStart,
            //end: input.selectionEnd
        //};
    }
    else if (input.createTextRange && document.selection!=null) {
        var sel = document.selection.createRange();
        if (sel.parentElement() === input) {
            var rng = input.createTextRange();
            rng.moveToBookmark(sel.getBookmark());
            for (var len = 0;
                     rng.compareEndPoints("EndToStart", rng) > 0;
                     rng.moveEnd("character", -1)) {
                len++;
            }
            rng.setEndPoint("StartToStart", input.createTextRange());
            for (var pos = { start: 0, end: len };
                     rng.compareEndPoints("EndToStart", rng) > 0;
                     rng.moveEnd("character", -1)) {
                pos.start++;
                pos.end++;
            }
            return pos;
        }
    }
    return -1;
}

function setSelection(input, start, end){
	setCursorPosition(input,start,end);
}

function getIndexOf (array, item, from) {
    if (array.prototype && array.prototype.indexOf) // Use the native array method if available
        return array.indexOf(item, from);
    for (var i = from || 0; i < array.length; i++) {
        if (array[i] === item)
            return i;
    }
    return -1;
}

function isEmptyObject(obj) {
  for ( var name in obj ) 
    return false;
  return true;
}

function joinKeys(delim,obj){
	r="";
	var first=true;
	for(var i in obj){
		if(first)
			first=false;
		else
			r=r.concat(delim);
		r=r.concat(i);
	}
	return r;
}

function diff(a,b){
	return a<b ? b-a : a-b;
}

function isFullScreen(){
  return !!(document.fullscreenElement || document.mozFullScreenElement || document.webkitFullscreenElement || document.msFullscreenElement);
}

function toggleFullScreen(){
	setFullScreen(!isFullScreen());
}

function setFullScreen(on) {
  if(on){
    if (document.documentElement.requestFullscreen)
      document.documentElement.requestFullscreen();
    else if (document.documentElement.msRequestFullscreen)
      document.documentElement.msRequestFullscreen();
    else if (document.documentElement.mozRequestFullScreen)
      document.documentElement.mozRequestFullScreen();
    else if (document.documentElement.webkitRequestFullscreen)
      document.documentElement.webkitRequestFullscreen(Element.ALLOW_KEYBOARD_INPUT);
    else
      return false;
  } else {
    if (document.exitFullscreen) 
      document.exitFullscreen();
    else if (document.msExitFullscreen) 
      document.msExitFullscreen();
    else if (document.mozCancelFullScreen) 
      document.mozCancelFullScreen();
    else if (document.webkitExitFullscreen) 
      document.webkitExitFullscreen();
    else 
      return false;
  }
  return true;
}

function fallbackCopyTextToClipboard(text) {
  var textArea = document.createElement("textarea");
  textArea.value = text;
  textArea.style.top = "0";
  textArea.style.left = "0";
  textArea.style.position = "fixed";
  document.body.appendChild(textArea);
  textArea.focus();
  textArea.select();

  try {
    document.execCommand('copy');
  } catch (err) {
    console.error('Fallback: unable to copy', err);
  }

  document.body.removeChild(textArea);
}
function copyToClipboard(text) {
  if (!navigator.clipboard) {
    fallbackCopyTextToClipboard(text);
    return;
  }
  try {
    navigator.clipboard.writeText(text);
  } catch (err) {
    console.error('Fallback: Unable to copy', err);
  }
}

var IMAGE_BUFFER_CANVAS=null;
var IMAGE_BUFFER_CONTEXT=null;
var IMAGE_BUFFER_DATA=null;
function toImageData(e,r,g,b,a){
	if(IMAGE_BUFFER_CANVAS==null){
      var IMAGE_BUFFER_CANVAS=getDocument(e).createElement('canvas');
      IMAGE_BUFFER_CANVAS.width=1;
      IMAGE_BUFFER_CANVAS.height=1;
      IMAGE_BUFFER_CONTEXT = IMAGE_BUFFER_CANVAS.getContext('2d');
       IMAGE_BUFFER_DATA = IMAGE_BUFFER_CONTEXT.createImageData(2,2);
	}
	var d  = IMAGE_BUFFER_DATA.data;
	d[0]   = r;
	d[1]   = g;
	d[2]   = b;
	d[3]   = a;
    IMAGE_BUFFER_CONTEXT.putImageData( IMAGE_BUFFER_DATA, 0, 0 );
    return "url('"+IMAGE_BUFFER_CANVAS.toDataURL('png')+"')";
}

function setCssClassProperty(e,className,key,value){
  var style = getDocument(e).createElement('style');
  style.type = 'text/css';
  style.innerHTML = '.'+className+'{ '+key+': '+value+'; }';
  getDocument(e).getElementsByTagName('head')[0].appendChild(style);
}

function getWindowParam(theWindow,key,dflt){
  var search=theWindow.location.search;
  var start=search.indexOf('?'+key+'=');
  if(start==-1){
    start=search.indexOf('&'+key+'=');
    if(start==-1)
  	  return dflt;
  }
  start+=key.length+2;
  var end=search.indexOf('&',start);
  if(end==-1)
     end=search.length;
  return search.substring(start,end);
}

function addClassName(target,cn,first){
	if(cn==null || cn=="")
		return;
	var cn2=target.className;
	var r;
	if(cn2==null || cn2=="" || cn2==cn){
		r=cn;
	}else{
		r=first ? cn : "";
		var parts=cn2.split(' +');
		for(var i in parts){
			var t=parts[i];
			if(t==cn)
				continue;
			if(r!="")
				r+=" ";
			r+=t;
		}
		if(!first)
			r+=" "+cn;
	}
	target.className=r;
}
function removeClassName(target,cn){
	if(cn==null || cn=="")
		return;
	var cn2=target.className;
	var r;
	var r="";
	if(cn2!=cn){
	  r="";
	  var parts=cn2.split(' +');
	  for(var i in parts){
		var t=parts[i];
	    if(t==cn)
	      continue;
	    if(r!="")
	      r+=" ";
	    r+=t;
	  }
	  target.className=r;
	}
	target.className="";
}

function dis(x,y,xx,yy){
	return Math.sqrt(sq(x-xx)+sq(y-yy));
}

function rotateDivElement(divElement,rotate,width,height,xpos,ypos){
  while(rotate>=360)
    rotate-=360;
  while(rotate<0)
    rotate+=360;
  var style=divElement.style;
  if(rotate==0){
    style.transform=null;
    style.height=toPx(height);
    style.width=toPx(width);
    style.left=toPx(xpos);
    style.top=toPx(ypos);
  }else if(rotate==270){
    var diff=fl((height-width)/2);
    var extra=Math.abs(height-width)%2;
    style.transform='rotate(-90deg)';
    style.height=toPx(width+extra);
    style.width=toPx(height);
    style.top=toPx(diff+ypos);
    style.left=toPx(-diff+xpos);
  }else if(rotate==90){
    var diff=fl((height-width)/2);
    var extra=Math.abs(height-width)%2;
    style.transform='rotate(90deg)';
    style.height=toPx(width+extra);
    style.width=toPx(height);
    style.top=toPx(diff+ypos);
    style.left=toPx(-diff-extra+xpos);
  } else if(rotate==180){
    style.transform='rotate(180deg)';
    style.height=toPx(height+ypos);
    style.width=toPx(width+xpos);
  }
}

function noNull(a,b){
	return a==null ? b: a;
}

function Glass(window, caller){
  var that = this;
  this.window=window;
  this.caller=caller;
  this.visible=false;
  this.glassElement=nw("div", "disable_glass_clear");
  this.glassElement.onclick = function(e) {that.hide();};
}
currentGlass = null;
Glass.prototype.setClassName=function(name){
	this.glassElement.className=name; 
}
Glass.prototype.show=function(){
	if(this.visible===false){
		this.window.document.body.appendChild(this.glassElement);
		this.visible=true;
		currentGlass = this;
	}
}
Glass.prototype.hide=function(){
	if(this.visible===true){
		if(this.caller.hideGlass)
			this.caller.hideGlass();
		this.window.document.body.removeChild(this.glassElement);
		this.visible=false;
		currentGlass = null;
	}
}
Glass.prototype.appendChild=function(e){
	this.glassElement.appendChild(e);
}
Glass.prototype.handleKeydown=function(e){
	if(e.key === "Escape" && e.shiftKey == false && e.ctrlKey == false && e.altKey == false){
		this.hide();
	}
}




function clip(x,min,max){
  return x<min ? min : (x>max ? max : x);
}

function flipRotation(axis,angle){
  return mod(axis+axis-angle,360);
}
function mod(value, modulous) {
  return (value %= modulous) < 0 ? modulous + value : value;
}


function deref(array,idx){
	if(array==null)
		return null;
	return array.length==1 ? array[0] : array[idx];
}

function add(a,b){
	return (a==null || b==null) ? null : a+b;
}
function sub(a,b){
	return (a==null || b==null) ? null : a-b;
}
function getOwningPortlet(element){
	if(element == null)
		return null;
	if(element.isPortletElement == true)
		return element.portlet;
	var e = element.parentNode;
	while(e!=null && (e.isPortletElement == undefined || e.isPortletElement == false))
		e=e.parentNode;
	if(e!=null)
		return e.portlet;
	else
		return null;
}

function escapeHtmlAdv(text, start, end, includeBackslash, replaceNewLineWith, sb){
	for (var i = start; i < end; i++) {
		var c = text.charAt(i);
		switch (c) {
			case '\'':
				sb.push("&#39;");
				break;
			case '"':
				sb.push("&#34;");
				break;
			case '\\':
				if (includeBackslash) {
					sb.push("&#92;");
				} else {
					if (++i < end) {
						if (text.charAt(i) == '\\') {
							sb.push("&#92;");
						} else {
							sb.push(text.charAt(i));
						}
					}
				}
				break;
			case '\n':
				sb.push(replaceNewLineWith);
				break;
			case '\r':
				break;
			case ' ':
				sb.push("&nbsp;");
				break;
			case '\t':
				sb.push("&nbsp;&nbsp;");
				break;
			case '>':
				sb.push("&gt;");
				break;
			case '<':
				sb.push("&lt;");
				break;
			case '&':
				sb.push("&amp;");
				break;
			case 0:
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
			case 7:
			case 8:
				sb.push("&#191;");
				break;
			default:
				sb.push(c);
		}

	}
	return sb.join('');
}
function escapeHtml(text){
	return escapeHtmlAdv(text, 0, text.length, true, "\\n", []);
}

function LiveDebugger(element, options){
	var that=this;
	this.__doc = null;
	this.__doc = element == null? document : getDocument(element);
	this.metricElements=new Map();
	this.metrics=new Map();
	this.__doc.addEventListener("readystatechange", function(e){if(e.target.readyState=="complete") that.init(null);});
}
LiveDebugger.prototype.metricElements;
LiveDebugger.prototype.metrics;
LiveDebugger.prototype.init=function(options){
	this.__options=options==null?{}:options;
	this.debugBox = nw("div");
	this.__doc.body.appendChild(this.debugBox);
	this.debugBox.style.border="1px solid black";
	this.debugBox.style.zIndex="2000";
	this.debugBox.style.minWidth="200px";
	this.debugBox.style.minHeight="200px";
	this.debugBox.style.background="#eaeaea";
	this.debugBox.style.right="0px";
	this.debugBox.style.top="28px";
}
LiveDebugger.prototype.updateStat=function(elem, key, value){
	elem.textContent = key + " = " + value;
}
LiveDebugger.prototype.removeStat=function(key){
	this.metrics.delete(key);
	var elem = this.metricElements.get(key);
	var r = this.metricElements.delete(key);
	elem.remove();
}
LiveDebugger.prototype.addStat=function(key, value){
	var nwMetric = nw("div");
	nwMetric.style.position="relative";
	this.debugBox.appendChild(nwMetric);

	this.metrics.set(key,value);
	this.metricElements.set(key, nwMetric);
	this.updateStat(nwMetric, key, value);

	if(this.__options.disableSorting != true)
		this.sortStats();
}
LiveDebugger.prototype.onStat=function(key, value){
	if(typeof value === 'object')
		value = JSON.stringify(value);
		
	if(this.metrics.has(key)){
		var elem = this.metricElements.get(key);
		this.updateStat(elem,key,value);
	}
	else
		this.addStat(key, value);
}
LiveDebugger.prototype.sortStats=function(){
	var keys = Array.from(this.metrics.keys()).sort();
	this.metricElements.forEach(function(value,key) { value.remove(); } );
	var that=this;
	keys.forEach(function(key){ 
		var elem = that.metricElements.get(key);
		that.debugBox.appendChild(elem);
	} );
}

// liveDebugger = new LiveDebugger(null, null);
// liveDebugger.onStat("metric", value);

//Get the root node's body from the given element
function getRootNodeBody(element) { 
	return element.getRootNode().body;
}

function parseNumber(value, locales = navigator.languages) {
	const example = Intl.NumberFormat(locales).format('1.1'); // example of formatting an en-us number according to the locale supplied
	const cleanPattern = new RegExp(`[^-+0-9${ example.charAt( 1 ) }]`, 'g'); // build regex (includes the locale-specific decimal representation in the regex)
	const cleaned = value.replace(cleanPattern, ''); // filter out anything that's not - + 0-9 and the decimal rep in the input
	const normalized = cleaned.replace(example.charAt(1), '.'); // replace the locale-specific decimal rep to . (which is en-us)

	return parseFloat(normalized);
}

function hexToRgb(hex) {
    var bigint = parseInt(hex, 16);
    var r = (bigint >> 16) & 255;
    var g = (bigint >> 8) & 255;
    var b = bigint & 255;

    return r + ", " + g + ", " + b;
}

function formatDate(format, selDate, timeMs) {
	// most detailed: weekday day/month/year
	if (!format)
		return '';
	var day=selDate.getDate();
	day = day < 10? '0'+day:day;
	const fullYear=selDate.getFullYear();
	const year=fullYear.toString().substr(2); // get last 2 digits
	const month=selDate.getMonth()+1; // index
	const fullMonth=month < 10? '0'+month:month;
	const weekDayName=weekdayNames[selDate.getDay()];
	const monthName=monthNames[selDate.getMonth()];
	const monthNameShortened=monthNamesShortened[selDate.getMonth()];
	var formated='';

	switch (format) {
	case 'M/dd/yyyy': // 3/14/2015
		formated=month+'/'+day+'/'+fullYear;
		break;
	case 'MM/dd/yyyy': // 03/14/2015
		formated=fullMonth+'/'+day+'/'+fullYear;
		break;
	case 'M/dd': // 3/14
		formated=month+'/'+day;
		break;
	case 'M/dd/yy': // 3/14/15
		formated=month+'/'+day+'/'+year;
		break;
	case 'MM/dd/yy': // 03/14/15
		formated=fullMonth+'/'+day+'/'+year;
		break;
	case 'MMMM dd, yyyy': // March 14, 2015
		formated=monthName+' '+day+', '+fullYear;
		break;
	case 'EEEE, MMMM dd, yyyy': // Saturday, March 14, 2015
		formated=weekDayName+', '+monthName+' '+day+', '+fullYear;
		break;
	case 'dd-MMM': // 14-Mar
		formated=day+'-'+monthNameShortened;
		break;
	case 'dd-MMM-yy': // 14-Mar-15
		formated=day+'-'+monthNameShortened+'-'+year;
		break;
	case 'MMM-yy': // Mar-15
		formated=monthNameShortened+'-'+year;
		break;
	case 'MMMM-yy': // March-15
		formated=monthName+'-'+year;
		break;
	case 'yyyy/MM/dd': // 2015/03/14
		formated=fullYear+'/'+fullMonth+'/'+day;
		break;
	case 'dd/MM/yyyy': // 14/03/2015
		formated=day+'/'+fullMonth+'/'+fullYear;
		break;
	default: // 20150314
		break;
	}
	return formated;
}


function formatTime(format, hour, minute) {
	if (!format)
		return '';
	var formated='';
	var meridian=hour > 11?'PM':'AM';
	var shortHour= hour %12; // 0-12
	if (shortHour==0) {
		shortHour=12;
	}
	const fullHour= hour < 10?'0'+ hour:hour;
	const shortFull= shortHour < 10?'0'+ shortHour:shortHour;
	var fullMinutes=minute<10?'0'+minute:minute;
	switch(format){
	case 'h:mm a': // 1:30 PM
		formated = shortHour+':'+fullMinutes+' '+meridian;
		break;
	case 'HH:mm': // 13:30
		formated = fullHour+':'+fullMinutes;
		break;
	case 'hh:mm a': // 01:30 PM 
		formated=shortFull+':'+fullMinutes+' '+ meridian;
		break;
	case 'H:mm': // 1:30
		formated=hour+':'+fullMinutes;
		break;
	default:
		console.log('invalid format: ', format); // should never happen
		break;
	}
	return formated;
}

function pxToInt(str) {
	if (typeof str == 'number')
		return str;
	return parseInt(fromPx(str));
}

function setBrowserURL(url){
    window.history.replaceState({}, document.title,url=='' ? location.pathname : url);
}

// used by calendar fields to ensure proper display of date/time
//function ensureFieldContentVisible(element, content) {
//	if (element ==null)
//		return null;
//	const cs=getComputedStyle(element);
//	const paddings=parseInt(fromPx(cs.paddingLeft)) + parseInt(fromPx(cs.paddingRight));
//	const margins=parseInt(fromPx(cs.marginLeft)) + parseInt(fromPx(cs.marginRight));
//	const borders=parseInt(fromPx(cs.borderLeftWidth)) + parseInt(fromPx(cs.borderRightWidth));
//	const oWidth=pxToInt(cs.width);
//	const totalOriginalWidth=oWidth+paddings+margins+borders;
//	// ensure no extra space
//	if (!content) {
////		element.style.width=oWidth;
////		element.style.minWidth=oWidth;
//		// returns original width allocated
//		return totalOriginalWidth;
//	}
//	const textWidth=getFieldContentSize(element,content);
//	// account for paddings
//	const w=Math.round(textWidth) + paddings+margins;
//	// if original width can accommodate the text;
//	if (oWidth > w) {
//		// no need to adjust
////		element.style.width=w;
////		element.style.minWidth=w;
//		return totalOriginalWidth;
//	}
////	element.style.width=toPx(w);
////	element.style.minWidth=toPx(w);
//	// returns total width allocated
//	return w;
//}

//function getFieldContentSize(element, text) {
//	const canvas = getTextWidth.canvas || (getTextWidth.canvas = document.createElement("canvas")); 
//	const context = canvas.getContext("2d");
//	context.font=element.style.fontSize+ ' ' + element.style.fontFamily;
//	// compute text width
//	const metrics=context.measureText(text);
//	return metrics.width;
//}