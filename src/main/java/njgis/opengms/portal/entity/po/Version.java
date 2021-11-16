package njgis.opengms.portal.entity.po;

import lombok.Data;
import njgis.opengms.portal.entity.doo.base.PortalId;
import njgis.opengms.portal.entity.doo.base.PortalItem;
import njgis.opengms.portal.enums.ItemTypeEnum;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
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
    String name; //由版本id和条目名组成
    String description; //需不需要加一个修改描述（需要审核的时候）
    PortalItem content; //修改条目数据
    // String preVersion; //上一个版本的id
    String itemCreator; //条目创建者邮箱
    String editor; //修改人邮箱
    String reviewer; //审核人邮箱
    Date createTime; //版本创建时间
    Date reviewTime; //审批时间
    Map<String,Object> changedField; //修改的字段
    int status = 0; //版本状态 0:待审核 -1:被拒绝 1:通过 2:原始版本（或忽略）
}
