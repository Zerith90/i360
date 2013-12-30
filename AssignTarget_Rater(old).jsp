
<%@ page import="java.sql.*,
                 java.io.* "%>  
                 
<jsp:useBean id="db" class="CP_Classes.Database" scope="session"/>
<jsp:useBean id="assignTR" class="CP_Classes.AssignTarget_Rater" scope="session"/>
<jsp:useBean id="CE_Survey" class="CP_Classes.Create_Edit_Survey" scope="session"/>
<jsp:useBean id="user" class="CP_Classes.User" scope="session"/>
<jsp:useBean id="GFunc" class="CP_Classes.GlobalFunc" scope="session"/> 
<jsp:useBean id="logchk" class="CP_Classes.Login" scope="session"/>   
<jsp:useBean id="trans" class="CP_Classes.Translate" scope="session"/>

<html>
<head>
<%@ page pageEncoding="UTF-8"%>
<meta http-equiv="Content-Type" content="text/html">
<%// by lydia Date 05/09/2008 Fix jsp file to support Thai language %>
</head>

<link REL="stylesheet" TYPE="text/css" href="Settings\Settings.css">

<SCRIPT LANGUAGE=JAVASCRIPT>
var x = parseInt(window.screen.width) / 2 - 250;  // the number 250 is the exact half of the width of the pop-up and so should be changed according to the size of the pop-up
var y = parseInt(window.screen.height) / 2 - 125;  // the number 125 is the exact half of the height of the pop-up and so should be changed according to the size of the pop-up

var target
function check(field)
{
	var check= false;
	
	for (i = 0; i < field.length; i++) 
	{
		if(field[i].checked)
		{
			target = field[i].value;
			check = true;
		}
	}
	if(field != null && target == null)
	{
		target = field.value;
		check =true;
	}
	return check;
		
}

function popme()
{
	alert("<%=trans.tslt("The assignment has been saved")%>.");
	//Edited by Xuehai, 06 Jun 2011. Changing location.href() to location.href='';
	//location.href('SurveyList_AssignTR.jsp');
	location.href='SurveyList_AssignTR.jsp';
}

function popme_Sup()
{
	alert("<%=trans.tslt("The assignment has been saved")%>.");
	location.href='Nomination_AssignTR.jsp';
}

function root(form,field) 
{
	if(check(field))
	{
		form.action="AssignTarget_Rater.jsp?root="+target;
		form.method="post";
		form.submit();
	}
	
}

function branch(form,field) 
{
	if(check(field))
	{
		form.action="AssignTarget_Rater.jsp?branch="+target;
		form.method="post";
		form.submit();
	}
	
}

function search(form)
{
	form.action="AssignTarget_Rater.jsp?search=1";
	form.method="post";
	form.submit();
}

function add()
{
	var myWindow=window.open('AssignTR_AddTarget.jsp','windowRef','scrollbars=no, width=450, height=600');
	//myWindow.moveTo(x,y);
    myWindow.location.href = 'AssignTR_AddTarget.jsp';
}
</SCRIPT>
<body>
   
<form name="AssignTarget_Rater" action="AssignTarget_Rater.jsp" method="post">
<table class="tablesetting" id="table1">
	<tr>
		<td width="400">
		<br>
		<font face="Arial" style="font-weight: 700" color="#000080" size="2">
		<%= trans.tslt("Assign Target / Rater") %>
		</font></td>
	</tr>
	<tr>
		<td>
		<ul>
			<li>
			<%= trans.tslt("To add Rater, simply select the target and a window will pop up for you to add Rater") %>
			</li>			
		</ul>
		</td>
	</tr>
</table>

