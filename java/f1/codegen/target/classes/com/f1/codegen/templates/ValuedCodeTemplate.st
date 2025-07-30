package $c.packageName$;

import com.f1.base.Valued;
import com.f1.base.Caster;
import com.f1.utils.DetailedException;
import com.f1.base.ValuedSchema;
import com.f1.base.Acker;
import com.f1.base.ValuedHashCodeGenerator;
import com.f1.base.Ackable;
import com.f1.base.ValuedParam;
import com.f1.base.StringBuildable;
import com.f1.utils.AbstractValuedParam;
import com.f1.utils.converter.bytes.ByteArraySelfConverter;
import com.f1.utils.converter.bytes.FromByteArrayConverterSession;
import com.f1.utils.converter.bytes.ToByteArrayConverterSession;
import com.f1.utils.converter.bytes.ObjectToByteArrayConverter;
import com.f1.utils.BasicFixPoint;
import java.io.IOException;
import java.io.DataInput;
import java.io.DataOutput;
import com.f1.utils.VH;
import com.f1.utils.OH;
import com.f1.utils.SH;

public $if(isAbstract)$abstract$endif$ class $c.simpleClassName$ $c.extendsClause$ $c.innerClass.canonicalName$ $c.implementsClause$ com.f1.base.ValuedSchema, com.f1.base.CodeGenerated $if(c.supportsPids)$,ByteArraySelfConverter$endif$,Cloneable{

    private int hashCode=ValuedHashCodeGenerator.next(this);
    
    @Override
    public int hashCode(){
      return this.hashCode;
    }
    
    @Override
	public $c.cloneReturnType$ clone(){
       try{
         $if(isLockable)$
	       final $c.cloneReturnType$ r= ($c.cloneReturnType$)super.clone();
	       (($c.simpleClassName$)r).__locked=false;
	       return r;
	     $else$
	       return ($c.cloneReturnType$)super.clone();
	     $endif$
       } catch( Exception e){
          throw new RuntimeException("error cloning",e);
       }
    }
    
    $if(isLockable)$
    private boolean __locked;
    @Override
    public void lock(){
       this.__locked=true;
    }

    @Override
	public boolean isLocked(){
	   return this.__locked;
	}
	
	$endif$
    
$c.params:{ x |
    private $x.type.canonicalName$ $x.varname$;
}$
    private static final String NAMES[]={$first(c.params):{ x| "$x.name$"}$ $rest(c.params):{x|,"$x.name$"}$};

	@Override
    public void put(String name, Object value){//asdf
    $if(isLockable)$
	   if(__locked)
	       throw newLockedException(name,value);
	$endif$
        final int h=Math.abs(name.hashCode()) % $c.buckets.hash$;
        try{
        switch(h){
$c.buckets.hashEntries:{x|
                case $x.key$:
$x.value:{y|
                    if(name == "$y.value.name$" || name.equals("$y.value.name$")) {this.$y.value.varname$=($y.value.boxedType.canonicalName$)value;return;}
}$break;}$
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
        }catch(NullPointerException e){
            throw new NullPointerException("primitive value can not be null: "+name);
        }
    }


	@Override
    public boolean putNoThrow(String name, Object value){
    $if(isLockable)$
	  if(this.__locked) return false;
	$endif$
           if(name==null)
               return false;
        final int h=Math.abs(name.hashCode()) % $c.buckets.hash$;
        switch(h){
$c.buckets.hashEntries:{x|
                case $x.key$:
$x.value:{y|
                    if(name == "$y.value.name$" || name.equals("$y.value.name$")) {this.$y.value.varname$=($y.value.boxedType.canonicalName$)value;return true;}
}$break;}$
        }
        return false;
    }

	@Override
    public Object ask(String name){
        final int h=Math.abs(name.hashCode()) % $c.buckets.hash$;
        switch(h){
$c.buckets.hashEntries:{x|
                case $x.key$:
$x.value:{y|
		    $if(y.value.type.primitive)$
                    if(name == "$y.value.name$" || name.equals("$y.value.name$")) {return OH.valueOf(this.$y.value.varname$);}
		    $else$
                    if(name == "$y.value.name$" || name.equals("$y.value.name$")) {return this.$y.value.varname$;}
            $endif$
}$break;}$
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }

	@Override
    public Class askClass(String name){
        final int h=Math.abs(name.hashCode()) % $c.buckets.hash$;
        switch(h){
$c.buckets.hashEntries:{x|
                case $x.key$:
$x.value:{y|
                    if(name == "$y.value.name$" || name.equals("$y.value.name$")) {return $y.value.type.canonicalName$.class;}
}$break;}$
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }
    
	@Override
    public ValuedParam askValuedParam(String name){
        final int h=Math.abs(name.hashCode()) % $c.buckets.hash$;
        switch(h){
$c.buckets.hashEntries:{x|
                case $x.key$:
$x.value:{y|
                    if(name == "$y.value.name$" || name.equals("$y.value.name$")) {return VALUED_PARAM_$y.value.name$;}
}$break;}$
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }

	@Override
    public int askPosition(String name){
        final int h=Math.abs(name.hashCode()) % $c.buckets.hash$;
        switch(h){
$c.buckets.hashEntries:{x|
                case $x.key$:
$x.value:{y|
                    if(name == "$y.value.name$" || name.equals("$y.value.name$")) {return $y.value.position$;}
}$break;}$
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }
    
	@Override
    public String[] askParams(){
        return NAMES;
    }

	@Override
    public int askParamsCount(){
        return $length(c.params)$;
    }

	@Override
	public Class<Valued> askType(){
	    return (Class)$c.simpleClassName$.class;
	}

	@Override
    public boolean askParamValid(String name){
        final int h=Math.abs(name.hashCode()) % $c.buckets.hash$;
        switch(h){
$c.buckets.hashEntries:{x|
                case $x.key$:
$x.value:{y|
                    if(name == "$y.value.name$" || name.equals("$y.value.name$")) {return true;}
}$break;}$
        }
        return false;
    }
    
	@Override
    public byte askBasicType(String name){
        final int h=Math.abs(name.hashCode()) % $c.buckets.hash$;
        switch(h){
$c.buckets.hashEntries:{x|
                case $x.key$:
$x.value:{y|
                    if(name == "$y.value.name$" || name.equals("$y.value.name$")) {return $y.value.basicType$;}
}$break;}$
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }
    
    public Object askAtPosition(int position){
        switch(position){
$c.params:{ x |
        case $x.position$:return $x.varname$;
}$
        }
        throw new IndexOutOfBoundsException("supplied position > param count: "+position+" >= "+ $length(c.params)$);
    }
$c.params:{ x |
    public $x.type.canonicalName$ get$x.upperCasedName$(){
        return this.$x.varname$;
    }
    public void set$x.upperCasedName$($x.type.canonicalName$ $x.varname$){
    $if(isLockable)$
	   if(__locked)
	       throw newLockedException("$x.name$",$x.varname$);
	$endif$
        this.$x.varname$=$x.varname$;
    }
}$

$c.unsupportedParams:{ x |
    public $x.type.canonicalName$ get$x.upperCasedName$(){
        return $x.defaultValue$;
	   //throw new UnsupportedOperationException("get$x.upperCasedName$");
    }
    public void set$x.upperCasedName$($x.type.canonicalName$ $x.varname$){
       //if($x.varname$==$x.defaultValue$)
       //return;
	   throw new UnsupportedOperationException("set$x.upperCasedName$("+$x.varname$+")");
    }
}$

$c.params:{ x |
  $if(x.supported)$
    private static final class VALUED_PARAM_CLASS_$x.name$ extends AbstractValuedParam<$c.simpleClassName$> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return $x.basicType$;
	    }
	    
	    @Override
	    public void write($c.simpleClassName$ valued, DataOutput stream) throws IOException{
		    $if(x.type.primitive)$
		      stream.write$x.streamName$(valued.get$x.upperCasedName$());
		    $else$
		       throw new IOException("can not write to type to dataStream: $x.type.name$}");
		    $endif$
	    }
	    
	    @Override
	    public void read($c.simpleClassName$ valued, DataInput stream) throws IOException{
		    $if(x.type.primitive)$
		      valued.set$x.upperCasedName$(stream.read$x.streamName$());
		    $else$
		       throw new IOException("can not write to type to dataStream: $x.type.name$}");
		    $endif$
	    }
	    
	    @Override
	    public boolean isPrimitive() {
		    return $x.type.primitive$;
	    }
    
	    @Override
	    public boolean isValued() {
		    return $x.valuedType$;
	    }
    
	    @Override
	    public byte getTransience() {
		    return $x.transience$;
	    }
    
	    @Override
	    public boolean isBoxed() {
		    return $x.isBoxedType$;
	    }
    
	    @Override
	    public boolean isPrimitiveOrBoxed() {
		    return $x.isBoxedType$ || $x.type.primitive$;
	    }
	    @Override
	    public boolean isImmutable() {
		    return $x.isImmutable$;
	    }
    
	    @Override
	    public int askPosition() {
		    return $x.position$;
	    }
    
	    @Override
	    public byte getPid() {
            return $if(c.supportsPids)$$x.annotations.PID_value$$else$NO_PID$endif$;
	    }
    
	    @Override
	    public String getName() {
            return "$x.name$";
	    }
    
	    @Override
	    public Object getValue($c.simpleClassName$ valued) {
		    return ($x.type.canonicalName$)(($c.simpleClassName$)valued).get$x.upperCasedName$();
	    }
    
	    @Override
	    public void setValue($c.simpleClassName$ valued, Object value) {
		    valued.set$x.upperCasedName$(($x.boxedType.canonicalName$)value);
	    }
    
	    @Override
	    public void copy($c.simpleClassName$ source, $c.simpleClassName$ dest) {
		    dest.set$x.upperCasedName$(source.get$x.upperCasedName$());
	    }
	    
	    @Override
	    public boolean areEqual($c.simpleClassName$ source, $c.simpleClassName$ dest) {
	        return OH.eq(dest.get$x.upperCasedName$(),source.get$x.upperCasedName$());
	    }
	    
	    $if(x.isBoolean)$
	    @Override
	    public boolean getBoolean($c.simpleClassName$ valued) {
		    return valued.get$x.upperCasedName$();
	    }
    
	    @Override
	    public void setBoolean($c.simpleClassName$ valued, boolean value) {
		    valued.set$x.upperCasedName$(value);
	    }
	    $endif$
	    
	    $if(x.isByte)$
	    @Override
	    public byte getByte($c.simpleClassName$ valued) {
		    return valued.get$x.upperCasedName$();
	    }
    
	    @Override
	    public void setByte($c.simpleClassName$ valued, byte value) {
		    valued.set$x.upperCasedName$(value);
	    }
	    $endif$
	    
	    $if(x.isShort)$
	    @Override
	    public short getShort($c.simpleClassName$ valued) {
		    return valued.get$x.upperCasedName$();
	    }
    
	    @Override
	    public void setShort($c.simpleClassName$ valued, short value) {
		    valued.set$x.upperCasedName$(value);
	    }
	    $endif$
	    
	    $if(x.isInt)$
	    @Override
	    public int getInt($c.simpleClassName$ valued) {
		    return valued.get$x.upperCasedName$();
	    }
    
	    @Override
	    public void setInt($c.simpleClassName$ valued, int value) {
		    valued.set$x.upperCasedName$(value);
	    }
	    $endif$
	    
	    $if(x.isLong)$
	    @Override
	    public long getLong($c.simpleClassName$ valued) {
		    return valued.get$x.upperCasedName$();
	    }
    
	    @Override
	    public void setLong($c.simpleClassName$ valued, long value) {
		    valued.set$x.upperCasedName$(value);
	    }
	    $endif$
	    
	    $if(x.isFloat)$
	    @Override
	    public float getFloat($c.simpleClassName$ valued) {
		    return valued.get$x.upperCasedName$();
	    }
    
	    @Override
	    public void setFloat($c.simpleClassName$ valued, float value) {
		    valued.set$x.upperCasedName$(value);
	    }
	    $endif$
	    
	    $if(x.isDouble)$
	    @Override
	    public double getDouble($c.simpleClassName$ valued) {
		    return valued.get$x.upperCasedName$();
	    }
    
	    @Override
	    public void setDouble($c.simpleClassName$ valued, double value) {
		    valued.set$x.upperCasedName$(value);
	    }
	    $endif$
	    
	    $if(x.isChar)$
	    @Override
	    public char getChar($c.simpleClassName$ valued) {
		    return valued.get$x.upperCasedName$();
	    }
    
	    @Override
	    public void setChar($c.simpleClassName$ valued, char value) {
		    valued.set$x.upperCasedName$(value);
	    }
	    $endif$
	    
    
	    @Override
	    public Class getReturnType() {
		    return $x.type.canonicalName$.class;
	    }
	    private static final Caster CASTER=OH.getCaster($x.type.canonicalName$.class);
	    @Override
	    public Caster getCaster() {
		    return CASTER;
	    }
	    
	    @Override
	    public void append($c.simpleClassName$ valued, StringBuilder sb){
	        $if(x.isString)$
	           SH.quote('"',valued.get$x.upperCasedName$(),sb);
	        $else$
	        sb.append(valued.get$x.upperCasedName$());
	        $endif$
	    }
	    @Override
	    public void append($c.simpleClassName$ valued, StringBuildable sb){
	        $if(x.isString)$
	           SH.quote('"',valued.get$x.upperCasedName$(),sb);
	        $else$
	        sb.append(valued.get$x.upperCasedName$());
	        $endif$
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:$x.type.canonicalName$ $x.name$";
	    }
	    @Override
	    public void clear($c.simpleClassName$ valued){
	       valued.set$x.upperCasedName$($x.defaultValue$);
	    }
	};
    private static final ValuedParam VALUED_PARAM_$x.name$=new VALUED_PARAM_CLASS_$x.name$();
  $endif$
}$

    private static final ValuedParam VALUED_PARAMS[]=new ValuedParam[]{ $c.params:{ x |VALUED_PARAM_$x.name$, }$};

$if(c.supportsPids)$

    private static final byte PIDS[]={$first(c.params):{ x| $x.annotations.PID_value$}$ $rest(c.params):{x|,$x.annotations.PID_value$}$};
    
    @Override
    public ValuedParam askValuedParam(byte pid){
        switch(pid){
$c.params:{x|             case $x.annotations.PID_value$: return VALUED_PARAM_$x.name$;
}$            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    
    public boolean askPidValid(byte pid){
        switch(pid){
$c.params:{x|             case $x.annotations.PID_value$: return true;
}$            default:return false;
        }
    }
    
    
    public String askParam(byte pid){
        switch(pid){
$c.params:{x|             case $x.annotations.PID_value$: return "$x.name$";
}$            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public int askPosition(byte pid){
        switch(pid){
$c.params:{x|             case $x.annotations.PID_value$: return $x.position$;
}$            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public byte askPid(String name){
$c.params:{x|             if(name=="$x.name$") return $x.annotations.PID_value$;
}$            
$c.params:{x|             if("$x.name$".equals(name)) return $x.annotations.PID_value$;
}$            throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }
    
    public boolean askSupportsPids(){
        return true;
    }
    
    public byte[] askPids(){
    return PIDS;
    }

    public Object ask(byte pid){
        switch(pid){
$c.params:{x|             case $x.annotations.PID_value$: return $if(x.type.primitive)$ OH.valueOf(this.$x.varname$); $else$ this.$x.varname$; $endif$
}$            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }

    public Class askClass(byte pid){
        switch(pid){
$c.params:{x|             case $x.annotations.PID_value$: return $x.type.canonicalName$.class;
}$            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }

    public byte askBasicType(byte pid){
        switch(pid){
$c.params:{x|             case $x.annotations.PID_value$: return $x.basicType$;
}$            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid for byte").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public void put(byte pid,Object value){
        try{
        switch(pid){
$c.params:{x|             case $x.annotations.PID_value$: this.$x.varname$=($x.boxedType.canonicalName$)value;return;
}$            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
        }catch(NullPointerException e){
            throw new NullPointerException("primitive value can not be null: "+askParam(pid));
        }catch(RuntimeException e){
            throw new RuntimeException("error setting value by: "+askParam(pid),e);
        }
    }
    
    public boolean putNoThrow(byte pid,Object value){
        switch(pid){
$c.params:{x|             case $x.annotations.PID_value$: this.$x.varname$=($x.boxedType.canonicalName$)value;return true;
}$            default:return false;
        }
    }
    
    public boolean askBoolean(byte pid){
        switch(pid){
$c.paramsByType.Boolean:{x|             case $x.annotations.PID_value$: return this.$x.varname$;
}$            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid for boolean value").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public byte askByte(byte pid){
        switch(pid){
$c.paramsByType.Byte:{x|             case $x.annotations.PID_value$: return this.$x.varname$;
}$            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid for byte value").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public char askChar(byte pid){
        switch(pid){
$c.paramsByType.Character:{x|             case $x.annotations.PID_value$: return this.$x.varname$;
}$            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid for char value").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public short askShort(byte pid){
        switch(pid){
$c.paramsByType.Short:{x|             case $x.annotations.PID_value$: return this.$x.varname$;
}$            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid for short value").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public int askInt(byte pid){
        switch(pid){
$c.paramsByType.Integer:{x|             case $x.annotations.PID_value$: return this.$x.varname$;
}$            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid for int value").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public float askFloat(byte pid){
        switch(pid){
$c.paramsByType.Float:{x|             case $x.annotations.PID_value$: return this.$x.varname$;
}$            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid for float value").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public long askLong(byte pid){
        switch(pid){
$c.paramsByType.Long:{x|             case $x.annotations.PID_value$: return this.$x.varname$;
}$            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid for long value").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public double askDouble(byte pid){
        switch(pid){
$c.paramsByType.Double:{x|             case $x.annotations.PID_value$: return this.$x.varname$;
}$            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid for double value").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public void putBoolean(byte pid,boolean value){
    $if(isLockable)$
	   if(__locked)
	       throw newLockedException(pid,value);
	$endif$
        switch(pid){
$c.paramsByType.Boolean:{x|             case $x.annotations.PID_value$: this.$x.varname$=value;return;
}$            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid for boolean value").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public void putByte(byte pid,byte value){
    $if(isLockable)$
	   if(__locked)
	       throw newLockedException(pid,value);
	$endif$
        switch(pid){
$c.paramsByType.Byte:{x|             case $x.annotations.PID_value$: this.$x.varname$=value;return;
}$            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid for byte value").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public void putChar(byte pid,char value){
    $if(isLockable)$
	   if(__locked)
	       throw newLockedException(pid,value);
	$endif$
        switch(pid){
$c.paramsByType.Character:{x|             case $x.annotations.PID_value$: this.$x.varname$=value;return;
}$            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid for char value").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public void putShort(byte pid,short value){
    $if(isLockable)$
	   if(__locked)
	       throw newLockedException(pid,value);
	$endif$
        switch(pid){
$c.paramsByType.Short:{x|             case $x.annotations.PID_value$: this.$x.varname$=value;return;
}$            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid for short value").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public void putInt(byte pid,int value){
    $if(isLockable)$
	   if(__locked)
	       throw newLockedException(pid,value);
	$endif$
        switch(pid){
$c.paramsByType.Integer:{x|             case $x.annotations.PID_value$: this.$x.varname$=value;return;
}$            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid for int value").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public void putFloat(byte pid,float value){
    $if(isLockable)$
	   if(__locked)
	       throw newLockedException(pid,value);
	$endif$
        switch(pid){
$c.paramsByType.Float:{x|             case $x.annotations.PID_value$: this.$x.varname$=value;return;
}$            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid for float value").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public void putLong(byte pid,long value){
    $if(isLockable)$
	   if(__locked)
	       throw newLockedException(pid,value);
	$endif$
        switch(pid){
$c.paramsByType.Long:{x|             case $x.annotations.PID_value$: this.$x.varname$=value;return;
}$            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid for long value").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public void putDouble(byte pid,double value){
    $if(isLockable)$
	   if(__locked)
	       throw newLockedException(pid,value);
	$endif$
        switch(pid){
$c.paramsByType.Double:{x|             case $x.annotations.PID_value$: this.$x.varname$=value;return;
}$            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid for double value").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public void read(FromByteArrayConverterSession session) throws IOException{
    $if(isLockable)$
	   if(__locked)
	       throw new com.f1.base.LockedException("can not modify locked object, hence can not deserialize from stream: " + this.toString());
	$endif$
        ObjectToByteArrayConverter converter=session.getConverter();
        DataInput in=session.getStream();//
        if(!in.readBoolean()){
            for (;;) {
                final String name = com.f1.utils.converter.bytes.StringToByteArrayConverter.readString(in);
                if (name.length() == 0)
                    break;
                put(name, converter.read(session));
            }
            return;
        }
        byte basicType;
        for(;;){
            final byte pid=in.readByte();
            switch(pid){
        $c.paramsByPid:{x|
        case $x.annotations.PID_value$:
        $if(x.type.primitive)$
            if((basicType=in.readByte())!=$x.basicType$)
                break;
            this.$x.varname$=in.read$x.streamName$();
        $elseif(x.isFixPoint)$
            if((basicType=in.readByte())!=$x.basicType$)
                break;
            this.$x.varname$=BasicFixPoint.nuw(in.readLong());
        $else$
            this.$x.varname$=($x.boxedType.canonicalName$)converter.read(session);
        $endif$
            break;
}$                case -1:
                    return;
                default:
                    basicType=in.readByte();
            putNoThrow(pid,converter.getConverter(basicType).read(session));
            }
        }
    }
    

    public void write(ToByteArrayConverterSession session) throws IOException{
        ObjectToByteArrayConverter converter=session.getConverter();
        byte transience=converter.getSkipTransience();
        DataOutput out=session.getStream();
        out.writeBoolean(true);//pids supported
        $c.paramsByPid:{x|
if(this.$x.varname$!=$x.defaultValue$ && ($x.transience$ & transience)==0){
    out.writeByte($x.annotations.PID_value$);
        $if(x.type.primitive)$
    out.writeByte($x.basicType$);
    out.write$x.streamName$(this.$x.varname$);
        $elseif(x.isFixPoint)$
    out.writeByte($x.basicType$);
    out.writeLong(this.$x.varname$.getBytes());
        $else$
    converter.write(this.$x.varname$,session);
        $endif$
}
}$;
        out.writeByte(-1);
    }
    
$else$
    public boolean askSupportsPids(){
        return false;
    }
    
    public byte[] askPids(){
        throw new UnsupportedOperationException("PIDS NOT SUPPORTED");
        
    }

    public String askParam(byte pid){
        throw new UnsupportedOperationException("PIDS NOT SUPPORTED");
    }
    
    public int askPosition(byte pid){
        throw new UnsupportedOperationException("PIDS NOT SUPPORTED");
    }
    
    public byte askPid(String name){
        throw new UnsupportedOperationException("PIDS NOT SUPPORTED");
    }
    
    public Object ask(byte pid){
        throw new UnsupportedOperationException("PIDS NOT SUPPORTED");
    }

    public Class askClass(byte pid){
        throw new UnsupportedOperationException("PIDS NOT SUPPORTED");
    }

    public byte askBasicType(byte pid){
        throw new UnsupportedOperationException("PIDS NOT SUPPORTED");
    }
    
    public void put(byte pid,Object value){
        throw new UnsupportedOperationException("PIDS NOT SUPPORTED");
    }
    
    public boolean putNoThrow(byte pid,Object value){
        throw new UnsupportedOperationException("PIDS NOT SUPPORTED");
    }
    
    public boolean askBoolean(byte pid){
        throw new UnsupportedOperationException("PIDS NOT SUPPORTED");
    }

    public byte askByte(byte pid){
        throw new UnsupportedOperationException("PIDS NOT SUPPORTED");
    }

    public short askShort(byte pid){
        throw new UnsupportedOperationException("PIDS NOT SUPPORTED");
    }

    public char askChar(byte pid){
        throw new UnsupportedOperationException("PIDS NOT SUPPORTED");
    }

    public int askInt(byte pid){
        throw new UnsupportedOperationException("PIDS NOT SUPPORTED");
    }

    public float askFloat(byte pid){
        throw new UnsupportedOperationException("PIDS NOT SUPPORTED");
    }

    public long askLong(byte pid){
        throw new UnsupportedOperationException("PIDS NOT SUPPORTED");
    }

    public double askDouble(byte pid){
        throw new UnsupportedOperationException("PIDS NOT SUPPORTED");
    }

    public void putBoolean(byte pid, boolean value){
        throw new UnsupportedOperationException("PIDS NOT SUPPORTED");
    }

    public void putByte(byte pid, byte value){
        throw new UnsupportedOperationException("PIDS NOT SUPPORTED");
    }

    public void putShort(byte pid, short value){
        throw new UnsupportedOperationException("PIDS NOT SUPPORTED");
    }

    public void putChar(byte pid, char value){
        throw new UnsupportedOperationException("PIDS NOT SUPPORTED");
    }

    public void putInt(byte pid, int value){
        throw new UnsupportedOperationException("PIDS NOT SUPPORTED");
    }

    public void putFloat(byte pid, float value){
        throw new UnsupportedOperationException("PIDS NOT SUPPORTED");
    }

    public void putLong(byte pid, long value){
        throw new UnsupportedOperationException("PIDS NOT SUPPORTED");
    }

    public void putDouble(byte pid, double value){
        throw new UnsupportedOperationException("PIDS NOT SUPPORTED");
    }
        
    public boolean askPidValid(byte param){
        throw new UnsupportedOperationException("PIDS NOT SUPPORTED");
    }
    
    public ValuedParam askValuedParam(byte param){
        throw new UnsupportedOperationException("PIDS NOT SUPPORTED");
    }
$endif$

    public ValuedParam[] askValuedParams(){
        return VALUED_PARAMS;
    }

    
    $if(isAckable)$
    
    private int ___ackerId;
    private Acker ___acker;
    
    @Override
	public boolean askAckIsPosDup(){
	    return ___ackerId<0;
	}
    @Override
	public int askAckId(){
	    return ___ackerId<0 ? -___ackerId : ___ackerId;
	}

    @Override
	public void putAckId(int ___ackerId,boolean isPosDup){
	    this.___ackerId=isPosDup ? -___ackerId : ___ackerId;
	}

    @Override
	public void ack(Object v){
	    if(___acker!=null)
	        ___acker.ack(this,v);
	}

    @Override
	public void registerAcker(Acker ___acker){
	    if(___acker!=null && this.___acker!=null)
	        throw new RuntimeException("Acker already registered");
	    this.___acker=___acker;
	}

    @Override
	public void transferAckerTo(Ackable ackable){
	    ackable.registerAcker(___acker);
	    this.___acker=null;
	    ackable.putAckId(askAckId(),askAckIsPosDup());
	    this.___ackerId=NO_ACK_ID;
	}
    $endif$
	
    @Override
    public String toString(){
        return VH.toString(this);
    }
    
    @Override
    public ValuedSchema<Valued> askSchema(){
        return this;
    }
    
    private static final DetailedException newMissingValueException(Object key,Object existing,String message){
      DetailedException e=new DetailedException(message);
      e.set("key",key);
      e.set("existing",existing);
      return e;
    }
    private final DetailedException newLockedException(Object key,Object value){
      DetailedException e=new DetailedException("Can not modify locked class");
      if(key instanceof String)
        e.set("target param",key);
      else if(key!=null)
        e.set("target pid",key);
      e.set("target value",value);
      e.set("target",this);
      e.initCause(new com.f1.base.LockedException());
      return e;
    }
    

}