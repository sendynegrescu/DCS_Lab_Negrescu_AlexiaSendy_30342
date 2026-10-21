package lab.Lab1_Ex2;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class CalculatorServer {
    public static void main(String[] args) throws IOException {
        ServerSocket ss = null;

        try {
            ss = new ServerSocket(1900);
            System.out.println("Calculator Server is waiting for connections on port 1900...");

            while (true) {
                Socket s = ss.accept();

                InetSocketAddress remoteadr = (InetSocketAddress) s.getRemoteSocketAddress();
                System.out.println("New client connected: " + remoteadr.getHostName() + ":" + remoteadr.getPort());

                BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
                PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(s.getOutputStream())), true);

                String line = "";
                while ((line = in.readLine()) != null) {
                    if (line.equals("END")) {
                        break;
                    }
                    System.out.println("Server received: " + line);

                    String[] parts = line.split(",");
                    if (parts.length == 3) {
                        try {
                            double n1 = Double.parseDouble(parts[0].trim());
                            double n2 = Double.parseDouble(parts[1].trim());
                            String operator = parts[2].trim();
                            double result = 0;
                            boolean valid = true;

                            switch (operator) {
                                case "+": result = n1 + n2; break;
                                case "-": result = n1 - n2; break;
                                case "*": result = n1 * n2; break;
                                case "/":
                                    if (n2 == 0) {
                                        out.println("Error: Div by zero");
                                        valid = false;
                                    } else {
                                        result = n1 / n2;
                                    }
                                    break;
                                default:
                                    out.println("Error: Unknown operator");
                                    valid = false;
                            }
                            System.out.println("Result: " + result);
                            if (valid) {
                                out.println(String.valueOf(result));
                            }
                        } catch (NumberFormatException e) {
                            out.println("Error: Invalid numbers");
                        }
                    } else {
                        out.println("Error: Invalid format");
                    }
                }

                System.out.println("Client disconnected.");
                s.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (ss != null) ss.close();
        }
    }
}