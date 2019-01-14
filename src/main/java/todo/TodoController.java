package todo;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class TodoController {

    @RequestMapping(value = "/")
    public @ResponseBody String hello() {
        return "Hello, World!";
    }
}
