# This file takes as input the individual datasets for each user (<number>.txt files) 
# and generates two output files: 
# - transformed.txt, contains the same data as the individual files, plus the transformed path coordinates so that
#   each selection path lies horizontally.
# - measures.txt, contains the various Mackenzie's accuracy measures for each path

source("functions.R")
options(warn=1)

# Transformed data points with abs(y) coordinate greater than NOISE_ERROR_THRESHOLD are 
# sanitized (ie. the previous point is considered) 
# Very large values (larger than the maximum screen coordinates) effectively disable this threshold
NOISE_ERROR_THRESHOLD <- 111400


# For speed calculation
SAMPLE_INTERVAL <- 25 #40 samples per second: 25 millisecond interval


# get the data from files named 0.txt, 1.txt, etc.

files <- list.files(path="data", pattern="^[0-9]+.*")
files
dataRaw <- data.frame()
for (file in files) {
  print (file)
  dat = read.csv(paste("data/", file, sep=""), sep="", head = TRUE)
  print(nrow(dat))
  dataRaw <- rbind(dataRaw, dat)
}
colnames(dataRaw)


# file name for the transformed coordinates
filenameTransformed <- paste("data/", "transformed.txt", sep="")

# file name for the measures 
filenameMeasures <- paste("data/", "measures.txt", sep="")


dataMeasures <- data.frame()
newData <- data.frame()


#For noise errors checking
noiseErrorCount <- 0