<%
	/* Nomination module */			
	if(logchk.getUserType() != 4)
	{	//User = Admin or SA
%>		
	<table class="tablesetting">
      <tr>
        <td width="113">Department</td>
        <td width="16">:</td>
        <td width="448"><select size="1" name="selDept">
<%
	int iDept = 0;
	
	if(request.getParameter("selDept") != null)
	{
		//Group 
		if (request.getParameter("selDept").equals(""))
			iDept = 0;
		else
			iDept = Integer.parseInt(request.getParameter("selDept"));
		//End Group 
	}
		
	if(iDept == 0)%>
			<option value='' selected>&nbsp</option>
<%
	String sql4 = "SELECT * FROM [Department] WHERE FKOrganization ="+logchk.getOrg() + " ORDER BY DepartmentName";
	ResultSet rs_Dept2 = db.getRecord(sql4);		
	while(rs_Dept2.next())
	{
		int Dept_ID = rs_Dept2.getInt("PKDepartment");
		String DeptName = rs_Dept2.getString("DepartmentName");

	if(iDept != 0 && iDept== Dept_ID )
	{
	%>
		<option value=<%=Dept_ID%> selected><%=DeptName%></option>
<%	}else{%>
		<option value=<%=Dept_ID%>><%=DeptName%></option>
<%	}
}	%>			
		</select></td>
      </tr>
      <tr>
        <td>&nbsp;</td>
        <td>&nbsp;</td>
        <td>&nbsp;</td>
      </tr>
      <tr>
        <td>Group / Section</td>
        <td>:</td>
        <td><select size="1" name="selGroup">
<%
	int iGroup = 0;
	
	if(request.getParameter("selGroup") != null)
	{
		//Group 
		if (request.getParameter("selGroup").equals(""))
			iGroup = 0;
		else
			iGroup = Integer.parseInt(request.getParameter("selGroup"));
		//End Group 
	}
		
	if(iGroup == 0)%>
			<option value='' selected>&nbsp</option>
<%
	String sql3 = "SELECT * FROM [Group] WHERE FKOrganization ="+logchk.getOrg() + " ORDER BY GroupName";
	ResultSet rs_Group2 = db.getRecord(sql3);		
	while(rs_Group2.next())
	{
		int Group_ID = rs_Group2.getInt("PKGroup");
		String GroupName = rs_Group2.getString("GroupName");

	if(iGroup != 0 && iGroup== Group_ID )
	{
	%>
		<option value=<%=Group_ID%> selected><%=GroupName%></option>
<%	}else{%>
		<option value=<%=Group_ID%>><%=GroupName%></option>
<%	}
}	%>			
		</select></td>
      </tr>
      <tr>
        <td>&nbsp;</td>
        <td>&nbsp;</td>
        <td>&nbsp;</td>
      </tr>
      <tr>
        <td colspan="3"><input type="button" value="<%= trans.tslt("Show")%>" name="btnSearch" onclick="search(this.form)"></td>
      </tr>
    </table>
<% } //END if(logchk.getUserType() != 4)
	/* END Nomination module */			
	
 %>		
		
<%
String username=(String)session.getAttribute("username");

  if (!logchk.isUsable(username)) 
  {%> <font size="2">
   
    	    	<script>
	parent.location.href = "index.jsp";
</script>
<%  } 

assignTR.setGroupID(0);
assignTR.setDivID(0);
assignTR.setDeptID(0);

if(request.getParameter("root") != null)
{
	int Target = new Integer(request.getParameter("root")).intValue();
	assignTR.setGroupID(0);
	assignTR.set_selectedTargetID(Target);

%>
<script>
	var myWindow=window.open('AssignTR_TargetMenu.jsp','windowRef','scrollbars=no, width=120, height=50');
	myWindow.moveTo(x,y);
    myWindow.location.href = 'AssignTR_TargetMenu.jsp';
    
</script>
<%
}

if(request.getParameter("branch") != null)
{
	int assignmentid = new Integer(request.getParameter("branch")).intValue();
	assignTR.set_selectedAssID(assignmentid);
%>
<script>
	window.document.location.href = "AssignTarget_Rater.jsp";
	var myWindow=window.open('AssignTR_RaterMenu.jsp','windowRef','scrollbars=no, width=120, height=20');
	myWindow.moveTo(x,y);
    myWindow.location.href = 'AssignTR_RaterMenu.jsp';
    
</script>
<%
}
%>
</font>
 <font size="2">

