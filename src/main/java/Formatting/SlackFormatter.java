package Formatting;

public class SlackFormatter implements ITextFormatter{

    @Override
    public String MakeBold(String text) {
        return '*' + text + '*';
    }

    @Override
    public String MakeItalic(String text) {
        return "_" + text + "_";
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
        return "|";
    }

    @Override
    public boolean SupportsHyperlinking() {
        return true;
    }
}
