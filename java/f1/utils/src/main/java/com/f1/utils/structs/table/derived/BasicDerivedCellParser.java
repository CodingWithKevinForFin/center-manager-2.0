package com.f1.utils.structs.table.derived;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.f1.base.CalcTypes;
import com.f1.base.NameSpaceCalcTypes;
import com.f1.base.NameSpaceIdentifier;
import com.f1.base.Pointer;
import com.f1.utils.BasicPointer;
import com.f1.utils.CH;
import com.f1.utils.OH;
import com.f1.utils.RH;
import com.f1.utils.SH;
import com.f1.utils.sql.DerivedCellCalculatorSql;
import com.f1.utils.sql.DerivedCellCalculatorSqlCast;
import com.f1.utils.sql.DerivedCellCalculatorSqlDeferred;
import com.f1.utils.sql.DerivedCellCalculator_SqlIn;
import com.f1.utils.sql.DerivedCellCalculator_SqlInSingle;
import com.f1.utils.sql.DerivedCellCalculator_SqlInnerSelect;
import com.f1.utils.sql.DerivedCellCalculator_SqlInnerSelectSingle;
import com.f1.utils.sql.SqlDerivedCellParser;
import com.f1.utils.sql.SqlProcessorUtils;
import com.f1.utils.string.ExpressionParser;
import com.f1.utils.string.ExpressionParserException;
import com.f1.utils.string.Node;
import com.f1.utils.string.node.AppendNode;
import com.f1.utils.string.node.ArrayNode;
import com.f1.utils.string.node.BlockNode;
import com.f1.utils.string.node.CastNode;
import com.f1.utils.string.node.CatchNode;
import com.f1.utils.string.node.ConstNode;
import com.f1.utils.string.node.DeclarationNode;
import com.f1.utils.string.node.DoWhileNode;
import com.f1.utils.string.node.ExpressionNode;
import com.f1.utils.string.node.ExternNode;
import com.f1.utils.string.node.FlowControlNode;
import com.f1.utils.string.node.ForEachNode;
import com.f1.utils.string.node.ForNode;
import com.f1.utils.string.node.GroupNode;
import com.f1.utils.string.node.IfElseNode;
import com.f1.utils.string.node.MapNode;
import com.f1.utils.string.node.MethodDeclarationNode;
import com.f1.utils.string.node.MethodNode;
import com.f1.utils.string.node.NewNode;
import com.f1.utils.string.node.OperationNode;
import com.f1.utils.string.node.StringTemplateNode;
import com.f1.utils.string.node.ThrowNode;
import com.f1.utils.string.node.VariableNode;
import com.f1.utils.string.node.WhileNode;
import com.f1.utils.string.sqlnode.SqlColumnsNode;
import com.f1.utils.string.sqlnode.SqlDeferredNode;
import com.f1.utils.string.sqlnode.SqlNode;
import com.f1.utils.string.sqlnode.UseNode;
import com.f1.utils.structs.table.derived.DerivedCellCalculatorBlock.CatchBlock;
import com.f1.utils.structs.table.stack.BasicCalcTypes;
import com.f1.utils.structs.table.stack.CalcTypesStack;
import com.f1.utils.structs.table.stack.CalcTypesTuple2;
import com.f1.utils.structs.table.stack.ChildCalcTypesStack;
import com.f1.utils.structs.table.stack.EmptyCalcTypes;
import com.f1.utils.structs.table.stack.SingletonCalcTypes;

public class BasicDerivedCellParser implements DerivedCellParser {
	public static final long DEFAULT_TIMEOUT = 10000L;

	private final boolean allowDelcarative = true;
	protected final ExpressionParser parser;
	protected final ExternFactoryManager externFactory;
	protected boolean optimize;

	public BasicDerivedCellParser(ExpressionParser parser) {
		this.parser = parser;
		this.externFactory = null;
		this.optimize = false;//TODO: this should change
	}

	public BasicDerivedCellParser(ExpressionParser parser, ExternFactoryManager externFactory, boolean optimize) {
		this.parser = parser;
		this.externFactory = externFactory;
		this.optimize = optimize;
	}

	@Override
	final public DerivedCellCalculator toCalc(CharSequence text, CalcTypesStack context) {
		if (text == null)
			return null;
		try {
			return toCalc(parser.parse(text), context);
		} catch (ExpressionParserException e) {
			if (e.getExpression() == null)
				e.setExpression(text.toString());
			throw e;
		}
	}
	@Override
	final public DerivedCellCalculator toCalcFromNode(Node node, CalcTypesStack context) {
		if (node == null)
			return null;
		return toCalc(node, context);
	}

	public DerivedCellCalculator toCalc(Node node, CalcTypesStack context) {
		if (node == null)
			return null;
		while (node instanceof ExpressionNode)
			node = ((ExpressionNode) node).getValue();
		try {
			return optimize(processNode(node, context));
		} catch (ExpressionParserException e) {
			throw e;
		} catch (Exception e) {
			throw new ExpressionParserException(node.getPosition(), "Error processing node of type " + node.getClass().getSimpleName() + ": " + node, e);
		}
	}

