package njgis.opengms.portal.entity.po;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import njgis.opengms.portal.entity.doo.base.PortalId;
import njgis.opengms.portal.entity.doo.base.PortalItem;
import njgis.opengms.portal.enums.ItemTypeEnum;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Description
 * @Author bin
 * @Date 2021/09/06
 */
@Data
@Document
public class Version extends PortalId {
    // String id; //版本id
    String itemId; //修改的条目id
    ItemTypeEnum type; //条目类型
    String itemName; //条目名称
    String versionName; //由版本id和条目名组成
    String description; //需不需要加一个修改描述（需要审核的时候）
    PortalItem content; //提交审核的修改的条目数据
    PortalItem original; //该版本原始条目数据
    // String preVersion; //上一个版本的id
    String itemCreator; //条目创建者邮箱
    List<String> authReviewers; //有权审核该版本的用户邮箱
    String editor; //修改人邮箱
    String reviewer; //审核人邮箱

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    Date submitTime; //版本提交时间

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    Date reviewTime; //审批时间
    Map<String,Object> changedField; //修改的字段
    int status = 0; //版本状态 0:待审核 -1:被拒绝 1:通过 2:原始版本（或忽略）
}
