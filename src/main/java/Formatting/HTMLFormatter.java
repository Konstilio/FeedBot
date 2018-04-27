package Formatting;

public class HTMLFormatter implements ITextFormatter {

    @Override
    public String MakeBold(String text) {
        return "<b>" + text + "<b>";
    }

    @Override
    public String MakeItalic(String text) {
        return "<i>" + text + "<i>";
    }

    @Override
    public String NewLine() {
        return "\n";
    }

    @Override
    public String OpenTag() {
        return "<";
    }

    @Override
    public String CloseTag() {
        return ">";
    }

    @Override
    public String HyperlinkSeparator() {
        return "";
    }

    @Override
    public boolean SupportsHyperlinking() {
        return false;
    }
}
