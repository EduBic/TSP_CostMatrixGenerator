# TSP_CostMatrixGenerator
Generator of Cost Matrix for Travel Salesman Problem (TSP). First it generates a set of point with three type of positions:
- random
- square area
- line

From this points it write a symmetric matrix of the cost, where the costs are the distances from one point to others.

## How to use
Open terminal, go into folder "CostMatrixGenerator/src"

Generate a new instance using the following command:

	
```powershell
java com.company.Main <NUM_NODES>
```
  
It writes new files fb<NUM_NODES>.dat (fb = fake board) and <NUM_NODES>.txt into the src folder.

.dat cointains the symmetric matrix of costs.
.txt cointains the x,y coordinates of every points.
