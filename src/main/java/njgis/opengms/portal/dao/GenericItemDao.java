package njgis.opengms.portal.dao;

import njgis.opengms.portal.entity.doo.PortalItem;
import njgis.opengms.portal.entity.po.DataHub;
import njgis.opengms.portal.entity.po.DataItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

/**
 * @Description 通用数据条目dao层，item、hub、method都继承自该类
 *              由于item、hub、method的searchDataItems方法参数都一样
 *              调用searchDataItems时动态注入相应的dao即可
 * @Author bin
 * @Date 2021/08/13
 */
public interface GenericItemDao<T>{
    T findFirstById(String id);

    T findFirstByAuthor(String author);

    T findFirstByName(String name);


    Page<T> findAllByNameContainsIgnoreCase(String name, Pageable pageable);
    Page<T> findAllByKeywordsContainsIgnoreCase(String keyword, Pageable pageable);
    Page<T> findAllByOverviewContainsIgnoreCase(String content, Pageable pageable);
    Page<T> findAllByAuthorLikeIgnoreCase(String author, Pageable pageable);

    Page<T> findAllByClassificationsIn(String categoryName,Pageable pageable);

    Page<T> findAllByNameContainsIgnoreCaseAndClassificationsIn(String name, String categoryName, Pageable pageable);

    Page<T> findAllByKeywordsContainsIgnoreCaseAndClassificationsIn(String keyword, String categoryName, Pageable pageable);
    Page<T> findAllByOverviewContainsIgnoreCaseAndClassificationsIn(String content, String categoryName, Pageable pageable);
    Page<T> findAllByAuthorLikeIgnoreCaseAndClassificationsIn(String author, String categoryName, Pageable pageable);

    Page<T> findByAuthorInAndClassificationsIn(List<String> authors, String categoryName, Pageable pageable);

    Page<T> findAll(Pageable pageable);

    Optional<T> findById(String id);

    <S extends T> S save(S var1);

    T insert(T item);
}
