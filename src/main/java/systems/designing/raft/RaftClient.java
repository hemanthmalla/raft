package systems.designing.raft;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A simple client that requests a greeting from the {@link RaftNode}.
 */
public class RaftClient {
    private static final Logger logger = Logger.getLogger(RaftClient.class.getName());
    private static final String LOCALHOST = "localhost";
    private final Map<Integer, RaftGrpc.RaftBlockingStub> clientMap = new HashMap<>();

    /**
     * Construct client connecting to HelloWorld server at {@code host:port}.
     */
    public RaftClient(List<Integer> clientPorts) {
        for (Integer port : clientPorts) {
            logger.info("Adding client for " + port);
            ManagedChannel channel = ManagedChannelBuilder.forAddress(LOCALHOST, port).usePlaintext().build();
            clientMap.put(port, RaftGrpc.newBlockingStub(channel));
        }
    }

    /**
     * Broadcast hello to all nodes
     *
     * @param name
     */
    public void greetAll(String name) {
        for (RaftGrpc.RaftBlockingStub client : clientMap.values()) {
            greet(name, client);
        }
    }

    /**
     * Say hello to a node.
     */
    private void greet(String name, RaftGrpc.RaftBlockingStub client) {
        logger.info("Will try to greet " + name + " ...");
        HelloRequest request = HelloRequest.newBuilder().setName(name).build();
        HelloReply response;
        try {
            response = client.sayHello(request);
        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
            return;
        }
        logger.info("Greeting: " + response.getMessage());
    }

}

