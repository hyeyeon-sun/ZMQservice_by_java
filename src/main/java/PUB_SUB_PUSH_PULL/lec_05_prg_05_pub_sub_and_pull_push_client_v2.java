package PUB_SUB_PUSH_PULL;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import org.zeromq.ZPoller;

public class lec_05_prg_05_pub_sub_and_pull_push_client_v2 {
    public static void main (String args[]) {
        ZContext context = new ZContext();
        ZMQ.Socket subscriber = context.createSocket(SocketType.SUB);
        subscriber.subscribe("".getBytes());
        subscriber.connect("tcp://localhost:5557");
        ZMQ.Socket publisher = context.createSocket(SocketType.PUSH);
        publisher.connect("tcp://localhost:5558");

        String clientID = args[0];

        ZPoller poll = new ZPoller(context);
        poll.register(subscriber, ZMQ.Poller.POLLIN);
        while (true) {
            if (poll.poll(100)>0) {
                byte[] message = subscriber.recv();
                String s_message = new String(message);
                System.out.println(clientID + ": receive status => " + s_message);
            } else {
                int rand = (int) (Math.random() * 98 + 1);
                if (rand < 10) {
                    String msg = "(" + clientID + ":ON)";
                    publisher.send(msg);
                    System.out.println(clientID + ": send status - activated");
                }
                else if (rand > 90){
                    String msg = "(" + clientID + ":OFF)";
                    publisher.send(msg);
                    System.out.println(clientID + ": send status - deactivated");
                }
            }
        }
    }
}
