package actions;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import pack.Dbconnection;
import pack.MailUtil;

/**
 * Servlet implementation class LoginAccess
 */
@WebServlet("/loginaccess")
public class LoginAccess extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public LoginAccess() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub

		PrintWriter out = response.getWriter();

		try {

			HttpSession user=request.getSession();
			String name=user.getAttribute("uname").toString();

			int x1=Integer.parseInt(request.getParameter("xx1"));
			int y1=Integer.parseInt(request.getParameter("yy1"));
			int  x2=Integer.parseInt(request.getParameter("xx2"));
			int y2=Integer.parseInt(request.getParameter("yy2"));

			Connection con= Dbconnection.getConn();
			Statement st=con.createStatement();
			Statement st1=con.createStatement();
			
			ResultSet prt=st.executeQuery("select * from register where username='"+name+"'");
			
			int px1Start=0;
			int px1End=0;
			int px2Start=0;
			int px2End=0;
			int py1Start=0;
			int py1End=0;
			int py2Start=0;
			int py2End=0;
			
			String activate="no";
			
			while(prt.next())
			{
				px1Start=prt.getInt(10)-2;
				px1End=prt.getInt(10)+2;
				
				py1Start=prt.getInt(11)-2;
				py1End=prt.getInt(11)+2;
				
				px2Start=prt.getInt(12)-2;
				px2End=prt.getInt(12)+2;
				
				py2Start=prt.getInt(13)-2;
				py2End=prt.getInt(13)+2;
				
				activate=prt.getString("activate");
			}
			
			
			
			if(x1>=px1Start && x1<=px1End)
			{
				System.out.println("in first");
				
				if(y1>=py1Start && y1<=py1End)
				{
					System.out.println("in second");
					
					if(x2>=px2Start && x2<=px2End)
					{
						System.out.println("in three");
						
						if(y2>=py2Start && y2<=py2End)
						{
							System.out.println("in four");
							
							if(activate.equalsIgnoreCase("yes")){

								//out.println("success");
								response.sendRedirect("user_page.jsp");
							}
							else{
								out.println("Your not Yet Activeted");
							}
						}
						else{
							out.println("In Valid Password 4");
						}
					}
					else{
						out.println("In Valid Password 3");
					}
				}
				else{
					out.println("In Valid Password 1");
				}
			}
			else{
				out.println("In Valid Password 1");
			}
		} 
		catch(Exception e){
			
			e.printStackTrace();
		}
	}
}
