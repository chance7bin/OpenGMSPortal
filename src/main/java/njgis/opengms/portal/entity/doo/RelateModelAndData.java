package njgis.opengms.portal.entity.doo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 相关模型与数据
 *
 * @author 7bin
 * @date 2023/03/31
 */
@Data
public class RelateModelAndData {

    List<String> modelItems = new ArrayList<>();
    List<String> conceptualModels = new ArrayList<>();
    List<String> logicalModels = new ArrayList<>();
    List<String> computableModels = new ArrayList<>();


    List<String> dataItems = new ArrayList<>();
    List<String> dataHubs = new ArrayList<>();
    List<String> dataMethods = new ArrayList<>();

}
