<%@ page import="java.sql.*,
                 java.io.*,
                 java.text.DateFormat,
                 java.util.*,
                 java.util.Date,
                 java.text.*,
                 java.lang.String"%>  

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html">
<%@ page pageEncoding="UTF-8"%>
<%// by lydia Date 05/09/2008 Fix jsp file to support Thai language %>
<title>Generate All Group Reports</title>
<!-- CSS -->

<link type="text/css" rel="stylesheet" href="lib/css/bootstrap.css">
<link type="text/css" rel="stylesheet"
	href="lib/css/bootstrap-responsive.css">
<link type="text/css" rel="stylesheet" href="lib/css/bootstrap.min.css">
<link type="text/css" rel="stylesheet"
	href="lib/css/bootstrap-responsive.min.css">


<!-- jQuery -->
<script type="text/javascript" src="lib/js/bootstrap.min.js"></script>
<script type="text/javascript" src="lib/js/bootstrap.js"></script>
<script type="text/javascript" src="lib/js/jquery-1.9.1.js"></script>


<script src="lib/js/bootstrap.min.js" type="text/javascript"></script>
<style type="text/css">
<!--
body,td,th {
	font-size: 14pt;
	color: #FF0000;
}
body {
	background-color: #FFFF33;
}
-->
</style></head>
<link REL="stylesheet" TYPE="text/css" href="Settings\Settings.css">

<jsp:useBean id="QE" class="CP_Classes.QuestionnaireReport" scope="session"/>
<jsp:useBean id="logchk" class="CP_Classes.Login" scope="session"/>

<body>
<form name="AllReports" method="post" target="parent">
<p>&nbsp;</p>
  <p align="center" size="2" face="Arial"><em><strong>System is generating reports.</strong></em></p>
  <p align="center" size="2" face="Arial"><em><strong>This process might take a while.</strong></em></p>
  <p align="center"><em><strong>Please do not close the main browser.</strong></em>
    </p>
  <p>&nbsp;</p>
</form>
<script language="JavaScript" type="text/javascript">
function closePopUp(){
	window.close();
}

window.setTimeout("closePopUp();", 5000);
</script>
</body>
</html>
