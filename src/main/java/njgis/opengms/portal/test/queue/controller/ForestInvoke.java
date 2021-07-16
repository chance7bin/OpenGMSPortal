package njgis.opengms.portal.test.queue.controller;

import com.dtflys.forest.annotation.Get;
import com.dtflys.forest.annotation.JSONBody;
import com.dtflys.forest.annotation.Post;
import com.dtflys.forest.extensions.DownloadFile;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @Description description
 * @Author bin
 * @Date 2021/07/14
 */
@Component
public interface ForestInvoke {

    // 运行模型服务(get)
    @Get("http://${0}/modelser/60ed970b88baba1c6caee5e9?ac=run&inputdata=${1}")
    void invoke(String url, String jsonString);

    // 运行模型服务(post)
    @Post("http://${0}/modelser/60ed970b88baba1c6caee5e9?ac=run")
    Map invokeRunMs(String url, @JSONBody String jsonString);

    // 获得模型服务运行信息
    @Get("http://${0}/modelserrun/json/${1}")
    Map getMsrInfo(String url, String msrid);

    // 下载模型运行结果
    @Get("http://${0}/geodata/${1}")
    @DownloadFile(dir = "${2}")
    void download(String url, String gdid, String dir);

    // 获得服务器信息
    // @Get("http://localhost:8060/geodata/${0}")

    // @Get("http://localhost:7777/invoking")
    // void invokeinvoke();

}
