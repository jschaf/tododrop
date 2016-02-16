package tododrop.conf;

import org.jooq.DSLContext;
import ru.vyarus.dropwizard.guice.module.support.DropwizardAwareModule;
import tododrop.TodoConfig;

public final class Module extends DropwizardAwareModule<TodoConfig> {

    @Override
    protected void configure() {
        bootstrap();
        configuration();
        bind(DSLContext.class).toProvider(DSLContextProvider.class);
        bindConstant().annotatedWith(Annotations.AppURL.class).to(configuration().getAppUrl());
    }
}
