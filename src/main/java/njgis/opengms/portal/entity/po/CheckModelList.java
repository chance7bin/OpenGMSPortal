package njgis.opengms.portal.entity.po;

import lombok.Data;
import njgis.opengms.portal.entity.doo.base.PortalId;
import njgis.opengms.portal.entity.doo.task.CheckedHistory;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Description 管理系统用于保存已检查的模型记录
 * @Author bin
 * @Date 2021/11/11
 */
@Data
@Document
public class CheckModelList extends PortalId {

    String draftName;
    List<CheckedHistory> historyList = new ArrayList<>();
    Date createTime = new Date();
    String operator; //操作人员email

}
