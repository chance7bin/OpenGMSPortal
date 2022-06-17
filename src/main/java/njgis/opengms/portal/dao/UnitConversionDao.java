package njgis.opengms.portal.dao;

import njgis.opengms.portal.entity.po.UnitConversion;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * @Description
 * @Author bin
 * @Date 2022/06/09
 */
public interface UnitConversionDao  extends MongoRepository<UnitConversion,String>  {
    List<UnitConversion> findAll();
    UnitConversion findFirstById(String id);
}
