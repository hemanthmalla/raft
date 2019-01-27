package systems.designing.raft;

import io.grpc.stub.StreamObserver;

public class RaftNode extends RaftGrpc.RaftImplBase {

    final int ID;
    final RaftClient client;

    public RaftNode(int ID, RaftClient client) {
        this.ID = ID;
        this.client = client;
    }

    @Override
    public void sayHello(HelloRequest request, StreamObserver<HelloReply> responseObserver) {
        HelloReply reply = HelloReply.newBuilder().setMessage("Hello " + request.getName()).build();
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }

    @Override
    public void requestVote(VoteRequest request, StreamObserver<VoteResponse> responseObserver) {
        super.requestVote(request, responseObserver);
    }

    @Override
    public void appendEntries(AppendEntriesRequest request, StreamObserver<AppendEntriesResponse> responseObserver) {
        super.appendEntries(request, responseObserver);
    }
}
