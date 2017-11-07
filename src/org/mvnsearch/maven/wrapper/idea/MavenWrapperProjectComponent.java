package org.mvnsearch.maven.wrapper.idea;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.apache.maven.wrapper.PathAssembler;
import org.apache.maven.wrapper.WrapperConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.idea.maven.project.MavenGeneralSettings;
import org.jetbrains.idea.maven.project.MavenProjectsManager;

import java.io.File;
import java.net.URI;
import java.util.Properties;

/**
 * maven wrapper project component
 *
 * @author linux_china
 */
public class MavenWrapperProjectComponent implements ProjectComponent {
    /**
     * project
     */
    private Project project;

    public MavenWrapperProjectComponent(Project project) {
        this.project = project;
        initMavenWrapper();
    }

    public void initComponent() {

    }

    public void disposeComponent() {

    }

    @NotNull
    public String getComponentName() {
        return "MavenWrapperProjectComponent";
    }

    /**
     * auto setup project sdk according
     */
    public void projectOpened() {


    }

    public void projectClosed() {

    }

    private void initMavenWrapper() {
        VirtualFile projectBaseDir = project.getBaseDir();
        VirtualFile wrapperMavenDir = projectBaseDir.findChild(".mvn");
        if (wrapperMavenDir != null && wrapperMavenDir.exists()) {
            Properties properties = new Properties();
            try {
                //noinspection ConstantConditions
                properties.load(wrapperMavenDir.findChild("wrapper").findChild("maven-wrapper.properties").getInputStream());
            } catch (Exception ignore) {

            }
            //maven
            if (properties.containsKey("distributionUrl")) {
                String distributionUrl = properties.getProperty("distributionUrl");
                try {
                    WrapperConfiguration wrapperConfiguration = new WrapperConfiguration();
                    wrapperConfiguration.setDistributionBase("MAVEN_USER_HOME");
                    wrapperConfiguration.setDistribution(new URI(distributionUrl));
                    File userHome = new File(System.getProperty("user.home"));
                    PathAssembler assembler = new PathAssembler(new File(userHome, ".m2"));
                    PathAssembler.LocalDistribution distribution = assembler.getDistribution(wrapperConfiguration);
                    File distributionDir = distribution.getDistributionDir();
                    if (distributionDir.exists()) {
                        String mavenDirName = distributionUrl.substring(distributionUrl.lastIndexOf("/") + 1).replace(".zip", "");
                        File mavenHome = new File(distributionDir, mavenDirName);
                        if (mavenHome.exists()) {
                            MavenGeneralSettings generalSettings = MavenProjectsManager.getInstance(project).getGeneralSettings();
                            if (generalSettings != null) {
                                generalSettings.setMavenHome(mavenHome.getAbsolutePath());
                                generalSettings.setUserSettingsFile(mavenHome.getAbsolutePath() + "/conf/settings.xml");
                            }
                        }
                    }
                } catch (Exception ignore) {

                }
            }
        }
    }
}
