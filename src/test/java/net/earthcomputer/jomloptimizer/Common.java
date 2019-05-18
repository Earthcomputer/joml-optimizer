package net.earthcomputer.jomloptimizer;

import com.google.common.collect.ImmutableMap;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;

import java.io.File;

public class Common {

    public static JomlOptimizerTask createTask() {
        Project project = ProjectBuilder.builder().build();
        project.getPlugins().apply(JomlOptimizerPlugin.class);
        JomlOptimizerTask task = (JomlOptimizerTask) project.getTasks().create(ImmutableMap.of("name", "optimizeJoml", "type", JomlOptimizerTask.class));
        task.setInputJar(new File("@inputJar@"));
        task.setOutputJar(new File("@outputJar@"));
        return task;
    }

}
