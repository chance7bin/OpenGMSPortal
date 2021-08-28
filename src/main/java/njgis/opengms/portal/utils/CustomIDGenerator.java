package njgis.opengms.portal.utils;

import org.hibernate.MappingException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.UUIDGenerator;

import java.io.Serializable;
import java.util.UUID;

/**
 * @Description todo
 * @Author kx
 * @Date 2021/7/1
 * @Version 1.0.0
 */
public class CustomIDGenerator extends UUIDGenerator {

    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) throws MappingException {

        return UUID.randomUUID().toString();

    }

}
