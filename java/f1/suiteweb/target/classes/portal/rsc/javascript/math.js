Quaternion = function( x, y, z, w ) {
  this.set( x || 0, y || 0, z || 0, w !== undefined ? w : 1 );
};

Quaternion.prototype.set= function ( x, y, z, w ) {
  this.x = x;
  this.y = y;
  this.z = z;
  this.w = w;
  return this;

};

Quaternion.prototype.copy= function ( q ) {
  this.x = q.x;
  this.y = q.y;
  this.z = q.z;
  this.w = q.w;
  return this;
},

Quaternion.prototype.setFromEuler= function ( vec3 ) {

  var c = Math.PI / 360, 
  x = vec3.x * c,
  y = vec3.y * c,
  z = vec3.z * c,

  c1 = Math.cos( y  ),
  s1 = Math.sin( y  ),
  c2 = Math.cos( z  ),
  s2 = Math.sin( z  ),
  c3 = Math.cos( x  ),
  s3 = Math.sin( x  ),

  c1c2 = c1 * c2,
  s1s2 = s1 * s2;

  this.w = c1c2 * c3  - s1s2 * s3;
  this.x = c1c2 * s3  + s1s2 * c3;
  this.y = s1 * c2 * c3 + c1 * s2 * s3;
  this.z = c1 * s2 * c3 - s1 * c2 * s3;

  return this;

};

Quaternion.prototype.setFromAxisAngle= function ( axis, angle ) {

  var halfAngle = angle / 2,
  s = Math.sin( halfAngle );

  this.x = axis.x * s;
  this.y = axis.y * s;
  this.z = axis.z * s;
  this.w = Math.cos( halfAngle );

  return this;

};

Quaternion.prototype.calculateW = function () {
  this.w = - Math.sqrt( Math.abs( 1.0 - this.x * this.x - this.y * this.y - this.z * this.z ) );
  return this;
};

Quaternion.prototype.inverse= function () {
  this.x *= -1;
  this.y *= -1;
  this.z *= -1;
  return this;

};

Quaternion.prototype.length= function () {
  return Math.sqrt( this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w );
};

Quaternion.prototype.normalize= function () {
  var l = Math.sqrt( this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w );
  if ( l === 0 ) {
    this.x = 0;
    this.y = 0;
    this.z = 0;
    this.w = 0;
  } else {
    l = 1 / l;
    this.x = this.x * l;
    this.y = this.y * l;
    this.z = this.z * l;
    this.w = this.w * l;
  }
  return this;
};

Quaternion.prototype.multiplySelf= function ( quat2 ) {

  var qax = this.x,  qay = this.y,  qaz = this.z,  qaw = this.w,
  qbx = quat2.x, qby = quat2.y, qbz = quat2.z, qbw = quat2.w;

  this.x = qax * qbw + qaw * qbx + qay * qbz - qaz * qby;
  this.y = qay * qbw + qaw * qby + qaz * qbx - qax * qbz;
  this.z = qaz * qbw + qaw * qbz + qax * qby - qay * qbx;
  this.w = qaw * qbw - qax * qbx - qay * qby - qaz * qbz;

  return this;
};

Quaternion.prototype.multiply= function ( q1, q2 ) {
  this.x =  q1.x * q2.w + q1.y * q2.z - q1.z * q2.y + q1.w * q2.x;
  this.y = -q1.x * q2.z + q1.y * q2.w + q1.z * q2.x + q1.w * q2.y;
  this.z =  q1.x * q2.y - q1.y * q2.x + q1.z * q2.w + q1.w * q2.z;
  this.w = -q1.x * q2.x - q1.y * q2.y - q1.z * q2.z + q1.w * q2.w;

  return this;
};

Quaternion.prototype.multiplyVector3= function ( vec, dest ) {
  if( !dest ) 
	  dest = vec; 

  var x    = vec.x,  y  = vec.y,  z  = vec.z,
  qx   = this.x, qy = this.y, qz = this.z, qw = this.w;

  // calculate quat * vec
  var ix =  qw * x + qy * z - qz * y,
  iy =  qw * y + qz * x - qx * z,
  iz =  qw * z + qx * y - qy * x,
  iw = -qx * x - qy * y - qz * z;

  // calculate result * inverse quat
  dest.x = ix * qw + iw * -qx + iy * -qz - iz * -qy;
  dest.y = iy * qw + iw * -qy + iz * -qx - ix * -qz;
  dest.z = iz * qw + iw * -qz + ix * -qy - iy * -qx;

  return dest;
}

Quaternion.slerp = function ( qa, qb, qm, t ) {

  var cosHalfTheta = qa.w * qb.w + qa.x * qb.x + qa.y * qb.y + qa.z * qb.z;

  if (cosHalfTheta < 0) {
    qm.w = -qb.w; qm.x = -qb.x; qm.y = -qb.y; qm.z = -qb.z;
    cosHalfTheta = -cosHalfTheta;
  } else {
    qm.copy(qb);
  }

  if ( Math.abs( cosHalfTheta ) >= 1.0 ) {
    qm.w = qa.w; qm.x = qa.x; qm.y = qa.y; qm.z = qa.z;
    return qm;
  }

  var halfTheta = Math.acos( cosHalfTheta ),
  sinHalfTheta = Math.sqrt( 1.0 - cosHalfTheta * cosHalfTheta );

  if ( Math.abs( sinHalfTheta ) < 0.001 ) {
    qm.w = 0.5 * ( qa.w + qb.w );
    qm.x = 0.5 * ( qa.x + qb.x );
    qm.y = 0.5 * ( qa.y + qb.y );
    qm.z = 0.5 * ( qa.z + qb.z );
    return qm;
  }
  var ratioA = Math.sin( ( 1 - t ) * halfTheta ) / sinHalfTheta,
  ratioB = Math.sin( t * halfTheta ) / sinHalfTheta;
  qm.w = ( qa.w * ratioA + qm.w * ratioB );
  qm.x = ( qa.x * ratioA + qm.x * ratioB );
  qm.y = ( qa.y * ratioA + qm.y * ratioB );
  qm.z = ( qa.z * ratioA + qm.z * ratioB );
  return qm;
}

Quaternion.prototype.setToEuler=function(v) {
	var test = this.x*this.y + this.z*this.w;
	if (test > 0.499) { // singularity at north pole
		v.y = 2 * Math.atan2(this.x,this.w);
		v.z = Math.PI/2;
		v.x = 0;
	}else if (test < -0.499) { // singularity at south pole
		v.y = -2 * Math.atan2(this.x,this.w);
		v.z = - Math.PI/2;
		v.x = 0;
		return;
	}else{
      var sqx = this.x*this.x;
      var sqy = this.y*this.y;
      var sqz = this.z*this.z;
      v.y = Math.atan2(2*this.y*this.w-2*this.x*this.z , 1 - 2*sqy - 2*sqz);
	  v.z = Math.asin(2*test);
	  v.x = Math.atan2(2*this.x*this.w-2*this.y*this.z , 1 - 2*sqx - 2*sqz);
	}
	
    var c = 180 / Math.PI ;
    v.x*=c;
    v.y*=c;
    v.z*=c;
    
}

