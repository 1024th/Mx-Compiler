package ir;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

import ast.*;
import ast.expr.*;
import ast.stmt.*;
import ir.constant.*;
import ir.inst.*;
import ir.type.*;
import utils.error.IRBuildError;
import utils.scope.*;

public class IRBuilder implements ASTVisitor {
  public GlobalScope gScope;
  private Scope curScope;
  private Function curFunc;
  private BasicBlock curBlock;

  // for class
  private ClassScope clsScope;
  private String clsName;
  private StructType clsType;

  public Module module = new Module();

  Logger logger = Logger.getLogger("IRBuilder");

  public IRBuilder(GlobalScope gScope) {
    this.gScope = gScope;
    addBuiltin();
    curScope = gScope;
  }

  @Override
  public void visit(ProgramNode node) {
    // declarations
    for (var i : node.defs) {
      if (i instanceof ClassDefNode)
        declareClassType((ClassDefNode) i);
    }
    for (var i : node.defs) {
      if (i instanceof ClassDefNode)
        defineClassType((ClassDefNode) i);
      if (i instanceof VarDefNode)
        for (var j : ((VarDefNode) i).vars)
          declareGlobalVar((SingleVarDefNode) j);
    }
    for (var i : node.defs) {
      if (i instanceof ClassDefNode)
        declareMemberFunc((ClassDefNode) i);
      if (i instanceof FuncDefNode)
        declareFunc((FuncDefNode) i);
    }

    // definitions
    initGlobalVar(node);

    for (var i : node.defs) {
      if (i instanceof ClassDefNode)
        i.accept(this);
    }
    for (var i : node.defs) {
      if (i instanceof FuncDefNode)
        i.accept(this);
    }
  }

  @Override
  public void visit(ClassDefNode node) {
    clsName = node.className;
    clsScope = gScope.getClassScope(clsName);
    clsType = gScope.getClassType(clsName);
    curScope = clsScope;
    boolean hasCtor = false;
    for (var i : node.defs) {
      if (i instanceof VarDefNode)
        continue;
      if (i instanceof ClassCtorDefNode)
        hasCtor = true;
      i.accept(this);
    }

    if (!hasCtor) {
      // add definition of default constructor
      curFunc = clsScope.getFunc(node.className);
      curFunc.entryBlock = newBlock("entry");
      curFunc.exitBlock = newBlock("exit");
      // curScope = new FuncScope(null, false, curScope);
      curBlock = curFunc.exitBlock;
      newRet(); // return void
      curBlock = curFunc.entryBlock;
      if (curFunc.isMember) { // add "this" pointer
        var thisType = new PointerType(clsType);
        // var ptr = newAlloca(thisType, "%this.addr");
        // curScope.addVar("this", ptr);
        var val = new Value(thisType, rename("%this"));
        curFunc.addArg(val);
        // newStore(val, ptr);
      }
      if (!curBlock.terminated)
        new BrInst(curFunc.exitBlock, curBlock);
      // curScope = curScope.parent;
    }

    curScope = curScope.parent;
    exitClass();
  }

  private void exitClass() {
    clsName = null;
    clsScope = null;
    clsType = null;
  }

  @Override
  public void visit(ClassCtorDefNode node) {
    // TODO Auto-generated method stub
    curFunc = clsScope.getFunc(node.name);
    curFunc.entryBlock = newBlock("entry");
    curFunc.exitBlock = newBlock("exit");
    curScope = node.scope;
    curBlock = curFunc.exitBlock;
    newRet(); // return void
    curBlock = curFunc.entryBlock;
    if (curFunc.isMember) { // add "this" pointer
      var thisType = new PointerType(clsType);
      var ptr = newAlloca(thisType, "%this.addr");
      curScope.addVar("this", ptr);
      var val = new Value(thisType, rename("%this"));
      curFunc.addArg(val);
      newStore(val, ptr);
    }
    node.body.accept(this);
    if (!curBlock.terminated)
      new BrInst(curFunc.exitBlock, curBlock);
    curScope = curScope.parent;
  }

