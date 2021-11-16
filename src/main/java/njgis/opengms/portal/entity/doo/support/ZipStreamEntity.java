package njgis.opengms.portal.entity.doo.support;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.InputStream;


@AllArgsConstructor
@Data

public class ZipStreamEntity {
    public String name;
    public InputStream inputstream;
}
