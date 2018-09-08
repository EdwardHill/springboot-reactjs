package react.api;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@RestController
@Profile("dev")
public class CookieController {
  private static final Logger logger = Logger.getLogger(CookieController.class);

  @RequestMapping("/dev/test/cookie")
  public String cookie(@RequestParam("browser") String browser, HttpServletRequest request, HttpSession session) {
    // 取出session中的browser
    Object sessionBrowser = session.getAttribute("browser");
    if (sessionBrowser == null) {
      logger.info("Session Info:\t不存在session，设置browser=" + browser);
      session.setAttribute("browser", browser);
    } else {
      logger.info("Session Info:\t存在session，browser=" + sessionBrowser.toString());
    }
    Cookie[] cookies = request.getCookies();
    if (cookies != null && cookies.length > 0) {
      for (Cookie cookie : cookies) {
        logger.info("Cookie Info:\t" + cookie.getName() + " : " + cookie.getValue());
      }
    }
    return "index";
  }
}
