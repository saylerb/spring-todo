package todo;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

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

    public Long getId() {
        return id;
    }
}
