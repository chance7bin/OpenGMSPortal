package njgis.opengms.portal.entity.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description 查询条件对象
 * @Author kx
 * @Date 2021/7/7
 * @Version 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpecificFindDTO extends FindDTO {
    @ApiModelProperty(value = "目录分类(是个id!是个id!是个id!)", example = "specific uuid")
    private String categoryName; //目录分类
    @ApiModelProperty(value = "当前查询的属性", example = "name")
    private String curQueryField = "name"; //属性分类

}
