package todo;

import javassist.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.PATCH;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping(value = "/todos")
public class TodoController {
    private final TodoRepository repository;

    public TodoController(TodoRepository repository) {
        this.repository = repository;
    }

    @RequestMapping("/hello")
    public @ResponseBody
    String hello() {
        return "Hello, World!";
    }

    @RequestMapping(method = POST)
    public @ResponseBody
    TodoResponse create(@RequestBody Todo newTodo) {
        Todo todo = repository.save(newTodo);
        return TodoResponse.from(todo);
    }

    @RequestMapping(method = GET)
    public @ResponseBody
    List<TodoResponse> getAll() {
        List<Todo> all = repository.findAll();

        return all.stream()
                .map(TodoResponse::from)
                .collect(Collectors.toList());
    }

    @RequestMapping(method = DELETE)
    @ResponseStatus(value = HttpStatus.OK)
    public void delete() {
        repository.deleteAll();
    }

    @RequestMapping(value = "/{id}", method = GET)
    public @ResponseBody
    TodoResponse getOne(@PathVariable("id") Long id) throws NotFoundException {
        Todo todo = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Todo does not exist!"));
        return TodoResponse.from(todo);
    }

    @RequestMapping(value = "/{id}", method = PATCH)
    public @ResponseBody
    TodoResponse edit(@RequestBody TodoPatchRequest updates, @PathVariable("id") Long id) throws Exception {
        Optional<Todo> byId = repository.findById(id);

        if (byId.isPresent()) {
            Todo existing = byId.get();
            Todo updatedTodo = Todo.from(updates, existing);
            return TodoResponse.from(repository.save(updatedTodo));
        } else {
            throw new NotFoundException("Todo not found");
        }
    }

    @RequestMapping(value = "/{id}", method = DELETE)
    @ResponseStatus(value = HttpStatus.OK)
    public void deleteById(@PathVariable("id") Long id) {
        repository.deleteById(id);
    }
}
