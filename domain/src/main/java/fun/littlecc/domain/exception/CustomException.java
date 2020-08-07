package fun.littlecc.domain.exception;

import fun.littlecc.common.response.RException;
import fun.littlecc.domain.enums.ExceptionEnum;

/**
 * @author
 * @date 2020-07-02 14:26
 */
public class CustomException extends RException {

    public CustomException(String code, String msg) {
        super(code, msg);
    }

    public CustomException(Throwable e) {
        super(e);
    }

    public CustomException(ExceptionEnum exceptionEnum) {
        super(exceptionEnum.getCode(), exceptionEnum.getMsg());
    }
}
