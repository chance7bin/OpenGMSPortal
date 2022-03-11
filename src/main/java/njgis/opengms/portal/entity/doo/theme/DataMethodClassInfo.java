package njgis.opengms.portal.entity.doo.theme;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: Xiaoyu He
 * @Date: 2021/05/18/21:48
 * @Description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataMethodClassInfo {
    @Id
    String oid;
    String dmcname;
    //多级菜单子节点
    List<DataMethodClassInfo> children;
    List<String> dataMethodsoid;
}
