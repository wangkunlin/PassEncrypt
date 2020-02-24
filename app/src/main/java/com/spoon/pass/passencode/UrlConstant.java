package com.spoon.pass.passencode;

import com.spoon.pass.decode.EncodeField;
import com.spoon.pass.decode.Encode;

/**
 * Created by wangkunlin
 * On 2020-02-21
 */
@Encode("mypassword")
class UrlConstant {

    @EncodeField("https://www.google.com")
    String CONFIG_URL = "";

    @EncodeField("https://www.google.com1")
    String new_url = "";

    @EncodeField("https://www.google.com2")
    String new_url1 = "";

    @EncodeField("https://www.google.com3")
    String new_url2 = "";
}
