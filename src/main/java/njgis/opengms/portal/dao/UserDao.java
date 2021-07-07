package njgis.opengms.portal.dao;


import njgis.opengms.portal.entity.doo.PortalId;
import njgis.opengms.portal.entity.po.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface UserDao extends MongoRepository<User,String> {

    User findFirstById(String id);

    User findFirstByAccessId(String userId);

    User findFirstByName(String name);

    User findFirstByEmail(String email);

    List<PortalId> findAllByAccessIdContains(String userId);

    @Query("{name:{$regex: '?0',$options:'i'}}")
    List<User> findAllByNameContainsIgnoreCase(String name);


}
