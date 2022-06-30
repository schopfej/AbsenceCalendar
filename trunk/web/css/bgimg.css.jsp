<%@ page language="java" isELIgnored="true" isThreadSafe="true"
	contentType="text/css; charset=UTF-8" pageEncoding="UTF-8"%>
body {<%String sysNam = (String) System.getProperty("SAPSYSTEMNAME");
			if (sysNam == null) {
				out.print("background: url(../img/backend/dev.png) repeat-y fixed right 60px; ");
			} else if (sysNam.startsWith("EP")) {
				out.print("background: url(../img/backend/test.png) repeat-y fixed right 60px; ");
			} else {
				// nothing, no specific background
			}%>font-family: sans-serif; margin: 0; padding: 0; font-size: 11.5px;
}
