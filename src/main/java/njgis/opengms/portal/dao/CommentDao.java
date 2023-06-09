package njgis.opengms.portal.dao;

import njgis.opengms.portal.entity.dto.comment.CommentResultDTO;
import njgis.opengms.portal.entity.po.Comment;
import njgis.opengms.portal.enums.ItemTypeEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CommentDao extends MongoRepository<Comment,String> {

     Comment findFirstById(String oid);

     Page<CommentResultDTO> findAllByRelateItemTypeAndRelateItemId(ItemTypeEnum type, String oid, Pageable pageable);

     List<Comment> findAllByCommentEmailOrReplyToUserEmail(String oid1, String oid2);

}
