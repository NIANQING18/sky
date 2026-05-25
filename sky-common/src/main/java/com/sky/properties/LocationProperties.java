package com.sky.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "sky.baidu")
@Component
@Data
public class LocationProperties {
    private String AK;
    private String shopAddress;
}
