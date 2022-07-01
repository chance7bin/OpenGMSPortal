package njgis.opengms.portal.entity.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import njgis.opengms.portal.entity.doo.base.PortalId;
import njgis.opengms.portal.enums.ItemTypeEnum;
import njgis.opengms.portal.enums.OperationEnum;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;


/**
 * @Description
 * @Author bin
 * @Date 2021/09/08
 */
@Document
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notice extends PortalId {

    String dispatcher; //发送者email
    OperationEnum action; //对资源的操作 ["comment","edit","rejected","accepted"]
    ItemTypeEnum objectType; //作用的对象所属类型 ["version","comment",...]
    // String objectId; //被作用的对象 如：versionId , commentId等 (废弃，考虑到如果之后这个删除的话那就找不到这个对象了)
    Object objectContent; //被作用的对象内容 如：version , comment等
    // String objectAuthor; //资源作者，用于消息内容生成，区别超级用户和普通用户
    // String objectName; //资源名
    String recipient; //接收者email

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    Date createTime; //发送时间
    // String title; //消息标题
    String message = "The message needs to be generated through a template"; //消息内容，根据action由消息模版生成
    String remark = ""; // 一些消息的附加信息 通知类型为Information时构造msg时使用
    String notifyChannel = "inside"; //为某个通知类型设置一个或多个推送渠道 inside/email 默认为站内
    boolean hasRead = false; //是否已读

}
