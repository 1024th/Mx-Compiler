package ir;

import java.util.HashMap;
import java.util.logging.Logger;

import ast.*;
import ast.expr.*;
import ast.stmt.*;
import ir.constant.GlobalVariable;
import ir.constant.IntConst;
import ir.constant.NullptrConst;
import ir.constant.StringConst;
import ir.inst.AllocaInst;
import ir.inst.*;
import ir.structure.BasicBlock;
import ir.structure.Function;
import ir.structure.Module;
import ir.type.*;
import utils.error.IRBuildError;
import utils.scope.*;

public class IRBuilder implements ASTVisitor {
  public GlobalScope gScope;
  private Scope curScope;
  private Function curFunc;
  private BasicBlock curBlock;
  public ClassScope clsScope;
  public Module module = new Module();

  Logger logger = Logger.getLogger("IRBuilder");

  public IRBuilder(GlobalScope gScope) {
    this.gScope = gScope;
  }

  @Override
  public void visit(ProgramNode node) {
    for (var i : node.defs) {
      if (i instanceof ClassDefNode) {
        declareClass((ClassDefNode) i);
      }
      if (i instanceof VarDefNode) {
        for (var j : node.defs)
          declareGlobalVar((SingleVarDefNode) j);
      }
    }
    for (var i : node.defs) {
      if (i instanceof ClassDefNode)
        declareMemberFunc((ClassDefNode) i);
      if (i instanceof FuncDefNode)
        declareFunc((FuncDefNode) i);
    }
    // TODO Auto-generated method stub
    for (var i : node.defs) {
      if (i instanceof ClassDefNode)
        i.accept(this);
    }
    for (var i : node.defs) {
      if (i instanceof VarDefNode)
        i.accept(this);
    }
    for (var i : node.defs) {
      if (i instanceof FuncDefNode)
        i.accept(this);
    }
  }

  @Override
  public void visit(ClassDefNode node) {
    // TODO Auto-generated method stub
  }

  @Override
  public void visit(ClassCtorDefNode node) {
    // TODO Auto-generated method stub
  }

  @Override
  public void visit(FuncDefNode node) {
    // TODO Auto-generated method stub
    // TODO member function
    curFunc = gScope.getFunc(node.funcName);
    curFunc.entryBlock = new BasicBlock("entry", curFunc);
    curFunc.exitBlock = new BasicBlock("exit", curFunc);
    curScope = node.scope;
    var funcType = curFunc.type();
    curBlock = curFunc.entryBlock;
    if (funcType.retType instanceof VoidType) {
      newRet();
    } else {
      curFunc.retValPtr = newAlloca(funcType.retType, "%.retval.addr");
      newRet(newLoad("%.retval", curFunc.retValPtr, curFunc.exitBlock));
    }
    for (int i = 0; i < node.params.params.size(); ++i) {
      var paramNode = node.params.params.get(i);
      var type = funcType.paramTypes.get(i);
      var ptr = newAlloca(type, "%" + paramNode.name + ".addr");
      curScope.addVar(paramNode.name, ptr);
      var val = new Value(type, paramNode.name);
      curFunc.addArg(val);
      newStore(val, ptr);
    }
    node.body.accept(this);
    if (!curBlock.terminated)
      new BrInst(curFunc.exitBlock, curBlock);
    curScope = curScope.parent;
  }

  @Override
  public void visit(VarDefNode node) {
    // TODO Auto-generated method stub
  }

  @Override
  public void visit(SingleVarDefNode node) {
    // TODO Auto-generated method stub
  }

  @Override
  public void visit(ForStmtNode node) {
    // TODO Auto-generated method stub
  }

  @Override
  public void visit(IfStmtNode node) {
    // TODO Auto-generated method stub
  }

  @Override
  public void visit(WhileStmtNode node) {
    // TODO Auto-generated method stub
  }

  @Override
  public void visit(BreakStmtNode node) {
    // TODO Auto-generated method stub
  }

  @Override
  public void visit(ContinueStmtNode node) {
    // TODO Auto-generated method stub
  }

  @Override
  public void visit(ReturnStmtNode node) {
    // TODO Auto-generated method stub
  }

  @Override
  public void visit(ExprStmtNode node) {
    // TODO Auto-generated method stub
  }

  @Override
  public void visit(SuiteNode node) {
    // TODO Auto-generated method stub
    curScope = node.scope;
    for (var i : node.stmts)
      i.accept(this);
    curScope = curScope.parent;
  }

  @Override
  public void visit(TypeNode node) {
    // TODO Auto-generated method stub
  }

  @Override
  public void visit(AssignExprNode node) {
    // TODO Auto-generated method stub
    node.rhs.accept(this);
    node.lhs.accept(this);
    newStore(getValue(node.rhs), node.lhs.ptr);
  }

  @Override
  public void visit(AtomExprNode node) {
    // TODO Auto-generated method stub
    if (node.isFunc) {
      node.val = gScope.getFunc(node.text);
    } else if (node.isLeftVal) { // identifier
      // TODO: class
      node.ptr = gScope.getVar(node.text, true);
    } else { // literal -> ir constant data
      var typename = node.type.typename;
      if (typename.equals("bool")) {
        node.val = new IntConst(node.text.equals("true") ? 1 : 0, 1);
      } else if (typename.equals("int")) {
        node.val = new IntConst(Integer.parseInt(node.text), 32);
      } else if (typename.equals("null")) {
        node.val = new NullptrConst();
      } else if (typename.equals("string")) {
        node.val = new StringConst(node.unescape());
      } else {
        throw new IRBuildError("unknown type when visiting AtomExprNode");
      }
    }
  }

