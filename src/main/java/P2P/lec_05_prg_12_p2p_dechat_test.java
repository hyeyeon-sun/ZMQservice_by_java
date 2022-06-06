package P2P;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

public class lec_05_prg_12_p2p_dechat_test {
    public static void main (String[] args){
        String ip_addr_p2p_server = "";
        int port_nameserver = 9001;
        int port_chat_publisher = 9002;
        int port_chat_collector = 9003;
        int port_subscribe = 9004;

        String user_name = args[0];
        String ip_addr = lec_05_prg_12_p2p_dechat.get_local_ip();
        String ip_mask = ip_addr.substring(0,ip_addr.lastIndexOf("."));

        System.out.println("searching for p2p server.");


        String name_server_ip_addr = lec_05_prg_12_p2p_dechat.search_nameserver(ip_mask, ip_addr, port_nameserver);
        if (name_server_ip_addr == null){
            ip_addr_p2p_server = ip_addr;
            System.out.println("p2p server is not found, and p2p server mode is activated.");
            Thread beacon_thread = new Thread(/*beacon_nameserver*/);
            System.out.println("p2p beacon server is activated.");
            System.out.println("p2p subsciber database server is activated.");
            System.out.println("p2p message relay server is activated.");
        }else {
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
                if(true/*p2p_rx.poll(100) & zmq.POLLIN*/){
                    String message = p2p_rx.recvStr();
                    System.out.println("p2p-recv::<<== "/*추가할 내용 있음*/);
                }else{
                    int rand = (int)(Math.random()*98+1);
                    if(rand < 10){
                        try {
                            Thread.sleep(3000);
                        } catch(Exception e){

                        }
                        String msg = "("+user_name+","+ip_addr+":ON)";
                        p2p_tx.send(msg);
                        System.out.println("pwp-send::==>>" + msg);
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

        /*static*/ final boolean global_flag_shutdown;
        global_flag_shutdown = true;
        db_client_socket.close(/*구현할 부분*/);
        p2p_rx.close(/*구현 필요*/);
        p2p_tx.close(/*구현 필요*/);
        db_client_context.destroy();
        relay_client.destroy();
    }
}
