package njgis.opengms.portal.service;

import com.alibaba.fastjson.JSONObject;
import njgis.opengms.portal.dao.*;
import njgis.opengms.portal.entity.doo.JsonResult;
import njgis.opengms.portal.entity.doo.base.PortalItem;
import njgis.opengms.portal.entity.dto.FindDTO;
import njgis.opengms.portal.entity.po.Comment;
import njgis.opengms.portal.entity.po.Notice;
import njgis.opengms.portal.entity.po.User;
import njgis.opengms.portal.entity.po.Version;
import njgis.opengms.portal.enums.ItemTypeEnum;
import njgis.opengms.portal.enums.OperationEnum;
import njgis.opengms.portal.utils.ResultUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description
 * @Author bin
 * @Date 2021/09/08
 */
@Service
public class NoticeService {

    @Autowired
    NoticeDao noticeDao;

    @Autowired
    UserDao userDao;

    @Autowired
    UserService userService;

    @Autowired
    GenericService genericService;

    @Autowired
    VersionDao versionDao;

    @Value("${rootUser}")
    String rootUser;

    @Autowired
    SimpMessagingTemplate messagingTemplate;

    @Autowired
    CommentDao commentDao;


    /**
     * 将通知发送给客户端
     * @param notice
     * @return void
     * @Author bin
     **/
    public void sendToClient(Notice notice){
        String message = getMessageByTemplate(notice,notice.getRecipient()).getMessage();
        int unreadNum = countUserUnreadNoticeNum(notice.getRecipient());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("message",message);
        jsonObject.put("unreadNum",unreadNum);
        messagingTemplate.convertAndSendToUser(notice.getRecipient(),"/subscribe/receiveNotice",jsonObject);
    }


    /**
     * 根据通知以及待收消息的用户生成定制的消息
     * @param notice 通知
     * @param currentUser 待接受消息的用户
     * @return njgis.opengms.portal.entity.po.Notice
     * @Author bin
     **/
    private Notice getMessageByTemplate(Notice notice, String currentUser){


        String message;


        // You accepted my changes to his model

        switch (notice.getAction().getType()){
            case Version:{
                // Version version = versionDao.findFirstById(notice.getObjectId());
                Version version = (Version) notice.getObjectContent();
                if (version == null){
                    message = "unknown";
                    break;
                }

                String itemName = version.getItemName();
                String p1;
                if (notice.getDispatcher().equals(currentUser))
                    p1 = "you";
                else {
                    // p1 = userDao.findFirstByEmail(notice.getDispatcher()).getName();
                    User u = userDao.findFirstByEmail(notice.getDispatcher());
                    p1 = u == null ? "unknown" : u.getName();
                }

                switch (notice.getAction()){
                    case Edit:{
                        String p2;
                        if (notice.getRecipient().equals(currentUser) && version.getItemCreator().equals(notice.getRecipient()))
                            p2 = "your ";
                        else{
                            // p2 = userDao.findFirstByEmail(version.getItemCreator()).getName() + "'s ";
                            User u = userDao.findFirstByEmail(version.getItemCreator());
                            p2 = (u == null ? "unknown" : u.getName()) + "'s ";
                        }

                        // message = p1 + " " + OperationEnum.Edit.getText() + "ed " + p2 + itemName + " (" + notice.getRemark() + ")";
                        message = p1 + " " + OperationEnum.Edit.getText() + "ed " + p2 + itemName;
                        break;
                    }
                    case Accept:
                    case Reject:{
                        String p2;
                        String p3;

                        if (notice.getRecipient().equals(currentUser) && version.getEditor().equals(notice.getRecipient()))
                            p2 = "your ";
                        else{
                            // p2 = userDao.findFirstByEmail(version.getEditor()).getName() + "'s ";
                            User u = userDao.findFirstByEmail(version.getEditor());
                            p2 = (u == null ? "unknown" : u.getName()) + "'s ";
                        }


                        if (notice.getRecipient().equals(currentUser) && version.getItemCreator().equals(notice.getRecipient()))
                            p3 = "your ";
                        else{
                            // p3 = userDao.findFirstByEmail(version.getItemCreator()).getName() + "'s ";
                            User u = userDao.findFirstByEmail(version.getItemCreator());
                            p3 = (u == null ? "unknown" : u.getName()) + "'s ";
                        }

                        // message = p1 + " " + notice.getAction().getText() + "ed " + p2 + "modification to " + p3  + itemName + " (" + notice.getRemark() + ")";
                        message = p1 + " " + notice.getAction().getText() + "ed " + p2 + "modification to " + p3  + itemName;
                        break;
                    }
                    default:{
                        message = "error action";
                        break;
                    }

                }
                break;
            }
            case Information:{
                String p1;
                if (notice.getDispatcher().equals(currentUser))
                    p1 = "you";
                else{
                    // p1 = userDao.findFirstByEmail(notice.getDispatcher()).getName();
                    User u = userDao.findFirstByEmail(notice.getDispatcher());
                    p1 = (u == null ? "unknown" : u.getName()) + "'s ";
                }

                message = p1 + " " + notice.getRemark();

                break;
            }
            case Comment:{
                // Comment comment = commentDao.findFirstById(notice.getObjectId());
                Comment comment = (Comment) notice.getObjectContent();

                if (comment == null){
                    message = "unknown";
                    break;
                }

                String p1;
                if (notice.getDispatcher().equals(currentUser))
                    p1 = "you";
                else{
                    // p1 = userDao.findFirstByEmail(notice.getDispatcher()).getName();
                    User u = userDao.findFirstByEmail(notice.getDispatcher());
                    p1 = (u == null ? "unknown" : u.getName()) + "'s ";
                }

                message = p1 + ": " + comment.getContent();
                break;
            }
            default:{
                message = "error action type";
                break;
            }
        }


        notice.setMessage(message);

        return notice;
    }



