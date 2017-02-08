<#macro snake2camel snakeCase>
	<#local camelCase = snakeCase?split("_")>
	<#list camelCase as word>
		<#if word?index != 0>
		<#t>${word?cap_first}<#t>
		<#else>
		<#t>${word}<#t>
		</#if>
	</#list>
</#macro>