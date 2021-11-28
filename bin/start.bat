SET worldFile="world.png"
SET threadSleep=20
SET population=100
SET lenghtOfDNA=300
SET selectionRate="0.30"
SET mutuationRate="0.005"
SET distanceMethod=1

java -jar genetic_planning.jar %worldFile% %threadSleep% %population% %lenghtOfDNA% %selectionRate% %mutuationRate% %distanceMethod%
