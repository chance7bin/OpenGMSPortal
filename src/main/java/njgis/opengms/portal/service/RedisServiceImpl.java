package njgis.opengms.portal.service;

import njgis.opengms.portal.component.AopCacheEvict;
import njgis.opengms.portal.dao.GenericItemDao;
import njgis.opengms.portal.entity.doo.base.PortalItem;
import njgis.opengms.portal.enums.ItemTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @Description 与redis服务有关的接口
 * @Author bin
 * @Date 2022/07/19
 */
@Service("redisService")
public class RedisServiceImpl implements RedisService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    GenericService genericService;

    @Override
    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    @Override
    public void set(String key, Object value, long expire) {
        redisTemplate.opsForValue().set(key, value, expire, TimeUnit.SECONDS);
    }

    @Override
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public void expire(String key, long expire) {
        redisTemplate.expire(key, expire, TimeUnit.SECONDS);
    }

    @Override
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    @Override
    @AopCacheEvict(key = "#item.id", group = "#type")
    public PortalItem saveItem(PortalItem item, ItemTypeEnum type) {
        GenericItemDao itemDao = (GenericItemDao)genericService.daoFactory(type).get("itemDao");
        PortalItem result = (PortalItem)itemDao.save(item);
        return result;
    }

    @Override
    @AopCacheEvict(key = "#item.id", group = "#type")
    public void deleteItem(PortalItem item, ItemTypeEnum type) {
        GenericItemDao itemDao = (GenericItemDao)genericService.daoFactory(type).get("itemDao");
        itemDao.delete(item);
    }
}

