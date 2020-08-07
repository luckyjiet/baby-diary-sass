package fun.littlecc.common.exception;

import cn.hutool.http.server.HttpServerRequest;
import fun.littlecc.common.response.R;
import fun.littlecc.common.response.REnum;
import fun.littlecc.common.response.RException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author heyi
 * @date 2020-07-02 10:03
 * 全局异常捕获
 */
@Slf4j
@ControllerAdvice
public class GlobalException {
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public R exceptionHandler(Exception e) {
        log.error("请求出错：", e);
        R r;
        if (e instanceof RException) {
            RException exception = (RException) e;
            r = R.error(exception.getCode(), exception.getMsg());
        } else if (e instanceof MethodArgumentNotValidException) {
            StringBuilder errMsg = new StringBuilder();
            for (ObjectError objectError : ((MethodArgumentNotValidException) e).getBindingResult().getAllErrors()) {
                errMsg.append(objectError.getDefaultMessage());
            }
            r = R.error(REnum.METHOD_ARGUMENT_NOT_VALID_ERROR.getCode(), REnum.METHOD_ARGUMENT_NOT_VALID_ERROR.getMsg() + errMsg.toString());
        } else if (e instanceof HttpRequestMethodNotSupportedException) {
            r = R.error(REnum.REQUEST_METHOD_NOT_SUPPORT_ERROR.getCode(), REnum.REQUEST_METHOD_NOT_SUPPORT_ERROR.getMsg() + e.getMessage());
        } else if (e instanceof HttpMessageNotReadableException) {
            r = R.error(REnum.HTTP_MESSAGE_NOT_READABLE);
        } else {
            r = R.error(REnum.ERROR);
        }
        return r;
    }
}
