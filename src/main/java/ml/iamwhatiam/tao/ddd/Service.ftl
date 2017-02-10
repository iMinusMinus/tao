<#import "pub.ftl" as software>
<@software.license />
 
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