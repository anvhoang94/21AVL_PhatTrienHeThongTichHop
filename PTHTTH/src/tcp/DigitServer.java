package tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class DigitServer {
	private static final int PORT = 5002;

    public static void main(String[] args) {
        try (ServerSocket server = new ServerSocket(PORT)) {
            System.out.println("Digit Server listening on port " + PORT);
            while (true) {
                try (Socket socket = server.accept()) {
                    serve(socket);
                } catch (IOException e) {
                    System.err.println("Lỗi phiên client: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Không mở được server: " + e.getMessage());
        }
    }

    static void serve(Socket socket) throws IOException {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(
                socket.getInputStream(), StandardCharsets.UTF_8));
             PrintWriter out = new PrintWriter(new OutputStreamWriter(
                socket.getOutputStream(), StandardCharsets.UTF_8), true)) {
            
            String request;
            while ((request = in.readLine()) != null) {
                String response = process(request);
                out.println(response);
                if (request.trim().equalsIgnoreCase("QUIT")) break;
            }
        }
    }

    static String process(String request) {
        String trimmed = request.trim();
        if (trimmed.equalsIgnoreCase("QUIT")) return "OK BYE";

        // Bắt lỗi: chuỗi rỗng, nhiều hơn 1 ký tự, hoặc chứa khoảng trắng ở giữa (ví dụ "1 2")
        if (trimmed.length() != 1) {
            return "ERR INVALID_DIGIT";
        }

        char c = trimmed.charAt(0);
        return switch (c) {
            case '0' -> "Không";
            case '1' -> "Một";
            case '2' -> "Hai";
            case '3' -> "Ba";
            case '4' -> "Bốn";
            case '5' -> "Năm";
            case '6' -> "Sáu";
            case '7' -> "Bảy";
            case '8' -> "Tám";
            case '9' -> "Chín";
            default -> "ERR INVALID_DIGIT";
        };
    }
}
