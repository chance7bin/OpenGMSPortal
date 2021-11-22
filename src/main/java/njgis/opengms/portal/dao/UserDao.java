package njgis.opengms.portal.dao;


import njgis.opengms.portal.entity.doo.base.PortalIdPlus;
import njgis.opengms.portal.entity.po.User;
import njgis.opengms.portal.enums.UserRoleEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface UserDao extends MongoRepository<User,String> {

    User findFirstById(String id);

    User findFirstByAccessId(String userId);

    User findFirstByName(String name);

    User findFirstByEmail(String email);

    List<PortalIdPlus> findAllByAccessIdContains(String userId);

    // @Query("{name:{$regex: '?0',$options:'i'}}")
    List<User> findAllByNameContainsIgnoreCase(String name);

    Page<User> findAllByNameContainsIgnoreCase(String name, Pageable pageable);


    List<User> findAllByUserRole(UserRoleEnum role);

}
