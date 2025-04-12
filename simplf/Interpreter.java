package simplf;

import java.util.List;

class Interpreter implements Expr.Visitor<Object>, Stmt.Visitor<Object> {
    public Environment globals = new Environment();
    Environment environment = globals;

    public static final Object uninitialized = new Object();

    Interpreter() {}

    public void interpret(List<Stmt> stmts) {
        try {
            for (Stmt stmt : stmts) {
                execute(stmt);
            }
        } catch (RuntimeError error) {
            Simplf.runtimeError(error);
        }
    }

    public Object execute(Stmt stmt) {
        return stmt.accept(this);
    }

    public Object executeBlock(List<Stmt> statements, Environment newEnv) {
        Environment previous = this.environment;
        this.environment = newEnv;
        try {
            for (Stmt stmt : statements) {
                execute(stmt);
            }
        } finally {
            this.environment = previous;
        }
        return null;
    }

    // ✅ Exposed for use in SimplfFunction
    public Object evaluate(Expr expr) {
        return expr.accept(this);
    }

    public Object evaluateInEnv(Expr expr, Environment env) {
        Environment previous = this.environment;
        this.environment = env;
        try {
            return expr.accept(this);
        } finally {
            this.environment = previous;
        }
    }

    public void executeInEnv(Stmt stmt, Environment env) {
        Environment previous = this.environment;
        this.environment = env;
        try {
            execute(stmt);
        } finally {
            this.environment = previous;
        }
    }

    @Override
    public Object visitVarStmt(Stmt.Var stmt) {
        Object value = uninitialized;
        if (stmt.initializer != null) {
            value = evaluate(stmt.initializer);
        }
        environment = environment.define(stmt.name.lexeme, value);
        return null;
    }

    @Override
    public Object visitFunctionStmt(Stmt.Function stmt) {
        SimplfFunction function = new SimplfFunction(stmt, environment);
        environment = environment.define(stmt.name.lexeme, function);
        function.setClosure(environment);
        return null;
    }

    @Override
    public Object visitExprStmt(Stmt.Expression stmt) {
        evaluate(stmt.expr);
        return null;
    }

    @Override
    public Object visitPrintStmt(Stmt.Print stmt) {
        Object val = evaluate(stmt.expr);
        System.out.println(stringify(val));
        return null;
    }

    @Override
    public Object visitBlockStmt(Stmt.Block stmt) {
        return executeBlock(stmt.statements, new Environment(environment));
    }

    @Override
    public Object visitIfStmt(Stmt.If stmt) {
        if (isTruthy(evaluate(stmt.cond))) {
            execute(stmt.thenBranch);
        } else if (stmt.elseBranch != null) {
            execute(stmt.elseBranch);
        }
        return null;
    }

    @Override
    public Object visitWhileStmt(Stmt.While stmt) {
        while (isTruthy(evaluate(stmt.cond))) {
            execute(stmt.body);
        }
        return null;
    }

    @Override
    public Object visitForStmt(Stmt.For stmt) {
        throw new UnsupportedOperationException("For loops are desugared.");
    }

    @Override
    public Object visitVarExpr(Expr.Variable expr) {
        return environment.get(expr.name);
    }

    @Override
    public Object visitAssignExpr(Expr.Assign expr) {
        Object value = evaluate(expr.value);
        environment.assign(expr.name, value);
        return value;
    }

    @Override
    public Object visitCallExpr(Expr.Call expr) {
        Object callee = evaluate(expr.callee);

        List<Object> args = new java.util.ArrayList<>();
        for (Expr arg : expr.args) {
            args.add(evaluate(arg));
        }

        if (!(callee instanceof SimplfCallable)) {
            throw new RuntimeError(expr.paren, "Can only call functions.");
        }

        SimplfCallable function = (SimplfCallable) callee;
        return function.call(this, args);
    }

    @Override
    public Object visitLogicalExpr(Expr.Logical expr) {
        Object left = evaluate(expr.left);
        if (expr.op.type == TokenType.OR) {
            if (isTruthy(left)) return left;
        } else {
            if (!isTruthy(left)) return left;
        }
        return evaluate(expr.right);
    }

    @Override
    public Object visitBinary(Expr.Binary expr) {
        Object left = evaluate(expr.left);
        Object right = evaluate(expr.right);

        switch (expr.op.type) {
            case PLUS:
                if (left instanceof String || right instanceof String) {
                    return stringify(left) + stringify(right);
                }
                if (left instanceof Double && right instanceof Double) {
                    return (double) left + (double) right;
                }
                throw new RuntimeError(expr.op, "Operands must be two numbers or strings.");
            case MINUS:
                checkNumbers(expr.op, left, right);
                return (double) left - (double) right;
            case STAR:
                checkNumbers(expr.op, left, right);
                return (double) left * (double) right;
            case SLASH:
                checkNumbers(expr.op, left, right);
                if ((double) right == 0) {
                    throw new RuntimeError(expr.op, "Cannot divide by zero.");
                }
                return (double) left / (double) right;
            case GREATER:
                checkNumbers(expr.op, left, right);
                return (double) left > (double) right;
            case GREATER_EQUAL:
                checkNumbers(expr.op, left, right);
                return (double) left >= (double) right;
            case LESS:
                checkNumbers(expr.op, left, right);
                return (double) left < (double) right;
            case LESS_EQUAL:
                checkNumbers(expr.op, left, right);
                return (double) left <= (double) right;
            case EQUAL_EQUAL:
                return isEqual(left, right);
            case BANG_EQUAL:
                return !isEqual(left, right);
            case COMMA:
                return right;
            default:
                throw new RuntimeError(expr.op, "Unsupported binary operator.");
        }
    }

    @Override
    public Object visitUnary(Expr.Unary expr) {
        Object right = evaluate(expr.right);
        switch (expr.op.type) {
            case MINUS:
                checkNumber(expr.op, right);
                return -(double) right;
            case BANG:
                return !isTruthy(right);
            default:
                throw new RuntimeError(expr.op, "Unsupported unary operator.");
        }
    }

    @Override
    public Object visitLiteral(Expr.Literal expr) {
        return expr.val;
    }

    @Override
    public Object visitGrouping(Expr.Grouping expr) {
        return evaluate(expr.expression);
    }

    @Override
    public Object visitConditionalExpr(Expr.Conditional expr) {
        if (isTruthy(evaluate(expr.cond))) {
            return evaluate(expr.thenBranch);
        } else {
            return evaluate(expr.elseBranch);
        }
    }

    private boolean isTruthy(Object object) {
        if (object == null) return false;
        if (object instanceof Boolean) return (boolean) object;
        return true;
    }

    private boolean isEqual(Object a, Object b) {
        if (a == null) return b == null;
        return a.equals(b);
    }

    private void checkNumber(Token op, Object object) {
        if (object instanceof Double) return;
        throw new RuntimeError(op, "Operand must be a number");
    }

    private void checkNumbers(Token op, Object a, Object b) {
        if (a instanceof Double && b instanceof Double) return;
        throw new RuntimeError(op, "Operands must be numbers");
    }

    private String stringify(Object object) {
        if (object == null) return "nil";
        if (object instanceof Double) {
            String num = object.toString();
            if (num.endsWith(".0")) {
                num = num.substring(0, num.length() - 2);
            }
            return num;
        }
        return object.toString();
    }
}
