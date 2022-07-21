package njgis.opengms.portal.dao;

import njgis.opengms.portal.component.AopCacheEnable;
import njgis.opengms.portal.entity.po.DataHub;
import njgis.opengms.portal.enums.ItemTypeEnum;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @Description
 * @Author bin
 * @Date 2021/08/13
 */
public interface DataHubDao extends MongoRepository<DataHub, String>, GenericItemDao<DataHub> {

    @AopCacheEnable(key = "#id", group = ItemTypeEnum.DataHub, expireTime = 300)
    DataHub findFirstById(String id);

    // DataHub findFirstById(String id);
    //
    // DataHub findFirstByAuthor(String author);
    //
    // DataHub findFirstByName(String name);
    //
    //
    // Page<DataHub> findAllByNameContainsIgnoreCase(String name, Pageable pageable);
    // Page<DataHub> findAllByKeywordsContainsIgnoreCase(String keyword, Pageable pageable);
    // Page<DataHub> findAllByOverviewContainsIgnoreCase(String content, Pageable pageable);
    // Page<DataHub> findAllByAuthorLikeIgnoreCase(String author, Pageable pageable);
    //
    // Page<DataHub> findAllByClassificationsIn(List<String> classifications,Pageable pageable);
    //
    // Page<DataHub> findAllByNameContainsIgnoreCaseAndClassificationsIn(String name, List<String> classifications, Pageable pageable);
    //
    // Page<DataHub> findAllByKeywordsContainsIgnoreCaseAndClassificationsIn(String keyword, List<String> classifications, Pageable pageable);
    // Page<DataHub> findAllByOverviewContainsIgnoreCaseAndClassificationsIn(String content, List<String> classifications, Pageable pageable);
    // Page<DataHub> findAllByAuthorLikeIgnoreCaseAndClassificationsIn(String author, List<String> classifications, Pageable pageable);
    //
    // Page<DataHub> findAllByAuthorInAndClassificationsIn(List<String> authors, List<String> classifications, Pageable pageable);
    //
    // Page<DataHub> findAllByAuthorIn(List<String> authors, Pageable pageable);

    // Page<DataHub> findByAuthor(Pageable pageable, String author);
}
