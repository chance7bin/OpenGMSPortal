package njgis.opengms.portal.dao;

import njgis.opengms.portal.entity.po.ModelItemVersion;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * @ClassName ModelItemDao
 * @Description todo
 * @Author Kai
 * @Date 2019/2/21
 * @Version 1.0.0
 * TODO
 */
public interface ModelItemVersionDao extends MongoRepository<ModelItemVersion,String> {

    List<ModelItemVersion> findAllByOriginOid(String oid,Pageable pageable);

    List<ModelItemVersion> findAllByVerStatus(int verStatus);

    ModelItemVersion findFirstByOid(String oid);

    List<ModelItemVersion> findAllByVerStatusAndClassifications2In(int num, String classification);

    List<ModelItemVersion> findByCreatorAndVerStatus(String creator, Integer verStatus);
}
