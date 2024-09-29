package com.myspringboot.serviceImpl;

import com.myspringboot.service.UserService;
import com.myspringboot.model.UserModel;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author xiehang
 * @create 2022-08-09 22:32
 */
@Service
public class UserServiceImpl implements UserService {

    @Override
    public String testTimeOut() {
        return null;
    }

    @Override
    public UserModel getUser(Long id) {
        return null;
    }

    @Override
    public Map<String, Object> addUser(UserModel userModel) {
        return null;
    }

    @Override
    public Map<String, Object> updateName(String userName) {
        return null;
    }

    @Override
    public UserModel findUser(String id) {
        return null;
    }
}
