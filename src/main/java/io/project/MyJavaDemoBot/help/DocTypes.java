package io.project.MyJavaDemoBot.help;

public enum DocTypes {
    DOCX("application/vnd.openxmlformats-officedocument.wordprocessingml.document"),
    DOC("application/msword"),
    RAR("application/vnd.rar"),
    ZIP("application/zip"),
    RTF("application/rtf"),
    PDF("application/pdf"),
    TXT("text/plain");
    private final String value;

    DocTypes(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
