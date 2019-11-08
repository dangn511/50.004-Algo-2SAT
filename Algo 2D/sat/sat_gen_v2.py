def genSat(n):
    f= open("sat_{}v{}c.cnf".format(n, (n-1)*2),"w+")
    f.write("p cnf {} {}\n".format(n, (n-1)*2))
    for i in range(2, n+1):
        f.write( "{:05} {:05} {}\n".format(1, i, 0))
        f.write( "-{:05} -{:05} {}\n".format(1, i, 0))
    
    f.close()
    
genSat(10)
genSat(100)
genSat(1000)
genSat(10000)