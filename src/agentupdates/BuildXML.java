/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agentupdates;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 *
 * @author shikha
 * 12-04-2020
 */
public class BuildXML {
    
    public static void build(String testcase_dir, int meta){
        
        ProcessBuilder processBuilder = new ProcessBuilder();
        
        //processBuilder.command("bash", "-c", "java -jar \"testKRR/KRR.jar\" -ONT  input.txt 1>input.xml");
        
        String  runCmd= "java -jar \"testKRR/KRR.jar\" -KD45 testcases/"+  testcase_dir+"/"+"input"+meta+".txt 1>"+"testcases/"+testcase_dir+"/"+"input.xml";
        
        processBuilder.command("bash", "-c", runCmd);
        	
        try {

		Process process = processBuilder.start();

		StringBuilder output = new StringBuilder();

		BufferedReader reader = new BufferedReader(
				new InputStreamReader(process.getInputStream()));

		String line;
		while ((line = reader.readLine()) != null) {
			output.append(line + "\n");
		}

		int exitVal = process.waitFor();
		if (exitVal == 0) {
			System.out.println("Done!");
			System.out.println(output);
			//System.exit(0);
		} else {
			System.out.println("err!");
		}

	} catch (IOException e) {
		e.printStackTrace();
	} catch (InterruptedException e) {
		e.printStackTrace();
	}

    }
    
}
