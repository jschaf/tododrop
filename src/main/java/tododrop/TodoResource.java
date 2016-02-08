package tododrop;

import com.codahale.metrics.annotation.Timed;
import io.dropwizard.jersey.PATCH;
import org.jooq.DSLContext;
import tododrop.models.tables.pojos.Todo;
import tododrop.models.tables.records.TodoRecord;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.List;

import static tododrop.models.Tables.TODO;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class TodoResource {
    @Timed
    @GET
    public List<Todo> getAllTodos(@Context DSLContext db) {
        return db.selectFrom(TODO).fetchInto(Todo.class);
    }

    @Timed
    @POST
    public Todo addTodo(Todo todo, @Context DSLContext db) {
        final TodoRecord todoRecord = db.newRecord(TODO, todo);

        // TODO: why can't I null out this.
        // id is determined by database, not user
//         todoRecord.setId(null);

        // url is determined based on id
        todoRecord.setUrl(null);

        if (todoRecord.getCompleted() == null) {
            todoRecord.setCompleted(false);
        }

        todoRecord.store();

        // build uri
        // Doesn't have host
//        final URI uri = UriBuilder.fromResource(TodoResource.class).build(todoRecord.getId());
        todoRecord.setUrl("http://localhost:8080/" + todoRecord.getId());
        todoRecord.store();

        return db.selectFrom(TODO)
                .where(TODO.ID.eq(todoRecord.getId()))
                .fetchOneInto(Todo.class);
    }

    @Timed
    @DELETE
    public List<Todo> delete(@Context DSLContext db) {
        db.truncate(TODO).execute();
        return db.selectFrom(TODO).fetchInto(Todo.class);
    }


    @Path("/{id: [0-9]+}")
    @Timed
    @DELETE
    public void deleteTodo(@PathParam("id") int id,
                           @Context DSLContext db) {
        db.deleteFrom(TODO).where(TODO.ID.eq(id)).execute();
    }

    @Path("/{id: [0-9]+}")
    @Timed
    @GET
    public Todo getTodo(@PathParam("id") int id,
                        @Context DSLContext db) {
        return db.selectFrom(TODO).where(TODO.ID.eq(id)).fetchOneInto(Todo.class);
    }

    @Path("/{id: [0-9]+}")
    @Timed
    @PATCH
    public Todo updateTodo(Todo patchTodo,
                           @PathParam("id") int id,
                           @Context DSLContext db) {

        final TodoRecord origTodo = db.selectFrom(TODO).where(TODO.ID.eq(id)).fetchOne();
        if (origTodo == null) {
            throw new WebApplicationException("No Todo with id" + id, 404);
        }
        if (patchTodo.getTitle() != null) {
            origTodo.setTitle(patchTodo.getTitle());
        }
        if (patchTodo.getCompleted() != null) {
            origTodo.setCompleted(patchTodo.getCompleted());
        }

        if (patchTodo.getOrder() != null) {
            origTodo.setOrder(patchTodo.getOrder());
        }

        origTodo.store();
        return origTodo.into(Todo.class);
    }

}
