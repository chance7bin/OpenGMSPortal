package njgis.opengms.portal.entity.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import njgis.opengms.portal.entity.doo.PortalId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.util.Date;
import java.util.Map;

/**
 * @Description
 * @Author bin
 * @Date 2021/08/31
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document
public class EditDraft extends PortalId {

    Map<String,Object> content;
    String user;
    String itemId;//如果是edit,填入条目信息
    String itemName;//如果是edit,填入编辑的条目名
    String itemType;//是哪一类条目
    String editType;//标识是新建条目还是编辑
//    Boolean self;//标识是自己的还是他人的
//    Boolean template = false;//用户是否设置为填写模板
    Date createTime;
    Date lastModifyTime;
}
