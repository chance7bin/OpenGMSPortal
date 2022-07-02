package njgis.opengms.portal.service;

import com.alibaba.fastjson.JSONObject;
import njgis.opengms.portal.dao.CommentDao;
import njgis.opengms.portal.dao.GenericItemDao;
import njgis.opengms.portal.dao.UserDao;
import njgis.opengms.portal.entity.doo.JsonResult;
import njgis.opengms.portal.entity.doo.base.PortalItem;
import njgis.opengms.portal.entity.dto.comment.CommentDTO;
import njgis.opengms.portal.entity.po.Comment;
import njgis.opengms.portal.enums.ItemTypeEnum;
import njgis.opengms.portal.enums.OperationEnum;
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

    @Autowired
    UserDao userDao;

    @Autowired
    NoticeService noticeService;

    @Autowired
    GenericService genericService;

    public JsonResult addComment(CommentDTO commentDTO, String commentEmail){
        Comment comment = new Comment();
        ItemTypeEnum itemTypeByName = ItemTypeEnum.getItemTypeByName(commentDTO.getRelateItemTypeName());
        BeanUtils.copyProperties(commentDTO, comment, "relateItemTypeName","relateItemId");

        if (itemTypeByName == null){
            return ResultUtils.error("itemTypeByName输入错误");
        }
        JSONObject jsonObject = genericService.daoFactory(itemTypeByName);
        GenericItemDao dao = (GenericItemDao)jsonObject.get("itemDao");

        //解决 relateItemId 出现 5cd455936af4560a78eff832?language=en 这种情况
        String relateItemId = commentDTO.getRelateItemId();
        if (relateItemId.contains("?"))
            relateItemId = (relateItemId.split("\\?"))[0];

        comment.setRelateItemId(relateItemId);
        comment.setCreateTime(new Date());
        comment.setCommentEmail(commentEmail);
        comment.setRelateItemType(itemTypeByName);
        comment.setReadStatus(0);
        commentDao.insert(comment);

        PortalItem item = (PortalItem) dao.findFirstById(relateItemId);

        //关联子评论
        if (commentDTO.getParentId() != null) {
            Comment parentComment = commentDao.findFirstById(commentDTO.getParentId());
            parentComment.getSubComments().add(comment.getId());
            commentDao.save(parentComment);
        }

        // 给条目条目创建者、管理员发送通知
        noticeService.sendNoticeContainsAllAdmin(commentEmail,item.getAuthor(),item.getAdmins(), ItemTypeEnum.Comment,comment, OperationEnum.Comment);



        return ResultUtils.success();
    }

}
