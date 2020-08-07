package fun.littlecc.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author
 * @date 2020-07-03 14:25
 */
@AllArgsConstructor
public enum StatusEnum {
    /**
     * 数据库中通用status 字段内容
     */
    DELETE(0, "无效/删除"),
    NORMAL(1, "正常");

    @Getter
    private final Integer status;
    @Getter
    private final String desc;
}
