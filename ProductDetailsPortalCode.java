                                       PRODUCT  DETAILS PORTAL

1)PRODUCT.JAVA

package core.programs;
public class Product {
	private int id;
	private String pname;
	private String ptype;
	private int pprice;
	
	public Product() {}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getPname() {
		return pname;
	}

	public void setPname(String pname) {
		this.pname = pname;
	}

	public String getPtype() {
		return ptype;
	}

	public void setPtype(String ptype) {
		this.ptype = ptype;
	}

	public int getPprice() {
		return pprice;
	}

	public void setPprice(int pprice) {
		this.pprice = pprice;
	}
	
	
}


2)DETAIL.JSP

<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Insert title here</title>
<style>
table, th, td {
  border: 1px solid black;
  border-collapse: collapse;
  padding: 5px;
}
</style>
</head>
<body>
<jsp:useBean id="product" class="core.programs.Product" scope="session"></jsp:useBean>
<h3>Product Details : </h3> 
<br><table >
<tr>
<th>Product ID 	</th>
<th>Product Name	</th>
<th>Product Type 	</th>
<th>Product Price  </th>
</tr>
<tr>
<td><jsp:getProperty property="id" name="product"/></td>
<td><jsp:getProperty property="pname" name="product"/></td>  
<td><jsp:getProperty property="ptype" name="product"/></td>  
<td><jsp:getProperty property="pprice" name="product" /></td>
</tr>
</table> 
</body>
</html>






2)HEADER.JSP

<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
<center>
<h1>Welcome to this page</h1>
</center>
</body>
</html>



3)INDEX.HTML

<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
<form action="pcapture.jsp" method="post">
<center>
<h2>Please fill the Product detail</h2>
<br>
Product Id : <input type = "text" name=id><br><br>
Product Name : <input type = "text" name=pname><br><br>
Product Type : <input type = "text" name=ptype><br><br>
Product Price : <input type = "text" name=pprice><br><br>
<br><br>
<input type = "submit" value = "Submit">
</center>
</form>
</body>
</html>

PCAPTURE.JSP

<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
   <jsp:include page="header.jsp"></jsp:include>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
<br>
<jsp:useBean id="product" class="core.programs.Product" scope="session"/>
	<jsp:setProperty property="id" name="product"/>  
	<jsp:setProperty property="pname" name="product"/>  
	<jsp:setProperty property="ptype" name="product"/>  
	<jsp:setProperty property="pprice" name="product"/>  <br><br>
	
<b><h2>
<center><a href = "detail.jsp"> Click here to get the Product Details</a></center></h2></b><br><br>


</body>
</html>
