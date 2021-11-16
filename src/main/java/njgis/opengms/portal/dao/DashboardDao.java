package njgis.opengms.portal.dao;

import njgis.opengms.portal.entity.po.DashBoard;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @Description
 * @Author bin
 * @Date 2021/11/16
 */
public interface DashboardDao extends MongoRepository<DashBoard, String> {

    DashBoard findFirstByName(String name);

}
