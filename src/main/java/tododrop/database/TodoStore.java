package tododrop.database;

import com.google.common.base.Optional;
import org.jooq.DSLContext;
import tododrop.conf.Annotations.AppURL;
import tododrop.models.tables.pojos.Todo;
import tododrop.models.tables.records.TodoRecord;

import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import java.util.List;

import static tododrop.models.Tables.TODO;

public class TodoStore {

    private final DSLContext db;
    private final String appUrl;

    @Inject
    public TodoStore(DSLContext db, @AppURL String appUrl) {
        this.db = db;
        this.appUrl = appUrl + "api/";
    }

    public List<Todo> getAll() {
        return db.selectFrom(TODO).fetchInto(Todo.class);
    }

    public Optional<Todo> getById(int id) {
        return Optional.fromNullable(db.selectFrom(TODO)
                .where(TODO.ID.eq(id))
                .fetchOneInto(Todo.class));
    }


    public Todo save(Todo todo) {
        final TodoRecord todoRecord = db.newRecord(TODO, todo);

        // id is determined by database, not user
        todoRecord.changed(TODO.ID, false);

        // url is determined based on id
        todoRecord.setUrl(null);

        if (todoRecord.getCompleted() == null) {
            todoRecord.setCompleted(false);
        }

        todoRecord.store();

        todoRecord.setUrl(appUrl + todoRecord.getId());
        todoRecord.store();

        return todoRecord.into(Todo.class);
    }

    public void deleteAll() {
        db.truncate(TODO).execute();
    }

    public void deleteById(int id) {
        db.deleteFrom(TODO).where(TODO.ID.eq(id)).execute();
    }

    public Todo updateAtIdWithTodo(int id, Todo patchTodo) {
        TodoRecord origTodo = db.selectFrom(TODO).where(TODO.ID.eq(id)).fetchOne();

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
