package Formatting;

public class SlackFormatter implements ITextFormatter{

    @Override
    public String MakeBold(String text) {
        String lines[] = text.split("\\u000D\\u000A|[\\u000A\\u000B\\u000C\\u000D\\u0085\\u2028\\u2029]");
        StringBuilder Builder = new StringBuilder();
        for(String line : lines) {
            Builder.append('*' + line + '*' + '\n');
        }

        return Builder.toString();
    }

    @Override
    public String MakeItalic(String text) {
        String lines[] = text.split("\\u000D\\u000A|[\\u000A\\u000B\\u000C\\u000D\\u0085\\u2028\\u2029]");
        StringBuilder Builder = new StringBuilder();
        for(String line : lines) {
            Builder.append('_' + line + '_' + '\n');
        }

        return Builder.toString();
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
