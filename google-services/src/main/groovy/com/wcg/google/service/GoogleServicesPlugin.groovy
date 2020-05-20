package com.wcg.google.service

import com.android.build.gradle.AppPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project

import java.util.regex.Matcher
import java.util.regex.Pattern

class GoogleServicesPlugin implements Plugin<Project> {

    private final static String JSON_FILE_NAME = 'google-services.json'

    private final static Pattern VARIANT_PATTERN = ~/(?:([^\p{javaUpperCase}]+)((?:\p{javaUpperCase}[^\p{javaUpperCase}]*)*)\/)?([^\/]*)/

    private final static Pattern FLAVOR_PATTERN = ~/(\p{javaUpperCase}[^\p{javaUpperCase}]*)/

    @Override
    void apply(Project project) {

        def hasAppPlugin = project.plugins.hasPlugin AppPlugin

        if (!hasAppPlugin) {
            throw new IllegalStateException("The 'android' plugin is required.")
        }
        project.afterEvaluate {
            project.android.applicationVariants.all { variant ->
                boolean debuggable = variant.buildType.debuggable
                if (!debuggable) {
                    handleVariant(project, variant)
                }
            }
        }

    }

    private static void handleVariant(Project project, def variant) {

        File quickstartFile = null
        List<String> fileLocations = getJsonLocations("$variant.dirName", project)
        String searchedLocation = System.lineSeparator()
        for (String location : fileLocations) {
            File jsonFile = project.file(location + '/' + JSON_FILE_NAME)
            searchedLocation = searchedLocation + jsonFile.getPath() + System.lineSeparator()
            if (jsonFile.isFile()) {
                quickstartFile = jsonFile
                break
            }
        }

        if (quickstartFile == null) {
            quickstartFile = project.file(JSON_FILE_NAME)
            searchedLocation = searchedLocation + quickstartFile.getPath()
        }

        File outputDir =
                project.file("$project.buildDir/generated/res/google-services/$variant.dirName")

        GoogleServicesTask task = project.tasks
                .create("process${variant.name.capitalize()}GoogleServices",
                        GoogleServicesTask)

        task.quickstartFile = quickstartFile
        task.intermediateDir = outputDir
        task.searchedLocation = searchedLocation

        // This is neccesary for backwards compatibility with versions of gradle that do not support
        // this new API.
        if (variant.metaClass.respondsTo(variant, "applicationIdTextResource")
                || variant.metaClass.hasProperty(variant, "applicationIdTextResource")) {
            task.packageNameXOR2 = variant.applicationIdTextResource
            task.dependsOn(variant.applicationIdTextResource)
        } else {
            task.packageNameXOR1 = variant.applicationId
        }

        // Use the target version for the task.
        variant.registerResGeneratingTask(task, outputDir)
    }

    static List<String> getJsonLocations(String variantDirname, Project project) {
        Matcher variantMatcher = VARIANT_PATTERN.matcher(variantDirname)
        List<String> fileLocations = new ArrayList<>()
        if (!variantMatcher.matches()) {
            project.getLogger().warn("$variantDirname failed to parse into flavors. Please start " +
                    "all flavors with a lowercase character")
            fileLocations.add("src/$variantDirname")
            return fileLocations
        }
        List<String> flavorNames = new ArrayList<>()
        if (variantMatcher.group(1) != null) {
            flavorNames.add(variantMatcher.group(1).toLowerCase())
        }
        flavorNames.addAll(splitVariantNames(variantMatcher.group(2)))
        String buildType = variantMatcher.group(3)
        String flavorName = "${variantMatcher.group(1)}${variantMatcher.group(2)}"
        fileLocations.add("src/$flavorName/$buildType")
        fileLocations.add("src/$buildType/$flavorName")
        fileLocations.add("src/$flavorName")
        fileLocations.add("src/$buildType")
        fileLocations.add("src/$flavorName${buildType.capitalize()}")
        fileLocations.add("src/$buildType")
        String fileLocation = "src"
        for (String flavor : flavorNames) {
            fileLocation += "/$flavor"
            fileLocations.add(fileLocation)
            fileLocations.add("$fileLocation/$buildType")
            fileLocations.add("$fileLocation${buildType.capitalize()}")
        }
        fileLocations.unique().sort { a, b -> countSlashes(b) <=> countSlashes(a) }
        return fileLocations
    }

    private static List<String> splitVariantNames(String variant) {
        if (variant == null) {
            return Collections.emptyList()
        }
        List<String> flavors = new ArrayList<>()
        Matcher flavorMatcher = FLAVOR_PATTERN.matcher(variant)
        while (flavorMatcher.find()) {
            String match = flavorMatcher.group(1)
            if (match != null) {
                flavors.add(match.toLowerCase())
            }
        }
        return flavors
    }

    private static long countSlashes(String input) {
        return input.codePoints().filter { x -> x == '/' }.count()
    }
}
