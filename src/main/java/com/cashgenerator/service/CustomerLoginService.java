package com.cashgenerator.service;

import com.cashgenerator.AuthenticationType;
import com.cashgenerator.model.LoginBo;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public interface CustomerLoginService {

    LoginBo validateCustomerCredentials(@NotNull @Valid LoginBo loginBo, AuthenticationType authenticationType);
}
