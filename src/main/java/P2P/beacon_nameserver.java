package P2P;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

public class beacon_nameserver implements Runnable{
    String local_ip_addr;
    String port_nameserver;

    public beacon_nameserver(String local_ip_addr, String port_nameserver){
        this.local_ip_addr = local_ip_addr;
        this.port_nameserver = port_nameserver;
    }

    @Override
    public void run() {
        ZContext context = new ZContext();
        ZMQ.Socket socket = context.createSocket(SocketType.PUB);
        socket.bind("tcp://" + local_ip_addr + ":" + port_nameserver);
        System.out.println("local p2p name server bind to tcp://" + local_ip_addr + ":" + port_nameserver + ".");
        while (true) {
            try {
                Thread.sleep(1000);
                String msg = "NAMESERVER:" + local_ip_addr;
                socket.send(msg);
            } catch (Exception e) {
                break;
            }
        }
    }
}
