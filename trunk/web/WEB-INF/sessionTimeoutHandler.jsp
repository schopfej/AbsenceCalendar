<?xml version="1.0" encoding="UTF-8" ?>
<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page" version="2.0">
	<jsp:directive.page import="java.io.PrintWriter" />
	<jsp:directive.page contentType="text/html; charset=ISO-8859-1"
		pageEncoding="UTF-8" session="true" />
	<jsp:directive.page errorPage="true" />
	<jsp:output doctype-root-element="html"
		doctype-public="-//W3C//DTD XHTML 1.0 Transitional//EN"
		doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"
		omit-xml-declaration="true" />
	<jsp:text>
		<![CDATA[<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>Anwendungssitzung terminiert</title>
</head>]]>
	</jsp:text>
	<jsp:scriptlet>Exception exception = (Exception) request.getAttribute("javax.servlet.error.exception");
			String requestUri = (String) request.getAttribute("javax.servlet.error.request_uri");
			String message = "HTTP Session Timeout at " + requestUri;
			org.apache.log4j.Logger.getLogger("sessionTimeoutHandlerJsf.jsp").info(message, exception);</jsp:scriptlet>
	<jsp:text>
		<![CDATA[<body>
	<style>
body {
	font-family: sans-serif;
	font-size: 14px;
}

h1 {
	font-size: 22px;
	color: #8E96B8;
}

a {
	color: #8E96B8;
}
</style>
	<h1>Anwendungssitzung wurde terminiert</h1>
	<div>
		Die Anwendungssitzung wurde aus Sicherheitsgründen nach ]]>
	</jsp:text>
	<jsp:expression>session.getMaxInactiveInterval() / 60</jsp:expression>
	<jsp:text>
		<![CDATA[ Minuten terminiert. Besten Dank für Ihr Verständnis.<br />Klicken Sie,
		um die Anwendung fortzusetzen, auf den nachfolgenden Link:
	</div>
	<p />
	<a href="]]>
	</jsp:text>
	<jsp:expression>requestUri</jsp:expression>
	<jsp:text>
		<![CDATA[">Weiter zur urspünglichen Anwendungsseite</a>
		</body>
	</html>]]>
	</jsp:text>
</jsp:root>
