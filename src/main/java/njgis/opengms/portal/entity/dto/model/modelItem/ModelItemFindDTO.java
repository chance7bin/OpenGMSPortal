package njgis.opengms.portal.entity.dto.model.modelItem;

import lombok.Data;
import njgis.opengms.portal.entity.dto.SpecificFindDTO;

import java.util.List;

/**
 * @Description 模型查询条件对象
 * @Author kx
 * @Date 2021/7/7
 * @Version 1.0.0
 */
@Data
public class ModelItemFindDTO extends SpecificFindDTO {

    List<String> classifications;// 在哪个分类下查找
    String authorEmail;//查找属于某位用户的模型条目

    private String sortField = "viewCount";// 排序字段
    private String queryField = "Name";// 查询哪个字段

}
