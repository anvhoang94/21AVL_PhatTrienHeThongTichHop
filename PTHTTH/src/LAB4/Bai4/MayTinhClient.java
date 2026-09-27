package LAB4.Bai4;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class MayTinhClient {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String host = args.length > 0 ? args[0] : "localhost";
		int port = args.length > 1 ? Integer.parseInt(args[1]) : 5000;
		
		try (Socket socket = new Socket(host, port);
				BufferedReader console = new BufferedReader(
						new InputStreamReader(System.in, StandardCharsets.UTF_8));
				BufferedReader in = new BufferedReader(
						new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
				PrintWriter out = new PrintWriter(
						new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true)){
			
			System.out.println("Đã kết nối tới Server máy tính từ xa (" + host + ":" + port + ")");
            System.out.println("Cú pháp: CALC    (Ví dụ: CALC + 100 200)");
            System.out.println("Nhập QUIT để thoát.\n");
            
            String line;
            while ((line = console.readLine()) != null) {
            	if (line.trim().isEmpty()) continue;
            	
            	out.println(line);
            	String response = in.readLine();
            	if (response == null) {
            		System.out.println("Server đã ngắt kết nối");
            		break;
            	}
            	
            	System.out.println("Server: " + response);
            	
            	if(line.trim().equalsIgnoreCase("QUIT")) {
            		break;
            	}
            }
		} catch (IOException e) {
			System.err.println("Lỗi kết nối: " + e.getMessage());
		}
	}

}
