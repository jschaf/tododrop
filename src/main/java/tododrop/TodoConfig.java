package tododrop;

import com.bendb.dropwizard.jooq.JooqFactory;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.flyway.FlywayFactory;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class TodoConfig extends Configuration {
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
    private
    @NotEmpty
    String schema;

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
