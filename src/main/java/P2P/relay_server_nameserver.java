package P2P;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

public class relay_server_nameserver implements Runnable{
    String local_ip_addr;
    String port_chat_publisher;
    String port_chat_collector;

    public relay_server_nameserver(String local_ip_addr, String port_chat_publisher, String port_chat_collector) {
        this.local_ip_addr = local_ip_addr;
        this.port_chat_collector = port_chat_collector;
        this.port_chat_publisher = port_chat_publisher;
    }

    @Override
    public void run() {
        ZContext context = new ZContext();
        ZMQ.Socket publisher = context.createSocket(SocketType.PUB);
        publisher.bind("tcp://" + local_ip_addr + ":" + port_chat_publisher);
        ZMQ.Socket collector = context.createSocket(SocketType.PULL);
        collector.bind("tcp://"+local_ip_addr+":"+port_chat_collector);
        System.out.println("local p2p relay server activated at tcp://" + local_ip_addr + ":" + port_chat_publisher + " & " + port_chat_collector + ".");

        while (true) {
            try {
                String message = collector.recvStr();
                System.out.println("p2p-relay:<==>" + message);
                publisher.send("RELAY:" + message);
            } catch (Exception e) {
                break;
            }
        }
    }
}
