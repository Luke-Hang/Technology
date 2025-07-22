package com.myspringboot.serviceImpl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransactionService {
    @Transactional
    public void methodA(int i){
        try {
            methodB(i);
        } catch (Exception e) {
            System.out.println("A 事务回滚"+i);
            throw e;//抛异常以触发A的事务回滚
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void methodB(int i) {
        System.out.println("B 事务提交"+i);
    }
}
