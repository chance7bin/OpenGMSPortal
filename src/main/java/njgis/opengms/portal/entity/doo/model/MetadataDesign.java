package njgis.opengms.portal.entity.doo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MetadataDesign {
    String purpose;
    List<String> principles;
    List<String> incorporatedModels;
    String framework;
    List<String> process;
}
