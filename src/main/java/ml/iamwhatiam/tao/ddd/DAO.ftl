<#import "pub.ftl" as software>
<@software.license />
 
package ml.iamwhatiam.tao.ddd.${namespace}<#if !samePackage>.dao</#if>;

import java.util.List;

import ml.iamwhatiam.tao.dao.CrudDao;
<#if !samePackage>import ml.iamwhatiam.tao.ddd.${namespace}.domain.${bean.name?cap_first}Domain;</#if>

/**
  * @author iMinusMinus
  * @since ${date?string('yyyy-MM-dd')}
  * @version 0.0.1
  */<#rt>
<#if config?contains("MyBatis") && !config?contains("iBatis")>
<#nt>
@Repository("${bean.name}Dao")
</#if>
<#nt>
public interface ${bean.name?cap_first}DAO<${bean.name?cap_first}Domain> extends CrudDao<${bean.name?cap_first}Domain> {


}