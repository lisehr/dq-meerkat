package dqm.jku.trustkg.pentaho;

public enum FileOutputType {
	csv("CSV"),
	json("JSON"),
	text("txt"),
	none("None");

  private String label; // the label of the type (string representation)
	
	FileOutputType(String string) {
		this.setLabel(string);
	}

	public String label() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
	
	public static String[] getTypes() {
		return new String[]{none.label, csv.label, json.label, text.label};
	}

}
