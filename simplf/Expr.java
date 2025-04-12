package simplf; 

import java.util.List;

public abstract class Expr {
    abstract <T> T accept(Visitor<T> vis);

    interface Visitor<T> {
        T visitBinary(Binary expr);

        T visitUnary(Unary expr);

        T visitLiteral(Literal expr);

        T visitGrouping(Grouping expr);

        T visitVarExpr(Variable expr);

        T visitAssignExpr(Assign expr);

        T visitLogicalExpr(Logical expr);

        T visitConditionalExpr(Conditional expr);

        T visitCallExpr(Call expr);

    }

    public static class Binary extends Expr {
        final Expr left, right;
        Token op;

        public Binary(Expr left, Token op, Expr right) {
            this.left = left;
            this.op = op;
            this.right = right;
        }

        @Override
        <T> T accept(Visitor<T> vis) {
            return vis.visitBinary(this);
        }
    }

    public static class Unary extends Expr {
        final Expr right;
        Token op;

        public Unary(Token op, Expr right) {
            this.op = op;
            this.right = right;
        }

        @Override
        <T> T accept(Visitor<T> vis) {
            return vis.visitUnary(this);
        }
    }

    public static class Literal extends Expr {
        final Object val;

        public Literal(Object val) {
            this.val = val;
        }

        @Override
        <T> T accept(Visitor<T> vis) {
            return vis.visitLiteral(this);
        }
    }

    public static class Grouping extends Expr {
        final Expr expression;

        public Grouping(Expr expression) {
            this.expression = expression;
        }

        @Override
        <T> T accept(Visitor<T> vis) {
            return vis.visitGrouping(this);
        }
    }

    public static class Variable extends Expr {
        final Token name;

        public Variable(Token name) {
            this.name = name;
        }

        @Override
        <T> T accept(Visitor<T> vis) {
            return vis.visitVarExpr(this);
        }
    }

    public static class Assign extends Expr {
        final Token name;
        final Expr value;

        public Assign(Token name, Expr value) {
            this.name = name;
            this.value = value;
        }

        <T> T accept(Visitor<T> visitor) {
            return visitor.visitAssignExpr(this);
        }
    }

    public static class Logical extends Expr {
        final Token op;
        final Expr left, right;

        public Logical(Expr left, Token op, Expr right) {
            this.left = left;
            this.op = op;
            this.right = right;
        }

        <T> T accept(Visitor<T> visitor) {
            return visitor.visitLogicalExpr(this);
        }
    }

    public static class Conditional extends Expr {
        final Expr cond, thenBranch, elseBranch;

        public Conditional(Expr cond, Expr thenBranch, Expr elseBranch) {
            this.cond = cond;
            this.thenBranch = thenBranch;
            this.elseBranch = elseBranch;
        }

        <T> T accept(Visitor<T> visitor) {
            return visitor.visitConditionalExpr(this);
        }
    }

    public static class Call extends Expr {
        final Expr callee;
        Token paren;
        final List<Expr> args;

        public Call(Expr callee, Token paren, List<Expr> args) {
            this.callee = callee;
            this.paren = paren;
            this.args = args;
        }

        <T> T accept(Visitor<T> vis) {
            return vis.visitCallExpr(this);
        }
    }
}

