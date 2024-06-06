package com.roman.chat.client.service;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class FeignClientRequestInterceptor implements RequestInterceptor {
    private String serverUrl;

    @Override
    public void apply(RequestTemplate template) {
        template.target(serverUrl);
    }
}
