package njgis.opengms.portal.entity.po;

import lombok.Data;
import njgis.opengms.portal.entity.doo.DailyViewCount;
import njgis.opengms.portal.entity.doo.base.PortalItem;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName ComputableModel
 * @Description 计算模型类
 * @Author Kai
 * @Date 2021/10
 * @Version 1.0.0
 */

@Document
@Data
public class ComputableModel extends PortalItem {

    List<String> relatedModelItems;

    String contentType; // Package; Service; Code; Link; Library; MD5
    String url;

    String md5;
    Boolean deploy; //是否已经部署
    Boolean verify = false; //是否

    List<String> resources;

    String mdl;
    String testDataPath;
    Object testDataCache;
    Object mdlJson;
    Object pageConfigJson;

    int invokeCount = 0;
    List<DailyViewCount> dailyInvokeCount = new ArrayList<>();
}
