package tododrop;


import com.codahale.metrics.JmxReporter;
import com.github.rholder.retry.*;
import com.google.common.collect.ImmutableMap;
import io.dropwizard.Application;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.flyway.FlywayBundle;
import io.dropwizard.flyway.FlywayFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.flywaydb.core.api.FlywayException;
import ru.vyarus.dropwizard.guice.GuiceBundle;
import ru.vyarus.dropwizard.guice.module.installer.feature.ManagedInstaller;
import ru.vyarus.dropwizard.guice.module.installer.feature.TaskInstaller;
import ru.vyarus.dropwizard.guice.module.installer.feature.jersey.ResourceInstaller;
import tododrop.conf.Module;
import tododrop.database.DBMigrationBundle;
import tododrop.resources.TodoResource;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import java.util.EnumSet;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public class TodoApp extends Application<TodoConfig> {


    public static void main(String[] args) throws Exception {


        // Convenience
        if (args.length == 0) {
            new TodoApp().run("server", "tododrop.yml");
            return;
        }

        // FIXME: how can we make the return type Void or ?
        final Callable<Object> appCallable = () -> {
            new TodoApp().run(args);
            return null;
        };

        final Retryer<Object> retryer = RetryerBuilder.newBuilder()
                .retryIfExceptionOfType(FlywayException.class)
                .retryIfRuntimeException()
                .withStopStrategy(StopStrategies.stopAfterAttempt(20))
                .withWaitStrategy(WaitStrategies.fixedWait(2, TimeUnit.SECONDS))
                .build();

        try {
            retryer.call(appCallable);
        } catch (RetryException e) {
            e.printStackTrace();
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

        // Run database migrations on app start.
        bootstrap.addBundle(new DBMigrationBundle());

        // Enable variable substitution with environment variables
        // http://www.dropwizard.io/0.9.2/docs/manual/core.html#environment-variables
        bootstrap.setConfigurationSourceProvider(
                new SubstitutingSourceProvider(bootstrap.getConfigurationSourceProvider(),
                        new EnvironmentVariableSubstitutor(false)
                ));

        // Dependency Injection
        // https://github.com/xvik/dropwizard-guicey
        bootstrap.addBundle(GuiceBundle.<TodoConfig>builder()
                .installers(ResourceInstaller.class, TaskInstaller.class, ManagedInstaller.class)
                .extensions(TodoResource.class)
                .modules(new Module())
                .build());

        // HTML
        bootstrap.addBundle(new AssetsBundle("/assets/", "/", "index.html", "root"));
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

        // Move REST api to /api so we can serve assets on /.  See
        // https://github.com/dropwizard/dropwizard/issues/661
        env.jersey().setUrlPattern("/api/*");
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


