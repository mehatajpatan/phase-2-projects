Source Code:
 
package com.database;

import java.sql.Connection;
import java.sql.DriverManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.simplilearn.workshop.util.StringUtil;

public class Dao {
	public Connection con = null;
	public Statement st = null;

	public Dao() throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.cj.jdbc.Driver");
		con = DriverManager.getConnection("jdbc:mysql://localhost:3306/airline", "root", "12345678");
		System.out.println("connection established with database");
		st = con.createStatement();
	}

	public List<String[]> getAvailableFlights(String S, String D, String d) {

		List<String[]> flights = new ArrayList<>();
		String query = "SELECT * FROM flight_info where Source='" + S + "' and Dest='" + D + "' and date='" + d + "'";
		try {
			ResultSet rs = st.executeQuery(query);

			if (rs.next()) {
				String[] flight = new String[3];
				flight[0] = rs.getString("name");
				flight[1] = rs.getString("time");
				flight[2] = rs.getString("price");
				flights.add(flight);
				return flights;
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return null;
	}

	public HashMap<String, String> checkUser(String email, String password) {

		HashMap<String, String> user = null;
		String query = "select * from user where email='" + email + "' and password='" + password + "'";
		try {
			ResultSet rs = st.executeQuery(query);
			if (rs.next()) {
				user = new HashMap<>();
				user.put("name", rs.getString("name"));
				user.put("email", rs.getString("email"));
				user.put("phno", rs.getString("phno"));
				user.put("adno", rs.getString("adno"));
			}
			return user;
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return user;
	}

	public boolean insertUser(HashMap<String, String> user) {

		String query = "INSERT INTO user (email, password, name, phno, adno) values('" + user.get("email") + "','"
				+ user.get("password") + "','" + user.get("name") + "','" + user.get("phno") + "','" + user.get("adno")
				+ "')";

		try {
			st.execute(query);
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean checkAdmin(String email, String password) {

		try {
			ResultSet rs = st
					.executeQuery("select * from admin where email='" + email + "' and password='" + password + "'");
			if (rs.next())
				return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean changeAdminPassword(String email, String password) {

		try {
			ResultSet rs = st.executeQuery("select * from admin where email='" + email + "'");
			if (!rs.next()) {
				return false;
			}
			st.execute("update admin set password='" + password + "' where email='" + email + "'");
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean insertFlight(HashMap<String, String> flight) throws SQLException {

		String query1 = "INSERT INTO flight_info (name, Source, Dest, date, time, price) VALUES" + " ('"
				+ StringUtil.fixSqlFieldValue(flight.get("name")) + "'," + " '"
				+ StringUtil.fixSqlFieldValue(flight.get("Source")) + "'," + " '"
				+ StringUtil.fixSqlFieldValue(flight.get("Dest")) + "'," + " '"
				+ StringUtil.fixSqlFieldValue(flight.get("date")) + "'," + " '"
				+ StringUtil.fixSqlFieldValue(flight.get("time")) + "'," + " '"
				+ StringUtil.fixSqlFieldValue(flight.get("price")) + "')";

		System.out.println(flight.get("date"));
		System.out.println(flight.get("time"));

		try {
			st.execute(query1);
			return true;
		} catch (SQLException e) {
			System.out.print("Error in inserting Data");
			e.printStackTrace();
		}
		return false;
	}
}
 ---------------------------------------------------------------------------------------------------------------------


package com.servlets;

import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.database.Dao;



@WebServlet("/AdminLogin")
public class AdminLogin extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
    	String email=request.getParameter("email");
		String password=request.getParameter("password");
		
		try {
			Dao dao=new Dao();
			
			if(dao.checkAdmin(email,password)) {
				response.sendRedirect("AdminHome.jsp");
			}
			else {
				HttpSession session=request.getSession();
				session.setAttribute("message", "Invalid Details");
				response.sendRedirect("AdminPage.jsp");
			}
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
 ----------------------------------------------------------------------------------------------------------


package com.servlets;


import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.database.Dao;

@WebServlet("/FlightList")
public class FlightList extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String Source=request.getParameter("Source");
		String Dest=request.getParameter("Dest");
		String departure=request.getParameter("departure");
		
		try {
			Dao dao = new Dao();
			List<String[]> flights=dao.getAvailableFlights(Source, Dest, departure);			
			HttpSession session=request.getSession();
			session.setAttribute("flights", flights);
			
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		response.sendRedirect("FlightList.jsp");
	}
}

 -----------------------------------------------------------------------------------------------------------------------------------


package com.servlets;

import javax.servlet.annotation.WebServlet;
import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.database.Dao;
@WebServlet("/ForgotPassword")
public class ForgotPassword extends HttpServlet {
	private static final long serialVersionUID = 1L;
protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String email=request.getParameter("email");
		String password=request.getParameter("password");
		
		try {
			Dao dao=new Dao();
			HttpSession session=request.getSession();
			if(dao.changeAdminPassword(email,password)) {
				session.setAttribute("message", "Password Changed Successfully");
			}
			else {
				session.setAttribute("message", "Invalid Details");
			}
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		response.sendRedirect("AdminPage.jsp");
		
	}

}

 -----------------------------------------------------------------------------------------------------------------------------------


package com.servlets;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.database.Dao;
import com.mysql.cj.xdevapi.Statement;


@WebServlet("/InsertFlight")
public class InsertFlight extends HttpServlet {
	private static final long serialVersionUID = 1L;
protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String name=request.getParameter("name");
		String Source=request.getParameter("Source");
		String Dest=request.getParameter("Dest");
		String departure=request.getParameter("departure");
		String time=request.getParameter("time");
		String price=request.getParameter("price");
		
		HashMap<String,String> flight=new HashMap<>();
		flight.put("name", name);
		flight.put("Source", Source);
		flight.put("Dest", Dest);
		flight.put("date", departure);
		flight.put("time", time);
		flight.put("price", price);
		
		try
		{
		Class.forName("com.mysql.jdbc.Driver");
		Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/db_world", "root", "12345678");
		Statement st=(Statement) conn.createStatement();

		int i=((java.sql.Statement) st).executeUpdate("insert into flight(name, Source, Dest, date, time, price)values('"+name+"','"+Source+"','"+Dest+"','"+departure+"','"+time+"','"+price+"')");
//		out.println("Data is successfully inserted!");
//		String sql = "insert into flight(name, Source, Dest, date, time, price) values (?,?, ?, ?, ?)";

		}
		catch(Exception e)
		{
		System.out.print(e);
		e.printStackTrace();
		}
		
		try {
			Dao dao=new Dao();
			HttpSession session=request.getSession();
			if(dao.insertFlight(flight)) {
				
				session.setAttribute("message", "Flight Added Successfully");
			}
			else {
				session.setAttribute("message", "Invalid Details");
			}
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			System.out.print("error");
			e.printStackTrace();
		}
		response.sendRedirect("AdminHome.jsp");
		
	}

}


 ------------------------------------------------------------------------------------------------------------------------------------


package com.servlets;

import javax.servlet.annotation.WebServlet;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


@WebServlet("/Logout")
public class Logout extends HttpServlet {
	private static final long serialVersionUID = 1L;
protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		HttpSession session=request.getSession();
		session.setAttribute("user", null);
		response.sendRedirect("HomePage.jsp");
	}

}

 -----------------------------------------------------------------------------------------------------------------------------------------------------------


package com.servlets;

import javax.servlet.annotation.WebServlet;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.database.Dao;
@WebServlet("/UserLogin")
public class UserLogin extends HttpServlet {
	private static final long serialVersionUID = 1L;
protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String email=request.getParameter("email");
		String password=request.getParameter("password");
		
		try {
			Dao dao=new Dao();
			HashMap<String,String> user=dao.checkUser(email,password);
			HttpSession session=request.getSession();
			if(user!=null) {
				session.setAttribute("user", user);
				response.sendRedirect("HomePage.jsp");
			}
			else {
				session.setAttribute("message", "Invalid Details");
				response.sendRedirect("UserPage.jsp");
			}
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

}
}
--------------------------------------------------------------------------------------------------------------------------------------


 
package com.servlets;

import javax.servlet.annotation.WebServlet;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.database.Dao;

@WebServlet("/UserRegistration")
public class UserRegistration extends HttpServlet {
	private static final long serialVersionUID = 1L;
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String email=request.getParameter("email");
		String password=request.getParameter("password");
		String name=request.getParameter("name");
		String phno=request.getParameter("phno");
		String adno=request.getParameter("adno");
		
		HashMap<String,String> user=new HashMap<>();
		user.put("email", email);
		user.put("password", password);
		user.put("name", name);
		user.put("phno", phno);
		user.put("adno", adno);
		
		try {
			Dao dao=new Dao();
			boolean result=dao.insertUser(user);
			HttpSession session=request.getSession();
			if(result) {
				session.setAttribute("message", "User Added Successfully");
			}
			else {
				session.setAttribute("message","Invalid Details");
			}
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		response.sendRedirect("UserPage.jsp");
	}

}
 ----------------------------------------------------------------------------------------------------------------------------------------
package org.simplilearn.workshop.util;

public class StringUtil {
	public static String fixSqlFieldValue(String value) {
		if (value == null) {
			return null;
		}
		int length = value.length();
		StringBuffer fixedValue = new StringBuffer((int)(length*1.1));
		for(int i = 0 ; i < length ;i++) {
			char c = value.charAt(i);
			if ( c == '\'') {
				fixedValue.append("''");
			}else {
				fixedValue.append(c);
			}
		}
		return fixedValue.toString();
	}
	
	public static String encodeHtmlTag(String tag) {
		if (tag==null)
			return null;
		int length = tag.length();
		StringBuffer encodedTag = new StringBuffer(2 * length);
		for(int i = 0 ; i < length;i++) {
			char c = tag.charAt(i);
			if(c=='<')
				encodedTag.append("<");
			else if(c=='>')
				encodedTag.append(">");
			else if(c=='&')
				encodedTag.append("&amp;");
			else if(c=='"')
				encodedTag.append("&quot;");
			else if(c==' ')
				encodedTag.append("&nbsp;");
			else
				encodedTag.append(c);
		}
		return encodedTag.toString();
	}
}

 ----------------------------------------------------------------------------------------------------------------------


<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Admin Home</title>
</head>
<body >
<br><center>
<a href=HomePage.jsp style="color:black;text-decoration:none ;font-size:35px;font-weight:bold;">FlyAway</a>
</center>
<br><br>
<center>
<h1>Insert New Flight Details</h1>

<div style="border:3px solid black;width:25%;border-radius:10px;padding:20px" align="center">
<form action=InsertFlight method=post>
	<label for=name>Name :-</label> <input type="text" name=name id=name /><br><br>
	<label for=Source>Source :-</label> <input type="text" name=Source id=Source /><br><br>
	<label for=Dest>Dest :-</label> <input type="text" name=Dest id=Dest /><br><br>
	<label for=departure>Departure :-</label> <input type="date" name=departure id=departure /><br><br>
	<label for=time>Time :-</label> <input type="time" name=time id=time /><br><br>
	<label for=price>Price :-</label> <input type="text" name=price id=price /><br><br>
	<input type=submit value=submit /> <input type=reset />
</form>
</div>
</center>
<center>
<%
	String message=(String)session.getAttribute("message");
	if(message!=null){
%>
<p style="color:Green;"><%=message %></p>
<%
		session.setAttribute("message", null);
	}
%>
</center>
</body>
</html>
 ----------------------------------------------------------------------------------------------------------------------
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Admin Page</title>
</head>
<body >
<br><center>
<a href=HomePage.jsp style="color:black;text-decoration:none ;font-size:35px;font-weight:bold;">FlyAway</a>
</center>
<br><br>
<center>
<h2>Admin Login</h2>
<div style="border:3px solid black;width:25%;border-radius:10px;padding:20px" align="center">
<form action=AdminLogin method=post>
	<label for=email>Email :-</label> <input type="email" name=email id=email /><br><br>
	<label for=pass>Password :-</label> <input type="password" name=password id=pass /><br><br>
	<input type=submit value=submit /> <input type=reset />
</form>
</div>
</center><br>
<center>
<a href=ForgotPassword.jsp style="font-size:25;color:red;">Forgot Password</a>
</center>
<%
	String message=(String)session.getAttribute("message");
	if(message!=null){
%>
<p style="color:silver;"><%=message %></p>
<%
		session.setAttribute("message", null);
	}
%>
</body>
</html>
 --------------------------------------------------------------------------------------------------------------------


%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ page import="java.util.*" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>FlyAway</title>
</head>
<body >
<br><center>
<a href=HomePage.jsp style="color:black;text-decoration:none ;font-size:35px;font-weight:bold;">FlyAway</a>
</center>
<br>

<%
	@SuppressWarnings("unchecked")
	HashMap<String,String> user=(HashMap<String,String>)session.getAttribute("user");
	if(user==null){
		response.sendRedirect("UserPage.jsp");
	}
%>
<p align="center"  style="color:Grey;font-size:40px;font-weight:bold">Your Ticket Booked Successfully</p>
</body>
</html>

 ----------------------------------------------------------------------------------------------------------------------------------------------


<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<%@ page import="java.util.*" %>
<html>
<head>
<meta charset="ISO-8859-1">
<center>
<title>Flight List</title>
</head>
<body >
<br>
<a href=HomePage.jsp style="color:black;text-decoration:none;font-size:35px;font-weight:bold;">FlyAway</a>
<br><br>
<%
	@SuppressWarnings("unchecked")
	List<String[]> flights=(List<String[]>)session.getAttribute("flights");
	if(flights!=null){
%>

<h1>Available Flights</h1>

<table border="2">
<tr>
	<th>Name</th>
	<th>Time</th>
	<th>Price</th>
</tr>



<%
	for(String[] flight:flights){
%>

<tr>
<td><%=flight[0]%></td>
<td><%=flight[1]%></td>
<td><%=flight[2]%></td>
</tr>
</table>
</center>
<center><a href=BookFlight.jsp>Book Now</a> </center>
<%
	}
%>


<%
	}
	else{
%>
<h1>There are no available flights</h1>
<%
	}
%>
</body>
</html>
-------------------------------------------------------------------------------------------------------------------------------------


 
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Insert title here</title>
</head>
<body >
<br>
<a href=HomePage.jsp style="color:black;text-decoration:none ;font-size:35px;font-weight:bold;">FlyAway</a>
<br><br>
<center>
<div style="border:3px solid black;width:25%;border-radius:20px;padding:20px" align="center">
<form action=ForgotPassword method=post>
	<label for=email>Email :-</label> <input type="email" name=email id=email /><br><br>
	<label for=pass>New Password :-</label> <input type="password" name=password id=pass /><br><br>
	<input type=submit value=submit /> <input type=reset />
</form>
</div>
</center>
</body>
</html>
------------------------------------------------------------------------------------------------------------------------


 
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ page import="java.util.*" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>FlyAway</title>
</head>
<style>
  h1 {color:Black;}
  p {color:blue;}
</style>
<body >
<center>
<h1>FlyAway</h1>
</center>

<div align="right">
<a href="AdminPage.jsp">Admin Login</a>
</div>

<%
	@SuppressWarnings("unchecked")
	HashMap<String,String> user=(HashMap<String,String>)session.getAttribute("user");
	if(user!=null){
%>
<p>Welcome <%=user.get("name") %></p>
<a href="Logout">Logout</a>
<%
	}else{
%>
<a href=UserPage.jsp>User Login</a>
<%
	}
%>
<br><br>
<center>
<div style="border:3px solid Black;width:25%;border-radius:10px;padding:15px" align="center">
<form action=FlightList method=post>
	<h3>Search for Flights</h3> <br>
	<p><label for=Source>Source :-</label> <input type=text name=Source id=Source/><br><br>
	<label for=Dest>Dest :-</label> <input type=text name=Dest id=Dest/><br><br>
	<label for=departure>Departure :-</label> <input type=date name=departure id=departure/><br><br>
	<label for=travellers>Travellers :-</label> <input type=number name=travellers id=travellers/><br><br>
	<input type=submit value=Search /> <input type=reset /> </p>
</form>
</div>
</center>
</body>
</html>
--------------------------------------------------------------------------------------------------------------------------------------------------


 
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Insert title here</title>
</head>
<body >
<br><center>
<a href=HomePage.jsp style="color:black;text-decoration:none ;font-size:35px;font-weight:bold;">FlyAway</a>
</center>
<br><br>
<center>
<h2>User Login</h2>
<div style="border:2px solid black;width:25%;border-radius:10px;padding:20px" align="center">
<form action=UserLogin method=post >
    <table >
    <tr>
    <td><label for=email>Email</label><br></td>
    <td><input type="email" name=email id=email /></td>
    </tr>
    <tr>
    <td><label for=pass>Password</label><br></td>
    <td><input type="password" name=password id=pass /></td>
    </tr>
    
    <tr>
    <td><input type=submit value=submit /></td>
    <td><input type=reset /></td>
    </tr>
    </table>
	
</form>
</div>
</center>
<br><br>
<center>
<a>New User-Create account</a>
<h4><a href=UserRegistration.jsp style="font-size:25;color:red;">Create Account</a></h4>
</center>
<%
	String message=(String)session.getAttribute("message");
	if(message!=null){
%>
<p style="color:silver;"><%=message %></p>
<%
		session.setAttribute("message", null);
	}
%>
</body>
</html>
--------------------------------------------------------------------------------------------------------------------------------


 
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Insert title here</title>
</head>
<body >
<br><center>
<a href=HomePage.jsp style="color:black;text-decoration:none ;font-size:35px;font-weight:bold;">FlyAway</a>
</center>
<br><br>
<center>
<div style="border:3px solid black;width:25%;border-radius:10px;padding:20px" align="center">
<form action=UserRegistration method=post>
	<label for=email>Email :-</label> <input type="email" name=email id=email /><br><br>
	<label for=pass>Password :-</label> <input type="password" name=password id=pass /><br><br>
	<label for=name>Name :-</label> <input type="text" name=name id=name /><br><br>
	<label for=phno>Phone No. :-</label> <input type="text" name=phno id=phno /><br><br>
	<label for=adno>Aadhaar No. :-</label> <input type="text" name=adno id=adno /><br><br>
	<input type=submit value=submit /> <input type=reset />
</form>
</div>
</center>
</body>
</html>
--------------------------------------------------------------------------------------------------------------------
 
<?xml version="1.0" encoding="UTF-8"?>
<web-app >
  <display-name>FlyAway</display-name>
  <welcome-file-list>
    <welcome-file>index.html</welcome-file>
    <welcome-file>index.htm</welcome-file>
    <welcome-file>HomePage.jsp</welcome-file>
    <welcome-file>default.html</welcome-file>
    <welcome-file>default.htm</welcome-file>
    <welcome-file>default.jsp</welcome-file>
  </welcome-file-list>
</web-app>
