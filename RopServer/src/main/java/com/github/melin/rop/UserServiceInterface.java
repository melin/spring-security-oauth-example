package com.github.melin.rop;

import com.rop.annotation.NeedInSessionType;
import com.rop.annotation.ServiceMethod;

public interface UserServiceInterface {

    @ServiceMethod(method = "user.getSession",version = "1.0",needInSession = NeedInSessionType.NO)
    Object getSession(LogonRequest request);
}
