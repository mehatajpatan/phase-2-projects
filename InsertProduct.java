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

@WebServlet("/InsertProduct")
public class InsertProduct extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String name = request.getParameter("name");
		String price = request.getParameter("price");
		String qty = request.getParameter("qty");
		

		HashMap<String, String> product = new HashMap<>();
		product.put("name", name);		
		product.put("price", price);
		product.put("qty", qty);
		

		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/ecommerce", "root", "12345678");
			Statement st = (Statement) conn.createStatement();

			int i = ((java.sql.Statement) st)
					.executeUpdate("insert into product( name, price, qty, date)values('" + name + "','"
							 + price +"','"+qty+ "')");

		} catch (Exception e) {
			System.out.print(e);
			e.printStackTrace();
		}

		try {
			Dao dao = new Dao();
			HttpSession session = request.getSession();
			if (dao.insertProducts(product)) {

				session.setAttribute("message", "Product Added Successfully");
			} else {
				session.setAttribute("message", "Invalid Details");
			}
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			System.out.print("error");
			e.printStackTrace();
		}
		response.sendRedirect("index.jsp");

	}

}

