package simplf;

class Environment {
    private final Environment enclosing;
    private final AssocList vars;

    Environment() {
        this.vars = null;
        this.enclosing = null;
    }

    Environment(Environment enclosing) {
        this.vars = null;
        this.enclosing = enclosing;
    }

    Environment(AssocList vars, Environment enclosing) {
        this.vars = vars;
        this.enclosing = enclosing;
    }

    Environment define(String name, Object value) {
        return new Environment(new AssocList(name, value, this.vars), this.enclosing);
    }

    // ✅ Overload for define with Token
    Environment define(Token token, String name, Object value) {
        return new Environment(new AssocList(name, value, this.vars), this.enclosing);
    }

    void assign(Token name, Object value) {
        for (AssocList current = vars; current != null; current = current.next) {
            if (current.name.equals(name.lexeme)) {
                current.value = value;
                return;
            }
        }
        if (enclosing != null) {
            enclosing.assign(name, value);
            return;
        }
        throw new RuntimeError(name, "Undefined variable '" + name.lexeme + "'.");
    }

    Object get(Token name) {
        for (AssocList current = vars; current != null; current = current.next) {
            if (current.name.equals(name.lexeme)) {
                Object val = current.value;
                if (val == Interpreter.uninitialized) {
                    throw new RuntimeError(name, "Variable '" + name.lexeme + "' used before initialization.");
                }
                return val;
            }
        }
        if (enclosing != null) return enclosing.get(name);
        throw new RuntimeError(name, "Undefined variable '" + name.lexeme + "'.");
    }
}
