package njgis.opengms.portal.entity.dto.community;

import lombok.Data;
import njgis.opengms.portal.entity.doo.RelateKnowledge;

/**
 * community的四类
 *
 * @author 7bin
 * @date 2023/04/01
 */
@Data
public class KnowledgeDTO extends RelateKnowledge {
    String id; // 条目id
}
