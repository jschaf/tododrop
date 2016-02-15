package tododrop;

import org.jooq.DSLContext;
import ru.vyarus.dropwizard.guice.module.support.DropwizardAwareModule;

final class Module extends DropwizardAwareModule<TodoConfig> {

    @Override
    protected void configure() {
        bootstrap();
        configuration();
        bind(DSLContext.class).toProvider(DSLContextProvider.class);
        bindConstant().annotatedWith(Annotations.AppURL.class).to(configuration().getAppUrl());
    }
}
