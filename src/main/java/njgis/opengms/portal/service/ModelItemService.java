package njgis.opengms.portal.service;

import njgis.opengms.portal.dao.DataItemDao;
import njgis.opengms.portal.dao.ModelItemDao;
import njgis.opengms.portal.entity.doo.Localization;
import njgis.opengms.portal.entity.doo.modelItem.ModelItemRelate;
import njgis.opengms.portal.entity.doo.modelItem.ModelRelation;
import njgis.opengms.portal.entity.dto.modelItem.ModelItemAddDTO;
import njgis.opengms.portal.entity.po.DataItem;
import njgis.opengms.portal.entity.po.ModelItem;
import njgis.opengms.portal.enums.ItemTypeEnum;
import njgis.opengms.portal.utils.ImageUtils;
import njgis.opengms.portal.utils.Utils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @ClassName ModelItemService
 * @Description 模型条目业务层
 * @Author Kai
 * @Date 2019/2/21
 * @Version 1.0.0
 */

@Service
public class ModelItemService {

    @Value("${resourcePath}")
    private String resourcePath;

    @Value("${htmlLoadPath}")
    private String htmlLoadPath;

    @Autowired
    ModelItemDao modelItemDao;

    @Autowired
    DataItemDao dataItemDao;

    @Autowired
    UserService userService;

    /**
     * @Description 添加模型条目
     * @param modelItemAddDTO 条目信息
     * @param email 条目作者
     * @Return njgis.opengms.portal.entity.po.ModelItem
     * @Author kx
     * @Date 2021/7/7
     **/
    public ModelItem insert(ModelItemAddDTO modelItemAddDTO, String email) {

        ModelItem modelItem = new ModelItem();
        BeanUtils.copyProperties(modelItemAddDTO, modelItem);
        Date now = new Date();
        modelItem.setCreateTime(now);
        modelItem.setLastModifyTime(now);
        modelItem.setStatus(modelItemAddDTO.getStatus());
        modelItem.setMetadata(modelItemAddDTO.getMetaData());
        modelItem.setAuthor(email);
        modelItem.setId(UUID.randomUUID().toString());
        String name = modelItemAddDTO.getName();
        modelItem.setAccessId(Utils.generateAccessId(name,modelItemDao.findAllByAccessIdContains(name),false));
        //localization图片本地化存储
        List<Localization> localizationList = modelItem.getLocalizationList();
        for(int l = 0;l<localizationList.size();l++){
            Localization localization = localizationList.get(l);
            localization.setDescription(ImageUtils.saveBase64Image(localization.getDescription(),modelItem.getId(),resourcePath,htmlLoadPath));
            localizationList.set(l,localization);
        }
        modelItem.setLocalizationList(localizationList);

        String path="/modelItem/" + UUID.randomUUID().toString() + ".jpg";

        if(modelItemAddDTO.getUploadImage()!=null){
            String[] strs=modelItemAddDTO.getUploadImage().split(",");
            if(strs.length>1) {
                String imgStr = modelItemAddDTO.getUploadImage().split(",")[1];
                ImageUtils.base64StrToImage(imgStr, resourcePath + path);
                modelItem.setImage(path);
            }
            else {
                modelItem.setImage("");
            }
        }else{
            modelItem.setImage(null);
        }

        ModelItemRelate modelItemRelate=new ModelItemRelate();

        modelItem.setRelate(new ModelItemRelate());
        modelItemDao.insert(modelItem);
        //用户条目计数加一
        userService.ItemCountPlusOne(email,ItemTypeEnum.ModelItem);

        return modelItem;

    }

    /**
     * @Description 根据id删除模型条目，删除之前
     * @param id
     * @param email
     * @Return int
     * @Author kx
     * @Date 2021/7/7
     **/
    public int delete(String id,String email){
        ModelItem modelItem=modelItemDao.findFirstById(id);
        if(!modelItem.getAuthor().equals(email))
            return 2;
        else if(modelItem!=null){
            //删除图片
            String image=modelItem.getImage();
            if(image.contains("/modelItem/")) {
                //删除旧图片
                File file=new File(resourcePath+modelItem.getImage());
                if(file.exists()&&file.isFile())
                    file.delete();
            }

            //删除与之关联数据中的记录
            List<String> relatedData=modelItem.getRelate().getDataItems();
            for(int i=0;i<relatedData.size();i++){
                DataItem dataItem=dataItemDao.findFirstById(relatedData.get(i));
                dataItem.getRelatedModels().remove(id);
                dataItemDao.save(dataItem);
            }

            //删除与之关联模型中的记录
            List<ModelRelation> relatedModelList=modelItem.getRelate().getModelRelationList();
            for(int i=0;i<relatedModelList.size();i++){
                ModelItem modelItem1 = modelItemDao.findFirstById(relatedModelList.get(i).getOid());
                List<ModelRelation> relatedModelList1 = modelItem1.getRelate().getModelRelationList();
                for(int j = 0;j<relatedModelList1.size();j++){
                    if(relatedModelList1.get(j).getOid().equals(modelItem.getId())){
                        modelItem1.getRelate().getModelRelationList().remove(j);
                        modelItemDao.save(modelItem1);
                    }
                }
            }

            //TODO 同时删除datahub dataApplication中的关联记录


            modelItemDao.delete(modelItem);
            userService.ItemCountMinusOne(email, ItemTypeEnum.ModelItem);
            return 1;
        }
        else{
            return -1;
        }
    }




}
