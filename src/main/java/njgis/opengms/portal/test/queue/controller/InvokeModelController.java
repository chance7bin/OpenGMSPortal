package njgis.opengms.portal.test.queue.controller;

import com.alibaba.fastjson.JSONObject;
import njgis.opengms.portal.entity.doo.JsonResult;
import njgis.opengms.portal.test.queue.entity.ServerInfo;
import njgis.opengms.portal.test.queue.service.InvokeService;
import njgis.opengms.portal.test.queue.service.ServerListener;
import njgis.opengms.portal.utils.ResultUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @Description 排队调用模型
 * @Author bin
 * @Date 2021/07/13
 */
@RestController
public class InvokeModelController {

    @Autowired
    private InvokeService invokeService;

    @Autowired
    private ServerListener serverListener;

    @Autowired
    private ForestInvoke forestInvoke;

    @GetMapping("/invoking")
    public String invoking(HttpServletResponse response){

        System.out.println("[          Task invoking] -- taskId");

        invokeService.invoking();
        // for (int i = 0; i < 3; i++) {
        //     invokeService.invoking(i);
        // }
        //        try {
//            response.sendRedirect("http://localhost:8060/geodata/" + gdid);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        return "finished";
    }

    @GetMapping("/getServerInfo")
    public List<ServerInfo> getServerInfo(){
        return serverListener.getServerInfo();
    }

    @RequestMapping(value = "/checkTaskStatus", method = RequestMethod.GET)
    public JsonResult checkTaskStatus(@RequestParam("taskId") String taskId){
        return ResultUtils.success(invokeService.checkTaskStatus(taskId)) ;
    }

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public JSONObject testStatus(){

        return (JSONObject) forestInvoke.getMsrInfo("http://172.21.212.78:8060/modelserrun/json/","60ed4f80392ee3134c9d4c0b" );
    }

    // @GetMapping("/thread")
    // public void thread() throws IOException, URISyntaxException {
    //     for (int i = 0; i < 6; i++) {
    //         MyHttpUtils.GET("http://localhost:7777/invoking","UTF-8",null);
    //     }
    // }
}
