package njgis.opengms.portal.component;

import lombok.extern.slf4j.Slf4j;
import njgis.opengms.portal.component.annotation.*;
import njgis.opengms.portal.entity.dto.SpecificFindDTO;
import njgis.opengms.portal.enums.ItemTypeEnum;
import njgis.opengms.portal.service.RedisService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import java.lang.reflect.Method;

/**
 * @Description 自定义缓存切面具体实现类
 * @Author bin
 * @Date 2022/07/19
 */
@Slf4j
@Aspect
@Component
public class CacheEnableAspect {


    @Autowired
    RedisService redisService;


    @Value("${redis.enable}")
    boolean redisEnable;

    /**
     * 用于SpEL表达式解析.
     */
    private SpelExpressionParser parser = new SpelExpressionParser();
    /**
     * 用于获取方法参数定义名字.
     */
    private DefaultParameterNameDiscoverer nameDiscoverer = new DefaultParameterNameDiscoverer();


    /**
     * Mapper层切点 使用到了我们定义的 AopCacheEnable 作为切点表达式。
     */
    @Pointcut("@annotation(njgis.opengms.portal.component.annotation.AopCacheEnable)")
    public void queryCache() {
    }

    /**
     * Mapper层切点 使用到了我们定义的 AopCacheEvict 作为切点表达式。
     */
    @Pointcut("@annotation(njgis.opengms.portal.component.annotation.AopCacheEvict)")
    public void ClearCache() {
    }

    @Pointcut("@annotation(njgis.opengms.portal.component.annotation.UserCacheEnable)")
    public void queryUserCache() {
    }

    @Pointcut("@annotation(njgis.opengms.portal.component.annotation.UserCacheEvict)")
    public void clearUserCache() {
    }


    //分页切点
    @Pointcut("@annotation(njgis.opengms.portal.component.annotation.PageableCacheEnable)")
    public void PageableCache() {
    }



    @Around("queryCache()")
    public Object Interceptor(ProceedingJoinPoint pjp) throws Throwable {

        // System.out.println(redisEnable);
        if (!redisEnable){
            return pjp.proceed();
        }

        // StringBuilder redisKeySb = new StringBuilder("AOP").append("::");
        StringBuilder redisKeySb = new StringBuilder("item");

        // 类
        // String className = pjp.getTarget().toString().split("@")[0];
        // redisKeySb.append(className).append("::");

        //获取当前被切注解的方法名
        Method method = getMethod(pjp);

        //获取当前被切方法的注解
        AopCacheEnable aopCacheEnable = method.getAnnotation(AopCacheEnable.class);
        if (aopCacheEnable == null) {
            return pjp.proceed();
        }

        //获取被切注解方法返回类型
        // Type returnType = method.getAnnotatedReturnType().getType();
        // String[] split = returnType.getTypeName().split("\\.");
        // String type = split[split.length - 1];
        // redisKeySb.append(":").append(type);

        //从注解中获取key
        //通过注解key使用的SpEL表达式获取到SpEL执行结果
        String key = aopCacheEnable.key();
        // redisKeySb.append(args);
        String resV = generateKeyBySpEL(key, pjp).toString();
        redisKeySb.append(":").append(aopCacheEnable.group()).append(":").append(resV);


        //获取方法参数值
        // Object[] arguments = pjp.getArgs();
        // redisKeySb.append(":").append(arguments[0]);

        return query(redisKeySb, aopCacheEnable.expireTime(), pjp);

    }

    /*** 定义清除缓存逻辑，先操作数据库，后清除缓存*/
    @Around(value = "ClearCache()")
    public Object evict(ProceedingJoinPoint pjp) throws Throwable {

        if (!redisEnable){
            return pjp.proceed();
        }

        StringBuilder redisKeySb = new StringBuilder("item");

        Method method = getMethod(pjp);
        // 获取方法的注解
        AopCacheEvict cacheEvict = method.getAnnotation(AopCacheEvict.class);
        if (cacheEvict == null) {
            return pjp.proceed();
        }

        //从注解中获取key
        //通过注解key使用的SpEL表达式获取到SpEL执行结果
        String key = cacheEvict.key();
        // redisKeySb.append(args);
        key = generateKeyBySpEL(key, pjp).toString();

        //清楚缓存的group从参数拿
        String group = cacheEvict.group();
        ItemTypeEnum type = (ItemTypeEnum)generateKeyBySpEL(group, pjp);

        redisKeySb.append(":").append(type).append(":").append(key);

        //先操作db
        Object result = pjp.proceed();

        //根据key从缓存中删除
        String redisKey = redisKeySb.toString();
        try {
            redisService.delete(redisKey);
        } catch (Exception e){
            log.error("redis connection exception");
        }

        return result;

    }