</p>
<table border="0" width="47%" cellspacing="0" cellpadding="0">
	<tr>
		<td width="32">&nbsp;</td>
		<td width="431" colspan="2" valign="top"> <font size="2">
   
		<font size="2">
   
		<p> </td>
	</tr>
	<tr>
		<td width="32">&nbsp;</td>
		<td width="216">
		<blockquote>
			
	<%		
		if(CE_Survey.getSurveyStatus() == 2)
		{ %>
			<input type="button" value="<%= trans.tslt("Back") %>" name="btnBack" style="float: left" onclick="location.href='SurveyList_AssignTR.jsp'">
	<%	}
		else
		{
			/* Nomination module */		
			
			if(logchk.getUserType() == 4)
			{	//User is Participant or Rater
		%>
			<input type="button" value="<%= trans.tslt("Save & Finish") %>" name="btnBack" style="float: left" onclick="popme_Sup()">
	<%		}
			else
			{	%>
			<input type="button" value="<%= trans.tslt("Save & Finish") %>" name="btnBack" style="float: left" onclick="popme()">
	<%		}	
		}	%>			
		</blockquote>
		</td>
		<td width="215">
<%	if(CE_Survey.getSurveyStatus() != 2)
	{ 
		/* Nomination module */			
		if(logchk.getUserType() == 4)
		{	//User is Participant or Rater
			//Don't Show Add Target Button
		}
		else
		{	
		%>
		<input type="button" value="<%= trans.tslt("Add Target") %>" name="btnAdd" onclick="add()">
		<%
		}
	}	
%>		
		
		
		</td>
	</tr>
	<tr>
		<td width="32">&nbsp;</td>
		<td width="216">&nbsp;
		</td>
		<td width="215">&nbsp;</td>
	</tr>
</table>
	<table border="0" width="68%" cellspacing="0" cellpadding="0">
		<tr>
			<td width="478">
			
<table border="0" width="89%" cellspacing="0" cellpadding="0" bordercolor="#3399FF" style="border-width: 0px">
		
<%	
	boolean anyRecord = false;
	String command ="SELECT * FROM tblSurvey a, tblOrganization b WHERE a.FKOrganization = PKOrganization AND SurveyID= "+ CE_Survey.getSurvey_ID();
	
	ResultSet rs_Survey = db.getRecord(command);		
	if(rs_Survey.next())
	{
		int FKOraganization = rs_Survey.getInt("FKOrganization");
		int NameSequence = rs_Survey.getInt("NameSequence");
		assignTR.set_NameSequence(NameSequence);
		CE_Survey.set_survOrg(FKOraganization);
	}

	assignTR.setGroupID(0);
	assignTR.setTargetID(0);
	String TargetDetail[];
	String RaterDetail[];
	String RaterCode =" ";
	int superior = 000;
	int other = 000;
	
	String query ="SELECT * FROM tblAssignment a, [User] b, [Group] c ";
	query = query + "WHERE a.TargetLoginID = b.PKUser AND b.Group_Section = c.PKGroup AND SurveyID="+ CE_Survey.getSurvey_ID();
	
	/* Nomination module */			
	if(logchk.getUserType() == 4)
	{	//User is Participant or Rater
		
		//Get list of Direct Reports of the user then Convert into String for SQL "IN" function to filter and show only DRs
		
		String sSQL = "SELECT * FROM tblUserRelation WHERE (User1 = " + logchk.getPKUser() + ") AND (RelationType = 0)";		
		ResultSet rsCheck = db.getRecord(sSQL);
		
		//Check whether the participant has no Relation, if No Relation, Participant can nominate anybody as target		
		if (!rsCheck.next())
		{
			String sArrUser = GFunc.putArrayListToString(user.listSpecificRelation(logchk.getPKUser(), 1));
			if (sArrUser.equals(""))
				query = query + " AND (PKUser IN (''))";
			else
				query = query + " AND (PKUser IN (" + sArrUser + "))";
		}
	}
	else
	{
		//Rianto 19-Jan-05: Implemented filter function to avoid showing the whole Targets list
		//assignTR.setSelectedGroup(0);
		if(request.getParameter("search") != null)
		{
			if(request.getParameter("selGroup").equals(""))
				assignTR.setSelectedGroup(-1); //Select all
			else
				assignTR.setSelectedGroup(Integer.parseInt(request.getParameter("selGroup")));
			
			// added by Jenty on 4th April to add in filter by Department as well	
			if(request.getParameter("selDept").equals(""))
				assignTR.setSelectedDept(-1); //Select all
			else
				assignTR.setSelectedDept(Integer.parseInt(request.getParameter("selDept")));
		}
		
		//System.out.println("Selected Group = " + assignTR.getSelectedGroup());
		if(assignTR.getSelectedGroup() != 0)
		{
			if(assignTR.getSelectedGroup() != -1)
				query = query + " AND Group_Section = " + assignTR.getSelectedGroup();
		}
		else
		{
			query = query + " AND Group_Section = 0";
		}
		//System.out.println("Selected Group = " + assignTR.getSelectedGroup());
		if(assignTR.getSelectedDept() != 0)
		{
			if(assignTR.getSelectedDept() != -1)
				query = query + " AND FKDepartment = " + assignTR.getSelectedDept();
		}
		else
		{
			query = query + " AND FKDepartment = 0";
		}
	}
	/* END Nomination module */
		
	query = query + " ORDER BY GroupName, PKGroup, LoginName ,AssignmentID,RaterLoginID";

