<%// Author: Dai Yong in June 2013%>
<%@ page import = "java.sql.*" %>
<%@ page import = "java.io.*" %>
<%@ page import = "java.util.*" %>
<%@ page import = "CP_Classes.vo.*" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>

<title>AddCoachingPerid</title>
<%@ page pageEncoding="UTF-8"%>
<meta http-equiv="Content-Type" content="text/html">
<style type="text/css">
<!--
body {
	background-color: #eaebf4;
}
-->
</style></head>

<body style="background-color: #DEE3EF">
<jsp:useBean id="Database" class="CP_Classes.Database" scope="session"/>
<jsp:useBean id="logchk" class="CP_Classes.Login" scope="session"/>   
<jsp:useBean id="DateGroup" class="Coach.CoachDateGroup"scope="session" />
<jsp:useBean id="LoginStatus" class="Coach.LoginStatus" scope="session" />
<script language = "javascript">
function confirmAdd(form)
{
	if(form.Name.value != "") {
		if(confirm("Add coaching perid?")) {
			form.action = "AddDateGroup.jsp?add=1";
			form.method = "post";
			form.submit();
			return true;
		}else
			return false;
	} else {
		if(form.Name.value == "") {
			alert("Please enter coaching peirod name");
			form.Name.focus();
		}
		return false;
	}
	return true;
}

function cancelAdd()
{
	window.close();
}
</script>

<%
String username=(String)session.getAttribute("username");

  if (!logchk.isUsable(username)) 
  {
%> <font size="2">
   
<script>
parent.location.href = "../index.jsp";
</script>
<%  } 
  else 
  { 
	
	if(request.getParameter("add") != null) {
		if(request.getParameter("Name") != null)	{
  			String name = request.getParameter("Name");
  			String des="";
  			if(request.getParameter("Description") != null){
  				des=request.getParameter("Description");
  			}
  			

			// check whether DateGroup name already exists in database
  			 Boolean Exist = false;
 		    
			//System.out.println("New Schedule Name:"+name);
 			Vector v =DateGroup.getAllDateGroup();
 			for(int i = 0; i < v.size(); i++){
 				voCoachDateGroup vo = (voCoachDateGroup)v.elementAt(i);
 				
 				String Name = vo.getName();
 				//System.out.println("Existing Schedule Name:"+Name);
 				if(name.trim().equalsIgnoreCase((Name.trim()))){
 					Exist = true;
 					System.out.println("Same Coaching Period Name");
 				}

 			}

			
			if(!Exist) {						
				try{					
					boolean add =DateGroup.addDateGroup(name, des);
					
					if(add){
						
						%>
						<script>
						alert("Coaching period successfully added");
						opener.location.href = 'DateGroup.jsp';
						window.close();
						</script>
						<% 
					}
					else{
						
					}
				}catch(Exception SE) {
                     System.out.println(SE);
				}
			} else {			
%>
	<script>
  		alert("Coaching Period Name exists");
		window.location.href='AddDateGroup.jsp';
	</script>
<%			
			}

	}
	}
%>	

<form name="Add" method="post">
	<p>	
		<br>
			<b><font color="#000080" size="3" face="Arial">Add Coaching Period</font></b>
		<br>
	</p>
		<br>
  <table border="0" width="480"  font span style='font-size:10.0pt;font-family:Arial'>
    <tr>
      <td width="150" height="33">Name:</td>
      <td width="10" height="33">&nbsp;</td>
      <td width="240" height="33">
    	<input name="Name" type="text"  style='font-size:10.0pt;font-family:Arial' id="Name" size="30" maxlength="50">
	  </td>
    </tr>
    <tr>
      <td width="150" height="33">Description:</td>
      <td width="10" height="33">&nbsp;</td>
      <td width="240" height="33">
    	<input name="Description" type="text"  style='font-size:10.0pt;font-family:Arial' id="Description" size="30" maxlength="200">
	  </td>
    </tr>
    <tr>
      <td width="82" height="12"></td>
      <td width="10" height="12"></td>
      <td width="303" height="12"></td>
    </tr>
   
  </table>
  <blockquote>
    <blockquote>
      <p>
		<font face="Arial">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</font>		<font face="Arial" span style="font-size: 10.0pt; font-family: Arial">		
	        <input class="btn btn-primary" type="button" name="Submit" value="Submit" onClick="confirmAdd(this.form)"></font><font span style='font-family:Arial'>
		</font>
			<font face="Arial">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
        </font>
		<font face="Arial" span style="font-size: 10.0pt; font-family: Arial">
			<input name="Cancel" class="btn btn-primary" type="button" id="Cancel" value="Cancel" onClick="cancelAdd()">
			</font> </p>
    </blockquote>
  </blockquote>
</form>
<% } %>
</body>
</html>