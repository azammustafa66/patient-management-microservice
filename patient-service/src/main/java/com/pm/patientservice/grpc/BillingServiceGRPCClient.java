package com.pm.patientservice.grpc;

import billing.BillingRequest;
import billing.BillingResponse;
import billing.BillingServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class BillingServiceGRPCClient {
    private final BillingServiceGrpc.BillingServiceBlockingStub stub;

    public BillingServiceGRPCClient(@Value("${billing.service.address:localhost}") String serverAddress, @Value("${billing.service.server.port}") int port) {
        log.info("Connected to billing GRPC server on {}:{}", serverAddress, port);
        ManagedChannel channel = ManagedChannelBuilder.forAddress(serverAddress, port).build();
        stub = BillingServiceGrpc.newBlockingStub(channel);
    }

    public BillingResponse createBillingAccount(String patientId, String name, String email) {
        BillingRequest request = BillingRequest.newBuilder().setPatientId(patientId).setName(name).setEmail(email).build();
        BillingResponse response = stub.createBillingAccount(request);
        log.info("Response from BillingServiceGrpc.BillingAccountBlockingStub is {}", response);
        return response;
    }
}
