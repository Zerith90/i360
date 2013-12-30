<%@ page pageEncoding="UTF-8"%>
<%// by lydia Date 05/09/2008 Fix jsp file to support Thai language %>

<script language="JavaScript">
<!--
function FP_swapImg() {//v1.0
 var doc=document,args=arguments,elm,n; doc.$imgSwaps=new Array(); for(n=2; n<args.length;
 n+=2) { elm=FP_getObjectByID(args[n]); if(elm) { doc.$imgSwaps[doc.$imgSwaps.length]=elm;
 elm.$src=elm.src; elm.src=args[n+1]; } }
}

function FP_preloadImgs() {//v1.0
 var d=document,a=arguments; if(!d.FP_imgs) d.FP_imgs=new Array();
 for(var i=0; i<a.length; i++) { d.FP_imgs[i]=new Image; d.FP_imgs[i].src=a[i]; }
}

function FP_getObjectByID(id,o) {//v1.0
 var c,el,els,f,m,n; if(!o)o=document; if(o.getElementById) el=o.getElementById(id);
 else if(o.layers) c=o.layers; else if(o.all) el=o.all[id]; if(el) return el;
 if(o.id==id || o.name==id) return o; if(o.childNodes) c=o.childNodes; if(c)
 for(n=0; n<c.length; n++) { el=FP_getObjectByID(id,c[n]); if(el) return el; }
 f=o.forms; if(f) for(n=0; n<f.length; n++) { els=f[n].elements;
 for(m=0; m<els.length; m++){ el=FP_getObjectByID(id,els[n]); if(el) return el; } }
 return null;
}
// -->
</script>
<%@ page import="java.sql.*,
                 java.io.*,
				 java.util.*,
                 java.lang.String"%>   
<jsp:useBean id="logchk" class="CP_Classes.Login" scope="session"/>                   
<jsp:useBean id="CE_Survey" class="CP_Classes.Create_Edit_Survey" scope="session"/>
<jsp:useBean id="assignTR" class="CP_Classes.AssignTarget_Rater" scope="session"/>
<jsp:useBean id="user" class="CP_Classes.User" scope="session"/>
<jsp:useBean id="setting" class="CP_Classes.Setting" scope="session"/>
<jsp:useBean id="trans" class="CP_Classes.Translate" scope="session"/>
<jsp:useBean id="department" class="CP_Classes.Department" scope="session"/>
<jsp:useBean id="group" class="CP_Classes.Group" scope="session"/>
<jsp:useBean id="division" class="CP_Classes.Division" scope="session"/>
<%@ page import="CP_Classes.vo.voGroup"%>
<%@ page import="CP_Classes.vo.voDepartment"%>
<%@ page import="CP_Classes.vo.voDivision"%>
<%@ page import="CP_Classes.vo.votblOrganization"%>
<%@ page import="CP_Classes.vo.voUserType"%>
<%@ page import="CP_Classes.vo.voUser"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html">
</head>
<SCRIPT LANGUAGE="JavaScript">
var x = parseInt(window.screen.width) / 2 - 385;  // the number 250 is the exact half of the width of the pop-up and so should be changed according to the size of the pop-up
var y = parseInt(window.screen.height) / 2 - 235;  // the number 125 is the exact half of the height of the pop-up and so should be changed according to the size of the pop-up

function goToURL() { window.location = "UserList.jsp"; }

function AssignRelation(form)
{
	//window.document.UserEdit.action="UserEdit.jsp?AssignRelation=1";
	//window.document.UserEdit.method="post";
	//window.document.UserEdit.submit();	
	//alert("LoginID = " + window.document.UserEdit.txtLoginID.value);
	
	var myWindow=window.open('AssignRelationUserList.jsp','windowRef','scrollbars=no, width=770, height=470');
	myWindow.moveTo(x,y);
	myWindow.location.href = 'AssignRelationUserList.jsp?PageOpener=UserEdit.jsp';
}

/****************************
** Created Eric Lu 14/5/08 **
****************************/

function confirmUpdate() {
	if (confirm("Update user?")) {
		return validate();
	} else {
		return false;
	}
}

function validate()
{

//By Hemilda Date 06/06/2008 Change Prompt message
    x = document.UserEdit;
    if (x.txtEmail.value == "")
    {
	alert("<%=trans.tslt("Enter Email")%>");
	return false 
	}
	if (x.txtEmail.value != "")
	{
      	if(emailCheck(x.txtEmail.value) == false)
      	return false
    }
    if (x.txtLoginID.value == "")
    {
	alert("<%=trans.tslt("Enter Login ID")%>");
	return false 
	}
	if (x.txtFamilyName.value == "")
    {
	alert("<%=trans.tslt("Enter Family Name")%>");
	return false 
	}
	if (x.txtGivenName.value == "")
    {
	alert("<%=trans.tslt("Enter Given Name")%>");
	return false 
	}
	if (x.txtDesignation.value == "")
    {
	alert("<%=trans.tslt("Enter Designation")%>");
	return false 
	}
	if (x.txtIDNumber.value == "")
    {
	alert("<%=trans.tslt("Enter ID Number")%>");
	return false 
	}	
	if (x.selDivision.selectedIndex <=0 )
    {
	alert("<%=trans.tslt("Select Division")%>");
	return false 
	}    
	if (x.selDepartment.selectedIndex <=0 )
    {
	alert("<%=trans.tslt("Select Department")%>");
	return false 
	} 
	if (x.selGroup.selectedIndex <=0 )
    {
	alert("<%=trans.tslt("Select Group")%>");
	return false 
	} 
	if (x.selUserType.value == "")
	{
	alert("<%=trans.tslt("Select User Type")%>");
	return false 
	}
//By Hemilda Date 09/06/2008 Check format password
	if (x.txtPassword.value == "") {
		alert("<%=trans.tslt("Enter Password")%>");
		return false;
	}  
	if (x.txtPassword.value.length < 4) {
		alert("<%=trans.tslt("Password must be at least 4 characters long")%>");
		return false;
	} 	

    if (x.txtSup.value == "")
    {
	alert("<%=trans.tslt("Supervisor is not assigned")%>");
	return true 
	}	
    return true;
    
}

