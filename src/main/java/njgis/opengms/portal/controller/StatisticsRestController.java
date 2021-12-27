package njgis.opengms.portal.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

/**
 * @Description
 * @Author bin
 * @Date 2021/12/27
 */
@RestController
@RequestMapping(value="/statistics")
public class StatisticsRestController {


    @RequestMapping(value="/serverNodes",method = RequestMethod.GET)
    ModelAndView serverNodes(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("serverNodes");
        return modelAndView;
    }

}
