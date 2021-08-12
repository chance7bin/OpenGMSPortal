package njgis.opengms.portal.entity.doo.data;

import lombok.Data;
import njgis.opengms.portal.entity.po.Categorys;

import java.util.List;

/**
 * @Author mingyuan
 * @Date 2020.08.19 22:29
 */
@Data
public class DataCategorys extends Categorys {
    List<String> dataItemNew;//原先的数据//待删
    List<String> dataHubs;
//    HashSet<String> repository;
}
