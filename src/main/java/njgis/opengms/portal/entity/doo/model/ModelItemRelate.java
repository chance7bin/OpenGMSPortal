package njgis.opengms.portal.entity.doo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import njgis.opengms.portal.entity.doo.RelateKnowledge;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @ClassName ModelItemRelate
 * @Description 模型条目关联的网站上其他信息
 * @Author Kai
 * @Date 2019/2/21
 * @Version 1.0.0
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModelItemRelate extends RelateKnowledge {

    List<ModelRelation> modelRelationList = new ArrayList<>();

    List<String> conceptualModels = new ArrayList<>();
    List<String> logicalModels = new ArrayList<>();
    List<String> computableModels = new ArrayList<>();
    List<String> dataItems = new ArrayList<>();
    List<String> dataHubs = new ArrayList<>();
    List<String> dataMethods = new ArrayList<>();

//    List<String> references = new ArrayList<>();//存放相关Article的Id

    List<Map<String,String>> localFiles = new ArrayList<Map<String,String>>();
    List<Map<String,String>> dataSpaceFiles = new ArrayList<Map<String,String>>();
    List<Map<String,String>> exLinks = new ArrayList<Map<String,String>>();

}
