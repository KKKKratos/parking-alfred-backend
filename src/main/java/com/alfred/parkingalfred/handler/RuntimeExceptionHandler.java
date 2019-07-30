package com.alfred.parkingalfred.handler;

import com.alfred.parkingalfred.exception.EmployeeNotExistedException;
import com.alfred.parkingalfred.exception.IncorrectParameterException;
import com.alfred.parkingalfred.exception.SecKillOrderException;
import com.alfred.parkingalfred.utils.ResultVOUtil;
import com.alfred.parkingalfred.vo.ResultVO;
import org.springframework.http.HttpStatus;
import org.springframework.mail.MailException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
@ResponseBody
public class RuntimeExceptionHandler {

  @ExceptionHandler(value = EmployeeNotExistedException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ResultVO handleEmployeeNotExistedException(EmployeeNotExistedException e){
    return ResultVOUtil.error(e.getCode(),e.getMessage());
  }
  @ExceptionHandler(value = SecKillOrderException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ResultVO handleSecKillOrderException(SecKillOrderException e){
    return ResultVOUtil.error(e.getCode(),e.getMessage());
  }
  @ExceptionHandler(value = IncorrectParameterException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResultVO handleIncorrectParameterException(IncorrectParameterException e){
    return ResultVOUtil.error(e.getCode(),e.getMessage());
  }
  @ExceptionHandler(value = MailException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResultVO handleMailException(MailException e){
    return ResultVOUtil.error(HttpStatus.BAD_REQUEST.value(),e.getMessage());
  }
}
