package com.f1.utils.string;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.f1.base.Caster;
import com.f1.utils.OH;
import com.f1.utils.RH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_Simple;
import com.f1.utils.math.DoubleMath;
import com.f1.utils.math.FloatMath;
import com.f1.utils.math.IntMath;
import com.f1.utils.math.LongMath;
import com.f1.utils.math.PrimitiveMathManager;
import com.f1.utils.string.node.ArrayNode;
import com.f1.utils.string.node.BlockNode;
import com.f1.utils.string.node.ConstNode;
import com.f1.utils.string.node.DoWhileNode;
import com.f1.utils.string.node.ExpressionNode;
import com.f1.utils.string.node.ForEachNode;
import com.f1.utils.string.node.ForNode;
import com.f1.utils.string.node.IfElseNode;
import com.f1.utils.string.node.KeywordNode;
import com.f1.utils.string.node.MethodNode;
import com.f1.utils.string.node.NewNode;
import com.f1.utils.string.node.OperationNode;
import com.f1.utils.string.node.VariableNode;
import com.f1.utils.string.node.WhileNode;
import com.f1.utils.structs.ArrayIterator;

/**
 * Supports a java like parse tree, with a few exceptions:<BR>
 * (1) No types, no casting and declared variables are not strongly typed<BR>
 * (2) No stack / block scope for variables<BR>
 * (3) An expression without an operand will be evaluated by calling toString()<BR>
 * (4) The final value evaluated will have toString() invoked.<BR>
 * <P>
 * The following statements are supported:<BR>
 * (1) <B>BLOCK</B> { <STATEMENT> } -- (returns void) <BR>
 * (2) <B>FOR LOOP</B> - for(initialization;termination;increment) <STATEMENT> -- (returns void)<BR>
 * (3) <B>FOREACH LOOP</B> - for(initialization;iterable_or_array) <STATEMENT> -- (returns void)<BR>
 * (4) <B>WHILE LOOP</B> - while(expression) <STATEMENT> -- (returns void)<BR>
 * (5) <B>DO WHILE LOOP</B> do { <STATEMENT> } while(expression); -- (returns void)<BR>
 * (6) <B>IF CONTROL</B> if(boolean_expression) true_expression (7) <B>IF ELSE CONTROL</B> if(boolean_expression) true_expression else false_expression -- (returns void)<BR>
 * (8) <B>ASSIGNMENT</B> variable assignmentOperator expression -- (returns evaluated expression)
 * <P>
 * 
 * The following declarations are supported:<BR>
 * (1) <B>NEW DECLARATION<B> new classtype(argmumentlist);<BR>
 * (2) <B>ARRAY DECLARATION<B> new classtype[]{valuelist};<BR>
 * (3) <B>ARRAY DEFAULT DECLARATION<B> new classtype[size];<BR>
 * (4) <B>STRING CONSTANT<B>"string";<BR>
 * (5) <B>CHAR CONSTANT<B>'character';<BR>
 * (6) <B>INT CONSTANT<B>number;<BR>
 * (7) <B>HEX INT CONSTANT<B>0xnumber;<BR>
 * (8) <B>OCT INT CONSTANT<B>0number;<BR>
 * (9) <B>LONG CONSTANT<B>numberL;<BR>
 * (10) <B>HEX LONG CONSTANT<B>0xnumberL;<BR>
 * (11) <B>OCT LONG CONSTANT<B>0numberL;
 * <P>
 * 
 * The following operators are supported:
 * 
 * Priority 1: <B>[ ]</B> array index <BR>
 * Priority 1: <B>()</B> method call<BR>
 * Priority 1: <B>.</B> member access<BR>
 * Priority 2: <B>++</B> pre- or postfix increment <BR>
 * Priority 2: <B>--</B> pre- or postfix decrement<BR>
 * Priority 2: <B>+ -</B> unary plus, minus<BR>
 * Priority 2: <B>~</B> bitwise NOT<BR>
 * Priority 2: <B>!</B> boolean (logical) NOT<BR>
 * Priority 2: <B>new</B> object creation<BR>
 * Priority 3: <B>* / %</B> multiplication, division, remainder <BR>
 * Priority 4: <B>+ -</B> addition, substraction,string concatenation <BR>
 * Priority 5: <B><< </B> signed bit shift <BR>
 * Priority 5: <B>>> </B>signed bit shift right<BR>
 * Priority 5: <B>>>> </B>unsigned bit shift right<BR>
 * Priority 6: <B>< <= </B>less than, less than or equal to <BR>
 * Priority 6: <B>> >= </B>greater than, greater than or equal to<BR>
 * Priority 6: <B>instanceof </B>reference test<BR>
 * Priority 7: <B>== </B>equal to <BR>
 * Priority 7: <B>!= </B>not equal to<BR>
 * Priority 8: <B>& </B>bitwise AND <BR>
 * Priority 8: <B>& </B>boolean (logical) AND<BR>
 * Priority 9: <B>^ </B>bitwise XOR <BR>
 * Priority 9: <B>^ </B>boolean (logical) XOR<BR>
 * Priority 10: <B>| </B>bitwise OR <BR>
 * Priority 10: <B>| </B>boolean (logical) OR<BR>
 * Priority 11: <B>&& </B>boolean (logical) AND <BR>
 * Priority 12: <B>|| </B>boolean (logical) OR <BR>
 * Priority 13: <B>? : </B>conditional <BR>
 * Priority 14: <B>= </B>assignment <BR>
 * Priority 14: <B>= /= += -= %= <<= >>= >>>= &= ^= |= </B>combined assigment<BR>
 * <BR>
 */
