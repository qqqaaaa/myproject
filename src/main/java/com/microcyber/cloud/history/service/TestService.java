package com.microcyber.cloud.history.service;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

@Service
public interface TestService {
    public void findById(@PathVariable("id") String id);
}
