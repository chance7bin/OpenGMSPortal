package njgis.opengms.portal.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.client.*;
import com.mongodb.client.model.Sorts;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * @ClassName TaskMonitorService.java
 * @Author wzh
 * @Version 1.0.0
 * @Description
 * @CreateDate(Y/M/D-H:M:S) 2021/04/28/ 10:40:00
 */
@Service
public class TaskMonitorService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    public JSONObject getTasks(Map<String,String> searchInfo){

        String taskServerIp = searchInfo.get("ip");
        int page = Integer.parseInt(searchInfo.get("page")==null?"0":searchInfo.get("page"));
        String searchText = searchInfo.get("searchText");

        String MONGO_HOST = taskServerIp;
        Integer MONGO_PORT = Integer.parseInt(searchInfo.get("port"));
        String MONGO_DB_NAME = "GeoTaskServerDB";
        String MONGO_COLLECTION_NAME = "task";
        JSONObject result = new JSONObject();

        // MongoClient mongoClient = new MongoClient(MONGO_HOST, MONGO_PORT);
        // new MongoClient新版本中已废弃了 ！
        MongoClient mongoClient = MongoClients.create("mongodb://" + MONGO_HOST + ":" + MONGO_PORT);
        MongoDatabase db = mongoClient.getDatabase(MONGO_DB_NAME);
        MongoCollection<Document> collection = db.getCollection(MONGO_COLLECTION_NAME);

//        logger.info("---get db---");
        MongoCursor<Document> cursor = collection.find().sort(Sorts.orderBy(Sorts.descending("t_datetime"))).skip(page*10).limit(10).iterator();
//        logger.info("---get result---");
        // collection.count新版本中已废弃了 ！
        long total = collection.countDocuments();
//        logger.info("---^^^get result, totally" + total + "^^^---");
        JSONArray jsonArray = new JSONArray();
//        logger.info("---while---");
        while (cursor.hasNext()){
//            logger.info("---next---");
            JSONObject task = JSONObject.parseObject(cursor.next().toJson().toString()) ;
//            logger.info("<---"+ task.getString("t_msrid") +"--->");
            String time = task.getJSONObject("t_datetime").getString("$date");
//            logger.info("<---"+ time +"--->");
//             Long lll = Long.parseLong(time);
//            logger.info("<---"+ lll + lll.getClass()+"--->");
            try{
                SimpleDateFormat origin = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                SimpleDateFormat target =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = origin.parse(time);
                // Date date = new Date(lll);
//                logger.info("<---"+ date +"--->");
                task.put("t_datetime",target.format(date));
//                logger.info("---get t_datetime---");
                jsonArray.add(task);
            }catch (Exception e){
                logger.error(e.getMessage());
            }


        }

        result.put("list",jsonArray);
        result.put("total",total);

        logger.info("---get result, totally" + total + "---");
        return result;
    }


    public JSONArray getContainers(Map<String,String> searchInfo){

        String taskServerIp = searchInfo.get("ip");
        int page = Integer.parseInt(searchInfo.get("page")==null?"0":searchInfo.get("page"));
        String searchText = searchInfo.get("searchText");

        String MONGO_HOST = taskServerIp;
        Integer MONGO_PORT = Integer.parseInt(searchInfo.get("port"));
        String MONGO_DB_NAME = "GeoTaskServerDB";
        String MONGO_COLLECTION_NAME = "server";

        JSONObject result = new JSONObject();

        // MongoClient mongoClient = new MongoClient(MONGO_HOST, MONGO_PORT);
        MongoClient mongoClient = MongoClients.create("mongodb://" + MONGO_HOST + ":" + MONGO_PORT);
        MongoDatabase db = mongoClient.getDatabase(MONGO_DB_NAME);
        MongoCollection<Document> collection = db.getCollection(MONGO_COLLECTION_NAME);
        long total = collection.countDocuments();
        MongoCursor<Document> cursor = collection.find().sort(Sorts.orderBy(Sorts.descending("t_datetime"))).iterator();

        JSONArray jsonArray = new JSONArray();
        while (cursor.hasNext()){
            JSONObject container = JSONObject.parseObject(cursor.next().toJson().toString()) ;

            jsonArray.add(container);
        }

        return jsonArray;
    }
}
