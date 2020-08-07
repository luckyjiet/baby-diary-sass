package fun.littlecc.common.response;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author heyi
 * @date 2020-07-02 13:15
 * 响应异常
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class RException extends RuntimeException {

    private String code;

    private String msg;

    /**
     * 枚举异常
     *
     * @param rEnum
     */
    public RException(REnum rEnum) {
        super(rEnum.getMsg());
        this.code = rEnum.getCode();
        this.msg = rEnum.getMsg();
    }

    public RException(Throwable e) {
        super(e);
        this.msg = e.getMessage();
    }

    /**
     * 指定错误代码 错误信息
     *
     * @param code 错误码
     * @param msg  错误信息
     */
    public RException(String code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }
}
