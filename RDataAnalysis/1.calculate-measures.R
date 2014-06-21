# This file takes as input the individual datasets for each user (<number>.txt files) 
# and generates two output files: 
# - transformed.txt, contains the same data as the individual files, plus the transformed path coordinates so that
#   each selection path lies horizontally.
# - measures.txt, contains the various Mackenzie's accuracy measures for each path

#install.packages(c("doParallel"))
library(doParallel)
source("functions.R")
options(warn=1)


# Transformed data points with abs(y) coordinate greater than NOISE_ERROR_THRESHOLD are 
# sanitized (ie. the previous point is considered) 
# Very large values (larger than the maximum screen coordinates) effectively disable this threshold
NOISE_ERROR_THRESHOLD <- 111400


# For speed calculation
SAMPLE_INTERVAL <- 25 #40 samples per second: 25 millisecond interval


# we run some computations in parallel
# adjust number of clusters to the number of cores in you machine
cl <- makeCluster(8)
registerDoParallel(cl)



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
remove(dat)
colnames(dataRaw)

#dataRaw <- dataRaw[dataRaw$NumberDevice==1,]

# file name for the transformed coordinates
filenameTransformed <- paste("data/", "transformed-test.txt", sep="")

# file name for the measures 
filenameMeasures <- paste("data/", "measures-test.txt", sep="")


# This function transforms the coordinates of one target selection so that the movement axis lies 
# horizontally.
# The function assumes that the data frame passed in its argument contains only the pointer sample for 
# one target selection
transform <- function (partial) {
    
    # we consider the movement axis as a line going from the first point to the click point inside the target
    axisStart <- c(partial[1,]$MouseX, partial[1,]$MouseY)
    axisEnd <- c(partial[nrow(partial),]$PixelEndCircleX, partial[nrow(partial),]$PixelEndCircleY)
    axis <- axisEnd-axisStart
    
    
    # calculate the angle between the axis and the x-axis
    # we will transform the coordinates so that the movements axis is horizontal, and starts at (0, 0)
    angle <- angleBetween( c(1, 0), axis )
    if ( axis[2] < 0) {
        angle <- -angle
    }
    
    # transform the target coordinates, so we can calculate if the pointer is inside the target
    point <- c(partial[1,]$PixelEndCircleX, partial[1,]$PixelEndCircleY)-axisStart
    transfTarget <- rotate(point, angle )
    
    
    
    # will hold the click point (transformed) this is the last coordinate found in the selection movement
    clickPointX <- 0
    clickPointY <- 0
    
    partialtransfTargetx <- numeric()
    partialtransfTargety <- numeric()
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
    
    #
    entered <- FALSE
    
    
    # go through all coordinates, transform them and calculate the various measures.
    for (n in 1:nrow(partial) ) {
        #sampleid[n] <- n    
        percentpath[n] = n/nrow(partial)
        
        # displacement
        displacement[n] <- dist(rbind(c(partial[n,]$MouseX, partial[n,]$MouseY), c(partial[1,]$MouseX, partial[1,]$MouseY)))
        
        partialtransfTargetx[n]<-transfTarget[1,1]
        partialtransfTargety[n]<-transfTarget[1,2]
        
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
        
        
        #inside count
        if (dist(rbind(newPoint, transfTarget)) < partial[n,]$TargetWidth/2) {
            if ( entered ) {
                entered <- TRUE
                insidecount <- 0
            } 
            partialinside[n]<-TRUE
            insidecount <- insidecount + 1
        } else {
            partialinside[n]<-FALSE
        }
        
       
    }
    
    partial$transfTargetx <- partialtransfTargetx
    partial$transfTargety <- partialtransfTargety
    partial$rx <- partialrx
    partial$ry <- partialry
    partial$inside <- partialinside
    partial$percentpath <- percentpath
    partial$speeds <- speeds
    partial$accels <- accels
    partial$displacement <- displacement
    partial$distance <- distance
    partial$insidecount <- insidecount
    #partial$sampleid <- sampleid    
    return(partial)
}

