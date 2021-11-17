package njgis.opengms.portal.service;

import com.alibaba.fastjson.JSONObject;
import njgis.opengms.portal.dao.NoticeDao;
import njgis.opengms.portal.dao.UserDao;
import njgis.opengms.portal.dao.VersionDao;
import njgis.opengms.portal.entity.doo.JsonResult;
import njgis.opengms.portal.entity.dto.FindDTO;
import njgis.opengms.portal.entity.po.Notice;
import njgis.opengms.portal.entity.po.Version;
import njgis.opengms.portal.enums.OperationEnum;
import njgis.opengms.portal.utils.ResultUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    public Notice getMessageByTemplate(Notice notice, String currentUser){


        String message;


        // You accepted my changes to his model

        switch (notice.getAction().getType()){
            case Version:{
                Version version = versionDao.findFirstById(notice.getObjectId());
                String itemName = version.getItemName();
                String p1;
                if (notice.getDispatcher().equals(currentUser))
                    p1 = "you";
                else
                    p1 = userDao.findFirstByEmail(notice.getDispatcher()).getName();
                switch (notice.getAction()){
                    case Edit:{
                        String p2;
                        if (notice.getRecipient().equals(currentUser) && version.getItemCreator().equals(notice.getRecipient()))
                            p2 = "your ";
                        else
                            p2 = userDao.findFirstByEmail(version.getItemCreator()).getName() + "'s ";
                        message = p1 + " " + OperationEnum.Edit.getText() + "ed " + p2 + itemName;
                        break;
                    }
                    case Accept:
                    case Reject:{
                        String p2;
                        String p3;

                        if (notice.getRecipient().equals(currentUser) && version.getEditor().equals(notice.getRecipient()))
                            p2 = "your ";
                        else
                            p2 = userDao.findFirstByEmail(version.getEditor()).getName() + "'s ";

                        if (notice.getRecipient().equals(currentUser) && version.getItemCreator().equals(notice.getRecipient()))
                            p3 = "your ";
                        else
                            p3 = userDao.findFirstByEmail(version.getItemCreator()).getName() + "'s ";
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
     * @param objectId
     * @param recipient
     * @return njgis.opengms.portal.entity.po.Notice
     * @Author bin
     **/
    public Notice initNotice(String dispatcher, OperationEnum action,String objectId, String recipient){
        Notice notice = new Notice();
        notice.setDispatcher(dispatcher);
        notice.setAction(action);
        notice.setObjectId(objectId);
        // notice.setObjectAuthor(objectAuthor);
        // notice.setObjectName(objectName);
        notice.setCreateTime(new Date());
        notice.setRecipient(recipient);
        notice = noticeDao.insert(notice);
        //更新用户的通知数量
        userService.updateUserNoticeNum(recipient);
        //通知生成的时候就要发给客户端了
        sendToClient(notice);
        return notice;
    }


    /**
     * 发送通知到指定的用户(不包括门户)
     * @param dispatcher 消息发送者
     * @param action 动作 edit/accept/reject等
     * @param objectId 消息类型对应的id version的id/comment的id
     * @param recipientList 消息接收者列表
     * @return java.util.List<njgis.opengms.portal.entity.po.Notice>
     * @Author bin
     **/
    public List<Notice> sendNotice(String dispatcher, OperationEnum action,String objectId,List<String> recipientList){
        List<Notice> noticeList = new ArrayList<>();
        for (String recipient : recipientList) {
            noticeList.add(initNotice(dispatcher, action, objectId, recipient));
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
    public List<Notice> sendNoticeContainRoot(String dispatcher, OperationEnum action, String objectId, List<String> recipientList){

        List<Notice> noticeList = sendNotice(dispatcher, action, objectId, recipientList);
        //如果消息接收列表里不包含超级用户的话那也要把通知发给超级用户
        if (!recipientList.contains("opengms@njnu.edu.cn")){
            noticeList.add(initNotice(dispatcher, action, objectId, rootUser));
        }
        return noticeList;
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
    public Page<Notice> getUserNoticeList(FindDTO findDTO, String email){
        Pageable pageable = genericService.getPageable(findDTO);
        Page<Notice> noticeList = noticeDao.findAllByRecipient(email, pageable);
        for (Notice notice : noticeList) {
            getMessageByTemplate(notice,email);
        }
        return noticeList;

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
            noticeDao.save(notice);
        }
        return ResultUtils.success();
    }



}
