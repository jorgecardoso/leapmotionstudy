#http://www.sigchi.org/chi96/proceedings/papers/Mithal/Akm_txt.htm
# (a) (b) (c) (d)
# Figure 3. Path variations. (a) target re-entry (b) task axis crossing (c) movement direction change (d) orthogonal direction change
# An example where target re-entry was not used, yet may have helped, is Akamatsu et al.???s evaluation of a mouse with tactile feedback [2]. This
# study found a main effect on fine
# positioning time ??? the time to select the target after the pointer entered the target region. With tactile feedback, users exhibited a lower fine positioning time than under the no feedback, auditory feedback, and colour feedback conditions. A measure such as target re-entry may also serve to reveal differences among
# on-target feedback conditions, for
# example.
# http://www.yorku.ca/mack/Ergonomics.html

require(psych)
require(doBy)

# Analyse only blocks after learning effect
dataTransformed <- dataTransformed[dataTransformed$Block>3,]
dataTransformed$cuts<-cut(dataTransformed$percentpath, 100)
#View(dataTransformed)


# Descriptive stats
describeBy(dataTransformed[, c("speeds", "accels", "insidecount", "displacement", "distance")], dataTransformed[, c("Device")])



agg <- aggregate(dataTransformed[, c("insidecount", "speeds", "accels")], dataTransformed[, c("Device", "UserId", "Block", "Sequence", "CircleID")], mean)
View(agg)
describeBy(agg$x*25, agg$Device)

agg <- aggregate(dataTransformed[, c("displacement", "distance")], dataTransformed[, c("Device", "UserId", "Block", "Sequence", "CircleID")], max)
View(agg)
describeBy(agg$x*25, agg$Device)


d <- dataTransformed[dataTransformed$Device=="Mouse" & dataTransformed$Block==4 & dataTransformed$UserId ==1 & dataTransformed$Sequence==1 & dataTransformed$CircleID==1, ]

d <- dataTransformed[dataTransformed$Device=="Mouse" & dataTransformed$Block==4 & dataTransformed$UserId ==1 & dataTransformed$Sequence==1, ]

#View()

#plot(d$speeds)
ggplot(dataTransformed, aes(x=sampleid*25, y=speeds, group=Device, colour=Device)) +
    geom_path() +
    geom_point()


speed<-aggregate(dataTransformed$speeds, dataTransformed[,c("sampleid", "Device")], mean)
ggplot(speed, aes(x=sampleid, y=x, group=Device, colour=Device)) +
    #geom_smooth() +
    geom_path()



speed<-aggregate(dataTransformed$speeds, dataTransformed[,c("cuts", "Device")], mean)
ggplot(speed, aes(x=cuts, y=x, group=Device, colour=Device)) +
    #geom_smooth() +
    geom_path()


accel<-aggregate(dataTransformed$accels, dataTransformed[,c("cuts", "Device")], mean)
ggplot(accel, aes(x=cuts, y=x, group=Device, colour=Device)) +
    #geom_smooth() +
    geom_path()