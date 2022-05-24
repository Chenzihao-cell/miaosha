package czihao.miaosha.access;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/*
 * 只被用过一次，
 * MiaoshaController.java->getMiaoshaPath()方法上
 * 标注“ @AccessLimit(seconds = 5, maxCount = 5, needLogin = true)”注解，防刷
 * */
@Retention(RUNTIME)
@Target(METHOD)
public @interface AccessLimit {
    int seconds();

    int maxCount();

    boolean needLogin() default true;
}
