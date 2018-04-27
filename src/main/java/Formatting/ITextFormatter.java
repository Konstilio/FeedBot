package Formatting;

public interface ITextFormatter {

    public String MakeBold(String text);
    public String MakeItalic(String text);
    public String NewLine();
    public String OpenTag();
    public String CloseTag();
    public String HyperlinkSeparator();
    public boolean SupportsHyperlinking();
}