# This function calculates the various measures for a given selection path.
# The function assumes that the 'partial' data frame passed as argument has transformed pointer coordinates
calculateMeasures <- function (partial) {
    
    # initialization of variables to calculate the various metrics
    # for easier structure we keep all variables needed to calculate a given metric in a list
    
    
    # Error Rate
    # here we compute the error rate for a selection as the percentage of clicks outside the target
    errorRate <- (partial$NumberClicks[1]-1)/partial$NumberClicks[1]
    
    # Error Rate Binary
    # here we compute error rate as a percentage of targets that were not correctly selected on the first click
    # e.g., the first click was outside the target
    errorRateBinary <- ifelse( (partial$NumberClicks[1]) > 1, 1, 0) 
    
    
    # TRE
    tre <- list()
    tre$TRE <- -1
    tre$entered <- FALSE
    
    # TAC
    tac <- list()
    tac$TAC <- 0
    tac$prevTACY <- 0
    
    # MDC
    mdc <- list()
    mdc$MDC <- 0
    mdc$prevDif <- 0
    
    # ODC
    odc <- list()
    odc$ODC <- 0
    odc$prevDif <- 0
    
    
        
    # 
    targetPoint <- c(partial$transfTargetx[1], partial$transfTargety[1])
    
    # go through all coordinates, and calculate the various measures.
    for (n in 1:nrow(partial) ) {
        
        currentPoint <- c(partial$rx[n], partial$ry[n])
        currentPointX <- currentPoint[1]
        currentPointY <- currentPoint[2]
        
        if ( n > 1 ) {
            previousPointX <- partial$rx[n-1]
            previousPointY <- partial$ry[n-1]
        } else {
            previousPointX <- currentPointX
            previousPointY <- currentPointY
        }
        
        #TRE
        if (dist(rbind(currentPoint, targetPoint)) < partial$TargetWidth[n]/2) {
            if ( !tre$entered ) {
                tre$entered <- TRUE
                tre$TRE <- tre$TRE + 1
            } 
        } else {
            tre$entered <- FALSE
        }
        
        #TAC
        if ( tac$prevTACY*currentPointY < 0 ) {
            tac$TAC <- tac$TAC + 1
        }
        tac$prevTACY <- currentPointY
        
        #MDC          
        mdc$curDif <- currentPointY-previousPointY
        if ( mdc$prevDif * mdc$curDif < 0 ) {
            mdc$MDC <- mdc$MDC +1
            #print("+1")
        }
        if ( mdc$curDif != 0 ) {
            mdc$prevDif <- mdc$curDif
        }
        
        #ODC        
        #curX <- newPoint[1, 1]
        odc$curDif <- currentPointX-previousPointX
        if ( odc$prevDif * odc$curDif < 0 ) {
            odc$ODC <- odc$ODC +1
        }
        if ( odc$curDif != 0 ) {
            odc$prevDif <- odc$curDif
        }   
    }
    
    #MO
    # MO is the mean Y
    MO <- sum(partial$ry) / nrow(partial)
    
    #MV
    # 
    MV <- sqrt(sum((partial$ry-MO)*(partial$ry-MO))/(nrow(partial)-1))
    
    #ME 
    ME <- sum(abs(partial$ry))/nrow(partial)
    
    row<-data.frame(Device          = partial[1,]$NumberDevice, 
                    UserId          = partial[1,]$UserId, 
                    Block           = partial[1,]$Block, 
                    Sequence        = partial[1,]$Sequence, 
                    CircleID        = partial[1,]$CircleID, 
                    ErrorRate       = errorRate, 
                    ErrorRateBinary = errorRateBinary,
                    TRE       = tre$TRE, 
                    TAC       = tac$TAC, 
                    MDC       = mdc$MDC, 
                    ODC       = odc$ODC, 
                    MV        = MV, 
                    ME        = ME, 
                    MO        = MO, 
                    MovementTime      = partial[1,]$ElapsedTime/1000, 
                    TargetWidth       = partial[1,]$TargetWidth, 
                    NominalDistance   = partial[1,]$DistanceCenter*2,
                    ClickPointX       = partial$rx[nrow(partial)],
                    ClickPointY       = partial$ry[nrow(partial)],
                    TargetX           = partial$transfTargetx[nrow(partial)],
                    TargetY           = partial$transfTargety[nrow(partial)],
                    EffectiveDistance = dist( rbind(c(partial$transfTargetx[1], partial$transfTargety[1]), c(partial$rx[1], partial$ry[1])) )[1] )

    return (row)
}


dataMeasures <- data.frame()
newData <- data.frame()


#For noise errors checking
noiseErrorCount <- 0


# This function is executed as a parallel job, for calculating the transformed pointer coordinates for a given sequence.
transformationJob <- function(user, device, block) {
    print(paste("User:", user, " Device:", device, " Block:", block))
    
    newDataSequence <- data.frame()
    
    for (sequence in 1:max(dataRaw[dataRaw$UserId==user & dataRaw$NumberDevice==device & dataRaw$Block==block,]$Sequence) ) {
        
        for (cid in 1:max(dataRaw$CircleID) ) {
        
            # indexes for the current target selection
            sampleIndexes <- which(dataRaw$UserId == user & dataRaw$NumberDevice == device & dataRaw$Block == block & dataRaw$Sequence == sequence & dataRaw$CircleID == cid)
            
            # partial will hold the new calculated variables for the current selection path
            partial <- transform(dataRaw[sampleIndexes, ])
            newDataSequence <- rbind(newDataSequence, partial)
        }
    }
    return (newDataSequence)
}


