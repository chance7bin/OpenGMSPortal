package njgis.opengms.portal.entity.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import njgis.opengms.portal.entity.doo.DataMeta;
import njgis.opengms.portal.entity.doo.PortalItem;
import njgis.opengms.portal.entity.doo.data.InvokeService;
import njgis.opengms.portal.entity.doo.data.RelatedProcessing;
import njgis.opengms.portal.entity.doo.data.RelatedVisualization;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * @ClassName DataItem
 * @Description 数据条目类
 * @Author sun_liber
 * @Date 2019/2/13
 * @Version 1.0.0
 */
@Document
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DataItem extends PortalItem {

    String contentType;
    String userName;
    String reference;
    String tabType;//标识四个tabs，包括hubs、repository、network与application

    List<String> classifications;
    List<String> displays;
    String image;
    List<String> relatedModels;
    List<DataMeta> dataList;
//    List<FileMetaUser> userDataList;//待删

    String token;
    String dataType;//标识Hub、Url、File、DistributedNode,目前新增了tabType，此字段可删除

    //Share in place
    String distributedNodeDataId;
    String type;
    Boolean authority;
    String workSpace;
    List<String> tags;
    String dataPath;
    String date;
    String dataUrl;
    String ip;

    List<InvokeService> invokeServices;

    List<RelatedProcessing> relatedProcessings;
    List<RelatedVisualization> relatedVisualizations;
}