public class JavaInvoker {

	public static class PartialOperation {

		public final Object left;
		public final String right;

		public PartialOperation(Object left, String right) {
			this.left = left;
			this.right = right;
		}

	}

	private final boolean writeMode = true;
	private static final Logger log = Logger.getLogger(JavaInvoker.class.getName());
	public static final Object VOID = new Object() {
		@Override
		public String toString() {
			return "VOID";
		}
	};

	private boolean allowPartial;

	private Object evaluateVariable(VariableNode n, ObjectScope objects) {
		if (objects.containsKey(n.getVarname()))
			return objects.get(n.getVarname());
		return new Package(n.getVarname());
	}

	public Object evaluate(Node n, ObjectScope objects) {
		if (n == null)
			return null;
		if (n instanceof ConstNode)
			return evaluateConst((ConstNode) n);
		if (n instanceof OperationNode)
			return evaluateOperation((OperationNode) n, objects);
		if (n instanceof ExpressionNode)
			return evaluateExpression((ExpressionNode) n, objects);
		if (n instanceof VariableNode)
			return evaluateVariable((VariableNode) n, objects);
		if (n instanceof NewNode)
			return evaluateNew((NewNode) n, objects);
		if (n instanceof MethodNode)
			return n;
		if (n instanceof KeywordNode)
			return n;
		if (n instanceof ForNode)
			return evaluateFor((ForNode) n, objects);
		if (n instanceof ForEachNode)
			return evaluateForEach((ForEachNode) n, objects);
		if (n instanceof IfElseNode)
			return evaluateIfElse((IfElseNode) n, objects);
		if (n instanceof BlockNode)
			return evaluateBlock((BlockNode) n, objects);
		if (n instanceof WhileNode)
			return evaluateWhile((WhileNode) n, objects);
		if (n instanceof DoWhileNode)
			return evaluateDoWhile((DoWhileNode) n, objects);
		throw new RuntimeException("cant handle: " + n.getClass());
	}

	public Invoker toInvoker(Node n) {

		if (n == null)
			return null;
		if (n instanceof ConstNode)
			return new ConstNodeInvoker((ConstNode) n);
		if (n instanceof OperationNode)
			return new OperationNodeInvoker((OperationNode) n);
		if (n instanceof ExpressionNode)
			return new ExpressionNodeInvoker((ExpressionNode) n);
		if (n instanceof VariableNode)
			return new VariableNodeInvoker((VariableNode) n);
		if (n instanceof NewNode)
			return new NewNodeInvoker((NewNode) n);
		if (n instanceof ForNode)
			return new ForNodeInvoker((ForNode) n);
		if (n instanceof ForEachNode)
			return new ForEachNodeInvoker((ForEachNode) n);
		if (n instanceof IfElseNode)
			return new IfElseNodeInvoker((IfElseNode) n);
		if (n instanceof BlockNode)
			return new BlockNodeInvoker((BlockNode) n);
		if (n instanceof WhileNode)
			return new WhileNodeInvoker((WhileNode) n);
		if (n instanceof DoWhileNode)
			return new DoWhileNodeInvoker((DoWhileNode) n);
		throw new RuntimeException("cant handle: " + n.getClass());
	}

