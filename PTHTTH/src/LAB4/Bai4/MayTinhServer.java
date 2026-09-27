package LAB4.Bai4;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MayTinhServer {
	private static final int PORT = 5000;
	private static final int MAX_CLIENTS = 10;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ExecutorService pool = Executors.newFixedThreadPool(MAX_CLIENTS);
		try (ServerSocket server = new ServerSocket(PORT)) {
			System.out.println("Máy tính Server đang nghe trên cổng " + PORT + "...");
			
			while (true) {
				Socket socket = server.accept();
				pool.submit(() -> handleClient(socket));
			}
		} catch (IOException e) {
			System.out.println("Lỗi server: " + e.getMessage());
		} finally {
			pool.shutdown();
		}
	}
	
	private static void handleClient(Socket socket) {
		String clientAddress = socket.getRemoteSocketAddress().toString();
		System.out.println("Client đã kết nối: " + clientAddress);
		
		try (socket;
				BufferedReader in = new BufferedReader(
						new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
				PrintWriter out = new PrintWriter(
						new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true)){
			String line;
			while ((line = in.readLine())!=null) {
				String trimmed = line.trim();
				if (trimmed.equalsIgnoreCase("QUIT")) {
					out.println("OK BYE");
					break;
				}
				
				String response = calculate(trimmed);
				out.println(response);
			}
		} catch (IOException e) {
			System.out.println("Lỗi giao tiếp với " + clientAddress + ": " + e.getMessage());
		} finally {
			System.out.println("Client đã ngắt kết nối: " + clientAddress);
		}
	}
	
	public static String calculate(String request) {
		// Tách chuỗi theo 1 hoặc nhiều khoảng trắng
		String[] parts = request.split("\\s+");
		
		//Kiểm tra tiền tố CALC và số lượng đối số (bắt buộc đúng 4 phần tử )
		if (parts.length != 4 || !parts[0].equalsIgnoreCase("CALC")) {
			return "ERR INVALID_FORMAT";
		}
		
		String op = parts[1];
		double num1, num2;
		
		//Kiểm tra định dạng số
		try {
			num1 = Double.parseDouble(parts[2]);
			num2 = Double.parseDouble(parts[3]);
		} catch (NumberFormatException e) {
			return "ERR INVALID_NUMBER";
		}
		
		// Xử lý tính toán theo phần tử
		switch (op) {
		case "+":
			return "OK" + formatResult(num1 + num2);
		case "-":
			return "OK" + formatResult(num1 - num2);
		case "*":
			return "OK" + formatResult(num1 * num2);
		case "/":
			if (num2 == 0) {
				return "ERR DIVIDE_BY_ZERO";
			}
			return "OK" + formatResult(num1 / num2);
		default:
			return "ERR UNSUPPORTED_OPERATOR";
		}
	}
	
	// Định dạng gọn kết quả: nếu là số nguyên thì bỏ phần 0
	private static String formatResult(double value) {
		if (value == (long) value) {
			return String.format("%d", (long) value);
		}
		return String.valueOf(value);
	}
}
