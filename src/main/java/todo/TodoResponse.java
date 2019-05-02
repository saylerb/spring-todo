package todo;

public class TodoResponse {
    private Long id;
    private String title;
    private boolean completed;
    private Integer order;
    private String url;

    TodoResponse(final Long id, final String title, final boolean completed, final Integer order, final String url) {
        this.id = id;
        this.title = title;
        this.completed = completed;
        this.order = order;
        this.url = url;
    }

    /**
     * Needed for serialization
     */
    public TodoResponse() {
    }

    static TodoResponse from(Todo todo, String url) {
        return new TodoResponse(todo.getId(), todo.getTitle(), todo.isCompleted(), todo.getOrderNumber(), url);
    }

    public Integer getOrder() {
        return order;
    }

    public String getUrl() {
        return url;
    }

    public String getTitle() {
        return title;
    }

    public Long getId() {
        return id;
    }

    public boolean isCompleted() {
        return completed;
    }
}

