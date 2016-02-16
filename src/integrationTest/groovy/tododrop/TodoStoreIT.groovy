package tododrop

import com.google.common.base.Optional
import com.google.inject.Inject
import ru.vyarus.dropwizard.guice.test.spock.UseDropwizardApp
import spock.lang.Specification
import tododrop.database.TodoStore
import tododrop.models.tables.pojos.Todo


@UseDropwizardApp(value = TodoApp.class, config = "tododrop.yml")
class TodoStoreIT extends Specification {

    @Inject
    TodoStore todoStore;

    def "getAll returns not null"() {
        when:
            List<Todo> todos = todoStore.getAll();

        then:
            todos.size() != null;
    }

    def "getById works"() {
        when: "we select an id of 324"
            Optional<Todo> todo = todoStore.getById(26)
        then:
            todo.get().id == 26

    }

}