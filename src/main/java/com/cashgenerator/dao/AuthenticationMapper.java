package com.cashgenerator.dao;

import com.cashgenerator.model.UserAuthentication;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AuthenticationMapper {
    void save(UserAuthentication authentication);
}
