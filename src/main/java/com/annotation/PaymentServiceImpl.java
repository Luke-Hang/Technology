package com.annotation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class PaymentServiceImpl {

    @Autowired
    @Qualifier("aliPayService")
    private PaymentTypeService paymentTypeService;

}
