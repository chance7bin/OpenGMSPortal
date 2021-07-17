package njgis.opengms.portal.test.queue.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import njgis.opengms.portal.test.queue.controller.ForestInvoke;
import njgis.opengms.portal.test.queue.dao.RunningDao;
import njgis.opengms.portal.test.queue.dao.SubmitedTaskDao;
import njgis.opengms.portal.test.queue.entity.DataItem;
import njgis.opengms.portal.test.queue.entity.IOData;
import njgis.opengms.portal.test.queue.entity.RunTask;
import njgis.opengms.portal.test.queue.entity.SubmitedTask;
import njgis.opengms.portal.utils.MyHttpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.net.URISyntaxException;
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
    private ForestInvoke forestInvoke;

    @Autowired
    private SubmitedTaskDao submitedTaskDao;

    @Autowired
    private ServerService serverListener;

    @Autowired
    private RunningDao runningDao;


    // 调用模型服务容器接口
    public void run(RunTask runTask) throws IOException {
        // TODO 做try-catch处理，可能请求模型容器接口出错
        String url = runTask.getIp() + ":" + runTask.getPort() + "/modelser";
        IOData inputData = new IOData();
        inputData.setInputdata(runTask.getInputData());
        String jsonString = JSON.toJSONString(inputData);

        JSONObject param = JSONObject.parseObject(jsonString);

        String result = MyHttpUtils.POSTWithJSON(url,"UTF-8",null,param);

        Map msMap = forestInvoke.invokeRunMs(url,jsonString);
        String msrid = (String)msMap.get("data");
        // 存储模型运行信息到运行表中
        runTask.setMsrid(msrid);
        runTask.setStatus(1);
        runningDao.save(runTask);

    }

    public RunTask checkTaskFromMSC(RunTask runTask) throws IOException, URISyntaxException {
        String ip = runTask.getIp();
        String port = runTask.getPort();
        String mid = runTask.getMid();

        String url = "http://" + ip + ":" +"port" + "/modelserrun/json/" + mid;
        String j_result = MyHttpUtils.GET(url,"UTF-8", null);

        JSONObject result = JSONObject.parseObject(j_result);

        int code = result.getInteger("code");
        if(code==-1){
            runTask.setStatus(-1);
        }else if(code==1){
            JSONObject modelInfo = (JSONObject) result.getJSONObject("data");
            JSONArray outputArray = modelInfo.getJSONArray("msr_output");
            List<DataItem> outputs = JSONObject.parseArray(outputArray.toString(),DataItem.class);
            runTask.setOutputData(outputs);
        }else{
            runTask.setStatus(0);
        }

        return runTask;
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
