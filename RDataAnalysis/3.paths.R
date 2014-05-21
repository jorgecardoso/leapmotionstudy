#require(RCurl) # web
require(ggplot2)
require(doBy)
#library("stringr") # for first word
#library(reshape)
#theme_set(theme_bw())

# Multiple plot function
#
# ggplot objects can be passed in ..., or to plotlist (as a list of ggplot objects)
# - cols:   Number of columns in layout
# - layout: A matrix specifying the layout. If present, 'cols' is ignored.
#
# If the layout is something like matrix(c(1,2,3,3), nrow=2, byrow=TRUE),
# then plot 1 will go in the upper left, 2 will go in the upper right, and
# 3 will go all the way across the bottom.
#
multiplot <- function(..., plotlist=NULL, file, cols=1, layout=NULL) {
    require(grid)
    
    # Make a list from the ... arguments and plotlist
    plots <- c(list(...), plotlist)
    
    numPlots = length(plots)
    
    # If layout is NULL, then use 'cols' to determine layout
    if (is.null(layout)) {
        # Make the panel
        # ncol: Number of columns of plots
        # nrow: Number of rows needed, calculated from # of cols
        layout <- matrix(seq(1, cols * ceiling(numPlots/cols)),
                         ncol = cols, nrow = ceiling(numPlots/cols))
    }
    
    if (numPlots==1) {
        print(plots[[1]])
        
    } else {
        # Set up the page
        grid.newpage()
        pushViewport(viewport(layout = grid.layout(nrow(layout), ncol(layout))))
        
        # Make each plot, in the correct location
        for (i in 1:numPlots) {
            # Get the i,j matrix positions of the regions that contain this subplot
            matchidx <- as.data.frame(which(layout == i, arr.ind = TRUE))
            
            print(plots[[i]], vp = viewport(layout.pos.row = matchidx$row,
                                            layout.pos.col = matchidx$col))
        }
    }
}


############################ data
#dataTransformedLeap <- read.csv(file="data/leapmotion-transformed.txt", head=TRUE, sep="")
#dataTransformedTouch <- read.csv(file="data/touchpad-transformed.txt", head=TRUE, sep="")
#dataTransformedMouse <- read.csv(file="data/mouse-transformed.txt", head=TRUE, sep="")


#dataTransformed <- rbind(dataTransformedLeap, dataTransformedTouch, dataTransformedMouse)
dataTransformed <- read.csv(file="data/10-transformed.txt", head=TRUE, sep="")

# change column name to get a nicer chart
colnames(dataTransformed)[colnames(dataTransformed)=="NumberDevice"] <- "Device"

# convert the column to factor and name the levels
dataTransformed$Device <- as.factor(dataTransformed$Device)
levels(dataTransformed$Device) <- c("LeapMotion",  "Mouse", "Touchpad")
#levels(dataTransformed$Device) <- c("Mouse")


#calculate the maximum and minimum y and x coords for setting the plots' scales
minX <- min(dataTransformed$rx)
maxX <- max(dataTransformed$rx)
minY <- min(dataTransformed$ry)
maxY <- max(dataTransformed$ry)
print ( paste("X scale: ", minX, maxX, " Y scale: ", minY, maxY))

# plot the paths for each user and device. a single plot aggregates one entire sequence
for (device in levels(dataTransformed$Device) ) {
    for (user in unique(dataTransformed$User)) {
        p <- ggplot(dataTransformed[dataTransformed$Device==device &
                                         dataTransformed$UserId==user,], 
                     aes(x=rx, y=ry, group=Device, colour=Device )) +
            geom_line() +
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
for (device in levels(dataTransformed$Device) ) {
    for (user in unique(dataTransformed$User)) {
        p <- ggplot(dataTransformed[dataTransformed$Device==device &
                                        dataTransformed$UserId==user,], 
                    aes(x=rx, y=ry, group=Device, colour=Device )) +
            geom_line() +
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
for (device in levels(dataTransformed$Device) ) {
    for (user in unique(dataTransformed$User)) {
        for (block in unique(dataTransformed$Block)) { 
            p <- ggplot(dataTransformed[dataTransformed$Device==device &
                                            dataTransformed$Block==block &
                                            dataTransformed$UserId==user,], 
                        aes(x=rx, y=ry, group=Device, colour=Device )) +
                geom_line() +
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






# # plot the aggregated paths each device
# dataLastBlock <- dataTransformed[dataTransformed$Block==10, ]
# p1 <- ggplot(dataLastBlock[dataLastBlock$Device=="Mouse",], aes(x=rx, y=ry, group=Device, colour=Device )) +
#     geom_line() +
#     coord_cartesian(xlim = c(0, 1000), ylim=c(-250, 250))
# 
# p2 <- ggplot(dataLastBlock[dataLastBlock$Device=="LeapMotion",], aes(x=rx, y=ry, group=Device, colour=Device )) +
#     geom_line() +
#     coord_cartesian(xlim = c(0, 1000), ylim=c(-250, 250))
# 
# p3 <- ggplot(dataLastBlock[dataLastBlock$Device=="Touchpad",], aes(x=rx, y=ry, group=Device, colour=Device )) +
#     geom_line() +
#     coord_cartesian(xlim = c(0, 1000), ylim=c(-250, 250)) +
#     facet_grid(Sequence ~ .)
# 
# multiplot(p1, p2, p3, cols=1)
