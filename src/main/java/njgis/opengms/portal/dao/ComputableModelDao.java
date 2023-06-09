package njgis.opengms.portal.dao;

import njgis.opengms.portal.component.annotation.AopCacheEnable;
import njgis.opengms.portal.entity.po.ComputableModel;
import njgis.opengms.portal.enums.ItemTypeEnum;
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

    @AopCacheEnable(key = "#id", group = ItemTypeEnum.ComputableModel, expireTime = 300)
    ComputableModel findFirstById(String id);

    Page<ComputableModel> findByContentType(String contentType, Pageable pageable);

    List<ComputableModel> findByContentType(String contentType);

    Page<ComputableModel> findByNameContainsIgnoreCaseAndDeploy(String name, boolean deploy, Pageable pageable);
    Page<ComputableModel> findAllByClassificationsInAndNameLikeIgnoreCaseAndDeploy(List<String> classifications,String name,boolean deploy, Pageable pageable);

    List<ComputableModel> findAllByNameIn(List<String> nameList);

    ComputableModel findFirstByIdAndDeploy(String id,boolean deploy);

    Page<ComputableModel> findAllByAuthorAndDeployAndStatusInAndNameLikeIgnoreCase(String userName, boolean deploy,List<String> status,String name, Pageable pageable);


}
