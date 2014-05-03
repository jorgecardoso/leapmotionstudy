require(RCurl) # web
require(ggplot2)
library("stringr") # for first word
library(reshape)
#theme_set(theme_bw())


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
filename <- "mouse.txt"
filenameTransformed <- "mouse-transformed.txt"
filenameMeasures <- "mouse-measures.txt"
dataRaw <- read.csv(file=filename, head=TRUE, sep="")

#edit(dataRaw)

dataMeasures <- data.frame()#NumberDevice=numeric(0), UserId=numeric(0), Block=numeric(0), Sequence=numeric(0), ErrorRate=numeric(0))

date()
for (block in 1:max(dataRaw$Block) ) {
#for (block in 1:1 ) {
    print(paste("Block: ", block))
    
    for (sequence in 1:max(dataRaw$Sequence) ) {
    #for (sequence in 1:1 ) {
        print(paste("Sequence: ", sequence))
        
        for (cid in 1:max(dataRaw$CircleID) ) {
        #for (cid in 3 ) {
            print(paste("CircleID: ", cid))
            
            #indexes for the current target selection
            indexes <- which(dataRaw$Block == block & dataRaw$Sequence == sequence & dataRaw$CircleID == cid)
            partial <- dataRaw[indexes, ]
            
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
            dataRaw$targetx[indexes[1]]<-target[1,1]
            dataRaw$targety[indexes[1]]<-target[1,2]
            #print(target)
            
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
            
            # will hold the click point (transformed)
            clickPointX <- 0
            clickPointY <- 0
            for (n in indexes ) {
                
                dataRaw$targetx[n]<-target[1,1]
                dataRaw$targety[n]<-target[1,2]
                
                # transform the coordinate
                point <- c(dataRaw[n,]$MouseX, dataRaw[n,]$MouseY)-axisStart #-c(partial[1,]$MouseX, partial[1,]$MouseY)
                newPoint <- rotate(point, angle )
                dataRaw$rx[n]<-newPoint[1,1]
                dataRaw$ry[n]<-newPoint[1,2]
                
                #print(target)
                #print(newPoint)
                #print(" ")
                clickPointX <- newPoint[1,1]
                clickPointY <- newPoint[1,2]
                
                #TRE
                if (dist(rbind(newPoint, target)) < dataRaw[n,]$TargetWidth/2) {
                    if ( !entered ) {
                        entered <- TRUE
                        TRE <- TRE + 1
                    } 
                    dataRaw$inside[n]<-TRUE
                }else {
                    entered <- FALSE
                    dataRaw$inside[n]<-FALSE
                    
                }
                
                #TAC
                curY <- newPoint[1, 2]
                if ( prevTACY*curY < 0 ) {
                    TAC <- TAC +1
                }
                prevTACY <- curY
                
                #MDC          
                curDif <- curY-prevMDCY
                #print(paste("prevMDCDif:", prevMDCDif ,"prevMDCY: ", prevMDCY, " curDif:", curDif))
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
                #print(paste("prevMDCDif:", prevMDCDif ,"prevMDCY: ", prevMDCY, " curDif:", curDif))
                if ( prevODCDif * curDif < 0 ) {
                    ODC <- ODC +1
                    #print("+1")
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
            

            #print(paste("ErrorRate:", errorRate))
            
            #print(paste("TRE: ", TRE))
            
            #print(paste("TAC: ", TAC))
            
            #print(paste("MDC: ", MDC))
            
            #print(paste("ODC: ", ODC))
            
            #print(paste("MV: ", MV))
            
            #print(paste("ME: ", ME))
            
            #print(paste("MO: ", MO))
            
            row <- c(partial[1,]$NumberDevice, partial[1,]$UserId, partial[1,]$Block, partial[1,]$Sequence, partial[1,]$CircleID, errorRate, TRE, TAC, MDC, ODC, MV, ME, MO, clickPointX, clickPointY, partial[1,]$ElapsedTime/1000, partial[1,]$TargetWidth, partial[1,]$DistanceCenter*2)
            dataMeasures <- rbind(dataMeasures, row)
            
        }
    }
}

names(dataMeasures) <- c("DeviceNumber", "UserId", "Block", "Sequence", "CircleID", "ErrorRate", "TRE", "TAC", "MDC", "ODC", "MV", "ME", "MO", "ClickPointX", "ClickPointY", "MovementTime", "TargetWidth", "Distance")

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

date()
summary(dataMeasures)

write.table(dataRaw, file = filenameTransformed, sep=" ", row.names=FALSE)
write.table(dataMeasures, file = filenameMeasures, sep=" ", row.names=FALSE)



toplot <- dataRaw[dataRaw$Block == 1 & dataRaw$Sequence == 1 & dataRaw$CircleID ==3, ]

plot(toplot$rx, 
     toplot$ry, type='l')

plot(partial$x, partial$y)
plot(partial$rx, partial$ry)
edit(partial)





