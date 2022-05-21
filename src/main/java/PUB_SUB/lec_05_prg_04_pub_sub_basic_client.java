package PUB_SUB;
import org.zeromq.SocketType;
import org.zeromq.ZMQ;
import org.zeromq.ZContext;

public class lec_05_prg_04_pub_sub_basic_client {
    public static void main(String Args[]){
        ZContext context = new ZContext();
        ZMQ.Socket socket = context.createSocket(SocketType.SUB);

        System.out.println("Collecting updates from seather server...");

        socket.connect("tcp://localhost:5555");

        //system argument//

        String zip_filter = "10001";
        socket.subscribe(zip_filter.getBytes());

        int total_temp = 0;
        for (int update_nbr = 0; update_nbr<20; update_nbr++){
            String string = socket.recvStr();
            String[] info = string.split(" ");
            String zipcode = info[0];
            String temperature = info[1];
            String relhumidity = info[2];
            total_temp += Integer.parseInt(temperature);

            System.out.println("Receive temperature for zipcode " + zip_filter + " was " + zipcode);
        }

        System.out.println("Average temperature for zipcode " + zip_filter + " was " + total_temp/20);

    }
}
