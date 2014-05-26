

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
