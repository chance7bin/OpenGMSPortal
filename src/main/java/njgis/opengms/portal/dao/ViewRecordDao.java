package njgis.opengms.portal.dao;


import njgis.opengms.portal.entity.po.ViewRecord;
import njgis.opengms.portal.enums.ItemTypeEnum;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Date;
import java.util.List;


public interface ViewRecordDao extends MongoRepository<ViewRecord,String> {

    List<ViewRecord> findAllByItemType(ItemTypeEnum itemTypeEnum);

    List<ViewRecord> findAllByItemTypeAndItemId(ItemTypeEnum type, String id);

    List<ViewRecord> findAllByItemTypeAndItemIdAndDateGreaterThanEqual(ItemTypeEnum type, String id, Date date);

    List<ViewRecord> findAllByItemIdInAndDateGreaterThanEqual(List<String> ids, Date date);

    List<ViewRecord> findAllByItemIdAndItemTypeAndDateGreaterThanEqual(String id, ItemTypeEnum type, Date date);

    List<ViewRecord> findAllByItemIdAndFlag(String id, boolean flag);

    List<ViewRecord> findAllByItemIdAndFlagAndDateAfter(String id, boolean flag, Date date);

}
