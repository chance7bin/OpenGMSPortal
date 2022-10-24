package njgis.opengms.portal.entity.dto.model;

import lombok.Data;
import njgis.opengms.portal.entity.doo.data.SimpleFileInfo;
import njgis.opengms.portal.entity.po.ComputableModel;

import java.util.List;

/**
 * @Description 用于前端展示的计算模型信息
 * @Author kx
 * @Date 21/11/11
 * @Version 1.0.0
 */
@Data
public class ComputableModelResultDTO extends ComputableModel {

    List<RelatedModelInfoDTO> relatedModelItemInfoList; //相关模型id及名称
    List<SimpleFileInfo> resourceJson; //用户上传的图片信息
    List<String> relateModelItemName;
}
