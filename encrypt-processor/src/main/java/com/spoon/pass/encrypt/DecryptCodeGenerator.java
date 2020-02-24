package com.spoon.pass.encrypt;

import com.spoon.pass.decode.Encode;
import com.spoon.pass.decode.EncodeDecode;
import com.spoon.pass.decode.EncodeField;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;

/**
 * Created by wangkunlin
 * On 2020-02-22
 */
class DecryptCodeGenerator {

    private Elements mElements;
    private Filer mFiler;

    DecryptCodeGenerator(Elements elements, Filer filer) {
        mElements = elements;
        mFiler = filer;
    }

    void generatorCode(TypeElement typeElement, List<VariableElement> fields) {
        String packageName = Utils.getPackageName(mElements, typeElement);
        JavaFile javaFile = JavaFile.builder(packageName, generateTypeCode(typeElement, fields))
                .build();
        try {
            javaFile.writeTo(mFiler);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private TypeSpec generateTypeCode(TypeElement typeElement, List<VariableElement> fields) {
        Encode encode = typeElement.getAnnotation(Encode.class);

        String password = encode.value();

        List<FieldSpec> fieldSpec = generateFieldCode(password, fields);

        return TypeSpec.interfaceBuilder(Utils.getSimpleName(typeElement) + "Encoded")
                .addModifiers(Modifier.PUBLIC)
                .addFields(fieldSpec)
                .build();
    }

    private List<FieldSpec> generateFieldCode(String password, List<VariableElement> fields) {
        List<FieldSpec> fieldSpecs = new ArrayList<>();

        for (VariableElement field : fields) {
            EncodeField fieldAnno = field.getAnnotation(EncodeField.class);
            String toEncode = fieldAnno.value();

            String encodedStr = EncodeDecode.encode(toEncode, password);

            TypeName typeName = TypeName.get(String.class);

            String fieldName = field.getSimpleName().toString();

            FieldSpec.Builder fieldSpec = FieldSpec.builder(typeName, fieldName,
                    Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL);

            fieldSpec.initializer("$1T.decode($2S, $3S)", EncodeDecode.class, encodedStr, password);
            fieldSpec.addJavadoc("$L", toEncode);
            fieldSpecs.add(fieldSpec.build());
        }
        return fieldSpecs;
    }

}
