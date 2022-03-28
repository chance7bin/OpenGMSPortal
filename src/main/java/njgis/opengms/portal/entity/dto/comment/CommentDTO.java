package njgis.opengms.portal.entity.dto.comment;

import lombok.Data;
import njgis.opengms.portal.enums.ItemTypeEnum;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data

public class CommentDTO {

    String parentId;
    String content;

    String replyToUserEmail;

    String relateItemId;
    ItemTypeEnum relateItemTypeName;

}
