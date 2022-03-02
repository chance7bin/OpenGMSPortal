package njgis.opengms.portal.entity.doo;

import lombok.Data;
import njgis.opengms.portal.entity.doo.support.DailyViewCount;

import java.util.Date;
import java.util.List;

/**
 * @Description
 * @Author bin
 * @Date 2021/11/18
 */
@Data
public class UserDailyViewCount extends DailyViewCount {
    List<String> ipList;

    public UserDailyViewCount(Date date, int count, List<String> ipList) {
        super(date, count);
        this.ipList = ipList;
    }
}
