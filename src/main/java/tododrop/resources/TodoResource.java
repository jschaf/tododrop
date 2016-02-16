package tododrop.resources;

import com.codahale.metrics.annotation.Timed;
import com.google.common.base.Optional;
import io.dropwizard.jersey.PATCH;
import tododrop.database.TodoStore;
import tododrop.models.tables.pojos.Todo;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/api")
@Produces(MediaType.APPLICATION_JSON)
public final class TodoResource {

    private final TodoStore todoStore;

    @Inject
    public TodoResource(TodoStore todoStore) {
        this.todoStore = todoStore;
    }

    @Timed
    @GET
    public List<Todo> getAllTodos() {
        return todoStore.getAll();
    }

    @Timed
    @POST
    public Todo addTodo(Todo todo) {
        final Todo save = todoStore.save(todo);
        return save;
    }

    @Timed
    @DELETE
    public List<Todo> deleteAll() {
        todoStore.deleteAll();
        return todoStore.getAll();
    }

    @Path("/{id: [0-9]+}")
    @Timed
    @DELETE
    public void deleteTodo(@PathParam("id") int id) {
        todoStore.deleteById(id);
    }

    @Path("/{id: [0-9]+}")
    @Timed
    @GET
    public Optional<Todo> getTodo(@PathParam("id") int id) {
        return todoStore.getById(id);
    }

    @Path("/{id: [0-9]+}")
    @Timed
    @PATCH
    public Todo updateTodo(@PathParam("id") int id, Todo patchTodo) {
        return todoStore.updateAtIdWithTodo(id, patchTodo);
    }

}
