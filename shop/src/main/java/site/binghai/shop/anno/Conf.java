package site.binghai.shop.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author huaishuo
 * @date 2020/2/28 下午9:32
 **/
@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Conf {
    /**
     * hint
     */
    String value();

    boolean img() default false;
}
