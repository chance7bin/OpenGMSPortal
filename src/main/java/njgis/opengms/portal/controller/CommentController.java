package njgis.opengms.portal.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import njgis.opengms.portal.component.LoginRequired;
import njgis.opengms.portal.dao.CommentDao;
import njgis.opengms.portal.dao.GenericItemDao;
import njgis.opengms.portal.dao.UserDao;
import njgis.opengms.portal.entity.doo.JsonResult;
import njgis.opengms.portal.entity.doo.base.PortalId;
import njgis.opengms.portal.entity.doo.base.PortalItem;
import njgis.opengms.portal.entity.dto.comment.CommentDTO;
import njgis.opengms.portal.entity.dto.comment.CommentResultDTO;
import njgis.opengms.portal.entity.po.Comment;
import njgis.opengms.portal.entity.po.User;
import njgis.opengms.portal.enums.ItemTypeEnum;
import njgis.opengms.portal.service.CommentService;
import njgis.opengms.portal.service.GenericService;
import njgis.opengms.portal.service.UserService;
import njgis.opengms.portal.utils.ResultUtils;
import njgis.opengms.portal.utils.Utils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @Description
 * @Author bin
 * @Date 2021/08/07
 */
@Controller
@RequestMapping(value="/comment")
public class CommentController {

    @Autowired
    CommentService commentService;

    @Autowired
    CommentDao commentDao;

    @Autowired
    UserDao userDao;

    @Autowired
    UserService userService;

    @Autowired
    GenericService genericService;

    private JSONArray userArray=new JSONArray();

    SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm");

    @LoginRequired
    @RequestMapping(value="/add",method = RequestMethod.POST)
    public JsonResult add(@RequestBody CommentDTO commentDTO, HttpServletRequest request){

        String email = Utils.checkLoginStatus(request);
        return commentService.addComment(commentDTO, email);


    }

    @LoginRequired
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public JsonResult delete(@RequestParam("oid") String oid, HttpServletRequest request){

        Comment comment = commentDao.findFirstById(oid);

        //删除与父评论关联
        if (comment.getParentId() != null) {
            Comment parentComment = commentDao.findFirstById(comment.getParentId());
            parentComment.getSubComments().remove(oid);
            commentDao.save(parentComment);
        }

        //删除子评论
        if (comment.getSubComments().size() != 0) {
            for (String subOid : comment.getSubComments()) {
                Comment subComment = commentDao.findFirstById(subOid);
                commentDao.delete(subComment);
            }
        }

        commentDao.delete(comment);

        return ResultUtils.success();


    }

    @RequestMapping(value="/getCommentsByTypeAndOid", method = RequestMethod.GET)
    public JsonResult getCommentsByTypeAndOid(@RequestParam("type") String type,
                                              @RequestParam("oid") String oid,
                                              @RequestParam("sort") int asc){
        ItemTypeEnum itemTypeEnum=ItemTypeEnum.getItemTypeByName(type);
        Sort sort=Sort.by(asc==1?Sort.Direction.ASC:Sort.Direction.DESC,"date");
        Pageable pageable=PageRequest.of(0,999,sort);
        Page<CommentResultDTO> comments=commentDao.findAllByRelateItemTypeAndRelateItemId(itemTypeEnum,oid,pageable);

        List<CommentResultDTO> commentResultDTOList=comments.getContent();
        JSONArray commentList=new JSONArray();
        int count=0;
        for(CommentResultDTO commentResultDTO:commentResultDTOList){
            if(commentResultDTO.getParentId()!=null){
                continue;
            }
            count++;
            JSONObject commentObj=new JSONObject();

            commentObj.put("oid",commentResultDTO.getOid());
            commentObj.put("content",commentResultDTO.getContent());
            commentObj.put("date",simpleDateFormat.format(commentResultDTO.getDate()));
            commentObj.put("likeNum",commentResultDTO.getThumbsUpNumber());
            commentObj.put("author",getUser(commentResultDTO.getAuthorId()));

            JSONArray subComments=new JSONArray();
            for(String subCommentOid:commentResultDTO.getSubComments()){
                count++;
                Comment subComment=commentDao.findFirstById(subCommentOid);
                JSONObject subCommentObj=new JSONObject();
                subCommentObj.put("oid",subComment.getId());
                subCommentObj.put("content",subComment.getContent());
                subCommentObj.put("date",simpleDateFormat.format(subComment.getDate()));
                subCommentObj.put("likeNum",subComment.getThumbsUpNumber());
                subCommentObj.put("author",getUser(subComment.getAuthorId()));
                subCommentObj.put("replyTo",getUser(subComment.getReplyToUserId()));
                subComments.add(subCommentObj);
            }
            commentObj.put("subCommentList",subComments);
            commentList.add(commentObj);

        }

        JSONObject result=new JSONObject();
        result.put("size",count);
        result.put("commentList",commentList);

        return ResultUtils.success(result);
    }

    private JSONObject getUser(String userOid){

        if(userOid.equals("")){
            return null;
        }

        for(int i=0;i<userArray.size();i++){
            JSONObject userObj=userArray.getJSONObject(i);
            if(userObj.getString("oid").equals(userOid)){
                return userObj;
            }
        }

        User user=userDao.findFirstById(userOid);
        JSONObject jsonObject = userService.getInfoFromUserServer(user.getEmail());
        JSONObject author=new JSONObject();
        author.put("oid",userOid);
        author.put("userId",user.getEmail());
        author.put("name",jsonObject.getString("name"));
        author.put("img",jsonObject.getString("avatar"));

        userArray.add(author);

        return author;
    }

    @RequestMapping(value="/getCommentsByUser", method = RequestMethod.GET)
    public JsonResult getCommentsByUser(HttpServletRequest request){

        HttpSession session=request.getSession();

        if(Utils.checkLoginStatus(request)==null){
            return ResultUtils.error(-1,"no login");
        }else {
            String eid = session.getAttribute("eid").toString();
            List<Comment> commentList = commentDao.findAllByAuthorIdOrReplyToUserId(eid,eid);
            JSONArray jsonArray = new JSONArray();
            for(int i=0;i<commentList.size();i++){
                Comment comment = commentList.get(i);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("content",comment.getContent());
                jsonObject.put("author",getUser(comment.getAuthorId()));
                jsonObject.put("replier",getUser(comment.getReplyToUserId()));
                jsonObject.put("date",simpleDateFormat.format(comment.getDate()));
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                jsonObject.put("modifyTimeDay", sdf.format(comment.getDate()));//与message的其他时间名称统一
                jsonObject.put("status","comment");
                jsonObject.put("readStatus",comment.getReadStatus());
                String id = comment.getRelateItemId();
                JSONObject itemInfo = getItemInfoByTypeAndId(comment.getRelateItemType(),comment.getRelateItemId());
                jsonObject.put("itemInfo",itemInfo);

                jsonArray.add(jsonObject);

            }
            jsonArray.add(eid);

            return ResultUtils.success(jsonArray);
        }


    }

    public JSONObject getItemInfoByTypeAndId(ItemTypeEnum itemType,String id){
        JSONObject itemInfo = new JSONObject();
        PortalItem item = (PortalItem) ((GenericItemDao)genericService.daoFactory(itemType).get("itemDao")).findFirstById(id);

        String itemTypeStr = itemType.name();

        itemInfo.put("oid",item.getId());
        itemInfo.put("name",item.getName());
        itemInfo.put("type",itemTypeStr.substring(0,1).toLowerCase()+itemTypeStr.substring(1));

        return itemInfo;
    }

}
