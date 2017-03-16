# MavenPlugin
my template for maven plugin

example usage:

<build>
    <plugins>
        <plugin>
            <groupId>sample.plugin</groupId>
            <artifactId>myfilter-maven-plugin</artifactId>
            <version>1.0-SNAPSHOT</version>
            <configuration>
                <fatal>true</fatal>
                <words>
                    <param>BadWord1</param>
                    <param>BadWord2</param>
                </words>
            </configuration>
            <executions>
                <execution>
                    <phase>compile</phase>
                    <goals>
                        <goal>filterCode</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
