package beans;

/**
 * 基本条件项类
 * 
 * @author WSL
 *
 */
public class ItemCondition {
	private String nature;
	private String value;
	private String operation;

	public ItemCondition(String nature, String operation, String value) {
		this.nature = nature;
		this.value = value;
		this.operation = operation;
	}

	public String getNature() {
		return nature;
	}

	public void setNature(String nature) {
		this.nature = nature;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

}