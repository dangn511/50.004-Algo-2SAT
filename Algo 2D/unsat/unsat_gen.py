def genUnsat(n):
    f= open("unsat_{}v{}c.cnf".format(n, (n-1)*4),"w+")
    f.write("p cnf {} {}\n".format(n, (n-1)*4))
    for i in range(2, n+1):
        f.write( "{:04} {:04} {}\n".format(1, i, 0))
        f.write( "{:04} -{:04} {}\n".format(1, i, 0))
        f.write( "-{:04} {:04} {}\n".format(1, i, 0))
        f.write( "-{:04} -{:04} {}\n".format(1, i, 0))
    
    f.close()
    
for i in range(1000,15001,1000):
    genUnsat(i)    