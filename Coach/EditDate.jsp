<%// Author: Dai Yong in June 2013%>
<%@ page import = "java.sql.*" %>
<%@ page import = "java.io.*" %>
<%@ page import = "java.util.*" %>
<%@ page import = "java.lang.*" %>
<%@ page import = "CP_Classes.vo.*" %>
<%@ page import="java.util.Date"%>
<%@ page import="java.text.SimpleDateFormat"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>

<title>Edit Coaching Date</title>
</head>

<body style="background-color: #DEE3EF">
<jsp:useBean id="Database" class="CP_Classes.Database" scope="session"/>
<jsp:useBean id="logchk" class="CP_Classes.Login" scope="session"/>    
<jsp:useBean id="CoachDate" class="Coach.CoachDate"scope="session" />
<jsp:useBean id="LoginStatus" class="Coach.LoginStatus" scope="session" />
<link rel="stylesheet" type="text/css" media="all" href="jsDatePick_ltr.min.css" />

<script type="text/javascript" src="jsDatePick.min.1.3.js"></script>

<script type="text/javascript">
	window.onload = function(){
		new JsDatePick({
			useMode:2,
			target:"inputField",
			dateFormat:"%d-%M-%Y"
		});
	};
</script>
<script language = "javascript">
function confirmEdit(form,field)
{
	if(field.value != "") {
				if (confirm("Edit Coaching Date?")) {
					form.action = "EditDate.jsp?edit=1";
					form.method = "post";
					form.submit();
					return true;
			} else
				return false;
		} else {
			if(field.value == "")
			alert("Please enter coaching date");
			
			return false;
		}
		return true;
	}

	function cancelEdit() {
		window.close();
	}
</script>

<%
	String username = (String) session.getAttribute("username");
	//System.out.println("check:" + request.getParameter("editedDate"));
	int CoachDatePK = LoginStatus.getSelectedDate();
	if (request.getParameter("editedDate") != null) {
		LoginStatus.setSelectedDate(Integer.valueOf(request.getParameter("editedDate")));
		CoachDatePK = Integer.valueOf(request.getParameter("editedDate"));
		
	}

	if (!logchk.isUsable(username)) {
%> <font size="2">
   
<script>
parent.location.href = "../index.jsp";
</script>
<%
	} 
  else 
  { 
	if(request.getParameter("edit") != null) {
		if(!request.getParameter("inputField").equalsIgnoreCase(""))	{
  			String Date = request.getParameter("inputField");
  			
  			

				//check time clash
				boolean Exist = false;

				//check time valid
				Vector v = CoachDate.getAllDate(LoginStatus.getSelectedDateGroup());
				for (int i = 0; i < v.size(); i++) {
					voCoachDate vo = (voCoachDate) v.elementAt(i);
					String dates = vo.getDate();
					if (dates.trim().equalsIgnoreCase((Date.trim()))) {
						Exist = true;
						break;
					}

				}

				if (!Exist) {
					try {
						boolean editsuc = CoachDate.updateDate(CoachDatePK, Date);

						//System.out.println("editsuc:" + editsuc);

						if (editsuc) {
%>
						<script>
						alert("Coaching Date edited successfully");
						opener.location.href = 'DateGroup.jsp';
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
  		alert("Coaching Date Exists");
		window.location.href='EditDate.jsp';
	</script>
<%			
			}

	}
	}
%>	

<%
String Date=CoachDate.getSelectedDate(CoachDatePK).getDate().substring(0, 10);
Date date = new SimpleDateFormat("yyyy-MM-dd").parse(Date);
SimpleDateFormat sdf=new SimpleDateFormat("dd-MMM-yyyy");
String finalDate=sdf.format(date);
%>
<form name="EditDate" method="post">
	<p>	
		<br>
			<b><font color="#000080" size="3" face="Arial">Edit Coaching Date</font></b>
		<br>
	</p>
  <table border="0" width="480" style='font-size:10.0pt;font-family:Arial'>
    <tr>
      <td width="70" height="33">Coaching Date</td>
      <td width="10" height="33">&nbsp;</td>
      <td width="400" height="33">
    	<input name="inputField" type="text" size="12" id="inputField" type="text"  style='font-size:10.0pt;font-family:Arial' id="Date" value="<%=finalDate%>"size="50" maxlength="20">
	  </td>
    </tr>
       
  </table>
  <blockquote>
    <blockquote>
      <p>
		<font face="Arial">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</font>		<font face="Arial" span style="font-size: 10.0pt; font-family: Arial">		
	        <input type="button" name="Submit" value="Submit" onClick="confirmEdit(this.form,this.form.inputField)"></font><font span style='font-family:Arial'>
	        
		</font>
			<font face="Arial">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
        </font>
		<font face="Arial" span style="font-size: 10.0pt; font-family: Arial">
			<input name="Cancel" type="button" id="Cancel" value="Cancel" onClick="cancelEdit()">
			</font> </p>
    </blockquote>
  </blockquote>
</form>
<% } %>
</body>
</html>