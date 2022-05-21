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
            int zipcode = (int)(Math.random()*99998+1); //1~99999
            int temperature = (int)(Math.random()*214-80); //-80~134
            int relhumidity = (int)(Math.random()*49+10); //10~59

            //integer을 string으로 변환하여 합침
            String response = Integer.toString(zipcode) +" "+ Integer.toString(temperature) +" "+ Integer.toString(relhumidity);
            socket.send(response);
        }
    }
}
