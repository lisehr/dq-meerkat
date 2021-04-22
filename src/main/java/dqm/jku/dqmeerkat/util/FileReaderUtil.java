package dqm.jku.dqmeerkat.util;

import java.io.BufferedReader;
import java.io.IOException;

public class FileReaderUtil {
    private FileReaderUtil(){};

    private static final String REGEX_CHAR_NO_QOUTATION_MARK = "[^\\\"]";
    private static final String REGEX_QOUTATION_MARK = "\\\"";
    private static final String EVEN_NO_OF_QUOTATION_MARK = REGEX_CHAR_NO_QOUTATION_MARK+
            "*("+REGEX_CHAR_NO_QOUTATION_MARK+"*"+REGEX_QOUTATION_MARK+REGEX_CHAR_NO_QOUTATION_MARK+"*"+REGEX_QOUTATION_MARK+")*"+
            REGEX_CHAR_NO_QOUTATION_MARK+"*";

    public static String readLineEscapingQuotedNewlines(BufferedReader reader, String linebreak) throws IOException {
        String readLine = reader.readLine();

        if (readLine == null) return null;


        StringBuilder readLineSb = new StringBuilder();

        readLineSb.append(readLine);
        while (!readLineSb.toString().matches(EVEN_NO_OF_QUOTATION_MARK)){
            readLineSb.append(linebreak);
            readLineSb.append(reader.readLine());
        }; // if there is not an an even number of " the loop continues
        return readLineSb.toString();
    }
}
