package njgis.opengms.portal.entity.dto.data.dataMethod;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import njgis.opengms.portal.entity.doo.data.TestData;
import njgis.opengms.portal.entity.dto.AddDTO;

import java.util.List;

/**
 * @Description
 * @Author bin
 * @Date 2021/08/26
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataMethodDTO extends AddDTO {

    String operation;
    String type;

    String url;//Third-party Web-Service Link
    String method;  // processing visualization

    List<TestData> testData;//存储testData的id
    String testDataPath;
    String packagePathContainer;
    List<String> bindDataTemplates;//存储对应的template的id
}
