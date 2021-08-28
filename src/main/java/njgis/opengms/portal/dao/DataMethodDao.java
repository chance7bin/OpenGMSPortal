package njgis.opengms.portal.dao;

import njgis.opengms.portal.entity.po.DataMethod;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * @Description
 * @Author bin
 * @Date 2021/08/13
 */
public interface DataMethodDao extends MongoRepository<DataMethod,String>, GenericItemDao<DataMethod> {
    DataMethod findFirstById(String id);

    DataMethod findFirstByAuthor(String author);

    DataMethod findFirstByName(String name);


    Page<DataMethod> findAllByNameContainsIgnoreCase(String name, Pageable pageable);
    Page<DataMethod> findAllByKeywordsContainsIgnoreCase(String keyword, Pageable pageable);
    Page<DataMethod> findAllByOverviewContainsIgnoreCase(String content, Pageable pageable);
    Page<DataMethod> findAllByAuthorLikeIgnoreCase(String author, Pageable pageable);

    Page<DataMethod> findAllByClassificationsIn(String categoryName,Pageable pageable);

    Page<DataMethod> findAllByNameContainsIgnoreCaseAndClassificationsIn(String name, String categoryName, Pageable pageable);

    Page<DataMethod> findAllByKeywordsContainsIgnoreCaseAndClassificationsIn(String keyword, String categoryName, Pageable pageable);
    Page<DataMethod> findAllByOverviewContainsIgnoreCaseAndClassificationsIn(String content, String categoryName, Pageable pageable);
    Page<DataMethod> findAllByAuthorLikeIgnoreCaseAndClassificationsIn(String author, String categoryName, Pageable pageable);

    Page<DataMethod> findByAuthorInAndClassificationsIn(List<String> authors, String categoryName, Pageable pageable);

    Page<DataMethod> findByAuthorAndType(Pageable pageable, String author, String type);

    Page<DataMethod> findByAuthorAndNameContainsAndType(Pageable pageable, String author, String name, String type);
}
