package com.alfred.parkingalfred.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("emailconfig")
@Data
public class EmailConfig {

  private String from;

  private String subject;

}
