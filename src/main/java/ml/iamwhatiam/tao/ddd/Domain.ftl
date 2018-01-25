<#import "pub.ftl" as software>
<@software.license />

package ml.iamwhatiam.tao.ddd.${namespace}<#if !samePackage>.domain</#if>;

<#if config?contains("lombok")>import lombok.Getter;
import lombok.Setter;</#if>
 
import ml.iamwhatiam.tao.domain.Taichi;
<#if bean.imports??>
<#list bean.imports as im>
import ${im};
</#list>
</#if>
 
 
/**
 * <#if bean.comment??>${bean.comment}</#if>
 *
 * @author iMinusMinus
 * @since ${date?string('yyyy-MM-dd')}
 * @version 0.0.1
 *
 */<#rt>
<#if config?contains("lombok")>
<#nt>
@Getter
@Setter
</#if>
<#nt>
public class ${bean.name?cap_first}Domain extends Taichi {
 
    private static final long serialVersionUID = 1L;
 	
 	<#list bean.properties as property>
 	<#if property.comment??>
 	/**
 	 * ${property.comment}
 	 */
 	</#if>
 	<#if property.constraints?? && property.constraints?size gt 0>
 	<#list property.constraints as constraint>
 	@${constraint.type}<#if constraint.values??>(<#list constraint.values?keys as key><#if key??>${key} = </#if>${constraint.values[key]}<#sep>, </#list>)</#if>
 	</#list>
 	</#if>
 	private ${software.getSimpleName(property.type)} ${property.name}<#if (property.defaultValue)??> = <#if property.defaultValue?is_string>"</#if>${property.defaultValue}<#if property.defaultValue?is_string>"</#if></#if>;
 	</#list>
 	<#nt>
 	<#if !config?contains("lombok")>
 	<#list bean.properties as property>
 	
 	public ${software.getSimpleName(property.type)} <#if property.type == "boolean">is<#else>get</#if>${property.name?cap_first}() {
 		return ${property.name};
 	}
 	
 	public void set${property.name?cap_first}(${software.getSimpleName(property.type)} ${property.name}) {
 		this.${property.name} = ${property.name};
 	}
 	</#list>
 	</#if>
 	
 	@Override
 	public String toString() {
 	<#-- return String.format(); -->
 	StringBuilder sb = new StringBuilder("{");
 	<#list bean.properties as property>
 	sb.append("\"${property.name}\":");<#if property.type == "java.lang.String">sb.append("\"").append(${property.name}).append("\"");<#else>sb.append(${property.name});</#if>
 	sb.append(",");
 	</#list>
 	sb.setLength(sb.length() - 1);
 	return sb.append("}").toString();
 	}
 
}