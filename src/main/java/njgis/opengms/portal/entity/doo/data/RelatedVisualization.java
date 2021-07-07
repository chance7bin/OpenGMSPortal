package njgis.opengms.portal.entity.doo.data;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @Author mingyuan
 * @Date 2020.07.03 20:13
 * TODO
 */
@Document
@Data
public class RelatedVisualization {
    String proId;
    String proName;
    String proDescription;
    String token;
    //    ArrayList<String> parameter;
    String xml;
}
