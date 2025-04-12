# Project 3: Implement the SimPL-F Interpreter

## Part 1: Variables and Assignments

For this part, you will need to implement the `Environment` class in `Environment.java`, as well as the relevant functions in `Interpreter.java`:
* `visitVarStmt`
* `visitBlockStmt`
* `visitVarExpr`
* `visitAssignExpr`

You may make use of the association list implemented in `AssocList.java` in your implementation of environments.

## Part 2: Control Flow

For this part, you will implement if statements, while loops, and for loops. You should implement the following functions in `Interpreter.java`:
* `visitIfStmt`
* `visitWhileStmt`
You should also implement the function `visitForStmt` in `Desugar.java`.

## Part 3 Functions

For this part, you should implement functions. This will require implementing:
* The `SimplfFunction` class in `SimplfFunction.java`.
* The `visitFunctionStmt` in `Interpreter.java`, and
* The `visitCallExpr` in `Interpreter.java`
