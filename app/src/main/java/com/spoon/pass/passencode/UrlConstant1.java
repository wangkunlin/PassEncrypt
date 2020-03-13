package com.spoon.pass.passencode;

import com.spoon.pass.encrypt.Encrypt;
import com.spoon.pass.encrypt.EncryptField;

/**
 * Created by wangkunlin
 * On 2020-02-21
 */
@Encrypt(randomPsw = true, password = "testpsw")
class UrlConstant1 {

    @EncryptField(src = "https://www.google.com", noDecrypt = true)
    String CONFIG_URL = "";

    @EncryptField(src = "https://www.google.com1", password = "1234")
    String new_url = "";

    @EncryptField(src = "https://www.google.com2")
    String new_url1 = "";

    @EncryptField(src = "https://www.google.com3")
    String new_url2 = "";

    @EncryptField(src = "https://www.baidu.com")
    String new_url3 = "";
}
