import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;

public class Client1 {
    public static void main(String[] args)  {
        try{
            int port = 2000;
            String host = "localhost";

            Scanner inputMessage = new Scanner(System.in);
            double radius;
            double area;

            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(host, port));

            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            DataInputStream inputStream = new DataInputStream(socket.getInputStream());

            while(true){
                System.out.print("Enter the radius: ");
                radius = inputMessage.nextDouble();

                outputStream.writeDouble(radius);

                area = inputStream.readDouble();
                System.out.printf("The area of radius %5.2f is %5.2f\n",radius, area);

                System.out.print("Close? y/n: ");
                String close = inputMessage.next();

                outputStream.writeUTF(close);
                outputStream.flush();

                if(close.equalsIgnoreCase("y")){
                    break;
                }
            }

            outputStream.close();
            inputStream.close();
            socket.close();

        }catch (Exception err){
            System.err.printf("Error encountered: %s", err.getMessage());
        }
    }
}
