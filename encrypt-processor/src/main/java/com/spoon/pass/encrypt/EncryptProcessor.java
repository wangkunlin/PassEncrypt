package com.spoon.pass.encrypt;

import com.spoon.pass.decode.Encode;

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
public class EncryptProcessor extends AbstractProcessor {

    private Elements mElements;

    private DecryptCodeGenerator mCodeGenerator;

    @Override
    public synchronized void init(ProcessingEnvironment environment) {
        super.init(environment);
        mElements = environment.getElementUtils();
        Filer filer = environment.getFiler();
        mCodeGenerator = new DecryptCodeGenerator(mElements, filer);
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_6;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotations = new HashSet<>();
        annotations.add(Encode.class.getCanonicalName());
        return annotations;
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment environment) {
        Set<? extends Element> elements = environment.getElementsAnnotatedWith(Encode.class);
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
