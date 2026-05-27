package dk.sdu.cbse.main;

import dk.sdu.cbse.common.services.IEntityProcessingService;
import dk.sdu.cbse.common.services.IGamePluginService;
import dk.sdu.cbse.common.services.IPostEntityProcessingService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

import static java.util.stream.Collectors.toList;


@Configuration
class ModuleConfig {

    public ModuleConfig() {
    }

    @Bean
    public dk.sdu.cbse.main.Game game(){
        return new dk.sdu.cbse.main.Game(gamePluginServices(), entityProcessingServiceList(), postEntityProcessingServices());
    }

    @Bean
    public List<IEntityProcessingService> entityProcessingServiceList(){
        // Collect from boot layer (mods-mvn) + child layer (plugins/)
        List<IEntityProcessingService> services = new ArrayList<>(
                ServiceLoader.load(IEntityProcessingService.class).stream().map(ServiceLoader.Provider::get).collect(toList()));
        services.addAll(PluginLoader.loadPluginServices(IEntityProcessingService.class));
        return services;
    }

    @Bean
    public List<IGamePluginService> gamePluginServices() {
        // Collect from boot layer (mods-mvn) + child layer (plugins/)
        List<IGamePluginService> services = new ArrayList<>(
                ServiceLoader.load(IGamePluginService.class).stream().map(ServiceLoader.Provider::get).collect(toList()));
        services.addAll(PluginLoader.loadPluginServices(IGamePluginService.class));
        return services;
    }

    @Bean
    public List<IPostEntityProcessingService> postEntityProcessingServices() {
        // Collect from boot layer (mods-mvn) + child layer (plugins/)
        List<IPostEntityProcessingService> services = new ArrayList<>(
                ServiceLoader.load(IPostEntityProcessingService.class).stream().map(ServiceLoader.Provider::get).collect(toList()));
        services.addAll(PluginLoader.loadPluginServices(IPostEntityProcessingService.class));
        return services;
    }
}
