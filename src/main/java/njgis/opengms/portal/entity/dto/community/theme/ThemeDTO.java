package njgis.opengms.portal.entity.dto.community.theme;

import lombok.Data;
import njgis.opengms.portal.entity.doo.support.theme.*;
import njgis.opengms.portal.entity.dto.AddDTO;

import java.util.List;

/**
 * @Description
 * @Author bin
 * @Date 2021/12/02
 */
@Data
public class ThemeDTO extends AddDTO {

    String themename;
    String sub_themename;
    String detail;
    List<SubDetail> subDetails;
    String sub_detail;//
    String uploadImage;
    String creator_name;
    String creator_eid;//email
    String tabledata;
    String info_edit;
    //    String maintainer;
    List<Maintainer> maintainer;//其余的维护者为下标0之后存储
    List<ClassInfo> classinfo;
    List<SubClassInfo> subClassInfos;
    List<ClassInfo> subclassinfo;  //
    List<DataClassInfo>dataClassInfo;
    //    dataMethod
    List<DataMethodClassInfo> dataMethodClassInfo;
    List<SubDataInfo> subDataInfos;
    List<DataClassInfo> sub_dataClassInfo;//
    List<Application>application;
    List<SubApplication> subApplications;
    List<Application> sub_application;//
    List<ThemeData> themeData;  // 多级菜单保存下来，为了后面好编辑
    List<ThemeModelData> themeModelData;

}
