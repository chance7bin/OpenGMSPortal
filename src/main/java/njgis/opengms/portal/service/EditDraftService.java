package njgis.opengms.portal.service;

import com.alibaba.fastjson.JSONObject;
import njgis.opengms.portal.dao.EditDraftDao;
import njgis.opengms.portal.entity.doo.JsonResult;
import njgis.opengms.portal.entity.dto.draft.EditDraftDTO;
import njgis.opengms.portal.entity.po.EditDraft;
import njgis.opengms.portal.utils.ResultUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @Description
 * @Author bin
 * @Date 2021/08/31
 */
@Service
public class EditDraftService {

    @Autowired
    EditDraftDao editDraftDao;

    /**
     * 根据用户以及类型得到草稿信息
     * @param user 
     * @param itemType 条目类型
     * @param editType 操作 create/edit 
     * @return java.util.List<njgis.opengms.portal.entity.po.EditDraft> 
     * @Author bin
     **/
    public List<EditDraft> getCreateDraftByUserByType(String user, String itemType, String editType){
        Sort sort = Sort.by(Sort.Direction.DESC,"lastModifyTime");

        List<EditDraft> editDrafts = editDraftDao.findByUserAndItemTypeAndEditTypeOrderByLastModifyTime(user,itemType,editType,sort);

        return editDrafts;
    }


    /**
     * 保存草稿
     * @param editDraftDTO 
     * @return njgis.opengms.portal.entity.po.EditDraft 
     * @Author bin
     **/
    public EditDraft init(EditDraftDTO editDraftDTO){
        String draftId = editDraftDTO.getDraftId();
        if(draftId == null || draftId.equals("")){
            return addNew(editDraftDTO);
        }else{
            return update(editDraftDTO);
        }

    }

    /**
     * 添加新草稿
     * @param editDraftDTO 
     * @return njgis.opengms.portal.entity.po.EditDraft 
     * @Author bin
     **/
    public EditDraft addNew(EditDraftDTO editDraftDTO){
        EditDraft editDraft = new EditDraft();
        if(editDraftDTO.getItemId()!=null){
            editDraft.setItemId(editDraftDTO.getItemId());
        }
        if(editDraftDTO.getItemName()!=null){
            editDraft.setItemName(editDraftDTO.getItemName());
        }
        editDraft.setContent(editDraftDTO.getContent());
        editDraft.setUser(editDraftDTO.getUser());
        editDraft.setItemType(editDraftDTO.getItemType());
        editDraft.setEditType(editDraftDTO.getEditType());
        Date date=new Date();
        editDraft.setCreateTime(date);
        editDraft.setLastModifyTime(date);
        return editDraftDao.insert(editDraft);
    }

    /**
     * 更新草稿
     * @param editDraftDTO 
     * @return njgis.opengms.portal.entity.po.EditDraft 
     * @Author bin
     **/
    public EditDraft update(EditDraftDTO editDraftDTO){
        EditDraft editDraft = editDraftDao.findFirstById(editDraftDTO.getDraftId());
        editDraft.setContent(editDraftDTO.getContent());
        Date date=new Date();
        editDraft.setLastModifyTime(date);
        return editDraftDao.save(editDraft);
    }

    /**
     * 根据用户得到草稿信息
     * @param asc
     * @param page
     * @param size
     * @param itemType
     * @param user 
     * @return com.alibaba.fastjson.JSONObject 
     * @Author bin
     **/
    public JSONObject pageByUser(int asc, int page, int size, String itemType, String user){
        Sort sort = Sort.by(asc == 1 ? Sort.Direction.ASC : Sort.Direction.DESC, "lastModifyTime");

        Pageable pageable = PageRequest.of(page - 1, size, sort);
        Page<EditDraft> editDraftPage = editDraftDao.findByUserAndItemType(user,itemType,pageable);

        JSONObject result = new JSONObject();
        result.put("content",editDraftPage.getContent());
        result.put("total",editDraftPage.getTotalElements());
        return result;
    }

    /**
     * 删除草稿
     * @param id 
     * @return njgis.opengms.portal.entity.doo.JsonResult 
     * @Author bin
     **/
    public JsonResult delete(String id){
        EditDraft editDraft = editDraftDao.findFirstById(id);
        if (editDraft == null)
            return ResultUtils.error("can not find item");
        editDraftDao.delete(editDraft);
        return ResultUtils.success();
    }


    public List<EditDraft> listByUser(String user,Boolean asc){
        Sort sort=Sort.by(asc == true ? Sort.Direction.ASC : Sort.Direction.DESC, "lastModifyTime");
        List<EditDraft> editDrafts = editDraftDao.findByUserOrderByLastModifyTime(user,sort);
        return editDrafts;
    }
    
}
