package simplf;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import simplf.parser.simplfLexer;
import simplf.parser.simplfParser;

public class Simplf {
    static boolean hadError = false;
    static boolean hadRuntimeError = false;
    private static final Interpreter interpreter = new Interpreter();

    public static void main(String[] args) throws IOException {
        if (args.length > 1) {
            System.exit(64);
        } else if (args.length == 1) {
            runFile(args[0]);
        } else {
            runPrompt();
        }
    }

    private static void runFile(String path) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        run(new String(bytes, Charset.defaultCharset()));
        if (hadError) System.exit(65);
        if (hadRuntimeError) System.exit(70);
    }

    private static void runPrompt() throws IOException {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(input);

        for (;;) {
            System.out.print("> ");
            String line = br.readLine();
            if (line == null) break;
            run(line);
            hadError = false;
            hadRuntimeError = false;
        }
    }

    private static void run(String source) {
        CharStream input = CharStreams.fromString(source);
        simplfLexer lexer = new simplfLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        simplfParser parser = new simplfParser(tokens);

        List<Stmt> statements = parser.program().val;
        List<Stmt> desugaredStatements = (new Desugar()).desugar(statements);
        interpreter.interpret(desugaredStatements);
    }

    static void error(int line, int col, String message) {
        report(line, col, "", message);
    }

    private static void report(int line, int col, String where, String message) {
        System.err.println("[line " + line + ", col " + col + "] Error" + where + ": " + message);
        hadError = true;
    }

    static void error(Token token, String message) {
        if (token.type == TokenType.EOF) {
            report(token.line, token.col, " at end", message);
        } else {
            report(token.line, token.col, " at '" + token.lexeme + "'", message);
        }
    }

    public static void runtimeError(RuntimeError error) {
        System.err.println(error.getMessage() + ": [line " + error.token.line + " col " + error.token.col + "]");
        hadRuntimeError = true;
    }
}
