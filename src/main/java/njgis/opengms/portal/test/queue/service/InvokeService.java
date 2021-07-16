package njgis.opengms.portal.test.queue.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import njgis.opengms.portal.test.queue.controller.ForestInvoke;
import njgis.opengms.portal.test.queue.dao.QueueDao;
import njgis.opengms.portal.test.queue.dao.ServerDao;
import njgis.opengms.portal.test.queue.dao.TaskDao;
import njgis.opengms.portal.test.queue.entity.DataItem;
import njgis.opengms.portal.test.queue.entity.IOData;
import njgis.opengms.portal.test.queue.entity.ServerTable;
import njgis.opengms.portal.test.queue.entity.TaskQueue;
import njgis.opengms.portal.test.queue.entity.TaskTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @Description description
 * @Author bin
 * @Date 2021/07/15
 */
@Service
public class InvokeService {

    // 注入接口实例
    // 注入forest
    @Autowired
    private ForestInvoke invoke;

    @Autowired
    private TaskDao taskDao;

    public void invoking(){

        // 创建任务记录
        TaskTable task = new TaskTable();
        task.setTaskId(UUID.randomUUID().toString());
        task.setTaskName("wordcloud");
        task.setUserId("localhost");
        task.setRunTime(new Date());
        task.setStatus(0);
        // 设置模型输入参数
        List<DataItem> inputdata = new ArrayList<>();
        DataItem inputdata1 = new DataItem();
        inputdata1.setStateId("349a82c8-7c63-443e-992f-eeff6defa9c2");
        inputdata1.setStateName("run");
        inputdata1.setEvent("inputTextFile");
        inputdata1.setDataId("gd_6992c5c0-e3e3-11eb-91dd-db660c0494d8");
        DataItem inputdata2 = new DataItem();
        inputdata2.setStateId("349a82c8-7c63-443e-992f-eeff6defa9c2");
        inputdata2.setStateName("run");
        inputdata2.setEvent("inputLanguageFile");
        inputdata2.setDataId("gd_6992ecd0-e3e3-11eb-91dd-db660c0494d8");
        inputdata.add(inputdata1);
        inputdata.add(inputdata2);
        task.setInputData(inputdata);
        // 把该任务加入到任务队列
        taskDao.insert(task);

       while (true){
           TaskTable taskFinished = taskDao.findByTaskId(task.getTaskId());
           if (taskFinished.getStatus() == 2){
               // 获取数据id
               String gdid = taskFinished.getOutputData().get(0).getDataId();
               // 下载结果
               String url = taskFinished.getIp() + ":" + taskFinished.getPort();
               invoke.download(url,gdid, "E:\\Downloads\\" + gdid);
               break;
           }

       }

    }
}
