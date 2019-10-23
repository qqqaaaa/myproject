package com.microcyber.cloud.history.service.Impl;

import com.microcyber.cloud.history.service.TestService;

public class TestServiceImpl implements TestService {
    @Override
    public void findById(String id) {
        System.out.print(id);
    }
}
