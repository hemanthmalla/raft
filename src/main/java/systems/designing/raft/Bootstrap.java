package systems.designing.raft;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Server that manages startup/shutdown of a {@code Greeter} server.
 */
public class Bootstrap {
    private static final Logger logger = Logger.getLogger(Bootstrap.class.getName());

    private Server server;
    private final List<Integer> clientPorts;
    private final int serverPort;

    public Bootstrap(List<Integer> clientPorts, int serverPort) {
        this.clientPorts = checkNotNull(clientPorts);
        this.serverPort = checkNotNull(serverPort);
    }

    public static void main(String[] args) throws InterruptedException, IOException {
        if (args.length != 2) {
            logger.warning("Usage: RaftNode ports idx; ports = comma separated ports of all Nodes in the cluster;" +
                    " idx = idx of the port to use for this RaftNode");
            throw new IllegalArgumentException("Must provide atleast two arguments");
        }
        String[] portStrings = args[0].split(",");
        ArrayList<Integer> clientPorts = new ArrayList<>();
        int idx = Integer.parseInt(args[1]);
        for (int i = 0; i < portStrings.length; ++i) {
            if (i == idx) {
                continue;
            }
            clientPorts.add(Integer.parseInt(portStrings[i]));
        }
        int serverPort = Integer.parseInt(portStrings[idx]);
        Bootstrap b = new Bootstrap(clientPorts, serverPort);
        b.start();
        b.blockUntilShutdown();
    }

    private void start() throws IOException {
        RaftClient raftClient = new RaftClient(clientPorts);
        RaftNode raftNode = new RaftNode(serverPort, raftClient);
        server = ServerBuilder.forPort(serverPort)
                .addService(raftNode)
                .build()
                .start();
        logger.info("Server started, listening on " + serverPort);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                // Use stderr here since the logger may have been reset by its JVM shutdown hook.
                System.err.println("*** shutting down gRPC server since JVM is shutting down");
                Bootstrap.this.stop();
                System.err.println("*** server shut down");
            }
        });
    }

    private void stop() {
        if (server != null) {
            server.shutdown();
        }
    }

    /**
     * Await termination on the main thread since the grpc library uses daemon threads.
     */
    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

}
