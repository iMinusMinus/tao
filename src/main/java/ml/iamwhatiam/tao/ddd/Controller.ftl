<#import "pub.ftl" as software>
<@software.license />

package ml.iamwhatiam.tao.ddd.${namespace}<#if !samePackage>.web</#if>;
 
import java.util.List;

import javax.annotation.Resource;
import javax.vaidation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

<#-- no need to import if Controller, Service, DAO and indeed POJOs are in same package -->
<#if !samePackage>import ml.iamwhatiam.tao.ddd.${namespace}.vo.${bean.name?cap_first}VO;</#if>
import ml.iamwhatiam.tao.service.CrudService;

 
/**
  * @author iMinusMinus
  * @since ${date?string('yyyy-MM-dd')}
  * @version 0.0.1
  */
@RequestMapping("/${namespace}")
@Controller
public class ${bean.name?cap_first}Controller {

    @Resource(name = "${bean.name}Service")
    private CrudService<${bean.name?cap_first}VO> service;

    @RequestMapping(value = "/${bean.name}", method = RequestMethod.GET)
    @ResponseBody
    public List<${bean.name?cap_first}VO> index() {
        return service.findAll();
    }

    @RequestMapping(value = "/${bean.name}/{id}", method = RequestMethod.GET)
    @ResponseBody
    public ${bean.name?cap_first}VO show(@PathVariable("id") long id) {
        return service.findOne(id);
    }
    
    <#-- 
    @RequestMapping(value = "/${bean.name}", method = RequestMethod.POST)
    @ResponseBody
    public List<${bean.name?cap_first}VO> search(@Valid ${bean.name?cap_first}VO form, BindingResult br) {
        return service.find(form);
    }
    -->

    @ResquestMapping(value = "/${bean.name}", method = RequestMethod.POST)
    @ResponseBody
    public ${bean.name?cap_first}VO create(@Valid ${bean.name?cap_first}VO form, BindingResult br) {
    	service.save(form);
        return form;
    }

    @RequestMapping(value = "/${bean.name}/{id}", method = RequestMethod.PUT)
    @ResponseBody
    public ${bean.name?cap_first}VO edit(@PathVariable("id") long id, @Valid ${bean.name?cap_first}VO form, BindingResult br) {
    	service.update(form);
        return form;
    }

    @RequestMapping(value = "/${bean.name}/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public boolean destroy(@PathVariable("id") long id) {
        return service.remove(id);
    }

    public CrudService<${bean.name?cap_first}VO> getService() {
        return service;
    }

    public void setService(CrudService<${bean.name?cap_first}VO> service) {
        this.service = service;
    }
 }