package njgis.opengms.portal.dao;

import njgis.opengms.portal.entity.po.Classification;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * @Description
 * @Author bin
 * @Date 2021/08/12
 */
public interface ClassificationDao extends MongoRepository<Classification,String>, GenericCategoryDao<Classification> {

    Classification findFirstById(String id);

    List<Classification> findAllByParentId(String id);

    Classification findFirstByNameEn(String name);
}