	public DerivedCellCalculator processNode(Node node, CalcTypesStack context) {
		final MethodFactoryManager mFactory = context.getFactory();
		switch (node.getNodeCode()) {
			case IfElseNode.CODE: {
				assertAllowsDeclarative(node);
				IfElseNode in = (IfElseNode) node;
				return new DerivedCellCalculatorIfElse(in.getPosition(), toCalc(in.getIfClause(), context), toCalcBlock(in.getIfBlock(), context),
						in.getElseBlock() == null ? null : toCalcBlock(in.getElseBlock(), context));
			}
			case BlockNode.CODE: {
				assertAllowsDeclarative(node);
				return blockToCalc((BlockNode) node, context);
			}
			case ForEachNode.CODE: {
				assertAllowsDeclarative(node);
				ForEachNode fn = (ForEachNode) node;
				final DerivedCellCalculatorAssignment precondition;
				final DeclarationNode dn = fn.getVar();
				if (dn.getNext() != null)
					throw new ExpressionParserException(dn.getPosition(), "Can not declare multiple variables in foreach(...)");
				final Class<?> type = forName(dn.getPosition(), mFactory, dn.getVartype());
				SingletonCalcTypes variables2 = new SingletonCalcTypes(dn.getVarname(), type);
				precondition = newDerivedCellCalculatorAssignment(dn.getPosition(), dn.getVarname(), type, new DerivedCellCalculatorConst(dn.getPosition(), null, type), true);
				CalcTypesStack context2 = new ChildCalcTypesStack(context, true, variables2);
				DerivedCellCalculator iterable = toCalc(fn.getArray(), context2);
				DerivedCellMemberMethod<Object> getIterator = mFactory.findMemberMethod(iterable.getReturnType(), "iterator", OH.EMPTY_CLASS_ARRAY);
				return new DerivedCellCalculatorForEach(fn.getPosition(), precondition, iterable, toCalcBlock(fn.getBlock(), context2), getIterator);
			}
			case ForNode.CODE: {
				assertAllowsDeclarative(node);
				ForNode fn = (ForNode) node;
				final DerivedCellCalculator precondition;
				Node preconditionnode = fn.getInits();
				while (preconditionnode instanceof ExpressionNode)
					preconditionnode = ((ExpressionNode) preconditionnode).getValue();
				if (preconditionnode instanceof DeclarationNode) {
					final DeclarationNode dn = (DeclarationNode) preconditionnode;
					final Class<?> type = forName(dn.getPosition(), mFactory, dn.getVartype());
					SingletonCalcTypes variables2 = new SingletonCalcTypes(dn.getVarname(), type);
					CalcTypesStack context2 = new ChildCalcTypesStack(context, true, variables2);
					final DerivedCellCalculator right = dn.getParam() == null ? new DerivedCellCalculatorConst(dn.getPosition(), null, type) : toCalc(dn.getParam(), context2);
					verifyAssignment(type, right, dn.getVarname(), mFactory);
					precondition = newDerivedCellCalculatorAssignment(dn.getPosition(), dn.getVarname(), type, right, true);
					return new DerivedCellCalculatorFor(fn.getPosition(), precondition, toCalc(fn.getConditions(), context2), toCalc(fn.getOps(), context2),
							toCalcBlock(fn.getBlock(), context2));
				}
				precondition = toCalc(preconditionnode, context);
				return new DerivedCellCalculatorFor(fn.getPosition(), precondition, toCalc(fn.getConditions(), context), toCalc(fn.getOps(), context),
						toCalc(fn.getBlock(), context));
			}
			case WhileNode.CODE: {
				assertAllowsDeclarative(node);
				WhileNode wn = (WhileNode) node;
				return new DerivedCellCalculatorWhile(wn.getPosition(), toCalc(wn.getConditions(), context), toCalcBlock(wn.getBlock(), context), false);
			}
			case DoWhileNode.CODE: {
				assertAllowsDeclarative(node);
				DoWhileNode wn = (DoWhileNode) node;
				return new DerivedCellCalculatorWhile(wn.getPosition(), toCalc(wn.getConditions(), context), toCalcBlock(wn.getBlock(), context), true);
			}
			case FlowControlNode.CODE: {
				assertAllowsDeclarative(node);
				final FlowControlNode fn = (FlowControlNode) node;
				if ("return".equalsIgnoreCase(fn.getStatement()))
					return new DerivedCellCalculatorFlowStatementReturn(node.getPosition(), toCalc(fn.getParam(), context));
				else if ("break".equalsIgnoreCase(fn.getStatement()))
					return new DerivedCellCalculatorFlowStatementBreak(node.getPosition(), toCalc(fn.getParam(), context));
				else if ("continue".equalsIgnoreCase(fn.getStatement()))
					return new DerivedCellCalculatorFlowStatementContinue(node.getPosition(), toCalc(fn.getParam(), context));
				else
					throw new ExpressionParserException(fn.getPosition(), "Unknown statement: " + fn.getStatement());
			}
			case ThrowNode.CODE: {
				ThrowNode tn = (ThrowNode) node;
				return new DerivedCellCalculatorFlowStatementThrow(tn.getPosition(), toCalc(tn.getNode(), context));
			}
			case NewNode.CODE: {
				final NewNode nn = (NewNode) node;
				DerivedCellCalculator[] params;
				if (nn.getParamsCount() == 0) {
					params = DerivedHelper.EMPTY_ARRAY;
				} else {
					params = new DerivedCellCalculator[nn.getParamsCount()];
					for (int i = 0; i < params.length; i++)
						params[i] = toCalc(nn.getParamAt(i), context);
				}
				DerivedCellCalculator[] dims;
				if (nn.getDimensionsCount() == 0) {
					dims = null;
				} else {
					dims = new DerivedCellCalculator[nn.getDimensionsCount()];
					for (int i = 0; i < dims.length; i++)
						dims[i] = toCalc(nn.getDimensionAt(i), context);
				}
				return new DerivedCellCalculatorNew(node.getPosition(), mFactory.getDefaultImplementation(forName(nn.getPosition(), mFactory, nn.getClassName())), params, dims,
						mFactory);
			}
			case DeclarationNode.CODE: {
				assertAllowsDeclarative(node);
				return blockToCalc(new BlockNode(node.getPosition(), CH.l(node), null, true, false), context);
			}
			case MethodDeclarationNode.CODE: {
				assertAllowsDeclarative(node);
				return blockToCalc(new BlockNode(node.getPosition(), CH.l(node), null, true, false), context);
			}
			case SqlNode.CODE: {
				assertAllowsDeclarative(node);
				return onSqlNode((SqlNode) node, context);
			}
			case SqlDeferredNode.CODE: {
				assertAllowsDeclarative(node);
				DerivedCellCalculatorStringTemplate t = (DerivedCellCalculatorStringTemplate) processNode(((SqlDeferredNode) node).getInner(), context);
				return new DerivedCellCalculatorSqlDeferred(t, (SqlDerivedCellParser) this);
			}
			case VariableNode.CODE: {
				String varname = ((VariableNode) node).getVarname();
				return getRef(node.getPosition(), varname, context);
			}
			case StringTemplateNode.CODE: {
				int size = 0;
				for (StringTemplateNode n = (StringTemplateNode) node; n != null; n = n.getNext())
					size++;
				DerivedCellCalculator[] params = new DerivedCellCalculator[size];
				char[] escapes = new char[size];
				int pos = 0;
				for (StringTemplateNode n = (StringTemplateNode) node; n != null; n = n.getNext()) {
					escapes[pos] = n.getQuoteChar();
					params[pos++] = optimize(processNode(n.getInjectionNode(), context));
				}
				return new DerivedCellCalculatorStringTemplate(params, escapes, ((StringTemplateNode) node).getIsNested());
			}
			case OperationNode.CODE: {
				return processOperationNode((OperationNode) node, context);
			}
			case ConstNode.CODE: {
				ConstNode cn = (ConstNode) node;
				return new DerivedCellCalculatorConst(node.getPosition(), cn.getValue());
			}
			case MethodNode.CODE: {
				return processMethod((MethodNode) node, context);
			}
			case CastNode.CODE: {
				CastNode cn = (CastNode) node;
				Class type = forName(cn.getPosition(), context.getFactory(), cn.getCastTo());
				return new DerivedCellCalculatorCast(node.getPosition(), type, toCalc(cn.getParam(), context), context.getFactory().getCaster(type));
			}
			case AppendNode.CODE: {
				return new DerivedCellCalculatorAppend(node.getPosition(), ((AppendNode) node).getText(), ((AppendNode) node).getQuoteChar());
			}
			case ExternNode.CODE: {
				ExternNode en = (ExternNode) node;
				Extern extern = externFactory == null ? null : externFactory.getExternNoThrow(en.getLanguageName().getVarname());
				if (extern == null)
					throw new ExpressionParserException(en.getLanguageName().getPosition(), "Extern language not supported: " + en.getLanguageName().getVarname());
				ExternCompiled compiled = extern.compile(context, en.getCodePosition(), en.getCode());
				return new DerivedCellCalculatorExtern(en.getCodePosition(), en.getLanguageName().getVarname(), en.getBracketsCount(), en.getCode(), compiled);
			}
			case ExpressionNode.CODE: {
				return processNode(((ExpressionNode) node).getValue(), context);
			}
			case ArrayNode.CODE: {
				return processArray((ArrayNode) node, context);
			}
			case MapNode.CODE: {
				return processMapNode((MapNode) node, context);
			}
			default:
				throw new ExpressionParserException(node.getPosition(), "Syntax Not supported: " + SH.stripSuffix(OH.getSimpleClassName(node), "Node", false));
		}
	}

