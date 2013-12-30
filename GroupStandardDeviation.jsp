<%@ page import = "java.sql.*" %>
<%@ page pageEncoding="UTF-8"%>
<%// by lydia Date 05/09/2008 Fix jsp file to support Thai language %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>Target Result</title>

<meta http-equiv="Content-Type" content="text/html">

</head>

<body>
<jsp:useBean id="Database" class="CP_Classes.Database" scope="session"/>
<jsp:useBean id="RDE" class="CP_Classes.RatersDataEntry" scope="session"/>
<jsp:useBean id="SurveyResult" class="CP_Classes.SurveyResult" scope="session"/>
<jsp:useBean id="Calculation" class="CP_Classes.Calculation" scope="session"/>
<jsp:useBean id="trans" class="CP_Classes.Translate" scope="session"/>

<table border="1" font style='font-size:11.0pt;font-family:Arial'>
<%
	//int surveyID = SurveyResult.getSurveyID();
	//int surveyLevel = SurveyResult.getSurveyLevel();	
	int surveyID = 246;
	int targetID = 3;
	int surveyLevel = 0;
	
	String compName;
	String KBName;
	String RTCode [] = SurveyResult.RatingCode(surveyID);

	double Result;
	int totalKB = SurveyResult.TotalKB(surveyID);		// total KB group by Competency
	
	int totalRT = SurveyResult.TotalRT(surveyID);
	
	//Calculation.GroupStDev(surveyID, targetID, surveyLevel);
	
	ResultSet targetResult = SurveyResult.getGroupStDev(surveyID);
	ResultSet AvgDev = SurveyResult.getGroupAvgStDev(surveyID);
	int totalDev = SurveyResult.getTotalGroupDev(surveyID);
	double avg [] = new double [totalDev];
	int s=0;
	while(AvgDev.next())
		avg[s++] = AvgDev.getDouble(3);
%>
<th width="200" align="left" bgcolor="navy"><b>
<font style='color:white' size="2"><%= trans.tslt("Competency") %></font></b></th>
<% if(surveyLevel == 1) {
%>
<th width="300" align="left" bgcolor="navy"><b>
<font style='color:white' size="2"><%= trans.tslt("Key Behaviour") %></font></b></th>
<% } %>
<% for(int a=0; a<RTCode.length; a++) {
%>
<th width="50" align="center" bgcolor="navy"><b>
<font style='color:white' size="2"><%=RTCode[a]%></font></b></th>
<% } %>
<%
	int j=0;
	while(targetResult.next()) {
		if(j == 0) {
			compName = targetResult.getString("CompetencyName");
%>
<tr>
	<td align="left">
		<font size="2">
		<%=compName%>
	</font>
	</td>
<%		
		}else {
%>
<tr>
	<td align="left">&nbsp;</td>
<%	}	
		if(surveyLevel == 1) {
			j++;
			if(j == totalKB)
				j = 0;
			KBName = targetResult.getString("KeyBehaviour");
%>
	<td align="left">
		<font size="2">
		<%=KBName%>
	</font>
	</td>			
<%		}	
		for(int i=0; i<totalRT; i++) {										
			Result = targetResult.getDouble("StDevResult");
%>
	<td align="center">
		<font size="2">
		<%=Result%>
	</font>
	</td>
<% 						
			if(i < totalRT-1)
				targetResult.next();				
		} 
%>
</tr>
<% } %>

</table>
</body>
</html>
