package njgis.opengms.portal.entity.po;

import lombok.Data;
import njgis.opengms.portal.entity.doo.AuthorInfo;
import njgis.opengms.portal.entity.doo.Localization;
import njgis.opengms.portal.entity.doo.model.ModelMetadata;
import njgis.opengms.portal.entity.doo.support.ModelItemRelateOld;
import njgis.opengms.portal.entity.doo.support.ModelRelationOld;
import njgis.opengms.portal.entity.doo.support.Reference;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Document
@Data
public class ModelItemVersion {

    String status;

    List<Localization> localizationList = new ArrayList<>();
    List<String> alias = new ArrayList<>();

    @Id
    String id;
    String originOid;//正式数据库对应条目的oid
    String oid;
    String name;//
    String image;//
    String description;//
    String detail;//
    String modifier;
    String creator;
    int readStatus;

    Long verNumber;//版本号
    int verStatus;//版本状态 0:待审核 -1:被拒绝 2:通过 1:原始版本

    List<String> classifications;//
    List<String> classifications2;//
    List<String> keywords;//
    List<Reference> references;//
    List<AuthorInfo> authorship;//

    List<String> relatedData = new ArrayList<>();
    List<ModelRelationOld> modelRelationList = new ArrayList<>();
    ModelMetadata metadata = new ModelMetadata();
    ModelItemRelateOld relate;

    Date modifyTime;
    Date acceptTime;
    Date rejectTime;




}
