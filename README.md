# Java Version Plugin

The Java Version Plugin automatically increases the version of a project. It assumes that [semantic versioning](http://semver.org/) is used and therefore the version number consists of three parts: (1) a major version number, (2) a minor version number and (3) a patch version number. 

It also assumes that you use git for your project as the version control system. The user that executes the plugin (no matter whether its a human or a CI Server such as Jenkins) also has to have the rights to commit to the git repo. 

The plugin supports feature and bugfix branches and can automatically increase through the `increaseVersion` task. The typical workflow for which the plugin has been designed is to develop a new feature on a feature branch or fix a bug on a bugfix branch. When this branch is merged into the master branch the `increaseVersion` task can be called on the master branch.

To be able to automatically increase the version number all necessary information are stored in a `version.properties` file.

## The version.properties file
The `version.properties` file is mandatory and contains all information necessary for the automatic version handling. If you don't have a `version.properties` file yet one is automatically generated. No matter how the file is created it has to have the following structure:

```
version=1.0.0                    # mandatory
version.upgrade=minor            # mandatory
version.snapshot=branch-SNAPSHOT # optional
```

As the comment in the example file above already tells you is the `version` property mandatory. It contains the latest version of the project and should not be edited manually.

The `version.upgrade` property is also mandatory and defines the strategy what part of the version number should be increased. The default value is `minor` which tells the plugin to increase the minor version number. When instead `patch` or `major` is defined, the other parts of the version is increased accordingly. 

When a part of the version is increased everthing after that part is reset to `0. This means when my current version is 1.3.4 and my strategy is major the next version will be 2.0.0.
  
As written above `minor` is the default strategy. If you work on a `bugfix` branch (i.e. a branch that starts with `bugfix`), the strategy is changed to `patch` automatically. This is done in the `setVersion` task which always runs before the `classes` task if this task exists. `

If you don't use the `java` plugin (or any other plugin that creates a `classes` task) you have to include the `setVersion` task in the gradle build lifecycle yourself. You can do this using the following code:

```
setVersion.finalizedBy [theTaskThatShouldRunAfterSetVersion]
```

More information about gradle task dependencies can be found in the [gradle docs](https://docs.gradle.org/current/userguide/tutorial_using_tasks.html#sec:task_dependencies).

When you know you need to increase the `major` version number you have to declare that yourself in the `version.properties` file.

After the version was increased the default strategy is restored and the changed `version.properties` file is committed again.

The `version.snapshot` property holds the version of the current snapshot branch. Also this property is optional and is only set on branches that are not the master branch. 

By default the name consists of the branch name (excluding the part before the `/`, i.e. without the bugfix or feature part) and a `-SNAPSHOT` suffix but you can set the snapshot version yourself, too. If this property is set, this version is used to set the project version.

Similar to the `patch` upgrade strategy also the snapshot version is set automatically when the `setVersion` task ran.

## Suggested Workflow
It is a good idea to use a CI Server to build your artifacts. In order to automatically increase the version of your project, you should create two tasks: (1) one to increase the version and (2) one to build the artifact. 

To increment the version number the first task should execute `./gradlew increaseVersion`. This will increment the version number according to the `version.upgrade` property and commit the changed file back to the git repository.

The second task should then build the artifact using something like `./gradlew clean build`. To avoid lots of manual steps it can be triggered automatically after the first tasks has completed successfully. 

### Why is it this way?
The reason why you need two tasks is gradles execution lifecycle. As you might know gradle has [three build phases](https://docs.gradle.org/current/userguide/build_lifecycle.html#sec:build_phases). During the first phase the projects gets initialized, in the second phase the tasks get configured and in the third phase all tasks are executed.

Some tasks (such as the jar or the mave-publish task) need the new version number in the configuration phase but we can only increase it in the execution phase since we only know in the execution phase whether the `increaseVersion` tasks should be executed or not (all tasks always run through the configuration phase).

This means that we need the increamented version number before we can increment it. Therefore you need to separate these two steps.

## Delegate IDE build tasks to gradle
Since the plugin handles the version of your project the IDE should invoke gradle tasks to build the project. For IntelliJ you can do this by opening the `Preferences` and then active the `Delegate IDE build/run actions to gradle` in the `Build, Execution, Deployment` > `Build Tools` > `Gradle` > `Runner` submenu.
 
![](assets/intellij-settings.jpg)
