<%@ page import="java.sql.*,
                 java.io.*,
                 java.lang.String,
				 java.util.*,
				 CP_Classes.vo.*"%>   
<jsp:useBean id="logchk" class="CP_Classes.Login" scope="session"/>                   
<jsp:useBean id="user" class="CP_Classes.User" scope="session"/>
<jsp:useBean id="RDTS" class="CP_Classes.Report_DistributionOfTotalScoreAndGaps" scope="session"/>
<jsp:useBean id="setting" class="CP_Classes.Setting" scope="session"/>
<jsp:useBean id="CE_Survey" class="CP_Classes.Create_Edit_Survey" scope="session"/>
<jsp:useBean id="trans" class="CP_Classes.Translate" scope="session"/>
<% 	// added to check whether organisation is a consulting company
// Mark Oei 09 Mar 2010 %>
<jsp:useBean id="Org" class="CP_Classes.Organization" scope="session"/>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html">
<%@ page pageEncoding="UTF-8"%>
<%// by lydia Date 05/09/2008 Fix jsp file to support Thai language %>
</head>
<SCRIPT LANGUAGE="JavaScript">
function proceed(form,field){
	form.action="Report_DistributionofTotalScores.jsp?proceed="+field.value;
	form.method="post";
	form.submit();
}

function selectSurvey1(form, field){
	form.action="Report_DistributionofTotalScores.jsp?survey1="+field.value;
	form.method="post";
	form.submit();
}
</script>

<body>
<%


