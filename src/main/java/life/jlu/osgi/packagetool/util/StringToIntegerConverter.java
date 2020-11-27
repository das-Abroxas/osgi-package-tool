package life.jlu.osgi.packagetool.util;

import javafx.util.StringConverter;

/**
 * Custom String to Integer Converter that returns 0 in the fromString() method if String is no number.
 * @author Jannis Hochmuth
 * */
public class StringToIntegerConverter extends StringConverter<Integer> {

    @Override
    public String toString(Integer object) {
        return object + "";
    }

    @Override
    public Integer fromString(String string) {
        try {
    	    return Integer.parseInt(string);

    	} catch (NumberFormatException nfe) {
    	    return 0;
    	}
    }
}
