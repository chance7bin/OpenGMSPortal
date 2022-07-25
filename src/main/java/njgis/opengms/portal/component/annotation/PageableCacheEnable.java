package njgis.opengms.portal.component.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Description 分页查询 redis缓存
 * @Author bin
 * @Date 2022/07/19
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface PageableCacheEnable {
    //redis缓存key
    String key();
    //redis缓存存活时间默认值（可自定义）
    long expireTime() default 1800;
    //redis缓存的分组
    String group();
}
