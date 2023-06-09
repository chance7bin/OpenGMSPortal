package njgis.opengms.portal.entity.doo.intergrate;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class Action {
    private String id;

    private String name;//对应Action的name

    private List<Map<String,Object>> outputData;

    private List<Map<String,Object>> inputData;

    private List<Map<String,Object>> params;

    private Integer status = 0; // 0代表未开始，-1代表运行失败，1代表运行成功, 2代表运行超时(不存在运行中状态，省略)

    private String taskId;

    private String type;//modelService,dataService,为了前端方便分类

    private String taskIp;

    private int port;
}
