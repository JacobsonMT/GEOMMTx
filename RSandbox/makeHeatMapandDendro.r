#input matrix is made by MakeHistograms.java
x<-read.table("ExperimentToConceptMatrix.txt")
x <- as.matrix(x)

#transpose
x <- t(x)

#remove experiments with no experiments
s<-sd(x)
x <- x[,which(s!=0)]


#show the data
#map <- heatmap(x, keep.dendro = TRUE, scale = "none", margins=c(20,10))


map <- heatmap(cor(x), symm = TRUE, distfun = function(c) as.dist(1 - abs(c)), keep.dendro = TRUE, scale = "none", margins=c(10,10))


png("dendro.png", width=8840, height=1040) 
par(mar=c(42,8,4,6))
plot(map$Colv)
dev.off()

c<- cor(x)

threshold <- 0.7
where <- which(c>threshold & c!=1, arr.ind=TRUE)[,2]
where <- unique(where)
length(where)

xx <- x[,where]

map <- heatmap(cor(xx), symm = TRUE, distfun = function(c) as.dist(1 - abs(c)), keep.dendro = TRUE, scale = "none", margins=c(10,10))

png("dendro.png", width=1340, height=5040) 
#bottom, ?, top, right
par(mar=c(2,13,2,42))
plot(map$Colv, horiz=TRUE)
dev.off()



