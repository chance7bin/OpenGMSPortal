package njgis.opengms.portal.entity.doo.intergrate;

import lombok.Data;
import org.springframework.core.io.FileSystemResource;

@Data
public class TaskFile {
  FileSystemResource file;
  String userName;
}
