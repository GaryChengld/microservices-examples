package io.examples.dropwizard.petstore;

import io.dropwizard.Application;
import io.dropwizard.setup.Environment;

/**
 * Main application class
 *
 * @author Gary Cheng
 */
public class App extends Application<AppConfiguration> {
    public static void main(final String[] args) throws Exception {
        new App().run(args);
    }

    @Override
    public void run(AppConfiguration appConfiguration, Environment environment) throws Exception {

    }
}
