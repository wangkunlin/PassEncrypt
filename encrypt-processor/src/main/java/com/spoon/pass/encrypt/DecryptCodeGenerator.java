package com.spoon.pass.encrypt;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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

    private static final char[] RANDOM_PASSWORD_CHARS = new char[]{
            'F', 'G', 'H', 'I', 'J', 'K', 'P', 'Q', 'R',
            'S', 'T', 'U', 'V', '4', '5', '6', '7', 'W',
            '\\', '|', '{', '}', 'a', 'b', 'c', 'A', 'B',
            'C', 'D', 'E', '8', '9', 'd', 'e', 'f', 'g',
            'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r',
            's', 'L', 'M', 'N', 'O', 't', 'u', 'v', 'w',
            '1', '2', 'X', 'Y', 'Z', '`', '3', 'x', 'y',
            'z', '0', '+', '/', 'h', 'i', '!', '^', '&',
            '*', '(', ')', ':', ';', '@', '#', '$', '%',
            '\'', '"', ',', '.', '~', '[', ']', '_', '='
    };

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

    private String randomPsw(int length) {

        if (length < 6) {
            length = new Random().nextInt(7) + 6;
        }

        StringBuilder psw = new StringBuilder();
        for (int i = 0; i < length; ++i) {
            int index = new Random().nextInt(RANDOM_PASSWORD_CHARS.length);
            psw.append(RANDOM_PASSWORD_CHARS[index]);
        }
        return psw.toString();
    }

    private TypeSpec generateTypeCode(TypeElement typeElement, List<VariableElement> fields) {
        Encrypt encode = typeElement.getAnnotation(Encrypt.class);

        String typeName = Utils.getSimpleName(typeElement);

        String password = encode.password();

        boolean random = encode.randomPsw();

        if (random) {
            password = randomPsw(encode.pswLength());
        } else {
            if (Utils.textEmpty(password)) {
                password = randomPsw(encode.pswLength());
            }
        }

        List<FieldSpec> fieldSpec = generateFieldCode(password, fields);

        return TypeSpec.interfaceBuilder(typeName + "Encrypted")
                .addModifiers(Modifier.PUBLIC)
                .addFields(fieldSpec)
                .build();
    }

    private List<FieldSpec> generateFieldCode(String password, List<VariableElement> fields) {
        List<FieldSpec> fieldSpecs = new ArrayList<>();

        for (VariableElement field : fields) {
            EncryptField fieldAnno = field.getAnnotation(EncryptField.class);
            String toEncode = fieldAnno.src();

            if (Utils.textEmpty(toEncode)) {
                continue;
            }
            boolean noDecrypt = fieldAnno.noDecrypt();

            String specifyPsw = fieldAnno.password();

            String finalPsw = Utils.textEmpty(specifyPsw) ? password : specifyPsw;

            String encodedStr = EncryptDecrypt.encrypt(toEncode, finalPsw);

            TypeName typeName = TypeName.get(String.class);

            String fieldName = field.getSimpleName().toString();

            FieldSpec.Builder fieldSpec = FieldSpec.builder(typeName, fieldName,
                    Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL);

            if (noDecrypt) {
                fieldSpec.initializer("$S", encodedStr);
            } else {
                fieldSpec.initializer("$1T.decrypt($2S, $3S)", EncryptDecrypt.class, encodedStr, finalPsw);
            }
            fieldSpec.addJavadoc("$L", toEncode);
            fieldSpecs.add(fieldSpec.build());
        }
        return fieldSpecs;
    }

}
