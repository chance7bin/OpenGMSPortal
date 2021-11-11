package njgis.opengms.portal.service;

import com.alibaba.fastjson.JSONObject;
import njgis.opengms.portal.dao.ComputableModelDao;
import njgis.opengms.portal.dao.UserDao;
import njgis.opengms.portal.entity.dto.FindDTO;
import njgis.opengms.portal.entity.po.ComputableModel;
import njgis.opengms.portal.entity.po.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description
 * @Author bin
 * @Date 2021/11/09
 */
@Service
public class ComputableModelService {

    @Autowired
    ComputableModelDao computableModelDao;

    @Value(value = "Public,Discoverable")
    private List<String> itemStatusVisible;

    @Autowired
    GenericService genericService;

    @Autowired
    UserDao userDao;

    public JSONObject searchDeployedModel(FindDTO findDTO){

        Pageable pageable = genericService.getPageable(findDTO);

        Page<ComputableModel> computableModelPage = computableModelDao.findAllByDeployAndStatusInAndNameLikeIgnoreCase(true,itemStatusVisible,findDTO.getSearchText(),pageable);
        List<ComputableModel> ComputableModelList = computableModelPage.getContent();

        for(ComputableModel computableModel:ComputableModelList){
            // String author = computableModel.getAuthor();
            User user = userDao.findFirstByEmail(computableModel.getAuthor());
            computableModel.setAuthor(user.getName());
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("total",computableModelPage.getTotalElements());
        jsonObject.put("content",ComputableModelList);

        return jsonObject;
    }

}
