package DEALEAR_ROUTER;

public class client_test {
    public static void main(String args[]){
        ClientTask client = new ClientTask(Integer.parseInt(args[0]));
        client.start();
    }
}
