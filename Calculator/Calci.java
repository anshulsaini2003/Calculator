import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Calci extends JFrame implements ActionListener {
    private JTextField displayField;
    private JButton[] buttons;
    private String[] buttonLabels = {
            "7", "8", "9", "/",
            "4", "5", "6", "*",
            "1", "2", "3", "-",
            "0", ".", "=", "+"
    };
    private String currentInput = "";

    public Calci() {
        setTitle("Bharat Calculator");
        setSize(300, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        displayField = new JTextField();
        displayField.setFont(new Font("Arial", Font.PLAIN, 24));
        displayField.setHorizontalAlignment(JTextField.RIGHT);
        displayField.setEditable(false);
        add(displayField, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(4, 4, 5, 5));
        buttons = new JButton[buttonLabels.length];
        for (int i = 0; i < buttonLabels.length; i++) {
            buttons[i] = new JButton(buttonLabels[i]);
            buttons[i].setFont(new Font("Arial", Font.PLAIN, 24));
            buttons[i].addActionListener(this);
            buttonPanel.add(buttons[i]);
        }
        add(buttonPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        if (command.equals("=")) {
            try {
                String result = evaluateExpression(currentInput);
                displayField.setText(result);
                currentInput = result;
            } catch (ArithmeticException ex) {
                displayField.setText("Error: Cannot divide by zero.");
                currentInput = "";
            }
        } else if (command.equals("C")) {
            currentInput = "";
            displayField.setText("");
        } else {
            currentInput += command;
            displayField.setText(currentInput);
        }
    }

    private String evaluateExpression(String expression) {
        try {
            return String.valueOf(eval(expression));
        } catch (Exception e) {
            return "Error";
        }
    }

    private double eval(String expression) {
        return new Object() {
            int pos = -1, ch;

            void nextChar() {
                ch = (++pos < expression.length()) ? expression.charAt(pos) : -1;
            }

            boolean isDigit(int c) {
                return c >= '0' && c <= '9';
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < expression.length()) {
                    throw new RuntimeException("Unexpected: " + (char) ch);
                }
                return x;
            }

            double parseExpression() {
                double x = parseTerm();
                while (true) {
                    if (ch == '+') {
                        nextChar();
                        x += parseTerm();
                    } else if (ch == '-') {
                        nextChar();
                        x -= parseTerm();
                    } else {
                        return x;
                    }
                }
            }

            double parseTerm() {
                double x = parseFactor();
                while (true) {
                    if (ch == '*') {
                        nextChar();
                        x *= parseFactor();
                    } else if (ch == '/') {
                        nextChar();
                        x /= parseFactor();
                    } else {
                        return x;
                    }
                }
            }

            double parseFactor() {
                if (ch == '(') {
                    nextChar();
                    double x = parseExpression();
                    if (ch != ')') {
                        throw new RuntimeException("Unclosed parenthesis");
                    }
                    nextChar();
                    return x;
                }
                if (isDigit(ch) || ch == '.') {
                    StringBuilder sb = new StringBuilder();
                    while (isDigit(ch) || ch == '.') {
                        sb.append((char) ch);
                        nextChar();
                    }
                    return Double.parseDouble(sb.toString());
                }
                throw new RuntimeException("Unexpected: " + (char) ch);
            }
        }.parse();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Calci());
    }
}
