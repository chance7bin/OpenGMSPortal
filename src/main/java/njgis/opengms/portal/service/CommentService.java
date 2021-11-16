package njgis.opengms.portal.service;

import njgis.opengms.portal.dao.CommentDao;
import njgis.opengms.portal.entity.doo.JsonResult;
import njgis.opengms.portal.entity.dto.comment.CommentDTO;
import njgis.opengms.portal.entity.po.Comment;
import njgis.opengms.portal.enums.ItemTypeEnum;
import njgis.opengms.portal.utils.ResultUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @Description 评论服务层
 * @Author kx
 * @Date 21/11/16
 * @Version 1.0.0
 */
@Service
public class CommentService {

    @Autowired
    CommentDao commentDao;

    public JsonResult addComment(CommentDTO commentDTO, String authorEmail){
        Comment comment = new Comment();
        BeanUtils.copyProperties(commentDTO, comment);

        comment.setDate(new Date());
        comment.setAuthorId(authorEmail);
        comment.setRelateItemType(ItemTypeEnum.getItemTypeByName(commentDTO.getRelateItemTypeName()));
        comment.setReadStatus(0);

        commentDao.insert(comment);

        //关联子评论
        if (commentDTO.getParentId() != null) {
            Comment parentComment = commentDao.findFirstById(commentDTO.getParentId());
            parentComment.getSubComments().add(comment.getId());
            commentDao.save(parentComment);
        }

        return ResultUtils.success();
    }

}
