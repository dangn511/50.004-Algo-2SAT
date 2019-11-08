package sat;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import implication.Graph;
import implication.Main;
import sat.env.Environment;
import sat.formula.Clause;
import sat.formula.Formula;
import sat.formula.Literal;
import sat.formula.NegLiteral;
import sat.formula.PosLiteral;


public class SATSolverTest {
    Literal a = PosLiteral.make("a");
    Literal b = PosLiteral.make("b");
    Literal c = PosLiteral.make("c");
    Literal na = a.getNegation();
    Literal nb = b.getNegation();
    Literal nc = c.getNegation();


    // TODO: add the main method that reads the .cnf file and calls SATSolver.solve to determine the satisfiability


    public void testSATSolver1() {
        // (a v b)
        Environment e = SATSolver.solve(makeFm(makeCl(a, b)));
/*
    	assertTrue( "one of the literals should be set to true",
    			Bool.TRUE == e.get(a.getVariable())
    			|| Bool.TRUE == e.get(b.getVariable())	);

*/
    }


    public void testSATSolver2() {
        // (~a)
        Environment e = SATSolver.solve(makeFm(makeCl(na)));
/*
    	assertEquals( Bool.FALSE, e.get(na.getVariable()));
*/
    }

    private static Formula makeFm(Clause... e) {
        Formula f = new Formula();
        for (Clause c : e) {
            f = f.addClause(c);
        }
        return f;
    }

    private static Clause makeCl(Literal... e) {
        Clause c = new Clause();
        for (Literal l : e) {
        	if(c.contains(l.getNegation())) return new Clause();
        	else if(!(c.contains(l))) c = c.add(l);
        }
        return c;
    }
    
    //parsing cnf file
    public static Formula readFile(File file) throws FileNotFoundException {
        Scanner in = new Scanner(file);
        while (!(in.next().equals("p"))) in.nextLine();					//search for start of problem
        String[] inputArr = in.nextLine().trim().split("\\s+");			//string parsing
        int n = Integer.valueOf(inputArr[1]);    						//no of variables
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
        return makeFm(formulaArrClause);
    }
    
    //write environment bindings to file. Arg must be a non-null Environment
    public static void tofile(Environment e){
    	//string parsing of e.toString()
        String trimmed = e.toString().substring(13,e.toString().length()-1);
        String[] arr = trimmed.split("[->, ]+");
        String[] arr2 = new String[arr.length/2];
        for (int i = 0; i < arr.length-1; i+=2) arr2[i/2] = arr[i] + ":"+ arr[i+1];
        
        //writing to output file
        File file = new File("BoolAssignment.txt");
        FileWriter fr = null;
        BufferedWriter br = null;
        try{
            fr = new FileWriter(file);
            br = new BufferedWriter(fr);
            for(int i = arr2.length-1; i >= 0; i--){
                br.write(arr2[i]);
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
    
    public static void main(String[] args) throws FileNotFoundException {		//main
    	String fileStr = "unsat\\unsat_10000v39996c.cnf";
        File file = new File(fileStr);						//input destination file as string
        long started;
        long time;
        long timeTaken;
        Formula f = readFile(file);												//parsing cnf file
        System.out.println(fileStr);
        started = System.nanoTime();
        Environment e = SATSolver.solve(f);
        time = System.nanoTime();
        timeTaken = time - started;
        System.out.println("Time for DPLL:" + timeTaken / 1000000.0 + "ms\n");
        
        Clause[] cli = Main.readFileGraph(file);
        started = System.nanoTime();
        Graph g = Main.createGraph(cli);
		//checking if graph is sat or unsat
		if(g.checkSat()) {
			System.out.println("FORMULA SATISFIABLE");
			//System.out.println(Main.parseEnv(g.env));
		}
		else {
			System.out.println("FORMULA UNSATISFIABLE");
		}
        time = System.nanoTime();
        timeTaken = time - started;
        System.out.println("Time for kosa:" + timeTaken / 1000000.0 + "ms");
    }
}