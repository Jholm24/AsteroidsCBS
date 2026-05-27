package dk.sdu.cbse.main;

import java.io.File;
import java.lang.module.Configuration;
import java.lang.module.ModuleFinder;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Dynamically loads JPMS modules from the plugins/ folder into a child ModuleLayer.
 *
 * This resolves split package conflicts: modules in child layers are isolated from
 * the boot layer, so two modules exporting the same package (one in boot, one in
 * a child layer) no longer cause a ResolutionException.
 */
public class PluginLoader {

    private static ModuleLayer pluginLayer = null;

    /**
     * Builds (once) a child ModuleLayer from all JARs in the plugins/ directory,
     * then returns only the providers found in that child layer (not re-scanning boot).
     */
    public static <T> List<T> loadPluginServices(Class<T> serviceClass) {
        if (pluginLayer == null) {
            pluginLayer = buildPluginLayer();
        }

        // If no child layer was created, there are no plugin-specific services
        if (pluginLayer == ModuleLayer.boot()) {
            return List.of();
        }

        // Only return providers whose module lives in the child layer itself,
        // not the boot layer — ServiceLoader.load(layer,...) also searches parents,
        // which would cause boot-layer services to be added a second time.
        Set<String> pluginModuleNames = pluginLayer.modules().stream()
                .map(Module::getName)
                .collect(Collectors.toSet());

        return ServiceLoader.load(pluginLayer, serviceClass)
                .stream()
                .filter(p -> pluginModuleNames.contains(p.type().getModule().getName()))
                .map(ServiceLoader.Provider::get)
                .collect(Collectors.toList());
    }

    private static ModuleLayer buildPluginLayer() {
        Path pluginsDir = Path.of(System.getProperty("user.dir"), "plugins");
        File dir = pluginsDir.toFile();

        if (!dir.exists() || !dir.isDirectory()) {
            System.out.println("PluginLoader: No plugins/ directory found at " + pluginsDir);
            return ModuleLayer.boot();
        }

        File[] jarFiles = dir.listFiles((f, name) -> name.endsWith(".jar"));
        if (jarFiles == null || jarFiles.length == 0) {
            System.out.println("PluginLoader: No JARs found in plugins/");
            return ModuleLayer.boot();
        }

        Path[] jarPaths = Arrays.stream(jarFiles).map(File::toPath).toArray(Path[]::new);
        ModuleFinder finder = ModuleFinder.of(jarPaths);

        List<String> moduleNames = finder.findAll().stream()
                .map(ref -> ref.descriptor().name())
                .collect(Collectors.toList());

        System.out.println("PluginLoader: Loading plugin modules from child layer: " + moduleNames);

        ModuleLayer bootLayer = ModuleLayer.boot();
        Configuration config = bootLayer.configuration()
                .resolve(finder, ModuleFinder.of(), moduleNames);

        ModuleLayer layer = bootLayer.defineModulesWithOneLoader(
                config, ClassLoader.getSystemClassLoader());

        System.out.println("PluginLoader: Child ModuleLayer created successfully");
        return layer;
    }
}