	private DerivedCellCalculatorMap processMapNode(MapNode node, CalcTypesStack context) {
		int count = node.getInnerNodesCount();
		DerivedCellCalculator[] params = new DerivedCellCalculator[count];
		for (int i = 0; i < count; i++)
			params[i] = processNode(node.getInnerNode(i), context);
		return new DerivedCellCalculatorMap(node.getPosition(), params);
	}

	private DerivedCellCalculator processArray(ArrayNode node, CalcTypesStack context) {
		final DerivedCellCalculator[] params = new DerivedCellCalculator[node.getParamsCount()];
		for (int i = 0; i < params.length; i++)
			params[i] = processNode(node.getParamAt(i), context);
		return new DerivedCellCalculatorArray(node.getPosition(), params);
	}

	protected DerivedCellCalculator onSqlNode(SqlNode sn, CalcTypesStack context) {
		throw new ExpressionParserException(sn.getPosition(), "SQL not supported");
	}

	private void assertAllowsDeclarative(Node node) {
		if (!allowDelcarative)
			throw new ExpressionParserException(node.getPosition(), "Not supported: " + node);
	}
	//	protected DerivedCellCalculator onConcurrentNode(SqlConcurrentBlockNode exe, DerivedCellCalculator[] inners, MethodFactoryManager dfactory, CalcTypes variables) {
	//		return new DerivedCellCalculatorBlock(exe.getPosition(), inners, dfactory, variables, catchClauses, false, true);
	//	}

