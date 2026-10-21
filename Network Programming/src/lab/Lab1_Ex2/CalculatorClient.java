package lab.Lab1_Ex2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class CalculatorClient extends JFrame {

    private JTextField num1Field, num2Field, resultField;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public CalculatorClient() {
        super("Network Calculator");

        setLayout(new GridLayout(3, 1));

        JPanel inputPanel = new JPanel(new FlowLayout());
        inputPanel.add(new JLabel("Num 1:"));
        num1Field = new JTextField(7);
        inputPanel.add(num1Field);
        inputPanel.add(new JLabel("Num 2:"));
        num2Field = new JTextField(7);
        inputPanel.add(num2Field);
        add(inputPanel);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton addBtn = new JButton("+");
        JButton subBtn = new JButton("-");
        JButton mulBtn = new JButton("*");
        JButton divBtn = new JButton("/");
        buttonPanel.add(addBtn);
        buttonPanel.add(subBtn);
        buttonPanel.add(mulBtn);
        buttonPanel.add(divBtn);
        add(buttonPanel);

        JPanel resultPanel = new JPanel(new FlowLayout());
        resultPanel.add(new JLabel("Result:"));
        resultField = new JTextField(15);
        resultField.setEditable(false);
        resultPanel.add(resultField);
        add(resultPanel);

        setSize(350, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);

        try {
            InetAddress address = InetAddress.getByName("localhost");
            socket = new Socket(address, 1900);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Could not connect to server.\nStart the server first.", "Connection Error", JOptionPane.ERROR_MESSAGE);
        }

        ActionListener opListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendCalculation(e.getActionCommand());
            }
        };

        addBtn.addActionListener(opListener);
        subBtn.addActionListener(opListener);
        mulBtn.addActionListener(opListener);
        divBtn.addActionListener(opListener);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                try {
                    if (out != null) out.println("END");
                    if (socket != null) socket.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    private void sendCalculation(String operator) {
        if (out == null || in == null) {
            resultField.setText("Disconnected");
            return;
        }

        String num1 = num1Field.getText().trim();
        String num2 = num2Field.getText().trim();

        if (num1.isEmpty() || num2.isEmpty()) {
            resultField.setText("Enter both numbers");
            return;
        }

        out.println(num1 + "," + num2 + "," + operator);

        try {
            String response = in.readLine();
            resultField.setText(response);
        } catch (IOException ex) {
            resultField.setText("Error reading server");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new CalculatorClient();
            }
        });
    }
}
