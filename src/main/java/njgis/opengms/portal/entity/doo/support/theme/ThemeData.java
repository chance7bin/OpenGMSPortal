package njgis.opengms.portal.entity.doo.support.theme;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ThemeData {
    int id;
    String label;
    List<ThemeData> children;
    List<String> tableData;
}
