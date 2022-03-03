package njgis.opengms.portal.dao;

import njgis.opengms.portal.entity.po.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ArticleDao extends MongoRepository<Article,String> {

     Article findFirstById(String id);

}
