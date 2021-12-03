package njgis.opengms.portal.dao;

import njgis.opengms.portal.entity.po.Theme;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @Description
 * @Author bin
 * @Date 2021/12/02
 */
public interface ThemeDao extends MongoRepository<Theme, String> ,GenericItemDao<Theme>{

}
