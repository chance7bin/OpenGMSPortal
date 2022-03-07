package njgis.opengms.portal.entity.dto.user;

import lombok.Data;
import njgis.opengms.portal.entity.doo.user.Project;

import java.util.List;

/**
 * @Description
 * @Author bin
 * @Date 2022/03/07
 */
@Data
public class ProjectDTO {

    List<Project> projects;

}
