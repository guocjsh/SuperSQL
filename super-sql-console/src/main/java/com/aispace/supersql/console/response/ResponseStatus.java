package com.aispace.supersql.console.response;

/**
 * @ClassName Constants
 * @Description 通用请求返回封装
 * @Author iron
 * @Date 2020/8/10 10:59
 * @Version 1.0
 */
public enum ResponseStatus {

    RESPONSE_SUCCESS(200, "请求成功"),
    RESPONSE_ERROR(500, "请求失败")
    ;


    /**
     * 内容
     */
    private Integer code;
    /**
     * name
     */
    private String value;

    /**
     * 构造方法
     */
    ResponseStatus(Integer code, String value) {
        this.code = code;
        this.value = value;
    }


    /**
     * 普通value查询name
     */
    public static String getName(Integer code) {
        for (ResponseStatus c : ResponseStatus.values()) {
            if (c.getCode().equals(code)) {
                return c.value;
            }
        }
        return null;
    }


    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }


    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}