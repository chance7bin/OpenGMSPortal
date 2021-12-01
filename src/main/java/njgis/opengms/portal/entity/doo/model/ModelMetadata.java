package njgis.opengms.portal.entity.doo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModelMetadata {

    MetadataOverview overview = new MetadataOverview();
    MetadataUsage usage = new MetadataUsage();
    MetadataDesign design = new MetadataDesign();
}
