package pt.keep.validator;

import java.util.ArrayList;
import java.util.List;

public class ValidatorOutput {
	
	
	public ValidatorOutput() {
		this.errors = new ArrayList<String>();
		this.warnings = new ArrayList<String>();
		this.valid=true;
		this.odfVersion="";
	}
	
	private List<String> errors;
	private List<String> warnings;
	private boolean valid;
	private String odfVersion;
	
	
	
	
	public String getOdfVersion() {
		return odfVersion;
	}
	public void setOdtVersion(String odtVersion) {
		this.odfVersion = odtVersion;
	}
	public List<String> getErrors() {
		return errors;
	}
	public void setErrors(List<String> errors) {
		this.errors = errors;
	}
	public List<String> getWarnings() {
		return warnings;
	}
	public void setWarnings(List<String> warnings) {
		this.warnings = warnings;
	}
	
	
	public boolean isValid() {
		if(errors.size()>0){
			valid=false;
		}else{
			valid = true;
		}
		return valid;
	}
	
	public void setValid(boolean valid) {
		this.valid = valid;
	}
	public void addError(String error){
		errors.add(error);
	}
	
	
	public void addWarning(String warning){
		warnings.add(warning);
	}

	

	
}
