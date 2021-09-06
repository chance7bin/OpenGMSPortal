package njgis.opengms.portal.dao;

import njgis.opengms.portal.entity.dto.ResultDTO;
import njgis.opengms.portal.entity.po.Template;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @Description
 * @Author bin
 * @Date 2021/08/26
 */
public interface TemplateDao extends MongoRepository<Template,String> , GenericItemDao<Template>{

    Page<ResultDTO> findByNameContainsIgnoreCase(String name, Pageable pageable);

    // Optional<Template> findById(String id);

    // Page<TemplateResultDTO> findByAuthor(String author,Pageable pageable);

}
