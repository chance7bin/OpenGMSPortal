package njgis.opengms.portal.enums;

/**
 * @Description 语言枚举类
 * @Author kx
 * @Date 21/10/13
 * @Version 1.0.0
 */
public enum LanguageEnum {

    Afrikaans("af","Afrikaans"),
    Albanian("sq", "Albanian"),
    Arabic("ar", "Arabic"),
    Armenian("hy", "Armenian"),
    Azeri("az", "Azeri"),
    Basque("eu", "Basque"),
    Belarusian("be", "Belarusian"),
    Bulgarian("bg", "Bulgarian"),
    Catalan("ca", "Catalan"),
    Chinese("zh", "Chinese"),
    Croatian("hr", "Croatian"),
    Czech("cs", "Czech"),
    Danish("da", "Danish"),
    Divehi("dv", "Divehi"),
    Dutch("nl", "Dutch"),
    English("en", "English"),
    Esperanto("eo", "Esperanto"),
    Estonian("et", "Estonian"),
    FYRO_Macedonian("mk", "FYRO Macedonian"),
    Faroese("fo", "Faroese"),
    Farsi("fa", "Farsi"),
    Finnish("fi", "Finnish"),
    French("fr", "French"),
    Galician("gl", "Galician"),
    Georgian("ka", "Georgian"),
    German("de", "German"),
    Greek("el", "Greek"),
    Gujarati("gu", "Gujarati"),
    Hebrew("he", "Hebrew"),
    Hindi("hi", "Hindi"),
    Hungarian("hu", "Hungarian"),
    Icelandic("is", "Icelandic"),
    Indonesian("id", "Indonesian"),
    Italian("it", "Italian"),
    Japanese("ja", "Japanese"),
    Kannada("kn", "Kannada"),
    Kazakh("kk", "Kazakh"),
    Konkani("kok", "Konkani"),
    Korean("ko", "Korean"),
    Kyrgyz("ky", "Kyrgyz"),
    Latvian("lv", "Latvian"),
    Lithuanian("lt", "Lithuanian"),
    Malay("ms", "Malay"),
    Maltese("mt", "Maltese"),
    Maori("mi", "Maori"),
    Marathi("mr", "Marathi"),
    Mongolian("mn", "Mongolian"),
    Northern_Sotho("ns", "Northern Sotho"),
    Norwegian("nb", "Norwegian"),
    Pashto("ps", "Pashto"),
    Polish("pl", "Polish"),
    Portuguese("pt", "Portuguese"),
    Punjabi("pa", "Punjabi"),
    Quechua("qu", "Quechua"),
    Romanian("ro", "Romanian"),
    Russian("ru", "Russian"),
    Sami("se", "Sami"),
    Sanskrit("sa", "Sanskrit"),
    Serbian("sr", "Serbian"),
    Slovak("sk", "Slovak"),
    Slovenian("sl", "Slovenian"),
    Spanish("es", "Spanish"),
    Swahili("sw", "Swahili"),
    Swedish("sv", "Swedish"),
    Syriac("syr","Syriac"),
    Tagalog("tl", "Tagalog"),
    Tamil("ta", "Tamil"),
    Tatar("tt", "Tatar"),
    Telugu("te", "Telugu"),
    Thai("th", "Thai"),
    Tsonga("ts", "Tsonga"),
    Tswana("tn", "Tswana"),
    Turkish("tr", "Turkish"),
    Ukrainian("uk", "Ukrainian"),
    Urdu("ur", "Urdu"),
    Uzbek("uz", "Uzbek"),
    Vietnamese("vi", "Vietnamese"),
    Welsh("cy", "Welsh"),
    Xhosa("xh", "Xhosa"),
    Zulu("zu", "Zulu");

    private String code;
    private String name;

    LanguageEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode(){
        return this.code;
    }

    public String getName(){
        return this.getName();
    }

    public static LanguageEnum getLanguageByCode(String code){
        for(LanguageEnum languageEnum:LanguageEnum.values()){
            if(languageEnum.name().toUpperCase().equals(code.toUpperCase())){
                return languageEnum;
            }
        }
        return null;
    }
}
