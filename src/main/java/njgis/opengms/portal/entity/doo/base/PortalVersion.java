package njgis.opengms.portal.entity.doo.base;

import lombok.Data;
import njgis.opengms.portal.entity.doo.AuthorInfo;
import njgis.opengms.portal.entity.doo.Localization;
import njgis.opengms.portal.entity.doo.modelItem.ModelItemRelate;
import njgis.opengms.portal.entity.doo.modelItem.ModelMetadata;

import java.util.Date;
import java.util.List;

/**
 * @Description 门户版本控制基类
 * @Author kx
 * @Date 2021/7/12
 * @Version 1.0.0
 */

@Data
public class PortalVersion extends PortalId {

    private static final long serialVersionUID = 1L;

    //Basic Info

    String name; //条目名称
    List<String> alias; //别名
    String image;
    String overview; //简单介绍 原为description
    List<Localization> localizationList; //多语言详细描述

    List<AuthorInfo> authorship;//

    ModelItemRelate relate = new ModelItemRelate();
    ModelMetadata metadata = new ModelMetadata();

    Long verNumber;//版本号
    int verStatus;//版本状态 0:待审核 -1:被拒绝 2:通过 1:原始版本

    Date modifyTime;
    Date reviewTime;
}
