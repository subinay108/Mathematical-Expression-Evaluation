import java.util.Stack;
import java.util.Vector;

enum Context{
    Number,
    Operator,
    Function,
    Parenthesis,
    Constant,
    Invalid
}


class Token{
    // type = 0->Number, 1->Operator, 2->Function, 3->Parenthesis, 4->Constant
    
    public static String ADD = "+",SUBTRACT = "-", MULTIPLY = "*", DIVIDE = "/", PERCENT = "%",
    POWER = "^", FACTORIAL = "!", RADICAL = "#", EOL = "$", PI = "@";

    public static String OperatorString = ADD + SUBTRACT + MULTIPLY + DIVIDE + PERCENT + POWER + FACTORIAL + RADICAL;

    Context type;
    String value;
    Token(String value, Context type){
        this.value = value;
        this.type = type;
    }
}

class Tokenizer{

    public static Vector<Token> tokenize(String expression){
        Vector<Token> tokens = new Vector<Token>();
        String s = String.valueOf(expression.charAt(0));
        expression += Token.EOL;

        Context context = getContext(expression.charAt(0));
        for(int i = 1; i < expression.length(); i++){
            char ch = expression.charAt(i);
            
            Context ct = getContext(ch);
            if(context != ct){
                if((context == Context.Number && ct == Context.Constant) ||
                   (context == Context.Constant && ct == Context.Number) ||
                   (context == Context.Number && ct == Context.Function) ||
                   (context == Context.Number && ch == '(')
                ){
                    tokens.add(new Token(Token.MULTIPLY, Context.Operator));    
                }
            }
            
            if(context != ct ||ct == Context.Parenthesis || ct == Context.Operator){
                if(context == Context.Operator && "+-".contains(s)){
                    if(i == 1 || expression.charAt(i-2) == '('){
                        tokens.add(new Token("0", Context.Number));
                    }
                }
                
                tokens.add(new Token(s, context));
                s = "";
                if(expression.charAt(i-1) == ')' && ct == Context.Number){
                    tokens.add(new Token(Token.MULTIPLY, Context.Operator));
                }
                context = ct;
            }

            s += String.valueOf(ch);
        }

        return tokens;
    }

    private static Context getContext(char ch){
        
        if(isDigit(ch) || ch == '.'){
            return Context.Number;
        }else if(isAlpha(ch)){
            return Context.Function;
        }else if(isOperator(ch)){
            return Context.Operator;
        }else if(ch == '(' || ch == ')'){
            return Context.Parenthesis;
        }else if(String.valueOf(ch).equals(Token.PI)){
            return Context.Constant;
        }
        return Context.Invalid;
    }
 
    private static boolean isOperator(char ch){
        if(Token.OperatorString.contains(String.valueOf(ch))){
            return true;
        }
        return false;
    }

    private static boolean isDigit(char ch){
        if(ch >= 48 && ch <= 57){
            return true;
        }
        return false;
    }

    private static boolean isAlpha(char ch){
        if((ch >= 65 && ch <= 90) || (ch >= 97 && ch <= 122)){
            return true;
        }
        return false;
    }

}


