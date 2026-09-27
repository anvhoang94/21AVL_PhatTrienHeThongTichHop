package LAB4.Bai9;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;

public class MulticastReceiver {
    private static final String MULTICAST_IP = "239.255.0.1";
    private static final int PORT = 5002;

    public static void main(String[] args) {
        String receiverName = args.length > 0 ? args[0] : "Receiver_" + (System.currentTimeMillis() % 1000);
        System.out.println("=== KHỞI ĐỘNG " + receiverName + " ===");

        try {
            InetAddress groupAddress = InetAddress.getByName(MULTICAST_IP);
            InetSocketAddress group = new InetSocketAddress(groupAddress, PORT);

            // Tìm card mạng thật có hỗ trợ IPv4 Multicast
            NetworkInterface netIf = findActiveMulticastInterface();
            if (netIf == null) {
                System.err.println("Lỗi: Không tìm thấy card mạng IPv4 nào hỗ trợ Multicast!");
                return;
            }
            System.out.println("Sử dụng Card mạng: " + netIf.getDisplayName());

            // Mở MulticastSocket trên cổng PORT
            try (MulticastSocket socket = new MulticastSocket(PORT)) {
                // Tham gia nhóm multicast
                socket.joinGroup(group, netIf);
                System.out.println(receiverName + " đã tham gia nhóm " + MULTICAST_IP + ":" + PORT);
                System.out.println("Đang chờ thông báo từ Sender...\n");

                byte[] buffer = new byte[4096];

                while (true) {
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    socket.receive(packet);

                    String message = new String(
                            packet.getData(),
                            packet.getOffset(),
                            packet.getLength(),
                            StandardCharsets.UTF_8
                    );

                    System.out.printf("[%s] Nhận từ %s: %s%n",
                            receiverName, packet.getSocketAddress(), message);

                    if (message.trim().equalsIgnoreCase("SHUTDOWN")) {
                        System.out.println("Nhận tín hiệu dừng từ Sender. Đang rời nhóm...");
                        break;
                    }
                }

                // Rời nhóm an toàn trước khi đóng socket
                socket.leaveGroup(group, netIf);
                System.out.println(receiverName + " đã rời nhóm an toàn.");
            }

        } catch (IOException e) {
            System.err.println("Lỗi Receiver: " + e.getMessage());
        }
    }

    private static NetworkInterface findActiveMulticastInterface() throws SocketException {
        Enumeration interfaces = NetworkInterface.getNetworkInterfaces();
        if (interfaces == null) {
            return null;
        }

        NetworkInterface loopbackIf = null;

        while (interfaces.hasMoreElements()) {
            NetworkInterface ni = (NetworkInterface) interfaces.nextElement();

            // Card mạng phải đang hoạt động và hỗ trợ Multicast
            if (!ni.isUp() || !ni.supportsMulticast()) {
                continue;
            }

            // Kiểm tra xem card mạng có địa chỉ IPv4 hay không
            boolean hasIpv4 = false;
            Enumeration addresses = ni.getInetAddresses();
            while (addresses.hasMoreElements()) {
                InetAddress addr = (InetAddress) addresses.nextElement();
                if (addr instanceof Inet4Address) {
                    hasIpv4 = true;
                    break;
                }
            }

            if (!hasIpv4) {
                continue;
            }

            String name = ni.getDisplayName().toLowerCase();
            // Bỏ qua các card mạng ảo của VMware, VirtualBox, Npcap gây lỗi
            if (name.contains("vmware") || name.contains("virtual") || name.contains("npcap")) {
                continue;
            }

            if (!ni.isLoopback()) {
                return ni; // Ưu tiên card Wi-Fi / Ethernet thật có IPv4
            } else if (loopbackIf == null) {
                loopbackIf = ni;
            }
        }

        if (loopbackIf != null) {
            return loopbackIf;
        }

        try {
            return NetworkInterface.getByInetAddress(InetAddress.getLoopbackAddress());
        } catch (Exception e) {
            return null;
        }
    }
}