package com.alfred.parkingalfred.exception;

import com.alfred.parkingalfred.enums.ResultEnum;
import lombok.Getter;

@Getter
public class SecKillOrderException extends RuntimeException {
  private Integer code;

  public SecKillOrderException(ResultEnum resultEnum) {
    super(resultEnum.getMessage());
    this.code = resultEnum.getStatus();
  }

  public SecKillOrderException(Integer code, String message) {
    super(message);
    this.code = code;
  }
}
