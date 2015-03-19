package com.imcode.controllers.restful;

import com.imcode.controllers.AbstractRestController;
import com.imcode.entities.AppTablePermission;
import com.imcode.services.AppTablePermissionService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/permissions")
public class AppTablePermissionRestControllerImpl extends AbstractRestController<AppTablePermission, Long, AppTablePermissionService> {

}
