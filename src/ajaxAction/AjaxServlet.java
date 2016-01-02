package ajaxAction;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.StateDao;

public class AjaxServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("ISO-8859-1");
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out = response.getWriter();
		String path = request.getRequestURI();
		String address = request.getRequestURI().substring(path.lastIndexOf('/'), path.indexOf('.'));
		System.out.println("req.getServletPath():"+request.getServletPath());
		if("/getStat".equals(address)){
			int state = StateDao.queryState();
			System.out.println("\tstate:"+state);
			out.print("{state:"+state+"}");
		}else if ("/setState".equals(address)) {
			int state = Integer.parseInt(request.getParameter("state"));
			StateDao.updateState(state);
			System.out.println("\tstate:"+state);
		}
		out.close();
	}
}
