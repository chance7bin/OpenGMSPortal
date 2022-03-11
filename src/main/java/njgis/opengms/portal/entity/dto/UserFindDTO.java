package njgis.opengms.portal.entity.dto;

import lombok.Data;

/**
 * @Description
 * @Author bin
 * @Date 2022/03/04
 */
@Data
public class UserFindDTO extends SpecificFindDTO{

    String authorEmail;//查找属于某位用户的模型条目
    
}
