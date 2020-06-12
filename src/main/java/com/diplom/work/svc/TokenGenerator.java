package com.diplom.work.svc;

import com.diplom.work.controller.ControllerUtils;
import org.springframework.stereotype.Component;

@Component
public class TokenGenerator {
    public String generateToken() {
        return ControllerUtils.randomString(64);
    }
}
