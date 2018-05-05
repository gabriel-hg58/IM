package view;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebFilter(filterName = "BlockAdmContents", urlPatterns = {"/private/*"})
public class BlockAdmContents implements Filter {
    
    public BlockAdmContents() {
    }
    
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain)
            throws IOException, ServletException {
        
        try {
            HttpServletRequest reqt = (HttpServletRequest) request;
            HttpServletResponse resp = (HttpServletResponse) response;
            HttpSession ses = reqt.getSession(false);

            String reqURI = reqt.getRequestURI();
            
            if (reqURI.contains("javax.faces.resource")
                    || reqURI.contains("/login.xhtml")
                    || (ses != null && ses.getAttribute("administrator") != null)
                    || reqURI.contains("/CSS")
                    || reqURI.contains("/JS")
                    || reqURI.contains("/images")) {
                chain.doFilter(request, response);
            } else {

                reqt.getRequestDispatcher("/public/manageDocuments/documentsReceived.xhtml?faces-redirect=true").forward(reqt, resp);
                //resp.sendRedirect(reqt.getContextPath() + "/Admin/login.xhtml");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void destroy() {        
    }

    public void init(FilterConfig filterConfig) {        
    }
}