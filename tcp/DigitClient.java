package tcp;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class DigitClient {
	public static void main(String[] args) {
        String host = args.length > 0 ? args[0] : "localhost";
        int port = args.length > 1 ? Integer.parseInt(args[1]) : 5002;

        try (Socket socket = new Socket(host, port);
             BufferedReader console = new BufferedReader(
                new InputStreamReader(System.in, StandardCharsets.UTF_8));
             BufferedReader in = new BufferedReader(new InputStreamReader(
                socket.getInputStream(), StandardCharsets.UTF_8));
             PrintWriter out = new PrintWriter(new OutputStreamWriter(
                socket.getOutputStream(), StandardCharsets.UTF_8), true)) {

            System.out.println("Connected. Nhập 1 chữ số (0-9) hoặc QUIT để thoát:");
            String request;
            while ((request = console.readLine()) != null) {
                out.println(request);
                String response = in.readLine();
                if (response == null) {
                    System.out.println("Server closed the connection");
                    break;
                }
                System.out.println("Server: " + response);
                if (request.trim().equalsIgnoreCase("QUIT")) break;
            }
        } catch (Exception e) {
            System.err.println("Lỗi kết nối: " + e.getMessage());
        }
    }
}
