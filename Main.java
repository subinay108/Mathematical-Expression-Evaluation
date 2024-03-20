import java.text.DecimalFormat;
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
    POWER = "^", FACTORIAL = "!", RADICAL = "#", EOL = "$", PI = "@", EULER = "e", ESCI = "E";

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

            if(ch == '2' && s.equals("log")){
                tokens.add(new Token("log2", Context.Function));
                s = "";
                context = Context.Invalid;
                continue;
            }

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
                // make unary plus and minus to binary by putting 0 before them
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
        }else if(String.valueOf(ch).equals(Token.PI) || String.valueOf(ch).equals(Token.EULER)){
            return Context.Constant;
        }else if(isAlpha(ch)){
            return Context.Function;
        }else if(isOperator(ch)){
            return Context.Operator;
        }else if(ch == '(' || ch == ')'){
            return Context.Parenthesis;
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
    private String infix;
    private boolean degreeAngle = false;

    Expression(String exp){
        infix = exp;
    }

    public void setDegreeAngle(Boolean b){
        degreeAngle = b;
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
                }else if(t.value.equals(Token.EULER)){
                    operandStack.add(Math.exp(1));
                }
            }
            else{
                if(t.type == Context.Operator){
                    if(t.value.equals(Token.PERCENT)){
                        double operand1 = operandStack.pop();
                        operandStack.push(operand1/100);
                    }else if(t.value.equals(Token.FACTORIAL)){
                        double operand1 = operandStack.pop();
                        if(operand1 >= 0 && operand1 <= 20 && (operand1*10 - (int)operand1*10 == 0 )){
                            operandStack.push(factorial(operand1));
                        }else{
                            return "Can't do factorial. Error";
                        }
                    }
                    else if(operatorStack.isEmpty()){
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
                            double operand1 = operandStack.pop();

                            if(fuToken.value.equals("sin")){
                                if(degreeAngle){
                                    operand1 = Math.toRadians(operand1);
                                }    
                                operandStack.push(Math.sin(operand1));
                            }else if(fuToken.value.equals("cos")){
                                if(degreeAngle){
                                    operand1 = Math.toRadians(operand1);
                                }
                                operandStack.push(Math.cos(operand1));
                            }else if(fuToken.value.equals("tan")){
                                if(degreeAngle){
                                    operand1 = Math.toRadians(operand1);
                                }
                                operandStack.push(Math.tan(operand1));
                            }else if(fuToken.value.equals("lg")){
                                operandStack.push(Math.log10(operand1));
                            }else if(fuToken.value.equals("ln")){
                                operandStack.push(Math.log(operand1));
                            }else if(fuToken.value.equals("log2")){
                                operandStack.push(Math.log(operand1)/Math.log(2));
                            }else if(fuToken.value.equals("sinh")){
                                operandStack.push(Math.sinh(operand1));
                            }else if(fuToken.value.equals("cosh")){
                                operandStack.push(Math.cosh(operand1));
                            }else if(fuToken.value.equals("tanh")){
                                operandStack.push(Math.tanh(operand1));
                            }else if(fuToken.value.equals("asin")){
                                if(degreeAngle){
                                    operandStack.push(Math.toDegrees(Math.asin(operand1)));
                                }else{
                                    operandStack.push(Math.asin(operand1));
                                }
                            }else if(fuToken.value.equals("acos")){
                                if(degreeAngle){
                                    operandStack.push(Math.toDegrees(Math.acos(operand1)));
                                }else{
                                    operandStack.push(Math.acos(operand1));
                                }
                            }else if(fuToken.value.equals("atan")){
                                if(degreeAngle){
                                    operandStack.push(Math.toDegrees(Math.atan(operand1)));
                                }else{
                                    operandStack.push(Math.atan(operand1));
                                }
                            }else if(fuToken.value.equals("asinh")){
                                operandStack.push(Math.log(operand1 + Math.sqrt(operand1 * operand1 + 1)));
                            }else if(fuToken.value.equals("acosh")){
                                operandStack.push(Math.log(operand1 + Math.sqrt(operand1 * operand1 - 1)));
                            }else if(fuToken.value.equals("atanh")){
                                operandStack.push(0.5 * Math.log((1 + operand1) / (1 - operand1)));
                            }
                        }
                    }
                }
                else if(t.type == Context.Function){
                    if(t.value.equals("Rand")){
                        operandStack.push(Math.random());
                    }else{
                        operatorStack.push(t);
                    }
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

        DecimalFormat df = new DecimalFormat("#.###############");
        // df.setRoundingMode(RoundingMode.CEILING);
        if(res > 10e15){
            return String.valueOf(res);
        }

        return String.valueOf(df.format(res));

    } 

    private static double factorial(double operand1){
        double r = 1;
        while(operand1 > 1){
            r *= operand1--;
        }
        return r;
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
        }else if(opToken.value.equals(Token.RADICAL)){
            return Math.pow(operand2, 1.0/operand1);
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
        }else if(opToken.value.equals(Token.POWER) || opToken.value.equals(Token.RADICAL)){
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
        exp.setDegreeAngle(true);

        String value = exp.eval();
        System.out.println(value);

    }
}