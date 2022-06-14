package teamturingbennett;

import java.util.HashMap;
import java.util.function.DoubleUnaryOperator;

public class Parser {

    // instance variables
    private final HashMap<String, DoubleUnaryOperator> map;
    private HashMap<String, Double> vars;
    private int pos;    // keep track of our position in the string
    private int val;    // keep track of the last char we consumed
    private String input;

    
    public Parser() {
        this.map = new HashMap<>();
        initFuncMap();
        reset();
    }

    
    private void initFuncMap() {
        map.put("sin", Math::sin);
        map.put("cos", Math::cos);
        map.put("tan", Math::tan);
        map.put("asin", Math::asin);
        map.put("acos", Math::acos);
        map.put("atan", Math::atan);
        map.put("sqrt", Math::sqrt);
        map.put("√", Math::sqrt);
        map.put("log", Math::log);
        map.put("exp", Math::exp);
        map.put("sec", (val) -> (1.0 / Math.cos(val)));
        map.put("csc", (val) -> (1.0 / Math.sin(val)));
        map.put("cot", (val) -> (1.0 / Math.tan(val)));
    }

    
    private void next() {
        val = (++pos < input.length() ? input.charAt(pos) : -1);
    }

    
    private boolean consume(char c) {
        if (val == c) {
            next();
            return true;
        }
        return false;
    }

    
    private Expression parse() {
        next(); //consume the next character
        Expression x = parseTier1();
        if (pos < input.length()) {
            throw new RuntimeException("unexpected char: " + (char) val);
        }
        return x;
    }

    
    private Expression parseTier1() {
        Expression x = parseTier2();
        while (true) {
            if (consume('+')) {
                Expression left = x, right = parseTier2();
                x = () -> left.eval() + right.eval();
            } else if (consume('-')) {
                Expression left = x, right = parseTier2();
                x = () -> left.eval() - right.eval();
            } else {
                return x;
            }
        }
    }

    
    private Expression parseTier2() {
        Expression x = parseTier3();
        while (true) {
            if (consume('*')) {
                Expression left = x, right = parseTier3();
                x = () -> left.eval() * right.eval();
            } else if (consume('/')) {
                Expression left = x, right = parseTier3();
                x = () -> left.eval() / right.eval();
            }else if (consume('%')) {
                Expression left = x, right = parseTier3();
                x = () -> left.eval() % right.eval();
            } else {
                return x;
            }
        }
    }

    
    private Expression parseTier3() {
        Expression x = parseTier4();
        while (true) {
            // handle exponentiation & nth roots/fractional exponents
            if (consume('^')) {
                Expression left = x, right = parseTier4();
                x = () -> Math.pow(left.eval(), right.eval());
            } else if (consume('@')) {
                Expression left = x, right = parseTier4();
                x = () -> Math.pow(left.eval(), (1.0 / right.eval()));
            } else {
                return x;
            }
        }
    }

    
    private Expression parseTier4() {
        int start = this.pos;
        Expression x;   // declare the Expression we're going to return
        if (consume('+')) {
            x = parseTier4();
            return x;
        } else if (consume('-')) {
            Expression right = parseTier4();
            x = () -> (-1.0 * right.eval());
            return x;
        }

        if (consume('(')) {
            x = parseTier1();     // branch our tree until we hit the ')'
            consume(')');
            return x;
        } else if (isNumber()) {
            while(isNumber()) {
                next();     // advance our parser to the first non-digit or '.'
            }
            double d = Double.parseDouble(input.substring(start, this.pos));
            x = () -> d;
            return x;
        } else if (isAlpha()) {     // handle unary functions, and variables
            while (isAlpha()) {
                next();     // advance our parser to the first non-alpha
            }
            String fn = input.substring(start, this.pos); // get the name of the function
            x = parseTier4();    // get the value the function will operate on
            if (map.containsKey(fn)) {
                Expression arg = x;
                DoubleUnaryOperator func = map.get(fn);
                x = () -> func.applyAsDouble(arg.eval());
            } else {
                x = () -> vars.get(fn);
            }
            return x;
        } else {
            System.out.println("Unexpected operation: " + val + ", " + (char) val);
            return null;
        }
    }

   
    private boolean isNumber() {
        return Character.isDigit(val) || val == '.';
    }

    
    private boolean isAlpha() {
        return Character.isAlphabetic(val) &&
	        !(val == '(' || val == ')');
    }

    
    private void reset() {
        this.pos = -1;  // set the starting position for our loop/parser
        this.val = -1;
        this.input = "";
    }

    
    private String formatInput(String in) {
        return in.replace(" ", "")    // strip spaces
                .replace("ⁿ√x", "@")    // use '@' to denote 'nth' roots
                .replace("√", "sqrt")   // handle square roots
                .replace("×", "*")      // convert 'pretty' * symbols
                .replace("÷", "/");     // convert 'pretty' / symbols
    }

    public Expression eval(String exp, HashMap<String, Double> vars) {
        this.vars = vars;
        return this.eval(exp);
    }

    public Expression eval(String exp) {
        try {
            this.input = formatInput(exp);
            return this.parse();
        } finally {
            reset();    // reset our parser
        }
    }
}
