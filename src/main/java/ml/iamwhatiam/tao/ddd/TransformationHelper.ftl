<#import "pub.ftl" as software>
<@software.license />

package ml.iamwhatiam.tao.ddd.${namespace};
<#if !samePackage>
import ml.iamwhatiam.tao.ddd.${namespace}.domain.${bean.name}Domain;
import ml.iamwhatiam.tao.ddd.${namespace}.vo.${bean.name}VO;
</#if>
 
/**
 * transformation between Domain and VO
 * 
 * @author iMinusMinus
 * @since 2017-02-09
 * @version 0.0.1
 *
 */
public class TransformationHelper {

    private TransformationHelper() {}

    public static ${bean.name}Domain vo2domain(${bean.name}VO vo) {
        ${bean.name}Domain domain = new ${bean.name}Domain();
        <#list bean.properties as property>
        domain.set${property.name?upper_first}(vo.get${property.name?upper_first}());
        </#list>
        return domain;
    }
    
    public static ${bean.name}VO domain2vo(${bean.name}Domain domain) {
        ${bean.name}VO vo = new ${bean.name}VO();
        <#list bean.properties as property>
        vo.set${property.name?upper_first}(domain.get${property.name?upper_first}());
        </#list>
        return vo;
    }

} 