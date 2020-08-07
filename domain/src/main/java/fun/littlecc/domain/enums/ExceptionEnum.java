package fun.littlecc.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author heyi
 * @date 2020-07-02 14:37
 */
@AllArgsConstructor
public enum ExceptionEnum {

    /**
     * 自定义错误异常信息
     */
    USER_IS_NOT_EXIST("20000", "用户不存在");

    @Getter
    private String code;

    @Getter
    private String msg;
}
