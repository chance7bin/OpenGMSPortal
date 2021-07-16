package njgis.opengms.portal.dao;

import njgis.opengms.portal.entity.po.ModelClassification;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @Description 模型分类数据访问层
 * @Author kx
 * @Date 2021/7/7
 * @Version 1.0.0
 */
public interface ModelClassificationDao extends MongoRepository<ModelClassification,String> {

    ModelClassification findFirstById(String id);

}