  @Override
  public void visit(FuncDefNode node) {
    if (clsScope != null)
      curFunc = clsScope.getFunc(node.funcName);
    else
      curFunc = gScope.getFunc(node.funcName);
    curFunc.entryBlock = newBlock("entry");
    curFunc.exitBlock = newBlock("exit");
    curScope = node.scope;
    curBlock = curFunc.exitBlock;
    var funcType = curFunc.type();
    if (funcType.retType instanceof VoidType) {
      newRet();
    } else {
      curFunc.retValPtr = newAlloca(funcType.retType, "%.retval.addr");
      newRet(newLoad("%.retval", curFunc.retValPtr, curFunc.exitBlock));
    }
    curBlock = curFunc.entryBlock;
    int offset = 0;
    if (curFunc.isMember) { // add "this" pointer
      offset = 1;
      var thisType = new PointerType(clsType);
      var ptr = newAlloca(thisType, "%this.addr");
      curScope.addVar("this", ptr);
      var val = new Value(thisType, rename("%this"));
      curFunc.addArg(val);
      newStore(val, ptr);
    }
    for (int i = 0; i < node.params.params.size(); ++i) {
      var paramNode = node.params.params.get(i);
      var type = funcType.paramTypes.get(i + offset);
      var ptr = newAlloca(type, "%" + paramNode.name + ".addr");
      curScope.addVar(paramNode.name, ptr);
      var val = new Value(type, rename("%" + paramNode.name));
      curFunc.addArg(val);
      newStore(val, ptr);
    }
    if (node.funcName.equals("main")) {
      new CallInst(nextName(), gScope.getFunc("__global_var_init"), curBlock);
    }
    node.body.accept(this);
    if (!curBlock.terminated)
      new BrInst(curFunc.exitBlock, curBlock);
    curScope = curScope.parent;
  }

  @Override
  public void visit(VarDefNode node) {
    for (var i : node.vars) {
      i.accept(this);
    }
  }

  @Override
  public void visit(SingleVarDefNode node) {
    // TODO Auto-generated method stub
    var ptr = newAlloca(getType(node.type), "%" + node.name + ".addr");
    if (node.initExpr != null) {
      node.initExpr.accept(this);
      newStore(getValue(node.initExpr), ptr);
    }
    curScope.addVar(node.name, ptr);
  }

  @Override
  public void visit(ForStmtNode node) {
    // TODO Auto-generated method stub
    var condBlock = newBlock("for.cond");
    var bodyBlock = newBlock("for.body");
    var incBlock = newBlock("for.inc");
    var endBlock = newBlock("for.end");
    node.scope.continueBlock = incBlock;
    node.scope.breakBlock = endBlock;
    curScope = node.scope;
    if (node.initVar != null) {
      node.initVar.accept(this);
    }
    if (node.initExpr != null) {
      node.initExpr.accept(this);
    }
    new BrInst(condBlock, curBlock);
    curBlock = condBlock;
    if (node.condition != null) {
      node.condition.accept(this);
      new BrInst(getValue(node.condition), bodyBlock, endBlock, curBlock);
    } else {
      new BrInst(bodyBlock, curBlock);
    }

    curBlock = bodyBlock;
    node.body.accept(this);
    new BrInst(incBlock, curBlock);

    curBlock = incBlock;
    if (node.increase != null) {
      node.increase.accept(this);
    }
    new BrInst(condBlock, curBlock);

    curBlock = endBlock;
    curScope = curScope.parent;
  }

  @Override
  public void visit(IfStmtNode node) {
    // TODO Auto-generated method stub
    var thenBlock = newBlock("if.then");
    var elseBlock = newBlock("if.else");
    var endBlock = newBlock("if.end");

    node.condition.accept(this);
    new BrInst(getValue(node.condition), thenBlock, elseBlock, curBlock);

    curBlock = thenBlock;
    curScope = node.thenScope;
    node.thenStmt.accept(this);
    new BrInst(endBlock, curBlock);
    curScope = curScope.parent;

    curBlock = elseBlock;
    if (node.elseStmt != null) {
      curScope = node.elseScope;
      node.elseStmt.accept(this);
      curScope = curScope.parent;
    }
    new BrInst(endBlock, curBlock);

    curBlock = endBlock;
  }

