package fun.littlecc.common.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author heyi
 * @date 2020-07-02 11:04
 */
@AllArgsConstructor
public enum REnum {

    /**
     * 通用系统错误
     */
    SUCCESS("10000", "成功"),
    METHOD_ARGUMENT_NOT_VALID_ERROR("10001", "请求参数校验不合格"),
    REQUEST_METHOD_NOT_SUPPORT_ERROR("10002", "不支持的请求方式"),
    HTTP_MESSAGE_NOT_READABLE("10003", "请求内容不可读"),
    ILLEGAL_SIGN("10004", "非法签名"),
    ERROR("99999", "系统错误，未知异常");

    @Getter
    private final String code;
    @Getter
    private final String msg;
}