ResultSet rs = db.getRecord(query);		
while(rs.next())
{	
	anyRecord = true;
	int AssID = rs.getInt("AssignmentID");
	int RTRelation = rs.getInt("RTRelation");
	RaterCode = rs.getString("RaterCode");
	int RaterStatus = rs.getInt("RaterStatus");
	int RaterID = rs.getInt("RaterLoginID");
	int TargetID = rs.getInt("TargetLoginID");
	String TargetName = rs.getString("LoginName");
	int GroupID = rs.getInt("PKGroup");
	String GroupName = rs.getString("GroupName");
	TargetDetail = user.getUserDetail(TargetID,assignTR.get_NameSequence());
	RaterDetail = user.getUserDetail(RaterID,assignTR.get_NameSequence());

	if((GroupID != assignTR.getGroupID()) || (assignTR.getGroupID() == 0))
	{
		assignTR.setGroupID(GroupID);
%>		
<tr>
		<td width="7%" valign="top" bordercolor="#3399FF" style="border-style:none; border-width:medium; ">&nbsp;
		</td>
		<td colspan="4" valign="top" bordercolor="#3399FF" style="border-style:none; border-width:medium; ">&nbsp;
		<%=query%>
		</td>
	</tr>
<tr>
		<td width="7%" valign="top" bordercolor="#3399FF" style="border-style:none; border-width:medium; ">
		<p align="center">
		<font size="2">
		<img border="0" src="images/Group.bmp" width="16" height="16"></font></td>
		<td colspan="4" valign="top" bordercolor="#3399FF" style="border-style:none; border-width:medium; ">
		<font face="Arial" style="font-weight: 700" color="#000080" size="2"><%=GroupName%></font></td>
	</tr>
<%	}	
	if(TargetID != assignTR.getTargetID() || assignTR.getTargetID() == 0)
	{
		assignTR.setTargetID(TargetID);
	%>	
	<tr>
		<td width="7%" valign="top" height="19" bordercolor="#3399FF" style="border-style:none; border-width:medium; ">&nbsp;</td>
		<td width="13%" valign="top" height="19" bordercolor="#3399FF" style="border-style: none; border-width: medium">&nbsp;
		</td>
		<td width="80%" valign="top" height="19" colspan="3" bordercolor="#3399FF" style="border-style:none; border-width:medium; ">&nbsp;
		</td>
	</tr>
	<tr>
		<td width="7%" valign="top" height="28"  style="border-style:none; border-width:medium; ">&nbsp;</td>
		<td width="13%" valign="top" height="28"  style="border-style: none; border-width: medium">
		<p align="center">

		<font size="2">

		<%		
			if(TargetID == assignTR.get_selectedTargetID())
			{	%>
				</font>
				<%	if(CE_Survey.getSurveyStatus() != 2)
				{ %>
				<input type="radio" value=<%=TargetID%> name="chkTarget" onclick="root(this.form,this.form.chkTarget)" checked><font size="2">
		<%		}	%>					
				<img border="0" src="images/Person.bmp" width="16" height="16" >
		<%	}
			else
			{	%>		
				</font>	
				<%	if(CE_Survey.getSurveyStatus() != 2)
				{ %>	
				<input type="radio" value=<%=TargetID%> name="chkTarget" onclick="root(this.form,this.form.chkTarget)"><font size="2">
			<%	}	%>
				<img border="0" src="images/Person.bmp" width="16" height="16" >
		<%	}	%>
				</font>
		    </td>
				<td valign="top" height="28" colspan="3" bordercolor="#0066ff" style="border-style:none; border-width:medium; ">
				<font face="Arial" color="#0066ff" size="2"><b><%=TargetDetail[0] +", "+TargetDetail[1]%></b></font></td>
				
		  </tr>
<%	}	%>
<tr>
		<td width="7%" valign="top" bordercolor="#3399FF" style="border-style:none; border-width:medium; ">&nbsp;</td>
		<td width="13%" valign="top" bordercolor="#3399FF" style="border-style:none; border-width:medium; " align="right">&nbsp;
		</td>
		
		<td width="13%" valign="top" align="right" bordercolor="#3399FF" style="border-style:none; border-width:medium; ">
		<p align="center">

<font size="2">

<%	if(RaterID != 0)
	{	
		String path=" ";	
		
		if(RaterStatus == 0)
			path = "images/RaterIncomplete.bmp";
		else if(RaterStatus == 1)
			path = "images/RaterCompleted.bmp";
		else if(RaterStatus == 2)
			path = "images/RaterUnreliable.bmp";
		else if(RaterStatus == 3)
			path = "images/RaterExcluded.bmp";	
		else if(RaterStatus == 4)
			path = "images/RaterCompleted.bmp";	
		else if(RaterStatus == 5)
			path = "images/RaterUnreliable.bmp";				

		if(AssID == assignTR.get_selectedAssID())
		{
	%>
			</font>
			<%	if(CE_Survey.getSurveyStatus() != 2)
			{ %>
			<input type="radio" name="chkRater" value=<%=AssID%> onclick="branch(this.form,this.form.chkRater)" checked><font size="2">
		<%	}	%>		
			<img border="0" src=<%=path%> width="16" height="16">
	<%	}
		else
		{	
	%>			
			</font>		
			<%	if(CE_Survey.getSurveyStatus() != 2)
			{ %>	
			<input type="radio" name="chkRater" value=<%=AssID%> onclick="branch(this.form,this.form.chkRater)"><font size="2">
		<%	}	%>
			<img border="0" src=<%=path%> width="16" height="16">
	<%	}	%>		
			</font>		
			</td>
			<td width="51%" valign="top" align="left" bordercolor="#3399FF" style="border-style:none; border-width:medium; ">
			<font face="Arial" size="2"><%=RaterDetail[0] +", "+RaterDetail[1]%>
			
					
			
		<td width="16%" bordercolor="#3399FF" style="border-style:none; border-width:medium; "><font face="Arial" size="2">(<%=RaterCode%>)</font></td>
		</tr>
	<%	
	}
}		//end while
%>

