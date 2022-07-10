package njgis.opengms.portal.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import njgis.opengms.portal.utils.MyFileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * @Description
 * @Author bin
 * @Date 2021/12/27
 */
@RestController
@RequestMapping(value = "/help")
public class HelpRestController {

    @Value("${resourcePath}")
    private String resourcePath;

    @RequestMapping(value = "/demo", method = RequestMethod.GET)
    public ModelAndView getDemo(HttpServletRequest req) {
        // System.out.println("demo");
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("demoNew");
        modelAndView.addObject("name","OpenGMS");

        return modelAndView;
    }

    @RequestMapping(value = "/demo/{item}", method = RequestMethod.GET)
    public ModelAndView demoDocument(@PathVariable("item") String item, HttpServletRequest req) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("supportDocument");
        modelAndView.addObject("name",item.replace("_"," "));
        modelAndView.addObject("type","demo");

        return modelAndView;
    }

    @RequestMapping(value = "/manual", method = RequestMethod.GET)
    public ModelAndView getManual(HttpServletRequest req) {
        // System.out.println("manual");
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("manual");
        modelAndView.addObject("name","OpenGMS");

        return modelAndView;
    }

    @RequestMapping(value = "/document", method = RequestMethod.GET)
    public ModelAndView getDocument(HttpServletRequest req) {
        // System.out.println("document");
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("document");
        modelAndView.addObject("name","OpenGMS");

        return modelAndView;
    }

    @RequestMapping(value = "/support", method = RequestMethod.GET)
    public ModelAndView getSupport(HttpServletRequest req) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("support");
        modelAndView.addObject("name","OpenGMS");

        return modelAndView;
    }

    @RequestMapping(value = "/support/{item}", method = RequestMethod.GET)
    public ModelAndView supportDocument(@PathVariable("item") String item, HttpServletRequest req) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("supportDocument");
        modelAndView.addObject("name",item.replace("_"," "));
        modelAndView.addObject("type","sup");
        return modelAndView;
    }

    @RequestMapping(value = "/cite", method = RequestMethod.GET)
    public ModelAndView cite() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("citeOpenGMS");
        String citeStr = MyFileUtils.readToString(resourcePath+"/cacheFile/cite.json");
        JSONArray citeArr = JSONArray.parseArray(citeStr);
        for(int i = 0;i<citeArr.size();i++){
            JSONObject block = citeArr.getJSONObject(i);
            block.put("id", block.getString("block").replaceAll(" ","_"));
            JSONArray children = block.getJSONArray("children");
            for(int j = 0;j<children.size();j++){
                JSONObject child = children.getJSONObject(j);
                child.put("id", child.getString("head").replaceAll(" ", "_"));
            }
        }

        modelAndView.addObject("cites",citeArr);

        return modelAndView;
    }


}
