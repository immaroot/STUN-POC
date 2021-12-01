import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.HashMap;

public class STUN {

    public static void main(String[] args) throws IOException {

        HashMap<String, InetSocketAddress> userMap = new HashMap<>();

        DatagramSocket socket = new DatagramSocket(9000);
        byte[] buf = new byte[256];
        DatagramPacket packet = new DatagramPacket(buf, buf.length);

        boolean end = false;

        while (!end) {
            System.out.println("Waiting to receive packet on port 9000");
            socket.receive(packet);
            System.out.println("Received packet...");

            InetAddress address = packet.getAddress();
            int port = packet.getPort();

            System.out.println("address: " + address.getHostAddress());
            System.out.println("port: " + port);

            String receivedMsg = new String(packet.getData(), 0, packet.getLength());

            System.out.println("Message: " + receivedMsg);

            if (receivedMsg.startsWith("END")) {
                end = true;
            } else {
                String senderName = receivedMsg.split(":")[0];
                String receiverName = receivedMsg.split(":")[1];

                if (userMap.containsKey(receiverName)) {
                    InetSocketAddress receiverAddress = userMap.get(receiverName);
                    String senderMessage = receiverAddress.getHostString() + ":" + receiverAddress.getPort();
                    byte[] senderMessageBytes = senderMessage.getBytes();
                    packet = new DatagramPacket(senderMessageBytes, senderMessageBytes.length, address, port);
                    System.out.println("Sending message to sender");
                    socket.send(packet);

                    InetSocketAddress senderAddress = new InetSocketAddress(address, port);
                    String receiverMessage = senderAddress.getAddress().getHostAddress() + ":" + senderAddress.getPort();
                    byte[] receiverMessageBytes = receiverMessage.getBytes();
                    packet = new DatagramPacket(receiverMessageBytes, receiverMessageBytes.length, InetAddress.getByName(receiverAddress.getHostName()), receiverAddress.getPort());
                    System.out.println("Sending message to receiver");
                    socket.send(packet);
                } else {
                    userMap.put(senderName, InetSocketAddress.createUnresolved(address.getHostAddress(), port));
                }
            }
        }
    }
}
