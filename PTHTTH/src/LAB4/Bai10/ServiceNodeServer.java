package LAB4.Bai10;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ServiceNodeServer {
    private static final int DISCOVERY_PORT = 8888;
    private static final int TCP_SERVICE_PORT = 5000;
    private static final String SERVICE_NAME = "CALC_SERVICE";
    private static final String VERSION = "v1.0";

    public static void main(String[] args) {
        System.out.println("=== KHỞI ĐỘNG DỊCH VỤ SERVER ===");

        // 1. Luồng chạy TCP Service
        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(TCP_SERVICE_PORT)) {
                System.out.println("[TCP] Dịch vụ đang chạy trên cổng " + TCP_SERVICE_PORT);
                while (true) {
                    Socket socket = serverSocket.accept();
                    new Thread(() -> handleTcpClient(socket)).start();
                }
            } catch (IOException e) {
                System.err.println("[TCP] Lỗi Server: " + e.getMessage());
            }
        }).start();

        // 2. Luồng lắng nghe UDP Discovery
        new Thread(() -> {
            byte[] buffer = new byte[1024];
            try (DatagramSocket udpSocket = new DatagramSocket(DISCOVERY_PORT)) {
                System.out.println("[UDP] Bộ phản hồi Discovery lắng nghe trên cổng " + DISCOVERY_PORT);

                while (true) {
                    DatagramPacket requestPacket = new DatagramPacket(buffer, buffer.length);
                    udpSocket.receive(requestPacket);

                    String msg = new String(
                            requestPacket.getData(),
                            requestPacket.getOffset(),
                            requestPacket.getLength(),
                            StandardCharsets.UTF_8
                    ).trim();

                    if ("DISCOVER_SERVICE".equalsIgnoreCase(msg)) {
                        // Trả về định dạng: SERVICE   
                        String responseMsg = String.format("SERVICE %s %d %s", SERVICE_NAME, TCP_SERVICE_PORT, VERSION);
                        byte[] respData = responseMsg.getBytes(StandardCharsets.UTF_8);

                        DatagramPacket responsePacket = new DatagramPacket(
                                respData, respData.length,
                                requestPacket.getAddress(), requestPacket.getPort()
                        );
                        udpSocket.send(responsePacket);
                        System.out.printf("[UDP] Đã phản hồi discovery cho client: %s:%d%n",
                                requestPacket.getAddress().getHostAddress(), requestPacket.getPort());
                    }
                }
            } catch (IOException e) {
                System.err.println("[UDP] Lỗi Discovery: " + e.getMessage());
            }
        }).start();
    }

    private static void handleTcpClient(Socket socket) {
        String client = socket.getRemoteSocketAddress().toString();
        System.out.println("[TCP] Kết nối mới từ: " + client);
        try (socket;
             BufferedReader in = new BufferedReader(
                     new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
             PrintWriter out = new PrintWriter(
                     new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true)) {

            out.println("OK Chào mừng tới " + SERVICE_NAME + " " + VERSION + "! Nhập lệnh hoặc QUIT để thoát.");

            String line;
            while ((line = in.readLine()) != null) {
                line = line.trim();
                if (line.equalsIgnoreCase("QUIT")) {
                    out.println("OK BYE");
                    break;
                }
                out.println("ECHO: " + line);
            }
        } catch (IOException e) {
            System.err.println("[TCP] Ngắt kết nối với " + client + ": " + e.getMessage());
        }
    }
}