	public class ConstNodeInvoker implements Invoker {

		private ConstNode node;

		public ConstNodeInvoker(ConstNode node) {
			this.node = node;
		}

		@Override
		public Object visit(ObjectScope o) {
			return evaluateConst(node);
		}

	}

	public class OperationNodeInvoker implements Invoker {
		private OperationNode node;

		public OperationNodeInvoker(OperationNode node) {
			this.node = node;
		}

		@Override
		public Object visit(ObjectScope o) {
			return evaluateOperation(node, o);
		}

	}

	public class ExpressionNodeInvoker implements Invoker {
		private ExpressionNode node;

		public ExpressionNodeInvoker(ExpressionNode node) {
			this.node = node;
		}

		@Override
		public Object visit(ObjectScope o) {
			return evaluateExpression(node, o);
		}

	}

	public class ForNodeInvoker implements Invoker {

		private ForNode node;

		public ForNodeInvoker(ForNode node) {
			this.node = node;
		}

		@Override
		public Object visit(ObjectScope o) {
			return evaluateFor(node, o);
		}

	}

	public class ForEachNodeInvoker implements Invoker {

		private ForEachNode node;

		public ForEachNodeInvoker(ForEachNode node) {
			this.node = node;
		}

		@Override
		public Object visit(ObjectScope o) {
			return evaluateForEach(node, o);
		}

	}

	public class VariableNodeInvoker implements Invoker {

		private VariableNode node;

		public VariableNodeInvoker(VariableNode node) {
			this.node = node;
		}

		@Override
		public Object visit(ObjectScope o) {
			return evaluateVariable(node, o);
		}

	}

	public class NewNodeInvoker implements Invoker {

		private NewNode node;

		public NewNodeInvoker(NewNode node) {
			this.node = node;
		}

		@Override
		public Object visit(ObjectScope o) {
			return evaluateNew(node, o);
		}

	}

	public class IfElseNodeInvoker implements Invoker {

		private IfElseNode node;

		public IfElseNodeInvoker(IfElseNode node) {
			this.node = node;
		}

		@Override
		public Object visit(ObjectScope o) {
			return evaluateIfElse(node, o);
		}

	}

	public class BlockNodeInvoker implements Invoker {

		private BlockNode node;

		public BlockNodeInvoker(BlockNode node) {
			this.node = node;
		}

		@Override
		public Object visit(ObjectScope o) {
			return evaluateBlock(node, o);
		}

	}

	public class WhileNodeInvoker implements Invoker {

		private WhileNode node;

		public WhileNodeInvoker(WhileNode node) {
			this.node = node;
		}

		@Override
		public Object visit(ObjectScope o) {
			return evaluateWhile(node, o);
		}

	}

	public class DoWhileNodeInvoker implements Invoker {

		private DoWhileNode node;

		public DoWhileNodeInvoker(DoWhileNode node) {
			this.node = node;
		}

		@Override
		public Object visit(ObjectScope o) {
			return evaluateDoWhile(node, o);
		}

	}

	private Object evaluateBlock(BlockNode block, ObjectScope objects) {
		for (int i = 0, l = block.getNodesCount(); i < l; i++)
			evaluate(block.getNodeAt(i), objects);
		return VOID;
	}

	public Object evaluateWhile(WhileNode n, ObjectScope objects) {
		while ((Boolean) evaluate(n.getConditions(), objects))
			evaluate(n.getBlock(), objects);
		return VOID;
	}

	public Object evaluateDoWhile(DoWhileNode n, ObjectScope objects) {
		do {
			evaluate(n.getBlock(), objects);
		} while ((Boolean) evaluate(n.getConditions(), objects));
		return VOID;
	}

	public Object evaluateFor(ForNode n, ObjectScope objects) {
		evaluate(n.getInits(), objects);
		while ((Boolean) evaluate(n.getConditions(), objects)) {
			evaluate(n.getBlock(), objects);
			evaluate(n.getOps(), objects);
		}

		return VOID;
	}

