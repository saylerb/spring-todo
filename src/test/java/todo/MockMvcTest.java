package todo;

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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static todo.WebLayerTest.API_ROOT;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles(profiles = "test")
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
        mockMvc.perform(get("/todos/hello"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Hello, World!")));
    }

    @Test
    public void shouldAllowCors() throws Exception {
        mockMvc.perform(options("/todos/hello")
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

        TodoResponse createdTodo = objectMapper.readValue(result.getResponse().getContentAsString(),
                TodoResponse.class);

        TodoResponse expected = new TodoResponse(null, "test todo", false, null, null);

        assertThat(createdTodo).isEqualToIgnoringNullFields(expected);
        assertThat(createdTodo).hasNoNullFieldsOrPropertiesExcept("order");
        TodoResponseAssert.assertThat(createdTodo).hasTitle("test todo");
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

        TodoResponse resultTodo = objectMapper.readValue(result.getResponse().getContentAsString(), TodoResponse.class);

        TodoResponseAssert.assertThat(resultTodo).isNotCompleted();
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

        TodoResponse createdTodo = objectMapper.readValue(result.getResponse().getContentAsString(),
                TodoResponse.class);

        TodoResponseAssert.assertThat(createdTodo).hasUrl("http://localhost/todos/" + createdTodo.getId());
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

        TodoResponse newTodo = objectMapper.readValue(newTodoRequest.getResponse().getContentAsString(),
                TodoResponse.class);

        System.out.println(newTodo.getUrl());

        MvcResult readBackTodo = mockMvc.perform(
                get(newTodo.getUrl()))
                .andDo(print())
                .andReturn();

        TodoResponse resultTodo = objectMapper.readValue(readBackTodo.getResponse().getContentAsString(),
                TodoResponse.class);

        assertThat(resultTodo).isEqualToComparingFieldByField(newTodo);
    }

    @Test
    public void shouldBeAbleToPatchATodoWithAPartialUpdateToTitle() throws Exception {
        Todo newTodo = new Todo("initial title");

        Todo savedTodo = todoRepository.save(newTodo);

        Todo readBackTodo = todoRepository.findById(savedTodo.getId())
                .orElseThrow(() -> new NotFoundException("Todo not found!"));

        MvcResult result = mockMvc.perform(
                patch(API_ROOT + "/" + readBackTodo.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"title\": \"edited title\" }".getBytes())
                        .characterEncoding("utf-8"))
                .andExpect(status().isOk())
                .andReturn();

        TodoResponse editedTodo = objectMapper.readValue(result.getResponse().getContentAsString(), TodoResponse.class);

        TodoResponse expected = new TodoResponse(readBackTodo.getId(), "edited title", readBackTodo.isCompleted(),
                null, "http://localhost/todos/" + readBackTodo.getId());

        assertThat(editedTodo).isEqualToComparingFieldByField(expected);
    }

    @Test
    public void shouldBeAbleToPatchATodoWithAPartialUpdateToConfirmed() throws Exception {
        Todo newTodo = new Todo(null, "initial title", false, null);

        Todo savedTodo = todoRepository.save(newTodo);

        Todo readBackTodo = todoRepository.findById(savedTodo.getId())
                .orElseThrow(() -> new NotFoundException("Todo not found!"));

        MvcResult result = mockMvc.perform(
                patch(API_ROOT + "/" + readBackTodo.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"completed\": \"true\" }".getBytes())
                        .characterEncoding("utf-8"))
                .andExpect(status().isOk())
                .andReturn();

        TodoResponse expected = new TodoResponse(readBackTodo.getId(), "initial title", true, null, "http://localhost" +
                "/todos/" + readBackTodo.getId());
        TodoResponse edited = objectMapper.readValue(result.getResponse().getContentAsString(), TodoResponse.class);

        assertThat(edited).isEqualToComparingFieldByField(expected);
    }

    @Test
    public void shouldReturnExitingTodoWithAnEmptyPatch() throws Exception {
        Todo newTodo = new Todo(null, "initial title", true, null);

        Todo savedTodo = todoRepository.save(newTodo);

        Todo readBackTodo = todoRepository.findById(savedTodo.getId())
                .orElseThrow(() -> new NotFoundException("Todo not found!"));
        TodoResponse expected = TodoResponse.from(readBackTodo);

        MvcResult result = mockMvc.perform(
                patch(API_ROOT + "/" + readBackTodo.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}".getBytes())
                        .characterEncoding("utf-8"))
                .andExpect(status().isOk())
                .andReturn();

        TodoResponse editedTodo = objectMapper.readValue(result.getResponse().getContentAsString(), TodoResponse.class);

        assertThat(editedTodo).isEqualToComparingFieldByField(expected);
    }

    @Test
    public void shouldPersistChangesAndShowUpWhenReFetchingTheTodo() throws Exception {
        Todo newTodo = new Todo(null, "initial title", false, null);

        Todo savedTodo = todoRepository.save(newTodo);

        Todo readBackTodo = todoRepository.findById(savedTodo.getId())
                .orElseThrow(() -> new NotFoundException("Not found"));

        TodoResponse expected = new TodoResponse(
                savedTodo.getId(),
                "changed title",
                true,
                null,
                "http://localhost/todos/" + savedTodo.getId());

        MvcResult patchResult = mockMvc.perform(
                patch(API_ROOT + "/" + readBackTodo.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"title\": \"changed title\", \"completed\": \"true\" }".getBytes())
                        .characterEncoding("utf-8"))
                .andReturn();

        TodoResponse editedTodo = objectMapper.readValue(patchResult.getResponse().getContentAsString(),
                TodoResponse.class);

        assertThat(editedTodo).isEqualToComparingFieldByField(expected);

        MvcResult getResult = mockMvc.perform(
                get(API_ROOT))
                .andReturn();

        List<TodoResponse> editedTodos = objectMapper.readValue(getResult.getResponse().getContentAsString(),
                new TypeReference<List<TodoResponse>>() {
                });

        assertThat(editedTodos.get(0)).isEqualToIgnoringNullFields(expected);
        assertThat(editedTodos.get(0).getUrl()).isNotNull();
    }

    @Test
    public void shouldDeleteATodoWhenMakingADeleteRequestToItsUrl() throws Exception {
        MvcResult newTodoRequest = mockMvc.perform(
                post(API_ROOT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"title\": \"test todo\" }".getBytes())
                        .characterEncoding("utf-8"))
                .andExpect(status().isOk())
                .andReturn();

        TodoResponse response = objectMapper.readValue(newTodoRequest.getResponse().getContentAsString(),
                TodoResponse.class);

        mockMvc.perform(
                delete(response.getUrl()))
                .andExpect(status().isOk());

        mockMvc.perform(
                get(API_ROOT))
                .andExpect(status().isOk())
                .andExpect(content().string("[]"));
    }

    @Test
    public void shouldCreateATodoWithAnOrderField() throws Exception {
        MvcResult newTodoRequest = mockMvc.perform(
                post(API_ROOT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(("{ \"title\": \"test todo\", \"order\": 523}").getBytes())
                        .characterEncoding("utf-8"))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        TodoResponse newTodo = objectMapper.readValue(newTodoRequest.getResponse().getContentAsString(),
                TodoResponse.class);

        assertThat(newTodo.getOrder()).isEqualTo(523);
    }
}