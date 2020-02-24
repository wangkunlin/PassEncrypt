package com.spoon.pass.encrypt;

import com.sun.source.util.Trees;

import net.ltgt.gradle.incap.IncrementalAnnotationProcessor;
import net.ltgt.gradle.incap.IncrementalAnnotationProcessorType;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;

/**
 * Created by wangkunlin
 * On 2020-02-22
 */
@IncrementalAnnotationProcessor(IncrementalAnnotationProcessorType.DYNAMIC)
public class EncryptProcessor extends AbstractProcessor {

    private Elements mElements;

    private DecryptCodeGenerator mCodeGenerator;

    private Trees mTrees;

    @Override
    public synchronized void init(ProcessingEnvironment environment) {
        super.init(environment);
        mElements = environment.getElementUtils();
        Filer filer = environment.getFiler();
        mCodeGenerator = new DecryptCodeGenerator(mElements, filer);

        try {
            mTrees = Trees.instance(processingEnv);
        } catch (IllegalArgumentException ignored) {
            try {
                // Get original ProcessingEnvironment from Gradle-wrapped one or KAPT-wrapped one.
                for (Field field : processingEnv.getClass().getDeclaredFields()) {
                    if (field.getName().equals("delegate") || field.getName().equals("processingEnv")) {
                        field.setAccessible(true);
                        ProcessingEnvironment javacEnv = (ProcessingEnvironment) field.get(processingEnv);
                        mTrees = Trees.instance(javacEnv);
                        break;
                    }
                }
            } catch (Throwable ignored2) {
            }
        }
    }

    @Override
    public Set<String> getSupportedOptions() {
        Set<String> options = new HashSet<>();
        if (mTrees != null) {
            options.add(IncrementalAnnotationProcessorType.ISOLATING.getProcessorOption());
        }
        return options;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotations = new HashSet<>();
        annotations.add(Encrypt.class.getCanonicalName());
        return annotations;
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment environment) {
        Set<? extends Element> elements = environment.getElementsAnnotatedWith(Encrypt.class);
        for (Element element : elements) {
            if (!Utils.isValidClass(element)) {
                continue;
            }

            List<? extends Element> memberFields = mElements.getAllMembers((TypeElement) element);
            List<VariableElement> annotatedFields = new ArrayList<>();

            if (memberFields == null) {
                return false;
            }

            for (Element member : memberFields) {
                if (Utils.isValidField(member)) {
                    annotatedFields.add((VariableElement) member);
                }
            }

            mCodeGenerator.generatorCode((TypeElement) element, annotatedFields);
        }
        return true;
    }
}