	public Object evaluateForEach(ForEachNode n, ObjectScope objects) {
		Object o = evaluate(n.getArray(), objects);
		Iterator it;
		if (o.getClass().isArray())
			it = new ArrayIterator<Object>((Object[]) o);
		else
			it = ((Iterable) o).iterator();
		while (it.hasNext()) {
			objects.put(n.getVar().getVarname(), it.next());
			evaluate(n.getBlock(), objects);
		}

		return VOID;
	}

	public Object evaluateIfElse(IfElseNode n, ObjectScope objects) {
		if ((Boolean) evaluate(n.getIfClause(), objects))
			evaluate(n.getIfBlock(), objects);
		else if (n.getElseBlock() != null)
			evaluate(n.getElseBlock(), objects);
		return VOID;
	}

	public Object evaluateExpression(ExpressionNode n, ObjectScope objects) {
		return evaluate(n.getValue(), objects);
	}

	public Object evaluateConst(ConstNode n) {
		return n.getValue();
	}

	public Object evaluateNew(NewNode n, ObjectScope objects) {
		if (!writeMode)
			throw new InvokerException("can not create objects in read only mode: " + n.getClassName());
		Object fullClassName2 = findClass(n.getClassName(), objects.getImports());
		if (!(fullClassName2 instanceof Class))
			throw new InvokerException("right side of new expression must be a class: " + n.getClassName());
		Class fullClassName = (Class) fullClassName2;
		if (n.getArrayNode() != null) {
			return buildArray(fullClassName, n.getArrayNode(), n.getDimensionsCount(), objects);
		} else if (n.getDimensionsCount() > 0) {
			Integer[] dims = new Integer[n.getDimensionsCount()];
			for (int i = 0; i < dims.length; i++)
				if (n.getDimensionAt(i) != null)
					dims[i] = (Integer) evaluate(n.getDimensionAt(i), objects);
			return RH.newArrayNd(fullClassName, dims);
		} else {
			Object[] params = new Object[n.getParamsCount()];
			for (int i = 0; i < params.length; i++) {
				params[i] = evaluate(n.getParamAt(i), objects);
				if (params[i] instanceof Package)
					throw new RuntimeException("object not found: " + params[i]);
			}
			return RH.invokeConstructor(fullClassName, params);
		}
	}

	private Object buildArray(Class clazz, ArrayNode arrayNode, int depth, ObjectScope objects) {
		final int size = arrayNode.getParamsCount();
		Integer[] sizes = new Integer[depth];
		sizes[0] = size;
		Object r = RH.newArrayNd(clazz, sizes);
		if (size == 0)
			return r;
		if (depth == 1)
			for (int i = 0; i < size; i++)
				Array.set(r, i, evaluate(arrayNode.getParamAt(i), objects));
		else
			for (int i = 0; i < size; i++) {
				Node node = ((ExpressionNode) arrayNode.getParamAt(i)).getValue();
				if (node instanceof ArrayNode)
					Array.set(r, i, buildArray(clazz, (ArrayNode) node, depth - 1, objects));
				else
					Array.set(r, i, evaluate(node, objects));
			}
		return r;
	}

