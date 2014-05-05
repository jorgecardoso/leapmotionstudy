

# Functions
rotationMatrix <- function(angle) {
    matrix <- matrix(c(cos(angle), sin(angle), -sin(angle), cos(angle)), nrow = 2, ncol = 2)
    return(matrix)
}
rotate <- function(point, angle) {
    return( point %*% rotationMatrix(angle) )
}

angleBetween <- function(v1, v2) {
    
    dot <- sum(v1*v2);# v1.x * v2.x + v1.y * v2.y + v1.z * v2.z;
    v1mag <- sqrt(v1[1] * v1[1] + v1[2] * v1[2] );
    v2mag <- sqrt(v2[1] * v2[1] + v2[2] * v2[2] );
    
    amt <- dot / (v1mag * v2mag);
    
    if (amt <= -1) {
        return (pi)
    } else if (amt >= 1) {
        return (0)
    }
    
    return ( acos(amt) );
    
}

rotateAround <- function(referencePoint, point, angle) {
    displacement <- point-referencePoint
    rotatedPoint <- rotate(point-displacement, angle)
    rotatedPoint <- rotatedPoint + displacement
    return( rotatedPoint)
}

toDegrees <- function(radians) {
    
    return( radians*360/(2*pi) )
}



############################ data
device <- "all"
filename <- paste("data/",device,".txt", sep="")
filenameTransformed <- paste("data/", device,"-transformed.txt", sep="")
filenameMeasures <- paste("data/", device,"-measures.txt", sep="")
dataRaw <- read.csv(file=filename, head=TRUE, sep="")



dataMeasures <- data.frame()

newData <- data.frame()

