package njgis.opengms.portal.dao;

import njgis.opengms.portal.entity.doo.GenericCategory;
import njgis.opengms.portal.entity.po.Classification;
import njgis.opengms.portal.entity.po.TemplateClassification;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @Description
 * @Author bin
 * @Date 2021/08/30
 */
public interface TemplateClassificationDao extends MongoRepository<TemplateClassification, String>, GenericCategoryDao<TemplateClassification> {
    TemplateClassification findFirstById(String id);
}
