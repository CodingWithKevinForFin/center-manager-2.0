package com.f1._jsps;

import com.f1.http.*;
import com.f1.utils.*;
import com.f1.http.handler.AbstractHttpHandler;
import com.f1.http.HttpUtils;
import static com.f1.http.HttpUtils.*;

public class portal_rsc_javascript_math_js_1 extends AbstractHttpHandler{

	public portal_rsc_javascript_math_js_1() {
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
            "Quaternion = function( x, y, z, w ) {\r\n"+
            "  this.set( x || 0, y || 0, z || 0, w !== undefined ? w : 1 );\r\n"+
            "};\r\n"+
            "\r\n"+
            "Quaternion.prototype.set= function ( x, y, z, w ) {\r\n"+
            "  this.x = x;\r\n"+
            "  this.y = y;\r\n"+
            "  this.z = z;\r\n"+
            "  this.w = w;\r\n"+
            "  return this;\r\n"+
            "\r\n"+
            "};\r\n"+
            "\r\n"+
            "Quaternion.prototype.copy= function ( q ) {\r\n"+
            "  this.x = q.x;\r\n"+
            "  this.y = q.y;\r\n"+
            "  this.z = q.z;\r\n"+
            "  this.w = q.w;\r\n"+
            "  return this;\r\n"+
            "},\r\n"+
            "\r\n"+
            "Quaternion.prototype.setFromEuler= function ( vec3 ) {\r\n"+
            "\r\n"+
            "  var c = Math.PI / 360, \r\n"+
            "  x = vec3.x * c,\r\n"+
            "  y = vec3.y * c,\r\n"+
            "  z = vec3.z * c,\r\n"+
            "\r\n"+
            "  c1 = Math.cos( y  ),\r\n"+
            "  s1 = Math.sin( y  ),\r\n"+
            "  c2 = Math.cos( z  ),\r\n"+
            "  s2 = Math.sin( z  ),\r\n"+
            "  c3 = Math.cos( x  ),\r\n"+
            "  s3 = Math.sin( x  ),\r\n"+
            "\r\n"+
            "  c1c2 = c1 * c2,\r\n"+
            "  s1s2 = s1 * s2;\r\n"+
            "\r\n"+
            "  this.w = c1c2 * c3  - s1s2 * s3;\r\n"+
            "  this.x = c1c2 * s3  + s1s2 * c3;\r\n"+
            "  this.y = s1 * c2 * c3 + c1 * s2 * s3;\r\n"+
            "  this.z = c1 * s2 * c3 - s1 * c2 * s3;\r\n"+
            "\r\n"+
            "  return this;\r\n"+
            "\r\n"+
            "};\r\n"+
            "\r\n"+
            "Quaternion.prototype.setFromAxisAngle= function ( axis, angle ) {\r\n"+
            "\r\n"+
            "  var halfAngle = angle / 2,\r\n"+
            "  s = Math.sin( halfAngle );\r\n"+
            "\r\n"+
            "  this.x = axis.x * s;\r\n"+
            "  this.y = axis.y * s;\r\n"+
            "  this.z = axis.z * s;\r\n"+
            "  this.w = Math.cos( halfAngle );\r\n"+
            "\r\n"+
            "  return this;\r\n"+
            "\r\n"+
            "};\r\n"+
            "\r\n"+
            "Quaternion.prototype.calculateW = function () {\r\n"+
            "  this.w = - Math.sqrt( Math.abs( 1.0 - this.x * this.x - this.y * this.y - this.z * this.z ) );\r\n"+
            "  return this;\r\n"+
            "};\r\n"+
            "\r\n"+
            "Quaternion.prototype.inverse= function () {\r\n"+
            "  this.x *= -1;\r\n"+
            "  this.y *= -1;\r\n"+
            "  this.z *= -1;\r\n"+
            "  return this;\r\n"+
            "\r\n"+
            "};\r\n"+
            "\r\n"+
            "Quaternion.prototype.length= function () {\r\n"+
            "  return Math.sqrt( this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w );\r\n"+
            "};\r\n"+
            "\r\n"+
            "Quaternion.prototype.normalize= function () {\r\n"+
            "  var l = Math.sqrt( this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w );\r\n"+
            "  if ( l === 0 ) {\r\n"+
            "    this.x = 0;\r\n"+
            "    this.y = 0;\r\n"+
            "    this.z = 0;\r\n"+
            "    this.w = 0;\r\n"+
            "  } else {\r\n"+
            "    l = 1 / l;\r\n"+
            "    this.x = this.x * l;\r\n"+
            "    this.y = this.y * l;\r\n"+
            "    this.z = this.z * l;\r\n"+
            "    this.w = this.w * l;\r\n"+
            "  }\r\n"+
            "  return this;\r\n"+
            "};\r\n"+
            "\r\n"+
            "Quaternion.prototype.multiplySelf= function ( quat2 ) {\r\n"+
            "\r\n"+
            "  var qax = this.x,  qay = this.y,  qaz = this.z,  qaw = this.w,\r\n"+
            "  qbx = quat2.x, qby = quat2.y, qbz = quat2.z, qbw = quat2.w;\r\n"+
            "\r\n"+
            "  this.x = qax * qbw + qaw * qbx + qay * qbz - qaz * qby;\r\n"+
            "  this.y = qay * qbw + qaw * qby + qaz * qbx - qax * qbz;\r\n"+
            "  this.z = qaz * qbw + qaw * qbz + qax * qby - qay * qbx;\r\n"+
            "  this.w = qaw * qbw - qax * qbx - qay * qby - qaz * qbz;\r\n"+
            "\r\n"+
            "  return this;\r\n"+
            "};\r\n"+
            "\r\n"+
            "Quaternion.prototype.multiply= function ( q1, q2 ) {\r\n"+
            "  this.x =  q1.x * q2.w + q1.y * q2.z - q1.z * q2.y + q1.w * q2.x;\r\n"+
            "  this.y = -q1.x * q2.z + q1.y * q2.w + q1.z * q2.x + q1.w * q2.y;\r\n"+
            "  this.z =  q1.x * q2.y - q1.y * q2.x + q1.z * q2.w + q1.w * q2.z;\r\n"+
            "  this.w = -q1.x * q2.x - q1.y * q2.y - q1.z * q2.z + q1.w * q2.w;\r\n"+
            "\r\n"+
            "  return this;\r\n"+
            "};\r\n"+
            "\r\n"+
            "Quaternion.prototype.multiplyVector3= function ( vec, dest ) {\r\n"+
            "  if( !dest ) \r\n"+
            "	  dest = vec; \r\n"+
            "\r\n"+
            "  var x    = vec.x,  y  = vec.y,  z  = vec.z,\r\n"+
            "  qx   = this.x, qy = this.y, qz = this.z, qw = this.w;\r\n"+
            "\r\n"+
            "  // calculate quat * vec\r\n"+
            "  var ix =  qw * x + qy * z - qz * y,\r\n"+
            "  iy =  qw * y + qz * x - qx * z,\r\n"+
            "  iz =  qw * z + qx * y - qy * x,\r\n"+
            "  iw = -qx * x - qy * y - qz * z;\r\n"+
            "\r\n"+
            "  // calculate result * inverse quat\r\n"+
            "  dest.x = ix * qw + iw * -qx + iy * -qz - iz * -qy;\r\n"+
            "  dest.y = iy * qw + iw * -qy + iz * -qx - ix * -qz;\r\n"+
            "  dest.z = iz * qw + iw * -qz + ix * -qy - iy * -qx;\r\n"+
            "\r\n"+
            "  return dest;\r\n"+
            "}\r\n"+
            "\r\n"+
            "Quaternion.slerp = function ( qa, qb, qm, t ) {\r\n"+
            "\r\n"+
            "  var cosHalfTheta = qa.w * qb.w + qa.x * qb.x + qa.y * qb.y + qa.z * qb.z;\r\n"+
            "\r\n"+
            "  if (cosHalfTheta < 0) {\r\n"+
            "    qm.w = -qb.w; qm.x = -qb.x; qm.y = -qb.y; qm.z = -qb.z;\r\n"+
            "    cosHalfTheta = -cosHalfTheta;\r\n"+
            "  } else {\r\n"+
            "    qm.copy(qb);\r\n"+
            "  }\r\n"+
            "\r\n"+
            "  if ( Math.abs( cosHalfTheta ) >= 1.0 ) {\r\n"+
            "    qm.w = qa.w; qm.x = qa.x; qm.y = qa.y; qm.z = qa.z;\r\n"+
            "    return qm;\r\n"+
            "  }\r\n"+
            "\r\n"+
            "  var halfTheta = Math.acos( cosHalfTheta ),\r\n"+
            "  sinHalfTheta = Math.sqrt( 1.0 - cosHalfTheta * cosHalfTheta );\r\n"+
            "\r\n"+
            "  if ( Math.abs( sinHalfTheta ) < 0.001 ) {\r\n"+
            "    qm.w = 0.5 * ( qa.w + qb.w );\r\n"+
            "    qm.x = 0.5 * ( qa.x + qb.x );\r\n"+
            "    qm.y = 0.5 * ( qa.y + qb.y );\r\n"+
            "    qm.z = 0.5 * ( qa.z + qb.z );\r\n"+
            "    return qm;\r\n"+
            "  }\r\n"+
            "  var ratioA = Math.sin( ( 1 - t ) * halfTheta ) / sinHalfTheta,\r\n"+
            "  ratioB = Math.sin( t * halfTheta ) / sinHalfTheta;\r\n"+
            "  qm.w = ( qa.w * ratioA + qm.w * ratioB );\r\n"+
            "  qm.x = ( qa.x * ratioA + qm.x * ratioB );\r\n"+
            "  qm.y = ( qa.y * ratioA + qm.y * ratioB );\r\n"+
            "  qm.z = ( qa.z * ratioA + qm.z * ratioB );\r\n"+
            "  return qm;\r\n"+
            "}\r\n"+
            "\r\n"+
            "Quaternion.prototype.setToEuler=function(v) {\r\n"+
            "	var test = this.x*this.y + this.z*this.w;\r\n"+
            "	if (test > 0.499) { // singularity at north pole\r\n"+
            "		v.y = 2 * Math.atan2(this.x,this.w);\r\n"+
            "		v.z = Math.PI/2;\r\n"+
            "		v.x = 0;\r\n"+
            "	}else if (test < -0.499) { // singularity at south pole\r\n"+
            "		v.y = -2 * Math.atan2(this.x,this.w);\r\n"+
            "		v.z = - Math.PI/2;\r\n"+
            "		v.x = 0;\r\n"+
            "		return;\r\n"+
            "	}else{\r\n"+
            "      var sqx = this.x*this.x;\r\n"+
            "      var sqy = this.y*this.y;\r\n"+
            "      var sqz = this.z*this.z;\r\n"+
            "      v.y = Math.atan2(2*this.y*this.w-2*this.x*this.z , 1 - 2*sqy - 2*sqz);\r\n"+
            "	  v.z = Math.asin(2*test);\r\n"+
            "	  v.x = Math.atan2(2*this.x*this.w-2*this.y*this.z , 1 - 2*sqx - 2*sqz);\r\n"+
            "	}\r\n"+
            "	\r\n"+
            "    var c = 180 / Math.PI ;\r\n"+
            "    v.x*=c;\r\n"+
            "    v.y*=c;\r\n"+
            "    v.z*=c;\r\n"+
            "    \r\n"+
            "}\r\n"+
            "\r\n"+
            "");

	}
	
}