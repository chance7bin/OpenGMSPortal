package njgis.opengms.portal.dao;

import njgis.opengms.portal.entity.po.Version;
import njgis.opengms.portal.enums.ItemTypeEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * @Description
 * @Author bin
 * @Date 2021/09/06
 */
public interface VersionDao extends MongoRepository<Version,String>{
    Version findFirstById(String id);

    Page<Version> findAll(Pageable pageable);

    List<Version> findAllByStatus(int status);

    Page<Version> findAllByStatus(int status, Pageable pageable);

    List<Version> findAllByItemCreator(String email);

    List<Version> findAllByEditor(String email);

    Page<Version> findAllByStatusAndTypeIn(int status, List<ItemTypeEnum> type, Pageable pageable);

    Page<Version> findAllByStatusAndEditorAndTypeIn(int status, String email, List<ItemTypeEnum> type, Pageable pageable);

    Page<Version> findAllByStatusAndItemCreatorAndTypeIn(int status, String email, List<ItemTypeEnum> type, Pageable pageable);
}