system.time(
  for (user in unique(dataRaw$UserId) ) {
    #for (user in 4 ) {
    
    newDataUser <- data.frame()
    
    for (device in unique(dataRaw[dataRaw$UserId==user,]$NumberDevice) ) {
    #  for (device in unique(dataRaw$NumberDevice)[1] ) {
      
      newDataDevice <- data.frame()
      
      for (block in 1:max(dataRaw[dataRaw$UserId==user & dataRaw$NumberDevice==device,]$Block) ) {
        #for (block in 1:1 ) {
        
        newDataBlock <- data.frame()
        
        for (sequence in 1:max(dataRaw[dataRaw$UserId==user & dataRaw$NumberDevice==device & dataRaw$Block==block,]$Sequence) ) {
          #for (sequence in 1:1 ) {
          print(paste("User:", user, " Device:", device, " Block:", block, " Sequence: ", sequence))
          newDataSequence <- data.frame()
          
          for (cid in 1:max(dataRaw$CircleID) ) {
            #for (cid in 1 ) {
            
            
            #indexes for the current target selection
            indexes <- which(dataRaw$UserId == user & dataRaw$NumberDevice == device & dataRaw$Block == block & dataRaw$Sequence == sequence & dataRaw$CircleID == cid)
            partial <- dataRaw[indexes, ]
            
            # we consider the movement axis as a line going from the first point to the click point inside the target
            axisStart <- c(partial[1,]$MouseX, partial[1,]$MouseY)
            axisEnd <- c(partial[nrow(partial),]$PixelEndCircleX, partial[nrow(partial),]$PixelEndCircleY)
            axis <- axisEnd-axisStart
            #print(axis)
            
            # we will transform the coordinates so that the movements axis is horizontal, and starts at (0, 0)
            angle <- angleBetween( c(1, 0), axis )
            if ( axis[2] < 0) {
              angle <- -angle
            }
            
            # transform also the target coordinates, so we can calculate the TRE measure
            point <- c(dataRaw[indexes[1],]$PixelEndCircleX, dataRaw[indexes[1],]$PixelEndCircleY)-axisStart
            target <- rotate(point, angle )
            
            
            # calculated distance (first click - target)
            calculatedDistance <- dist(rbind(target, c(0,0)))[1]
            
            
            # initialization of variables to calculate the various metrics
            errorRate <- (partial[1,]$NumberClicks-1)/partial[1,]$NumberClicks
            
            TRE <- -1
            entered <- FALSE
            
            TAC <- 0
            prevTACY <- 0
            
            MDC <- 0
            prevMDCY <- 0
            prevMDCDif <- 0
            
            ODC <- 0
            prevODCX <- 0
            prevODCDif <- 0
            
            MO <- 0
            
            MV <- 0
            ys <- c()
            
            ME <- 0
            
            # will hold the click point (transformed) this is the last coordinate found in the selection movement
            clickPointX <- 0
            clickPointY <- 0
            
            partialtargetx <- numeric()
            partialtargety <- numeric()
            partialrx <- numeric()
            partialry <- numeric()
            partialinside <- logical()
            speeds <- numeric()
            accels <- numeric()
            percentpath <-numeric()
            displacement <- numeric()
            distance <- numeric()
            insidecount <- 0
            sampleid <- numeric()
            
            # for noise errors checking
            lastCalculatedPointY <- 0
            
            
            # for speed and accel calculation
            lastPointX <- 0
            lastPointY <- 0
            lastSpeed <- 0
            
            # go through all coordinates, transform them and calculate the various measures.
            #for (n in indexes ) {
            for (n in 1:nrow(partial) ) {
              sampleid[n] <- n    
              percentpath[n] = n/nrow(partial)
              
              displacement[n] <- dist(rbind(c(partial[n,]$MouseX, partial[n,]$MouseY), c(partial[1,]$MouseX, partial[1,]$MouseY)))
              
              partialtargetx[n]<-target[1,1]
              partialtargety[n]<-target[1,2]
              
              point <- c(partial[n,]$MouseX, partial[n,]$MouseY)-axisStart
              
              # transform the coordinate
              newPoint <- rotate(point, angle )
              
              partialrx[n]<-newPoint[1,1]
              partialry[n]<-newPoint[1,2]
              
              #Check for noise 
              if (abs(partialry[n]-lastCalculatedPointY) > NOISE_ERROR_THRESHOLD) {
                print(paste("Correcting coordinate due to noise", partialry[n]))    
                partialry[n] <- lastCalculatedPointY
                noiseErrorCount <- noiseErrorCount +1
              }
              lastCalculatedPointY <- partialry[n]
              
              
              clickPointX <- newPoint[1,1]
              clickPointY <- newPoint[1,2]
              
              
              # Distance
              dist <- dist(rbind(c(clickPointX,clickPointY), c(lastPointX, lastPointY)))
              if ( n > 1 ) {
                  distance[n] <- distance[n-1]+dist
              } else {
                distance[n] <- 0
              }
              # Speed/Accel
              speed <- dist/(SAMPLE_INTERVAL/1000)
              speeds[n] <- speed
              lastPointX <- clickPointX
              lastPointY <- clickPointY
              
              lastspeed <- 0
              if (n > 1) {
                lastspeed <- speeds[n-1]
              }
              accels[n] <- (speed-lastspeed)/(SAMPLE_INTERVAL/1000)
              
              
              #TRE
              if (dist(rbind(newPoint, target)) < partial[n,]$TargetWidth/2) {
                if ( !entered ) {
                  entered <- TRUE
                  TRE <- TRE + 1
                  insidecount <- 0
                } 
                partialinside[n]<-TRUE
                insidecount <- insidecount + 1
              } else {
                entered <- FALSE
                partialinside[n]<-FALSE
              }
              
              #TAC
              curY <- newPoint[1, 2]
              if ( prevTACY*curY < 0 ) {
                TAC <- TAC +1
              }
              prevTACY <- curY
              
              #MDC          
              curDif <- curY-prevMDCY
              if ( prevMDCDif * curDif < 0 ) {
                MDC <- MDC +1
                #print("+1")
              }
              if ( curDif != 0 ) {
                prevMDCDif <- curDif
              }
              prevMDCY <- curY
              
              #ODC        
              curX <- newPoint[1, 1]
              curDif <- curX-prevODCX
              if ( prevODCDif * curDif < 0 ) {
                ODC <- ODC +1
              }
              if ( curDif != 0 ) {
                prevODCDif <- curDif
              }
              prevODCX <- curX   
              
              
              #MO
              MO <- MO + curY
              
              #MV
              ys <- append(ys, curY)
              
            }
            
            
            #MO
            MO <- MO / length(indexes)
            
            #MV
            MV <- sqrt(sum((ys-MO)*(ys-MO))/(length(indexes)-1))
            
            #ME 
            ME <- sum(abs(ys))/length(indexes)
            
            
            row <- c(partial[1,]$NumberDevice, partial[1,]$UserId, partial[1,]$Block, partial[1,]$Sequence, partial[1,]$CircleID, errorRate, TRE, TAC, MDC, ODC, MV, ME, MO, clickPointX, clickPointY, partialtargetx[1], partialtargety[1], partial[1,]$ElapsedTime/1000, partial[1,]$TargetWidth, partial[1,]$DistanceCenter*2, calculatedDistance)
            dataMeasures <- rbind(dataMeasures, row)
            
            partial$targetx <- partialtargetx
            partial$targety <- partialtargety
            partial$rx <- partialrx
            partial$ry <- partialry
            partial$inside <- partialinside
            
            partial$percentpath <- percentpath
            partial$speeds <- speeds
            partial$accels <- accels
            partial$displacement <- displacement
            partial$distance <- distance
            partial$insidecount <- insidecount
            partial$sampleid <- sampleid
            
            #newData <- rbind(newData, partial)
            newDataSequence <- rbind(newDataSequence, partial)
          }
          newDataBlock <- rbind(newDataBlock, newDataSequence)    
        }
        newDataDevice <- rbind(newDataDevice, newDataBlock)
      }
      newDataUser <- rbind(newDataUser, newDataDevice)
    }
    newData <- rbind(newData, newDataUser)
  }
)
names(dataMeasures) <- c("Device", "UserId", "Block", "Sequence", "CircleID", "ErrorRate", "TRE", "TAC", "MDC", "ODC", "MV", "ME", "MO", "ClickPointX", "ClickPointY","TargetX","TargetY", "MovementTime", "TargetWidth", "Distance", "CalculatedDistance")

