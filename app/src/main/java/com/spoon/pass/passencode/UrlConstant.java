package com.spoon.pass.passencode;

import com.spoon.pass.encrypt.Encrypt;
import com.spoon.pass.encrypt.EncryptField;

/**
 * Created by wangkunlin
 * On 2020-02-21
 */
@Encrypt("mypassword")
class UrlConstant {

    @EncryptField("https://www.google.com")
    String CONFIG_URL = "";

    @EncryptField("https://www.google.com1")
    String new_url = "";

    @EncryptField("https://www.google.com2")
    String new_url1 = "";

    @EncryptField("https://www.google.com3")
    String new_url2 = "";
}
