package njgis.opengms.portal.test.queue.controller;

import com.alibaba.fastjson.JSONObject;
import njgis.opengms.portal.entity.doo.JsonResult;
import njgis.opengms.portal.test.queue.entity.ServerInfo;
import njgis.opengms.portal.test.queue.service.InvokeService;
import njgis.opengms.portal.test.queue.service.ServerService;
import njgis.opengms.portal.utils.ResultUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URISyntaxException;
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
    private ServerService serverService;

    @GetMapping("/invoking")
    public String invoking(HttpServletResponse response){

        System.out.println("[          Task invoking]");

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
        return serverService.getServerInfo();
    }

    @RequestMapping(value = "/checkTaskStatus", method = RequestMethod.GET)
    public JsonResult checkTaskStatus(@RequestParam("taskId") String taskId){
        return ResultUtils.success(invokeService.checkTaskStatus(taskId)) ;
    }


    // @GetMapping("/thread")
    // public void thread() throws IOException, URISyntaxException {
    //     for (int i = 0; i < 6; i++) {
    //         MyHttpUtils.GET("http://localhost:7777/invoking","UTF-8",null);
    //     }
    // }
}
