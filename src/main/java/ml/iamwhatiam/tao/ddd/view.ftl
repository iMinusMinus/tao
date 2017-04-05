<#import "pub.ftl" as software>
<@software.license />
<#switch view>
<#case "jsp"><#-- JSP, JSTL, EL, jQuery, jQueryUI -->
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt"  uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<#-- http://java.sun.com/jsp/jstl/xml, http://java.sun.com/jsp/jstl/sql -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Frameset//EN" "http://www.w3.org/TR/html4/frameset.dtd">
<html>
	<head>
		<meta charset="utf-8">
	    <meta name="viewport" content="width=device-width, initial-scale=1">
	    <meta name="keywords" content="${view.keywords}">
	    <meta http-equiv="X-UA-Compatible" content="IE=edge">
		<title>${view.title}</title>
		<link href="//${view.domain.static.style}/common.min.css" rel="stylesheet">
		<link href="//${view.domain.static.style}/jqueryui.min.css" rel="stylesheet">
		<script src="//${view.domain.static.script}/jquery.min.js"></script>
		<!--[if lt IE 9]> 
		
		<![endif]-->
	</head>
	<body>
		<div id="header">
			<ul id="nav">
			<#noparse>
			<c:forEach item="${view.menu}" var="item">
				<li><a href="${item.menu.href}">${item.menu.name}</a>
					<if test="${!empty item.menu.children}">
					<ul>
						<c:forEach item="${item.menu.children}" var="child">
						<li><a href="${child.menu.href}">${child.menu.name}</a>
						<if test="${!empty child.menu.children}">
						<ul>
							<c:forEach item="${child.menu.children}" var="successor">
							<li><a href="${successor.menu.href}">${successor.menu.name}</a></li>
							</c:forEach>
						</ul>
						</c:if>
						</li>
						</c:forEach>
					</ul>
					</if>
				</li>
			</c:forEach>
			</#noparse>
			</ul>
		</div>
		<form name="${view.form.name!"query"}" action="${view.form.action}">
		<#if view.form.inputs??>
			<#list view.form.inputs as input>
			<#-- label -->
			<input name="${input.name}" type="${input.type}" <#if input.value??>value="${input.value}" </#if><#rt>
			<#lt><#if input.maxLength??>maxlength="${input.maxLength}" </#if><#rt>
			<#lt><#if input.minLength??>minlength="${input.minLength}" </#if><#rt>
			<#lt><#if input.size??>size="${input.size}" </#if><#rt>
			<#lt><#if input.required>required </#if><#rt>
			<#lt><#if input.pattern??>pattern="${input.pattern}" </#if><#rt>
			<#lt><#if input.min??>min="${input.min}" </#if><#rt>
			<#lt><#if input.max??>max="${input.max}" </#if><#rt>
			<#lt><#if input.step??>pattern="${input.step}" </#if><#rt>
			<#lt><#if input.placeHolder??>placeHolder="${input.placeHolder}" </#if><#rt>
			<#lt>>
			</#list>
		</#if>
		<#if view.form.selects>
			<#list view.form.selects as select>
			<select name="${select.name}">
			<#if select.options??>
				<#list select.options as option>
				<option value="${option.value}" <#if option.selected>selected</#if>>${option.text}</option>
				</#list>
			</#if>
			</select>
			</#list>
		</#if>	
		</form>
		<table id="result">
			<thead>
				<tr>
					<th><label for="ma">全选</label><input id="ma" name="select" type="radio" /></th>
					<c:forEach item="${'${'}{qr.head}${'}'}" var="item">
					<th><#noparse>${item.field}</#noparse></th>
					</c:forEach>
				</tr>
			</thead>
			<tbody>
				
			</tbody>
		</table>
		<iframe name="download" src="" class="hidden">IFRAME NOT SUPPORT IF YOU SAW THIS TEXT.</iframe>
<#break>
<#case "extjs">

<#break>
<#case "bootstrap"><#-- http://getbootstrap.com/customize/ -->
<!DOCTYPE html>
<html>
	<head>
		<meta charset="utf-8">
		<meta name="viewport" content="width=device-width, initial-scale=1">
		<title>${view.title}</title>
		<link href="//${view.domain.static.style}/bootstrap.min.css" rel="stylesheet">
		<!--[if lt IE 9]>
      	<script src="//${view.domain.static.script}/html5shiv.min.js"></script>
      	<script src="//${view.domain.static.script}/respond.min.js"></script>
    	<![endif]-->
	</head>
	<body>
		<div id="query">
		
		</div>
		<script src="//${view.domain.static.script}/jquery.min.js"></script>
		<script src="//${view.domain.static.script}/bootstrap.min.js"></script>
<#break>		
<#default><#-- html -->
<!DOCTYPE html>
<html>
	<head>
		<meta charset="utf-8">
		<meta name="viewport" content="width=device-width, initial-scale=1">
		<title>${view.title}</title>
	</head>
	<body>
		<header>
			<nav>
			
			</nav>
		</header>
		<section id="main">
		
		</section>
		<footer>
		
		</footer>
</#switch>
	</body>
</html>