package njgis.opengms.portal.component;

import org.hibernate.MappingException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.id.UUIDGenerator;

import java.io.Console;
import java.io.Serializable;
import java.util.UUID;

/**
 * @Description 门户统一Id生成方法类
 * @Author kx
 * @Date 2021/7/1
 * @Version 1.0.0
 */
public class CustomIDGenerator implements IdentifierGenerator {

    @Override
        public Serializable generate(SharedSessionContractImplementor session, Object object) throws MappingException {
        System.out.println("CustomIDGenerator");
        return UUID.randomUUID().toString();

    }

}
