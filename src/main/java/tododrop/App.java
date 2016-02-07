package tododrop;


import com.bendb.dropwizard.jooq.JooqBundle;
import com.bendb.dropwizard.jooq.JooqFactory;
import com.codahale.metrics.JmxReporter;
import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Optional;
import io.dropwizard.Application;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.flyway.FlywayBundle;
import io.dropwizard.flyway.FlywayFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.jooq.DSLContext;
import tododrop.models.tables.pojos.Todo;
import static tododrop.models.Tables.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

public class App extends Application<App.TodoConfig> {


    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            new App().run("server", System.getProperty("dropwizard.config"));
        } else {
            new App().run(args);
        }
    }

    @Override
    public void initialize(Bootstrap<TodoConfig> bootstrap) {
        // https://github.com/dropwizard/dropwizard-flyway
        bootstrap.addBundle(new FlywayBundle<TodoConfig>() {
            @Override
            public DataSourceFactory getDataSourceFactory(TodoConfig config) {
                return config.getDataSourceFactory();
            }

            @Override
            public FlywayFactory getFlywayFactory(TodoConfig configuration) {
                return configuration.getFlywayFactory();
            }
        });

        // https://github.com/benjamin-bader/droptools/tree/master/dropwizard-jooq
        bootstrap.addBundle(new JooqBundle<TodoConfig>() {
            @Override
            public DataSourceFactory getDataSourceFactory(TodoConfig configuration) {
                return configuration.getDataSourceFactory();
            }

            @Override
            public JooqFactory getJooqFactory(TodoConfig configuration) {
                return configuration.getJooqFactory();
            }
        });


    }

    @Override
    public void run(TodoConfig config, Environment env) {

        JmxReporter.forRegistry(env.metrics()).build().start(); // Manually add JMX reporting (Dropwizard regression)

        env.jersey().register(new HelloWorldResource(config));
        env.jersey().register(new TodoResource());
    }

    public static class TodoConfig extends Configuration {
        @JsonProperty
        private
        @NotEmpty
        String template;
        @JsonProperty
        private
        @NotEmpty
        String defaultName;

        public String getDefaultName() {
            return defaultName;
        }

        public String getTemplate() {
            return template;
        }

        @JsonProperty
        private @NotEmpty String schema;

        public String getSchema() {
            return schema;
        }

        @Valid
        @NotNull
        @JsonProperty("database")
        private DataSourceFactory database = new DataSourceFactory();

        public DataSourceFactory getDataSourceFactory() {
            return database;
        }

        public void setDataSourceFactory(DataSourceFactory dataSourceFactory) {
            this.database = dataSourceFactory;
        }

        @Valid
        @NotNull
        @JsonProperty("flyway")
        private FlywayFactory flywayFactory = new FlywayFactory();

        public FlywayFactory getFlywayFactory() {
            return flywayFactory;
        }



        @JsonProperty("jooq")
        private JooqFactory jooqFactory = new JooqFactory();

        public JooqFactory getJooqFactory() {
            return jooqFactory;
        }

    }

    @Path("/hello-world")
    @Produces(MediaType.APPLICATION_JSON)
    public static class HelloWorldResource {
        private final AtomicLong counter = new AtomicLong();
        private final String template;
        private final String defaultName;

        public HelloWorldResource(TodoConfig config) {
            this.template = config.getTemplate();
            this.defaultName = config.getDefaultName();
        }

        @Timed
        @GET
        public Saying sayHello(@QueryParam("name") Optional<String> name) throws InterruptedException {
            final String value = String.format(template, name.or(defaultName));
            Thread.sleep(ThreadLocalRandom.current().nextInt(10, 500));
            return new Saying(counter.incrementAndGet(), value);
        }
    }

    @Path("/todo")
    @Produces(MediaType.APPLICATION_JSON)
    public static class TodoResource {
        @Timed
        @GET
        public List<Todo> getAllTodos(@Context DSLContext db) {
            return db.selectFrom(TODO).fetchInto(Todo.class);
        }
    }

    public static class Saying {
        private long id;
        private @Length(max = 10) String content;

        public Saying(long id, String content) {
            this.id = id;
            this.content = content;
        }

        public Saying() {} // required for deserialization

        @JsonProperty public long getId() { return id; }
        @JsonProperty public String getContent() { return content; }
    }
}


