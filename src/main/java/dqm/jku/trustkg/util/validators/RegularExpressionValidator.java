package dqm.jku.trustkg.util.validators;

import java.util.regex.Pattern;

import dqm.jku.trustkg.dsd.elements.Attribute;
import dqm.jku.trustkg.dsd.records.Record;

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
