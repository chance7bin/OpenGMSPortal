package njgis.opengms.portal.test.queue.service;

import njgis.opengms.portal.test.queue.dao.ServerDao;
import njgis.opengms.portal.test.queue.entity.ServerInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description description
 * @Author bin
 * @Date 2021/07/16
 */
@Service
public class ServerListener {

    @Autowired
    private ServerDao serverDao;


    public List<ServerInfo> getServerInfo(){
        return serverDao.findAll();
    }

    // 查看空闲服务器
    public String getIdleServer(){
        // 获取服务器信息
        // boolean hasServer = false;
        List<ServerInfo> serverInfos;
        String runServerIp = null;

        serverInfos = getServerInfo();
        for (ServerInfo serverInfo : serverInfos) {
            int cr = serverInfo.getCurrentRun();
            if (cr < serverInfo.getMaxRun()) {
                cr += 1;
                serverInfo.setCurrentRun(cr);
                serverDao.save(serverInfo);
                // hasServer = true;
                runServerIp = serverInfo.getIp();
                break;
            }
        }

        return runServerIp;
    }

    public void updateServerStatus(String runServerIp){
        ServerInfo server = serverDao.findByIp(runServerIp);
        int currentRunCount = server.getCurrentRun();
        currentRunCount -= 1;
        server.setCurrentRun(currentRunCount);
        serverDao.save(server);
    }

}
