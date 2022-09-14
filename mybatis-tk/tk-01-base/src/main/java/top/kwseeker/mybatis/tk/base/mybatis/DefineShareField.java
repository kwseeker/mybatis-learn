package top.kwseeker.mybatis.tk.base.mybatis;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DefineShareField {

    /** 分表数量 */
    int value() default 1;
}
