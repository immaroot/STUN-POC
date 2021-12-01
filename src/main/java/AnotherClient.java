public class AnotherClient {

    public static void main(String[] args) throws InterruptedException {
        Thread send = new Client("send", "localhost", "localhost", 6000, 5000);
        send.start();
        Thread listen = new Client("listen", "localhost", "localhost", 6000, 5000);
        listen.start();

        listen.join();
        send.join();

    }
}
