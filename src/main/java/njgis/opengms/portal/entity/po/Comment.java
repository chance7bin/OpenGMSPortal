package njgis.opengms.portal.entity.po;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import njgis.opengms.portal.entity.doo.base.PortalId;
import njgis.opengms.portal.enums.ItemTypeEnum;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Document
@Data

public class Comment extends PortalId {

    String parentId;
    String content;
    String commentEmail; //评论人的email
    String replyToUserEmail; //回复评论的那个人的email

    String relateItemId;
    ItemTypeEnum relateItemType;

    int thumbsUpNumber = 0;
    int readStatus;//标记是否已读

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    Date createTime;

    Boolean isPublic = false; // 该评论是否对公众开放，如不开放，则只有评论者，被评论人，条目创建者、管理者，以及门户管理员才能看到。

    List<String> subComments=new ArrayList<>();

}
