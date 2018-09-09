package react.config;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.AbstractHandlerExceptionResolver;

/**
 * <strong>Global exception handler for spring</strong> <br>
 */
@Component
public class GlobalExceptionHandler extends AbstractHandlerExceptionResolver {

  private static final Logger logger = Logger.getLogger(GlobalExceptionHandler.class);

  @Override
  protected ModelAndView doResolveException
    (HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
    try {
      if (ex instanceof IllegalArgumentException) {
        return handleIllegalArgument(request, (IllegalArgumentException) ex, response);
      } else {
        return handleGeneralExceptions(request, ex, response);
      }
    } catch (Exception handlerException) {
      logger.warn("Handling of [" + ex.getClass().getName() + "] resulted in Exception", handlerException);
    }
    return null;
  }

  private ModelAndView handleIllegalArgument
    (HttpServletRequest request, IllegalArgumentException ex, HttpServletResponse response) throws IOException {
    response.sendError(HttpServletResponse.SC_CONFLICT);
    String accept = request.getHeader(HttpHeaders.ACCEPT);
    // ...
    logger.error(ex.getMessage(), ex);

    return new ModelAndView();
  }

  private ModelAndView handleGeneralExceptions
    (HttpServletRequest request, Exception ex, HttpServletResponse response) throws IOException {
    response.sendError(HttpServletResponse.SC_CONFLICT);
    // ...
    // log exception message and stacktrace
    logger.error(ex.getMessage(), ex);
    Map<String, Object> map = new HashMap<String, Object>();
    map.put("exception", ex.getMessage());
    String header = request.getHeader("X-Requested-With");
    // handle asynchronous request
    if (header.trim().isEmpty() && (header.equals("X-Requested-With") || header.equals("XMLHttpRequest"))) {
      response.setContentType("application/json;charset=UTF-8");
      ObjectMapper mapper = new ObjectMapper();
      PrintWriter pw = null;
      try {
        pw = response.getWriter();
        pw.write(mapper.writeValueAsString(map));
        pw.flush();
        pw.close();
      } catch (IOException e) {
        e.printStackTrace();
      } finally {
        if (pw != null)
          pw.close();
      }
      return null;
    }
    return new ModelAndView("error", map);
  }
}
