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
@Target(ElementType.TYPE)
public @interface Encrypt {

    /**
     * 指定密码
     */
    String password() default "";

    /**
     * 是否是 随机密码
     * 如果这里返回了 true，并且 同时指定了密码，
     * 则使用随机密码，也就是说，随机的优先级高于固定密码
     */
    boolean randomPsw();

    /**
     * 随机密码时，密码的长度最小为6，如果小于6，则在 6-12 中随机一个长度。
     */
    int pswLength() default -1;

}