  @Override
  public void visit(WhileStmtNode node) {
    // TODO Auto-generated method stub
    var condBlock = newBlock("while.cond");
    var bodyBlock = newBlock("while.body");
    var endBlock = newBlock("while.end");
    node.scope.continueBlock = condBlock;
    node.scope.breakBlock = endBlock;
    curScope = node.scope;

    new BrInst(condBlock, curBlock);
    curBlock = condBlock;
    node.condition.accept(this);
    new BrInst(getValue(node.condition), bodyBlock, endBlock, curBlock);

    curBlock = bodyBlock;
    node.body.accept(this);
    new BrInst(condBlock, curBlock);

    curBlock = endBlock;
    curScope = curScope.parent;
  }

  @Override
  public void visit(BreakStmtNode node) {
    // TODO Auto-generated method stub
    new BrInst(curScope.getLoopScope().breakBlock, curBlock);
  }

  @Override
  public void visit(ContinueStmtNode node) {
    // TODO Auto-generated method stub
    new BrInst(curScope.getLoopScope().continueBlock, curBlock);
  }

  @Override
  public void visit(ReturnStmtNode node) {
    if (node.expr != null) {
      node.expr.accept(this);
      var val = getValue(node.expr);
      newStore(val, curFunc.retValPtr);
    }
    new BrInst(curFunc.exitBlock, curBlock);
  }

