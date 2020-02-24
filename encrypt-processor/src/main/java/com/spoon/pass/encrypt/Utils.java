package com.spoon.pass.encrypt;

import com.spoon.pass.decode.EncodeField;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;

/**
 * Created by wangkunlin
 * On 2020-02-22
 */
class Utils {

    static boolean isValidClass(Element element) {
        return element.getKind() == ElementKind.CLASS;
    }

    static boolean isValidField(Element element) {
        if (element.getKind() != ElementKind.FIELD || !(element instanceof VariableElement)) {
            return false;
        }

        EncodeField fieldAnno = element.getAnnotation(EncodeField.class);
        return fieldAnno != null;
    }

    static String getPackageName(Elements elements, TypeElement typeElement) {
        return elements.getPackageOf(typeElement).getQualifiedName().toString();
    }

    static String getSimpleName(TypeElement typeElement) {
        return typeElement.getSimpleName().toString();
    }

}
