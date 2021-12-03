package njgis.opengms.portal.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

/**
 * @Description
 * @Author bin
 * @Date 2021/12/02
 */

@RestController
public class MainRestController {

    @RequestMapping(value={"/","/home"},method = RequestMethod.GET)
    public ModelAndView homepage() {
        // System.out.println("home");
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("index");
        return modelAndView;
    }

}
