package njgis.opengms.portal.dao;

import njgis.opengms.portal.entity.po.CheckModelList;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @Description
 * @Author bin
 * @Date 2021/11/11
 */
public interface CheckModelListDao extends MongoRepository<CheckModelList, String> {
    Page<CheckModelList> findAllByDraftNameContainsIgnoreCase(String draftName, Pageable pageable);
}
