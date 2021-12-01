import java.io.*;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Client extends Thread {

    String activity, myIP, peerIP;
    int myPort, peerPort;

    public Client(String activity, String myIP, String peerIP, int myPort, int peerPort) {
        this.activity = activity;
        this.myIP = myIP;
        this.peerIP = peerIP;
        this.myPort = myPort;
        this.peerPort = peerPort;
    }

    public void run() {
        try {
            if (activity.equals("listen")) {
                peerListen();
            } else {
                peerSend();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void peerSend() throws IOException, InterruptedException {
        System.out.println("peerSend");

        Socket socket = null;
        while (true) {
            try {
                socket = new Socket(peerIP, peerPort);
                break;
            } catch (ConnectException e) {
                Thread.sleep(1000);
            }
        }

        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        Scanner scanner = new Scanner(System.in);

        while (true) {
            String messageOut = scanner.nextLine();
            if (messageOut.startsWith("end")) {
                break;
            }
            out.writeUTF(messageOut);
        }

        socket.close();

    }

    private void peerListen() throws IOException {
        System.out.println("peerListen");

        ServerSocket serverSocket = new ServerSocket();
        serverSocket.bind(new InetSocketAddress(myIP, myPort));

        Socket peer = serverSocket.accept();

        DataInputStream in = new DataInputStream(peer.getInputStream());

        while (true) {
            String messageIn = in.readUTF();
            System.out.println(messageIn);
            if (messageIn.startsWith("end")) {
                break;
            }
        }
        serverSocket.close();
    }

    public static void main(String[] args) throws InterruptedException {

        Thread listen = new Client("listen", "localhost", "localhost", 5000, 6000);
        listen.start();

        Thread send = new Client("send", "localhost", "localhost", 5000, 6000);
        send.start();


        listen.join();
        send.join();
    }
}
