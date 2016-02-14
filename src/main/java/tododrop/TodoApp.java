package tododrop;


import com.codahale.metrics.JmxReporter;
import com.google.common.collect.ImmutableMap;
import io.dropwizard.Application;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.flyway.FlywayBundle;
import io.dropwizard.flyway.FlywayFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import ru.vyarus.dropwizard.guice.GuiceBundle;
import ru.vyarus.dropwizard.guice.module.installer.feature.ManagedInstaller;
import ru.vyarus.dropwizard.guice.module.installer.feature.TaskInstaller;
import ru.vyarus.dropwizard.guice.module.installer.feature.jersey.ResourceInstaller;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import java.util.EnumSet;

public class TodoApp extends Application<TodoConfig> {


    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            new TodoApp().run("server", "tododrop.yml");
        } else {
            new TodoApp().run(args);
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

        // Enable variable substitution with environment variables
        // http://www.dropwizard.io/0.9.2/docs/manual/core.html#environment-variables
        bootstrap.setConfigurationSourceProvider(
                new SubstitutingSourceProvider(bootstrap.getConfigurationSourceProvider(),
                        new EnvironmentVariableSubstitutor(false)
                ));

        // https://github.com/xvik/dropwizard-guicey
        bootstrap.addBundle(GuiceBundle.<TodoConfig> builder()
                .installers(ResourceInstaller.class, TaskInstaller.class, ManagedInstaller.class)
                .extensions(TodoResource.class)
                .modules(new Module())
                .build());
    }

    @Override
    public void run(TodoConfig config, Environment env) {

        FilterRegistration.Dynamic cors = env.servlets().addFilter("CORS", CrossOriginFilter.class);
        cors.setInitParameter("allowedOrigins", "*");
        cors.setInitParameter("allowedHeaders", "*");
        cors.setInitParameter("Access-Control-Request-Method", "*");
        cors.setInitParameter("allowedMethods", "OPTIONS,GET,PUT,POST,PATCH,DELETE,HEAD");
        // Add URL mapping
        cors.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");

        // Manually add JMX reporting (Dropwizard regression)
        JmxReporter.forRegistry(env.metrics()).build().start();


        // env.jersey().register(new TodoResource());
        env.jersey().register(new WebExceptionMapper());
    }

    private static class WebExceptionMapper implements ExceptionMapper<WebApplicationException> {
        @Override
        public Response toResponse(WebApplicationException e) {
            // If the message did not come with a status code, we'll default to an internal
            // server error status.
            int status = e.getResponse() == null ? 500 : e.getResponse().getStatus();

            // Get a nice human readable message for our status code if the exception doesn't have
            // a message
            String msg = e.getMessage() == null ? HttpStatus.getMessage(status) : e.getMessage();

            return Response.status(status)
                    .type(MediaType.APPLICATION_JSON_TYPE)
                    .entity(ImmutableMap.of("error", msg))
                    .build();
        }
    }
}


