package njgis.opengms.portal.entity.doo.user;

import lombok.Data;

/**
 * @Description 用户相关资源计数类
 * @Author kx
 * @Date 2021/7/1
 * @Version 1.0.0
 */
@Data
public class UserResourceCount {

    int modelItem;
    int dataItem;
    int dataHub;
    int dataMethod;
    int conceptualModel;
    int logicalModel;
    int computableModel;

    int concept;
    int spatial;
    int template;
    int unit;
    int theme;

    // int article;
    // int project;
    // int conference;
}
