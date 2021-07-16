package njgis.opengms.portal.test.queue.controller;

import njgis.opengms.portal.test.queue.entity.ServerTable;
import njgis.opengms.portal.test.queue.service.InvokeService;
import njgis.opengms.portal.test.queue.service.ServerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

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
    private ServerService serverListener;

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
    public List<ServerTable> getServerInfo(){
        return serverListener.getServerInfo();
    }


    // @GetMapping("/thread")
    // public void thread() throws IOException, URISyntaxException {
    //     for (int i = 0; i < 6; i++) {
    //         MyHttpUtils.GET("http://localhost:7777/invoking","UTF-8",null);
    //     }
    // }
}
