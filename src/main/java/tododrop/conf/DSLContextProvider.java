package tododrop.conf;

import com.bendb.dropwizard.jooq.JooqFactory;
import com.google.inject.Inject;
import com.google.inject.Provider;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.setup.Environment;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import tododrop.TodoConfig;

class DSLContextProvider implements Provider<DSLContext> {

    private Configuration jooqConfig;


    @Inject
    public DSLContextProvider(TodoConfig config, Environment env) {
        JooqFactory jooqFactory = config.getJooqFactory();
        DataSourceFactory dataSourceFactory = config.getDataSourceFactory();
        try {
            jooqConfig = jooqFactory.build(env, dataSourceFactory, "jooq");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        // register jooq health-check
    }

    @Override
    public DSLContext get() {
        return DSL.using(jooqConfig);
    }
}
