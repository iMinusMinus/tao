<#import "pub.ftl" as software>
<@software.license />

package ml.iamwhatiam.tao.ddd.${namespace}<#if !samePackage>.support</#if>;

import java.util.List;
import java.util.ArrayList;
<#if !samePackage>
import ml.iamwhatiam.tao.ddd.${namespace}.domain.${bean.name?cap_first}Domain;
import ml.iamwhatiam.tao.ddd.${namespace}.vo.${bean.name?cap_first}VO;
</#if>
 
/**
 * transformation between Domain and VO
 * 
 * @author iMinusMinus
 * @since ${date?string('yyyy-MM-dd')}
 * @version 0.0.1
 *
 */
public class ${bean.name?cap_first}TransformationHelper {

    private ${bean.name?cap_first}TransformationHelper() {}

    public static ${bean.name?cap_first}Domain vo2domain(${bean.name?cap_first}VO vo) {
        ${bean.name?cap_first}Domain domain = new ${bean.name?cap_first}Domain();
        <#list bean.properties as property>
        domain.set${property.name?cap_first}(vo.get${property.name?cap_first}());
        </#list>
        return domain;
    }
    
    public static ${bean.name?cap_first}VO domain2vo(${bean.name?cap_first}Domain domain) {
        ${bean.name?cap_first}VO vo = new ${bean.name?cap_first}VO();
        <#list bean.properties as property>
        vo.set${property.name?cap_first}(domain.get${property.name?cap_first}());
        </#list>
        return vo;
    }
    
    public static List<${bean.name?cap_first}VO> domain2vo(List<${bean.name?cap_first}Domain> domains) {
    	List<${bean.name?cap_first}VO> result = new ArrayList<${bean.name?cap_first}VO>();
    	if(domains == null || domains.isEmpty()) return result;
    	for(${bean.name?cap_first}Domain domain : domains)
    	    result.add(domain2vo(domain));
    	   return  result;
    }

} 