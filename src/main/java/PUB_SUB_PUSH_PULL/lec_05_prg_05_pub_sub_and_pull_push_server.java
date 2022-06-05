package PUB_SUB_PUSH_PULL;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

public class lec_05_prg_05_pub_sub_and_pull_push_server {
    public static void main(String args[]){
        ZContext context = new ZContext();

        ZMQ.Socket publisher = context.createSocket(SocketType.PUB);
        publisher.bind("tcp://localhost:5557");
        ZMQ.Socket collector = context.createSocket(SocketType.PULL);
        collector.bind("tcp://localhost:5558");

        while(true){
            byte[] message = collector.recv();
            String s_message = new String(message);
            System.out.println("I: publishing update " + s_message);
            publisher.send(message);
            try{
                Thread.sleep(500);
            }catch(Exception e){

            }

        }
    }
}
