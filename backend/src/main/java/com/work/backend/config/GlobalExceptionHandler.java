package com.work.backend.config;

import com.work.backend.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(RuntimeException.class)
  public Result<String> handleRuntimeException(RuntimeException e) {
    log.error("运行时异常：", e);
    return Result.error(e.getMessage());
  }

  @ExceptionHandler(Exception.class)
  public Result<String> handleException(Exception e) {
    log.error("系统异常：", e);
    return Result.error("系统异常，请联系管理员");
  }
}
