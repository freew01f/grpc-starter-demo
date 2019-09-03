package com.example.grpcstarterdemo.config;

import com.example.grpcdemo.grpc.MemberListServiceGrpc;
import com.example.grpcdemo.grpc.MemberLoginServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrpcConfig {
    @Bean
    ManagedChannel channel(@Value("${app-config.grpc-server-name}") String name,
                           @Value("${app-config.grpc-server-port}") Integer port){
        return ManagedChannelBuilder.forAddress(name, port)
                .usePlaintext()
                .build();
    }

    @Bean
    MemberListServiceGrpc.MemberListServiceBlockingStub memberListServiceBlockingStub(ManagedChannel channel){
        return MemberListServiceGrpc.newBlockingStub(channel);
    }

    @Bean
    MemberLoginServiceGrpc.MemberLoginServiceBlockingStub memberLoginServiceStub(ManagedChannel channel){
        return MemberLoginServiceGrpc.newBlockingStub(channel);
    }
}
