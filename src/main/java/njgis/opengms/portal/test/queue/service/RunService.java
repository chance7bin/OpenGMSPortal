package njgis.opengms.portal.test.queue.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import njgis.opengms.portal.test.queue.controller.ForestInvoke;
import njgis.opengms.portal.test.queue.dao.RunningDao;
import njgis.opengms.portal.test.queue.dao.TaskDao;
import njgis.opengms.portal.test.queue.entity.DataItem;
import njgis.opengms.portal.test.queue.entity.IOData;
import njgis.opengms.portal.test.queue.entity.RunningTask;
import njgis.opengms.portal.test.queue.entity.TaskTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @Description description
 * @Author bin
 * @Date 2021/07/16
 */
@Service
public class RunService {

    // 注入forest
    @Autowired
    private ForestInvoke invoke;

    @Autowired
    private TaskDao taskDao;

    @Autowired
    private ServerService serverListener;

    @Autowired
    private RunningDao runningDao;


    // 调用模型服务容器接口
    public void run(TaskTable execTask){
        // TODO 做try-catch处理，可能请求模型容器接口出错
        String url = execTask.getIp() + ":" + execTask.getPort();
        IOData inputData = new IOData();
        inputData.setInputdata(execTask.getInputData());
        String jsonString = JSON.toJSONString(inputData);
        Map msMap = invoke.invokeRunMs(url,jsonString);
        String msrid = (String)msMap.get("data");
        // 存储模型运行信息到运行表中
        RunningTask runningTask = new RunningTask();
        runningTask.setMsrid(msrid);
        runningTask.setTaskId(execTask.getTaskId());
        runningDao.save(runningTask);

    }

    // 更新正在运行的模型任务
    public void runningListener(){
        JSONObject msrInfo;
        List<RunningTask> runningTasks = runningDao.findAll();
        for (RunningTask runningTask : runningTasks) {
            TaskTable execTask = taskDao.findByTaskId(runningTask.getTaskId());
            if (execTask.getStatus() == 1){
                String url = execTask.getIp() + ":" + execTask.getPort();
                msrInfo = (JSONObject) invoke.getMsrInfo(url,runningTask.getMsrid()).get("data");
                int msrStatus = (int) msrInfo.get("msr_status");
                if (msrStatus == 0)
                    break;
                if (msrStatus == 1){
                    // JSONObject转化为List
                    List<DataItem> outputdata = JSONObject.parseArray(msrInfo.get("msr_output").toString(),DataItem.class) ;
                    execTask.setOutputData(outputdata);
                    execTask.setStatus(2);
                    // 任务更新
                    taskDao.save(execTask);
                }
                else if (msrStatus == -1){
                    execTask.setStatus(-1);
                    taskDao.save(execTask);
                }

                // 任务结束,更新服务器信息
                serverListener.updateServerStatus(execTask.getIp());
                System.out.println("[          Task Finished] -- taskId : " + execTask.getTaskId());
            }
        }


    }

}