	public Object evaluateOperation(OperationNode n, ObjectScope objects) {
		String operation = n.getOpString();
		if ("=".equals(operation)) {
			Object r = evaluate(n.getRight(), objects);
			if (n.getLeft() instanceof OperationNode && "[".equals(((OperationNode) n.getLeft()).getOpString())) {
				OperationNode on = (OperationNode) n.getLeft();
				VariableNode l = (VariableNode) on.getLeft();
				Integer index = (Integer) evaluate(on.getRight(), objects);
				Object array = objects.get(l.getVarname());
				if (array == null)
					objects.put(l.getVarname(), array = Array.newInstance(r.getClass(), index + 1));
				if (Array.getLength(array) <= index) {
					Object array2 = Array.newInstance(array.getClass().getComponentType(), index + 1);
					System.arraycopy(array, 0, array2, 0, Array.getLength(array));
					objects.put(l.getVarname(), array = array2);
				}
				Array.set(array, index, r);
			} else {
				if (n.getLeft() instanceof OperationNode) {
					OperationNode o = (OperationNode) n.getLeft();
					if (".".equals(o.getOpString())) {
						if (!(o.getRight() instanceof VariableNode))

							throw new InvokerException("left side of assignment expression is invalid, should be target.fieldName=value.  For: " + n.getLeft());
						if (!writeMode)
							throw new InvokerException("can not assign member variables in read only mode");
						RH.setField(evaluate(o.getLeft(), objects), ((VariableNode) o.getRight()).getVarname(), r);
					} else
						throw new InvokerException("left side of assignment expression is invalid, should be target.fieldName=value.  For: " + n.getLeft());
				} else {
					VariableNode l = (VariableNode) n.getLeft();
					objects.put(l.getVarname(), r);
				}
			}
			return r;
		}
		if (".".equals(operation)) {
			return evaluatePeriod(n, objects);
		}
		if ("!".equals(operation)) {
			Object r = evaluate(n.getRight(), objects);
			return !(Boolean) r;
		}
		if (SH.endsWith(operation, '=') && !"==".equals(operation) && !"!=".equals(operation)) {
			VariableNode var = (VariableNode) n.getLeft();
			Number value = (Number) getOrThrow(Caster_Simple.OBJECT, objects, var.getVarname());
			Object r = PrimitiveMathManager.INSTANCE.eval(value, (Number) evaluate(n.getRight(), objects), operation.substring(0, operation.length() - 1));
			objects.put(var.getVarname(), r);
			return r;
		}
		if ("++".equals(operation)) {
			if (n.getLeft() != null) {
				VariableNode var = (VariableNode) n.getLeft();
				Number value = (Number) getOrThrow(Caster_Simple.OBJECT, objects, var.getVarname());
				Object r = PrimitiveMathManager.INSTANCE.eval(value, 1, "+");
				objects.put(var.getVarname(), r);
				return value;
			} else {
				VariableNode var = (VariableNode) n.getRight();
				Number value = (Number) getOrThrow(Caster_Simple.OBJECT, objects, var.getVarname());
				Object r = PrimitiveMathManager.INSTANCE.eval(value, 1, "+");
				objects.put(var.getVarname(), r);
				return r;
			}
		}
		if ("--".equals(operation)) {
			if (n.getLeft() != null) {
				VariableNode var = (VariableNode) n.getLeft();
				Number value = (Number) getOrThrow(Caster_Simple.OBJECT, objects, var.getVarname());
				Object r = PrimitiveMathManager.INSTANCE.eval(value, 1, "-");
				objects.put(var.getVarname(), r);
				return value;
			} else {
				VariableNode var = (VariableNode) n.getRight();
				Number value = (Number) getOrThrow(Caster_Simple.OBJECT, objects, var.getVarname());
				Object r = PrimitiveMathManager.INSTANCE.eval(value, 1, "-");
				objects.put(var.getVarname(), r);
				return r;
			}

		}
		if ("~".equals(operation)) {
			Object r = evaluate(n.getRight(), objects);
			if (r instanceof Long)
				return ~(Long) r;
			else
				return ~(Integer) r;
		}
		if (RH.IMPORT.equals(operation)) {
			String imprt = ((ConstNode) n.getRight()).getValue().toString();
			if (objects.getImports().indexOf(imprt) == -1) {
				objects.getImports().add(imprt);
				return true;
			} else
				return false;
		}
		if ("[".equals(operation)) {
			return Array.get(evaluate(n.getLeft(), objects), (Integer) evaluate(n.getRight(), objects));
		}
		Object l = evaluate(n.getLeft(), objects);
		if ("?".equals(operation)) {
			OperationNode on = (OperationNode) n.getRight();
			if (!":".equals(on.getOpString()))
				throw new RuntimeException("after ? expecting : operator");
			return ((Boolean) l) ? evaluate(on.getLeft(), objects) : evaluate(on.getRight(), objects);
		}
		if (RH.INSTANCEOF.equals(operation)) {
			if (l == null)
				return false;
			Package clazz = (Package) evaluate(n.getRight(), objects);
			Object clazz2 = findClass(clazz.name, objects.getImports());
			if (!(clazz2 instanceof Class))
				throw new InvokerException("right side of instanceof must be a class: " + l.getClass());

		}
		Object r = evaluate(n.getRight(), objects);
		if ((l instanceof String || r instanceof String) && "+".equals(operation)) {
			return new StringBuilder().append(l).append(r).toString();
		}
		if (l instanceof Boolean && r instanceof Boolean) {
			boolean bl = (Boolean) l;
			boolean br = (Boolean) r;
			if ("^".equals(operation))
				return bl ^ br;
			if ("^".equals(operation))
				return bl ^ br;
			if ("&&".equals(operation))
				return bl && br;
			if ("||".equals(operation))
				return bl || br;
			if ("==".equals(operation))
				return bl == br;
			if ("!=".equals(operation))
				return bl != br;
			if ("&".equals(operation))
				return bl & br;
			if ("|".equals(operation))
				return bl | br;
			else
				throw new RuntimeException("invalid binary operator: " + operation);
		}
		if (l instanceof Number && r instanceof Number && OH.isBoxed(l.getClass()) && OH.isBoxed(r.getClass())) {
			Number ln = (Number) l;
			Number rn = (Number) r;
			if (l instanceof Double || r instanceof Double)
				return PrimitiveMathManager.INSTANCE.eval(DoubleMath.INSTANCE, ln, rn, operation);
			if (l instanceof Float || r instanceof Float)
				return PrimitiveMathManager.INSTANCE.eval(FloatMath.INSTANCE, ln, rn, operation);
			if (l instanceof Long || r instanceof Long)
				return PrimitiveMathManager.INSTANCE.eval(LongMath.INSTANCE, ln, rn, operation);
			return PrimitiveMathManager.INSTANCE.eval(IntMath.INSTANCE, ln, rn, operation);
		}
		if ("+".equals(operation) && l instanceof String)
			return l.toString() + r;
		throw new RuntimeException("can not handle: " + n);
	}

