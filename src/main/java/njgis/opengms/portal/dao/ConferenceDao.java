package njgis.opengms.portal.dao;

import njgis.opengms.portal.entity.po.Conference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @Description
 * @Author bin
 * @Date 2021/09/01
 */
public interface ConferenceDao extends MongoRepository<Conference,String>{

    Page<Conference> findByTitleContainsIgnoreCase(String name, Pageable pageable);

}
