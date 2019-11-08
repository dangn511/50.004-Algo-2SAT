package implication;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class RandomCNFGenerator {
	public static void main(String[] args) {
		int n = 10;
		int m = 10;
		
		File f = new File("randomCNF.cnf");
		FileWriter fr = null;
        BufferedWriter br = null;
        try{
        	Random r = new Random();
            fr = new FileWriter(f);
            br = new BufferedWriter(fr);
            br.write("p cnf " + n + " " + m);
            br.newLine();
            for(int i = 0; i<m; i++) {
            	String r1 = Integer.toString(r.nextInt(n));
            	String minus1;
            	if(r.nextInt()%2 == 0) {
            		minus1 = new String("");
            	}
            	else {
            		minus1 = new String("-");
            	}
            	String r2 = Integer.toString(r.nextInt(n));
            	String minus2;
            	if(r.nextInt()%2 == 0) {
            		minus2 = new String("");
            	}
            	else {
            		minus2 = new String("-");
            	}
            	br.write(minus1+r1+" "+minus2+r2+" "+"0");
            	br.newLine();
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        } finally {
            try{
                br.close();
                fr.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
	}
}
