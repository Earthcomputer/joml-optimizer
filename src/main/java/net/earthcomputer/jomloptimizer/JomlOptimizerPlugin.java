package net.earthcomputer.jomloptimizer;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class JomlOptimizerPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        project.getExtensions().create("jomlOptimizer", JomlOptimizerExtension.class);
    }
}