# This function is executed as a parallel job, for calculating the transformed pointer coordinates for a given sequence.
measuresJob <- function(user, device, block) {
    measures <- data.frame()
    
    for (sequence in 1:max(newData[newData$UserId==user & newData$NumberDevice==device & newData$Block==block,]$Sequence) ) {
        #for (sequence in 1:1 ) {
        print(paste("User:", user, " Device:", device, " Block:", block, " Sequence: ", sequence))
        
        for (cid in 1:max(newData$CircleID) ) {
            # indexes for the current target selection
            sampleIndexes <- which(newData$UserId == user & newData$NumberDevice == device & newData$Block == block & newData$Sequence == sequence & newData$CircleID == cid)
            
            # partial will hold the new calculated variables for the current selection path
            partial <- newData[sampleIndexes, ]
            
            measuresRow <- calculateMeasures(partial)  
            measures <- rbind(measures, measuresRow)
        }
        
    }
    return (measures)
}


# this will go through all users, devices, blocks, and launch a job for each sequence 
# for calculating the transformed coordinates
print("Transforming pointer coordinates...")
time = system.time(
for (user in unique(dataRaw$UserId) ) {
#for (user in 4 ) {
    
    newDataUser <- data.frame()
    
    for (device in unique(dataRaw[dataRaw$UserId==user,]$NumberDevice) ) {
    #for (device in unique(dataRaw$NumberDevice)[1] ) {
             
        print(paste("User:", user, " Device:", device, " Blocks:",  
                    max(dataRaw[dataRaw$UserId==user & dataRaw$NumberDevice==device,]$Block )))
        
        f <- foreach(block = 1:max(dataRaw[dataRaw$UserId==user & dataRaw$NumberDevice==device,]$Block),
                     .combine='rbind') %dopar% transformationJob(user, device, block)
        
        newDataUser <- rbind(newDataUser, f)
      
      #newDataUser <- rbind(newDataUser, newDataDevice)
    }
    newData <- rbind(newData, newDataUser)
  }
)
print(time)

# this will go through all users, devices, blocks,  sequences, and circle ids, and calculate the various measures for each selection

print("Calculating MacKenzie's measures...")
time1 = system.time(
for (user in unique(newData$UserId) ) {
    #for (user in 4 ) {
    
    for (device in unique(newData[newData$UserId==user,]$NumberDevice) ) {
    #for (device in unique(newData$NumberDevice)[1] ) {
        
        print(paste("User:", user, " Device:", device, " Blocks:",  
                    max(newData[newData$UserId==user & newData$NumberDevice==device,]$Block )))
              
        f <- foreach(block = 1:max(newData[newData$UserId==user & newData$NumberDevice==device,]$Block),
                       .combine='rbind') %dopar% measuresJob(user, device, block)
              
        dataMeasures <- rbind(dataMeasures, f)

    }
}
)

print(time1)
print(paste("Total time:", (time+time1)[3]/60, "minutes."))

# 
print(paste("Noise errors found: ", noiseErrorCount))


# Calculate throughput
dataMeasures$Throughput <- -1
for ( device in unique(dataMeasures$Device)) {
  meanX <- mean(dataMeasures[dataMeasures$Device == device,]$ClickPointX - dataMeasures[dataMeasures$Device == device,]$TargetX)
  meanY <- mean(dataMeasures[dataMeasures$Device == device,]$ClickPointY - dataMeasures[dataMeasures$Device == device,]$TargetY )
  
  diffX <- (dataMeasures[dataMeasures$Device == device,]$ClickPointX - dataMeasures[dataMeasures$Device == device,]$TargetX)-meanX
  diffY <- (dataMeasures[dataMeasures$Device == device,]$ClickPointY - dataMeasures[dataMeasures$Device == device,]$TargetY)-meanY
  
  diffSQX <- diffX*diffX
  diffSQY <- diffY*diffY
  
  SD <- sqrt(sum(diffSQX+diffSQY)/(length(diffX)-1))
  We <- 4.133*SD
  IDe <- log(dataMeasures[dataMeasures$Device == device,]$EffectiveDistance/We + 1, 2)
  Throughput <- IDe/dataMeasures[dataMeasures$Device == device,]$MovementTime
  dataMeasures[dataMeasures$Device == device,]$Throughput <-Throughput
}

#View(newData)
#View(dataMeasures)
write.table(newData, file = filenameTransformed, sep=" ", row.names=FALSE)
write.table(dataMeasures, file = filenameMeasures, sep=" ", row.names=FALSE)

stopCluster(cl)