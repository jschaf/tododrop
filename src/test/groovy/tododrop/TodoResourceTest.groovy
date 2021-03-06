package tododrop

import io.dropwizard.testing.junit.ResourceTestRule
import org.glassfish.jersey.server.model.Resource
import org.junit.Rule
import spock.lang.Specification
import tododrop.database.TodoStore
import tododrop.models.tables.pojos.Todo
import tododrop.resources.TodoResource

import javax.ws.rs.core.Response

import static javax.ws.rs.client.Entity.*
import static javax.ws.rs.core.MediaType.*

class TodoResourceTest extends Specification {

    TodoStore todoStore = Mock(TodoStore)
    TodoResource todoResource = new TodoResource(todoStore)
    String uri = Resource.from(todoResource.class).getPath()

    @Rule
    ResourceTestRule resources = ResourceTestRule.builder()
            .addResource(todoResource)
            .build()


    def setup() {
    }

    def "Get root with no Todos returns empty list"() {
        given: "no objects in todoStore"
        todoStore.getAll() >> []

        when: "we GET root"
        def result = resources.client().target(uri).request().get(List)

        then: "we receive an empty list"
        result == []
    }

    def "Get root with 1 Todo returns 1 Todo"() {
        given: "one Todo in TodoStore"
        Todo todo = new Todo(1, "title", null, null, null)
        todoStore.getAll() >> [todo]

        when: "we GET root"
        List<Todo> result = resources.client().target(uri).request().get(List)

        then: "we get a list of one todo"
        result.size() == 1
        result.get(0).id == todo.id
        result.get(0).title == todo.title
    }

    def "Adding a todo increases number of Todos"() {
        given: "no todos in TodoStore"
        Todo todo = new Todo(1, "title", null, null, null)

        when: "we add a Todo"
        def response = resources.client().target(uri)
                .request(APPLICATION_JSON_TYPE)
                .post(entity(todo, APPLICATION_JSON_TYPE))

        then:
        // FIXME: How do you test that the returned todo_object is the same?

        response.getStatusInfo() == Response.Status.OK

        1 * todoStore.save(_ as Todo) >> todo
    }
}

