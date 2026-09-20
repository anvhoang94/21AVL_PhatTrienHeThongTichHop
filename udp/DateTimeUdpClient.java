package udp;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;

public class DateTimeUdpClient {
	public static void main(String[] args) {
        try (DatagramSocket socket = new DatagramSocket();
             BufferedReader console = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8))) {
            
            socket.setSoTimeout(3000); // Yêu cầu timeout cho UDP
            InetAddress server = InetAddress.getByName("localhost");
            
            System.out.println("UDP Ready. Enter DATE, TIME, DATETIME (QUIT to exit app):");
            String request;
            while ((request = console.readLine()) != null) {
                if (request.trim().equalsIgnoreCase("QUIT")) break; // Chỉ thoát app cục bộ, không gửi server
                
                byte[] data = request.getBytes(StandardCharsets.UTF_8);
                socket.send(new DatagramPacket(data, data.length, server, 5004));
                
                byte[] buffer = new byte[1024];
                DatagramPacket response = new DatagramPacket(buffer, buffer.length);
                
                try {
                    socket.receive(response);
                    String text = new String(response.getData(), response.getOffset(), response.getLength(), StandardCharsets.UTF_8);
                    System.out.println("Server: " + text);
                } catch (SocketTimeoutException e) {
                    System.out.println("Server: Hết 3 giây không nhận được phản hồi (Timeout)");
                }
            }
        } catch (Exception e) {
            System.err.println("Lỗi: " + e.getMessage());
        }
    }
}
