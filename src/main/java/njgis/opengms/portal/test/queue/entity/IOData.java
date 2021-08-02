package njgis.opengms.portal.test.queue.entity;

import lombok.Data;
import njgis.opengms.portal.test.queue.entity.DataItem;

import java.util.List;

/**
 * @Description 输入输出数据格式
 * @Author bin
 * @Date 2021/07/14
 */
@Data
public class IOData {
    private List<DataItem> inputdata;
}
