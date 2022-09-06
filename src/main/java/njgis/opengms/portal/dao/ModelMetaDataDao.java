package njgis.opengms.portal.dao;

import njgis.opengms.portal.entity.po.ModelMetaData;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ModelMetaDataDao extends MongoRepository<ModelMetaData, String> {

  ModelMetaData findFirstById(String id);

}
