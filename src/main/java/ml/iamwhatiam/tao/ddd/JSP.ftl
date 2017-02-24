<#import "pub.ftl" as software>
<@software.license />
<#-- JSP, JSTL, EL, jQuery, jQueryUI -->
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
	<head>
		<meta charset="utf-8">
	    <meta name="viewport" content="width=device-width, initial-scale=1.0">
	    <meta name="keywords" content="${view.keywords}">
		<title>${view.title}</title>
		<link href="//${view.domain.static.style}/common.min.css" rel="stylesheet">
		<link href="//${view.domain.static.style}/jqueryui.min.css" rel="stylesheet">
		<script src=""//${view.domain.static.script}/jquery.min.js"></script>
		<!--[if lt IE 9]> 
		
		<![endif]-->
	</head>
	<body>
	
	</body>
</html>