# 
print(paste("Noise errors found: ", noiseErrorCount))




# Calculate throughput
dataMeasures$Throughput <- -1
for ( device in unique(dataMeasures$Device)) {
  #for ( device in 1) {
  #device <- "Mouse"
  meanX <- mean(dataMeasures[dataMeasures$Device == device,]$ClickPointX - dataMeasures[dataMeasures$Device == device,]$TargetX)
  meanY <- mean(dataMeasures[dataMeasures$Device == device,]$ClickPointY - dataMeasures[dataMeasures$Device == device,]$TargetY )
  
  diffX <- (dataMeasures[dataMeasures$Device == device,]$ClickPointX - dataMeasures[dataMeasures$Device == device,]$TargetX)-meanX
  diffY <- (dataMeasures[dataMeasures$Device == device,]$ClickPointY - dataMeasures[dataMeasures$Device == device,]$TargetY)-meanY
  
  diffSQX <- diffX*diffX
  diffSQY <- diffY*diffY
  
  SD <- sqrt(sum(diffSQX+diffSQY)/(length(diffX)-1))
  We <- 4.133*SD
  IDe <- log(dataMeasures[dataMeasures$Device == device,]$CalculatedDistance/We + 1, 2)
  Throughput <- IDe/dataMeasures[dataMeasures$Device == device,]$MovementTime
  dataMeasures[dataMeasures$Device == device,]$Throughput <-Throughput
  
}

write.table(newData, file = filenameTransformed, sep=" ", row.names=FALSE)
write.table(dataMeasures, file = filenameMeasures, sep=" ", row.names=FALSE)
