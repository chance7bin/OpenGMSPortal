package njgis.opengms.portal.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.List;

/**
 * @Description 数组工具类
 * @Author kx
 * @Date 21/11/12
 * @Version 1.0.0
 */
public class ArrayUtils {

    /**
     * @Description 将list转换成JSONArray
     * @param anyList
     * @Return com.alibaba.fastjson.JSONArray
     * @Author kx
     * @Date 21/11/11
     **/
    public static JSONArray parseListToJSONArray(List anyList){
        return JSONArray.parseArray(JSON.toJSONString(anyList));
    }

    /**
     * @Description 将JSONArray转换成list
     * @param jsonArray
     * @param cls
     * @Return java.util.List
     * @Author kx
     * @Date 21/11/12
     **/
    public static <T> List<T> parseJSONArrayToList(JSONArray jsonArray, Class<T> cls){
        return JSONObject.parseArray(jsonArray.toJSONString(), cls);
    }

}
