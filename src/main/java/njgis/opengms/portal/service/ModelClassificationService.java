package njgis.opengms.portal.service;

import njgis.opengms.portal.dao.ModelClassificationDao;
import njgis.opengms.portal.entity.po.ModelClassification;
import njgis.opengms.portal.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description 模型分类业务层
 * @Author kx
 * @Date 2021/7/7
 * @Version 1.0.0
 */
@Service
public class ModelClassificationService {

    @Autowired
    ModelClassificationDao modelClassificationDao;

    /**
     * @Description 通过分类id获取指定分类
     * @param id
     * @Return njgis.opengms.portal.entity.po.ModelClassification
     * @Author kx
     * @Date 2021/7/7
     **/
    public ModelClassification getById(String id){
        return modelClassificationDao.findFirstById(id);
    }

    /**
     * @Description 获取分类列表中的所有分类与子分类id
     * @param classIdList
     * @Return java.util.List<java.lang.String>
     * @Author kx
     * @Date 2021/7/7
     **/
    public List<String> getAllClsIdByClsList(List<String> classIdList){
        //null则返回空列表
        if(classIdList == null) return new ArrayList<>();

        for(String classId : classIdList){
            ModelClassification classification=getById(classId);
            if(classification!=null) {
                List<String> children = classification.getChildrenId();
                if (children.size() > 0) {
                    for (String child : children) {
                        if(!Utils.isStrInList(child, classIdList)) {
                            classIdList.add(child);
                        }
                        ModelClassification classification1=getById(child);
                        List<String> children1=classification1.getChildrenId();
                        if(children1.size()>0){
                            for(String child1:children1){
                                if(!Utils.isStrInList(child, classIdList)) {
                                    classIdList.add(child1);
                                }
                            }
                        }
                    }
                }
            }
        }
        return classIdList;
    }

}
