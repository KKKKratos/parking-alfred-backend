package com.alfred.parkingalfred.service.impl;

import com.alfred.parkingalfred.config.EmailConfig;
import com.alfred.parkingalfred.entity.Employee;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

@SpringBootTest
public class EmailServiceImplTest {

  private JavaMailSender javaMailSender;


  private EmailConfig emailConfig;

  @Autowired
  private EmailServiceImpl emailService;

  @Test
  void should_send_a_email_when_call_sendEmail_with_true_param() {
    javaMailSender = Mockito.mock(JavaMailSender.class);
    emailConfig = Mockito.mock(EmailConfig.class);
    ReflectionTestUtils.setField(emailService,EmailServiceImpl.class
      ,"javaMailSender",javaMailSender, JavaMailSender.class);
    ReflectionTestUtils.setField(emailService,EmailServiceImpl.class
      ,"emailConfig",emailConfig, EmailConfig.class);
    Employee employee = new Employee();
    employee.setId(1l);
    employee.setStatus(1);
    employee.setTelephone("12321312");
    employee.setMail("764974614@qq.com");
    employee.setPassword("213213213");
    emailService.sendEmail(employee);
  }
}