<%@ page import="java.sql.*,
                 java.io.*,
				 java.util.*,
                 java.lang.String"%> 
				 
<jsp:useBean id="logchk" class="CP_Classes.Login" scope="session"/>                     
<jsp:useBean id="JobFunction" class="CP_Classes.JobFunction" scope="session"/>
<% 	// added to check whether organisation is a consulting company
// Mark Oei 09 Mar 2010 %>
<jsp:useBean id="Org" class="CP_Classes.Organization" scope="session"/>
<jsp:useBean id="CE_Survey" class="CP_Classes.Create_Edit_Survey" scope="session"/>
<jsp:useBean id="trans" class="CP_Classes.Translate" scope="session"/>
<%@ page import="CP_Classes.vo.voJobFunction"%>
<%@ page import="CP_Classes.vo.votblOrganization"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html">
<%@ page pageEncoding="UTF-8"%>
<%// by lydia Date 05/09/2008 Fix jsp file to support Thai language %>
</head>
<SCRIPT LANGUAGE=JAVASCRIPT>

function check(field)
{
	var isValid = 0;
	var clickedValue = 0;
	//check whether any checkbox selected
	if( field == null ) {
		isValid = 2;
	
	} else {

		if(isNaN(field.length) == false) {
			for (i = 0; i < field.length; i++)
				if(field[i].checked) {
					clickedValue = field[i].value;
					isValid = 1;
				}
		}else {		
			if(field.checked) {
				clickedValue = field.value;
				isValid = 1;
			}
				
		}
	}
	
	if(isValid == 1)
		return clickedValue;
	else if(isValid == 0)
		alert("No record selected");
	else if(isValid == 2)
		alert("No record available");
	
	isValid = 0;

}	
//Edited by Xuehai 02 Jun 2011. Remove 'void'. Enable to run on different browers like Chrome&Firefox.
//void function Delete(form, field)
function Delete(form, field)
{
	if(check(field))
	{
		if(confirm("<%=trans.tslt("Delete Job Function")%>?"))
		{
			form.action="JobFunction.jsp?Delete=1";
			form.method="post";
			form.submit();
		}
	}
}
//void function Edit(form, field1, field2)
function Edit(form, field1, field2)
{
	if(check(field1))
	{
		if(field2.value != "")
		{
			if(confirm("<%=trans.tslt("Edit Job Function")%>?"))
			{
					form.action="JobFunction.jsp?Edit=1";
					form.method="post";
					form.submit();
			}
		}
		else
		{
			alert("<%=trans.tslt("Please enter Job Function")%>");
		}
	}

}
//void function Add(form, field)
function Add(form, field)
{
	if(field.value != "")
	{
		if(confirm("<%=trans.tslt("Add Job Function")%>?"))
		{
				form.action="JobFunction.jsp?Add=1";
				form.method="post";
				form.submit();
		}
	}
	else
	{
		alert("<%=trans.tslt("Please enter Job Function")%>");
	}
}


/*	choosing organization*/

function proceed(form,field)
{
	form.action="JobFunction.jsp?proceed="+field.value;
	form.method="post";
	form.submit();
}	

function show(field1, field2,field3)
{
	
	for (i = 0; i < field1.length; i++) 
	{
		if(field1[i].checked)
		{
			field2.value = field3[i].value;
		}
	}
	if(field1.checked)
	{	
		field2.value = field3.value;
	}
}
	
</script>
<body style="text-align: left">
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
	int PKOrg = new Integer(request.getParameter("proceed")).intValue();
 	logchk.setOrg(PKOrg);
}  

if(request.getParameter("Delete") != null)
{
	
	int JobFunction_ID = new Integer(request.getParameter("JobFunction_ID")).intValue();

	boolean bIsDeleted = JobFunction.deleteRecord(JobFunction_ID,logchk.getOrg(), logchk.getPKUser());

	if(bIsDeleted) {
%>
		<script>
		alert("Deleted successfully");
		</script>
<%
	} 

}

if(request.getParameter("Edit") != null)
{
	int JobFunction_ID = new Integer(request.getParameter("JobFunction_ID")).intValue();
	
	String txtJobFunction = request.getParameter("txtJobFunction");
	
	int iPKJobFunction = JobFunction.getPKJobFunction(txtJobFunction, logchk.getOrg());
	
	if(iPKJobFunction != JobFunction_ID && iPKJobFunction != 0) {

%>		
		<script>
		alert("Record exists");
		</script>
<%	
	} else {
	
		boolean bIsEdited = JobFunction.editRecord(JobFunction_ID, txtJobFunction,logchk.getOrg(), logchk.getPKUser());

		if(bIsEdited) {
%>
		<script>
		alert("Edited successfully");
		</script>
<%
		} else {
%>		
		<script>
		alert("Record exists");
		</script>
<%		
		}
	}
}

if(request.getParameter("Add") != null)
{
	String txtJobFunction = request.getParameter("txtJobFunction");
	
	boolean bExist = JobFunction.existRecord(txtJobFunction, logchk.getOrg());
	
	if(!bExist) {
		boolean bIsAdded = JobFunction.addRecord(txtJobFunction,logchk.getOrg(), logchk.getPKUser());
	
		if(bIsAdded) {
%>
		<script>
		alert("Added successfully");
		</script>
<%
		} 
	} else {
%>		
		<script>
		alert("Record exists");
		</script>
<%		
	}
}
%>