<tr>
		<td width="7%" valign="top" bordercolor="#3399FF" style="border-style:none; border-width:medium; ">&nbsp;</td>
		<td width="13%" valign="top" bordercolor="#3399FF" style="border-style:none; border-width:medium; ">&nbsp;
		</td>
		
		<td width="13%" valign="top" align="left" bordercolor="#3399FF" style="border-style:none; border-width:medium; ">&nbsp;
		</td>
			<td width="51%" valign="top" align="left" bordercolor="#3399FF" style="border-style:none; border-width:medium; ">
			<td width="16%" bordercolor="#3399FF" style="border-style:none; border-width:medium; ">&nbsp;</td>
		</tr>
	
</table>

	
</td>
			<td valign="top">
<table width="178" border="1" style='border-width:2px; font-size:10.0pt;font-family:Arial' bordercolor="#3399FF">
  <tr height="20">
    <td colspan="2" align="center" bordercolor="#3399FF" bgcolor="#000099" style="border-left-style:solid; border-left-width:1px; border-right-style:solid; border-right-width:1px; border-top-style:solid; border-top-width:1px">
	<b>
	<font color="#FFFFFF"><span class="style12"><%= trans.tslt("LEGEND") %></span></font></b></td>
    </tr>
  <tr height="20">
    <td align="center" style="border-style: solid; border-width: 1px; " bordercolor="#3399FF" bgcolor="#FFFFCC">
	<img src="images/Group.bmp"></td>
    <td style="border-left-style:solid; border-left-width:1px; border-right-style:solid; border-right-width:1px; border-bottom-style:solid; border-bottom-width:1px" bordercolor="#3399FF" bgcolor="#FFFFCC">
	<font color="#000080"><span class="style10">&nbsp;<%= trans.tslt("Group") %></span></font></td>
  </tr>
  <tr height="20">
    <td align="center" style="border-style: solid; border-width: 1px; " bordercolor="#3399FF" bgcolor="#FFFFCC">
	<img src="images/Person.bmp"></td>
    <td style="border-style: solid; border-width: 1px; " bordercolor="#3399FF" bgcolor="#FFFFCC">
	<font color="#000080"><span class="style10">&nbsp;<%= trans.tslt("Target") %></span></font></td>
  </tr>
  <tr height="20">
    <td align="center" style="border-style: solid; border-width: 1px; " bordercolor="#3399FF" bgcolor="#FFFFCC">
	<img src="images/RaterCompleted.bmp"></td>
    <td style="border-style: solid; border-width: 1px; " bordercolor="#3399FF" bgcolor="#FFFFCC">
	<font color="#000080"><span class="style10">&nbsp;<%= trans.tslt("Rater Completed") %></span></font></td>
  </tr>
  <tr height="20">
    <td align="center" style="border-style: solid; border-width: 1px; " bordercolor="#3399FF" bgcolor="#FFFFCC">
	<img src="images/RaterIncomplete.bmp"></td>
    <td style="border-style: solid; border-width: 1px; " bordercolor="#3399FF" bgcolor="#FFFFCC">
	<font color="#000080"><span class="style10">&nbsp;<%= trans.tslt("Rater Incomplete") %></span></font></td>
  </tr>
  <tr height="20">
    <td align="center" style="border-style: solid; border-width: 1px; " bordercolor="#3399FF" bgcolor="#FFFFCC">
	<img src="images/RaterUnreliable.bmp"></td>
    <td style="border-style: solid; border-width: 1px; " bordercolor="#3399FF" bgcolor="#FFFFCC">
	<font color="#000080"><span class="style10">&nbsp;<%= trans.tslt("Rater Unreliable") %></span></font></td>
  </tr>
</table>
			</td>
		</tr>
</table>
</form>
<table border="0" width="610" height="26">
	<tr>
		<td align="center" height="5" valign="top">
		<font size="1" color="navy" face="Arial">&nbsp;<a style="TEXT-DECORATION: none; color:navy;" href="Login.jsp">Home</a>&nbsp;|
		<a color="navy" face="Arial">&nbsp;</a><a style="TEXT-DECORATION: none; color:navy;" href="mailto:3SixtyProfiler@pcc.com.sg?subject=Regarding:">Contact 
		Us</a><a color="navy" face="Arial" href="termofuse.htm"><span style="color: #000080; text-decoration: none"> 
		| Terms of Use </span></a>|
		<span style="color: #000080; text-decoration: none">
		<a style="TEXT-DECORATION: none; color:navy;" href="http://www.pcc.com.sg/">
		PCC Website</a></span></font></td>
	</tr>
	<tr>
		<td align="center" height="5" valign="top">
		<font size="1" color="navy" face="Arial">&nbsp;Copyright ? 2004 Pacific 
		Century Consulting Pte Ltd. All Rights Reserved.</font></td>
	</tr>
</table>
</body>
</html>