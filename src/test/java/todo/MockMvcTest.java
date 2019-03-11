package todo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static todo.WebLayerTest.API_ROOT;

import com.fasterxml.jackson.databind.ObjectMapper;
import javassist.NotFoundException;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class MockMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TodoRepository todoRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @After
    public void cleanUp() {
        todoRepository.deleteAll();
    }

    @Test
    public void shouldStartFullSpringContextWithoutServerAndRetrieveMessage() throws Exception {
        mockMvc.perform(get("/"))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("Hello, World!")));
    }

    @Test
    public void shouldAllowCors() throws Exception {
        mockMvc.perform(options("/")
            .header("Access-Control-Request-Method", "GET")
            .header("Origin", "www.somethingelse.com"))
            .andExpect(status().isOk());
    }

    @Test
    public void shouldBeAbleToGetAnEmptyListOfTodos() throws Exception {
        mockMvc.perform(
            get(API_ROOT))
            .andExpect(status().isOk())
            .andExpect(content().string("[]"));
    }

    @Test
    public void shouldBeAbleToAddATodo() throws Exception {
        MvcResult result = mockMvc.perform(
            post(API_ROOT)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"title\": \"test todo\" }".getBytes())
                .characterEncoding("utf-8"))
            .andExpect(status().isOk())
            .andReturn();

        Todo createdTodo = objectMapper.readValue(result.getResponse().getContentAsString(), Todo.class);

        assertThat(createdTodo.getTitle()).isEqualTo("test todo");
    }

    @Test
    public void shouldBeAbleToDeleteAllExistingTodos() throws Exception {
        mockMvc.perform(
            delete(API_ROOT))
            .andExpect(status().isOk());
    }

    @Test
    public void shouldSetUpANewTodoAsInitiallyNotCompleted() throws Exception {
        MvcResult result = mockMvc.perform(
            post(API_ROOT)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"title\": \"test todo\" }".getBytes())
                .characterEncoding("utf-8"))
            .andExpect(status().isOk())
            .andReturn();

        Todo resultTodo = objectMapper.readValue(result.getResponse().getContentAsString(), Todo.class);

        assertThat(resultTodo.isCompleted()).isFalse();
    }

    @Test
    public void shouldSetUpANewTodoWithAUniqueUrlEqualToItsId() throws Exception {
        MvcResult result = mockMvc.perform(
            post(API_ROOT)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"title\": \"test todo\" }".getBytes())
                .characterEncoding("utf-8"))
            .andExpect(status().isOk())
            .andReturn();

        Todo createdTodo = objectMapper.readValue(result.getResponse().getContentAsString(), Todo.class);

        assertThat(createdTodo.getUrl()).isEqualTo(String.valueOf(createdTodo.getId()));
    }

    @Test
    public void shouldBeAbleToRetrieveATodoByItsUrl() throws Exception {
        Todo newTodo = new Todo("test todo");

        Todo savedTodo = todoRepository.save(newTodo);

        Todo readBackTodo = todoRepository.findById(savedTodo.getId())
            .orElseThrow(() -> new NotFoundException("Todo not found!"));

        MvcResult result = mockMvc.perform(
            get(API_ROOT + "/" + readBackTodo.getUrl()))
            .andReturn();

        Todo resultTodo = objectMapper.readValue(result.getResponse().getContentAsString(), Todo.class);

        assertThat(resultTodo).isEqualTo(readBackTodo);
    }

    @Test
    public void shouldBeAbleToPatchATodoWithAPartialUpdateToTitle() throws Exception {
        Todo newTodo = new Todo("initial title");

        Todo savedTodo = todoRepository.save(newTodo);

        Todo readBackTodo = todoRepository.findById(savedTodo.getId())
            .orElseThrow(() -> new NotFoundException("Todo not found!"));

        MvcResult result = mockMvc.perform(
            patch(API_ROOT + "/" + readBackTodo.getUrl())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"title\": \"edited title\" }".getBytes())
                .characterEncoding("utf-8"))
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn();

        Todo editedTodo = objectMapper.readValue(result.getResponse().getContentAsString(), Todo.class);

        Todo expected = new Todo(readBackTodo.getId(), "edited title", readBackTodo.isCompleted());

        assertThat(editedTodo).isEqualTo(expected);
    }

    @Test
    public void shouldBeAbleToPatchATodoWithAPartialUpdateToConfirmed() throws Exception {
        Todo newTodo = new Todo(null, "initial title", false);

        Todo savedTodo = todoRepository.save(newTodo);

        Todo readBackTodo = todoRepository.findById(savedTodo.getId())
            .orElseThrow(() -> new NotFoundException("Todo not found!"));

        MvcResult result = mockMvc.perform(
            patch(API_ROOT + "/" + readBackTodo.getUrl())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"completed\": \"true\" }".getBytes())
                .characterEncoding("utf-8"))
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn();

        Todo edited = objectMapper.readValue(result.getResponse().getContentAsString(), Todo.class);

        Todo expected = new Todo(readBackTodo.getId(), "initial title", true);

        assertThat(edited).isEqualTo(expected);
    }

    @Test
    public void shouldReturnExitingTodoWithAnEmptyPatch() throws Exception {
        Todo newTodo = new Todo(null, "initial title", true);

        Todo savedTodo = todoRepository.save(newTodo);

        Todo readBackTodo = todoRepository.findById(savedTodo.getId())
            .orElseThrow(() -> new NotFoundException("Todo not found!"));

        MvcResult result = mockMvc.perform(
            patch(API_ROOT + "/" + readBackTodo.getUrl())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}".getBytes())
                .characterEncoding("utf-8"))
            .andExpect(status().isOk())
            .andReturn();

        Todo editedTodo = objectMapper.readValue(result.getResponse().getContentAsString(), Todo.class);

        assertThat(editedTodo).isEqualTo(readBackTodo);
    }
}

