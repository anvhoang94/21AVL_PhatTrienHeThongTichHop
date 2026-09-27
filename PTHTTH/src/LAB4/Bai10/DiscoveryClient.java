package LAB4.Bai10;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DiscoveryClient {
    private static final int DISCOVERY_PORT = 8888;
    private static final int TIMEOUT_MS = 2500;

    // Lớp chứa thông tin dịch vụ tìm thấy
    public static class DiscoveredService {
        public String ip;
        public String name;
        public int port;
        public String version;

        public DiscoveredService(String ip, String name, int port, String version) {
            this.ip = ip;
            this.name = name;
            this.port = port;
            this.version = version;
        }

        @Override
        public String toString() {
            return String.format("%s (%s) tại %s:%d", name, version, ip, port);
        }
    }

    public static void main(String[] args) {
        System.out.println("=== BẮT ĐẦU TÌM KIẾM DỊCH VỤ (DISCOVERY) ===");

        List discoveredList = new ArrayList<>();
        Set uniqueEndpoints = new HashSet<>();

        try (DatagramSocket udpSocket = new DatagramSocket()) {
            udpSocket.setBroadcast(true);
            udpSocket.setSoTimeout(TIMEOUT_MS);

            byte[] requestData = "DISCOVER_SERVICE".getBytes(StandardCharsets.UTF_8);

            // Gửi tới broadcast và loopback để kiểm tra cả trên 1 máy
            DatagramPacket broadcastPacket = new DatagramPacket(
                    requestData, requestData.length, InetAddress.getByName("255.255.255.255"), DISCOVERY_PORT
            );
            udpSocket.send(broadcastPacket);

            DatagramPacket loopbackPacket = new DatagramPacket(
                    requestData, requestData.length, InetAddress.getByName("127.0.0.1"), DISCOVERY_PORT
            );
            udpSocket.send(loopbackPacket);

            byte[] buffer = new byte[1024];
            long endTime = System.currentTimeMillis() + TIMEOUT_MS;

            while (System.currentTimeMillis() < endTime) {
                try {
                    DatagramPacket responsePacket = new DatagramPacket(buffer, buffer.length);
                    udpSocket.receive(responsePacket);

                    String text = new String(
                            responsePacket.getData(),
                            responsePacket.getOffset(),
                            responsePacket.getLength(),
                            StandardCharsets.UTF_8
                    ).trim();

                    // Cú pháp phản hồi: SERVICE tên_dịch_vụ tcp_port phiên_bản
                    String[] parts = text.split("\\s+");
                    if (parts.length >= 4 && "SERVICE".equalsIgnoreCase(parts[0])) {
                        String ip = responsePacket.getAddress().getHostAddress();
                        int tcpPort = Integer.parseInt(parts[2]);
                        String endpointKey = ip + ":" + tcpPort;

                        // Lọc phản hồi trùng
                        if (!uniqueEndpoints.contains(endpointKey)) {
                            uniqueEndpoints.add(endpointKey);
                            DiscoveredService service = new DiscoveredService(ip, parts[1], tcpPort, parts[3]);
                            discoveredList.add(service);
                            System.out.println("  [+] Tìm thấy: " + service);
                        }
                    }
                } catch (SocketTimeoutException e) {
                    break;
                }
            }

        } catch (IOException e) {
            System.err.println("Lỗi UDP Discovery: " + e.getMessage());
        }

        // Bắt buộc kiểm tra rỗng trước khi lấy phần tử đầu tiên
        if (discoveredList.isEmpty()) {
            System.out.println("\nKhông tìm thấy dịch vụ nào đang chạy. Vui lòng bật ServiceNodeServer trước!");
            return;
        }

        System.out.println("\n--- DANH SÁCH DỊCH VỤ KHẢ DỤNG ---");
        for (int i = 0; i < discoveredList.size(); i++) {
            System.out.printf("[%d] %s%n", i + 1, discoveredList.get(i));
        }

        // Lấy dịch vụ đầu tiên để kết nối TCP
        DiscoveredService target = (DiscoveredService) discoveredList.get(0);
        System.out.printf("%n-> Đang kết nối TCP tới %s:%d...%n", target.ip, target.port);
        connectTcpService(target.ip, target.port);
    }

    private static void connectTcpService(String host, int port) {
        try (Socket socket = new Socket(host, port);
             BufferedReader console = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
             PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true)) {

            System.out.println("Kết nối TCP thành công!");
            System.out.println("Server: " + in.readLine());

            String line;
            while ((line = console.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                out.println(line);

                String resp = in.readLine();
                if (resp == null) {
                    System.out.println("Server đã đóng kết nối.");
                    break;
                }
                System.out.println("Server: " + resp);

                if (line.trim().equalsIgnoreCase("QUIT")) {
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("Lỗi kết nối TCP: " + e.getMessage());
        }
    }
}