/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package actions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import pack.Dbconnection;

/**
 *
 * @author IBN5
 */
public class download extends HttpServlet {

	/**
	 * Processes requests for both HTTP
	 * <code>GET</code> and
	 * <code>POST</code> methods.
	 *
	 * @param request servlet request
	 * @param response servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException if an I/O error occurs
	 */
	protected void processRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html;charset=UTF-8");
		PrintWriter out = response.getWriter();
		try {
			
			int x1=Integer.parseInt(request.getParameter("xx1"));
			int y1=Integer.parseInt(request.getParameter("yy1"));
			int x2=Integer.parseInt(request.getParameter("xx2"));
			int y2=Integer.parseInt(request.getParameter("yy2"));

			// out.println(x1);

			String id=request.getQueryString();
			String fname=null;
			Connection con= Dbconnection.getConn();
			Statement st=con.createStatement();
			Statement st1=con.createStatement();


			InputStream is=null;
		
			ResultSet prt=st.executeQuery("select * from file where idfile='"+id+"' and (x1='"+x1+"' and y1='"+y1+"' and x2='"+x2+"' and y2='"+y2+"')");
			
			int px1Start=0;
			int px1End=0;
			int px2Start=0;
			int px2End=0;
			int py1Start=0;
			int py1End=0;
			int py2Start=0;
			int py2End=0;
			
			while(prt.next())
			{
				px1Start=prt.getInt(5)-2;
				px1End=prt.getInt(5)+2;
				
				py1Start=prt.getInt(7)-2;
				py1End=prt.getInt(7)+2;
				
				px2Start=prt.getInt(6)-2;
				px2End=prt.getInt(6)+2;
				
				py2Start=prt.getInt(8)-2;
				py2End=prt.getInt(8)+2;
				
				is=prt.getAsciiStream("file");
				fname=prt.getString("filename");
				
			}
			
			System.out.println(px1Start+"-"+px1End);
			System.out.println(py1Start+"-"+py1End);
			System.out.println(px2Start+"-"+px2End);
			System.out.println(py2Start+"-"+py2End);
			
			System.out.println(x1+"-"+y1+"-"+x2+"-"+y2);
			
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
							

							BufferedReader br=new BufferedReader(new InputStreamReader(is));
							String temp=null;
							StringBuffer sb=new StringBuffer();
							while((temp=br.readLine())!=null){
								sb.append(temp);
							}
							br.close();

							response.setHeader("Content-Disposition","attachment;filename=\""+fname+"\"");        
							out.write(sb.toString());

							HttpSession user=request.getSession();
							String name=user.getAttribute("uname").toString();

							DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
							//get current date time with Date()
							Date date = new Date();
							System.out.println(dateFormat.format(date));
							String time=dateFormat.format(date); 

							st1.executeUpdate("insert into downloads (filename,name,time)values('"+fname+"','"+name+"','"+time+"')");
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
					out.println("In Valid Password 2");
				}
			}
			else{
				out.println("In Valid Password 1");
			}

			// response.sendRedirect("download.jsp");
		} 
		catch(Exception e){
			
			e.printStackTrace();
		}
		finally {            
			out.close();
		}
	}

	// <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
	/**
	 * Handles the HTTP
	 * <code>GET</code> method.
	 *
	 * @param request servlet request
	 * @param response servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException if an I/O error occurs
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		processRequest(request, response);
	}

	/**
	 * Handles the HTTP
	 * <code>POST</code> method.
	 *
	 * @param request servlet request
	 * @param response servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException if an I/O error occurs
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		processRequest(request, response);
	}

	/**
	 * Returns a short description of the servlet.
	 *
	 * @return a String containing servlet description
	 */
	@Override
	public String getServletInfo() {
		return "Short description";
	}// </editor-fold>
}