  @Override
  public void visit(BinaryExprNode node) {
    // TODO Auto-generated method stub
  }

  @Override
  public void visit(FuncCallExprNode node) {
    // TODO Auto-generated method stub
  }

  @Override
  public void visit(ParamListNode node) {
    // TODO Auto-generated method stub
  }

  @Override
  public void visit(IndexExprNode node) {
    // TODO Auto-generated method stub
  }

  @Override
  public void visit(MemberExprNode node) {
    // TODO Auto-generated method stub
  }

  @Override
  public void visit(NewExprNode node) {
    // TODO Auto-generated method stub
  }

  @Override
  public void visit(PostfixExprNode node) {
    // TODO Auto-generated method stub
  }

  @Override
  public void visit(PrefixExprNode node) {
    // TODO Auto-generated method stub
  }

  @Override
  public void visit(UnaryExprNode node) {
    // TODO Auto-generated method stub
  }

  @Override
  public void visit(LambdaExprNode node) {
    // TODO Auto-generated method stub
  }

  @Override
  public void visit(ParamNode node) {
    // TODO Auto-generated method stub
  }

  private void declareClass(ClassDefNode node) {
    StructType cls = new StructType("%class." + node.className);
    for (var i : node.defs) {
      if (i instanceof VarDefNode) {
        cls.typeList.add(getType(((VarDefNode) i).type));
      }
    }
    module.classes.add(cls);
    gScope.addClassType(cls);
  }

  private void declareMemberFunc(ClassDefNode node) {
    var cls = gScope.getClassType(node.className);
    for (var i : node.defs) {
      if (i instanceof FuncDefNode) {
        var funcDef = (FuncDefNode) i;
        var funcType = new FuncType(getType(funcDef.returnType));
        funcType.paramTypes.add(new PointerType(cls)); // "this" pointer
        for (var j : funcDef.params.params) {
          funcType.paramTypes.add(getType(j.type));
        }
        var func = new Function(funcType, "@%s.%s".formatted(node.className, funcDef.funcName));
        module.funcs.add(func);
        // TODO
        // gScope.addFunc(funcDef, func);
      }
    }
  }

  private void declareFunc(FuncDefNode node) {
    var funcType = new FuncType(getType(node.returnType));
    for (var j : node.params.params) {
      funcType.paramTypes.add(getType(j.type));
    }
    var func = new Function(funcType, "@" + node.funcName);
    module.funcs.add(func);
    gScope.addFunc(node.funcName, func);
  }

  private void declareGlobalVar(SingleVarDefNode node) {
    var name = "@" + node.name;
    var v = new GlobalVariable(getType(node.type), name);
    if (node.initExpr != null) {
      // TODO
      // v.initVal = ;
    }
    module.globalVars.add(v);
    gScope.globalVars.put(name, v);
  }

  private static BaseType getElemType(String typename) {
    if (typename.equals("int")) {
      return new IntType(32);
    } else if (typename.equals("bool")) {
      return new IntType(1);
    } else if (typename.equals("string")) {
      return new PointerType(new IntType(8));
    } else if (typename.equals("void")) {
      return new VoidType();
    } else if (typename.equals("null")) {
      // Note: the ir type of nullptr constant may be incorrect!
      // When null is used, the correct type is inferred from other operand.
      return new PointerType(new IntType(32));
    } else {
      throw new IRBuildError("unknown elementary type");
    }
  }

  private BaseType getType(TypeNode type) {
    if (type.isArrayType) {
      if (type.dimension == 1)
        return new PointerType(getElemType(type.typename));
      return new PointerType(getType(type));
    }
    if (type.isClass) {
      return new PointerType(gScope.getClassType(type.typename));
    }
    return getElemType(type.typename);
  }

  private Value getValue(ExprNode node) {
    if (node.val != null)
      return node.val;
    return newLoad(nextName(), node.ptr);
  }

  private HashMap<String, Integer> identifiers = new HashMap<>();
  private int cntName = 0; // for unnamed values

  private String nextName() {
    return "%" + cntName++;
  }

  private String rename(String rawName) {
    var cnt = identifiers.get(rawName);
    String name;
    if (cnt == null) {
      name = rawName;
      cnt = 1;
    } else {
      name = rawName + '.' + cnt;
      cnt += 1;
    }
    identifiers.put(rawName, cnt);
    return name;
  }

  private boolean isBool(BaseType type) {
    return type instanceof IntType && ((IntType) type).bitWidth == 1;
  }

  private AllocaInst newAlloca(BaseType type, String name) {
    // bool (i1), alloca i8
    if (isBool(type))
      type = new IntType(8);
    return new AllocaInst(type, rename(name), curFunc.entryBlock);
  }

  private Value newLoad(String name, Value ptr, BasicBlock parent) {
    var loadInst = new LoadInst(rename(name), ptr, parent);
    if (isBool(((PointerType) ptr.type).elemType)) {
      return newTrunc(loadInst, new IntType(1), rename(name + ".tobool"));
    }
    return loadInst;
  }

  private Value newLoad(String name, Value ptr) {
    return newLoad(rename(name), ptr, curBlock);
  }

  private StoreInst newStore(Value val, Value ptr) {
    return new StoreInst(val, ptr, curBlock);
  }

  private RetInst newRet(Value val) {
    return new RetInst(val, curFunc.exitBlock);
  }

  /** Return void */
  private RetInst newRet() {
    return new RetInst(curFunc.exitBlock);
  }

  private TruncInst newTrunc(Value val, BaseType toType, String name) {
    return new TruncInst(val, toType, rename(name), curBlock);
  }
}
