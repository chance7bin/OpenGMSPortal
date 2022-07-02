package njgis.opengms.portal.component.interceptor;

import njgis.opengms.portal.dao.ViewRecordDao;
import njgis.opengms.portal.entity.po.ViewRecord;
import njgis.opengms.portal.enums.ItemTypeEnum;
import njgis.opengms.portal.service.GenericService;
import njgis.opengms.portal.service.ManagementSystemService;
import njgis.opengms.portal.utils.IpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Date;


@Component
public class ItemViewRecordInterceptor implements HandlerInterceptor {

    @Autowired
    ViewRecordDao viewRecordDao;

    @Autowired
    ManagementSystemService managementSystemService;

    @Autowired
    GenericService genericService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String servletPath = request.getServletPath();
        HttpSession session = request.getSession();
        Object email = session.getAttribute("email");//userOid
        String[] paths = servletPath.split("/");
        String ip = IpUtil.getIpAddr(request);
        ViewRecord viewRecord = new ViewRecord();

        String userEmail;
        if(email!=null){
            userEmail = email.toString();
        }else {
            userEmail = null;
        }

        viewRecord.setEmail(userEmail);
        viewRecord.setIp(ip);
        viewRecord.setUrl(servletPath);
        viewRecord.setMethod(request.getMethod());
        viewRecord.setUserAgent(request.getHeader("user-agent"));
        viewRecord.setDate(new Date());

        ItemTypeEnum itemType;



        if(paths.length > 0 && paths[1].equals("repository")){
            if(paths.length>3) {
                String itemId = paths[3];
                itemId = genericService.formatId(itemId);
                if (itemId.length() >= 36) {
                    itemType = ItemTypeEnum.getItemTypeByName(paths[2]);
                    viewRecord.setItemId(itemId);
                    viewRecord.setItemType(itemType);
                }
            }
        }else {
            if(paths.length>2) {
                String itemId = paths[2];
                itemId = genericService.formatId(itemId);
                if (itemId.length() >= 24) {
                    itemType = ItemTypeEnum.getItemTypeByName(paths[1]);
                    viewRecord.setItemId(itemId);
                    viewRecord.setItemType(itemType);
                }
            }
        }

        // 记录用户访问的数量
        managementSystemService.recordUserViewCount(ip);

        viewRecordDao.insert(viewRecord);

        return true;
    }

}