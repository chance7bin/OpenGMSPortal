package njgis.opengms.portal.dao;

import njgis.opengms.portal.entity.po.ComputableModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * @Description
 * @Author bin
 * @Date 2021/11/08
 */
public interface ComputableModelDao  extends MongoRepository<ComputableModel,String>, GenericItemDao<ComputableModel> {

    Page<ComputableModel> findAllByDeployAndStatusInAndNameLikeIgnoreCase(boolean deploy, List<String> status, String name, Pageable pageable);

    ComputableModel findFirstByMd5(String md5);

    ComputableModel findFirstById(String id);

    Page<ComputableModel> findByContentType(String contentType, Pageable pageable);

    List<ComputableModel> findByContentType(String contentType);

    Page<ComputableModel> findByNameContainsIgnoreCaseAndDeploy(String name, boolean deploy, Pageable pageable);
    Page<ComputableModel> findAllByClassificationsInAndNameLikeIgnoreCaseAndDeploy(List<String> classifications,String name,boolean deploy, Pageable pageable);

    List<ComputableModel> findAllByNameIn(List<String> nameList);

}
