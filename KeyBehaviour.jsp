<%@ page import = "java.sql.*" %>
<%@ page import = "java.io.*" %>
<%@ page import = "java.util.*" %>
<%@ page import = "CP_Classes.Competency" %>
<%@ page import = "CP_Classes.KeyBehaviour" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
<jsp:useBean id="logchk" class="CP_Classes.Login" scope="session"/>   
<jsp:useBean id="Comp" class="CP_Classes.Competency" scope="session"/>
<jsp:useBean id="KB" class="CP_Classes.KeyBehaviour" scope="session"/>
<jsp:useBean id="trans" class="CP_Classes.Translate" scope="session"/>
<% 	// added to check whether organisation is a consulting company
// Mark Oei 09 Mar 2010 %>
<jsp:useBean id="Org" class="CP_Classes.Organization" scope="session"/>
<jsp:useBean id="CE_Survey" class="CP_Classes.Create_Edit_Survey" scope="session"/>
<%@ page import="CP_Classes.vo.votblOrganization"%>
<%@ page import="CP_Classes.vo.voCompetency"%>
<%@ page import="CP_Classes.vo.voKeyBehaviour"%>

<title>Key Behaviour</title>

<meta http-equiv="Content-Type" content="text/html">
<%@ page pageEncoding="UTF-8"%>
<%// by lydia Date 05/09/2008 Fix jsp file to support Thai language %>

</head>

<body>
<p>
  <SCRIPT LANGUAGE="JavaScript">
<!-- Begin

var x = parseInt(window.screen.width) / 2 - 225;  // the number 250 is the exact half of the width of the pop-up and so should be changed according to the size of the pop-up
var y = parseInt(window.screen.height) / 2 - 125; 

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
//void function confirmAdd(form) {
function confirmAdd(form) {
	form.action = "KeyBehaviour.jsp";
	
	var myWindow=window.open('AddKB.jsp','windowRef','scrollbars=no, width=450, height=250');
	myWindow.moveTo(x,y);
    myWindow.location.href = 'AddKB.jsp';
}

//void function confirmEdit(form, field) {	
function confirmEdit(form, field) {	
	var value = check(field);
	
	if(value) {
		var myWindow=window.open('EditKB.jsp','windowRef','scrollbars=no, width=500, height=750');
		var query = "EditKB.jsp?clicked="+value;
		myWindow.moveTo(x,y);
    	myWindow.location.href = query;
	}
}

function confirmDelete(form, field) {
	var value = check(field);
	
	if(value)
	{	
		if(confirm('<%=trans.tslt("Delete Key Behaviour")%>?')) {
			form.action = "KeyBehaviour.jsp?delete="+value;
			form.method = "post";
			form.submit();
			return true;
		} else
			return false;
	}

}

/*------------------------------------------------------------start: LOgin modification 1------------------------------------------*/
/*	choosing organization*/

function proceed(form, org, comp, kb)
{  
	form.action="KeyBehaviour.jsp?org="+org.value + "&comp=" + comp.value + "&kb=" + kb.value;
	form.method="post";
	form.submit();	
}	

/* Function added by Ha on 15/05/08 to reload the page when changing organisation*/
function proceed1(form, org, comp, kb)
{
    comp.value = 0;
    kb.value = 0;
    form.action="KeyBehaviour.jsp?org="+org.value + "&comp=" + comp.value + "&kb=" + kb.value;
	form.method="post";
	form.submit();
}
</script>
  

<%	
	//response.setHeader("Pragma", "no-cache");
	//response.setHeader("Cache-Control", "no-cache");
	//response.setDateHeader("expires", 0);