class Expression{
    String infix;
    Expression(String exp){
        infix = exp;
    }
    public String eval(){
        Vector<Token> tokens = Tokenizer.tokenize(infix);
        Stack<Double> operandStack = new Stack<Double>();
        Stack<Token> operatorStack = new Stack<Token>();

        for(Token t: tokens){
            // System.out.print(t.value);
            if(t.type == Context.Number){
                operandStack.add(Double.parseDouble(t.value));
            }
            else if(t.type == Context.Constant){
                if(t.value.equals(Token.PI)){
                    operandStack.add(Math.PI);
                }
            }
            else{
                if(t.type == Context.Operator){
                    if(t.value.equals(Token.PERCENT)){
                        double operand1 = operandStack.pop();
                        operandStack.push(operand1/100);
                    }else if(operatorStack.isEmpty()){
                        operatorStack.push(t);
                    }
                    else if(getAssociativity(t) == 1 || getPrecedence(t)>getPrecedence(operatorStack.lastElement())){
                        operatorStack.push(t);
                    }else{
                        while(!operatorStack.isEmpty() && getPrecedence(t)<=getPrecedence(operatorStack.lastElement())){
                            double operand2 = operandStack.pop();
                            double operand1 = operandStack.pop();
                            double result = calc(operand1, operand2, operatorStack.pop());
                            operandStack.push(result);
                        }
                        operatorStack.push(t);
                    }
                }
                else if(t.type == Context.Parenthesis){
                    if(t.value.equals("(")){
                        operatorStack.push(t);
                    }else{
                        while(!operatorStack.lastElement().value.equals("(")){
                            if(operatorStack.size() > 0){
                                double operand2 = operandStack.pop();
                                double operand1 = operandStack.pop();
                                double result = calc(operand1, operand2, operatorStack.pop());
                                operandStack.push(result);
                            }
                        }
                        operatorStack.pop();
                        if(!operatorStack.isEmpty() && operatorStack.lastElement().type == Context.Function){
                            Token fuToken = operatorStack.pop();
                            if(fuToken.value.equals("sin")){
                                double operand1 = operandStack.pop();
                                operandStack.push(Math.sin(operand1));
                            }else if(fuToken.value.equals("cos")){
                                double operand1 = operandStack.pop();
                                operandStack.push(Math.cos(operand1));
                            }else if(fuToken.value.equals("tan")){
                                double operand1 = operandStack.pop();
                                operandStack.push(Math.tan(operand1));
                            }else if(fuToken.value.equals("lg")){
                                double operand1 = operandStack.pop();
                                operandStack.push(Math.log10(operand1));
                            }else if(fuToken.value.equals("ln")){
                                double operand1 = operandStack.pop();
                                operandStack.push(Math.log(operand1));
                            }else if(fuToken.value.equals("log2")){
                                double operand1 = operandStack.pop();
                                operandStack.push(Math.log(operand1)/Math.log(2));
                            }else if(fuToken.value.equals("sinh")){
                                double operand1 = operandStack.pop();
                                operandStack.push(Math.sinh(operand1));
                            }else if(fuToken.value.equals("cosh")){
                                double operand1 = operandStack.pop();
                                operandStack.push(Math.cosh(operand1));
                            }else if(fuToken.value.equals("tanh")){
                                double operand1 = operandStack.pop();
                                operandStack.push(Math.tanh(operand1));
                            }else if(fuToken.value.equals("asin")){
                                double operand1 = operandStack.pop();
                                operandStack.push(Math.asin(operand1));
                            }else if(fuToken.value.equals("acos")){
                                double operand1 = operandStack.pop();
                                operandStack.push(Math.acos(operand1));
                            }else if(fuToken.value.equals("atan")){
                                double operand1 = operandStack.pop();
                                operandStack.push(Math.atan(operand1));
                            }else if(fuToken.value.equals("asinh")){
                                double operand1 = operandStack.pop();
                                operandStack.push(Math.log(operand1 + Math.sqrt(operand1 * operand1 + 1)));
                            }else if(fuToken.value.equals("acosh")){
                                double operand1 = operandStack.pop();
                                operandStack.push(Math.log(operand1 + Math.sqrt(operand1 * operand1 - 1)));
                            }else if(fuToken.value.equals("atanh")){
                                double operand1 = operandStack.pop();
                                operandStack.push(0.5 * Math.log((1 + operand1) / (1 - operand1)));
                            }
                        }
                    }
                }
                else if(t.type == Context.Function){
                    operatorStack.push(t);
                }
            }
        }
        // System.out.println();

        while(!operatorStack.isEmpty()){
            double operand2 = operandStack.pop();
            double operand1 = operandStack.pop();
            double result = calc(operand1, operand2, operatorStack.pop());
            operandStack.push(result);
        }
        double res = operandStack.pop();

        if(res * 10 - ((int)res)*10 == 0){
            return String.valueOf(((int)res));
        }

        return String.valueOf(res);
    } 

    private static double calc(double operand1, double operand2, Token opToken) {
        if(opToken.value.equals(Token.ADD)){
            return operand1 + operand2;
        }else if(opToken.value.equals(Token.SUBTRACT)){
            return operand1 - operand2;
        }else if(opToken.value.equals(Token.MULTIPLY)){
            return operand1 * operand2;
        }else if(opToken.value.equals(Token.DIVIDE)){
            return operand1 / operand2;
        }else if(opToken.value.equals(Token.POWER)){
            return Math.pow(operand1, operand2);
        }
        return 0;
        
    }

    private int getAssociativity(Token opToken){
        // 0 -> left, 1-> right
        if(opToken.value.equals(Token.POWER)){
            return 1;
        }
        return 0;
    }
    
    private int getPrecedence(Token opToken){
        if(opToken.value.equals(Token.ADD) || opToken.value.equals(Token.SUBTRACT)){
            return 1;
        }else if(opToken.value.equals(Token.MULTIPLY) || opToken.value.equals(Token.DIVIDE)){
            return 2;
        }else if(opToken.value.equals(Token.POWER)){
            return 3;
        }
        return 0;
    }

}

public class Main{
    public static void main(String[] args) {
        UserInput ui = new UserInput();
        String infixExp = ui.input("Enter a mathematical expression: ");

        Expression exp = new Expression(infixExp);

        String value = exp.eval();
        System.out.println(value);

    }
}