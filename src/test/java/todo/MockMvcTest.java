package todo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static todo.WebLayerTest.API_ROOT;

import com.fasterxml.jackson.core.type.TypeReference;
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

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class MockMvcTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private TodoRepository todoRepository;

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

        assertThat(createdTodo.getUrl()).contains(String.valueOf(createdTodo.getId()));
    }

    @Test
    public void shouldBeAbleToRetrieveATodoByItsUrl() throws Exception {
        MvcResult newTodoRequest = mockMvc.perform(
                post(API_ROOT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"title\": \"test todo\" }".getBytes())
                        .characterEncoding("utf-8"))
                .andExpect(status().isOk())
                .andReturn();

        Todo newTodo = objectMapper.readValue(newTodoRequest.getResponse().getContentAsString(), Todo.class);

        MvcResult readBackTodo = mockMvc.perform(
                get(newTodo.getUrl()))
                .andDo(print())
                .andReturn();

        Todo resultTodo = objectMapper.readValue(readBackTodo.getResponse().getContentAsString(), Todo.class);

        assertThat(resultTodo).isEqualTo(newTodo);
    }

    @Test
    public void shouldBeAbleToPatchATodoWithAPartialUpdateToTitle() throws Exception {
        Todo newTodo = new Todo("initial title");

        Todo savedTodo = todoRepository.save(newTodo);

        Todo readBackTodo = todoRepository.findById(savedTodo.getId())
                .orElseThrow(() -> new NotFoundException("Todo not found!"));

        MvcResult result = mockMvc.perform(
                patch(readBackTodo.getUrl())
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
                patch(readBackTodo.getUrl())
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
                patch(readBackTodo.getUrl())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}".getBytes())
                        .characterEncoding("utf-8"))
                .andExpect(status().isOk())
                .andReturn();

        Todo editedTodo = objectMapper.readValue(result.getResponse().getContentAsString(), Todo.class);

        assertThat(editedTodo).isEqualTo(readBackTodo);
    }

    @Test
    public void shouldPersistChangesAndShowUpWhenReFetchingTheTodo() throws Exception {
        Todo newTodo = new Todo(null, "initial title", false);

        Todo savedTodo = todoRepository.save(newTodo);

        Todo readBackTodo = todoRepository.findById(savedTodo.getId()).get();
        Todo expected = new Todo(savedTodo.getId(), "changed title", true);

        MvcResult patchResult = mockMvc.perform(
                patch(readBackTodo.getUrl())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"title\": \"changed title\", \"completed\": \"true\" }".getBytes())
                        .characterEncoding("utf-8"))
                .andDo(print())
                .andReturn();

        Todo editedTodo = objectMapper.readValue(patchResult.getResponse().getContentAsString(), Todo.class);

        assertThat(editedTodo).isEqualTo(expected);

        MvcResult getResult = mockMvc.perform(
                get(API_ROOT))
                .andReturn();

        List<Todo> editedTodos = objectMapper.readValue(getResult.getResponse().getContentAsString(),
                new TypeReference<List<Todo>>() {
                });

        assertThat(editedTodos).contains(expected);
    }
}

