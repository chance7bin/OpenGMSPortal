package njgis.opengms.portal.dao;

import njgis.opengms.portal.entity.dto.ResultDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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

    T findFirstByAccessId(String accessId);


    Page<T> findAllByNameContainsIgnoreCaseAndStatusIn(String name, List<String> itemStatusVisible, Pageable pageable);
    Page<T> findAllByKeywordsContainsIgnoreCaseAndStatusIn(String keyword, List<String> itemStatusVisible, Pageable pageable);
    Page<T> findAllByOverviewContainsIgnoreCaseAndStatusIn(String content, List<String> itemStatusVisible, Pageable pageable);
    Page<T> findAllByAuthorLikeIgnoreCase(String author, Pageable pageable);

    Page<T> findAllByClassificationsIn(List<String> classifications,Pageable pageable);
    Page<T> findAllByClassificationsInAndStatusIn(List<String> classifications,List<String> itemStatusVisible,Pageable pageable);

    Page<T> findAllByNameContainsIgnoreCaseAndClassificationsInAndStatusIn(String name, List<String> classifications, List<String> itemStatusVisible, Pageable pageable);

    Page<T> findAllByKeywordsContainsIgnoreCaseAndClassificationsInAndStatusIn(String keyword, List<String> classifications, List<String> itemStatusVisible, Pageable pageable);
    Page<T> findAllByOverviewContainsIgnoreCaseAndClassificationsInAndStatusIn(String content, List<String> classifications, List<String> itemStatusVisible, Pageable pageable);
    Page<T> findAllByAuthorLikeIgnoreCaseAndClassificationsIn(String author, List<String> classifications, Pageable pageable);

    Page<T> findAllByAuthorInAndClassificationsInAndStatusIn(List<String> authors, List<String> classifications, List<String> itemStatusVisible, Pageable pageable);

    Page<T> findAllByAuthorInAndStatusIn(List<String> authors, List<String> itemStatusVisible, Pageable pageable);
    Page<T> findAllByAuthorInOrContributorsIn(List<String> authors, List<String> contributors, Pageable pageable);


    Page<T> findAll(Pageable pageable);

    Page<T> findAllByStatusIn(List<String> itemStatusVisible, Pageable pageable);

    Optional<T> findById(String id);

    <S extends T> S save(S var1);

    T insert(T item);

    Page<ResultDTO> findByAuthor(Pageable pageable, String author);

    void delete(T item);

    Page<T> findAllByNameContainsIgnoreCaseAndAuthor(String name, String author, Pageable pageable);

    Page<T> findAllByAuthor(String author, Pageable pageable);
}
