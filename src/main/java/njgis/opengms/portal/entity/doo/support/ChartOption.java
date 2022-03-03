package njgis.opengms.portal.entity.doo.support;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChartOption {

    String[] types;
    int[][] data;
    String[] valXis;
    String title;
    String subTitle;
    String titlePosition="center";//center left

}
