package com.example.grpcstarterdemo.rpc;

import com.example.grpcdemo.grpc.Member;
import com.example.grpcdemo.grpc.MemberLoginRequest;
import com.example.grpcdemo.grpc.MemberLoginResponse;
import com.example.grpcdemo.grpc.MemberLoginServiceGrpc;
import io.grpc.stub.StreamObserver;
import org.lognet.springboot.grpc.GRpcService;

@GRpcService
public class MemberLoginServiceImpl extends MemberLoginServiceGrpc.MemberLoginServiceImplBase {

    @Override
    public void memberLogin(MemberLoginRequest request, StreamObserver<MemberLoginResponse> responseObserver) {

        String token = "";
        if(request.getMember().getPassword().equals("123456")){
            token = "success";
        }

        MemberLoginResponse response = MemberLoginResponse
                .newBuilder()
                .setToken(token)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