function emailCheck (emailStr) {

/* The following variable tells the rest of the function whether or not
to verify that the address ends in a two-letter country or well-known
TLD.  1 means check it, 0 means don't. */

var checkTLD=1;

/* The following is the list of known TLDs that an e-mail address must end with. */

var knownDomsPat=/^(com|net|org|edu|int|mil|gov|arpa|biz|aero|name|coop|info|pro|museum)$/;

/* The following pattern is used to check if the entered e-mail address
fits the user@domain format.  It also is used to separate the username
from the domain. */

var emailPat=/^(.+)@(.+)$/;

/* The following string represents the pattern for matching all special
characters.  We don't want to allow special characters in the address. 
These characters include ( ) < > @ , ; : \ " . [ ] */

var specialChars="\\(\\)><@,;:\\\\\\\"\\.\\[\\]";

/* The following string represents the range of characters allowed in a 
username or domainname.  It really states which chars aren't allowed.*/

var validChars="\[^\\s" + specialChars + "\]";

/* The following pattern applies if the "user" is a quoted string (in
which case, there are no rules about which characters are allowed
and which aren't; anything goes).  E.g. "jiminy cricket"@disney.com
is a legal e-mail address. */

var quotedUser="(\"[^\"]*\")";

/* The following pattern applies for domains that are IP addresses,
rather than symbolic names.  E.g. joe@[123.124.233.4] is a legal
e-mail address. NOTE: The square brackets are required. */

var ipDomainPat=/^\[(\d{1,3})\.(\d{1,3})\.(\d{1,3})\.(\d{1,3})\]$/;

/* The following string represents an atom (basically a series of non-special characters.) */

var atom=validChars + '+';

/* The following string represents one word in the typical username.
For example, in john.doe@somewhere.com, john and doe are words.
Basically, a word is either an atom or quoted string. */

var word="(" + atom + "|" + quotedUser + ")";

// The following pattern describes the structure of the user

var userPat=new RegExp("^" + word + "(\\." + word + ")*$");

/* The following pattern describes the structure of a normal symbolic
domain, as opposed to ipDomainPat, shown above. */

var domainPat=new RegExp("^" + atom + "(\\." + atom +")*$");

/* Finally, let's start trying to figure out if the supplied address is valid. */

/* Begin with the coarse pattern to simply break up user@domain into
different pieces that are easy to analyze. */

var matchArray=emailStr.match(emailPat);

if (matchArray==null) {

/* Too many/few @'s or something; basically, this address doesn't
even fit the general mould of a valid e-mail address. */

alert("<%=trans.tslt("Email address seems incorrect")%> (<%=trans.tslt("check")%> @ <%=trans.tslt("and")%> .'s)");
return false;
}
var user=matchArray[1];
var domain=matchArray[2];

// Start by checking that only basic ASCII characters are in the strings (0-127).

for (i=0; i<user.length; i++) {
if (user.charCodeAt(i)>127) {
alert("<%=trans.tslt("The username contains invalid characters")%>.");
return false;
   }
}
for (i=0; i<domain.length; i++) {
if (domain.charCodeAt(i)>127) {
alert("<%=trans.tslt("The domain name contains invalid characters")%>.");
return false;
   }
}

// See if "user" is valid 

if (user.match(userPat)==null) {

// user is not valid

alert("<%=trans.tslt("The username doesn't seem to be valid")%>.");
return false;
}

/* if the e-mail address is at an IP address (as opposed to a symbolic
host name) make sure the IP address is valid. */

var IPArray=domain.match(ipDomainPat);
if (IPArray!=null) {

// this is an IP address

for (var i=1;i<=4;i++) {
if (IPArray[i]>255) {
alert("<%=trans.tslt("Destination IP address is invalid")%>!");
return false;
   }
}
return true;
}

// Domain is symbolic name.  Check if it's valid.
 
var atomPat=new RegExp("^" + atom + "$");
var domArr=domain.split(".");
var len=domArr.length;
for (i=0;i<len;i++) {
if (domArr[i].search(atomPat)==-1) {
alert("<%=trans.tslt("The domain name does not seem to be valid")%>.");
return false;
   }
}

/* domain name seems valid, but now make sure that it ends in a
known top-level domain (like com, edu, gov) or a two-letter word,
representing country (uk, nl), and that there's a hostname preceding 
the domain or country. */

if (checkTLD && domArr[domArr.length-1].length!=2 && 
domArr[domArr.length-1].search(knownDomsPat)==-1) {
alert("<%=trans.tslt("The address must end in a well-known domain or two letter country")%>.");
return false;
}

// Make sure there's a host name preceding the domain.

if (len<2) {
alert("<%=trans.tslt("This address is missing a hostname")%>!");
return false;
}

// If we've gotten this far, everything's valid!
return true;

}

function populateDept(form, field)
{
	form.action="UserEdit.jsp?div="+field.value;
	form.method="post";
	form.submit();
}

function populateGrp(form, field1, field2)
{
	form.action="UserEdit.jsp?div=" + field1.value + "&dept="+ field2.value;
	form.method="post";
	form.submit();
}


</script>
<body onload="FP_preloadImgs(/*url*/'images/btnAssign2.jpg',/*url*/'images/btnAssign3.jpg')">
<%
response.setHeader("Pragma", "no-cache");
response.setHeader("Cache-Control", "no-cache");
response.setDateHeader("expires", 0);
boolean rePopulate = false;
String username=(String)session.getAttribute("username");

  if (!logchk.isUsable(username)) 
  {%> <font size="2">
	<script>
	parent.location.href = "index.jsp";
	</script>
<%  } 
  else 
  { 
  	String LoginID =" ";
	String FamilyName=" ";
	String GivenName = " ";
	String Designation =" ";
	String IDNumber=" ";
	int Division =0;
	int Department=0;
	int Group=0;
	int UserType=0;
	String dis=" ";
	String email=" ";
  	String password=" ";
  	int round = 0;
  	String offTel = " ";
  	String handphone = " ";
  	String remark = " ";
  	String mobileProvider = " ";
  	
	if(request.getParameter("AssignRelation") != null)
	{
		if(Integer.parseInt(request.getParameter("AssignRelation")) == 1)
		{
			//Send Assign request
		%>
		<script>
			var myWindow=window.open('AssignRelationUserList.jsp','windowRef','scrollbars=no, width=770, height=470');
			myWindow.moveTo(x,y);
		    myWindow.location.href = 'AssignRelationUserList.jsp';
		</script>
		<%
		}
	}

if(request.getParameter("btnEdit") != null)	
{
	try
	{	
		int disable = 1;
		int iPKUserID2 = Integer.parseInt(request.getParameter("hdnSupID")); //User ID for second person in tblUserRelation
		LoginID = request.getParameter("txtLoginID");
		FamilyName = request.getParameter("txtFamilyName");
		GivenName = request.getParameter("txtGivenName");
		Designation = request.getParameter("txtDesignation");
		IDNumber = request.getParameter("txtIDNumber");
		Division = new Integer(request.getParameter("selDivision")).intValue();
		Department = new Integer(request.getParameter("selDepartment")).intValue();
		Group = new Integer(request.getParameter("selGroup")).intValue();
		UserType = new Integer(request.getParameter("selUserType")).intValue();
		dis = request.getParameter("cliDisabled");
		int PKUser = new Integer(request.getParameter("txthidden")).intValue();
		email = request.getParameter("txtEmail");
		offTel = request.getParameter("txtOffTel");
	  	handphone = request.getParameter("txtHandphone");
	  	remark = request.getParameter("txtRemark");
	  	mobileProvider = request.getParameter("txtMobileProvider");
		if (!(request.getParameter("txtRound")).equals(""))
			round = new Integer(request.getParameter("txtRound")).intValue();

		//By Hemilda date  06/06/2008 cek if user exist for edit
		if(user.checkLoginIdExist_Edit(LoginID, logchk.getOrg(),PKUser) == false)
		{
			if((logchk.getUserType() == 1))
				password = request.getParameter("txtPassword");
			
			int CompanyID = 1;
		
			if(dis != null)
			{
				disable = 0;
			}
			
			System.out.println("PKUser=" + PKUser + "\niPKUserID2=" + iPKUserID2);
			//Edit By James 7-July 2008
			if (iPKUserID2 == -1 || iPKUserID2 == 0)// add iPKUserID2 == 0
			{	
				System.out.println("Supervisor = -1 (" + iPKUserID2 + ")");
				user.updateNoRelation(PKUser);
			}
			else
			{
				System.out.println("James Supervisor != -1 (" + iPKUserID2 + ")");
				System.out.println(password);
				user.updateRelation(PKUser, iPKUserID2, 1);
			}
	
			if(logchk.getUserType() == 1)
				if(!(request.getParameter("txtRound")).equals(""))
					user.editRecord( user.get_selectedUser(),Department,Division,UserType,FamilyName,GivenName,LoginID,Designation,IDNumber,Group,disable,email,offTel, handphone, mobileProvider, remark, logchk.getPKUser(),password, round);
				else
					user.editRecord( user.get_selectedUser(),Department,Division,UserType,FamilyName,GivenName,LoginID,Designation,IDNumber,Group,disable,email,offTel, handphone, mobileProvider, remark, logchk.getPKUser(),password);
			else
				if(!(request.getParameter("txtRound")).equals(""))
					user.editRecord( user.get_selectedUser(),Department,Division,UserType,FamilyName,GivenName,LoginID,Designation,IDNumber,Group,disable,email,offTel, handphone, mobileProvider, remark, logchk.getPKUser(), round);
				else
					user.editRecord( user.get_selectedUser(),Department,Division,UserType,FamilyName,GivenName,LoginID,Designation,IDNumber,Group,disable,email,offTel, handphone, mobileProvider, remark, logchk.getPKUser());
		   if (assignTR.iftargetassignmodified(user.get_selectedUser()))
		   {
	    %>
	         <script>
	        alert("<%=trans.tslt("User detail(s) has been modified. Please go to Processing -> Assign Target/Rater to update assignments as well.")%>");
	        </script> <%} %> 
	        
			    <script>
			    	alert("<%=trans.tslt("Updated successfully")%>");
		// *********Edited by Tracy on 08 Aug 08**************
		// Transfer to UserList page after editing user details
			    	//location.href('UserSearch.jsp');
			    	//location.href('UserList.jsp');
			    	location.href='UserList.jsp';
	   // ****************************************************
			    </script>
		<%
			}else{%>
				<script>
					alert("<%=trans.tslt("User exists")%>");
				</script>
	<%		}	
	}catch (SQLException sqle) 
    {

		%>
			<script>
				alert("<%=trans.tslt("Existing User")%>");
			</script>
<%	}	
}	
	
	if(request.getParameter("div") != null)
	{
		if(request.getParameter("div").length() > 0)
		{
			Division = new Integer(request.getParameter("div")).intValue();
			rePopulate = true;
		}
	}
	
	if(request.getParameter("dept") != null)
	{	
		if(request.getParameter("dept").length() > 0)
		{
			Department = new Integer(request.getParameter("dept")).intValue();
			rePopulate = true;
		}
	}
	
	if(request.getParameter("selGroup") != null)
	{	
		if(request.getParameter("selGroup").length() > 0)
		{
			Group = new Integer(request.getParameter("selGroup")).intValue();
			rePopulate = true;
		}
	}
	
	if(request.getParameter("selUserType") != null)
	{	
		if(request.getParameter("selUserType").length() > 0)
		{
			UserType = new Integer(request.getParameter("selUserType")).intValue();
			rePopulate = true;
		}
	}
	
	if(rePopulate)
	{
		LoginID = request.getParameter("txtLoginID");
		FamilyName = request.getParameter("txtFamilyName");
		GivenName = request.getParameter("txtGivenName");
		Designation = request.getParameter("txtDesignation");
		IDNumber = request.getParameter("txtIDNumber");
		UserType = new Integer(request.getParameter("selUserType")).intValue();
		dis = request.getParameter("cliDisabled");
		int PKUser = new Integer(request.getParameter("txthidden")).intValue();
		email = request.getParameter("txtEmail");
		offTel = request.getParameter("txtOffTel");
	  	handphone = request.getParameter("txtHandphone");
	  	remark = request.getParameter("txtRemark");
	  	mobileProvider = request.getParameter("txtMobileProvider");						
		if (!(request.getParameter("txtRound")).equals(""))
			round = new Integer(request.getParameter("txtRound")).intValue();
	}
	
%>
<form name="UserEdit" action="UserEdit.jsp" onSubmit="return confirmUpdate()">




<table border="0" width="100%" cellspacing="0" cellpadding="0">
	<tr>
		<td>
		<b><font face="Arial" size="2" color="#000080"><%=trans.tslt("Edit User Detail")%></font></b></td>
		</tr>
	<tr>
		<td>
<font size="2" face="Arial">
   
<ul>
	<li><%=trans.tslt("Fill up all the necessary fields")%>.</li>
	<li><%=trans.tslt("To Update, click on the Update User button")%>.</li>
</ul>
		</td>
		</tr>
	</table>
<table border="0" width="100%" cellspacing="0" cellpadding="0">
	<tr>
		<td>&nbsp;</td>
	</tr>
</table>
<table border="1" width="36%" bordercolor="#3399FF" bgcolor="#FFFFCC">
<%
	String [] sDetail =new String[17];
	int PKUser = user.get_selectedUser();
	sDetail = user.getUserDetailWithRound(PKUser);
	%>

	<input type="hidden" name="txthidden" value=<%=PKUser%>>
	<tr>
		<td colspan="2" bgcolor="#000080">
		<p align="center"><b><font face="Arial" color="#FFFFFF" size="2"><%=trans.tslt("User Detail")%></font></b></td>
		
	</tr>
	<%  // Codes added to check whether user to be edited is Admin, if yes - textbox set to readonly
		// Mark Oei 09 Mar 2010
		if ((logchk.getOrgCode().equals("PCC"))) { %>

	<tr>
	<td width="36%" align="left"><font face="Arial" size="2">
		<%=trans.tslt("Login ID")%>:</font></td>
		<td align="left" width="59%">
		<font face="Arial"><span style="font-size: 11pt">
		<input name="txtLoginID" size="20" style="float: left;" value="<%if (!rePopulate){out.println(sDetail[2]);} else{out.println(LoginID);}%>"></span></font></td>
	</tr>
	<% } else { %>
	<tr>
		<td width="36%" align="left"><font face="Arial" size="2">
		<%=trans.tslt("Login ID")%>:</font></td>
		<td align="left" width="59%">
		<font face="Arial"><span style="font-size: 11pt">
		<input name="txtLoginID" size="20" style="float: left; background-color: #99ccff;" readOnly value="<%if (!rePopulate){out.println(sDetail[2]);} else{out.println(LoginID);}%>"></span></font></td>
	</tr>
<% } // End of Admin check %>
	<tr>
		<td width="36%" align="left"><font face="Arial" size="2">
		<%=trans.tslt("Family Name")%>:</font></td>
		<td align="left" width="59%"><font face="Arial">
		<span style="font-size: 11pt">
		<input type="text" name="txtFamilyName" size="20" value="<%if (!rePopulate){out.println(sDetail[0]);} else{out.println(FamilyName);}%>"></span></font></td>
	</tr>
	<tr>
		<td width="36%" align="left"><font face="Arial" size="2">
		<%=trans.tslt("Given Name")%>:</font></td>
		<td align="left" width="59%"><font face="Arial">
		<span style="font-size: 11pt">
		<input type="text" name="txtGivenName" size="20" value="<%if (!rePopulate){out.println(sDetail[1]);} else{out.println(GivenName);}%>"></span></font></td>
	</tr>
	<tr>
		<td width="36%" align="left"><font face="Arial" size="2">
		<%=trans.tslt("ID Number")%>:</font></td>
		<td align="left" width="59%"><font face="Arial">
		<span style="font-size: 11pt">
		<input type="text" name="txtIDNumber" size="20" value="<%if (!rePopulate){out.println(sDetail[4]);} else{out.println(IDNumber);}%>"></span></font></td>
	</tr>
	<tr>
		<td width="36%" align="left"><font face="Arial" size="2">
		<%=trans.tslt("Designation")%>:</font></td>
		<td align="left" width="59%"><font face="Arial">
		<span style="font-size: 11pt">
		<% System.out.println("Designation = " + sDetail[3]);%>
		<input type="text" name="txtDesignation" size="20" value="<%if (!rePopulate){out.println(sDetail[3]);} else{out.println(Designation);}%>"></span></font></td>
	</tr>
	<tr>
		<td width="36%" align="left"><font face="Arial" size="2">
		<%=trans.tslt("Email")%>:</font></td>
		<td align="left" width="59%"> <font size="2">
   
		<font face="Arial">
		<input type="text" name="txtEmail" size="27" value="<%if (!rePopulate){out.println(sDetail[12]);} else{out.println(email);}%>"></span></font></td>
   		</tr>
	<tr>
		<td width="36%" align="left"><font face="Arial" size="2">
		<%=trans.tslt("Division")%>:</font></td>
		<td align="left" width="59%"><font face="Arial">
		<span style="font-size: 11pt">
			<select size="1" name="selDivision" onChange="populateDept(this.form, this.form.selDivision)">
				<option value="-1" selected></option>
<%
	/***********************
	* Edited by Yuni 14 Nov 2007
	************************/
	Vector v = division.getAllDivisions(logchk.getOrg());
	
	for(int i=0; i<v.size(); i++) {
		voDivision vo = (voDivision)v.elementAt(i);

		int div_ID = vo.getPKDivision();
		String div_Name = vo.getDivision();
		
		if(request.getParameter("div") == null)
		{
			if(sDetail[7].equals(div_Name))
			{
%>				<option value=<%=div_ID%> selected><%=div_Name%></option>
<%				
			}
			else
			{
%>			<option value=<%=div_ID%>><%=div_Name%></option>
<%			}
		}
		else
		{
			String divisionID = request.getParameter("div");
			if(divisionID.length() > 0 && div_ID == Integer.parseInt(divisionID))
			{
%>				<option value=<%=div_ID%> selected><%=div_Name%></option>
<%			}
			else
			{
%>				<option value=<%=div_ID%>><%=div_Name%></option>
	<%		}
		}
}	%>
			</select></span></font></td>
	</tr>
	<tr>
		<td width="36%" align="left"><font face="Arial" size="2">
		<%=trans.tslt("Department")%>:</font></td>
		<td align="left" width="59%"><font face="Arial">
		<span style="font-size: 11pt"><select size="1" name="selDepartment" onChange="populateGrp(this.form, this.form.selDivision, this.form.selDepartment)">
				<option value="-1" selected></option>
<%
	int div = 0;
	if(request.getParameter("div") != null)
	{
		String divID = request.getParameter("div");
		if(divID.length() > 0)
		{
			div = Integer.parseInt(divID);
		}
	}
	else
	{
		div =  Integer.parseInt(sDetail[14]);
	}
	
	
	/********************
	* Edited by Yuni 15 Nov 2007
	************************/
	 Vector vDepartments = department.getAllDepartments(logchk.getOrg(),div);
	 for(int i=0; i<vDepartments.size(); i++) { 
	
	
		voDepartment voD = (voDepartment)vDepartments.elementAt(i);
        
		int dep_ID=voD.getPKDepartment();
		String dep_Name =voD.getDepartmentName();
		
		String departmentID = request.getParameter("dept");
		
		if (departmentID != null)
		{
			if(departmentID.length() > 0 && dep_ID == Integer.parseInt(departmentID))
			{
%>				<option value=<%=dep_ID%> selected><%=dep_Name%></option>
<%			}
			else
			{
%>				<option value=<%=dep_ID%>><%=dep_Name%></option>
	<%		}
		}
		else
		{
			//if (request.getParameter("div") == null)
			//{
			/*
			 * Change : get the ID directly not using checkDeptExists
			 * Reason : because many duplicate name "NA"
			 * Add by : Johanes
			 * Add on : 26/10/2009
			 */	
			int deptID = Integer.parseInt(sDetail[13]);
				//int deptID = department.checkDeptExist(sDetail[6]);
				System.out.println("tes" + deptID);
				if(dep_ID == deptID)
				{
%>					<option value="<%=deptID%>" selected><%=sDetail[6]%></option>
<%				}
				else
				{
%>					<option value=<%=dep_ID%>><%=dep_Name%></option>
<%				}
			//}
		}
}
%>
		
		</select></span></font></td>
	</tr>
	<tr>
		<td width="36%" align="left"><font face="Arial" size="2">
		<%=trans.tslt("Group/Section")%>:</font></td>
		<td align="left" width="59%"><font face="Arial"><select size="1" name="selGroup">
			<option value='' selected>&nbsp</option>
<%
	int dept = 0;
	if (request.getParameter("dept") != null)
	{
		if(request.getParameter("dept").length() > 0)
		{
			dept = Integer.parseInt(request.getParameter("dept"));
		}
	}
	else
	{
		//dept =  department.checkDeptExist(sDetail[6]);
		dept =  Integer.parseInt(sDetail[13]);
	}
	
	
	/********************
	* Edited by Yuni 15 Nov 2007
	************************/
	
	 Vector vGroup = group.getAllGroups(logchk.getOrg(),dept);
	 for(int i=0; i<vGroup.size(); i++) { 
	   
		voGroup voG = (voGroup)vGroup.elementAt(i);      
		
		int Group_ID = voG.getPKGroup();
		String GroupName = voG.getGroupName();
		
		/*
		 * Change : Check by the id not by name
		 * Reason : because many duplicate name "NA"
		 * Add by : Johanes
		 * Add on : 26/10/2009
		 */	
		//if(sDetail[9].equals(GroupName))
			if(Integer.parseInt(sDetail[15]) == Group_ID)
		{
%>		
		<option value=<%=Group_ID%> selected><%=GroupName%></option>
<%		}
		else
		{
	%>		
		<option value=<%=Group_ID%>><%=GroupName%></option>
<%		}
	}	%>

		</select></font></td>
	</tr>
	<tr>
		<td width="36%" align="left"><font face="Arial" size="2">
		<%=trans.tslt("User Type")%>:</font></td>
		<td align="left" width="59%"><font face="Arial">
		<span style="font-size: 11pt">
		<% 	// Added to check whether it is PCC, if yes, allow to change to any usertype
			// Mark Oei 09 Mar 2010
		if (logchk.getOrgCode().equals("PCC")){ %>
			<select size="1" name="selUserType">
		<%
			 Vector vUser = user.getAllUsers();
			 for(int i=0; i<vUser.size(); i++) { 
			   
				voUserType voU = (voUserType)vUser.elementAt(i);      
				
				int type_ID  =voU.getPKUserType();
		        String type_Name = voU.getUserTypeName();
				if (sDetail[8].equals(type_Name)) { %>		
					<option value=<%=type_ID%> selected><%=type_Name%></option>
				<%	} else { %>		
					<option value=<%=type_ID%>><%=type_Name%></option>
				<%	}
			 }	%>	// End of for loop 
			 	</select></span></font></td>
		<% 
		} else {
			// Check whether it is Administrator account, if yes, not allow to change
			// Mark Oei 09 Mar 2010
			if (sDetail[8].equals("Administrator")){ %>
				<select size="1" name="selUserType">
				<option value=6 selected>Administrator</option>
				</selected>
			<% } else { %>
				<select size="1" name="selUserType">
				<option value='' selected>&nbsp</option>
<%
	/********************
	* Edited by Yuni 15 Nov 2007
	************************/
	
	 Vector vUser = user.getAllUsers();
	 for(int i=0; i<vUser.size(); i++) { 
	   
		voUserType voU = (voUserType)vUser.elementAt(i);      
		
		int type_ID  =voU.getPKUserType();
        String type_Name = voU.getUserTypeName();
        
    	/* Codes added to limit other organization from creating Administrator
    	 * accounts. Only PCC can create Administrator accounts
    	 * Mark Oei 05 Mar 2010
    	 */
		if (type_Name.equals("Administrator")){
			continue;
		} else {
			if (sDetail[8].equals(type_Name)) { %>		
				<option value=<%=type_ID%> selected><%=type_Name%></option>
		<%	} else { %>		
				<option value=<%=type_ID%>><%=type_Name%></option>
		<%	}
		} 	// End of Administrator check
	}	%>	// End of for loop 
		</select></span></font></td>
	<% } 	// End of Administrator check before loop
		} // End of PCC check %>
	</tr>

	<tr>
		<td width="36%" align="left"><font face="Arial" size="2"><%=trans.tslt("Supervisor")%></font><font face="Arial" style="font-size: 11pt">:</font></td>
		<td align="left" width="59%">

		<span style="font-size: 11pt">
		<font size="2">
   
		<font face="Arial" size="2">
		<%	
		voUser voSup = user.getSupervisorInfo(PKUser);
		if(voSup != null)
		{
		System.out.println("James 1: SupInformation ");
			int PKUser1 = voSup.getPKUser();
			String Supervisor = voSup.getSupervisorName(); 
			int RelationType = voSup.getRelationType();
		System.out.println("James 2: "+RelationType );	
			if ( RelationType == 0)
			{
				//No Relation
				%>
				
				<input type="hidden" name="hdnSupID" value="-1">
				<input type="text" name="txtSup" size="15" value="" readonly>
				<%
			}
			else
			{
				//Set Supervisor Textbox = record retrieved from DB
				%>
				<input type="hidden" name="hdnSupID" value="<%=PKUser1%>">
				<input type="text" name="txtSup" size="15" value="<%=Supervisor%>" readonly>
				<%
			}
		}
		else
		{
			//Set Supervisor Textbox = ""
			%>
			<input type="text" name="txtSup" size="15" value="" readonly>
			<input type="hidden" name="hdnSupID" value="-1">
			<%
		}
		%>
		</font></font></span><font face="Arial" size="2"><span style="font-size: 11pt"><img border="0" id="img1" src="images/btnAssign1.jpg" height="20" width="60" alt="Assign" onclick="AssignRelation(this.form)" fp-style="fp-btn: Glow Rectangle 1; fp-proportional: 0; fp-orig: 0" fp-title="Assign" onmouseover="FP_swapImg(1,0,/*id*/'img1',/*url*/'images/btnAssign3.jpg')" onmouseout="FP_swapImg(0,0,/*id*/'img1',/*url*/'images/btnAssign1.jpg')" onmousedown="FP_swapImg(1,0,/*id*/'img1',/*url*/'images/btnAssign2.jpg')" onmouseup="FP_swapImg(0,0,/*id*/'img1',/*url*/'images/btnAssign3.jpg')"></span></font></td>		
		
	</tr>

<%
if (setting.getPasswordSetting() == 1) //Password module enabled, show Password information
{%>
	<tr>
		<td width="36%" align="left"><font face="Arial" size="2">
		<%=trans.tslt("Password")%>:</font></td>
		<td align="left" width="59%"><font face="Arial">
		<font size="2">

		<span style="font-size: 11pt">
   
		<font face="Arial" size="2">
<%
		String [] sUser =new String[13];
		sUser = user.getUserDetail(PKUser, 0);
		
		if(setting.getCompanySetting() == 3) {
			if(logchk.getUserType() == 1)
			{	
%>
			<input type="text" name="txtPassword" size="15" value="<%=sUser[12]%>"></font></span>
<%			}
			else
			{
			out.println(sUser[12]);
			}

		} else {		
		
		
			if(logchk.getUserType() == 1)
			{	
%>
			<input type="password" name="txtPassword" size="15" value="<%=sUser[12]%>"></font></span>
<%			}
			else
			{ 	// Added to display actual password and make it readonly
				// Mark Oei 09 Mar 2010
%>
				<input type="password" name="txtPassword" STYLE="border: 0; background-color: #FFFFCC;" readonly size="15" value="<%=sUser[12]%>"></font></span>
<%			} %>

		</font></font></td>		
		
	</tr>
<%
		}

}

%>
	<!--  add new fields: office tel, handphone, remark, mbile provider -->
	<tr>
		<td width = "36%" align="left"><font face="Arial" size="2"> <%=trans.tslt("Office Tel")%>:</font></td>
		<td align="left" width="59"><font face="Arial">
		<span style="font-size: 11pt">
<%		
		if(sDetail[17] == null){
%>		<input type="text" name="txtOffTel" size="20" value=""></span></font></td>
<%		}else{
%>		<input type="text" name="txtOffTel" size="20" value="<%if (!rePopulate){out.println(sDetail[17]);} else{out.println(offTel);}%>"></span></font></td>
<%		}
%>	
	</tr>
	<tr>
		<td width = "36%" align="left"><font face="Arial" size="2"> <%=trans.tslt("Handphone")%>:</font></td>
		<td align="left" width="59"><font face="Arial">
		<span style="font-size: 11pt">
<%		
		if(sDetail[18] == null){
%>		<input type="text" name="txtHandphone" size="20" value=""></span></font></td>
<%		}else{
%>		<input type="text" name="txtHandphone" size="20" value="<%if (!rePopulate){out.println(sDetail[18]);} else{out.println(handphone);}%>"></span></font></td>
<%		}
%>	
	</tr>
	<tr>
		<td width = "36%" align="left"><font face="Arial" size="2"> <%=trans.tslt("Mobile Provider")%>:</font></td>
		<td align="left" width="59"><font face="Arial">
		<span style="font-size: 11pt">
<%		
		if(sDetail[19] == null){
%>		<select name="txtMobileProvider">
			<option value=""></option>
			<option value="SingTel">SingTel</option>
			<option value="StarHub">StarHub</option>
			<option value="M1">M1</option>
			<option value = "Others">Others</option>
		</select>
<%		}else{ 
%>		<select name="txtMobileProvider">
			<option value="SingTel" <%if(!rePopulate){if(sDetail[19].toLowerCase().equals("singtel")){ %>selected<%} else if(mobileProvider.toLowerCase().equals("singtel")) { %>selected<%};} %>>SingTel</option>
			<option value="StarHub" <%if(!rePopulate){if(sDetail[19].toLowerCase().equals("starhub")) { %>selected<%} else if(mobileProvider.toLowerCase().equals("starhub")) { %>selected<%}; }%>>StarHub</option>
			<option value="M1" <%if(!rePopulate){if(sDetail[19].toLowerCase().equals("m1")) { %>selected<%} else if(mobileProvider.toLowerCase().equals("m1")) { %>selected<%};} %>>M1</option>
			<option value="Others" <%if(!rePopulate){if(sDetail[19].toLowerCase().equals("others")) { %>selected<%} else if(mobileProvider.toLowerCase().equals("others")) { %>selected<%};} %>>Others</option>
		</select></span></font></td>

<%		}
%>	
	</tr>
	<tr>
		<td width = "36%" align="left"><font face="Arial" size="2"> <%=trans.tslt("Remark")%>:</font></td>
		<td align="left" width="59"><font face="Arial">
		<span style="font-size: 11pt">
<%		
		if(sDetail[20] == null){
%>		<textarea span style='font-family:Arial;font-size:10.0pt' name="txtRemark" cols="50" rows="5" id="textarea"></textarea></span></font></td>
<%		}else{
%>		<textarea span style='font-family:Arial;font-size:10.0pt' name="txtRemark" cols="50" rows="5" id="textarea"><%if (!rePopulate){out.println(sDetail[20]);} else{out.println(remark);}%></textarea></span></font></td>
<%		}
%>	
	</tr>



	<!-- Adding a new field round by Albert (1 June 2012)-->
	<tr>
		<td width="36%" align="left"><font face="Arial" size="2">
		<%=trans.tslt("Round")%>:</font></td>
		<td align="left" width="59%"><font face="Arial">
		<span style="font-size: 11pt">
<%
		if(sDetail[16]==null){
%>		<input type="text" name="txtRound" size="20" value="">
<%		} else{
%>		<input type="text" name="txtRound" size="20" value="<%if (!rePopulate){out.println(sDetail[16]);} else{out.println(round);}%>"></span></font></td>
<%		}
%>
		
	</tr>
	<tr>
		<td width="36%" align="left"><font face="Arial" size="2">
		<%=trans.tslt("Disabled")%>:</font></td>
		<td align="left" width="59%"><font face="Arial">
		<font size="2">
<%		
	if(!rePopulate)
		if(sDetail[5].equals("0"))
		{	%>
		</font>
		<span style="font-size: 11pt">
		<input type="checkbox" name="cliDisabled" value="ON" checked></span><font size="2">
<%		}	
		else
		{	%>
		</font>
		<span style="font-size: 11pt">
		<input type="checkbox" name="cliDisabled" value="ON"><font size="2">
	<%	}	
	else
	{
		if(request.getParameter("cliDisabled") != null && request.getParameter("cliDisabled").equalsIgnoreCase("on"))
		{
	%>		</font>
			<span style="font-size: 11pt">
			<input type="checkbox" name="cliDisabled" value="ON" checked><font size="2">
	<%	}
		else
		{
	%>		</font>
			<span style="font-size: 11pt">
			<input type="checkbox" name="cliDisabled" value="ON"><font size="2">
	<%	}
	}
	
	%>
		</font>
		</span></font></td>		
		
	</tr>
</table>
<table border="0" width="36%">
	<tr>
		<td width="195">&nbsp;</td>
		<td width="150" align="right">&nbsp;</td>
	</tr>
	<tr>
		<td width="195" align="right">
		<font size="2">
   
          <input type="button" value="<%=trans.tslt("Back")%>" name="back" style="float: right" onclick="goToURL()"></td>
		<td width="150" align="right">
		<input type="submit" value="<%=trans.tslt("Update User")%>" name="btnEdit"></td>
	</tr>
</table>
</form>
<%	}	%>
<table border="0" width="610" height="26">

	<tr>
   
		<td align="center" height="5" valign="top"><font size="1" color="navy" face="Arial">&nbsp;&nbsp;<a style="TEXT-DECORATION: none; color:navy;" href="Login.jsp">Home</a>&nbsp;| <a color="navy" face="Arial">&nbsp;<a style="TEXT-DECORATION: none; color:navy;" href="mailto:3SixtyProfiler@pcc.com.sg?subject=Regarding:">Contact 
		Us</a><a color="navy" face="Arial" href="termofuse.htm"><span style="color: #000080; text-decoration: none"> | Terms of Use </span></a>| <span style="color: #000080; text-decoration: none"><a style="TEXT-DECORATION: none; color:navy;" href="http://www.pcc.com.sg/" target="_blank">PCC Website</a></span></font></td></tr><tr>
   
		<td align="center" height="5" valign="top">
		<font size="1" color="navy" face="Arial">&nbsp;Copyright 
		? 2004 Pacific Century Consulting Pte Ltd. All Rights Reserved.</font></td></tr></table>
</body>
</html>