	protected DerivedCellCalculator processOperationNode(OperationNode on, CalcTypesStack context) {
		final MethodFactoryManager mFactory = context.getFactory();
		switch (on.getOp()) {
			case OperationNode.OP_MINUS_MINUS:
			case OperationNode.OP_PLUS_PLUS: {
				if ((on.getLeft() == null) == (on.getRight() == null))
					throw new ExpressionParserException(on.getPosition(), "Operator " + on.getOpString() + " expecting only left or only right expression");
				boolean returnAfterChange = on.getLeft() == null;
				Node node = returnAfterChange ? on.getRight() : on.getLeft();
				if (node instanceof OperationNode) {
					OperationNode t2 = (OperationNode) node;
					if (t2.getOp() != OperationNode.OP_SBRACKET)
						throw new ExpressionParserException(on.getPosition(), "Left side of declaration must be a variable: " + node);
					DerivedCellCalculator deref = toCalc(t2.getLeft(), context);
					DerivedCellCalculator t = deref;
					while (t instanceof DerivedCellCalculatorArrayDeref)
						t = t.getInnerCalcAt(0);
					if (!(t instanceof DerivedCellCalculatorWithDependencies))
						throw new ExpressionParserException(on.getPosition(), "Left side of declaration must be a variable");
					DerivedCellCalculator offset = toCalc(t2.getRight(), context);
					DerivedCellCalculator right = toCalc(on.getRight(), context);
					return new DerivedCellCalculatorAssignmentIncWithDeref(on.getPosition(), deref, offset, on.getOp() == OperationNode.OP_PLUS_PLUS, true);
				}
				String varname = ((VariableNode) node).getVarname();
				Class type = getTypeNoConst(context, varname, on.getPosition());
				if (type != null)
					return new DerivedCellCalculatorAssignmentInc(on.getPosition(), varname, type, on.getOp() == OperationNode.OP_PLUS_PLUS, returnAfterChange);
				DerivedCellCalculator r = determineVariableType(on.getPosition(), varname, context);
				if (r != null)
					return r;
				throw new ExpressionParserException(on.getPosition(), "Unknown variable: " + varname);
			}
			case OperationNode.OP_PERIOD: {
				if (on.getLeft() != null && on.getRight() instanceof MethodNode) {
					MethodNode mn = (MethodNode) on.getRight();
					DerivedCellCalculator[] params;
					if (mn.getParamsCount() == 0) {
						params = DerivedHelper.EMPTY_ARRAY;
					} else {
						params = new DerivedCellCalculator[mn.getParamsCount()];
						for (int i = 0; i < params.length; i++)
							params[i] = toCalc(mn.getParamAt(i), context);
					}
					return new DerivedCellCalculatorMemberMethod(on.getPosition(), processNode(on.getLeft(), context), mn.getMethodName(), params, mFactory);
				} else if (on.getLeft() != null && on.getRight() instanceof VariableNode && RH.LENGTH.equals(((VariableNode) on.getRight()).getVarname())) {
					DerivedCellCalculator leftVal = toCalc(on.getLeft(), context);
					return new DerivedCellCalculatorArrayLength(on.getPosition(), leftVal);
				} else if (on.getLeft() instanceof VariableNode && on.getRight() instanceof VariableNode) {
					CalcTypes v = context.getFrame();
					if (v instanceof NameSpaceCalcTypes) {
						NameSpaceCalcTypes nsm = (NameSpaceCalcTypes) v;
						String namespaceName = (String) on.getLeft().toString();
						String varname = (String) on.getRight().toString();
						NameSpaceIdentifier nsi = new NameSpaceIdentifier(namespaceName, varname);
						Class<?> type = nsm.getType(nsi);
						if (type != null)
							return newDerivedCellCalculatorRef(on.getPosition(), type, nsi);
						DerivedCellCalculator r = determineVariableType(on.getPosition(), varname, context);
						if (r != null)
							return r;
						throw new ExpressionParserException(on.getPosition(), "Unknown namespaced variable: " + nsi);
					} else {
						String varname = on.getLeft() + "." + on.getRight();
						return getRef(on.getPosition(), varname, context);
					}
				}
			}
			case OperationNode.OP_EQ: {
				if (on.getLeft() != null && on.getRight() != null) {
					if (on.getLeft() instanceof OperationNode) {
						OperationNode t2 = (OperationNode) on.getLeft();
						if (t2.getOp() != OperationNode.OP_SBRACKET)
							throw new ExpressionParserException(on.getPosition(), "Left side of declaration must be a variable: " + on.getLeft());
						DerivedCellCalculator deref = toCalc(t2.getLeft(), context);
						DerivedCellCalculator t = deref;
						while (t instanceof DerivedCellCalculatorArrayDeref)
							t = t.getInnerCalcAt(0);
						if (!(t instanceof DerivedCellCalculatorWithDependencies))
							throw new ExpressionParserException(on.getPosition(), "Left side of declaration must be a variable");
						DerivedCellCalculator offset = toCalc(t2.getRight(), context);
						DerivedCellCalculator right = toCalc(on.getRight(), context);
						return new DerivedCellCalculatorAssignmentWithDeref(on.getPosition(), deref, offset, right);
					}
					if (!(on.getLeft() instanceof VariableNode))
						throw new ExpressionParserException(on.getPosition(), "Left side of declaration must be a variable: " + on.getLeft());
					String varname = ((VariableNode) on.getLeft()).getVarname();
					Class type = getTypeNoConst(context, varname, on.getPosition());
					DerivedCellCalculator right = toCalc(on.getRight(), context);
					if (type == null) {
						if (varname.startsWith("$"))
							return new DerivedCellCalculatorAssignment(on.getPosition(), varname, String.class, right, true);
						throw new ExpressionParserException(on.getPosition(), "Unknown variable: " + varname);
					}
					right = autoCast(right, type, varname, mFactory);
					return newDerivedCellCalculatorAssignment(on.getPosition(), varname, type, right, false);
				}
				break;
			}
			case OperationNode.OP_STAR_EQ:
			case OperationNode.OP_PLUS_EQ:
			case OperationNode.OP_MINUS_EQ:
			case OperationNode.OP_SLASH_EQ:
			case OperationNode.OP_HAT_EQ:
			case OperationNode.OP_PIPE_EQ:
			case OperationNode.OP_AMP_EQ:
			case OperationNode.OP_PERCENT_EQ: {
				if (on.getLeft() instanceof OperationNode) {
					OperationNode t2 = (OperationNode) on.getLeft();
					if (t2.getOp() != OperationNode.OP_SBRACKET)
						throw new ExpressionParserException(on.getPosition(), "Left side of declaration must be a variable: " + on.getLeft());
					DerivedCellCalculator deref = toCalc(t2.getLeft(), context);
					DerivedCellCalculator t = deref;
					while (t instanceof DerivedCellCalculatorArrayDeref)
						t = t.getInnerCalcAt(0);
					if (!(t instanceof DerivedCellCalculatorWithDependencies))
						throw new ExpressionParserException(on.getPosition(), "Left side of declaration must be a variable");
					DerivedCellCalculator offset = toCalc(t2.getRight(), context);
					DerivedCellCalculator getter = toCalc(on.getLeft(), context);
					DerivedCellCalculator right = DerivedCellCalculatorMath.valueOf(on.getPosition(), getSubOpCode(on.getOp()), getter, toCalc(on.getRight(), context));
					return new DerivedCellCalculatorAssignmentWithDeref(on.getPosition(), deref, offset, right);
				}
				String varname = ((VariableNode) on.getLeft()).getVarname();
				Class type = getTypeNoConst(context, varname, on.getPosition());
				if (type == null) {
					DerivedCellCalculator r = determineVariableType(on.getPosition(), varname, context);
					if (r != null)
						return r;
					throw new ExpressionParserException(on.getPosition(), "Unknown variable: " + varname);
				}
				DerivedCellCalculator getter = newDerivedCellCalculatorRef(on.getPosition(), type, varname);
				DerivedCellCalculator right = DerivedCellCalculatorMath.valueOf(on.getPosition(), getSubOpCode(on.getOp()), getter, toCalc(on.getRight(), context));
				verifyAssignment(type, right, varname, mFactory);
				return newDerivedCellCalculatorAssignment(on.getPosition(), varname, type, right, false);
			}
			case OperationNode.OP_IN: {
				DerivedCellCalculator[] lefts;
				Node right = on.getRight();
				boolean not = on.getLeft() instanceof OperationNode && ((OperationNode) on.getLeft()).getOp() == OperationNode.OP_NOT;
				if (not)
					on = (OperationNode) on.getLeft();
				if (on.getLeft() instanceof GroupNode) {
					GroupNode gn = (GroupNode) on.getLeft();
					lefts = new DerivedCellCalculator[gn.getParamsCount()];
					for (int i = 0; i < lefts.length; i++)
						lefts[i] = toCalc(gn.getParamAt(i), context);
				} else {
					lefts = new DerivedCellCalculator[] { toCalc(on.getLeft(), context) };
				}
				right = reduce(right);
				DerivedCellCalculator r;
				if (lefts.length == 1) {
					DerivedCellCalculator left = lefts[0];
					if (right instanceof SqlColumnsNode) {
						DerivedCellCalculatorSql inner = (DerivedCellCalculatorSql) toCalc(right, context);
						r = new DerivedCellCalculator_SqlInnerSelectSingle(right.getPosition(), left, inner);
					} else if (right instanceof UseNode) {
						DerivedCellCalculatorSql inner = (DerivedCellCalculatorSql) toCalc(right, context);
						r = new DerivedCellCalculator_SqlInnerSelectSingle(right.getPosition(), left, inner);
					} else {
						Pointer<Class> leftType = new BasicPointer<Class>(left.getReturnType());
						DerivedCellCalculator[] values = toTableSingle(right, context, leftType);
						r = new DerivedCellCalculator_SqlInSingle(on.getPosition(), left, values, leftType.get());
					}
				} else {
					if (right instanceof SqlColumnsNode) {
						//						SqlColumnsNode sql = (SqlColumnsNode) right;
						DerivedCellCalculatorSql inner = (DerivedCellCalculatorSql) toCalc(right, context);
						//						if (sql.getOperation() == SqlExpressionParser.ID_SELECT) {
						//							DerivedCellCalculatorSql inner = new DerivedCellCalculatorSql(sql, (SqlDerivedCellParser) this);
						//						inner.setIsInnerQuery(true);
						r = new DerivedCellCalculator_SqlInnerSelect(right.getPosition(), lefts, inner);
						//						} else
						//							throw new ExpressionParserException(right.getPosition(), "Expecting SELECT clause for in statement");
					} else {
						Class[] leftTypes = new Class[lefts.length];
						for (int i = 0; i < lefts.length; i++)
							leftTypes[i] = lefts[i].getReturnType();
						DerivedCellCalculator[][] values = toTable(right, context, leftTypes);
						r = new DerivedCellCalculator_SqlIn(on.getPosition(), lefts, values, leftTypes);
					}
				}
				if (not)
					r = new DerivedCellCalculatorNot(on.getPosition(), r);
				return r;
			}
			case OperationNode.OP_QMARK: {
				if (!(on.getRight() instanceof OperationNode))
					throw new ExpressionParserException(on.getPosition(), "missing ':' after '?' clause");
				OperationNode options = (OperationNode) on.getRight();
				if (options.getRight() == null || options.getLeft() == null)
					throw new ExpressionParserException(options.getPosition(), "expecting true/false expression before/after ':' clause");
				return new DerivedCellCalculatorConditional(toCalc(on.getLeft(), context), toCalc(options.getLeft(), context), toCalc(options.getRight(), context));
			}
			case OperationNode.OP_BANG: {
				if (on.getLeft() != null || on.getRight() == null)
					throw new ExpressionParserException(on.getPosition(), "Operator " + on.getOpString() + " expecting left and right expression");
				return new DerivedCellCalculatorNot(on.getPosition(), toCalc(on.getRight(), context));
			}
			case OperationNode.OP_NOT: {
				if (on.getLeft() == null || on.getRight() == null)
					throw new ExpressionParserException(on.getPosition(), "Operator " + on.getOpString() + " expecting left and right expression");
				return new DerivedCellCalculatorNot(on.getPosition(), toCalc(on.getRight(), context));
			}
			case OperationNode.OP_MINUS: {
				if (on.getLeft() != null) {
					if (on.getRight() != null)
						break;
					else
						throw new ExpressionParserException(on.getPosition(), "Operator " + on.getOpString() + " expecting left and right expression");
				}
				return new DerivedCellCalculatorNegate(on.getPosition(), toCalc(on.getRight(), context));
			}
			case OperationNode.OP_SBRACKET: {
				if (on.getLeft() == null || on.getRight() == null)
					throw new ExpressionParserException(on.getPosition(), "Operator " + on.getOpString() + " expecting left and right expression");
				DerivedCellCalculator leftVal = toCalc(on.getLeft(), context);
				DerivedCellCalculator rightVal = toCalc(on.getRight(), context);
				if (leftVal instanceof DerivedCellCalculatorCast) {//Makes sure casting derefs is proper, Ex: (String)a[123] is evaled as (String)(a[123]) not ((String)a)[123]
					DerivedCellCalculatorCast cast = (DerivedCellCalculatorCast) leftVal;
					return new DerivedCellCalculatorCast(cast.getPosition(), cast.getReturnType(), new DerivedCellCalculatorArrayDeref(on.getPosition(), cast.getRight(), rightVal),
							cast.getCaster());
				}
				return new DerivedCellCalculatorArrayDeref(on.getPosition(), leftVal, rightVal);
			}
			case OperationNode.OP_PIPE_PIPE: {
				if (on.getLeft() == null || on.getRight() == null)
					throw new ExpressionParserException(on.getPosition(), "Operator " + on.getOpString() + " expecting left and right expression");
				int size = 2;
				Node t = on.getLeft();
				while (t instanceof OperationNode && ((OperationNode) t).getOp() == OperationNode.OP_PIPE_PIPE) {
					size++;
					t = ((OperationNode) t).getLeft();
				}
				if (size == 2)
					return DerivedCellCalculatorMath.valueOf(on.getPosition(), on.getOp(), toCalc(on.getLeft(), context), toCalc(on.getRight(), context));
				DerivedCellCalculator[] r = new DerivedCellCalculator[size];
				int pos = size - 1;
				r[pos--] = toCalc(on.getRight(), context);
				t = on.getLeft();
				while (t instanceof OperationNode && ((OperationNode) t).getOp() == OperationNode.OP_PIPE_PIPE) {
					OperationNode o = (OperationNode) t;
					r[pos--] = toCalc(o.getRight(), context);
					t = o.getLeft();
				}
				r[pos--] = toCalc(t, context);
				return DerivedCellCalculatorOr.valueOf(on.getPosition(), r);
			}
		}
		//		if (on.getLeft() == null || on.getRight() == null)
		//			throw new ExpressionParserException(on.getPosition(), "'" + on.getOperation() + "' operator requires two parameters");
		//		if (isOr(on)) {
		//		}
		//		if (SH.equalsIgnoreCase("IN", on.getOperation())) {
		//			DerivedCellCalculator[] lefts;
		//			Node right = on.getRight();
		//			boolean not = on.getLeft() instanceof OperationNode && "not".equalsIgnoreCase(((OperationNode) on.getLeft()).getOperation());
		//			if (not)
		//				on = (OperationNode) on.getLeft();
		//			if (on.getLeft() instanceof GroupNode) {
		//				GroupNode gn = (GroupNode) on.getLeft();
		//				lefts = new DerivedCellCalculator[gn.getParamsCount()];
		//				for (int i = 0; i < lefts.length; i++)
		//					lefts[i] = toCalc(gn.getParamAt(i), context);
		//			} else {
		//				lefts = new DerivedCellCalculator[] { toCalc(on.getLeft(), context) };
		//			}
		//			right = reduce(right);
		//			DerivedCellCalculator r;
		//			if (lefts.length == 1) {
		//				DerivedCellCalculator left = lefts[0];
		//				if (right instanceof SqlColumnsNode) {
		//					SqlColumnsNode sql = (SqlColumnsNode) right;
		//					if (sql.getOperation() == SqlExpressionParser.ID_SELECT) {
		//						DerivedCellCalculatorSql inner = new DerivedCellCalculatorSql(sql, (SqlDerivedCellParser) this);
		//						inner.setIsInnerQuery(true);
		//						r = new DerivedCellCalculator_SqlInnerSelectSingle(right.getPosition(), left, inner);
		//					} else
		//						throw new ExpressionParserException(right.getPosition(), "Expecting SELECT clause for in statement");
		//				} else {
		//					Pointer<Class> leftType = new BasicPointer<Class>(left.getReturnType());
		//					DerivedCellCalculator[] values = toTableSingle(right, context, leftType);
		//					r = new DerivedCellCalculator_SqlInSingle(on.getPosition(), left, values, leftType.get());
		//				}
		//			} else {
		//				if (right instanceof SqlColumnsNode) {
		//					SqlColumnsNode sql = (SqlColumnsNode) right;
		//					if (sql.getOperation() == SqlExpressionParser.ID_SELECT) {
		//						DerivedCellCalculatorSql inner = new DerivedCellCalculatorSql(sql, (SqlDerivedCellParser) this);
		//						inner.setIsInnerQuery(true);
		//						r = new DerivedCellCalculator_SqlInnerSelect(right.getPosition(), lefts, inner);
		//					} else
		//						throw new ExpressionParserException(right.getPosition(), "Expecting SELECT clause for in statement");
		//				} else {
		//					Class[] leftTypes = new Class[lefts.length];
		//					for (int i = 0; i < lefts.length; i++)
		//						leftTypes[i] = lefts[i].getReturnType();
		//					DerivedCellCalculator[][] values = toTable(right, context, leftTypes);
		//					r = new DerivedCellCalculator_SqlIn(on.getPosition(), lefts, values, leftTypes);
		//				}
		//			}
		//			if (not)
		//				r = new DerivedCellCalculatorNot(on.getPosition(), r);
		//			return r;
		//		}
		return DerivedCellCalculatorMath.valueOf(on.getPosition(), on.getOp(), toCalc(on.getLeft(), context), toCalc(on.getRight(), context));
	}
	private byte getSubOpCode(byte op) {
		switch (op) {
			case OperationNode.OP_STAR_EQ:
				return OperationNode.OP_STAR;
			case OperationNode.OP_PLUS_EQ:
				return OperationNode.OP_PLUS;
			case OperationNode.OP_MINUS_EQ:
				return OperationNode.OP_MINUS;
			case OperationNode.OP_SLASH_EQ:
				return OperationNode.OP_SLASH;
			case OperationNode.OP_HAT_EQ:
				return OperationNode.OP_HAT;
			case OperationNode.OP_PIPE_EQ:
				return OperationNode.OP_PIPE;
			case OperationNode.OP_AMP_EQ:
				return OperationNode.OP_AMP;
			case OperationNode.OP_PERCENT_EQ:
				return OperationNode.OP_PERCENT;
			default:
				throw new RuntimeException("" + op);
		}
	}

