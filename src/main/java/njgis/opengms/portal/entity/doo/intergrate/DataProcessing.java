package njgis.opengms.portal.entity.doo.intergrate;

import lombok.Data;

@Data
public class DataProcessing extends Action {

    private String token;//对应data service的计算节点

    private String service;//对应model service的md5或data service的标识

}
