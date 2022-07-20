package njgis.opengms.portal.service;

import njgis.opengms.portal.entity.doo.base.PortalItem;
import njgis.opengms.portal.enums.ItemTypeEnum;

/**
 * @Description
 * @Author bin
 * @Date 2022/07/19
 */
public interface RedisService {
    void set(String key, Object value);

    void set(String key, Object value, long expire);

    Object get(String key);

    void expire(String key, long expire);

    void delete(String key);


    // 由于dao接口同时继承了MongoRepository和GenericItemDao，
    // 所以这边写接口调用他们防止继承冲突
    PortalItem saveItem(PortalItem item, ItemTypeEnum type);

    void deleteItem(PortalItem item, ItemTypeEnum type);

}

