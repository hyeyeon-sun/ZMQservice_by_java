package P2P;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import org.zeromq.ZPoller;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class lec_05_prg_12_p2p_dechat {
    public static boolean global_flag_shutdown;

    public static String search_nameserver(String ip_mask, String local_ip_addr, int port_nameserver) {
        ZContext context = new ZContext();
        ZMQ.Socket req = context.createSocket(SocketType.SUB);
        for (int last = 1; last < 255; last++) {
            String target_ip_addr = "tcp://" + ip_mask + "."+last+ ":" + port_nameserver;
            if (target_ip_addr != local_ip_addr || target_ip_addr == local_ip_addr) {
                req.connect(target_ip_addr);
            }
            req.setReceiveTimeOut(2000);
            req.subscribe("NAMESERVER".getBytes());
        }

        try {
            String res = req.recvStr();
            String[] res_list = res.split(":");
            if (res_list[0] == "NAMESERVER") {
                return res_list[1];
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    public static String get_local_ip() {
        InetAddress local;
        try {
            local = InetAddress.getLocalHost();
            String ip = local.getHostAddress();
            return "127.0.0.1";
        } catch (UnknownHostException e1) {
            e1.printStackTrace();
            return "127.0.0.1";
        }
    }

    public static void main(String args[]){
        String ip_addr_p2p_server = "";
        int port_nameserver = 9001;
        int port_chat_publisher = 9002;
        int port_chat_collector = 9003;
        int port_subscribe = 9004;

        String user_name = args[0];
        String ip_addr = get_local_ip();
        String ip_mask = ip_addr.substring(0,ip_addr.lastIndexOf("."));

        System.out.println("searching for p2p server.");

        String name_server_ip_addr = search_nameserver(ip_mask, ip_addr, port_nameserver);
        if (name_server_ip_addr == null){
            ip_addr_p2p_server = ip_addr;
            System.out.println(ip_addr);
            System.out.println("p2p server is not found, and p2p server mode is activated.");
            beacon_nameserver beacon_runnable = new beacon_nameserver(ip_addr, Integer.toString(port_nameserver));
            Thread beacon_thread = new Thread(beacon_runnable);
            beacon_thread.start();
            System.out.println("p2p beacon server is activated.");
            user_manager_nameserver db_runnable = new user_manager_nameserver(ip_addr, Integer.toString(port_subscribe));
            Thread db_thread = new Thread(db_runnable);
            db_thread.start();
            System.out.println("p2p subsciber database server is activated.");
            relay_server_nameserver relay_runnable = new relay_server_nameserver(ip_addr, Integer.toString(port_chat_publisher), Integer.toString(port_chat_collector));
            Thread relay_thread = new Thread(relay_runnable);
            relay_thread.start();
            System.out.println("p2p message relay server is activated.");
        }else{
            ip_addr_p2p_server = name_server_ip_addr;
            System.out.println("p2p server found at " + ip_addr_p2p_server + ", and p2p client mode is activated.");
        }
        System.out.println("starting user registration procedure.");

        ZContext db_client_context = new ZContext();
        ZMQ.Socket db_client_socket = db_client_context.createSocket(SocketType.REQ);
        db_client_socket.connect("tcp://"+ip_addr_p2p_server+":"+port_subscribe);
        db_client_socket.send(ip_addr+":"+user_name);
        if (db_client_socket.recvStr() == "ok"){
            System.out.println("user registration to p2p server completed.");
        }else{
            System.out.println("user registration to p2p server failed.");
        }

        System.out.println("starting message transfer procedure.");

        ZContext relay_client = new ZContext();
        ZMQ.Socket p2p_rx = relay_client.createSocket(SocketType.SUB);
        p2p_rx.subscribe("RELAY".getBytes());
        p2p_rx.connect("tcp://"+ip_addr_p2p_server+":"+port_chat_publisher);
        ZMQ.Socket p2p_tx = relay_client.createSocket(SocketType.PUSH);
        p2p_tx.connect("tcp://"+ip_addr_p2p_server+":"+port_chat_collector);

        System.out.println("starting autonomous message transmit and receive scenario.");

        while(true){
            try{
                ZPoller poll = new ZPoller(relay_client);
                poll.register(p2p_rx, ZMQ.Poller.POLLIN);
                if(poll.poll(100)>0){
                    String message = p2p_rx.recvStr();
                    System.out.println("p2p-recv::<<== "+message.split(":")[1]+":"+message.split(":")[2]);
                }else{
                    int rand = (int)(Math.random()*98+1);
                    if(rand < 10){
                        try {
                            Thread.sleep(3000);
                        } catch(Exception e){

                        }
                        String msg = "("+user_name+","+ip_addr+":ON)";
                        p2p_tx.send(msg);
                        System.out.println("p2p-send::==>>" + msg);
                    }else if (rand > 90) {
                        try {
                            Thread.sleep(3000);
                        } catch(Exception e){

                        }
                        String msg = "("+user_name+","+ip_addr+":OFF)";
                        p2p_tx.send(msg);
                        System.out.println("p2p-send::==>>"+msg);
                    }
                }
            }catch (Exception e){
                break;
            }
        }

        System.out.println("closing p2p chatting program.");

        global_flag_shutdown = true;
        db_client_socket.setLinger(0);
        db_client_socket.close();
        p2p_rx.setLinger(0);
        p2p_rx.close();
        p2p_tx.setLinger(0);
        p2p_tx.close();
        db_client_context.destroy();
        relay_client.destroy();
    }
}
