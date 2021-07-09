package njgis.opengms.portal.test.WebSocketTest;

import lombok.Data;

@Data
public class Chat {
    private String to;
    private String from;
    private String content;
}