system.time(
    for (device in unique(dataRaw$NumberDevice) ) {
        print(paste("Device: ", device))
        newDataDevice <- data.frame()
        
        for (block in 1:max(dataRaw$Block) ) {
            #for (block in 1:1 ) {
            print(paste("Block: ", block))
            newDataBlock <- data.frame()
            
            for (sequence in 1:max(dataRaw$Sequence) ) {
                #for (sequence in 1:1 ) {
                print(paste("Sequence: ", sequence))
                newDataSequence <- data.frame()
                
                for (cid in 1:max(dataRaw$CircleID) ) {
                    #for (cid in 1 ) {
                    
                    
                    #indexes for the current target selection
                    indexes <- which(dataRaw$NumberDevice == device & dataRaw$Block == block & dataRaw$Sequence == sequence & dataRaw$CircleID == cid)
                    partial <- dataRaw[indexes, ]
                    
                    #print(paste("CircleID: ", cid, " samples: ", length(indexes)))
                    
                    # we consider the movement axis as a line goind from the first point to the center of the target
                    axisStart <- c(partial[1,]$MouseX, partial[1,]$MouseY)
                    axisEnd <- c(partial[nrow(partial),]$PixelEndCircleX, partial[nrow(partial),]$PixelEndCircleY)
                    axis <- axisEnd-axisStart
                    #print(axis)
                    
                    # TODO: check why some selections started and ended on the same point
                    # these lines guarantee that we skip these errors
                    if (axis[1] == 0 & axis[2] == 0 ) {
                        print(paste("Error? Block:", block, " Sequence: ", sequence, " CircleID: ", cid, " Axis:", axis))
                        next
                    }
                    
                    # we will transform the coordinates so that the movements axis is horizontal, and starts at (0, 0)
                    angle <- angleBetween( c(1, 0), axis )
                    if ( axis[2] < 0) {
                        angle <- -angle
                    }
                    
                    # transform also the target coordinates, so we can calculate the TRE measure
                    point <- c(dataRaw[indexes[1],]$PixelEndCircleX, dataRaw[indexes[1],]$PixelEndCircleY)-axisStart
                    target <- rotate(point, angle )
                    #dataRaw$targetx[indexes[1]]<-target[1,1]
                    #dataRaw$targety[indexes[1]]<-target[1,2]
                    
                    # calculated distance (first click - target)
                    calculatedDistance <- dist(rbind(target, c(0,0)))[1]
                    
                    
                    # go through all coordinates, transform them and calculate the various measures.
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
                    
                    
                    #for (n in indexes ) {
                    for (n in 1:nrow(partial) ) {
                        
                        #dataRaw$targetx[n]<-target[1,1]
                        #dataRaw$targety[n]<-target[1,2]
                        
                        #partial$targetx[n]<-target[1,1]
                        #partial$targety[n]<-target[1,2]
                        
                        partialtargetx[n]<-target[1,1]
                        partialtargety[n]<-target[1,2]
                        
                        # transform the coordinate
                        #point <- c(dataRaw[n,]$MouseX, dataRaw[n,]$MouseY)-axisStart 
                        point <- c(partial[n,]$MouseX, partial[n,]$MouseY)-axisStart 
                        newPoint <- rotate(point, angle )
                        #dataRaw$rx[n]<-newPoint[1,1]
                        #dataRaw$ry[n]<-newPoint[1,2]
                        #partial$rx[n]<-newPoint[1,1]
                        #partial$ry[n]<-newPoint[1,2]
                        partialrx[n]<-newPoint[1,1]
                        partialry[n]<-newPoint[1,2]
                        
                        
                        clickPointX <- newPoint[1,1]
                        clickPointY <- newPoint[1,2]
                        
                        #TRE
                        if (dist(rbind(newPoint, target)) < partial[n,]$TargetWidth/2) {
                            if ( !entered ) {
                                entered <- TRUE
                                TRE <- TRE + 1
                            } 
                            #partial$inside[n]<-TRUE
                            partialinside[n]<-TRUE
                        }else {
                            entered <- FALSE
                            #partial$inside[n]<-FALSE
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
                    
                    
                    row <- c(partial[1,]$NumberDevice, partial[1,]$UserId, partial[1,]$Block, partial[1,]$Sequence, partial[1,]$CircleID, errorRate, TRE, TAC, MDC, ODC, MV, ME, MO, clickPointX, clickPointY, partial[1,]$ElapsedTime/1000, partial[1,]$TargetWidth, partial[1,]$DistanceCenter*2, calculatedDistance)
                    dataMeasures <- rbind(dataMeasures, row)
                    
                    partial$targetx <- partialtargetx
                    partial$targety <- partialtargety
                    partial$rx <- partialrx
                    partial$ry <- partialry
                    partial$inside <- partialinside
                    
                    #newData <- rbind(newData, partial)
                    newDataSequence <- rbind(newDataSequence, partial)
                }
                newDataBlock <- rbind(newDataBlock, newDataSequence)    
            }
            newDataDevice <- rbind(newDataDevice, newDataBlock)
        }
        newData <- rbind(newData, newDataDevice)
    }
)
names(dataMeasures) <- c("DeviceNumber", "UserId", "Block", "Sequence", "CircleID", "ErrorRate", "TRE", "TAC", "MDC", "ODC", "MV", "ME", "MO", "ClickPointX", "ClickPointY", "MovementTime", "TargetWidth", "Distance", "CalculatedDistance")

#Calculate throughput
meanX <- mean(dataMeasures$ClickPointX)
meanY <- mean(dataMeasures$ClickPointY)

diffX <- dataMeasures$ClickPointX-meanX
diffY <- dataMeasures$ClickPointY-meanY

diffSQX <- diffX*diffX
diffSQY <- diffY*diffY

SD <- sqrt(sum(diffSQX+diffSQY)/length(diffX))
We <- 4.133*SD
IDe <- log(dataMeasures[1,]$Distance/We + 1)
Throughput <- IDe/dataMeasures$MovementTime



write.table(newData, file = filenameTransformed, sep=" ", row.names=FALSE)
write.table(dataMeasures, file = filenameMeasures, sep=" ", row.names=FALSE)





# Plot the calculated distances 
plot(dataMeasures[dataMeasures$DeviceNumber==0,]$CalculatedDistance)


toplot <- newData[newData$NumberDevice==2 & newData$Block == 1 & newData$Sequence == 1 & newData$CircleID ==3, ]

plot(toplot$rx, 
     toplot$ry, type='l')






