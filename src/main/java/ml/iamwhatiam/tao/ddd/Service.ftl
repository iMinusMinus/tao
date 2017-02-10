<#import "pub.ftl" as software>
<@software.license />
 
package ml.iamwhatiam.tao.ddd.${namespace}<#if !samePackage>.service</#if>;
 
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import ml.iamwhatiam.tao.ddd.dao.CrudDao;
import ml.iamwhatiam.tao.ddd.service.CrudService;
<#if !samePackage>
import ml.iamwhatiam.tao.ddd.${namespace}.TransformationHelper;
import ml.iamwhatiam.tao.ddd.${namespace}.domain.${bean.name}Domain;
import ml.iamwhatiam.tao.ddd.${namespace}.vo.${bean.name}VO;
<#/if>


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
        return ${bean.name}DaoImpl.findAll();
    }
    
    public List<${bean.name}VO> find(${bean.name}VO form, int offset, int limit) {
        return ${bean.name}DaoImpl.query(TransformationHelper.vo2domain(form), offset, limit);
    }
    
    public long count(${bean.name}VO form) {
        return ${bean.name}DaoImpl.count(TransformationHelper.vo2domain(form));
    }
    
    public ${bean.name}VO findOne(long id) {
        return ${bean.name}DaoImpl.findOne(id);
    }
    
    public long save(${bean.name}VO form) {
        return ${bean.name}DaoImpl.save(TransformationHelper.vo2domain(form));
    }
    
    public boolean update(${bean.name}VO form) {
        return ${bean.name}DaoImpl.update(TransformationHelper.vo2domain(form));
    }
    
    public boolean remove(long id) {
        return ${bean.name}DaoImpl.delete(id);
    }
    
    public CrudDao<${bean.name}Domain> get${bean.name?upper_first}DaoImpl() {
        return ${bean.name}DaoImpl;
    }
    
    public CrudDao<${bean.name}Domain> set${bean.name?upper_first}DaoImpl(CrudDao<${bean.name}Domain> ${bean.name}DaoImpl) {
        this.${bean.name}DaoImpl = ${bean.name}DaoImpl;
    }

}  