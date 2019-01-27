package todo;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class TodoController {

    @RequestMapping(value = "/")
    public @ResponseBody String hello() {
        return "Hello, World!";
    }

    @RequestMapping(value = "/todos", method = POST)
    public @ResponseBody ResponseEntity<?> create() {
        return new ResponseEntity<Void>(HttpStatus.OK);
    }
}
