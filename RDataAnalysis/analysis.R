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
angleBetween <- function(vector1, vector2) {
	return( acos( sum(vector1*vector2) / ( sqrt(sum(vector1 * vector1)) * sqrt(sum(vector2 * vector2)) ) ) )
}

rotateAround <- function(referencePoint, point, angle) {
	displacement <- point-referencePoint
	rotatedPoint <- rotate(point-displacement, angle)
	rotatedPoint <- rotatedPoint + displacement
	return( rotatedPoint)
}

getXRotate(x, y, )


# data
dataRaw <- read.csv(file="todosOsTrials.txt", head=TRUE,sep="")

plot(dataRaw[dataRaw$PixelStartCircleX==500,]$MouseX, dataRaw[dataRaw$PixelStartCircleX==500,]$MouseY)


rotateAround(c(dataRaw[1,]$MouseX, dataRaw[1,]$MouseY), c(dataRaw[1,]$MouseX, dataRaw[1,]$MouseY), angleBetween(axis, c(1, 0) ) )


partial <- dataRaw[dataRaw$PixelStartCircleX==800,]

#edit(partial)
plot(partial$MouseX, partial$MouseY)
for (n in 1:nrow(partial) ) {

	axisStart <- c(partial[1,]$PixelStartCircleX, partial[1,]$PixelStartCircleY)
	axisEnd <- c(partial[1,]$PixelEndCircleX, partial[1,]$PixelEndCircleY)

	axis <- axisEnd-axisStart

	
	
	
	point <- c(partial[n,]$MouseX, partial[n,]$MouseY)-c(partial[1,]$MouseX, partial[1,]$MouseY)
	partial$x[n] <- point[1]
	partial$y[n] <- point[2]
	newPoint <- rotate(point, angleBetween(axis, c(1, 0) ) )
	print(n)
	print(point)
	print(newPoint)
	partial$rx[n] <- newPoint[1,1]
	partial$ry[n] <- newPoint[1,2]
}
plot(partial$x, partial$y)
plot(partial$rx, partial$ry)
edit(partial)

