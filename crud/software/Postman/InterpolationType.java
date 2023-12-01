package crud.software.Postman;

public enum InterpolationType {
    PHP("${", "}", false),
    DART("$", "", true);

    private final String prefix;
    private final String suffix;
	private final boolean camelCase;

    InterpolationType(String prefix, String suffix, boolean camelCase) {
        this.prefix = prefix;
        this.suffix = suffix;
		this.camelCase = camelCase;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getSuffix() {
        return suffix;
    }
	
	public boolean isCamelCase() {
        return camelCase;
    }
}