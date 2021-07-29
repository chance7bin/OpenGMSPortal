package njgis.opengms.portal.test.queue.service;

import njgis.opengms.portal.test.queue.controller.ForestInvoke;
import njgis.opengms.portal.test.queue.dao.RunTaskDao;
import njgis.opengms.portal.test.queue.dao.SubmittedTaskDao;
import njgis.opengms.portal.test.queue.dto.TaskDTO;
import njgis.opengms.portal.test.queue.entity.DataItem;
import njgis.opengms.portal.test.queue.entity.RunTask;
import njgis.opengms.portal.test.queue.entity.SubmittedTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

//import javax.management.Query;
import javax.annotation.Resource;
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
    private SubmittedTaskDao submittedTaskDao;


    @Autowired
    private RunTaskDao runTaskDao;

    @Resource
    private MongoTemplate mongoTemplate;

    public void invoking() {

        // 创建任务记录
        SubmittedTask task = new SubmittedTask();
        task.setTaskId(UUID.randomUUID().toString());
        task.setTaskName("wordcloud");
        task.setUserId("localhost");
        task.setRunTime(new Date());
        task.setStatus(0);
        task.setMd5("818daf6969e1d7472739677b81f3bd98");
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
        submittedTaskDao.insert(task);
        mergeTask(task);

//       while (true){
//           TaskTable taskFinished = taskDao.findByTaskId(task.getTaskId());
//           if (taskFinished.getStatus() == 2){
//               // 获取数据id
//               String gdid = taskFinished.getOutputData().get(0).getDataId();
//               // 下载结果
//               String url = taskFinished.getIp() + ":" + taskFinished.getPort();
//               invoke.download(url,gdid, "E:\\Downloads\\" + gdid);
//               break;
//           }
//
//       }

    }

    public void mergeTask(SubmittedTask submittedTask){//如有参数相同的提交任务，合并到同一个执行任务，没有则新建一个

        String md5 = submittedTask.getMd5();

        List<RunTask> runTaskList = runTaskDao.findAllByMd5AndStatus(md5,0);

        if(runTaskList.size()!=0){
            for(RunTask runTask:runTaskList){
                if(compareTask(submittedTask,runTask)==0){
                    continue;
                }else {
                    List<String> relateTaskIds = runTask.getRelatedTasks();

                    relateTaskIds.add(submittedTask.getTaskId());

                    //runtask的字段必须单独更新，否则可能出现并发覆盖之前的修改
                    Query query = Query.query(Criteria.where("taskId").is(runTask.getTaskId()));
                    Update update = new Update();
                    update.set("relatedTasks",relateTaskIds);
                    mongoTemplate.updateFirst(query, update, RunTask.class);


                    submittedTask.setRunTaskId(runTask.getTaskId());
                    submittedTaskDao.save(submittedTask);

                    return;
                }
            }
        }

        //如果没有相同的，则新建
        RunTask runTask = new RunTask();
        runTask.setTaskId(UUID.randomUUID().toString());
        runTask.setMd5(submittedTask.getMd5());
        runTask.setStatus(0);
        runTask.setInputData(submittedTask.getInputData());
        runTask.setOutputData(submittedTask.getOutputData());
        // runTask与SubmittedTask进行关联
        runTask.getRelatedTasks().add(submittedTask.getTaskId());
        runTaskDao.insert(runTask);


        submittedTask.setRunTaskId(runTask.getTaskId());
        submittedTaskDao.save(submittedTask);

        return;

    }

    public int compareTask(SubmittedTask submittedTask, RunTask runTask){
        List<DataItem> runInputs = runTask.getInputData();
        List<DataItem> submitInputs = submittedTask.getInputData();

        int flag = 0;
        for(DataItem runInput:runInputs){
            flag = 0;
            String runStateId = runInput.getStateId();
            String runEvent = runInput.getEvent();
            String runData = runInput.getDataId();
            for(DataItem submitInput:submitInputs){
                if(runStateId.equals(submitInput.getStateId())
                        &&runEvent.equals(submitInput.getEvent())
                ){
                    // 判断提交的数据dataId是否与默认的几个选项的dataId相同
                   if( runData.equals(submitInput.getDataId())){
                       flag = 1;
                       break;
                   }else {
                       return 0;
                   }
                }else{
                    continue;
                }
            }

            if(flag==0){
                return 0;
            }
        }

        return 1;
    }

    public TaskDTO checkTaskStatus(String taskId){
        SubmittedTask submittedTask = submittedTaskDao.findFirstByTaskId(taskId);
        int status = submittedTask.getStatus();

        TaskDTO taskDTO = new TaskDTO();

        taskDTO.setStatus(0);
        taskDTO.setTaskId(taskId);
        //TODO status=1的时候返回什么
        if(status==0){
            taskDTO.setQueueNum(submittedTask.getQueueNum());
        }else if(status==2){
            taskDTO.setInputData(submittedTask.getInputData());
            taskDTO.setOutputData(submittedTask.getOutputData());
        }

        return taskDTO;
    }
}
