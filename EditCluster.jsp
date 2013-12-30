<%@ page import = "java.sql.*" %>
<%@ page import = "java.io.*" %>
<%@ page import = "java.util.*" %>
<%@ page import = "java.lang.*" %>
<%@ page import = "CP_Classes.vo.*" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>

<title>EditCluster</title>
<%@ page pageEncoding="UTF-8"%>
<meta http-equiv="Content-Type" content="text/html">
<%// by lydia Date 05/09/2008 Fix jsp file to support Thai language %>
<style type="text/css">
<!--
body {
	background-color: #eaebf4;
}
-->
</style></head>

<body style="background-color: #DEE3EF">
<jsp:useBean id="Database" class="CP_Classes.Database" scope="session"/>
<jsp:useBean id="Comp" class="CP_Classes.Competency" scope="session"/>
<jsp:useBean id="logchk" class="CP_Classes.Login" scope="session"/>    
<jsp:useBean id="trans" class="CP_Classes.Translate" scope="session"/>
<jsp:useBean id="Cluster" class="CP_Classes.Cluster" scope="session"/>
<script language = "javascript">
function confirmEdit(form)
{
	if(form.Name.value != "") {
		if(confirm("<%=trans.tslt("Edit Cluster")%>?")) {
			form.action = "EditCluster.jsp?edit=1";
			form.method = "post";
			form.submit();
			return true;
		}else
			return false;
	} else {
		if(form.Name.value == "") {
			alert("<%=trans.tslt("Please enter Cluster Name")%>");
			form.Name.focus();
		}
		return false;
	}
	return true;
}
//Edited by Xuehai 02 Jun 2011. Remove 'void'. Enable to run on different browers like Chrome&Firefox.
//void function cancelAdd()
function cancelEdit()
{
	window.close();
	//opener.location.href = 'Competency.jsp';
}
</script>

<%
String username=(String)session.getAttribute("username");

  if (!logchk.isUsable(username)) 
  {
%> <font size="2">
   
<script>
	parent.location.href = "index.jsp";
</script>
<%  } 
  else 
  { 

/*-------------------------------------------------------------------end login modification 1--------------------------------------*/

%>

<%
	int orgID = logchk.getOrg();	
	int compID = logchk.getCompany();
	int pkUser = logchk.getPKUser();
	int userType = logchk.getUserType();	// 1= super admin
	
	int compExist = 0;
	
	if(request.getParameter("edit") != null) {
		if(request.getParameter("Name") != null)	{
  			String name = request.getParameter("Name");
  			

			// check whether Cluster name already exists in database
		    Boolean Exist = false;
		    
			Vector v = Cluster.getOrganizationCluster(logchk.getOrg());
			for(int i = 0; i < v.size(); i++){
				voCluster vo = (voCluster)v.elementAt(i);
				
				String ClusterName = vo.getClusterName();
				
				if(name.equals(ClusterName)){
					Exist = true;
				}

			}
			
			if(!Exist) {						
				try{					
					boolean edit = Cluster.updateCluster(logchk.getClusterID(),name, pkUser);
					
					if(edit){
						%>
						<script>
						alert("Cluster edited successfully");
						opener.location.href = 'Cluster.jsp';
						window.close();
						</script>
						<% 
					}
					else{
						
					}
				}catch(SQLException SE) {
                     System.out.println(SE);
				}
			} else {			
%>
	<script>
  		alert("<%=trans.tslt("Cluster Name exists")%>");
		window.location.href='EditCluster.jsp';
	</script>
<%			
			}

	}
	}
%>	


<form name="EditCluster" method="post">
  <table border="0" width="480" height="141" font span style='font-size:10.0pt;font-family:Arial'>
    <tr>
      <td width="70" height="33"><%= trans.tslt("Cluster Name") %></td>
      <td width="10" height="33">&nbsp;</td>
      <td width="400" height="33">
    	<input name="Name" type="text"  style='font-size:10.0pt;font-family:Arial' id="Name" value="<%=Cluster.getCluster(logchk.getClusterID()).getClusterName()%>"size="50" maxlength="50">
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
	        <input type="button" name="Submit" value="<%= trans.tslt("Submit") %>" onClick="return confirmEdit(this.form)"></font><font span style='font-family:Arial'>
		</font>
			<font face="Arial">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
        </font>
		<font face="Arial" span style="font-size: 10.0pt; font-family: Arial">
			<input name="Cancel" type="button" id="Cancel" value="<%= trans.tslt("Cancel") %>" onClick="cancelEdit()">
			</font> </p>
    </blockquote>
  </blockquote>
</form>
<% } %>
</body>
</html>