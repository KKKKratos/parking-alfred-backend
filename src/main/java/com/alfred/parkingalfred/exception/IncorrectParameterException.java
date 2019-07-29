package com.alfred.parkingalfred.exception;

import com.alfred.parkingalfred.enums.ResultEnum;
import lombok.Getter;

@Getter
public class IncorrectParameterException extends RuntimeException{
  private Integer code;

  public IncorrectParameterException(ResultEnum resultEnum) {
    super(resultEnum.getMessage());
    this.code = resultEnum.getStatus();
  }

  public IncorrectParameterException(Integer code, String message) {
    super(message);
    this.code = code;
  }
}
