package implication;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.Scanner;

import sat.env.Environment;
import sat.formula.Clause;
import sat.formula.Literal;
import sat.formula.NegLiteral;
import sat.formula.PosLiteral;

public class Main {
	
    private static Clause makeCl(Literal... e) {
        Clause c = new Clause();
        for (Literal l : e) {
        	if(c.contains(l.getNegation())) return new Clause();
        	else if(!(c.contains(l))) c = c.add(l);
        }
        return c;
    }
    
    //parsing cnf file into an implication graph
    public static Clause[] readFileGraph(File file) throws FileNotFoundException {
        Scanner in = new Scanner(file);
        while (!(in.next().equals("p"))) in.nextLine();					//search for start of problem
        String[] inputArr = in.nextLine().trim().split("\\s+");			//string parsing
        //int n = Integer.valueOf(inputArr[1]);    						//no of variables
        int m = Integer.valueOf(inputArr[2]);    						//no of clauses

        int mCounter = 0;
        Clause[] formulaArrClause = new Clause[m];
        while (mCounter < m) {
            String x = in.nextLine().trim();
            String[] clauseArr = x.split("\\s+");
            if (clauseArr.length == 1) {
                continue;
            } else {
                Literal[] clauseArrLiteral = new Literal[clauseArr.length - 1];
                for (int i = 0; i < clauseArr.length - 1; i++) {
                    if (clauseArr[i].charAt(0) == '-') {
                        clauseArrLiteral[clauseArr.length - 2 - i] =
                                NegLiteral.make(clauseArr[i].substring(1));								//case when literal is negative
                    } else {
                        clauseArrLiteral[clauseArr.length - 2 - i] = PosLiteral.make(clauseArr[i]);		//case when literal is positive
                    }
                }
                
                formulaArrClause[mCounter] = makeCl(clauseArrLiteral);
                mCounter++;
            }
        }
        in.close();
        return formulaArrClause;
    }
    
    public static Graph createGraph(Clause[] formulaArrClause) {
        // creating implication graph
        Graph g = new Graph();
        for(Clause c: formulaArrClause) {
        	//System.out.println(c);
        	Literal[] lits = new Literal[2];
        	int i = 0;
        	for(Literal l :c) {
        		if(i<=1) {
		        	lits[i] = l;
		        	i++;
        		}
        	}
        	if(i == 2) g.addImplication(lits[0], lits[1]);
        	else g.addImplication(lits[0], lits[0]);
        }
        
        return g;
    }
	
    //return String of the solution from environment
    public static String parseEnv(Environment e){
    	StringBuilder s = new StringBuilder();
        String trimmed = e.toString().substring(13,e.toString().length()-1);
        String[] arr = trimmed.split("[->, ]+");
        String[] arr2 = new String[arr.length/2];
        for (int i = 0; i < arr.length-1; i+=2) arr2[i/2] = arr[i] + ":"+ arr[i+1];
        for(int i = arr2.length-1; i >= 0; i--) {
        	s.append(arr2[i]+"\n");
        }
        return s.toString();
    }
	
    //main body
	public static void main(String[] args) throws FileNotFoundException {
		File file = new File("randomshitonline_5v7c.cnf");						//input destination file as string
        //constructing implication graph
		Clause[] cli = readFileGraph(file);
		long starttime = System.currentTimeMillis();
		Graph g = createGraph(cli);
		
		//checking if graph is sat or unsat
		if(g.checkSat()) {
			System.out.println("FORMULA SATISFIABLE");
			System.out.println(parseEnv(g.env));
		}
		else {
			System.out.println("FORMULA UNSATISFIABLE");
		}
		System.out.println("Time taken: " + (System.currentTimeMillis()-starttime) + "ms");
		
	}
}