</font> <font style="font-size: 14pt" color="#000080">
   
<form name ="JobFunction" method="post" action="JobFunction.jsp">
</font><font size="2">
   
    	<table border="0" width="99%" cellspacing="0" cellpadding="0">
	<tr>
		<td colspan="3">
		<b>
		<font face="Arial" color="#000080" size="2"><%= trans.tslt("Job Function") %></font></b></td>
		</tr>
	<tr>
		<td colspan="3">
<font size="2">
   
<ul>
	<li><font face="Arial" size="2"><%= trans.tslt("To Add, click on the Add button") %>.</font></li>
	<li><font face="Arial" size="2"><%= trans.tslt("To Edit, click on the relevant radio button and click on the Edit button") %>.</font></li>
	<li><font face="Arial" size="2"><%= trans.tslt("To Delete, click on the relevant radio button and click on the Delete button") %>.</font></li>
</ul>
		</td>
		</tr>
	<tr>
		<td colspan="3">&nbsp;</td>
	</tr>
	<tr>
		<td width="18%"><b><font face="Arial" size="2"><%= trans.tslt("Organisation") %>:</font></b></td>
		<td width="15%"><select size="1" name="selOrg" onchange="proceed(this.form,this.form.selOrg)">
<%
// Added to check whether organisation is also a consulting company
// if yes, will display a dropdown list of organisation managed by this company
// else, it will display the current organisation only
// Mark Oei 09 Mar 2010
	String [] UserDetail = new String[14];
	UserDetail = CE_Survey.getUserDetail(logchk.getPKUser());
	boolean isConsulting = true;
	isConsulting = Org.isConsulting(UserDetail[10]); // check whether organisation is a consulting company 
	if (isConsulting){
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
		<option value=<%=logchk.getSelfOrg()%>><%=UserDetail[10]%></option>
	<% } // End of isConsulting %>
</select></td>
		<td width="67%">&nbsp;
</td>
	</tr>
	<tr>
		<td width="18%">&nbsp;</td>
		<td width="15%">&nbsp;</td>
		<td width="67%">&nbsp;</td>
	</tr>
</table>
<table border="0" width="99%" cellspacing="0" cellpadding="0" height="79">
	<tr>
		<td>
<table border="0" width="69%" cellspacing="0" cellpadding="0">
	<tr>
		<td width="303">
		<div style='width:232px; height:136px; z-index:1; overflow:auto'>
<table border="1" width="210" height="12" bgcolor="#FFFFCC" bordercolor="#3399FF">
	<tr>
		<td colspan="2" bgcolor="Navy" height="27">
		<p align="center">
		<b><font face="Arial" color="#FFFFFF" size="2"><%= trans.tslt("Job Function") %></font></b></td>
	</tr>

<%
/********************
	* Edited by James 17 Oct 2007
	************************/
	 Vector v = JobFunction.getAllJobFunctions(logchk.getOrg());
	 
	 for(int i=0; i<v.size(); i++) {
	  
		voJobFunction vo = (voJobFunction)v.elementAt(i);
        

            	int JobFunction_ID = vo.getFKOrganization();
                String JobFunction_Desc = vo.getJobFunctionName();
		
%>
<tr onMouseOver = "this.bgColor = '#99ccff'"
    	onMouseOut = "this.bgColor = '#FFFFcc'">

		<td width="9%" align="center"><font face="Arial">
		<span style="font-size: 11pt"><input type="radio" name="JobFunction_ID" value=<%=JobFunction_ID%> onclick="show(JobFunction_ID,txtJobFunction,data)"></span><font size="2">
		</font></font>
		<td width="85%" align="left">
		<font face="Arial" size="2"><%=JobFunction_Desc%><input type=hidden value="<%=JobFunction_Desc%>" name="data"></font></td>
</tr>
<%	}	%>		

</table>
</div>
		</td>
		<td width="367" align="center">
		<b>
		<font face="Arial" size="2"><%= trans.tslt("Job Function") %>:</font></b><font size="2">&nbsp;
		</font>
		<input type="text" name="txtJobFunction" size="21"><table border="0" width="55%" cellspacing="0" cellpadding="0">
			<tr>
				<td align="center">&nbsp;</td>
				<td align="center">&nbsp;</td>
			</tr>
			<tr>
				<td align="center"><input type="button" value="<%= trans.tslt("Add") %>" name="btnAdd" onclick="Add(this.form, this.form.txtJobFunction)"></td>
				<td align="center">
				<input type="button" value="<%= trans.tslt("Edit") %>" name="btnEdit" onclick="Edit(this.form, this.form.JobFunction_ID,this.form.txtJobFunction)"></td>
			</tr>
		</table>
		</td>
	</tr>
</table>
		</td>
	</tr>
	<tr>
		<td width="65%">&nbsp;
		</td>
	</tr>
	<tr>
		<td width="72%">
		<table border="0" width="30%" cellspacing="0" cellpadding="0">
			<tr>
				<td width="99">&nbsp;</td>
				<td>
				<input type="button" value="<%= trans.tslt("Delete") %>" name="btnDel" style="float: right" onclick="Delete(this.form, this.form.JobFunction_ID)"></td>
			</tr>
		</table>
		</td>
	</tr>
</table>
</form>
<%	}	%>
<p></p>
<%@ include file="Footer.jsp"%></tr>

</body>
</html>