package ar.edu.utn.frba.dds.server;

import ar.edu.utn.frba.dds.middelwares.AuthMiddleware;
import ar.edu.utn.frba.dds.server.handlers.AppHandlers;
import ar.edu.utn.frba.dds.utils.Initializer;
import ar.edu.utn.frba.dds.utils.JavalinRenderer;
import ar.edu.utn.frba.dds.utils.PrettyProperties;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import io.javalin.Javalin;
import io.javalin.config.JavalinConfig;
import io.javalin.http.HttpStatus;

import java.io.IOException;
import java.util.function.Consumer;

public class Server {
    private static Javalin app = null;

    public static Javalin app() {
        if (app == null)
            throw new RuntimeException("App no inicializada");
        return app;
    }

    public static void init() throws Exception {
        if (app == null) {
            Integer port = Integer.parseInt(PrettyProperties.getInstance().propertyFromName("server_port"));
            app = Javalin.create(config()).start(port);


            AuthMiddleware.apply(app);
            AppHandlers.applyHandlers(app);
            Router.init(app);

            if (Boolean.parseBoolean(PrettyProperties.getInstance().propertyFromName("dev_mode"))) {
                //acá se inicializan datos de prueba
                   Initializer.init("../2024-tpa-mi-no-grupo-14/DB.sql", "jdbc:mysql://localhost:3306/tpanualdds?serverTimezone=America/Argentina/Buenos_Aires",
                      "root", "123456");
            }
        }
    }

    private static Consumer<JavalinConfig> config() {
        return config -> {
            config.staticFiles.add(staticFiles -> {
                staticFiles.hostedPath = "/";
                staticFiles.directory = "/public";
            });

            config.fileRenderer(new JavalinRenderer().register("hbs", (path, model, context) -> {
                Handlebars handlebars = new Handlebars();
                Template template = null;
                try {
                    template = handlebars.compile(
                            "templates/" + path.replace(".hbs", ""));
                    return template.apply(model);
                } catch (IOException e) {
                    e.printStackTrace();
                    context.status(HttpStatus.NOT_FOUND);
                    return "No se encuentra la página indicada...";
                }
            }));
        };
    }
}