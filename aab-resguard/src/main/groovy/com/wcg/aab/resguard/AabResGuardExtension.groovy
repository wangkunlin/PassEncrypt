package com.wcg.aab.resguard

import java.nio.file.Path

/**
 * On 2020-10-22
 */
class AabResGuardExtension {
    public Boolean enableObfuscate = true
    public Path mappingFile
    public Set<String> whiteList = new HashSet()
    public String obfuscatedBundleFileName
    public Boolean mergeDuplicatedRes = false
    public Boolean enableFilterFiles = false
    public Set<String> filterList = new HashSet()
    public Boolean enableFilterStrings = false
    public String unusedStringPath = ""
    public Set<String> languageWhiteList = new HashSet()

}