String username=(String)session.getAttribute("username");

  if (!logchk.isUsable(username)) 
  {%> <font size="2">
   
    	    	<script>
	parent.location.href = "index.jsp";
</script>
<%  } 
  else 
  { 	
String compDef = " ";

if(request.getParameter("org") != null)
{ 
	int PKOrg = new Integer(request.getParameter("org")).intValue();
 	logchk.setOrg(PKOrg);
	
	int comp = Integer.parseInt(request.getParameter("comp"));	
	int kb = Integer.parseInt(request.getParameter("kb"));
	
	if(comp != 0)
		compDef = Comp.CompetencyDefinition(comp);
	
	KB.setKBLevel(kb);
	Comp.setPKComp(comp);
}

/*-------------------------------------------------------------------end login modification 1--------------------------------------*/

/************************************************** ADDING TOGGLE FOR SORTING PURPOSE *************************************************/

	int type = KB.getSortType(); //1=name, 2=origin	
	int toggle = KB.getToggle();	//0=asc, 1=desc		
			
	if(request.getParameter("name") != null)
	{	 
		if(toggle == 0)
			toggle = 1;
		else
			toggle = 0;
		
		KB.setToggle(toggle);
		
		type = Integer.parseInt(request.getParameter("name"));			 
		KB.setSortType(type);									
	} 
  
	
/************************************************** ADDING TOGGLE FOR SORTING PURPOSE *************************************************/
	

%>

<form name="KBList" method="post" action="KeyBehaviour.jsp">
<table border="0" width="555" cellspacing="0" cellpadding="0" span style='font-size:10.0pt;font-family:Arial;'>
	<tr>
	  <td colspan="3"><b><font color="#000080" size="2" face="Arial"><%= trans.tslt("Key Behaviour") %> </font></b></td>
	  <td>&nbsp;</td>
    </tr>
	<tr>
	  <td colspan="3"><ul>
	      <li><font face="Arial" size="2"><%= trans.tslt("To Add, click on the Add button to create English version, then use Edit function to fill in information for other languages")%>.</font></li>
          <li><font face="Arial" size="2"><%= trans.tslt("To Edit, click on the relevant radio button and click on the Edit button") %>.</font></li>
          <li><font face="Arial" size="2"><%= trans.tslt("To Delete, click on the relevant radio button and click on the Delete button") %>.</font></li>
	      </ul></td>
	  <td>&nbsp;</td>
    </tr>
	<tr>
		<td width="115"><%= trans.tslt("Organisation") %></td>
		<td width="12">&nbsp;</td>
		<td width="363"><select size="1" name="selOrg" onChange="proceed1(this.form,this.form.selOrg, this.form.Competency,this.form.KBLevel)">
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
		<td width="65">&nbsp;</td>
	</tr>

<%
	int DisplayNo, KBLevel;
	String KeyB, compName, Compname = "", origin="";		
	int pkComp, pkKB;
	DisplayNo = 1;
	KBLevel = 1;
   
	int PostKBLevel = KB.getKBLevel();
	
	int CompID = 0;	
	CompID = Comp.getPKComp();
	int compID = logchk.getCompany();
	int orgID = logchk.getOrg();
	int pkUser = logchk.getPKUser();	
	if(CompID != 0)
		compDef = Comp.CompetencyDefinition(CompID);	

	Vector CompResult = Comp.FilterRecord(compID, orgID);
	Vector KBResult = null;

	KBResult = KB.FilterKBList(CompID, PostKBLevel, compID, orgID);
		
	int t =0;
%>
    <tr>
      <td>&nbsp;</td>
      <td>&nbsp;</td>
      <td>&nbsp;</td>
      <td>&nbsp;</td>
    </tr>   
    <tr>
      <td><%= trans.tslt("Competency Level") %></td>
      <td>&nbsp;</td>
      <td>
        <select name="KBLevel">
          <option value=<%=t%>><%=trans.tslt("All")%>
          <%
			for(KBLevel = 1; KBLevel <= 10; KBLevel++) {
				if(PostKBLevel != 0 && PostKBLevel == KBLevel) {
		%>
          <option value=<%=KBLevel%> selected><%=KBLevel%>
          <% } else {
		%>
          <option value=<%=KBLevel%>><%=KBLevel%>
          <%
			}				
		}       
		%>
        </select></td>
      <td>&nbsp;</td>
    </tr>
	 <tr>
      <td>&nbsp;</td>
      <td>&nbsp;</td>
      <td>&nbsp;</td>
      <td>&nbsp;</td>
    </tr>
	 <tr>
       <td><%= trans.tslt("Competency") %></td>
       <td>&nbsp;</td>
       <td><select name="Competency">
           <option value=<%=t%>><%=trans.tslt("All")%>
           <%
		   /********************
			* Edited by James 30 Oct 2007
			************************/
			
			//while(CompResult.next()) {
			for(int i=0; i<CompResult.size(); i++) {
				voCompetency voC = (voCompetency)CompResult.elementAt(i);
				pkComp = voC.getPKCompetency();
				compName = voC.getCompetencyName();
				//origin = aResult.getString("Description");
				if(pkComp == CompID) {
		%>
           <option value = <%=pkComp%> selected><%=compName%>
           <% } else {
		%>
           <option value = <%=pkComp%>><%=compName%>
           <%
			}
				
			}
			
		%>
            </select></td>
       <td align="left"><input type="button" value="<%= trans.tslt("Show") %>" name="btnShow" onclick="proceed(this.form,this.form.selOrg, this.form.Competency, this.form.KBLevel)"></td>
    </tr>
	 <tr>
	   <td>&nbsp;</td>
	   <td>&nbsp;</td>
	   <td>&nbsp;</td>
	   <td>&nbsp;</td>
    </tr>
	 <tr>
      <td><%= trans.tslt("Definition") %></td>
      <td>&nbsp;</td>
      <td colspan="2"><textarea name="compDef" style='font-size:10.0pt;font-family:Arial' cols="44" rows="5" readonly><%=compDef%></textarea></td>
    </tr>
  </table>
&nbsp;

<div style='width:610px; height:259px; z-index:1; overflow:auto'>
<table width="593" border="1" style='font-size:10.0pt;font-family:Arial' bordercolor="#3399FF" bgcolor="#FFFFCC">
<th bgcolor="navy" width="10" bordercolor="#3399FF">&nbsp;
</th>
<th bgcolor="navy" width="10" bordercolor="#3399FF"><b><font span style='font-size:10.0pt;font-family:Arial;color:white'>
No</font></b></th>
<th bgcolor="navy" width="480" bordercolor="#3399FF"><a href="KeyBehaviour.jsp?name=1"><b><font span style='font-size:10.0pt;font-family:Arial;color:white'><u><%= trans.tslt("Key Behaviour") %></u></font></b></a></th>
<th bgcolor="navy" width="90" bordercolor="#3399FF"><a href="KeyBehaviour.jsp?name=2"><b><font style='font-size:10.0pt;font-family:Arial;color:white'><u><%= trans.tslt("Origin") %></u></font></b></a></th>

<% 	
			/********************
			* Edited by James 30 Oct 2007
			************************/
	//while(KBResult.next()) {		
		
	for(int i=0; i<KBResult.size(); i++) {
		voKeyBehaviour voKB = (voKeyBehaviour)KBResult.elementAt(i);
		pkKB = voKB.getPKKeyBehaviour();		
		KeyB =  voKB.getKeyBehaviour();
		origin =  voKB.getDescription();
%>
   <tr onMouseOver = "this.bgColor = '#99ccff'"
    	onMouseOut = "this.bgColor = '#FFFFCC'">
       <td bordercolor="#3399FF">
	   		<input type="radio" name="radioKB" value=<%=pkKB%>>
	   </td>
	   	<td align="center" bordercolor="#3399FF">
	   		
   		  <% out.print(DisplayNo);%>&nbsp;</td>
	   <td bordercolor="#3399FF">
		<% out.print(KeyB); %>&nbsp;
	   </td>
	   <td align="center" bordercolor="#3399FF">
		<% out.print(origin); %>&nbsp;
	   </td>
   </tr>
<% 	DisplayNo++;
	} 
	
%>
</table>
</div>

<p></p>
<font face="Arial">
<input type="button" name="Add" value="<%= trans.tslt("Add") %>" onclick="confirmAdd(this.form)">
<input type="button" name="btnEdit" value="<%= trans.tslt("Edit") %>"  onclick = "confirmEdit(this.form, this.form.radioKB)">
<input type="button" name="Submit" value="<%= trans.tslt("Delete") %>"  onclick = "return confirmDelete(this.form, this.form.radioKB)">
</font>
</form>
<%
	if(request.getParameter("delete") != null) {
		int pkKB1 = Integer.parseInt(request.getParameter("delete"));
		int check = KB.CheckSysLibKB(pkKB1);

		int totalKB = KB.totalKB(pkKB1);
		int userType = logchk.getUserType();	// 1= super admin
		if((userType == 1) || (check == 0)) {
		if(totalKB > 3) {
			
				boolean delete = KB.deleteRecord(pkKB1, pkUser);
				
			if(delete) {
			
%>
		<script>
			alert("Deleted successfully");
			window.location.href = "KeyBehaviour.jsp";
		</script>
<%
			}else{
			
			%>	
<script>
		alert("<%=trans.tslt("Deletion cannot be performed. Data is being used")%>!");
</script>
<%		
		}
		} else {
%>
		<script>
			alert("<%=trans.tslt("The total number of Key Behaviour in each Competency cannot be less than 3")%>!");
			//window.location.href = "KeyBehaviour.jsp";
		</script>		
<%		
		}
		} else if((userType != 1) && check == 1){
%>
		<script>
			alert("<%=trans.tslt("You are not allowed to delete System Generated Libraries")%>!");
			//window.location.href = "Competency.jsp";
		</script>
<%	
	}
	
		
		
	}
	}
%>

<p></p>
<%@ include file="Footer.jsp"%>

</body>
</html>