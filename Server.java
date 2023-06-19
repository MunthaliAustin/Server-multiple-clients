import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class Server {
    public static void main(String[] args) {
        int port = 2000;
        String host = "localhost";
        AtomicInteger clientNo = new AtomicInteger(0);

        new Thread(() -> {
            try{
                ServerSocket serverSocket = new ServerSocket();
                serverSocket.bind(new InetSocketAddress(host, port));
                System.out.printf("Server bound and listening @ %s \n \n",
                        serverSocket.getLocalSocketAddress().toString().split("/")[1]);

                //Receiving connection
                while(true){
                    try{
                        Socket clientSocket = serverSocket.accept();
                        clientNo.getAndIncrement();
                        System.out.printf(" Client %s accepted and connected @ %s \n \n", clientNo,
                                clientSocket.getRemoteSocketAddress().toString().split("/")[1]);


                        //Calculating the area
                        new Thread(new ClientHandler(clientSocket, serverSocket)).start();

                    }catch (Exception ex){
                        break;
                    }

                }

                serverSocket.close();
            }catch (Exception ex){
                System.err.printf("ERROR Encountered: %s",
                        ex.getMessage());
            }
        }).start();
    }
}

class ClientHandler implements Runnable {
    private final Socket clientSocket;
    ServerSocket serverSocket ;
    AtomicReference<Double> radius = new AtomicReference<>((double) 0);
    AtomicReference<Double> area = new AtomicReference<>((double) 0);

    String clientAddress;
    public ClientHandler(Socket socket, ServerSocket serverSocket){
        this.clientSocket = socket;
        clientAddress = clientSocket.getRemoteSocketAddress().toString().split("/")[1];
        this.serverSocket = serverSocket;
    }

    @Override
    public void run() {
        try {
            DataInputStream input = new DataInputStream(clientSocket.getInputStream());
            DataOutputStream output = new DataOutputStream(clientSocket.getOutputStream());

            //Recalculating
            while(true){
                try {
                    //get the radius from the client
                    radius.set(input.readDouble());
                    System.out.printf("Receiver the radius of %s from the client: %s \n",
                            radius, clientAddress);

                    //Calculating the area of the circle
                    area.set(2 * Math.PI * Math.pow(radius.get(), 2));

                    output.writeDouble(area.get());
                    System.out.printf("Sent the area to the client: %s \n \n",
                            clientAddress);
                    output.flush();

                    String close = input.readUTF();
                    if (close.equalsIgnoreCase("y")) {
                        System.out.printf("Connection closed to client: %s \n",
                                clientAddress);
                        break;
                    }

                    input.close();
                    output.close();
                    clientSocket.close();
                }catch(Exception e) {
                    // Terminates loop
                    break;
                }

            }
            serverSocket.close();

        } catch (Exception ex){
            ex.printStackTrace();
        }
    }
}