	private DerivedCellCalculator toCalcBlock(Node node, CalcTypesStack context) {
		final DerivedCellCalculator calc = toCalc(node, context);
		return (calc instanceof DerivedCellCalculatorBlock) ? calc : new DerivedCellCalculatorBlockSingleton(calc);

	}
	public DerivedCellCalculator autoCast(DerivedCellCalculator right, Class type, String varname, MethodFactoryManager mFactory) {
		if (right instanceof DerivedCellCalculatorSql) {
			//			((DerivedCellCalculatorSql) right).setIsInnerQuery(true);
			return new DerivedCellCalculatorSqlCast(right.getPosition(), type, right, mFactory.getCaster(type));
		} else if (right instanceof DerivedCellCalculatorSqlDeferred) {
			//			((DerivedCellCalculatorSqlDeferred) right).setIsInnerQuery(true);
			return new DerivedCellCalculatorSqlCast(right.getPosition(), type, right, mFactory.getCaster(type));
		}
		if (type == null && varname.startsWith("$"))
			type = String.class;
		if (right.getReturnType() == null)
			throw new ExpressionParserException(right.getPosition(), "Right hand side of assignment must return a value");
		if (type != String.class && !OH.isCoercable(type, right.getReturnType())) {
			if (!type.isAssignableFrom(right.getReturnType())) {
				right = new DerivedCellCalculatorCast(right.getPosition(), type, right, mFactory.getCaster(type));
			}
		} else
			verifyAssignment(type, right, varname, mFactory);
		return right;
	}