    /**
     * 初始化通知
     * @param dispatcher
     * @param action
     * @param objectContent
     * @param recipient
     * @return njgis.opengms.portal.entity.po.Notice
     * @Author bin
     **/
    private Notice initNotice(String dispatcher, OperationEnum action, ItemTypeEnum itemType,Object objectContent, String recipient, @Nullable List<String> additionList){
        Notice notice = new Notice();
        notice.setDispatcher(dispatcher);
        notice.setAction(action);
        notice.setObjectType(itemType);
        // notice.setObjectId(objectId);
        notice.setObjectContent(objectContent);
        // notice.setObjectAuthor(objectAuthor);
        // notice.setObjectName(objectName);
        notice.setCreateTime(new Date());
        notice.setRecipient(recipient);

        //设置附加的信息
        if (additionList != null && additionList.size() != 0){
            //列表的第0个是remark(备注)
            if (additionList.size() == 1){
                notice.setRemark(additionList.get(0));
            }
            if (additionList.size() == 2){
                notice.setNotifyChannel(additionList.get(1));
            }

        }

        notice = noticeDao.insert(notice);
        //更新用户的通知数量
        userService.updateUserNoticeNum(recipient);
        //通知生成的时候就要发给客户端了
        sendToClient(notice);
        return notice;
    }


    /**
     * 发送通知到指定的用户, 根据recipientList统一发送
     * @param dispatcher 消息发送者
     * @param action 动作 edit/accept/reject等
     * @param itemType 作用的条目类型
     * @param objectContent 消息类型  version/comment
     * @param recipientList 消息接收者列表
     * @return java.util.List<njgis.opengms.portal.entity.po.Notice>
     * @Author bin
     **/
    private List<Notice> sendNotice(String dispatcher, OperationEnum action,ItemTypeEnum itemType,Object objectContent,List<String> recipientList, @Nullable List<String> additionList){
        List<Notice> noticeList = new ArrayList<>();
        for (String recipient : recipientList) {
            noticeList.add(initNotice(dispatcher, action, itemType, objectContent, recipient,additionList));
        }
        return noticeList;

    }

    /**
     * 发送通知到指定的用户(包括门户)
     * @param dispatcher 消息发送者
     * @param action 动作 edit/accept/reject等
     * @param objectId 消息类型对应的id version的id/comment的id
     * @param recipientList 消息接收者列表
     * @return java.util.List<njgis.opengms.portal.entity.po.Notice>
     * @Author bin
     **/
    // public List<Notice> sendNoticeContainRoot(String dispatcher, OperationEnum action, ItemTypeEnum itemType, String objectId, List<String> recipientList){
    //
    //     List<Notice> noticeList = sendNotice(dispatcher, action, itemType, objectId, recipientList, null);
    //     //如果消息接收列表里不包含超级用户的话那也要把通知发给超级用户
    //     if (!recipientList.contains("opengms@njnu.edu.cn")){
    //         noticeList.add(initNotice(dispatcher, action, itemType, objectId, rootUser,null));
    //     }
    //     return noticeList;
    // }

