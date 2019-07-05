package main;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import assembly.UmiAssembly;

@SuppressWarnings("deprecation")
public class UmiAssemblyMain {

	public static void main(String[] args) throws ParseException, IOException {
		CommandLineParser parser = new BasicParser();  
		
		Options options = new Options();
		options.addOption("i", "input", true, "Iutput file");
		options.addOption("o", "outdir", true, "Output directory");
		options.addOption("n", "num_overlap", true, "Minimum of overlap. (5)");
		options.addOption("x", "rate_mismatch", true, "Frequence of mismatchs. (0)");
		options.addOption("s", "seq_type", true, "Type of sequences.[short | long]");
		
		CommandLine commandLine = parser.parse( options, args );
		
		int n = Integer.parseInt(commandLine.getOptionValue("n"));
		double x = Double.parseDouble(commandLine.getOptionValue("x"));
		String seq_type = commandLine.getOptionValue("s");
		
		UmiAssembly assembly = new UmiAssembly(commandLine.getOptionValue("i"));
		
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		System.out.println(df.format(new Date()));
		if("short".equals(seq_type)) {
			assembly.runAssembly(n, 0, "short");
		}else {	
			assembly.runAssembly(n, x, "long");
		}
		
		System.out.println(df.format(new Date()));
	}
		
}
