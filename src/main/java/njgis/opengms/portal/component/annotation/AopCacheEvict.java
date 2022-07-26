package njgis.opengms.portal.component.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Description
 * @Author bin
 * @Date 2022/07/19
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AopCacheEvict {
    //redis中的key值
    String key();
    //redis缓存的分组
    String group();
}