    /**
     * @Description 发送通知给所有管理者，包括条目管理者及门户管理者
     * @param dispatcher 消息发送者
     * @param authorEmail 条目作者email
     * @param itemAdmins 条目管理者email列表
     * @param itemType 消息对应的类型
     * @param objectContent 消息类型 version/comment
     * @param action 动作 edit/accept/reject等
     * @Return void
     * @Author kx
     * @Date 22/3/8
     **/
    public void sendNoticeContainsAllAdmin(String dispatcher, String authorEmail, List<String> itemAdmins, ItemTypeEnum itemType, Object objectContent, OperationEnum action){
        List<String> recipientList = new ArrayList<>();
        recipientList.add(authorEmail);
        recipientList = addItemAdmins(recipientList,itemAdmins);
        recipientList = addPortalAdmins(recipientList);
        recipientList = addPortalRoot(recipientList);
        sendNoticeContains(dispatcher, action, itemType, objectContent, recipientList);
    }

    /**
     * 发送通知, 在调用这个方法前先构造一下通知要发送的用户列表
     * @param dispatcher 消息发送者
     * @param action 动作 edit/accept/reject等
     * @param itemType 作用的条目类型
     * @param objectContent 消息类型 version/comment
     * @param recipientList 消息接收者列表
     * @param additionProperties 附加的notice属性, 目前附加的属性只有一个remark(备注), 之后有多的按顺序加在后面
     * @return java.util.List<njgis.opengms.portal.entity.po.Notice>
     * @Author bin
     **/
    public List<Notice> sendNoticeContains(String dispatcher, OperationEnum action, ItemTypeEnum itemType, Object objectContent, List<String> recipientList, @Nullable String... additionProperties){

        recipientList = recipientList.stream().distinct().collect(Collectors.toList());

        List<String> additionList = additionProperties != null ? new ArrayList<>(Arrays.asList(additionProperties)) : null;


        //添加门户管理员
        // List<User> adminUser = userService.getAdminUser();
        // List<String> adminEmail = new ArrayList<>();
        // for (User user : adminUser) {
        //     adminEmail.add(user.getEmail());
        // }
        // recipientList = addPortalAdmins(recipientList, adminEmail);

        //添加门户root用户
        // List<User> rootUser = userService.getRootUser();
        // List<String> rootEmail = new ArrayList<>();
        // for (User user : rootUser) {
        //     rootEmail.add(user.getEmail());
        // }
        // recipientList = addPortalRoot(recipientList, rootEmail);


        List<Notice> noticeList = sendNotice(dispatcher, action, itemType,objectContent, recipientList, additionList);

        return noticeList;
    }



    /**
     * 往通知的接收者添加条目管理员
     * @param recipientList 通知者列表
     * @param itemAdmins 条目管理员
     * @return java.util.List<java.lang.String>
     * @Author bin
     **/
    public List<String> addItemAdmins(List<String> recipientList, List<String> itemAdmins){
        if (itemAdmins == null)
            return recipientList;


        for (String itemAdmin : itemAdmins) {
            if (!recipientList.contains(itemAdmin))
                recipientList.add(itemAdmin);
        }

        return recipientList;
    }


    /**
     * 往通知的接收者添加门户管理员
     * @param recipientList
     * @return java.util.List<java.lang.String>
     * @Author bin
     **/
    public List<String> addPortalAdmins(List<String> recipientList){
        List<User> adminUser = userService.getAdminUser();
        List<String> portalAdmins = new ArrayList<>();
        for (User user : adminUser) {
            portalAdmins.add(user.getEmail());
        }

        for (String portalAdmin : portalAdmins) {
            if (!recipientList.contains(portalAdmin))
                recipientList.add(portalAdmin);
        }
        return recipientList;
    }

