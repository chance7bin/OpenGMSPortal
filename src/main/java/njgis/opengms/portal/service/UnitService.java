package njgis.opengms.portal.service;

import njgis.opengms.portal.dao.UnitDao;
import njgis.opengms.portal.entity.doo.JsonResult;
import njgis.opengms.portal.entity.dto.SpecificFindDTO;
import njgis.opengms.portal.entity.dto.unit.UnitDTO;
import njgis.opengms.portal.entity.po.Unit;
import njgis.opengms.portal.enums.ItemTypeEnum;
import njgis.opengms.portal.utils.ResultUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Description
 * @Author bin
 * @Date 2021/09/03
 */
@Service
public class UnitService {

    @Autowired
    GenericService genericService;

    @Autowired
    RepositoryService repositoryService;

    @Autowired
    UnitDao unitDao;

    @Autowired
    UserService userService;

    public JsonResult getUnitList(SpecificFindDTO repositoryQueryDTO){
        return ResultUtils.success(genericService.searchItems(repositoryQueryDTO, ItemTypeEnum.Unit));
    }


    public JsonResult insertUnit(UnitDTO unitDTO, String email) {
        Unit unit = new Unit();

        unit = (Unit) repositoryService.commonInsertPart(unit,unitDTO, email, ItemTypeEnum.Unit);

        try {
            userService.updateUserResourceCount(email,ItemTypeEnum.Unit.getText(),"add");
        }catch (Exception e){
            return ResultUtils.error("update user unit resource fail");
        }

        return ResultUtils.success(unitDao.insert(unit).getId());

    }

    public JsonResult updateUnit(UnitDTO unitDTO, String email, String id) {
        return repositoryService.commonUpdatePart(id,email,unitDTO,ItemTypeEnum.Unit);
    }


    public JsonResult deleteUnit(String id, String email) {
        return repositoryService.commonDeletePart(id,email,ItemTypeEnum.Unit);
    }


    public JsonResult getUnitById(String id) {
        return repositoryService.getRepositoryById(id,unitDao);
    }

}
