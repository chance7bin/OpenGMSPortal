package njgis.opengms.portal.entity.dto.draft;

import lombok.Data;

import java.util.Map;

/**
 * @Description
 * @Author bin
 * @Date 2021/08/31
 */
@Data
public class EditDraftDTO {
    String draftId; //草稿id
    Map<String,Object> content;
    String itemId; //如果是edit,填入条目信息
    String itemName;
    String itemType;
    String user;
    String editType; //标识是新建条目还是编辑create edit
}
