package njgis.opengms.portal.entity.dto.model.modelItem;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * @ClassName ModelItemAddDTO
 * @Description 用于列表展示的查询结果
 * @Author Kai
 * @Date 2019/2/21
 * @Version 1.0.0
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModelItemResultDTO  {

    String id;
    String name;
    String image = "";
    String overview;
    String author;
    String status;

    boolean lock;

    List<String> keywords;

    Date createTime;

    int viewCount=0;
}
