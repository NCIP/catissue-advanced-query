package edu.wustl.query.util.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.query.util.global.Constants;
import edu.wustl.query.util.global.Utility;

/**
 * Request filter added to check SessionDataBean since Query actions were accessible without loggin in to the system
 * @author vijay_pande
 *
 */
public class QueryRequestFilter implements Filter
{

	private org.apache.log4j.Logger logger = Logger.getLogger(QueryRequestFilter.class);
	
	/* (non-Javadoc)
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException
	{

		HttpServletRequest request = (HttpServletRequest) req;
		HttpSession session = request.getSession();
		HttpServletResponse response = (HttpServletResponse) res;
		
		
		SessionDataBean sessionDataBean = Utility.getSessionData(request);
		String url = request.getRequestURL().toString().substring(request.getContextPath().length());
		if(sessionDataBean == null && url.indexOf(".do")>-1)
		{
			if (!isValidURL(url, sessionDataBean))
			{
				logger.error("Redirecting to Login Page!");
				session.setAttribute(Constants.IS_LOADING_LOGIN_PAGE, Constants.TRUE);
				response.sendRedirect("Logout.do");
				return;
			}
		}
		chain.doFilter(req, res);
	}

	/**
	 * Method to check validity of URL
	 * @param url
	 * @param sessionDataBean
	 * @return
	 */
	private boolean isValidURL(String url, SessionDataBean sessionDataBean)
	{
		boolean isValidURL = false;
		String[] array = new String[]{"Logout.do", "RedirectHome.do","Login.do"};
		for(String string : array)
		{
			if(url.indexOf(string)>-1)
			{
				isValidURL = true;
				break;
			}
		}
		return isValidURL;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	public void init(FilterConfig config) throws ServletException
	{

		//add code to initialize resource   
	}

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#destroy()
	 */
	public void destroy()
	{
		//add code to release any resource   
	}
}
