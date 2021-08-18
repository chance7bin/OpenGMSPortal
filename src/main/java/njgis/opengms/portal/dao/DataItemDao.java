package njgis.opengms.portal.dao;


import njgis.opengms.portal.entity.doo.PortalItem;
import njgis.opengms.portal.entity.po.DataItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * @InterfaceName DataItemDao
 * @Description 数据条目数据访问层
 * @Author sun_liber
 * @Date 2019/2/13
 * @Version 1.0.0
 */
public interface DataItemDao extends MongoRepository<DataItem,String>, GenericItemDao<DataItem> {

    DataItem findFirstById(String id);

    DataItem findFirstByAuthor(String author);

    DataItem findFirstByName(String name);


    Page<DataItem> findAllByNameContainsIgnoreCase(String name, Pageable pageable);
    Page<DataItem> findAllByKeywordsContainsIgnoreCase(String keyword, Pageable pageable);
    Page<DataItem> findAllByOverviewContainsIgnoreCase(String content, Pageable pageable);
    Page<DataItem> findAllByAuthorLikeIgnoreCase(String author, Pageable pageable);

    Page<DataItem> findAllByClassificationsIn(String categoryName,Pageable pageable);

    Page<DataItem> findAllByNameContainsIgnoreCaseAndClassificationsIn(String name, String categoryName, Pageable pageable);

    Page<DataItem> findAllByKeywordsContainsIgnoreCaseAndClassificationsIn(String keyword, String categoryName, Pageable pageable);
    Page<DataItem> findAllByOverviewContainsIgnoreCaseAndClassificationsIn(String content, String categoryName, Pageable pageable);
    Page<DataItem> findAllByAuthorLikeIgnoreCaseAndClassificationsIn(String author, String categoryName, Pageable pageable);

    Page<DataItem> findByAuthorInAndClassificationsIn(List<String> authors, String categoryName, Pageable pageable);

}
