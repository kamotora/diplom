package com.diplom.work.svc;

import com.diplom.work.controller.ControllerUtils;
import org.springframework.stereotype.Service;

@Service
public class TokenService {
    public String generateToken() {
        return ControllerUtils.randomString(64);
    }
}
