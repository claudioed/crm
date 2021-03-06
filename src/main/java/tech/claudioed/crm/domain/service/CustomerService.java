package tech.claudioed.crm.domain.service;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tech.claudioed.crm.domain.Customer;
import tech.claudioed.customer.grpc.CustomerFindRequest;
import tech.claudioed.customer.grpc.CustomerFindResponse;
import tech.claudioed.customer.grpc.CustomerServiceGrpc;
import tech.claudioed.customer.grpc.CustomerServiceGrpc.CustomerServiceBlockingStub;

/** @author claudioed on 2019-04-11. Project crm */
@Slf4j
@Service
public class CustomerService {

  private final String customerHost;

  private final Integer customerPort;

  private final ManagedChannel managedChannel;

  public CustomerService(
      @Value("${customer.host}") String customerHost,
      @Value("${customer.port}") Integer customerPort) {
    log.info("Customer SVC URL {}",customerHost);
    log.info("Customer SVC PORT {}",customerPort);
    this.customerHost = customerHost;
    this.customerPort = customerPort;
    this.managedChannel = ManagedChannelBuilder.forAddress(this.customerHost, this.customerPort).usePlaintext().build();
  }

  public Customer customer(String id) {
    log.info("Finding customer ID {} data",id);
    final CustomerServiceBlockingStub stub =
        CustomerServiceGrpc.newBlockingStub(this.managedChannel);
    val request = CustomerFindRequest.newBuilder().setId(id).build();
    final CustomerFindResponse response = stub.findCustomer(request);
    log.info("Customer ID {} is valid ",id);
    return Customer.builder()
        .id(response.getId())
        .address(response.getAddress())
        .city(response.getCity())
        .country(response.getCountry())
        .email(response.getEmail())
        .build();
  }

}
