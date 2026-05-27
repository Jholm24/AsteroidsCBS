package dk.sdu.cbse.main;

import dk.sdu.cbse.common.services.IEntityProcessingService;
import dk.sdu.cbse.common.services.IGamePluginService;
import dk.sdu.cbse.common.services.IPostEntityProcessingService;
import dk.sdu.cbse.commonscoring.IScoringService;
import dk.sdu.cbse.commonscoring.ScoreEntry;
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
        return new dk.sdu.cbse.main.Game(gamePluginServices(), entityProcessingServiceList(), postEntityProcessingServices(), scoringService());
    }

    @Bean
    public List<IEntityProcessingService> entityProcessingServiceList(){
        List<IEntityProcessingService> services = new ArrayList<>(
                ServiceLoader.load(IEntityProcessingService.class).stream().map(ServiceLoader.Provider::get).collect(toList()));
        services.addAll(PluginLoader.loadPluginServices(IEntityProcessingService.class));
        return services;
    }

    @Bean
    public List<IGamePluginService> gamePluginServices() {
        List<IGamePluginService> services = new ArrayList<>(
                ServiceLoader.load(IGamePluginService.class).stream().map(ServiceLoader.Provider::get).collect(toList()));
        services.addAll(PluginLoader.loadPluginServices(IGamePluginService.class));
        return services;
    }

    @Bean
    public List<IPostEntityProcessingService> postEntityProcessingServices() {
        List<IPostEntityProcessingService> services = new ArrayList<>(
                ServiceLoader.load(IPostEntityProcessingService.class).stream().map(ServiceLoader.Provider::get).collect(toList()));
        services.addAll(PluginLoader.loadPluginServices(IPostEntityProcessingService.class));
        return services;
    }

    @Bean
    public IScoringService scoringService() {
        List<IScoringService> services = new ArrayList<>(
                ServiceLoader.load(IScoringService.class).stream().map(ServiceLoader.Provider::get).collect(toList()));
        services.addAll(PluginLoader.loadPluginServices(IScoringService.class));
        if (!services.isEmpty()) return services.get(0);
        // Fallback: no-op if ScoreClient is unavailable
        return new IScoringService() {
            public void addScore(int a, long s) {}
            public List<ScoreEntry> getTopScores() { return List.of(); }
        };
    }
}
