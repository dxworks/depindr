package depindr.utils;

public class ImportUtils {

    public static final String IMPORT_SUFFIX = "([\\.;])([a-zA-Z_0-9]*\\.)*([a-zA-Z_0-9]*|\\*)*(;){0,1}";

    public static String wrapImportPackage(String _import) {
        return "(" + _import.replace(".", "\\.") + ")" + IMPORT_SUFFIX;
    }

}