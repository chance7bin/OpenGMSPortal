package njgis.opengms.portal.entity.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import njgis.opengms.portal.entity.doo.DataMeta;
import njgis.opengms.portal.entity.doo.base.PortalItem;
import njgis.opengms.portal.entity.doo.data.InvokeService;
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

    String url;//reference 若dataType为Url，则需填写该字段

    List<String> imageList;//displays 以图片的形式展示数据内容
    List<DataMeta> dataList; //若dataType为File, 则添加上传至数据容器的文件元数据
    String dataType; //Url File
    List<String> relatedModels;
    String tabType;//标识四个tabs，包括hubs、repository、network与application


    //数据条目原author存的是用户编号，需要改成email

    List<InvokeService> invokeServices; //绑定本用户数据容器节点数据 （做数据就地共享） 这个只在dataApplication中有

}
