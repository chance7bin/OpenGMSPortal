package njgis.opengms.portal.dao;

import njgis.opengms.portal.entity.po.Template;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * @Description
 * @Author bin
 * @Date 2021/08/26
 */
public interface TemplateDao extends MongoRepository<Template,String> , GenericItemDao<Template>{

    // Page<ResultDTO> findByNameContainsIgnoreCase(String name, Pageable pageable);

    List<Template> findAllByName(String name);
    // Optional<Template> findById(String id);

    // Page<TemplateResultDTO> findByAuthor(String author,Pageable pageable);

}
