import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ExpressionProcessor {

    // Define operator precedence
    private static int precedence(String operator) {
        switch (operator) {
            case "+":
            case "-":
                return 1;
            case "*":
            case "/":
                return 2;
            case "^":
                return 3;
            default:
                return -1;
        }
    }

    // Convert infix to postfix
    public static String infixToPostfix(String infix) {
        StringBuilder postfix = new StringBuilder();
        Stack<String> operators = new Stack<>();
        List<String> tokens = tokenize(infix);

        for (String token : tokens) {
            if (isNumeric(token) || token.equals("x")) {
                postfix.append(token).append(" ");
            } else if (token.equals("(")) {
                operators.push(token);
            } else if (token.equals(")")) {
                while (!operators.isEmpty() && !operators.peek().equals("(")) {
                    postfix.append(operators.pop()).append(" ");
                }
                operators.pop(); // Remove '('
            } else { // Operator
                while (!operators.isEmpty() && precedence(operators.peek()) >= precedence(token)) {
                    postfix.append(operators.pop()).append(" ");
                }
                operators.push(token);
            }
        }

        while (!operators.isEmpty()) {
            postfix.append(operators.pop()).append(" ");
        }

        return postfix.toString().trim();
    }

    // Convert infix to prefix
    public static String infixToPrefix(String infix) {
        StringBuilder prefix = new StringBuilder();
        Stack<String> operators = new Stack<>();
        List<String> tokens = tokenize(infix);
        Collections.reverse(tokens);

        for (int i = 0; i < tokens.size(); i++) {
            String token = tokens.get(i);
            if (token.equals("(")) tokens.set(i, ")");
            else if (token.equals(")")) tokens.set(i, "(");
        }

        for (String token : tokens) {
            if (isNumeric(token) || token.equals("x")) {
                prefix.append(token).append(" ");
            } else if (token.equals("(")) {
                operators.push(token);
            } else if (token.equals(")")) {
                while (!operators.isEmpty() && !operators.peek().equals("(")) {
                    prefix.append(operators.pop()).append(" ");
                }
                operators.pop(); // Remove '('
            } else { // Operator
                while (!operators.isEmpty() && precedence(operators.peek()) > precedence(token)) {
                    prefix.append(operators.pop()).append(" ");
                }
                operators.push(token);
            }
        }

        while (!operators.isEmpty()) {
            prefix.append(operators.pop()).append(" ");
        }

        return new StringBuilder(prefix.toString().trim()).reverse().toString();
    }

    // Evaluate postfix expression for a given x
    public static double evaluatePostfix(String postfix, double x) {
        Stack<Double> stack = new Stack<>();
        List<String> tokens = Arrays.asList(postfix.split(" "));

        for (String token : tokens) {
            if (isNumeric(token)) {
                stack.push(Double.parseDouble(token));
            } else if (token.equals("x")) {
                stack.push(x);
            } else { // Operator
                double b = stack.pop();
                double a = stack.pop();
                switch (token) {
                    case "+":
                        stack.push(a + b);
                        break;
                    case "-":
                        stack.push(a - b);
                        break;
                    case "*":
                        stack.push(a * b);
                        break;
                    case "/":
                        stack.push(a / b);
                        break;
                    case "^":
                        stack.push(Math.pow(a, b));
                        break;
                }
            }
        }
        return stack.pop();
    }

    // Tokenize the infix expression
    private static List<String> tokenize(String infix) {
        List<String> tokens = new ArrayList<>();
        StringBuilder number = new StringBuilder();
        for (int i = 0; i < infix.length(); i++) {
            char c = infix.charAt(i);
            if (Character.isDigit(c) || c == '.') {
                number.append(c);
            } else {
                if (number.length() > 0) {
                    tokens.add(number.toString());
                    number.setLength(0);
                }
                if (c == '-' && (i == 0 || infix.charAt(i - 1) == '(')) {
                    number.append(c); // Negative sign
                } else if (!Character.isWhitespace(c)) {
                    tokens.add(String.valueOf(c));
                }
            }
        }
        if (number.length() > 0) {
            tokens.add(number.toString());
        }
        return tokens;
    }

    // Check if a string is numeric
    private static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Draw the graph
    private static void drawGraph(List<Double> xValues, List<Double> yValues) {
        JFrame frame = new JFrame("Graph of the Function");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 800);

        frame.add(new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int width = getWidth();
                int height = getHeight();
                int originX = width / 2;
                int originY = height / 2;

                // Draw axes
                g2d.drawLine(0, originY, width, originY); // x-axis
                g2d.drawLine(originX, 0, originX, height); // y-axis

                // Scale factors
                double xScale = width / (xValues.size() * 1.0);
                double yScale = height / (2 * Collections.max(yValues));

                // Draw graph
                g2d.setColor(Color.RED);
                for (int i = 1; i < xValues.size(); i++) {
                    int x1 = (int) (originX + xValues.get(i - 1) * xScale);
                    int y1 = (int) (originY - yValues.get(i - 1) * yScale);
                    int x2 = (int) (originX + xValues.get(i) * xScale);
                    int y2 = (int) (originY - yValues.get(i) * yScale);

                    g2d.drawLine(x1, y1, x2, y2);
                }
            }
        });

        frame.setVisible(true);
    }

    // Main method
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter an infix expression: ");
        String infix = scanner.nextLine();

        String postfix = infixToPostfix(infix);
        String prefix = infixToPrefix(infix);

        System.out.println("Postfix expression: " + postfix);
        System.out.println("Prefix expression: " + prefix);

        if (postfix.contains("x")) {
            // Handle variable expression
            List<Double> xValues = new ArrayList<>();
            List<Double> yValues = new ArrayList<>();

            for (double x = -50; x <= 50; x += 1) {
                xValues.add(x);
                yValues.add(evaluatePostfix(postfix, x));
            }

            drawGraph(xValues, yValues);
        } else {
            // Handle numerical expression
            double result = evaluatePostfix(postfix, 0);
            System.out.println("Evaluated result: " + result);
        }
    }
}