package pt.keep.validator;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xml.sax.InputSource;

import pt.keep.validator.utils.FileUtils;
import pt.keep.validator.utils.ZipUtils;

import com.thaiopensource.validate.SchemaReader;
import com.thaiopensource.validate.ValidationDriver;
import com.thaiopensource.validate.rng.SAXSchemaReader;

public class OdfValidator
{
	public static String version = "0.1";
	public void run(String input,String version){
		try{
			File inputFile = new File(input);
			File outputDirectory = FileUtils.createTempDirectory();
			ZipUtils.unzip(inputFile, outputDirectory);
			
			List<ValidatorOutput> outputs = new ArrayList<ValidatorOutput>();
			
			System.out.println("1.0");
			if(version.equalsIgnoreCase("1.0") || version.equals("all")){
				ValidatorOutput outputV10 = validateV10(outputDirectory);
				outputs.add(outputV10);
			}
			System.out.println("1.1");
			if(version.equalsIgnoreCase("1.1") || version.equals("all")){
				ValidatorOutput outputV11 = validateV11(outputDirectory);
				outputs.add(outputV11);
			}
			System.out.println("1.2");
			if(version.equalsIgnoreCase("1.2") || version.equals("all")){
				ValidatorOutput outputV12 = validateV12(outputDirectory);
				outputs.add(outputV12);
			}
			
			
			showResults(outputs);		
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private void showResults(List<ValidatorOutput> outputs) {
		boolean valid=false;;
		String version=null;
		for(ValidatorOutput v : outputs){
			if(v.isValid()){
				valid=true;
				version=v.getOdfVersion();
				break;
			}
		}
		System.out.println("valid:"+valid);
		if(valid){
			System.out.println("version:"+version);
		}
	}

	public ValidatorOutput validate(File unzippedDir,String manifestSchemaPath, String schemaPath, String version){
		ValidatorOutput output = new ValidatorOutput();
		output.setOdtVersion(version);
		SchemaReader reader = SAXSchemaReader.getInstance();
		try{
			File manifest = new File(unzippedDir,"META-INF/manifest.xml");
			if(!manifest.exists()){
				output.addError("manifest.xml doesn't exists");
			}
			ValidationDriver driver = new ValidationDriver(reader);
			driver.loadSchema(new InputSource(OdfValidator.class.getResourceAsStream(manifestSchemaPath)));
			//System.err.close(); 
			boolean manifestValid = driver.validate(new InputSource(new FileInputStream(manifest)));
			if(!manifestValid){
				output.addError("manifest.xml doesn't validate against schema");
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		List<String> filesToCheck = Arrays.asList("content.xml","styles.xml","meta.xml","settings.xml");
		for(String s : filesToCheck){
			try{
				File f = new File(unzippedDir,s);
				if(!f.exists()){
					output.addError(""+s+ " doesn't exists");
				}
				ValidationDriver driver = new ValidationDriver(reader);
				driver.loadSchema(new InputSource(OdfValidator.class.getResourceAsStream(schemaPath)));
				boolean fileValid = driver.validate(new InputSource(new FileInputStream(f)));
				if(!fileValid){
					output.addError(""+s+" doesn't validate against schema");
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return output;
	}
	
	public ValidatorOutput validateV10(File unzippedDir){
		String manifestSchema = "/schemas/1.0/OpenDocument-manifest-schema-v1.0-os.rng";
		String schema = "/schemas/1.0/OpenDocument-schema-v1.0-os.rng";
		String strictSchema = "/schemas/1.0/OpenDocument-strict-schema-v1.0-os.rng";
		return validate(unzippedDir, manifestSchema, schema,"1.0");	
	}
	public ValidatorOutput validateV11(File unzippedDir){
		String manifestSchema = "/schemas/1.1/OpenDocument-manifest-schema-v1.1.rng";
		String schema = "/schemas/1.1/OpenDocument-schema-v1.1.rng";
		String strictSchema = "resources/1.1/OpenDocument-strict-schema-v1.1.rng";
		return validate(unzippedDir, manifestSchema, schema,"1.1");	
	}
	public ValidatorOutput validateV12(File unzippedDir){
		String manifestSchema = "/schemas/1.2/OpenDocument-v1.2-os-manifest-schema.rng";
		String schema = "/schemas/1.2/OpenDocument-v1.2-os-schema.rng";
		String dsigSchema = "/schemas/1.2/OpenDocument-v1.2-os-dsig-schema.rng";
		return validate(unzippedDir, manifestSchema, schema,"1.2");	
	}
	
	private void printHelp(Options opts) {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("validator", opts );
	}
	
	private void printVersion() {
		System.out.println("Validator" + version);
	}
	
	
	
    public static void main( String[] args )
    {
		 Logger.getRootLogger().setLevel(Level.OFF);

    	try{
    		OdfValidator v = new OdfValidator();
	    	Options options = new Options();
			options.addOption("f",true, "file to process");
			options.addOption("s",true, "schema version to validate against (1.0, 1.1, 1.2)");
			options.addOption("v",false,"version");
			options.addOption("h",false,"print this message");
	
			CommandLineParser parser = new GnuParser();
			CommandLine cmd = parser.parse(options, args);
	
			if(cmd.hasOption("h")) {
				v.printHelp(options);
				System.exit(0);
			}
			
			if(cmd.hasOption("v")) {
				v.printVersion();
				System.exit(0);
			}
	
			String version = "all";
			if(cmd.hasOption("s")) {
				version = cmd.getOptionValue("s");
				if(!version.equals("1.0") && !version.equals("1.1") && !version.equals("1.1")){
					v.printHelp(options);
					System.exit(0);
				}
			}
			
			
			if(!cmd.hasOption("f")){
				v.printHelp(options);
				System.exit(0);
			}
			v.run(cmd.getOptionValue("f"),version);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }

}