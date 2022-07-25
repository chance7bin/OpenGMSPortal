package njgis.opengms.portal.dao;

import njgis.opengms.portal.component.annotation.AopCacheEnable;
import njgis.opengms.portal.entity.doo.base.PortalIdPlus;
import njgis.opengms.portal.entity.po.ModelItem;
import njgis.opengms.portal.enums.ItemTypeEnum;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * @ClassName ModelItemDao
 * @Description 模型条目数据访问层
 * @Author Kai
 * @Date 2019/2/21
 * @Version 1.0.0
 */
public interface ModelItemDao extends MongoRepository<ModelItem,String>, GenericItemDao<ModelItem> {

    List<PortalIdPlus> findAllByAccessIdContains(String text);

    @AopCacheEnable(key = "#id", group = ItemTypeEnum.ModelItem, expireTime = 300)
    ModelItem findFirstById(String id);

    ////服务于QueryList////
    // Page<ModelItemResultDTO> findByNameContainsIgnoreCase(String name, Pageable pageable);

    // Page<ModelItemResultDTO> findByNameContainsIgnoreCaseAndStatusIn(String name, List<String> status, Pageable pageable);

    // Page<ModelItemResultDTO> findByNameContainsIgnoreCaseAndAuthorAndStatusIn(String name, String email, List<String> status, Pageable pageable);

    // ?0表明使用第一个变量，以此类推
    // @Query("{'keywords':{'$regex': '?0','$options':'i'}}")
    // Page<ModelItemResultDTO> findByKeywordsIgnoreCaseIn(String keyword, Pageable pageable);

    // @Query("{'keywords':{'$regex': '?0','$options':'i'}, 'status':{$in:?1}}")
    // Page<ModelItemResultDTO> findByKeywordsIgnoreCaseInAndStatusIn(String keyword, List<String> status,Pageable pageable);

    // @Query("{'keywords':{'$regex': '?0','$options':'i'}, 'author':?1, 'status':{$in:?2}}")
    // Page<ModelItemResultDTO> findByKeywordsIgnoreCaseInAndAuthorAndStatusIn(String keyword, String email, List<String> status,Pageable pageable);


    // @Query("{$or:[{ 'overview':{'$regex': '?0','$options':'i'}}, {'localizationList.description': {'$regex': '?0','$options':'i'}}]}")
    // Page<ModelItemResultDTO> findByOverviewContainsIgnoreCaseAndLocalizationDescription(String overview, Pageable pageable);

    //    @Query("{$and:[{$or:[{ 'description':{'$regex':/swat/,'$options':'i'}}, {'localizationList.description': {'$regex':/swat/,'$options':'i'}}]},{'status':{$in:['Private','Public']}}]}")
    // @Query("{$and:[{$or:[{ 'overview':{'$regex': '?0','$options':'i'}}, {'localizationList.description': {'$regex': '?0','$options':'i'}}]},{'status':{$in:?1}}]}")
    // Page<ModelItemResultDTO> findByOverviewContainsIgnoreCaseAndLocalizationDescriptionAndStatusIn(String overview, List<String> status,Pageable pageable);

    // @Query("{$and:[{$or:[{ 'overview':{'$regex': '?0','$options':'i'}}, {'localizationList.description': {'$regex': '?0','$options':'i'}}]},{'author':?1},{'status':{$in:?2}}]}")
    // Page<ModelItemResultDTO> findByOverviewContainsIgnoreCaseAndLocalizationDescriptionAndAuthorAndStatusIn(String overview, String email, List<String> status,Pageable pageable);


    // Page<ModelItemResultDTO> findByAuthorInAndStatusInOrContributorsInAndStatusIn(List<String> authors, List<String> status0, List<String> contributors, List<String> status1,Pageable pageable);

    // Page<ModelItemResultDTO> findByClassificationsInAndStatusIn(List<String> classes, List<String> status, Pageable pageable);

    ////END////

}
