package njgis.opengms.portal.entity.dto.task;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description
 * @Author bin
 * @Date 2021/11/09
 */
@Data
public class TaskFindDTO {

    @ApiModelProperty(value = "状态[calculating, inited, successful, failed]", example = "all")
    String status;
    @ApiModelProperty(value = "当前页数", example = "1")
    int page;
    @ApiModelProperty(value = "每页数量", example = "10")
    int pageSize;
    @ApiModelProperty(value = "排序属性", example = "runTime")
    String sortType;
    @ApiModelProperty(value = "升序:1, 降序:-1", example = "-1")
    int asc;
}
