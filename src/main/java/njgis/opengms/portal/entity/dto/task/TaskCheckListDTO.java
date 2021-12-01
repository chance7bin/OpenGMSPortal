package njgis.opengms.portal.entity.dto.task;

import lombok.Data;

import java.util.List;

/**
 * @Description
 * @Author bin
 * @Date 2021/11/11
 */
@Data
public class TaskCheckListDTO {

    List<String> modelList;  //模型id列表
    String draftName;

}
