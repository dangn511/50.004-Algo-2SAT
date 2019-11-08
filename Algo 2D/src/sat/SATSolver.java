package sat;

import immutable.EmptyImList;
import immutable.ImList;
import immutable.NonEmptyImList;
import sat.env.Bool;
import sat.env.Environment;
import sat.formula.Clause;
import sat.formula.Formula;
import sat.formula.Literal;
import sat.formula.NegLiteral;
import sat.formula.PosLiteral;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.spec.EncodedKeySpec;
import java.util.Arrays;

/**
 * A simple DPLL SAT solver. See http://en.wikipedia.org/wiki/DPLL_algorithm
 */
public class SATSolver {
    /**
     * Solve the problem using a simple version of DPLL with backtracking and
     * unit propagation. The returned environment binds literals of class
     * bool.Variable rather than the special literals used in clausification of
     * class clausal.Literal, so that clients can more readily use it.
     *
     * @return an environment for which the problem evaluates to Bool.TRUE, or
     * null if no such environment exists.
     */
    public static Environment solve(Formula formula) {
    	Environment env = solve(formula.getClauses(), new Environment());
    	if(env == null) {
    		System.out.println("not satisfiable");
    	}
    	else {
    		System.out.println("satisfiable");
    	}
        return env;
    }

    /**
     * Takes a partial assignment of variables to values, and recursively
     * searches for a complete satisfying assignment.
     *
     * @param clauses formula in conjunctive normal form
     * @param env     assignment of some or all variables in clauses to true or
     *                false values.
     * @return an environment for which all the clauses evaluate to Bool.TRUE,
     * or null if no such environment exists.
     */
    private static Environment solve(ImList<Clause> clauses, Environment env) {	
    	
    	//if clauses is empty, formula is satisfiable 
        if (clauses.isEmpty()) {
            return env;
        }
        
        //if any of the clause is empty, formula is unsatisfiable
        for (Clause c : clauses) {
            if (c.isEmpty()) {
            	return null;
            }
        }

        Environment temp = env;
        ImList<Clause> tempClauses = clauses;

        //Choosing the smallest clause (by number of literals)
        Clause smallest = clauses.first();
        for (Clause c : clauses) {
        	if(smallest.size() > c.size()) {
        		smallest = c;
        	}
        }
        
        
        //Case 1: if number of literals in clause chosen is 1
        if (smallest.isUnit()) {
            Literal x = smallest.chooseLiteral();

            //setting the literal as positive (variable may be true or false depending on NegLiteral/PosLiteral)
            if (x instanceof NegLiteral) {
                env = env.putFalse(x.getVariable());
            } else {
                env = env.putTrue(x.getVariable());
            }

            //start recursion
            tempClauses = substitute(clauses, x);
            env = solve(tempClauses, env);
            return env;
        }

        
        //Case 2: More than 1 literal -> Try put literal as true
        Literal x = smallest.chooseLiteral();
        
        //Set literal as true
        if (x instanceof NegLiteral) {
            temp = temp.putFalse(x.getVariable());
        } else {
            temp = temp.putTrue(x.getVariable());
        }
        tempClauses = substitute(clauses,x);
        temp= solve(tempClauses, temp);
        
        //if temp is null, we move onto case 3
        if (temp != null) {
            return temp;
        }


        //Case 3: More than 1 literal -> Try False
        if (x instanceof NegLiteral) {
            env = env.putTrue(x.getVariable());
        } else {
            env = env.putFalse(x.getVariable());
        }

        clauses = substitute(clauses,x.getNegation());
        env = solve(clauses, env);
        return env;

    }

    /**
     * given a clause list and literal, produce a new list resulting from
     * setting that literal to true
     *
     * @param clauses , a list of clauses
     * @param l       , a literal to set to true
     * @return a new list of clauses resulting from setting l to true
     */
    private static ImList<Clause> substitute(ImList<Clause> clauses,
                                             Literal l) {
    	ImList<Clause> newClauses = clauses;
        //runs through clauses and reduces them if they contain literal l
        for (Clause c : clauses) {
            if (c.contains(l) || c.contains(l.getNegation())) {
                Clause temp = c.reduce(l);
                newClauses = newClauses.remove(c);
                if (temp != null) {
                	newClauses = newClauses.add(temp);
                }
            }
        }
        return newClauses;
    }
}
