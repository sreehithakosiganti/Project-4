package simplf; 

import java.util.List;

public abstract class Stmt {
    abstract <T> T accept(Visitor<T> vis);

    interface Visitor<T> {
        T visitPrintStmt(Print stmt);

        T visitExprStmt(Expression stmt);

        T visitVarStmt(Var stmt);

        T visitBlockStmt(Block stmt);

        T visitIfStmt(If stmt);

        T visitWhileStmt(While stmt);

        T visitForStmt(For stmt);

        T visitFunctionStmt(Function stmt);

    }

    public static class Expression extends Stmt {
        final Expr expr;

        public Expression(Expr expr) {
            this.expr = expr;
        }

        @Override
        <T> T accept(Visitor<T> vis) {
            return vis.visitExprStmt(this);
        }
    }

    public static class Print extends Stmt {
        final Expr expr;

        public Print(Expr expr) {
            this.expr = expr;
        }

        @Override
        <T> T accept(Visitor<T> vis) {
            return vis.visitPrintStmt(this);
        }
    }

    public static class Var extends Stmt {
        final Token name;
        final Expr initializer;

        public Var(Token name, Expr initializer) {
            this.name = name;
            this.initializer = initializer;
        }

        @Override
        <T> T accept(Visitor<T> vis) {
            return vis.visitVarStmt(this);
        }
    }

    public static class Block extends Stmt {
        final List<Stmt> statements;

        public Block(List<Stmt> statements) {
            this.statements = statements;
        }

        <T> T accept(Visitor<T> vis) {
            return vis.visitBlockStmt(this);
        }
    }

    public static class If extends Stmt {
        final Expr cond;
        final Stmt thenBranch;
        final Stmt elseBranch;

        public If(Expr cond, Stmt thenBranch, Stmt elseBranch) {
            this.cond = cond;
            this.thenBranch = thenBranch;
            this.elseBranch = elseBranch;
        }

        <T> T accept(Visitor<T> vis) {
            return vis.visitIfStmt(this);
        }
    }

    public static class While extends Stmt {
        final Expr cond;
        final Stmt body;

        public While(Expr cond, Stmt body) {
            this.cond = cond;
            this.body = body;
        }

        <T> T accept(Visitor<T> vis) {
            return vis.visitWhileStmt(this);
        }
    }

    public static class For extends Stmt {
        final Expr init, cond, incr;
        final Stmt body;

        public For(Expr init, Expr cond, Expr incr, Stmt body) {
            this.init = init;
            this.cond = cond;
            this.incr = incr;
            this.body = body;
        }

        <T> T accept(Visitor<T> vis) {
            return vis.visitForStmt(this);
        }
    }

    public static class Function extends Stmt {
        Token name;
        List<Token> params;
        List<Stmt> body;

        public Function(Token name, List<Token> params, List<Stmt> body) {
            this.name = name;
            this.params = params;
            this.body = body;
        }

        <T> T accept(Visitor<T> vis) {
            return vis.visitFunctionStmt(this);
        }
    }

}