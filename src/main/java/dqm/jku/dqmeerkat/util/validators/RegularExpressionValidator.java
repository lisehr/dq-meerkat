package dqm.jku.dqmeerkat.util.validators;

import java.util.regex.Pattern;

import dqm.jku.dqmeerkat.dsd.elements.Attribute;
import dqm.jku.dqmeerkat.dsd.records.Record;

public class RegularExpressionValidator extends StringValidator {

	private Pattern regex;

	public RegularExpressionValidator(Attribute a, String regex, boolean patternCaseInsensitive) {
		super(a);
		this.regex = !patternCaseInsensitive ? Pattern.compile(regex)
				: Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
	}

	@Override
	public boolean validate(Record r) {
		if (getValue(r) == null)
			return false;
		return regex.matcher(getValue(r)).matches();
	}

}
