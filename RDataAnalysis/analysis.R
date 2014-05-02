require(RCurl) # web
require(ggplot2)
library("stringr") # for first word
library(reshape)
#theme_set(theme_bw())


# Functions
SIZE = 100000
sinTable <- sin(0:SIZE / SIZE * 2 * pi);


fastSin <- function(theta) {
   index <- floor(theta / (2 * pi) * SIZE) %% SIZE
  return ( sinTable[index+1]);
}
fastCos <- function(theta) {
  return (fastSin(theta+pi/2))
}
errorSin <- c()
errorCos <- c()
for (i in seq(0, 2*pi, by=0.1) ) {
  fastS <- fastSin(i)
  s <- sin(i)
  c <- cos(i)
  fastC <- fastCos(i)
  
  #print(paste(n, " ", fast, " ", abs(fast-n)))
  errorSin <- append(error, abs(fastS-s))
  errorCos <- append(error, abs(fastC-c))
}
max(errorSin)
max(errorCos)

fastCos(theta) {
  return fastSin(theta + Math.PI / 2);
}

fastrotationMatrix <- function(angle) {
  matrix <- matrix(c(fastCos(angle), fastSin(angle), -fastSin(angle), fastCos(angle)), nrow = 2, ncol = 2)
  return(matrix)
}
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
dataRaw <- read.csv(file=filename, head=TRUE, sep="")

#edit(dataRaw)


date()
#for (block in 1:max(dataRaw$Block) ) {
for (block in 1:1 ) {
  print(paste("Block: ", block))
  
  #for (sequence in 1:max(dataRaw$Sequence) ) {
  for (sequence in 1:1 ) {
    print(paste("Sequence: ", sequence))
    
    for (cid in 1:max(dataRaw$CircleID) ) {
    #for (cid in 14 ) {
        print(paste("CircleID: ", cid))
        
        #indexes for the current target selection
        indexes <- which(dataRaw$Block == block & dataRaw$Sequence == sequence & dataRaw$CircleID == cid)
        partial <- dataRaw[indexes, ]
      
        # we consider the movement axis as a line goind from the first point to the center of the target
        axisStart <- c(partial[1,]$MouseX, partial[1,]$MouseY)
        axisEnd <- c(partial[nrow(partial),]$PixelEndCircleX, partial[nrow(partial),]$PixelEndCircleY)
        axis <- axisEnd-axisStart
        
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
      
        
        # go through all coordinates, transform them and calculate the various measures.
        
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
        for (n in indexes ) {
          # transform the coordinate
          point <- c(dataRaw[n,]$MouseX, dataRaw[n,]$MouseY)-axisStart #-c(partial[1,]$MouseX, partial[1,]$MouseY)
          newPoint <- rotate(point, angle )
          dataRaw$rx[n]<-newPoint[1,1]
          dataRaw$ry[n]<-newPoint[1,2]
          
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
                            
        print(paste("TRE: ", TRE))
        
        print(paste("TAC: ", TAC))
        
        print(paste("MDC: ", MDC))
        
        print(paste("ODC: ", ODC))
        
        print(paste("MV: ", MV))
        
        print(paste("ME: ", ME))
        
        print(paste("MO: ", MO))
        
        
    }
  }
}
date()






write.table(dataRaw, file = filenameTransformed, sep=" ", row.names=FALSE)

toplot <- dataRaw[dataRaw$Block == 1 & dataRaw$Sequence == 1 & dataRaw$CircleID ==12, ]

plot(toplot$rx, 
     toplot$ry, type='l')

plot(partial$x, partial$y)
plot(partial$rx, partial$ry)
edit(partial)





