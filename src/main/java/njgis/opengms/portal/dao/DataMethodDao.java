package njgis.opengms.portal.dao;

import njgis.opengms.portal.component.annotation.AopCacheEnable;
import njgis.opengms.portal.entity.po.DataMethod;
import njgis.opengms.portal.enums.ItemTypeEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @Description
 * @Author bin
 * @Date 2021/08/13
 */
public interface DataMethodDao extends MongoRepository<DataMethod,String>, GenericItemDao<DataMethod> {
    @AopCacheEnable(key = "#id", group = ItemTypeEnum.DataMethod, expireTime = 300)
    DataMethod findFirstById(String id);

    // DataMethod findFirstById(String id);
    //
    // DataMethod findFirstByAuthor(String author);
    //
    // DataMethod findFirstByName(String name);
    //
    //
    // Page<DataMethod> findAllByNameContainsIgnoreCase(String name, Pageable pageable);
    // Page<DataMethod> findAllByKeywordsContainsIgnoreCase(String keyword, Pageable pageable);
    // Page<DataMethod> findAllByOverviewContainsIgnoreCase(String content, Pageable pageable);
    // Page<DataMethod> findAllByAuthorLikeIgnoreCase(String author, Pageable pageable);
    //
    // Page<DataMethod> findAllByClassificationsIn(List<String> classifications,Pageable pageable);
    //
    // Page<DataMethod> findAllByNameContainsIgnoreCaseAndClassificationsIn(String name, List<String> classifications, Pageable pageable);
    //
    // Page<DataMethod> findAllByKeywordsContainsIgnoreCaseAndClassificationsIn(String keyword, List<String> classifications, Pageable pageable);
    // Page<DataMethod> findAllByOverviewContainsIgnoreCaseAndClassificationsIn(String content, List<String> classifications, Pageable pageable);
    // Page<DataMethod> findAllByAuthorLikeIgnoreCaseAndClassificationsIn(String author, List<String> classifications, Pageable pageable);
    //
    // Page<DataMethod> findAllByAuthorInAndClassificationsIn(List<String> authors, List<String> classifications, Pageable pageable);
    //
    // Page<DataMethod> findAllByAuthorIn(List<String> authors, Pageable pageable);

    Page<DataMethod> findByAuthorAndType(Pageable pageable, String author, String type);

    Page<DataMethod> findByAuthorAndNameContainsAndType(Pageable pageable, String author, String name, String type);

    Page<DataMethod> findByNameLike(Pageable pageable,String name);


    Page<DataMethod> findAllByNameContainsIgnoreCase(String name, Pageable pageable);
    Page<DataMethod> findAllByKeywordsContainsIgnoreCase(String keyword, Pageable pageable);
    Page<DataMethod> findAllByOverviewContainsIgnoreCase(String content, Pageable pageable);
    Page<DataMethod> findAllByAuthorLikeIgnoreCase(String author, Pageable pageable);
    Page<DataMethod> findAllByNameContainsIgnoreCaseAndMethodLikeIgnoreCase(String name, String method, Pageable pageable);
    Page<DataMethod> findAllByKeywordsContainsIgnoreCaseAndMethodLikeIgnoreCase(String keyword, String method, Pageable pageable);
    Page<DataMethod> findAllByOverviewContainsIgnoreCaseAndMethodLikeIgnoreCase(String content, String method, Pageable pageable);
    Page<DataMethod> findAllByAuthorLikeIgnoreCaseAndMethodLikeIgnoreCase(String author, String method, Pageable pageable);
    Page<DataMethod> findAllByMethodLikeIgnoreCase(String method,Pageable pageable);

}
