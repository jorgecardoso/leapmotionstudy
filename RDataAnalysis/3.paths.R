# This script generates the graphics with the various selection paths

require(ggplot2)
require(doBy)

GENERATE_INDIVIDUAL_PATHS <- FALSE


############################ data


files <- list.files(path="data", pattern="transformed.*")
files
dataTransformed <- data.frame()
for (file in files) {
  print (file)
  dat = read.csv(paste("data/", file, sep=""), sep="", head = TRUE)
  print(nrow(dat))
  dataTransformed <- rbind(dataTransformed, dat)
}
colnames(dataTransformed)

#dataTransformed <- read.csv(file="data/transformed.txt", head=TRUE, sep="")

# change column name to get a nicer chart
colnames(dataTransformed)[colnames(dataTransformed)=="NumberDevice"] <- "Device"


# convert the column to factor and name the levels
dataTransformed$Device <- as.factor(dataTransformed$Device)
levels(dataTransformed$Device) <- list( LeapMotion=c(0),  Mouse=c(1), Touchpad=c(2), LeapMotionTouchless=c(4))
#levels(dataTransformed$Device) <- c("Mouse")


#calculate the maximum and minimum y and x coords for setting the plots' scales
minX <- min(dataTransformed$rx)
maxX <- max(dataTransformed$rx)
minY <- min(dataTransformed$ry)
maxY <- max(dataTransformed$ry)
print ( paste("X scale: ", minX, maxX, " Y scale: ", minY, maxY))




# plot the paths for each user and device. a single plot aggregates one entire sequence
for (device in unique(dataTransformed$Device) ) {
  for (user in unique(dataTransformed$UserId)) {
    p <- ggplot(dataTransformed[dataTransformed$Device==device &
                                  dataTransformed$UserId==user,], 
                aes(x=rx, y=ry, group=Device, colour=Device )) +
      geom_path() +
      coord_cartesian(xlim = c(minX, maxX), ylim=c(minY, maxY)) +
      facet_grid(Block ~ Sequence) +
      ylab("Block") +
      xlab("Sequence") +
      theme(legend.position="none") +
      ggtitle(paste("Device: ", device, " User: ", user))
    p
    filename <- paste("charts/paths/byblocksequence", device, "-user-", user, ".pdf", sep="")
    print( filename  )
    ggsave(file = filename, width=21/2.54, height=29/2.54, dpi=100)
  }
}


# plot the paths for each user and device. a single plot aggregates one circleid
for (device in unique(dataTransformed$Device) ) {
  for (user in unique(dataTransformed$UserId)) {
    p <- ggplot(dataTransformed[dataTransformed$Device==device &
                                  dataTransformed$UserId==user,], 
                aes(x=rx, y=ry, group=Device, colour=Device )) +
      geom_path() +
      coord_cartesian(xlim = c(minX, maxX), ylim=c(minY, maxY)) +
      facet_grid(CircleID ~ .) +
      ylab("Circle ID") +
      xlab("x") +
      theme(legend.position="none") +
      ggtitle(paste("Device: ", device, " User: ", user))
    p
    filename <- paste("charts/paths/bycircleid-", device, "-user-", user, ".pdf", sep="")
    print( filename )
    ggsave(file = filename, width=21/2.54, height=29/2.54, dpi=100)
  }
}



# plot INDIVIDUAL paths for each user and device. 
# PRODUCES LOTS OF FILES
if (GENERATE_INDIVIDUAL_PATHS == TRUE) {
  for (device in unique(dataTransformed$Device) ) {
    for (user in unique(dataTransformed$UserId)) {
      for (block in unique(dataTransformed[dataTransformed$Device==device &
                                             dataTransformed$UserId==user,]$Block)) { 
        p <- ggplot(dataTransformed[dataTransformed$Device==device &
                                      dataTransformed$Block==block &
                                      dataTransformed$UserId==user,], 
                    aes(x=rx, y=ry, group=Device, colour=Device )) +
          geom_path() +
          coord_cartesian(xlim = c(minX, maxX), ylim=c(minY, maxY)) +
          facet_grid(CircleID ~ Sequence) +
          ylab("Circle Id") +
          xlab("Block") +
          theme(legend.position="none") +
          ggtitle(paste("Device: ", device, " User: ", user, " Block: ", block))
        p
        filename <- paste("charts/paths/individual-", device, "-user-", user, "-block-", block,".pdf", sep="")
        print( filename )
        ggsave(file = filename, width=29/2.54, height=35/2.54, dpi=100)
      }
    }
  }
}



dataTransformed$cuts<-cut(dataTransformed$percentpath, 100)


speed<-aggregate(dataTransformed$speeds, dataTransformed[,c("cuts", "Device")], mean)
ggplot(speed, aes(x=cuts, y=x, group=Device, colour=Device)) +
  #geom_smooth() +
  geom_path()


accel<-aggregate(dataTransformed$accels, dataTransformed[,c("cuts", "Device")], mean)
ggplot(accel, aes(x=cuts, y=x, group=Device, colour=Device)) +
  #geom_smooth() +
  geom_path()


