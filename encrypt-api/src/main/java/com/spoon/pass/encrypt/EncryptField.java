package com.spoon.pass.encrypt;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by wangkunlin
 * On 2020-02-20
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.FIELD)
public @interface EncryptField {

    /**
     * 需要加密的字符串
     */
    String src();

    /**
     * 是否是 不解密
     */
    boolean noDecrypt() default false;

    /**
     * 单独指定密码，如果为空则使用 顶级密码
     */
    String password() default "";

}
