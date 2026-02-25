package com.cashgenerator.service;

import com.cashgenerator.model.Customer;

import java.util.List;

public interface CustomerService {
    List<Customer> getCustomers(String storeId);
}
