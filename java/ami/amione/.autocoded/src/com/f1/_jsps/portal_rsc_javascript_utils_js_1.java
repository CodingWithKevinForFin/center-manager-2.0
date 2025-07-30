package com.f1._jsps;

import com.f1.http.*;
import com.f1.utils.*;
import com.f1.http.handler.AbstractHttpHandler;
import com.f1.http.HttpUtils;
import static com.f1.http.HttpUtils.*;

public class portal_rsc_javascript_utils_js_1 extends AbstractHttpHandler{

	public portal_rsc_javascript_utils_js_1() {
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
            "//################\r\n"+
            "//##### init #####\r\n"+
            "\r\n"+
            "function wheel(event,passive){\r\n"+
            "    var delta = 0;\r\n"+
            "    if (!event) /* For IE. */\r\n"+
            "            event = window.event;\r\n"+
            "    if(event.consumed)\r\n"+
            "    	return;\r\n"+
            "    event.consumed=true;\r\n"+
            "    if (event.wheelDelta) { /* IE/Opera. */\r\n"+
            "            delta = event.wheelDelta/120;\r\n"+
            "    } else if (event.detail) { /* Mozilla case. */\r\n"+
            "            delta = -event.detail/3;\r\n"+
            "    }\r\n"+
            "    if (delta)\r\n"+
            "        fireOnMouseWheel(event,delta);\r\n"+
            "    if (passive!=true && event.preventDefault)\r\n"+
            "        event.preventDefault();\r\n"+
            "//    event.returnValue = false;\r\n"+
            "    return false;\r\n"+
            "};\r\n"+
            "\r\n"+
            "var IS_LITTLE_ENDIAN;\r\n"+
            "{\r\n"+
            "  var buf=new ArrayBuffer(8);\r\n"+
            "  var data = new Uint32Array(buf);\r\n"+
            "  //Determine whether Uint32 is little- or big-endian.\r\n"+
            "  data[1] = 0x0a0b0c0d;\r\n"+
            "  var isLittleEndian = true;\r\n"+
            "  if (buf[4] === 0x0a && buf[5] === 0x0b && buf[6] === 0x0c && buf[7] === 0x0d) {\r\n"+
            "    IS_LITTLE_ENDIAN=false;\r\n"+
            "  }else\r\n"+
            "    IS_LITTLE_ENDIAN=true;\r\n"+
            "}\r\n"+
            "\r\n"+
            "var SVG_PREFIX='url(\\'data:image/svg+xml;utf8,<svg version=\"1.1\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" ';\r\n"+
            "var SVG_SUFFIX='</svg>\\')';\r\n"+
            "var COLORS = {\"aliceblue\":\"#f0f8ff\",\"antiquewhite\":\"#faebd7\",\"aqua\":\"#00ffff\",\"aquamarine\":\"#7fffd4\",\"azure\":\"#f0ffff\",\"beige\":\"#f5f5dc\",\"bisque\":\"#ffe4c4\",\"black\":\"#000000\",\"blanchedalmond\":\"#ffebcd\",\r\n"+
            "              \"blue\":\"#0000ff\",\"blueviolet\":\"#8a2be2\",\"brown\":\"#a52a2a\",\"burlywood\":\"#deb887\",\"cadetblue\":\"#5f9ea0\",\"chartreuse\":\"#7fff00\",\"chocolate\":\"#d2691e\",\"coral\":\"#ff7f50\",\"cornflowerblue\":\"#6495ed\",\r\n"+
            "              \"cornsilk\":\"#fff8dc\",\"crimson\":\"#dc143c\",\"cyan\":\"#00ffff\",\"darkblue\":\"#00008b\",\"darkcyan\":\"#008b8b\",\"darkgoldenrod\":\"#b8860b\",\"darkgray\":\"#a9a9a9\",\"darkgreen\":\"#006400\",\"darkkhaki\":\"#bdb76b\",\r\n"+
            "              \"darkmagenta\":\"#8b008b\",\"darkolivegreen\":\"#556b2f\",\"darkorange\":\"#ff8c00\",\"darkorchid\":\"#9932cc\",\"darkred\":\"#8b0000\",\"darksalmon\":\"#e9967a\",\"darkseagreen\":\"#8fbc8f\",\"darkslateblue\":\"#483d8b\",\r\n"+
            "              \"darkslategray\":\"#2f4f4f\",\"darkturquoise\":\"#00ced1\",\"darkviolet\":\"#9400d3\",\"deeppink\":\"#ff1493\",\"deepskyblue\":\"#00bfff\",\"dimgray\":\"#696969\",\"dodgerblue\":\"#1e90ff\",\"firebrick\":\"#b22222\",\r\n"+
            "              \"floralwhite\":\"#fffaf0\",\"forestgreen\":\"#228b22\",\"fuchsia\":\"#ff00ff\", \"gainsboro\":\"#dcdcdc\",\"ghostwhite\":\"#f8f8ff\",\"gold\":\"#ffd700\",\"goldenrod\":\"#daa520\",\"gray\":\"#808080\",\"green\":\"#008000\",\r\n"+
            "              \"greenyellow\":\"#adff2f\",\"honeydew\":\"#f0fff0\",\"hotpink\":\"#ff69b4\",\"indianred \":\"#cd5c5c\",\"indigo \":\"#4b0082\",\"ivory\":\"#fffff0\",\"khaki\":\"#f0e68c\",\"lavender\":\"#e6e6fa\",\"lavenderblush\":\"#fff0f5\",\r\n"+
            "              \"lawngreen\":\"#7cfc00\",\"lemonchiffon\":\"#fffacd\",\"lightblue\":\"#add8e6\",\"lightcoral\":\"#f08080\",\"lightcyan\":\"#e0ffff\",\"lightgoldenrodyellow\":\"#fafad2\",\"lightgrey\":\"#d3d3d3\",\"lightgreen\":\"#90ee90\",\r\n"+
            "              \"lightpink\":\"#ffb6c1\",\"lightsalmon\":\"#ffa07a\",\"lightseagreen\":\"#20b2aa\",\"lightskyblue\":\"#87cefa\",\"lightslategray\":\"#778899\",\"lightsteelblue\":\"#b0c4de\",\"lightyellow\":\"#ffffe0\",\"lime\":\"#00ff00\",\r\n"+
            "              \"limegreen\":\"#32cd32\",\"linen\":\"#faf0e6\",\"magenta\":\"#ff00ff\",\"maroon\":\"#800000\",\"mediumaquamarine\":\"#66cdaa\",\"mediumblue\":\"#0000cd\",\"mediumorchid\":\"#ba55d3\",\"mediumpurple\":\"#9370d8\",\r\n"+
            "              \"mediumseagreen\":\"#3cb371\",\"mediumslateblue\":\"#7b68ee\",\"mediumspringgreen\":\"#00fa9a\",\"mediumturquoise\":\"#48d1cc\",\"mediumvioletred\":\"#c71585\",\"midnightblue\":\"#191970\",\"mintcream\":\"#f5fffa\",\r\n"+
            "              \"mistyrose\":\"#ffe4e1\",\"moccasin\":\"#ffe4b5\",\"navajowhite\":\"#ffdead\",\"navy\":\"#000080\",\"oldlace\":\"#fdf5e6\",\"olive\":\"#808000\",\"olivedrab\":\"#6b8e23\",\"orange\":\"#ffa500\",\"orangered\":\"#ff4500\",\r\n"+
            "              \"orchid\":\"#da70d6\",\"palegoldenrod\":\"#eee8aa\",\"palegreen\":\"#98fb98\",\"paleturquoise\":\"#afeeee\",\"palevioletred\":\"#d87093\",\"papayawhip\":\"#ffefd5\",\"peachpuff\":\"#ffdab9\",\"peru\":\"#cd853f\",\r\n"+
            "              \"pink\":\"#ffc0cb\",\"plum\":\"#dda0dd\",\"powderblue\":\"#b0e0e6\",\"purple\":\"#800080\",\"red\":\"#ff0000\",\"rosybrown\":\"#bc8f8f\",\"royalblue\":\"#4169e1\",\"saddlebrown\":\"#8b4513\",\"salmon\":\"#fa8072\",\r\n"+
            "              \"sandybrown\":\"#f4a460\",\"seagreen\":\"#2e8b57\",\"seashell\":\"#fff5ee\",\"sienna\":\"#a0522d\",\"silver\":\"#c0c0c0\",\"skyblue\":\"#87ceeb\",\"slateblue\":\"#6a5acd\",\"slategray\":\"#708090\",\"snow\":\"#fffafa\",\r\n"+
            "              \"springgreen\":\"#00ff7f\",\"steelblue\":\"#4682b4\",\"tan\":\"#d2b48c\",\"teal\":\"#008080\",\"thistle\":\"#d8bfd8\",\"tomato\":\"#ff6347\",\"turquoise\":\"#40e0d0\",\"violet\":\"#ee82ee\",\"wheat\":\"#f5deb3\",\"white\":\"#ffffff\",\r\n"+
            "              \"whitesmoke\":\"#f5f5f5\",\"yellow\":\"#ffff00\",\"yellowgreen\":\"#9acd32\"};\r\n"+
            "var SVG_ERROR_BANNER = '<svg id=\"Layer_1\" data-name=\"Layer 1\" xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"0 0 400 150\"><defs><style>.cls-2{fill:#f06b22;}.cls-3{fill:#1092bc;}.cls-4{fill:#7bcdf3;}.cls-5{fill:none;stroke:#545252;stroke-linecap:round;stroke-miterlimit:10;stroke-width:1.61px;}</style></defs>'\r\n"+
            "						+'<circle class=\"cls-1\" cx=\"203.72\" cy=\"74.38\" r=\"71.56\" /><g id=\"Page-1\"><g id=\"Group\"><rect id=\"Rectangle\" class=\"cls-2\" x=\"188.97\" y=\"24.77\" width=\"29.81\" height=\"29.81\" rx=\"6.62\" transform=\"translate(3.3 93.6) rotate(-26.06)\" /><rect id=\"Rectangle-2\" class=\"cls-2\" x=\"224.89\" y=\"84.22\" width=\"29.81\" height=\"29.81\" rx=\"6.62\" transform=\"translate(-17.9 129.43) rotate(-29.17)\" /><rect id=\"Rectangle-3\" class=\"cls-3\" x=\"205.61\" y=\"54.79\" width=\"29.81\" height=\"29.81\" rx=\"6.62\" transform=\"translate(115.88 275.93) rotate(-80.64)\" /><rect id=\"Rectangle-4\" class=\"cls-4\" x=\"165.73\" y=\"51.96\" width=\"29.81\" height=\"29.81\" rx=\"6.62\" transform=\"translate(-10.73 44.95) rotate(-13.78)\" /><rect id=\"Rectangle-5\" class=\"cls-3\" x=\"154.54\" y=\"85.02\" width=\"29.81\" height=\"29.81\" rx=\"6.62\" transform=\"translate(10.33 215.59) rotate(-66.54)\" /><rect id=\"Rectangle-6\" class=\"cls-2\" x=\"189.54\" y=\"87.28\" width=\"29.81\" height=\"29.81\" rx=\"6.62\" transform=\"translate(-1.96 4.02) rotate(-1.12)\" /></g></g>'\r\n"+
            "						+ '<line class=\"cls-5\" x1=\"237.74\" y1=\"72.5\" x2=\"239.85\" y2=\"66.47\" /><line class=\"cls-5\" x1=\"240.5\" y1=\"74.83\" x2=\"247.68\" y2=\"69.92\" /><line class=\"cls-5\" x1=\"242.22\" y1=\"78.37\" x2=\"247.65\" y2=\"77.51\" /><line class=\"cls-5\" x1=\"178.66\" y1=\"48.87\" x2=\"172.47\" y2=\"47.33\" /><line class=\"cls-5\" x1=\"180.73\" y1=\"45.9\" x2=\"175.18\" y2=\"39.22\" /><line class=\"cls-5\" x1=\"184.09\" y1=\"43.87\" x2=\"182.73\" y2=\"38.54\" />'\r\n"+
            "						+ '</svg>';\r\n"+
            "var SVG_LOGO_WHITE = '<svg xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"0 0 575.35 153.35\"><defs><style>.cls-1{fill:#fff;}</style></defs><g id=\"Layer_2\" data-name=\"Layer 2\"><g id=\"Layer_1-2\" data-name=\"Layer 1\"><g id=\"Page-1\"><g id=\"Group\"><g id=\"_3forge\" data-name=\" 3forge\"><path class=\"cls-1\" d=\"M222.89,80.74a46.76,46.76,0,0,1-3.08,17A34.63,34.63,0,0,1,206,114.85,40.64,40.64,0,0,1,183.27,121a40.36,40.36,0,0,1-22.79-6.42A36,36,0,0,1,146.54,97a49,49,0,0,1-2.73-13.49A2.58,2.58,0,0,1,146,80.54a2.81,2.81,0,0,1,.71,0h18.44a2.82,2.82,0,0,1,3.07,2.56,1.53,1.53,0,0,1,0,.37A26.76,26.76,0,0,0,169.93,91,13.22,13.22,0,0,0,175,97.87a13.82,13.82,0,0,0,8.28,2.48A13.15,13.15,0,0,0,196.41,92a28.68,28.68,0,0,0,2.37-12,30.23,30.23,0,0,0-2.72-13.14A13.24,13.24,0,0,0,182.92,59a15.13,15.13,0,0,0-7.83,3.24,4,4,0,0,1-1.72.5A3.1,3.1,0,0,1,171,61.59l-9-12.13a3.37,3.37,0,0,1-.71-2.07,3,3,0,0,1,1-2l26.63-23.24a.61.61,0,0,0,.35-.76c0-.31-.4-.46-.85-.46H149A2.56,2.56,0,0,1,146,18.74a2.41,2.41,0,0,1,0-.71V3A2.58,2.58,0,0,1,148.26.14a2.73,2.73,0,0,1,.7,0h69.48a2.59,2.59,0,0,1,2.93,2.18,2.73,2.73,0,0,1,0,.7V19.75a5.07,5.07,0,0,1-1.56,3.59L197.42,43.85c-.66.71-.45,1.17.71,1.37A28.38,28.38,0,0,1,219.45,62.8,45.05,45.05,0,0,1,222.89,80.74Z\"/><path class=\"cls-1\" d=\"M280.24,34.61v14.3a2.58,2.58,0,0,1-2.18,2.92,2.73,2.73,0,0,1-.7,0H261.8a.91.91,0,0,0-1,.79v64.14a2.53,2.53,0,0,1-3.08,2.88H239.61a2.53,2.53,0,0,1-2.88-2.12,2.42,2.42,0,0,1,0-.76V53.05a.91.91,0,0,0-.78-1,.87.87,0,0,0-.28,0h-9a2.58,2.58,0,0,1-2.89-2.23,2.35,2.35,0,0,1,0-.7V34.61a2.52,2.52,0,0,1,2.11-2.88,2.49,2.49,0,0,1,.77,0h9a1,1,0,0,0,1.06-.85.81.81,0,0,0,0-.22V27.08A34.57,34.57,0,0,1,240,10.6a18,18,0,0,1,10.51-8.33A58.21,58.21,0,0,1,270.69.14h5.61a2.58,2.58,0,0,1,2.92,2.18,2.35,2.35,0,0,1,0,.7V15.15A2.59,2.59,0,0,1,277,18a2.35,2.35,0,0,1-.7,0h-4.75a12.36,12.36,0,0,0-8.29,2.48A11.89,11.89,0,0,0,260.94,29v1.66a.91.91,0,0,0,.73,1.06.88.88,0,0,0,.28,0h15.56A2.51,2.51,0,0,1,280.25,34,2.41,2.41,0,0,1,280.24,34.61Z\"/><path class=\"cls-1\" d=\"M292.21,113.48a37.41,37.41,0,0,1-14-20.46,60.16,60.16,0,0,1-2.43-17.43,56.87,56.87,0,0,1,2.58-18.09,36.92,36.92,0,0,1,14.1-19.81,41.51,41.51,0,0,1,24.66-7.18,39.94,39.94,0,0,1,24,7.18A36.83,36.83,0,0,1,355,57.34a56.82,56.82,0,0,1,2.58,17.89,67.64,67.64,0,0,1-2.22,17.28A38.26,38.26,0,0,1,317,121,40.42,40.42,0,0,1,292.21,113.48ZM326.37,97a18.66,18.66,0,0,0,5.61-9.4,43.59,43.59,0,0,0,1.52-12,46.29,46.29,0,0,0-1.37-12.13,17.8,17.8,0,0,0-5.61-9.05,14.7,14.7,0,0,0-9.75-3.23,14.53,14.53,0,0,0-9.55,3.23,18.24,18.24,0,0,0-5.66,9.05,46.71,46.71,0,0,0-1.36,12.13,45.55,45.55,0,0,0,1.36,12,18.87,18.87,0,0,0,5.76,9.4,14.5,14.5,0,0,0,9.81,3.39A13.86,13.86,0,0,0,326.37,97Z\"/><path class=\"cls-1\" d=\"M415,32.74a2.88,2.88,0,0,1,1.57,3.58l-3.24,17.94a2,2,0,0,1-1,1.87,4.92,4.92,0,0,1-2.47,0,21.07,21.07,0,0,0-5.05-.71,19,19,0,0,0-3.95.36,16.94,16.94,0,0,0-10.41,4.55A14,14,0,0,0,386.2,71v45.78a2.57,2.57,0,0,1-2.22,2.88,2.81,2.81,0,0,1-.71,0H365.13a2.59,2.59,0,0,1-2.93-2.18,2.73,2.73,0,0,1,0-.7v-82a2.58,2.58,0,0,1,2.16-2.93,2.49,2.49,0,0,1,.77,0h18.24A2.58,2.58,0,0,1,386.3,34a2.49,2.49,0,0,1,0,.77v5c0,.46,0,.76.35.86s.51,0,.81-.35a21.38,21.38,0,0,1,18.45-10.11A18,18,0,0,1,415,32.74Z\"/><path class=\"cls-1\" d=\"M469.27,31.88h18.29a2.57,2.57,0,0,1,2.89,2.22,2.41,2.41,0,0,1,0,.71v77q0,22.38-12.78,31.88a53.48,53.48,0,0,1-33,9.6,111.5,111.5,0,0,1-12.78-.86c-1.72,0-2.58-1.16-2.58-3.08l.71-15.87a2.79,2.79,0,0,1,.91-2.17,2.46,2.46,0,0,1,2.32-.4,82.54,82.54,0,0,0,10.41.85,26.11,26.11,0,0,0,16.83-4.8,18.59,18.59,0,0,0,5.91-15.15c0-.36,0-.56-.35-.61s-.51,0-.86.45a24.38,24.38,0,0,1-18.8,6.83,40.14,40.14,0,0,1-20.21-5.36A29.8,29.8,0,0,1,413,95.8a67,67,0,0,1-2.58-20.21,63.52,63.52,0,0,1,3.09-21.68,33.92,33.92,0,0,1,12.12-16.73,32.41,32.41,0,0,1,19.81-6.31,26,26,0,0,1,20.21,7.68c.36.35.61.45.86.35");
          out.print(
            "a.92.92,0,0,0,.36-.86V34.81A2.57,2.57,0,0,1,469,31.9Zm-2.88,43.2q0-4.1-.35-8.19a30.74,30.74,0,0,0-1.16-5.45A15.15,15.15,0,0,0,459.82,54a14.07,14.07,0,0,0-8.89-2.83A13.72,13.72,0,0,0,442.14,54a16,16,0,0,0-5.06,7.43,32.66,32.66,0,0,0-2.52,13.84,33,33,0,0,0,2,13.7A14.84,14.84,0,0,0,451.08,99a13.9,13.9,0,0,0,13.8-9.7A56.93,56.93,0,0,0,466.39,75.08Z\"/><path class=\"cls-1\" d=\"M556.33,92.82a3.16,3.16,0,0,1,2.23-1.17,2.61,2.61,0,0,1,1.92.86l9.7,9.4a2.8,2.8,0,0,1,1,2,3.26,3.26,0,0,1-.66,2.07,39,39,0,0,1-14.45,11,46.17,46.17,0,0,1-19.2,4,41.05,41.05,0,0,1-26-8,39.51,39.51,0,0,1-13.95-22,59.87,59.87,0,0,1-2-15.87,63.74,63.74,0,0,1,1.87-16.73A36.38,36.38,0,0,1,510.5,38.14,39.72,39.72,0,0,1,535,30.46a35.36,35.36,0,0,1,26.53,10.11,49.15,49.15,0,0,1,12.73,27.79c.46,3.79.81,7.93,1.06,12.48a2.62,2.62,0,0,1-2.28,2.93,2.36,2.36,0,0,1-.65,0H520c-.7,0-1.06.3-1.06,1a26.72,26.72,0,0,0,1.21,5.31,13.88,13.88,0,0,0,6.78,8.08A24.54,24.54,0,0,0,539.1,101,22.28,22.28,0,0,0,556.33,92.82ZM525.51,54a13.37,13.37,0,0,0-5,7.43c-.56,2.17-.91,3.58-1,4.29s0,1,.81,1h29c.56,0,.86,0,.86-.65a12.36,12.36,0,0,0-.71-4,14.4,14.4,0,0,0-14.85-10.76A15.1,15.1,0,0,0,525.51,54Z\"/></g><rect id=\"Rectangle\" class=\"cls-1\" y=\"1.41\" width=\"35.27\" height=\"35.27\" rx=\"7.83\"/><rect id=\"Rectangle-2\" class=\"cls-1\" x=\"43.15\" y=\"1.41\" width=\"35.27\" height=\"35.27\" rx=\"7.83\"/><rect id=\"Rectangle-3\" class=\"cls-1\" y=\"44.56\" width=\"35.27\" height=\"35.27\" rx=\"7.83\"/><rect id=\"Rectangle-4\" class=\"cls-1\" y=\"87.66\" width=\"35.27\" height=\"35.27\" rx=\"7.83\"/><rect id=\"Rectangle-5\" class=\"cls-1\" x=\"43.15\" y=\"44.56\" width=\"35.27\" height=\"35.27\" rx=\"7.83\"/><rect id=\"Rectangle-6\" class=\"cls-1\" x=\"86.25\" y=\"1.41\" width=\"35.27\" height=\"35.27\" rx=\"7.83\"/></g></g></g></g></svg>';\r\n"+
            "\r\n"+
            "var SVG_LOADING_ANIMATION_color1 = '<svg xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" style=\"margin: auto; background: none; display: block; shape-rendering: auto; transform: scale(1.5);\" width=\"75px\" height=\"75px\" viewBox=\"0 0 100 100\" preserveAspectRatio=\"xMidYMid\"><rect x=\"17.5\" y=\"30\" width=\"15\" height=\"40\" fill=';\r\n"+
            "\r\n"+
            "var SVG_LOADING_ANIMATION_color2 = '> <animate attributeName=\"y\" repeatCount=\"indefinite\" dur=\"1s\" calcMode=\"spline\" keyTimes=\"0;0.5;1\" values=\"16;30;30\" keySplines=\"0 0.5 0.5 1;0 0.5 0.5 1\" begin=\"-0.2s\"></animate> <animate attributeName=\"height\" repeatCount=\"indefinite\" dur=\"1s\" calcMode=\"spline\" keyTimes=\"0;0.5;1\" values=\"68;40;40\" keySplines=\"0 0.5 0.5 1;0 0.5 0.5 1\" begin=\"-0.2s\"></animate> </rect> <rect x=\"42.5\" y=\"30\" width=\"15\" height=\"40\" fill=';\r\n"+
            "	\r\n"+
            "var SVG_LOADING_ANIMATION_color3 = '> <animate attributeName=\"y\" repeatCount=\"indefinite\" dur=\"1s\" calcMode=\"spline\" keyTimes=\"0;0.5;1\" values=\"19.499999999999996;30;30\" keySplines=\"0 0.5 0.5 1;0 0.5 0.5 1\" begin=\"-0.1s\"></animate> <animate attributeName=\"height\" repeatCount=\"indefinite\" dur=\"1s\" calcMode=\"spline\" keyTimes=\"0;0.5;1\" values=\"61.00000000000001;40;40\" keySplines=\"0 0.5 0.5 1;0 0.5 0.5 1\" begin=\"-0.1s\"></animate> </rect><rect x=\"67.5\" y=\"30\" width=\"15\" height=\"40\" fill='; \r\n"+
            "\r\n"+
            "var SVG_LOADING_ANIMATION_end = '> <animate attributeName=\"y\" repeatCount=\"indefinite\" dur=\"1s\" calcMode=\"spline\" keyTimes=\"0;0.5;1\" values=\"19.499999999999996;30;30\" keySplines=\"0 0.5 0.5 1;0 0.5 0.5 1\"></animate> <animate attributeName=\"height\" repeatCount=\"indefinite\" dur=\"1s\" calcMode=\"spline\" keyTimes=\"0;0.5;1\" values=\"61.00000000000001;40;40\" keySplines=\"0 0.5 0.5 1;0 0.5 0.5 1\"></animate> </rect> </svg>'; \r\n"+
            "\r\n"+
            "const weekdayNames = [\"Sunday\",\"Monday\",\"Tuesday\",\"Wednesday\",\"Thursday\",\"Friday\",\"Saturday\"];\r\n"+
            "const dayNames=['Su','Mo','Tu','We','Th','Fr','Sa']; \r\n"+
            "const monthNames=['January','February','March','April','May','June','July','August','September','October','November','December']; \r\n"+
            "const monthNamesShortened=['Jan','Feb','Mar','Apr','May','Jun','Jul','Aug','Sep','Oct','Nov','Dec']; \r\n"+
            "\r\n"+
            "fireOnMouseWheel=function(e,delta){\r\n"+
            "  var point = getMousePoint(e);\r\n"+
            "  let doc = document;\r\n"+
            "  if(e.target){\r\n"+
            "	  doc = getWindow(e.target).document;\r\n"+
            "  }\r\n"+
            "  var element=doc.elementFromPoint(point.x,point.y);\r\n"+
            "  while(element!=null && element!=document && element!=document.body){\r\n"+
            "      if(element.onMouseWheel){\r\n"+
            "    	  element.onMouseWheel(e,delta);\r\n"+
            "    	  return false;\r\n"+
            "      }\r\n"+
            "  	element=element.parentNode;\r\n"+
            "  }\r\n"+
            "  return true;\r\n"+
            "};\r\n"+
            "\r\n"+
            "\r\n"+
            "function initUtils(){\r\n"+
            "  window.HIDDEN_IFRAME=nw('iframe');\r\n"+
            "  window.HIDDEN_IFRAME.style.display='none';\r\n"+
            "  window.HIDDEN_IFRAME.style.width='0px';\r\n"+
            "  window.HIDDEN_IFRAME.style.height='0px';\r\n"+
            "  window.HIDDEN_IFRAME.id='HIDDEN_IFRAME';\r\n"+
            "  window.HIDDEN_IFRAME.name='HIDDEN_IFRAME';\r\n"+
            "  document.body.appendChild(window.HIDDEN_IFRAME);\r\n"+
            "  document.oncontextmenu='return false;';\r\n"+
            "  document.addEventListener('mousemove', onMouseMove, false);\r\n"+
            "  document.onkeydown=function(event){\r\n"+
            "    if(event.keyCode===8){\r\n"+
            "      var tgt=getMouseTarget(event);\r\n"+
            "      if(isInput(tgt) || tgt.className==\"customTextArea\")\r\n"+
            "    	  return;\r\n"+
            "      event.preventDefault();\r\n"+
            "    }else if(event.ctrlKey && event.keyCode==82){\r\n"+
            "      event.preventDefault();\r\n"+
            "      event.stopPropagation();\r\n"+
            "    }\r\n"+
            "  };\r\n"+
            "\r\n"+
            "\r\n"+
            "  //if (window.addEventListener){\r\n"+
            "        /** DOMMouseScroll is for mozilla. */\r\n"+
            "      //var eventType = (navigator.userAgent.indexOf('Firefox') !=-1) ? \"DOMMouseScroll\" : \"mousewheel\";            \r\n"+
            "      //window.addEventListener(eventType, wheel, true);*\r\n"+
            "  //}\r\n"+
            "  /** IE/Opera. */\r\n"+
            "  //window.onmousewheel = document.onmousewheel = wheel;\r\n"+
            "  \r\n"+
            "};\r\n"+
            "\r\n"+
            "function buildSVGLoadAnimation(color1, color2, color3) {\r\n"+
            "	SVG_LOADING_ANIMATION = SVG_LOADING_ANIMATION_color1 + (color1?JSON.stringify(color1):\"#77cefa\") + SVG_LOADING_ANIMATION_color2 + (color2?JSON.stringify(color2):\"#f16900\") + SVG_LOADING_ANIMATION_color3 + (color3?JSON.stringify(color3):\"#0e91bb\") + SVG_LOADING_ANIMATION_end;\r\n"+
            "}\r\n"+
            "\r\n"+
            "function isInput(tgt){\r\n"+
            "	  if(tgt==null)\r\n"+
            "		  return false;\r\n"+
            "      var tn=tgt.tagName;\r\n"+
            "      if(tn==='INPUT'){\r\n"+
            "    	var type=tgt.type;\r\n"+
            "    	if(type!=null){\r\n"+
            "    	  type=type.toUpperCase();\r\n"+
            "    	  if(type==='TEXT' || type==='PASSWORD' || type==='FILE' || type==='EMAIL' || type==='SEARCH' || type==='DATE')\r\n"+
            "    	    return true;\r\n"+
            "    	}\r\n"+
            "      }else if(tn==='TEXTAREA')\r\n"+
            "        return true;\r\n"+
            "      else if(tn==='COMBO-BOX')\r\n"+
            "    	return true;\r\n"+
            "      return false;\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "var MOUSE_POSITION_X=-1;\r\n"+
            "\r\n"+
            "var MOUSE_POSITION_Y=-1;\r\n"+
            "var MOUSE_WINDOW=null;\r\n"+
            "var USER_DRIVEN_PENDING_TASKS=[];\r\n"+
            "\r\n"+
            "function onMouseMove(e){\r\n"+
            "	MOUSE_WINDOW=e.view;\r\n"+
            "	if(USER_DRIVEN_PENDING_TASKS.length>0 && e.button>0){\r\n"+
            "		for(var i in USER_DRIVEN_PENDING_TASKS){\r\n"+
            "			USER_DRIVEN_PENDING_TASKS[i]();\r\n"+
            "		}\r\n"+
            "		USER_DRIVEN_PENDING_TASKS=[];\r\n"+
            "	}\r\n"+
            "    MOUSE_POSITION_X = e.clientX;\r\n"+
            "    MOUSE_POSITION_Y = e.clientY;\r\n"+
            "}\r\n"+
            "\r\n"+
            "function addUserPendingTask(func){\r\n"+
            "	USER_DRIVEN_PENDING_TASKS[USER_DRIVEN_PENDING_TASKS.length]=func;\r\n"+
            "}\r\n"+
            "\r\n"+
            "function getDocumentHeight(window) {\r\n"+
            "    var d = window.document;\r\n"+
            "    var r= Math.max( d.body.scrollHeight, d.documentElement.scrollHeight, d.body.offsetHeight, d.documentElement.offsetHeight, d.body.clientHeight, d.documentElement.clientHeight);\r\n"+
            "    return r;\r\n"+
            "}\r\n"+
            "function getDocumentWidth(window) {\r\n"+
            "    var body=window.document.getElementsByTagName('body')[0];\r\n"+
            "    return new Rect().readFromElement(body).width;\r\n"+
            "}\r\n"+
            "\r\n"+
            "function isChildOf(parent,child){\r\n"+
            "	while(child!=null){\r\n"+
            "		if(child==parent)\r\n"+
            "			return true;\r\n"+
            "		child=child.parentNode;\r\n"+
            "	}\r\n"+
            "	return false;\r\n"+
            "	\r\n"+
            "}\r\n"+
            "\r\n"+
            "//#######################\r\n"+
            "//##### inheritance #####\r\n"+
            "\r\n"+
            "\r\n"+
            "//Function.prototype.method = function (name, func) {\r\n"+
            "    //this.prototype[name] = func;\r\n"+
            "    //return this;\r\n"+
            "//};\r\n"+
            "\r\n"+
            "Function.prototype.inherits = function (parent) {\r\n"+
            "    var d = {}, p = (this.prototype = new parent());\r\n"+
            "    this.prototype.uber=function uber(name) {\r\n"+
            "        if (!(name in d)) {\r\n"+
            "            d[name] = 0;\r\n"+
            "        }        \r\n"+
            "        var f, r, t = d[name], v = parent.prototype;\r\n"+
            "        if (t) {\r\n"+
            "            while (t) {\r\n"+
            "                v = v.constructor.prototype;\r\n"+
            "                t -= 1;\r\n"+
            "            }\r\n"+
            "            f = v[name];\r\n"+
            "        } else {\r\n"+
            "            f = p[name];\r\n"+
            "            if (f == this[name]) {\r\n"+
            "                f = v[name];\r\n"+
            "            }\r\n"+
            "        }\r\n"+
            "        d[name] += 1;\r\n"+
            "        r = f.apply(this, Array.prototype.slice.apply(arguments, [1]));\r\n"+
            "        d[name] -= 1;\r\n"+
            "        return r;\r\n"+
            "    };\r\n"+
            "    return this;\r\n"+
            "};\r\n"+
            "\r\n"+
            "//################\r\n"+
            "//##### Text #####\r\n"+
            "\r\n"+
            "function joinMap(delim,eq,map){\r\n"+
            "  if(map.length==0)\r\n"+
            "    return '';\r\n"+
            "  var r='';\r\n"+
            "  var first=true;\r\n"+
            "  for(var i in map){\r\n"+
            "     if(first)\r\n"+
            "       first=false;\r\n"+
            "     else\r\n"+
            "       r+=delim;\r\n"+
            "     r+=i+eq+map[i];\r\n"+
            "  }\r\n"+
            "  return r;\r\n"+
            "};\r\n"+
            "function joinAndEncodeMap(delim,eq,map){\r\n"+
            "  if(map.length==0)\r\n"+
            "    return '';\r\n"+
            "  var r='';\r\n"+
            "  var first=true;\r\n"+
            "  for(var i in map){\r\n"+
            "	  var val=map[i];\r\n"+
            "	  if(val!=null){\r\n"+
            "       if(first)\r\n"+
            "         first=false;\r\n"+
            "       else\r\n"+
            "         r+=delim;\r\n"+
            "       r+=encodeURIComponent(i)+eq;\r\n"+
            "       if(typeof val === 'object'){\r\n"+
            "         r+=encodeURIComponent(JSON.stringify(val));\r\n"+
            "       }else{\r\n"+
            "         r+=encodeURIComponent(val);\r\n"+
            "	   }\r\n"+
            "	 }\r\n"+
            "  }\r\n"+
            "  return r;\r\n"+
            "};\r\n"+
            "\r\n"+
            "\r\n"+
            "//Diffs two strings\r\n"+
            "//Arguments: two strings \r\n"+
            "//	The order of the string matters\r\n"+
            "//Returns the positions where the characters start to differ from the beginning and from the end of the left string\r\n"+
            "//		and the substring of the right string bounded by the positions where the right string begins to differ from the left string\r\n"+
            "//The start (s) is inclusive\r\n"+
            "//The end (e) is exclusive\r\n"+
            "//The substring is denoted as (c)\r\n"+
            "//Example: strDiff(\"Mount Everst\", \"Mount Everest\")\r\n"+
            "//returns: {s:10, e:10, c:\"e\"}\r\n"+
            "//Example2: strDiff(\"Mount Everest\", \"Mount Everst\")\r\n"+
            "//returns: {s:10, e:11, c:\"\"}\r\n"+
            "\r\n"+
            "function strDiff(lString, rString){\r\n"+
            "	if(lString == null)\r\n"+
            "		return {c:rString};\r\n"+
            "	var rLen = rString.length;\r\n"+
            "	var lLen = lString.length;\r\n"+
            "	var len = rLen < lLen ? rLen : lLen;\r\n"+
            "	var start = 0;\r\n"+
            "	var endOffset = 0;\r\n"+
            "	for(; start < len; start++){\r\n"+
            "		if(lString[start] != rString[start])\r\n"+
            "			break;\r\n"+
            "	}\r\n"+
            "	len = len-start;\r\n"+
            "	for(;endOffset < len; ++endOffset){\r\n"+
            "		if(lString[lLen-1-endOffset] != r");
          out.print(
            "String[rLen-1-endOffset])\r\n"+
            "			break;\r\n"+
            "	}\r\n"+
            "	return {s:start, e:lLen-endOffset , c:rString.substring(start, rLen-endOffset)};\r\n"+
            "}\r\n"+
            "\r\n"+
            "//#################\r\n"+
            "//##### Touch #####\r\n"+
            "\r\n"+
            "function getTouchTarget(touchEvent){\r\n"+
            "var mouseEvent = getMouseEvent(touchEvent);\r\n"+
            "if(touchEvent.touchTarget!=null)\r\n"+
            "	  return touchEvent.touchTarget;\r\n"+
            "return touchEvent.target ? touchEvent.target : touchEvent.srcElement;\r\n"+
            "};\r\n"+
            "\r\n"+
            "function getTouchEvent(touchEvent){\r\n"+
            "if(touchEvent!=null) return touchEvent;\r\n"+
            "return event;\r\n"+
            "};\r\n"+
            "\r\n"+
            "function getTouchLayerPoint(touchEvent){\r\n"+
            "	// falls back to layerX and layerY in firefox.\r\n"+
            "	if (touchEvent != null) {\r\n"+
            "		var offX = touchEvent.offsetX || touchEvent.layerX;\r\n"+
            "		var offY = touchEvent.offsetY || touchEvent.layerY;\r\n"+
            "		return new Point(offX, offY);\r\n"+
            "	}\r\n"+
            "	var offX = event.offsetX || event.layerX;\r\n"+
            "	var offY = event.offsetY || event.layerY;\r\n"+
            "	return new Point(offX,offY);\r\n"+
            "};\r\n"+
            "\r\n"+
            "//#################\r\n"+
            "//##### Mouse #####\r\n"+
            "\r\n"+
            "function getMouseTarget(mouseEvent){\r\n"+
            "  var mouseEvent = getMouseEvent(mouseEvent);\r\n"+
            "  if(mouseEvent.touchTarget!=null)\r\n"+
            "	  return mouseEvent.touchTarget;\r\n"+
            "  return mouseEvent.target ? mouseEvent.target : mouseEvent.srcElement;\r\n"+
            "};\r\n"+
            "function getMouseRelatedTarget(mouseEvent){\r\n"+
            "  var mouseEvent = getMouseEvent(mouseEvent);\r\n"+
            "  return mouseEvent.toElement || mouseEvent.relatedTarget;\r\n"+
            "};\r\n"+
            "\r\n"+
            "//left=1,right=2,middle=3\r\n"+
            "function getMouseButton(mouseEvent){\r\n"+
            "  var mouseEvent = getMouseEvent(mouseEvent);\r\n"+
            "  if(mouseEvent.which==2)\r\n"+
            "	  return 3;\r\n"+
            "  if(mouseEvent.which==3)\r\n"+
            "	  return 2;\r\n"+
            "  return mouseEvent.button ? mouseEvent.button : mouseEvent.which;\r\n"+
            "};\r\n"+
            "\r\n"+
            "function getMouseEvent(mouseEvent){\r\n"+
            "  if(mouseEvent!=null) return mouseEvent;\r\n"+
            "  return event;\r\n"+
            "};\r\n"+
            "\r\n"+
            "var lastTouch;\r\n"+
            "function getMousePoint(mouseEvent){\r\n"+
            "  if(mouseEvent){\r\n"+
            "	if(mouseEvent.targetTouches!=null){\r\n"+
            "	  if(mouseEvent.targetTouches.length>0)\r\n"+
            "	      lastTouch=mouseEvent.targetTouches[0];\r\n"+
            "      return new Point(rd(lastTouch.pageX),rd(lastTouch.pageY));\r\n"+
            "	}\r\n"+
            "    return new Point(rd(mouseEvent.pageX),rd(mouseEvent.pageY));\r\n"+
            "  }\r\n"+
            "  return new Point(rd(event.clientX),rd(event.clientY));\r\n"+
            "};\r\n"+
            "\r\n"+
            "function getMouseLayerPoint(mouseEvent){\r\n"+
            "	// falls back to layerX and layerY in firefox.\r\n"+
            "	if (mouseEvent != null) {\r\n"+
            "		var offX = mouseEvent.offsetX || mouseEvent.layerX;\r\n"+
            "		var offY = mouseEvent.offsetY || mouseEvent.layerY;\r\n"+
            "		return new Point(offX, offY);\r\n"+
            "	}\r\n"+
            "	var offX = event.offsetX || event.layerX;\r\n"+
            "	var offY = event.offsetY || event.layerY;\r\n"+
            "	return new Point(offX,offY);\r\n"+
            "};\r\n"+
            "\r\n"+
            "function getMousePointRelativeTo(mouseEvent,div){\r\n"+
            "  var r=getMousePoint(mouseEvent);\r\n"+
            "  var t=new Rect().readFromElementRelatedToWindow(div);\r\n"+
            "  r.x-=t.getLeft();\r\n"+
            "  r.y-=t.getTop();\r\n"+
            "  return r;\r\n"+
            "};\r\n"+
            "\r\n"+
            "function isMouseInside(mouseEvent,element,padding){\r\n"+
            "  var point=getMousePoint(mouseEvent);\r\n"+
            "  var rect=new Rect();\r\n"+
            "  rect.readFromElement(element);\r\n"+
            "  if(padding==null)\r\n"+
            "    return rect.pointInside(point);\r\n"+
            "  else\r\n"+
            "    return rect.pointInside(point,padding);\r\n"+
            "};\r\n"+
            "\r\n"+
            "\r\n"+
            "//################\r\n"+
            "//##### Math #####\r\n"+
            "\r\n"+
            "function max(a,b){\r\n"+
            "   return a>b ? a : b;\r\n"+
            "};\r\n"+
            "function abs(a){\r\n"+
            "   return a<0 ? -a : a;\r\n"+
            "};\r\n"+
            "\r\n"+
            "function min(a,b){\r\n"+
            "   return a<b ? a : b;\r\n"+
            "};\r\n"+
            "var PXP={};\r\n"+
            "for(var i=-1000;i<5000;i++){\r\n"+
            "  var str=i+\"px\";\r\n"+
            "  PXP[i]=str;\r\n"+
            "  PXP[\"\"+i]=str;\r\n"+
            "}\r\n"+
            "\r\n"+
            "function toPx(px){\r\n"+
            "  var r=PXP[px];\r\n"+
            "  if(r!=null)\r\n"+
            "	  return r;\r\n"+
            "  if(px==null || Number.isNaN(px)) return null;\r\n"+
            "  if(typeof px == 'number'){\r\n"+
            "    var px=Math.floor(px+.5);\r\n"+
            "    var r=PXP[px];\r\n"+
            "    if(r!=null)\r\n"+
            "	    return r;\r\n"+
            "  }\r\n"+
            "   return px+\"px\";\r\n"+
            "};\r\n"+
            "function fromPx(px){\r\n"+
            "   return px.substr(0,px.length-2);\r\n"+
            "};\r\n"+
            "\r\n"+
            "//missleading function name, clips\r\n"+
            "function between(val,min,max){\r\n"+
            "  if(min<max)\r\n"+
            "    return val<min ? min : val > max ? max : val;\r\n"+
            "  else\r\n"+
            "    return val<max ? max : val > min ? min : val;\r\n"+
            "};\r\n"+
            "\r\n"+
            "//inclusive\r\n"+
            "function isBetween(val,min,max){\r\n"+
            "	if(min>max)\r\n"+
            "	  return val>=max && val<=min;\r\n"+
            "	else\r\n"+
            "	  return val>=min && val<=max;\r\n"+
            "};\r\n"+
            "\r\n"+
            "function isBetweenExclusive(val,min,max) {\r\n"+
            "	if(min>max)\r\n"+
            "	  return val>max && val<min;\r\n"+
            "	else\r\n"+
            "	  return val>min && val<max;\r\n"+
            "	\r\n"+
            "}\r\n"+
            "\r\n"+
            "//#################\r\n"+
            "//##### Point #####\r\n"+
            "\r\n"+
            "function Point(x,y){\r\n"+
            "  if(x!=null && x.x!=null){\r\n"+
            "	  this.x=x.x;\r\n"+
            "	  this.y=x.y;\r\n"+
            "  }else{\r\n"+
            "    this.x=x;\r\n"+
            "    this.y=y;\r\n"+
            "  }\r\n"+
            "};\r\n"+
            "\r\n"+
            "Point.prototype.x;\r\n"+
            "Point.prototype.y;\r\n"+
            "\r\n"+
            "Point.prototype.getX = function(){\r\n"+
            "  return this.x;\r\n"+
            "};\r\n"+
            "\r\n"+
            "Point.prototype.getY = function(){\r\n"+
            "  return this.y;\r\n"+
            "};\r\n"+
            "\r\n"+
            "Point.prototype.move = function(x,y){\r\n"+
            "  this.x+=x;\r\n"+
            "  this.y+=y;\r\n"+
            "  return this;\r\n"+
            "};\r\n"+
            "\r\n"+
            "Point.prototype.clone=function(){\r\n"+
            "   return new Point(this.x,this.y);\r\n"+
            "};\r\n"+
            "\r\n"+
            "Point.prototype.toString = function(){\r\n"+
            "  return \"{'x':\"+this.x+\",'y':\"+this.y+\"}\";\r\n"+
            "};\r\n"+
            "\r\n"+
            "Point.prototype.equals=function(point){\r\n"+
            "  return this.x == point.x && this.y == point.y;\r\n"+
            "};\r\n"+
            "\r\n"+
            "\r\n"+
            "//###################\r\n"+
            "//##### Element #####\r\n"+
            "\r\n"+
            "\r\n"+
            "function getElement(id){\r\n"+
            "  return document.getElementById(id);\r\n"+
            "};\r\n"+
            "\r\n"+
            "function getElementOrThrow(id){\r\n"+
            "  r= document.getElementById(id);\r\n"+
            "  if(r)\r\n"+
            "      return r;\r\n"+
            "  alert(\"Element not found:\"+id);\r\n"+
            "};\r\n"+
            "\r\n"+
            "function nw(type,className){\r\n"+
            "  var r=document.createElement(type);\r\n"+
            "  if(className!=null)\r\n"+
            "	  r.className=className;\r\n"+
            "  return r;\r\n"+
            "};\r\n"+
            "\r\n"+
            "function nw2(type,className,element) {\r\n"+
            "	var r;\r\n"+
            "	if (element != null && getWindow(element) != null)\r\n"+
            "		r=getWindow(element).document.createElement(type);\r\n"+
            "	else\r\n"+
            "		r=document.createElement(type);\r\n"+
            "	if(className!=null)\r\n"+
            "		r.className=className;\r\n"+
            "	return r;\r\n"+
            "};\r\n"+
            "\r\n"+
            "function nw1(type,className){\r\n"+
            "	 var frame=getHiddenIFrame();\r\n"+
            "	  var r=frame.document.createElement(type);\r\n"+
            "	  if(className!=null)\r\n"+
            "		  r.className=className;\r\n"+
            "	  return r;\r\n"+
            "	};\r\n"+
            "	\r\n"+
            "function ensureInDiv(inner,outer){\r\n"+
            "   var innerRect=new Rect().readFromElement(inner);\r\n"+
            "   var outerRect=new Rect().readFromElement(outer);\r\n"+
            "   if(outerRect.rectInside(innerRect))\r\n"+
            "	   return;\r\n"+
            "   //log();\r\n"+
            "   //log(innerRect);\r\n"+
            "   innerRect.ensureInsideRect(outerRect);\r\n"+
            "   innerRect.writeToElementRelatedToWindow(inner);\r\n"+
            "   //log(innerRect);\r\n"+
            "   //log(outerRect);\r\n"+
            "}\r\n"+
            "function ensureInWindow(element){\r\n"+
            "   var divPos=new Rect();\r\n"+
            "   var divParent=element.parentNode;\r\n"+
            "   divPos.readFromElement(element);\r\n"+
            "   divParent.removeChild(element);\r\n"+
            "   var bodyPos=new Rect();\r\n"+
            "   var body=getWindow(element).document.getElementsByTagName('body')[0];\r\n"+
            "   bodyPos.readFromElement(body);\r\n"+
            "   bodyPos.height=getDocumentHeight(getWindow(element));\r\n"+
            "   divParent.appendChild(element);\r\n"+
            "   if(divPos.left<0){\r\n"+
            "     divPos.left=0;\r\n"+
            "     element.style.left=toPx(0);\r\n"+
            "   }\r\n"+
            "   if(divPos.top<0){\r\n"+
            "     divPos.top=0;\r\n"+
            "     element.style.top=toPx(0);\r\n"+
            "     element.style.bottom=null;\r\n"+
            "   }\r\n"+
            "   var h=bodyPos.getRight()-divPos.getRight()-10 ;\r\n"+
            "   if(h<0)\r\n"+
            "     divPos.left+=h;\r\n"+
            "   var v=bodyPos.getBottom()-divPos.getBottom()-10 ;\r\n"+
            "\r\n"+
            "   if(v<0)\r\n"+
            "     divPos.top+=v;\r\n"+
            "   if(h<0 || v<0){\r\n"+
            "     element.style.left=toPx(divPos.left);\r\n"+
            "     element.style.top=toPx(divPos.top);\r\n"+
            "   }\r\n"+
            "   if(divPos.height > bodyPos.height){\r\n"+
            "	   element.style.height=toPx(bodyPos.height - 8);\r\n"+
            "	   element.style.overflowY=\"scroll\";\r\n"+
            "	   element.style.top=\"4px\";\r\n"+
            "   }\r\n"+
            "};\r\n"+
            "\r\n"+
            "function containInWindow(element){\r\n"+
            "   var divPos=new Rect();\r\n"+
            "   var divParent=element.parentNode;\r\n"+
            "   divPos.readFromElement(element);\r\n"+
            "   divParent.removeChild(element);\r\n"+
            "   var bodyPos=new Rect();\r\n"+
            "   var body=getWindow(element).document.getElementsByTagName('body')[0];\r\n"+
            "   bodyPos.readFromElement(body);\r\n"+
            "   bodyPos.height=getDocumentHeight(getWindow(element));\r\n"+
            "   divParent.appendChild(element);\r\n"+
            "   var newHeight = divPos.height;\r\n"+
            "   var newWidth = divPos.width;\r\n"+
            "   if(divPos.left<0){\r\n"+
            "     newWidth + divPos.left;\r\n"+
            "     divPos.left=0;\r\n"+
            "   }\r\n"+
            "   if(divPos.top<0){\r\n"+
            "	 newHeight + divPos.top;\r\n"+
            "     divPos.top=0;\r\n"+
            "   }\r\n"+
            "   var h=bodyPos.getRight()-divPos.getRight()-10 ;\r\n"+
            "   if(h<0)\r\n"+
            "	 newWidth += h;\r\n"+
            "   var v=bodyPos.getBottom()-divPos.getBottom()-10 ;\r\n"+
            "   if(v<0)\r\n"+
            "	 newHeight += v;\r\n"+
            "   element.style.width = toPx(newWidth);\r\n"+
            "   element.style.height = toPx(newHeight);\r\n"+
            "   if(divPos.height > bodyPos.height){\r\n"+
            "	   element.style.height=toPx(newHeight- 8);\r\n"+
            "	   element.style.overflowY=\"scroll\";\r\n"+
            "   }\r\n"+
            "   if(divPos.height > newHeight){\r\n"+
            "	   element.style.width=toPx(newWidth + 17);\r\n"+
            "   }\r\n"+
            "};\r\n"+
            "\r\n"+
            "//################\r\n"+
            "//##### Rect ##### \r\n"+
            "\r\n"+
            "function Rect(left,top,width,height){\r\n"+
            "  this.left=left;\r\n"+
            "  this.top=top;\r\n"+
            "  this.width=width;\r\n"+
            "  this.height=height;\r\n"+
            "};\r\n"+
            "\r\n"+
            "\r\n"+
            "Rect.prototype.left;\r\n"+
            "Rect.prototype.top;\r\n"+
            "Rect.prototype.width;\r\n"+
            "Rect.prototype.height;\r\n"+
            "\r\n"+
            "Rect.prototype.getLeft = function(){\r\n"+
            "  return this.left;\r\n"+
            "};\r\n"+
            "\r\n"+
            "Rect.prototype.getTop = function(){\r\n"+
            "  return this.top;\r\n"+
            "};\r\n"+
            "\r\n"+
            "Rect.prototype.getBottom = function(){\r\n"+
            "  return this.top+this.height;\r\n"+
            "};\r\n"+
            "\r\n"+
            "Rect.prototype.getRight = function(){\r\n"+
            "  return this.left+this.width;\r\n"+
            "};\r\n"+
            "\r\n"+
            "Rect.prototype.setLeft = function(left){\r\n"+
            "  this.left=left;\r\n"+
            "};\r\n"+
            "\r\n"+
            "Rect.prototype.setTop = function(top){\r\n"+
            "  this.top=top;\r\n"+
            "};\r\n"+
            "Rect.prototype.setRight = function(right){\r\n"+
            "  this.left=right-this.width;\r\n"+
            "};\r\n"+
            "Rect.prototype.setBottom = function(bottom){\r\n"+
            "  this.top=bottom-this.height;\r\n"+
            "};\r\n"+
            "\r\n"+
            "Rect.prototype.setTop = function(top){\r\n"+
            "  this.top=top;\r\n"+
            "};\r\n"+
            "\r\n"+
            "Rect.prototype.setWidth = function(width){\r\n"+
            "  this.width=width;\r\n"+
            "};\r\n"+
            "\r\n"+
            "Rect.prototype.setHeight = function(height){\r\n"+
            "  this.height=height;\r\n"+
            "  if(height<0)\r\n"+
            "	  throw \"invalid height: \"+height;\r\n"+
            "};\r\n"+
            "\r\n"+
            "Rect.prototype.clone=function(){\r\n"+
            "   return new Rect(this.left,this.top,this.width,this.height);\r\n"+
            "};\r\n"+
            "\r\n"+
            "Rect.prototype.toString = function(){\r\n"+
            "  return \"{'left':\"+this.left+\",'top':\"+this.top+\",'width':\"+this.width+\",'height':\"+this.height+\"}\";\r\n"+
            "};\r\n"+
            "\r\n"+
            "Rect.prototype.equals=function(rect){\r\n"+
            "  return this.left == rect.left && this.top == rect.top && this.width == rect.width && this.height == rect.height;\r\n"+
            "};\r\n"+
            "Rect.prototype.move=function(x,y){\r\n"+
            "  this.left+=x;\r\n"+
            "  this.top+=y;\r\n"+
            "  return this;\r\n"+
            "};\r\n"+
            "\r\n"+
            "Rect.prototype.grow=function(width,height){\r\n"+
            "  this.width+=width;\r\n"+
            "  this.height+=height;\r\n"+
            "  return this;\r\n"+
            "};\r\n"+
            "\r\n"+
            "Rect.prototype.expand=function(width,height){\r\n"+
            "  if(height==null)\r\n"+
            "  height=width;\r\n"+
            "  this.left-=width;\r\n"+
            "  this.top-=height;\r\n"+
            "  this.width+=width*2;\r\n"+
            "  this.");
          out.print(
            "height+=height*2;\r\n"+
            "  return this;\r\n"+
            "};\r\n"+
            "\r\n"+
            "\r\n"+
            "Rect.prototype.clipY=function(y,padding){\r\n"+
            "  return between(y,this.top-padding,this.getBottom()+padding);\r\n"+
            "};\r\n"+
            "\r\n"+
            "Rect.prototype.clipX=function(x,padding){\r\n"+
            "  return between(x,this.left-padding,this.getRight()+padding);\r\n"+
            "};\r\n"+
            "\r\n"+
            "Rect.prototype.inside = function(x,y) {\r\n"+
            "  return x>=this.left && y>=this.top && x<this.getRight() && y<this.getBottom();\r\n"+
            "};\r\n"+
            "Rect.prototype.inside = function(x,y,padding) {\r\n"+
            "  return x>=this.left-padding && y>=this.top-padding && x<this.getRight()+padding && y<this.getBottom()+padding;\r\n"+
            "};\r\n"+
            "\r\n"+
            "Rect.prototype.pointInside = function(p,padding) {\r\n"+
            "  if(padding==null)\r\n"+
            "	  padding=0;\r\n"+
            "  return this.inside(p.x,p.y,padding);\r\n"+
            "};\r\n"+
            "\r\n"+
            "\r\n"+
            "Rect.prototype.rectInsidePadded = function(rect,padding) {\r\n"+
            "  this.expand( padding );\r\n"+
            "  var r=this.pointInside(rect.getUpperLeft()) && this.pointInside(rect.getLowerRight());\r\n"+
            "  this.expand( -padding );\r\n"+
            "  return r;\r\n"+
            "};\r\n"+
            "\r\n"+
            "Rect.prototype.rectInside = function(rect) {\r\n"+
            "  var r=this.inside(rect.getUpperLeft()) && this.inside(rect.getLowerRight());\r\n"+
            "  return r;\r\n"+
            "};\r\n"+
            "Rect.prototype.ensureInsideRect = function(outer) {\r\n"+
            "  var r=true;\r\n"+
            "  if(this.getLeft()<outer.getLeft()){\r\n"+
            "	  this.setLeft(outer.getLeft());\r\n"+
            "  }\r\n"+
            "  if(this.getTop()<outer.getTop()){\r\n"+
            "	  this.setTop(outer.getTop());\r\n"+
            "  }\r\n"+
            "  if(this.getRight()>outer.getRight()){\r\n"+
            "	  this.setRight(outer.getRight());\r\n"+
            "	  r=r && this.getLeft()<outer.getLeft();\r\n"+
            "  }\r\n"+
            "  if(this.getBottom()>outer.getBottom()){\r\n"+
            "	  this.setBottom(outer.getBottom());\r\n"+
            "	  r=r && this.getTop()<outer.getTop();\r\n"+
            "  }\r\n"+
            "  return r;\r\n"+
            "};\r\n"+
            "\r\n"+
            "Rect.prototype.insidePadded = function(x,y,padding) {\r\n"+
            "  this.expand( padding );\r\n"+
            "  var r=this.inside( x , y );\r\n"+
            "  this.expand( -padding );\r\n"+
            "  return r;\r\n"+
            "};\r\n"+
            "\r\n"+
            "Rect.prototype.readFromElementRelatedToParent = function(element) {\r\n"+
            "  this.left = element.offsetLeft;\r\n"+
            "  this.top = element.offsetTop;\r\n"+
            "  this.width = element.offsetWidth;\r\n"+
            "  this.height = element.offsetHeight;\r\n"+
            "  return this;\r\n"+
            "};\r\n"+
            "Rect.prototype.readFromElementRelatedToWindow = function(element) {\r\n"+
            "  this.left = 0;\r\n"+
            "  this.top = 0;\r\n"+
            "  this.width = element.offsetWidth;\r\n"+
            "  this.height = element.offsetHeight;\r\n"+
            "  while (element) {\r\n"+
            "    this.left += element.offsetLeft;\r\n"+
            "    this.top += element.offsetTop;\r\n"+
            "    element = element.offsetParent;\r\n"+
            "  }\r\n"+
            "  return this;\r\n"+
            "};\r\n"+
            "\r\n"+
            "function getAbsoluteLeft(element){\r\n"+
            "	var r= 0;\r\n"+
            "  while (element) {\r\n"+
            "    r += element.offsetLeft;\r\n"+
            "    element = element.offsetParent;\r\n"+
            "  }\r\n"+
            "  return r;\r\n"+
            "}\r\n"+
            "function getAbsoluteTop(element){\r\n"+
            "  var r= 0;\r\n"+
            "  while (element) {\r\n"+
            "      r += element.offsetTop;\r\n"+
            "      element = element.offsetParent;\r\n"+
            "  }\r\n"+
            "  return r;\r\n"+
            "}\r\n"+
            "\r\n"+
            "//deprecated\r\n"+
            "Rect.prototype.readFromElement = function(element) {\r\n"+
            "	return this.readFromElementRelatedToWindow(element);\r\n"+
            "};\r\n"+
            "//deprecated\r\n"+
            "Rect.prototype.writeToElement=function(elem){\r\n"+
            "   return this.writeToElementRelatedToParent(elem);\r\n"+
            "}\r\n"+
            "\r\n"+
            "Rect.prototype.writeToElementRelatedToWindow=function(elem){\r\n"+
            "   //elem.style.width=toPx(this.width);\r\n"+
            "   //elem.style.height=toPx(this.height);\r\n"+
            "   \r\n"+
            "  var left = this.left;\r\n"+
            "  var top = this.top;\r\n"+
            "  //this.width = elem.offsetWidth;\r\n"+
            "  //this.height = elem.offsetHeight;\r\n"+
            "  var element=elem.offsetParent;\r\n"+
            "  while (element) {\r\n"+
            "    left -= element.offsetLeft;\r\n"+
            "    top -= element.offsetTop;\r\n"+
            "    element = element.offsetParent;\r\n"+
            "  }\r\n"+
            "  elem.style.left=toPx(left);\r\n"+
            "  elem.style.top=toPx(top);\r\n"+
            "  return this;\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "Rect.prototype.writeToElementRelatedToParent=function(elem){\r\n"+
            "   elem.style.left=toPx(this.left);\r\n"+
            "   elem.style.top=toPx(this.top);\r\n"+
            "   elem.style.width=toPx(this.width);\r\n"+
            "   elem.style.height=toPx(this.height);\r\n"+
            "   return this;\r\n"+
            "};\r\n"+
            "\r\n"+
            "Rect.prototype.getLowerRight=function(){\r\n"+
            "  return new Point(this.getRight(),this.getBottom());\r\n"+
            "};\r\n"+
            "\r\n"+
            "Rect.prototype.getLowerLeft=function(){\r\n"+
            "  return new Point(this.getLeft(),this.getBottom());\r\n"+
            "};\r\n"+
            "\r\n"+
            "Rect.prototype.getUpperRight=function(){\r\n"+
            "  return new Point(this.getRight(),this.getTop());\r\n"+
            "};\r\n"+
            "\r\n"+
            "Rect.prototype.getUpperLeft=function(){\r\n"+
            "  return new Point(this.getLeft(),this.getTop());\r\n"+
            "};\r\n"+
            "\r\n"+
            "Rect.prototype.setMidpoint=function(p){\r\n"+
            "  this.left=abs(p.x-this.width/2);\r\n"+
            "  this.top=abs(p.y-this.height/2);\r\n"+
            "  return this;\r\n"+
            "};\r\n"+
            "Rect.prototype.getMidpoint=function(){\r\n"+
            "  return new Point(this.left+this.width/2,this.top+this.height/2);\r\n"+
            "};\r\n"+
            "\r\n"+
            "Rect.prototype.setLocation = function(left,top,width,height){\r\n"+
            "  this.left=left;\r\n"+
            "  this.top=top;\r\n"+
            "  this.width=width;\r\n"+
            "  this.height=height;\r\n"+
            "  return this;\r\n"+
            "};\r\n"+
            "\r\n"+
            "function getHiddenIFrame(){\r\n"+
            "  return window.HIDDEN_IFRAME;\r\n"+
            "};\r\n"+
            "\r\n"+
            "function generateTicket()\r\n"+
            "{\r\n"+
            "    var text = \"JT-\";\r\n"+
            "    var possible = \"BCDFGHJKMNPQRSTVWXZ23456789\";\r\n"+
            "    for( var i=0; i < 5; i++ )\r\n"+
            "        text += possible.charAt(Math.floor(Math.random() * possible.length));\r\n"+
            "    return text;\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "//################\r\n"+
            "//##### Ajax #####\r\n"+
            "\r\n"+
            "function onAjaxError(o){\r\n"+
            "	var dialog = document.getElementById(\"alert_dialog\");\r\n"+
            "	if (!dialog)\r\n"+
            "		alertDialog(\"It appears the web server is not responding.<BR>Please refresh to try again (press F5)\");\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "function ajaxAndEval(url,isPost,isAsync,params){\r\n"+
            "  var callback= function(resp,status) { \r\n"+
            "    if(status==200){\r\n"+
            "       var code = decompressAndDecode(resp);\r\n"+
            "       eval(code); \r\n"+
            "    }\r\n"+
            "  }\r\n"+
            "  ajax(url,isPost,isAsync,params,callback);\r\n"+
            "}\r\n"+
            "function ajax(url,isPost,isAsync,params,callback){\r\n"+
            "  var paramsText=joinAndEncodeMap('&','=',params);\r\n"+
            "  var r;\r\n"+
            "  if (window.XMLHttpRequest) \r\n"+
            "     r=new XMLHttpRequest(); // code for IE7+, Firefox, Chrome, Opera, Safari\r\n"+
            "  else \r\n"+
            "     r= new ActiveXObject(\"Microsoft.XMLHTTP\"); // code for IE6, IE5\r\n"+
            "  if(!isPost && paramsText)\r\n"+
            "     url=url+'?'+paramsText;\r\n"+
            "  r.open(isPost ? \"POST\" : \"GET\",url,isAsync);\r\n"+
            "  if(callback){\r\n"+
            "    r.onerror=onAjaxError;\r\n"+
            "    r.responseType=\"arraybuffer\";\r\n"+
            "    r.onreadystatechange=function(o){ if (r.readyState==4) callback(r.response,r.status);  };\r\n"+
            "  }\r\n"+
            "  r.setRequestHeader(\"Content-type\",\"text/html\"); \r\n"+
            "    if(isPost)\r\n"+
            "      r.send(paramsText);\r\n"+
            "    else\r\n"+
            "      r.send();\r\n"+
            "  return ;\r\n"+
            "};\r\n"+
            "\r\n"+
            "//##################\r\n"+
            "//##### select #####\r\n"+
            "\r\n"+
            "\r\n"+
            "function Select(element){\r\n"+
            "  this.element=element;\r\n"+
            "};\r\n"+
            "\r\n"+
            "Select.prototype.element;\r\n"+
            "\r\n"+
            "Select.prototype.ensureSelectedVisible = function(){\r\n"+
            "	var min,max;\r\n"+
            "	\r\n"+
            "    var options=this.element.options;\r\n"+
            "    for(var i=0;i<options.length;i++){\r\n"+
            "        var option=options[i];\r\n"+
            "		if(option.selected){\r\n"+
            "			if(min==null)\r\n"+
            "				min=option;\r\n"+
            "		    max=option;\r\n"+
            "		}\r\n"+
            "	}\r\n"+
            "    if(min==null)\r\n"+
            "    	return;\r\n"+
            "    min.scrollIntoView();\r\n"+
            "    max.scrollIntoView();\r\n"+
            "	\r\n"+
            "}\r\n"+
            "Select.prototype.setDisabled = function(disabled){\r\n"+
            "	this.element.disabled=disabled;\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "Select.prototype.moveSelectedUp = function(){\r\n"+
            "  var options=this.element.options;\r\n"+
            "  var notSelected=false;\r\n"+
            "  for(var i=0;i<options.length;i++){\r\n"+
            "    var option=options[i];\r\n"+
            "    if(!option.selected){\r\n"+
            "      notSelected=true;\r\n"+
            "      continue;\r\n"+
            "    } else if(!notSelected)\r\n"+
            "      continue;\r\n"+
            "    this.element.removeChild(option);\r\n"+
            "    this.element.insertBefore(option, this.element[i - 1]);\r\n"+
            "  }\r\n"+
            "};\r\n"+
            "Select.prototype.moveSelectedDown = function(){\r\n"+
            "  var options=this.element.options;\r\n"+
            "  var notSelected=false;\r\n"+
            "  for(var i=options.length-1;i>=0;i--){\r\n"+
            "    var option=options[i];\r\n"+
            "    if(!option.selected){\r\n"+
            "      notSelected=true;\r\n"+
            "      continue;\r\n"+
            "    }else if(!notSelected)\r\n"+
            "      continue;\r\n"+
            "    this.element.removeChild(option);\r\n"+
            "    this.element.insertBefore(option, this.element[i + 1]);\r\n"+
            "  }\r\n"+
            "};\r\n"+
            "\r\n"+
            "Select.prototype.clearSelected = function(){\r\n"+
            "  this.element.selectedIndex=this.element.multiple ?  -1 : 0;\r\n"+
            "};\r\n"+
            "\r\n"+
            "Select.prototype.setSelectedValue = function(value){\r\n"+
            "  for(var i=0;i<this.element.length;i++)\r\n"+
            "    if(this.element.options[i].value==value){\r\n"+
            "      this.element.selectedIndex=i;\r\n"+
            "      return true;\r\n"+
            "    }\r\n"+
            "  this.element.selectedIndex=-1;\r\n"+
            "  return false;\r\n"+
            "};\r\n"+
            "Select.prototype.setSelectedValueDelimited = function(values,delim){\r\n"+
            "  var valuesMap={};\r\n"+
            "  var vals=values==null ? [] : values.split(delim);\r\n"+
            "  for(i in vals)\r\n"+
            "	  valuesMap[vals[i]]=true;\r\n"+
            "  for(var i=0;i<this.element.length;i++){\r\n"+
            "	var o=this.element.options[i];\r\n"+
            "    if(valuesMap[o.value]!=null){\r\n"+
            "      o.selected='selected';\r\n"+
            "    }\r\n"+
            "  }\r\n"+
            "};\r\n"+
            "Select.prototype.addOption=function(value,text,isSelected){\r\n"+
            "  var option=nw('option');\r\n"+
            "  option.value=value;\r\n"+
            "  option.text=text;\r\n"+
            "  option.selected=isSelected;\r\n"+
            "  this.element.add(option,null);\r\n"+
            "  return option;\r\n"+
            "};\r\n"+
            "Select.prototype.clear=function(){\r\n"+
            "  this.element.length=0;\r\n"+
            "};\r\n"+
            "\r\n"+
            "Select.prototype.getSelected=function(){\r\n"+
            "    var options=this.element.options;\r\n"+
            "    var selected=[];\r\n"+
            "    for(var i=0;i<options.length;i++){\r\n"+
            "      if(options[i].selected)\r\n"+
            "        selected[selected.length]=options[i];\r\n"+
            "    }\r\n"+
            "    return selected;\r\n"+
            "};\r\n"+
            "Select.prototype.getSelectedValues=function(){\r\n"+
            "    var options=this.element.options;\r\n"+
            "    var selected=[];\r\n"+
            "    for(var i=0;i<options.length;i++){\r\n"+
            "      if(options[i].selected)\r\n"+
            "        selected[selected.length]=options[i].value;\r\n"+
            "    }\r\n"+
            "    return selected;\r\n"+
            "};\r\n"+
            "Select.prototype.getSelectedValuesDelimited=function(delim){\r\n"+
            "    var options=this.element.options;\r\n"+
            "    var selected=\"\";\r\n"+
            "    for(var i=0;i<options.length;i++){\r\n"+
            "      if(options[i].selected){\r\n"+
            "    	if(selected!=\"\")\r\n"+
            "    		selected+=delim;\r\n"+
            "        selected+=options[i].value;\r\n"+
            "      }\r\n"+
            "    }\r\n"+
            "    return selected;\r\n"+
            "};\r\n"+
            "Select.prototype.moveSelectedTo=function(target){\r\n"+
            "    var options=this.getSelected();\r\n"+
            "    for(var i=0;i<options.length;i++){\r\n"+
            "      var option=options[i];\r\n"+
            "      target.addOption(option.value,option.text,false);\r\n"+
            "      this.element.removeChild(option);\r\n"+
            "    }\r\n"+
            "};\r\n"+
            "Select.prototype.getSelectedValue=function(){\r\n"+
            "    return this.element.options[this.element.selectedIndex].value;\r\n"+
            "};\r\n"+
            "Select.prototype.getSelectedTitle=function(){\r\n"+
            "    return this.element.options[this.element.selectedIndex].title;\r\n"+
            "};\r\n"+
            "Select.prototype.getValues=function(){\r\n"+
            "    var options=this.element.options;\r\n"+
            "    var r=[];\r\n"+
            "    for(var i=0;i<options.length;i++){\r\n"+
            "      r[i]=options[i].value;\r\n"+
            "    }\r\n"+
            "    return r;\r\n"+
            "};\r\n"+
            "\r\n"+
            "\r\n"+
            "// windowsObjMap expecting object map\r\n"+
            "// dialogSink expecting object map\r\n"+
            "function alertDia");
          out.print(
            "logWindowsGeneric(dialogFunc, args, windowsObjMap, dialogsSink){\r\n"+
            "	if(dialogsSink == null)\r\n"+
            "		dialogsSink = {};\r\n"+
            "	\r\n"+
            "	if(!Array.isArray(args))\r\n"+
            "		args = [args]; // if not array, convert to array\r\n"+
            "		\r\n"+
            "	for(k in windowsObjMap){\r\n"+
            "		dialogsSink[k] = dialogFunc.call(window, ...args, windowsObjMap[k]);\r\n"+
            "	}\r\n"+
            "	return dialogsSink;\r\n"+
            "}\r\n"+
            "// dialogSink expecting object map\r\n"+
            "function closeDialogWindowsGeneric(dialogsSink){\r\n"+
            "	if(dialogsSink == null)\r\n"+
            "		return null;\r\n"+
            "	\r\n"+
            "	for(k in dialogsSink){\r\n"+
            "		if(closeDialogGeneric(dialogsSink[k]))\r\n"+
            "			dialogsSink[k] = null;\r\n"+
            "	}\r\n"+
            "	return dialogsSink;\r\n"+
            "}\r\n"+
            "\r\n"+
            "function closeDialogGeneric(dialog){\r\n"+
            "	if(dialog == null)\r\n"+
            "		return false;\r\n"+
            "	if(dialog.visible != false)\r\n"+
            "		dialog.close();\r\n"+
            "	return true;\r\n"+
            "}\r\n"+
            "\r\n"+
            "function alertWarningDialog(title, text, jsErrorText, _window){\r\n"+
            "	if(_window == null)\r\n"+
            "		_window = window;\r\n"+
            "\r\n"+
            "    var content=nw('div','dialog_alert');\r\n"+
            "    content.innerHTML=text;\r\n"+
            "	var refreshFunc = function(e) { location.reload();};\r\n"+
            "	var dialog=new Dialog(content, _window);\r\n"+
            "	dialog.setHeaderTitle(PORTAL_DIALOG_HEADER_TITLE);\r\n"+
            "  	dialog.setTitle(title);\r\n"+
            "	dialog.setImageHtml(SVG_ERROR_BANNER);\r\n"+
            "	dialog.addButton(\"Refresh\", null, refreshFunc);\r\n"+
            "	dialog.setCanResize(false);\r\n"+
            "	if (jsErrorText) {\r\n"+
            "		dialog.setJsErrorText(jsErrorText);\r\n"+
            "		dialog.showMoreButton();\r\n"+
            "	}\r\n"+
            "	dialog.show();\r\n"+
            "	return dialog;\r\n"+
            "}\r\n"+
            "\r\n"+
            "//##### Dialog Error #####\r\n"+
            "//##################\r\n"+
            "function DialogError(Dialog) {\r\n"+
            "	var that = this;\r\n"+
            "	this.owningDialog = Dialog;\r\n"+
            "	this.divElement = nw(\"div\", \"dialog_error\");\r\n"+
            "	\r\n"+
            "	this.headerElement=nw(\"div\",\"dialog_header\");\r\n"+
            "	this.headerTitle=nw(\"div\", \"dialog_header_title\");\r\n"+
            "	this.headerLogoContainer=nw(\"div\", \"dialog_header_logo_container\");\r\n"+
            "	this.headerLogoContainer.innerHTML=SVG_LOGO_WHITE;\r\n"+
            "	this.headerElement.appendChild(this.headerTitle);\r\n"+
            "	this.headerElement.appendChild(this.headerLogoContainer);\r\n"+
            "	\r\n"+
            "	this.dialogErrorBody=nw(\"div\", \"dialog_err_body\");\r\n"+
            "	this.messageElement = nw(\"div\", \"dialog_err_msg_ele\");\r\n"+
            "	this.message = nw(\"div\", \"dialog_err_cont_msg\");\r\n"+
            "	this.copyErrorContainer = nw(\"div\", \"dialog_copy_err_cont\");\r\n"+
            "	this.messageElement.appendChild(this.message);\r\n"+
            "	this.messageElement.appendChild(this.copyErrorContainer);\r\n"+
            "	\r\n"+
            "	this.dialogErrorContainer = nw(\"div\", \"dialog_err_container\");\r\n"+
            "	this.dialogErrorTextContainer = nw(\"textArea\", \"dialog_err_text_container\");\r\n"+
            "	this.dialogErrorContainer.appendChild(this.dialogErrorTextContainer);\r\n"+
            "	\r\n"+
            "	this.dialogErrorCloseBtn = nw(\"div\", \"dialog_err_close_btn\");\r\n"+
            "	this.dialogErrorCloseBtn.innerHTML=\"<span>Close</span>\"\r\n"+
            "	\r\n"+
            "	this.dialogErrorBody.appendChild(this.messageElement);\r\n"+
            "	this.dialogErrorBody.appendChild(this.dialogErrorContainer);\r\n"+
            "	this.dialogErrorBody.appendChild(this.dialogErrorCloseBtn);\r\n"+
            "	\r\n"+
            "	this.divElement.appendChild(this.headerElement);\r\n"+
            "	this.divElement.appendChild(this.dialogErrorBody);\r\n"+
            "	//event listeners\r\n"+
            "	this.dialogErrorCloseBtn.onclick=function(){ that.onCloseBtnClicked(); };\r\n"+
            "	this.location=new Rect(0,0,600,450);\r\n"+
            "	makeDraggable(this.headerElement,this.divElement);\r\n"+
            "	this.headerElement.ondraggingEnd=function(e,x,y){that.location.move(x,y);};\r\n"+
            "	this.setCanResize(true);\r\n"+
            "	\r\n"+
            "}\r\n"+
            "DialogError.prototype.flashHeader=function() {\r\n"+
            "	this.headerElement.style.backgroundColor=\"#c15400\";\r\n"+
            "	var that = this;\r\n"+
            "	// it's safe not to clear the timeout\r\n"+
            "	setTimeout(function() {\r\n"+
            "		that.headerElement.style.backgroundColor=\"#f16900\";\r\n"+
            "	}, 150);\r\n"+
            "}\r\n"+
            "DialogError.prototype.setTitle=function(title) {\r\n"+
            "	this.headerTitle.innerHTML = title;\r\n"+
            "}\r\n"+
            "DialogError.prototype.setCanResize=function(canResize){\r\n"+
            "  var that=this;\r\n"+
            "  if(canResize){\r\n"+
            "    this.resizeButtonElement=nw(\"div\",\"dialog_resizebutton\");\r\n"+
            "    this.divElement.appendChild(this.resizeButtonElement);\r\n"+
            "    makeDraggable(this.resizeButtonElement,null);\r\n"+
            "    this.resizeButtonElement.ondragging   =function(e,x,y){var rect=that.location.clone().grow(x,y);rect.writeToElement(that.divElement);if(that.onResize)that.onResize(rect); };\r\n"+
            "    this.resizeButtonElement.ondraggingEnd=function(e,x,y){that.location.grow(x,y).writeToElement(that.divElement);if(that.onResize)that.onResize(that.location); };\r\n"+
            "  }else{\r\n"+
            "    if(this.resizeButtonElement!=null){\r\n"+
            "      this.divElement.removeChild(this.resizeButtonElement);\r\n"+
            "      this.resizeButtonElement=null;\r\n"+
            "    }\r\n"+
            "  }\r\n"+
            "};\r\n"+
            "DialogError.prototype.setErrorText=function(errorText) {\r\n"+
            "	this.dialogErrorTextContainer.value = errorText;\r\n"+
            "}\r\n"+
            "DialogError.prototype.onCloseBtnClicked=function() {\r\n"+
            "	this.close();\r\n"+
            "}\r\n"+
            "DialogError.prototype.close=function() {\r\n"+
            "	var win = this.owningDialog.getWindow();\r\n"+
            "	if (win) {\r\n"+
            "		win.document.body.removeChild(this.divElement);\r\n"+
            "		this.owningDialog.errorDialog = null;\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "DialogError.prototype.show=function() {\r\n"+
            "	var win = this.owningDialog.getWindow();\r\n"+
            "	if (win) {\r\n"+
            "		var bodyRect=new Rect().readFromElement(win.document.body);\r\n"+
            "		win.document.body.appendChild(this.divElement);\r\n"+
            "		this.location.setMidpoint(bodyRect.getMidpoint());\r\n"+
            "		this.location.writeToElement(this.divElement);\r\n"+
            "		this.setErrorText(this.owningDialog.getJsErrorText());\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "//##### Dialog #####\r\n"+
            "//##################\r\n"+
            "\r\n"+
            "function Dialog (content, _window) {\r\n"+
            "  this._window = _window == null? window :_window;\r\n"+
            "  var that=this;\r\n"+
            "  this.jsErrorText = null;\r\n"+
            "  this.errorDialog = null;\r\n"+
            "  this.divElement=nw(\"div\",\"dialog\");\r\n"+
            "  this.divElement.id=\"alert_dialog\";\r\n"+
            "  this.headerElement=nw(\"div\",\"dialog_header\");\r\n"+
            "  this.headerTitle=nw(\"div\", \"dialog_header_title\");\r\n"+
            "  this.headerLogoContainer=nw(\"div\", \"dialog_header_logo_container\");\r\n"+
            "  this.headerLogoContainer.innerHTML=SVG_LOGO_WHITE;\r\n"+
            "  this.headerElement.appendChild(this.headerTitle);\r\n"+
            "  this.headerElement.appendChild(this.headerLogoContainer);\r\n"+
            "  \r\n"+
            "  this.dialogBody=nw(\"div\", \"dialog_body\");\r\n"+
            "  this.imageElement=nw(\"div\", \"dialog_image_container\");\r\n"+
            "  this.mainMessage=nw(\"div\", \"dialog_main_message\");\r\n"+
            "  this.subMessage=nw(\"div\", \"dialog_sub_message\");\r\n"+
            "  this.jsErrorButton=nw(\"div\", \"dialog_js_error_button\");\r\n"+
            "  this.jsErrorButton.innerHTML = \"<span class='dialog_js_error_button_text'>More</span>\";\r\n"+
            "  this.closeButtonElement=nw(\"div\", \"dialog_close_button\");\r\n"+
            "  this.closeButtonElement.innerHTML=\"&#x2716;\";\r\n"+
            "  \r\n"+
            "  this.divElement.appendChild(this.headerElement);\r\n"+
            "  this.divElement.appendChild(this.closeButtonElement);\r\n"+
            "  this.divElement.appendChild(this.dialogBody);\r\n"+
            "  \r\n"+
            "  this.dialogBody.appendChild(this.imageElement);\r\n"+
            "  this.dialogBody.appendChild(this.mainMessage);\r\n"+
            "  this.dialogBody.appendChild(this.subMessage);\r\n"+
            "  this.dialogBody.appendChild(this.jsErrorButton);\r\n"+
            "  \r\n"+
            "  this.backgroundElement=nw(\"div\",\"disable_glass dialog_glass\");\r\n"+
            "\r\n"+
            "  this.visible=false;\r\n"+
            "  this.location=new Rect(0,0,400,330);\r\n"+
            "  makeDraggable(this.headerElement,this.divElement);\r\n"+
            "  this.closeButtonElement.onclick=function(e){that.close(e,'EXIT');};\r\n"+
            "  this.headerElement.ondraggingEnd=function(e,x,y){that.location.move(x,y);};\r\n"+
            "  this.jsErrorButton.onclick=function(e){ that.onJsErrorButtonClicked() };\r\n"+
            "  this.content=content;\r\n"+
            "  this.subMessage.appendChild(this.content);\r\n"+
            "}\r\n"+
            "\r\n"+
            "Dialog.prototype.setHeaderBgColor=function(newColor) {\r\n"+
            "	this.headerElement.style.backgroundColor = newColor;\r\n"+
            "}\r\n"+
            "\r\n"+
            "Dialog.prototype.setDialogBgColor=function(newColor) {\r\n"+
            "	this.divElement.style.backgroundColor = newColor;\r\n"+
            "}\r\n"+
            "\r\n"+
            "Dialog.prototype.setType=function(type) {\r\n"+
            "	if (type == \"loading\") {\r\n"+
            "		this.dialogBody.removeChild(this.mainMessage);\r\n"+
            "		this.dialogBody.removeChild(this.subMessage);\r\n"+
            "		this.dialogBody.removeChild(this.jsErrorButton);\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "Dialog.prototype.showMoreButton=function() {\r\n"+
            "	this.jsErrorButton.style.display=\"flex\";\r\n"+
            "}\r\n"+
            "Dialog.prototype.hideMoreButton=function() {\r\n"+
            "	this.jsErrorButton.style.display=\"none\";\r\n"+
            "}\r\n"+
            "Dialog.prototype.onJsErrorButtonClicked=function() {\r\n"+
            "	if (this.errorDialog) {\r\n"+
            "		this.errorDialog.flashHeader();\r\n"+
            "	} else {\r\n"+
            "		this.errorDialog = new DialogError(this);\r\n"+
            "		var l = this.subMessage.innerText.split(':');\r\n"+
            "		if (l.length == 2)\r\n"+
            "			this.errorDialog.setTitle(l[1]);\r\n"+
            "		this.errorDialog.show();\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "Dialog.prototype.setHeaderTitle=function(headerTitle) {\r\n"+
            "	headerTitle = headerTitle ? headerTitle : \"\";\r\n"+
            "	this.headerTitle.innerHTML=headerTitle;\r\n"+
            "}\r\n"+
            "Dialog.prototype.setSize=function(width,height){\r\n"+
            "	this.location.width=width;\r\n"+
            "	this.location.height=height;\r\n"+
            "};\r\n"+
            "Dialog.prototype.setCanResize=function(canResize){\r\n"+
            "  var that=this;\r\n"+
            "  if(canResize){\r\n"+
            "    this.resizeButtonElement=nw(\"div\",\"dialog_resizebutton\");\r\n"+
            "    this.divElement.appendChild(this.resizeButtonElement);\r\n"+
            "    makeDraggable(this.resizeButtonElement,null);\r\n"+
            "    this.resizeButtonElement.ondragging   =function(e,x,y){var rect=that.location.clone().grow(x,y);rect.writeToElement(that.divElement);if(that.onResize)that.onResize(rect); };\r\n"+
            "    this.resizeButtonElement.ondraggingEnd=function(e,x,y){that.location.grow(x,y).writeToElement(that.divElement);if(that.onResize)that.onResize(that.location); };\r\n"+
            "  }else{\r\n"+
            "    if(this.resizeButtonElement!=null){\r\n"+
            "      this.divElement.removeChild(this.resizeButtonElement);\r\n"+
            "      this.resizeButtonElement=null;\r\n"+
            "    }\r\n"+
            "  }\r\n"+
            "};\r\n"+
            "Dialog.prototype.setTitle=function(title){\r\n"+
            "	this.mainMessage.innerText = title;\r\n"+
            "}\r\n"+
            "Dialog.prototype.setImageHtml=function(imageHtml){\r\n"+
            "	this.imageElement.innerHTML = imageHtml;\r\n"+
            "}\r\n"+
            "Dialog.prototype.setJsErrorText=function(jsErrorText) {\r\n"+
            "	this.jsErrorText = jsErrorText;\r\n"+
            "}\r\n"+
            "Dialog.prototype.getJsErrorText=function() {\r\n"+
            "	return this.jsErrorText;\r\n"+
            "}\r\n"+
            "Dialog.prototype.addButton=function(title, reason, func){\r\n"+
            "   var button=nw('div','dialog_refresh_button'); //This css is generic\r\n"+
            "   button.innerText=title;\r\n"+
            "   this.dialogBody.appendChild(button);\r\n"+
            "   var that=this;\r\n"+
            "   if(func != null){\r\n"+
            "	   button.onclick=func;\r\n"+
            "   }\r\n"+
            "   else{\r\n"+
            "	   if(reason==null)\r\n"+
            "	       reason=title;\r\n"+
            "	   button.onclick=function(e){that.close(e,reason);};\r\n"+
            "   }\r\n"+
            "   return button;\r\n"+
            "}\r\n"+
            "Dialog.prototype.setGlassOpacity=function(opacity){\r\n"+
            "	this.backgroundElement.style.opacity=opacity;\r\n"+
            "}\r\n"+
            "Dialog.prototype.setImageSize=function(width, height) {\r\n"+
            "	var img = this.imageElement.firstChild;\r\n"+
            "	if (img) {\r\n"+
            "		img.style.height = toPx(height);\r\n"+
            "		img.styl");
          out.print(
            "e.width = toPx(width);\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "Dialog.prototype.setSmallerCloseButton=function() {\r\n"+
            "	this.closeButtonElement.classList.add(\"dialog_close_button_smaller\");\r\n"+
            "}\r\n"+
            "Dialog.prototype.show=function(){\r\n"+
            "  if(this.visible)\r\n"+
            "    return;\r\n"+
            "  var bodyRect=new Rect().readFromElement(this._window.document.body);\r\n"+
            "  this._window.document.body.appendChild(this.backgroundElement);\r\n"+
            "  this._window.document.body.appendChild(this.divElement);\r\n"+
            "  this.location.setMidpoint(bodyRect.getMidpoint());\r\n"+
            "  this.location.writeToElement(this.divElement);\r\n"+
            "  if(this.onResize!=null)\r\n"+
            "	  this.onResize(this.location);\r\n"+
            "  this.visible=true;\r\n"+
            "};\r\n"+
            "Dialog.prototype.close=function(e,reason){\r\n"+
            "  if(!this.visible){\r\n"+
            "    alert('not visible');\r\n"+
            "    return;\r\n"+
            "  }\r\n"+
            "  if(this.onClose)\r\n"+
            "    if(this.onClose(e,this,reason)==false)\r\n"+
            "    	return;\r\n"+
            "  var b=this._window.document.body;\r\n"+
            "  if(b!=null){\r\n"+
            "     b.removeChild(this.backgroundElement);\r\n"+
            "     b.removeChild(this.divElement);\r\n"+
            "  }\r\n"+
            "  if (this.errorDialog)\r\n"+
            "	  this.errorDialog.close();\r\n"+
            "  this.visible=false;\r\n"+
            "};\r\n"+
            "Dialog.prototype.getWindow=function() {\r\n"+
            "	return this._window;\r\n"+
            "}\r\n"+
            "\r\n"+
            "var dragStart;\r\n"+
            "var dragStartMouse;\r\n"+
            "var dragElement;\r\n"+
            "    	function stopDefault(e2) {\r\n"+
            "    	    if (e2 && e2.preventDefault) { e2.preventDefault(); }\r\n"+
            "    	    else { window.event.returnValue = false; }\r\n"+
            "    	}	\r\n"+
            "    	\r\n"+
            "function getWindow(element){\r\n"+
            "	if(element.ownerDocument)\r\n"+
            "	  return element.ownerDocument.defaultView;\r\n"+
            "	else{\r\n"+
            "		log('no window!');\r\n"+
            "	  return window;\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "function getDocument(element){\r\n"+
            "   return getWindow(element).document;\r\n"+
            "}\r\n"+
            "    	\r\n"+
            "//var draggableElements=[];\r\n"+
            "\r\n"+
            "function getDraggable(element, containerElement){\r\n"+
            "	if(element == null)\r\n"+
            "		return null;\r\n"+
            "	if(element.__draggableContainer == containerElement)\r\n"+
            "		return element;\r\n"+
            "	if(element.__isPushDraggableContainer == true)\r\n"+
            "		return element;\r\n"+
            "	var e = element.parentNode;\r\n"+
            "	while(e!=null && e.isDraggable != true && e.__draggableContainer != containerElement && e.__isPushDraggableContainer != true)\r\n"+
            "		e=e.parentNode;\r\n"+
            "	if(e != containerElement)\r\n"+
            "		return e;\r\n"+
            "	else return null;\r\n"+
            "}\r\n"+
            "\r\n"+
            "function resetPushDraggableContainer(containerElement){\r\n"+
            "	containerElement.__pushDraggableElements=[];\r\n"+
            "	containerElement.__pushDraggableElementsNew=[];\r\n"+
            "	containerElement.__pushDraggableTriggerElements=[];\r\n"+
            "	containerElement.__isDraggedVisible=true;\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "function ContainerWithDraggableElements(element, calcContainerRect, onDragHandler, onDragEndHandler, onMousePositionHandler, additionalTargetsList, \r\n"+
            "		getIdx, getMousePosition, animated, moveX, moveY){\r\n"+
            "	var that = this;\r\n"+
            "	this.__element = element;\r\n"+
            "	this.__doc = getDocument(this.__element);\r\n"+
            "	this.__dragOver = this.dragOver.bind(this);\r\n"+
            "	this.__doc.body.addEventListener(\"dragover\", this.__dragOver);\r\n"+
            "	this.__drop = this.drop.bind(this);\r\n"+
            "//	this.__doc.body.addEventListener(\"drop\", this.__drop);\r\n"+
            "	this.__sortedDraggableElementsX=[];\r\n"+
            "	this.__sortedDraggableElementsY=[];\r\n"+
            "	// This is for actions where you want to drag outside or trigger other custom actions\r\n"+
            "	// Expecting elements with optionally __onDragOver or __onDrop event\r\n"+
            "	this.__additionalTargetsList = additionalTargetsList == null ? []: additionalTargetsList;\r\n"+
            "	this.onDragHandler = onDragHandler;\r\n"+
            "	this.onDragEndHandler = onDragEndHandler;\r\n"+
            "	this.onMousePositionHandler = onMousePositionHandler;\r\n"+
            "	this.getIdx=getIdx;\r\n"+
            "	this.getMousePosition = getMousePosition == null? this.defaultGetMousePosition: getMousePosition;\r\n"+
            "	this.animated = animated;\r\n"+
            "	this.__moveX = moveX;\r\n"+
            "	this.__moveY = moveY;\r\n"+
            "\r\n"+
            "	//TODO default indexfuncs;\r\n"+
            "	var getDefaultLocationX = function(de){\r\n"+
            "		return de.__indexX;\r\n"+
            "	};\r\n"+
            "	var getDefaultLocationY = function(de){\r\n"+
            "		return de.__indexY;\r\n"+
            "	};\r\n"+
            "//	https://stackoverflow.com/questions/37958394/html5-on-drag-change-dragging-image-or-icon\r\n"+
            "\r\n"+
            "	this.calcContainerRect = calcContainerRect;\r\n"+
            "	this.initRect(calcContainerRect);\r\n"+
            "\r\n"+
            "    this.noDragImage = new Image();\r\n"+
            "    this.noDragImage.src = \"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAAC0lEQVQI12NgAAIAAAUAAeImBZsAAAAASUVORK5CYII=\";\r\n"+
            "} \r\n"+
            "ContainerWithDraggableElements.prototype.__element;\r\n"+
            "ContainerWithDraggableElements.prototype.__rect;\r\n"+
            "//ContainerWithDraggableElements.prototype.__pushDraggableElements; // unused;\r\n"+
            "ContainerWithDraggableElements.prototype.__sortedDraggableElementsX;\r\n"+
            "ContainerWithDraggableElements.prototype.__sortedDraggableElementsY;\r\n"+
            "\r\n"+
            "ContainerWithDraggableElements.prototype.__moveX;\r\n"+
            "ContainerWithDraggableElements.prototype.__moveY;\r\n"+
            "ContainerWithDraggableElements.prototype.__flipMouseXY;\r\n"+
            "\r\n"+
            "ContainerWithDraggableElements.prototype.__sourceElement;\r\n"+
            "ContainerWithDraggableElements.prototype.__changedElements = new Set();\r\n"+
            "//ContainerWithDraggableElements.prototype.__initialMouseOffsetX;\r\n"+
            "//ContainerWithDraggableElements.prototype.__initialMouseOffsetY;\r\n"+
            "ContainerWithDraggableElements.prototype._lastTargetIndex;\r\n"+
            "ContainerWithDraggableElements.prototype.__isDraggedVisible=true;\r\n"+
            "\r\n"+
            "ContainerWithDraggableElements.prototype.getIdx;\r\n"+
            "\r\n"+
            "ContainerWithDraggableElements.prototype.initRect=function(calc){\r\n"+
            "	if(this.calcContainerRect != null){\r\n"+
            "		this.__rect = this.calcContainerRect(); // Intent: given the rectangle will apply the position\r\n"+
            "	}\r\n"+
            "	else{\r\n"+
            "		this.__rect = this.__element.getBoundingClientRect();\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "ContainerWithDraggableElements.prototype.getDraggableElementsMidX=function(){\r\n"+
            "	var midpoints = [];\r\n"+
            "	if(this.__sortedDraggableElementsX.length == 0)\r\n"+
            "		return midpoints;\r\n"+
            "	var rectFirst = this.__sortedDraggableElementsX[0].__rect;\r\n"+
            "	var first = rectFirst.left;\r\n"+
            "	midpoints.push(first);\r\n"+
            "	var len = this.__sortedDraggableElementsX.length;\r\n"+
            "	for(var i = 0; i < len; i++){\r\n"+
            "		var rect = this.__sortedDraggableElementsX[i].__rect;\r\n"+
            "		var mp = rect.left + rect.width/2;\r\n"+
            "		midpoints.push(mp);\r\n"+
            "	}\r\n"+
            "	var rectLast = this.__sortedDraggableElementsX[len-1].__rect;\r\n"+
            "	var last = rectLast.left + rectLast.width;\r\n"+
            "	midpoints.push(last);\r\n"+
            "	return midpoints;\r\n"+
            "}\r\n"+
            "// Find index of an ordered list for a number if it were to be inserted\r\n"+
            "// Returns 0 if before any number in list or list length if after all numbers in list\r\n"+
            "ContainerWithDraggableElements.prototype.findIndexOfNum=function(orderedList, num){\r\n"+
            "	var i = 0;\r\n"+
            "	for(; i < orderedList.length; i++){\r\n"+
            "		var num0 = orderedList[i];\r\n"+
            "		if(num0 > num)\r\n"+
            "			break;\r\n"+
            "	}\r\n"+
            "	return i;\r\n"+
            "}\r\n"+
            "\r\n"+
            "ContainerWithDraggableElements.prototype.clear=function(){\r\n"+
            "	this.__doc.body.removeEventListener(\"dragover\", this.__dragOver);\r\n"+
            "	for(var i = 0; i < this.__sortedDraggableElementsX.length; i++){\r\n"+
            "		this.__sortedDraggableElementsX[i].__triggerElement.removeEventListener(\"dragstart\", this.__dragStart);\r\n"+
            "		this.__sortedDraggableElementsX[i].__triggerElement.removeEventListener(\"dragend\", this.__dragEnd);\r\n"+
            "	}\r\n"+
            "\r\n"+
            "}\r\n"+
            "ContainerWithDraggableElements.prototype.sort=function(){\r\n"+
            "	var sortX =  function(a,b){\r\n"+
            "		return a.__rect.left < b.__rect.left ? -1 : a.__rect.left > b.__rect.left ? 1 : 0;\r\n"+
            "	};\r\n"+
            "	var sortY =  function(a,b){\r\n"+
            "		return a.__rect.top < b.__rect.top ? -1 : a.__rect.top > b.__rect.top ? 1 : 0;\r\n"+
            "	};\r\n"+
            "	this.__sortedDraggableElementsX.sort(sortX);\r\n"+
            "	this.__sortedDraggableElementsY.sort(sortY);\r\n"+
            "\r\n"+
            "	// Update index\r\n"+
            "	for(var i = 0; i < this.__sortedDraggableElementsX.length; i++){\r\n"+
            "		this.__sortedDraggableElementsX[i].__indexX= i;\r\n"+
            "	}\r\n"+
            "	for(var i = 0; i < this.__sortedDraggableElementsY.length; i++){\r\n"+
            "		this.__sortedDraggableElementsY[i].__indexY= i;\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "ContainerWithDraggableElements.prototype.addDraggableElement=function(draggableElement){\r\n"+
            "	this.__sortedDraggableElementsX.push(draggableElement);\r\n"+
            "	this.__sortedDraggableElementsY.push(draggableElement);\r\n"+
            "	this.__dragStart = this.dragStart.bind(this);\r\n"+
            "	this.__dragEnd = this.dragEnd.bind(this);\r\n"+
            "	draggableElement.__triggerElement.addEventListener(\"dragstart\", this.__dragStart);\r\n"+
            "    draggableElement.__triggerElement.addEventListener(\"dragend\", this.__dragEnd);\r\n"+
            "}\r\n"+
            "//Returns the object DraggableElement otherwise returns the html element that is an additional target\r\n"+
            "ContainerWithDraggableElements.prototype.getDraggable=function(element){\r\n"+
            "	if(element == null)\r\n"+
            "		return null;\r\n"+
            "	if(element.__draggableElement != null && element.__draggableElement.__container == this){\r\n"+
            "		return element.__draggableElement;\r\n"+
            "	}\r\n"+
            "	for(var i = 0 ; i < this.__additionalTargetsList.length; i++){\r\n"+
            "		var elem = this.__additionalTargetsList[i];\r\n"+
            "		if(element == elem)\r\n"+
            "			return element;\r\n"+
            "	}\r\n"+
            "	\r\n"+
            "	var e = element.parentNode;\r\n"+
            "	while(e != null){\r\n"+
            "		if(e.__draggableElement != null && e.__draggableElement.__container == this)\r\n"+
            "			return e.__draggableElement;\r\n"+
            "		for(var i = 0 ; i < this.__additionalTargetsList.length; i++){\r\n"+
            "			var elem = this.__additionalTargetsList[i];\r\n"+
            "			if(element == elem)\r\n"+
            "				return element;\r\n"+
            "		}\r\n"+
            "		\r\n"+
            "		e=e.parentNode;\r\n"+
            "	} \r\n"+
            "	return null;\r\n"+
            "}\r\n"+
            "ContainerWithDraggableElements.prototype.moveElement=function(draggableElement, offsetX, offsetY, indexOffsetX, indexOffsetY){\r\n"+
            "	draggableElement.__rect.left += offsetX;\r\n"+
            "	draggableElement.__indexX += indexOffsetX;\r\n"+
            "	draggableElement.applyRect(draggableElement.__rect);\r\n"+
            "	\r\n"+
            "}\r\n"+
            "ContainerWithDraggableElements.prototype.defaultGetMousePosition=function(event){\r\n"+
            "	var r = { x : event.x - this.__rect.left , \r\n"+
            "			y :	this.__rect.top - event.y  };\r\n"+
            "	return r;\r\n"+
            "}\r\n"+
            "ContainerWithDraggableElements.prototype.dragOver=function(event){\r\n"+
            "	event.preventDefault();\r\n"+
            "	event.dataTransfer.dropEffect = \"move\";\r\n"+
            "\r\n"+
            "	var mouse = this.getMousePosition(event);\r\n"+
            "	var relativeMouseX = mouse.x;\r\n"+
            "	var relativeMouseY = mouse.y;\r\n"+
            "\r\n"+
            "	if(this.__moveX == true){\r\n"+
            "		var listMidpointsX = this.getDraggableElementsMidX();\r\n"+
            "		var midPtIdx = this.findIndexOfNum(listMidpointsX, relativeMouseX) - 1;\r\n"+
            "\r\n"+
            "		var currentIndex = this.__sourceElement.__indexX;\r\n"+
            "		var afterSourceIndex = null;\r\n"+
            "		var targetIndex = currentIndex;\r\n"+
            "\r\n"+
            "		// Move right * needs to be current idx plus one\r\n"+
            "		if(midPtIdx <= -1 || midPtIdx > this.__sortedDraggableElementsX.length){\r\n"+
            "			// do nothing\r\n"+
            "		}\r\n"+
            "		else if(midPtIdx > (currentIndex +1)){\r\n"+
            "			afterSourceIndex = currentIndex + 1;\r\n"+
            "			targetIndex = midPtIdx - 1;\r\n"+
            "		}\r\n"+
            "		// Move left * needs to be just less than ");
          out.print(
            "currentIdx\r\n"+
            "		else if (midPtIdx < (currentIndex)){\r\n"+
            "			afterSourceIndex = currentIndex - 1;\r\n"+
            "			targetIndex = midPtIdx - 0;\r\n"+
            "		}\r\n"+
            "\r\n"+
            "		if(targetIndex != currentIndex){\r\n"+
            "			var sourceIndexOffset = targetIndex - currentIndex;\r\n"+
            "			var targetIndexOffset = currentIndex - afterSourceIndex;\r\n"+
            "			var sourcePxOffset = 0;\r\n"+
            "	   		var targetPxOffset = 0;\r\n"+
            "	   		\r\n"+
            "	   		var srcRect = this.__sourceElement.__rect;\r\n"+
            "	   		var afterSrcRect = this.__sortedDraggableElementsX[afterSourceIndex].__rect;\r\n"+
            "	   		\r\n"+
            "\r\n"+
            "	   		var tgtRect = this.__sortedDraggableElementsX[targetIndex].__rect;\r\n"+
            "\r\n"+
            "	   		\r\n"+
            "	   		if(targetIndex > currentIndex){\r\n"+
            "	   			sourcePxOffset = Math.round(tgtRect.left - srcRect.left) + (Math.round(tgtRect.width - srcRect.width));\r\n"+
            "	   			targetPxOffset = Math.round(srcRect.left - afterSrcRect.left);\r\n"+
            "	   		}else{\r\n"+
            "	   			sourcePxOffset = Math.round(tgtRect.left - srcRect.left) ;\r\n"+
            "		   		targetPxOffset = Math.round(srcRect.left - afterSrcRect.left) + Math.round(srcRect.width - afterSrcRect.width);\r\n"+
            "	   		}\r\n"+
            "	   		\r\n"+
            "			this.moveElement(this.__sourceElement, sourcePxOffset, null, sourceIndexOffset ,null);\r\n"+
            "\r\n"+
            "			var startIndex = currentIndex < targetIndex? currentIndex+1:targetIndex;\r\n"+
            "			var endIndex = currentIndex < targetIndex? targetIndex:currentIndex-1;\r\n"+
            "			for(var i = startIndex; i <= endIndex; i++){\r\n"+
            "				var currentTarget= this.__sortedDraggableElementsX[i];\r\n"+
            "				this.moveElement(currentTarget, targetPxOffset, null, targetIndexOffset ,null);\r\n"+
            "	 		}\r\n"+
            "			this.sort();\r\n"+
            "		}\r\n"+
            "   		// Handle Mouse Position \r\n"+
            "		var mousePositionHandler = this.onMousePositionHandler;\r\n"+
            "   		if(mousePositionHandler != null){\r\n"+
            "			var contRect = this.__sourceElement.__rect;\r\n"+
            "			var moved = false;\r\n"+
            "   			if(this.__moveX )\r\n"+
            "   				moved = mousePositionHandler(event, this.__rect, relativeMouseX, this.__sourceElement.__rect.width, this.__moveX , this.__moveY );\r\n"+
            "//   				moved = mousePositionHandler(event.x, this.__rect.x, this.__rect.width, relativeMouseX, this.__sourceElement.__rect.width, this.__moveX , this.__moveY );\r\n"+
            "   				//moved = mousePositionHandler(event.x, contRect.x, containerElement.__initialMouseOffsetX, eventSource.__rect.width, moveX, moveY);\r\n"+
            "   			if(this.__moveY)\r\n"+
            "   				moved = mousePositionHandler(event.y, contRect.top, relativeMouseY, this.__sourceElement.__rect.width, this.__moveX , this.__moveY );\r\n"+
            "   			if(moved){\r\n"+
            "				for(var changedIndex of containerElement.__changedIndexes){\r\n"+
            "					var draggableElement = containerElement.__pushDraggableElements[changedIndex];\r\n"+
            "//					saveRect(draggableElement)\r\n"+
            "//					updateRect(draggableElement);\r\n"+
            "//					repaintRect(draggableElement);\r\n"+
            "				}\r\n"+
            "\r\n"+
            "   			}\r\n"+
            "   		}\r\n"+
            "\r\n"+
            "		\r\n"+
            "		\r\n"+
            "	}\r\n"+
            "	// loop through all objs\r\n"+
            "	// find the first\r\n"+
            "\r\n"+
            "//	var eventTarget = this.getDraggable(event.target);\r\n"+
            "//	if(eventTarget != null && eventTarget != this.__sourceElement){\r\n"+
            "//		eventTarget.__onDragOver.call(this, event, this.__sourceElement, eventTarget);\r\n"+
            "//		err(eventTarget);\r\n"+
            "//	}\r\n"+
            "\r\n"+
            "\r\n"+
            "//	err([\"dragging\",event, event.pageX, event.offsetX, event.x]);\r\n"+
            "	\r\n"+
            "}\r\n"+
            "ContainerWithDraggableElements.prototype.drop=function(event){\r\n"+
            "//	err(event);\r\n"+
            "	event.preventDefault();\r\n"+
            "//	err(\"drop\");\r\n"+
            "	\r\n"+
            "}\r\n"+
            "ContainerWithDraggableElements.prototype.dragStart=function(event){\r\n"+
            "\r\n"+
            "    event.dataTransfer.setDragImage(this.noDragImage, 0, 0);\r\n"+
            "//    event.dataTransfer.dropEffect= \"move\";\r\n"+
            "\r\n"+
            "\r\n"+
            "\r\n"+
            "	this.__sourceElement = event.target.__draggableElement; \r\n"+
            "	this.__sourceElement.__element.style.opacity=0.2;\r\n"+
            "	this.__changedElements = new Set();\r\n"+
            "	this.__initialMouseOffsetX = event.offsetX;\r\n"+
            "	this.__initialMouseOffsetY = event.offsetY;\r\n"+
            "\r\n"+
            "	this.sort();\r\n"+
            "\r\n"+
            "	this.__sourceElement.__origIndexX= this.__sourceElement.__indexX;\r\n"+
            "	this.__sourceElement.__origIndexY= this.__sourceElement.__indexY;\r\n"+
            "	this.__oldIdx = this.__sourceElement.getIdx();\r\n"+
            "}\r\n"+
            "\r\n"+
            "ContainerWithDraggableElements.prototype.dragEnd=function(event){\r\n"+
            "\r\n"+
            "   	var dropSuccess = true;\r\n"+
            "	if(event.dataTransfer.dropEffect== \"none\"){\r\n"+
            "		dropSuccess = false;\r\n"+
            "	}\r\n"+
            "	\r\n"+
            "	this.sort();\r\n"+
            "	\r\n"+
            "	var oldIndex = this.__oldIdx;\r\n"+
            "	var newIndex = this.__sourceElement.__indexX;\r\n"+
            "	\r\n"+
            "	this.__sourceElement.__element.style.opacity=\"initial\";\r\n"+
            "\r\n"+
            "    //newLocation..\r\n"+
            "    this.__oldIdx = null\r\n"+
            "	this.__changedElements.clear();\r\n"+
            "	this.__initialMouseOffsetX = null;\r\n"+
            "	this.__initialMouseOffsetY = null;\r\n"+
            "	this.__sourceElement.__origIndexX= null;\r\n"+
            "	this.__sourceElement.__origIndexY= null;\r\n"+
            "\r\n"+
            "\r\n"+
            "\r\n"+
            "\r\n"+
            "	if(dropSuccess && this.onDragEndHandler!=null)\r\n"+
            "		this.onDragEndHandler(oldIndex, newIndex);\r\n"+
            "	this.__sourceElement = null; \r\n"+
            "}\r\n"+
            "\r\n"+
            "/*\r\n"+
            " * The element is full element, the triggerElement is what holds the event listener, these may just be the same\r\n"+
            " * The rect is used to set the location of the element and is relative to the parent element\r\n"+
            " * Idx is the idx location given from the server this may be any object\r\n"+
            " * The element is expected to be positioned absolutely\r\n"+
            " */\r\n"+
            "function DraggableElement(draggableContainer, element, triggerElement, calcRect, idx){\r\n"+
            "	this.__container = draggableContainer;\r\n"+
            "	this.__element = element;\r\n"+
            "	this.__element.__draggableElement=this;\r\n"+
            "	this.__triggerElement = triggerElement;\r\n"+
            "	this.__triggerElement.__draggableElement=this;\r\n"+
            "	this.__triggerElement.draggable=\"true\";\r\n"+
            "	this.__element.isDraggable=true; // ??\r\n"+
            "	this.calcRect = calcRect;\r\n"+
            "	this.initRect(calcRect);\r\n"+
            "	this.applyRect(this.__origRect);\r\n"+
            "	this.__idx = idx;\r\n"+
            "}\r\n"+
            "DraggableElement.prototype.__container;\r\n"+
            "DraggableElement.prototype.__element;\r\n"+
            "DraggableElement.prototype.__triggerElement;\r\n"+
            "\r\n"+
            "DraggableElement.prototype.__onDragOver;\r\n"+
            "DraggableElement.prototype.__onDrop;\r\n"+
            "\r\n"+
            "DraggableElement.prototype.__rect;\r\n"+
            "DraggableElement.prototype.__origRect;\r\n"+
            "\r\n"+
            "DraggableElement.prototype.__relativeRect;\r\n"+
            "DraggableElement.prototype.__indexX;\r\n"+
            "DraggableElement.prototype.__indexY;\r\n"+
            "DraggableElement.prototype.__origIndexX;\r\n"+
            "DraggableElement.prototype.__origIndexY;\r\n"+
            "DraggableElement.prototype.__idx; // Location for server\r\n"+
            "\r\n"+
            "DraggableElement.prototype.initRect=function(calcRect){\r\n"+
            "	if(calcRect == null){\r\n"+
            "		var rect = this.__element.getBoundingClientRect();\r\n"+
            "		var prect = this.__container.__rect;\r\n"+
            "		this.__origRect = new Rect(\r\n"+
            "				rect.x-prect.x, rect.y - prect.y,\r\n"+
            "				rect.width, rect.height\r\n"+
            "				);\r\n"+
            "		this.__rect = this.__origRect;\r\n"+
            "	}\r\n"+
            "	else{\r\n"+
            "		this.__origRect=this.calcRect(); // Intent: given the rectangle will apply the position\r\n"+
            "		this.__rect = this.__origRect;\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "DraggableElement.prototype.applyRect=function(rect){\r\n"+
            "	this.__element.style.position=\"absolute\";\r\n"+
            "	this.__element.style.left=toPx(rect.left);\r\n"+
            "	this.__element.style.top=toPx(rect.top);\r\n"+
            "	this.__element.style.width=toPx(rect.width);\r\n"+
            "	this.__element.style.height=toPx(rect.height);\r\n"+
            "};\r\n"+
            "\r\n"+
            "DraggableElement.prototype.getIdx=function(){\r\n"+
            "	return this.__container.getIdx(this);\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "function makePushDraggableContainer(containerElement, moveX, moveY, onDragHandler, mousePositionHandler){\r\n"+
            "	if(containerElement == null) \r\n"+
            "		return; \r\n"+
            "	if(containerElement.__isPushDraggableContainer == true)\r\n"+
            "		return;\r\n"+
            "	containerElement.__isPushDraggableContainer = true;\r\n"+
            "	containerElement.__moveX=moveX;\r\n"+
            "	containerElement.__moveY=moveY;\r\n"+
            "	resetPushDraggableContainer(containerElement);\r\n"+
            "	\r\n"+
            "	// Moves element to calculated offset x and y to follow mouse plane\r\n"+
            "	var repaintRect = function(element){\r\n"+
            "		if(element.__offsetX !=null)\r\n"+
            "	 		element.style.left = toPx(element.__offsetX);\r\n"+
            "		if(element.__offsetY !=null)\r\n"+
            "			element.style.top  = toPx(element.__offsetY);\r\n"+
            "	};\r\n"+
            "	var saveRect = function(element){\r\n"+
            "		element.__rect = element.getBoundingClientRect(); \r\n"+
            "	}\r\n"+
            "	// Calculates Offset\r\n"+
            "	var updateRect = function(element){\r\n"+
            "		if(element.style.display=='none')\r\n"+
            "			element.style.display='initial';\r\n"+
            "		if(element.__draggableContainer != null){\r\n"+
            "			if(element.__draggableContainer.__moveX && element.__offsetX == null)\r\n"+
            "				element.__offsetX = element.__rect.x - element.__draggableContainer.__rect.x;\r\n"+
            "			if(element.__draggableContainer.__moveY && element.__offsetY == null)\r\n"+
            "				element.__offsetY = element.__rect.y - element.__draggableContainer.__rect.y;\r\n"+
            "		}\r\n"+
            "	};\r\n"+
            "	\r\n"+
            "	var moveDraggable = function(draggableContainerElement, draggableElement, indexOffset, pxOffset, isMoveX, isMoveY){\r\n"+
            "		draggableElement.__draggableNewIndex += indexOffset;\r\n"+
            "			draggableContainerElement.__pushDraggableElementsNew[draggableElement.__draggableNewIndex] = draggableElement; \r\n"+
            "		if(isMoveX){\r\n"+
            "			draggableElement.__rect.x += pxOffset;\r\n"+
            "			draggableElement.__rect.left += pxOffset;\r\n"+
            "			\r\n"+
            "			draggableElement.__offsetX += pxOffset; \r\n"+
            "			draggableElement.style.left = toPx(draggableElement.__offsetX);\r\n"+
            "		}\r\n"+
            "		else if(isMoveY){\r\n"+
            "			draggableElement.__rect.y += pxOffset;\r\n"+
            "			draggableElement.__rect.top += pxOffset;\r\n"+
            "\r\n"+
            "			draggableElement.__offsetY += pxOffset;\r\n"+
            "			draggableElement.style.top = toPx(draggableElement.__offsetY);\r\n"+
            "		}\r\n"+
            "	};\r\n"+
            "	\r\n"+
            "	\r\n"+
            "    var dragOver = function(event){\r\n"+
            "    	var eventSource = containerElement.__initiatorElement;\r\n"+
            "    	// If the target isn't draggable don't start the drag action which means drag event hasn't started\r\n"+
            "    	if(eventSource == null) // Source is the object being dragged over\r\n"+
            "    	    return false;\r\n"+
            "    	var eventTarget = getDraggable(event.target,containerElement);\r\n"+
            "    	\r\n"+
            "//    	if(eventTarget == eventSource) // Target is the dragged item\r\n"+
            "//    		return false;\r\n"+
            "\r\n"+
            "	   	if(eventTarget !=null){\r\n"+
            "	    	var isContainer = eventTarget.__isPushDraggableContainer == true;\r\n"+
            "	    	var isDraggableElement = eventTarget.__draggableContainer == containerElement;\r\n"+
            "	    	\r\n"+
            "	    	if(isContainer){\r\n"+
            "	    		containerElement.__isDraggedVisible=true;\r\n"+
            "	    		event.preventDefault();\r\n"+
            "	    		event.dataTransfer.dropEffect = \"move\";\r\n"+
            "	    	}\r\n"+
            "	    	else if(isDraggableElement){\r\n"+
            "	    		event.preventDefault();\r\n"+
            "	    		event.dataTransfer.dropEffect = \"move\";\r\n"+
            "	\r\n"+
            "	\r\n"+
            "				var srcRect = eventSource.__rect;\r\n"+
            "				var tgtRect = eventTarget.__rect;\r\n"+
            "	    		var midPointXsrc = srcRect.left + srcRect.width/2;\r\n"+
            "	    		var midPointYsrc = srcRect.top + srcRect.height/2;\r\n"+
            "	    		var midPointX = tgtRect.left + tgtRect.width/2; // Target is itself because you're always dragging it");
          out.print(
            "\r\n"+
            "	    		var midPointY = tgtRect.top + tgtRect.height/2;\r\n"+
            "	    		var currentIndex = eventSource.__draggableNewIndex;\r\n"+
            "				var targetIndex = eventTarget.__draggableNewIndex;\r\n"+
            "	\r\n"+
            "				if(currentIndex == targetIndex){\r\n"+
            "					containerElement.__lastTargetIndex = targetIndex;\r\n"+
            "				}\r\n"+
            "	    		else if(containerElement.__isDraggedVisible==true || targetIndex != containerElement.__lastTargetIndex){\r\n"+
            "	   				containerElement.__isDraggedVisible=false;\r\n"+
            "					containerElement.__lastTargetIndex = targetIndex;\r\n"+
            "	    		}\r\n"+
            "	    		else if(containerElement.__isDraggedVisible == false){\r\n"+
            "					//err(\"show\");\r\n"+
            "	    			var update = false;\r\n"+
            "	    			var newTargetIndex = null;\r\n"+
            "	    			if(containerElement.__moveY == true){\r\n"+
            "	    				if(currentIndex < targetIndex){\r\n"+
            "	    					if(event.y > midPointY){\r\n"+
            "	    					}\r\n"+
            "	    					else{\r\n"+
            "	    						newTargetIndex = targetIndex-1;\r\n"+
            "	    					}\r\n"+
            "	    				}\r\n"+
            "	    				else if(currentIndex > targetIndex){\r\n"+
            "	    					if(event.y < midPointY){\r\n"+
            "	    					}\r\n"+
            "	    					else{\r\n"+
            "	    						newTargetIndex = targetIndex+1;\r\n"+
            "	    					}\r\n"+
            "	    				}\r\n"+
            "	    			}\r\n"+
            "	    			if(containerElement.__moveX == true){\r\n"+
            "	    				if(currentIndex < targetIndex){\r\n"+
            "	    					if(event.x > midPointX){\r\n"+
            "	    					}\r\n"+
            "	    					else{\r\n"+
            "	    						newTargetIndex = targetIndex-1;\r\n"+
            "	    					}\r\n"+
            "	    				}\r\n"+
            "	    				else if(currentIndex > targetIndex){\r\n"+
            "	    					if(event.x < midPointX){\r\n"+
            "	    					}\r\n"+
            "	    					else{\r\n"+
            "	    						newTargetIndex = targetIndex+1;\r\n"+
            "	    					}\r\n"+
            "	    				}\r\n"+
            "	    			}\r\n"+
            "	    			\r\n"+
            "	    			//if(newTargetIndex == null)\r\n"+
            "	    			//	return false;\r\n"+
            "	    			if(newTargetIndex !=null && newTargetIndex != targetIndex){\r\n"+
            "	    				var newTargetElement = containerElement.__pushDraggableElements[newTargetIndex];\r\n"+
            "	    				tgtRect	= newTargetElement.__rect;\r\n"+
            "						targetIndex = newTargetIndex;\r\n"+
            "//						err([newTargetElement, eventTarget, eventSource]);\r\n"+
            "	    			}\r\n"+
            "	    			\r\n"+
            "	    			if(currentIndex != targetIndex)\r\n"+
            "	    				update=true;\r\n"+
            "	    				\r\n"+
            "	    			\r\n"+
            "	    			if(update == true){\r\n"+
            "	    				containerElement.__isDraggedVisible=true;\r\n"+
            "						//move\r\n"+
            "						var sourcePxOffset = 0;\r\n"+
            "	   					var targetPxOffset = 0;\r\n"+
            "	   					var sourceIndexOffset = targetIndex - currentIndex;\r\n"+
            "	   					var targetIndexOffset = - (sourceIndexOffset);\r\n"+
            "	   					\r\n"+
            "	   					if(moveX == true){\r\n"+
            "	    					if(currentIndex < targetIndex){\r\n"+
            "	    						var srcElement1Offset = containerElement.__pushDraggableElements[currentIndex + 1];\r\n"+
            "	    						var src1OffsetRect = srcElement1Offset.__rect;\r\n"+
            "\r\n"+
            "		   						sourcePxOffset = Math.round(tgtRect.x - srcRect.x) + (Math.round(tgtRect.width - srcRect.width)) -1;\r\n"+
            "		   						targetPxOffset = Math.round(srcRect.x - src1OffsetRect.x) - 1 ;\r\n"+
            "	    					}\r\n"+
            "	    					else if(currentIndex > targetIndex){\r\n"+
            "	    						var srcElement1Offset = containerElement.__pushDraggableElements[currentIndex - 1];\r\n"+
            "	    						var src1OffsetRect = srcElement1Offset.__rect;\r\n"+
            "\r\n"+
            "		   						sourcePxOffset = Math.round(tgtRect.x - srcRect.x) + 1;\r\n"+
            "		   						targetPxOffset = Math.round(srcRect.x - src1OffsetRect.x) + Math.round(srcRect.width - src1OffsetRect.width) + 1;\r\n"+
            "	   						}\r\n"+
            "	\r\n"+
            "	   					}\r\n"+
            "	   					if(moveY == true){\r\n"+
            "	    					if(currentIndex < targetIndex){\r\n"+
            "	    						var srcElement1Offset = containerElement.__pushDraggableElements[currentIndex - 1];\r\n"+
            "	    						var src1OffsetRect = srcElement1Offset.__rect;\r\n"+
            "\r\n"+
            "		   						sourcePxOffset = tgtRect.y - srcRect.y + (tgtRect.height - srcRect.height);\r\n"+
            "		   						targetPxOffset = (srcRect.y - src1OffsetRect.y);\r\n"+
            "	    					}\r\n"+
            "	    					else if(currentIndex > targetIndex){\r\n"+
            "	    						var srcElement1Offset = containerElement.__pushDraggableElements[currentIndex + 1];\r\n"+
            "	    						var src1OffsetRect = srcElement1Offset.__rect;\r\n"+
            "\r\n"+
            "		   						sourcePxOffset = (tgtRect.y - srcRect.y) ;\r\n"+
            "		   						targetPxOffset = +(src1OffsetRect.y - srcRect.y);\r\n"+
            "	   						}\r\n"+
            "	   					}\r\n"+
            "	   					//Moving and updating indexes on element\r\n"+
            "						containerElement.__changedIndexes.add(currentIndex);\r\n"+
            "						moveDraggable(containerElement, eventSource, sourceIndexOffset, sourcePxOffset, moveX, moveY);\r\n"+
            "	 					\r\n"+
            "	 					var startIndex = currentIndex < targetIndex? currentIndex+1:targetIndex;\r\n"+
            "	 					var endIndex = currentIndex < targetIndex? targetIndex:currentIndex-1;\r\n"+
            "	 					\r\n"+
            "	 					var countTarget = abs(targetIndexOffset);\r\n"+
            "	 					targetIndexOffset /= countTarget; \r\n"+
            "	 					\r\n"+
            "	 					for(var i = startIndex; i <= endIndex; i++){\r\n"+
            "	 						var currentTarget= containerElement.__pushDraggableElements[i];\r\n"+
            "							containerElement.__changedIndexes.add(currentTarget.__draggableIndex);\r\n"+
            "		   					moveDraggable(containerElement, currentTarget, targetIndexOffset, targetPxOffset, moveX, moveY); \r\n"+
            "	 					}\r\n"+
            "\r\n"+
            "	 					\r\n"+
            "	 					//update indexes on container\r\n"+
            "	 					containerElement.__pushDraggableElements[currentIndex] = containerElement.__pushDraggableElementsNew[currentIndex]; \r\n"+
            "	 					for(var i = startIndex; i <= endIndex; i++){\r\n"+
            "							containerElement.__pushDraggableElements[i] = containerElement.__pushDraggableElementsNew[i];\r\n"+
            "	 					}\r\n"+
            "	 					\r\n"+
            "	\r\n"+
            "						//update indexes\r\n"+
            "						containerElement.__lastTargetIndex = currentIndex;\r\n"+
            "						\r\n"+
            "						\r\n"+
            "						//Call callback if provided\r\n"+
            "						if(onDragHandler!=null){\r\n"+
            "							onDragHandler(eventSource.__draggableIndex, eventSource.__draggableNewIndex);\r\n"+
            "						}\r\n"+
            "						\r\n"+
            "	    			}\r\n"+
            "	\r\n"+
            "	    		}\r\n"+
            "    		}\r\n"+
            "\r\n"+
            "			//err(\"ci \" + currentIndex + \" ti \" + targetIndex);\r\n"+
            "\r\n"+
            "	    }\r\n"+
            "    	else{\r\n"+
            "    		//err(\"invalid\");\r\n"+
            "    	}\r\n"+
            "\r\n"+
            "   		// Handle Mouse Position \r\n"+
            "   		if(mousePositionHandler != null){\r\n"+
            "			var contRect = containerElement.__rect;\r\n"+
            "			var moved = false;\r\n"+
            "   			if(moveX)\r\n"+
            "   				moved = mousePositionHandler(event.x, contRect.x, containerElement.__initialMouseOffsetX, eventSource.__rect.width, moveX, moveY);\r\n"+
            "   			if(moveY)\r\n"+
            "   				moved = mousePositionHandler(event.y, contRect.y, containerElement.__initialMouseOffsetY, eventSource.__rect.width, moveX, moveY);\r\n"+
            "   			if(moved){\r\n"+
            "				for(var changedIndex of containerElement.__changedIndexes){\r\n"+
            "					var draggableElement = containerElement.__pushDraggableElements[changedIndex];\r\n"+
            "//					saveRect(draggableElement)\r\n"+
            "//					updateRect(draggableElement);\r\n"+
            "//					repaintRect(draggableElement);\r\n"+
            "				}\r\n"+
            "				\r\n"+
            "   			}\r\n"+
            "   		}\r\n"+
            "\r\n"+
            "    };\r\n"+
            "	containerElement.addEventListener(\"dragover\", dragOver); \r\n"+
            "    \r\n"+
            "    /*\r\n"+
            "    var onDrop = function(event){\r\n"+
            "    	//err(\"drop\");\r\n"+
            "    };\r\n"+
            "	containerElement.addEventListener(\"drop\", onDrop); \r\n"+
            "    */\r\n"+
            "};\r\n"+
            "\r\n"+
            "function makeChildrenPushDraggable(boundsElement, listDraggableElements, moveX, moveY, onDragHandler, onDragEndHandler, mousePositionHandler, animated){\r\n"+
            "	var containerElement = boundsElement;\r\n"+
            "\r\n"+
            "	var children = listDraggableElements;\r\n"+
            "	for(var i = 0; i < children.length; i++){\r\n"+
            "		var child = children[i];\r\n"+
            "		\r\n"+
            "		makePushDraggable(containerElement, child, child, moveX, moveY, onDragHandler, onDragEndHandler, mousePositionHandler, animated);\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "// Either Row or Column, set moveX or moveY to true but not both\r\n"+
            "function makePushDraggable(boundsElement, initiatorElement, triggerElement, moveX, moveY, onDragHandler, onDragEndHandler, mousePositionHandler, animated){ \r\n"+
            "	if(!moveX && !moveY) \r\n"+
            "		return; \r\n"+
            "	if(boundsElement == null) \r\n"+
            "		return; \r\n"+
            "	if(triggerElement == null)\r\n"+
            "	    triggerElement = initiatorElement;\r\n"+
            "\r\n"+
            "	var containerElement = boundsElement;\r\n"+
            "	makePushDraggableContainer(containerElement, moveX, moveY, onDragHandler, mousePositionHandler);\r\n"+
            "\r\n"+
            "    var element = initiatorElement;\r\n"+
            "\r\n"+
            "	//Give it an index;\r\n"+
            "	var nextIndex = containerElement.__pushDraggableElements.length;\r\n"+
            "	containerElement.__pushDraggableElements[nextIndex] = element;\r\n"+
            "	containerElement.__pushDraggableElementsNew[nextIndex] = element;\r\n"+
            "	element.__draggableContainer = containerElement;\r\n"+
            "	element.__draggableIndex = nextIndex;\r\n"+
            "	element.__draggableNewIndex = nextIndex;\r\n"+
            "	element.__rect = element.getBoundingClientRect();\r\n"+
            "	if(animated == true)\r\n"+
            "		element.classList.add(\"ami_draggable_element\");\r\n"+
            "\r\n"+
            "    element.isDraggable=true;\r\n"+
            "    triggerElement.draggable=\"true\";\r\n"+
            "\r\n"+
            "    \r\n"+
            "    //Reference\r\n"+
            "    /*\r\n"+
            "	var savRect = function(element){\r\n"+
            "		element.__rect = element.getBoundingClientRect(); \r\n"+
            "	}\r\n"+
            "	var updRect = function(element){\r\n"+
            "		if(element.__draggableContainer != null){\r\n"+
            "			if(element.__draggableContainer.__moveX && element.__offsetX == null)\r\n"+
            "				element.__offsetX = element.__rect.x - element.__draggableContainer.__rect.x;\r\n"+
            "			if(element.__draggableContainer.__moveY && element.__offsetY == null)\r\n"+
            "				element.__offsetY = element.__rect.y - element.__draggableContainer.__rect.y;\r\n"+
            "		}\r\n"+
            "		\r\n"+
            "\r\n"+
            "	};\r\n"+
            "	*/\r\n"+
            "\r\n"+
            "    var dragStart = function(event){\r\n"+
            "    	//err(\"dragStart\");\r\n"+
            "		containerElement.__initiatorElement = containerElement.__pushDraggableElements[element.__draggableIndex];\r\n"+
            "		if(containerElement.__initiatorElement == null)\r\n"+
            "            return false; // allow other listeners to handle event\r\n"+
            "		containerElement.__lastTargetIndex = element.__draggableIndex;\r\n"+
            "		containerElement.__changedIndexes = new Set();\r\n"+
            "		containerElement.__initialMouseOffsetX = event.offsetX;\r\n"+
            "		containerElement.__initialMouseOffsetY = event.offsetY;\r\n"+
            "\r\n"+
            "\r\n"+
            "		// SaveRect + UpdateRect onDragStart\r\n"+
            "		containerElement.__rect = containerElement.getBoundingClientRect();\r\n"+
            "		for(var elementi of containerElement.__pushDraggableElements){\r\n"+
            "			elementi.__rect = elementi.getBoundingClientRect(); \r\n"+
            "			elementi.__offsetX = elementi.__rect.x - containerElement.__rect.x;\r\n"+
            "			elementi.__offsetY = elementi.__rect.y - containerElement.__rect.y;\r\n"+
            "		}\r\n"+
            "		//element.dragTarget=movingElement;\r\n"+
            "    	//element.draggedElement=initiatorElement;\r\n"+
            "    };\r\n"+
            "    var dragEnd = function(event){\r\n"+
            "    	//err(\"dragEnd\");\r\n"+
            "    	if(containerElement.__initiatorElement == null)\r\n"+
            "            return false; // allow other listeners to handle event\r\n"+
            "    	var dropSuccess = true;\r\n"+
            "    	var oldIndex = element.__draggableIndex;\r\n"+
            "    	var newIndex = element.__draggableNewIndex;\r\n"+
            "    	if(event.dataTransfer.dropEffect == \"none\"){\r\n"+
            "    		dropSuccess = false;\r\n"+
            "    	}\r\n"+
            "    	\r\n"+
            "	   	//containerElement.__initiatorElement.style.opacity=\"100%\";\r\n"+
            "");
          out.print(
            "   		containerElement.__isDraggedVisible=true;\r\n"+
            "\r\n"+
            "   		// Copy the rect\r\n"+
            "   		/*\r\n"+
            "		for(var changedIndex of containerElement.__changedIndexes){\r\n"+
            "			var draggableElement = containerElement.__pushDraggableElements[changedIndex];\r\n"+
            "			draggableElement.__oldRect = draggableElement.__rect;\r\n"+
            "		}\r\n"+
            "		*/\r\n"+
            "		for(var changedIndex of containerElement.__changedIndexes){\r\n"+
            "			var draggableElement = containerElement.__pushDraggableElements[changedIndex];\r\n"+
            "			\r\n"+
            "			//var newIndexOldRect = containerElement.__pushDraggableElements[draggableElement.__draggableNewIndex].__oldRect;\r\n"+
            "			draggableElement.__draggableIndex = draggableElement.__draggableNewIndex;\r\n"+
            "			//draggableElement.__rect = draggableElement.__oldRect;\r\n"+
            "		}\r\n"+
            "		/*\r\n"+
            "		for(var changedIndex of containerElement.__changedIndexes){\r\n"+
            "			var draggableElement = containerElement.__pushDraggableElements[changedIndex];\r\n"+
            "			draggableElement.__oldRect = null;\r\n"+
            "		}\r\n"+
            "		*/\r\n"+
            "\r\n"+
            "    	containerElement.__initiatorElement.__rect = null;\r\n"+
            "		containerElement.__initiatorElement = null;\r\n"+
            "		containerElement.__lastTargetIndex = null;\r\n"+
            "		containerElement.__rect = null;\r\n"+
            "		containerElement.__changedIndexes = null;\r\n"+
            "		containerElement.__initialMouseOffsetX = null;\r\n"+
            "		containerElement.__initialMouseOffsetY = null;\r\n"+
            "		\r\n"+
            "		//Call onDragEnd Callback if provided\r\n"+
            "		if(onDragEndHandler!=null){\r\n"+
            "			onDragEndHandler(oldIndex, newIndex, dropSuccess);\r\n"+
            "		}\r\n"+
            "		\r\n"+
            "\r\n"+
            "    };\r\n"+
            "\r\n"+
            "\r\n"+
            "    triggerElement.addEventListener(\"dragstart\", dragStart);\r\n"+
            "    triggerElement.addEventListener(\"dragend\", dragEnd);\r\n"+
            "\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "function makeDraggable2(boundsElement, movingElement, initiatorElement, moveX, moveY){\r\n"+
            "    /* boundsElement - where the movingElement can move around in, if not set it should be the document\r\n"+
            "     * movingElement - the element that is moving, if not set, it is the initiatorElement\r\n"+
            "     * initiatorElement - the element that initiates it\r\n"+
            "     * moveX - if it moves in the x direction \r\n"+
            "     * moveY - if it moves in the y direction \r\n"+
            "     */ \r\n"+
            "      if(!moveX && !moveY)\r\n"+
            "          return;\r\n"+
            "      if(boundsElement == null)\r\n"+
            "          boundsElement = getWindow(initiatorElement).document;\r\n"+
            "      if(movingElement == null)\r\n"+
            "          movingElement = initiatorElement;\r\n"+
            "      \r\n"+
            "    //Save properties for the listeners to use for later\r\n"+
            "      var element = initiatorElement;\r\n"+
            "    element.isDraggable=true;\r\n"+
            "    element.dragTarget=movingElement;\r\n"+
            "    element.draggedElement=initiatorElement;\r\n"+
            "    element.noDragX=!moveX;\r\n"+
            "    element.noDragY=!moveY;\r\n"+
            "    element.draggable=\"true\";\r\n"+
            "    \r\n"+
            "    //If dropEvent needs to be fired dropable must be set to true\r\n"+
            "    var dragOverFunc = function(event,dragged){\r\n"+
            "       // prevent default to allow drop\r\n"+
            "//       if(false && dropable)\r\n"+
            "//           err(\"ddrag\");\r\n"+
            "           event.preventDefault(); \r\n"+
            "           event.stopPropagation();\r\n"+
            "        var e2 = event;\r\n"+
            "        var point=getMousePoint(e2);\r\n"+
            "        var dragStartMouse = dragged.dragStartMouse;\r\n"+
            "        var dragStart = dragged.dragStart;\r\n"+
            "        var dragElement = dragged;\r\n"+
            "      var diffx=point.x-dragStartMouse.x;\r\n"+
            "      var diffy=point.y-dragStartMouse.y;\r\n"+
            "      if(dragStart){\r\n"+
            "        var rect=dragStart.clone().move(diffx,diffy);\r\n"+
            "        if(dragElement.clipDragging)\r\n"+
            "        	dragElement.clipDragging(dragElement,rect);\r\n"+
            "        if(!dragElement.noDragX)\r\n"+
            "          dragElement.dragTarget.style.left=toPx(rect.left);\r\n"+
            "        if(!dragElement.noDragY)\r\n"+
            "          dragElement.dragTarget.style.top=toPx(rect.top);\r\n"+
            "      }\r\n"+
            "      if(dragElement.ondragging)\r\n"+
            "        dragElement.ondragging(dragElement,diffx,diffy,e2);\r\n"+
            "//      else if(target && target.ondragging)\r\n"+
            "//        target.ondragging(dragElement,diffx,diffy,e2);\r\n"+
            "//      var doc=getWindow(element).document;\r\n"+
            "//      doc.body.focus(); // do we need to do this?    \r\n"+
            "    };\r\n"+
            "    var dragEnterFunc = function(event,dragElement){\r\n"+
            "    };\r\n"+
            "    var dragLeaveFunc = function(event,dragElement){\r\n"+
            "    };\r\n"+
            "    var dragDropFunc = function(event,dragElement){\r\n"+
            "      // prevent default action (open as link for some elements)\r\n"+
            "      event.preventDefault();\r\n"+
            "    };\r\n"+
            "\r\n"+
            "    var draggedElement = initiatorElement;\r\n"+
            "    var dragTarget = movingElement;\r\n"+
            "    var dragList = \"drag\";\r\n"+
            "    element.onmousedown=function(e){\r\n"+
            "//		  e.preventDefault();\r\n"+
            "        e.stopPropagation();\r\n"+
            "    };\r\n"+
            "    element.ondragstart=function(e){\r\n"+
            "    	e.dataTransfer.setData('text/plain',null);\r\n"+
            "        e.stopPropagation();\r\n"+
            "      if(element.onpredragstart)\r\n"+
            "    	  element.onpredragstart(e); \r\n"+
            "      \r\n"+
            "//        err(\"start\");\r\n"+
            "//      var doc=getWindow(element).document;\r\n"+
            "//      doc.body.focus(); // do we need to do this?\r\n"+
            "      \r\n"+
            "      var dragStart;\r\n"+
            "          //If there is a dragTarget get the start position of the target\r\n"+
            "      if(draggedElement){\r\n"+
            "          dragStart=new Rect().readFromElementRelatedToParent(dragTarget);\r\n"+
            "      }else\r\n"+
            "          dragStart=null;\r\n"+
            "      \r\n"+
            "      dragStartMouse=getMousePoint(e);\r\n"+
            "\r\n"+
            "      draggedElement.dragStart= dragStart;\r\n"+
            "      draggedElement.dragStartMouse= dragStartMouse;\r\n"+
            "\r\n"+
            "      draggedElement.addEventListener(dragList, function(event){dragOverFunc(event,draggedElement);}, false);\r\n"+
            "      \r\n"+
            "        \r\n"+
            "    };\r\n"+
            "    element.ondragend=function(e){\r\n"+
            "        draggedElement.removeEventListener(dragList, function(event){dragOverFunc(event,draggedElement);}, false);\r\n"+
            "        if(element.onpostdragend)\r\n"+
            "    	  element.onpostdragend(e); \r\n"+
            "    };\r\n"+
            "  }\r\n"+
            "function removeDraggable(element,target,noDragX,noDragY){\r\n"+
            "  delete element.isDraggable;\r\n"+
            "  delete element.dragTarget;\r\n"+
            "  delete element.noDragX;\r\n"+
            "  delete element.noDragY;\r\n"+
            "  delete element.ondragstart;\r\n"+
            "  delete element.onselectstart;\r\n"+
            "  delete element.onmousedown;\r\n"+
            "}\r\n"+
            "function makeDraggable(element,target,noDragX,noDragY){\r\n"+
            " \r\n"+
            "  //for(var i=0;i<draggableElements.length;i++)\r\n"+
            "	  //if(!document.contains(draggableElements[i]))\r\n"+
            "		  //draggableElements.splice(i--,1);\r\n"+
            "  //draggableElements[draggableElements.length]=element;\r\n"+
            "  \r\n"+
            "  /* element - the element in which the event listener sits on\r\n"+
            "   * target - the element that is moving\r\n"+
            "   * noDragX - if it moves in the x direction (negated)\r\n"+
            "   * noDragY - if it moves in the y direction (negated)\r\n"+
            "   */ \r\n"+
            "	\r\n"+
            "  //Save properties for the listeners to use for later\r\n"+
            "  element.isDraggable=true;\r\n"+
            "  element.dragTarget=target;\r\n"+
            "  element.noDragX=noDragX;\r\n"+
            "  element.noDragY=noDragY;\r\n"+
            "  \r\n"+
            "  //\r\n"+
            "  element.ondragstart=function(){return false;};\r\n"+
            "  element.onselectstart=function(){return false;};\r\n"+
            "  element.onmousedown=function(e){\r\n"+
            "    var doc=getWindow(element).document;\r\n"+
            "    stopDefault(e);\r\n"+
            "    // This was added in for touch screens tablets, commenting it out for divider custom menus\r\n"+
            "    if(e.stopPropagation!=null)\r\n"+
            "      e.stopPropagation();\r\n"+
            "    doc.body.focus(); \r\n"+
            "    dragElement=getMouseTarget(e);\r\n"+
            "    if(dragElement.ondraggingStart)\r\n"+
            "      if(false==dragElement.ondraggingStart(dragElement,e))\r\n"+
            "    	  return;\r\n"+
            "    if(dragElement.dragTarget){\r\n"+
            "      dragStart=new Rect().readFromElementRelatedToParent(dragElement.dragTarget);\r\n"+
            "    }else\r\n"+
            "      dragStart=null;\r\n"+
            "    dragStartMouse=getMousePoint(e);\r\n"+
            "    doc.onmousemove=function(e2){\r\n"+
            "      stopDefault(e2);\r\n"+
            "    	\r\n"+
            "    	\r\n"+
            "      var point=getMousePoint(e2);\r\n"+
            "      var diffx=point.x-dragStartMouse.x;\r\n"+
            "      var diffy=point.y-dragStartMouse.y;\r\n"+
            "      if(dragStart){\r\n"+
            "        var rect=dragStart.clone().move(diffx,diffy);\r\n"+
            "        if(dragElement.clipDragging)\r\n"+
            "        	dragElement.clipDragging(dragElement,rect);\r\n"+
            "        if(!dragElement.noDragX)\r\n"+
            "          dragElement.dragTarget.style.left=toPx(rect.left);\r\n"+
            "        if(!dragElement.noDragY)\r\n"+
            "          dragElement.dragTarget.style.top=toPx(rect.top);\r\n"+
            "      }\r\n"+
            "      if(dragElement.ondragging)\r\n"+
            "        dragElement.ondragging(dragElement,diffx,diffy,e2);\r\n"+
            "      else if(target && target.ondragging)\r\n"+
            "        target.ondragging(dragElement,diffx,diffy,e2);\r\n"+
            "      doc.body.focus(); \r\n"+
            "      return true;\r\n"+
            "    };\r\n"+
            "    doc.onmouseup=function(e2){\r\n"+
            "      var point=getMousePoint(e2);\r\n"+
            "      var diffx=point.x-dragStartMouse.x;\r\n"+
            "      var diffy=point.y-dragStartMouse.y;\r\n"+
            "      if(dragElement.ondraggingEnd)\r\n"+
            "        dragElement.ondraggingEnd(dragElement,diffx,diffy,e2);\r\n"+
            "      else if(target && target.ondraggingEnd)\r\n"+
            "        target.ondraggingEnd(target,diffx,diffy,e2);\r\n"+
            "      doc.onmousemove=null;\r\n"+
            "      doc.onmouseup=null;\r\n"+
            "      doc.ontouchmove=null;\r\n"+
            "      doc.ontouchend=null;\r\n"+
            "      doc.body.focus(); \r\n"+
            "      return true;\r\n"+
            "    };\r\n"+
            "    doc.ontouchmove=doc.onmousemove;\r\n"+
            "    doc.ontouchend=doc.onmouseup;\r\n"+
            "    doc.body.focus();\r\n"+
            "  };\r\n"+
            "  //element.ontouchstart=element.onmousedown;\r\n"+
            "};\r\n"+
            "\r\n"+
            "            	\r\n"+
            "var SWIPE_START_X;\r\n"+
            "var SWIPE_START_Y;\r\n"+
            "var SWIPE_TARGET;\r\n"+
            "var PADDING_FOR_TOUCH=20;\r\n"+
            "document.ontouchstart=function(event){\r\n"+
            "    var point=getMousePoint(event);\r\n"+
            "    var x=point.x;\r\n"+
            "    var y=point.y;\r\n"+
            "    MOUSE_POSITION_X = x;\r\n"+
            "    MOUSE_POSITION_Y = y;\r\n"+
            "	var element=document.elementFromPoint(x,y);\r\n"+
            "    for(var i=element;i!=null;i=i.parentNode){\r\n"+
            "    	if(i.onSwipe){\r\n"+
            "    		SWIPE_START_X=x;\r\n"+
            "    		SWIPE_START_Y=y;\r\n"+
            "    		SWIPE_TARGET=i;\r\n"+
            "            document.ontouchmove=function(event2){\r\n"+
            "              var point=getMousePoint(event2);\r\n"+
            "              SWIPE_TARGET.onSwipe(point.x-SWIPE_START_X,point.y-SWIPE_START_Y);\r\n"+
            "            }\r\n"+
            "            document.ontouchend=function(event2){\r\n"+
            "                var point=getMousePoint(event2);\r\n"+
            "                SWIPE_TARGET.onSwipeDone(point.x-SWIPE_START_X,point.y-SWIPE_START_Y);\r\n"+
            "    		    SWIPE_START_X=null;\r\n"+
            "    		    SWIPE_START_Y=null;\r\n"+
            "    		    SWIPE_TARGET=null;\r\n"+
            "            	document.ontouchmove=null;document.ontouchend=null;\r\n"+
            "            };\r\n"+
            "    		//log('onSwipe!!!');\r\n"+
            "    		return;\r\n"+
            "    	}\r\n"+
            "    }\r\n"+
            "    for(xO=0;xO<PADDING_FOR_TOUCH*2;xO++){\r\n"+
            "      for(yO=0;yO<PADDING_FOR_TOUCH*2;yO++){\r\n"+
            "	    var element=document.elementFromPoint(x+xO/(xO%2==0 ? 2 : -2),y+yO/(yO%2==0 ? 2 : -2));\r\n"+
            "	    if(isInput(element)){\r\n"+
            "	    	element.focus();\r\n"+
            "	    	return;\r\n"+
            "	    }\r\n"+
            "	    if(element.onmousedown==null && element.onclick==null)\r\n"+
            "	    	continue;\r\n"+
            "        stopDefault(event);\r\n"+
            "	    event={pageX:rd(x),pageY:rd(y),target:element,");
          out.print(
            "stopPropagation:function(){},button:1,ctrlKey:event.ctrlKey,shiftKey:event.shiftKey};\r\n"+
            "	    if(element.onmousedown){\r\n"+
            "		  element.onmousedown(event);\r\n"+
            "	    }else if(element.onclick){\r\n"+
            "		  element.onclick(event);\r\n"+
            "	    }\r\n"+
            "		  return;\r\n"+
            "      }\r\n"+
            "	}\r\n"+
            "	\r\n"+
            "}\r\n"+
            "document.ontouchstart2=function(event){\r\n"+
            "    var point=getMousePoint(event);\r\n"+
            "    var x=point.x;\r\n"+
            "    var y=point.y;\r\n"+
            "    var rect=new Rect();\r\n"+
            "	var all = document.getElementsByTagName(\"*\");\r\n"+
            "	var candidates=[];\r\n"+
            "	for (var i=0, max=all.length; i < max; i++) {\r\n"+
            "		var element=all[i];\r\n"+
            "		if(element.onmousedown!=null || element.onclick!=null){\r\n"+
            "           rect.readFromElementRelatedToWindow(element);\r\n"+
            "           if(rect.insidePadded(x,y,PADDING_FOR_TOUCH)){\r\n"+
            "        	   candidates[candidates.length]=element;\r\n"+
            "           }\r\n"+
            "		}\r\n"+
            "	}\r\n"+
            "	if(candidates.length==0)\r\n"+
            "	  return;\r\n"+
            "	for(var i=0;i<candidates.length;i++){\r\n"+
            "	  for(var j=0;j<candidates.length;j++){\r\n"+
            "		 if(i!=j && isChildOf(candidates[i],candidates[j]) || isInfrontOf(candidates[i],candidates[j]) ){\r\n"+
            "			 candidates.splice(i--,1);\r\n"+
            "			 break;\r\n"+
            "		 }\r\n"+
            "	  }\r\n"+
            "	}\r\n"+
            "	\r\n"+
            "	var result;\r\n"+
            "	if(candidates.length==1){\r\n"+
            "	  result=candidates[0];\r\n"+
            "	}else{\r\n"+
            "	  outer:for(var n=0;;n++){\r\n"+
            "	    for(var i=0;i<candidates.length;i++){\r\n"+
            "	      var element=candidates[i];\r\n"+
            "          rect.readFromElementRelatedToWindow(element);\r\n"+
            "          if(rect.insidePadded(x,y,n)){\r\n"+
            "            result=element;\r\n"+
            "            break outer;\r\n"+
            "          }\r\n"+
            "        }\r\n"+
            "	  }\r\n"+
            "	}\r\n"+
            "	event.touchTarget=result;\r\n"+
            "	if(result.onmousedown!=null){\r\n"+
            "	  result.onmousedown(event);\r\n"+
            "      stopDefault(event);\r\n"+
            "    }else{\r\n"+
            "	  result.onclick(event);\r\n"+
            "      stopDefault(event);\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "function isInfrontOf(a,b){\r\n"+
            "	var bz=null;\r\n"+
            "	for(var bb=b;bb!=null;bb=bb.parentNode){\r\n"+
            "		var z=bb.style!=null && bb.style.zIndex;\r\n"+
            "		if(z!=null && (bz==null || z>bz))\r\n"+
            "			bz=z;\r\n"+
            "	}\r\n"+
            "	if(bz==null)\r\n"+
            "	  return false;\r\n"+
            "	var az=null;\r\n"+
            "	for(var aa=a;aa!=null;aa=aa.parentNode){\r\n"+
            "		var z=aa.style!=null && aa.style.zIndex;\r\n"+
            "		if(z!=null && (az==null || z>az))\r\n"+
            "			az=z;\r\n"+
            "	}\r\n"+
            "	if(az==null || az<bz){\r\n"+
            "		return;\r\n"+
            "	}\r\n"+
            "	return false;\r\n"+
            "//    \r\n"+
            "//	\r\n"+
            "//	\r\n"+
            "//	if(az==null)\r\n"+
            "//		return false;\r\n"+
            "//	  for(var bb=b;bb!=null;bb=bb.parentNode)\r\n"+
            "//		  if(aa.parentNode==bb.parentNode)\r\n"+
            "//			return bb.style.zIndex!=null && (aa.style.zIndex==null || aa.style.zIndex*1 < bb.style.zIndex*1);\r\n"+
            "//  return false;\r\n"+
            "}\r\n"+
            "function isChildOf(parent,child){\r\n"+
            "	for(var i=child;i!=null;i=i.parentNode)\r\n"+
            "		if(i==parent)\r\n"+
            "			return true;\r\n"+
            "	return false;\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "\r\n"+
            "\r\n"+
            "function Array2d(width,height){\r\n"+
            "  \r\n"+
            "  this.data=new Array();\r\n"+
            "  this.setSize(width,height);\r\n"+
            "};\r\n"+
            "\r\n"+
            "Array2d.prototype.data;\r\n"+
            "Array2d.prototype.width=0;\r\n"+
            "Array2d.prototype.height=0;\r\n"+
            "\r\n"+
            "Array2d.prototype.setSize = function(width,height){\r\n"+
            "  if(width!=null)\r\n"+
            "    this.setWidth(width);\r\n"+
            "  if(height!=null)\r\n"+
            "    this.setHeight(height);\r\n"+
            "};\r\n"+
            "\r\n"+
            "\r\n"+
            "Array2d.prototype.setWidth = function(width){\r\n"+
            "  if(this.width>width){\r\n"+
            "    this.data.length=width;\r\n"+
            "    this.width=width;\r\n"+
            "  } else if(this.width<width){\r\n"+
            "    this.data.length=width;\r\n"+
            "    for(var i=this.width;i<width;i++)\r\n"+
            "      this.data[i]=new Array();\r\n"+
            "    this.width=width;\r\n"+
            "  }\r\n"+
            "};\r\n"+
            "\r\n"+
            "\r\n"+
            "Array2d.prototype.clear = function(){\r\n"+
            "    for(var i=0;i<this.width;i++){\r\n"+
            "      this.data[i]=[];\r\n"+
            "      this.data[i].length=this.height;\r\n"+
            "    }\r\n"+
            "};\r\n"+
            "\r\n"+
            "Array2d.prototype.setHeight = function(height){\r\n"+
            "  if(height < 0){\r\n"+
            "	  height = 0;\r\n"+
            "  }\r\n"+
            "  if(height!=this.height){\r\n"+
            "    for(var i=0;i<this.width;i++)\r\n"+
            "      this.data[i].length=height;\r\n"+
            "    this.height=height;\r\n"+
            "  }\r\n"+
            "};\r\n"+
            "\r\n"+
            "Array2d.prototype.remove = function(x,y){\r\n"+
            "	delete this.data[x][y];\r\n"+
            "};\r\n"+
            "Array2d.prototype.getWidth = function(){\r\n"+
            "  return this.width;\r\n"+
            "};\r\n"+
            "\r\n"+
            "Array2d.prototype.getHeight = function(){\r\n"+
            "  return this.height;\r\n"+
            "};\r\n"+
            "\r\n"+
            "Array2d.prototype.set = function(x,y,data){\r\n"+
            "  var t=this.data[x];\r\n"+
            "  var r=t[y];\r\n"+
            "  t[y]=data;\r\n"+
            "  return r;\r\n"+
            "};\r\n"+
            "\r\n"+
            "Array2d.prototype.get = function(x,y){\r\n"+
            "  return this.data[x][y];\r\n"+
            "};\r\n"+
            "\r\n"+
            "Array2d.prototype.ensureSize = function(x,y){\r\n"+
            "   if(x+1 > this.width)\r\n"+
            "      this.setWidth(x+1);\r\n"+
            "   if(y+1 > this.height)\r\n"+
            "      this.setHeight(y+1);\r\n"+
            "\r\n"+
            "};\r\n"+
            "\r\n"+
            "var mouseRepeatCount;\r\n"+
            "var mouseRepeatTimeout;\r\n"+
            "var mouseRepeatEvent;\r\n"+
            "var mouseRepeatElement;\r\n"+
            "\r\n"+
            "function makeMouseRepeat(element){\r\n"+
            "  element.onmousedown = function(e){\r\n"+
            "      mouseRepeatElement=getMouseTarget(e);\r\n"+
            "      if(mouseRepeatElement.onMouseRepeat==null)\r\n"+
            "        return;\r\n"+
            "      mousedownCount=0;\r\n"+
            "      mouseRepeatEvent=e;\r\n"+
            "      mouseRepeatCount=0;\r\n"+
            "      if(mouseRepeatTimeout!=null)\r\n"+
            "          window.clearInterval(mouseRepeatTimeout);\r\n"+
            "      fireMouseRepeat(e);\r\n"+
            "      mouseRepeatTimeout = window.setInterval('fireMouseRepeat()', 100);\r\n"+
            "  };\r\n"+
            "  \r\n"+
            "  element.onmouseup = function(){\r\n"+
            "      if(mouseRepeatTimeout!=null)\r\n"+
            "          window.clearInterval(mouseRepeatTimeout);\r\n"+
            "      mouseRepeatCount=0;\r\n"+
            "      mouseRepeatTimeout=null;\r\n"+
            "      mouseRepeatEvent=null;\r\n"+
            "      mouseRepeatElement=null;\r\n"+
            "  };\r\n"+
            "  element.onmouseout=element.onmouseup;\r\n"+
            "  element.ontouchend=element.onmouseup;\r\n"+
            "};\r\n"+
            "\r\n"+
            "function fireMouseRepeat(){\r\n"+
            "    if(mouseRepeatCount==0)\r\n"+
            "        mouseRepeatElement.onMouseRepeat(mouseRepeatEvent,mouseRepeatCount);\r\n"+
            "    else if(mouseRepeatCount > 3)\r\n"+
            "        mouseRepeatElement.onMouseRepeat(mouseRepeatEvent,mouseRepeatCount-3);\r\n"+
            "    mouseRepeatCount++;\r\n"+
            "};\r\n"+
            "\r\n"+
            "function makeEnterable(element,button){\r\n"+
            "	element.onkeypress=function(e){if(e.keyCode==13) button.onclick(e);};\r\n"+
            "};\r\n"+
            "\r\n"+
            "function removeAllChildren(element){\r\n"+
            "	while(element.hasChildNodes())\r\n"+
            "		element.removeChild(element.firstChild);\r\n"+
            "};\r\n"+
            "function makeEditable(element,enable){\r\n"+
            "	if(enable==false)\r\n"+
            "	  element.ondblclick=null;\r\n"+
            "	else\r\n"+
            "	  element.ondblclick=function(e){makeTextInput(getMouseTarget(e));};\r\n"+
            "};\r\n"+
            "\r\n"+
            "function htmlToText(html){\r\n"+
            "	if(html==null)\r\n"+
            "		return \"\";\r\n"+
            "	html=html.replace(/\\&nbsp\\;/ig,\" \");\r\n"+
            "	html=html.replace(/\\&lt\\;/ig,\"<\");\r\n"+
            "	html=html.replace(/\\&gt\\;/ig,\">\");\r\n"+
            "	html=html.replace(/\\&amp\\;/ig,\"&\");\r\n"+
            "	return html;\r\n"+
            "}\r\n"+
            "\r\n"+
            "function makeTextInput(e){\r\n"+
            "	var input=nw(\"input\");\r\n"+
            "	input.origValue=e.innerHTML;\r\n"+
            "	input.value=htmlToText(e.innerHTML);\r\n"+
            "	e.innerHTML=\"\";\r\n"+
            "	e.appendChild(input);\r\n"+
            "	input.focus();\r\n"+
            "	input.select();\r\n"+
            "	input.onblur=function(e){\r\n"+
            "	  var inp=getMouseTarget(e);\r\n"+
            "      inp.onblur=null;\r\n"+
            "      inp.onkeyup=null;\r\n"+
            "      var parent=inp.parentNode;\r\n"+
            "      parent.innerHTML=inp.origValue;\r\n"+
            "      parent.onEdit(inp.origValue,inp.value);\r\n"+
            "	};\r\n"+
            "	input.onkeyup=function(e){\r\n"+
            "	  var inp=getMouseTarget(e);\r\n"+
            "      if(e.keyCode==27){\r\n"+
            "        inp.onblur=null;\r\n"+
            "        inp.onkeyup=null;\r\n"+
            "        inp.parentNode.innerHTML=inp.origValue;\r\n"+
            "      } else if(e.keyCode==13){\r\n"+
            "        inp.onblur=null;\r\n"+
            "        inp.onkeyup=null;\r\n"+
            "        var parent=inp.parentNode;\r\n"+
            "        parent.innerHTML=inp.origValue;\r\n"+
            "        parent.onEdit(inp.origValue,inp.value);\r\n"+
            "      }\r\n"+
            "    };\r\n"+
            "};\r\n"+
            "function makeHelpable(element,help){\r\n"+
            "  if(help==null)\r\n"+
            "	  help='No help available at this time';\r\n"+
            "  if(element.className)\r\n"+
            "  element.className+=' help_label';\r\n"+
            "  else\r\n"+
            "  element.className=' help_label';\r\n"+
            "  element.help=help;\r\n"+
            "  element.onclick=showHelp;\r\n"+
            "};\r\n"+
            "\r\n"+
            "\r\n"+
            "var helpDiv=nw('div','help_popup');\r\n"+
            "{\r\n"+
            "	helpDiv.onmouseout=function(e){document.body.removeChild(getMouseTarget(e));};\r\n"+
            "};\r\n"+
            "\r\n"+
            "function showHelp(e){\r\n"+
            "	var text=getMouseTarget(e).help;\r\n"+
            "	var p=getMousePoint(e);\r\n"+
            "	helpDiv.style.left=toPx(p.getX()-10);\r\n"+
            "	helpDiv.style.top=toPx(p.getY()-50);\r\n"+
            "	helpDiv.innerHTML=text;\r\n"+
            "	document.body.appendChild(helpDiv);\r\n"+
            "};\r\n"+
            "\r\n"+
            "function makeErrorIcon(errors){\r\n"+
            "	  var element = nw('div', 'portlet_field_icon_error');\r\n"+
            "	  if(errors==null)\r\n"+
            "		  element.errors=\"No error\";\r\n"+
            "	  else\r\n"+
            "		  element.errors=errors;\r\n"+
            "	\r\n"+
            "//	  element.onclick=showError;\r\n"+
            "	  element.onmouseover=showError;\r\n"+
            "	  \r\n"+
            "	  return element;\r\n"+
            "};\r\n"+
            "\r\n"+
            "\r\n"+
            "var errorDiv=nw('div','error_popup');\r\n"+
            "{\r\n"+
            "	errorDiv.onmouseout=function(e){document.body.removeChild(getMouseTarget(e));removeAllChildren(errorDiv)};\r\n"+
            "};\r\n"+
            "\r\n"+
            "function showError(e){\r\n"+
            "	var errors=getMouseTarget(e).errors;\r\n"+
            "	var p=getMousePoint(e);\r\n"+
            "	errorDiv.style.left=toPx(p.getX()-10);\r\n"+
            "	errorDiv.style.top=toPx(p.getY()-50);\r\n"+
            "	\r\n"+
            "	var ul = nw(\"ul\", \"ul_disc\");\r\n"+
            "	errorDiv.appendChild(ul);\r\n"+
            "	for( i in errors ){\r\n"+
            "		var l = nw(\"li\", \"ul_disc\");\r\n"+
            "		l.innerHTML=errors[i];\r\n"+
            "		ul.appendChild(l);\r\n"+
            "	}\r\n"+
            "	\r\n"+
            "	document.body.appendChild(errorDiv);\r\n"+
            "};\r\n"+
            "\r\n"+
            "function log(){\r\n"+
            "	var w = getMainWindow();\r\n"+
            "	if(w!=null & w.console!=null && w.console.log !=null)\r\n"+
            "		w.console.log(...arguments);\r\n"+
            "	else if(w!=null)\r\n"+
            "		w.alert([...arguments]);\r\n"+
            "	else\r\n"+
            "		alert([...arguments]);\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "function err(){\r\n"+
            "	var w = getMainWindow();\r\n"+
            "	if(w!=null & w.console!=null && w.console.error !=null)\r\n"+
            "		w.console.error(...arguments);\r\n"+
            "	else if(w!=null)\r\n"+
            "		w.alert([...arguments]);\r\n"+
            "	else\r\n"+
            "		alert([...arguments]);\r\n"+
            "}\r\n"+
            "\r\n"+
            "function nwDiv(className,left,top,width,height,innerHTML){ \r\n"+
            "	  var r=nw('div',className); \r\n"+
            "	  r.style.left=toPx(left); \r\n"+
            "	  r.style.top=toPx(top); \r\n"+
            "	  r.style.width=toPx(width); \r\n"+
            "	  r.style.height=toPx(height); \r\n"+
            "	  if(innerHTML!=null)\r\n"+
            "		  r.innerHTML=innerHTML;\r\n"+
            "	  return r; \r\n"+
            "} \r\n"+
            "\r\n"+
            "\r\n"+
            "function fl(x){\r\n"+
            "	  return Math.floor(x);\r\n"+
            "}\r\n"+
            "function rd(x){\r\n"+
            "	if(x==null) return null;\r\n"+
            "	return Math.floor(x+.5);\r\n"+
            "}\r\n"+
            "\r\n"+
            "function cl(x){\r\n"+
            "    return Math.ceil(x);\r\n"+
            "}\r\n"+
            "\r\n"+
            "function sq(x){\r\n"+
            "    return x*x;\r\n"+
            "}\r\n"+
            "\r\n"+
            "function ColorGradient(){\r\n"+
            "	this.clear();\r\n"+
            "}\r\n"+
            "\r\n"+
            "ColorGradient.prototype.clear = function(value,color){\r\n"+
            "	this.colors=[];\r\n"+
            "	this.colorsSorted=[];\r\n"+
            "}\r\n"+
            "\r\n"+
            "ColorGradient.prototype.colorsSorted = function(){\r\n"+
            "	return this.colorsSorted;\r\n"+
            "}\r\n"+
            "\r\n"+
            "ColorGradient.prototype.clone = function(){\r\n"+
            "	var r=new ColorGradient();\r\n"+
            "	  for(var i in this.colors)\r\n"+
            "	    r.addStepRgb(+i,this.colors[i][0],this.colors[i][1],this.colors[i][2],this.colors[i][3]);\r\n"+
            "	return r;\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "ColorGradient.prototype.getStepForValue = function(value){\r\n"+
            "	for(var i=0;i<this.colorsSorted.length;i++){\r\n"+
            "		if(this.colorsSorted[i][0]==value)\r\n"+
            "			return i;\r\n"+
            "	}\r\n"+
            "	return -1;\r\n"+
            "}\r\n"+
            "ColorGradient.prototype.removeStepByValue=function(value){\r\n"+
            "	delete this.colors[+value];\r\n"+
            "	this.buildSortedList();\r\n"+
            "}\r\n"+
            "\r\n"+
            "ColorGradient.prototype.addStepRgb = function(value,r,g,b,a){\r\n"+
            "	if(a==null || a==undefined)\r\n"+
            "		a=255;\r\n"+
            "	this.colors[+value]=[r,g,b,a];\r\n"+
            "	");
          out.print(
            "this.buildSortedList();\r\n"+
            "}\r\n"+
            "ColorGradient.prototype.addStep = function(value,color){\r\n"+
            "	this.colors[+value]=parseColor(color);\r\n"+
            "	this.buildSortedList();\r\n"+
            "}\r\n"+
            "ColorGradient.prototype.buildSortedList = function(){\r\n"+
            "	this.colorsSorted=[];\r\n"+
            "	var j=0;\r\n"+
            "	for(var i in this.colors){\r\n"+
            "		var rgb=this.colors[i];\r\n"+
            "		this.colorsSorted[j++]=[+i,rgb[0],rgb[1],rgb[2],rgb[3],toColor(rgb[0],rgb[1],rgb[2],rgb[3])];\r\n"+
            "	}\r\n"+
            "	this.colorsSorted.sort(function(a,b){return a[0]-b[0];});\r\n"+
            "}\r\n"+
            "\r\n"+
            "ColorGradient.prototype.length = function(){\r\n"+
            "	return this.colorsSorted.length;\r\n"+
            "}\r\n"+
            "ColorGradient.prototype.getColorAtStep = function(i){\r\n"+
            "	  return this.colorsSorted[+i][5];\r\n"+
            "}\r\n"+
            "ColorGradient.prototype.getValueAtStep = function(i){\r\n"+
            "	return this.colorsSorted[+i][0];\r\n"+
            "}\r\n"+
            "ColorGradient.prototype.getColorsSorted = function(){\r\n"+
            "	return this.colorsSorted;\r\n"+
            "}\r\n"+
            "ColorGradient.prototype.getMinValue = function(i){\r\n"+
            "	return this.colorsSorted[0][0];\r\n"+
            "}\r\n"+
            "ColorGradient.prototype.getMaxValue = function(i){\r\n"+
            "	return this.colorsSorted[this.colorsSorted.length-1][0];\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "ColorGradient.prototype.toString = function(){\r\n"+
            "    var r=\"\";\r\n"+
            "	for(var i=0;i<this.colorsSorted.length;i++){\r\n"+
            "	    if(i>0)\r\n"+
            "	      r+=\",\";\r\n"+
            "		r+=this.colorsSorted[i][0]+\":\"+this.colorsSorted[i][5];\r\n"+
            "	}\r\n"+
            "	return r;\r\n"+
            "}\r\n"+
            "\r\n"+
            "ColorGradient.prototype.parseString = function(s){\r\n"+
            "    this.clear();\r\n"+
            "    var parts=s.split(',');\r\n"+
            "    for(var i=0;i<parts.length;i++){\r\n"+
            "      var parts2=parts[i].split(':');\r\n"+
            "      var value=parts2[0];\r\n"+
            "      var color=parts2[1];\r\n"+
            "	  this.colors[+value]=parseColor(color);\r\n"+
            "    }\r\n"+
            "    this.buildSortedList();\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "ColorGradient.prototype.toColor = function(val){\r\n"+
            "	var len=this.colorsSorted.length;\r\n"+
            "	var rgb1,rgb2;\r\n"+
            "	switch(len){\r\n"+
            "	   case 0:\r\n"+
            "	  return [0,0,0,255];\r\n"+
            "	   case 1:\r\n"+
            "	  var rgb=this.colorsSorted[0];\r\n"+
            "	  return [rgb[1],rgb[2],rgb[3],rgb[4]];\r\n"+
            "	   case 2:\r\n"+
            "	  rgb1=this.colorsSorted[0];\r\n"+
            "	  rgb2=this.colorsSorted[1];\r\n"+
            "	  break;\r\n"+
            "	   default:\r\n"+
            "	  for(var i=0;;i++){\r\n"+
            "		  if(i==len){\r\n"+
            "	          var rgb=this.colorsSorted[len-1];\r\n"+
            "	          return [rgb[1],rgb[2],rgb[3],rgb[4]];\r\n"+
            "		  }\r\n"+
            "		  if(val <= this.colorsSorted[i][0]){\r\n"+
            "			  rgb1=this.colorsSorted[i==0 ? i : i-1];\r\n"+
            "			  rgb2=this.colorsSorted[i];\r\n"+
            "			  break;\r\n"+
            "		  }\r\n"+
            "	  }\r\n"+
            "	 }\r\n"+
            "	 if(val<=rgb1[0])\r\n"+
            "	  return [rgb1[1],rgb1[2],rgb1[3],rgb1[4]];\r\n"+
            "	 else if(val>=rgb2[0])\r\n"+
            "	  return [rgb2[1],rgb2[2],rgb2[3],rgb2[4]];\r\n"+
            "	 var pct=(val-rgb1[0]) / (rgb2[0]-rgb1[0]);\r\n"+
            "	 return [\r\n"+
            "		   (rgb2[1]-rgb1[1])*pct+rgb1[1],\r\n"+
            "		   (rgb2[2]-rgb1[2])*pct+rgb1[2],\r\n"+
            "		   (rgb2[3]-rgb1[3])*pct+rgb1[3],\r\n"+
            "		   (rgb2[4]-rgb1[4])*pct+rgb1[4]\r\n"+
            "		   ];\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "\r\n"+
            "function parseColor(color){\r\n"+
            "	if(color.charAt(0)=='#'){\r\n"+
            "	  color=color.substring(1);\r\n"+
            "	  if (color.length == 3) { // Support shorthand hexadecimal form\r\n"+
            "		  first = color.charAt(0);\r\n"+
            "		  second = color.charAt(1);\r\n"+
            "		  third = color.charAt(2);\r\n"+
            "		  color = first + first + second + second + third + third\r\n"+
            "	  }\r\n"+
            "	}else {\r\n"+
            "		var c=COLORS[color.toLowerCase()];\r\n"+
            "		if(c)\r\n"+
            "		  color=c.substring(1);\r\n"+
            "	}\r\n"+
            "	if(color.length==8){//includes alpha\r\n"+
            "	  var val=parseInt(color.substring(0,6),16);\r\n"+
            "	  var opc=parseInt(color.substring(6,8),16);\r\n"+
            "      return [ (val & 0xff0000)>>16 , (val &0x00ff00)>>8, val & 0x0000ff, opc & 0x0000ff];\r\n"+
            "	}else{\r\n"+
            "	  var val=parseInt(color,16);\r\n"+
            "	  return [ (val & 0xff0000)>>16 , (val &0x00ff00)>>8, val & 0x0000ff,255];\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "function parseColorAlpha(color){\r\n"+
            "	if(color.charAt(0)=='#'){\r\n"+
            "	  color=color.substring(1);\r\n"+
            "	}else {\r\n"+
            "		var c=COLORS[color.toLowerCase()];\r\n"+
            "		if(c)\r\n"+
            "		  color=c.substring(1)+\"FF\";\r\n"+
            "	}\r\n"+
            "	//ensure color is in RGBA Format\r\n"+
            "	if(color.length == 6)\r\n"+
            "		color += \"FF\";\r\n"+
            "	var val=parseInt(color,16); \r\n"+
            "	var t=(val & 0xff000000)>>24;\r\n"+
            "	if(t<0)\r\n"+
            "		t=255+t;\r\n"+
            "	return [ t , (val &0x00ff0000)>>16, (val & 0x0000ff00)>>8,(val & 0x000000ff) ];\r\n"+
            "}\r\n"+
            "\r\n"+
            "function toColor(r,g,b,a){\r\n"+
            "  var rt='#';\r\n"+
            "  rt+=toColorPart(r);\r\n"+
            "  rt+=toColorPart(g);\r\n"+
            "  rt+=toColorPart(b);\r\n"+
            "  if(a!=undefined && a!=null && a<255)\r\n"+
            "    rt+=toColorPart(a);\r\n"+
            "  return rt;\r\n"+
            "}\r\n"+
            "function toColorPart(r){\r\n"+
            "  if(r<1)return '00'; else if(r>254)return 'ff';else if(r<16) return '0'+fl(r).toString(16); else return ''+fl(r).toString(16);\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "TENS=[1,10,100,1000,10000,100000, 1000000, 10000000, 100000000, 1000000000, 10000000000];\r\n"+
            "function roundDecimals(x,precision){\r\n"+
            "	return fl(x*TENS[precision])/TENS[precision];\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "function noNaN(val,dflt){\r\n"+
            "	return isNaN(val) ? dflt: val;\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "function colourNameToHex(colour)\r\n"+
            "{\r\n"+
            "    if (typeof colours[colour.toLowerCase()] != 'undefined')\r\n"+
            "    	return colours[colour.toLowerCase()];\r\n"+
            "\r\n"+
            "    return false;\r\n"+
            "}\r\n"+
            "\r\n"+
            "function resetAppliedStyles(target){\r\n"+
            "	var a=target.appliedStyles;\r\n"+
            "	if(a==null)\r\n"+
            "		return;\r\n"+
            "	for(var i in a){\r\n"+
            "		target.style[i]='';\r\n"+
            "	}\r\n"+
            "	target.appliedStyles={};\r\n"+
            "}\r\n"+
            "\r\n"+
            "/**\r\n"+
            " * val is pipe delimited list of:\r\n"+
            " * _h = height\r\n"+
            " * _w = width\r\n"+
            " * _fg = foreground color\r\n"+
            " * _bgi = background image\r\n"+
            " * _bg = background color\r\n"+
            " * _cna = class name append\r\n"+
            " * _cn = class name \r\n"+
            " * _fs = font size\r\n"+
            " * _fm = format which is comma delimited list of: strike,italic,bold,hide,center,left,right\r\n"+
            " * style.* = apply style\r\n"+
            " */\r\n"+
            "function applyStyle(target,val){\r\n"+
            "	if(target.appliedStyles==null)\r\n"+
            "	  target.appliedStyles={};\r\n"+
            "	var entries=(val==null) ? [] : val.split('\\|');\r\n"+
            "	for(var i=0;i<entries.length;i++){\r\n"+
            "		var keyValue=entries[i].split(/=(.*)?/);\r\n"+
            "		var key=keyValue[0];\r\n"+
            "		var value=keyValue[1];\r\n"+
            "		if (value === \"\") \r\n"+
            "			continue;\r\n"+
            "		if(key.charAt(0)=='_'){\r\n"+
            "			if(key==\"_fg\")\r\n"+
            "			  target.style.color=value;\r\n"+
            "			else if(key==\"_h\")\r\n"+
            "			  target.style.height=value;\r\n"+
            "			else if(key==\"_w\")\r\n"+
            "			  target.style.width=value;\r\n"+
            "			else if(key==\"_br\")\r\n"+
            "			  target.style.border=value;\r\n"+
            "			else if(key==\"_bgi\")\r\n"+
            "			  target.style.backgroundImage=value;\r\n"+
            "			else if(key==\"_bg\")\r\n"+
            "			  target.style.backgroundColor=value;\r\n"+
            "			else if(key==\"_cna\"){\r\n"+
            "			  target.classList.add(value);\r\n"+
            "			  target.cellClassName=value;\r\n"+
            "			}else if(key==\"_cnr\"){\r\n"+
            "			  target.classList.remove(value);\r\n"+
            "			}else if(key==\"_cn\"){\r\n"+
            "			  target.className=value;\r\n"+
            "			  target.cellClassName=value;\r\n"+
            "			}else if(key==\"_fs\"){\r\n"+
            "			  target.style.fontSize=toPx(value);\r\n"+
            "			}else if(key==\"_fm\"){\r\n"+
            "			   var formats = [];\r\n"+
            "			   if(value != null)\r\n"+
            "				   formats=value.split(',');\r\n"+
            "	           var underline=false,strike=false,italic=false,bold=false,font=\"\",align=\"\",hide=false;\r\n"+
            "	           var textTransform=null;\r\n"+
            "	           var blink=false;\r\n"+
            "	           var alignItems = null;\r\n"+
            "	           for(var j=0;j<formats.length;j++){\r\n"+
            "	        	   var fmt=formats[j];\r\n"+
            "	        	   switch(fmt.length){\r\n"+
            "	        	      case 4:\r\n"+
            "	        	        if(fmt=='bold') {bold=true;continue;}\r\n"+
            "	        	        if(fmt=='hide') {hide=true;continue;}\r\n"+
            "	        	        if(fmt=='left') {align='left'; alignItems=\"flex-start\";continue;}\r\n"+
            "	        	        break;\r\n"+
            "	        	      case 5:\r\n"+
            "	        	        if(fmt=='blink') {blink=true;continue;}\r\n"+
            "	        	        if(fmt=='right')  {align='right'; alignItems=\"flex-end\";continue;}\r\n"+
            "	        	        break;\r\n"+
            "	        	      case 6:\r\n"+
            "	        	        if(fmt=='strike') {strike=true;continue;}\r\n"+
            "	        	        if(fmt=='italic') {italic=true;continue;}\r\n"+
            "	        	        if(fmt=='center') {align='center'; alignItems=\"center\";continue;}\r\n"+
            "	        	        break;\r\n"+
            "	        	      case 9:\r\n"+
            "	        		    if(fmt==\"uppercase\"){ textTransform=\"uppercase\";continue;}\r\n"+
            "	        		    if(fmt==\"lowercase\"){ textTransform=\"lowercase\";continue;}\r\n"+
            "	        	        if(fmt=='underline'){ underline=true;continue;}\r\n"+
            "	        		    break;\r\n"+
            "	        	      case 10:\r\n"+
            "	        		    if(fmt==\"capitalize\"){ textTransform=\"capitalize\";continue;}\r\n"+
            "	        		    if(fmt==\"normalcase\") {textTransform=\"none\";continue;}\r\n"+
            "	        		    break;\r\n"+
            "	        	   }\r\n"+
            "	        	   if(fmt.length>4)  font=fmt;/*TODO:validate font*/\r\n"+
            "	           }\r\n"+
            "	           target.style.fontFamily=font;\r\n"+
            "	           if(!underline && !strike) target.style.textDecoration='';\r\n"+
            "	           else if(underline && strike) target.style.textDecoration='underline line-through';\r\n"+
            "	           else target.style.textDecoration=strike ? 'line-through' : 'underline';\r\n"+
            "	           target.style.fontWeight=bold ? 'bold' : 'normal'; \r\n"+
            "	           if(hide)\r\n"+
            "	             target.style.textIndent='-9999px';\r\n"+
            "	           else\r\n"+
            "	             target.style.textIndent='inherit'\r\n"+
            "	           target.style.textAlign=align;\r\n"+
            "	           if(alignItems) target.style.alignItems=alignItems;\r\n"+
            "	           target.style.fontStyle=italic ? 'italic' : '';\r\n"+
            "	           if(textTransform != null)\r\n"+
            "	             target.style.textTransform=textTransform;\r\n"+
            "        	   target.classList.toggle(\"ami_blink\", blink);\r\n"+
            "			}\r\n"+
            "		}else if(key.indexOf('style.')==0){\r\n"+
            "			var k=key.slice(6);\r\n"+
            "			if(target.appliedStyles[k]!=value){  \r\n"+
            "			  target.appliedStyles[k]=value;\r\n"+
            "			}\r\n"+
            "			if(target.style[k]!=value){\r\n"+
            "				target.style[k]=value;\r\n"+
            "			}\r\n"+
            "		}else if(key=='className'){\r\n"+
            "			target.className+=' '+value;\r\n"+
            "			target.cellClassName=value;\r\n"+
            "		} else if (key=='disabled') {\r\n"+
            "			target.setAttribute('disabled', 'true');\r\n"+
            "		}\r\n"+
            "		else{\r\n"+
            "			target[key]=value;\r\n"+
            "		}\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "/**\r\n"+
            " * get the corresponding value given the key from the pipe delimetered style string: getStyleValue(\"_fg\", \"_fs=13|_fg=#e54949|_bg=#ffffff\")\r\n"+
            " */\r\n"+
            "function getStyleValue(key,style){\r\n"+
            "	var entries=(style==null) ? [] : style.split('\\|');\r\n"+
            "	for(var i=0;i<entries.length;i++){\r\n"+
            "		var keyValue=entries[i].split(/=(.*)?/);\r\n"+
            "		var k=keyValue[0];\r\n"+
            "		var value=keyValue[1];\r\n"+
            "		if (value === \"\") \r\n"+
            "			continue;\r\n"+
            "		if(k==key)\r\n"+
            "			return value;\r\n"+
            "	}\r\n"+
            "	return null;\r\n"+
            "}\r\n"+
            "\r\n"+
            "function setCursorPosition(input, start, end) {\r\n"+
            "    if (arguments.length < 3) end = start;\r\n"+
            "    if (\"selectionStart\" in input) {\r\n"+
            "        setTimeout(function() {\r\n"+
            "            input.selectionStart = start;\r\n"+
            "            input.selectionEnd = end;\r\n"+
            "        }, 1);\r\n"+
            "    }\r\n"+
            "    else if (input.createTextRange) {\r\n"+
            "        var rng = input.createTextRange();\r\n"+
            "        rng.moveStart(\"character\", start);\r\n"+
            "        rng.collapse();\r\n"+
            "        rng.moveEnd(\"character\", end - start);\r\n"+
            "        rng.select();\r\n"+
            "    }\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "function getCursorPos");
          out.print(
            "ition(input) {\r\n"+
            "    if (\"selectionStart\" in input) {\r\n"+
            "    	return input.selectionStart;\r\n"+
            "        //return {\r\n"+
            "            //start: input.selectionStart,\r\n"+
            "            //end: input.selectionEnd\r\n"+
            "        //};\r\n"+
            "    }\r\n"+
            "    else if (input.createTextRange && document.selection!=null) {\r\n"+
            "        var sel = document.selection.createRange();\r\n"+
            "        if (sel.parentElement() === input) {\r\n"+
            "            var rng = input.createTextRange();\r\n"+
            "            rng.moveToBookmark(sel.getBookmark());\r\n"+
            "            for (var len = 0;\r\n"+
            "                     rng.compareEndPoints(\"EndToStart\", rng) > 0;\r\n"+
            "                     rng.moveEnd(\"character\", -1)) {\r\n"+
            "                len++;\r\n"+
            "            }\r\n"+
            "            rng.setEndPoint(\"StartToStart\", input.createTextRange());\r\n"+
            "            for (var pos = { start: 0, end: len };\r\n"+
            "                     rng.compareEndPoints(\"EndToStart\", rng) > 0;\r\n"+
            "                     rng.moveEnd(\"character\", -1)) {\r\n"+
            "                pos.start++;\r\n"+
            "                pos.end++;\r\n"+
            "            }\r\n"+
            "            return pos;\r\n"+
            "        }\r\n"+
            "    }\r\n"+
            "    return -1;\r\n"+
            "}\r\n"+
            "\r\n"+
            "function setSelection(input, start, end){\r\n"+
            "	setCursorPosition(input,start,end);\r\n"+
            "}\r\n"+
            "\r\n"+
            "function getIndexOf (array, item, from) {\r\n"+
            "    if (array.prototype && array.prototype.indexOf) // Use the native array method if available\r\n"+
            "        return array.indexOf(item, from);\r\n"+
            "    for (var i = from || 0; i < array.length; i++) {\r\n"+
            "        if (array[i] === item)\r\n"+
            "            return i;\r\n"+
            "    }\r\n"+
            "    return -1;\r\n"+
            "}\r\n"+
            "\r\n"+
            "function isEmptyObject(obj) {\r\n"+
            "  for ( var name in obj ) \r\n"+
            "    return false;\r\n"+
            "  return true;\r\n"+
            "}\r\n"+
            "\r\n"+
            "function joinKeys(delim,obj){\r\n"+
            "	r=\"\";\r\n"+
            "	var first=true;\r\n"+
            "	for(var i in obj){\r\n"+
            "		if(first)\r\n"+
            "			first=false;\r\n"+
            "		else\r\n"+
            "			r=r.concat(delim);\r\n"+
            "		r=r.concat(i);\r\n"+
            "	}\r\n"+
            "	return r;\r\n"+
            "}\r\n"+
            "\r\n"+
            "function diff(a,b){\r\n"+
            "	return a<b ? b-a : a-b;\r\n"+
            "}\r\n"+
            "\r\n"+
            "function isFullScreen(){\r\n"+
            "  return !!(document.fullscreenElement || document.mozFullScreenElement || document.webkitFullscreenElement || document.msFullscreenElement);\r\n"+
            "}\r\n"+
            "\r\n"+
            "function toggleFullScreen(){\r\n"+
            "	setFullScreen(!isFullScreen());\r\n"+
            "}\r\n"+
            "\r\n"+
            "function setFullScreen(on) {\r\n"+
            "  if(on){\r\n"+
            "    if (document.documentElement.requestFullscreen)\r\n"+
            "      document.documentElement.requestFullscreen();\r\n"+
            "    else if (document.documentElement.msRequestFullscreen)\r\n"+
            "      document.documentElement.msRequestFullscreen();\r\n"+
            "    else if (document.documentElement.mozRequestFullScreen)\r\n"+
            "      document.documentElement.mozRequestFullScreen();\r\n"+
            "    else if (document.documentElement.webkitRequestFullscreen)\r\n"+
            "      document.documentElement.webkitRequestFullscreen(Element.ALLOW_KEYBOARD_INPUT);\r\n"+
            "    else\r\n"+
            "      return false;\r\n"+
            "  } else {\r\n"+
            "    if (document.exitFullscreen) \r\n"+
            "      document.exitFullscreen();\r\n"+
            "    else if (document.msExitFullscreen) \r\n"+
            "      document.msExitFullscreen();\r\n"+
            "    else if (document.mozCancelFullScreen) \r\n"+
            "      document.mozCancelFullScreen();\r\n"+
            "    else if (document.webkitExitFullscreen) \r\n"+
            "      document.webkitExitFullscreen();\r\n"+
            "    else \r\n"+
            "      return false;\r\n"+
            "  }\r\n"+
            "  return true;\r\n"+
            "}\r\n"+
            "\r\n"+
            "function fallbackCopyTextToClipboard(text) {\r\n"+
            "  var textArea = document.createElement(\"textarea\");\r\n"+
            "  textArea.value = text;\r\n"+
            "  textArea.style.top = \"0\";\r\n"+
            "  textArea.style.left = \"0\";\r\n"+
            "  textArea.style.position = \"fixed\";\r\n"+
            "  document.body.appendChild(textArea);\r\n"+
            "  textArea.focus();\r\n"+
            "  textArea.select();\r\n"+
            "\r\n"+
            "  try {\r\n"+
            "    document.execCommand('copy');\r\n"+
            "  } catch (err) {\r\n"+
            "    console.error('Fallback: unable to copy', err);\r\n"+
            "  }\r\n"+
            "\r\n"+
            "  document.body.removeChild(textArea);\r\n"+
            "}\r\n"+
            "function copyToClipboard(text) {\r\n"+
            "  if (!navigator.clipboard) {\r\n"+
            "    fallbackCopyTextToClipboard(text);\r\n"+
            "    return;\r\n"+
            "  }\r\n"+
            "  try {\r\n"+
            "    navigator.clipboard.writeText(text);\r\n"+
            "  } catch (err) {\r\n"+
            "    console.error('Fallback: Unable to copy', err);\r\n"+
            "  }\r\n"+
            "}\r\n"+
            "\r\n"+
            "var IMAGE_BUFFER_CANVAS=null;\r\n"+
            "var IMAGE_BUFFER_CONTEXT=null;\r\n"+
            "var IMAGE_BUFFER_DATA=null;\r\n"+
            "function toImageData(e,r,g,b,a){\r\n"+
            "	if(IMAGE_BUFFER_CANVAS==null){\r\n"+
            "      var IMAGE_BUFFER_CANVAS=getDocument(e).createElement('canvas');\r\n"+
            "      IMAGE_BUFFER_CANVAS.width=1;\r\n"+
            "      IMAGE_BUFFER_CANVAS.height=1;\r\n"+
            "      IMAGE_BUFFER_CONTEXT = IMAGE_BUFFER_CANVAS.getContext('2d');\r\n"+
            "       IMAGE_BUFFER_DATA = IMAGE_BUFFER_CONTEXT.createImageData(2,2);\r\n"+
            "	}\r\n"+
            "	var d  = IMAGE_BUFFER_DATA.data;\r\n"+
            "	d[0]   = r;\r\n"+
            "	d[1]   = g;\r\n"+
            "	d[2]   = b;\r\n"+
            "	d[3]   = a;\r\n"+
            "    IMAGE_BUFFER_CONTEXT.putImageData( IMAGE_BUFFER_DATA, 0, 0 );\r\n"+
            "    return \"url('\"+IMAGE_BUFFER_CANVAS.toDataURL('png')+\"')\";\r\n"+
            "}\r\n"+
            "\r\n"+
            "function setCssClassProperty(e,className,key,value){\r\n"+
            "  var style = getDocument(e).createElement('style');\r\n"+
            "  style.type = 'text/css';\r\n"+
            "  style.innerHTML = '.'+className+'{ '+key+': '+value+'; }';\r\n"+
            "  getDocument(e).getElementsByTagName('head')[0].appendChild(style);\r\n"+
            "}\r\n"+
            "\r\n"+
            "function getWindowParam(theWindow,key,dflt){\r\n"+
            "  var search=theWindow.location.search;\r\n"+
            "  var start=search.indexOf('?'+key+'=');\r\n"+
            "  if(start==-1){\r\n"+
            "    start=search.indexOf('&'+key+'=');\r\n"+
            "    if(start==-1)\r\n"+
            "  	  return dflt;\r\n"+
            "  }\r\n"+
            "  start+=key.length+2;\r\n"+
            "  var end=search.indexOf('&',start);\r\n"+
            "  if(end==-1)\r\n"+
            "     end=search.length;\r\n"+
            "  return search.substring(start,end);\r\n"+
            "}\r\n"+
            "\r\n"+
            "function addClassName(target,cn,first){\r\n"+
            "	if(cn==null || cn==\"\")\r\n"+
            "		return;\r\n"+
            "	var cn2=target.className;\r\n"+
            "	var r;\r\n"+
            "	if(cn2==null || cn2==\"\" || cn2==cn){\r\n"+
            "		r=cn;\r\n"+
            "	}else{\r\n"+
            "		r=first ? cn : \"\";\r\n"+
            "		var parts=cn2.split(' +');\r\n"+
            "		for(var i in parts){\r\n"+
            "			var t=parts[i];\r\n"+
            "			if(t==cn)\r\n"+
            "				continue;\r\n"+
            "			if(r!=\"\")\r\n"+
            "				r+=\" \";\r\n"+
            "			r+=t;\r\n"+
            "		}\r\n"+
            "		if(!first)\r\n"+
            "			r+=\" \"+cn;\r\n"+
            "	}\r\n"+
            "	target.className=r;\r\n"+
            "}\r\n"+
            "function removeClassName(target,cn){\r\n"+
            "	if(cn==null || cn==\"\")\r\n"+
            "		return;\r\n"+
            "	var cn2=target.className;\r\n"+
            "	var r;\r\n"+
            "	var r=\"\";\r\n"+
            "	if(cn2!=cn){\r\n"+
            "	  r=\"\";\r\n"+
            "	  var parts=cn2.split(' +');\r\n"+
            "	  for(var i in parts){\r\n"+
            "		var t=parts[i];\r\n"+
            "	    if(t==cn)\r\n"+
            "	      continue;\r\n"+
            "	    if(r!=\"\")\r\n"+
            "	      r+=\" \";\r\n"+
            "	    r+=t;\r\n"+
            "	  }\r\n"+
            "	  target.className=r;\r\n"+
            "	}\r\n"+
            "	target.className=\"\";\r\n"+
            "}\r\n"+
            "\r\n"+
            "function dis(x,y,xx,yy){\r\n"+
            "	return Math.sqrt(sq(x-xx)+sq(y-yy));\r\n"+
            "}\r\n"+
            "\r\n"+
            "function rotateDivElement(divElement,rotate,width,height,xpos,ypos){\r\n"+
            "  while(rotate>=360)\r\n"+
            "    rotate-=360;\r\n"+
            "  while(rotate<0)\r\n"+
            "    rotate+=360;\r\n"+
            "  var style=divElement.style;\r\n"+
            "  if(rotate==0){\r\n"+
            "    style.transform=null;\r\n"+
            "    style.height=toPx(height);\r\n"+
            "    style.width=toPx(width);\r\n"+
            "    style.left=toPx(xpos);\r\n"+
            "    style.top=toPx(ypos);\r\n"+
            "  }else if(rotate==270){\r\n"+
            "    var diff=fl((height-width)/2);\r\n"+
            "    var extra=Math.abs(height-width)%2;\r\n"+
            "    style.transform='rotate(-90deg)';\r\n"+
            "    style.height=toPx(width+extra);\r\n"+
            "    style.width=toPx(height);\r\n"+
            "    style.top=toPx(diff+ypos);\r\n"+
            "    style.left=toPx(-diff+xpos);\r\n"+
            "  }else if(rotate==90){\r\n"+
            "    var diff=fl((height-width)/2);\r\n"+
            "    var extra=Math.abs(height-width)%2;\r\n"+
            "    style.transform='rotate(90deg)';\r\n"+
            "    style.height=toPx(width+extra);\r\n"+
            "    style.width=toPx(height);\r\n"+
            "    style.top=toPx(diff+ypos);\r\n"+
            "    style.left=toPx(-diff-extra+xpos);\r\n"+
            "  } else if(rotate==180){\r\n"+
            "    style.transform='rotate(180deg)';\r\n"+
            "    style.height=toPx(height+ypos);\r\n"+
            "    style.width=toPx(width+xpos);\r\n"+
            "  }\r\n"+
            "}\r\n"+
            "\r\n"+
            "function noNull(a,b){\r\n"+
            "	return a==null ? b: a;\r\n"+
            "}\r\n"+
            "\r\n"+
            "function Glass(window, caller){\r\n"+
            "  var that = this;\r\n"+
            "  this.window=window;\r\n"+
            "  this.caller=caller;\r\n"+
            "  this.visible=false;\r\n"+
            "  this.glassElement=nw(\"div\", \"disable_glass_clear\");\r\n"+
            "  this.glassElement.onclick = function(e) {that.hide();};\r\n"+
            "}\r\n"+
            "currentGlass = null;\r\n"+
            "Glass.prototype.setClassName=function(name){\r\n"+
            "	this.glassElement.className=name; \r\n"+
            "}\r\n"+
            "Glass.prototype.show=function(){\r\n"+
            "	if(this.visible===false){\r\n"+
            "		this.window.document.body.appendChild(this.glassElement);\r\n"+
            "		this.visible=true;\r\n"+
            "		currentGlass = this;\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "Glass.prototype.hide=function(){\r\n"+
            "	if(this.visible===true){\r\n"+
            "		if(this.caller.hideGlass)\r\n"+
            "			this.caller.hideGlass();\r\n"+
            "		this.window.document.body.removeChild(this.glassElement);\r\n"+
            "		this.visible=false;\r\n"+
            "		currentGlass = null;\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "Glass.prototype.appendChild=function(e){\r\n"+
            "	this.glassElement.appendChild(e);\r\n"+
            "}\r\n"+
            "Glass.prototype.handleKeydown=function(e){\r\n"+
            "	if(e.key === \"Escape\" && e.shiftKey == false && e.ctrlKey == false && e.altKey == false){\r\n"+
            "		this.hide();\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "\r\n"+
            "\r\n"+
            "function clip(x,min,max){\r\n"+
            "  return x<min ? min : (x>max ? max : x);\r\n"+
            "}\r\n"+
            "\r\n"+
            "function flipRotation(axis,angle){\r\n"+
            "  return mod(axis+axis-angle,360);\r\n"+
            "}\r\n"+
            "function mod(value, modulous) {\r\n"+
            "  return (value %= modulous) < 0 ? modulous + value : value;\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "function deref(array,idx){\r\n"+
            "	if(array==null)\r\n"+
            "		return null;\r\n"+
            "	return array.length==1 ? array[0] : array[idx];\r\n"+
            "}\r\n"+
            "\r\n"+
            "function add(a,b){\r\n"+
            "	return (a==null || b==null) ? null : a+b;\r\n"+
            "}\r\n"+
            "function sub(a,b){\r\n"+
            "	return (a==null || b==null) ? null : a-b;\r\n"+
            "}\r\n"+
            "function getOwningPortlet(element){\r\n"+
            "	if(element == null)\r\n"+
            "		return null;\r\n"+
            "	if(element.isPortletElement == true)\r\n"+
            "		return element.portlet;\r\n"+
            "	var e = element.parentNode;\r\n"+
            "	while(e!=null && (e.isPortletElement == undefined || e.isPortletElement == false))\r\n"+
            "		e=e.parentNode;\r\n"+
            "	if(e!=null)\r\n"+
            "		return e.portlet;\r\n"+
            "	else\r\n"+
            "		return null;\r\n"+
            "}\r\n"+
            "\r\n"+
            "function escapeHtmlAdv(text, start, end, includeBackslash, replaceNewLineWith, sb){\r\n"+
            "	for (var i = start; i < end; i++) {\r\n"+
            "		var c = text.charAt(i);\r\n"+
            "		switch (c) {\r\n"+
            "			case '\\'':\r\n"+
            "				sb.push(\"&#39;\");\r\n"+
            "				break;\r\n"+
            "			case '\"':\r\n"+
            "				sb.push(\"&#34;\");\r\n"+
            "				break;\r\n"+
            "			case '\\\\':\r\n"+
            "				if (includeBackslash) {\r\n"+
            "					sb.push(\"&#92;\");\r\n"+
            "				} else {\r\n"+
            "					if (++i < end) {\r\n"+
            "						if (text.charAt(i) == '\\\\') {\r\n"+
            "							sb.push(\"&#92;\");\r\n"+
            "						} else {\r\n"+
            "							sb.push(text.charAt(i));\r\n"+
            "						}\r\n"+
            "					}\r\n"+
            "				}\r\n"+
            "				break;\r\n"+
            "			case '\\n':\r\n"+
            "				sb.push(replaceNewLineWith);\r\n"+
            "				break;\r\n"+
            "			case '\\r':\r\n"+
            "				break;\r\n"+
            "			case ' ':\r\n"+
            "				sb.push(\"&nbsp;\");\r\n"+
            "				break;\r\n"+
            "			case '\\t':\r\n"+
            "				sb.push(\"&nbsp;&nbsp;\");\r\n"+
            "				break;\r\n"+
            "			case '>':\r\n"+
            "				sb.push(\"&gt;\");\r\n"+
            "				break;\r\n"+
            "			case '<':\r\n"+
            "				sb.push(\"&lt;\");\r\n"+
            "				break;\r\n"+
            "			case '&':\r\n"+
            "				sb.push(\"&amp;\");\r\n"+
            "				break;\r\n"+
            "			case 0:\r\n"+
            "			case 1:\r\n"+
            "			case 2:\r\n"+
            "			case 3:\r\n"+
            "			case 4:\r\n"+
            "			case 5:\r\n"+
            "			case 6:\r\n"+
            "			case 7:\r\n"+
            "			case 8:\r\n"+
            "				sb.push(\"&#191;\");\r\n"+
            "				break;\r\n"+
            "			default:\r\n"+
            "				sb.push(c);\r\n"+
            "		}\r\n"+
            "\r\n"+
            "	}\r\n"+
            "	return sb.join('');\r\n"+
            "}\r\n"+
            "function escapeHtml(text){\r\n"+
            "	return escapeHtmlAdv(text, 0, text.length, true, \"\\\\n\", []);\r\n"+
            "}\r\n"+
            "\r\n"+
            "function LiveDebugger(element, optio");
          out.print(
            "ns){\r\n"+
            "	var that=this;\r\n"+
            "	this.__doc = null;\r\n"+
            "	this.__doc = element == null? document : getDocument(element);\r\n"+
            "	this.metricElements=new Map();\r\n"+
            "	this.metrics=new Map();\r\n"+
            "	this.__doc.addEventListener(\"readystatechange\", function(e){if(e.target.readyState==\"complete\") that.init(null);});\r\n"+
            "}\r\n"+
            "LiveDebugger.prototype.metricElements;\r\n"+
            "LiveDebugger.prototype.metrics;\r\n"+
            "LiveDebugger.prototype.init=function(options){\r\n"+
            "	this.__options=options==null?{}:options;\r\n"+
            "	this.debugBox = nw(\"div\");\r\n"+
            "	this.__doc.body.appendChild(this.debugBox);\r\n"+
            "	this.debugBox.style.border=\"1px solid black\";\r\n"+
            "	this.debugBox.style.zIndex=\"2000\";\r\n"+
            "	this.debugBox.style.minWidth=\"200px\";\r\n"+
            "	this.debugBox.style.minHeight=\"200px\";\r\n"+
            "	this.debugBox.style.background=\"#eaeaea\";\r\n"+
            "	this.debugBox.style.right=\"0px\";\r\n"+
            "	this.debugBox.style.top=\"28px\";\r\n"+
            "}\r\n"+
            "LiveDebugger.prototype.updateStat=function(elem, key, value){\r\n"+
            "	elem.textContent = key + \" = \" + value;\r\n"+
            "}\r\n"+
            "LiveDebugger.prototype.removeStat=function(key){\r\n"+
            "	this.metrics.delete(key);\r\n"+
            "	var elem = this.metricElements.get(key);\r\n"+
            "	var r = this.metricElements.delete(key);\r\n"+
            "	elem.remove();\r\n"+
            "}\r\n"+
            "LiveDebugger.prototype.addStat=function(key, value){\r\n"+
            "	var nwMetric = nw(\"div\");\r\n"+
            "	nwMetric.style.position=\"relative\";\r\n"+
            "	this.debugBox.appendChild(nwMetric);\r\n"+
            "\r\n"+
            "	this.metrics.set(key,value);\r\n"+
            "	this.metricElements.set(key, nwMetric);\r\n"+
            "	this.updateStat(nwMetric, key, value);\r\n"+
            "\r\n"+
            "	if(this.__options.disableSorting != true)\r\n"+
            "		this.sortStats();\r\n"+
            "}\r\n"+
            "LiveDebugger.prototype.onStat=function(key, value){\r\n"+
            "	if(typeof value === 'object')\r\n"+
            "		value = JSON.stringify(value);\r\n"+
            "		\r\n"+
            "	if(this.metrics.has(key)){\r\n"+
            "		var elem = this.metricElements.get(key);\r\n"+
            "		this.updateStat(elem,key,value);\r\n"+
            "	}\r\n"+
            "	else\r\n"+
            "		this.addStat(key, value);\r\n"+
            "}\r\n"+
            "LiveDebugger.prototype.sortStats=function(){\r\n"+
            "	var keys = Array.from(this.metrics.keys()).sort();\r\n"+
            "	this.metricElements.forEach(function(value,key) { value.remove(); } );\r\n"+
            "	var that=this;\r\n"+
            "	keys.forEach(function(key){ \r\n"+
            "		var elem = that.metricElements.get(key);\r\n"+
            "		that.debugBox.appendChild(elem);\r\n"+
            "	} );\r\n"+
            "}\r\n"+
            "\r\n"+
            "// liveDebugger = new LiveDebugger(null, null);\r\n"+
            "// liveDebugger.onStat(\"metric\", value);\r\n"+
            "\r\n"+
            "//Get the root node's body from the given element\r\n"+
            "function getRootNodeBody(element) { \r\n"+
            "	return element.getRootNode().body;\r\n"+
            "}\r\n"+
            "\r\n"+
            "function parseNumber(value, locales = navigator.languages) {\r\n"+
            "	const example = Intl.NumberFormat(locales).format('1.1'); // example of formatting an en-us number according to the locale supplied\r\n"+
            "	const cleanPattern = new RegExp(`[^-+0-9${ example.charAt( 1 ) }]`, 'g'); // build regex (includes the locale-specific decimal representation in the regex)\r\n"+
            "	const cleaned = value.replace(cleanPattern, ''); // filter out anything that's not - + 0-9 and the decimal rep in the input\r\n"+
            "	const normalized = cleaned.replace(example.charAt(1), '.'); // replace the locale-specific decimal rep to . (which is en-us)\r\n"+
            "\r\n"+
            "	return parseFloat(normalized);\r\n"+
            "}\r\n"+
            "\r\n"+
            "function hexToRgb(hex) {\r\n"+
            "    var bigint = parseInt(hex, 16);\r\n"+
            "    var r = (bigint >> 16) & 255;\r\n"+
            "    var g = (bigint >> 8) & 255;\r\n"+
            "    var b = bigint & 255;\r\n"+
            "\r\n"+
            "    return r + \", \" + g + \", \" + b;\r\n"+
            "}\r\n"+
            "\r\n"+
            "function formatDate(format, selDate, timeMs) {\r\n"+
            "	// most detailed: weekday day/month/year\r\n"+
            "	if (!format)\r\n"+
            "		return '';\r\n"+
            "	var day=selDate.getDate();\r\n"+
            "	day = day < 10? '0'+day:day;\r\n"+
            "	const fullYear=selDate.getFullYear();\r\n"+
            "	const year=fullYear.toString().substr(2); // get last 2 digits\r\n"+
            "	const month=selDate.getMonth()+1; // index\r\n"+
            "	const fullMonth=month < 10? '0'+month:month;\r\n"+
            "	const weekDayName=weekdayNames[selDate.getDay()];\r\n"+
            "	const monthName=monthNames[selDate.getMonth()];\r\n"+
            "	const monthNameShortened=monthNamesShortened[selDate.getMonth()];\r\n"+
            "	var formated='';\r\n"+
            "\r\n"+
            "	switch (format) {\r\n"+
            "	case 'M/dd/yyyy': // 3/14/2015\r\n"+
            "		formated=month+'/'+day+'/'+fullYear;\r\n"+
            "		break;\r\n"+
            "	case 'MM/dd/yyyy': // 03/14/2015\r\n"+
            "		formated=fullMonth+'/'+day+'/'+fullYear;\r\n"+
            "		break;\r\n"+
            "	case 'M/dd': // 3/14\r\n"+
            "		formated=month+'/'+day;\r\n"+
            "		break;\r\n"+
            "	case 'M/dd/yy': // 3/14/15\r\n"+
            "		formated=month+'/'+day+'/'+year;\r\n"+
            "		break;\r\n"+
            "	case 'MM/dd/yy': // 03/14/15\r\n"+
            "		formated=fullMonth+'/'+day+'/'+year;\r\n"+
            "		break;\r\n"+
            "	case 'MMMM dd, yyyy': // March 14, 2015\r\n"+
            "		formated=monthName+' '+day+', '+fullYear;\r\n"+
            "		break;\r\n"+
            "	case 'EEEE, MMMM dd, yyyy': // Saturday, March 14, 2015\r\n"+
            "		formated=weekDayName+', '+monthName+' '+day+', '+fullYear;\r\n"+
            "		break;\r\n"+
            "	case 'dd-MMM': // 14-Mar\r\n"+
            "		formated=day+'-'+monthNameShortened;\r\n"+
            "		break;\r\n"+
            "	case 'dd-MMM-yy': // 14-Mar-15\r\n"+
            "		formated=day+'-'+monthNameShortened+'-'+year;\r\n"+
            "		break;\r\n"+
            "	case 'MMM-yy': // Mar-15\r\n"+
            "		formated=monthNameShortened+'-'+year;\r\n"+
            "		break;\r\n"+
            "	case 'MMMM-yy': // March-15\r\n"+
            "		formated=monthName+'-'+year;\r\n"+
            "		break;\r\n"+
            "	case 'yyyy/MM/dd': // 2015/03/14\r\n"+
            "		formated=fullYear+'/'+fullMonth+'/'+day;\r\n"+
            "		break;\r\n"+
            "	case 'dd/MM/yyyy': // 14/03/2015\r\n"+
            "		formated=day+'/'+fullMonth+'/'+fullYear;\r\n"+
            "		break;\r\n"+
            "	default: // 20150314\r\n"+
            "		break;\r\n"+
            "	}\r\n"+
            "	return formated;\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "function formatTime(format, hour, minute) {\r\n"+
            "	if (!format)\r\n"+
            "		return '';\r\n"+
            "	var formated='';\r\n"+
            "	var meridian=hour > 11?'PM':'AM';\r\n"+
            "	var shortHour= hour %12; // 0-12\r\n"+
            "	if (shortHour==0) {\r\n"+
            "		shortHour=12;\r\n"+
            "	}\r\n"+
            "	const fullHour= hour < 10?'0'+ hour:hour;\r\n"+
            "	const shortFull= shortHour < 10?'0'+ shortHour:shortHour;\r\n"+
            "	var fullMinutes=minute<10?'0'+minute:minute;\r\n"+
            "	switch(format){\r\n"+
            "	case 'h:mm a': // 1:30 PM\r\n"+
            "		formated = shortHour+':'+fullMinutes+' '+meridian;\r\n"+
            "		break;\r\n"+
            "	case 'HH:mm': // 13:30\r\n"+
            "		formated = fullHour+':'+fullMinutes;\r\n"+
            "		break;\r\n"+
            "	case 'hh:mm a': // 01:30 PM \r\n"+
            "		formated=shortFull+':'+fullMinutes+' '+ meridian;\r\n"+
            "		break;\r\n"+
            "	case 'H:mm': // 1:30\r\n"+
            "		formated=hour+':'+fullMinutes;\r\n"+
            "		break;\r\n"+
            "	default:\r\n"+
            "		console.log('invalid format: ', format); // should never happen\r\n"+
            "		break;\r\n"+
            "	}\r\n"+
            "	return formated;\r\n"+
            "}\r\n"+
            "\r\n"+
            "function pxToInt(str) {\r\n"+
            "	if (typeof str == 'number')\r\n"+
            "		return str;\r\n"+
            "	return parseInt(fromPx(str));\r\n"+
            "}\r\n"+
            "\r\n"+
            "function setBrowserURL(url){\r\n"+
            "    window.history.replaceState({}, document.title,url=='' ? location.pathname : url);\r\n"+
            "}\r\n"+
            "\r\n"+
            "// used by calendar fields to ensure proper display of date/time\r\n"+
            "//function ensureFieldContentVisible(element, content) {\r\n"+
            "//	if (element ==null)\r\n"+
            "//		return null;\r\n"+
            "//	const cs=getComputedStyle(element);\r\n"+
            "//	const paddings=parseInt(fromPx(cs.paddingLeft)) + parseInt(fromPx(cs.paddingRight));\r\n"+
            "//	const margins=parseInt(fromPx(cs.marginLeft)) + parseInt(fromPx(cs.marginRight));\r\n"+
            "//	const borders=parseInt(fromPx(cs.borderLeftWidth)) + parseInt(fromPx(cs.borderRightWidth));\r\n"+
            "//	const oWidth=pxToInt(cs.width);\r\n"+
            "//	const totalOriginalWidth=oWidth+paddings+margins+borders;\r\n"+
            "//	// ensure no extra space\r\n"+
            "//	if (!content) {\r\n"+
            "////		element.style.width=oWidth;\r\n"+
            "////		element.style.minWidth=oWidth;\r\n"+
            "//		// returns original width allocated\r\n"+
            "//		return totalOriginalWidth;\r\n"+
            "//	}\r\n"+
            "//	const textWidth=getFieldContentSize(element,content);\r\n"+
            "//	// account for paddings\r\n"+
            "//	const w=Math.round(textWidth) + paddings+margins;\r\n"+
            "//	// if original width can accommodate the text;\r\n"+
            "//	if (oWidth > w) {\r\n"+
            "//		// no need to adjust\r\n"+
            "////		element.style.width=w;\r\n"+
            "////		element.style.minWidth=w;\r\n"+
            "//		return totalOriginalWidth;\r\n"+
            "//	}\r\n"+
            "////	element.style.width=toPx(w);\r\n"+
            "////	element.style.minWidth=toPx(w);\r\n"+
            "//	// returns total width allocated\r\n"+
            "//	return w;\r\n"+
            "//}\r\n"+
            "\r\n"+
            "//function getFieldContentSize(element, text) {\r\n"+
            "//	const canvas = getTextWidth.canvas || (getTextWidth.canvas = document.createElement(\"canvas\")); \r\n"+
            "//	const context = canvas.getContext(\"2d\");\r\n"+
            "//	context.font=element.style.fontSize+ ' ' + element.style.fontFamily;\r\n"+
            "//	// compute text width\r\n"+
            "//	const metrics=context.measureText(text);\r\n"+
            "//	return metrics.width;\r\n"+
            "//}");

	}
	
}