	private DerivedCellCalculatorBlock blockToCalc(BlockNode bn, CalcTypesStack context) {
		//		com.f1.base.Types variablesOrig = context.getGlobalVarTypes();
		MethodFactoryManager factory = context.getFactory();
		final List<DerivedCellCalculator> calcs = new ArrayList<DerivedCellCalculator>(bn.getNodesCount());
		final int position = bn.getPosition();
		com.f1.utils.structs.table.stack.BasicCalcTypes lcvs = null;
		com.f1.base.CalcTypes variables = EmptyCalcTypes.INSTANCE;
		BasicMethodFactory dfactory = null;
		List<DeclaredMethodFactory> declaredMethods = null;
		for (int i = 0; i < bn.getNodesCount(); i++) {
			Node innerNode = bn.getNodeAt(i);
			if (innerNode instanceof MethodDeclarationNode) {
				final MethodDeclarationNode dn = (MethodDeclarationNode) innerNode;
				final Class[] params = new Class[dn.getParamsCount()];
				final String[] names = new String[dn.getParamsCount()];
				final Set<String> types = new HashSet<String>();
				for (int k = 0, l = dn.getParamsCount(); k < l; k++) {
					final DeclarationNode decn = dn.getParamAt(k);
					final Class<?> type = forName(decn.getPosition(), factory, decn.getVartype());
					if (!types.add(decn.getVarname()))
						throw new ExpressionParserException(dn.getPosition(), "Method " + dn.getMethodName() + " has duplicate argument: " + decn.getVarname());
					names[k] = decn.getVarname();
					params[k] = type;
				}
				if (dfactory == null) {
					dfactory = new BasicMethodFactory();
					declaredMethods = new ArrayList<DeclaredMethodFactory>();
				}

				if (dn.isVirtual() && factory.getFactoryForVirtuals() != null) {
					MethodFactory vreplace = factory.getFactoryForVirtuals().getMethodFactory(dn.getMethodName(), params);
					if (vreplace != null) {
						dfactory.addFactory(vreplace);
						declaredMethods.add(null);
						continue;
					}
				}
				MethodFactory existing = dfactory.getMethodFactory(dn.getMethodName(), params);
				if (existing != null)
					throw new ExpressionParserException(dn.getPosition(), "Duplicate Method Declaration");
				DeclaredMethodFactory dmf = newDeclaredMethodFactory(forName(dn.getPosition(), factory, dn.getReturnType()), dn.getMethodName(), names, params, dn.getModifiers());
				declaredMethods.add(dmf);
				dfactory.addFactory(dmf);
			}
		}
		MethodFactoryManager mFactory;
		if (dfactory != null) {
			dfactory.addFactoryManager(factory);
			mFactory = dfactory;
		} else
			mFactory = factory;
		int declaredMethodIndex = 0;
		for (int i = 0; i < bn.getNodesCount(); i++) {
			Node innerNode = bn.getNodeAt(i);
			while (innerNode instanceof ExpressionNode)
				innerNode = ((ExpressionNode) innerNode).getValue();
			if (innerNode instanceof DeclarationNode) {
				if (lcvs == null) {
					lcvs = new com.f1.utils.structs.table.stack.BasicCalcTypes();
					variables = new CalcTypesTuple2(lcvs, variables);
				}
				CalcTypesStack dcpc = new ChildCalcTypesStack(context, true, variables, mFactory);
				processDeclarationNode(dcpc, (List) calcs, lcvs, (DeclarationNode) innerNode);
			} else if (innerNode instanceof MethodDeclarationNode) {
				final MethodDeclarationNode dn = (MethodDeclarationNode) innerNode;
				DeclaredMethodFactory dmf = declaredMethods.get(declaredMethodIndex++);
				if (dmf == null)//this means it was virtual
					continue;
				com.f1.base.CalcTypes types = dmf.getDefinition().getParamTypesMapping();
				CalcTypesTuple2 types2 = new CalcTypesTuple2(types, variables);
				DerivedCellCalculator calc = toCalc(dn.getBody(), new ChildCalcTypesStack(context, false, types2, mFactory));
				dmf.setInner(dn.geBodytText(), dn.getBodytStart(), dn.getBodytEnd(), calc);

			} else {
				DerivedCellCalculator t = toCalc(innerNode, new ChildCalcTypesStack(context, true, variables, mFactory));
				if (t instanceof DerivedCellCalculatorAssignment) {
					DerivedCellCalculatorAssignment assignment = (DerivedCellCalculatorAssignment) t;
					if (assignment.getVariableName().startsWith("$") && (lcvs == null || lcvs.getType(assignment.getVariableName()) == null)) {
						if (lcvs == null) {
							lcvs = new com.f1.utils.structs.table.stack.BasicCalcTypes();
							variables = new CalcTypesTuple2(lcvs, variables);
						}
						lcvs.putType(assignment.getVariableName(), assignment.getReturnType());
					}

				}
				if (t != null)
					calcs.add(t);
			}
		}
		String catchVarName = null;
		Class catchVarType = null;
		DerivedCellCalculatorBlock catchBlock = null;
		CatchBlock[] cbs = null;
		if (bn.getCatchNodesCount() > 0) {
			cbs = new CatchBlock[bn.getCatchNodesCount()];
			for (int i = 0; i < bn.getCatchNodesCount(); i++) {
				CatchNode cn = bn.getCatchNodeAt(i);
				catchVarName = cn.getVar().getVarname();
				catchVarType = forName(cn.getType().getPosition(), mFactory, cn.getType().getVarname());
				final BasicCalcTypes types = new BasicCalcTypes();
				types.putType(catchVarName, catchVarType);
				//				com.f1.utils.TypesTuple2 types2 = new com.f1.utils.TypesTuple2(types, variablesOrig);
				catchBlock = blockToCalc(cn.getNode(), new ChildCalcTypesStack(context, true, types, mFactory));
				cbs[i] = new CatchBlock(catchVarName, catchVarType, catchBlock);
			}
		}
		return new DerivedCellCalculatorBlock(position, calcs.toArray(new DerivedCellCalculator[calcs.size()]), mFactory, variables, cbs, bn.isImplicit(), bn.isConcurrent());
	}
	protected DeclaredMethodFactory newDeclaredMethodFactory(Class<?> returnType, String methodName, String[] argumentNames, Class[] argumentTypes, byte modifiers) {
		return new DeclaredMethodFactory(returnType, methodName, argumentNames, argumentTypes, modifiers);
	}
	public void processDeclarationNode(CalcTypesStack context, List<DerivedCellCalculatorAssignment> calcs, com.f1.utils.structs.table.stack.BasicCalcTypes lcvs,
			DeclarationNode innerNode) {
		for (DeclarationNode dn = (DeclarationNode) innerNode; dn != null; dn = dn.getNext()) {
			final Class<?> type = forName(dn.getPosition(), context.getFactory(), dn.getVartype());
			final DerivedCellCalculator right;
			Node p = dn.getParam();
			while (p instanceof ExpressionNode)
				p = ((ExpressionNode) p).getValue();
			if (p == null || p instanceof VariableNode)
				right = new DerivedCellCalculatorConst(dn.getPosition(), null, type);
			else if (p instanceof OperationNode) {
				Node right2 = ((OperationNode) p).getRight();
				if (right2 == null)
					throw new ExpressionParserException(p.getPosition(), "Expression missing after operator");
				right = autoCast(toCalc(right2, context), type, dn.getVarname(), context.getFactory());
			} else
				right = toCalc(p, context);

			if (lcvs.getType(dn.getVarname()) != null)
				throw new ExpressionParserException(dn.getPosition(), "Duplicate variable declaration: " + dn.getVarname());
			lcvs.putType(dn.getVarname(), type);
			verifyAssignment(type, right, dn.getVarname(), context.getFactory());
			if (type != right.getReturnType()) {//coercision or casting is required, for instances: String x = new map(1,2);
				DerivedCellCalculator toCast = new DerivedCellCalculatorCast(right.getPosition(), type, right, context.getFactory().getCaster(type));
				calcs.add(newDerivedCellCalculatorAssignment(dn.getPosition(), dn.getVarname(), type, toCast, true));
			} else
				calcs.add(newDerivedCellCalculatorAssignment(dn.getPosition(), dn.getVarname(), type, right, true));
		}
	}
	private DerivedCellCalculatorAssignment newDerivedCellCalculatorAssignment(int position, String varname, Class<?> type, DerivedCellCalculator right, boolean isDeclaration) {
		if (varname.startsWith("$")) {
			if (type == null)
				type = String.class;
			else if (type != String.class)
				throw new ExpressionParserException(position, "Variables starting wiht dollar sign($) must be of type String");
		}
		if (type == null)
			throw new ExpressionParserException(position, "Unknown variable: " + varname);
		return new DerivedCellCalculatorAssignment(position, varname, type, right, isDeclaration);
	}
	public static void verifyAssignment(Class<?> type, DerivedCellCalculator right, String varname, MethodFactoryManager mFactory) {
		if (type == null)
			throw new ExpressionParserException(right.getPosition(),
					"Illegal Assignment for '" + varname + "', can not cast " + mFactory.forType(right.getReturnType()) + " to " + mFactory.forType(type));
		if (type != String.class && !OH.isCoercable(type, right.getReturnType()))
			throw new ExpressionParserException(right.getPosition(),
					"Illegal Assignment for '" + varname + "', can not cast " + mFactory.forType(right.getReturnType()) + " to " + mFactory.forType(type));

	}