	private <T> T getOrThrow(Caster<T> caster, ObjectScope objects, String varname) {
		Object r = objects.get(varname);
		if (r == null)
			throw new RuntimeException("variable not found: " + varname);
		return caster.cast(r);
	}

	protected Object evaluatePeriod(OperationNode n, ObjectScope objects) {
		Object l = evaluate(n.getLeft(), objects);
		if (n.getRight() instanceof VariableNode) {
			if (l instanceof Package) {
				return new Package(((Package) l).name + "." + ((VariableNode) n.getRight()).getVarname());
			}
			try {
				getReflectedField(l, ((VariableNode) n.getRight()).getVarname());
			} catch (RuntimeException e) {
				if (allowPartial)
					return new PartialOperation(l, ((VariableNode) n.getRight()).getVarname());
				throw e;
			}
		}
		if (n.getRight() instanceof MethodNode) {
			MethodNode mn = (MethodNode) n.getRight();
			Object[] params = mn.getParamsCount() == 0 ? null : new Object[mn.getParamsCount()];
			if (params != null) {
				for (int i = 0; i < params.length; i++) {
					params[i] = evaluate(mn.getParamAt(i), objects);
					if (params[i] instanceof Package) {
						String name = ((Package) params[i]).name;
						try {
							params[i] = findClass(name, objects.getImports());
						} catch (Exception e) {
							if (allowPartial)
								return new PartialOperation(null, name);
							throw new InvokerException("class or variable not found: " + name, e);
						}
					}
				}
			}
			if (l instanceof Package) {
				String name = ((Package) l).name;
				Object result;
				try {
					result = findClass(name, objects.getImports());
				} catch (Exception e) {
					throw new InvokerException("class or variable not found: " + name, e);
				}
				if (!writeMode)
					throw new InvokerException("can not invoke methods in read only mode");
				try {
					if (result instanceof Class)
						return RH.invokeStaticMethod((Class) result, mn.getMethodName(), params);
					return RH.invokeMethod(result, mn.getMethodName(), params == null ? OH.EMPTY_OBJECT_ARRAY : params);
				} catch (RuntimeException e) {
					if (allowPartial)
						return new PartialOperation(result, mn.getMethodName() + "(");
					throw e;
				}

			} else {
				if (!writeMode)
					throw new InvokerException("can not invoke methods in read only mode");
				try {
					return RH.invokeMethod(l, mn.getMethodName(), params == null ? OH.EMPTY_OBJECT_ARRAY : params);
				} catch (RuntimeException e) {
					if (allowPartial)
						return new PartialOperation(l, mn.getMethodName() + "(");
					throw e;
				}
			}
		}
		if (n.getRight() instanceof ConstNode) {
			if (RH.CLASS.equals(((ConstNode) n.getRight()).getValue())) {
				if (n.getLeft() instanceof VariableNode)
					return findClass(((Package) evaluate(n.getLeft(), objects)).name, objects.getImports());
				else
					throw new InvokerException("left side of .class expression must be a class name. At: " + n);
			}
			throw new InvokerException("right side of dereference expression (.) must be a method, variable or 'class'. At: " + n);
		}
		if (n.getRight() instanceof VariableNode) {
			return getReflectedField(l, ((VariableNode) n.getRight()).getVarname());
		}
		if (n.getRight() == null && allowPartial) {
			if (l instanceof Package) {
				String name = ((Package) l).name;
				Object result = findClass(name, objects.getImports());
				return new PartialOperation(result, "");
			} else
				return new PartialOperation(l, "");

		}
		if (allowPartial)
			return new PartialOperation(n, "");
		throw new RuntimeException("can not evaluate: " + n);

	}

