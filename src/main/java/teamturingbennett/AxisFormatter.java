package teamturingbennett;

import javafx.util.StringConverter;

public class AxisFormatter extends StringConverter<Number> {

    public AxisFormatter() {
    }

    @Override
    public String toString(Number n) {
        if (n.intValue() != n.doubleValue()) {
            return "";
        } else {
            return "" + (n.intValue());
        }
    }
    @Override
    public Number fromString(String s) {
        Number val = Double.parseDouble(s);
        return val.intValue();
    }
}
