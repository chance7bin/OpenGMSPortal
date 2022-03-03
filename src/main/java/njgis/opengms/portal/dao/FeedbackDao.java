package njgis.opengms.portal.dao;

import njgis.opengms.portal.entity.po.Feedback;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @Description
 * @Author bin
 * @Date 2022/03/03
 */
public interface FeedbackDao extends MongoRepository<Feedback,String> {

}
