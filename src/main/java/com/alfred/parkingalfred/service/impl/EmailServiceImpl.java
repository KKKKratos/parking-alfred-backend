package com.alfred.parkingalfred.service.impl;

import com.alfred.parkingalfred.config.EmailConfig;
import com.alfred.parkingalfred.entity.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl {

  @Autowired
  private JavaMailSender javaMailSender;

  @Autowired
  private EmailConfig emailConfig;

  public void sendEmail(Employee employee) throws MailException {
    SimpleMailMessage message = new SimpleMailMessage();
    message.setFrom(emailConfig.getFrom());
    message.setSubject(emailConfig.getSubject());
    message.setTo(employee.getMail());
    message.setText("username:" + employee.getMail()
        + "\npassword:" + employee.getPassword());
    javaMailSender.send(message);
  }

  @Async
  @EventListener
  public void placeCreateEmployeeService(Employee employee) {
    sendEmail(employee);
  }
}
