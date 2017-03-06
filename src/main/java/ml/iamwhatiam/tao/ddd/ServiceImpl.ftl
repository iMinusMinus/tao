<#import "pub.ftl" as software>
<@software.license />
 
package ml.iamwhatiam.tao.ddd.${namespace}<#if !samePackage>.service</#if>;
 
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import ml.iamwhatiam.tao.dao.CrudDao;
import ml.iamwhatiam.tao.service.CrudService;
<#if !samePackage>
import ml.iamwhatiam.tao.ddd.${namespace}.support.${bean.name?cap_first}TransformationHelper;
import ml.iamwhatiam.tao.ddd.${namespace}.domain.${bean.name?cap_first}Domain;
import ml.iamwhatiam.tao.ddd.${namespace}.vo.${bean.name?cap_first}VO;
</#if>


/**
  * @author iMinusMinus
  * @since ${date?string('yyyy-MM-dd')}
  * @version 0.0.1
  */
@Service("${bean.name}Service")  
public class ${bean.name?cap_first}ServiceImpl implements CrudService<${bean.name?cap_first}VO> {

    @Resource(name = "${bean.name}Dao")
    private CrudDao<${bean.name?cap_first}Domain> ${bean.name}Dao;
    
    public List<${bean.name?cap_first}VO> findAll() {
        List<${bean.name?cap_first}Domain> result = ${bean.name}Dao.findAll();
        return ${bean.name?cap_first}TransformationHelper.domain2vo(result);
    }
    
    public List<${bean.name?cap_first}VO> find(${bean.name?cap_first}VO form, int offset, int limit) {
        List<${bean.name?cap_first}Domain> result = ${bean.name}Dao.query(${bean.name?cap_first}TransformationHelper.vo2domain(form), offset, limit);
        return ${bean.name?cap_first}TransformationHelper.domain2vo(result);
    }
    
    public long count(${bean.name?cap_first}VO form) {
        return ${bean.name}Dao.count(${bean.name?cap_first}TransformationHelper.vo2domain(form));
    }
    
    public ${bean.name?cap_first}VO findOne(long id) {
        return ${bean.name?cap_first}TransformationHelper.domain2vo(${bean.name}Dao.findOne(id));
    }
    
    public long save(${bean.name?cap_first}VO form) {
        return ${bean.name}Dao.save(${bean.name?cap_first}TransformationHelper.vo2domain(form));
    }
    
    public boolean update(${bean.name?cap_first}VO form) {
        return ${bean.name}Dao.update(${bean.name?cap_first}TransformationHelper.vo2domain(form));
    }
    
    public boolean remove(long id) {
        return ${bean.name}Dao.delete(id);
    }
    
    public CrudDao<${bean.name?cap_first}Domain> get${bean.name?cap_first}Dao() {
        return ${bean.name}Dao;
    }
    
    public CrudDao<${bean.name?cap_first}Domain> set${bean.name?cap_first}Dao(CrudDao<${bean.name?cap_first}Domain> ${bean.name}Dao) {
        this.${bean.name}Dao = ${bean.name}Dao;
    }

}  