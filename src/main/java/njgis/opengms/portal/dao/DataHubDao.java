package njgis.opengms.portal.dao;

import njgis.opengms.portal.entity.doo.PortalItem;
import njgis.opengms.portal.entity.po.DataHub;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * @Description
 * @Author bin
 * @Date 2021/08/13
 */
public interface DataHubDao extends MongoRepository<DataHub, String>, GenericItemDao<DataHub> {
    DataHub findFirstById(String id);

    DataHub findFirstByAuthor(String author);

    DataHub findFirstByName(String name);


    Page<DataHub> findAllByNameContainsIgnoreCase(String name, Pageable pageable);
    Page<DataHub> findAllByKeywordsContainsIgnoreCase(String keyword, Pageable pageable);
    Page<DataHub> findAllByOverviewContainsIgnoreCase(String content, Pageable pageable);
    Page<DataHub> findAllByAuthorLikeIgnoreCase(String author, Pageable pageable);

    Page<DataHub> findAllByClassificationsIn(String categoryName,Pageable pageable);

    Page<DataHub> findAllByNameContainsIgnoreCaseAndClassificationsIn(String name, String categoryName, Pageable pageable);

    Page<DataHub> findAllByKeywordsContainsIgnoreCaseAndClassificationsIn(String keyword, String categoryName, Pageable pageable);
    Page<DataHub> findAllByOverviewContainsIgnoreCaseAndClassificationsIn(String content, String categoryName, Pageable pageable);
    Page<DataHub> findAllByAuthorLikeIgnoreCaseAndClassificationsIn(String author, String categoryName, Pageable pageable);

    Page<DataHub> findByAuthorInAndClassificationsIn(List<String> authors, String categoryName, Pageable pageable);
}
