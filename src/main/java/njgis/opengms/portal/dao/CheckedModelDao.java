package njgis.opengms.portal.dao;

import njgis.opengms.portal.entity.doo.CheckedModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * @Description
 * @Author bin
 * @Date 2021/11/16
 */
public interface CheckedModelDao extends MongoRepository<CheckedModel, String> {
    // CheckedModel findFirstById(String id);

    List<CheckedModel> findAllByStatusIn(List<Integer> status);
}
