package todo;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Objects;

@Entity
public class Todo {
    private @Id @GeneratedValue Long id;
    private String title;
    private boolean completed;

    public Todo() {
        // No args constructor, needed for deserialization
    }

    public Todo(String title) {
        this.title = title;
    }

    public String getTitle() {
        return this.title;
    }

    public boolean isCompleted() {
        return this.completed;
    }

    public String getUrl() {
        return String.valueOf(this.id);
    }

    public void setUrl(String url) {
        this.id = Long.valueOf(url);
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Todo todo = (Todo) o;
        return completed == todo.completed &&
            Objects.equals(id, todo.id) &&
            Objects.equals(title, todo.title);
    }

    @Override public int hashCode() {
        return Objects.hash(id, title, completed);
    }

    public Long getId() {
        return id;
    }
}
