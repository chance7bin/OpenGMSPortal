package njgis.opengms.portal.dao;


import njgis.opengms.portal.entity.po.DataItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @InterfaceName DataItemDao
 * @Description 数据条目数据访问层
 * @Author sun_liber
 * @Date 2019/2/13
 * @Version 1.0.0
 */
public interface DataItemDao extends MongoRepository<DataItem,String>, GenericItemDao<DataItem> {

    // DataItem findFirstById(String id);
    //
    // DataItem findFirstByAuthor(String author);
    //
    // DataItem findFirstByName(String name);
    //
    //
    // Page<DataItem> findAllByNameContainsIgnoreCaseAndStatusIn(String name, Pageable pageable);
    // Page<DataItem> findAllByKeywordsContainsIgnoreCase(String keyword, Pageable pageable);
    // Page<DataItem> findAllByOverviewContainsIgnoreCase(String content, Pageable pageable);
    // Page<DataItem> findAllByAuthorLikeIgnoreCase(String author, Pageable pageable);
    //
    // Page<DataItem> findAllByClassificationsIn(List<String> classifications,Pageable pageable);
    //
    // Page<DataItem> findAllByNameContainsIgnoreCaseAndClassificationsIn(String name, List<String> classifications, Pageable pageable);
    //
    // Page<DataItem> findAllByKeywordsContainsIgnoreCaseAndClassificationsIn(String keyword, List<String> classifications, Pageable pageable);
    // Page<DataItem> findAllByOverviewContainsIgnoreCaseAndClassificationsIn(String overview, List<String> classifications, Pageable pageable);
    // Page<DataItem> findAllByAuthorLikeIgnoreCaseAndClassificationsIn(String author, List<String> classifications, Pageable pageable);
    //
    // Page<DataItem> findAllByAuthorInAndClassificationsIn(List<String> authors, List<String> classifications, Pageable pageable);
    //
    // Page<DataItem> findAllByAuthorIn(List<String> authors, Pageable pageable);

    // Page<DataItem> findAllByStatusIn(Pageable pageable, String author);

    Page<DataItem> findByNameContainingOrOverviewContainingOrKeywordsContaining(Pageable pageable, String name, String overview, String keywords);

}