    @Around("queryUserCache()")
    public Object userQuery(ProceedingJoinPoint pjp) throws Throwable{
        // System.out.println(redisEnable);
        if (!redisEnable){
            return pjp.proceed();
        }

        StringBuilder redisKeySb = new StringBuilder("user");


        //获取当前被切注解的方法名
        Method method = getMethod(pjp);

        //获取当前被切方法的注解
        UserCacheEnable userCacheEnable = method.getAnnotation(UserCacheEnable.class);
        if (userCacheEnable == null) {
            return pjp.proceed();
        }

        //从注解中获取key
        //通过注解key使用的SpEL表达式获取到SpEL执行结果
        String key = userCacheEnable.key();
        // redisKeySb.append(args);
        String resV = generateKeyBySpEL(key, pjp).toString();
        redisKeySb.append(":").append(resV);


        return query(redisKeySb, userCacheEnable.expireTime(), pjp);
    }

    @Around("clearUserCache()")
    public Object userEvict(ProceedingJoinPoint pjp) throws Throwable{
        if (!redisEnable){
            return pjp.proceed();
        }

        StringBuilder redisKeySb = new StringBuilder("user");

        Method method = getMethod(pjp);
        // 获取方法的注解
        UserCacheEvict cacheEvict = method.getAnnotation(UserCacheEvict.class);
        if (cacheEvict == null) {
            return pjp.proceed();
        }

        //从注解中获取key
        //通过注解key使用的SpEL表达式获取到SpEL执行结果
        String key = cacheEvict.key();
        // redisKeySb.append(args);
        key = generateKeyBySpEL(key, pjp).toString();

        redisKeySb.append(":").append(key);

        //先操作db
        Object result = pjp.proceed();

        //根据key从缓存中删除
        String redisKey = redisKeySb.toString();
        try {
            redisService.delete(redisKey);
        } catch (Exception e){
            log.error("redis connection exception");
        }

        return result;
    }


    @Around("PageableCache()")
    public Object pageableQuery(ProceedingJoinPoint pjp) throws Throwable{


        if (!redisEnable){
            return pjp.proceed();
        }

        StringBuilder redisKeySb = new StringBuilder("pageable");

        Method method = getMethod(pjp);
        // 获取方法的注解
        PageableCacheEnable pageableCacheEnable = method.getAnnotation(PageableCacheEnable.class);
        if (pageableCacheEnable == null) {
            return pjp.proceed();
        }

        //从注解中获取key
        //通过注解key使用的SpEL表达式获取到SpEL执行结果
        String key = pageableCacheEnable.key();
        // redisKeySb.append(args);
        SpecificFindDTO findDTO = (SpecificFindDTO)generateKeyBySpEL(key, pjp);

        //获取group
        String group = pageableCacheEnable.group();
        ItemTypeEnum type = (ItemTypeEnum)generateKeyBySpEL(group, pjp);


        redisKeySb.append(":").append(type).append(":").append(findDTO.getCategoryName())
            .append("_").append(findDTO.getSortField())
            .append("_").append(findDTO.getAsc())
            .append("_").append(findDTO.getPage())
            .append("_").append(findDTO.getPageSize())
            .append("_").append(findDTO.getCurQueryField())
            .append("_").append(findDTO.getSearchText())
            ;

        return query(redisKeySb, pageableCacheEnable.expireTime(), pjp);
    }


    /**
     * 获取被拦截方法对象
     */
    public Method getMethod(ProceedingJoinPoint pjp) {
        Signature signature = pjp.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method targetMethod = methodSignature.getMethod();
        return targetMethod;
    }



    public Object generateKeyBySpEL(String spELString, ProceedingJoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String[] paramNames = nameDiscoverer.getParameterNames(methodSignature.getMethod());
        Expression expression = parser.parseExpression(spELString);
        EvaluationContext context = new StandardEvaluationContext();
        Object[] args = joinPoint.getArgs();
        for (int i = 0; i < args.length; i++) {
            context.setVariable(paramNames[i], args[i]);
        }
        return expression.getValue(context);
    }



    public Object query(StringBuilder redisKeySb, long expire, ProceedingJoinPoint pjp){
        String redisKey = redisKeySb.toString();

        Object result = null;
        //redis被击穿连不上之后直接访问数据库
        try {
            result = redisService.get(redisKey);
        } catch (Exception e){
            log.error("redis connection exception");
            try {
                result = pjp.proceed();
                // log.info("从数据库中获取数据");
            } catch (Throwable ex) {
                throw new RuntimeException(ex.getMessage(), ex);
            }
            return result;
        }
        if (result != null) {
            // log.info("从Redis中获取数据");
            return result;
        } else {
            try {
                result = pjp.proceed();
                // log.info("从数据库中获取数据");
            } catch (Throwable e) {
                throw new RuntimeException(e.getMessage(), e);
            }

            // 获取失效时间
            // long expire = pageableCacheEnable.expireTime();
            redisService.set(redisKey, result, expire);
        }
        return result;
    }
}
