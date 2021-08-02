package njgis.opengms.portal.test.queue.entity;

import lombok.Data;
import njgis.opengms.portal.entity.doo.PortalId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description description
 * @Author bin
 * @Date 2021/07/15
 */
@Document
@Data
public class ServerInfo extends PortalId {
    private String ip;
    private String port;

    private Map<String,String> modelInfo = new HashMap<>(); //k-模型md5,v-模型在容器里的id,用于运行模型

    private int currentRun;
    private int maxRun;
}
