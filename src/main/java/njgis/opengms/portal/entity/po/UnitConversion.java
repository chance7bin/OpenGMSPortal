package njgis.opengms.portal.entity.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import njgis.opengms.portal.entity.doo.base.PortalId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * @Description
 * @Author bin
 * @Date 2022/06/09
 */
@Document
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UnitConversion extends PortalId {
    String Name;
    String BaseUnit;
    List<String> classifications;
    String Logarithmic;
    String LogarithmicScalingFactor;
    Object BaseDimensions;
    String XmlDoc;
    List<Object> Units;
}
