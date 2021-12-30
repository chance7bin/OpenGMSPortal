package njgis.opengms.portal.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.sql.SQLException;

/**
 * @Description 数据库配置
 * @Author bin
 * @Date 2021/12/16
 */
@Slf4j
@Configuration
public class DBConfig {

    String taskServerAddr = "172.21.213.105:27017";


    @Bean(name = "taskCollection")
    public MongoCollection<Document> taskCollection() throws SQLException {
        return getTaskServerCollection("task");
    }

    @Bean(name = "serverCollection")
    public MongoCollection<Document> serverCollection() throws SQLException {
        return getTaskServerCollection("server");
    }


    /**
     * 连TaskServer服务器，得到其中的collection
     * @param  table 数据库中的表名
     * @return com.mongodb.client.MongoCollection<org.bson.Document>
     * @Author bin
     **/
    public MongoCollection<Document> getTaskServerCollection(String table){

        String MONGO_DB_NAME = "GeoTaskServerDB";
        String MONGO_COLLECTION_NAME = table;

        // MongoClient mongoClient = new MongoClient(MONGO_HOST, MONGO_PORT);
        // new MongoClient新版本中已废弃了 ！
        MongoClient mongoClient = MongoClients.create("mongodb://" + taskServerAddr);
        MongoDatabase db = mongoClient.getDatabase(MONGO_DB_NAME);
        MongoCollection<Document> collection = db.getCollection(MONGO_COLLECTION_NAME);

        //查数据库的写法
        // Document filter = new Document();
        // filter.append("_id", new ObjectId(taskId));
        // FindIterable<Document> data = collection
        //     .find(filter)
        //     .skip(0)
        //     .limit(1)
        //     .sort(new Document("t_datetime", -1));

        return collection;
    }

}
