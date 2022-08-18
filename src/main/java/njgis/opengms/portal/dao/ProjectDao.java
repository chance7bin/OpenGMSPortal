package njgis.opengms.portal.dao;

import njgis.opengms.portal.entity.po.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @Description
 * @Author bin
 * @Date 2022/03/14
 */
public interface ProjectDao extends MongoRepository<Project, String> {

    Page<Project> findByProjectNameContainsIgnoreCase(String name, Pageable pageable);

    Page<Project> findByProjectNameContainsIgnoreCaseAndContributor(String name, String email, Pageable pageable);
}
