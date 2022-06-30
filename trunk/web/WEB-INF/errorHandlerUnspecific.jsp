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
	<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>Unspezifischer Anwendungfehler</title>
</head>
<jsp:scriptlet>Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
			Exception exception = (Exception) request.getAttribute("javax.servlet.error.exception");
			String requestUri = (String) request.getAttribute("javax.servlet.error.request_uri");
			String message = "HTTP Request at " + requestUri + "resulted in an error. (Status: "
					+ statusCode + ")\n" + exception.getMessage();
			StackTraceElement source = exception.getStackTrace()[0];
			org.apache.log4j.Logger.getLogger(source.getClassName()).error(message, exception);</jsp:scriptlet>
<body>
	<style>
body {
	font-family: sans-serif;
	font-size: 12px;
}

h1 {
	font-size: 18px;
}

th {
	text-align: left;
	vertical-align: top;
	white-space: nowrap;
}

td {
	white-space: pre;
}

.logo {
	float: right;
}
</style>
	<h1>Unspezifischer Anwendungfehler</h1>
	<hr style="color: red;" />
	<table>
		<tr>
			<th>Statuscode:</th>
			<td><jsp:expression>statusCode</jsp:expression></td>
		</tr>
		<tr>
			<th>Request URI:</th>
			<td><jsp:expression>requestUri</jsp:expression></td>
		</tr>
		<tr>
			<th>Message:</th>
			<td><jsp:expression>request.getAttribute("javax.servlet.error.message")</jsp:expression></td>
		</tr>
		<tr>
			<th>Class:</th>
			<td><jsp:expression>request.getAttribute("javax.servlet.error.exception_type")</jsp:expression></td>
		</tr>
		<tr>
			<th>Exception:</th>
			<td>
				<jsp:scriptlet>exception.printStackTrace(new java.io.PrintWriter(out));
			out.flush();</jsp:scriptlet>
			</td>
		</tr>
	</table>
</body>
	</html>
</jsp:root>
