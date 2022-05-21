package PUB_SUB;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import java.util.Random;

public class lec_05_prg_03_pub_sub_basic_server {
    public static void main(String args[]){
        System.out.println("Publishing updates at weather server...");

        ZContext context = new ZContext();
        ZMQ.Socket socket = context.createSocket(SocketType.PUB);
        socket.bind("tcp://localhost:5555");

        while(true){
            int zipcode = (int)(Math.random()*99998+1);
            int temperature = (int)(Math.random()*214-80);
            int relhumidity = (int)(Math.random()*49+10);

            String response = Integer.toString(zipcode) +" "+ Integer.toString(temperature) +" "+ Integer.toString(relhumidity);
            socket.send(response);

            System.out.println(response);
        }
    }
}
