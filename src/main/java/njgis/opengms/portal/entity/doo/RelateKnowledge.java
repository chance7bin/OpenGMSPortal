package njgis.opengms.portal.entity.doo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 相关知识 community下面的四个部分
 *
 * @author 7bin
 * @date 2023/03/31
 */
@Data
public class RelateKnowledge {

    List<String> concepts = new ArrayList<>();
    List<String> spatialReferences = new ArrayList<>();
    List<String> templates = new ArrayList<>();
    List<String> units = new ArrayList<>();

}