  @Override
  public void visit(ExprStmtNode node) {
    if (node.expr != null) {
      node.expr.accept(this);
    }
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
    // Not used
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
      if (clsScope != null) {
        node.val = clsScope.getFunc(node.text);
      }
      if (node.val == null) {
        node.val = gScope.getFunc(node.text);
      }
    } else if (node.isLeftVal) { // identifier
      node.ptr = curScope.getVar(node.text, true);
      if (node.ptr == null) { // member variable
        var thisPtr = newLoad("%this", curScope.getVar("this", true));
        var index = clsScope.getVarIndex(node.text);
        var type = clsType.typeList.get(index);
        node.ptr = new GetElementPtrInst(rename("%" + node.text),
            new PointerType(type), thisPtr, curBlock,
            new IntConst(0), new IntConst(index));
      }
      // node.val = newLoad("%" + node.text, node.ptr);
    } else if (node.text.equals("this")) {
      node.ptr = curScope.getVar("this", true);
    } else { // literal -> ir constant data
      var typename = node.type.typename;
      if (typename.equals("bool")) {
        node.val = node.text.equals("true") ? trueConst : falseConst;
      } else if (typename.equals("int")) {
        node.val = new IntConst(Integer.parseInt(node.text), 32);
      } else if (typename.equals("null")) {
        node.val = new NullptrConst();
      } else if (typename.equals("string")) {
        var s = getStringConst(node.unescape());
        node.val = new GetElementPtrInst(nextName(), i8PtrType, s,
            curBlock, new IntConst(0), new IntConst(0));
      } else {
        throw new IRBuildError("unknown type when visiting AtomExprNode@%s".formatted(node.pos));
      }
    }
  }

  @Override
  public void visit(BinaryExprNode node) {
    // TODO Auto-generated method stub
    node.lhs.accept(this);

    // short-circuit evaluation for '&&' and '||'
    if (node.op.equals("&&")) {
      // a && b
      // bool tmp;
      // if (a) tmp = b;
      // else tmp = 0;
      node.ptr = newAlloca(i1Type, "%land.addr");
      var rhsBlock = newBlock("land.rhs");
      var shortBlock = newBlock("land.short");
      var endBlock = newBlock("land.end");
      new BrInst(getValue(node.lhs), rhsBlock, shortBlock, curBlock);

      curBlock = rhsBlock;
      node.rhs.accept(this);
      newStore(getValue(node.rhs), node.ptr);
      new BrInst(endBlock, curBlock);
      curBlock = shortBlock;
      newStore(falseConst, node.ptr);
      new BrInst(endBlock, curBlock);
      curBlock = endBlock;
      return;
    } else if (node.op.equals("||")) {
      // a || b
      // bool tmp;
      // if (a) tmp = 1;
      // else tmp = b;
      node.ptr = newAlloca(i1Type, "%lor.addr");
      var rhsBlock = newBlock("lor.rhs");
      var shortBlock = newBlock("lor.short");
      var endBlock = newBlock("lor.end");
      new BrInst(getValue(node.lhs), shortBlock, rhsBlock, curBlock);

      curBlock = shortBlock;
      newStore(trueConst, node.ptr);
      new BrInst(endBlock, curBlock);
      curBlock = rhsBlock;
      node.rhs.accept(this);
      newStore(getValue(node.rhs), node.ptr);
      new BrInst(endBlock, curBlock);
      curBlock = endBlock;
      return;
    }

    node.rhs.accept(this);
    if (node.lhs.type.isString()) {
      node.val = new CallInst(
          nextName(), getStrMethod(node.op), curBlock,
          getValue(node.lhs), getValue(node.rhs));
      return;
    }

    // @formatter:off
    String op = null;
    switch (node.op) {
      case "==": op = "eq";  break;
      case "!=": op = "ne";  break;
      case ">":  op = "sgt"; break;
      case ">=": op = "sge"; break;
      case "<":  op = "slt"; break;
      case "<=": op = "sle"; break;
    }
    // @formatter:on
    if (op != null) {
      node.val = new IcmpInst(op, getValue(node.lhs), getValue(node.rhs), nextName(), curBlock);
      return;
    }

    // @formatter:off
    switch (node.op) {
      case "+": op = "add"; break;
      case "-": op = "sub"; break;
      case "*": op = "mul"; break;
      case "/": op = "sdiv"; break;
      case "%": op = "srem"; break;
      case "&": op = "and"; break;
      case "|": op = "or";  break;
      case "^": op = "xor"; break;
      case "<<": op = "shl"; break;
      case ">>": op = "ashr"; break;
    }
    // @formatter:on
    node.val = newBinary(op, getValue(node.lhs), getValue(node.rhs), "%" + op);
  }

  @Override
  public void visit(FuncCallExprNode node) {
    node.function.accept(this);
    if (!(node.function.val instanceof Function)) {
      // .size() function is already inlined in visit(MemberExprNode)
      node.val = node.function.val;
      return;
    }
    var func = (Function) node.function.val;
    var paramTypes = func.type().paramTypes;
    var args = new ArrayList<Value>();
    int offset = 0;
    if (func.isMember) { // add "this" pointer
      offset = 1;
      if (node.function instanceof MemberExprNode) {
        args.add(getValue(((MemberExprNode) node.function).instance));
      } else {
        // in a member function, "this" is omitted
        var arg_val = newLoad("%this", curScope.getVar("this", true));
        args.add(arg_val);
      }
    }
    for (int i = 0; i < node.args.size(); ++i) {
      var arg = node.args.get(i);
      arg.accept(this);
      var arg_val = getValue(arg);
      arg_val.type = paramTypes.get(i + offset); // null as argument
      args.add(arg_val);
    }
    node.val = new CallInst(nextName(), func, curBlock, args);
  }

  @Override
  public void visit(ParamListNode node) {
    // TODO Auto-generated method stub
  }

  @Override
  public void visit(IndexExprNode node) {
    node.array.accept(this);
    node.index.accept(this);
    var arr = getValue(node.array);
    node.ptr = newGEP("%arrayidx", arr.type, arr, getValue(node.index));
  }

  @Override
  public void visit(MemberExprNode node) {
    // TODO Auto-generated method stub
    node.instance.accept(this);
    if (node.instance.type.isArrayType) {
      // .size function
      var ptr = new BitCastInst(nextName(), i32PtrType, getValue(node.instance), curBlock);
      var sizePtr = new GetElementPtrInst(nextName(), i32PtrType, ptr, curBlock, new IntConst(-1));
      node.val = newLoad(nextName(), sizePtr);
    } else if (node.instance.type.isString()) {
      // builtin function of string
      node.val = gScope.getFunc("__str_" + node.member);
    } else {
      // class
      var clsName = node.instance.type.typename;
      var cls = gScope.getClassScope(clsName);
      var clsType = gScope.getClassType(clsName);
      if (node.isFunc) {
        node.val = cls.getFunc(node.member);
      } else {
        var idx = cls.getVarIndex(node.member);
        node.ptr = new GetElementPtrInst(rename("%" + node.member),
            new PointerType(clsType.typeList.get(idx)),
            getValue(node.instance),
            curBlock, new IntConst(0), new IntConst(idx));
      }
    }
  }

  @Override
  public void visit(NewExprNode node) {
    // TODO Auto-generated method stub
    if (node.type.isArrayType) {
      var sizeVals = new ArrayList<Value>();
      for (var i : node.sizeExprs) {
        i.accept(this);
        sizeVals.add(getValue(i));
      }
      if (sizeVals.size() > 0)
        node.val = newArray(getType(node.type), 0, sizeVals);
      else
        node.val = nullConst;
    } else {
      var clsName = node.type.typename;
      var clsType = gScope.getClassType(clsName);
      var clsScope = gScope.getClassScope(clsName);

      var rawPtr = new CallInst(rename("%.new.ptr"), getMalloc(), curBlock, new IntConst(clsType.size()));
      node.val = new BitCastInst(rename("%.new.clsPtr"), new PointerType(clsType), rawPtr, curBlock);

      // call constructor
      new CallInst(rename("%.new.ctor"), clsScope.getFunc(clsName), curBlock, node.val);
    }
  }

  private Value newArray(BaseType type, int n, ArrayList<Value> sizeVals) {
    var elemType = ((PointerType) type).elemType;
    var size = sizeVals.get(n);
    // tmp = size * sizeof(elemType)
    var tmp = newBinary("mul", size, new IntConst(elemType.size()));
    // mallocSize = tmp + 4
    var mallocSize = newBinary("add", tmp, new IntConst(4), "%.new.mallocsize");
    var rawPtr = new CallInst(rename("%.new.ptr"), getMalloc(), curBlock, mallocSize);
    // size of the array is stored in the first 4 Bytes
    var sizePtr = new BitCastInst(rename("%.new.sizeptr"), i32PtrType, rawPtr, curBlock);
    newStore(size, sizePtr);
    // arrPtr = (elemType*) (ptr + 4);
    var tmpPtr = new GetElementPtrInst(nextName(), i8PtrType, rawPtr, curBlock, new IntConst(4));
    var arrPtr = new BitCastInst(rename("%.new.arrPtr"), getMemType(type), tmpPtr, curBlock);

    if (n + 1 < sizeVals.size()) {
      // loop variable ptr = arrPtr
      var ptrAddr = newAlloca(type, "%.new.ptr");
      newStore(arrPtr, ptrAddr);
      // endPtr = arrPtr + size;
      var endPtr = new GetElementPtrInst(rename("%.new.endPtr"), type, arrPtr, curBlock, size);
      /**
       * {@code
       * while (ptr < endPtr) {
       *   *ptr = newArray(elemType, n+1, sizeVals);
       *   ptr++;
       * }}
       */
      var condBlock = newBlock("new.while.cond");
      var bodyBlock = newBlock("new.while.body");
      var endBlock = newBlock("new.while.end");

      new BrInst(condBlock, curBlock);
      curBlock = condBlock;
      var ptr1 = newLoad("%.new.ptr", ptrAddr);
      var cond = new IcmpInst("ne", ptr1, endPtr, rename("%.new.cond"), curBlock);
      new BrInst(cond, bodyBlock, endBlock, curBlock);

      curBlock = bodyBlock;
      var ptr2 = newLoad("%.new.ptr", ptrAddr);
      newStore(newArray(((PointerType) type).elemType, n + 1, sizeVals), ptr2);
      newStore(new GetElementPtrInst(nextName(), type, ptr2, curBlock, new IntConst(1)), ptrAddr);
      new BrInst(condBlock, curBlock);

      curBlock = endBlock;
    }
    return arrPtr;
  }

  @Override
  public void visit(PostfixExprNode node) {
    // TODO Auto-generated method stub
    node.expr.accept(this);
    node.val = getValue(node.expr);
    Value val = null;
    switch (node.op) {
      case "++":
        val = newBinary("add", node.val, new IntConst(1), "%inc");
        break;
      case "--":
        val = newBinary("sub", node.val, new IntConst(1), "%dec");
        break;
    }
    newStore(val, node.expr.ptr);
  }

  @Override
  public void visit(PrefixExprNode node) {
    // TODO Auto-generated method stub
    node.expr.accept(this);
    node.ptr = node.expr.ptr;
    switch (node.op) {
      case "++":
        node.val = newBinary("add", getValue(node.expr), new IntConst(1), "%inc");
        break;
      case "--":
        node.val = newBinary("sub", getValue(node.expr), new IntConst(1), "%dec");
        break;
    }
    newStore(node.val, node.ptr);
  }

  @Override
  public void visit(UnaryExprNode node) {
    // TODO Auto-generated method stubnode.
    node.expr.accept(this);
    switch (node.op) {
      case "+":
        node.val = getValue(node.expr);
        break;
      case "-":
        node.val = newBinary("sub", new IntConst(0), getValue(node.expr), "%sub");
        break;
      case "!":
        node.val = newBinary("xor", getValue(node.expr), trueConst, "%lnot");
        break;
      case "~":
        node.val = newBinary("xor", getValue(node.expr), new IntConst(-1), "%neg");
        break;
      default:
        break;
    }
  }

  @Override
  public void visit(LambdaExprNode node) {
    throw new IRBuildError("not implemented");
  }

  @Override
  public void visit(ParamNode node) {
    // TODO Auto-generated method stub
  }

  private void declareClassType(ClassDefNode node) {
    StructType cls = new StructType("%class." + node.className);
    module.classes.add(cls);
    gScope.addClassType(node.className, cls);
  }

  private void defineClassType(ClassDefNode node) {
    var cls = gScope.getClassType(node.className);
    for (var i : node.defs) {
      if (i instanceof VarDefNode) {
        for (var j : ((VarDefNode) i).vars)
          cls.typeList.add(getType(((SingleVarDefNode) j).type));
      }
    }
  }

  private void declareMemberFunc(ClassDefNode node) {
    clsName = node.className;
    clsType = gScope.getClassType(clsName);
    clsScope = gScope.getClassScope(clsName);
    for (var i : node.defs) {
      if (i instanceof FuncDefNode) {
        var funcDef = (FuncDefNode) i;
        var funcType = new FuncType(getType(funcDef.returnType));
        funcType.paramTypes.add(new PointerType(clsType)); // "this" pointer
        for (var j : funcDef.params.params) {
          funcType.paramTypes.add(getType(j.type));
        }
        var funcName = "@%s.%s".formatted(clsName, funcDef.funcName);
        var func = new Function(funcType, funcName, true);
        module.funcs.add(func);
        // TODO
        // gScope.addFunc(funcDef, func);
        clsScope.addFunc(funcDef.funcName, func);
      }
    }

    // constructor
    var funcType = new FuncType(voidType);
    funcType.paramTypes.add(new PointerType(clsType)); // "this" pointer
    var funcName = "@%s.%s".formatted(clsName, clsName);
    var func = new Function(funcType, funcName, true);
    module.funcs.add(func);
    clsScope.addFunc(clsName, func);

    exitClass();
  }

  private void declareFunc(FuncDefNode node) {
    var funcType = new FuncType(getType(node.returnType));
    for (var j : node.params.params) {
      funcType.paramTypes.add(getType(j.type));
    }
    var funcName = node.funcName.equals("main") ? "@main" : "@func." + node.funcName;
    var func = new Function(funcType, funcName, false);
    module.funcs.add(func);
    gScope.addFunc(node.funcName, func);
  }

  private void declareGlobalVar(SingleVarDefNode node) {
    var name = rename("@" + node.name);
    var type = getType(node.type);
    var v = newGlobalVar(type, name);
    module.globalVars.add(v);
    gScope.globalVars.put(node.name, v);
  }

  private void initGlobalVar(ProgramNode node) {
    curFunc = newBuiltinFunc("__global_var_init", false, voidType);
    module.funcs.add(curFunc);
    gScope.addFunc("__global_var_init", curFunc);
    curFunc.entryBlock = newBlock("entry");
    curFunc.exitBlock = newBlock("exit");
    curBlock = curFunc.exitBlock;
    newRet(); // return void
    curBlock = curFunc.entryBlock;
    for (var i : node.defs) {
      if (!(i instanceof VarDefNode))
        continue;
      for (var j : ((VarDefNode) i).vars) {
        var v = gScope.globalVars.get(j.name);
        if (j.initExpr == null)
          continue;
        j.initExpr.accept(this);
        if (j.initExpr.val instanceof Constant) {
          v.initVal = j.initExpr.val;
        } else {
          newStore(getValue(j.initExpr), v);
        }
      }
    }
    if (!curBlock.terminated)
      new BrInst(curFunc.exitBlock, curBlock);
    curFunc = null;
    curBlock = null;
  }

  // @formatter:off
  private static final BaseType
    i32Type = new IntType(32),
    i8Type  = new IntType(8),
    i8BoolType  = new IntType(8, true),
    i1Type  = new IntType(1),
    i8PtrType = new PointerType(i8Type),
    i32PtrType = new PointerType(i32Type),
    voidType = new VoidType();
  private static final IntConst
    trueConst = new IntConst(1, 1),
    falseConst = new IntConst(0, 1);
  private static final NullptrConst nullConst = new NullptrConst();
  // @formatter:on

  private BaseType getElemType(String typename) {
    if (typename.equals("int")) {
      return i32Type;
    } else if (typename.equals("bool")) {
      return i1Type;
    } else if (typename.equals("string")) {
      return i8PtrType;
    } else if (typename.equals("void")) {
      return voidType;
    } else if (typename.equals("null")) {
      // Note: the ir type of nullptr constant may be incorrect!
      // When null is used, the correct type is inferred from other operand.
      return i32PtrType;
    } else {
      throw new IRBuildError("unknown elementary type");
    }
  }

  private BaseType getType(TypeNode type) {
    if (type.isArrayType) {
      var elemType = new TypeNode(type);
      elemType.dimension--;
      if (elemType.dimension == 0)
        elemType.isArrayType = false;
      return new PointerType(getType(elemType));
    }
    if (type.isString())
      return i8PtrType;
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

  /** get next available identifier for unnamed values */
  private String nextName() {
    return "%." + cntName++;
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
    if (!(type instanceof IntType))
      return false;
    var t = (IntType) type;
    return t.bitWidth == 1 || t.isBool;
  }

  /**
   * bool type, i.e. i1 type, is stored as i8 type in memory.
   * i1 -> i8 with isBool=true
   * i1* -> (i8 with isBool=true)*
   */
  private BaseType getMemType(BaseType type) {
    if (isBool(type))
      return i8BoolType;
    if (type instanceof PointerType)
      return new PointerType(getMemType(((PointerType) type).elemType));
    return type;
  }

  private AllocaInst newAlloca(BaseType type, String name) {
    return new AllocaInst(getMemType(type), rename(name), curFunc.entryBlock);
  }

  private GlobalVariable newGlobalVar(BaseType type, String name) {
    return new GlobalVariable(getMemType(type), rename(name));
  }

  private Value newLoad(String name, Value ptr, BasicBlock parent) {
    var loadInst = new LoadInst(rename(name), ptr, parent);
    if (isBool(((PointerType) ptr.type).elemType)) {
      return newTrunc(loadInst, i1Type, name + ".tobool");
    }
    return loadInst;
  }

  private Value newLoad(String name, Value ptr) {
    return newLoad(name, ptr, curBlock);
  }

  private StoreInst newStore(Value val, Value ptr) {
    if (isBool(val.type)) {
      val = new ZextInst(val, i8Type, rename("%zext"), curBlock);
    }
    return new StoreInst(val, ptr, curBlock);
  }

  private BinaryInst newBinary(String op, Value op1, Value op2, String name) {
    return new BinaryInst(op, op1, op2, rename(name), curBlock);
  }

  private BinaryInst newBinary(String op, Value op1, Value op2) {
    return newBinary(op, op1, op2, nextName());
  }

  private GetElementPtrInst newGEP(String name, BaseType retType, Value ptr, Value... idx) {
    return new GetElementPtrInst(rename(name), retType, ptr, curBlock, idx);
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

  private BasicBlock newBlock(String name) {
    return new BasicBlock(rename(name), curFunc); // TODO: "%" + name?
  }

  private Function getStrMethod(String op) {
    // @formatter:off
    String name;
    switch (op) {
      case "==": name = "eq"; break;
      case "!=": name = "ne"; break;
      case ">":  name = "gt"; break;
      case ">=": name = "ge"; break;
      case "<":  name = "lt"; break;
      case "<=": name = "le"; break;
      case "+": name = "cat"; break;
      default: throw new IRBuildError("unknown str operator");
    }
    // @formatter:on
    return gScope.getFunc("__str_" + name);
  }

  private void addBuiltin() {
    addBuiltinFunc("print", voidType, i8PtrType);
    addBuiltinFunc("println", voidType, i8PtrType);
    addBuiltinFunc("printInt", voidType, i32Type);
    addBuiltinFunc("printlnInt", voidType, i32Type);
    addBuiltinFunc("getString", i8PtrType);
    addBuiltinFunc("getInt", i32Type);
    addBuiltinFunc("toString", i8PtrType, i32Type);

    // ClassScope stringCls = this.gScope.getClassScope("string");
    addBuiltinFunc("__malloc", i8PtrType, i32Type);
    addBuiltinFunc("__str_length", true, i32Type, i8PtrType);
    addBuiltinFunc("__str_substring", true, i8PtrType, i8PtrType, i32Type, i32Type);
    addBuiltinFunc("__str_parseInt", true, i32Type, i8PtrType);
    addBuiltinFunc("__str_ord", true, i32Type, i8PtrType, i32Type);

    addBuiltinFunc("__str_eq", i1Type, i8PtrType, i8PtrType);
    addBuiltinFunc("__str_ne", i1Type, i8PtrType, i8PtrType);
    addBuiltinFunc("__str_gt", i1Type, i8PtrType, i8PtrType);
    addBuiltinFunc("__str_ge", i1Type, i8PtrType, i8PtrType);
    addBuiltinFunc("__str_lt", i1Type, i8PtrType, i8PtrType);
    addBuiltinFunc("__str_le", i1Type, i8PtrType, i8PtrType);

    addBuiltinFunc("__str_cat", i8PtrType, i8PtrType, i8PtrType);
  }

  private Function getMalloc() {
    return this.gScope.getFunc("__malloc");
  }

  private StringConst getStringConst(String val) {
    for (var s : module.stringConsts) {
      if (s.val.equals(val))
        return s;
    }
    var s = new StringConst(rename("@.str"), val);
    module.stringConsts.add(s);
    return s;
  }

  private void addBuiltinFunc(String funcName, boolean isMember, BaseType returnType, BaseType... paramTypes) {
    var func = newBuiltinFunc(funcName, isMember, returnType, paramTypes);
    module.funcDecls.add(func);
    this.gScope.addFunc(funcName, func);
  }

  private void addBuiltinFunc(String funcName, BaseType returnType, BaseType... paramTypes) {
    addBuiltinFunc(funcName, false, returnType, paramTypes);
  }

  private Function newBuiltinFunc(String funcName, boolean isMember, BaseType returnType, BaseType... paramTypes) {
    var funcType = new FuncType(returnType);
    for (var j : paramTypes) {
      funcType.paramTypes.add(j);
    }
    funcName = rename("@" + funcName);
    return new Function(funcType, funcName, isMember);
  }

}
