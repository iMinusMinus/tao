/**
 * MIT License
 * 
 * Copyright (c) 2016 iMinusMinus
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
 
package ml.iamwhatiam.tao.ddd.${module.name};
 
import java.util.List;

import javax.annotation.Resource;
import javax.vaidation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

<#-- Controller, Service, DAO and inneed POJOs are in same package -->
import ml.iamwhatiam.tao.ddd.${module.name}.vo.${module.vo.name?upper_first}VO;
import ml.iamwhatiam.tao.ddd.${module.name}.service.${module.service.name?upper_first}Service;
import ml.iamwhatiam.tao.ddd.${module.name}.dao.${module.dao.name?upper_first}DAO;
 
/**
  * @author iMinusMinus
  * @since ${date}
  * @version 0.0.1
  */
@RequestMapping("/${module.classMapping}")
@Controller
public class ${module.controller.name?cap_first}Controller {

    @Resource(name = "${module.service.name}")
    private ${module.service.name?upper_first}Service service;

    @RequestMapping(value = "/${module.methodMapping}", method = RequestMethod.GET)
    @ResponseBody
    public List<${module.vo.type}> index() {
        return service.findAll();
    }

    @RequestMapping(value = "/${module.controller.name}/{id}", method = RequestMethod.GET)
    @ResponseBody
    public ${controller.po} show(@PathVariable("id") String id) {
        return service.findById(id);
    }

    @ResquestMapping(value = "/${module.controller.name}", method = RequestMethod.POST)
    @ResponseBody
    public ${controller.po} create(@Valid ${module.vo.type} form, BindingResult br) {
        return service.save(form);
    }

    @RequestMapping(value = "/${module.controller.name}/{id}", method = RequestMethod.PUT)
    @ResponseBody
    public boolean edit(@PathVariable("id") String id, @Valid ${module.vo.type} form, BindingResult br) {
        return service.update();
    }

    @RequestMapping(value = "/${module.controller.name}/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public boolean destroy(@PathVariable("id") String id) {
        return service.remove(id);
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }
 }