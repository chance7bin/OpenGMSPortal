package njgis.opengms.portal.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import njgis.opengms.portal.dao.ClassificationDao;
import njgis.opengms.portal.entity.po.Classification;
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
    ClassificationDao classificationDao;

    /**
     * @Description 通过分类id获取指定分类
     * @param id
     * @Return njgis.opengms.portal.entity.po.ModelClassification
     * @Author kx
     * @Date 2021/7/7
     **/
    public Classification getById(String id){
        return classificationDao.findFirstById(id);
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
            Classification classification=getById(classId);
            if(classification!=null) {
                List<String> children = classification.getChildrenId();
                if (children.size() > 0) {
                    for (String child : children) {
                        if(!Utils.isStrInList(child, classIdList)) {
                            classIdList.add(child);
                        }
                        Classification classification1=getById(child);
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

    /**
     * @Description 根据分类id该分类的路径
     * @param classifications
     * @Return com.alibaba.fastjson.JSONArray
     * @Author kx
     * @Date 21/11/15
     **/
    public JSONArray getClassifications(List<String> classifications){
        JSONArray classResult=new JSONArray();

        for(int i=0;i<classifications.size();i++){

            JSONArray array=new JSONArray();
            String classId=classifications.get(i);

            do{
                Classification classification=classificationDao.findFirstById(classId);

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("name",classification.getNameEn());
                jsonObject.put("id",classification.getId());

                array.add(jsonObject);
                classId=classification.getParentId();
            }while(classId!=null);

            JSONArray array1=new JSONArray();
            for(int j=array.size()-1;j>=0;j--){
                array1.add(array.get(j));
            }

            classResult.add(array1);

        }
        System.out.println(classResult);
        return classResult;
    }

}
