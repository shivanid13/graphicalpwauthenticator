package actions;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import pack.Dbconnection;

/**
 * Servlet implementation class RegisterCheckingServlet
 */
@WebServlet("/registerchecking")
public class RegisterCheckingServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public RegisterCheckingServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		try {

			HttpSession user=request.getSession();
			String owner=user.getAttribute("username").toString();
			
			System.out.println("user is \t"+owner);

			String  x1=request.getParameter("xx1");
			String  y1=request.getParameter("yy1");
			String  x2=request.getParameter("xx2");
			String  y2=request.getParameter("yy2");
			
			System.out.println("points: \t"+x1+"\t"+y1+"\t"+x2+"\t"+y2);
			
			boolean isMailed=false;
			
			Connection con=Dbconnection.getConn();
			
			Statement st=con.createStatement();
			
			ResultSet rs=st.executeQuery("select mail from register where username='"+owner+"'");
			
			String mail=null;
			
			while(rs.next())
			{
				mail=rs.getString("mail");
			}
			
			if(mail!=null)
			{
				try
				{
					String key=owner+" your graphical password is "+x1+" "+x2+" "+ y1+" "+y2;
					
					mailsend(key,mail);
					isMailed=true;
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
				
				String imagePath=request.getParameter("imagepath");

				
				
				System.out.println("after connection is \t"+con);
				
				if(isMailed)
				{
					PreparedStatement ps=con.prepareStatement("update register set x1=?,y1=?,x2=?,y2=?,captchaname=? where username=?");

					ps.setString(1,x1);
					ps.setString(2,y1);
					ps.setString(3,x2);
					ps.setString(4,y2);
					ps.setString(5,imagePath);
					ps.setString(6,owner);


					int result=ps.executeUpdate();
					
					System.out.println("result is \t"+result);

					if(result==1)
					{
						response.sendRedirect("index.html?status=success");
					}
					else
					{
						response.sendRedirect("index.html?status=failed");
					}
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
	}
	
	 public static void mailsend(String key, String email) throws MessagingException
		{
				String host = "smtp.gmail.com";
				String from = "sheev0813@gmail.com";
				String pass = "gfmeoiwxvjdedqsj";
			
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
}
