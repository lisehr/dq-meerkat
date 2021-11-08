package dqm.jku.dqmeerkat.util;

import java.io.BufferedReader;
import java.io.IOException;

public class FileReaderUtil {
    private FileReaderUtil(){}

    public static String readLineEscapingQuotedNewlines(BufferedReader reader, String linebreak) throws IOException {
        String readLine = reader.readLine();

        if (readLine == null) return null;


        StringBuilder readLineSb = new StringBuilder();


        readLineSb.append(readLine);

        long noOfQoutationMarks = readLine.chars().filter(c -> c == '"').count(); // get number of " in the String readLine

        while ((noOfQoutationMarks % 2) != 0){
            readLineSb.append(linebreak);
            readLine = reader.readLine();
            noOfQoutationMarks += readLine.chars().filter(c -> c == '"').count();
            readLineSb.append(readLine);
        } // if there is not an an even number of " the loop continues

        return readLineSb.toString();
    }
}
