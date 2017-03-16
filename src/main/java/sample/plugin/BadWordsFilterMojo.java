package sample.plugin;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;

@Mojo(name = "filterCode",
        executionStrategy = "always",
        threadSafe = true,
        defaultPhase = LifecyclePhase.PROCESS_SOURCES)
public class BadWordsFilterMojo extends AbstractMojo {

    @Parameter(property = "words", required = true)
    private List<String> words;

    @Parameter(property = "directory", defaultValue = "${project.build.sourceDirectory}")
    private File directory;

    @Parameter(property = "include", defaultValue = ".java")
    private String include;

    @Parameter(property = "encoding", defaultValue = "${project.build.sourceEncoding}")
    private String encoding;

    @Parameter(property = "fatal")
    private boolean fatal = false;


    public void execute() throws MojoExecutionException {
        final StringBuilder reports = new StringBuilder();
        try {
            final Charset charset = getCharset();
            final PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:**/*" + include);

            getLog().info("Scanning files in directory: " + directory);
            Files.walkFileTree(Paths.get(directory.toURI()), new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    FileVisitResult result = super.visitFile(file, attrs);
                    if (matcher.matches(file)) {
                        getLog().info("Scanning file: " + file);

                        List<String> lines = Files.readAllLines(file, charset);
                        for (int i = 0; i < lines.size(); i++) {
                            String line = lines.get(i);
                            for (String word : words) {
                                if (line.contains(word)) {
                                    String report = report(file, i + 1, word);
                                    if (fatal == true) {
                                        throw new IOException(report);
                                    }
                                    reports.append(report);
                                }
                            }
                        }
                    }
                    return result;
                }
            });
            if (reports.length() != 0) {
                getLog().error("=========================================");
                getLog().error(reports.toString());
            }
        } catch (Exception e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }

    }

    private String report(Path file, int line, String word) {
        return file + ":" + line + " contains " + word + "\n";
    }

    private Charset getCharset() {
        if (encoding == null) {
            return Charset.defaultCharset();
        } else {
            return Charset.forName(encoding);
        }
    }
}
