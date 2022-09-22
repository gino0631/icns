# ICNS
[![Build Status](https://api.travis-ci.com/gino0631/icns.svg?branch=master)](https://app.travis-ci.com/gino0631/icns)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.gino0631/icns-maven-plugin/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.gino0631/icns-maven-plugin)

A pure Java implementation of tools for working with [Apple Icon Image format](https://en.wikipedia.org/wiki/Apple_Icon_Image_format) icons.

# Requirements
* Maven 3
* Java 8

# Usage
The tools consist of a Java library and plugins for build systems (currently, only Maven is supported).

## Maven plugin
The first step is to add the plugin to your project:
```xml
<project>
  ...
  <build>
    <!-- To define the plugin version in your parent POM -->
    <pluginManagement>
        <plugin>
          <groupId>com.github.gino0631</groupId>
          <artifactId>icns-maven-plugin</artifactId>
          <version>...</version>
        </plugin>
        ...
      </plugins>
    </pluginManagement>
    <!-- To use the plugin goals in your POM or parent POM -->
    <plugins>
      <plugin>
        <groupId>com.github.gino0631</groupId>
        <artifactId>icns-maven-plugin</artifactId>
        <executions>
          <execution>
            <id>create-macosx-icons</id>
            <phase>generate-resources</phase>
            <goals><goal>icns</goal></goals>
            <configuration>
              <outputFile>${project.build.directory}/product/macosx-x64/Applications/${product.name}.app/Contents/Resources/Main.icns</outputFile>
              <icons>
                <icon>icons/main_window_128.png</icon>
              </icons>
            </configuration>
          </execution>
        </executions>
      </plugin>
    ...
    </plugins>
    ...
  </build>
  ...
</project>
```

It is necessary to specify `icons` and `outputFile` parameters, which define source icon set to use, and an ICNS file to produce.

## Standalone library
Add a dependency on `com.github.gino0631:icns-core` to your project, and use `IcnsIcons`, `IcnsBuilder`, and `IcnsParser` classes.
