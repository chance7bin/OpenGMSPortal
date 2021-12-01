package njgis.opengms.portal.entity.po;

import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Description 用于存放检查模型的信息
 * @Author bin
 * @Date 2021/11/16
 */
@Data
public class CheckedModel {
    // String id;  //模型id
    // String name;  //模型名字
    Date lastCheckTime;  //最后一次检查时间
    boolean hasChecked = false;  //是否检查过
    boolean isOnline = true; //模型是否在线
    boolean hasTest = true; //是否有测试数据
    String msg; //调用模型返回的信息
    int status;  //模型状态  Started: 1, Finished: 2, Inited: 0, Error: -1
    List<String> taskIdList = new ArrayList<>(); //存放调用的任务的taskId的列表 (task表的taskId)
}
