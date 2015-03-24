package com.imcode.controllers.html;

import com.imcode.entities.School;
import com.imcode.services.SchoolClassService;
import com.imcode.services.SchoolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Created by vitaly on 05.03.15.
 */
@Controller
public class MockControler {
    @Autowired
    private SchoolService schoolService;

    @Autowired
    SchoolClassService classService;

    @RequestMapping("/index")
    public String listSchools(Map<String, Object> map) {
        map.put("school", new School());
        map.put("schoolList", schoolService.findAll());

        return "school";
    }

    @RequestMapping("/")
    public String home() {
        return "redirect:/index";
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String addContact(@ModelAttribute("contact") School school,
                             BindingResult result) {

        schoolService.save(school);

        return "redirect:/index";
    }

    @RequestMapping("/delete/{schoolId}")
    public String deleteContact(@PathVariable("schoolId") Long schoolId) {

        schoolService.delete(schoolId);

        return "redirect:/index";
    }

    @RequestMapping("/tok")
//    @ResponseBody
    String takeToken(@RequestParam Map<String,String> allRequestParams, HttpServletRequest req) {
        String thisUri = req.getScheme() + "://" +   // "http" + "://
                req.getServerName() +       // "myhost"
                ":" +                           // ":"
                req.getServerPort();
        if (allRequestParams.get("redirect_uri") != null) {
            System.out.println(allRequestParams.get("redirect_uri"));
        }
        RestTemplate restTemplate = new RestTemplate();
        allRequestParams.put("grant_type", "password");
        allRequestParams.put("client_id", "restapp2");
        allRequestParams.put("client_secret", "restapp2");
        allRequestParams.put("scope", "read+write+trust");
//        allRequestParams.put("", "");
        String uri = thisUri + "/oauth/token?grant_type=password&client_id=restapp2&client_secret=restapp2&scope=read+write+trust&username=" + allRequestParams.get("username") + "&password=" + allRequestParams.get("password");
        Map result = restTemplate.getForObject(uri, Map.class);

        if (allRequestParams.get("redirect_uri") != null) {
            restTemplate.postForObject(allRequestParams.get("redirect_uri") + "?access_token=" + result.get("access_token") + "&refresh_token=" + result.get("refresh_token"), result, String.class, result);
        }

//        return result.toString();
        return "redirect:" + allRequestParams.get("redirect_uri");
    }
   @RequestMapping("/resp")
    @ResponseBody
    String getToken(@RequestParam Map<String,String> allRequestParams) {
//        if (allRequestParams.get("redirect_uri") != null) {
//            System.out.println(allRequestParams.get("redirect_uri"));
//        }
//        RestTemplate restTemplate = new RestTemplate();
//        allRequestParams.put("grant_type", "password");
//        allRequestParams.put("client_id", "restapp2");
//        allRequestParams.put("client_secret", "restapp2");
//        allRequestParams.put("scope", "read+write+trust");
////        allRequestParams.put("", "");
//        String uri = "http://localhost:8080/oauth/token?grant_type=password&client_id=restapp2&client_secret=restapp2&scope=read+write+trust&username="+ allRequestParams.get("username") + "&password=" + allRequestParams.get("password");
//        Map result = restTemplate.getForObject(uri, Map.class);

        return allRequestParams.toString();
    }
//
//    @RequestMapping("/login")
//    public String home() {
//        return "redirect:/index";
//    }
//
//    @RequestMapping("/")
//    public String home() {
//        return "redirect:/index";
//    }
    
    
    
}

