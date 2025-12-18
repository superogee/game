import javax.swing.*;
import java.awt.event.ActionListener;

class Main extends JFrame {

    String current = "";
    double first = 0;
    String operation = "";

    Main() {
        super("Calculator");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setBounds(0, 0, 360, 450);

        ImageIcon bgIcon = new ImageIcon("background.jpg");
        JLabel background = new JLabel(bgIcon);
        background.setBounds(0, 0, 360, 450);
        setContentPane(background);
        background.setLayout(null);

        JTextField textField = new JTextField();
        textField.setBounds(20, 20, 300, 40);
        background.add(textField);

        JButton[] numbers = new JButton[10];
        int x = 20, y = 80;

        for (int i = 1; i <= 9; i++) {
            numbers[i] = new JButton(String.valueOf(i));
            numbers[i].setBounds(x, y, 60, 40);
            background.add(numbers[i]);

            int number = i;
            numbers[i].addActionListener(e -> {
                current += number;
                textField.setText(current);
            });

            x += 80;
            if (i % 3 == 0) {
                x = 20;
                y += 60;
            }
        }

        numbers[0] = new JButton("0");
        numbers[0].setBounds(20, 260, 60, 40);
        background.add(numbers[0]);
        numbers[0].addActionListener(e -> {
            current += "0";
            textField.setText(current);
        });

        JButton plus = new JButton("+");
        plus.setBounds(260, 80, 60, 40);
        background.add(plus);

        JButton minus = new JButton("-");
        minus.setBounds(260, 140, 60, 40);
        background.add(minus);

        JButton multiply = new JButton("*");
        multiply.setBounds(260, 200, 60, 40);
        background.add(multiply);

        JButton divide = new JButton("/");
        divide.setBounds(260, 260, 60, 40);
        background.add(divide);

        ActionListener opListener = e -> {
            first = Double.parseDouble(current);
            operation = ((JButton) e.getSource()).getText();
            current = "";
        };

        plus.addActionListener(opListener);
        minus.addActionListener(opListener);
        multiply.addActionListener(opListener);
        divide.addActionListener(opListener);

        JButton equals = new JButton("=");
        equals.setBounds(100, 260, 140, 40);
        background.add(equals);

        equals.addActionListener(e -> {
            double second = Double.parseDouble(current);
            double result = 0;

            switch (operation) {
                case "+": result = first + second; break;
                case "-": result = first - second; break;
                case "*": result = first * second; break;
                case "/": result = second != 0 ? first / second : 0; break;
            }

            current = String.valueOf(result);
            textField.setText(current);
        });

        JButton AC = new JButton("C");
        AC.setBounds(20, 320, 300, 40);
        background.add(AC);

        AC.addActionListener(e -> {
            current = "";
            first = 0;
            operation = "";
            textField.setText("");
        });
    }

    public static void main(String[] args) {
        new Main().setVisible(true);
    }
}
