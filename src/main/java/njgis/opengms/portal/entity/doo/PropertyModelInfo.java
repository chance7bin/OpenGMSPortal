package njgis.opengms.portal.entity.doo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Description
 * @Author bin
 * @Date 2021/09/07
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PropertyModelInfo implements Serializable {

    private String propertyName;

    private Object value;

    private Class<?> returnType;

}

