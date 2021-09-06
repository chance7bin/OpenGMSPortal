package njgis.opengms.portal.dao;

import njgis.opengms.portal.entity.po.EditDraft;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * @Description
 * @Author bin
 * @Date 2021/08/31
 */
public interface EditDraftDao extends MongoRepository<EditDraft, String> {
    List<EditDraft> findByUserAndItemTypeAndEditTypeOrderByLastModifyTime(String user, String itemType, String editType , Sort sort);

    EditDraft findFirstByUserAndItemType(String user, String itemType);

    EditDraft findFirstById(String id);

    Page<EditDraft> findByUserAndItemType(String user, String itemType, Pageable pageable);

}
