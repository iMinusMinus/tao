<#import "pub.ftl" as pub>
测试字符串第一例首<@pub.snake2camel snakeCase="scalar_value" />测试字符串第一例尾
<#assign x = "some_value_to_test">
测试字符串第二例首<@pub.snake2camel snakeCase=x />
<#assign type1="java.lang.Long" />
${pub.getSimpleName(type1)}
<#assign type2="long" />
${pub.getSimpleName(type2)}