String username=(String)session.getAttribute("username");

  if (!logchk.isUsable(username)) 
  {%> <font size="2">
   
	<script>
	parent.location.href = "index.jsp";
	</script>
<%  } 
  else 
  { 

if(request.getParameter("proceed") != null)
{ 
	int var2 = new Integer(request.getParameter("selOrg")).intValue();
	CE_Survey.set_survOrg(var2);
	logchk.setOrg(var2);
	logchk.setSelfSurvey(0);
}
if(request.getParameter("survey1") != null)
{
	logchk.setSelfSurvey(new Integer(request.getParameter("survey1")).intValue());
}
if(request.getParameter("btnPreview") != null)
{
	int SurveyID1 = new Integer(request.getParameter("selSurvey1")).intValue();
	int SurveyID2 = new Integer(request.getParameter("selSurvey2")).intValue();
	if (SurveyID1==0){ %>
	<script>alert("Please select a survey")</script>
	<%} else
	if(SurveyID2 == 0){ %>
	<script>alert("Please select a survey to be compared")</script>
	<%}
	else if(!RDTS.compareSurvey(SurveyID1, SurveyID2)){
		 %>
	<script>alert("These two survey is not comparable")</script>
		<%
	}else{
	String file_name = "Distribuion of Ratings by Competency.xls";
	if (RDTS.checkPCAllGenerated(SurveyID1) && RDTS.checkPCAllGenerated(SurveyID2)){
		RDTS.write(SurveyID1,SurveyID2,file_name);	
		//read the file name.

		String output = setting.getReport_Path() + "\\"+file_name;
		File f = new File (output);

		//set the content type(can be excel/word/powerpoint etc..)
		response.reset();
		response.setContentType ("application/xls");
		//set the header and also the Name by which user will be prompted to save
		response.addHeader ("Content-Disposition", "attachment;filename=\"Total Scores Distrivution Report.xls\"");
			
		//get the file name
		String name = f.getName().substring(f.getName().lastIndexOf("/") + 1,f.getName().length());
		//OPen an input stream to the file and post the file contents thru the 
		//servlet output stream to the client m/c
		
			InputStream in = new FileInputStream(f);
			ServletOutputStream outs = response.getOutputStream();
			
			
			int bit = 256;
			int i = 0;


	    		try {
	        			while ((bit) >= 0) {
	        				bit = in.read();
	        				outs.write(bit);
	        			}
	        			//System.out.println("" +bit);

	            		} catch (IOException ioe) {
	            			ioe.printStackTrace(System.out);
	            		}
	            //		System.out.println( "\n" + i + " bytes sent.");
	            //		System.out.println( "\n" + f.length() + " bytes sent.");
	            		outs.flush();
	            		outs.close();
	            		in.close();	
	}else{
		%><script>
		alert("Individual report for one or more of the selected report haven't been generated, please generate it first");
		</script><%
	}
	
	}//end if (surveyID==0)
}
%>
<form name="Report_DistributionofTotalScores" action="Report_DistributionofTotalScores.jsp" method="post">
<table border="0" width="483" cellspacing="0" cellpadding="0">
	<tr>
		<td><b>
		<font face="Arial" size="2" color="#000080"><%=trans.tslt("Total Score Distribution Report")%></font></b></td>
	</tr>
	<tr>
		<td>&nbsp;</td>
	</tr>
</table>
<table border="2" width="483" cellspacing="0" cellpadding="0" bgcolor="#FFFFCC" bordercolor="#3399FF">
		<tr>
		<td width="117" style="border-left-style: solid; border-left-width: 1px; border-right-style: none; border-right-width: medium; border-top-style: solid; border-top-width: 1px; border-bottom-style: none; border-bottom-width: medium">&nbsp;
		</td>
		<td width="228" style="border-left-style: none; border-left-width: medium; border-right-style: none; border-right-width: medium; border-top-style: solid; border-top-width: 1px; border-bottom-style: none; border-bottom-width: medium">&nbsp;
		</td>
		<td width="154" style="border-left-style: none; border-left-width: medium; border-right-style: solid; border-right-width: 1px; border-top-style: solid; border-top-width: 1px; border-bottom-style: none; border-bottom-width: medium">&nbsp; </td>
	</tr>
		<tr>
		<td width="117" style="border-left-style: solid; border-left-width: 1px; border-right-style: none; border-right-width: medium; border-top-style: none; border-top-width: medium; border-bottom-style: none; border-bottom-width: medium">
		<b><font face="Arial" size="2">&nbsp;<%=trans.tslt("Organisation")%>:</font></b></td>
		<td width="228" style="border-style: none; border-width: medium">
		<p align="left">		
<%
// Added to check whether organisation is also a consulting company
// if yes, will display a dropdown list of organisation managed by this company
// else, it will display the current organisation only
// Mark Oei 09 Mar 2010
	String [] UserDetail = new String[14];
	UserDetail = CE_Survey.getUserDetail(logchk.getPKUser());
	boolean isConsulting = true;
	isConsulting = Org.isConsulting(UserDetail[10]); // check whether organisation is a consulting company 
	if (isConsulting){ %>
		<select size="1" name="selOrg" onchange="proceed(this.form,this.form.selOrg)">
		<option value="0" selected><%=trans.tslt("All")%></option>
	<%
		Vector vOrg = logchk.getOrgList(logchk.getCompany());

		for(int i=0; i<vOrg.size(); i++)
		{
			votblOrganization vo = (votblOrganization)vOrg.elementAt(i);
			int PKOrg = vo.getPKOrganization();
			String OrgName = vo.getOrganizationName();

			if(logchk.getOrg() == PKOrg)
			{ %>
				<option value=<%=PKOrg%> selected><%=OrgName%></option>
			<% } else { %>
				<option value=<%=PKOrg%>><%=OrgName%></option>
			<%	}	
		} 
	} else { %>
		<select size="1" name="selOrg" onchange="proceed(this.form,this.form.selOrg)">
		<option value=<%=logchk.getSelfOrg()%>><%=UserDetail[10]%></option>
	<% } // End of isConsulting %>
</select><font size="2"> </font>
</td>
		<td width="154" style="border-left-style: none; border-left-width: medium; border-right-style: solid; border-right-width: 1px; border-top-style: none; border-top-width: medium; border-bottom-style: none; border-bottom-width: medium"> </td>
	</tr>
	<tr>
		<td width="117" align="center" height="25" style="border-left-style: solid; border-left-width: 1px; border-right-style: none; border-right-width: medium; border-top-style: none; border-top-width: medium; border-bottom-style: none; border-bottom-width: medium">&nbsp;
		</td>
		<td width="851" height="25" colspan="2" style="border-left-style: none; border-left-width: medium; border-right-style: solid; border-right-width: 1px; border-top-style: none; border-top-width: medium; border-bottom-style: none; border-bottom-width: medium">&nbsp;
		</td>
	</tr>
	<tr>
		<td width="117" align="center" height="25" style="border-left-style: solid; border-left-width: 1px; border-right-style: none; border-right-width: medium; border-top-style: none; border-top-width: medium; border-bottom-style: none; border-bottom-width: medium">
		<p align="left"><b><font face="Arial" size="2">&nbsp;<%=trans.tslt("Survey")%>1:</font></b></td>
		<td width="851" height="25" colspan="2" style="border-left-style: none; border-left-width: medium; border-right-style: solid; border-right-width: 1px; border-top-style: none; border-top-width: medium; border-bottom-style: none; border-bottom-width: medium"> <font size="2">
   
    	<select size="1" name="selSurvey1" onChange="selectSurvey1(this.form, this.form.selSurvey1)">
    	<option value="0" selected><%=trans.tslt("Please select one")%></option>
<%
	boolean anyRecord1=false;
	// Changed by Ha 23/05/08: calling method getSurveys passing different parameters
	Vector vS1=CE_Survey.getSurveys(logchk.getCompany(),logchk.getOrg());
	for(int i=0;i<vS1.size();i++){
	votblSurvey vo=(votblSurvey)vS1.elementAt(i);
		anyRecord1=true;
		int Surv_ID = vo.getSurveyID();
		String Surv_Name = vo.getSurveyName();
		if(logchk.getSelfSurvey() == Surv_ID)
			{ %>
				<option value=<%=Surv_ID%> selected><%=Surv_Name%></option>
			<% } else { %>
				<option value=<%=Surv_ID%>><%=Surv_Name%></option>
			<%	}
	}%>
</select></td>
	</tr>
	<tr>
		<td width="117" align="center" height="25" style="border-left-style: solid; border-left-width: 1px; border-right-style: none; border-right-width: medium; border-top-style: none; border-top-width: medium; border-bottom-style: none; border-bottom-width: medium">&nbsp;
		</td>
		<td width="851" height="25" colspan="2" style="border-left-style: none; border-left-width: medium; border-right-style: solid; border-right-width: 1px; border-top-style: none; border-top-width: medium; border-bottom-style: none; border-bottom-width: medium">&nbsp;
		</td>
	</tr>
	<tr>
		<td width="117" align="center" height="25" style="border-left-style: solid; border-left-width: 1px; border-right-style: none; border-right-width: medium; border-top-style: none; border-top-width: medium; border-bottom-style: none; border-bottom-width: medium">
		<p align="left"><b><font face="Arial" size="2">&nbsp;<%=trans.tslt("Survey")%>2:</font></b></td>
		<td width="851" height="25" colspan="2" style="border-left-style: none; border-left-width: medium; border-right-style: solid; border-right-width: 1px; border-top-style: none; border-top-width: medium; border-bottom-style: none; border-bottom-width: medium"> <font size="2">
   
    	<select size="1" name="selSurvey2" >
    	<option value="0" selected><%=trans.tslt("Please select one")%></option>
<%
	boolean anyRecord2=false;
	// Changed by Ha 23/05/08: calling method getSurveys passing different parameters
	Vector vS2=CE_Survey.getSurveys(logchk.getCompany(),logchk.getOrg());
	for(int i=0;i<vS2.size();i++){
	votblSurvey vo=(votblSurvey)vS2.elementAt(i);
		anyRecord2=true;
		int Surv_ID = vo.getSurveyID();
		String Surv_Name = vo.getSurveyName();
		if(RDTS.compareSurvey(logchk.getSelfSurvey(), Surv_ID)){
		if(logchk.getSelfSurvey() == Surv_ID)
			{ %>
				<option value=<%=Surv_ID%> selected><%=Surv_Name%></option>
			<% } else { %>
				<option value=<%=Surv_ID%>><%=Surv_Name%></option>
			<%	}
	}
	}%>
</select></td>
	</tr>
	<tr>
		<td width="117" align="center" height="25" style="border-left-style: solid; border-left-width: 1px; border-right-style: none; border-right-width: medium; border-top-style: none; border-top-width: medium; border-bottom-style: none; border-bottom-width: medium">&nbsp;
		</td>
		<td width="851" height="25" colspan="2" style="border-left-style: none; border-left-width: medium; border-right-style: solid; border-right-width: 1px; border-top-style: none; border-top-width: medium; border-bottom-style: none; border-bottom-width: medium">&nbsp;
		</td>
	</tr>
	<tr>
		<td width="116" align="center" style="border-left-style: solid; border-left-width: 1px; border-right-style: none; border-right-width: medium; border-top-style: none; border-top-width: medium; border-bottom-style: none; border-bottom-width: medium"> <font size="2">
   
		<p align="right">
		</td>
		<td width="228" align="center" style="border-style: none; border-width: medium"> <font size="2">
   
		<p align="left"> </td>
		<td width="155" align="center" style="border-left-style: none; border-left-width: medium; border-right-style: solid; border-right-width: 1px; border-top-style: none; border-top-width: medium; border-bottom-style: none; border-bottom-width: medium"> <font size="2">
   <%if(anyRecord1 && anyRecord2)
   	{%>
<% if(!logchk.getCompanyName().equalsIgnoreCase("demo") || logchk.getUserType() == 1) {
%>	
		<input type="Submit" value="<%=trans.tslt("Preview")%>" name="btnPreview" style="float: left">
<% } else {
%>	
		<input type="Submit" value="<%=trans.tslt("Preview")%>" name="btnPreview" style="float: left" disabled>
<%
} %>		
<%	}%>		
		</td>
	</tr>
	<tr>
		<td width="116" align="center" style="border-left-style: solid; border-left-width: 1px; border-right-style: none; border-right-width: medium; border-top-style: none; border-top-width: medium; border-bottom-style: solid; border-bottom-width: 1px">&nbsp; </td>
		<td width="228" align="center" style="border-left-style: none; border-left-width: medium; border-right-style: none; border-right-width: medium; border-top-style: none; border-top-width: medium; border-bottom-style: solid; border-bottom-width: 1px">&nbsp; </td>
		<td width="155" align="center" style="border-left-style: none; border-left-width: medium; border-right-style: solid; border-right-width: 1px; border-top-style: none; border-top-width: medium; border-bottom-style: solid; border-bottom-width: 1px">&nbsp; </td>
	</tr>
</table>
</form>
<%	}	%>
<table border="0" width="610" height="26">
	<tr>
		<td align="center" height="5" valign="top">
		</td>
		</tr>
	<tr>
		<td align="center" height="5" valign="top">
		</td>
		</tr>
	<tr>
		<td align="center" height="5" valign="top">
		</td>
		</tr>
	<tr>
		<td align="center" height="5" valign="top">
		</td>
		</tr>
	<tr>
		<td align="center" height="5" valign="top">
		</td>
		</tr>
	<tr>
		<td align="center" height="5" valign="top">
		</td>
		</tr>
	<tr>
		<td align="center" height="5" valign="top">
		</td>
		</tr>
	<tr>
		<td align="center" height="5" valign="top">
		</td>
		</tr>
	<tr>
		<td align="center" height="5" valign="top">
		</td>
		</tr>
	<tr>
		<td align="center" height="5" valign="top">
		</td>
		</tr>
	<tr>
		<td align="center" height="5" valign="top">
		</td>
		</tr>
	<tr>
		<td align="center" height="5" valign="top">
		</td>
		</tr>
	<tr>
		<td align="center" height="5" valign="top">
		</td>
		</tr>
	<tr>
		<td align="center" height="5" valign="top">
		</td>
		</tr>
	<tr>
		<td align="center" height="5" valign="top">
		</td>
		</tr>
	<tr>
		<td align="center" height="5" valign="top">
		</td>
		</tr>
	<tr>
		<td align="center" height="5" valign="top">
		</td>
		</tr>

	<tr>
		<td align="center" height="5" valign="top">
		</td>
	</tr>
	<tr>
		
	<%@ include file="Footer.jsp"%>
		</tr>
</table>
</body>
</html>