	public static final class Package {
		final public String name;

		public Package(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return name;
		}
	}

	public Object findClass(String clazz, List<String> imports) {
		int end = clazz.length();
		for (;;) {
			Class r = findClass2(clazz.substring(0, end), imports);
			if (r == null) {
				end = clazz.lastIndexOf('.', end - 1);
				if (end == -1)
					throw new InvokerException("class not found: " + clazz);
			} else {
				if (end == clazz.length())
					return r;
				else
					return getField(r, clazz.substring(end + 1));
			}
		}
	}

	private Object getField(Class clazz, String field) {
		final String parts[] = SH.split('.', field);
		try {
			boolean isClass = true;
			Object o = clazz;
			for (int i = 0; i < parts.length; i++)
				if (isClass) {
					try {
						Field f = RH.findField((Class) o, parts[i]);
						f.setAccessible(true);
						isClass = false;
						o = f.get(null);
					} catch (RuntimeException e) {
						throw e;
					} catch (Exception e) {
						o = RH.getDeclaredClass((Class) o, parts[i]);
						isClass = true;
					}
				} else {
					o = getReflectedField(o, parts[i]);
				}
			return o;
		} catch (Exception e) {
			throw new InvokerException("error getting field: " + field + " for class " + clazz.getName(), e);
		}
	}

	protected Object getReflectedField(Object o, String name) {
		return RH.getField(o, name);
	}

	public Class findClass2(String clazz, List<String> imports) {
		if (clazz.indexOf('.') != -1)
			return RH.getClassNoThrow(clazz);
		String t = "." + clazz;
		for (String i : imports) {
			if (i.endsWith(t))
				return RH.getClassNoThrow(i);
			if ((i.endsWith(".*"))) {
				String t2 = i.substring(0, i.length() - 2) + t;
				Class r = RH.getClassNoThrow(t2);
				if (r != null)
					return r;
			}
		}
		return RH.getClassNoThrow(clazz);

	}

	public void setAllowPartial(boolean allowPartial) {
		this.allowPartial = allowPartial;
	}

	public boolean getAllowPartial() {
		return allowPartial;
	}

	public boolean isWriteMode() {
		return writeMode;
	}

	public static class MapBackedObjectScope implements ObjectScope {

		final private Map<String, Object> map;
		final private List<String> imports;

		@Override
		public List<String> getImports() {
			return imports;
		}

		public MapBackedObjectScope(Map<String, Object> map, List<String> imports) {
			this.map = map;
			this.imports = imports;

		}

		@Override
		public boolean containsKey(String key) {
			return map.containsKey(key);
		}

		@Override
		public Object get(String key) {
			return map.get(key);
		}

		@Override
		public Object put(String key, Object value) {
			return map.put(key, value);
		}

	}

	public interface ObjectScope {

		public boolean containsKey(String key);

		public Object get(String key);

		public Object put(String key, Object value);

		public List<String> getImports();
	}

	public interface Invoker {

		public Object visit(ObjectScope o);

	}
}
