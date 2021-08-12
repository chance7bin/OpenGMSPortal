package njgis.opengms.portal.entity.po;

import lombok.Data;
import njgis.opengms.portal.entity.doo.data.InvokeService;
import njgis.opengms.portal.entity.doo.data.TestData;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * @Description
 * @Author bin
 * @Date 2021/08/04
 */
@Document
@Data
public class DataMethod extends DataItem{

    List<String> resources;

    String contentType;

    Boolean isAuthor;
    String applicationType;//区分process与visual

    // List<InvokeService> invokeServices; 在DataItem已经设置了
    boolean invokable;//是否绑定了invokeService

    String method; // Conversion Processing Visualization

    List<TestData> testData;//存储testData的id
    String testDataPath;
    String packagePathContainer;
    String packagePath;//存储部署包的路径

    Boolean batch = false;
    List<String> bindDataTemplates;//存储对应的template的id
}
