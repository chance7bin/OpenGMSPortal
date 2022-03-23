package njgis.opengms.portal.entity.dto.user;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
public class UploadUserFileDTO implements Serializable {
//    Boolean isFolder;
//    Boolean isUserUpload;
//
//    String name;
//
//    String source_store_id;//dataContainer返回的id
//
//    String father;
    List<Map> files;

    List<String> paths;
    List<String> oldPaths;
}

