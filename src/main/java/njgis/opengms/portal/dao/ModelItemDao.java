package njgis.opengms.portal.dao;

import njgis.opengms.portal.entity.doo.PortalId;
import njgis.opengms.portal.entity.po.ModelItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @ClassName ModelItemDao
 * @Description 模型条目数据访问层
 * @Author Kai
 * @Date 2019/2/21
 * @Version 1.0.0
 */
public interface ModelItemDao extends MongoRepository<ModelItem,String> {

    List<PortalId> findAllByAccessIdContains(String text);

    ModelItem findFirstById(String id);

}
