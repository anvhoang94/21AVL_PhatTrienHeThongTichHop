package tcp;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class DateTimeTcpClient {
	public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 5003);
             BufferedReader console = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
             PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true)) {

            System.out.println("TCP Connected. Enter DATE, TIME, DATETIME, QUIT:");
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
            System.err.println("Lỗi: " + e.getMessage());
        }
    }
}
