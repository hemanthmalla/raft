package systems.designing.raft;

import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * A simple testing client.
 */
public class testClient {
    private static final Logger logger = Logger.getLogger(testClient.class.getName());

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            logger.warning("Must have atleast one arg, i.e. server port");
            throw new IllegalArgumentException("Must provide atleast one arguments. i.e. server port");
        }
        Integer port = Integer.parseInt(args[0]);
        ArrayList<Integer> clientPorts = new ArrayList<>();
        clientPorts.add(port);
        RaftClient client = new RaftClient(clientPorts);
        String user = "world";
        if (args.length > 0) {
            user = args[0];
        }
        client.greetAll(user);
    }

}