	public static Class<?> getTypeNoConst(CalcTypesStack c, String varname, int i) {
		for (;;) {
			Class<?> r = c.getFrame().getType(varname);
			if (r != null)
				return r;
			if (c.getFrameConsts().getType(varname) != null)
				throw new ExpressionParserException(i, "Can not assign to constant: " + varname);
			if (!c.isParentVisible())
				break;
			CalcTypesStack c2 = c.getParent();
			if (c2 == null)
				break;
			c = c2;
		}
		Class<?> r = c.getGlobal().getType(varname);
		if (r != null)
			return r;
		if (c.getGlobalConsts().getType(varname) != null)
			throw new ExpressionParserException(i, "Can not assign to constant: " + varname);
		return null;
	}
	private DerivedCellCalculator[][] toTable(Node node, CalcTypesStack context, Class[] typesSink) {
		DerivedCellCalculator o[][];
		node = reduce(node);
		if (node instanceof GroupNode) {
			GroupNode gn = (GroupNode) node;
			//			List<Node> gn = ((GroupNode) node).params;
			o = new DerivedCellCalculator[gn.getParamsCount()][];
			boolean hasGroups = false;
			for (int i = 0; i < o.length; i++) {
				Node n = reduce(gn.getParamAt(i));
				DerivedCellCalculator row[];
				if (n instanceof GroupNode) {
					hasGroups = true;
					GroupNode gn2 = (GroupNode) n;
					row = new DerivedCellCalculator[gn2.getParamsCount()];
					for (int j = 0; j < row.length; j++)
						row[j] = toConst(reduce(gn2.getParamAt(j)), context);
				} else {
					row = new DerivedCellCalculator[] { toConst(n, context) };
				}
				if (hasGroups && row.length != typesSink.length)
					throw new ExpressionParserException(n.getPosition(), "Expecting " + typesSink.length + " columns, not " + row.length);
				o[i] = row;
			}
			if (!hasGroups && typesSink.length > 1) {
				if (o.length != typesSink.length)
					throw new ExpressionParserException(node.getPosition(), "Expecting " + typesSink.length + " columns, not " + o.length);
				DerivedCellCalculator[][] o2 = o;
				o = new DerivedCellCalculator[1][o2.length];
				for (int i = 0; i < typesSink.length; i++)
					o[0][i] = o2[i][0];
			}
		} else {
			DerivedCellCalculator[] row = new DerivedCellCalculator[] { toConst(node, context) };
			if (row.length != typesSink.length)
				throw new ExpressionParserException(node.getPosition(), "Expecting " + typesSink.length + " columns, not " + row.length);
			o = new DerivedCellCalculator[][] { row };
		}
		for (int i = 0; i < typesSink.length; i++) {
			Class t = typesSink[i];
			for (int j = 0; j < o.length; j++)
				t = SqlProcessorUtils.getWidest(t, o[j][i].getReturnType());
			typesSink[i] = t;
		}
		return o;
	}
	static private Node reduce(Node n) {
		while (n instanceof ExpressionNode)
			n = ((ExpressionNode) n).getValue();
		return n;
	}
	private DerivedCellCalculator toConst(Node node, CalcTypesStack context) {
		DerivedCellCalculator calc = toCalc(node, context);
		return calc;
	}
	private DerivedCellCalculator[] toTableSingle(Node node, CalcTypesStack context, Pointer<Class> typesSink) {
		DerivedCellCalculator o[];
		node = reduce(node);
		if (node instanceof GroupNode) {
			GroupNode gn = (GroupNode) node;
			o = new DerivedCellCalculator[gn.getParamsCount()];
			for (int i = 0; i < o.length; i++) {
				Node n = reduce(gn.getParamAt(i));
				DerivedCellCalculator row;
				if (n instanceof GroupNode) {
					GroupNode gn2 = (GroupNode) n;
					if (gn2.getParamsCount() != 1)
						throw new ExpressionParserException(n.getPosition(), "Expecting 1 column, not " + gn2.getParamsCount());
					row = toConst(reduce(gn2.getParamAt(0)), context);
				} else {
					row = toConst(n, context);
				}
				o[i] = row;
			}
		} else {
			o = new DerivedCellCalculator[] { toConst(node, context) };
		}
		Class t = typesSink.get();
		for (int j = 0; j < o.length; j++)
			t = SqlProcessorUtils.getWidest(t, o[j].getReturnType());
		typesSink.put(t);
		return o;
	}
	protected DerivedCellCalculator optimize(DerivedCellCalculator r) {
		if (!optimize)
			return r;
		return DerivedHelper.reduceConst(r);
	}
	protected DerivedCellCalculator getRef(int position, String varname, CalcTypesStack context) {
		CalcTypesStack c = context;
		for (;;) {
			Class<?> r = c.getFrame().getType(varname);
			if (r != null)
				return newDerivedCellCalculatorRef(position, r, varname);
			r = c.getFrameConsts().getType(varname);
			if (r != null)
				return new DerivedCellCalculatorConst(position, c.getFrameConsts().getValue(varname), r);
			if (!c.isParentVisible())
				break;
			CalcTypesStack c2 = c.getParent();
			if (c2 == null)
				break;
			c = c2;
		}
		Class<?> r = c.getGlobal().getType(varname);
		if (r != null) {
			return new DerivedCellCalculatorRefGlobal(position, r, varname);
		}
		r = c.getGlobalConsts().getType(varname);
		if (r != null)
			return new DerivedCellCalculatorConst(position, c.getGlobalConsts().getValue(varname), r);
		DerivedCellCalculator dcc = determineVariableType(position, varname, context);
		if (dcc != null)
			return dcc;
		throw new ExpressionParserException(position, "Unknown variable: " + varname);
	}

