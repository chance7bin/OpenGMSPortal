package njgis.opengms.portal.entity.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description 查询条件对象
 * @Author kx
 * @Date 2021/7/7
 * @Version 1.0.0
 */
@Data
public class SpecificFindDTO extends FindDTO {
    @ApiModelProperty(value = "目录分类", example = "specific uuid")
    private String categoryName; //目录分类
    @ApiModelProperty(value = "按属性查询", example = "name")
    private String curQueryField; //属性分类

}
