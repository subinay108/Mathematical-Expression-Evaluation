# Mathematical Expression Evaluation using Two-Stack Algorithm

This repository contains a Java implementation of a mathematical expression evaluation algorithm based on two stacks. The algorithm tokenizes the expression, parses it, and directly evaluates it using two stacks.

## Overview

This implementation offers a straightforward approach to evaluate mathematical expressions without converting them to postfix notation. It utilizes two stacks to parse and evaluate the expressions efficiently.

## Features

- Tokenizes the input expression to handle different types of operands, operators, functions, and parentheses.
- Utilizes two stacks for parsing and evaluating the expression in a single pass.
- Supports basic arithmetic operations (addition, subtraction, multiplication, division).
- Supports exponentiation and percentage operations.
- Handles unary operators, functions, and parentheses to specify the order of operations.
- Provides accurate evaluation results for complex mathematical expressions.

## Getting Started

Follow these steps to set up and run the project locally:

1. Clone the repository to your local machine:

   ```bash
   git clone https://github.com/subinay108/Mathematical-Expression-Evaluation.git
   ```
2. Navigate to the project directory:
    ```bash
    cd math-expression-evaluation
    ```
3. Compile the Java source files:
    ```bash
    javac *.java
    ```
4. Run the main class to evaluate mathematical expressions:
    ```bash
    java Main
    ```


## Usage/Examples

To evaluate a mathematical expression, simply input it when prompted. The program will then output the result of the expression.
1. Simple Operations
    ```yaml
    Enter a mathematical expression: 2 + 3 * (4 - 1)
    Result: 11.0
    ```
2. Exponentiation Operations with right to left associativity
    ```yaml
    Enter a mathematical expression: 2 ^ 3 ^ 2
    Result: 512.0
    ```
3. Unary minus and unary plus 
    ```yaml
    Enter a mathematical expression: -2 * (-3) + (+4)
    Result: 10.0
    ```
4. Functions (Trigonometric, Inverse Trigonometric, Logarithmic) 
    ```yaml
    Enter a mathematical expression: sin(20) + lg(34) - atan(10)
    Result: 0.9732964934661483
    ```
5. Function composition 
    ```yaml
    Enter a mathematical expression: sin(cos(1) + 5)
    Result: -0.676414112265833
    ```


## Contributing

Contributions are welcome! If you have any ideas for improvements, new features, or bug fixes, feel free to open an issue or submit a pull request.

## License

This project is licensed under the MIT License - see the [LICENSE](https://github.com/subinay108/Mathematical-Expression-Evaluation/blob/main/LICENSE) file for details.

## Acknowledgments

Dijkstra's Shunting Yard Algorithm: [Wikipedia](https://en.wikipedia.org/wiki/Shunting_yard_algorithm)