	protected DerivedCellCalculator determineVariableType(int position, String varnameNoTicks, CalcTypesStack context) {
		if (varnameNoTicks.startsWith("$")) {
			return newDerivedCellCalculatorRef(position, String.class, varnameNoTicks);
		}
		return null;
	}
	public DerivedCellCalculatorRef newDerivedCellCalculatorRef(int position, Class type, String varname) {
		return new DerivedCellCalculatorRef(position, type, varname);
	}
	public DerivedCellCalculator newDerivedCellCalculatorRef(int position, Class type, NameSpaceIdentifier varname) {
		return new DerivedCellCalculatorRef(position, type, varname);
	}

	public static Class forName(int position, MethodFactoryManager mFactory, String name) {
		int dimensions = 0;
		while (name.endsWith("[]")) {
			name = name.substring(0, name.length() - 2);
			dimensions++;
		}
		final Class<?> r;
		try {
			r = mFactory.forName(name);
		} catch (ClassNotFoundException e) {
			throw new ExpressionParserException(position, "Class not found: " + name, e);
		}
		if (dimensions == 0) {
			return r;
		} else
			return Array.newInstance(r, new int[dimensions]).getClass();
	}
	protected DerivedCellCalculator processMethod(MethodNode mn, CalcTypesStack context) {
		DerivedCellCalculator[] calcs = new DerivedCellCalculator[mn.getParamsCount()];
		if (context.getFactory() == null)
			throw new ExpressionParserException(mn.getPosition(), "Function not supported: " + mn.getMethodName());
		//		variables = mFactory.getVariables(mn.getPosition(), mn.methodName, variables);
		for (int i = 0; i < calcs.length; i++)
			calcs[i] = toCalc(mn.getParamAt(i), context);
		DerivedCellCalculator method = context.getFactory().toMethod(mn.getPosition(), mn.getMethodName(), calcs, context);
		if (method == null) {
			StringBuilder sb = new StringBuilder();
			sb.append("No such method: ");
			sb.append(mn.getMethodName()).append("(");
			for (int i = 0; i < calcs.length; i++) {
				if (i > 0)
					sb.append(',');
				sb.append(context.getFactory().forType(calcs[i].getReturnType()));
			}
			sb.append(")");
			throw new ExpressionParserException(mn.getPosition(), sb.toString());
		}
		return method;

	}

	public ExpressionParser getExpressionParser() {
		return parser;
	}
}
