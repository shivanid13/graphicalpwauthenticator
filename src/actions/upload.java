/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package actions;

import com.oreilly.servlet.MultipartRequest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
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
public class upload extends HttpServlet {
	final String filepath="D:\\";
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
			HttpSession user=request.getSession();
			String owner=user.getAttribute("uname").toString();

			MultipartRequest m=new MultipartRequest(request,filepath);
			File file=m.getFile("file");
			String  x1=m.getParameter("xx1");
			String  y1=m.getParameter("yy1");
			String  x2=m.getParameter("xx2");
			String  y2=m.getParameter("yy2");
			String imagePath=m.getParameter("imagepath");

			//           out.println(x1+" "+y1+" "+x2 +" "+y2);
			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			//get current date time with Date()
			Date date = new Date();
			System.out.println(dateFormat.format(date));
			String time=dateFormat.format(date); 

			InputStream is=new FileInputStream(file);


			boolean isMailed=false;


			Connection con=Dbconnection.getConn();

			Statement st1=con.createStatement();

			ResultSet rs=st1.executeQuery("select mail from register where username='"+owner+"'");

			String mail=null;

			while(rs.next())
			{
				mail=rs.getString("mail");
			}

			if(mail!=null)
			{
				try
				{
					String key="owner your file graphical password is: "+x1+" "+x2+" "+y1+" "+y2;

					mailsend(key, mail);
					isMailed=true;
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}

				if(isMailed)
				{

					Statement st=con.createStatement();
					PreparedStatement ps=con.prepareStatement("insert into file (filename,file,owner,x1,y1,x2,y2,time,captchaname)values(?,?,?,?,?,?,?,?,?)");
					ps.setString(1, file.getName());
					ps.setAsciiStream(2, is);
					ps.setString(3,owner );
					ps.setString(4, x1);
					ps.setString(5, y1);
					ps.setString(6, x2);
					ps.setString(7, y2);
					ps.setString(8, time);
					ps.setString(9,imagePath);

					boolean status= ps.execute();

					response.sendRedirect("upload.jsp?status='uploaded'");
					//		           if(status){
					//		               out.println("success");
					//		           }
					//		           else{
					//		               out.println("error");
					//		           }
				}
				else
				{
					System.out.println("mail sending failed");
				}
			}
			else
			{
				System.out.println("mail not found");
			}
		} 
		catch(Exception e){
			
			e.printStackTrace();
		}
		finally {            
			out.close();
		}
	}

	public static void mailsend(String key, String email) throws MessagingException
	{
		String host = "smtp.gmail.com";
		String from = "studentfeedback.orbitdsnr@gmail.com";
		String pass = "9663729899";

		Properties props = System.getProperties();

		props.put("mail.smtp.starttls.enable", "true"); // added this line
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.user", from);
		props.put("mail.smtp.password", pass);
		props.put("mail.smtp.port", "587");
		props.put("mail.smtp.auth", "true");

		String[] to = {email}; // added this line

		Session session = Session.getDefaultInstance(props, null);
		MimeMessage message = new MimeMessage(session);
		message.setFrom(new InternetAddress(from));

		InternetAddress[] toAddress = new InternetAddress[to.length];

		// To get the array of addresses

		for( int i=0; i < to.length; i++ ) 
		{ 
			// changed from a while loop
			toAddress[i] = new InternetAddress(to[i]);
		}

		System.out.println(Message.RecipientType.TO);

		for( int i=0; i < toAddress.length; i++)
		{ 
			// changed from a while loop
			message.addRecipient(Message.RecipientType.TO, toAddress[i]);
		}

		message.setSubject("sending key to your mail");
		message.setText("Key:"+key);

		Transport transport = session.getTransport("smtp");

		transport.connect(host, from, pass);
		transport.sendMessage(message, message.getAllRecipients());

		transport.close();
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
