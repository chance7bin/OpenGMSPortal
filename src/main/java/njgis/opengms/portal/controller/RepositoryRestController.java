package njgis.opengms.portal.controller;

import io.swagger.annotations.ApiOperation;
import njgis.opengms.portal.service.RepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * @Description
 * @Author bin
 * @Date 2021/08/30
 */
@RestController
@RequestMapping(value = "/repository")
public class RepositoryRestController {

    @Autowired
    RepositoryService repositoryService;


    /**
     * template专题
     * @param
     * @return org.springframework.web.servlet.ModelAndView
     * @Author bin
     **/
    @ApiOperation(value = "template专题页面")
    @RequestMapping(value="/template",method = RequestMethod.GET)
    public ModelAndView getTemplateRepository() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("templateRepository");
        return modelAndView;
    }


    /**
     * 根据id得到template页面
     * @param id
     * @return org.springframework.web.servlet.ModelAndView
     * @Author bin
     **/
    @ApiOperation(value = "根据id得到template详情页面")
    @RequestMapping(value="/template/{id}",method = RequestMethod.GET)
    public ModelAndView getTemplatePage(@PathVariable("id") String id){
        return repositoryService.getTemplatePage(id);
    }


    /**
     * concept专题页面
     * @param
     * @return org.springframework.web.servlet.ModelAndView
     * @Author bin
     **/
    @ApiOperation(value = "concept专题页面")
    @RequestMapping(value="/concept",method = RequestMethod.GET)
    public ModelAndView getConceptRepository() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("conceptRepository");
        return modelAndView;
    }

    /**
     * 根据id得到concept页面
     * @param id
     * @return org.springframework.web.servlet.ModelAndView
     * @Author bin
     **/
    @ApiOperation(value = "根据id得到concept页面")
    @RequestMapping(value="/concept/{id}",method = RequestMethod.GET)
    public ModelAndView getConceptPage(@PathVariable("id") String id){
        return repositoryService.getConceptPage(id);
    }


    /**
     * spatialReference专题
     * @param
     * @return org.springframework.web.servlet.ModelAndView
     * @Author bin
     **/
    @ApiOperation(value = "spatialReference专题")
    @RequestMapping(value="/spatialReference",method = RequestMethod.GET)
    public ModelAndView getSpatialReferenceRepository() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("spatialReferenceRepository");
        return modelAndView;
    }

    /**
     * 根据id得到spatialReference页面
     * @param id
     * @return org.springframework.web.servlet.ModelAndView
     * @Author bin
     **/
    @ApiOperation(value = "根据id得到spatialReference页面")
    @RequestMapping(value="/spatialReference/{id}",method = RequestMethod.GET)
    public ModelAndView getSpatialReferencePage(@PathVariable("id") String id){
        return repositoryService.getSpatialReferencePage(id);
    }

    /**
     * unit专题页面
     * @param req
     * @return org.springframework.web.servlet.ModelAndView
     * @Author bin
     **/
    @ApiOperation(value = "unit专题")
    @RequestMapping(value="/unit",method = RequestMethod.GET)
    public ModelAndView getUnitRepository(HttpServletRequest req) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("unitRepository");
        return modelAndView;
    }

    /**
     * 根据id得到unit页面
     * @param id
     * @return org.springframework.web.servlet.ModelAndView
     * @Author bin
     **/
    @ApiOperation(value = "根据id得到unit页面")
    @RequestMapping(value="/unit/{id}",method = RequestMethod.GET)
    public ModelAndView getUnitPage(@PathVariable("id") String id){
        return repositoryService.getUnitPage(id);
    }

}
