package njgis.opengms.portal.entity.po;

import lombok.Data;
import njgis.opengms.portal.entity.doo.DailyViewCount;
import njgis.opengms.portal.entity.doo.PortalId;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description 仪表盘展示的数据
 * @Author bin
 * @Date 2021/11/16
 */
@Data
public class DashBoard extends PortalId {

    String name = "dashboard";

    //每日点击数量
    List<DailyViewCount> dailyViewCount = new ArrayList<>();

}
