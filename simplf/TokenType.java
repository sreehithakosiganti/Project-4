package simplf; 

public enum TokenType {
    // Grouping
    LEFT_PAREN, RIGHT_PAREN, LEFT_BRACE, RIGHT_BRACE,
    COMMA, DOT, MINUS, PLUS, SEMI_COLON, STAR, SLASH,

    BANG, BANG_EQUAL,
    EQUAL, EQUAL_EQUAL,
    GREATER, GREATER_EQUAL,
    LESS, LESS_EQUAL,

    // Boolean
    AND, OR,

    // Literals
    IDENTIFIER, STRING, NUMBER,

    // Reserved words
    IF, ELSE, VAR, FUN, FOR, WHILE, COLON,
    TRUE, FALSE, NIL, PRINT,

    // Others
    EOF
}