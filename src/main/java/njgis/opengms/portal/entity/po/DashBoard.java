package njgis.opengms.portal.entity.po;

import lombok.Data;
import njgis.opengms.portal.entity.doo.UserDailyViewCount;
import njgis.opengms.portal.entity.doo.base.PortalId;
import njgis.opengms.portal.entity.doo.support.DailyViewCount;

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

    //每日点击页面的数量
    List<DailyViewCount> dailyViewCount = new ArrayList<>();

    //每日登录的用户数量
    List<UserDailyViewCount> userDailyViewCount = new ArrayList<>();

}
