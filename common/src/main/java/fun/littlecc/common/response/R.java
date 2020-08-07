package fun.littlecc.common.response;

import lombok.Getter;

import java.io.Serializable;

/**
 * @author
 * @date 2020-07-02 10:14
 */
@Getter
public class R<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 返回数据
     */
    private T data;

    /**
     * 返回码
     */
    private String code;

    /**
     * 返回信息
     */
    private String msg;

    /**
     * 构造方法
     */
    private R() {
    }

    /**
     * 构造方法
     *
     * @param code 响应码
     * @param msg  响应信息
     */
    private R(String code, String msg) {
        this.code = code;
        this.msg = msg;
        this.data = null;
    }

    /**
     * 构造方法
     *
     * @param code 响应码
     * @param msg  响应信息
     * @param data 响应结果
     */
    private R(String code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    /**
     * 构造方法
     *
     * @param rEnum 枚举信息
     */
    private R(REnum rEnum) {
        this.code = rEnum.getCode();
        this.msg = rEnum.getMsg();
        this.data = null;
    }

    /**
     * 无参请求成功
     *
     * @return R
     */
    public static R success() {
        return new R(REnum.SUCCESS);
    }

    /**
     * 带返回值 请求成功
     *
     * @param data 返回结果
     * @param <T>  泛型
     * @return R
     */
    public static <T> R<T> success(T data) {
        return new R<>(REnum.SUCCESS.getCode(), REnum.SUCCESS.getMsg(), data);
    }

    /**
     * 未知错误请求返回
     *
     * @param msg 未知错误
     * @return R
     */
    public static R error(String msg) {
        return new R(REnum.ERROR.getCode(), msg);
    }

    /**
     * 已知错误请求返回
     *
     * @param code 已知错误 code
     * @param msg  已知错误 msg
     * @return R
     */
    public static R error(String code, String msg) {
        return new R(code, msg);
    }

    /**
     * 枚举错误请求返回
     *
     * @param rEnum 枚举错误
     * @return R
     */
    public static R error(REnum rEnum) {
        return new R(rEnum);
    }
}
