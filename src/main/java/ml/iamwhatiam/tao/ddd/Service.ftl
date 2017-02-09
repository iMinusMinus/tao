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

import org.springframework.stereotype.Service;

import ml.iamwhatiam.tao.ddd.dao.CrudDao;
import ml.iamwhatiam.tao.ddd.service.CrudService;
import ml.iamwhatiam.tao.ddd.${namespace}.TransformationHelper;
import ml.iamwhatiam.tao.ddd.${namespace}.domain.${bean.name}Domain;
import ml.iamwhatiam.tao.ddd.${namespace}.vo.${bean.name}VO;

/**
  * @author iMinusMinus
  * @since ${date?string.iso}
  * @version 0.0.1
  */
@Service("${bean.name}Service")  
public class ${bean.name}ServiceImpl implements CrudService {

    @Resource("${bean.name}Dao")
    private CrudDao<${bean.name}Domain> ${bean.name}DaoImpl;
    
    public List<${bean.name}Domain> findAll() {
    
    }
    
    public List<${bean.name}VO> find(${bean.name}VO form, int offset, int limit) {
    
    }
    
    public int count(${bean.name}VO form) {
    
    }
    
    public ${bean.name}VO findById(long id) {
    
    }
    
    public long save(${bean.name}VO form) {
    
    }
    
    public boolean update(${bean.name}VO form) {
    
    }
    
    public boolean remove(long id) {
    
    }
    
    public CrudDao<${bean.name}Domain> get${bean.name?upper_first}DaoImpl() {
        return ${bean.name}DaoImpl;
    }
    
    public CrudDao<${bean.name}Domain> set${bean.name?upper_first}DaoImpl(CrudDao<${bean.name}Domain> ${bean.name}DaoImpl) {
        this.${bean.name}DaoImpl = ${bean.name}DaoImpl;
    }

}  