package tododrop;


import com.codahale.metrics.JmxReporter;
import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Optional;
import io.dropwizard.Application;
import io.dropwizard.Configuration;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

public class Main extends Application<Main.TodoDropConfiguration> {


    public static void main(String[] args) throws Exception {
        new Main().run("server", System.getProperty("dropwizard.config"));
    }

    @Override
    public void initialize(Bootstrap<TodoDropConfiguration> bootstrap) {
    }


    @Override
    public void run(TodoDropConfiguration config, Environment env) {

        JmxReporter.forRegistry(env.metrics()).build().start(); // Manually add JMX reporting (Dropwizard regression)

        env.jersey().register(new HelloWorldResource(config));
    }

    public static class TodoDropConfiguration extends Configuration {
        @JsonProperty private @NotEmpty String template;
        @JsonProperty private @NotEmpty String defaultName;

        public String getDefaultName() {
            return defaultName;
        }

        public String getTemplate() {
            return template;
        }
    }

    @Path("/hello-world")
    @Produces(MediaType.APPLICATION_JSON)
    public static class HelloWorldResource {
        private final AtomicLong counter = new AtomicLong();
        private final String template;
        private final String defaultName;

        public HelloWorldResource(TodoDropConfiguration config) {
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


