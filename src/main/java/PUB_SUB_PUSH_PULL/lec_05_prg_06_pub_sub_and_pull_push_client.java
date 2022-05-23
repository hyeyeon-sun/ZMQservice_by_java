package PUB_SUB_PUSH_PULL;

import org.zeromq.SocketType;
import org.zeromq.ZMQ;
import org.zeromq.ZContext;

public class lec_05_prg_06_pub_sub_and_pull_push_client {
    public static void main (String args[]){
        ZContext context = new ZContext();
        ZMQ.Socket subscriber = context.createSocket(SocketType.SUB);
        subscriber.subscribe("".getBytes());
        subscriber.connect("tcp://localhost:5557");
        ZMQ.Socket publisher = context.createSocket(SocketType.PUSH);
        publisher.connect("tcp://localhost:5558");
    }
}
