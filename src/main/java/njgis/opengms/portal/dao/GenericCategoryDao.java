package njgis.opengms.portal.dao;

import njgis.opengms.portal.entity.doo.GenericCatalog;
import njgis.opengms.portal.entity.po.Classification;

/**
 * @Description 通用目录dao
 * @Author bin
 * @Date 2021/08/16
 */
public interface GenericCategoryDao<T> {
    T findFirstById(String id);

    T findFirstByNameEn(String name);
}
