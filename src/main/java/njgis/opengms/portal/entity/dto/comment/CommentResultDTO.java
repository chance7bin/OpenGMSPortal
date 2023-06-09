package njgis.opengms.portal.entity.dto.comment;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Document
@Data

public class CommentResultDTO {

    String id;
    String parentId;
    String content;
    // String authorId;
    String commentEmail;
    // String replyToUserId;
    String replyToUserEmail;

    int thumbsUpNumber=0;
    Date createTime;

    List<String> subComments=new ArrayList<>();

}