    /**
     * 往通知的接收者添加门户root用户
     * @param recipientList
     * @return java.util.List<java.lang.String>
     * @Author bin
     **/
    public List<String> addPortalRoot(List<String> recipientList){
        List<User> rootUser = userService.getRootUser();
        List<String> portalRoot = new ArrayList<>();
        for (User user : rootUser) {
            portalRoot.add(user.getEmail());
        }

        for (String root : portalRoot) {
            if (!recipientList.contains(root))
                recipientList.add(root);
        }
        return recipientList;
    }




    /**
     * 统计用户的通知数量
     * @param email
     * @return int
     * @Author bin
     **/
    public int countUserNoticeNum(String email){
        return noticeDao.countByRecipient(email);
    }



    /**
     * 统计用户未读的通知数量
     * @param email
     * @return int
     * @Author bin
     **/
    public int countUserUnreadNoticeNum(String email){
        return noticeDao.countByRecipientAndHasRead(email,false);
    }



    /**
     * 得到用户的通知列表
     * @param findDTO
     * @param email
     * @return org.springframework.data.domain.Page<njgis.opengms.portal.entity.po.Notice>
     * @Author bin
     **/
    public JSONObject getUserNoticeList(FindDTO findDTO, String email){
        Pageable pageable = genericService.getPageable(findDTO);
        Page<Notice> noticeList = noticeDao.findAllByRecipient(email, pageable);
        JSONObject result = new JSONObject();
        List<JSONObject> content = new ArrayList<>();
        for (Notice notice : noticeList) {

            getMessageByTemplate(notice,email);

            JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(notice));

            //email -> name
            String dispatcher = notice.getDispatcher();
            User user = userDao.findFirstByEmail(dispatcher);
            if (user != null){
                jsonObject.put("dispatcher",user.getName());
                jsonObject.put("dispatcherAccessId",user.getAccessId());
            }


            //根据notice.getObjectType()找到通知关联的条目
            ItemTypeEnum type = notice.getObjectType();
            if (type == ItemTypeEnum.Version){
                Version version = (Version)notice.getObjectContent();
                jsonObject.put("relateItemId",version.getItemId());
                // ItemTypeEnum itemType = version.getType();
                // GenericItemDao itemDao = (GenericItemDao)genericService.daoFactory(itemType).get("itemDao");
                // PortalItem item = (PortalItem)itemDao.findFirstById(version.getItemId());
                jsonObject.put("relateItemName",version.getItemName());
                jsonObject.put("relateItemType",version.getType().getText());
            } else if (type == ItemTypeEnum.Comment){
                Comment comment = (Comment)notice.getObjectContent();
                jsonObject.put("relateItemId",comment.getRelateItemId());
                ItemTypeEnum itemType = comment.getRelateItemType();
                GenericItemDao itemDao = (GenericItemDao)genericService.daoFactory(itemType).get("itemDao");
                PortalItem item = (PortalItem)itemDao.findFirstById(comment.getRelateItemId());
                if (item!=null){
                    jsonObject.put("relateItemName",item.getName());
                }
                jsonObject.put("relateItemType",comment.getRelateItemType().getText());
            }



            content.add(jsonObject);

        }

        result.put("content",content);
        //分页总数
        result.put("totalElements",noticeList.getTotalElements());

        return result;

    }


    /**
     * 将通知全部设置为已读
     * @param email
     * @return njgis.opengms.portal.entity.doo.JsonResult
     * @Author bin
     **/
    public JsonResult allNotice2read(String email){
        List<Notice> notices = noticeDao.findAllByRecipientAndHasRead(email, false);
        for (Notice notice : notices) {
            notice.setHasRead(true);
        }
        noticeDao.saveAll(notices);
        updateUserUnreadNum(email);
        return ResultUtils.success();
    }

    private void updateUserUnreadNum(String email) {
        User user = userService.getByEmail(email);
        if (user != null){
            int unreadNoticeNum = countUserUnreadNoticeNum(email);
            user.setUnreadNoticeNum(unreadNoticeNum);
            userDao.save(user);
        }
    }


    public JsonResult notice2readById(String noticeId) {

        Notice notice = noticeDao.findFirstById(noticeId);
        notice.setHasRead(true);
        noticeDao.save(notice);
        String recipient = notice.getRecipient();
        updateUserUnreadNum(recipient);
        return ResultUtils.success();
    }
}
