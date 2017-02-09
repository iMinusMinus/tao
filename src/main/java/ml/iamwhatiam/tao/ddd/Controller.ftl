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
 
package ml.iamwhatiam.tao.ddd.${namespace};
 
import java.util.List;

import javax.annotation.Resource;
import javax.vaidation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

<#-- no need to import if Controller, Service, DAO and indeed POJOs are in same package -->
import ml.iamwhatiam.tao.ddd.${namespace}.vo.${bean.name}VO;
import ml.iamwhatiam.tao.ddd.service.CrudService;
 
/**
  * @author iMinusMinus
  * @since ${date?string.iso}
  * @version 0.0.1
  */
@RequestMapping("/${namespace}")
@Controller
public class ${bean.name}Controller {

    @Resource(name = "${bean.name}Service")
    private CrudService<${bean.name}VO> service;

    @RequestMapping(value = "/${bean.name}", method = RequestMethod.GET)
    @ResponseBody
    public List<${bean.name}VO> index() {
        return service.findAll();
    }

    @RequestMapping(value = "/${bean.name}/{id}", method = RequestMethod.GET)
    @ResponseBody
    public ${bean.name}VO show(@PathVariable("id") long id) {
        return service.findById(id);
    }
    
    <#-- 
    @RequestMapping(value = "/${bean.name}", method = RequestMethod.POST)
    @ResponseBody
    public List<${bean.name}VO> search(@Valid ${bean.name}VO form, BindingResult br) {
        return service.find(form);
    }
    -->

    @ResquestMapping(value = "/${bean.name}", method = RequestMethod.POST)
    @ResponseBody
    public ${bean.name}VO create(@Valid ${bean.name}VO form, BindingResult br) {
        return service.save(form);
    }

    @RequestMapping(value = "/${bean.name}/{id}", method = RequestMethod.PUT)
    @ResponseBody
    public boolean edit(@PathVariable("id") long id, @Valid ${bean.name}VO form, BindingResult br) {
        return service.update();
    }

    @RequestMapping(value = "/${bean.name}/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public boolean destroy(@PathVariable("id") long id) {
        return service.remove(id);
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }
 }