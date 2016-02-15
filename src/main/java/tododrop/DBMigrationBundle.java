package tododrop;

import io.dropwizard.ConfiguredBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.flywaydb.core.Flyway;

class DBMigrationBundle implements ConfiguredBundle<TodoConfig> {
    @Override
    public void run(TodoConfig config, Environment env) throws Exception {
        Flyway flyway = new Flyway();
        flyway.setDataSource(config.getDataSourceFactory().getUrl(),
                config.getDataSourceFactory().getUser(),
                config.getDataSourceFactory().getPassword());
        flyway.migrate();
    }

    @Override
    public void initialize(Bootstrap<?> bootstrap) {
    }
}
