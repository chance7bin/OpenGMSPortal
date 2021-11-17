package njgis.opengms.portal.entity.dto.model;

import lombok.Data;
import njgis.opengms.portal.entity.doo.data.SimpleFileInfo;
import njgis.opengms.portal.entity.po.ConceptualModel;

import java.util.List;

/**
 * @Description 用于前端展示的概念模型信息
 * @Author kx
 * @Date 21/10/14
 * @Version 1.0.0
 */
@Data
public class ConceptualModelResultDTO extends ConceptualModel {

    List<RelatedModelInfoDTO> relatedModelItemInfoList; //相关模型id及名称
    List<SimpleFileInfo> resourceJson; //用户上传的图片信息

}
