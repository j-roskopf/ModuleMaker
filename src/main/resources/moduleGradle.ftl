<#if isAndroidLibrary?? && isAndroidLibrary == true>
apply plugin: 'com.android.library'
apply plugin: 'kotlin-android'
<#elseif isKotlinLibrary?? && isKotlinLibrary == true>
apply plugin: 'kotlin'
</#if>

dependencies {

}